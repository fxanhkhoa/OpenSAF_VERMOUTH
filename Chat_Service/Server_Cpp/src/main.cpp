#include "../include/server.h"

List<user> user_data;
string file_path = "/home/ubuntu/database";
mutex mu;
mutex mu_lock_file;
server *sv = NULL;
volatile int state;
int start()
{

	// syslog(6, "tao cung dang chay ne");
	syslog(6, "bat dau start");
	user_data.load(file_path.c_str());
	sv = server::get_instance();
	syslog(6, "load data ok");
	while (state == 0)
		;
	syslog(6, "chay vo day ok");

hell:
	if (state == 1)
	{

		thread yyy(&server::chat_client, sv);

		yyy.detach();

		thread xxx(&server::listen_connect, sv);
		xxx.join();
	}
	else
	{
		thread www(&server::stand_by, sv);
		www.join();
	}
	goto hell;
}

/*      -*- OpenSAF  -*-
 *
 * (C) Copyright 2009 The OpenSAF Foundation
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. This file and program are licensed
 * under the GNU Lesser General Public License Version 2.1, February 1999.
 * The complete license can be accessed from the following location:
 * http://opensource.org/licenses/lgpl-license.php
 * See the Copying file included with the OpenSAF distribution for full
 * licensing terms.
 *
 * Author(s): Ericsson
 */

/*****************************************************************************

  DESCRIPTION:

  This file contains a sample AMF component. It behaves nicely and responds OK
  to every AMF request.
  It can be used as a template for making a service SA-Aware.

  References are made to sequence diagrams in the AMF specification B.04

******************************************************************************
*/

thread *p = NULL;
#define MD5_LEN 32
extern "C" void saAisNameLend(SaConstStringT value, SaNameT *name);
extern "C" SaConstStringT saAisNameBorrow(const SaNameT *name);
/* Some dummies in place of real service logic */
int foo_activate(void)
{

	//syslog(6, "");
	mu.lock();
	state = 1;

	mu.unlock();
	syslog(6, "state = 1");
	if (sv == NULL)
	{
		thread xx(start);
		xx.detach();
	}
	return 0;
}

int foo_deactivate(void) { return 0; }

int foo_healthcheck(void) { return 0; }

/* AMF Handle */
static SaAmfHandleT my_amf_hdl;

/* HealthCheck Key on which ddhealthcheck is started */
static SaAmfHealthcheckKeyT my_healthcheck_key = {"AmfDemo", 7};

/* HA state of the application */
static SaAmfHAStateT my_ha_state;

/* Distinguised Name of the AMF component */
static SaNameT my_comp_name;

/* Logical HA State names for nicer logging */
static const char *ha_state_name[] = {
	"None", "Active", /* SA_AMF_HA_ACTIVE       */
	"Standby",		  /* SA_AMF_HA_STANDBY      */
	"Quiesced",		  /* SA_AMF_HA_QUIESCED     */
	"Quiescing"		  /* SA_AMF_HA_QUIESCING    */
};

/**
 * AMF invokes this callback to assign a new workload (ADD_ONE) or
 * to change state of an already assigned workload (TARGET_ALL).
 * The callback is used for the initial assignment, as a consequence
 * of admin operations and fail/switch-over
 *
 * See example sequence diagrams in chapter 10.
 *
 * @param invocation
 * @param comp_name
 * @param ha_state
 * @param csi_desc
 */
