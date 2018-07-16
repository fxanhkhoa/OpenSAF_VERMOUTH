
#include "../include/server.h"
extern List<user> user_data;
extern string file_path;

server *server::sv = NULL;
server *server::get_instance()
{
    if (sv == NULL)
        sv = new server;
    return sv;
}

bool server::add_room(room *r)
{
    if (r)
    {
        sv->list_room.insert(r);
        return true;
    }
    return false;
}
void server::load_data()
{
}

user *server::create_new_user(const char *u_name, const char *u_pass)
{
    user *res = user_data.insert(u_name, u_pass);
    //ghi xuong database
    if (res)
    {
        mu_lock_file.lock();
        fstream f(file_path, ios::app | ios::binary);
        f.write((char *)res, sizeof(user));
        f.close();
        mu_lock_file.unlock();
        //gui cho server standby chua lam
    }

    return res;
    // return true;
}

room *server::get_room_from_ID(int room_id)
{
    for (auto &c : this->list_room)
        if (c->get_key() == room_id)
            return c;
    return NULL;
}
/*tim id cua user name*/
id *server::get_id_from_name(const char *u_name)
{
    for (auto &c : this->list_online)
        if (strcmp(c->get_user()->get_username(), u_name) == 0)
            return c;
    return NULL;
}

/*xoa 1 room ra khoi list room*/
bool server::remove_room(int r_id, const char *u_name)
{
    for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
        if ((*it)->get_key() == r_id)
        {
            if ((*it)->get_captain() == NULL)
            {
                list_room.erase(it);
                return true;
            }
            if (strcmp((*it)->get_captain()->get_username(), u_name) == 0)
            {
                room *r = (*it);
                list_room.erase(it);
                r->~room();
                return true;
            }

            else
            {
                return false;
            }
        }
    return false;
}

bool server::remove_id(id *u)
{
    for (set<id *>::iterator it = this->list_online.begin(); it != list_online.end(); ++it)
    {
        if ((*it) == u)
        {
            this->list_online.erase(it);
            return true;
        }
    }
    return false;
}
id *server::remove_id(int fd)
{
    id *res = NULL;
    for (set<id *>::iterator it = this->list_online.begin(); it != list_online.end(); ++it)
    {
        if ((*it)->get_key() == fd)
        {
            this->list_online.erase(it);
            res = *it;
            return res;
        }
    }
    return NULL;
}

