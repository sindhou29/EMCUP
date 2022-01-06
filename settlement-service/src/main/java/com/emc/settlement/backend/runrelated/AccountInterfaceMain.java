/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.util.ArrayList;
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
import com.emc.settlement.model.backend.pojo.AccountInterface;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class AccountInterfaceMain implements Serializable {

	/**
	 * 
	 */
	public AccountInterfaceMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(AccountInterfaceMain.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    AccountInterfaceImpl accountInterfaceImpl;

    String logPrefix ="[EMC]";
    String msgStep = "";
    String process_name = "AccountInterfaceMain";

    @Transactional
    public void doAccountingInterface(Map<String, Object> variableMap)
	{

		String runFrom = (String) variableMap.get("runFrom");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		String eveId = (String) variableMap.get("eveId");	
		
	    try{
			/* ***************
			Populates the accounting interface table for all final settlement runs which have not had an 
			authorisation status set yet
 
			Input Arguments: 
			@param eveId   ... jam event id 
 
			Main Logic:	
			1.  Retrieve all settlement run ids with status 'F' from NEM_SETTLEMENT_RUNS table in which corresponding
			 	accounting data has not been generated in accounting interfaces table
			2.  Generate the accounting data and popuate the accounting interfaces table for settlement ids fetched
			 	   
			Uses BPM component CommonMaster.AccountingInterface 
			 	   
    * ******************* */
			 msgStep =  "AccountInterface.doAccountingInterface()";
			
			 logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");
			 
			 logger.log(Priority.INFO,logPrefix + "RunFrom: " + runFrom + " Eve Id :"+eveId);
			 
			 logger.log(Priority.INFO,logPrefix + "settlementDate in Account: " + settlementDate + " ...");
			 
			 
			
			 String sqlAuthSett;
			
			 // SQL to find all final settlement runs which need data published to the Account accounting system
			 if ((!runFrom.equalsIgnoreCase("B")) && (settlementDate != null)) {
			     sqlAuthSett = "SELECT str.ID, str.settlement_date, str.run_type, str.eve_id " + 
			                   "FROM nem_settlement_runs str " + 
			                   "WHERE str.run_type = 'F' " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_package_authorisations pau " + 
			                   "WHERE pau.pkg_id = str.pkg_id " + 
			                   "AND pau.authorisation_status IN ('NOT AUTHORISED', 'AUTHORISED')) " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_accounting_interfaces aif " + 
			                   "WHERE aif.str_id = str.ID) " + 
			                   "AND str.SETTLEMENT_DATE = ? ";
			                   
			 }else if ((runFrom.equalsIgnoreCase("B")) && (settlementDate != null)) { //DRCAP PHASE2
			 
			     logger.log(Priority.INFO,logPrefix + "Inside DRcap cond runFrom " + runFrom + " ...");
			 
			     sqlAuthSett = "SELECT str.ID, str.settlement_date, str.run_type, str.eve_id " + 
			                   "FROM nem_settlement_runs str " + 
			                   "WHERE str.run_type = 'F' " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_package_authorisations pau " + 
			                   "WHERE pau.pkg_id = str.pkg_id " + 
			                   "AND pau.authorisation_status IN ('NOT AUTHORISED', 'AUTHORISED')) " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_accounting_interfaces aif " + 
			                   "WHERE aif.str_id = str.ID) " + 
			                   "AND str.SETTLEMENT_DATE = ? ";
			 }
			 else {
			     sqlAuthSett = "SELECT str.ID, str.settlement_date, str.run_type, str.eve_id " + 
			                   "FROM nem_settlement_runs str " + 
			                   "WHERE str.run_type = 'F' " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_package_authorisations pau " + 
			                   "WHERE pau.pkg_id = str.pkg_id " + 
			                   "AND pau.authorisation_status IN ('NOT AUTHORISED', 'AUTHORISED')) " + 
			                   "AND NOT EXISTS ( SELECT 1 FROM nem_accounting_interfaces aif " + 
			                   "WHERE aif.str_id = str.ID) ";
			 }
			
			 // SQL to fetch all of the settlement results BUT EXCLUDING non period charges which need to
			 // be sent to the Account accounting system for a specific final settlement run.
			 // The outer inline view is to sum up the calculation amount on the GST name and max the SRT version
			 String sqlResultExcludeNpr = "SELECT SUM ( DECODE (rtyp.result_sign_inheritance, " + 
			 "'N', ABS (calculation_result), calculation_result )) AS calculation_result, " + 
			 "SUM ( DECODE (rtyp.result_sign_inheritance, " + 
			 "'N', ABS (gst_amount), gst_amount )) AS gst_amount, " + 
			 "str_id, srt_or_npc_id, gst.name, sac_id, sac_version, " + 
			 "'SRT' srt_npc_type, MAX(subq.srt_version) srt_version, rtyp.name AS rtyp_name " + 
			 "FROM " + 
			 "( SELECT srt.calculation_result calculation_result, " + 
			 "srt.gst_amount gst_amount, srt.str_id str_id, srt.srt_version srt_version, " + 
			 "srt.srt_id srt_or_npc_id, srt.sac_id sac_id, srt.sac_version sac_version, " + 
			 "'SRT' srt_npc_type " + 
			 "FROM nem_settlement_results srt " + 
			 "WHERE srt.str_id = ? " + 
			 ") subq, " + 
			 "nem_settlement_accounts sac, nem_settlement_result_types rtyp, nem_gst_codes gst " + 
			 "WHERE subq.srt_or_npc_id = rtyp.ID " + 
			 "AND subq.srt_version = rtyp.VERSION AND subq.sac_id = sac.ID " + 
			 "AND subq.sac_version = sac.VERSION and rtyp.gst_id = gst.id " + 
			 "and rtyp.gst_version = gst.version AND rtyp.accounting_file = 'Y' " + 
			 "GROUP BY str_id, srt_or_npc_id, gst.name, sac_id, sac_version, " + 
			 "srt_npc_type, rtyp.name ORDER BY str_id, srt_or_npc_id, gst.name, sac_id, sac_version, srt_npc_type, rtyp.name ";
			
			 // Added the Order By for SRP for Oracle 11g
			 // SQL to fetch all of the settlement non period charges which need to
			 // be sent to the Account accounting system for a specific final settlement run.
			 String sqlNprResult = " SELECT SUM (calculation_result) as calculation_result, " + 
			 "SUM (gst_amount) as gst_amount, " + 
			 "str_id, srt_or_npc_id, 'UNKNOWN', sac_id, sac_version, " + 
			 "'NPC' as srt_npc_type, ? as srt_version, '' " + 
			 "FROM " + 
			 "( SELECT npr.ID sr_id,  npr.calculation_result calculation_result, " + 
			 "npr.gst_amount gst_amount, npr.str_id str_id, npr.npc_id srt_or_npc_id, " + 
			 "npr.sac_id sac_id, npr.sac_version sac_version, 'NPC' srt_npc_type " + 
			 "FROM nem_periodic_results npr,  nem_non_period_charges npc, nem_settlement_accounts sac " + 
			 "WHERE npr.str_id = ? AND npr.npc_id = npc.ID " + 
			 "AND npr.sac_id = sac.ID AND npr.sac_version = sac.VERSION ) " + 
			 "GROUP BY str_id, srt_or_npc_id, 'UNKNOWN', sac_id, sac_version, srt_npc_type ORDER BY str_id, srt_or_npc_id, 'UNKNOWN', sac_id, sac_version, srt_npc_type ";
			
			 // Added the Order By for SRP for Oracle 11g
			 List<SettlementRunInfo> runs = new ArrayList<SettlementRunInfo>();
			 String msg;
			 int i = 0;
			
			 // Fetch final run str.ID, str.settlement_date, str.run_type, str.eve_id
			 if (((settlementDate != null) && !runFrom.equalsIgnoreCase("B"))) {

					//Object[] params = new Object[1];
					//params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlAuthSett, utilityFunctions.convertUDateToSDate(settlementDate));
					for (Map row : list) { 
						SettlementRunInfo run = new SettlementRunInfo();
				         run.runId = (String)row.get("ID");
				         run.settlementDate =(Date)row.get("settlement_date");
				         run.runType = (String)row.get("run_type");
				         run.runEveId = (String)row.get("eve_id");
				         runs.add(run);
				         i = i + 1;
				     }		

			 } else if ((settlementDate != null) && (runFrom.equalsIgnoreCase("B"))) {
					//Object[] params = new Object[1];
					//params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlAuthSett, utilityFunctions.convertUDateToSDate(settlementDate));
					for (Map row : list) {
						SettlementRunInfo run = new SettlementRunInfo();
						 run.runId = (String)row.get("ID");
				         run.settlementDate =(Date)row.get("settlement_date");
				         run.runType = (String)row.get("run_type");
				         run.runEveId = (String)row.get("eve_id");
				         runs.add(run);
				         i = i + 1;
				     }			
			 }
			else {
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlAuthSett, new Object[] {});
				for (Map row : list) {
					SettlementRunInfo run = new SettlementRunInfo();
					run.runId = (String) row.get("ID");
					run.settlementDate = (Date) row.get("settlement_date");
					run.runType = (String) row.get("run_type");
					run.runEveId = (String) row.get("eve_id");
					runs.add(run);
					i = i + 1;
				}
			}
			
			     logger.log(Priority.INFO,logPrefix + "SQL: " + sqlAuthSett);
			
			
			 if (runs.size() <= 0) {
			     logger.log(Priority.WARN,logPrefix + "No settlement run data found for populating the accounting table.");
			
			 }
			AccountInterface acif=null;
			 for(SettlementRunInfo run : runs) {
				 acif = new AccountInterface();
			     acif.runId = run.runId;
			     acif.settlementDate = run.settlementDate;
			     acif.runType = run.runType;
			     acif.eveId = run.runEveId;
			
			     // NOTE:  sqlResultExcludeNpr and sqlNprResult calls the same method doSettlementFinalRun() to process 
			     // the query. 
			     // In PL/SQL, sqlResultExcludeNpr and sqlNprResult were originally a union of 2 queries

			     //String[] inParam = new String[1];
			     //inParam[0] = acif.runId;
			     msg = "Populating Accounting Interface for: " + utilityFunctions.getddMMMyyyy(acif.settlementDate) + "-" + acif.runType;
			
			     logger.log(Priority.INFO,logPrefix + msg);
			
			     utilityFunctions.logJAMMessage(eveId, "I", msgStep, msg, "");
			
			     accountInterfaceImpl.doSettlementFinalRun(sqlResultExcludeNpr, new Object[] { acif.runId}, acif);
			
			     Object[] inParam1 = new Object[2];
			     inParam1[0] = acif.srtVersion;
			     inParam1[1] = acif.runId;
			     
			     // The above sql will have set acif.srtVersion = MAX(srt_version), or null if no rows are fetch
			     accountInterfaceImpl.doSettlementFinalRun(sqlNprResult, inParam1, acif);
			
			     msg = "Successfully populated Accounting Interface for: " + utilityFunctions.getddMMMyyyy(acif.settlementDate) + "-" + acif.runType;
			     utilityFunctions.logJAMMessage(eveId, "I", msgStep, msg, "");
			 }
		}catch (SettlementRunException e) {
			logger.error("Exception "+e.getMessage());
			String messageType = "Populate Accounting Table failed for all final settlement runs" + 
			" which don’t have an Authorisation status set yet.";
			String exceptionMessage = messageType + "\n" + e.getMessage();
			utilityFunctions.logJAMMessage(eveId, "E", msgStep, exceptionMessage, "");
			throw new SettlementRunException(exceptionMessage, msgStep);
		}catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			utilityFunctions.logJAMMessage(eveId, "E", msgStep, e.getMessage(), "");
			throw new SettlementRunException(e.getMessage(), msgStep);
		}	
	}
    
    
    @Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{

		try {
			String eveId = (String) variableMap.get("eveId");
			SettlementRunException exception = (SettlementRunException) variableMap.get("exception");

			String exceptionMessage = "";
			// retrieve generic Java exception message and stack trace
			String messageType = "Populate Accounting Table failed for all final settlement runs"
					+ " which don’t have an Authorisation status set yet.";
			exceptionMessage = messageType + "\n" + exceptionMessage;

			// 1. log exception into BPM log
			logger.log(Priority.ERROR, logPrefix + exceptionMessage);
			logger.log(Priority.ERROR, logPrefix + exception.toString());

			utilityFunctions.logJAMMessage(eveId, "E", process_name + ".exceptionHandler()", exceptionMessage, "");
		} catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
		}
	}
	

}
