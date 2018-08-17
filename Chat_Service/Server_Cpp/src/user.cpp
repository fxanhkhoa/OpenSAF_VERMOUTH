#include "../include/server.h"
int user::user_id = 0;
const char *user::get_username()
{
    return user_name;
}
const char *user::get_passwd()
{
    return pass;
}
/* Tao room moi, sau do them nguoi tao room
 vao danh sach nhung nguoi dang online trong room va them room vao danh sach cac room tren sv*/
room *user::create_room(const char *room_name, const char *pass)
{
    return NULL;
}

/*Toan tu < so sah theo user_name*/
bool operator<(user x, user y)
{
    return strcmp(x.user_name, y.user_name) < 0;
}
bool operator==(user x, user y)
{
    return strcmp(x.user_name, y.user_name) == 0;
}

user::user(const char *u_name, const char *p)
{
    strcpy(user_name, u_name);
    strcpy(pass, p);
    f_list = new unordered_set<user *>;
    u_ID = ++user_id;
    //  u = NULL;
}

const char *user::get_key()
{
    return user_name;
}

user::user(const user &u)
{
    this->f_list = u.f_list;
    strcpy(this->user_name, u.user_name);
    strcpy(this->pass, u.pass);
    this->u_ID = u.u_ID;
    // this->u = u.u;
}

user::user()
{
    this->u_ID = ++user_id;
    f_list = new unordered_set<user *>;
}

void user::show()
{
    cout << "user name: " << this->user_name << "\tpass: " << this->pass << " u_ID: " << this->u_ID << endl;
}

int user::get_id()
{
    return u_ID;
}

user::user(const char *n, const char *p, int i)
{
    this->u_ID = i;
    strcpy(this->user_name, n);
    strcpy(this->pass, p);
    f_list = new unordered_set<user *>;
}

void user::get_name_pass_id(void *res)
{
    struct _US
    {
        char u_name[30];
        char u_pass[30];
        int u_id;
    };
    _US *p = (_US *)res;
    strcpy(p->u_name, user_name);
    strcpy(p->u_pass, pass);
    p->u_id = u_ID;
}

void user::set_pass(const char *p)
{
    strcpy(this->pass, p);
}
void user::add_friend(user *f)
{
    if (f)
    {
        f_list->insert(f);
    }
}
unordered_set<user *> *user::get_friend_list()
{
    return f_list;
}

void user::get_friend_list(vector<string> &v)
{
    for (auto &c : (*f_list))
    {
        v.push_back(c->get_username());
    }
}
bool user::is_friend(user *f)
{
    if(f_list->find(f) != f_list->end())
    {
        syslog(6,"da la ban");
        return true;
    }
    syslog(6,"chua la ban");
    return false;
}