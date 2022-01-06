/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.invoice.services.bc.am.common.InvInternalServices;
import com.emc.invoice.services.bc.am.common.InvInternalServices_Service;
import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.AuthorisationException;
import com.emc.settlement.model.backend.pojo.AccountInterface;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.emc.settlement.model.backend.pojo.USAPCommon;

/**
 * @author DWTN1561
 *
 */
@Service
public class FinalAuthoriseSettlementRun implements Serializable {

	/**
	 * 
	 */
	public FinalAuthoriseSettlementRun() {

	}

	protected static final Logger logger = Logger.getLogger(FinalAuthoriseSettlementRun.class);
	
	public String msgStep="";
	public String logPrefix="";
	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private USAPCommonImpl usapCommonImpl;
	@Autowired
	private AlertNotificationImpl alertNotificationImpl;
	@Autowired
	private AccountInterfaceImpl accountInterfaceImpl;
	
	SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"));
	SimpleDateFormat DISPLAY_TIME_FORMAT = new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
	
	
	@Transactional
	public Map<String, Object> authorisationSettlementRun(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo = (SettlementRunInfo)variableMap.get("runInfo");
		String authorisation = (String)variableMap.get("authorisation");
		String packageId = (String)variableMap.get("packageId"); 
		String username = (String)variableMap.get("username");
		Date drEffectiveDate = (Date)variableMap.get("drEffectiveDate");
		Boolean drEffective = (Boolean)variableMap.get("drEffective");
		
		
		logPrefix = "[2AU] ";
	    msgStep = "FinalAuthoriseSettlementRun.authorisationSettlementRun()" ;
		logger.log(Priority.INFO,logPrefix + " Starting Activity: " + msgStep + " ...");
		
		// ///////////////////////////////////////////
		// Input Parameters:
//		 	@packageId 
//		 	@authrisation
		// 
		// ///////////////////////////////////////////
		logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");
		
		try{
		    
		    String sqlCommand;
		    String[] params;
		    boolean forceDeny = false;
		    
		    // Get DR Effective Start Date
		     drEffectiveDate = utilityFunctions.getSysParamTime("DR_EFFECTIVE_DATE");
		    
			logger.log(Priority.INFO,logPrefix + "DR Effective Start Date: " + drEffectiveDate);

		    if (!authorisation.equalsIgnoreCase("AUTHORISED") && !authorisation.equalsIgnoreCase("NOT AUTHORISED")) {
		    	throw new Exception("Unrecognized Authorisation Status: " + authorisation);
		    }

		    logger.log(Priority.INFO,logPrefix + "Package Id: " + packageId);

		    // Get Settlement Run Information
		    runInfo = utilityFunctions.getSettRunInfo(packageId);
		    String toStatus = "Second Tier Authorisation";

		    if (authorisation.equalsIgnoreCase("NOT AUTHORISED")) {
		        toStatus = "Force Denial";
		    }
		    
		    
		    //DRCAP PHASE2 START //
		    
		    if (runInfo.settlementDate.compareTo(drEffectiveDate) < 0) {
		            drEffective = false;
		        }
		        else {
		            drEffective = true;
		        }
		        
		    //DRCAP PHASE2 END //
		    

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, 
		                                   toStatus + " for Settlement Run Id: " + runInfo.runId, 
		                                   "");

		    if (!runInfo.completed.equalsIgnoreCase("Y") || !runInfo.success.equalsIgnoreCase("Y")) {
		        throw new Exception("The Run has not finished yet or not success. Second Tier Authorisation is not allowed.");
		    }

		    if (runInfo.authStatus.equalsIgnoreCase("AUTHORISED")) {
		        if (authorisation.equalsIgnoreCase("AUTHORISED")) {
		            throw new Exception("Settlement Run is already Second Tier Authorised, no need Authorisation any more.");
		        }
		        else {
		            // "NOT AUTHORISED"
		            forceDeny = true;

		            logger.log(Priority.INFO,logPrefix + "User is performing a Force Deny.");
		        }
		    }
		    else if (runInfo.authStatus.equalsIgnoreCase("1ST AUTHORISED")) {
		        if (authorisation.equalsIgnoreCase("AUTHORISED")) {
		            logger.log(Priority.INFO,logPrefix + "User is performing a Second Tier Authorisation.");
		        }
		        else {
		            // "NOT AUTHORISED"
		            logger.log(Priority.INFO,logPrefix + "User is performing a Deny.");
		        }
		    }
		    else {
		        throw new Exception("First Tier Authorisation not done. Final Authorisation is not allowed.");
		    }

		    if (runInfo.runType.equalsIgnoreCase("F")) {
		        // F-Run
		        int count = 0;

		        sqlCommand = "SELECT COUNT(ID) AS counter FROM NEM.nem_accounting_interfaces" + 
		                     " WHERE str_id = ?";

				count = jdbcTemplate.queryForObject(sqlCommand, new Object[] {runInfo.runId}, Integer.class);
				logger.log(Priority.INFO,logPrefix + "count: " + count);
		        if ((count == 0) && (authorisation.equalsIgnoreCase("AUTHORISED"))) {
		            throw new Exception("Accounting data has not been prepared yet. Authorisation is not allowed.");
		        }
		    }

		    String userId = utilityFunctions.getUserId(username);

		    // Update Authorisation Status
		    sqlCommand = "INSERT INTO NEM.nem_package_authorisations (id, authorisation_status," + 
		                 " authorisation_date, pkg_id, usr_id ) values (get_guid()," + 
		                 " nvl(?, 'PENDING'), SYSDATE, ?, ?)";

			Object[] params1 = new Object[3];
			params1[0] =  authorisation;
			params1[1] =  packageId;
			params1[2] =  userId;
			jdbcTemplate.update(sqlCommand, params1);
			
		    logger.log(Priority.INFO,logPrefix + "Authorisation Status successfully updated to: " + authorisation);

		    if (!UtilityFunctions.getProperty("TEST_RERUN_INCLUDE").equalsIgnoreCase("Y")) {
		        if (forceDeny && (runInfo.runType.equalsIgnoreCase("P") || runInfo.runType.equalsIgnoreCase("F"))) {
		            // De-include included Re-runs
		            sqlCommand = "DELETE FROM nem.nem_settlement_rerun_incs WHERE str_id=?";

					jdbcTemplate.update(sqlCommand, new Object[] {runInfo.runId});
		            logger.log(Priority.INFO,logPrefix + "Re-runs have successfully been de-included from Denied Run.");
		        }
		    }

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, "Authorisation Status successfully updated to: " + authorisation, "");
		                                   
		                                   
			//DRCAP PHASE2 INVOICE EFT CHANGES START //

