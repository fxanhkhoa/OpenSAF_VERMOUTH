
#include "../include/server.h"
extern List<user> user_data;
extern string file_path;

server *server::sv = NULL;

volatile int off = 1;

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
        //gui cho server standby
        if (server_stanby_sock > 0)
        {
            block_data buf;
            buf.cmd = 1004;
            buf.u_id = res->get_id();
            strcpy(buf.sender_username, u_name);
            strcpy(buf.sender_pass, u_pass);
            buf.room_id = user::user_id;
            send(server_stanby_sock, &buf, 1024, 0);
        }
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

void *server::listen_connect()
{
    syslog(LOG_INFO, "start listen_connect thread");
    thread *th;
    block_data buf;

    system("echo ubuntu|sudo -S ifconfig eth0:server 10.0.3.105");
    syslog(LOG_INFO, "listen_connect thread da bat alias ip");
    int listen_sock = open_listening_socket(8888);
    if (listen_sock >= 0)
    {
        syslog(6, "onpen listening sock complete!");
    }

    cout << "socket " << listen_sock << endl;
    pollfd t;
    t.fd = listen_sock;
    t.events = POLLIN;
    pollfd_push(list_poll_unlogin, empty_unlogin_pos, unlogin_size, t);
    int rc = -1, i, new_sd, sz, tmp;

    while (state == 1)
    {
        sz = unlogin_size;
        //cout << "sz= " << unlogin_size << " dang lang nghe tren poll\n";
        rc = poll(list_poll_unlogin, sz, 100);
        //cout << "rc: " << rc << endl;

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
                //cout << "new connection\n";
                syslog(6, "new connection sz = %d", sz);
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
                    // close(server_stanby_sock);

                    if (server_stanby_sock == list_poll_unlogin[i].fd)
                    {
                        shutdown(server_stanby_sock, SHUT_RDWR);
                        close(server_stanby_sock);
                        mu.lock();
                        server_stanby_sock = -1;
                        mu.unlock();
                    }
                    shutdown(list_poll_unlogin[i].fd,SHUT_RDWR);
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
                    cout << "len = " << len << endl;
                    cout << "do dai khac 1024\n";
                    ((char *)&buf)[len] = 0;
                    cout << "noi dung: " << (char *)&buf;
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
                            syslog(6, "user %s logined", u->get_username());
                            //chuyen socket cua user da dang nhap qua list connected
                            pollfd_push(list_poll_connected, empty_connected_pos, connected_size, list_poll_unlogin[i]);
                            //xoa socket cua user da dang nhap tai list unlogin
                            pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);

                            //gui tin hieu bao cho client biet no da dang nhap thanh cong (code 101)
                            buf.u_id = u->get_id();

                            buf.cmd = 101; //dang nhap thanh cong
                            send(tmp, &buf, 1024, 0);

                            //open thread gui du lieu cac user va room cho nguoi vua dang nhap
                            th = new thread(&server::send_data_to_login_user, this, tmp);
                            th->detach();

                            /*bao cho cac client va standby server biet co nguoi moi dang nhap*/
                            buf.cmd = 997;
                            send_to_world(&buf);
                            if (server_stanby_sock != -1)
                            {
                                send(server_stanby_sock, &buf, 1024, 0);
                            }
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
                if (buf.cmd == 999) //reconnect
                {
                    user *u = user_data.search(buf.sender_username);
                    id *t = new id(u, list_poll_unlogin[i].fd);
                    this->list_online.insert(t);

                    tmp = list_poll_unlogin[i].fd;

                    //chuyen socket cua user da dang nhap qua list connected
                    pollfd_push(list_poll_connected, empty_connected_pos, connected_size, list_poll_unlogin[i]);
                    //xoa socket cua user da dang nhap tai list unlogin
                    pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);

                    buf.cmd = 998; //reconnect successful
                    send(tmp, &buf, 1024, 0);

                    //mu.unlock();
                    while (unlogin_size < sz)
                        --sz;
                }
                if (buf.cmd == 1002) //la server standby
                {
                    this->server_stanby_sock = list_poll_unlogin[i].fd;
                    syslog(LOG_INFO, "stand by sock connected");
                }
            }
        }
    }
    shutdown(listen_sock, SHUT_RDWR);
    close(listen_sock);
    for (int j = 0; j < unlogin_size; ++j)
    {
        if (server_stanby_sock != list_poll_unlogin[j].fd)
        {
            shutdown(list_poll_unlogin[j].fd, SHUT_RDWR);
            close(list_poll_unlogin[j].fd);
        }
    }
    syslog(LOG_INFO, "chuan bi end listen_connect thread");
    //  server_stanby_sock = -1;
    while (empty_unlogin_pos.size() > 0)
        empty_unlogin_pos.pop();
    unlogin_size = 0;
    while (off)
        ;
    syslog(LOG_INFO, "end listen_connect thread");
}