void server::listen_connect()
{
    thread *th;
    int listen_sock = open_listening_socket(8888);
    this->server_stanby_sock = -5;
    cout << "socket " << listen_sock << endl;
    pollfd t;
    t.fd = listen_sock;
    t.events = POLLIN;
    pollfd_push(list_poll_unlogin, empty_unlogin_pos, unlogin_size, t);
    int rc = -1, i, new_sd, sz, tmp;
    block_data buf;
    while (1)
    {
        sz = unlogin_size;
        cout << "sz= " << unlogin_size << " dang lang nghe tren poll\n";
        rc = poll(list_poll_unlogin, sz, -1);
        cout << "rc: " << rc << endl;
        if (rc < 0)
        {
            perror("  poll() failed");
            continue;
            ;
        }

        for (i = 0; i < sz; ++i)
        {
            if (list_poll_unlogin[i].events == 0)
                continue;
            if (list_poll_unlogin[i].revents != POLLIN)
                continue;

            /*i=0 -> listen sock*/
            if (i == 0)
            {
                new_sd = accept(listen_sock, NULL, NULL);
                if (new_sd < 0)
                    continue;
                cout << "new connection\n";
                t.fd = new_sd;
                t.events = POLLIN;
                pollfd_push(list_poll_unlogin, empty_unlogin_pos, unlogin_size, t);
            }
            else
            {
                /*lam nhung chuyen cua cac client da conect nhung chua dang nhap
                o day chi lam 2 chuyen la dang ki
                hoac dang nhap*/
                //  memset(&buf, 0, 1024);
                int len = read(list_poll_unlogin[i].fd, &buf, 1024);
                if (len <= 0)
                {
                    //client da ngat ket noi (client chua dang nhap)
                    //chua lam truong hop no la sv stanby
                    close(list_poll_unlogin[i].fd);
                    //list_poll_unlogin.erase(list_poll_unlogin.begin() + i);
                    pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);
                    while (unlogin_size < sz)
                        --sz;
                    continue;
                }
                if (len != 1024)
                {
                    //do dai khong bang 1024 la khong xu ly (bat chap)
                    mu.lock();
                    cout<<"len = "<<len<<endl;
                    cout << "do dai khac 1024\n";
                    ((char*)&buf)[len]=0;
                    cout<<"noi dung: "<<(char*)&buf;
                    mu.unlock();
                    continue;
                }
                mu.lock();
                cout << "data da nhan: \ncmd: " << buf.cmd << "\nuser nguoi gui: " << buf.sender_username << "\npass nguoi gui: " << buf.sender_pass << endl;
                mu.unlock();
                if (buf.cmd == 1) //dang ky
                {
                    user *u = this->create_new_user(buf.sender_username, buf.sender_pass);
                    //code 105 dang ky thanh cong, 104 that bai
                    buf.cmd = u ? 105 : 104;
                    send(list_poll_unlogin[i].fd, &buf, 1024, 0);
                    continue;
                }
                if (buf.cmd == 2) //dang nhap
                {
                    if (get_id_from_name(buf.sender_username))
                    {
                        cout << buf.sender_username << " already login\n";
                        buf.cmd = 100; //dang nhap that bai
                        send(list_poll_unlogin[i].fd, &buf, 1024, 0);
                        continue;
                    }
                    user *u = user_data.search(buf.sender_username);
                    if (u)
                    {
                        if (strcmp(u->get_passwd(), buf.sender_pass) == 0)
                        {
                            id *t = new id(u, list_poll_unlogin[i].fd);

                            this->list_online.insert(t);

                            tmp = list_poll_unlogin[i].fd;

                            //chuyen socket cua user da dang nhap qua list connected
                            pollfd_push(list_poll_connected, empty_connected_pos, connected_size, list_poll_unlogin[i]);
                            //xoa socket cua user da dang nhap tai list unlogin
                            pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);

                            //gui tin hieu bao cho client biet no da dang nhap thanh cong (code 101)
                            buf.cmd = 101; //dang nhap thanh cong
                            send(tmp, &buf, 1024, 0);

                            //open thread gui du lieu cac user va room cho nguoi vua dang nhap
                            th = new thread(&server::send_data_to_login_user, this, tmp);
                            th->detach();
                            //  list_poll_unlogin.erase(list_poll_unlogin.begin() + i);

                            //mu.unlock();
                            while (unlogin_size < sz)
                                --sz;
                            cout << "user " << *u->get_username() << " dang nhap thanh cong\n";
                        }
                        else
                        {
                            cout << "dang nhap that bai\n";
                            buf.cmd = 100; //dang nhap that bai
                            send(list_poll_unlogin[i].fd, &buf, 1024, 0);
                        }
                    }
                    else
                    {
                        buf.cmd = 100; //dang nhap that bai
                        send(list_poll_unlogin[i].fd, &buf, 1024, 0);
                    }
                    continue;
                }
                if (buf.cmd == 14)
                {
                    this->server_stanby_sock = list_poll_unlogin[i].fd;
                }
            }
        }
    }
}

