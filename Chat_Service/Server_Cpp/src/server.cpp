
#include "../include/server.h"
extern List<user> user_data;
extern SaImmHandleT immHandle;
server *server::sv = NULL;
extern SaStringT StringToSaString(const std::string &input);
volatile int off = 1;
extern Oi *oi;
extern vector<string> search_friend(const char *u_name);
extern void sleep_ms(int milliseconds);
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

SaAisErrorT create_object(int u_id, const char *u_name, const char *u_pass)
{
    SaAisErrorT rc;
    SaImmAdminOwnerHandleT ownerHandle;
    std::string classNameS = "USER";
    const SaImmClassNameT className = (SaImmClassNameT)(classNameS.c_str());
    std::string adminOwnerNameS = "server";
    const SaImmClassNameT adminOwnerName = (SaImmClassNameT)(adminOwnerNameS.c_str());
    rc = saImmOmAdminOwnerInitialize(immHandle, adminOwnerName, SA_TRUE, &ownerHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "Admin owner creation succeed");
    else
        syslog(6, "Admin owner creation failed with error: %d", rc);
    /*******/

    /*Initialize a CCB handle */
    SaImmCcbHandleT ccbHandle;
    rc = saImmOmCcbInitialize(ownerHandle, 0, &ccbHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "CCB handle creation succeed"); // << std::endl;
    else
        syslog(6, "CCB Handle creation failed with error: %d", rc); //<< std::endl;
    /*******/

    SaStringT attr0Val = StringToSaString("user_name=root");
    SaConstStringT objNameArr[] = {(SaConstStringT)attr0Val, NULL};
    rc = saImmOmAdminOwnerSet_o3(ownerHandle, objNameArr, SA_IMM_SUBLEVEL);
    if (rc == SA_AIS_OK)
        syslog(6, "Set new administrative owner for user_name=root succeed");
    else
        syslog(6, "Set new administrative owner for user_name=root failed with error: %d", rc);
    /*Create configuration object*/
    SaStringT attr1Val = StringToSaString(string(u_name));
    SaImmAttrValueT attrVals1 = &attr1Val;
    SaImmAttrValuesT_2 attrRDNVal = {.attrName = StringToSaString("user_name"),
                                     .attrValueType = SA_IMM_ATTR_SASTRINGT,
                                     .attrValuesNumber = 1,
                                     .attrValues = &attrVals1};
    SaUint32T attr2Val = u_id;
    SaImmAttrValueT attrVals2 = &attr2Val;
    SaImmAttrValuesT_2 attrVal2 = {.attrName = StringToSaString("user_id"),
                                   .attrValueType = SA_IMM_ATTR_SAUINT32T,
                                   .attrValuesNumber = 1,
                                   .attrValues = &attrVals2};

    SaStringT attr4Val = StringToSaString(string(u_pass));
    SaImmAttrValueT attrVals4 = &attr4Val;
    SaImmAttrValuesT_2 attrVal4 = {.attrName = StringToSaString("passwd"),
                                   .attrValueType = SA_IMM_ATTR_SASTRINGT,
                                   .attrValuesNumber = 1,
                                   .attrValues = &attrVals4};

    const SaImmAttrValuesT_2 *attrValArr[] = {&attrRDNVal, &attrVal2, &attrVal4, NULL};
    SaNameT parentName;
    parentName.length = strlen("user_name=root");
    memcpy(parentName.value, "user_name=root", parentName.length);
    rc = saImmOmCcbObjectCreate_2(ccbHandle, className, &parentName, attrValArr);
    if (rc == SA_AIS_OK)
        syslog(6, "Object creation succeed");
    else
        syslog(6, "Object creation failed with error: %d", rc); // << std::endl;

    saImmOmCcbApply(ccbHandle);
    saImmOmCcbFinalize(ccbHandle);
    saImmOmAdminOwnerFinalize(ownerHandle);
    return rc;
}

