/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
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
public class ScheduledCMWHDataVerification {

	/**
	 * 
	 */
	public ScheduledCMWHDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledCMWHDataVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private AlertNotificationImpl notificationImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-CMWHV] ";
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = "ScheduledCMWHDataVerification.checkBusinessDay()";
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
		
		String msgStep = "ScheduledCMWHDataVerification.initializeVariables()";
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " soapServiceUrl : " + soapServiceUrl
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
			throw new Exception("Abnormal Termination of Scheduled CMWH Data Verification : (initializeVariables)");
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
	public Map<String, Object> createEvent(Map<String, Object> variableMap) {

		String msgStep = "ScheduledCMWHDataVerification.createEvent()";
		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		String eveId = (String) variableMap.get("eveId");
		Integer pollInterval = (Integer) variableMap.get("pollInterval");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId
				+ " pollInterval : " + pollInterval
				+ " settlementDate : " + settlementDate + ")");

		try {
    	    logger.log(Priority.INFO, logPrefix + "Starting Activity: ScheduledCMWHDataVerification.createEvent()");

    	    // Set Cut-off Time
			cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);

    	    // Set Poll Interval
    	    pollInterval = 0;
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.log(Priority.INFO, logPrefix + "Settlement Date: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")).format(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "CMWH Data Verification");
    	} catch (Exception e)
    	{
    		logger.log(Priority.ERROR, logPrefix + "Exception occured on ScheduledCMWHDataVerification.createEvent(): " + e.getMessage());
    	}
    	
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("pollInterval", pollInterval);
    	variableMap.put("settlementDate", settlementDate);
    	variableMap.put("eveId", eveId);
    	
    	logger.info("Returning from service = cutoffTime :"+cutoffTime+" pollInterval :"+pollInterval+" settlementDate :"+settlementDate+" eveId :"+eveId);
    	return variableMap;
    }
    
    @Transactional(readOnly = true)
	public Map<String, Object> checkCMWH(Map<String, Object> variableMap) {

		Date settlementDate = (Date) variableMap.get("settlementDate");
		Boolean valid = (Boolean) variableMap.get("valid");
		String msgStep = "ScheduledCMWHDataVerification.checkCMWH()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementDate : " + settlementDate
				+ " valid : " + valid + ")");

    	try {
    	    msgStep = "ScheduledCMWHDataVerification" + "." + "checkCMWH()";
    	    logPrefix = "[SH-CMWHV]";
    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
    	    int rowcnt = 0;
    	    String sqlCommand;
    	    sqlCommand = "SELECT 1 FROM aps_system_parameters WHERE name = 'CMWH' AND nvl(number_value,0) > 0 ";
    	    BigDecimal val = new BigDecimal(0);

			List<Map<String, Object>> recordList = jdbcTemplate.queryForList(sqlCommand);
			for(Map recordMap : recordList)
			{
				val = (BigDecimal) recordMap.get("1");
			}

    	    if (val.intValue() == 0) {
    	    	logger.info(logPrefix + "CMWH Data for Settlement Date: " + utilityFunctions.getddMMyyyy(settlementDate) + 
    	        " is not ready at: " + utilityFunctions.getddMMMyyyyhhmmss(new Date()));
    	    }
    	    else {
    	        valid = true;

    	        logger.info(logPrefix + "CMWH Data is ready.");
    	    }
    	}catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ". " + e.getMessage());
    	}
    	
     	variableMap.put("valid", valid);
    	logger.info("Returning from service = valid :"+valid);
    	return variableMap;
    }
    
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters updateEvent - valid :"+valid+" eveId :"+eveId);
       	try {
       		utilityFunctions.updateJAMEvent(valid, eveId);
       	}catch (Exception e)
       	{
       		logger.log(Priority.ERROR, logPrefix + "Exception occured on ScheduledCMWHDataVerification.updateEvent(): " + e.getMessage());
       	}

       }
   	
  
   	@Transactional(readOnly = true)
   	public void alertNotification(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		logger.info("Input parameters alertNotification - settlementDate :"+settlementDate+" eveId :"+eveId);

       	try {

       	    String msg = "CMWH for Settlement Date " + 
       	    		utilityFunctions.getddMMyyyy(settlementDate) + " is not available";

       	    // BPM Log
			logger.log(Priority.ERROR, logPrefix + msg);

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			// Send Alert Email
			AlertNotification alert = new AlertNotification();
			alert.businessModule = "CMWH Data Verification";
			alert.content = msg + " at timing " + utilityFunctions.geHHmm(new Date());
			alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
			alert.subject = msg;
			alert.noticeType = "CMWH Data Verification";
			notificationImpl.sendEmail(alert);

			// Log JAM Message
		 	utilityFunctions.logJAMMessage(eveId, "E", "CMWH Data Verification",  msg, "");
       	}catch (Exception e){
       		logger.log(Priority.ERROR, logPrefix + "Exception occured on ScheduledCMWHDataVerification.alertNotification(): " + e.getMessage());
       	}
   	}
}