static void amf_csi_set_callback(SaInvocationT invocation,
								 const SaNameT *comp_name,
								 SaAmfHAStateT ha_state,
								 SaAmfCSIDescriptorT csi_desc)
{
	SaAisErrorT rc, error;
	SaAmfCSIAttributeT *attr;
	int i, status;
	thread *th;
	if (csi_desc.csiFlags == SA_AMF_CSI_ADD_ONE)
	{

		syslog(LOG_INFO, "CSI Set - add '%s' HAState %s",
			   saAisNameBorrow(&csi_desc.csiName),
			   ha_state_name[ha_state]);

		/* For debug log the CSI attributes, they could
		** define the workload characteristics */
		for (i = 0; i < csi_desc.csiAttr.number; i++)
		{
			attr = &csi_desc.csiAttr.attr[i];
			syslog(LOG_DEBUG, "    name: %s, value: %s",
				   attr->attrName, attr->attrValue);
		}
	}
	else if (csi_desc.csiFlags == SA_AMF_CSI_TARGET_ALL)
	{
		syslog(LOG_INFO, "CSI Set - HAState %s for all assigned CSIs",
			   ha_state_name[ha_state]);
	}
	else
	{
		syslog(LOG_INFO, "CSI Set - HAState %s for '%s'",
			   ha_state_name[ha_state],
			   saAisNameBorrow(&csi_desc.csiName));
	}

	switch (ha_state)
	{
	case SA_AMF_HA_ACTIVE:
		status = foo_activate();
		break;
	case SA_AMF_HA_STANDBY:
		/*
		 * Not much to do in this simple example code
		 * For real one could open a checkpoint for reads
		 * Open a communication channel for listening
		 * etc.
		 */
		//	system("echo ubuntu|sudo -S ifconfig eth0:server down");
		mu.lock();
		state = 2;
		mu.unlock();
		syslog(6, "state = 2");
		if (sv == NULL)
		{
			th = new thread(start);
			th->detach();
		}
		status = 0;
		break;
	case SA_AMF_HA_QUIESCED:
		/* the effect of admin op lock on SU or node or ... */
		status = foo_deactivate();
		break;
	case SA_AMF_HA_QUIESCING:
		/* the effect of admin op lock on SU or node or ... */
		status = 0;
		break;
	default:
		syslog(LOG_ERR, "unmanaged HA state %u", ha_state);
		status = -1;
		break;
	}

	if (status == 0)
		error = SA_AIS_OK;
	else
		error = SA_AIS_ERR_FAILED_OPERATION;

	rc = saAmfResponse_4(my_amf_hdl, invocation, 0, error);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		exit(1);
	}

	if (ha_state == SA_AMF_HA_QUIESCING)
	{
		/* "gracefully quiescing CSI work assignment" */
		sleep(1);
		rc = saAmfCSIQuiescingComplete(my_amf_hdl, invocation, SA_AIS_OK);
		if (rc != SA_AIS_OK)
		{
			syslog(LOG_ERR, "saAmfCSIQuiescingComplete FAILED - %u",
				   rc);
			exit(1);
		}
		if (csi_desc.csiFlags == SA_AMF_CSI_TARGET_ONE)
		{
			rc = saAmfHAStateGet(my_amf_hdl, comp_name,
								 &csi_desc.csiName, &my_ha_state);
			if (rc != SA_AIS_OK)
			{
				syslog(LOG_ERR, "saAmfHAStateGet FAILED - %u",
					   rc);
				exit(1);
			}
		}
		else if (csi_desc.csiFlags == SA_AMF_CSI_TARGET_ALL)
		{
			// Application could iterate saAmfHAStateGet() for every
			// csi which had been assigned to this component to
			// ensure all csi(s) are QUIESCED

			// temporary set to QUIESCED
			my_ha_state = SA_AMF_HA_QUIESCED;
		}

		syslog(LOG_INFO, "My HA state is %s",
			   ha_state_name[my_ha_state]);
	}
}

/**
 * AMF invokes this callback to remove a previously assigned workload.
 * As a consequence of admin lock of the SU, a CSI would first get QUIESCED
 * and then removed.
 * See Figure 44, page 405
 *
 * @param invocation
 * @param comp_name
 * @param csi_name
 * @param csi_flags
 */
