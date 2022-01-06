/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
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
public class ScheduledRegenerationOfMCRData {

	/**
	 * 
	 */
	public ScheduledRegenerationOfMCRData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(ScheduledRegenerationOfMCRData.class);

	private static final String  PROCESS_NAME = "ScheduledRegenerationOfMCRData";

	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private AlertNotificationImpl notificationImpl;
	@Autowired
	private PavPackageImpl pavPackageImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-MCR] ";

	@Transactional(readOnly = true)
	public Map<String, Object> checkBusinessDay(Map<String, Object> variableMap) {

		String msgStep = PROCESS_NAME + "." + "checkBusinessDay";
		Boolean businessDay = false;

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		Calendar nextDay = Calendar.getInstance();
		nextDay.add(Calendar.DATE, 1);

		logger.info(logPrefix + "Today is: " + utilityFunctions.getddMMMyyyy(new Date()));

		try {
			businessDay = utilityFunctions.isBusinessDay(nextDay.getTime());
		} catch (Exception e) {
			logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
		}
		variableMap.put("businessDay", businessDay);
		logger.info("Returning from service "+msgStep+" - ( businessDay :" + businessDay + ")");
		return variableMap;

	}

	@Transactional
	public Map<String, Object>  initializeVariables(Map<String, Object> variableMap) throws Exception
	{
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		String soapServiceUrl = null;


		String msgStep = "ScheduledRegenerationOfMCRData.initializeVariables()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam : " + settlementParam + ")");
		try{
		    logger.info("Starting Activity "+msgStep);
		    Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		    soapServiceUrl = propertiesMap.get("soapServiceUrl");
		    
		    settlementParam.setRunType("P");
		    
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, 1);
		    settlementParam.setRunDate(cal.getTime());
		}
		catch (Exception e) {
		    logger.log(Priority.FATAL,logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new Exception("Abnormal Termination of Scheduled Regeneration Of MCR Data : (initializeVariables)");
		}
		logger.info("Returning from service "+msgStep+" - soapServiceUrl : "+soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam.getRunDate() : " + settlementParam.getRunDate()
				+ " settlementParam.getRunType() : " + settlementParam.getRunType() + ")");
		return variableMap;
		
	}

    @Transactional
	public Map<String, Object> initialize(Map<String, Object> variableMap)
    {
		String eveId = (String) variableMap.get("eveId");
		PeriodNumber pd = (PeriodNumber) variableMap.get("pd");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");

    	int rowcnt = 0;
    	String daytype = "";

		String msgStep = "ScheduledRegenerationOfMCRData.checkBusinessDay()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " pd : " + pd
				+ " settlementDate : " + settlementDate
				+ " settlementParam : " + settlementParam + ")");
		try {
			// Create JAM_EVENTS
			eveId = utilityFunctions.createJAMEvent("EXE", "MCE Data Regeneration");

			// Get Period Numbers
			pd.total = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
			pd.sum = ((int) utilityFunctions.getSysParamNum("SUM_OF_PERIODS"));
			pd.sum2 = ((int) utilityFunctions.getSysParamNum("SUM_SQUARE_OF_PERIODS"));
			pd.avg3 = ((int) utilityFunctions.getSysParamNum("AVG_CUBIC_OF_PERIODS"));
		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	settlementParam.setSettlementDate(settlementDate);
    	settlementParam.setMainEveId(eveId);
    	settlementParam.setRunEveId(eveId);
    	settlementParam.setRunType("P");

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		String soapServiceUrl = propertiesMap.get("soapServiceUrl");
    	
    	variableMap.put("eveId", eveId);
    	variableMap.put("pd", pd);
    	variableMap.put("settlementParam", settlementParam);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		logger.info("Returning from service "+msgStep+" - (eveId :" + eveId
				+ " pd :" + pd
				+ " settlementDate :" + settlementDate
				+ " soapServiceUrl :" + soapServiceUrl
				+ " settlementParam :" + settlementParam + ")");
    	
    	return variableMap;
    }


    @Transactional
	public Map<String, Object> createMCEDataPackages(Map<String, Object> variableMap)
    {

		SettRunPkg settRunPackage = (SettRunPkg) variableMap.get("settRunPackage");
		Date settlementDate = (Date) variableMap.get("settlementDate");		

    	String msgStep = "ScheduledRegenerationOfMCRData.createMCEDataPackages()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( settlementDate : " + settlementDate
				+ " settRunPackage : " + settRunPackage + ")");
		try{
			settRunPackage.settlementDate = settlementDate;

			// Create MC Packages
			pavPackageImpl.createMCPackages(settRunPackage);

			// Get Standing Version
			settRunPackage.standingVersion = pavPackageImpl.getStandingVersion(settlementDate);

		}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	variableMap.put("settRunPackage", settRunPackage);
		logger.info("Returning from service "+msgStep+" - ( settlementDate :" + settlementDate
				+ " settRunPackage :" + settRunPackage + ")");
    	return variableMap;
    }
  
    @Transactional
	public Map<String, Object> nextDay(Map<String, Object> variableMap)
    {
		Date settlementDate = (Date) variableMap.get("settlementDate");
		logger.log(Priority.INFO, "Input Parameters for nextDay  ( settlementDate : " + settlementDate + ")");

		variableMap.put("finish", 1);
		variableMap.put("settlementDate", utilityFunctions.dateToString(settlementDate));
		logger.info("Returning from service nextDay - ( settlementDate :" + settlementDate
				+ " finish :" + 1 + ")");
    	return variableMap;
    }
	
   
}
