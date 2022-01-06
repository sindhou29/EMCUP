/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.rex.model.bc.am.common.RexInternalService;
import com.emc.rex.model.bc.am.common.RexInternalService_Service;
import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.DateRange;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.oracle.xmlns.adf.svc.errors.ServiceException;

/**
 * @author DWTN1561
 *
 */
@Service
public class ScheduledDailyRunSummary {

	/**
	 * 
	 */
	public ScheduledDailyRunSummary() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(ScheduledDailyRunSummary.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private UtilityService utilityService;
	@Autowired
	private AlertNotificationImpl alertNotificationImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-DRS] ";
	
	
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {
		String msgStep = "ScheduledDailyRunSummary.initializeVariables()";
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam : " + settlementParam + ")");
		try {
			logger.info("Starting Activity " + msgStep);
			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			soapServiceUrl = propertiesMap.get("soapServiceUrl");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			settlementParam.setRunDate(cal.getTime());
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new Exception("Abnormal Termination of Scheduled RiskExposure Verification: (initializeVariables)");
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
	public void checkTodayRun(Map<String, Object> variableMap) throws Exception
    {

    	SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
    
    	logger.info("[DRS] Starts New Vertification Process: version 4.1.01");

    	try {
//    	    DateRange newDateRange;
    	    List<Date> startDate = new ArrayList<Date>();
    	    List<Date> endDate = new ArrayList<Date>();
    	    Date getDate;
    	    Date nowDate = new Date();
    	    String[] runType = new String[]{ "P", "F", "R", "S" };
    	    List<String> recordList = new ArrayList<String>();
    	    String body = "Run Date: " + utilityFunctions.getddMMMyyyy(nowDate) + "\n\n" + 
    	    "Settlement Date\tRun Type\tRun Status\t\tStart Time\tEnd Time\tRun Duration\tCompleted\tSuccess\n";
    	    String runStatus;
    	    String dateString;
    	    String enhanceString;
    	    String dbpath = "NEM_DB";
    	    int iCount = 0;
    	    String result="";
    	    
    	    String sqlCommand = "select to_char(start_date, 'hh24:mi:ss') start_date, to_char(end_date, 'hh24:mi:ss') end_date, " + 
    	    "round((end_date - start_date)*24*60, 2) round, completed, success from NEM.nem_settlement_runs str, NEM.jam_events eve " + 
    	    "where str.eve_id = eve.id and run_type = ? and trunc(settlement_date) = trunc(to_date(?, 'dd Mon yyyy')) " + 
    	    "and trunc(run_date) = trunc(to_date(?, 'dd Mon yyyy')) " + 
    	    "order by run_type, settlement_date, start_date";

    	    // Validate whether today is a business day
    	    boolean bizDay = utilityFunctions.isBusinessDay(nowDate);

    	    if (bizDay == true) {
    	        // If yes, loop through the runType[0]...runType[3] array
    	        for (int i = 0; i <= 3; i++) {
    	            // Calculate Settlement Date Range	
    	        	settlementParam.setRunType(runType[i]);
    	        	variableMap = utilityService.getTradingDatesRange(variableMap);
    	        	settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
    	        	startDate.add(settlementParam.getFromSettlementDate());
    	        	endDate.add(settlementParam.getToSettlementDate());
    	            // logMessage "[ 1 ]: runType[" + i + "]=" + runType[i] + ", startDate[" + i + "]=" + startDate[i] + ", endDate[" + i + "]=" + endDate[i] using severity = SEVERE
    	        }

    	        // Compare with database to get the status for each runType
    	        // Save into recordList array (eg YYYYDDMM P C) 
    	        for (int i = 0; i <= 3; i++) {
    	            iCount = 0;
    	            getDate = startDate.get(i);
    	            runStatus = utilityFunctions.getSettRunStatus( getDate, runType[i]);
    	            dateString = utilityFunctions.getddMMMyyyy(getDate);
    	            Object[] params = new Object[3];
    	            params[0] = runType[i];
    	            params[1] = dateString;

    	            // sett date format in 'dd Mon yyyy'	
    	            params[2] = utilityFunctions.getddMMMyyyy(nowDate); 

    	            // run date format in 'dd Mon yyyy'	
    	            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, params);
    	            for(Map recordMap : resultList) {
    	                // start time, end time, run duration, completed, success
    	                enhanceString = null;
    	                BigDecimal d =  (BigDecimal) recordMap.get("round");
    	                d.setScale(2, BigDecimal.ROUND_UP);
    	                enhanceString = (String) recordMap.get("start_date") + "\t" + (String) recordMap.get("end_date") + "\t" + String.valueOf(d)+ "\t" + "minutes\t" + (String) recordMap.get("completed") + "\t\t" + (String) recordMap.get("success");
    	                recordList.add( dateString + "\t\t" + runType[i] + "\t\t'" + runStatus + "'" + "\t" + enhanceString);
    	                
    	                logger.info( dateString + "\t\t" + runType[i] + "\t\t'" + runStatus + "'" + "\t" + enhanceString);
    	            }

    	            // Increment from start date to end date for each runType
    	            while (getDate.compareTo(endDate.get(i)) != 0) {
    	                iCount = iCount + 1;
    	                getDate = utilityFunctions.addDays(startDate.get(i), iCount);

    	                // increment 1 day	
    	                runStatus = utilityFunctions.getSettRunStatus(getDate,runType[i]);
    	                dateString =  utilityFunctions.getddMMMyyyy(getDate);
    	                Object[] params1 = new Object[3];
    	                params1[0] = runType[i];
    	                params1[1] = dateString;

    	                // sett date format in 'dd Mon yyyy'
    	                params1[2] = utilityFunctions.getddMMMyyyy(nowDate); 

    	                // run date format in 'dd Mon yyyy'	
    	                List<Map<String, Object>> resultList1 = jdbcTemplate.queryForList(sqlCommand, params1);
    	                for(Map recordMap : resultList1) {
    	                    // start time, end time, run duration, completed, success
    	                    enhanceString = null;
    	                    BigDecimal d =  (BigDecimal) recordMap.get("round");
        	                d.setScale(2, BigDecimal.ROUND_UP);
    	                    enhanceString = (String) recordMap.get("start_date") + "\t" + (String) recordMap.get("end_date") + "\t" + String.valueOf(d) + "\t" + "minutes\t" + (String) recordMap.get("completed") + "\t\t" + (String) recordMap.get("success");
    	                    recordList.add( dateString + "\t\t" + runType[i] + "\t\t'" + runStatus + "'" + "\t" + enhanceString);
    	                    
    	                    logger.info( dateString + "\t\t" + runType[i] + "\t\t'" + runStatus + "'" + "\t" + enhanceString);
    	                }
    	            }
    	        }

    	        // Loop through recordList. 
    	        // If status is 'C' and 'P' and 'E' and '', set it as Not Triggered
    	        // If status is 'F' and 'A', set it as Triggered
    	        for (String record : recordList) {
    	            if (record.contains("'C'")) {
    	                result = record.replace("'C'",  "Not Triggered");
    	            }
    	            else if (record.contains("'P'")) {
    	                result = record.replace("'P'",  "Not Triggered");
    	            }
    	            else if (record.contains("'A'")) {
    	                result = record.replace("'A'",  "Triggered" + "\t");
    	            }
    	            else if (record.contains("'F'")) {
    	                result = record.replace( "'F'", "Triggered" + "\t");
    	            }
    	            else if (record.contains("'E'")) {
    	                result = record.replace("'E'",  "Not Triggered");
    	            }
    	            else {
    	                result = record.replace( "",  "Not Triggered");
    	            }

    	            body = body + "\n" + result;
    	        }

    	        body = body + "\n\n\n\nThis email is automatically generated at " + utilityFunctions.getddMMMyyyy(nowDate); 

    	        //display(body);
    	        logger.info("body : "+body);

    	        Map<String, String> propertiesMap = UtilityFunctions.getProperties();
    	        // send as Email
    	        AlertNotification alert = new AlertNotification();
    	        alert.recipients = propertiesMap.get("DAILY_RUN_SUMMARY_EMAIL");//DAILY_RUN_SUMMARY_EMAIL;
    	        alert.cc = "";
    	        alert.content = body;
    	        alert.sender = "emcsettlement@emcsg.com";
    	        alert.subject = "Daily Run Summary of " + utilityFunctions.getddMMMyyyy(nowDate);
    	        alert.noticeType = "Settlements Run";
    	        alertNotificationImpl.sendEmail(alert);
    	    }
    	    else {
    	        // BPM Log
    	    	logger.warn("[DRS] Today (" + utilityFunctions.getddMMMyyyy(nowDate) + 
    	        ") is not a Business Day. No Scheduled Settlement Run will performed.");
    	    }
    	}
    	catch (Exception e) {
    		logger.error("Exception : "+e.getMessage());
    	    throw new Exception("[DRS] Abnormal Termination of New Process: " + e.getMessage());
    	}

    }



   
}
