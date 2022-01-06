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
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class IPDataVerfications implements Serializable {

	/**
	 * 
	 */
	public IPDataVerfications() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(IPDataVerfications.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

    String logPrefix ="[EMC]";
    String msgStep = "";

    @Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	SettlementRunException exception= (SettlementRunException)variableMap.get("exception");
    	msgStep = "IPDataVerfications.exceptionHandler()" ;
    	logger.info("Inside "+ msgStep+" settlementParam : "+settlementParam+" exception : "+exception);
		try {
			// BPM Log
			logger.log(Priority.ERROR,
					logPrefix + msgStep+" Data validation found errors in Settlement Data." + exception != null ? exception.message  : "");

			// Log Error JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E",
					"IPDataVerificationsAndValidations.exceptionHandler()", exception != null ? exception.message  : "", "");

			// Log Info JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "I",
					"IPDataVerificationsAndValidations.exceptionHandler()",
					"Data validation found errors in Settlement data", "");
			// for WebGUI to capture
		}catch(Exception e)
    	{
			logger.log(Priority.ERROR, logPrefix + msgStep + e.getMessage());
    	}
				
	}
    
    @Transactional
	public void otherExceptionHandler(Map<String, Object> variableMap)
	{
    	SettlementRunParams settlementParam= (SettlementRunParams)variableMap.get("settlementParam"); 
    	msgStep = "IPDataVerfications.otherExceptionHandler()" ;
    	logger.info("Inside "+ msgStep+" settlementParam : "+settlementParam);
		try {
			// BPM Log
			logger.log(Priority.ERROR,
					logPrefix + "Other Exception - Data validation found errors in Settlement Data.");

			// Log Info JAM Message
			utilityFunctions.logJAMMessage(settlementParam.runEveId, "E",
					"IPDataVerificationsAndValidations.exceptionHandler()",
					"Data validation found errors in Settlement data", "");
		}catch(Exception e)
    	{
			logger.log(Priority.ERROR, logPrefix + e.getMessage());
    	}
				
	}
}