static void amf_csi_remove_callback(SaInvocationT invocation,
									const SaNameT *comp_name,
									const SaNameT *csi_name,
									SaAmfCSIFlagsT csi_flags)
{
	SaAisErrorT rc;

	if (csi_flags == SA_AMF_CSI_TARGET_ALL)
		syslog(LOG_INFO, "CSI Remove for all CSIs");
	else if (csi_flags == SA_AMF_CSI_TARGET_ONE)
		syslog(LOG_INFO, "CSI Remove for '%s'",
			   saAisNameBorrow(csi_name));
	else
		// A non valid case, see 7.9.3
		abort();

	/* Reset the HA state */
	my_ha_state = (SaAmfHAStateT)0;

	rc = saAmfResponse_4(my_amf_hdl, invocation, 0, SA_AIS_OK);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		exit(1);
	}
}

/**
 * AMF invoked this callback periodically to assess our health.
 *
 * @param inv
 * @param comp_name
 * @param health_check_key
 */
static void amf_healthcheck_callback(SaInvocationT inv,
									 const SaNameT *comp_name,
									 SaAmfHealthcheckKeyT *health_check_key)
{
	SaAisErrorT rc, status = SA_AIS_OK;
	static int healthcheck_count = 0;

	healthcheck_count++;

	syslog(LOG_DEBUG, "Health check %u", healthcheck_count);

	/* Check the status of our service but only if active */
	if ((my_ha_state == SA_AMF_HA_ACTIVE) && (foo_healthcheck() != 0))
	{
		/* 7.8.2 - an error report should be done before returning
		 * failed op */
		rc = saAmfComponentErrorReport(my_amf_hdl, &my_comp_name, 0,
									   SA_AMF_COMPONENT_RESTART,
									   SA_NTF_IDENTIFIER_UNUSED);
		if (rc != SA_AIS_OK)
		{
			syslog(LOG_ERR, "saAmfComponentErrorReport FAILED - %u",
				   rc);
			exit(1);
		}
		status = SA_AIS_ERR_FAILED_OPERATION;
	}

	rc = saAmfResponse_4(my_amf_hdl, inv, 0, status);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		exit(1);
	}
}

/**
 * AMF invokes this callback as a consequence of admin operations
 * such as SU or node lock-instantiation.
 *
 * @param inv
 * @param comp_name
 */
static void amf_comp_terminate_callback(SaInvocationT inv,
										const SaNameT *comp_name)
{
	SaAisErrorT rc;

	syslog(LOG_NOTICE, "Terminating");

	rc = saAmfResponse_4(my_amf_hdl, inv, 0, SA_AIS_OK);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		exit(1);
	}

	exit(0);
}

/**
 * AMF invokes this callback as a consequence of change in
 * csi attribute value.
 * @param inv
 * @param csi_name
 * @param csiAttr
 */
static void amf_csi_attr_change_callback(SaInvocationT invocation,
										 const SaNameT *csi_name,
										 SaAmfCSIAttributeListT csiAttr)
{
	SaAisErrorT rc;
	SaAmfCSIAttributeT *attr;
	static int i;
	syslog(LOG_INFO, "=====CSI Attr Change====>");

	syslog(LOG_INFO, "CSI----->:'%s'", saAisNameBorrow(csi_name));
	for (i = 0; i < csiAttr.number; i++)
	{
		attr = &csiAttr.attr[i];
		syslog(LOG_INFO, "CSIATTR--->: %s, val--->: %s", attr->attrName,
			   attr->attrValue);
	}
	rc = saAmfResponse_4(my_amf_hdl, invocation, 0, SA_AIS_OK);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		exit(1);
	}
	syslog(LOG_INFO, "<=================");
}

/**
 * Create a PID file in directory
 *
 * @param directory
 * @param filename_prefix
 */
static void create_pid_file(const char *directory, const char *filename_prefix)
{
	char path[256];
	FILE *fp;

	snprintf(path, sizeof(path), "%s/%s.pid", directory, filename_prefix);

	fp = fopen(path, "w");
	if (fp == NULL)
	{
		syslog(LOG_ERR, "fopen '%s' failed: %s", path, strerror(errno));
		exit(EXIT_FAILURE);
	}
	fprintf(fp, "%d\n", getpid());
	fclose(fp);
}

/**
 * Our TERM signal handler
 * @param sig
 */
