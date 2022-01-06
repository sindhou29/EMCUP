/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class RunValidations implements Serializable {

	/**
	 * 
	 */
	public RunValidations() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(RunValidations.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    private SettlementRunProcess settlementRunProcess;

    String logPrefix ="[EMC]";
    String msgStep = "";

    @Transactional
    public void validateSettlementRun(Map<String, Object> variableMap) 
	{

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
		
    	msgStep = "RunValidations.validateSettlementRun()" ;
		try{
			logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

			String sqlCommand;
			int rowcnt = 0;
			String runStatus = null;
			String runType = settlementParam.runType;
			Date settlementDate = settlementParam.settlementDate;
			String runFrom = settlementParam.runFrom;
			String eveId = settlementParam.runEveId;
			String thisRunId = settlementParam.runId;
			
			if(utilityFunctions.isAfterFSCEffectiveStartDate(settlementParam.settlementDate) && 
			utilityFunctions.isBeforeFSCEffectiveEndDate(settlementParam.settlementDate)){
				settlementParam.isFSCEffective = true;
			}else{
				settlementParam.isFSCEffective = false;
			}
			logger.log(Priority.INFO,logPrefix + " Is FSC Effective-->"+settlementParam.isFSCEffective);
			    

			
			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                               "Validating Settlement Run ... ", "");

			String sqlStatusV = "SELECT DECODE (status, 'P', 'In Progress', " + 
			"DECODE (authorised, 'W', 'Waiting Authorisation', '1', '1st Authorised', 'A', 'Authorised')) status " + 
			"FROM NEM.nem_settlement_run_status_v " + 
			"WHERE trunc(settlement_date) = trunc(?) AND run_type = ? AND ( status = 'P' OR authorised IN ('W', 'A', '1') ) " + 
			"AND id <> ? ";

			if (runType.equalsIgnoreCase("F")) {
			    // FINAL RUN VALIDATION
			    // For all Final Runs an Authorised Preliminary Run must exist
			    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                   "Validating: For F-Run, check Authorised P-Run existance", 
			                                   "");

			    logger.log(Priority.INFO,logPrefix + "Validating: For F-Run, check Authorised P-Run existance");

			    // Check whether an authorised prelim run is existing before running a final run
			    rowcnt = 0;
			    sqlCommand = "SELECT 1 FROM NEM.nem_settlement_run_status_v " + 
			                 "WHERE run_type = 'P' " + 
			                 "AND authorised = 'A' " + 
			                 "AND trunc(settlement_date) = trunc(?) ORDER BY settlement_date DESC ";

				//Object[] params = new Object[1];
				//params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementDate));
				for (Map row : list) {
					rowcnt = rowcnt + 1;
				}
			    if (rowcnt == 0) {
			        throw new SettlementRunException( "Settlement Run Terminated - No authorised Preliminary Run exists.", msgStep);
			    }

			    // If manual Run
			    if (runFrom.equalsIgnoreCase("F")) {
			        // Check for a final run that is waiting authorisation, authorised or in progress
			        utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                       "Validating: For F-Run from Frontend, check Waiting or In-process Run", 
			                                       "");

			        logger.log(Priority.INFO,logPrefix + "Validating: For F-Run from Frontend, check Waiting or In-process Run");

			        rowcnt = 0;

					Object[] params1 = new Object[3];
					params1[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					params1[1] =  runType;
					params1[2] =  thisRunId;
					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlStatusV, params1);
					for (Map row : list1) {
						rowcnt = rowcnt + 1;
						runStatus = (String)row.get("status");
					}
			        if (rowcnt > 0) {
			            // IF c_progress_waiting%FOUND THEN
			            throw new SettlementRunException("A final run that has a status of: " + runStatus + " exists.", msgStep);
			        	
			        }

			        if (settlementParam.ignoreScheduledRunCheck == false) {
			            utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                           "Validating: For F-Run from Frontend, check Failed Scheduled F-Run for Settlement Date: " + 
			                                        		   utilityFunctions.getddMMMyyyy(settlementDate), 
			                                           "");

			            logger.log(Priority.INFO,logPrefix + "Validating: For F-Run from Frontend, check Failed Scheduled F-Run for Settlement Date: " + 
			            		utilityFunctions.getddMMMyyyy(settlementDate));

			            // There must exist an errored or cancelled final run
			            // for this date if it is being run manually
			            rowcnt = 0;
			            sqlCommand = "SELECT id FROM NEM.nem_settlement_run_status_v WHERE trunc(settlement_date) = trunc(?) " + 
			                         "AND run_type = ? AND run_from = 'B' AND status IN ('E', 'C')";

						Object[] params2 = new Object[2];
						params2[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
						params2[1] =  runType;
						List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, params2);
						for (Map row : list2) {
							rowcnt = rowcnt + 1;
						}
			            if (rowcnt == 0) {
			                // IF c_cancel_errored%NOTFOUND THEN
			                throw new SettlementRunException( "You cannot start a final run when the scheduled run has not yet being run.", msgStep);
			            }
			        }
			    }
			}

			// if runType = "F"
			// Premliminary Validation
			// Run from foreground validation
			if (runType.equalsIgnoreCase("P") && runFrom.equalsIgnoreCase("F") && settlementParam.ignoreScheduledRunCheck == false) {
			    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                   "Validating: For P-Run from Frontend, check failed Scheduled Run for Settlement Date: " + 
			                                		   utilityFunctions.getddMMMyyyy(settlementDate), 
			                                   "");

			    logger.log(Priority.INFO,logPrefix + "Validating: For P-Run from Frontend, check failed Scheduled Run for Settlement Date: " + 
			    		utilityFunctions.getddMMMyyyy(settlementDate));

			    // Check for a manually created prelim with status or Error/Cancelled
			    // Find a final run that has been cancelled or has errors started in the background
			    rowcnt = 0;

			    sqlCommand = "SELECT id FROM NEM.nem_settlement_run_status_v " + 
			                 "WHERE trunc(settlement_date) = trunc(?) AND run_type = ? AND run_from = 'B' " + 
			                 "AND status IN ('E', 'C')";

				Object[] params = new Object[2];
				params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
				params[1] =  runType;
				List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list2) {
					rowcnt = rowcnt + 1;
				}

			    if (rowcnt == 0) {
			        throw new SettlementRunException("You cannot start a premliminary run when the scheduled run has not yet being run.", msgStep);
			    }

			    // Check for a prelim run that is waiting authorisation, authorised or in progress
			    // Find a final run that is waiting for authorisation or has been authorised or is in progress
			    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                   "Validating: For P-Run from Frontend, check In-process, Waiting or Authorised Run for Settlement Date: " + 
			                                		   utilityFunctions.getddMMMyyyy(settlementDate), 
			                                   "");

			    logger.log(Priority.INFO,logPrefix + "Validating: For P-Run from Frontend, check In-process, Waiting or Authorised Run for Settlement Date: " + 
			    		utilityFunctions.getddMMMyyyy(settlementDate));
			    rowcnt = 0;

				Object[] params1 = new Object[3];
				params1[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
				params1[1] =  runType;
				params1[2] =  thisRunId;
				List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlStatusV, params1);
				for (Map row : list1) {
					rowcnt = rowcnt + 1;
					runStatus = (String)row.get("status");
				}

			    if (rowcnt > 0) {
			        throw new SettlementRunException("A preliminary run that has a status of: " + runStatus + " exists.", msgStep);
			    }
			}

			// 	if runType = "P" and runFrom = "F"
			// Re-run Validation
			// There must be a final run for that same trading date with an execution
			// status of "completed" and an authorisation status of "authorised"
			if (runType.equalsIgnoreCase("R") || runType.equalsIgnoreCase("S")) {
			    String msg = "Validating: For " + (runType.equalsIgnoreCase("R") ? "First" : "Second") + " Re-Run, check existance of Authorised F-Run";

			    // Test that a relevant run type exists for the rerun.
			    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                   msg, "");

			    logger.log(Priority.INFO,logPrefix + msg);

			    rowcnt = 0;
			    sqlCommand = "select count(id) as counter from NEM.nem_settlement_run_status_v " + 
			                 "where run_type = ? and authorised = 'A' and trunc(settlement_date) = trunc(?) ";

				Object[] params = new Object[2];
				params[0] =  "F";
				params[1] =  utilityFunctions.convertUDateToSDate(settlementDate);
				List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list2) {
					rowcnt = ((BigDecimal)row.get("counter")).intValue();
				}
			    if (rowcnt == 0) {
			        throw new SettlementRunException("A settlement re-run requires a completed and authorized final settlement run for the same settlement date as the re-run.", msgStep);
			    }
			}

			// Second Re-run Validation
			// Here we identify what settlement_dates are exempt from needing an authorised r rerun
			Date minExemptDate = utilityFunctions.getSysParamTime("SETT_RERUN_S_NOR_BEGIN");
			Date maxExemptDate = utilityFunctions.getSysParamTime("SETT_RERUN_S_NOR_END");

			if (runType.equalsIgnoreCase("S") && (settlementDate.before(maxExemptDate)  || settlementDate.after(minExemptDate))) {
				String msg = "Validating: For Second Re-Run, because Settlement Date is not within Exemption Date (" + 
						utilityFunctions.getddMMMyyyy(minExemptDate) + " - " + utilityFunctions.getddMMMyyyy(maxExemptDate) + 
			          "), need to check existance of Authorised First Re-Run";
			    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                                   msg, "");

			    logger.log(Priority.INFO,logPrefix + msg);

			    rowcnt = 0;
			    sqlCommand = "select count(id) as counter from NEM.nem_settlement_run_status_v " + 
			                 "where run_type = ? and authorised = 'A' and trunc(settlement_date) = ? ";

				Object[] params = new Object[2];
				params[0] =  "R";
				params[1] =  utilityFunctions.convertUDateToSDate(settlementDate);
				List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, params);
				for (Map row : list2) {
					rowcnt = ((BigDecimal)row.get("counter")).intValue();
				}
			    if (rowcnt == 0) {
			        throw new SettlementRunException("The second settlement re-run requires a completed and authorized initial re-run for the same settlement date.", msgStep);
			    }
			}

			utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
			                               "Settlement Run is Valid", "");

			logger.log(Priority.INFO,logPrefix + "Settlement Run is Valid");

		} catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
			throw new SettlementRunException(e.getMessage(), msgStep);
		}	
	}
    
    @Transactional
    public void notValid(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception"); 

		try {

			msgStep = "RunValidations.notValid()";
			logger.log(Priority.WARN, logPrefix + "Settlement Run Validation found error: " + exception.toString());


			// Log Info JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E", msgStep, exception.toString(), "");

			// Log Error JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E", msgStep,
					"Settlement Run Validation found errors.", "");

		}catch(Exception e)
    	{
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
    	}
	}
    
    @Transactional
	public Map<String, Object> forTestRun(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	msgStep = "RunValidations.forTestRun()" ;
		try {
			logger.log(Priority.INFO,logPrefix + "This is a Test Run. Skip Run Validation.");

			if(utilityFunctions.isAfterFSCEffectiveStartDate(settlementParam.settlementDate) && 
				utilityFunctions.isBeforeFSCEffectiveEndDate(settlementParam.settlementDate)){
				settlementParam.isFSCEffective = true;
			}else{
				settlementParam.isFSCEffective = false;
			}
			logger.log(Priority.INFO,logPrefix + " Is FSC Effective-->"+settlementParam.isFSCEffective);
			
		}catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
			throw new SettlementRunException(e.getMessage(), msgStep);
		}	
		variableMap.put("settlementParam", settlementParam);
		
		return variableMap;
	}
    
    
    @Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception");
    	msgStep = "RunValidations.exceptionHandler()" ;
		try {
			// BPM Log
			logger.log(Priority.ERROR,
					logPrefix + "Data validation found errors in Settlement Data." + exception.message);

			// Log Error JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E",
					"IPDataVerificationsAndValidations.exceptionHandler()", exception.message, "");

			// Log Info JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "I",
					"IPDataVerificationsAndValidations.exceptionHandler()",
					"Data validation found errors in Settlement data", "");
			// for WebGUI to capture
		}catch(Exception e)
    	{
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
    	}
				
	}
}