SaAisErrorT server::add_friend(const char *u_name, const char *friend_name, int flag)
{
    SaAisErrorT rc;
    SaImmAdminOwnerHandleT ownerHandle;

    std::string adminOwnerNameS = "server";
    const SaImmClassNameT adminOwnerName = (SaImmClassNameT)(adminOwnerNameS.c_str());
    rc = saImmOmAdminOwnerInitialize(immHandle, adminOwnerName, SA_TRUE, &ownerHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "Admin owner creation succeed");
    else
        syslog(6, "Admin owner creation failed with error: %d", rc);
    /*******/

    /*Initialize a CCB handle */
    SaImmCcbHandleT ccbHandle;
    rc = saImmOmCcbInitialize(ownerHandle, 0, &ccbHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "CCB handle creation succeed"); // << std::endl;
    else
        syslog(6, "CCB Handle creation failed with error: %d", rc); //<< std::endl;
    /*******/

    SaStringT attr0Val = StringToSaString("user_name=root");
    SaConstStringT objNameArr[] = {(SaConstStringT)attr0Val, NULL};
    rc = saImmOmAdminOwnerSet_o3(ownerHandle, objNameArr, SA_IMM_SUBLEVEL);
    if (rc == SA_AIS_OK)
        syslog(6, "Set new administrative owner for user_name=root succeed");
    else
        syslog(6, "Set new administrative owner for user_name=root failed with error: %d", rc);
    /*Modify object*/
    SaStringT attr4MVal = StringToSaString(string(friend_name));
    SaImmAttrValueT attrVals4M = &attr4MVal;

    SaImmAttrValueT attrValsArr[] = {&attr4MVal, NULL};
    std::string name = "friend";
    SaImmAttrValuesT_2 attrVal4M = {.attrName = StringToSaString(name),
                                    .attrValueType = SA_IMM_ATTR_SASTRINGT,
                                    .attrValuesNumber = 1,
                                    .attrValues = attrValsArr};
    SaImmAttrModificationT_2 attrMod2 = {.modType = (flag == 1 ? SA_IMM_ATTR_VALUES_ADD : SA_IMM_ATTR_VALUES_DELETE),
                                         .modAttr = attrVal4M};

    const SaImmAttrModificationT_2 *attrMods1[] = {&attrMod2, NULL};
    std::string name2 = u_name;
    if (name2 == "root")
        name2 = "user_name=root";
    else
        name2 += ",user_name=root";
    SaConstStringT objName = StringToSaString(name2);
    rc = saImmOmCcbObjectModify_o3(ccbHandle, objName, attrMods1);

    /*Aplly configuration change bundle*/
    rc = saImmOmCcbApply(ccbHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "add friend succeed");
    else
        syslog(6, "add friend error %d", rc); // << std::endl;

    saImmOmCcbFinalize(ccbHandle);
    saImmOmAdminOwnerFinalize(ownerHandle);
    return rc;
}

