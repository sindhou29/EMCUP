/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
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
public class ScheduledMSSLFileReceivingVerification {

	/**
	 * 
	 */
	public ScheduledMSSLFileReceivingVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledMSSLFileReceivingVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-MRECV] ";
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = "ScheduledMSSLFileReceivingVerification.checkBusinessDay()";
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
	
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {
		String msgStep = "ScheduledMSSLFileReceivingVerification.initializeVariables()";
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
					"Abnormal Termination of Scheduled MSSL File Receiving Verification : (initializeVariables)");
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

    	String msgStep = "ScheduledMSSLFileReceivingVerification.createEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
    	try {
    		


    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "MSSL File Recv Verification");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + ")");
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> checkMSSL(Map<String, Object> variableMap)
    {
		Date settlementDate = (Date) variableMap.get("settlementDate");
		Boolean valid = (Boolean) variableMap.get("valid");

    	String msgStep = "ScheduledMSSLFileReceivingVerification.checkMSSL()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( valid : " + valid
				+ " settlementDate : " + settlementDate + ")");
		try {

    	   logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
    	    int rowcnt = 0;
    	    String sqlCommand = "SELECT 1 count FROM NEM.NEM_EBT_EVENTS A INNER JOIN NEM.NEM_SETTLEMENT_RAW_FILES B" +
    	    " ON A.ID=B.EBE_ID WHERE SETTLEMENT_DATE = ? AND EVENT_TYPE IN ('MTR', 'DMF') " + 
    	    " AND VALID_YN = 'Y' AND ROWNUM = 1 ";

    	    // ITSM 15086
    	    BigDecimal num = new BigDecimal(0);

    	    List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementDate));
			for (Map row : list) {
    	    	num = (BigDecimal)row.get("count");
    	    }

    	    if (num.intValue() == 0) {
    	        logger.info(logPrefix + "MSSL Metering File is not available.");
    	    }
    	    else {
    	        valid = true;
    	        logger.info(logPrefix + "Metering File is available.");
    	    }
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	variableMap.put("valid", valid);
		logger.info("Returning from service "+msgStep+" - ( valid :" + valid + ")");
    	return variableMap;
    }
  
	public void alertNotification(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

    	String msgStep = "ScheduledMSSLFileReceivingVerification.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
			
			// msg as String = "Metering File for Settlement Date "                     // ITSM 15386          
		    // 	+ settlementDate.format(DISPLAY_DATE_FORMAT) + " are not available"	   // ITSM 15386
		    String msgSubject = "Metering File for Settlement Date " + 
		    utilityFunctions.getddMMyyyy(settlementDate) + " is not available";
		    
			// ITSM 15386
		    String msgLog = "Metering File for Settlement Date " + utilityFunctions.getddMMyyyy(settlementDate) + " is not available at timing " + utilityFunctions.geHHmm(new Date());

		    // ITSM 15386	
		    String msg = "This is an added email notification service provided by EMC." + 
		    "\n" + 
		    "\nBased on the Market Operations Market Manual (Settlements), " + 
		    "MSSL shall provide the EMC with metering data in accordance with section 2.1.1.1, Chapter 7 of the market rules in ASCII format no later than 5:00pm, " + 
		    "five business days after trading day. This metering file for Settlement Date " + utilityFunctions.getddMMyyyy(settlementDate) + " is not available at timing 4:00pm." + 
		    "\n" + 
		    "\nFor your necessary actions please." + 
		    "\n" + 
		    "\n" + 
		    "\n" + utilityFunctions.getSysParamVarChar("HIGH_USEP_PRICE_EMAIL_FOOTER");

		    // BPM Log
		    logger.info(logPrefix + "" + msgLog);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "MSSL File Receiving Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		 // alert.subject = msg                                           // ITSM 15386
		    alert.subject = msgSubject;

		    // ITSM 15386
		    // alert.content = msg + " at timing " + 'now'.format("HH:mm")   // ITSM 15386
		    alert.content = msg;

		    // ITSM 15386
		    alert.noticeType = "MSSL Metering File Receiving Verification";

		    // ITSM 15386 
		    notificationImpl.sendSchAlertEmailIncExtParty( "ScheduledMSSLFileReceivingVerification", UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY"), 
		                                       eveId, alert);

		 // sendEmail alert
		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", msgStep , msgLog, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters ScheduledMSSLFileReceivingVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		logger.log(Priority.INFO, "Input Parameters for updateEvent  ( eveId : " + eveId
				+ " valid : " + valid + ")");
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledMEUCDataVerification.updateEvent(): " + e.getMessage());
		}

	}
}
