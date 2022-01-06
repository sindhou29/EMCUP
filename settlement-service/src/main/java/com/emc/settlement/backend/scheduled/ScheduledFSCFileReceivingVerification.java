/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.pojo.AlertNotification;
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
public class ScheduledFSCFileReceivingVerification {

	/**
	 * 
	 */
	public ScheduledFSCFileReceivingVerification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledFSCFileReceivingVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-FSCRV]";
 
    @Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap)
    {

		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "scheduledFSCFileReceivingVerification.createEvent()";
    	try {
    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    // Set Cut-off Time
    	    settlementDate = utilityFunctions.getSettlementDate(new Date(),5);

    	   logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Forwad Sales File Recv Verify");
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("settlementDate", settlementDate);
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> checkEBTEvent(Map<String, Object> variableMap)
    {

		Boolean valid = false;

		String msgStep = "scheduledFSCFileReceivingVerification.checkEBTEvent()";

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		String sqlCommand = "SELECT 1 RESULT FROM NEM.NEM_EBT_EVENTS A INNER JOIN NEM.NEM_SETTLEMENT_RAW_FILES B" +
				" ON A.ID=B.EBE_ID WHERE UPLOADED_DATE > ? AND EVENT_TYPE in ('FSR','FSM') " +
				" and exists (select 1 from nem.nem_fsc_contracts " +
				" where  settlement_date = TRUNC (SYSDATE, 'Q') and rownum = 1) " +
				" AND VALID_YN = 'Y' AND ROWNUM = 1 ";

		int num = 0;


		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(utilityFunctions.addDays(new Date(), -30)));
		for(Map recordMap : resultList) {
			num = (int) recordMap.get("RESULT");
		}

		if (num == 0) {
			logger.info(logPrefix + "Forward Sales Contract File is not available.");
		}
		else {
			logger.info(logPrefix + "Forward Sales Contract File is available.");
			valid = true;
		}

    	variableMap.put("valid", valid);
    	return variableMap;
    }
    
    
  
	@Transactional
	public void alertNotification(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");

    	String msgStep = "scheduledFSCFileReceivingVerification.alertNotification()";
		try {
			logger.info(logPrefix + " Starting Activity: " + msgStep);
		    
			String sqlCommand = " SELECT 'Quarter '||TO_CHAR(SYSDATE, 'Q')||' '|| TO_CHAR(SYSDATE, 'RRRR') STR FROM DUAL ";
		    String quarterString = "";

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(utilityFunctions.addDays(new Date(), -30)));
			for(Map recordMap : resultList) {
				quarterString = (String) recordMap.get("STR");
				break;
			}

    	    String msgSubject = "Forward Sales Contract File for " + quarterString + " is not available";

    	    String msgLog = "Forward Sales Contract File for " + quarterString + " is not available at timing " + utilityFunctions.geHHmm(new Date()) ;

    	    String msg = "This is an added email notification service provided by EMC." + 
    	    "\n" + 
    	    "\nBased on the Market Operations Market Manual (Settlements), MSSL counterparty shall, up to seven business days before and by 5:00pm on the fifth business day after the dispatch day, " + 
    	    "provide the EMC with forward sales contract quantities and prices (including any revisions to such forward sales contract quantities and prices) in accordance " +
    	    "with section 2.5.3 or 2.5.5, Chapter 7 of the market rules.    " + 
    	    "This forward sales contract file for " + quarterString + " is not available at timing 4:00pm." + 
    	    "\n" + 
    	    "\nFor your necessary actions please." + 
    	    "\n" + 
    	    "\n" + 
    	    "\n" + utilityFunctions.getSysParamVarChar("HIGH_USEP_PRICE_EMAIL_FOOTER");

    	    
		    // BPM Log
		    logger.info(logPrefix + "" + msgLog);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "Forward Sales Contract File Receiving Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    
		 // alert.subject = "Forward Sales Contract File is not received via EBT"     
		    alert.subject = msgSubject;

		    // alert.content = msg + " at timing " + 'now'.format("HH:mm")         
		    alert.content = msg;

		    alert.noticeType = "Forward Sales Contract File Receiving Verification";

			//Alert disabled as no email required for FSC File verification
		    //alert.sendSchAlertEmailIncExtParty(processName : process.name, p_sendEmailAlert2ExternalParty : SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY, eveId : eveId);

		    // Log JAM Message
		    

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", msgStep, msgLog, "");
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    }
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters scheduledFSCFileReceivingVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on scheduledFSCFileReceivingVerification.updateEvent(): " + e.getMessage());
		}

	}
}
