#pragma once
#include <saImmOi.h>
#include<ctype.h>
#include<string.h>
#include<string>
#include<iostream>
using namespace std;
void adminOperationCallback(SaImmOiHandleT immOiHandle,
					      SaInvocationT invocation,
					      SaConstStringT objectName,
					      SaImmAdminOperationIdT operationId,
					      const SaImmAdminOperationParamsT_2 **params);
void ccbAbortCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId);
void ccbApplyCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId);
SaAisErrorT ccbCompletedCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId);
SaAisErrorT ccbObjectCreateCallback(SaImmOiHandleT immOiHandle,
					       SaImmOiCcbIdT ccbId,
					       const SaImmClassNameT className,
					       SaConstStringT objectName, const SaImmAttrValuesT_2 **attr);
SaAisErrorT ccbObjectDeleteCallback(SaImmOiHandleT immOiHandle,
					     SaImmOiCcbIdT ccbId, SaConstStringT objectName);
SaAisErrorT ccbObjectModifyCallback(SaImmOiHandleT immOiHandle,
					       SaImmOiCcbIdT ccbId,
					       SaConstStringT objectName, const SaImmAttrModificationT_2 **attrMods);
SaAisErrorT rtAttrUpdateCallback(SaImmOiHandleT immOiHandle,
					  SaConstStringT objectName, const SaImmAttrNameT *attributeNames);