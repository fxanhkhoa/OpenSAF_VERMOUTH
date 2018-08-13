#include "../include/server.h"
bool operator==(id x, id y)
{
    return x.sock == y.sock;
}
bool operator<(id x, id y)
{
    return x.sock < y.sock;
}

room *id::create_new_room(const char *r_name, const char *passwd) //ok
{
    room *r = new room(r_name, this->u, passwd);
    if (r){
        r->add_people(this);
        this->add_room(r);
    }
    return r;
}

id::id(user *_u, int _sock)
{

    this->sock = _sock;
    this->u = _u;
}
bool id::remove_room(room *r) //ok da dung`
{

    set<room *>::iterator it = room_list.find(r);
    if (it != room_list.end())
    {
        room_list.erase(it);
        return true;
    }

    return false;
}
int id::get_key()
{
    return sock;
}
void id::set_sock(int s)
{
    sock = s;
}
bool id::quit_room(room *r)
{

    for (set<room *>::iterator it = this->room_list.begin();
         it != this->room_list.end(); ++it)
    {
        if ((*it) == r)
        {
            this->room_list.erase(it);
            r->remove_people(this);
            return true;
        }
    }
    return false;
}

bool id::invite_friend(const char *u_name, int ID_room)
{
    server *sv = server::get_instance();
    id *u = sv->get_id_from_name(u_name);
    if (u == NULL)
        return false;
    room *r = sv->get_room_from_ID(ID_room);
    if (r->check_belong_room(this) == false)
        return false;

    r->add_people(u);
    u->add_room(r);
    return true;
}
void id::add_room(room *r)
{
    this->room_list.insert(r);
}
bool id::join_room(int ID_room, const char *pass)
{
    room *r = server::get_instance()->get_room_from_ID(ID_room);
    if (r == NULL)
        return false;

    if (r->check_pass(pass))
    {
        r->add_people(this);
        this->add_room(r);
        return true;
    }
    else
        return false;
}
user *id::get_user()
{
    return this->u;
}

bool id::kick_out(const char *user_name, int room_ID)
{
    server *sv = server::get_instance();
    id *u = sv->get_id_from_name(user_name);
    room *r = sv->get_room_from_ID(room_ID);
    if (r->check_owner(this->get_user()))
    {
        if (r->check_belong_room(u))
        {
            u->quit_room(r);
            return 1;
        }
        else
            return 0;
    }
    return 0;
}

id::~id()
{
    syslog(6,"cho vo huy id sz room = %d",room_list.size());

    for (auto &c : room_list)
    {
        syslog(6,"%d",c->get_key());
        c->remove_people(this);
    }
}

