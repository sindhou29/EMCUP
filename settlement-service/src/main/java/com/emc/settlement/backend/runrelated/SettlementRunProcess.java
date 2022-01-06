/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.drcap.penalty.model.bc.am.common.PenaltyService;
import com.emc.drcap.penalty.model.bc.am.common.PenaltyService_Service;
import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.KieServerUtility;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.RestRequest;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * @author DWTN1561
 *
 */
@Service
public class SettlementRunProcess implements Serializable{

	/**
	 * 
	 */
	public SettlementRunProcess() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(SettlementRunProcess.class);
	
	String process_name = "RunProcess";

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private PavPackageImpl pavPackageImpl;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    private AlertNotificationImpl alertNotificationImpl;

    @Autowired
    private KieServerUtility kieServerUtility;
	
	
    String logPrefix ="[EMC]";
    String msgStep = "";
    
    @Transactional
    public Map<String, Object> startLoggingMessages(Map<String, Object> variableMap)
	{

		Date startTime = (Date)variableMap.get("startTime");
		SettlementRunParams settlementParam = (SettlementRunParams)variableMap.get("settlementParam"); 
		//,String nemsControllerEveId
		
		try {
			msgStep = "SettlementRunProcess.startLoggingMessages()";
			logger.log(Priority.INFO, logPrefix + " Starting Activity: " + msgStep + " ...");
			logPrefix = "[SR-" + settlementParam.runType + utilityFunctions.getddMMMyyyy(settlementParam.settlementDate)
					+ "] ";
			startTime = new Date();

			// BPM Log
			logger.log(Priority.INFO, logPrefix + "MainEveId: " + settlementParam.mainEveId+"  SchEveId : "+settlementParam.schEveId);

			logger.log(Priority.INFO, logPrefix + "[SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY="
					+ UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY")
					+ "]----- Settlement Run (Individual) Starts with BPM Release version RM253_CR196-13JULY16_1639-(ON_RM203_CR173) -----");

			logger.log(Priority.INFO,
					logPrefix + "[SETT_SYSTEM_UPGRADE=" + UtilityFunctions.getProperty("SETT_SYSTEM_UPGRADE") + "]");

			// Log JAM Message
			utilityFunctions.logJAMMessage(settlementParam.mainEveId, "I",
					"SettlementRunProcess.startLoggingMessages()",
					"Settlement Run (type: " + settlementParam.runType + " date: "
							+ utilityFunctions.getddMMMyyyy(settlementParam.settlementDate) + " Event ID: "
							+ settlementParam.mainEveId + ") about to start.",
					"");

			// Log BPM Release version in jam messages table
			// Log JAM Message
			utilityFunctions.logJAMMessage(settlementParam.mainEveId, "I",
					"SettlementRunProcess.startLoggingMessages()",
					"[SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY=" + UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY")
							+ "] BPM Release version used for the invoice Settlement Run: RM253_CR196-13JULY16_1639-(ON_RM203_CR173)",
					"");

			utilityFunctions.logJAMMessage(settlementParam.mainEveId, "I",
					"SettlementRunProcess.startLoggingMessages()",
					"[SETT_SYSTEM_UPGRADE=" + UtilityFunctions.getProperty("SETT_SYSTEM_UPGRADE") + "]", "");
		}
    	catch (Exception e) {
    	    logger.info(logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
    	    
    	    throw new SettlementRunException(e.getMessage(), msgStep);
    	}
		variableMap.put("startTime", startTime);
		logger.log(Priority.INFO,logPrefix + " Completed Activity: " + msgStep + " ...");
		return variableMap;
	}
    
    
    @Transactional
    public Map<String, Object> registerEventForRun(Map<String, Object> variableMap) 
	{

		SettlementRunParams settlementParam = (SettlementRunParams)variableMap.get("settlementParam"); 
		
    	// Creating Event for each settlement date       
    	try {
    	    msgStep =  "SettlementRunProcess.registerEventForRun()";

    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    //Changed for anonymous transaction
    	    //settlementParam.runEveId = utilityFunctions.getEveId();
    	    int rowcnt = 0;
    	    String eventType = "SDE";
    	   //Changed for anonymous transaction
    	   // String sqlCommand = "INSERT INTO NEM.JAM_EVENTS (id, eve_type, start_date, completed, esd_id) " + 
    	   // "VALUES ('" + settlementParam.runEveId + "','" + eventType + 
    	   // "',SYSDATE,'N','" + settlementParam.schEveId + "')";
    	   // jdbcTemplate.update(sqlCommand, new Object[] {});
    	    
    	    settlementParam.runEveId = utilityFunctions.createJAMEvent(eventType, "N", settlementParam.schEveId);

    	    logger.info(logPrefix + "Run Event Id: " + settlementParam.runEveId);

    	    String srInfo = "Settlement Run (Run Type: " + settlementParam.runType + " Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementParam.settlementDate);

    	    // Log Main Event JAM Message
    	    utilityFunctions.logJAMMessage(settlementParam.mainEveId, "I", 
    	                                   msgStep, srInfo + " Main Event ID: " + settlementParam.mainEveId + ") starting.", 
    	                                   "");

    	    // Log Run Event JAM Message
    	    utilityFunctions.logJAMMessage( settlementParam.runEveId,  "I", 
    	                                    msgStep,  srInfo + " Run Event ID: " + settlementParam.runEveId + ") starting.", 
    	                                    "");

    	    // BPM Log for current BPM Release version
    	    logger.info(logPrefix + "[SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY=" + UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY") + "]  BPM Release version used for the Settlement Run: RM499CP66-ON_RM433RiskExp-ON_RM510NetAFP-ON_PRD_RM412_CR393 " + ", Run Event Id: " + settlementParam.runEveId);

    	    logger.info(logPrefix + "[SETT_SYSTEM_UPGRADE=" + UtilityFunctions.getProperty("SETT_SYSTEM_UPGRADE") + "]" + ", Schedule Event Id: " + settlementParam.schEveId);

    	    // Log current BPM Release version in JAM Message
    	    utilityFunctions.logJAMMessage( settlementParam.runEveId,  "I", 
    	                                    msgStep,  "[SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY=" + UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY") + "]  BPM Release version used for the Settlement Run: RM499CP66-ON_RM433RiskExp-ON_RM510NetAFP-ON_PRD_RM412_CR393 ", 
    	                                    "");

    	    utilityFunctions.logJAMMessage( settlementParam.runEveId,  "I", 
    	                                    msgStep, "[SETT_SYSTEM_UPGRADE=" + UtilityFunctions.getProperty("SETT_SYSTEM_UPGRADE") + "]", 
    	                                    "");
    	}
    	catch (Exception e) {
    	    // BPM Log
    		logger.error(logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());

    	    // Log JAM Message
    	    utilityFunctions.logJAMMessage( settlementParam.runEveId,  "E", 
    	                                    msgStep,  e.getMessage(),  "");

    	    throw new SettlementRunException(e.getMessage(), msgStep);
    	}

		variableMap.put("settlementParam", settlementParam);
		logger.log(Priority.INFO,logPrefix + " Completed Activity: " + msgStep + " ...");
		return variableMap;
	}
    