static void sigterm_handler(int sig)
{
	/* Don't log in a signal handler! But we're exiting anyway... */
	syslog(LOG_NOTICE, "exiting (caught term signal)");
	exit(EXIT_SUCCESS);
}

/**
 * Initialize with AMF
 * @param amf_sel_obj [out]
 *
 * @return SaAisErrorT
 */
static SaAisErrorT amf_initialize(SaSelectionObjectT *amf_sel_obj)
{
	SaAisErrorT rc;
	SaAmfCallbacksT_o4 amf_callbacks = {0};
	SaVersionT api_ver = {.releaseCode = 'B',
						  api_ver.majorVersion = 0x04,
						  api_ver.minorVersion = 0x02};

	/* Initialize our callbacks */
	amf_callbacks.saAmfCSISetCallback = amf_csi_set_callback;
	amf_callbacks.saAmfCSIRemoveCallback = amf_csi_remove_callback;
	amf_callbacks.saAmfHealthcheckCallback = amf_healthcheck_callback;
	amf_callbacks.saAmfComponentTerminateCallback =
		amf_comp_terminate_callback;
	amf_callbacks.osafCsiAttributeChangeCallback =
		amf_csi_attr_change_callback;
	rc = saAmfInitialize_o4(&my_amf_hdl, &amf_callbacks, &api_ver);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, " saAmfInitialize FAILED %u", rc);
		goto done;
	}

	rc = saAmfSelectionObjectGet(my_amf_hdl, amf_sel_obj);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfSelectionObjectGet FAILED %u", rc);
		goto done;
	}

	rc = saAmfComponentNameGet(my_amf_hdl, &my_comp_name);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfComponentNameGet FAILED %u", rc);
		goto done;
	}

	syslog(LOG_INFO, "before saAmfComponentRegister [%s]",
		   saAisNameBorrow(&my_comp_name));
	rc = saAmfComponentRegister(my_amf_hdl, &my_comp_name, 0);
	syslog(LOG_INFO, "after saAmfComponentRegister ");
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfComponentRegister FAILED %u", rc);
		goto done;
	}

	rc = saAmfHealthcheckStart(
		my_amf_hdl, &my_comp_name, &my_healthcheck_key,
		SA_AMF_HEALTHCHECK_AMF_INVOKED, SA_AMF_COMPONENT_RESTART);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfHealthcheckStart FAILED - %u", rc);
		goto done;
	}
done:
	return rc;
}

static int getMD5Code(const char *str, char *md5_sum)
{
	char cmd[2048];
	FILE *pipe;
	int i, ch;

	sprintf(cmd, "echo %s | md5sum | awk '{print $1}' 2>/dev/null", str);
	pipe = popen(cmd, "r");
	if (pipe == NULL)
		return 0;

	for (i = 0; i < MD5_LEN && isxdigit(ch = fgetc(pipe)); i++)
	{
		*md5_sum++ = ch;
	}

	*md5_sum = '\0';
	pclose(pipe);
	return i == MD5_LEN;
}

