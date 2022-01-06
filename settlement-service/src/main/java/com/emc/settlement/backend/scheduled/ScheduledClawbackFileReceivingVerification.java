/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.DateRange;
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
public class ScheduledClawbackFileReceivingVerification {

	/**
	 * 
	 */
	public ScheduledClawbackFileReceivingVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(ScheduledClawbackFileReceivingVerification.class);


	
@Autowired
private UtilityFunctions utilityFunctions;
@Autowired
private AlertNotificationImpl notificationImpl;
@Autowired
private JdbcTemplate jdbcTemplate;

String logPrefix = "[SH-CLWRV]";

	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {
	
		String msgStep = "ScheduledClawbackFileReceivingVerification.checkBusinessDay()";
		Boolean businessDay = false;
	
		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");
	
		logger.info(logPrefix + "Today is: " + utilityFunctions.getddMMMyyyy(new Date()));
	
		try {
			businessDay = utilityFunctions.isBusinessDay(new Date());
		} catch (Exception e) {
			logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
		}
		variableMap.put("businessDay", businessDay);
		logger.info("Returning from service " + msgStep + " - ( businessDay :" + businessDay + ")");
		return variableMap;
	
	}

	@Transactional
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {
		String msgStep = "ScheduledClawbackFileReceivingVerification.initializeVariables()";
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam : " + settlementParam + ")");
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
			throw new Exception(
					"Abnormal Termination of Scheduled Clawback File Receiving Verification : (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - ( soapServiceUrl :" + soapServiceUrl
				+ " settlementParam :" + settlementParam + ")");
		return variableMap;
	
	}

	@Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap) 
    {

		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");


		String msgStep = "ScheduledClawbackFileReceivingVerification.createEvent()";
    	try {
    		
    	    logPrefix = "[SH-CLWRV] ";

    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    // Get Settlement Date
    	    Date now = new Date();

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Clawback File Receiving Verification");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	return variableMap;
    }


    @Transactional(readOnly = true)
	public Map<String, Object> checkClawbackFile(Map<String, Object> variableMap)
	{

		Date settlementDate = (Date) variableMap.get("settlementDate");
		Boolean valid = (Boolean) variableMap.get("valid");

    	String msgStep = "ScheduledClawbackFileReceivingVerification.checkClawbackFile()";
		try {

    	   logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
    	    String sqlGetClawbackFile = " SELECT A.ID FROM NEM.NEM_EBT_EVENTS A INNER JOIN NEM.NEM_SETTLEMENT_RAW_FILES B " + 
    	    " ON A.ID=B.EBE_ID WHERE SETTLEMENT_DATE = ? AND EVENT_TYPE IN ('CLF','CLS') AND VALID_YN = 'Y' AND ROWNUM = 1 ";
    	    String ebeEventId = null;

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlGetClawbackFile, utilityFunctions.convertUDateToSDate(settlementDate));
			for(Map recordMap : resultList) {
				ebeEventId = (String) recordMap.get("ID");
			}

    	    // Check the existance of the clawback file
    	    if (ebeEventId == null || ebeEventId.isEmpty()) {
    	        logger.info(logPrefix + "Clawback File is not available.");
    	    }
    	    else {
    	        valid = true;
    	    }
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("valid", valid);
    	return variableMap;
    }
    
    

    @Transactional(readOnly = true)
	public void alertNotification(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "ScheduledClawbackFileReceivingVerification.alertNotification()";
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
			
//		    msg as String = "Clawback File for Settlement Date " + 
		    //    format(settlementDate, mask : DISPLAY_DATE_FORMAT) + " are not available"
		    // ITSM 15386
		    String msgSubject = "Reserve and Regulation Non-Provision Information File for Settlement Date " + utilityFunctions.getddMMMyyyy(settlementDate) + " is not available";
		    String msgLog = "Clawback File for Settlement Date " + utilityFunctions.getddMMMyyyy(settlementDate) + " is not available at timing " +  utilityFunctions.geHHmm(new Date());
		    String msg = "This is an added email notification service provided by EMC." + 
		    "\n" + 
		    "\nBased on the Market Operations Market Manual (Settlements),  PSO shall provide the EMC with the Reserve and Regulation Non-Provision Information file in accordance with Chapter 7, " + 
		    "section 2.6.3 of the market rules no later than 5:00pm, of the fifth business day after the trading day.   " + 
		    "This Reserve and Regulation Non-Provision Information file for Settlement Date " + utilityFunctions.getddMMMyyyy(settlementDate) + " is not available at timing 4:00pm." + 
		    "\n" + 
		    "\nFor your necessary actions please." + 
		    "\n" + 
		    "\n" + 
		    "\n" + utilityFunctions.getSysParamVarChar("HIGH_USEP_PRICE_EMAIL_FOOTER");

		    // BPM Log
		    logger.info(logPrefix + "" + msgLog// ITSM 15386
		    );

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "Non Providing Facilities File Receiving Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = msgSubject;

		    // alert.content = msg + " at timing " + format('now', mask : "HH:mm")
		    alert.content = msg;
		    alert.noticeType = "Non Providing Facilities File Receiving Verification";

		    // ITSM 15386 
		    notificationImpl.sendSchAlertEmailIncExtParty( "ScheduledClawbackFileReceivingVerification", UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY"), 
                    eveId, alert);

		    // sendEmail alert
		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", msgStep, 
		                                    msgLog, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters ScheduledClawbackFileReceivingVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledClawbackFileReceivingVerification.updateEvent(): " + e.getMessage());
		}

	}
}
