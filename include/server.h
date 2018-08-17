#pragma once
#include "user.h"
#include "func.h"
#include "immOM.h"
#include "oi.h"
//using namespace std;
extern mutex mu;
extern volatile int state;
class server
{
private:
  static server *sv;
  set<id *> list_online;
  set<room *> list_room;
  //int listen_sock;

  pollfd list_poll_connected[10000];
  stack<int> empty_connected_pos;
  int connected_size;
  pollfd list_poll_unlogin[10000];
  stack<int> empty_unlogin_pos;
  int unlogin_size;
  map<int, int> u_online; //standby
  server();
  void pollfd_push(pollfd *l, stack<int> &c, int &size, pollfd val);
  void pollfd_erase(pollfd *l, stack<int> &c, int &size, int pos);
  int listen_sock;

public:
  int server_stanby_sock;
  static server *get_instance(); //xx

  void *listen_connect();

  /*add a new room*/
  bool add_room(room *r); //xx

  /*tao moi 1 user, thanh cong tra ve con tro user, that bai(user da ton tai) tra ve NULL*/
  user *create_new_user(const char *u_name, const char *u_pass); //xx

  room *get_room_from_ID(int room_id); //xx

  /*tim id cua user name*/
  id *get_id_from_name(const char *u_name); //xx

  /*delete 1 room ra khoi list room, u_name la ten nguoi xoa*/
  bool remove_room(int r_id, const char *u_name);

  /*xoa 1 id trong list online*/

  id *remove_id(int fd);

  /*luong chat*/
  void *chat_client();

  /*lay user name tu socket*/
  id *get_id_from_fd(int fd);

  //void trans_data_to_standby_sv();

  static void send_data_to_login_user(int sock,server *const sv);

  void send_room_stanby();
  //gui cho toan bo set user online
  void send_to_world(void *buf);

  void stand_by();

  void load_r(void *buf);
  void reconnect_complete(id *u);
  void send_list_friend(int sock);

  SaAisErrorT edit_passw(const char *user_name, const char *pass);
  //void send_us_stanby();
  SaAisErrorT add_friend(const char *u_name, const char *friend_name, int flag = 1);
  void sync_room();
  static void load_user_data();
};
