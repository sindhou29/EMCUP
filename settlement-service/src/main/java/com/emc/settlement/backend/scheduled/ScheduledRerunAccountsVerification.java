/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.AccountValidationException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author DWTN1561
 *
 */
@Service
public class ScheduledRerunAccountsVerification {

	/**
	 * 
	 */
	public ScheduledRerunAccountsVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledRerunAccountsVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private PavPackageImpl pavPackageImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-SACV] ";
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = "ScheduledRerunAccountsVerification.checkBusinessDay()";
		Boolean businessDay = false;

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");
		
		Calendar nextDay = Calendar.getInstance();
		nextDay.add(Calendar.DATE, 1);
		
		logger.info(logPrefix + "Today is: " + utilityFunctions.getddMMMyyyy(new Date()));

		try {
			businessDay = utilityFunctions.isBusinessDay(nextDay.getTime());
		} catch (Exception e) {
			logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
		}
		variableMap.put("businessDay", businessDay);
		logger.info("Returning from service " + msgStep + " - (nextDay - businessDay :" + businessDay + ")");
		return variableMap;

	}
	
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {
		String msgStep = "ScheduledRerunAccountsVerification.initializeVariables()";
		String soapServiceUrl;
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementParam : " + settlementParam + ")");
		try {
			logger.info("Starting Activity " + msgStep);
			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			soapServiceUrl = propertiesMap.get("soapServiceUrl");

			settlementParam.setRunType("P");

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, 1);
			settlementParam.setRunDate(cal.getTime());
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new Exception("Abnormal Termination of Scheduled Rerun Accounts Verification: (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		return variableMap;

	}
 
    @Transactional
	public Map<String, Object> prepareData(Map<String, Object> variableMap)
    {

		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		String eveId = (String) variableMap.get("eveId");
		Integer pollInterval = (Integer) variableMap.get("pollInterval");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		String standingVersion = (String) variableMap.get("standingVersion");

		String msgStep = "ScheduledRerunAccountsVerification.prepareData()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId
				+ " pollInterval : " + pollInterval
				+ " standingVersion : " + standingVersion
				+ " settlementDate : " + settlementDate + ")");
    	try {
    		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);
    	    
    	 // Set Poll Interval
    	    pollInterval = 0;
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMyyyy(settlementDate));

    	 // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Rerun Accounts Verification"); 
    	    standingVersion = pavPackageImpl.getCurrVersionPkt("STANDING", utilityFunctions.getddMMMyyyyHyphen(settlementDate), 
    	                                                   null);

    	    logger.info(logPrefix + "Standing Version: " + standingVersion);
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("settlementDate", settlementDate);
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("standingVersion", standingVersion);
    	variableMap.put("pollInterval", pollInterval);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval
				+ " standingVersion :" + standingVersion
				+ " settlementDate :" + settlementDate + ")");
    	return variableMap;
    }


    @Transactional(readOnly = true)
	public Map<String, Object> verifySettAccountsInRerun(Map<String, Object> variableMap)
    {

		Date settlementDate = (Date) variableMap.get("settlementDate");
		String errAlert = (String) variableMap.get("errAlert");
		String standingVersion = (String) variableMap.get("standingVersion");
		Boolean valid = false;

		if(errAlert == null)
			errAlert="";
		
		String msgStep = "ScheduledRerunAccountsVerification.verifySettAccountsInRerun()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( standingVersion : " + standingVersion
				+ " valid : " + valid
				+ " settlementDate : " + settlementDate + ")");

		try {
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

			Date settlementDateRerun;
			String standingVersionRerun;
			String settlementRerunType;
			String sqlCommand;
			String sqlCommand1;
			String sqlCommand2;
			standingVersion = pavPackageImpl.getCurrVersionPkt("STANDING", utilityFunctions.getddMMMyyyyHyphen(settlementDate), null);

			logger.info(logPrefix + "Standing Version: " + standingVersion);

			int rowcnt = 0;
			String settRerunId = "";

			// TODO get Authorised R-S Run which not associated with P,F run.
			sqlCommand = "SELECT ID, SETTLEMENT_DATE, RUN_TYPE FROM NEM.NEM_SETTLEMENT_RUN_STATUS_V WHERE (RUN_TYPE='R' or RUN_TYPE='S')" +
    	                 " AND AUTHORISED='A' AND ID NOT IN (SELECT RERUN_STR_ID FROM NEM.NEM_SETTLEMENT_RERUN_INCS)";

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
			for(Map recordMap : resultList) {
				rowcnt = rowcnt + 1;
				settRerunId = (String) recordMap.get("ID");
				settlementDateRerun = (Date) recordMap.get("SETTLEMENT_DATE");
				settlementRerunType = (String) recordMap.get("RUN_TYPE");

				logger.info(logPrefix + "Settlement Date of Rerun is : " + settlementDateRerun);

				standingVersionRerun = pavPackageImpl.getCurrVersionPkt("STANDING", utilityFunctions.getddMMMyyyyHyphen(settlementDateRerun),null);

				logger.info(logPrefix + "Standing Version for Rerun is: " + standingVersionRerun);

				//sqlCommand1 = "SELECT ID From NEM.NEM_SETTLEMENT_ACCOUNTS WHERE VERSION = ?";
				// Replaced Acc Display Title with External ID as Display Title is not Unique attribute for Account
				sqlCommand1 = "SELECT ID, external_id FROM nem.nem_settlement_accounts WHERE VERSION = ? " +
    	                      "MINUS " +
    	                      "SELECT * FROM " +
    	                      "( " +
    	                      "SELECT out_sac.ID, out_sac.external_id FROM nem.nem_settlement_accounts out_sac WHERE out_sac.VERSION = ? " +
    	                      "AND (SELECT MIN (TO_NUMBER (in_sac.VERSION)) FROM nem.nem_settlement_accounts in_sac WHERE in_sac.ID = out_sac.ID) <= TO_NUMBER (?) " +
    	                      "UNION " +
    	                      "SELECT DISTINCT out_sac.ID, out_sac.external_id FROM nem.nem_settlement_accounts out_sac WHERE (SELECT MAX (TO_NUMBER (in_sac.VERSION)) FROM nem.nem_settlement_accounts in_sac WHERE in_sac.ID = out_sac.ID) >= TO_NUMBER (?) " +
    	                      "AND (SELECT MAX (TO_NUMBER (in_sac.VERSION)) FROM nem.nem_settlement_accounts in_sac WHERE in_sac.ID = out_sac.ID) < TO_NUMBER (?) ) " ;

				Object[] params = new Object[5];
				params[0] = standingVersionRerun;
				params[1] =  standingVersion;
				params[2] =  standingVersionRerun;
				params[3] =  standingVersionRerun;
				params[4] =  standingVersion;
				List<Map<String, Object>> resultList1 = jdbcTemplate.queryForList(sqlCommand1, params);
				for(Map recordMap1 : resultList1) {
					String tempAccountIdRerun = (String) recordMap1.get("ID");

					throw new AccountValidationException("Account not exists: " + tempAccountIdRerun);
				}

				//checking whether NMEA exists for expiring account

				sqlCommand2 = "SELECT DISTINCT SAC_ID, SAC_VERSION FROM NEM.NEM_SETTLEMENT_RESULTS WHERE SETTLEMENT_DATE = ? AND STR_ID = ? AND (SRT_ID, SRT_VERSION)  = " +
    	                      " (SELECT ID, VERSION FROM NEM_SETTLEMENT_RESULT_TYPES WHERE VERSION = ? AND NAME = 'NMEA') " +
    	                      " AND NVL (CALCULATION_RESULT, 0) <> 0 " +
    	                      " AND SAC_ID NOT IN (SELECT ID FROM NEM.NEM_SETTLEMENT_ACCOUNTS WHERE VERSION = ?) " +
    	                      " AND 'S' = (SELECT RUN_TYPE FROM NEM_SETTLEMENT_RUNS WHERE ID = ? ) " ;

				params = new Object[5];
				params[0] = utilityFunctions.getddMMMyyyyHyphen(settlementDateRerun);
				params[1] = settRerunId;
				params[2] = standingVersionRerun;
				params[3] = standingVersion;
				params[4] = settRerunId;
				List<Map<String, Object>> resultList2 = jdbcTemplate.queryForList(sqlCommand2, params);
				for(Map recordMap2 : resultList2) {
					String tempAccountIdNMEAExists = (String) recordMap2.get("SAC_ID");

					throw new AccountValidationException("This Account with NON-ZERO NMEA values in included S Run does not exists in the following P/F Run: " + tempAccountIdNMEAExists);
				}
			}

			if (rowcnt == 0) {
				logger.info(logPrefix + "No unassociated Re-Run found.");
			}
    	    else {
				logger.info(logPrefix + "All Accounts in Re-Runs are valid.");
			}

			valid = true;
		}
    	catch (AccountValidationException e) {
			logger.error(logPrefix + "Accounts in Rerun is not valid. " + e.getMessage());
			errAlert = e.getMessage();

			valid = false;
		}catch(Exception e) {
			logger.error(logPrefix + "Accounts in Rerun is not valid. " + e.getMessage());
			errAlert = e.getMessage();

			valid = false;
		}
		variableMap.put("valid", valid);
		variableMap.put("standingVersion", standingVersion);
		variableMap.put("errAlert", errAlert);
		logger.info("Returning from service "+msgStep+" - ( standingVersion :" + standingVersion
				+ " valid :" + valid + ")");
		return variableMap;
	}
  

	@Transactional
	public void prepareAlertInfo(Map<String, Object> variableMap)
    {
		String errAlert = (String) variableMap.get("errAlert");
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "ScheduledRerunAccountsVerification.prepareAlertInfo()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " errAlert : " + errAlert
				+ " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
			
			String msg = "Error related to Rerun Settlement Accounts Existense / NMEA related error for Settlement Date " + 
					utilityFunctions.getddMMyyyy(settlementDate) ;

				    // BPM Log
				    logger.info(logPrefix + msg);

				    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
				    // Send Alert Email
				    AlertNotification alert = new AlertNotification();
				    alert.businessModule = "MEUC Data Verification";
				    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
				    alert.subject = msg;
				    alert.content = msg + "." + errAlert + "," + " at timing " + utilityFunctions.geHHmm(new Date());
				    alert.noticeType = "Existence of Settlement Accounts used in Rerun Verification";
				    notificationImpl.sendEmail(alert);

				    // Log JAM Message
				    utilityFunctions.logJAMMessage(eveId, "E", "Rerun Accounts Verification", msg, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		String msgStep = "ScheduledRerunAccountsVerification.updateEvent()";
		logger.info("Input parameters "+msgStep+" - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + msgStep + e.getMessage());
		}

	}
	
   
}
