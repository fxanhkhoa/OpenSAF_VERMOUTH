#include <saImmOi.h> 
#include <iostream> 
#include <string> 
#include <unistd.h> 
#include <poll.h>
#include <syslog.h>
#include <string.h>

SaStringT StringToSaString(const std::string& input) 
{    
    return (SaStringT)input.c_str(); 
} 

void adminOperationCallback(SaImmOiHandleT immOiHandle,
					      SaInvocationT invocation,
					      SaConstStringT objectName,
					      SaImmAdminOperationIdT operationId,
					      const SaImmAdminOperationParamsT_2 **params)
{
    std::cout << "Admin operation callback" << std::endl;
}

void ccbAbortCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
    std::cout << "Abort callback" << std::endl;
}

void ccbApplyCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
    std::cout << "CCB aplly callback" << std::endl;
}

SaAisErrorT ccbCompletedCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
    std::cout << "CCB complete callback" << std::endl;
    return SA_AIS_OK;
}

SaAisErrorT ccbObjectCreateCallback(SaImmOiHandleT immOiHandle,
					       SaImmOiCcbIdT ccbId,
					       const SaImmClassNameT className,
					       SaConstStringT objectName, const SaImmAttrValuesT_2 **attr)
{
    std::cout << "Attributes of new object" << std::endl;
    std::cout << "testRDN = " << (char*)objectName << std::endl;
    const SaImmAttrValuesT_2 **curAttr = attr;
    while(*curAttr)
    {
        std::cout << (*curAttr)->attrName << " = ";
        if ((*curAttr)->attrValues == NULL)
			{
				std::cout << "NULL" << std::endl;
				curAttr++;
				continue;
			}	
        if ((*curAttr)->attrValueType == SA_IMM_ATTR_SAFLOATT)
            std::cout << *(SaFloatT*)(*((*curAttr)->attrValues)) << std::endl;
        if ((*curAttr)->attrValueType == SA_IMM_ATTR_SAUINT32T)
            std::cout << *(SaUint32T*)(*((*curAttr)->attrValues)) << std::endl;
        if ((*curAttr)->attrValueType == SA_IMM_ATTR_SASTRINGT)
            std::cout << *(SaStringT*)(*((*curAttr)->attrValues)) << std::endl;
        curAttr++;
    }
    std::cout << "CCB object create callback" << std::endl;
    return SA_AIS_OK;
}

SaAisErrorT ccbObjectDeleteCallback(SaImmOiHandleT immOiHandle,
					     SaImmOiCcbIdT ccbId, SaConstStringT objectName)
{
    std::cout << "CCB object delete callback" << std::endl;
    return SA_AIS_OK;
}      

SaAisErrorT ccbObjectModifyCallback(SaImmOiHandleT immOiHandle,
					       SaImmOiCcbIdT ccbId,
					       SaConstStringT objectName, const SaImmAttrModificationT_2 **attrMods)
{
    std::cout << "CCB object modify callback" << std::endl;
    return SA_AIS_OK;  
}

SaAisErrorT rtAttrUpdateCallback(SaImmOiHandleT immOiHandle,
					  SaConstStringT objectName, const SaImmAttrNameT *attributeNames)
{
    std::cout << "Attribute update callback" << std::endl;
    return SA_AIS_OK;  
}
int main (int argc, char* argv[]) 
{ 	
    SaAisErrorT rc; 	

    /* Daemonize ourselves and detach from terminal.
	** This important since our start script will hang forever otherwise.
	** Note daemon() is not LSB but impl by libc so fairly portable...
	*/
	//if (daemon(0, 1) == -1) 
    //{
	//	syslog(LOG_DEBUG, "daemon failed: %s", strerror(errno));
	//	return 1;
	//}

    /* Initialize object implementer functions of the IMM Service */ 	
    SaImmOiHandleT immOiHandle; 	
    SaVersionT version ={.releaseCode = 'A', 						
                        .majorVersion = 2, 						
                        .minorVersion = 16}; 
    SaImmOiCallbacksT_o3 immOiCallbacks = {.saImmOiAdminOperationCallback = adminOperationCallback,
                                        .saImmOiCcbAbortCallback = ccbAbortCallback,
                                        .saImmOiCcbApplyCallback = ccbApplyCallback,
                                        .saImmOiCcbCompletedCallback = ccbCompletedCallback,
		                                .saImmOiCcbObjectCreateCallback = ccbObjectCreateCallback,
		                                .saImmOiCcbObjectDeleteCallback = ccbObjectDeleteCallback,
		                                .saImmOiCcbObjectModifyCallback = ccbObjectModifyCallback,
                                        .saImmOiRtAttrUpdateCallback = rtAttrUpdateCallback};                 	
    rc = saImmOiInitialize_o3(&immOiHandle, &immOiCallbacks, &version); 	
    if (rc == SA_AIS_OK)
    	std::cout << "Library initialization succeed" << std::endl; 	
    else 		
        std::cout << "Library initialization failed with error: " << rc << std::endl; 	
    /*******/ 	
    //syslog(LOG_DEBUG,"Hello I am OI");
    /*Get operation system handle*/ 	
    SaSelectionObjectT selectionObject; 	
    rc = saImmOiSelectionObjectGet(immOiHandle, &selectionObject); 	
    if (rc == SA_AIS_OK)
    	std::cout << "Get operation system handle succeed" << std::endl; 		
    else 		
        std::cout << "Get operation system handle failed with error: " << rc << std::endl; 	
    /*******/ 	

    /*Set the implementer name*/
    rc = saImmOiImplementerSet(immOiHandle,StringToSaString("shawryOi"));
    if (rc == SA_AIS_OK)
    	std::cout << "Set implementer name succeed" << std::endl; 		
    else 		
        std::cout << "Set implementer name failed with error: " << rc << std::endl; 	
    /*******/ 	
    
    /*Set roles for all objects that are instances of the class*/
    std::string classNameS = "TestClass";
	const SaImmClassNameT className = (SaImmClassNameT)(classNameS.c_str());
    rc = saImmOiClassImplementerSet(immOiHandle,className);
    if (rc == SA_AIS_OK)
    	std::cout << "Set class implementer name succeed" << std::endl; 		
    else 		
        std::cout << "Set class implementer name failed with error: " << rc << std::endl; 	
    /*******/
    
    /*Invokes pending callbacks*/
    struct pollfd fds[1];
    fds[0].fd = selectionObject;
	fds[0].events = POLLIN;
    /* Loop forever waiting for events on watched file descriptors */
	while (1) 
    {
		int res = poll(fds, 1, 1);

		if (res == -1) 
        {
			if (errno == EINTR)
				continue;
			else 
            {
				std::cout << "return";
                break;
			}
		}

		if (fds[0].revents & POLLIN) 
        {
			rc = saImmOiDispatch(immOiHandle, SA_DISPATCH_ONE);
			if (rc == SA_AIS_OK)
    	        std::cout << "Dispatch succeed" << std::endl; 		
            else 		
                std::cout << "Dispatch failed with error: " << rc << std::endl; 			
		}
	}
    /*******/

    /*Release roles for all object*/
    /*******/

    /*Clear the implementer name*/
    /*******/

    /* Close IMM Service */ 	
    saImmOiFinalize(immOiHandle); 	
    /*******/ 	
    return 0; 
}

