#pragma once
#include "include.h"
//using namespace std;
class room;
class id;
#include "room.h"
#include "id.h"
class user
{
  private:
    char user_name[30];
    char pass[30];
    int sex;
    int u_ID;
   // id *u;
    static int user_id;
  public:
    

    room *create_room(const char *room_name, const char *pass); //Da lam
    //void remove_room(int id);
    
    const char* get_username();                 //da lam
    const char* get_passwd();                   //da lam
    user(const char *u_name, const char *p, int s = 0);
    user(const user &u);
    user();
    friend ostream &operator<<(ostream &os, user x);//xx
    friend istream &operator>>(istream &is,user &x);
    const char* get_key();
    friend bool operator<(user x,user y);
    friend bool operator==(user x,user y);
    void show();
    int get_id();
    
};
