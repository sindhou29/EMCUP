/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
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
 * @author THC
 *
 */
@Service
public class ScheduledFSCDataVerification {

	/**
	 * 
	 */
	public ScheduledFSCDataVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledFSCDataVerification.class);


	
@Autowired
private UtilityFunctions utilityFunctions;
@Autowired
private AlertNotificationImpl notificationImpl;
@Autowired
private JdbcTemplate jdbcTemplate;

String logPrefix = "[SH-FSCDV]";
 
    @Transactional
	public Map<String, Object> initializeSettlementDate(Map<String, Object> variableMap)
    {
    	Date cutoffTime = (Date)variableMap.get("cutoffTime");
    	Integer pollInterval = (Integer)variableMap.get("pollInterval");
    	Date settlementDate = (Date)variableMap.get("settlementDate");
    	String eveId = (String)variableMap.get("eveId");
    	PeriodNumber pd = (PeriodNumber)variableMap.get("pd");
    	String soapServiceUrl = (String)variableMap.get("soapServiceUrl");
    	//Map<String, Object> retMap = new HashMap<String, Object>();
    	
    	String msgStep = "ScheduledFSCDataVerification.initializeSettlementDate()";
    	try {
    		


    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    // Set Cut-off Time
    	    cutoffTime = utilityFunctions.addDays(new Date(), 1);

    	    // Set Poll Interval
    	    pollInterval = 0;
    	    pollInterval = pollInterval + UtilityFunctions.getIntProperty("POLL_INTERVAL_IN_MINUTE");

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Forward Sales Data Verification");
    	    
    	 // Get Period Numbers
    	    pd.total = ((int) utilityFunctions.getSysParamNum( "NO_OF_PERIODS"));
    	    pd.sum = ((int) utilityFunctions.getSysParamNum( "SUM_OF_PERIODS"));
    	    pd.sum2 = ((int) utilityFunctions.getSysParamNum( "SUM_SQUARE_OF_PERIODS"));
    	    pd.avg3 = ((int) utilityFunctions.getSysParamNum( "AVG_CUBIC_OF_PERIODS"));
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	SettlementRunParams settlementParam = new SettlementRunParams();
    	settlementParam.setSettlementDate(settlementDate);
    	settlementParam.setRunEveId(eveId);
    	settlementParam.setRunType("P");

		Map<String, String> propMap = utilityFunctions.getProperties();
		soapServiceUrl = propMap.get("soapServiceUrl");
    	
		variableMap.put("eveId", eveId);
		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("pd", pd);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("soapServiceUrl", soapServiceUrl);
    	
    	return variableMap;
    }


    
    @Transactional
	public void sendNotification(Map<String, Object> variableMap)
    {	
    	Date settlementDate = (Date)variableMap.get("settlementDate");
    	String eveId = (String)variableMap.get("eveId");
    	String breachAccMsg = (String)variableMap.get("breachAccMsg");
    	
    	String msgStep = "ScheduledFSCDataVerification.sendNotification()";
		try{
			logger.info(logPrefix + " Starting Activity: " + msgStep);
		    
			String content1 = "Forward Sales Contract Data for Settlement Date " + utilityFunctions.getddMMMyyyyhhmmss(settlementDate) + " are not available";
			String content3 = breachAccMsg;
			String content2 = " at timing " + utilityFunctions.geHHmm(new Date()) + "h.";

		    // BPM Log
		    logger.info(logPrefix + "" + content1 + content2);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = content1;
		    alert.content = content1 + content3 + content2;
		    alert.noticeType = "Forward Sales Contract Data Verification";

		    notificationImpl.sendEmail(alert);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", "\"Forward Sales Contract Data Verification\"", content1 + content2, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    }
	
    @Transactional
   	public void updateEvent(Map<String, Object> variableMap)
	{
    	Boolean valid = (Boolean)variableMap.get("valid");
    	String eveId = (String)variableMap.get("eveId");

		logger.info("Input parameters ScheduledFSCDataVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledFSCDataVerification.updateEvent(): " + e.getMessage());
		}

	}
   	
    @Transactional
   	public Map<String, Object> checkCutoffTime(Map<String, Object> variableMap)
   	{
    	Boolean exceedCutoffTime = (Boolean)variableMap.get("exceedCutoffTime");
    	Date cutoffTime = (Date)variableMap.get("cutoffTime");

   		Map<String, Object> retMap = new HashMap<String, Object>();
		logger.info("Input parameters ScheduledFSCDataVerification.checkCutoffTime() - exceedCutoffTime :" + exceedCutoffTime + " cutoffTime :" + cutoffTime);
		try {
			exceedCutoffTime = false;
			Date now = new Date();

			if (now.compareTo(cutoffTime) >= 0) {
			    exceedCutoffTime = true;
			}
			
			logger.info(logPrefix + "Now(" + utilityFunctions.getddMMyyyy(now) + ") exceed CutoffTime(" + 
					utilityFunctions.getddMMMyyyyhhmmss(cutoffTime) + ") ? " + exceedCutoffTime);

		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledFSCDataVerification.checkCutoffTime(): " + e.getMessage());
		}

		variableMap.put("exceedCutoffTime", exceedCutoffTime);
		return variableMap;
	}
}
