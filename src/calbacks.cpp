#include "../include/callbacks.h"

void adminOperationCallback(SaImmOiHandleT immOiHandle,
							SaInvocationT invocation,
							SaConstStringT objectName,
							SaImmAdminOperationIdT operationId,
							const SaImmAdminOperationParamsT_2 **params)
{
}

void ccbAbortCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
}

void ccbApplyCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
}

SaAisErrorT ccbCompletedCallback(SaImmOiHandleT immOiHandle, SaImmOiCcbIdT ccbId)
{
	return SA_AIS_OK;
}

SaAisErrorT ccbObjectCreateCallback(SaImmOiHandleT immOiHandle,
									SaImmOiCcbIdT ccbId,
									const SaImmClassNameT className,
									SaConstStringT objectName, const SaImmAttrValuesT_2 **attr)
{

	std::string str = (char *)objectName;

	if (str.find(",user_name=root") == std::string::npos)
		return SA_AIS_OK;
	str = str.substr(0, str.length() - 15);
	if (str.length() < 6)
		return SA_AIS_ERR_INVALID_PARAM;
	if (str[0] >= '0' && str[0] <= '9')
		return SA_AIS_ERR_INVALID_PARAM;
	for (auto &c : str)
		if (!(isalpha(c) || c >= '0' && c <= '9'))
		{
			return SA_AIS_ERR_INVALID_PARAM;
		}
	const SaImmAttrValuesT_2 **curAttr = attr;
	for (; *curAttr; curAttr++)
	{

		//syslog(6, "attr name: %s", (*curAttr)->attrName);
		if (strcmp((*curAttr)->attrName, "passwd"))
		{
			continue;
		}
		str = *(SaStringT *)(*((*curAttr)->attrValues));
		//	syslog(6, "attr value: %s", *(SaStringT *)(*((*curAttr)->attrValues)));

		if (str.length() < 6)
		{
			//LOG("mat khau qua ngan");
			return SA_AIS_ERR_INVALID_PARAM;
		}
		int a = 0, b = 0, c = 0;
		for (auto &x : str)
		{
			if (isalpha(x))
				a = 1;
			else if (x >= '0' && x <= '9')
				b = 1;
			else
				c = 1;
		}
		if (a && b && c)
		{
			//LOG("Create callback ok");
			return SA_AIS_OK;
		}
		else
		{
			//LOG("passwd khong dung dinh dang");
			return SA_AIS_ERR_INVALID_PARAM;
		}
		//}
	}

	//	LOG("Create callback ok");
	return SA_AIS_OK;
}

SaAisErrorT ccbObjectDeleteCallback(SaImmOiHandleT immOiHandle,
									SaImmOiCcbIdT ccbId, SaConstStringT objectName)
{
	return SA_AIS_OK;
}

SaAisErrorT ccbObjectModifyCallback(SaImmOiHandleT immOiHandle,
									SaImmOiCcbIdT ccbId,
									SaConstStringT objectName, const SaImmAttrModificationT_2 **attrMods)
{
	int i = 0;
	while (attrMods[i])
	{
		const char *attr_name = attrMods[i]->modAttr.attrName;
		//syslog(6, "%s", attr_name);
		if (strcmp(attr_name, "passwd") == 0)
		{
			const char *attr_value = *(SaStringT *)(*(attrMods[i]->modAttr.attrValues));
			string str = attr_value;
			if (str.length() < 6)
			{
				//	LOG("mat khau qua ngan");
				return SA_AIS_ERR_INVALID_PARAM;
			}
			int a = 0, b = 0, c = 0;
			for (auto &x : str)
			{
				if (isalpha(x))
					a = 1;
				else if (x >= '0' && x <= '9')
					b = 1;
				else
					c = 1;
			}
			if (a && b && c)
			{
				//			LOG("Create callback ok");
				return SA_AIS_OK;
			}
			else
			{
				//		LOG("passwd khong dung dinh dang");
				return SA_AIS_ERR_INVALID_PARAM;
			}
		}
		++i;
	}

	//LOG("CCB object modify callback");
	return SA_AIS_OK;
}

SaAisErrorT rtAttrUpdateCallback(SaImmOiHandleT immOiHandle,
								 SaConstStringT objectName, const SaImmAttrNameT *attributeNames)
{
	return SA_AIS_OK;
}