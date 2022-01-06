/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
public class ScheduledPendingForAuthorizationEMCPSOFeesDataExistence {

	/**
	 * 
	 */
	public ScheduledPendingForAuthorizationEMCPSOFeesDataExistence() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.class);


	
@Autowired
private UtilityFunctions utilityFunctions;
@Autowired
private AlertNotificationImpl notificationImpl;
@Autowired
private JdbcTemplate jdbcTemplate;

String logPrefix = "[SH-EPWV] ";

	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {
	
		String msgStep = "ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.checkBusinessDay()";
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
		String msgStep = "ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.initializeVariables()";
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
			throw new Exception("Abnormal Termination of ScheduledPending For Authorization EMC PSOFees DataExistence : (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		return variableMap;
	
	}
     
	@Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap) 
    {	
		Date settlementDate=(Date) variableMap.get("settlementDate");
		String eveId = (String) variableMap.get("eveId");;
		
    	Map<String, Object> retMap = new HashMap<String, Object>();
    	
    	String msgStep = "ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.createEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
    	try {
    		


    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    logger.info(logPrefix + "Pending for Authorization EMC/PSO Fees Data Existence for Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Pending for Authorization EMC/PSO Fees Data Existence");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	retMap.put("eveId", eveId);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + ")");
    	return retMap;
    }

	@Transactional
    public Map<String, Object> checkAuthFeesData(Map<String, Object> variableMap) 
    {
		Boolean valid=(Boolean) variableMap.get("valid");
		Date settlementDate=(Date) variableMap.get("settlementDate");
		
    	Map<String, Object> retMap = new HashMap<String, Object>();
    	
    	String msgStep = "ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.checkAuthFeesData()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( valid : " + valid
				+ " settlementDate : " + settlementDate + ")");
		try {

    	   logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    valid = false;
    	    Date firstDayOfMonth = utilityFunctions.firstDayOfMonth(settlementDate);
    	    String sqlCommand = "SELECT count(*) count FROM NEM.NEM_SETTLEMENT_FEES_BUDGET_DTL " + 
    	    	    "WHERE month = ? AND lock_version = 1 AND approval_status = 'W'";
    	    int count = 0;
			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, firstDayOfMonth);
			for (Map row : list) {
				count = ((BigDecimal)row.get("count")).intValue();
    	    	//count = (Integer)row.get("count");
    	    }

    	    if (count == 0) {
    	        valid = true;

    	        logger.info(logPrefix + "NO Pending for Authorization EMC/PSO Fees Data Existence for Settlement Date: " + utilityFunctions.getddMMyyyy(settlementDate));
    	    }
    	    else if (count > 0) {
    	        logger.info(logPrefix + "Pending for Authorization EMC/PSO Fees Data exists for Settlement Date: " + utilityFunctions.getddMMyyyy(settlementDate));
    	    }
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	retMap.put("valid", valid);
		logger.info("Returning from service "+msgStep+" - ( valid :" + valid + ")");
    	return retMap;
    }
  
	@Transactional
	public void sendAlertEmail(Map<String, Object> variableMap) 
    {	
		Date settlementDate=(Date) variableMap.get("settlementDate");
		String eveId=(String) variableMap.get("eveId");
		Boolean valid=(Boolean) variableMap.get("valid");
    	String msgStep = "ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementDate : " + settlementDate
				+ " eveId : " + eveId + "valid :" + valid +")");
		if(!valid.booleanValue()) {
			try{
				logger.info(logPrefix + " Starting Activity: " + msgStep);
				
				String msg = "Pending for Authorization EMC/PSO Fees Data for Settlement Date " + 
						utilityFunctions.getddMMyyyy(settlementDate) + " exists.";
	
			    // BPM Log
			    logger.info(logPrefix + "" + msg);
	
			    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			    AlertNotification alert = new AlertNotification();
			    alert.businessModule = "Pending for Authorization EMC/PSO Fees Data Existence";
			    alert.content = msg + " at timing " + utilityFunctions.geHHmm(new Date());
			    alert.recipients = propertiesMap.get("EMCPSO_UPLOAD_FAIL_EMAIL");//BusinessParameters.EMCPSO_UPLOAD_FAIL_EMAIL;
			    alert.subject = msg;
			    alert.noticeType = "Pending for Authorization EMC/PSO Fees Data Existence";
			    notificationImpl.sendEmail(alert);
			    
			    // Log JAM Message
			    utilityFunctions.logJAMMessage(eveId, "E", "Pending for Authorization EMC/PSO Fees Data Existence" , msg, "");
			}
	    	catch (Exception e) {
	    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
	    	}
		}

    }
	
	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		Boolean valid=(Boolean) variableMap.get("valid");
		String eveId=(String) variableMap.get("eveId");
		logger.info("Input parameters ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.updateEvent(): " + e.getMessage());
		}
	}
}
