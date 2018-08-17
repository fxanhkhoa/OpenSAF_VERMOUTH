#include "../include/oi.h"
#include "../include/callbacks.h"
#include <iostream>
Oi::Oi(std::string implementerName, std::string className)
{
    SaImmOiCallbacksT_o3 immOiCallbacks = {.saImmOiAdminOperationCallback = adminOperationCallback,
                                        .saImmOiCcbAbortCallback = ccbAbortCallback,
                                        .saImmOiCcbApplyCallback = ccbApplyCallback,
                                        .saImmOiCcbCompletedCallback = ccbCompletedCallback,
		                                .saImmOiCcbObjectCreateCallback = ccbObjectCreateCallback,
		                                .saImmOiCcbObjectDeleteCallback = ccbObjectDeleteCallback,
		                                .saImmOiCcbObjectModifyCallback = ccbObjectModifyCallback,
                                        .saImmOiRtAttrUpdateCallback = rtAttrUpdateCallback}; 
    oiClass.initLibrary(&immOiCallbacks);
    oiClass.initSelectionObjectGet();
    oiClass.setImplementerName(implementerName);
    oiClass.setClassImplementer(className);
    m_className = className;
    oiClass.startDispatch();
}

Oi::~Oi()
{
    oiClass.stopDispatch();
    oiClass.releaseClass(m_className);
    oiClass.clearImplementerName();
    oiClass.finalizeLibrary();
}