#pragma once
#include "include.h"


int connect_to_sv(const char *host, int port = 8888);
int open_listening_socket(int _port);


struct block_data
{
    int cmd; //lenh
    int room_id;
    char sender_username[30];
    char recver_usename[30];
    char sender_pass[30];
    char room_pass[30];
    char mess[892];
    int u_id;

    block_data()
    {
        cmd = room_id  = u_id=0;
        memset(sender_username, 0, 30);
        memset(recver_usename, 0, 30);
        memset(sender_pass, 0, 30);
        memset(room_pass, 0, 30);
        memset(mess, 0, 892);
    }
    void show()
    {
        cout << cmd << endl
             << sender_username << endl
             << recver_usename << endl;
    }
};
