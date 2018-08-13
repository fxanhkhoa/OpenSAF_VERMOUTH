#pragma once
#include "include.h"

class immOI;
#include "immOI.h"

class immOM
{
  private:
    SaImmHandleT immHandle;
    immOM();
    SaAisErrorT init();
    static immOM *instance;
public:
    static immOM* get_instance();
    SaImmHandleT get_handle();
    SaAisErrorT finalize();
    vector<immObject> search(char *attr_name);
};