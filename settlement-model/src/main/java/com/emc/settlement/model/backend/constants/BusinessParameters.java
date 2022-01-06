package com.emc.settlement.model.backend.constants;

public class BusinessParameters {

	public static final String UPLOAD_METHOD_EBT = "EBT";

	public static final String FILE_VALIDATION = "FILE_VALIDATION";

	public static final String CLW_UPLEFDATE_VALIDATION = "CLW_UPLEFDATE_VALIDATION";

	public static final String TIME_FORMAT_1 = "yyyy.MM.dd.HH.mm.ss.SSS";

//	public static final String ACK_FILE_BASE_DIR = "/Outgoing/ACK_FILES/";

	

//	public static String CASHFLOW_BASE_DIR = "/app/CashFlowReconciliationFile/";
	//public static String CASHFLOW_BASE_DIR = "D:\\\\temp\\\\CashFlowReconciliationFile\\\\";

//	public static String CMF_BASE_DIR = "/CMF/";

	
	//Moved to properties
	//public static String ALLOW_SCHEDULED_TASK = "N";
	//public static String DAILY_RUN_SUMMARY_EMAIL = "settlement.project2008@emcsg.com";
	//public static final String EMCPSO_UPLOAD_FAIL_EMAIL = "settlement.project2008@emcsg.com";
	//public static final String FILE_UPLOAD_EBT_EMAIL = "settlement.project2008@emcsg.com";
	//public static final String FILE_UPLOAD_FAIL_EMAIL = "settlement.project2008@emcsg.com";
	//public static String MSSL_DESTINATION_URL = "https://UATServer1:50002/emcwsProject/MSSLNotificationService";
	//public static String SETTLEMENT_RUN_EMAIL = "settlement.project2008@emcsg.com";
	
//	public static String DATE_FORMAT = "dd-MM-yyyy";

