/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

/**
 * @author DWTN1561
 *
 */
@Service
public class ExecuteRun implements Serializable{

	/**
	 * 
	 */
	public ExecuteRun() {

	}

	private static final Logger logger = Logger.getLogger(ExecuteRun.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

    String logPrefix ="[EMC]";
    String msgStep = "";
    
    @Transactional
    public Map<String, Object> manualSettlementDate(Map<String, Object> variableMap)
	{

		String nemsControllerEveId = (String)variableMap.get("nemsControllerEveId");
		SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
		
    	msgStep = "ExecuteRun.manualSettlementDate()";
    	logger.log(Priority.INFO, "Executing "+msgStep);

    	try {
		settlementParam.fromSettlementDate = settlementParam.settlementDate;
		settlementParam.toSettlementDate = settlementParam.settlementDate;
		
		settlementParam.setSqlFromSettlementDate(utilityFunctions.convertUDateToSDate(settlementParam.getFromSettlementDate()));
		settlementParam.setSqlToSettlementDate(utilityFunctions.convertUDateToSDate(settlementParam.getToSettlementDate()));
		settlementParam.setSqlSettlementDate(utilityFunctions.convertUDateToSDate(settlementParam.getSettlementDate()));

		logger.log(Priority.INFO,logPrefix+"Nems Controller Id value in ExecuteRun: " +nemsControllerEveId);

		logger.log(Priority.INFO,logPrefix + "Manual Run. From Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementParam.fromSettlementDate) + 
		", to Settlement Date: " + utilityFunctions.getddMMMyyyy(settlementParam.toSettlementDate));

		// Log JAM Message
		utilityFunctions.logJAMMessage(settlementParam.mainEveId,  "I", "ExecuteRun.manualSettlementDate()", "Starting Settlement Runs ('" + settlementParam.runType + "') " + 
		                               "from: " + utilityFunctions.getddMMMyyyy(settlementParam.fromSettlementDate) + " to " + 
		                               utilityFunctions.getddMMMyyyy(settlementParam.toSettlementDate), 
		                               "");
    	}catch(Exception e)
    	{
    		logger.log(Priority.ERROR, "Exception "+e.getMessage());
    		throw new SettlementRunException(e.getMessage(), msgStep);
    	}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		variableMap.put("settlementParam", settlementParam);
		return variableMap;
	}
    
    
    @Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception");
		try {
			// BPM Log
			logger.log(Priority.ERROR, "[EMC] Settlement Main Exception Occurred!!! " + exception.toString());

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
