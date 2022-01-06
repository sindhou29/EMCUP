/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
public class ScheduledMEUCDataVerification {

	/**
	 * 
	 */
	public ScheduledMEUCDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledMEUCDataVerification.class);


	
@Autowired
private UtilityFunctions utilityFunctions;
@Autowired
private AlertNotificationImpl notificationImpl;
@Autowired
private JdbcTemplate jdbcTemplate;

String logPrefix = "[SH-MEUCV] ";
 	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {
	
		String msgStep = "ScheduledMEUCDataVerification.checkBusinessDay()";
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
	
	@Transactional
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {

		String msgStep = "ScheduledMEUCDataVerification.initializeVariables()";
		String soapServiceUrl = null;
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
			//cal.add(Calendar.DATE, 1);
			cal.add(Calendar.MINUTE, 10);
			settlementParam.setRunDate(cal.getTime());
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new Exception("Abnormal Termination of Scheduled MEUC Data Verification : (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - ( soapServiceUrl :" + soapServiceUrl
				+ " settlementParam.getRunType() :" + settlementParam.getRunType()
				+ " settlementParam.getRunDate() :" + settlementParam.getRunDate()
				+ " settlementParam :" + settlementParam + ")");
		return variableMap;
	}

    @Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap)
    {

		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		String eveId = (String) variableMap.get("eveId");
		Integer pollInterval = (Integer) variableMap.get("pollInterval");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "ScheduledMEUCDataVerification.createEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId
				+ " pollInterval : " + pollInterval
				+ " settlementDate : " + settlementDate + ")");
    	try {

    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    // Set Cut-off Time
    	    cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);

    	    // Set Poll Interval
    	    pollInterval = 0;
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "MEUC Data Verification");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("pollInterval", pollInterval);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval + ")");
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> checkMEUC(Map<String, Object> variableMap)
    {

		Date settlementDate = (Date) variableMap.get("settlementDate");
		Boolean valid = (Boolean) variableMap.get("valid");
    	String msgStep = "ScheduledMEUCDataVerification.checkMEUC()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementDate : " + settlementDate
				+ " valid : " + valid + ")");

		try {

    	   logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
			BigDecimal count = new BigDecimal(0);
			String sqlCommand = "SELECT count(*) CNT FROM NEM.nem_meuc " +
    	    	    "WHERE TRUNC (settlement_month, 'MON') = TRUNC (?, 'MON') " + 
    	    	    "AND expired_date > NVL (?, SYSDATE) and approval_status = 'A'";

			Object[] params = new Object[2];
			params[0] = utilityFunctions.convertUDateToSDate(settlementDate);
			params[1] = utilityFunctions.convertUDateToSDate(new Date());
			Object[] result = utilityFunctions.queryforList(sqlCommand, params, "CNT");
			count = (BigDecimal) result[0];

    	    if (count.intValue() > 0) {
    	        valid = true;

    	        logger.info(logPrefix + "MEUC Data is ready.");
    	    }
    	    else {
    	        logger.info(logPrefix + "MEUC Data is not ready.");
    	    }
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("valid", valid);
		logger.info("Returning from service "+msgStep+" - ( valid :" + valid + ")");
    	return variableMap;
    }



	@Transactional(readOnly = true)
	public void alertNotification(Map<String, Object> variableMap)
    {

		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

    	String msgStep = "ScheduledMEUCDataVerification.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
		    
			String msg = "MEUC for Settlement Date " + utilityFunctions.getddMMyyyy(settlementDate) + 
				    " are not available";

		    // BPM Log
		    logger.info(logPrefix + "" + msg);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "MEUC Data Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = msg;
		    alert.content = msg + " at timing " + utilityFunctions.geHHmm(new Date());
		    alert.noticeType = "MEUC Data Verification";

		    notificationImpl.sendEmail(alert);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", "MEUC Data Verification", msg, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters ScheduledMEUCDataVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledMEUCDataVerification.updateEvent(): " + e.getMessage());
		}
	}
}