		 	//if (runInfo.runType == "F" && drEffective) {  --commented after raising observation during DRY RUN
		 	
			if (runInfo.runType.equalsIgnoreCase("F")) {
		    	//InvInternalServicesService.exposeEFTGeneration( runInfo.settlementDate, runInfo.runId, "STL", false); 
//				InvInternalServices_Service service = new InvInternalServices_Service();
//			    InvInternalServices invInternalServicesSoapHttpPort = service.getInvInternalServicesSoapHttpPort();
//				invInternalServicesSoapHttpPort.exposeEFTGeneration(utilityFunctions.toXMLGregorianCalendar(runInfo.settlementDate), runInfo.runId, "STL", false);	
				
				try {
					Map<String, String> propertiesMap = UtilityFunctions.getProperties();
					String invrptservicesURL = propertiesMap.get("invrptservicesURL");
					URL url = new URL(invrptservicesURL + "/invrptservices/InvInternalServices?WSDL");
					logger.log(Priority.INFO, logPrefix + "<" + msgStep + "> URL: " + url);

					InvInternalServices_Service service = new InvInternalServices_Service();
				    InvInternalServices invInternalServicesSoapHttpPort = service.getInvInternalServicesSoapHttpPort();
					invInternalServicesSoapHttpPort.exposeEFTGeneration(utilityFunctions.toXMLGregorianCalendar(runInfo.settlementDate), runInfo.runId, "STL", false);	

				} catch (Exception e) {
					logger.log(Priority.FATAL, logPrefix + "<" + msgStep + "> exception: " + e.getMessage());
					
				} 
				
				
			}
		   //DRCAP PHASE2 INVOICE EFT CHANGES START //
		                   
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
		   
