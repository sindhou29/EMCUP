/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.VestingContractException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
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
public class ScheduledVestingContractDataVerification implements Serializable{

	/**
	 * 
	 */
	public ScheduledVestingContractDataVerification() {
		// TODO Auto-generated constructor stub
	}


	private static final Logger logger = Logger.getLogger(ScheduledVestingContractDataVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-VCDV] ";
	String msgStep = "";
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = "ScheduledVestingContractDataVerification.checkBusinessDay()";
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
	public Map<String, Object>  initializeVariables(Map<String, Object> variableMap)
	{
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam =(SettlementRunParams) variableMap.get("settlementParam");
	    msgStep = "ScheduledVestingContractDataVerification.initializeVariables()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam : " + settlementParam + ")");
		try{
		    logger.info("Starting Activity "+msgStep);
		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    soapServiceUrl = propertiesMap.get("soapServiceUrl");
		    
		    settlementParam.setRunType("P");
		    
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, 1);
			settlementParam.setRunDate(cal.getTime());
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract Data Verification : "+e.getMessage(), msgStep);
		}
		logger.info("Returning from service "+msgStep+" - soapServiceUrl : "+soapServiceUrl + " settlementParam :" + settlementParam + ")");
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		return variableMap;
		
	}
	
	
    @Transactional
	public Map<String, Object> initializeSettlementDate(Map<String, Object> variableMap)
    {

		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		String eveId = (String) variableMap.get("eveId");
		PeriodNumber pd = (PeriodNumber) variableMap.get("pd");
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		Integer pollInterval = (Integer) variableMap.get("pollInterval");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		SettlementRunParams settlementParam =(SettlementRunParams) variableMap.get("settlementParam");


		msgStep = "ScheduledVestingContractDataVerification.initializeSettlementDate()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId + " pd : "
				+ pd + " soapServiceUrl : " + soapServiceUrl + " pollInterval : " + pollInterval + " settlementDate : " + settlementDate +
				" settlementParam : " + settlementParam + ")");
    	try {

    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    // Set Cut-off Time
			cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);

    	    // Set Poll Interval
    	    pollInterval = 0;
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Vesting Data Verification");
    	    
    	    // Get Period Numbers
    	    pd.total = ((int) utilityFunctions.getSysParamNum( "NO_OF_PERIODS"));
    	    pd.sum = ((int) utilityFunctions.getSysParamNum( "SUM_OF_PERIODS"));
    	    pd.sum2 = ((int) utilityFunctions.getSysParamNum( "SUM_SQUARE_OF_PERIODS"));
    	    pd.avg3 = ((int) utilityFunctions.getSysParamNum( "AVG_CUBIC_OF_PERIODS"));

    	    settlementParam.setMainEveId(eveId);
    	    settlementParam.setRunEveId(eveId);
    	    settlementParam.setSettlementDate(settlementDate);
    	    
    	    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
    		soapServiceUrl = propertiesMap.get("soapServiceUrl");
    	
    	}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract Data Verification : "+e.getMessage(), msgStep);
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("pollInterval", pollInterval);
    	variableMap.put("pd", pd);
    	variableMap.put("settlementDate", settlementDate);
    	variableMap.put("soapServiceUrl", soapServiceUrl);
    	variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval + " pd :" + pd + " settlementDate :" + settlementDate
				+ " soapServiceUrl :" + soapServiceUrl + " settlementParam :" + settlementParam + ")");
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> checkCutoffTime(Map<String, Object> variableMap)
    {


		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		Boolean exceedCutoffTime = (Boolean) variableMap.get("exceedCutoffTime");
		Date settlementDate = (Date) variableMap.get("settlementDate");

    	msgStep = "ScheduledVestingContractDataVerification.checkCutoffTime()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " exceedCutoffTime : " + exceedCutoffTime + ")");
		try{
			
			exceedCutoffTime = false;
			Date now = new Date();

			if (now.compareTo(cutoffTime) >= 0) {
			    exceedCutoffTime = true;
			}
			logger.info("Settlement Date: " + settlementDate);
			logger.info("Current Time: "+now);
			logger.info("Cutoff Time: "+cutoffTime);
			logger.info("exceedCutoffTime: "+exceedCutoffTime);

			logger.info(logPrefix + "Now(" + utilityFunctions.getddMMMyyyyhhmmss(new Date())+ ") exceed CutoffTime(" + 
					utilityFunctions.getddMMMyyyyhhmmss(cutoffTime) + ") ? " + exceedCutoffTime);

		}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract Data Verification : "+e.getMessage(), msgStep);
    	}

    	variableMap.put("exceedCutoffTime", exceedCutoffTime);
		logger.info("Returning from service "+msgStep+" - (exceedCutoffTime :" + exceedCutoffTime + ")");
    	return variableMap;
    }
    
    
  
	@Transactional(readOnly = true)
	public void alertNotification(Map<String, Object> variableMap)
    {

		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

    	msgStep = "ScheduledVestingContractDataVerification.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId + " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
			
			String content1 = "Vesting Contract Data for Settlement Date " + utilityFunctions.getddMMyyyy(settlementDate) + " are not available";
			String content2 = " at timing " + utilityFunctions.geHHmm(new Date())  + "h.";


		    // BPM Log
		    logger.info(logPrefix + "" +  content1 + content2);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = content1;
		    alert.content = content1 + content2;
		    alert.noticeType = "Vesting Contract Data Verification";

		    notificationImpl.sendEmail(alert);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", "Verify Vesting Contract", content1 + content2, "");
		}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract Data Verification : "+e.getMessage(), msgStep);
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {


		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		msgStep = "ScheduledVestingContractDataVerification.updateEvent()";
		logger.info("Input parameters ScheduledVestingContractDataVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract Data Verification : "+e.getMessage(), msgStep);
		}

	}
}
