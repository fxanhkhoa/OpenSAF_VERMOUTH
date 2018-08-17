#include "../include/server.h"

List<user> user_data;
mutex mu;
void start_immOI();
Oi *oi = NULL;
server *sv = NULL;
volatile int state;
SaImmHandleT immHandle;
int start()
{
	sv = server::get_instance();
pig:
	while (state == 0)
		;
	syslog(6, "state = %d", state);

hell:
	if (state == 1)
	{

		thread yyy(&server::chat_client, sv);

		yyy.detach();

		thread xxx(&server::listen_connect, sv);
		xxx.join();
		thread zzz(&server::sync_room, sv);
		zzz.detach();
		goto hell;
	}
	else if (state == 2)
	{
		thread www(&server::stand_by, sv);
		www.join();
		goto hell;
	}
	goto pig;
}

thread *p = NULL;
#define MD5_LEN 32
extern "C" void saAisNameLend(SaConstStringT value, SaNameT *name);
extern "C" SaConstStringT saAisNameBorrow(const SaNameT *name);
/* Some dummies in place of real service logic */
int foo_activate(void)
{

	mu.lock();
	state = 1;

	mu.unlock();
	if (user_data.empty())
	{
		server::load_user_data();
	}

	oi = new Oi("Server", "USER");
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
		mu.lock();
		state = 0;
		mu.unlock();
		syslog(6, "quiesced state = 0");
		status = 0;
		break;
	case SA_AMF_HA_QUIESCING:
		/* the effect of admin op lock on SU or node or ... */
		mu.lock();
		state = 0;
		mu.unlock();
		syslog(6, "quiescing state = 0");
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
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
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
			system("echo ubuntu|sudo -S ifconfig eth0:server down");
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
				system("echo ubuntu|sudo -S ifconfig eth0:server down");
				exit(1);
			}
		}
		else if (csi_desc.csiFlags == SA_AMF_CSI_TARGET_ALL)
		{

			my_ha_state = SA_AMF_HA_QUIESCED;
		}

		syslog(LOG_INFO, "My HA state is %s",
			   ha_state_name[my_ha_state]);
	}
}

static void amf_csi_remove_callback(SaInvocationT invocation,
									const SaNameT *comp_name,
									const SaNameT *csi_name,
									SaAmfCSIFlagsT csi_flags)
{
	mu.lock();
	state = 0;
	mu.unlock();
	syslog(6, "remove callback state = 0");

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
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(1);
	}
}

static void amf_healthcheck_callback(SaInvocationT inv,
									 const SaNameT *comp_name,
									 SaAmfHealthcheckKeyT *health_check_key)
{
	SaAisErrorT rc, status = SA_AIS_OK;
	static int healthcheck_count = 0;

	healthcheck_count++;

	//syslog(LOG_DEBUG, "Health check %u", healthcheck_count);

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
			system("echo ubuntu|sudo -S ifconfig eth0:server down");
			exit(1);
		}
		status = SA_AIS_ERR_FAILED_OPERATION;
	}

	rc = saAmfResponse_4(my_amf_hdl, inv, 0, status);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(1);
	}
}

static void amf_comp_terminate_callback(SaInvocationT inv,
										const SaNameT *comp_name)
{
	SaAisErrorT rc;

	syslog(LOG_NOTICE, "Terminating");

	rc = saAmfResponse_4(my_amf_hdl, inv, 0, SA_AIS_OK);
	if (rc != SA_AIS_OK)
	{
		syslog(LOG_ERR, "saAmfResponse_4 FAILED - %u", rc);
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(1);
	}
	system("echo ubuntu|sudo -S ifconfig eth0:server down");
	exit(0);
}

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
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(1);
	}
	syslog(LOG_INFO, "<=================");
}

static void create_pid_file(const char *directory, const char *filename_prefix)
{
	char path[256];
	FILE *fp;

	snprintf(path, sizeof(path), "%s/%s.pid", directory, filename_prefix);

	fp = fopen(path, "w");
	if (fp == NULL)
	{
		syslog(LOG_ERR, "fopen '%s' failed: %s", path, strerror(errno));
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(EXIT_FAILURE);
	}
	fprintf(fp, "%d\n", getpid());
	fclose(fp);
}

