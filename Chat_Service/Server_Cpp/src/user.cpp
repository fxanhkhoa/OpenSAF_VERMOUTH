#include "../include/server.h"
int user::user_id = 1;
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

user::user(const char *u_name, const char *p, int s)
{
    strcpy(user_name, u_name);
    strcpy(pass, p);
    sex = s;
    u_ID = ++user_id;
  //  u = NULL;
}
ostream &operator<<(ostream &os, user x)
{
    os << x.user_name << '\t' << x.pass << endl;
    return os;
}

istream &operator>>(istream &is, user &x)
{
    is >> x.user_name >> x.pass;
    //  char x[10];
    is.get();
    return is;
}

const char *user::get_key()
{
    return user_name;
}

user::user(const user &u)
{
    this->sex = u.sex;
    strcpy(this->user_name, u.user_name);
    strcpy(this->pass, u.pass);
    this->u_ID = u.u_ID;
   // this->u = u.u;
}

user::user()
{
    this->u_ID = ++user_id;
  //  u = NULL;
}

void user::show()
{
    cout << "user name: " << this->user_name << "\tpass: " << this->pass << " u_ID: " << this->u_ID << endl;
}

int user::get_id()
{
    return u_ID;
}