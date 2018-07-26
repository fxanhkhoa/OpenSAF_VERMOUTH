#pragma once
#include "../include/func.h"
#include <sstream>

int connect_to_sv(const char *host, int port)
{

    int sock;
hell:
    struct sockaddr_in serv_addr;
    // clock_t t;
    memset(&serv_addr, '0', sizeof(serv_addr));

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        syslog(6, "connect_to_sv() Error : Could not create socket \n");
        goto hell;
    }

hell1:
    if (inet_pton(AF_INET, host, &serv_addr.sin_addr) <= 0)
    {
        syslog(6, "connect_to_sv() inet_pton error occured\n");
        goto hell1;
    }
hell2:
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        syslog(6, "connect_to_sv() Error : Connect Failed");
        goto hell2;
    }
    return sock;
}
int open_listening_socket(int _port)
{
    int rc = -1, on;

    int listen_sd; // = socket(AF_INET, SOCK_STREAM, 0);
H1:
    listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if (listen_sd < 0)
    {
        syslog(6, "socket() failed");
        goto H1;
    }

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(_port);
H4:
    rc = bind(listen_sd, (struct sockaddr *)&addr, sizeof(addr));
    if (rc < 0)
    {
        perror("bind() failed");
        goto H4;
    }
H5:
    rc = listen(listen_sd, 32);
    if (rc < 0)
    {
        syslog(6, "listen() failed");
        goto H5;
    }
    return listen_sd;
}