		    throw new AuthorisationException(e.getMessage());
		}
		logger.info("Returning from service  - startTime : "+runInfo.getRunEveId()+" runType : "+runInfo.getRunType()+" settlementDate : "+runInfo.getSettlementDate());
		variableMap.put("runInfo", runInfo);
		return variableMap;
	}
	
	@Transactional
	public void createUsapFileForMSSL(Map<String, Object> variableMap)
	{
		 SettlementRunInfo runInfo  = (SettlementRunInfo)variableMap.get("runInfo");
		 msgStep = "FinalAuthoriseSettlementRun.createUsapFileForMSSL()";
		logger.log(Priority.INFO,logPrefix + " Starting Activity: FinalAuthoriseSettlementRun.createUsapFileForMSSL() ...");

		/* ***************
		   Extract data from NEM_SETTLEMENT_USAP_RESULTS table to generate USAP interface file 

		   Main Logic:	
		   1.  Retrieve all authorised final settlement runs from NEM_SETTLEMENT_RUNS table with usap_date as null
		   2.  For each settRunID, retrieve associated USAP data from NEM_SETTLEMENT_USAP_RESULTS table
		   3.  Output USAP data into text file
		   
		* ******************* */
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		try{

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, 
		                                   "Generating USAP File for Settlement Run ID: " + runInfo.runId, 
		                                   "");
		    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		    USAPCommon usap = new USAPCommon();
		    usap.rowNo = 0;
		    usap.initializeDbItem();

		    usap.settRunId = runInfo.runId;
		    usap.settDate = df.format(runInfo.settlementDate).toUpperCase();
		    usap.eveId = runInfo.runEveId;
		    usap.rowNo = usap.rowNo + 1;
		    usap.settlementDate = runInfo.settlementDate;

		    // 7.1.01 Added the attribute settlementDate as Time
		    // generate the Usap file for this settlement run
		    
		    usapCommonImpl.populateUsapFile(usap, UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("USAP_BASE_DIR"), UtilityFunctions.getProperty("FTP_SERVER_NAME"), 
		    		UtilityFunctions.getProperty("FTP_USER_NAME"), UtilityFunctions.getProperty("FTP_PASSWORD"), 
		    		UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ"), propertiesMap.get("MSSL_DESTINATION_URL"), 
		    		UtilityFunctions.getProperty("JMS_TYPE_USAP"));//BusinessParameters.MSSL_DESTINATION_URL

		    // BPM 2.6.07 (AQ)
		    // update USAP generation date for this settlement run
		    String sqlCommand = "UPDATE NEM.nem_settlement_runs " + 
		    "SET usap_date = SYSDATE WHERE ID = '" + runInfo.runId + "'";

			jdbcTemplate.update(sqlCommand, new Object[] {});
		    // Log JAM Message
		    if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("N")) {
		        // BPM 2.6.07 (AQ)
		        utilityFunctions.logJAMMessage(runInfo.runEveId, "I", 
		                                       msgStep, "USAP File successfully generated and posted to DB Server", 
		                                       "");
		    }
		    else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("Y")) {
		        // BPM 2.6.07 (AQ)
		        utilityFunctions.logJAMMessage(runInfo.runEveId, "I", 
		                                       msgStep, "USAP File successfully generated and posted to Oracle AQ", 
		                                       "");
		    }

		    // BPM 2.6.07 (AQ)
		}
		catch (Exception e) {
			// BPM Log
			logger.log(Priority.ERROR, logPrefix + "Second Tier Authorisation Exception: " + e.getMessage());

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "E", msgStep, e.getMessage(), "");

		    // Send Alert Email
		    AlertNotification alert = new AlertNotification();
		    alert.recipients = propertiesMap.get("SETTLEMENT_RUN_EMAIL");//BusinessParameters.SETTLEMENT_RUN_EMAIL;
		    alert.subject = "USAP generation failed";
		    alert.content = "USAP Generation Time: " + DISPLAY_TIME_FORMAT.format(new Date()) + "\n\n" + 
		    "Settlement Run Date: " + DISPLAY_TIME_FORMAT.format(runInfo.runDate) + "\n\n" + 
		    "USAP Date: " + DISPLAY_DATE_FORMAT.format(runInfo.settlementDate) + "\n\n" + 
		    "Run Type: " + runInfo.runType + "\n\n" + 
		    "Settlement Run ID: " + runInfo.runId + "\n\n" + 
		    "Comments: Value of FILE_TO_MSSL_BY_AQ is " + UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ") + "\n\n" + 
		    "Error Msg: " + e.getMessage();
		    alert.noticeType = "Final Run USAP";
		    alertNotificationImpl.sendEmail(alert);
		    
		 // for WebGUI to capture
		    throw new AuthorisationException("Settlement Run Authorisation Exception.");
		}
		
	}
	
	@Transactional
	public void generateAccountFiles(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo  = (SettlementRunInfo)variableMap.get("runInfo");
		logger.log(Priority.INFO,logPrefix + " Starting Activity: FinalAuthoriseSettlementRun.generateAccountFiles() ...");

		/* ***************
		   Generate Account Report / interface file from final settlement run results upon authorisation

		   Input Arguments: 
		   @param eveId as String
		       
		   Main Logic:	
		   1.  Use @param eveId to retrieve settRunId from NEM_SETTLEMENT_RUNS table
		   2.  Use settRunID to retrieve accounting data from NEM_ACCOUNTING_INTERFACES table
		   3.  Output accounting data into text file
		   
		   Items To confirm:
		   
		* ********************/

		try{
		    msgStep = "FinalAuthoriseSettlementRun.generateAccountFiles()" ;

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    // Log JAM Messages
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep, 
		                                   "Starting to generate Account File", "");

		    // create filename for Account interface file and open it for input
		    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		    SimpleDateFormat df1 = new SimpleDateFormat("ddMMMyyyy");
		    File accountFile;
		    String datetimeSuffix = df.format(new Date());
		    String filename = UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACCOUNT_BASE_DIR") + "account_" + datetimeSuffix + ".dat";
		    accountFile = new File(filename);
	        FileOutputStream fos = new FileOutputStream(accountFile);		        
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		    
		    String lineOut;
		    String dueDate = null;
		    String prevSacId = null;
		    String prevSettRunId = null;
		    String sqlCommand = "SELECT settlement_date, settlement_run_date, " + 
		    " run_type, account_external_id, " + 
		    " calculation_code, calculation_total, gst_code, gst_amount, description, " + 
		    " str_id, sac_id, sac_version FROM NEM.nem_accounting_interfaces " + 
		    " WHERE str_id = '" + runInfo.runId + "'";
		    int i = 0;
		    
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
		        AccountInterface account = new AccountInterface();
		        account.initializeDbItem();

		        account.settlementDate = (Date)row.get("settlement_date");
		        account.runDate = (Date)row.get("settlement_run_date");
		        account.runType = (String)row.get("run_type");
		        account.clientId = (String)row.get("account_external_id");
		        account.calcCode = (String)row.get("calculation_code");
		        account.calculationTotal = (BigDecimal)row.get("calculation_total");
		        account.gstCode = (String)row.get("gst_code");
		        account.gstAmount = (BigDecimal)row.get("gst_amount");
		        account.description = (String)row.get("description");
		        account.sacId = (String)row.get("sac_id");
		        account.sacVersion = (String)row.get("sac_version");

		        if (runInfo.runId != prevSettRunId || account.sacId != prevSacId) {
		            dueDate = accountInterfaceImpl.calculateDueDate(runInfo.runId, account.sacId, account.sacVersion);
		            prevSettRunId = runInfo.runId;
		            prevSacId = account.sacId;
		        }

		        lineOut = "";
		        lineOut = runInfo.runId + "," + df1.format(account.settlementDate).toUpperCase() + "," + 
		                  df1.format(account.runDate).toUpperCase() + "," + dueDate + ",F" + 
		                  "," + account.clientId + "," + account.calcCode + "," + String.valueOf(account.calculationTotal) + 
		                  "," + account.gstCode + "," + String.valueOf(account.gstAmount) + "," + account.description;
		     	    
		        try {
					bw.write(lineOut);
					bw.newLine();
				} catch (Exception e) {
					throw new AuthorisationException( "Error creating or writing to Account File: " + filename + ".");
				}
		        i = i + 1;
		    }
		    

		    bw.close();

		    if (i <= 0) {
		        logger.log(Priority.INFO,logPrefix + "No data found for Settlement Run ID: " + runInfo.runId);

		        utilityFunctions.logJAMMessage(runInfo.runEveId, "I", 
		                                       msgStep, "No Accounting Interface data found for Settlement Run ID: " + runInfo.runId, 
		                                       "");
		    }
		    else {
		        utilityFunctions.logJAMMessage(runInfo.runEveId, "I", 
		                                       msgStep, "Generate Account File success, filename: " + filename, 
		                                       "");
		    }

		    logger.log(Priority.INFO,logPrefix + "CreateAccountReport.createAccountReport() -- Account data written into file: " + filename);
		}
		catch (Exception e) {
		    // BPM Log
		    logger.log(Priority.FATAL,logPrefix + "Exception in Activity: " + msgStep);

		    // TODO: No corresponding Notice Type defined.
		    // Send Alert Email
		    // 	alert as EMC.AlertNotification
		    // 	alert.subject = "Account file generation failed"
		    // 	alert.content = "Time: " + 'now'.format(DISPLAY_TIME_FORMAT) + "\n"
		    // 		+ "Run date: " + runDate.format(DISPLAY_DATE_FORMAT) + "\n"
		    // 		+ "Account date: " + 'now'.format(DISPLAY_DATE_FORMAT) + "\n"
		    // 		+ "Run Type: " + settlementType + "\n"
		    // 		+ "Settlement Run ID: " + settRunId + "\n"
		    // 		+ "Comments: " + "\n"
		    // 		+ "Error Msg: " + e.message
		    // 	alert.noticeType = "???"
		    // 	sendEmail alert
		    // Log JAM Message
		    utilityFunctions.logJAMMessage(runInfo.runEveId, "E", msgStep, e.getMessage(), "");
		    throw new AuthorisationException(e.getMessage());
		}
		
	}
	
	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	
	@Transactional
	public void startDailyManualProcess(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo  = (SettlementRunInfo)variableMap.get("runInfo");
	    msgStep = "FinalAuthoriseSettlementRun.startDailyManualProcess()" ;
		try{

		    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

		    HashMap<String,Object> args = new HashMap<String,Object>();
		    args.put("runTypeArg","F");
		    args.put("settlementDateArg",runInfo.settlementDate);
		    args.put("strIdArg",runInfo.runId);

		    // Create Process Instance for Account Payable
		    args.put("aparArg","AP");
		    //commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
		    //Instance.create(processId : "/MonitorDailyFRunProcess", arguments : args, argumentsSetName : "BeginIn");

		    // Create Process Instance for Account Payable
		    args.put("aparArg","AR");
		    //commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
		    //Instance.create(processId : "/MonitorDailyFRunProcess", arguments : args, argumentsSetName : "BeginIn");

		    Date lastBizDOM = utilityFunctions.getLastBusinessDayOfMonth(new Date());

		    if (runInfo.settlementDate.compareTo(lastBizDOM) == 0) {
		    	args.put("settlementDateArg",runInfo.settlementDate);
		    	args.put("runTypeArg","F");

		        // dummy value for strId
		    	args.put("strId", new Date().getSeconds());
		        //commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
		        //Instance.create(processId : "/MonitorMonthlyIEQWEQReports", arguments : args, 
		        //                argumentsSetName : "BeginIn");
		    	
		        logger.log(Priority.INFO,"[EMC] Started Monthly Monitoring Task: <IEQ_WEQ Reports To Finance> on " + df.format(new Date()));

		        //commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
		        //Instance.create(processId : "/MonitorMonthlyIntertieReportToEMA", arguments : args, 
		        //                argumentsSetName : "BeginIn");

		        logger.log(Priority.INFO,"[EMC] Started Monthly Monitoring Task: <Report Intertie To EMA> on " + df.format(new Date()));
		    }
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] <" + msgStep + "> Exception: " + e.getMessage());
		   
		    throw new AuthorisationException(e.getMessage());
		}
	}
	
	@Transactional
	public void startMMBRAP(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo  = (SettlementRunInfo)variableMap.get("runInfo");
		try{
			// For Test Monthly Bank Recon
			HashMap<String, Object> args = new HashMap<String,Object>();
			args.put("settlementDateArg",runInfo.settlementDate);
			args.put("runTypeArg","F");

			// dummy value for strId
			args.put("strIdArg",new Date().getSeconds());
			args.put("aparArg","AP");
			args.put("runDateArg",new Date());
			//commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
			//Instance.create(processId : "/MonitorMonthlyBankRecon", arguments : args, argumentsSetName : "BeginIn");

			logger.log(Priority.INFO,"[EMC] Started Monthly Monitoring Task: <Monitor Monthly Bank Recon - AP> on " + df.format(new Date()));
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw new AuthorisationException(e.getMessage());
		}
	}
	
	@Transactional
	public void startMMBRAR(Map<String, Object> variableMap)
	{
		SettlementRunInfo runInfo  = (SettlementRunInfo)variableMap.get("runInfo");
		try {
			// For Test Monthly Bank Recon
			HashMap<String, Object> args = new HashMap<String, Object>();
			args.put("settlementDateArg",runInfo.settlementDate);
			args.put("runTypeArg","F");

			// dummy value for strId
			args.put("strIdArg",new Date().getSeconds());
			args.put("aparArg","AR");
			args.put("runDateArg",new Date());
			//commented as Manual Process is no more in use for IGS ITSM 17002 Rel 18
			//Instance.create(processId : "/MonitorMonthlyBankRecon", arguments : args, argumentsSetName : "BeginIn");

			logger.log(Priority.INFO,"[EMC] Started Monthly Monitoring Task: <Monitor Monthly Bank Recon - AR> on " + df.format(new Date()));
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw new AuthorisationException(e.getMessage());
		}
		
	}
	
	@Transactional
	public void exceptionHandler(Map<String, Object> variableMap)
	{
		try {
			SettlementRunInfo runInfo = (SettlementRunInfo) variableMap.get("runInfo");
			Object exception = (Object) variableMap.get("exception");
			// BPM Log
			logger.log(Priority.INFO, logPrefix + "Second Tier Authorisation Exception: " + exception.toString());

			// Log JAM Message
			if (runInfo != null && runInfo.runEveId != null && runInfo.runEveId.length() > 1) {
				utilityFunctions.logJAMMessage(runInfo.runEveId, "E", msgStep, exception.toString(), "");
			}

		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}
	}
	
	
	
	
	
	
}
