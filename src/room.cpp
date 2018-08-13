#include "../include/server.h"

int room::ID = 0;
room::room(const char *r_name, user *owner, const char *passwd)
{
    strcpy(this->room_name, r_name);
    this->captain = owner;
    strcpy(this->pass, passwd);
    this->room_id = ++ID;
    this->id_captain = captain->get_id();
    this->id_user.insert(id_captain);
}
room::room()
{
    captain = NULL;
    id_captain = 0;
}
room::~room()
{

    for (auto &c : user_onl)
    {
        c->remove_room(this);
    }
}
bool operator<(room a, room b)
{
    return a.room_id < b.room_id;
}

bool operator==(room b, room a)
{
    return a.room_id == b.room_id;
}

const char *room::get_room_name()
{
    return room_name;
}
bool room::set_room_name(const char *n)
{
    strcpy(room_name, n);
    return 1;
}

bool room::set_pass(const char *p)
{
    strcpy(pass, p);
    return 1;
}

set<id *> room::get_user_onl()
{
    return user_onl;
}
int room::get_key()
{
    return room_id;
}

bool room::check_belong_room(id *u)
{

    return user_onl.find(u) != user_onl.end();
}

bool room::check_pass(const char *p)
{
    return strcmp(p, this->pass) == 0;
}
bool room::check_owner(user *u)
{
    return this->captain == u;
}

void room::add_people(id *u)
{
    if (u)
    {
        this->user_onl.insert(u);
        this->id_user.insert(u->get_user()->get_id());
    }
}
void room::clear()
{
    user_onl.clear();
    captain = NULL;
}
void room::set_key(int k)
{
    this->room_id = k;
}
bool room::remove_people(id *u)
{
    if (!check_belong_room(u))
        return true;
    for (set<id *>::iterator it = this->user_onl.begin();
         it != this->user_onl.end(); ++it)
    {
        if ((*it) == u)
        {
            this->user_onl.erase(it);
            if (id_user.find(u->get_user()->get_id()) != id_user.end())
                this->id_user.erase(id_user.find(u->get_user()->get_id()));
            if (this->user_onl.size())
            {
                if (captain == u->get_user())
                {
                    this->captain = (*this->user_onl.begin())->get_user();
                    this->id_captain = captain->get_id();
                }
                block_data buf;
                buf.cmd = 41;
                strcpy(buf.sender_username, captain->get_username());
                buf.room_id = this->room_id;
                buf.u_id = id_captain;
                send(server::get_instance()->server_stanby_sock, &buf, 1024, 0);
            }
            else
            {
                captain = NULL;
                this->id_captain = 0;
                server::get_instance()->remove_room(this->room_id, NULL);

                this->~room();
            }

            return true;
        }
    }
    return false;
}
int room::size()
{
    return this->user_onl.size();
}
void room::send_message(void *buf)
{
    for (auto &c : this->user_onl)
    {
        send(c->get_key(), buf, 1024, 0);
    }
}
void room::load_s(id *u)
{
    if (!u)
        return;
    unordered_set<int>::iterator it;
    int x = u->get_user()->get_id();
    it = id_user.find(x);
    if (it == id_user.end())
        return;
    this->user_onl.insert(u);
    u->add_room(this);
    if (x == id_captain)
        this->captain = u->get_user();
}

bool room::edit_pass(const char *user_name, const char *new_pass)
{
    id *u = server::get_instance()->get_id_from_name(user_name);
    if (u->get_user() == this->captain)
    {
        this->set_pass(new_pass);
        return true;
    }
    return false;
}

void room::data_client_res(int sock)
{
    struct user_room
    {
        int _id;
        char name[30];
    };
    struct block_room_
    {
        int cmd;
        int size;
        int id_r;
        user_room data[29];
        char xx[26];
    };
    block_room_ buf;
    buf.size = 0;
    buf.cmd = 15;
    buf.id_r = room_id;
    for (auto &c : user_onl)
    {
        if (buf.size == 29)
        {
            send(sock, &buf, 1024, 0);
            buf.size = 0;
        }
        buf.data[buf.size]._id = c->get_user()->get_id();
        strcpy(buf.data[buf.size].name, c->get_user()->get_username());
        ++buf.size;
    }
    send(sock, &buf, 1024, 0);
}

void room::show()
{
    syslog(6, "room name: %s", this->room_name);
    syslog(6, "room id: %d", this->room_id);
    syslog(6, "captain: %s", this->captain->get_username());
    syslog(6, "nhung nguoi trong room %s", this->room_name);
    for (auto &c : this->user_onl)
    {
        syslog(6, "%s      %d", c->get_user()->get_username(), c->get_user()->get_id());
    }
}

void room::sync()
{

    id_user.clear();
    for (auto &c : user_onl)
        id_user.insert(c->get_user()->get_id());
    if (!captain)
    {
        captain = (*user_onl.begin())->get_user();
        id_captain = captain->get_id();
    }
}

bool room::empty()
{
    return user_onl.empty();
}

void room::get_room_data(void *buf)
{
    if (user_onl.empty())
        return;
    struct room_inf
    {
        int id;
        int sz_u;
        char name[30];
        char pass[30];
        char captain[30];
        int u[100];
    };
    room_inf *t = (room_inf *)buf;

    t->id = this->room_id;
    strcpy(t->name, this->room_name);
    strcpy(t->captain, this->captain->get_username());
    user *u = this->captain;
    if (u)
        strcpy(t->captain, u->get_username());
    strcpy(t->pass, this->pass);
    int i = 0;
    for (auto &c : this->user_onl)
    {
        t->u[i++] = c->get_user()->get_id();
    }
    t->sz_u = i;
}

user *room::get_captain()
{
    return captain;
}

bool room::out_s(int u)
{
    if (id_user.empty())
        return true;
    if (id_user.find(u) == id_user.end())
        return false;
    id_user.erase(id_user.find(u));
    if (id_user.size())
    {
        if (id_captain == u)
        {
            id_captain = *(id_user.begin());
        }
        return false;
    }
    return true;
}
