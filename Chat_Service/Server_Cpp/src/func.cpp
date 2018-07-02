#pragma once
#include "../include/func.h"
#include <sstream>

int connect_to_sv(const char *host, int port)
{
    int sockfd;

    struct sockaddr_in serv_addr;
    struct hostent *server;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
    {
        printf("ERROR opening socket\n");
        return -2;
    }
    server = gethostbyname(host);
    if (server == NULL)
    {
        fprintf(stderr, "ERROR, no such host\n");
        return -3;
    }
    bzero((char *)&serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(port);
    if (connect(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        fprintf(stderr, "ERROR, can't connect to server\n");
        return -1;
    }
    return sockfd;
}
int open_listening_socket(int _port)
{
    int rc = -1, on;
    int listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if (listen_sd < 0)
    {
        perror("socket() failed");
        exit(-1);
    }
    rc = setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR,
                    (char *)&on, sizeof(on));
    if (rc < 0)
    {
        perror("setsockopt() failed");
        close(listen_sd);
        exit(-1);
    }
    rc = ioctl(listen_sd, FIONBIO, (char *)&on);
    if (rc < 0)
    {
        perror("ioctl() failed");
        close(listen_sd);
        exit(-1);
    }
    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(_port);
    rc = bind(listen_sd, (struct sockaddr *)&addr, sizeof(addr));
    if (rc < 0)
    {
        perror("bind() failed");
        close(listen_sd);
        exit(-1);
    }
    rc = listen(listen_sd, 32);
    if (rc < 0)
    {
        perror("listen() failed");
        close(listen_sd);
        exit(-1);
    }
    return listen_sd;
}
/*da test chay tot*/
// int process_message(const char *str, string &arg1, string &arg2)
// {
//     stringstream ss(str);
//     string cmd;
//     getline(ss, cmd, '\n');
//     if (cmd == "SIGN_IN")
//     {
//         getline(ss, arg1, '\n'); //username
//         getline(ss, arg2, '\0'); //pass
//         return 4;
//     }
//     if (cmd == "SIGN_UP")
//     {
//         getline(ss, arg1, '\n'); //username
//         getline(ss, arg2, '\0'); //pass
//         return 9;
//     }
//     //gui tin nhan 1-1 value tra ve 1
//     if (cmd == "SEND_PR")
//     {
//         getline(ss, arg1, '\n'); //nguoi nhan
//         getline(ss, arg2, '\0'); //noi dung tin nhan
//         return 1;
//     }
//     //gui tn 1-> group tra ve 2
//     if (cmd == "SEND_PL")
//     {
//         getline(ss, arg1, '\n'); //room_id
//         getline(ss, arg2, '\0'); //noi dung tin nhan
//         return 2;
//     }
//     if (cmd == "SIGN_OUT")
//     {
//         return 3;
//     }

//     //them nguoi vo group
//     if (cmd == "ADD_MEM")
//     {
//         getline(ss, arg1, '\n'); //username cua nguoi bi add
//         getline(ss, arg2, '\0'); //room_id
//         return 5;
//     }
//     //xoa nguoi khoi group
//     if (cmd == "REM_MEM")
//     {
//         getline(ss, arg1, '\n'); //username cua nguoi bi xoa
//         getline(ss, arg2, '\0'); //room_id
//         return 6;
//     }
//     //tao room
//     if (cmd == "ADD_ROOM")
//     {
//         getline(ss, arg1, '\0'); //ten room
//         return 7;
//     }
//     //xoa room
//     if (cmd == "DEL_ROOM")
//     {
//         getline(ss, arg1, '\0'); //room id
//         return 8;
//     }
//     //thay doi user pass
//     if (cmd == "EDIT_U_PASS")
//     {
//         getline(ss, arg1, '\0'); //new pass
//         return 10;
//     }
//     //tay doi pass room
//     if (cmd == "EDIT_R_PASS")
//     {
//         getline(ss, arg1, '\0'); //new pass
//         return 11;
//     }
//     if (cmd == "SERVER")
//         return 12;
//     return 0;
// }

// template <class T>
// int binarySearch(T arr[], int l, int r, int x)
// {
//     if (r >= l)
//     {
//         int mid = l + (r - l) / 2;
//         if (arr[mid] == x)
//             return mid;

//         if (x < arr[mid])
//             return binarySearch(arr, l, mid - 1, x);

//         return binarySearch(arr, mid + 1, r, x);
//     }

//     return -1;
// }
