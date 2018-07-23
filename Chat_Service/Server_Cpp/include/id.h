#pragma once
#include "include.h"
using namespace std;
class user;
class room;

#include "user.h"
#include "room.h"

class id
{
  private:
    user *u;
 
    int sock;
    std::set<room*> room_list;
    int xxx;

  public:
    friend bool operator<(id x,id y);
    friend bool operator==(id x,id y);
    room* create_new_room(const char *r_name, const char * passwd);//xong
    /*xoa room *r trong set room*/
    bool remove_room(room *r);
    id(user *_u = NULL, int _sock = -1);
    /*get this user*/
    user* get_user();

    int get_key();
    /*them ban vao phong chat*/
    bool invite_friend(const char *u_name,int ID_room);//xx
    /*thoat khoi mot phong thuoc list room*/
    bool quit_room(room *r);//xx
    /*dua room r vao list room*/
    void add_room(room *r);//xx
    /*id muon vo phong id_room*/
    bool join_room(int ID_room, const char *pass);//xx
    /*xoa ten trong tat ca cac room, xoa ra khoi ds dang online (chua xong)*/
    void log_out();
    
    
    /*Moi lam them*/
    bool kick_out(const char *user_name,int ID_room);//xx


    ~id();

};