/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.AuthorisationException;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * @author DWTN1561
 *
 */
@Service
public class ForceDenySettlementRun implements Serializable{

	/**
	 * 
	 */
	public ForceDenySettlementRun() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ForceDenySettlementRun.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;

    String logPrefix ="[EMC]";
    String msgStep = "";
    String service_name = "ForceDenySettlementRun";
    
    @Transactional
    public void forceDenySettRun(Map<String, Object> variableMap)
	{

		String authorisation= (String)variableMap.get("authorisation");
		SettlementRunInfo runInfo= (SettlementRunInfo)variableMap.get("runInfo");
		String packageId= (String)variableMap.get("packageId");
		String username= (String)variableMap.get("username");
		logger.log(Priority.INFO,logPrefix + " Starting Activity: ForceDenySettlementRun.forceDenySettRun() ...");

		// ///////////////////////////////////////////
		// Input Parameters:
//		 	@packageId 
//		 	@authrisation
		// 
		// ///////////////////////////////////////////
		try{
		    msgStep = service_name+".forceDenySettRun()" ;
		    logPrefix = "[FDENY] ";

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    String sqlCommand;
		    boolean forceDeny = false;

		    logger.log(Priority.INFO,logPrefix + "Package Id: " + packageId);

		    authorisation = "NOT AUTHORISED";

		    // Get Settlement Run Information
		    runInfo = utilityFunctions.getSettRunInfo(packageId);
		    String toStatus = "Force Denial";

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, 
		                                   toStatus + " for Settlement Run Id: " + runInfo.runId, 
		                                   "");

		    if (!runInfo.completed.equalsIgnoreCase("Y") || !runInfo.success.equalsIgnoreCase("Y")) {
		        throw new Exception("The Run has not finished yet or not success. Second Tier Authorisation is not allowed.");
		    }

		    if (!runInfo.authStatus.equalsIgnoreCase("AUTHORISED")) {
		        throw new Exception("Settlement Run is not Authorised, cannot do Force Deny.");
		    }

		    String userId = utilityFunctions.getUserId(username);

		    // Update Authorisation Status

		    sqlCommand = "INSERT INTO NEM.nem_package_authorisations (id, authorisation_status," + 
		                 " authorisation_date, pkg_id, usr_id ) values (get_guid()," + 
		                 " nvl(?, 'PENDING'), SYSDATE, ?, ?)";
			Object[] params = new Object[3];
			params[0] =  authorisation;
			params[1] =  packageId;
			params[2] =  userId;
			jdbcTemplate.update(sqlCommand, params);
			
		    logger.log(Priority.INFO,logPrefix + "Authorisation Status successfully updated to: " + authorisation);

		    if (!UtilityFunctions.getProperty("TEST_RERUN_INCLUDE").equalsIgnoreCase("Y")) {
		        if (runInfo.runType.equalsIgnoreCase("P") || runInfo.runType.equalsIgnoreCase("F")) {
		            // De-include included Re-runs
		            sqlCommand = "DELETE FROM nem.nem_settlement_rerun_incs WHERE str_id=?";

					//Object[] params1 = new Object[1];
					//params1[0] =  runInfo.runId;
					jdbcTemplate.update(sqlCommand, runInfo.runId);
		            logger.log(Priority.INFO,logPrefix + "Re-runs have successfully been de-included from Denied Run.");
		        }
		    }

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, 
		                                   "Settlement Run: " + runInfo.runId + " has been Force Denied.", 
		                                   "");
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
		    throw new AuthorisationException(e.getMessage());
		}
		
	}
    
    
	@Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo= (SettlementRunInfo)variableMap.get("runInfo");
		AuthorisationException exception= (AuthorisationException)variableMap.get("exception");
		// BPM Log
		try {
		logger.log(Priority.ERROR,logPrefix + "Force Deny Exception:  " + exception.toString());

		// Log JAM Message
		if (runInfo != null && runInfo.runEveId != null && runInfo.runEveId.length() > 1) {
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "E", msgStep, exception.toString(), "");
		}
		}catch(Exception e)
		{
			 logger.log(Priority.FATAL,logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
			 e.printStackTrace();
		}
		
	}

}
