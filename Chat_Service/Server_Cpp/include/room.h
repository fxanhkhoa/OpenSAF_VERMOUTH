
#pragma once
#include "include.h"
//using namespace std;
class id;
class user;
#include "id.h"
#include "user.h"

class room
{
  //toi da 100 nguoi
private:
  char room_name[30];
  set<id *> user_onl;
  int room_id;
  user *captain;
  char pass[30];
  static int ID;
  set<int> id_user;//stand by sv
public:
  room(const char *r_name, user *owner, const char *passwd); //da lam
  room();                                                       //da lam
  ~room();
  friend bool operator<(room x, room y);                        //da lam
  friend bool operator==(room x, room y);                       //da lam
  const char *get_room_name();                                  // da lam

  user* get_captain();
  
  bool set_room_name(const char *n);                                 //da lam
  void add_people(id *u);                                       //da lam
  bool remove_people(id *u);                                    //da lam
  
  bool set_pass(const char *p); //da lam
  set<id *> get_user_onl();
  /*get room id*/
  int get_key(); //xx
  /*kiem tra chu phong*/
  bool check_owner(user *u); //xx
  
  /*kiem tra id u co thuoc room hay khong*/
  bool check_belong_room(id *u); //xx
  /*kiem tra passwd*/
  bool check_pass(const char *p); //xx
  /*gui tin nhan cho toan bo room*/
  void send_message(void *buf);

  void get_room_data(void *buf);

  bool edit_pass(const char *user_name, const char *new_pass);
};
