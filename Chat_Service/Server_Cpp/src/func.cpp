#pragma once
#include "../include/func.h"
#include <sstream>
void sleep_ms(int milliseconds) // cross-platform sleep function
{
#ifdef WIN32
    Sleep(milliseconds);
#elif _POSIX_C_SOURCE >= 199309L
    struct timespec ts;
    ts.tv_sec = milliseconds / 1000;
    ts.tv_nsec = (milliseconds % 1000) * 1000000;
    nanosleep(&ts, NULL);
#else
    usleep(milliseconds * 1000);
#endif
}
int connect_to_sv(const char *host, int port)
{

    int sock;
    //hell:
    struct sockaddr_in serv_addr;
    // clock_t t;
    memset(&serv_addr, 0, sizeof(serv_addr));

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        syslog(6, "connect_to_sv() Error : Could not create socket \n");
        return -1; // goto hell;
    }

    //hell1:
    if (inet_pton(AF_INET, host, &serv_addr.sin_addr) <= 0)
    {
        syslog(6, "connect_to_sv() inet_pton error occured\n");
        close(sock);
        return -1; //goto hell1;
    }
    //hell2:
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        syslog(6, "connect_to_sv() Error : Connect Failed");
        close(sock);
        return -1; //goto hell2;
    }

    return sock;
}
int open_listening_socket(int _port)
{
    int rc = -1, on;

    int listen_sd;
H1:
    listen_sd = socket(AF_INET, SOCK_STREAM, 0);
    if (listen_sd < 0)
    {
        syslog(6, "socket() failed");
        goto H1;
    }

    struct sockaddr_in addr;
H4:
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(_port);
    int opt = 1;
    rc = setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt));
    if (rc < 0)
    {
        syslog(6, "setsockopt() failed");
        sleep_ms(50);
        goto H4;
    }
    rc = bind(listen_sd, (struct sockaddr *)&addr, sizeof(addr));
    if (rc < 0)
    {
        syslog(6, "bind() failed");
        sleep_ms(50);
        goto H4;
    }
H5:
    if (listen(listen_sd, 32) < 0)
    {
        syslog(6, "listen() failed");
        goto H5;
    }
    else
        syslog(6, "onpen listening sock complete!");

    return listen_sd;
}