void server::chat_client()
{
    cout << "bat dau thread chat client\n";
    int sz, rc, i, len, rv, tmp;
    block_data buf;
    room *r;
    id *p_id;
    list_poll_connected[0].fd = 0;
    list_poll_connected[0].events = POLLIN;
    connected_size = 1;
    while (1)
    {
        sz = connected_size;
        cout << "poll connected sz: " << sz << endl;
        rc = poll(list_poll_connected, sz, 200);
        // cout<<"tao co chay xuong day nha may "<<rc<<endl;
        if (rc < 0)
        {
            perror("  poll() failed");
            continue;
        }
        for (i = 0; i < sz; ++i)
        {
            if (list_poll_connected[i].events == 0 || i == 0)
                continue;
            if (list_poll_connected[i].revents != POLLIN)
                continue;
            // memset(&buf, 0, 1024);
            len = read(list_poll_connected[i].fd, &buf, 1024);
            cout << "\nchat client thread nhan duoc " << len << " byte\n";
            cout << "noi dung nhu sau: nguoinhan: " << buf.recver_usename << "\nnoi dung: " << buf.mess << endl
                 << "cmd = " << buf.cmd << endl;

            if (len <= 0)
            {
                //client ngat ket noi...
                //xoa client khoi list id online
                p_id = remove_id(list_poll_connected[i].fd);

                if (p_id)
                {
                    //gui thong bao den toan client cho biet co nguoi vua thoat
                    buf.cmd = 109;
                    strcpy(buf.sender_username, p_id->get_user()->get_username());
                    send_to_world(&buf);

                    //thoat khoi toan bo cac room
                    delete p_id;
                }

                //close fd
                close(list_poll_connected[i].fd);
                pollfd_erase(list_poll_connected, empty_connected_pos, connected_size, i);
                while (connected_size < sz)
                    --sz;
                continue;
            }
            if (len != 1024)
                continue;
            switch (buf.cmd)
            {
                //ok
            case 3: //gui tin nhan 1-1
                // cout << "co nhan dc tin nhan\n";
                send(this->get_id_from_name(buf.recver_usename)->get_key(), &buf, 1024, 0);
                break;
                //chua test
            case 4: //sendpl gui cho room
                this->get_room_from_ID(buf.room_id)->send_message(&buf);
                break;
                //chua test
            case 5:                                                                                            //tao room
                r = get_id_from_name(buf.sender_username)->create_new_room(buf.recver_usename, buf.room_pass); //ten room la recv_username
                if (r)
                {
                    this->add_room(r);
                    buf.cmd = 107;
                    buf.room_id = r->get_key();
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                else
                {
                    buf.cmd = 106; //tao room that bai
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 6: //xoa room chua test (chua gui cho server stand by)
                if (this->remove_room(buf.room_id, buf.sender_username))
                {
                    buf.cmd = 108;
                    send_to_world(&buf);
                    //chua gui cho sv stand by
                }
                break;
            case 7: //moi ban vao room //sai chua sua
                if (get_id_from_name(buf.sender_username)->invite_friend(buf.recver_usename, buf.room_id))
                    send(list_poll_connected[i].fd, "1", 1, 0);
                else
                    send(list_poll_connected[i].fd, "0", 1, 0);
                break;
            case 8: //duoi ban khoi room //s
                if (get_id_from_name(buf.sender_username)->kick_out(buf.recver_usename, buf.room_id))
                    send(list_poll_connected[i].fd, "1", 1, 0);
                else
                    send(list_poll_connected[i].fd, "0", 1, 0);
                break;
            case 9: //dang xuat
                break;
            case 10: //xin vao room //s
                if (get_id_from_name(buf.sender_username)->join_room(buf.room_id, buf.room_pass))
                    send(list_poll_connected[i].fd, "1", 1, 0);
                else
                    send(list_poll_connected[i].fd, "0", 1, 0);
                break;
            case 11: //doi mat khau user

                break;
            case 12: //doi mat khau room
                if (get_room_from_ID(buf.room_id)->edit_pass(buf.sender_username, buf.room_pass))
                    send(list_poll_connected[i].fd, "1", 1, 0);
                else
                    send(list_poll_connected[i].fd, "0", 1, 0);
                break;
            default:
                break;
            }
        }
    }
}

id *server::get_id_from_fd(int fd)
{
    for (auto &c : this->list_online)
        if (c->get_key() == fd)
            return c;
}

//void server::operator()(){}
void server::trans_data_to_standby_sv()
{
}

/*gui du lieu cho client khi no vua dang nhap thanh cong*/
void server::send_data_to_login_user(int sock)
{
    struct block_user
    {
        int cmd;
        int size;          //so nguoi
        char data[33][30]; //user name
        char x[26];
    };
    struct room_inf
    {
        int id;
        int sz_u;
        char name[30];
        char pass[30];
        char captain[30];
        int u[100];
    };
    struct block_room
    {
        int cmd;
        int size;
        room_inf data[2];
        char x[20];
    };

    //gui tat ca user online
    block_user t;
    t.size = 0;
    t.cmd = 102;
    for (auto &c : this->list_online)
    {
        if (t.size == 33)
        {
            send(sock, &t, 1024, 0);
            t.size = 0;
        }
        strcpy(t.data[t.size], c->get_user()->get_username());
        ++t.size;
    }
    send(sock, &t, 1024, 0);

    //gui tat ca cac phong
    block_room *r = (block_room *)&t;
    r->size = 0;
    r->cmd = 103;
    for (auto &c : this->list_room)
    {
        if (r->size == 2)
        {
            send(sock, r, 1024, 0);
            r->size = 0;
        }
        c->get_room_data(&(r->data[r->size]));
        ++r->size;
    }
    send(sock, r, 1024, 0);
}

void server::send_to_world(void *buf)
{
    for (auto &c : list_online)
    {
        send(c->get_key(), buf, 1024, 0);
    }
}