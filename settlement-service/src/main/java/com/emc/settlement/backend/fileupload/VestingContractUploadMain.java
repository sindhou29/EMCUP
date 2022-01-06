/**
 *
 */
package com.emc.settlement.backend.fileupload;


import static org.apache.commons.lang3.time.DateFormatUtils.format;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.CsvFileValidatorImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.common.VestingContractUploaderImpl;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.EnergyBidPriceMinException;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.exceptions.VestingContractUploadException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.VestingContract;
import com.emc.settlement.model.backend.pojo.fileupload.VestingContractUploader;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author DWTN1561
 *
 */
@Service
public class VestingContractUploadMain {

	protected static final Logger logger = Logger.getLogger(VestingContractUploadMain.class);

	public String msgStep;

	public String logPrefix = "[VST] ";

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	CsvFileValidatorImpl csvFileValidatorImpl;

	@Autowired
	AlertNotificationImpl alertNotificationImpl;

	@Autowired
	VestingContractUploaderImpl vstUploaderImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public Map<String, Object> captureMetaData(Map<String, Object> variableMap) {

		String comments = (String) variableMap.get("comments");
		String compressed = null;
		Boolean drDeployment = null;
		Date drDeploymentDate = null;
		String ebtEventsRowId = null;
		String eveId =  null;
		String fileContentName = (String) variableMap.get("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String filename = (String) variableMap.get("filename");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		String uploadTime = null;
		String uploadUser = (String) variableMap.get("uploadUser");
		String userId = null;

		final String activityName = "captureMetaData()";
		msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + "." + activityName;
		logPrefix = "[VST] ";

		try {
			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( comments : " + comments
					+ " fileContentName : " + fileContentName
					+ " fileInfo : " + fileInfo
					+ " filename : " + filename
					+ " uploadMethod : " + uploadMethod
					+ " uploadUser : " + uploadUser + ")");
			// Create JAM Event
			eveId = utilityFunctions.createJAMEvent("EXE", "VESTING CONTRACT UPLOAD");
			Date uploadDateTime = new Date();
			uploadTime = utilityFunctions.getddMMMyyyyhhmmss(uploadDateTime);
			Date CurrentDate = new Date();

			//DRCAP PHASE2 START

			// Get DR Effective Start Date
			drDeploymentDate = utilityFunctions.getSysParamTime("DR_DEPLOYMENT_DATE");

			if (CurrentDate.compareTo(drDeploymentDate) > 0) {
				drDeployment = true;
			}
			else {
				drDeployment = false;
			}

			//DRCAP PHASE END

			// read file into content
			String content = utilityFunctions.fileToString(fileContentName);
			String zippedContent;

			if (uploadMethod.toUpperCase().equals("EBT")) {
				// fileInfo as EMC.UploadFileInfo
				fileInfo.uploadUsername = "SYSTEM";
				fileInfo.comments = "";
				fileInfo.filename = "";
				fileInfo.transId = null;
				fileInfo.contentFormat = null;
				utilityFunctions.getMSSLMetaData(content, fileInfo);

				// Auto-generate filename for Upload via EBT
				filename = "vst_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(uploadDateTime);

				if (fileInfo.compressed.toUpperCase().equals("Y")) {
					filename = filename + ".zip";
				}
				else {
					filename = filename + ".csv";
				}

				uploadUser = fileInfo.uploadUsername;
				compressed = fileInfo.compressed;

				// fileContent = fileInfo.content
				comments = "";
			}
			else {
				// Manual File Upload
				if (filename.indexOf("zip") > 0) {
					compressed = "Y";
				}
				else {
					compressed = "N";
				}
			}

			// get User Id
			userId = utilityFunctions.getUserId(uploadUser);

			if (userId == null) {
				throw new VestingContractUploadException(1, 1, "User Name: " + uploadUser + " is not valid.",
						msgStep);

			}

			// Create EBT Event
			ebtEventsRowId = utilityFunctions.createEbtEvent(eveId, "VST",
					filename, userId,
					comments, fileInfo.transId);

			// Log JAM Message [Correction on EMCS-459]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Receiving Vesting Contract file:" +
							filename + ", Uploaded by: " + uploadUser + ", Upload Method: " +
							uploadMethod, "");
		}
		catch (VestingContractUploadException vestingContractUploadException) {
			throw vestingContractUploadException;
		} catch (Exception e) {
			throw new VestingContractUploadException(1, 1, "Exception in Capture Metadata ",
					msgStep);
		}

		variableMap.put("eveId", eveId);
		variableMap.put("uploadTime", uploadTime);
		variableMap.put("drDeploymentDate", drDeploymentDate);
		variableMap.put("drDeployment", drDeployment);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("filename", filename);
		variableMap.put("uploadUser", uploadUser);
		variableMap.put("compressed", compressed);
		variableMap.put("userId", userId);
		variableMap.put("ebtEventsRowId", ebtEventsRowId);
		logger.info("Returning from service "+msgStep+" - ( eveId :" + eveId
				+ " uploadTime :" + uploadTime
				+ " drDeploymentDate :" + drDeploymentDate
				+ " fileInfo :" + fileInfo
				+ " filename :" + filename
				+ " uploadUser :" + uploadUser
				+ " compressed :" + compressed
				+ " userId :" + userId
				+ " ebtEventsRowId :" + ebtEventsRowId + ")");
		return variableMap;
	}