user *server::create_new_user(const char *u_name, const char *u_pass)
{
    if (strlen(u_name) > 29 || strlen(u_pass) > 29)
    {
        syslog(6, "signup failed!");
        return NULL;
    }
    user *res = user_data.search(u_name);
    if (res)
        return NULL;
    SaAisErrorT rc;

    rc = create_object(user::user_id + 1, u_name, u_pass);
    if (rc != SA_AIS_OK)
    {
        syslog(6, "signup failed!");

        return NULL;
    }
    // system(("immcfg -m currentCount=any -a value=" + to_string(user::user_id)).c_str());
    res = user_data.insert(u_name, u_pass);

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

    return res;
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

// void server::send_us_stanby()
// {
//     struct _US
//     {
//         char u_name[30];
//         char u_pass[30];
//         int u_id;
//     };
//     struct dat
//     {
//         int cmd;
//         int sz;

//         _US d[15];
//         char xxx[56];
//     };
//     vector<user *> v;
//     user_data.to_vector(v);
//     dat buf;
//     buf.cmd = 1010;
//     buf.sz = 0;
//     for (int i = 0; i < v.size(); ++i)
//     {
//         if (buf.sz == 15)
//         {
//             send(server_stanby_sock, &buf, 1024, 0);
//             buf.sz = 0;
//         }
//         v[i]->get_name_pass_id(&(buf.d[buf.sz++]));
//     }
//     send(server_stanby_sock, &buf, 1024, 0);
// }

///////Doi mat khau
SaAisErrorT server::edit_passw(const char *user_name, const char *pass)
{
    SaAisErrorT rc;
    SaImmAdminOwnerHandleT ownerHandle;
    // std::string classNameS = "USER";
    // const SaImmClassNameT className = (SaImmClassNameT)(classNameS.c_str());
    std::string adminOwnerNameS = "server";
    const SaImmClassNameT adminOwnerName = (SaImmClassNameT)(adminOwnerNameS.c_str());
    rc = saImmOmAdminOwnerInitialize(immHandle, adminOwnerName, SA_TRUE, &ownerHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "Admin owner creation succeed");
    else
        syslog(6, "Admin owner creation failed with error: %d", rc);

    /*Initialize a CCB handle */
    SaImmCcbHandleT ccbHandle;
    rc = saImmOmCcbInitialize(ownerHandle, 0, &ccbHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "CCB handle creation succeed"); // << std::endl;
    else
        syslog(6, "CCB Handle creation failed with error: %d", rc); //<< std::endl;
    SaStringT attr0Val = StringToSaString("user_name=root");
    SaConstStringT objNameArr[] = {(SaConstStringT)attr0Val, NULL};
    rc = saImmOmAdminOwnerSet_o3(ownerHandle, objNameArr, SA_IMM_SUBLEVEL);
    if (rc == SA_AIS_OK)
        syslog(6, "Set new administrative owner for user_name=root succeed");
    else
        syslog(6, "Set new administrative owner for user_name=root failed with error: %d", rc);
    /*Modify object*/
    SaStringT attr3MVal = StringToSaString(pass);
    SaImmAttrValueT attrVals3M = &attr3MVal;
    SaImmAttrValuesT_2 attrVal3M = {.attrName = StringToSaString("passwd"),
                                    .attrValueType = SA_IMM_ATTR_SASTRINGT,
                                    .attrValuesNumber = 1,
                                    .attrValues = &attrVals3M};
    SaImmAttrModificationT_2 attrMod1 = {.modType = SA_IMM_ATTR_VALUES_REPLACE,
                                         .modAttr = attrVal3M};

    const SaImmAttrModificationT_2 *attrMods[] = {&attrMod1, NULL};

    std::string name2 = user_name;
    if (name2 == "root")
        name2 = "user_name=root";
    else
        name2 += ",user_name=root";
    SaConstStringT objName = StringToSaString(name2);

    rc = saImmOmCcbObjectModify_o3(ccbHandle, objName, attrMods);
    if (rc == SA_AIS_OK)
        syslog(6, "Object modification succeed");
    else
        syslog(6, "Object modification failed with error: %d", rc);
    /*******/

    /*Aplly configuration change bundle*/
    rc = saImmOmCcbApply(ccbHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "edit pass succeed");
    else
        syslog(6, "edit pass error %d", rc); // << std::endl;

    saImmOmCcbFinalize(ccbHandle);
    saImmOmAdminOwnerFinalize(ownerHandle);
    return rc;
}
void server::send_room_stanby()
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
    block_room t;
    block_room *r = &t;
    r->size = 0;
    r->cmd = 103;
    for (auto &c : this->list_room)
    {
        if (r->size == 2)
        {
            send(server_stanby_sock, r, 1024, 0);
            r->size = 0;
        }
        c->get_room_data(&(r->data[r->size]));
        ++r->size;
    }
    send(server_stanby_sock, r, 1024, 0);
}
void *server::listen_connect()
{

    syslog(LOG_INFO, "start listen_connect thread");
    thread *th;
    block_data buf;

    system("echo ubuntu|sudo -S ifconfig eth0:server 10.0.3.105");
    syslog(LOG_INFO, "listen_connect thread da bat alias ip");

    listen_sock = open_listening_socket(8888);

    //cout << "socket " << listen_sock << endl;
    pollfd t;
    t.fd = listen_sock;
    t.events = POLLIN;
    pollfd_push(list_poll_unlogin, empty_unlogin_pos, unlogin_size, t);
    int rc = -1, i, new_sd, sz, tmp;

    while (state == 1)
    {
        sz = unlogin_size;
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

                int len = read(list_poll_unlogin[i].fd, &buf, 1024);
                if (len <= 0)
                {

                    if (server_stanby_sock == list_poll_unlogin[i].fd)
                    {
                        shutdown(server_stanby_sock, SHUT_RDWR);
                        close(server_stanby_sock);
                        mu.lock();
                        server_stanby_sock = -1;
                        mu.unlock();
                    }
                    shutdown(list_poll_unlogin[i].fd, SHUT_RDWR);
                    close(list_poll_unlogin[i].fd);
                    //list_poll_unlogin.erase(list_poll_unlogin.begin() + i);
                    pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);
                    while (unlogin_size < sz)
                        --sz;
                    continue;
                }
                if (len != 1024)
                {
                    continue;
                }

                // if (buf.cmd == 1010)
                // {
                //     thread ss(&server::send_us_stanby, this);
                //     ss.detach();
                //     continue;
                // }

                if (buf.cmd == 1015)
                {
                    thread ss(&server::send_room_stanby, this);
                    ss.detach();
                    continue;
                }

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
                    buf.sender_username[29] = 0;
                    buf.sender_pass[29] = 0;
                    if (get_id_from_name(buf.sender_username))
                    {
                        syslog(6, "%s already login, can't login again", buf.sender_username);
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
                            syslog(6, "ok");

                            // th = new thread(&server::send_data_to_login_user, this, tmp);
                            // th->detach();

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
                            //cout << "user " << *u->get_username() << " dang nhap thanh cong\n";
                        }
                        else
                        {

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
                if (buf.cmd == 125) //reconnect
                {
                    id *t = get_id_from_name(buf.sender_username);
                    user *u;
                    if (t)
                        continue;

                    u = user_data.search(buf.sender_username);
                    if (!u)
                        continue;
                    t = new id(u, list_poll_unlogin[i].fd);
                    this->list_online.insert(t);

                    tmp = list_poll_unlogin[i].fd;
                    this->reconnect_complete(t);
                    //chuyen socket cua user da dang nhap qua list connected
                    pollfd_push(list_poll_connected, empty_connected_pos, connected_size, list_poll_unlogin[i]);
                    //xoa socket cua user da dang nhap tai list unlogin
                    pollfd_erase(list_poll_unlogin, empty_unlogin_pos, unlogin_size, i);

                    buf.cmd = 124; //reconnect successful
                    send(tmp, &buf, 1024, 0);
                    syslog(6, "user %s reconnect successful", buf.sender_username);
                    //mu.unlock();
                    while (unlogin_size < sz)
                        --sz;
                    continue;
                }
                if (buf.cmd == 1002) //la server standby
                {
                    this->server_stanby_sock = list_poll_unlogin[i].fd;
                    syslog(LOG_INFO, "stand by sock connected");
                }
            }
        }
    }
    //start
    shutdown(listen_sock, SHUT_RDWR);
    close(listen_sock);
    for (int j = 1; j < unlogin_size; ++j)
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
    if (server_stanby_sock >= 0)
    {
        buf.cmd = 1009;
        send(server_stanby_sock, &buf, 1024, 0);

        system("echo ubuntu|sudo -S ifconfig eth0:server down");
        shutdown(server_stanby_sock, SHUT_RDWR);
        close(server_stanby_sock);
        server_stanby_sock = -1;
    }
    else
        system("echo ubuntu|sudo -S ifconfig eth0:server down");
    for (auto &c : list_room)
        c->clear();
    if (oi)
    {
        delete oi;
        oi = NULL;
    }
    syslog(LOG_INFO, "end listen_connect thread");
}
void server::send_list_friend(int sock)
{
    syslog(6, "runing send friend list");
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
    block_user buf;
    vector<string> dat;
    id *pp = get_id_from_fd(sock);
    if (!pp)
        return;
    user *u = pp->get_user();
    u->get_friend_list(dat);
    buf.cmd = 22;
    buf.size = 0;
    for (auto &c : dat)
    {
        if (buf.size == 29)
        {
            send(sock, &buf, 1024, 0);
            buf.cmd = 0;
        }
        strcpy(buf.data[buf.size].n, c.c_str());
        buf.data[buf.size++].id = user_data.search(c.c_str())->get_id();
        syslog(6, "%s", c.c_str());
    }
    send(sock, &buf, 1024, 0);
    syslog(6, "end send friend list thread");
}

