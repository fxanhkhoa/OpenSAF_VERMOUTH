#pragma once
#include "../include/immOM.h"
immOM *immOM::instance = NULL;

immOM *immOM::get_instance()
{
    if (instance == NULL)
    {
        instance = new immOM;
        instance->init();
    }
    return instance;
}
SaImmHandleT immOM::get_handle()
{
    return immHandle;
}
SaAisErrorT immOM::init()
{
    SaAisErrorT rc;

    SaVersionT version = {.releaseCode = 'A',
                          .majorVersion = 2,
                          .minorVersion = 16};
    rc = saImmOmInitialize_o2(&immHandle, NULL, &version);

    if (rc == SA_AIS_OK)
        syslog(6, "Library initialization succeed");
    else
        syslog(6, "Library initialization failed with error: %d", rc);
    return rc;
}

SaAisErrorT immOM::finalize()
{
    return saImmOmFinalize(immHandle);
}
immOM::immOM() {}
vector<immObject> immOM::search(char *attr_name)
{
    SaAisErrorT rc;

    SaImmSearchHandleT searchHandle;
    SaImmSearchOneAttrT_2 searchOneAttr = {.attrName = attr_name,
                                           .attrValueType = SA_IMM_ATTR_SASTRINGT,
                                           .attrValue = NULL};
    SaImmSearchParametersT_2 searchParam = {.searchOneAttr = searchOneAttr};
    rc = saImmOmSearchInitialize_o3(immHandle, NULL, SA_IMM_SUBTREE, SA_IMM_SEARCH_GET_ALL_ATTR | SA_IMM_SEARCH_ONE_ATTR, &searchParam, NULL, &searchHandle);
    if (rc == SA_AIS_OK)
        syslog(6, "Search handle initialization succeed");
    else
        syslog(6, "Search handle initialization failed with error: %d", rc);

    SaStringT objectName;

    SaImmAttrValuesT_2 **attributes, **attributes2;
    vector<immObject> res;

    vector<void *> tmp;
    int len;
    while (saImmOmSearchNext_o3(searchHandle, &objectName, &attributes2) == SA_AIS_OK)
    {
        immObject t;
        len = strlen(&objectName[0]);
        if (len <= 0)
            continue;
        t.obj_name = new char[len + 1];
        t.obj_name[len] = 0;
        strcpy(t.obj_name, &objectName[0]);

        attributes = attributes2;
        for (; *attributes; attributes++)
        {
            if (strcmp((*attributes)->attrName, "SaImmAttrImplementerName") == 0)
                continue;
            if (strcmp((*attributes)->attrName, "SaImmAttrClassName") == 0)
                continue;
            if (strcmp((*attributes)->attrName, "SaImmAttrAdminOwnerName") == 0)
                continue;
            len = strlen((*attributes)->attrName);
            t.attr_name.push_back(NULL);
            t.attr_name.back() = new char[len + 1];
            t.attr_name.back()[len] = 0;
            strcpy(t.attr_name.back(), (*attributes)->attrName);
            t.val_type.push_back((*attributes)->attrValueType);
            t.val.push_back(tmp);
            for (int i = 0; i < (*attributes)->attrValuesNumber; ++i)
            {
                if ((*attributes)->attrValueType == SA_IMM_ATTR_SAUINT32T)
                {
                    int v = *(int *)(*((*attributes)->attrValues + i));
                    int *p = new int;
                    *p = v;
                    t.val.back().push_back(p);
                    continue;
                }
                if ((*attributes)->attrValueType == SA_IMM_ATTR_SASTRINGT)
                {
                    len = strlen(*(SaStringT *)(*((*attributes)->attrValues + i)));
                    char *p = new char[len + 1];
                    p[len] = 0;
                    strcpy(p, *(SaStringT *)(*((*attributes)->attrValues + i)));
                    t.val.back().push_back(p);
                }
            }
        }
        res.push_back(t);
    }
    saImmOmSearchFinalize(searchHandle);
    return res;
}