void *server::chat_client()
{

    int sz, rc, i, len, rv, tmp;
    block_data buf;
    room *r;
    id *p_id;
    list_poll_connected[0].fd = 0;
    list_poll_connected[0].events = POLLIN;
    connected_size = 1;
    syslog(LOG_INFO, "start chat_client thread");
    while (state == 1)
    {
        sz = connected_size;
        cout << "poll connected sz: " << sz << endl;

        rc = poll(list_poll_connected, sz, 100);
        // cout<<"tao co chay xuong day nha may "<<rc<<endl;
        if (rc < 0)
        {
            perror("  poll() failed");
            continue;
        }
        if (rc == 0)
            continue;
        for (i = 0; i < sz; ++i)
        {
            if (list_poll_connected[i].events == 0 || i == 0)
                continue;
            if (list_poll_connected[i].revents != POLLIN)
                continue;
            // memset(&buf, 0, 1024);
            len = read(list_poll_connected[i].fd, &buf, 1024);
            // cout << "\nchat client thread nhan duoc " << len << " byte\n";
            // cout << "noi dung nhu sau\n nguoinhan: " << buf.recver_usename << "\nnoi dung: " << buf.mess << endl
            //      << "cmd = " << buf.cmd << endl;

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
                    buf.u_id = p_id->get_user()->get_id();
                    send_to_world(&buf);

                    //gui cho server standby
                    send(server_stanby_sock, &buf, 1024, 0);
                    //thoat khoi toan bo cac room
                    delete p_id;
                }

                //close fd
                shutdown(list_poll_connected[i].fd, SHUT_RDWR);
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
                p_id = this->get_id_from_name(buf.recver_usename);
                if (p_id)
                    send(p_id->get_key(), &buf, 1024, 0);
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

                break;
            case 8: //duoi ban khoi room //s

                break;
            case 9: //dang xuat
                break;
            case 10: //xin vao room //s

                break;
            case 11: //doi mat khau user

                break;
            case 12: //doi mat khau room

                break;
            default:
                break;
            }
        }
    }
    for (int j = 1; j < connected_size; ++j)
    {
        shutdown(list_poll_connected[j].fd, SHUT_RDWR);
        close(list_poll_connected[j].fd);
    }
    while (empty_connected_pos.size() > 0)
        empty_connected_pos.pop();
    connected_size = 0;
    list_online.clear();
    syslog(LOG_INFO, "end chat client_ thread");
    mu.lock();
    off = 0;
    mu.unlock();
    // list_room.clear();
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
    struct block_uid
    {
        int cmd;
        int sz;
        int data[254];
    };
    block_room buf;
    buf.size = 0;
    buf.cmd = 103;
    for (auto &c : this->list_room)
    {
        if (buf.size == 2)
        {
            send(server_stanby_sock, &buf, 1024, 0);
            buf.size = 0;
        }
        c->get_room_data(&(buf.data[buf.size]));
        ++buf.size;
    }
    send(server_stanby_sock, &buf, 1024, 0);
    block_uid *t = (block_uid *)&buf;
    t->cmd = 1001;
    t->sz = 0;
    for (auto &c : list_online)
    {
        if (t->sz == 254)
        {
            send(server_stanby_sock, t, 1024, 0);
            t->sz = 0;
        }
        t->data[t->sz++] = c->get_user()->get_id();
    }
    send(server_stanby_sock, t, 1024, 0);
}