void *server::chat_client()
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

    block_room bl_r;
    bl_r.size = 1;

    /////////
    int sz, rc, i, len, rv, tmp;
    block_data buf;
    thread *th;
    room *r;
    user *p_us, *p_us2;
    id *p_id;
    list_poll_connected[0].fd = 0;
    list_poll_connected[0].events = POLLIN;
    connected_size = 1;
    syslog(LOG_INFO, "start chat_client thread");
    while (state == 1)
    {
        sz = connected_size;
        //cout << "poll connected sz: " << sz << endl;

        rc = poll(list_poll_connected, sz, 50);
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
                syslog(6, " close connection");
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
            mu.lock();
            syslog(6, "cmd: %d  nguoi gui %s   nguoi nhan %s   noi dung %s", buf.cmd, buf.sender_username, buf.recver_usename, buf.mess);

            mu.unlock();
            switch (buf.cmd)
            {

            case 3: //gui tin nhan 1-1

                p_id = this->get_id_from_name(buf.recver_usename);
                if (p_id)
                    send(p_id->get_key(), &buf, 1024, 0);
                break;
                //chua test
            case 4: //sendpl gui cho room

                r = this->get_room_from_ID(buf.room_id);
                p_id = get_id_from_fd(list_poll_connected[i].fd);
                if (r && p_id && r->check_belong_room(p_id))
                    r->send_message(&buf);
                break;
                //chua test
            case 5: //tao room
                p_id = get_id_from_name(buf.sender_username);
                if (p_id)
                    r = p_id->create_new_room(buf.recver_usename, buf.room_pass); //ten room la recv_username
                if (r)
                {
                    this->add_room(r);
                    buf.cmd = 107;
                    buf.room_id = r->get_key();
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                    buf.cmd = 110;

                    buf.u_id = get_id_from_name(buf.sender_username)->get_user()->get_id();
                    send_to_world(&buf);
                    send(server_stanby_sock, &buf, 1024, 0);
                }
                else
                {
                    buf.cmd = 106;
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 15:
                r = get_room_from_ID(buf.room_id);
                p_id = get_id_from_fd(list_poll_connected[i].fd);
                if (r && p_id && r->check_belong_room(p_id))
                {
                    th = new thread(&room::data_client_res, r, list_poll_connected[i].fd);
                    th->detach();
                }
                break;
            case 6:

                if (this->remove_room(buf.room_id, buf.sender_username))
                {

                    buf.cmd = 108;
                    send_to_world(&buf);
                    buf.cmd = 6;
                    send(server_stanby_sock, &buf, 1024, 0);
                }
                else
                {
                    buf.cmd = 300;
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 7: //moi ban vao room

                p_id = this->get_id_from_name(buf.recver_usename);

                if (p_id)
                {
                    r = this->get_room_from_ID(buf.room_id);

                    if (r && !(r->check_belong_room(p_id)))
                    {

                        //Thong bao cho user trong room biet co nguoi moi vao phong
                        buf.cmd = 91;
                        r->send_message(&buf);

                        r->add_people(p_id);
                        p_id->add_room(r);

                        buf.cmd = 81; //moi thanh cong
                        send(list_poll_connected[i].fd, &buf, 1024, 0);

                        //co nguoi moi ban vao phong
                        r->get_room_data(&(bl_r.data[0]));
                        bl_r.cmd = 82;
                        send(p_id->get_key(), &bl_r, 1024, 0);
                        buf.cmd = 7;
                        buf.u_id = p_id->get_user()->get_id();
                        if (server_stanby_sock >= 0)
                            send(server_stanby_sock, &buf, 1024, 0);
                    }
                    else
                    {
                        buf.cmd = 80; //moi that bai
                        send(list_poll_connected[i].fd, &buf, 1024, 0);
                    }
                }
                else
                {
                    buf.cmd = 80; //moi that bai
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 8: //duoi ban khoi room
                p_id = this->get_id_from_name(buf.sender_username);
                r = this->get_room_from_ID(buf.room_id);
                if (p_id && r && r->check_owner(p_id->get_user()))
                {
                    p_id = this->get_id_from_name(buf.recver_usename);
                    if (p_id != NULL)
                    {
                        if (r->check_belong_room(p_id))
                        {

                            r->remove_people(p_id);
                            p_id->remove_room(r);
                            buf.cmd = 85; //duoi thanh cong
                            send(list_poll_connected[i].fd, &buf, 1024, 0);
                            buf.cmd = 86; //co nguoi duoi ban khoi phong
                            send(p_id->get_key(), &buf, 1024, 0);

                            //Thong bao co 1 nguoi thoat khoi phong
                            buf.cmd = 92;
                            r->send_message(&buf);

                            buf.u_id = p_id->get_key();
                            if (server_stanby_sock >= 0)
                            {
                                send(server_stanby_sock, &buf, 1024, 0);
                            }
                        }
                        else
                        {

                            buf.cmd = 84; //duoi that bai
                            send(list_poll_connected[i].fd, &buf, 1024, 0);
                        }
                    }
                    else
                    {

                        buf.cmd = 84; //duoi that bai
                        send(list_poll_connected[i].fd, &buf, 1024, 0);
                    }
                }
                else
                {
                    buf.cmd = 84; //duoi that bai
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }

                break;
            case 9: //dang xuat
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
                    if (server_stanby_sock >= 0)
                        send(server_stanby_sock, &buf, 1024, 0);
                    //thoat khoi toan bo cac room
                    delete p_id;
                }

                pollfd_push(list_poll_unlogin, empty_unlogin_pos, unlogin_size, list_poll_connected[i]);
                pollfd_erase(list_poll_connected, empty_connected_pos, connected_size, i);
                while (connected_size < sz)
                    --sz;

                break;
            case 13: //xin vao room
                p_id = this->get_id_from_name(buf.sender_username);
                r = this->get_room_from_ID(buf.room_id);
                if (r && r->check_pass(buf.room_pass))
                {
                    if (!(r->check_belong_room(p_id)))
                    {
                        //Thong bao cho nhung user trong phong biet co nguoi moi vao phong
                        buf.cmd = 91;
                        strcpy(buf.recver_usename, buf.sender_username);
                        r->send_message(&buf);

                        r->add_people(p_id);
                        p_id->add_room(r);

                        //vao phong thanh cong
                        r->get_room_data(&(bl_r.data[0]));
                        bl_r.cmd = 87;
                        send(p_id->get_key(), &bl_r, 1024, 0);

                        if (server_stanby_sock >= 0)
                        {
                            buf.cmd = 7;
                            buf.u_id = p_id->get_key();
                            send(server_stanby_sock, &buf, 1024, 0);
                        }
                    }
                    else
                    {
                        buf.cmd = 88; //vao phong that bai
                        send(list_poll_connected[i].fd, &buf, 1024, 0);
                    }
                }
                else
                {
                    buf.cmd = 88; //vao phong that bai
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 11: //doi mat khau user
                if (strlen(buf.sender_pass) <= 29 && strlen(buf.sender_username) <= 29)
                {
                    if (edit_passw(buf.sender_username, buf.sender_pass) == 1)
                    {
                        p_us = user_data.search(buf.sender_username);
                        if (p_us)
                            p_us->set_pass(buf.sender_pass);
                        buf.cmd = 35;
                        if (server_stanby_sock >= 0)
                            send(server_stanby_sock, &buf, 1024, 0);
                    }

                    else
                    {
                        buf.cmd = 34;
                    }
                }
                else
                    buf.cmd = 34;
                send(list_poll_connected[i].fd, &buf, 1024, 0);

                break;
            case 12: //doi mat khau room
                p_id = this->get_id_from_name(buf.sender_username);
                r = this->get_room_from_ID(buf.room_id);
                if (r && p_id && r->check_owner(p_id->get_user()))
                {
                    r->set_pass(buf.room_pass);
                    buf.cmd = 89; //doi mat khau phong thanh cong
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                    if (server_stanby_sock >= 0)
                    {
                        send(server_stanby_sock, &buf, 1024, 0);
                    }
                }
                else
                {
                    buf.cmd = 90; //doi mat khau phong that bai
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                }
                break;
            case 14: //Thoat phong
                syslog(6, "user %s, thoat khoi phong %d", buf.sender_username, buf.room_id);
                r = this->get_room_from_ID(buf.room_id);
                p_id = this->get_id_from_name(buf.sender_username);
                if (!p_id)
                    break;
                syslog(6, "14-1");
                if (r && r->check_belong_room(p_id))
                {
                    if (r->get_user_onl().size() != 1)
                    {
                        syslog(6, "14-2");
                        p_id->quit_room(r);
                        //Thog bao thoat phong thanh cong
                        buf.cmd = 93;
                        send(p_id->get_key(), &buf, 1024, 0);

                        //Thong bao co 1 nguoi thoat khoi phong
                        buf.cmd = 92;
                        strcpy(buf.recver_usename, buf.sender_username);
                        r->send_message(&buf);

                        if (server_stanby_sock >= 0)
                        {
                            buf.cmd = 14;
                            buf.u_id = p_id->get_key();
                            send(server_stanby_sock, &buf, 1024, 0);
                        }
                    }
                    else
                    {
                        p_id->quit_room(r);
                        //Thog bao thoat phong thanh cong
                        buf.cmd = 93;
                        send(p_id->get_key(), &buf, 1024, 0);
                    }
                }
                else
                {
                    //Thong bao thoat phong that bai
                    buf.cmd = 94;
                    send(p_id->get_key(), &buf, 1024, 0);
                }
                break;

            case 20: //ket ban
                if (user_data.search(buf.recver_usename) == NULL || strcmp(buf.sender_username, buf.recver_usename) == 0)
                {
                    buf.cmd = 30;
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                    break;
                }
                p_us = user_data.search(buf.recver_usename);
                p_us2 = user_data.search(buf.sender_username);
                if (p_us->is_friend(p_us2))
                {
                    buf.cmd = 30;
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                    break;
                }
                if (add_friend(buf.sender_username, buf.recver_usename) == 1 && add_friend(buf.recver_usename, buf.sender_username) == 1)
                {

                    p_us->add_friend(p_us2);
                    p_us2->add_friend(p_us);
                    buf.cmd = 31;
                    if (server_stanby_sock > 0)
                    {
                        send(server_stanby_sock, &buf, 1024, 0);
                    }
                }
                else
                {
                    buf.cmd = 30;
                }
                send(list_poll_connected[i].fd, &buf, 1024, 0);
                break;
            case 21:
                th = new thread(&server::send_list_friend, this, list_poll_connected[i].fd);
                th->detach();
                break;
            case 25: //xoa ban
                if (user_data.search(buf.recver_usename) == NULL)
                {
                    buf.cmd = 32;
                    send(list_poll_connected[i].fd, &buf, 1024, 0);
                    break;
                }
                if (add_friend(buf.sender_username, buf.recver_usename, 0) == 1 && add_friend(buf.recver_usename, buf.sender_username, 0) == 1)
                {
                    buf.cmd = 33;
                }
                else
                {
                    buf.cmd = 32;
                }
                send(list_poll_connected[i].fd, &buf, 1024, 0);

                break;
            case 40: //send to public room

                send_to_world(&buf);
                break;
            case 42:
                th = new thread(&server::send_data_to_login_user, list_poll_connected[i].fd, this);
                th->detach();
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

/*gui du lieu cho client khi no vua dang nhap thanh cong*/
void server::send_data_to_login_user(int sock, server *const sv)
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

    block_user t;
    memset(&t, 0, 1024);
    t.size = 0;
    t.cmd = 102;
    for (auto &c : sv->list_online)
    {
        if (t.size == 33)
        {
            send(sock, &t, 1024, 0);

            memset(&t, 0, 1024);
            t.size = 0;
        }
        strcpy(t.data[t.size].n, c->get_user()->get_username());
        t.data[t.size].id = c->get_user()->get_id();
        ++t.size;
    }
    send(sock, &t, 1024, 0);

    block_room *r = (block_room *)&t;
    memset(r, 0, 1024);
    r->size = 0;
    r->cmd = 42;
    for (auto &c : sv->list_room)
    {
        if (c->empty())
            continue;
        if (r->size == 2)
        {
            send(sock, r, 1024, 0);
            memset(r, 0, 1024);
            r->size = 0;
        }
        c->get_room_data(&(r->data[r->size]));
        ++r->size;
    }
    send(sock, r, 1024, 0);
    syslog(6, "ok2");
}

void server::send_to_world(void *buf)
{
    block_data *p = (block_data *)buf;
    // syslog(6, "%s\n", p->recver_usename);
    // syslog(6, "%s\n", p->sender_username);
    // syslog(6, "%s\n", p->room_pass);
    for (int i = 0; i < connected_size; ++i)
    {
        send(list_poll_connected[i].fd, buf, 1024, 0);
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
    listen_sock = -1;
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
    room *r;
    //fstream f;
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
        sleep_ms(100);
        server_stanby_sock = connect_to_sv("10.0.3.105", 8888);
        if (server_stanby_sock > 0)
        {
            buf.cmd = 1002;
            send(server_stanby_sock, &buf, 1024, 0);
            syslog(6, "connected to active server");
        }
    }
    // if (state == 2 && user_data.empty())
    // {
    //     buf.cmd = 1010; //chua co data trong user_data
    //     send(server_stanby_sock, &buf, 1024, 0);
    // }
    if (state == 2 && list_room.empty())
    {
        buf.cmd = 1015; //chua co room nao
        send(server_stanby_sock, &buf, 1024, 0);
    }

    struct pollfd fds[1];
    fds[0].fd = server_stanby_sock;
    fds[0].events = POLLIN;
    int rc;
    user *p_us1, *p_us2;
    while (state == 2)
    {
        rc = poll(fds, 1, 5);

        if (rc == -1)
        {
            break;
        }

        if (fds[0].revents & POLLIN)
        {
            len = read(fds[0].fd, &buf, 1024);

            if (len == 0)
            {
                //sleep_ms(100);
                if (state != 2)
                    break;
                server_stanby_sock = connect_to_sv("10.0.3.105", 8888);
                if (server_stanby_sock >= 0)
                {
                    fds[0].fd = server_stanby_sock;
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

                r_buf = (block_room *)&buf;
                for (int i = 0; i < r_buf->size; ++i)
                {
                    load_r(&(r_buf->data[i]));
                }
                break;
            case 109:
            case 9: //co nguoi logout
                for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
                {
                    if ((*it)->out_s(buf.u_id))
                    {
                        delete (*it);
                        list_room.erase(it);
                        break;
                    }
                }
                break;
            case 110: //co phong moi tao
                syslog(6, "(standby) co phong moi tao nhan duoc tu active");
                r = new room;
                r->set_room_name(buf.recver_usename);
                r->set_key(buf.room_id);
                r->set_pass(buf.room_pass);
                r->id_captain = buf.u_id;
                r->id_user.insert(buf.u_id);
                if (room::ID < buf.room_id)
                    room::ID = buf.room_id;
                syslog(6, "room id: %d --> id captain: %d --> room name: %s", r->get_key(), buf.u_id, r->get_room_name());
                list_room.insert(r);
                syslog(6, "list_room.size = %d", list_room.size());

                break;
            case 6: //xoa room
                for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
                {
                    syslog(6, "room %s bi xoa", (*it)->get_room_name());
                    if ((*it)->get_key() == buf.room_id)
                    {
                        delete (*it);
                        list_room.erase(it);
                        send_to_world(&buf);
                        break;
                    }
                }

                break;
            case 41: //set chu phong moi
                for (auto &c : list_room)
                {
                    if (c->get_key() == buf.room_id)
                    {
                        c->id_captain = buf.u_id;
                        strcpy(c->name_captain, buf.sender_username);
                        break;
                    }
                }

                break;

            case 1004: //co nguoi vua dang ky
                usr = user_data.insert(user(buf.sender_username, buf.sender_pass, buf.u_id));
                break;
            case 35: //co nguoi doi mat khau thanh cong
                usr = user_data.search(buf.sender_username);
                if (usr)
                {
                    usr->set_pass(buf.sender_pass);
                }
                break;
            case 7: //moi ddc nguoi vao room
                syslog(6, "(standby) co nguoi duoc moi vao phong tu active");
                syslog(6, "id_room: %d --> nguoi moi: %s --> u_id (recv): %d", buf.room_id, buf.sender_username, buf.u_id);
                for (auto &c : list_room)
                {
                    if (c->get_key() == buf.room_id)
                    {
                        c->id_user.insert(buf.u_id);
                        break;
                    }
                }
                break;
            case 89: //doi pass room
                for (auto &c : list_room)
                {
                    if (c->get_key() == buf.room_id)
                    {
                        c->set_pass(buf.room_pass);
                        break;
                    }
                }
                break;
            case 14: //thoat khoi 1 phong
                for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
                {
                    if ((*it)->get_key() == buf.room_id)
                    {
                        if ((*it)->out_s(buf.u_id))
                        {
                            delete (*it);
                            list_room.erase(it);
                            break;
                        }
                    }
                }
                break;
            case 92: //co nguoi ra khoi room
                for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
                {
                    if ((*it)->get_key() == buf.room_id)
                    {
                        if ((*it)->out_s(buf.u_id))
                        {
                            delete (*it);
                            list_room.erase(it);
                            break;
                        }
                    }
                }

                break;
            case 1009:
                shutdown(server_stanby_sock, SHUT_RDWR);
                close(server_stanby_sock);
                server_stanby_sock = -1;

                syslog(6, "1009 end stand-by thread");
                goto H;
                //break;
            case 31: //co nguoi ket ban thanh cong
                p_us1 = user_data.search(buf.recver_usename);
                p_us2 = user_data.search(buf.sender_username);
                p_us1->add_friend(p_us2);
                p_us2->add_friend(p_us1);
                break;
            // case 1010: //nhan data user

            //     break;
            default:
                break;
            }
        }
    }
    if (server_stanby_sock > 0)
    {
        sleep_ms(50);
        shutdown(server_stanby_sock, SHUT_RDWR);
        close(server_stanby_sock);
        server_stanby_sock = -1;
    }
H:
    while (state == 2)
        ;
    syslog(6, "end stand-by thread");
}
void server::load_r(void *buf)
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
    room_inf *r = (room_inf *)buf;
    room *p = new room;
    p->set_pass(r->pass);
    p->set_room_name(r->name);
    p->id_captain = r->id;
    p->set_key(r->id);
    strcpy(p->name_captain, r->captain);
    for (int i = 0; i < r->sz_u; ++i)
    {
        p->id_user.insert(r->u[i]);
    }
    this->list_room.insert(p);
}
void server::sync_room()
{
    sleep(5);
    for (auto &c : list_room)
    {
        c->sync();
    }
    for (set<room *>::iterator it = list_room.begin(); it != list_room.end(); ++it)
    {
        if ((*it)->empty())
        {
            list_room.erase(it);
            --it;
        }
    }
}
void server::reconnect_complete(id *u)
{
    for (auto &c : list_room)
        c->load_s(u);
}

void server::load_user_data()
{
    immOM *p = immOM::get_instance();
    char attr_name[] = "user_name";
    vector<immObject> v = p->search(attr_name);

    string u_name;
    vector<user *> tmp;
    for (auto &c : v)
    {
        if (strstr(c.obj_name, "user_name="))
        {
            u_name = c.obj_name;

            int pos = u_name.find(",user_name=");
            if (pos != string::npos)
            {
                u_name = u_name.substr(0, u_name.length() - 15);
            }
            else
            {
                u_name = u_name.substr(10);
            }
        }

        int _id;
        char *pass;
        int sz = c.attr_name.size();

        for (int i = 0; i < sz; ++i)
        {

            if (strcmp(c.attr_name[i], "passwd") == 0)
            {
                pass = (char *)c.val[i][0];
                continue;
            }
            if (strcmp(c.attr_name[i], "user_id") == 0)
            {
                _id = *((int *)c.val[i][0]);
            }
        }
        user *u = user_data.insert(u_name.c_str(), pass, _id);
        if (u)
            tmp.push_back(u);
    }

    for (int i = 0; i < tmp.size(); ++i)
    {

        int sz = v[i].attr_name.size();

        for (int j = 0; j < sz; ++j)
        {
            if (strcmp(v[i].attr_name[j], "friend") == 0)
            {
                int attr_sz = v[i].val[j].size();

                char *x;
                for (int k = 0; k < attr_sz; ++k)
                {
                    x = (char *)v[i].val[j].at(k);

                    user *u = user_data.search(x);
                    if (u)
                    {
                        tmp[i]->add_friend(u);
                    }
                }
                break;
            }
        }
    }
    user::user_id = user_data.get_max_ID();
    tmp.clear();
    user_data.to_vector(tmp);
    // for(auto &c:tmp)
    // {
    //     syslog(6, "user_name = %s --> passwd = %s --> id = %d",c->get_username(),c->get_passwd(),c->get_id());
    //     unordered_set<user*> *xx=c->get_friend_list();
    //     for(auto &r:(*xx))
    //     {
    //         syslog(6,"%s",r->get_username());
    //     }
    //     syslog(6,"--------------------");
    // }
}