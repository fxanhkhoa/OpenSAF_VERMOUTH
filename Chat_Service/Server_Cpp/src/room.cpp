#include "../include/server.h"

int room::ID = 1;
room::room(const char *r_name, user *owner, const char *passwd)
{
    strcpy(this->room_name, r_name);
    this->captain = owner;
    strcpy(this->pass, passwd);
    this->room_id = ++ID;
}
room::room()
{
    this->room_id = ++ID;
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
    for (auto &c : this->user_onl)
        if (c == u)
            return true;
    return false;
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
        this->user_onl.insert(u);
}

bool room::remove_people(id *u)
{
    for (set<id *>::iterator it = this->user_onl.begin();
         it != this->user_onl.end(); ++it)
    {
        if ((*it) == u)
        {
            this->user_onl.erase(it);
            if (this->user_onl.size())
            {
                if (captain == u->get_user())
                    this->captain = (*this->user_onl.begin())->get_user();
            }
            else
            {
                captain = NULL;
                server::get_instance()->remove_room(this->ID, NULL);
                this->~room();
            }
            return true;
        }
    }
    return false;
}

void room::send_message(void *buf)
{
    for (auto &c : this->user_onl)
    {
        send(c->get_key(), buf, 1024, 0);
    }
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

void room::get_room_data(void *buf)
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
    room_inf *t = (room_inf *)buf;

    t->id = this->room_id;
    strcpy(t->name, this->room_name);
    strcpy(t->captain, this->captain->get_username());
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