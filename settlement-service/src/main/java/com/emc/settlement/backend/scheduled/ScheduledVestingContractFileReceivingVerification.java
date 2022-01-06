/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.VestingContractException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
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
public class ScheduledVestingContractFileReceivingVerification implements Serializable{

	/**
	 * 
	 */
	public ScheduledVestingContractFileReceivingVerification() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(ScheduledVestingContractFileReceivingVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-VCRV] ";
	String msgStep = "";
 
	@Transactional
	public Map<String, Object>  initializeVariables(Map<String, Object> variableMap) 
	{
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		
		msgStep = "ScheduledVestingContractFileReceivingVerification.initializeVariables()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  (  soapServiceUrl : " + soapServiceUrl
				+ " soapServiceUrl : " + soapServiceUrl + ")");
		try{
		    logger.info("Starting Activity "+msgStep);
		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    soapServiceUrl = propertiesMap.get("soapServiceUrl");
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract File Recieving Verification :"+e.getMessage(), msgStep);
		}
		logger.info("Returning from service "+msgStep+" - soapServiceUrl : "+soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		return variableMap;
	}
	
	public Map<String, Object> checkCount(Map<String, Object> variableMap) {
		
		Integer numCount = (Integer) variableMap.get("numCount");

		msgStep = "ScheduledVestingContractFileReceivingVerification.checkCount()";
		try {

		    // ITSM 15386    
		    // Copied DRCAP-Phase2-RahulRaghu
		    //String sqlCommand = " SELECT COUNT (*) " + 
		    //"   FROM (SELECT  MIN(settlement_date) min_date " + 
		    //"           FROM (SELECT   day_type, settlement_date " + 
		    //"                      FROM nem.nem_settlement_calendar " + 
		    //"                    WHERE day_type = 'B' " + 
		    //"                      AND settlement_date <ADD_MONTHS(TRUNC(SYSDATE , 'Q'),3)  " + 
		    //"                 ORDER BY settlement_date DESC) " + 
		    //"          WHERE ROWNUM < 11) " + 
		    //"   WHERE min_date = TRUNC (SYSDATE) ";
		    
			//DRSAT-692
			String sqlCommand = " SELECT COUNT (*) COUNT FROM "+
			" (select max(settlement_date) alert_date from  nem.nem_settlement_calendar "+
			" where settlement_date <= "+
			" (       "+
			" SELECT  MIN(settlement_date) min_date "+  
		    "	FROM (SELECT   settlement_date   "+
		    "                      FROM nem.nem_settlement_calendar "+  
		    "                    WHERE   "+
		    "                      settlement_date <ADD_MONTHS(TRUNC(SYSDATE , 'Q'),3) "+   
		    "                 ORDER BY settlement_date DESC)  "+
		    "          WHERE ROWNUM < 12    "+
			" ) "+
			" and day_type = 'B') "+ 
		    "   WHERE alert_date = TRUNC (SYSDATE) ";

			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for(Map row : list) {
				numCount = ((BigDecimal)row.get("COUNT")).intValue();
		        break;
		    }
		
		}catch(Exception e) {
			logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract File Recieving Verification :"+e.getMessage(), msgStep);
		}
		variableMap.put("numCount", numCount);
		logger.info("Returning from service "+msgStep+" - (numCount :" + numCount + ")");
		return variableMap;
	}
	
    @Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		Date settlementDate = (Date) variableMap.get("settlementDate");