static void sigterm_handler(int sig)
{
	/* Don't log in a signal handler! But we're exiting anyway... */
	syslog(LOG_NOTICE, "exiting (caught term signal)");
	
	state = 0;
	sleep(1);
	//system("echo ubuntu|sudo -S ifconfig eth0:server down");
	exit(EXIT_SUCCESS);
}

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

SaStringT StringToSaString(const std::string &input)
{
	return (SaStringT)input.c_str();
}

#define LOG(a) syslog(6, a)

int main(int argc, char **argv)
{
	state = 0;
	SaAisErrorT rc;

	/*amf start*/
	thread *th_p;
	SaSelectionObjectT amf_sel_obj;
	struct pollfd fds[1];
	char *env_comp_name;
	char md5[MD5_LEN + 1];
	signal(SIGTERM,sigterm_handler);
	/* Environment variable "SA_AMF_COMPONENT_NAME" exist when started by
		 * AMF */
	//	LOG("co chayj vo day");
	if ((env_comp_name = getenv("SA_AMF_COMPONENT_NAME")) == NULL)
	{
		fprintf(stderr, "not started by AMF exiting...\n");
		system("echo ubuntu|sudo -S ifconfig eth0:server down");
		exit(EXIT_FAILURE);
	}

	/* Daemonize ourselves and detach from terminal.
		** This important since our start script will hang forever otherwise.
		** Note daemon() is not LSB but impl by libc so fairly portable...
		*/
	if (daemon(0, 1) == -1)
	{
		syslog(LOG_ERR, "daemon failed: %s", strerror(errno));
		goto done;
	}

	/* Install a TERM handler just to log and visualize when cleanup is
		 * called */
	if ((signal(SIGTERM, sigterm_handler)) == SIG_ERR)
	{
		syslog(LOG_ERR, "signal TERM failed: %s", strerror(errno));
		goto done;
	}

	/* Create a PID file which is needed by our CLC-CLI script.
		** Use AMF component name as file name so multiple instances of this
		** component can be managed by the same script.
		*/

	if (!getMD5Code(env_comp_name, md5))
	{
		syslog(LOG_ERR, "failed to get the hash code of comp: %s",
			   env_comp_name);
		goto done;
	}

	// Create a file with the hashed name
	create_pid_file("/tmp", md5);

	// Enable long DN
	if (setenv("SA_ENABLE_EXTENDED_NAMES", "1", 1))
	{
		syslog(LOG_ERR, "failed to set SA_ENABLE_EXTENDED_NAMES");
	}

	/* Use syslog for logging */
	openlog(basename(argv[0]), LOG_PID, LOG_USER);

	/* Make a log to associate component name with PID */
	syslog(LOG_INFO, "'%s' started", env_comp_name);

	if (amf_initialize(&amf_sel_obj) != SA_AIS_OK)
		goto done;

	syslog(LOG_INFO, "Registered with AMF and HC started");

	immOM::get_instance();
	immHandle = immOM::get_instance()->get_handle();
	server::load_user_data();
	fds[0].fd = amf_sel_obj;
	fds[0].events = POLLIN;

	/* Loop forever waiting for events on watched file descriptors */
	while (1)
	{
		int res = poll(fds, 1, -1);

		if (res == -1)
		{
			if (errno == EINTR)
				continue;
			else
			{
				syslog(LOG_ERR, "poll FAILED - %s",
					   strerror(errno));
				goto done;
			}
		}

		if (fds[0].revents & POLLIN)
		{
			rc = saAmfDispatch(my_amf_hdl, SA_DISPATCH_ONE);
			if (rc != SA_AIS_OK)
			{
				syslog(LOG_ERR, "saAmfDispatch FAILED %u", rc);
				goto done;
			}
		}
	}

done:
	system("echo ubuntu|sudo -S ifconfig eth0:server down");
	//saImmOmFinalize(immHandle);
	immOM::get_instance()->finalize();
	return EXIT_FAILURE;
}