    @Transactional
    public Map<String, Object> preRequisiteComplete(Map<String, Object> variableMap)
	{
    	Boolean invokedBy = (Boolean)variableMap.get("invokedBy");
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");  
		try{
    	    msgStep = "SettlementMainProcess.preRequisiteComplete()";

    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    int updcnt;
    	    String preRequisiteStatus = "";
    	    String sqlCommand;
    	    /*Added for DRCAP Phase2-- JIRA DRSAT-255 FOR ZERO FSC AND RSC*/
    	    
    	    sqlCommand = " update nem.nem_runs_pre_requisites_status  "+
    					" set SUCCESS_YN = 'N', LOCK_VERSION = '99', expiry_date = sysdate "+
    					" where trading_date = ? and run_type = ? and lock_version = 1 and SUCCESS_YN = 'Y'   "+
    					" and not exists (   "+
    					" select 1 from nem.nem_settlement_quantities qty "+
    					" where qty.version = (select pkg.version from NEM.pav_packages pkg, NEM.pav_package_types pkt "+
    					" where pkt.id = pkg.pkt_id and pkt.name = 'SETTLEMENT_MC_QUANTITIES' and ready = 'Y' "+
    					" and ? between effective_date and end_date   "+
    					" and version = ( select max(to_number(version)) from NEM.pav_packages pkg, NEM.pav_package_types pkt "+
    					" where pkt.id = pkg.pkt_id and pkt.name = 'SETTLEMENT_MC_QUANTITIES'  "+
    					" and ready = 'Y' and ? between effective_date and end_date ))  "+
    					" and qty.settlement_date = ? and qty.period = 1  "+
    					") ";
			Object[] params = new Object[5];
			params[0] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			params[1] =  settlementParam.getRunType();
			params[2] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			params[3] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			params[4] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			int update_flag = jdbcTemplate.update(sqlCommand, params);
    	    /*End for DRCAP Phase2-- JIRA DRSAT-255 FOR ZERO FSC AND RSC*/
    	    
    	    sqlCommand = "SELECT SUCCESS_YN FROM NEM.NEM_RUNS_PRE_REQUISITES_STATUS " + 
    	                 "where trunc(trading_date) = trunc(?) " + 
    	                 "and lock_version = 1 " + 
    	                 "AND success_yn = 'Y' " + 
    	                 "and version = ( " + 
    	                 "SELECT MAX (TO_NUMBER (version)) " + 
    	                 "FROM NEM.NEM_RUNS_PRE_REQUISITES_STATUS " + 
    	                 "WHERE trunc(trading_date) = trunc(?) " + 
    	                 " )";

			Object[] params1 = new Object[2];
			params1[0] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			params1[1] =  utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate());
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params1);
			for (Map row : list) {
				preRequisiteStatus = (String) row.get("SUCCESS_YN");
			}

			int quantityData = checkQuantityData(settlementParam);
			logger.info("preRequisiteStatus : "+preRequisiteStatus+ " quantityData : "+quantityData);
			
