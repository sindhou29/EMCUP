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
import com.emc.settlement.model.backend.exceptions.AuthorisationException;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class ApproveSettlementRun implements Serializable {

	/**
	 * 
	 */
	public ApproveSettlementRun() {
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ApproveSettlementRun.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
    String msgStep =  "";
    String logPrefix = "";
    String service_name = "ApproveSettlementRun";
    
    
    @Transactional
	public void firstTierAuthSettlementRun(Map<String, Object> variableMap) 
    {
		String authorisation =  (String)variableMap.get("authorisation");
		String packageId =  (String)variableMap.get("packageId");
		String username =  (String)variableMap.get("username");
		/*
		 * This process is called by Web GUI to First Tier Authorise or Deny a Settlement Run
		 * Input Parameters:
		 *	@packageId 
		 *	@authrisation
		 *
		 */
	    msgStep =  "ApproveSettlementRun.firstTierAuthSettlementRun()";
	    logPrefix = "[1AU] ";
	    SettlementRunInfo runInfo = null;

		try{

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    // Testing
		    // 	packageId = "74677AFFBD1C7098E0440003BADB42DB"
		    // 	authorisation = "1ST AUTHORISED"
		    // Testing
		    if (!authorisation.equalsIgnoreCase("1ST AUTHORISED") && !authorisation.equalsIgnoreCase("NOT AUTHORISED")) {
		        throw new AuthorisationException( "Unrecognized Authorisation Status: " + authorisation);
		    }

		    logger.log(Priority.INFO, logPrefix + "Package Id: " + packageId);

		    // Get Settlement Run Information
		    runInfo = utilityFunctions.getSettRunInfo(packageId);
		    String toStatus = "Authorisation";

		    if (authorisation.equalsIgnoreCase("NOT AUTHORISED")) {
		        toStatus = "Denial";
		    }

		    // Log JAM Message
		    utilityFunctions.logJAMMessage( runInfo.runEveId,  "I",  msgStep, "First Tier " + toStatus + " for Settlement Run: " + runInfo.runId, "");

		    if (!runInfo.completed.equalsIgnoreCase("Y") || !runInfo.success.equalsIgnoreCase("Y")) {
		        throw new AuthorisationException( "The Run has not finished yet or not success. First Tier Authorisation is not allowed.");
		    }

		    // Only "WAITING" status can be Firist Tier Authorised
		    if (!runInfo.authStatus.equalsIgnoreCase("WAITING")) {
		        throw new AuthorisationException( "Run is Authorised or Cancelled. First Tier Authorisation is not allowed.");
		    }

		    // Get User ID from User name
		    String userId = utilityFunctions.getUserId(username);

		    // Update Authorisation Status
		    String sqlCommand = "INSERT INTO NEM.nem_package_authorisations (id, authorisation_status," + 
		                 " authorisation_date, pkg_id, usr_id ) values (SYS_GUID(),?,SYSDATE,?,?)";

		    Object[] params = new Object[3];
			params[0] =  authorisation;
			params[1] =  packageId;
			params[2] =  userId;
			jdbcTemplate.update(sqlCommand, params);
		    logger.log(Priority.INFO,logPrefix + "Authorisation Status successfully updated to: " + authorisation);

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, "Authorisation Status successfully updated to: " + authorisation, "");
		}
		catch (AuthorisationException e) {
		    logger.log(Priority.INFO,logPrefix + "Exception in <" + msgStep + ">: " + e.getMessage());
		    throw new  AuthorisationException(e.getMessage());
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,logPrefix + "Exception in <" + msgStep + ">: " + e.getMessage());
		    throw new  AuthorisationException(e.getMessage());  
		}
    }
    
    @Transactional
    public void exceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunInfo runInfo = (SettlementRunInfo)variableMap.get("runInfo");
    	Object exception = (Object)variableMap.get("exception");
		// BPM Log
		logger.error("[EMC] Settlement Run Authorisation Exception: " + exception.toString());
		
		// Log JAM Message
		if (runInfo != null && runInfo.runEveId != null) {
			utilityFunctions.logJAMMessage(runInfo.runEveId, "E", service_name, exception.toString(), "");
		}
		//throw new Exception( "Exception in [Approve Settlement Run]."+exception.toString());
	}

}
