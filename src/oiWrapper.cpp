#include "../include/oiWrapper.h"

SaAisErrorT OiWrapper::initLibrary(SaImmOiCallbacksT_o3* immOiCallbacks)
{
    SaVersionT version ={.releaseCode = 'A', 						
                        .majorVersion = 2, 						
                        .minorVersion = 16}; 
     
    return saImmOiInitialize_o3(&immOiHandle, immOiCallbacks, &version); 
}

SaAisErrorT OiWrapper::initSelectionObjectGet()
{
    return saImmOiSelectionObjectGet(immOiHandle, &selectionObject); 	
}

SaAisErrorT OiWrapper::setImplementerName(std::string implementerName)
{
    const SaImmOiImplementerNameT saImmImplementerName = (SaImmOiImplementerNameT)implementerName.c_str();
    return saImmOiImplementerSet(immOiHandle, saImmImplementerName);
}
SaAisErrorT OiWrapper::setClassImplementer(std::string className)
{
    SaImmClassNameT saImmClassName = (SaImmClassNameT)className.c_str();
    return saImmOiClassImplementerSet(immOiHandle,saImmClassName);
}
void OiWrapper::startDispatch()
{
    startPollingThread = std::thread(&OiWrapper::dispatch,this);
}

SaAisErrorT OiWrapper::dispatch()
{
    struct pollfd fds[1];
    fds[0].fd = selectionObject;
    fds[0].events = POLLIN;
    while (stopImmOi == false) 
    {
        int res = poll(fds, 1, 1000);
        if (res == -1) 
        {
            if (errno == EINTR)
                continue;
            else 
                break;
        }

        if (fds[0].revents & POLLIN) 
        {
            SaAisErrorT rc = saImmOiDispatch(immOiHandle, SA_DISPATCH_ONE);
            if (rc != SA_AIS_OK)
                return rc;
        }        
    }
    return SA_AIS_OK;
}

void OiWrapper::stopDispatch()
{
    stopImmOi = true;  
    startPollingThread.join();
}

SaAisErrorT OiWrapper::releaseClass(std::string className)
{
    SaImmClassNameT saImmClassName = (SaImmClassNameT)(className.c_str());
    return saImmOiClassImplementerRelease(immOiHandle,saImmClassName);
}

SaAisErrorT OiWrapper::clearImplementerName()
{
    return saImmOiImplementerClear(immOiHandle);
}

SaAisErrorT OiWrapper::finalizeLibrary()
{
    return saImmOiFinalize(immOiHandle); 	
}

