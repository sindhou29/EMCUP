/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.emc.settlement.model.backend.constants.BusinessParameters;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.DateRange;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

/**
 * @author DWTN1561
 *
 */
@Service
public class ScheduledBilateralDataVerification {

	/**
	 * 
	 */
	public ScheduledBilateralDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledBilateralDataVerification.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {
		String logPrefix = "[SH-BCDV] ";
		String msgStep = "ScheduledBilateralDataVerification.checkBusinessDay()";
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
		String logPrefix = "[SH-BCDV] ";
		String msgStep = "ScheduledBilateralDataVerification.initializeVariables()";
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
			throw new Exception("Abnormal Termination of Scheduled Bilateral Data Verification : (initializeVariables)");
		}
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - ( soapServiceUrl :" + soapServiceUrl
				+ " settlementParam.getRunType() :" + settlementParam.getRunType()
				+ " settlementParam.getRunDate() :" + settlementParam.getRunDate()
				+ " settlementParam :" + settlementParam + ")");
		return variableMap;

	}

	public void check() throws Exception {
		String splex_mode = utilityFunctions.getShareplexMode();

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		
		if (propertiesMap.get("ALLOW_SCHEDULED_TASK").equalsIgnoreCase("Y") && !splex_mode.equalsIgnoreCase("Y")) {
		    logger.info("[EMC] Starting Scheduled Task: <Scheduled - Bilateral Data Verification> on " + utilityFunctions.getddMMMyyyyhhmmss(new Date()));
		    Date today = new Date();
			Date nextDay = utilityFunctions.addDays(today, 1);

		
		    if (utilityFunctions.isBusinessDay(nextDay)) {
		        DateRange settDates = utilityFunctions.getSettlementDateRange(nextDay,  "P", false);
		        Date settDate = settDates.startDate;
		    }
		}
	}
	
	@Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap) {

		String logPrefix = "[SH-BCDV] ";
		String msgStep = "ScheduledBilateralDataVerification.initializeVariables()";
		Date settlementDate = (Date)variableMap.get("settlementDate");
		String eveId = (String) variableMap.get("eveId");
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		PeriodNumber pd = (PeriodNumber)variableMap.get("pd");
		Date cutoffTime = null;
		Integer pollInterval = null;
		SettlementRunParams settlementParam = new SettlementRunParams();
		settlementParam.setSettlementDate(settlementDate);
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( cutoffTime : " + cutoffTime + " eveId : " + eveId
				+ " pd : " + pd
				+ " soapServiceUrl : " + soapServiceUrl
				+ " pollInterval : " + pollInterval
				+ " settlementDate : " + settlementDate
				+ " settlementParam : " + settlementParam + ")");
		try {
			logger.log(Priority.INFO,
					logPrefix + "Starting Activity: ScheduledBilateralDataVerification.createEvent()");

			// Set Cut-off Time
			cutoffTime = utilityFunctions.addDays(utilityFunctions.truncateTime(new Date()), 1);

			// Set Poll Interval
			pollInterval = 0;
			pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");
			
			
			logger.log(Priority.INFO, logPrefix + "Settlement Date: "
					+ utilityFunctions.getddMMMyyyy(settlementDate));

			// Create JAM_EVENTS
			eveId = utilityFunctions.createJAMEvent("EXE", "Bilateral Data Verification");
			settlementParam.setRunEveId(eveId);

			// Get Period Number
			pd.total = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));

		} catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix
					+ "Exception occured on ScheduledBilateralDataVerification.createEvent(): " + e.getMessage());
		}

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		soapServiceUrl = propertiesMap.get("soapServiceUrl");

		/*logger.info("Returning from service = cutoffTime :" + cutoffTime + " pollInterval :" + pollInterval
				+ " settlementDate :" + settlementDate + " eveId :" + eveId + " pd :" + pd.toString());*/
		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("pd", pd);
		variableMap.put("eveId", eveId);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - ( eveId :" + eveId + " cutoffTime :" + cutoffTime
				+ " pollInterval :" + pollInterval
				+ " pd :" + pd
				+ " settlementDate :" + settlementDate
				+ " soapServiceUrl :" + soapServiceUrl
				+ " settlementParam :" + settlementParam + ")");
		return variableMap;
	}
	
	@Transactional
	public void updateEvent(Map<String, Object> variableMap) {
		String eveId = (String) variableMap.get("eveId");
		boolean valid = (Boolean) variableMap.get("valid");
		String logPrefix = "[SH-BCDV] ";
		logger.info("Input parameters updateEvent - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix
					+ "Exception occured on ScheduledBilateralDataVerification.updateEvent(): " + e.getMessage());
		}

	}
	
	@Transactional
	public void sendAlertEmail(Map<String, Object> variableMap) {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date)variableMap.get("settlementDate");
		logger.info("Input parameters sendAlertEmail - settlementDate :" + settlementDate + " eveId :" + eveId);
		String logPrefix = "[SH-BCDV] ";
		try {
			String msg = "Bilateral Contract Data for Settlement Date "
					+ new SimpleDateFormat(UtilityFunctions.getProperty("DATE_FORMAT")).format(settlementDate) + " is not available";

			// BPM Log
			logger.log(Priority.ERROR, logPrefix + msg);

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			// Send Alert Email
			AlertNotification alert = new AlertNotification();
			alert.businessModule = "Bilateral Contract Data Verification";
			alert.content = msg + " at timing " + new SimpleDateFormat("HH:mm").format(new Date());
			alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
			alert.subject = msg;
			alert.noticeType = "Bilateral Contract Data Verification";
			notificationImpl.sendEmail(alert);

			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId, "E", "ScheduledBilateralDataVerification.sendAlertEmail()", msg, "");
		} catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix
					+ "Exception occured on ScheduledBilateralDataVerification.updateEvent(): " + e.getMessage());
		}

	}

}
