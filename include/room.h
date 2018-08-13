
#pragma once
#include "include.h"

class id;
class user;
#include "id.h"
#include "user.h"
#include <unordered_set>
class room
{
private:
  char room_name[30];
  set<id *> user_onl;
  int room_id;
  user *captain;
  char pass[30];

public:
  static int ID;
  unordered_set<int> id_user; //stand by sv
  int id_captain;
  char name_captain[30];                                     //sv standby
  room(const char *r_name, user *owner, const char *passwd); //da lam
  room();                                                    //da lam
  ~room();
  friend bool operator<(room x, room y);  //da lam
  friend bool operator==(room x, room y); //da lam
  const char *get_room_name();            // da lam

  user *get_captain();

  bool set_room_name(const char *n); //da lam
  void add_people(id *u);            //da lam
  bool remove_people(id *u);         //da lam

  bool set_pass(const char *p); //da lam
  set<id *> get_user_onl();
  /*get room id*/
  int get_key(); //xx
  void set_key(int k);
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

  void load_s(id *u);
  bool out_s(int u);
  void show();

  int size();
  void clear();
  void sync();
  bool empty();
  void data_client_res(int sock);
};
