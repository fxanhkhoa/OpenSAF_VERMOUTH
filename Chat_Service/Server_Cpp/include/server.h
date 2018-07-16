#pragma once
#include "user.h"
#include "func.h"
//using namespace std;
extern mutex mu;
extern mutex mu_lock_file;
class server
{
  private:
    static server *sv;
    set<id *> list_online;
    set<room *> list_room;
    int listen_sock;
    int server_stanby_sock;
    pollfd list_poll_connected[10000];
    stack<int> empty_connected_pos;
    int connected_size;
    pollfd list_poll_unlogin[10000];
    stack<int> empty_unlogin_pos;
    int unlogin_size;
    server()
    {
        connected_size = unlogin_size = 0;
    }
    void pollfd_push(pollfd *l, stack<int> &c, int &size, pollfd val)
    {
        mu.lock();
        if (c.empty())
        {
            l[size++] = val;
        }

        else
        {
            l[c.top()] = val;
            c.pop();
        }
        mu.unlock();
    }
    void pollfd_erase(pollfd *l, stack<int> &c, int &size, int pos)
    {
        mu.lock();
        l[pos].fd = -1;
        if (pos == size - 1)
        {
            --size;
        }
        else
        {

            c.push(pos);
        }
        mu.unlock();
    }
    block_data send_to_standby;
  public:
    static server *get_instance(); //xx

    void listen_connect();

    /*add a new room*/
    bool add_room(room *r); //xx

    /*load all user from file to map*/
    void load_data();

    /*tao moi 1 user, thanh cong tra ve con tro user, that bai(user da ton tai) tra ve NULL*/
    user *create_new_user(const char *u_name, const char *u_pass); //xx

    room *get_room_from_ID(int room_id); //xx

    /*tim id cua user name*/
    id *get_id_from_name(const char *u_name); //xx

    /*delete 1 room ra khoi list room, u_name la ten nguoi xoa*/
    bool remove_room(int r_id,const char *u_name);

    /*xoa 1 id trong list online*/
    bool remove_id(id *u);
    id *remove_id(int fd);


    /*luong chat*/
    void chat_client();

    /*lay user name tu socket*/
    id *get_id_from_fd(int fd);

    void trans_data_to_standby_sv();

    void send_data_to_login_user(int sock);

    //gui cho toan bo set user online
    void send_to_world(void *buf);

  
};