		String msgStep = "ScheduledVestingContractFileReceivingVerification.createEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementDate : " + settlementDate
				+ "eveId : " + eveId + ")");
    	try {
    	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    	    settlementDate = utilityFunctions.getSettlementDate(new Date(), 5);

    	    logger.info(logPrefix + "Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Vesting File Recv Verify");
    	   
    	}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract File Recieving Verification :"+e.getMessage(), msgStep);
    	}

    	variableMap.put("eveId", eveId);
    	variableMap.put("settlementDate", settlementDate);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId
				+ " settlementDate :" + settlementDate + ")");
    	return variableMap;
    }


    @Transactional(readOnly = true)
	public Map<String, Object> checkEBTEvent(Map<String, Object> variableMap)
    {

		Boolean valid = (Boolean) variableMap.get("valid");

    	String msgStep = "ScheduledVestingContractFileReceivingVerification.checkEBTEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( valid : " + valid + ")");
		try {
			
		    // ITSM 15386
		    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    valid = false;
		    int rowcnt = 0;
		    String sqlCommand = "SELECT 1 FROM NEM.NEM_EBT_EVENTS A INNER JOIN NEM.NEM_SETTLEMENT_RAW_FILES B" + 
		    " ON A.ID=B.EBE_ID WHERE UPLOADED_DATE > ? AND EVENT_TYPE = 'VST' " + 
		    " and exists (select 1 from nem.nem_vesting_contracts " + 
		    " where  settlement_date = TRUNC (SYSDATE, 'Q') and rownum = 1) " + 
		    " AND VALID_YN = 'Y' AND ROWNUM = 1 ";

		    // ITSM 15086
		    int num = 0;

		    /*pstmt = conn.prepareStatement(sqlCommand);
		    pstmt.setDate(1, utilityFunctions.convertUDateToSDate(utilityFunctions.addDays(new Date(), -30)));
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		        num = rs.getInt(1);
		        break;
		    }*/

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(utilityFunctions.addDays(new Date(), -30)));
			for(Map recordMap : resultList) {
		        num = ((BigDecimal) recordMap.get("1")).intValue();
				break;
		    }

		    if (num == 0) {
		        logger.info(logPrefix + "Vesting Contract File is not available.");
		    }
		    else {
		        logger.info(logPrefix + "Vesting Contract File is available.");

		        valid = true;
		    }
		}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract File Recieving Verification :"+e.getMessage(), msgStep);
    	}/*finally {
			if(rs != null)UtilityFunctions.close(rs);
			if(pstmt != null)UtilityFunctions.close(pstmt);
		}*/
    	variableMap.put("valid", valid);
			logger.info("Returning from service "+msgStep+" - ( valid :" + valid + ")");
    	return variableMap;
    }
    
    
  
	@Transactional(readOnly = true)
	public void alertNotification(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");

    	String msgStep = "ScheduledVestingContractFileReceivingVerification.alertNotification()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId + ")");
		try {
			logger.info(logPrefix + " Starting Activity: " + msgStep);
			
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    // ITSM 15386
		    //String sqlCommand = " SELECT 'Quarter '||TO_CHAR(SYSDATE, 'Q')||' '|| TO_CHAR(SYSDATE, 'RRRR') FROM DUAL ";
		    String sqlCommand = " SELECT 'Quarter ['||TO_CHAR(SYSDATE, 'Q')||'-'|| TO_CHAR(SYSDATE, 'RRRR') || ']' VALUE FROM DUAL ";  //DRSAT-692
		    String quarterString = "";

		    /*pstmt = conn.prepareStatement(sqlCommand);
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		        quarterString = rs.getString(1);
		        break;
		    }*/

			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
			for(Map recordMap : resultList) {
		        quarterString = (String) recordMap.get("VALUE");
		        break;
		    }

		    // 	msg as String = "Vesting Contract File is not received via EBT on last business day ("        // ITSM 15386
		    // 		+ 'now'.datePart.format(DISPLAY_DATE_FORMAT) + ") of the month before the next quarter."  // ITSM 15386
		    String msgSubject = "Vesting Contract File for " + quarterString + " is not available";

		    // ITSM 15386
		    String msgLog = "Vesting Contract File for " + quarterString + " is not available at timing " + utilityFunctions.geHHmm(new Date()) ;

		    // ITSM 15386
		    //DRSAT-692	
		    String msg = "This is an added email notification service provided by EMC." + 
		    "\n" + 
		    //"\nBased on the Market Operations Market Manual (Settlements), MSSL counterparty shall, up to seven business days before and by 5:00pm on the fifth business day after the dispatch day, " + 
		    //"provide the EMC with vesting contract quantities and prices (including any revisions to such vesting contract quantities and prices) in accordance with section 2.5.3 or 2.5.5, Chapter 7 of the market rules.    " + 
		    "\nBased on the Market Operations Market Manual (Settlements), MSSL counterparty shall provide the EMC with vesting contract quantities and prices at least 10 calendar days before the dispatch day and any subsequent revision shall be provided by " +
		    "no later than 5:00pm on the fifth business day after the dispatch day in accordance with section 2.5.3 or 2.5.5, Chapter 7 of the market rules. " +
		    "This vesting contract file for " + quarterString + " is not available at timing 4:00pm." + 
		    "\n" + 
		    "\nFor your necessary actions please." + 
		    "\n" + 
		    "\n" + 
		    "\n" + utilityFunctions.getSysParamVarChar("HIGH_USEP_PRICE_EMAIL_FOOTER");

		    // BPM Log
		    // logger.info "[EMC] " + msg using severity = SEVERE       // ITSM 15386
		    logger.info("[EMC] " + msgLog);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    // ITSM 15386
		    AlertNotification alert = new AlertNotification();
		    alert.businessModule = "Vesting Contract File Receiving Verification";
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;

		    // alert.subject = "Vesting Contract File is not received via EBT"     // ITSM 15386
		    alert.subject = msgSubject;

		    // ITSM 15386
		    // alert.content = msg + " at timing " + 'now'.format("HH:mm")         // ITSM 15386
		    alert.content = msg;

		    // ITSM 15386
		    alert.noticeType = "Vesting Contract File Receiving Verification";

		    // ITSM 15386 
		    notificationImpl.sendSchAlertEmailIncExtParty( "ScheduledVestingContractFileReceivingVerification",  UtilityFunctions.getProperty("SEND_EMAIL_ALERT_TO_EXTERNAL_PARTY"), eveId, alert);

		    // sendEmail alert
		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "E", msgStep,msgLog, "");
		}
    	catch (Exception e) {
    		logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new VestingContractException("Abnormal Termination of Scheduled Vesting Contract File Recieving Verification :"+e.getMessage(), msgStep);
    	}

    }
	
   	@Transactional(readOnly = true)
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");
		Boolean valid = (Boolean) variableMap.get("valid");

		logger.info("Input parameters ScheduledVestingContractFileReceivingVerification.updateEvent() - valid :" + valid + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(valid, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + "Exception occured on ScheduledVestingContractFileReceivingVerification.updateEvent(): " + e.getMessage());
		}

	}
}
