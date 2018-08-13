#pragma once
#include <iostream>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <net/if.h>
#include <ifaddrs.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <thread>
#include <set>
#include <sys/poll.h>
#include <sys/ioctl.h>
#include <mutex>
#include <fstream>
#include "List.h"
#include <vector>
#include <map>
#include <stack>
#include <errno.h>
#include <poll.h>
#include <syslog.h>
#include <libgen.h>
#include <signal.h>
#include <saAmf.h>
#include <saAis.h>
#include <malloc.h>
#include <ctype.h>
#include <saImm.h>
#include <saImmOm.h>
#include <saImmOi.h>
#include <unordered_set>
using namespace std;
struct immObject
{
    char *obj_name;
    vector<char *> attr_name;
    vector<int> val_type;
    vector<vector<void *>> val;
};