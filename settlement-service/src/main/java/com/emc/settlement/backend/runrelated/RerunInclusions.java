/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class RerunInclusions implements Serializable{

	/**
	 * 
	 */
	public RerunInclusions() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(RerunInclusions.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;

    String logPrefix ="[EMC]";
    String msgStep = "";
    
    @Transactional
    public void includeRerunsPrelim(Map<String, Object> variableMap)
	{

		SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
		Boolean firstPrelim = (Boolean)variableMap.get("firstPrelim");
		
		// Purpose: Finds all settlement re-runs that need to be included in the
		// priliminary run being executed today.
		// Find any authorised settlement re-runs which have not
		// already been associated with preliminary runs that are
		// either authorised or waiting authorisation.
		try{
			msgStep = "IncludeRerun.includeRerunsPrelim()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");

			String msgText;
			String sqlCommand;
			String sqlCommand1;

			if (settlementParam.runType.equalsIgnoreCase("P") && firstPrelim) {
				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep,
						"Including reruns for the prelimiary run " + settlementParam.runId, "");

				// Include all outstanding reruns into the current run.
				// Find authorised re-runs
				// New rule to only pick up re-runs
				// only where they are not associated with preliminary runs
				sqlCommand = "select rrstat.id, rrstat.run_type, rrstat.settlement_date "
						+ "from NEM.nem_settlement_run_status_v  rrstat "
						+ "where rrstat.run_type in ('R','S') and rrstat.authorised  = 'A' and not exists "
						+ "( select 1 from NEM.nem_settlement_run_status_v prstat, NEM.nem_settlement_rerun_incs princs "
						+ "where prstat.run_type = 'P' and prstat.authorised in ('A','W','1') "
						+ "and princs.str_id = prstat.id and princs.rerun_str_id  = rrstat.id )";
				sqlCommand1 = "insert into NEM.nem_settlement_rerun_incs (str_id, rerun_str_id ) " + "values (?,?)";
				String sqlChkResult = "select count(*) COUNT from nem.nem_settlement_results " + "where str_id = ?";
				int rowcnt = 0;
				int rstCnt = 0;
				String rerunId = "";
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
				for (Map row : list) {
					rerunId = (String)row.get("id");

					// Check if the will-be-included rerun has Result Data or not, if not,
					// it will not be included in the P-Run
					rstCnt = 0;

					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlChkResult, new Object[] {rerunId});
					for (Map row1 : list1) {
						rstCnt = ((BigDecimal)row1.get("COUNT")).intValue();
					}
					if (rstCnt > 0) {
						rowcnt = rowcnt + 1;

						Object[] params = new Object[2];
						params[0] =  settlementParam.runId;
						params[1] =  rerunId;
						jdbcTemplate.update(sqlCommand1,params);
						msgText = "Inserted Re-run: " + rerunId + " for inclusion against Prelim Run: "
								+ settlementParam.runId;

						logger.log(Priority.INFO, logPrefix + msgText);

						utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");

						// For performance issue, limit the maximum number of included reruns
						if (rowcnt >= UtilityFunctions.getIntProperty("MAX_INCLUDE_RERUNS")) {
							// modified part of ITSM 15932
							break;
						}
					} else {
						msgText = "Rerun (ID: " + rerunId
								+ ") has no data in NEM_SETTLEMENT_RESULTS table, will not be included in Prelim Run.";

						logger.log(Priority.WARN, logPrefix + msgText);

						utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");
					}
				}

				msgText = "Total of " + rowcnt + " re-run(s) have been included into P-Run: " + settlementParam.runId;

				logger.log(Priority.INFO, logPrefix + msgText);

				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");
			} else {
				if (settlementParam.runType.equalsIgnoreCase("P") && !firstPrelim) {
					msgText = "The re-runs have already been included in a previous prelim during this processing, the preliminary re-run inclusion step will be skipped.";
				}
				//TODO MURALI - remove else if
				/*else if (settlementParam.runType.equalsIgnoreCase("R") ) {
					settlementRunProcess.updateFailedStatus(settlementParam);
					throw new SettlementRunException("The re-runs have already been included in a previous prelim during this processing, the preliminary re-run inclusion step will be skipped.", msgStep);
				} */
				
				else {
					msgText = "This settlement run is not a preliminary run, the preliminary re-run inclusion step will be skipped.";
				}

				logger.log(Priority.INFO, logPrefix + msgText);

				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");
			}
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
			//settlementRunProcess.updateFailedStatus(settlementParam);
			throw new SettlementRunException(e.getMessage(), msgStep);
		} 
	}
	
    @Transactional
	public void includeRerunsFinal(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
		// Purpose: Finds all settlement re-runs that need to be included in the final
		// run being executed today.
		// Find any settlement re-runs which are associated with the -- authorised
		// preliminary run for the same trading date as
		// the final run
		try{
			msgStep = "IncludeRerun.includeRerunsFinal()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");

			String msgText;
			msgText = "Including reruns with current run (final) ... ";
			String sqlCommand;
			String sqlCommand1;

			if (settlementParam.runType.equalsIgnoreCase("F")) {
				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep,
						"Including reruns for the final run " + settlementParam.runId, "");

				// Find any settlement re-runs which are associated with the
				// authorised preliminary run for the same trading date as
				// the final run

				sqlCommand = "select incp.rerun_str_id from NEM.nem_settlement_run_status_v strp, "
						+ "NEM.nem_settlement_run_status_v strf, NEM.nem_settlement_rerun_incs incp "
						+ "where strf.id = ? and strp.settlement_date  = strf.settlement_date "
						+ "and strp.run_type = 'P' and strp.authorised = 'A' and incp.str_id = strp.id ";
				sqlCommand1 = "insert into NEM.nem_settlement_rerun_incs (str_id, rerun_str_id ) " + "values (?,?)";

				//Object[] params = new Object[1];
				//params[0] =  settlementParam.runId;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, settlementParam.runId);
				for (Map row : list) {
					Object[] params1 = new Object[2];
					params1[0] =  settlementParam.runId;
					params1[1] =  (String)row.get("rerun_str_id");
					jdbcTemplate.update(sqlCommand1, params1);
					msgText = "Inserted Re-run: " + (String)row.get("rerun_str_id") + " for inclusion against Final Run: "
							+ settlementParam.runId;

					logger.log(Priority.INFO, logPrefix + msgText);

					utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");
				}

			} else {
				msgText = "This settlement run is not a final run, final re-run inclusion step will be skipped";

				logger.log(Priority.INFO, logPrefix + msgText);

				// Log JAM Message
				utilityFunctions.logJAMMessage(settlementParam.runEveId, "I", msgStep, msgText, "");
			}
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
			//settlementRunProcess.updateFailedStatus(settlementParam);
			throw new SettlementRunException(e.getMessage(), msgStep);

		}
	}
	
    @Transactional
	public void getLastIncludedReruns(Map<String, Object> variableMap)
	{

    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
		String lastRunId = (String)variableMap.get("lastRunId");
		
		try{
		    msgStep =  "IncludeRerun.getLastIncludedReruns()" ;

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    String sqlCommand;
		    lastRunId = null;

		    // Get last run Id

		    sqlCommand = "select nsr.id from nem.nem_settlement_runs nsr, nem.jam_events eve " + 
		                 "where nsr.eve_id = eve.id and nsr.run_type=? and nsr.settlement_date=? " + 
		                 "and eve.completed = 'Y' and eve.success = 'Y' " + 
		                 "order by nsr.seq";

			Object[] params = new Object[2];
			params[0] =  settlementParam.runType;
			params[1] =  utilityFunctions.convertUDateToSDate(settlementParam.settlementDate);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
			for (Map row : list) {
				lastRunId = (String)row.get("id");
				logger.log(Priority.INFO,logPrefix + "Last Run Id: " + lastRunId);
				break;
				
			}

		    if (lastRunId != null) {
		        logger.log(Priority.INFO,logPrefix + "Including reruns as same as last run.");

		        sqlCommand = "INSERT INTO NEM.NEM_SETTLEMENT_RERUN_INCS (STR_ID, RERUN_STR_ID) " + 
		                     "SELECT '" + settlementParam.runId + "', RERUN_STR_ID " + 
		                     "FROM NEM.NEM_SETTLEMENT_RERUN_INCS WHERE STR_ID=?";

				jdbcTemplate.update(sqlCommand, new Object[] {lastRunId});
				
		        sqlCommand = "SELECT RERUN_STR_ID FROM NEM.NEM_SETTLEMENT_RERUN_INCS WHERE STR_ID=?";

				List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {settlementParam.runId});
				for (Map row : list1) {
					logger.log(Priority.INFO,logPrefix + "Included rerun Id: " + (String)row.get("RERUN_STR_ID"));
				}
		    }
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + "<IncludeRerun.getLastIncludedReruns()> Exception: " + e.getMessage());
		    //settlementRunProcess.updateFailedStatus(settlementParam);
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}

		// do start
		
	}

}