	public static final String DATE_FORMAT_DD_MMM_YY = "dd-MMM-yy";

//	public static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
	public static final String DISPLAY_DATE_FORMAT_1 = "yyyyMMdd_HHmmss";
	public static final String DISPLAY_DATE_FORMAT_2 = "yyyyMMddHHmmss";
	public static final String DISPLAY_DATE_FORMAT_3 = "dd-MMM-yyyy";
	public static final String DISPLAY_DATE_MONTH_FORMAT = "dd.Mon";

//	public static final String DISPLAY_TIME_FORMAT = "dd MMM yyyy HH:mm:ss";
//	public static final String TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
	public static final String HHMM_FORMAT = "HH:mm";
	
//	public static final String ENERGY_BID_PRICE_MIN_RECAL_1ST_CUTOF = "1";
//	public static final String FILE_TO_MSSL_BY_AQ = "Y";

//	public static final String FTP_PASSWORD = "XXXX";
//	public static final String FTP_SERVER_NAME = "XXXX";
//	public static final String FTP_USER_NAME = "XXXX";
	/*public static final String HALEY_ACCTSTMT_CONFIG = "acct_stmt.xml";
	public static final String HALEY_ACCTSTMT_CONFIG_BASE = "acct_stmt_base.xml";
	public static final String HALEY_BATCH_CMD = "sett_run.bat";
	public static final String HALEY_DATA_DIR = "\\EMC_Data\\";
	public static final String HALEY_RESERVE_CONFIG = "reserve.xml";
	public static final String HALEY_RESERVE_CONFIG_BASE = "reserve_base.xml";
	public static final String HALEY_RULE_BASE_DIR = "C:\\Software\\EMCDEV-SSR\\OPA\\batch processor\\rules\\2014-07-30";
	*/
//	public static String INCOMING_JMS_DIR = "InvalidMessages";
//	public static String JMS_HEADER_INBOUND_DESTINATION = "9300000016";
//	public static String JMS_HEADER_INBOUND_SENDER = "9300000024";
//	public static String JMS_TYPE_CMF = "Adjusted Usage Data";
//	public static String JMS_TYPE_DMF = "Usage Data";
//	public static String JMS_TYPE_FILE_ACK = "Transaction Acknowledgement";

//	public static String JMS_TYPE_FSC = "FSC Contract Data";
//	public static String JMS_TYPE_USAP = "Raw Price Document";
//	public static String JMS_TYPE_VESTING = "MC Contract Data";
//	public static String LOG_PREFIX = "[EMC]";
	
//	public static String MSSL_PKG_FROM_PREV_RUN = "N";
	//public static String OPA_LOG_DIR = "C:\\Software\\EMCDEV-SSR\\WebAppDeploy\\OPAlog\\";
//	public static String REPORT_COMPARATOR_DIR = "/EMC_Report";
//	public static String REPORT_COMPARISON = "Y";
//	public static String SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY = "N";
	
//	public static String SETT_SYSTEM_UPGRADE = "Y";
//	public static String SHARED_DRIVE = "/settdata";//"C:\\Software\\EMCDEV-SSR\\SettData";//
//	public static String ACCOUNT_BASE_DIR = "/Outgoing/AccountAccountingFile/";
//	public static String TEST_RERUN_INCLUDE = "N";
//	public static String UPLOADED_FILE_DIR = "/UploadedFiles/";
//	public static String USAP_BASE_DIR = "/Outgoing/USAP/";
//	public static String EMAIL_SENDER = "settlement.app@emcsg.com";
//	public static int BLC_CUTOFF_TIME = 20;
//	public static int BLC_CUTOFF_TIME_CP66 = 17;
//	public static int BLC_SUBMISSION_DEADLINE = -1;
//	public static int BLC_SUBMISSION_DEADLINE_CP66 = -10;
//	public static int  BLS_CUTOFF_TIME = 17;
//	public static int  BLS_CUTOFF_TIME_CP66 = 17;

//	public static int BLS_SUBMISSION_DEADLINE = 0;
//	public static int BLS_SUBMISSION_DEADLINE_CP66 = -10;
//	public static int ENE_MARGIN_CALL_THRESHOLD = 70;
//	public static int  ENE_NOTIFICATION_THRESHOLD = 60;
//	public static int GREENZONE_END_MINUTE1 = 8;
//	public static int GREENZONE_END_MINUTE2 = 38;
//	public static int  GREENZONE_START_MINUTE1 = 0;
//	public static int  GREENZONE_START_MINUTE2 = 30;
//	public static int  MAX_INCLUDE_RERUNS = 20;
	public static int  POLL_INTERVAL_IN_MINUTE = 15;	// for BPM processes only
//	public static double  MAX_CLAWBACK_FILE_SIZE = 4194304.0;
//	public static double  MAX_EBT_FILE_SIZE = 8388608;//7340032.0;
	//PROCESS NAME
	public static final String PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN = "BilateralContractUploadMain";
	public static final String PROCESS_NAME_BILATERAL_CONTRACT_VALIDATE_AND_UPLOAD_DATA = "BilateralContractValidateAndUploadData";
	public static final String PROCESS_NAME_CMFPROCESS_POLLER = "CMFProcessPoller";
	public static final String PROCESS_NAME_CMF_EMAIL_NOTIFICATION = "CMFEmailNotification";
	public static final String PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD = "EMCPSOBudgetUpload";
	public static final String PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN = "ForwardSalesContractUploadMain";
	public static final String PROCESS_NAME_JMS_LISTENER_MAIN = "JMSListenerMain";
	public static final String PROCESS_NAME_MM_FILE_UPLOAD = "MMFileUpload";
	public static final String PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE = "SaveClawbackQuantitiesFile";
	public static final String PROCESS_NAME_SAVE_MSSL_METERING_FILE = "SaveMSSLMeteringFile";
	public static final String PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA = "VerifyAndLoadClawbackQuantitiesData";
	public static final String PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA = "VerifyAndLoadMSSLMeteringData";
	public static final String PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN = "VestingContractUploadMain";

	//BILATERAL CONTRACT UPLOAD MAIN
	public static final int BILATERAL_CONTRACT_CSV_NUM_COLS = 9;
	public static final String BLC_JAVA_DATE_FORMAT = "dd-MMM-yyyy";

	// FORWARD SALES CONTRACT UPLOAD MAIN
	public static final int FSC_CONTRACT_CSV_NUM_COLS = 7;
}
