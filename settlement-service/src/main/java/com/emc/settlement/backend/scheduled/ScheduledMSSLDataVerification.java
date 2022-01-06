/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
public class ScheduledMSSLDataVerification {

	/**
	 * 
	 */
	public ScheduledMSSLDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledMSSLDataVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private PavPackageImpl pavPackageImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-MSDV] ";
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = "ScheduledMSSLDataVerification.checkBusinessDay()";
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
		String msgStep = "ScheduledMSSLDataVerification.initializeVariables()";
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
					"Abnormal Termination of Scheduled MSSL Data Verification : (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl
				+ " settlementParam.getRunDate() :" + settlementParam.getRunDate()
				+ " settlementParam.getRunType() :" + settlementParam.getRunType() + ")");
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		return variableMap;

	}
 
    @Transactional
	public Map<String, Object> prepareData(Map<String, Object> variableMap)
    {
		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		String eveId = (String) variableMap.get("eveId");
		String msslQuantityVersion = (String) variableMap.get("msslQuantityVersion");
		PeriodNumber pd = (PeriodNumber) variableMap.get("pd");
		Integer pollInterval = 0;
		Date settlementDate = (Date) variableMap.get("settlementDate");
		String standingVersion = (String) variableMap.get("standingVersion");
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		SettRunPkg settRunPackage = (SettRunPkg) variableMap.get("settRunPackage");

		String msgStep = "ScheduledMSSLDataVerification.prepareData()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId
				+ " msslQuantityVersion : " + msslQuantityVersion
				+ " pd : " + pd + " soapServiceUrl : " + soapServiceUrl + " pollInterval : " + pollInterval
				+ " standingVersion : " + standingVersion
				+ " soapServiceUrl : " + soapServiceUrl
				+ " settlementDate : " + settlementDate
				+ " settRunPackage : " + settRunPackage
				+ " settlementParam : " + settlementParam + ")");
		try {

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

			// Set Cut-off Time
			cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);

			// Set Poll Interval
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "MSSL Data Verification");
    	    
    	 // Get STANDING version
    	    standingVersion = pavPackageImpl.getCurrVersionPkt("STANDING", utilityFunctions.getddMMMyyyyHyphen(settlementDate), null);
    	    
    	    try {
    	        // Get MSSL Quantity Version
    	        msslQuantityVersion = pavPackageImpl.getCurrVersionPkt( "SETTLEMENT_MSSL_QUANTITIES",utilityFunctions.getddMMMyyyyHyphen(settlementDate), null);

    	        logger.info(logPrefix + "MSSL Quantity Version: " + msslQuantityVersion);
    	    }
    	    catch (Exception e) {
    	        logger.info(logPrefix + "No MSSL Quantity Version found for Settlement Date: " + utilityFunctions.getddMMyyyy(settlementDate));

    	        msslQuantityVersion = null;
    	    }
    	 // Get Period Numbers
    	    pd.total = ((int) utilityFunctions.getSysParamNum( "NO_OF_PERIODS"));
    	    pd.sum = ((int) utilityFunctions.getSysParamNum( "SUM_OF_PERIODS"));
    	    pd.sum2 = ((int) utilityFunctions.getSysParamNum( "SUM_SQUARE_OF_PERIODS"));
    	    pd.avg3 = ((int) utilityFunctions.getSysParamNum( "AVG_CUBIC_OF_PERIODS"));
    	    
    	    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    soapServiceUrl = propertiesMap.get("soapServiceUrl");
		    
		    settlementParam.setRunEveId(eveId);
		    settlementParam.setSettlementDate(settlementDate);
		    
		    settRunPackage.setStandingVersion(standingVersion);
		    settRunPackage.setMsslQtyPkgVer(msslQuantityVersion);
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("cutoffTime", cutoffTime);
    	variableMap.put("pollInterval", pollInterval);
    	variableMap.put("pd", pd);
    	variableMap.put("standingVersion", standingVersion);
    	variableMap.put("msslQuantityVersion", msslQuantityVersion);
    	variableMap.put("soapServiceUrl", soapServiceUrl);
    	variableMap.put("settRunPackage", settRunPackage);
    	variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval
				+ " pd :" + pd
				+ " standingVersion :" + standingVersion
				+ " msslQuantityVersion :" + msslQuantityVersion
				+ " settlementDate :" + settlementDate
				+ " soapServiceUrl :" + soapServiceUrl
				+ " settRunPackage :" + settRunPackage
				+ " settlementParam :" + settlementParam + ")");
    	return variableMap;
    }


    
	@Transactional(readOnly = true)
	public void sendAlertEmail(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "ScheduledMSSLDataVerification.sendAlertEmail()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " settlementDate : " + settlementDate + ")");
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);

			 String msg = "Metering Data for Settlement Date " + 
					 utilityFunctions.getddMMyyyy(settlementDate) + " is not available or is not valid";
			
		    // BPM Log
		    logger.info(logPrefix + "" + msg);
		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();

		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "Metering Data Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = msg;
		    alert.content = msg + " at timing " + utilityFunctions.geHHmm(new Date());
		    alert.noticeType = "MSSL Metering Data Verification";

		    notificationImpl.sendEmail(alert);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", "MSSL Metering Data Verification", msg, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		try {
			String eveId = (String) variableMap.get("eveId");
			Boolean result = Boolean.valueOf((String) variableMap.get("result"));

			logger.info("Input parameters ScheduledMSSLDataVerification.updateEvent() - result :" + result + " eveId :" + eveId);
			utilityFunctions.updateJAMEvent(result, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledMSSLDataVerification.updateEvent(): " + e.getMessage());
		}

	}
   	
   	@Transactional(readOnly = true)
   	public Map<String, Object> checkMSSLVersion(Map<String, Object> variableMap) {

		String msslQuantityVersion = (String) variableMap.get("msslQuantityVersion");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		Date cutoffTime = (Date) variableMap.get("cutoffTime");
		Boolean exceedCutoffTime = false;

		logger.log(Priority.INFO, "Input Parameters for checkMSSLVersion  ( msslQuantityVersion : " + msslQuantityVersion
				+ " settlementDate : " + settlementDate + ")");

		try {
			if (msslQuantityVersion == null) {
			    try {
			        // Get MSSL Quantity Version
			        msslQuantityVersion = pavPackageImpl.getCurrVersionPkt("SETTLEMENT_MSSL_QUANTITIES", utilityFunctions.getddMMMyyyyHyphen(settlementDate),null);

			        logger.info(logPrefix + "MSSL Quantity Version: " + msslQuantityVersion);
			    }
			    catch (Exception e) {
			        logger.info(logPrefix + "No MSSL Quantity Version found for Settlement Date: " + 
			        		utilityFunctions.getddMMyyyy(settlementDate));

			        msslQuantityVersion = null;
			    }
			}
			Date now = new Date();
			logger.info(logPrefix + "Settlement Date: " + settlementDate);
			logger.info(logPrefix + "Current Time: "+now);
			logger.info(logPrefix +"Cutoff Time: "+cutoffTime);
			if (now.compareTo(cutoffTime) >= 0) {
				exceedCutoffTime = true;
			} else {
				exceedCutoffTime = false;
			}
			logger.info(logPrefix + "exceedCutoffTime: "+exceedCutoffTime);

		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledMSSLDataVerification.checkMSSLVersion(): " + e.getMessage());
		}

		variableMap.put("msslQuantityVersion", msslQuantityVersion);
		variableMap.put("exceedCutoffTime", exceedCutoffTime);
		logger.info("Returning from service checkMSSLVersion - ( msslQuantityVersion :" + msslQuantityVersion
				+ " settlementDate :" + settlementDate + ")");
		return variableMap;
	}
}
