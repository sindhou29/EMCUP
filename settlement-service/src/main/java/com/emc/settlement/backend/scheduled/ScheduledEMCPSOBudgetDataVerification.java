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
public class ScheduledEMCPSOBudgetDataVerification {

	/**
	 * 
	 */
	public ScheduledEMCPSOBudgetDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledEMCPSOBudgetDataVerification.class);


	
@Autowired
private UtilityFunctions utilityFunctions;
@Autowired
private AlertNotificationImpl notificationImpl;
@Autowired
private JdbcTemplate jdbcTemplate;

String logPrefix = "[SH-EPDV]";
 	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {
	
		String msgStep = "ScheduledEMCPSOBudgetDataVerification.checkBusinessDay()";
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
		String msgStep = "ScheduledEMCPSOBudgetDataVerification.initializeVariables()";
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
			throw new Exception("Abnormal Termination of Scheduled EMC PSO Budget Data Verification : (initializeVariables)");
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

		String msgStep = "ScheduledEMCPSOBudgetDataVerification.createEvent()";
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
    	    eveId = utilityFunctions.createJAMEvent("EXE", "EMC/PSO Data Verification");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("settlementDate", settlementDate);
    	variableMap.put("pollInterval", pollInterval);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval
				+ " settlementDate :" + settlementDate + ")");
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> checkEMCPSOBudgetData(Map<String, Object> variableMap)
    {

		Date settlementDate = (Date) variableMap.get("settlementDate");
		Boolean valid = (Boolean) variableMap.get("valid");

    	String msgStep = "ScheduledEMCPSOBudgetDataVerification.checkEMCPSOBudgetData()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( valid : " + valid
				+ " settlementDate : " + settlementDate + ")");
		try {

    	   logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
    	 // [ITSM-15890] Check EMC Price Cap effective date
    	    boolean pcEffective = utilityFunctions.isAfterPriceCapEffectiveDate(settlementDate);
    	    Date firstDayOfMonth = utilityFunctions.truncateTime(utilityFunctions.getFirstDateOfMonth(settlementDate));
    	    String sqlCommand;

    	    if (pcEffective == false) {
    	        sqlCommand = "SELECT count(*) FROM NEM.NEM_SETTLEMENT_FEES_BUDGET " + 
    	                     "WHERE month = ? ";
    	    }
    	    else {
    	        sqlCommand = "SELECT count(*) CNT FROM NEM.NEM_SETTLEMENT_FEES_BUDGET " +
    	                     "WHERE month = ? AND FEE_CODE IN ('PSOADMIN','EMCPRCAP','EMCADJST') ";
    	    }
    	    BigDecimal count = new BigDecimal(0);

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(firstDayOfMonth));
			for(Map recordMap : resultList) {
				count = (BigDecimal) recordMap.get("CNT");
			}

    	    if (count.intValue() > 0 && pcEffective == false) {
    	        valid = true;

    	        logger.info(logPrefix + "EMC/PSO Budget Data is ready.");
    	    }
    	    else {
    	        if (count.intValue() == 3 && pcEffective == true) {
    	            valid = true;

    	            logger.info(logPrefix + "EMC/PSO Price Cap Regime Data is ready.");
    	        }
    	        else {
    	            logger.info(logPrefix + "EMC/PSO Budget Data is not ready.");
    	        }
    	    }
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("valid", valid);
		logger.info("Returning from service "+msgStep+" - (valid :" + valid + ")");
    	return variableMap;
    }
    
    
  
	@Transactional(readOnly = true)
	public void alertNotification(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

    	String msgStep = "ScheduledEMCPSOBudgetDataVerification.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
		    
		    String msg = "EMC/PSO Budget Data for Settlement Date " +  utilityFunctions.getddMMyyyy(settlementDate) + 
		    	    " are not available";

		    // BPM Log
		    logger.info(logPrefix + "" + msg);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "EMC/PSO Budget Data Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = msg;
		    alert.content = msg + " at timing " + utilityFunctions.geHHmm(new Date());
		    alert.noticeType = "EMC/PSO Budget Data Verification";

		    notificationImpl.sendEmail(alert);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", msgStep, msg, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters ScheduledEMCPSOBudgetDataVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledEMCPSOBudgetDataVerification.updateEvent(): " + e.getMessage());
		}
	}
}
