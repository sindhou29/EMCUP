/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.invoice.services.bc.am.common.InvInternalServices;
import com.emc.invoice.services.bc.am.common.InvInternalServices_Service;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.DateRange;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

/**
 * @author DWTN1561
 *
 */

@Service
public class SettlementMainProcess {

	/**
	 * 
	 */
	public SettlementMainProcess() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(SettlementMainProcess.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	PavPackageImpl pavPackageImpl;
	@Autowired
	SetPackageAuthorization setPackageAuthorization;
    @Autowired
	private JdbcTemplate jdbcTemplate;

    String logPrefix ="[EMC]";
    String msgStep = "";
    
    @Transactional
    public Map<String, Object>  initializeVariables(Map<String, Object> variableMap)
	{

		String soapServiceUrl  = (String)variableMap.get("soapServiceUrl");
		try{
			logPrefix ="[EMC]";
		    logger.log(Priority.INFO,logPrefix + " =========== Settlement Run (Main) Start ==========");

		    msgStep =  "SettlementMainProcess.initializeVariables()";
		    
		    logger.info("Starting Activity "+msgStep);

		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    soapServiceUrl = propertiesMap.get("soapServiceUrl");
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new SettlementRunException("Abnormal Termination of Settlement Main Process: (initializeVariables)", msgStep);
		}
		logger.info("Returning from service "+msgStep+" - soapServiceUrl : "+soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("isSchedule", false);

		return variableMap;
		
	}
    
    @Transactional
    public Map<String, Object>  createScheduleEvent(Map<String, Object> variableMap)
	{

		Date startTime = (Date)variableMap.get("startTime"); 
		String comments = (String)variableMap.get("comments");
		Boolean ignoreScheduledRunCheck = (Boolean)variableMap.get("ignoreScheduledRunCheck");
		Boolean invokedBy = (Boolean)variableMap.get("invokedBy");
		String nemsControllerEveId = (String)variableMap.get("nemsControllerEveId");
		String nemsControllerSchEveId = (String)variableMap.get("nemsControllerSchEveId");
		Date runDate = (Date)variableMap.get("runDate");
		String runFrom = (String)variableMap.get("runFrom");
		Date settlementDate = (Date)variableMap.get("settlementDate"); 
		String settlementType = (String)variableMap.get("settlementType");
		Boolean testRun = (Boolean)variableMap.get("testRun");
		String username = (String)variableMap.get("username");
		String logPrefix = (String)variableMap.get("logPrefix");
		String mainEventId = (String)variableMap.get("mainEventId");
		String schEventId = (String)variableMap.get("schEventId");
		String startMsg = (String)variableMap.get("startMsg");
		PeriodNumber pd = (PeriodNumber)variableMap.get("pd");
		boolean mceDataLoadByController = false;
		try{
			logPrefix ="[EMC]";
//		    logger.log(Priority.INFO,logPrefix + " =========== Settlement Run (Main) Start ==========");

		    msgStep =  "SettlementMainProcess.createScheduleEvent()";
		    String msgText;
		    String description;
		    

		    logger.log(Priority.INFO,logPrefix + " Starting Activity: " + msgStep + " ...");

		    if (runDate == null) {
		        runDate = new Date();
		    }

		    startTime = new Date();

		    logger.log(Priority.INFO,logPrefix + " Parameter - comments: " + comments);

		    logger.log(Priority.INFO,logPrefix + " Parameter - runDate: " + utilityFunctions.getddMMMyyyy(runDate));

		    logger.log(Priority.INFO,logPrefix + " Parameter - runFrom: " + runFrom);

		    if (settlementDate != null) {
		        logger.log(Priority.INFO,logPrefix + " Parameter - settlementDate: " + utilityFunctions.getddMMMyyyy(settlementDate));
		    }
		    else {
		        logger.log(Priority.INFO,logPrefix + " Parameter - settlementDate: <null>");
		    }

		    logger.log(Priority.INFO,logPrefix + " Parameter - settlementType: " + settlementType);

		    logger.log(Priority.INFO,logPrefix + " Parameter - testRun: " + testRun);

		    logger.log(Priority.INFO,logPrefix + " Parameter - username: " + username);
		    
		    logger.log(Priority.INFO,logPrefix + "ignoreScheduledRunCheck: " +ignoreScheduledRunCheck);
		    
		    logger.log(Priority.INFO,logPrefix + " Parameter - invokedBy: " + invokedBy);
		    
		    if(invokedBy == null) invokedBy = false;
		        logger.log(Priority.INFO,logPrefix + "runFrom: " +runFrom);
		    
		    
		    logger.log(Priority.INFO,logPrefix + "Nems Controller Id: " +nemsControllerEveId);
		    
		    logger.log(Priority.INFO,logPrefix + "Nems Controller Schedule event Id: " +nemsControllerSchEveId);
		    

		    String srEvtId = null;
		    
		    if(ignoreScheduledRunCheck == null) ignoreScheduledRunCheck = false;
		    if(invokedBy == true) {
		    	mceDataLoadByController = true;
		    }
	    	logger.log(Priority.INFO,logPrefix + " mceDataLoadByController : " +mceDataLoadByController);

		    // Get Event Type Id for "SETTLEMENT RUN"
		    String sqlCommand = "SELECT ID FROM NEM.JAM_EVENT_TYPES WHERE NAME = 'SETTLEMENT RUN'";

			srEvtId = jdbcTemplate.queryForObject(sqlCommand, new Object[] {}, String.class);

		    if (runFrom.equalsIgnoreCase("B") && !invokedBy) {  //DRCAP PHASE2
		        // Check if there is a scheduled event started
		        sqlCommand = "SELECT ID FROM NEM.JAM_EVENT_SCHEDULES WHERE FREQUENCY = 'D' AND " + 
		                     "EVT_ID = ? AND TRUNC(START_DATE) = ? AND ACTIVE = 'Y'";

		        int rowcnt = 0;

				Object[] params = new Object[2];
				params[0] =  srEvtId;
				params[1] =  utilityFunctions.convertUDateToSDate(runDate);
				
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list) {
					rowcnt = rowcnt + 1;
				}
				
		        if (rowcnt > 0) {
		            msgText = "A Scheduled Settlement Run for run date: " + utilityFunctions.getddMMMyyyy(runDate) + 
		                      " has started, system will not start a Scheduled Run again.";

		            logger.log(Priority.WARN,logPrefix + " " + msgText);

		            throw new SettlementRunException(msgText, msgStep);
		        }
		    }

		   

			if ((runFrom.equalsIgnoreCase("B") && !invokedBy) || (runFrom.equalsIgnoreCase("F") && !invokedBy) )  //DRCAP PHASE2
			{
			
				 //Changed for anonymous transaction
			 // Creating Schedule Event        
		    //schEventId = utilityFunctions.getEveId();
		   
		    //mainEventId = utilityFunctions.getEveId();
		    
		    // Create JAM_EVENT_SCHEDULES record
		  //Changed for anonymous transaction
			schEventId = utilityFunctions.createEventSchedules(runFrom, runDate, settlementDate, settlementType, username, srEvtId);
		   
		    // Create JAM_EVENTS record
			 //Changed for anonymous transaction
		    //sqlCommand = "INSERT INTO NEM.JAM_EVENTS (id, eve_type, start_date, completed, esd_id) " + 
		    //             "VALUES ('" + mainEventId + "','SDE',SYSDATE,'N','" + schEventId + "')";

			//jdbcTemplate.update(sqlCommand, new Object[] {});
			
			mainEventId = utilityFunctions.createJAMEvent("SDE", "N", schEventId);
			
		    logger.log(Priority.INFO,logPrefix + " Event Schedule Id : " + schEventId);

		    logger.log(Priority.INFO,logPrefix + " Main Event Id: " + mainEventId);

		    if (runFrom.equalsIgnoreCase("B")) {
		        // Backend
		        startMsg = "Daily Processing started on " + utilityFunctions.getddMMMyyyyhhmmss(runDate);

		        logger.log(Priority.INFO,logPrefix + " Run from: Backend");

		        description = "SettlementMainProcess" + runFrom + utilityFunctions.getddMMMyyyyhhmmss(runDate);
		    }
		    else {
		        runFrom = "F";
		        startMsg = "Manual Settlement Run started on date: " + utilityFunctions.getddMMMyyyyhhmmss(runDate) + 
		        " for settlement date: " + utilityFunctions.getddMMMyyyy(settlementDate);

		        logger.log(Priority.INFO,logPrefix + " Run from: Frontend. Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementDate));

		        description = "SettlementMainProcess" + runFrom + utilityFunctions.getddMMMyyyy(settlementDate);
		    }

		    logger.log(Priority.INFO,logPrefix + " Run Type: " + settlementType);

		    logger.log(Priority.INFO,logPrefix + " Run Date: " +   utilityFunctions.getddMMMyyyyhhmmss(runDate));

		    // Log JAM Messages
		    utilityFunctions.logJAMMessage( mainEventId, "I","Settlement Main Process", 
		                                   startMsg, "");
		}else
		{

			mainEventId =nemsControllerEveId;
			schEventId=nemsControllerSchEveId;
			
			// Backend
		        startMsg = "Daily Processing started on " + utilityFunctions.getddMMMyyyyhhmmss(runDate);

		        logger.log(Priority.INFO,logPrefix + " Run from: Backend");
			// Log JAM Messages
		    utilityFunctions.logJAMMessage( mainEventId, "I","Settlement Main Process", 
		                                   startMsg, "");
		}
		    // Get Period Numbers
		    pd.total = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
		    pd.sum = ((int) utilityFunctions.getSysParamNum("SUM_OF_PERIODS"));
		    pd.sum2 = ((int) utilityFunctions.getSysParamNum("SUM_SQUARE_OF_PERIODS"));
		    pd.avg3 = ((int) utilityFunctions.getSysParamNum("AVG_CUBIC_OF_PERIODS"));
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new SettlementRunException("Abnormal Termination of Settlement Run Process: (Create Schedule Event)" + runFrom, msgStep);
		}
		logger.info("Returning from service "+msgStep+" - startTime : "+startTime+" schEventId : "+schEventId+" mainEventId : "+mainEventId+" pd : "+pd);
		variableMap.put("startTime", startTime);
		variableMap.put("schEventId", schEventId);
		variableMap.put("mainEventId", mainEventId);
		variableMap.put("pd", pd);
		variableMap.put("ignoreScheduledRunCheck", ignoreScheduledRunCheck);
		variableMap.put("invokedBy", invokedBy);
		variableMap.put("runDate", runDate);
		variableMap.put("mceDataLoadByController", mceDataLoadByController);
		return variableMap;
		
	}
    
    @Transactional
    public void startDailyPrudentialProcess(Map<String, Object> variableMap)
	{

		Date runDate = (Date)variableMap.get("runDate");
		try{
		    msgStep =  "SettlementMainProcess.startDailyPrudentialProcess()" ;

		    logger.log(Priority.INFO,"[EMC] Starting Activity: " + msgStep + " ...");

		    String sqlCmd = "select count(*) from sebo.seb_manual_process_instance a, " + 
		    "sebo.seb_manual_process b where a.MANUAL_PROCESS_ID = b.ID and " + 
		    "b.NAME = 'Prudential Risk Assessment Process' " + 
		    "and trunc(a.START_TIME) = trunc(sysdate)";
		    int count = 0;

			count = jdbcTemplate.queryForObject(sqlCmd, new Object[] {}, Integer.class);
		    if (count == 0) {
		        // Daily Prudential Risk Assessment Process is not running, run it
		        DateRange pRunDates = utilityFunctions.getSettlementDateRange(new Date(),  "P", false);
		        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		        Date settDate = pRunDates.startDate;
		        List<String> settDates = new ArrayList<String>();
		        settDates.add(sdf.format(settDate));

		        while (pRunDates.endDate.compareTo(settDate) > 0) {
					settDate = utilityFunctions.addDays(settDate, 1);
		            settDates.add(sdf.format(settDate));
		        }

		        String sqlCommand = "select sr.id " + 
		        "from nem_settlement_runs sr, jam_events eve " + 
		        "where sr.eve_id = eve.id and eve.COMPLETED = 'Y' and eve.SUCCESS = 'Y' " + 
		        "and trunc(sr.RUN_DATE) = trunc(?) and sr.run_type = 'F' " + 
		        "order by sr.settlement_date ";

		        String fRunStrId = null;

				//Object[] params = new Object[1];
				//params[0] =  utilityFunctions.convertUDateToSDate(runDate);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(runDate));
				for (Map row : list) {
					fRunStrId = (String) row.get("ID");
					break;
				}
				
		        if (fRunStrId != null) {
		            Map<String, Object> args = new HashMap<String, Object>();
		            args.put("settDatesArg", settDates);
		            args.put("strIdArg" , fRunStrId);
		            args.put("runDateArg", runDate);
		            //commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
		            //Instance.create(processId : "/MonitorDailyPrudentialAssessment", arguments : args, 
		            //                argumentsSetName : "BeginIn");

		            logger.log(Priority.INFO,"[EMC] Starting Daily Prudential Risk Assessment Process.");
		        }
		        else {
		            logger.log(Priority.WARN,"[EMC] No F-Run found in today's run, cannot start Daily Perform Productial Assessment Process.");
		        }
		    }
		    else {
		        logger.log(Priority.INFO,"[EMC] There is a Prudential Risk Assessment Process running, " + 
		        "will not start another instance.");
		    }
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		    
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		
	}
    
    @Transactional
    public void updateEventStatus(Map<String, Object> variableMap)
	{
		String mainEventId = (String)variableMap.get("mainEventId");
		String schEventId = (String)variableMap.get("schEventId");
		SettlementRunParams settlementParam = (SettlementRunParams)variableMap.get("settlementParam");
		Date startTime = (Date)variableMap.get("startTime");
		Date endTime = (Date)variableMap.get("endTime");
		Date runDate  = (Date)variableMap.get("runDate");
		msgStep = "SettlementMainProcess.updateEventStatus()";
		try {
			logger.info("[EMC] Starting Activity: SettlementMainProcess.updateEventStatus() ... ");

			// Update JAM Event Schedules, set ACTIVE = 'N'
			String sqlCommand = "update nem.jam_event_schedules set active = 'N' " + "where ID = '" + schEventId + "'";

			jdbcTemplate.update(sqlCommand, new Object[] {});
			// Update JAM Event
			utilityFunctions.updateJAMEvent(true, mainEventId);

			logger.info(" Run id " + settlementParam.runId);

			String runIdInvoice = utilityFunctions.getLatestRunId(settlementParam.getSettlementDate(),
					settlementParam.getRunType());

			String sqlCommandEveId = "SELECT EVE_ID FROM NEM.NEM_SETTLEMENT_RUNS " + " WHERE ID = '" + runIdInvoice
					+ "' ";

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommandEveId, new Object[] {});
			for (Map row : list) {
				settlementParam.runEveId = (String) row.get("EVE_ID");
				break;
			}

			logger.info(" Run eve id invoice last " + settlementParam.runEveId);

			String sqlCommandInvoice = "UPDATE NEM.NEM_CONT_SYSTEM_RUN_STATUS " + " SET STATUS = 'COMPLETED', "
					+ " END_TIME = SYSDATE WHERE EVE_ID = '" + settlementParam.runEveId + "' ";

			jdbcTemplate.update(sqlCommandInvoice, new Object[] {});
			endTime = new Date();

			logger.info(" Settlement Run (Main), Start Time: " + utilityFunctions.getddMMMyyyyhhmmss(startTime)
					+ ", End Time: " + utilityFunctions.getddMMMyyyyhhmmss(endTime) + ". Process Last: "
					+ getDifference(startTime, endTime));

			// Log JAM Message
			utilityFunctions.logJAMMessage(mainEventId, "I", "SettlementMainProcess",
					"Settlement Run (Main) Takes: " + getDifference(startTime, endTime), "");

			// Log JAM Message
			utilityFunctions.logJAMMessage(mainEventId, "I", "SettlementMainProcess",
					"Settlement Run (Main) Finished Successfully", "");

			// BPM Log
			logger.info(" =========== Settlement Run (Main) Finished Successfully ==========");

		} catch (Exception e) {
			logger.warn("[EMC] Failed to update JAM_EVENTS table.");

			throw new SettlementRunException("Abnormal Termination of Settlement Run Process: "+e.getMessage(), msgStep);
		}
	}
    
    public String getDifference(Date startDate, Date endDate){
		
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();
		
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		
		long elapsedSeconds = different / secondsInMilli;
		
		logger.log(Priority.INFO, 
		    "Elapsed days :"+elapsedDays+" hours : "+elapsedHours+" minutes : "+elapsedMinutes+"seconds :"+elapsedSeconds);
	
		return (elapsedDays +" days, "+elapsedHours+ " hours,"+elapsedMinutes+"minutes, "+elapsedSeconds+" seconds");
	}
    
    @Transactional
	public void generateADFFile(Map<String, Object> variableMap)
	{
		String settRunId = (String)variableMap.get("settRunId"); 
		Date settlementDate = (Date)variableMap.get("settlementDate");
		
		logPrefix = "[EMC] ";
		msgStep = "FinalAuthoriseSettlementRun.generateADFFile()";
		logger.log(Priority.INFO, logPrefix + " Starting Activity: " + msgStep + " runId : "+settRunId);

		try {
			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			String invrptservicesURL = propertiesMap.get("invrptservicesURL");
			URL url = new URL(invrptservicesURL + "/invrptservices/InvInternalServices?WSDL");
			logger.log(Priority.INFO, logPrefix + "<" + msgStep + "> URL: " + url);

			InvInternalServices_Service service = new InvInternalServices_Service();
			InvInternalServices invInternalServicesSoapHttpPort = service.getInvInternalServicesSoapHttpPort();
			invInternalServicesSoapHttpPort.generateADFFile(utilityFunctions.toXMLGregorianCalendar(settlementDate),
					settRunId, "STL", false);
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + "<" + msgStep + "> exception: " + e.getMessage());
			throw new SettlementRunException(e.getMessage(), msgStep);
		} 

	}
    
    @Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception");
		try {
			// BPM Log
			logger.log(Priority.ERROR, "[EMC] Settlement Main Exception Occurred!!! " + exception.toString());

			if (settlementParam != null && settlementParam.getSchEveId() != null) {
				// Update JAM Event Schedules, set ACTIVE = 'N'
				String sqlCommand = "update nem.jam_event_schedules set active = 'N' " + "where ID = '"
						+ settlementParam.getSchEveId() + "'";
				jdbcTemplate.update(sqlCommand, new Object[] {});
			}

			if (settlementParam != null && settlementParam.getMainEveId() != null) {
				// Update JAM_EVENTS table
				utilityFunctions.updateJAMEvent(false, settlementParam.getMainEveId());

				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.getMainEveId(), "E", "Settlement Main Process",
						"Abnormal termination of Settlement Run.", "");
			}

		}catch(Exception e) {
    		e.printStackTrace();
    	}
	}

}