/*gui du lieu cho client khi no vua dang nhap thanh cong*/
void server::send_data_to_login_user(int sock)
{
    struct udata
    {

        int id;
        char n[30];
    };
    struct block_user
    {
        int cmd;
        int size;       //so nguoi
        udata data[29]; //user name
        char x[30];
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
        strcpy(t.data[t.size].n, c->get_user()->get_username());
        t.data[t.size].id = c->get_user()->get_id();
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

void server::pollfd_push(pollfd *l, stack<int> &c, int &size, pollfd val)
{
    mu.lock();
    if (c.empty())
    {
        l[size++] = val;
    }

    else
    {
        l[c.top()] = val;
        c.pop();
    }
    mu.unlock();
}
void server::pollfd_erase(pollfd *l, stack<int> &c, int &size, int pos)
{
    mu.lock();
    l[pos].fd = -1;
    if (pos == size - 1)
    {
        --size;
    }
    else
    {

        c.push(pos);
    }
    mu.unlock();
}

server::server()
{
    connected_size = unlogin_size = 0;
    server_stanby_sock = -1;
    user::user_id = user_data.get_max_ID();
    syslog(6, "max user id: %d", user::user_id);
}

void server::stand_by()
{
    syslog(6, "start stand-by thread");
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
    struct block_uid
    {
        int cmd;
        int sz;
        int data[254];
    };
    block_data buf;
    block_room *r_buf;
    block_uid *u_buf;
    user *usr;
    fstream f;
    int len;
    if (server_stanby_sock >= 0)
    {
        buf.cmd = 1009;
        send(server_stanby_sock, &buf, 1024, 0);

        system("echo ubuntu|sudo -S ifconfig eth0:server down");
        shutdown(server_stanby_sock, SHUT_RDWR);
        close(server_stanby_sock);
        server_stanby_sock = -1;
    }

    while (server_stanby_sock < 0 && state == 2)
    {
        server_stanby_sock = connect_to_sv("10.0.3.105", 8888);
        if (server_stanby_sock > 0)
        {
            buf.cmd = 1002;
            send(server_stanby_sock, &buf, 1024, 0);
            syslog(6, "connected to active server");
        }
    }

    while (state == 2)
    {
        len = recv(server_stanby_sock, &buf, 1024, 0);
        if (len == 0)
        {
            server_stanby_sock = connect_to_sv("10.0.3.105", 8888);
            if (server_stanby_sock >= 0)
            {
                buf.cmd = 1002;
                send(server_stanby_sock, &buf, 1024, 0);
            }
            continue;
        }
        if (len != 1024)
            continue;

        switch (buf.cmd)
        {
        case 103: //room data

            break;
        // case 1001: //userdata
        //     u_buf = (block_uid *)&buf;
        //     while (--u_buf->sz >= 0)
        //         u_online[u_buf->data[u_buf->sz]] = 1;
        //     break;
        // case 109: //co nguoi dang xuat
        //     u_online[buf.u_id] = 0;
        //     break;
        case 1004: //co nguoi vua dang ky
            usr = user_data.insert(user(buf.sender_username, buf.sender_pass, buf.u_id));
            mu_lock_file.lock();
            f.open(file_path, ios::out | ios::binary);
            f.write((char *)usr, sizeof(user));
            f.close();
            mu_lock_file.unlock();
            break;
            // case 1002:
            //     buf.cmd = 1002;
            //     send(server_stanby_sock, &buf, 1024, 0);
            //     break;
        case 1009:
            shutdown(server_stanby_sock, SHUT_RDWR);
            close(server_stanby_sock);
            server_stanby_sock = -1;

            syslog(6, "1009 end stand-by thread");
            return;
            break;

        default:
            break;
        }
    }
    if (server_stanby_sock > 0)
    {
        shutdown(server_stanby_sock, SHUT_RDWR);
        close(server_stanby_sock);
        server_stanby_sock = -1;
    }
    syslog(6, "end stand-by thread");
}