	@Transactional
	public void sendACKToMSSL(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String uploadMethod = (String) variableMap.get("uploadMethod");

		try{
			msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + ".sendACKToMSSL()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.info("Returning from service "+msgStep+" - ( ebtEventsRowId :" + ebtEventsRowId
					+ " eveId :" + eveId
					+ " fileInfo :" + fileInfo
					+ " uploadMethod :" + uploadMethod + ")");

			if (uploadMethod.equals("EBT")) {
				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Sending ACK to MSSL, Transaction ID: " + fileInfo.transId, "");

				// BPM 2.6.07 (AQ)
				if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equals("N")) {
					// If start
					int res = 0;

					SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
							.withSchemaName("SEBO")
							.withFunctionName("send_mssl_ack_unix_call");
					Map<String, Object> inMap = new HashMap<String, Object>();
					inMap.put("p_ebe_id", ebtEventsRowId);
					inMap.put("p_eve_id", eveId);
					jdbcCall.execute(inMap);
					
					
					logger.log(Priority.INFO, "[EMC] SEND_MSSL_ACK_UNIX_CALL: " + eveId);

					logger.log(Priority.INFO, logPrefix + "Result of calling PL/SQL SEND_MSSL_ACK_UNIX_CALL(): " + res);

					if (res != 0) {
						logger.log(Priority.INFO, logPrefix + "Failed to send Acknowledge to MSSL.");

						// Log JAM Message
						utilityFunctions.logJAMMessage(eveId, "E", msgStep, "Failed to send Acknowledge to MSSL.", "");
					}
				}
				else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equals("Y")) {
					// BPM 2.6 AQ Concept Flag If Start
					// Send ACK via AQ
					StringBuffer sbuf = new StringBuffer();

					// Create the ACK file
					// per revision no 06
					sbuf = sbuf.append("<TransactionAcknowledgement>" + "\n");

					sbuf = sbuf.append("<TransactionId>" + fileInfo.transId + "</TransactionId>" + "\n");
					sbuf = sbuf.append("<UserId>" + "MC" + "</UserId>" + "\n");
					sbuf = sbuf.append("<SendingPartyType>" + "SE" + "</SendingPartyType>" + "\n");
					sbuf = sbuf.append("</TransactionAcknowledgement>");

					logger.log(Priority.INFO, "[Vesting File] " + msgStep
							+ " , Sending Acknowledgement to MSSL using Oracle AQ : fileContents=" + sbuf.toString());

					// Write to USAP File
					String ackFileName = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK") + "_" + fileInfo.transId + ".txt";

					// "Transaction Acknowledgement" + "_" + fileInfo.transId + ".txt";
					boolean successFileWrite = false;
					BufferedWriter bw = null;
					FileWriter fw = null;
					try {
						fw = new FileWriter(
								UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR") + ackFileName);
						bw = new BufferedWriter(fw);
						bw.write(sbuf.toString());
						successFileWrite = true;
					}
					catch (IOException e) {
						logger.error("Exception "+e.getMessage());
					}
					finally {
						try {
							if (bw != null)
								bw.close();
							if (fw != null)
								fw.close();
						}
						catch (IOException ex) {
							logger.error("Exception "+ex.getMessage());
						}
					}

					if (successFileWrite == false) {
						logger.log(Priority.INFO,
								"[Vesting File] " + msgStep + " , Error creating or writing to Acknowledgement File: "
										+ UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR")
										+ ackFileName + ".");

						logger.log(Priority.INFO, "[Vesting File] " + msgStep
								+ " , Try sending this Acknowledgement using Oracle AQ ignoring the ERROR.");

						// writing to ACK File: " + baseDir + ackFileName + ".");
					}

					if (successFileWrite == true) {
						logger.log(Priority.INFO,
								"[Vesting File] " + msgStep + " - Acknowledgement content saved into file: "
										+ UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR")
										+ ackFileName + ".");

						logger.log(Priority.INFO,
								"[Vesting File] " + msgStep + " , Now sending this Acknowledgement using Oracle AQ.");
					}

					Map<String, String> propertiesMap = UtilityFunctions.getProperties();
					AlertNotification alert = new AlertNotification();

					alert.importance = "HIGH";

					alert.jmsType = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK");

					// "Transaction Acknowledgement";
					alert.content = (sbuf.toString());
					alert.noticeType = "Sending Acknowledgement and USAP Data To MSSL";
					alert.destination = propertiesMap.get("MSSL_DESTINATION_URL");//BusinessParameters.MSSL_DESTINATION_URL;
					alert.ackEbeEveId = eveId;
					alert.ackEbeId = ebtEventsRowId;
					alert.ackDBUpdate = true;
					alertNotificationImpl.sendAckUsapToMSSLviaAQ(alert);

					// Log JAM Message
					utilityFunctions.logJAMMessage(eveId, "I", msgStep,
							"[Vesting File] Successfully sent Acknowledgement to MSSL using Oracle AQ, Transaction ID: "
									+ fileInfo.transId,
							"");
				}

				// BPM 2.6.07 (AQ)
				// BPM 2.6 AQ Concept Flag If End
			}

		}
		catch (Exception e) {
			// BPM 2.6.07 (AQ)
			if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equals("N")) {
				logger.log(Priority.INFO, logPrefix
						+ "[Vesting File] Failed to send Acknowledgement to MSSL using Unix and Oracle program.");

				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "E", msgStep,
						"[Vesting File] Failed to send Acknowledgement to MSSL using Unix and Oracle program.", "");
			}
			else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equals("Y")) {
				logger.log(Priority.INFO,
						logPrefix + "[Vesting File] Failed to send Acknowledgement to MSSL using Oracle AQ.");

				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "E", msgStep,
						"[Vesting File] Failed to send Acknowledgement to MSSL using Oracle AQ.", "");
			}
			// BPM 2.6.07 (AQ)
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public Map<String, Object> validateVestingFile(Map<String, Object> variableMap) {

		String compressed = (String) variableMap.get("compressed");
		CsvFileValidator csvFileValidator = (CsvFileValidator) variableMap.get("csvFileValidator");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		String fileContentName = (String) variableMap.get("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String filename = (String) variableMap.get("filename");
		String strFirstSettDate = (String) variableMap.get("strFirstSettDate");
		String strLastSettDate = (String) variableMap.get("strLastSettDate");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		String uploadTime = (String) variableMap.get("uploadTime");

		try {
			msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + ".validateVestingFile";

			logger.info(logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( compressed : " + compressed
					+ " csvFileValidator : " + csvFileValidator
					+ " ebtEventId : " + ebtEventId
					+ " ebtEventsRowId : " + ebtEventsRowId
					+ " eveId : " + eveId
					+ " fileContentName : " + fileContentName
					+ " fileInfo : " + fileInfo
					+ " filename : " + filename
					+ " strFirstSettDate : " + strFirstSettDate
					+ " strLastSettDate : " + strLastSettDate
					+ " uploadMethod : " + uploadMethod
					+ " uploadTime : " + uploadTime + ")");

			// read file into content
			String content = utilityFunctions.fileToString(fileContentName);
			String zippedContent = null;
			try {
				if (uploadMethod.toUpperCase().equals("EBT")) {
					content = utilityFunctions.getMSSLContent(content);

					// If compressed, unzip
					if (fileInfo.compressed != null && fileInfo.compressed.toUpperCase().equals("Y")) {
						zippedContent = content;
						content = utilityFunctions.base64Decode(content);
					}
				}
				else {
					// Manual File Upload
					// If compressed, unzip
					if (fileInfo.compressed != null && fileInfo.compressed.toUpperCase().equals("Y")) {
						zippedContent = utilityFunctions.base64Encode(content.getBytes());
						content = utilityFunctions.base64Decode(zippedContent);
					}
				}
			}
			catch (Exception e) {
				logger.error(logPrefix + "Invalid zip file content.");

				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "E", msgStep, "Invalid zip file content", "");

				throw new VestingContractUploadException(1, 1, "Invalid zip file content", msgStep);
			}

			// /////////////////////////////////////////////
			// Validate File
			// /////////////////////////////////////////////
			// Log JAM Message [Correction on EMCS-459]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Validating Vesting Contract File Format", "");

			int VESTING_CONTRACT_CSV_NUM_COLS = 7;
			ebtEventId = "VESTING_CONTRACT_INPUT_PREPARATION";

			if (csvFileValidatorImpl.isFilenameEmpty(filename)) {
				throw new VestingContractUploadException(1, 1, "Filename is empty", msgStep);
			}

			csvFileValidator.csv_column_count = VESTING_CONTRACT_CSV_NUM_COLS;
			CSVReader csvReader = null;
			StringReader sReader = new StringReader(content);
			csvReader = new CSVReader(sReader);
			int validNum = csvFileValidatorImpl.readFileData(filename, csvFileValidator.csv_column_count, csvReader, csvFileValidator);

			if (validNum != 0) {
				throw new VestingContractUploadException(1, 1, csvFileValidator.message, msgStep);
			}

			// Log JAM Message [Correction on EMCS-459]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Validating Vesting Contract File Format: Vesting Contract File Format is Valid",
					"");

			// /////////////////////////////////////////////
			// Send Email Notification
			// /////////////////////////////////////////////
			List<String> line;
			int totalLines = csvFileValidator.csvFileData.size();
			line = csvFileValidator.csvFileData.get(1);

			// skip the header line at index 0
			strFirstSettDate = String.valueOf(line.get(3));
			line = csvFileValidator.csvFileData.get(totalLines - 1);
			strLastSettDate = String.valueOf(line.get(3));

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			if (uploadMethod.toUpperCase().equals("EBT")) {
				// Send Notification if the file received via EBT
				AlertNotification alert = new AlertNotification();
				alert.businessModule = "Vesting Contract File Upload via " + uploadMethod.toUpperCase();
				alert.recipients = propertiesMap.get("FILE_UPLOAD_EBT_EMAIL");//BusinessParameters.FILE_UPLOAD_EBT_EMAIL;
				alert.subject = "Vesting Contract File for " + strFirstSettDate + " to " +
						strLastSettDate + " is received via EBT.";

				// alert.content = "Vesting Contract File is received via EBT on " + formatDate('now')
				alert.content = "Vesting Contract File is received via EBT on " + uploadTime;
				alert.noticeType = "Vesting Contract Input Preparation";
				alertNotificationImpl.sendEmail(alert);
			}

			// /////////////////////////////////////////////
			// Store file into database
			// /////////////////////////////////////////////
			if (compressed.equals("Y")) {
				utilityFunctions.storeStringIntoDbClob(ebtEventsRowId, zippedContent);
			}
			else {
				utilityFunctions.storeStringIntoDbClob(ebtEventsRowId, content);
			}

			// /////////////////////////////////////////////
			// Store file into database
			// /////////////////////////////////////////////
			VestingContractUploader vcUploader = new VestingContractUploader();
			vstUploaderImpl.validateVestingContracts(logPrefix, csvFileValidator, eveId, vcUploader);
			vstUploaderImpl.uploadVestingContracts(logPrefix, eveId, csvFileValidator, vcUploader);

			// constQuarterlyBVP = vstUploader.uploadVestingContracts   // Added DRCAP-Phase2-RahulRaghu
		}
		catch (VestingContractUploadException vestingContractUploadException) {
			throw vestingContractUploadException;
		}
		catch (Exception e) {
			logger.error(logPrefix + "Exception when updating database in Verify and Load Vesting Contract Data. " + e.getMessage());

			throw new MsslException("DATA INSERT", 0, 0, e.getMessage(), msgStep);
		}

		// This exception handling is introduced part of BPM 2.5.6 on BPM 2.5.4
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("csvFileValidator", csvFileValidator);
		variableMap.put("strFirstSettDate", strFirstSettDate);
		variableMap.put("strLastSettDate", strLastSettDate);
		variableMap.put("compressed", compressed);
		logger.info("Return from service - ebtEventId :" + ebtEventId
				+ " csvFileValidator :" + csvFileValidator
				+ " strFirstSettDate :" + strFirstSettDate
				+ " strLastSettDate :" + strLastSettDate
				+ " compressed :" + compressed);
		return variableMap;
	}


	@Transactional
	public void updateEBTEvent(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");

		msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + ".updateEBTEvent()";

		logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");
		logger.info(logPrefix + "Input parameters - eveId : " + eveId + " ebtEventsRowId :" + ebtEventsRowId);

		// Log JAM Message [Correction on EMCS-459]
		utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Upload Vesting Contract File successfully finished.", "");

		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(ebtEventsRowId, true);

		// Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(true, eveId);

	}

	@Transactional
	public Map<String, Object> checkCutoff(Map<String, Object> variableMap) throws EnergyBidPriceMinException {

		String egbEveId = (String) variableMap.get("egbEveId");
		Boolean realTimeCalc = false;

		try{

			// Added DRCAP-Phase2-RahulRaghu
			msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + ".checkCutoff()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");
			logger.info(" Input parameters - egbEveId : " + egbEveId + " realTimeCalc : " + realTimeCalc);

			// Create JAM Event
			egbEveId = utilityFunctions.createJAMEvent("EXE", "REAL TIME EGB REVALIDATION");

			logger.log(Priority.INFO, logPrefix + "egbEveId: " + egbEveId + " ...");

			// Log JAM Message [Correction on EMCS-459]
			utilityFunctions.logJAMMessage(egbEveId, "I", BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN,
					"Vesting Contract File Upload Post Processing started successfully.", "");

			String sqlCommand = "SELECT 1  FROM " +
					"(              " +
					"SELECT CASE    " +
					"      WHEN (NEM.NEM$UTIL.is_active_production_db = 'Y') OR (NEM.NEM$UTIL.is_active_production_db = 'N' and nem.nem$util.get_sp_vc('BYPASS_BID_VAL_CUTOFF')='N') " +
					"      THEN " +
					"      CASE " +
					"       WHEN " +
					"       NVL( (SELECT MIN (vc.settlement_date) " +
					"        FROM NEM.NEM_VESTING_CONTRACTS vc, " +
					"        nem.nem_vesting_contract_params vcp " +
					"        WHERE  vc.created_date = (SELECT MAX (vc1.created_date) " +
					"        FROM NEM.NEM_VESTING_CONTRACTS vc1 " +
					"        WHERE vc1.created_date <= SYSDATE  " +
					"          AND vc1.contract_type IN ('VEQ') " +  // added by Rupesh for RM#412 - CR-393
					"         ) " +
					"         AND contract_type IN ('VEQ') " +
					"         AND VC.ID = VCP.VC_ID " +
					"         HAVING MAX (vc.settlement_date)> MIN (vc.settlement_date) " +
					"         AND MIN (vc.settlement_date) >= nem.nem$util.get_sp_dt('DR_EFFECTIVE_DATE')  " +
					"         AND MIN (vc.settlement_date) >= ADD_MONTHS (TRUNC (SYSDATE , 'Q'), 3)), '01-Jan-1000') > TRUNC (SYSDATE)  " +
					"         THEN " +
					"         1     " +
					"         ELSE " +
					"         0    " +
					"         END  " +
					"         WHEN nem.nem$util.get_sp_vc('BYPASS_BID_VAL_CUTOFF')='Y' " +
					"         THEN  " +
					"         1 " +
					"         ELSE " +
					"         0 " +
					"       END check_date  " +
					"  FROM DUAL  " +
					") " +
					"where   check_date = 1 ";

			// Allow EGB BID MIN inserts untill QA START Date - 1

			// logger.log(Priority.INFO,logPrefix + "Sql Command to find realtime: " +
			// sqlCommand + " ...");
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				realTimeCalc = true;
			}

		}
		catch (Exception e) {
			logger.log(Priority.INFO,
					logPrefix + "Exception in <" + msgStep + "> " + e.getMessage() + " Vesting EGB ID " + egbEveId);

			EnergyBidPriceMinException ebdMinException = new EnergyBidPriceMinException(1, 1, "Exception occured in Check Cut Off " + egbEveId, msgStep);
			throw ebdMinException;

		}
		logger.info("Return from service - egbEveId :" + egbEveId + " realTimeCalc :" + realTimeCalc);
		variableMap.put("egbEveId", egbEveId);
		variableMap.put("realTimeCalc", realTimeCalc);
		return variableMap;
	}

	@Transactional
	public void calculateEnergyBidPriceMin(Map<String, Object> variableMap) throws EnergyBidPriceMinException {

		String ebtEventId = (String) variableMap.get("ebtEventId");
		String egbEveId = (String) variableMap.get("egbEveId");
		String eveId = (String) variableMap.get("eveId");

		try {
			msgStep = BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + ".calculateEnergyBridPriceMin()";
			Date startQuarterDate;
			Date endQuarterDate;
			String egoVersion;
			String revalidateFlag;
			String revalidateStatus;

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.info("Input parameters - egbEveId :" + egbEveId + " ebtEventId :" + ebtEventId + " eveId :" + eveId);

			// Log JAM Message [Correction on EMCS-459]
			utilityFunctions.logJAMMessage(egbEveId, "I", BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN,
					"Start Calculating EGB MIN Price for Vesting EBT ID " + ebtEventId, "");

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("NEM")
					.withCatalogName("NEM$DR_CALCULATIONS")
					.withProcedureName("CALL_EGB_MIN_BVP_COMPARISON");
			Map<String, Object> inMap = new HashMap<String, Object>();
			inMap.put("pin_EVE_ID", egbEveId);
			Map<String, Object> retmap = jdbcCall.execute(inMap);
			egoVersion  = (String)retmap.get("pout_VERSION");
			startQuarterDate  = (Date)retmap.get("pout_FROM_DATE");
			endQuarterDate  = (Date)retmap.get("pout_TO_DATE");
			revalidateFlag  = (String)retmap.get("pout_EGB_PRICEMIN_CHANGE");
			revalidateStatus  = (String)retmap.get("pout_REVALIDATE_STATUS");
			
			

			/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("NEM")
					.withCatalogName("NEM$DR_CALCULATIONS")
					.withProcedureName("CALL_EGB_MIN_BVP_COMPARISON");
			Map<String, Object> inMap = new HashMap<String, Object>();
			inMap.put("pin_EVE_ID", egbEveId);
			jdbcCall.execute(inMap);*/

			logger.log(Priority.INFO, "[EMC] NEMDR_CALCULATIONSCALL_EGB_MIN_BVP_COMPARISON " + eveId);

			// logger.log(Priority.INFO,logPrefix + "EGO BID BVP Comparison completed" );

		}
		catch (Exception e) {
			logger.log(Priority.INFO,
					logPrefix + "Exception in <" + msgStep + "> " + e.getMessage() + " Vesting EBT ID " + ebtEventId);

			throw new EnergyBidPriceMinException(1, 1,
					"Exception occured in EnergyBidPrice Min Calculation Vesting EBT ID " + ebtEventId, msgStep);

		}
	}

	@Transactional
	public void handleException(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		VestingContractUploadException vcException = (VestingContractUploadException) variableMap.get("vcException");

		// 1. log exception into BPM log
		logger.log(Priority.ERROR, logPrefix + vcException.message);

		// 2. Log JAM Message
		String errorCode = Integer.toString(vcException.validationType) + ","
				+ Integer.toString(vcException.validationNumber);

		utilityFunctions.logJAMMessage(
				eveId,
				"E",
				vcException.execStep,
				vcException.message,
				errorCode);

		// 3. Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(
				ebtEventsRowId,
				false);

		// 4. Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(
				false,
				eveId);
	}

	@Transactional
	public void sendExceptionNotification(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String filename = (String) variableMap.get("filename");
		String strFirstSettDate = (String) variableMap.get("strFirstSettDate");
		String strLastSettDate = (String) variableMap.get("strLastSettDate");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		String uploadUser = (String) variableMap.get("uploadUser");
		VestingContractUploadException vcException = (VestingContractUploadException) variableMap.get("vcException");

		String activityName = "sendExceptionNotification";
		logger.log(Priority.INFO, logPrefix + " Starting Activity: " + BusinessParameters.PROCESS_NAME_VESTING_CONTRACT_UPLOAD_MAIN + "." + activityName + " ...");

		VestingContract vc;
		int iFirstMonth = 0;
		int iLastMonth = 0;
		String strSettDate = null;

		if (strFirstSettDate != null && strFirstSettDate.length() > 0 && strLastSettDate != null && strLastSettDate.length() > 0) {
			String[] strFirstMonth = strFirstSettDate.split("-");
			String[] strLastMonth = strLastSettDate.split("-");

			if (utilityFunctions.convertMonth(strFirstMonth[1]) >= 1 && utilityFunctions.convertMonth(strLastMonth[1]) <= 3) {
				strSettDate = "Q1 " + strFirstMonth[1] + " to " + strLastMonth[1] + " " + strLastMonth[2];
			}
			else if (utilityFunctions.convertMonth(strFirstMonth[1]) >= 4 && utilityFunctions.convertMonth(strLastMonth[1]) <= 6) {
				strSettDate = "Q2 " + strFirstMonth[1] + " to " + strLastMonth[1] + " " + strLastMonth[2];
			}
			else if (utilityFunctions.convertMonth(strFirstMonth[1]) >= 7 && utilityFunctions.convertMonth(strLastMonth[1]) <= 9) {
				strSettDate = "Q3 " + strFirstMonth[1] + " to " + strLastMonth[1] + " " + strLastMonth[2];
			}
			else if (utilityFunctions.convertMonth(strFirstMonth[1]) >= 10 && utilityFunctions.convertMonth(strLastMonth[1]) <= 12) {
				strSettDate = "Q4 " + strFirstMonth[1] + " to " + strLastMonth[1] + " " + strLastMonth[2];
			}
		}

		List<Map<String, Object>> eventList = utilityFunctions.getNemEbtEvents(ebtEventsRowId);

		StringBuilder content = new StringBuilder();
		content.append("File Name: " + filename + "\n\n");

		for (Map m : eventList) {
			content.append("File Upload Date and Time: " + m.get("uploaded_date") + "\n\n");

			// content.append("Settlement Date: " + strSettDate + "\n\n");
			content.append("File Upload User: " + uploadUser + "\n\n");
			content.append("User Comments: " + m.get("comments") + "\n\n");
			content.append("Validated Time: " + m.get("validated_date") + "\n\n");
			content.append("Valid: " + m.get("valid_yn") + "\n\n");
		}

		content.append("Error Message: " + vcException.message);
		
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		AlertNotification alertNotifier = new AlertNotification();
		alertNotifier.businessModule = "Vesting Contract File Upload via " + uploadMethod;
		alertNotifier.content = content.toString();
		alertNotifier.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;
		alertNotifier.subject = "Vesting Contract file upload failed";
		alertNotifier.noticeType = "Vesting Contract Input Preparation";

		alertNotificationImpl.sendEmail(alertNotifier);

	}

	@Transactional
	public void energyBidPriceMinException(Map<String, Object> variableMap) {

		String egbEveId = (String) variableMap.get("egbEveId");
		EnergyBidPriceMinException egbException = (EnergyBidPriceMinException) variableMap.get("egbException");

		// 1. log exception into BPM log
		logger.log(Priority.ERROR, logPrefix + egbException.message);

		// 2. Log JAM Message
		String errorCode = Integer.toString(egbException.validationType) + "," + Integer.toString(egbException.validationNumber);

		utilityFunctions.logJAMMessage(
				egbEveId,
				"W",
				egbException.execStep,
				egbException.message,
				errorCode);

		// 4. Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(false, egbEveId);

	}

	@Transactional
	public void sendWarningNotification(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");

		// ITSM 15386 Changes for Null Pointer Exception during sending upload failure email
		String fileInfoSettlementDateStr;

		if (fileInfo.settlementDate == null) {
			fileInfoSettlementDateStr = "-";
		}
		else {
			fileInfoSettlementDateStr = format(fileInfo.settlementDate, UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"));
		}

		// end
	}

}
