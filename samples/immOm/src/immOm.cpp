#include <saImmOm.h>
#include <iostream>
#include <string>
#include <unistd.h>
#include <stdio.h>
#include <string.h>

SaStringT StringToSaString(const std::string& input)
{
   return (SaStringT)input.c_str();
}

int main (int argc, char* argv[])
{
	SaAisErrorT rc;
	/* Initialize object management functions of the IMM Service */
	SaImmHandleT immHandle;
	SaVersionT version ={.releaseCode = 'A',
						.majorVersion = 2,
						.minorVersion = 16};
	rc = saImmOmInitialize_o2(&immHandle, NULL, &version);
	if (rc == SA_AIS_OK)
		std::cout << "Library initialization succeed" << std::endl;
	else
		std::cout << "Library initialization failed with error: " << rc << std::endl;
	/*******/

	/* Create new configuration class*/
	std::string classNameS = "TestClass";
	const SaImmClassNameT className = (SaImmClassNameT)(classNameS.c_str());
	SaImmAttrDefinitionT_2 rdnAttr = {.attrName = StringToSaString("infoRDN"),
										.attrValueType = SA_IMM_ATTR_SASTRINGT ,
										.attrFlags = SA_IMM_ATTR_RDN | SA_IMM_ATTR_CONFIG,
										NULL};						
	SaImmAttrDefinitionT_2 attr2 = {.attrName = StringToSaString("testUint32"),
										.attrValueType = SA_IMM_ATTR_SAUINT32T,
										.attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
										NULL};
	SaFloatT attr3DefVal = (SaFloatT)3.14159;	
	SaImmAttrDefinitionT_2 attr3 = {.attrName = StringToSaString("testFloat"),
										.attrValueType = SA_IMM_ATTR_SAFLOATT,
										.attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
										.attrDefaultValue = &attr3DefVal};	
	SaImmAttrDefinitionT_2 attr4 = {.attrName = StringToSaString("testString"),
										.attrValueType = SA_IMM_ATTR_SASTRINGT ,
										.attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
										NULL};								
	const SaImmAttrDefinitionT_2 *classAttrArr[] = {&rdnAttr,&attr2,&attr3,&attr4,NULL};		
	rc = saImmOmClassCreate_2(immHandle, className, SA_IMM_CLASS_CONFIG, classAttrArr);
	if (rc == SA_AIS_OK)
		std::cout << "Class creation succeed" << std::endl;
	else 
		std::cout << "Class creation failed with error: " << rc << std::endl;
	/*******/

	/*Initialize a handle for an administrative owner*/
	SaImmAdminOwnerHandleT ownerHandle;
	std::string adminOwnerNameS = "shawry";
	const SaImmClassNameT adminOwnerName = (SaImmClassNameT)(adminOwnerNameS.c_str());
	rc = saImmOmAdminOwnerInitialize(immHandle,adminOwnerName,SA_TRUE,&ownerHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Admin owner creation succeed" << std::endl;
	else 
		std::cout << "Admin owner creation failed with error: " << rc << std::endl;
	/*******/

	/*Initialize a CCB handle */
	SaImmCcbHandleT ccbHandle;
	rc = saImmOmCcbInitialize(ownerHandle,0,&ccbHandle);
	if (rc == SA_AIS_OK)
		std::cout << "CCB handle creation succeed" << std::endl;
	else 
		std::cout << "CCB Handle creation failed with error: " << rc << std::endl;
	/*******/

	/*Create configuration object*/
	SaStringT attr1Val = StringToSaString("Unfortune");
	SaImmAttrValueT attrVals1 = &attr1Val;
	SaImmAttrValuesT_2 attrRDNVal = {.attrName = StringToSaString("infoRDN"),
									.attrValueType = SA_IMM_ATTR_SASTRINGT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals1};
	SaUint32T attr2Val = 22;
	SaImmAttrValueT attrVals2 = &attr2Val;
	SaImmAttrValuesT_2 attrVal2 = {.attrName = StringToSaString("testUint32"),
									.attrValueType = SA_IMM_ATTR_SAUINT32T,
									.attrValuesNumber = 1,
									.attrValues = &attrVals2};

	SaFloatT attr3Val = 3.15;
	SaImmAttrValueT attrVals3 =&attr3Val;
	SaImmAttrValuesT_2 attrVal3 = {.attrName = StringToSaString("testFloat"),
									.attrValueType = SA_IMM_ATTR_SAFLOATT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals3};

	SaStringT attr4Val = StringToSaString("Hello");
	SaImmAttrValueT attrVals4 = &attr4Val;
	SaImmAttrValuesT_2 attrVal4 = {.attrName = StringToSaString("testString"),
									.attrValueType = SA_IMM_ATTR_SASTRINGT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals4};

	const SaImmAttrValuesT_2 *attrValArr[] = {&attrRDNVal,&attrVal2,&attrVal3,&attrVal4,NULL};
	rc = saImmOmCcbObjectCreate_2(ccbHandle, className, NULL, attrValArr);
	if (rc == SA_AIS_OK)
		std::cout << "Object creation succeed" << std::endl;
	else 
		std::cout << "Object creation failed with error: " << rc << std::endl;
	/*******/
	
	/*Modify configuration object*/
	SaFloatT attr3MVal = 7.30;
	SaImmAttrValueT attrVals3M =&attr3MVal;
	SaImmAttrValuesT_2 attrVal3M = {.attrName = StringToSaString("testFloat"),
									.attrValueType = SA_IMM_ATTR_SAFLOATT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals3M};	
	SaImmAttrModificationT_2 attrMod1 = {.modType = SA_IMM_ATTR_VALUES_REPLACE,
								.modAttr = attrVal3M};

	const SaImmAttrModificationT_2 *attrMods[] = {&attrMod1,NULL};
	rc = saImmOmCcbObjectModify_o3(ccbHandle,attr1Val,attrMods);
	if (rc == SA_AIS_OK)
		std::cout << "Object modification succeed" << std::endl;
	else 
		std::cout << "Object modification failed with error: " << rc << std::endl;
	/*******/
	/*Modify configuration object attribute String*/
	SaStringT attr3MValString = StringToSaString("Hello world");
	SaImmAttrValueT attrVals3MString =&attr3MValString;
	SaImmAttrValuesT_2 attrVal3MString = {.attrName = StringToSaString("testString"),
									.attrValueType = SA_IMM_ATTR_SASTRINGT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals3MString};	
	SaImmAttrModificationT_2 attrModString = {.modType = SA_IMM_ATTR_VALUES_REPLACE,
								.modAttr = attrVal3MString};

	const SaImmAttrModificationT_2 *attrModsString[] = {&attrModString,NULL};
	rc = saImmOmCcbObjectModify_o3(ccbHandle,attr1Val,attrModsString);
	if (rc == SA_AIS_OK)
		std::cout << "Object modification Atribute String succeed" << std::endl;
	else 
		std::cout << "Object modification failed with error: " << rc << std::endl;
	/*******/
	
	/*Aplly configuration change bundle*/
	rc = saImmOmCcbApply(ccbHandle);
	if (rc == SA_AIS_OK)
		std::cout << "CCB applicableness succeed" << std::endl;
	else 
		std::cout << "CCB applicableness failed with error: " << rc << std::endl;
	/*******/
	system("immfind -c TestClass | xargs immlist");
	/*Initialize search operation*/
	SaImmSearchHandleT searchHandle;
	SaImmSearchOneAttrT_2 searchOneAttr = {.attrName = StringToSaString("infoRDN"),
											.attrValueType = SA_IMM_ATTR_SASTRINGT,
											.attrValue = NULL};
	SaImmSearchParametersT_2 searchParam = {.searchOneAttr = searchOneAttr};
	rc = saImmOmSearchInitialize_o3(immHandle,NULL,SA_IMM_SUBTREE,SA_IMM_SEARCH_GET_ALL_ATTR | SA_IMM_SEARCH_ONE_ATTR, &searchParam, NULL, &searchHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Search handle initialization succeed" << std::endl;
	else 
		std::cout << "Search handle initialization failed with error: " << rc << std::endl;
	/*******/

	/*Initialize an object accessor*/
	SaImmAccessorHandleT accessorHandle;
	rc =saImmOmAccessorInitialize(immHandle,&accessorHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Object accessor handle initialization succeed" << std::endl;
	else 
		std::cout << "Object accessor handle initialization failed with error: " << rc << std::endl;
	/*******/

	/*Obtain next object matching the search criteria */
	SaStringT objectName;
	SaImmAttrNameT attributeNames[] = {(SaImmAttrNameT)("infoRDN"),
										(SaImmAttrNameT)("testUint32"),
										(SaImmAttrNameT)("testFloat"),
										(SaImmAttrNameT)("testString"),
										(SaImmAttrNameT)SA_IMM_ATTR_CLASS_NAME,
										(SaImmAttrNameT)SA_IMM_ATTR_ADMIN_OWNER_NAME,
										(SaImmAttrNameT)SA_IMM_ATTR_IMPLEMENTER_NAME,
										NULL};
	
	SaImmAttrValuesT_2 **attributes;
	SaImmAttrValuesT_2 **attributes2;
	while (saImmOmSearchNext_o3(searchHandle, &objectName, &attributes) == SA_AIS_OK)
	{
		rc = saImmOmAccessorGet_o3(accessorHandle,objectName,attributeNames,&attributes2);
		if (rc == SA_AIS_OK)
			std::cout << "Get accessor succeed" << std::endl;
		else 
			std::cout << "Get accessor failed with error: " << rc << std::endl;
		while(*attributes2)
		{
			std::cout << (*attributes2)->attrName << " = ";
			if ((*attributes2)->attrValues == NULL)
			{
				std::cout << "NULL" << std::endl;
				attributes2++;
				continue;
			}	
			if ((*attributes2)->attrValueType == SA_IMM_ATTR_SAFLOATT)
				std::cout << *(SaFloatT*)(*((*attributes2)->attrValues)) << std::endl;
			if ((*attributes2)->attrValueType == SA_IMM_ATTR_SAUINT32T)
				std::cout << *(SaUint32T*)(*((*attributes2)->attrValues)) << std::endl;
			if ((*attributes2)->attrValueType == SA_IMM_ATTR_SASTRINGT)
				std::cout << *(SaStringT*)(*((*attributes2)->attrValues)) << std::endl;
			attributes2++;
		}
	}
	std::cout << "Searching completed" << std::endl;
	/*******/

	/*Finalize accessor handle*/
	rc = saImmOmAccessorFinalize(accessorHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Finalize accessor handle succeed" << std::endl;
	else 
		std::cout << "Finalize accessor handle failed with error: " << rc << std::endl;
	/*******/

	/*Release administative owner of Unfortune object*/
	SaConstStringT objNameArr[] = {(SaConstStringT)attr1Val,NULL};
	rc = saImmOmAdminOwnerRelease_o3(ownerHandle, objNameArr, SA_IMM_ONE);
	if (rc == SA_AIS_OK)
		std::cout << "Release administrative owner succeed" << std::endl;
	else 
		std::cout << "Release administrative owner failed with error: " << rc << std::endl;
	/*******/

	/*Initialize a handle for other administrative owner*/
	SaImmAdminOwnerHandleT ownerHandleNew;
	std::string adminOwnerNameNewS = "lucy";
	const SaImmClassNameT adminOwnerNameNew = (SaImmClassNameT)(adminOwnerNameNewS.c_str());
	rc = saImmOmAdminOwnerInitialize(immHandle,adminOwnerNameNew,SA_TRUE,&ownerHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "New admin owner creation succeed" << std::endl;
	else 
		std::cout << "New admin owner creation failed with error: " << rc << std::endl;
	/*******/

	/*Initialize a CCB handle for new administrative owner*/
	SaImmCcbHandleT ccbHandleNew;
	rc = saImmOmCcbInitialize(ownerHandleNew,0,&ccbHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "New CCB handle creation succeed" << std::endl;
	else 
		std::cout << "New CCB Handle creation failed with error: " << rc << std::endl;
	/*******/

	/*Set new administative owner of object*/
	rc = saImmOmAdminOwnerSet_o3(ownerHandleNew, objNameArr, SA_IMM_ONE);
	if (rc == SA_AIS_OK)
		std::cout << "Set new administrative owner succeed" << std::endl;
	else 
		std::cout << "Set new administrative owner failed with error: " << rc << std::endl;
	/*******/

	system("immfind -c TestClass | xargs immlist");

	/*Modify configuration object by new owner*/
	SaFloatT attr4MVal = 15.6;
	SaImmAttrValueT attrVals4M =&attr4MVal;
	SaImmAttrValuesT_2 attrVal4M = {.attrName = StringToSaString("testFloat"),
									.attrValueType = SA_IMM_ATTR_SAFLOATT,
									.attrValuesNumber = 1,
									.attrValues = &attrVals4M};	
	SaImmAttrModificationT_2 attrMod2 = {.modType = SA_IMM_ATTR_VALUES_REPLACE,
								.modAttr = attrVal3M};

	const SaImmAttrModificationT_2 *attrMods1[] = {&attrMod2,NULL};
	rc = saImmOmCcbObjectModify_o3(ccbHandleNew,attr1Val,attrMods1);
	if (rc == SA_AIS_OK)
		std::cout << "Object modification by new owner succeed" << std::endl;
	else 
		std::cout << "Object modification by new owner failed with error: " << rc << std::endl;
	rc = saImmOmCcbApply(ccbHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "CCB applicableness succeed" << std::endl;
	else 
		std::cout << "CCB applicableness failed with error: " << rc << std::endl;
	/*******/

	/*Delete configuration object by new owner*/
	rc = saImmOmCcbObjectDelete_o3(ccbHandleNew,attr1Val);
	if (rc == SA_AIS_OK)
		std::cout << "Object deletion by new owner succeed" << std::endl;
	else 
		std::cout << "Object deletion by new owner failed with error: " << rc << std::endl;
	rc = saImmOmCcbApply(ccbHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "CCB applicableness succeed" << std::endl;
	else 
		std::cout << "CCB applicableness failed with error: " << rc << std::endl;
	/*******/

	/*Finalize old CCB handle*/
	rc = saImmOmCcbFinalize(ccbHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Old CCB handle finalization succeed" << std::endl;
	else 
		std::cout << "Old CCB handle finalization failed with error: " << rc << std::endl;
	/*******/

	/*Finalize new CCB handle*/
	rc = saImmOmCcbFinalize(ccbHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "New CCB handle finalization succeed" << std::endl;
	else 
		std::cout << "New CCB handle finalization failed with error: " << rc << std::endl;
	/*******/
	/*Finalize old administrative owner handle*/
	rc = saImmOmAdminOwnerFinalize(ownerHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Old admin owner finalization succeed" << std::endl;
	else 
		std::cout << "Old admin owner finalization failed with error: " << rc << std::endl;
	/*******/

	/*Finalize new administrative owner handle*/
	rc = saImmOmAdminOwnerFinalize(ownerHandleNew);
	if (rc == SA_AIS_OK)
		std::cout << "New admin owner finalization succeed" << std::endl;
	else 
		std::cout << "New admin owner finalization failed with error: " << rc << std::endl;
	/*******/

	/*Delete configuration class*/
	rc = saImmOmClassDelete(immHandle,className);
	if (rc == SA_AIS_OK)
		std::cout << "Class deletion succeed" << std::endl;
	else 
		std::cout << "Class deletion failed with error: " << rc << std::endl;
	/*******/
	system("immfind -c TestClass | xargs immlist");
	/* Close IMM Service */
	saImmOmFinalize(immHandle);
	/*******/

	return 0;
}