//#define StringToSaString(u) (SaStringT) u
#define StringToSaString(u) (SaStringT) u
void load_user_data(unsigned long long immHandle)
{
	SaAisErrorT rc;
	/*Initialize search operation*/
	SaImmSearchHandleT searchHandle;
	SaImmSearchOneAttrT_2 searchOneAttr = {.attrName = "user_id",
										   .attrValueType = SA_IMM_ATTR_SASTRINGT,
										   .attrValue = NULL};
	SaImmSearchParametersT_2 searchParam = {.searchOneAttr = searchOneAttr};
	rc = saImmOmSearchInitialize_o3(immHandle, NULL, SA_IMM_SUBTREE, SA_IMM_SEARCH_GET_ALL_ATTR | SA_IMM_SEARCH_ONE_ATTR, &searchParam, NULL, &searchHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Search handle initialization succeed" << std::endl;
	else
		std::cout << "Search handle initialization failed with error: " << rc << std::endl;
	/*******/

	/*Obtain next object matching the search criteria */
	SaStringT objectName;

	SaImmAttrValuesT_2 **attributes;
	char u_name[30], pass[30];
	int _id;

	while (saImmOmSearchNext_o3(searchHandle, &objectName, &attributes) == SA_AIS_OK)
	{

		for (int i = 0; i < 4; ++i)
		{
			if (strcmp(attributes[i]->attrName, "user_id") == 0)
			{
				_id = *((int *)attributes[i]->attrValues);
			}
			if (strcmp(attributes[i]->attrName, "user_name") == 0)
			{
				strcpy(u_name, (char *)attributes[i]->attrValues);
			}
			if (strcmp(attributes[i]->attrName, "passwd") == 0)
			{
				strcpy(pass, (char *)attributes[i]->attrValues);
			}
		}
		user_data.insert(u_name, pass, _id);
	}
	std::cout << "Searching completed" << std::endl;
	saImmOmSearchFinalize(searchHandle);
}
inline SaAisErrorT create_IMM_user_class(SaImmHandleT immHandle)
{
	const SaImmClassNameT className = "USER";
	SaImmAttrDefinitionT_2 attr1 = {.attrName = StringToSaString("user_name"),
									.attrValueType = SA_IMM_ATTR_SASTRINGT,
									.attrFlags = SA_IMM_ATTR_RDN | SA_IMM_ATTR_CONFIG,
									NULL};
	SaImmAttrDefinitionT_2 attr2 = {.attrName = StringToSaString("passwd"),
									.attrValueType = SA_IMM_ATTR_SASTRINGT,
									.attrFlags = SA_IMM_ATTR_RDN | SA_IMM_ATTR_CONFIG,
									NULL};
	SaImmAttrDefinitionT_2 rdnAttr = {.attrName = StringToSaString("user_id"),
									  .attrValueType = SA_IMM_ATTR_SAUINT32T,
									  .attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
									  NULL};
	SaImmAttrDefinitionT_2 attr3 = {.attrName = StringToSaString("xxx"),
									.attrValueType = SA_IMM_ATTR_SAUINT32T,
									.attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
									NULL};

	const SaImmAttrDefinitionT_2 *classAttrArr[] = {&rdnAttr, &attr1, &attr2, &attr3, NULL};
	return saImmOmClassCreate_2(immHandle, className, SA_IMM_CLASS_CONFIG, classAttrArr);
}
int main(int argc, char **argv)
{
	state = 0;
	SaAisErrorT rc;

	SaImmHandleT immHandle;
	SaVersionT version = {.releaseCode = 'A',
						  .majorVersion = 2,
						  .minorVersion = 16};
	rc = saImmOmInitialize_o2(&immHandle, NULL, &version);
	if (rc == SA_AIS_OK)
		std::cout << "Library initialization succeed" << std::endl;
	else
		std::cout << "Library initialization failed with error: " << rc << std::endl;

	rc = create_IMM_user_class(immHandle);
	if (rc == SA_AIS_OK)
		std::cout << "Class creation succeed" << std::endl;
	else
		std::cout << "Class creation failed with error: " << rc << std::endl;



	SaImmAttrDefinitionT_2 rdnAttr1 = {.attrName = StringToSaString("currentCount"),
									   .attrValueType = SA_IMM_ATTR_SASTRINGT,
									   .attrFlags = SA_IMM_ATTR_RDN | SA_IMM_ATTR_CONFIG,
									   0};
	SaImmAttrDefinitionT_2 attr21 = {.attrName = StringToSaString("value"),
									 .attrValueType = SA_IMM_ATTR_SAUINT32T,
									 .attrFlags = SA_IMM_ATTR_WRITABLE | SA_IMM_ATTR_CONFIG,
									 NULL};

	const SaImmAttrDefinitionT_2 *classAttrArr1[] = {&rdnAttr1, &attr21, NULL};
	rc = saImmOmClassCreate_2(immHandle, "UIDClass", SA_IMM_CLASS_CONFIG, classAttrArr1);
	if (rc == SA_AIS_OK)
	{
		std::cout << "Class UID creation succeed" << std::endl;
		user::user_id = 0;
	}
	else
	{
		std::cout << "Class creation failed with error: " << rc << std::endl;
	}

	/*amf start*/

	// 	SaSelectionObjectT amf_sel_obj;
	// 	struct pollfd fds[1];
	// 	char *env_comp_name;
	// 	char md5[MD5_LEN + 1];

	// 	/* Environment variable "SA_AMF_COMPONENT_NAME" exist when started by
	// 	 * AMF */
	// 	if ((env_comp_name = getenv("SA_AMF_COMPONENT_NAME")) == NULL)
	// 	{
	// 		fprintf(stderr, "not started by AMF exiting...\n");
	// 		exit(EXIT_FAILURE);
	// 	}

	// 	/* Daemonize ourselves and detach from terminal.
	// 	** This important since our start script will hang forever otherwise.
	// 	** Note daemon() is not LSB but impl by libc so fairly portable...
	// 	*/
	// 	if (daemon(0, 1) == -1)
	// 	{
	// 		syslog(LOG_ERR, "daemon failed: %s", strerror(errno));
	// 		goto done;
	// 	}

	// 	/* Install a TERM handler just to log and visualize when cleanup is
	// 	 * called */
	// 	if ((signal(SIGTERM, sigterm_handler)) == SIG_ERR)
	// 	{
	// 		syslog(LOG_ERR, "signal TERM failed: %s", strerror(errno));
	// 		goto done;
	// 	}

	// 	/* Create a PID file which is needed by our CLC-CLI script.
	// 	** Use AMF component name as file name so multiple instances of this
	// 	** component can be managed by the same script.
	// 	*/
	// 	// This is a temporary solution to overcome the limit of linux in
	// 	// filename length (255)
	// 	// create_pid_file("/tmp", env_comp_name);
	// 	if (!getMD5Code(env_comp_name, md5))
	// 	{
	// 		syslog(LOG_ERR, "failed to get the hash code of comp: %s",
	// 			   env_comp_name);
	// 		goto done;
	// 	}

	// 	// Create a file with the hashed name
	// 	create_pid_file("/tmp", md5);

	// 	// Enable long DN
	// 	if (setenv("SA_ENABLE_EXTENDED_NAMES", "1", 1))
	// 	{
	// 		syslog(LOG_ERR, "failed to set SA_ENABLE_EXTENDED_NAMES");
	// 	}

	// 	/* Use syslog for logging */
	// 	openlog(basename(argv[0]), LOG_PID, LOG_USER);

	// 	/* Make a log to associate component name with PID */
	// 	syslog(LOG_INFO, "'%s' started", env_comp_name);

	// 	if (amf_initialize(&amf_sel_obj) != SA_AIS_OK)
	// 		goto done;

	// 	syslog(LOG_INFO, "Registered with AMF and HC started");

	// 	fds[0].fd = amf_sel_obj;
	// 	fds[0].events = POLLIN;

	// 	/* Loop forever waiting for events on watched file descriptors */
	// 	while (1)
	// 	{
	// 		int res = poll(fds, 1, -1);

	// 		if (res == -1)
	// 		{
	// 			if (errno == EINTR)
	// 				continue;
	// 			else
	// 			{
	// 				syslog(LOG_ERR, "poll FAILED - %s",
	// 					   strerror(errno));
	// 				goto done;
	// 			}
	// 		}

	// 		if (fds[0].revents & POLLIN)
	// 		{
	// 			/* An AMF event is received, call AMF dispatch which in
	// 			 * turn will call our installed callbacks. In context of
	// 			 * this main thread.
	// 			 */
	// 			rc = saAmfDispatch(my_amf_hdl, SA_DISPATCH_ONE);
	// 			if (rc != SA_AIS_OK)
	// 			{
	// 				syslog(LOG_ERR, "saAmfDispatch FAILED %u", rc);
	// 				goto done;
	// 			}
	// 		}
	// 	}

	// done:
	// 	system("echo ubuntu|sudo -S ifconfig eth0:server down");
	// 	return EXIT_FAILURE;
}
