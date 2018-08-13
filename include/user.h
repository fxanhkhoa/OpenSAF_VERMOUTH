#pragma once
#include "include.h"
#include <vector>
using namespace std;
class room;
class id;
#include "room.h"
#include "id.h"
class user
{
private:
  char user_name[30];
  char pass[30];
  unordered_set<user *> *f_list;
  int u_ID;
  // id *u;
  // static int user_id;
public:
  static int user_id;

  room *create_room(const char *room_name, const char *pass); //Da lam
  //void remove_room(int id);

  const char *get_username(); //da lam
  const char *get_passwd();   //da lam
  user(const char *u_name, const char *p);
  user(const user &u);
  user();
  user(const char *n, const char *p, int i);
  const char *get_key();
  friend bool operator<(user x, user y);
  friend bool operator==(user x, user y);
  void show();
  int get_id();
  void get_name_pass_id(void *res);
  void set_pass(const char *p);
  void add_friend(user *f);
  unordered_set<user *> *get_friend_list();
  void get_friend_list(vector<string> &v);
};
