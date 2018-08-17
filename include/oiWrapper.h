#pragma once

#include <stdio.h>
#include <unistd.h>
#include <saImmOi.h>
#include <string>
#include <string.h>
#include <thread>
#include <poll.h>

class OiWrapper
{
    private:
        SaImmOiHandleT immOiHandle; 
        SaSelectionObjectT selectionObject; 
        volatile bool stopImmOi = false;
        SaAisErrorT dispatch();
        std::thread startPollingThread;
    public:
        SaAisErrorT initLibrary(SaImmOiCallbacksT_o3* immOiCallbacks);
        SaAisErrorT initSelectionObjectGet();
        SaAisErrorT setImplementerName(std::string implementerName);
        SaAisErrorT setClassImplementer(std::string className);
        void startDispatch();
        void stopDispatch();
        SaAisErrorT releaseClass(std::string className);
        SaAisErrorT clearImplementerName();
        SaAisErrorT finalizeLibrary();

};