    	    if (preRequisiteStatus.equalsIgnoreCase("Y") && quantityData == 1) {
    	        invokedBy = true;
    	    }
    	    else {
    	        invokedBy = false;
    	    }
    	}
    	catch (Exception e) {
    	    logger.info(logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
    	    
    	    throw new SettlementRunException(e.getMessage(), msgStep);
    	    
    	}
		logger.info("Completed Activity "+msgStep+" - invokedBy : "+invokedBy);
		variableMap.put("invokedBy", invokedBy);
		return variableMap;
	}
    
    
    public int checkQuantityData(SettlementRunParams settlementParam)
    {
    	String mcQuantityVersion = "";
    	String msg = "";
    	int quantityData = 0;
        String tradeDateMCR = new SimpleDateFormat("dd-MMM-yyyy").format(settlementParam.getSettlementDate());
        String tradeDateMCRVersion = new SimpleDateFormat("dd-MM-yyyy").format(settlementParam.getSettlementDate());
        
        String sqlCommand =
                "select pkg.version from NEM.pav_packages pkg, NEM.pav_package_types pkt " +
                "where pkt.id = pkg.pkt_id and pkt.name = 'SETTLEMENT_MC_QUANTITIES' and ready = 'Y' " +
                "and ? between effective_date and end_date " +
                "and version = ( select max(to_number(version)) from NEM.pav_packages pkg, NEM.pav_package_types pkt " +
                "where pkt.id = pkg.pkt_id and pkt.name = 'SETTLEMENT_MC_QUANTITIES' " +
                "and ready = 'Y' and ? between effective_date and end_date )";
        
        Object[] params = new Object[2];
		params[0] =  tradeDateMCR;
		params[1] =  tradeDateMCR;
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
		for (Map row : list) {
			mcQuantityVersion = (String) row.get("version");
            break;
		}
		logger.info(logPrefix + "Current version for 'SETTLEMENT_MC_QUANTITIES' : " +mcQuantityVersion);
        if (mcQuantityVersion == null) {
            msg ="MCE Data will be re-copied - data was deleted to save space.";
        } else {

            

            sqlCommand =
                    "select 1 from nem.nem_settlement_quantities qty " +
                    "where qty.version = ? " +
                    "and to_char(qty.settlement_date,'dd-mm-yyyy') = ? and qty.period = 1";


            logger.info("mcQuantityVersion: " + mcQuantityVersion);
            logger.info("tradeDateMCR: " + tradeDateMCR);

            Object[] params1 = new Object[2];
    		params1[0] =  mcQuantityVersion;
    		params1[1] =  tradeDateMCRVersion;
    		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params1);
    		for (Map row : list1) {
                quantityData = ((BigDecimal) row.get("1")).intValue();
                break;
    		}
        }
        return quantityData;
    }
    
    
    @Transactional
    public Map<String, Object> prepareRunPackages(Map<String, Object> variableMap) 
	{

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");
    	SettRunPkg settRunPackage= (SettRunPkg)variableMap.get("settRunPackage");
		Boolean mcrChange = (Boolean)variableMap.get("mcrChange");
		Boolean invokedBy = (Boolean)variableMap.get("invokedBy");
		String invokedByModule = (String)variableMap.get("invokedByModule");
		Boolean clwqExists = (Boolean)variableMap.get("clwqExists");
		
		// Purpose : Creates a new settlement run data package
		try{
			msgStep = "SettlementRunProcess.prepareRunPackages()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " mcrChange : "+mcrChange+ " clwqExists : "+clwqExists+" invokedBy : "+invokedBy+" invokedByModule : "+invokedByModule+" mceDataLoadByController : "+settlementParam.isMceDataLoadByController() );

			int pkgVersion;
			int upd_cnt;
			int rowcnt;
			String pkgTypeId;
			String pkgType;
			String pkgId = null;
			String version = null;
			settRunPackage.settlementDate = settlementParam.settlementDate;
			String sqlCommand;
			Map<String, String> pkgMap = new HashMap<String, String>();
			
			if(mcrChange == null) mcrChange = false;

			// Create Package Version for 'SETTLEMENT_RUN'
			pkgMap = pavPackageImpl.createNextPkgVersion("SETTLEMENT_RUN", settlementParam.settlementDate);

			settRunPackage.settRunPkgId = pkgMap.get("pkgId");
			settRunPackage.settRunPkgVer = pkgMap.get("nextVersion");

			// Create Package Version for 'SETTLEMENT_INPUTS'
			pkgMap = null;
			pkgMap = pavPackageImpl.createNextPkgVersion("SETTLEMENT_INPUTS", settlementParam.settlementDate);

			settRunPackage.settInputPkgId = pkgMap.get("pkgId");
			settRunPackage.settInputPkgVer = pkgMap.get("nextVersion");

			// Only create a new version if we need to copy data,
			// otherwise we will reuse an old version.
			// DRCAP Changes added to include invokedBy Parameter //
			if (settlementParam.isMceDataLoadByController() == false && (mcrChange == true || clwqExists  == true)) {
				// Creating packages for MCE Price and Quantity
				logger.log(Priority.INFO,
						logPrefix + "Creating package for SETTLEMENT_MC_QUANTITIES and SETTLEMENT_MC_PRICES ...");

				pavPackageImpl.createMCPackages(settRunPackage);
			} else {
				// Get current packages for MCE Price and Quantity
				pavPackageImpl.getCurrentMCPackages(settRunPackage);

				logger.log(Priority.INFO,
						logPrefix + "Current version for 'SETTLEMENT_MC_QUANTITIES' : " + settRunPackage.mcQtyPkgVer);

				logger.log(Priority.INFO,
						logPrefix + "Current version for 'SETTLEMENT_MC_PRICES' : " + settRunPackage.mcPricePkgVer);
			}

			// DRCAP Changes END to include invokedBy Parameter //
			// get Current MSSL Quantity Version
			settRunPackage.msslQtyPkgVer = pavPackageImpl.getCurrentMSSLPackage(settlementParam.settlementDate);
			settRunPackage.msslQtyPkgId = "";

			if (settRunPackage.msslQtyPkgVer == null) {
				logger.log(Priority.INFO, logPrefix + "MSSL Quantity Version is empty, Settlement Run cannot proceed.");

				throw new SettlementRunException("MSSL Quantity Version is empty, Settlement Run cannot proceed.", msgStep);
			}

			logger.log(Priority.INFO,
					logPrefix + "Current version of 'SETTLEMENT_MSSL_QUANTITIES': " + settRunPackage.msslQtyPkgVer);

			// For Testing, because there may be new MSSL version in database, but to
			// compare with
			// Production result, we should use the same MSSL version used in previous Run.
			if (UtilityFunctions.getProperty("MSSL_PKG_FROM_PREV_RUN").equalsIgnoreCase("Y")) {
				logger.log(Priority.INFO, "[EMC] For testing, use previous MSSL package version.");

				sqlCommand = "SELECT MSSL_QTY_VERSION FROM NEM.NEM_SETTLEMENT_RUN_STATUS_V "
						+ "WHERE trunc(SETTLEMENT_DATE) = trunc(?) AND RUN_TYPE = ? ORDER BY SEQ";

				Object[] params = new Object[2];
				params[0] =  utilityFunctions.convertUDateToSDate(settlementParam.settlementDate);
				params[1] =  settlementParam.runType;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list) {
					settRunPackage.msslQtyPkgVer = (String) row.get("MSSL_QTY_VERSION");
					settRunPackage.msslQtyPkgId = "";
					logger.log(Priority.INFO, logPrefix + "Current version for 'SETTLEMENT_MSSL_QUANTITIES' : "
							+ settRunPackage.msslQtyPkgVer);
					break;
				}
			}

			// Create Package Aggregations
			pavPackageImpl.createPkgAggregations(settRunPackage);

			// Get Standing Version
			settRunPackage.standingVersion = pavPackageImpl.getStandingVersion(settlementParam.settlementDate);

			logger.log(Priority.INFO, logPrefix + "Packages and aggregations for SR prepared.");
		} catch (Exception e) {
			// BPM Log
			// String invokedByModule="";
			if (invokedBy) {
				invokedByModule = " NEMSCONTROLLER: ";
			}

			logger.log(Priority.INFO, logPrefix + invokedByModule + "<" + msgStep + "> Exception: " + e.getMessage());
			
			// Log JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E", msgStep, e.getMessage() + invokedByModule, "");

			throw new SettlementRunException(e.getMessage(), msgStep);
			
		}

		variableMap.put("invokedByModule", invokedByModule);
		variableMap.put("settRunPackage", settRunPackage);
		logger.log(Priority.INFO,logPrefix + " Completed Activity: " + msgStep + " ...");
		return variableMap;
	}
    
    @Transactional
    public Map<String, Object> createRunEntry(Map<String, Object> variableMap) 
	{

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");
    	SettRunPkg settRunPackage= (SettRunPkg)variableMap.get("settRunPackage");
		String nemsControllerEveId = (String)variableMap.get("nemsControllerEveId");
		String mcrString = (String)variableMap.get("mcrString");
		String settRunId = (String)variableMap.get("settRunId");
		
		
		try{
		    msgStep =  "SettlementRunProcess.createRunEntry()";

		    logger.log(Priority.INFO,logPrefix + " Starting Activity: " + msgStep + " ...");

		    String msg;
		    String runId = utilityFunctions.getEveId();
		    String runPkgId = settRunPackage.settRunPkgId;
		    String runEventId = settlementParam.runEveId;
		    String runDate = utilityFunctions.getTimeFormat(settlementParam.runDate);
		    String runType = settlementParam.runType;
		    String comment = settlementParam.comment;
		    String ComplianceResult; // Added for DRCAP Phase2
		    String ComplianceRunType; // Added for DRCAP Phase2

		    logger.log(Priority.INFO,logPrefix + "Nems Controller Id in CreateRunEntry: " + nemsControllerEveId+"  settRunId : "+settRunId);

		    // Added by DRCAP Phase2
		    String nemsControllerId = nemsControllerEveId;
		    String mcrId = mcrString;

		    // End by DRCAP Phase2
		    String sqlCommand;
		    int seq = 1;
		    sqlCommand = " SELECT (MAX(seq) + 1) MAXSEQ FROM NEM.NEM_SETTLEMENT_RUNS " + 
		                 "WHERE trunc(settlement_date) = trunc(?) ";
			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementParam.settlementDate));
			for (Map row : list) {
				seq = row.get("MAXSEQ") == null ? 0 : ((BigDecimal)row.get("MAXSEQ")).intValue();
				break;
			}
		    if (seq == 0) {
		        seq = 1;
		    }

		    // Check latest Settlement Run Status
		    String runStatus = utilityFunctions.getSettRunStatus(settlementParam.settlementDate, settlementParam.runType);

			if(settlementParam.getComment() == null || !settlementParam.getComment().trim().equalsIgnoreCase("Test Rule")) {
				if (runStatus != null && runStatus.equalsIgnoreCase("P")) {
					msg = "There is In Progress Run exists for Settlement Date: " +
							utilityFunctions.getddMMMyyyy(settlementParam.settlementDate) +
							" and Run Type: " + settlementParam.runType;

					logger.log(Priority.WARN, logPrefix + msg);

					throw new SettlementRunException(msg, msgStep);
				}
				else if (runStatus != null && runStatus.equalsIgnoreCase("F")) {
					msg = "There is Finished Run exists for Settlement Date: " +
							utilityFunctions.getddMMMyyyy(settlementParam.settlementDate) +
							" and Run Type: " + settlementParam.runType;

					logger.log(Priority.WARN, logPrefix + msg);

					throw new SettlementRunException(msg, msgStep);
				}
			}

		    sqlCommand = "INSERT INTO NEM.NEM_SETTLEMENT_RUNS " + 
		                 "(id, seq, settlement_date, run_date, run_type, pkg_id, eve_id, comments,nems_ctrl_eve_id,mcr_id) " + 
		                 " VALUES (?,?,?,?,?,?,?,?,?,?)";

			Object[] params1 = new Object[10];
			params1[0] =  runId;
			params1[1] =  seq;
			params1[2] =  utilityFunctions.convertUDateToSDate(settlementParam.settlementDate);
			params1[3] =  utilityFunctions.convertUDateToSDate(settlementParam.runDate);
			params1[4] =  runType;
			params1[5] =  runPkgId;
			params1[6] =  runEventId;
			params1[7] =  comment;
			params1[8] =  nemsControllerId;
			params1[9] =  mcrId;
			jdbcTemplate.update(sqlCommand, params1);
		    logger.log(Priority.INFO,logPrefix + "Settlement Run Package Id = " + runPkgId);

		    logger.log(Priority.INFO,logPrefix + "Settlement Run Id = " + runId);

		    settlementParam.runId = runId;
		    settlementParam.runPkgId = runPkgId;
		    settRunId = runId;
		    
		    settlementParam.runEveId=runEventId;
		    
		    // To facilitate for DB Polling start
		    sqlCommand = "INSERT INTO NEM.NEM_CONT_SYSTEM_RUN_STATUS " + 
		                 "(id, PKG_VERSION, TRADING_DATE, RUN_TYPE, RUN_ID, STATUS, START_TIME, END_TIME,REMARKS,EVE_ID) " + 
		                 " VALUES (?,?,?,?,?,?,?,?,?,?)";

			Object[] params2 = new Object[10];
			params2[0] =  utilityFunctions.getEveId();
			params2[1] =  "000";
			params2[2] =  utilityFunctions.convertUDateToSDate(settlementParam.settlementDate);
			params2[3] =  runType;
			params2[4] =  runId;
			params2[5] =  "PROCESSING";
			params2[6] =  utilityFunctions.convertUDateToSDate(new Date());
			params2[7] =  utilityFunctions.convertUDateToSDate(new Date());
			params2[8] =  comment;
			params2[9] =  runEventId;
			jdbcTemplate.update(sqlCommand, params2);
		        // To facilitate for DB Polling end
		 String reCalculateDRCompliance;  // Added for DRCAP Phase2 Compliance
		    
		 if(settlementParam.runFrom.equalsIgnoreCase("F") && (settlementParam.runType.equalsIgnoreCase("P") || settlementParam.runType.equalsIgnoreCase("F") ) )
		 
		 {
			CallableStatement cstmt;
			try {
				/*cstmt = conn.prepareCall("{call NEM$DR_CALCULATIONS.CALL_RELOAD_MCE_DIF_DR_RESULTS(?,?,?,?)}");
				cstmt.setDate(1, utilityFunctions.convertUDateToSDate(settlementParam.settlementDate));
				cstmt.setString(2, settRunPackage.standingVersion);
				cstmt.setString(3, settlementParam.mainEveId);
				cstmt.registerOutParameter(4, Types.VARCHAR);
				cstmt.executeUpdate();
				reCalculateDRCompliance = cstmt.getString(4);*/
				
				SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
						.withSchemaName("NEM")
						.withCatalogName("NEM$DR_CALCULATIONS")
						.withProcedureName("CALL_RELOAD_MCE_DIF_DR_RESULTS");
				Map<String, Object> inMap = new HashMap<String, Object>();
				inMap.put("pin_TRADING_DATE", utilityFunctions.convertUDateToSDate(settlementParam.settlementDate));
				inMap.put("pin_STANDING_VERSION", settRunPackage.standingVersion);
				inMap.put("pin_EVE_ID", settlementParam.mainEveId);
				Map<String, Object> retmap = jdbcCall.execute(inMap);
				reCalculateDRCompliance  = (String)retmap.get("pout_DR_INPUT_COMPL_RECALC_YN");
				
				
				
				logger.log(Priority.INFO, "[EMC] Calling Procedure: NEMDR_CALCULATIONSCALL_RELOAD_MCE_DIF_DR_RESULTS " + reCalculateDRCompliance);
			} catch (Exception e) {
				logger.info("Exception "+e.getMessage());
				throw new SettlementRunException(e.getMessage(), msgStep);
			}
			
		  if(settlementParam.runType.equalsIgnoreCase("P"))
		  {
		  	ComplianceRunType = "P_RUN_NORM";
		  }else
		  {
		  	ComplianceRunType = "F_RUN_NORM";
		  }

		  // PenaltyService.execComplianceCheck(tradingDate : settlementParam.settlementDate, runDate : settlementParam.runDate, runId : settlementParam.runId, 
		  //                                 runType : ComplianceRunType, out result : result);
			try {
				
				Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		 	 	String penrptservicesURL = propertiesMap.get("penservicesURL");
		    	URL url = new URL(penrptservicesURL+"/penservices/PenaltyService?wsdl");
		    	logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> URL: " + url);
		    	logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> SettlementDate: " + utilityFunctions.toXMLGregorianCalendar(settlementParam.settlementDate));
		    	logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> RunDate: " +  utilityFunctions.toXMLGregorianCalendar(settlementParam.runDate));
		    	logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> RunId: " + settlementParam.runId);
		    	logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> RunType: " + ComplianceRunType);
		 	    			 	    	
				PenaltyService_Service service = new PenaltyService_Service(url);
				PenaltyService penaltyServiceSoapHttpPort = service.getPenaltyServiceSoapHttpPort();
				penaltyServiceSoapHttpPort.execComplianceCheck(utilityFunctions.toXMLGregorianCalendar(settlementParam.settlementDate), utilityFunctions.toXMLGregorianCalendar(settlementParam.runDate), settlementParam.runId, ComplianceRunType);
			} catch (Exception e) {
				logger.log(Priority.WARN,logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
				
			}
		 }   
		    
		    
		}
		catch (Exception e) {
		    // BPM Log
		    logger.log(Priority.FATAL,logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
		   
		    // Log JAM Message
		    utilityFunctions.logJAMMessage(settlementParam.runEveId, "E", msgStep,e.getMessage(), "");

		    throw new SettlementRunException(e.getMessage(), msgStep);
		    
		}
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("settRunId", settRunId);
		logger.log(Priority.INFO,logPrefix + " Completed Activity: " + msgStep + " ...");
		return variableMap;
	}
    
    

    @Transactional
    public Map<String, Object> invokeRules(Map<String, Object> variableMap)
	{

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");
    	SettRunPkg settRunPackage= (SettRunPkg)variableMap.get("settRunPackage");
    	AlertNotification alertNotification = (AlertNotification)variableMap.get("alertNotification");
		
    	Map<String, Object> retMap = new HashMap<String, Object>();
    	Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		try{
			msgStep = "SettlementRunProcess.invokeRules()";
			logger.log(Priority.INFO, logPrefix + " Starting Activity: " + msgStep + " ...");

			RestRequest payload = new RestRequest();
			String processUrl = null;

			settRunPackage.setSqlSettlementDate(utilityFunctions.convertUDateToSDate(
					settRunPackage.getSettlementDate() == null ? new Date() : settRunPackage.getSettlementDate()));
			AbstractMap.SimpleEntry<String, Object> runPackageParam = new AbstractMap.SimpleEntry<>(
					"com.emc.settlement.model.backend.pojo.SettRunPkg", settRunPackage);

			payload.setRunPackage(runPackageParam);

			settlementParam.setSqlRunDate(utilityFunctions.convertUDateToSDate(
					settlementParam.getRunDate() == null ? new Date() : settlementParam.getRunDate()));
			
			settlementParam.setCsvStorage(propertiesMap.get("csvStorage"));
			//settlementParam.setCsvStorage("/app/test/settlement/rule");

			AbstractMap.SimpleEntry<String, Object> runParamsParam = new AbstractMap.SimpleEntry<>(
					"com.emc.settlement.model.backend.pojo.SettlementRunParams", settlementParam);
			payload.setRunParams(runParamsParam);

			// AlertNotification alertNotification = new AlertNotification();

			// populating to send the email from rules
			alertNotification.setNotfId(utilityFunctions.getEveId());
			alertNotification.setRequestedQueueName("omsfo.aq_notification_queue");
			alertNotification.setCc(propertiesMap.get("EMCPSO_UPLOAD_FAIL_EMAIL"));//BusinessParameters.EMCPSO_UPLOAD_FAIL_EMAIL
			alertNotification.setSender(UtilityFunctions.getProperty("EMAIL_SENDER"));
			alertNotification.setImportance(null);
			alertNotification.setMsgType("ExternalEmailNotification");
			alertNotification.setMsgChannel("Email");
			alertNotification.setMsgSecurity("NONSSL");
			alertNotification.setNotificationReady(false);
			alertNotification.setHostNemsDBDetails(utilityFunctions.getHostNEMSDBDetails());
			alertNotification.setEnQueue("NEMAQ_SERVICESAQ_ENQUEUE_MESSAGE");

			AbstractMap.SimpleEntry<String, Object> alertNotifParam = new AbstractMap.SimpleEntry<>(
					"com.emc.settlement.model.backend.pojo.AlertNotification", alertNotification);
			payload.setAlert(alertNotifParam);

			if(settlementParam.getComment() != null && settlementParam.getComment().trim().equalsIgnoreCase("Test Rule"))
			{
				logger.log(Priority.INFO,  " Testing  Mode :  true");
				settlementParam.setTestingMode(true);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			logger.info("PayLoad : " + mapper.writeValueAsString(payload));

			processUrl = utilityFunctions.getProcessUrl("MarketRulesProcess", settlementParam.getSettlementDate());
			
			
			logger.info("request : "+processUrl);
			
			

			
			
			logger.info("McPricePkgId - "+settRunPackage.getMcPricePkgId());
			logger.info("McPricePkgVer - "+settRunPackage.getMcPricePkgVer());
			logger.info("McQtyPkgId - "+settRunPackage.getMcQtyPkgId());
			logger.info("McQtyPkgVer - "+settRunPackage.getMcQtyPkgVer());
			logger.info("MsslQtyPkgId - "+settRunPackage.getMsslQtyPkgId());
			logger.info("MsslQtyPkgVer - "+settRunPackage.getMsslQtyPkgVer());
			logger.info("SettInputPkgId - "+settRunPackage.getSettInputPkgId());
			logger.info("SettInputPkgVer - "+settRunPackage.getSettInputPkgVer());
			logger.info("SettRunPkgId - "+settRunPackage.getSettRunPkgId());
			logger.info("SettRunPkgVer - "+settRunPackage.getSettRunPkgVer());
			logger.info("StandingVersion - "+settRunPackage.getStandingVersion());
			logger.info("SettlementDate - "+utilityFunctions.getddMMyyyy(settRunPackage.getSettlementDate()));
			logger.info("SqlSettlementDate - "+settRunPackage.getSqlSettlementDate());
			logger.info(" - ");
			logger.info(" - ");
			logger.info("Comment - "+settlementParam.getComment());
			logger.info("CsvStorage - "+settlementParam.getCsvStorage());
			logger.info("MainEveId - "+settlementParam.getMainEveId());
			logger.info("RegressionData - "+settlementParam.getRegressionData());
			logger.info("RunEveId - "+settlementParam.getRunEveId());
			logger.info("RunFrom - "+settlementParam.getRunFrom());
			logger.info("RunId - "+settlementParam.getRunId());
			logger.info("RunPkgId - "+settlementParam.getRunPkgId());
			logger.info("RunType - "+settlementParam.getRunType());
			logger.info("RunUser - "+settlementParam.getRunUser());
			logger.info("SchEveId - "+settlementParam.getSchEveId());
			logger.info("FromSettlementDate - "+utilityFunctions.getddMMyyyy(settlementParam.getFromSettlementDate()));
			logger.info("RunDate - "+utilityFunctions.getddMMyyyy(settlementParam.getRunDate()));
			logger.info("SettlementDate - "+settlementParam.getSettlementDate());
			logger.info("SqlFromSettlementDate - "+settlementParam.getSqlFromSettlementDate());
			logger.info("SqlRunDate - "+settlementParam.getSqlRunDate());
			logger.info("SqlSettlementDate - "+settlementParam.getSqlSettlementDate());
			logger.info("SqlToSettlementDate - "+settlementParam.getSqlToSettlementDate());
			logger.info("ToSettlementDate - "+utilityFunctions.getddMMyyyy(settlementParam.getToSettlementDate()));

			if(settRunPackage.getMcPricePkgId() != null && settRunPackage.getMcPricePkgVer() != null  && settRunPackage.getMcQtyPkgId() != null  && settRunPackage.getMcQtyPkgVer() != null  && 
			settRunPackage.getMsslQtyPkgVer()  != null && settRunPackage.getSettInputPkgId() != null  && settRunPackage.getSettInputPkgVer()  != null &&
			settRunPackage.getSettRunPkgId()  != null && settRunPackage.getSettRunPkgVer()  != null && settRunPackage.getStandingVersion()  != null && settRunPackage.getSettlementDate()  != null &&
			settRunPackage.getSqlSettlementDate()  != null && settlementParam.getComment()  != null && settlementParam.getCsvStorage()  != null && settlementParam.getMainEveId()  != null &&
			settlementParam.getRegressionData()  != null && settlementParam.getRunEveId()  != null && settlementParam.getRunFrom()  != null && settlementParam.getRunId()  != null &&
			settlementParam.getRunPkgId()  != null && settlementParam.getRunType()  != null && settlementParam.getSchEveId()  != null &&
			settlementParam.getFromSettlementDate()  != null && settlementParam.getRunDate()  != null && settlementParam.getSettlementDate()  != null && settlementParam.getSqlFromSettlementDate()  != null &&
			settlementParam.getSqlRunDate()  != null && settlementParam.getSqlSettlementDate()  != null && settlementParam.getSqlToSettlementDate()  != null && settlementParam.getToSettlementDate()  != null )
			{
				Integer instanceId = kieServerUtility.startProcessInstance(processUrl , payload);
				logger.info("instanceId " + instanceId);
				if(instanceId == null)
				{
					//this.updateFailedStatus(settlementParam);
				    // Log JAM Message
				    utilityFunctions.logJAMMessage(settlementParam.getMainEveId(),  "E", "Settlement Run Process", 
				                                    "Exception while processing Settlement Rules. Run ID - "+settlementParam.getRunId(), 
				                                    "");
					throw new SettlementRunException("Exception while processing Settlement Rules", msgStep);
				}
				logger.log(Priority.INFO, logPrefix + " Completed Activity: " + msgStep + " ...");
				
			}else {
				logger.log(Priority.INFO, logPrefix + " Cannot invoke Rules with null values." + msgStep + " ...");
				utilityFunctions.logJAMMessage(settlementParam.getMainEveId(),  "E", "Settlement Run Process", 
                        "Cannot invoke Rules with null values.. Run ID - "+settlementParam.getRunId(), "");
			}
		} catch (Exception e) {
			
			logger.log(Priority.ERROR, logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
			//this.updateFailedStatus(settlementParam);
			utilityFunctions.logJAMMessage(settlementParam.getMainEveId(),  "E", "Settlement Run Process", 
                    "Exception while processing Settlement Rules. Run ID - "+settlementParam.getRunId(), "");
			throw new SettlementRunException(e.getMessage(), msgStep);
			
		}
    	return variableMap;
	}
    
    @Transactional
    public void updateTestRunStatus(Map<String, Object> variableMap) {

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");
    	Boolean isTestRun =  (Boolean)variableMap.get("isTestRun");
    	
    	msgStep = "SettlementRunProcess.updateTestRunStatus()";
    	logger.info("Input parameters "+msgStep+" - isTestRun :" + isTestRun + " runEveId :" + settlementParam.runEveId);

		try {
			if (settlementParam.runEveId != null && isTestRun) {
				logger.info(logPrefix
						+ "Start entering data for Settlement Test Run for excluding test runs from Risk Exposure related calculations for EVE ID: "
						+ settlementParam.runEveId);
				String insertTestRunDtl = "INSERT INTO NEM.NEM_TEST_SETTLEMENT_RUNS_DTL "
						+ "SELECT SYS_GUID (), SETTLEMENT_DATE, RUN_DATE, EVE_ID, ID, RUN_TYPE FROM NEM.NEM_SETTLEMENT_RUNS "
						+ "WHERE EVE_ID =?";

				jdbcTemplate.update(insertTestRunDtl, new Object[] { settlementParam.runEveId });

				// logMessage(logPrefix + "Test Run Data Entered Successfully.");
				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep,
						" Test Run Data Entered Successfully.", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Priority.INFO, "Exception: " + e.getMessage(), e);
		}
		
	}
    
    
    @Transactional
    public void exceptionNotification(Map<String, Object> variableMap) {
    	
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam");
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception");
    	Boolean invokedBy =  (Boolean)variableMap.get("invokedBy");
		
		
    	logger.info("[EMC] SettlementRunProcess.exceptionNotification() ...");
    	
    	try {
			if(settlementParam != null && settlementParam.runEveId != null)
			{
			    // BPM Log
			    logger.log(Priority.ERROR, logPrefix + "Exception: Abnormal Termination of Settlement Run... " + exception.toString());
			    
			    String invokedByModule="";
			    
			    if(invokedBy)
			    {
			    	invokedByModule="NEMSCONTROLLER: ";
			    }

			    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			    // Send Alert Email
			    AlertNotification alert = new AlertNotification();
			    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
			    alert.subject = invokedByModule + "Settlement Run failed (SettRevamp)";
			    alert.content = invokedByModule +"Settlement Run failed.\nCurrent Time: " + utilityFunctions.getddMMMyyyyhhmmss(new Date()) + "\n" + 
			    "Run Date: " + utilityFunctions.getddMMMyyyyhhmmss(settlementParam.runDate)  + "\n" + 
			    "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementParam.settlementDate) + "\n" + 
			    "Type of Run: " + settlementParam.runType + "\n" + 
			    "Settlement Run ID: " + settlementParam.runId + "\n" + 
			    "Comments: " + settlementParam.comment + "\n" + 
			    "Error Msg: " + exception.message;
			    alert.noticeType = "Settlements Run";
			    alertNotificationImpl.sendEmail(alert);

			    // Log JAM Message
			    utilityFunctions.logJAMMessage(settlementParam.runEveId,  "E",  process_name,  "Abnormal Termination of Settlement Run... " + exception.message, 
			                                   "");
			}
		} catch (Exception e) {
			logger.error("Exception : "+e.getMessage());
		}
		
		
		
	}
    
    
    
}
