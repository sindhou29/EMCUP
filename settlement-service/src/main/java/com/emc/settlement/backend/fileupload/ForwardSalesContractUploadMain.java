/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN;
import static com.emc.settlement.model.backend.constants.BusinessParameters.UPLOAD_METHOD_EBT;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.CsvFileValidatorImpl;
import com.emc.settlement.common.ForwardSalesContractUploaderImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.FSCUploadException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.ForwardSalesContract;
import com.emc.settlement.model.backend.pojo.fileupload.ForwardSalesContractUploader;
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
public class ForwardSalesContractUploadMain {

	private static final Logger logger = Logger.getLogger(ForwardSalesContractUploadMain.class);


	String msgStep = null;

	String logPrefix = "[FSC] ";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	ForwardSalesContractUploaderImpl objFscUploaderImpl;

	@Autowired
	CsvFileValidatorImpl csvFileValidatorImpl;

	@Autowired
	AlertNotificationImpl alertNotificationImpl;

	@Transactional
	public Map<String, Object> captureMetaData(Map<String, Object> variableMap) {

		String comments = (String) variableMap.get("comments");
		String compressed = (String) variableMap.get("compressed");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		String fileContentName = (String) variableMap.get("fileContentName");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String filename = (String) variableMap.get("filename");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		Date uploadTime = (Date) variableMap.get("uploadTime");
		String uploadUser = (String) variableMap.get("uploadUser");
		String userId = (String) variableMap.get("userId");

		String activityName = "captureMetaData()";
		msgStep = PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN + "." + activityName;
		logPrefix = "[FSC] ";

		logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
		logger.log(Priority.INFO, "Input parameters - eveId :" + eveId + " uploadTime :" + uploadTime + " userId :" + userId + " fileContentName :" + fileContentName + " uploadMethod :" + uploadMethod + " uploadUser :" + uploadUser + " fileInfo :" + fileInfo + " filename :" + filename + " compressed :" + compressed + " comments :" + comments + " ebtEventsRowId :" + ebtEventsRowId);

		//Create JAM Event
		eveId = utilityFunctions.createJAMEvent("EXE", "FORWARD SALES CONTRACT UPLOAD");
		uploadTime = new Date();


		//get User Id
		userId = utilityFunctions.getUserId(uploadUser);

		if (userId == null) {
			FSCUploadException fscUploadException = new FSCUploadException(1, 1, "User Name: " + uploadUser + " is not valid.", msgStep);
			throw fscUploadException;
		}
		String content = null;
		if (fileContentName != null) {
			content = utilityFunctions.fileToString(fileContentName);
		}

		if (uploadMethod.toUpperCase().equals(UPLOAD_METHOD_EBT)) {
			fileInfo = new UploadFileInfo();
			fileInfo.uploadUsername = "SYSTEM";
			fileInfo.comments = "";
			fileInfo.filename = "";
			fileInfo.transId = null;
			fileInfo.contentFormat = null;
			try {
				utilityFunctions.getMSSLMetaData(content, fileInfo);
			}
			catch (Exception e) {
				FSCUploadException fscUploadException = new FSCUploadException(1, 1, "Get MSSL data thrown exception", msgStep);
				throw fscUploadException;
			}

			// Auto-generate filename for Upload via EBT
			filename = "fsc_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(uploadTime);

			if (fileInfo.compressed !=null && fileInfo.compressed.toUpperCase().equals("Y")) {
				filename = filename + ".zip";
			}
			else {
				filename = filename + ".csv";
			}

			uploadUser = fileInfo.uploadUsername;
			compressed = fileInfo.compressed;

			// fileContentName = fileInfo.content
			comments = "";

			//Create EBT Event use FSR as eventtype
			ebtEventsRowId = utilityFunctions.createEbtEvent(eveId, "FSR",
					filename, userId,
					comments, fileInfo.transId);
		}
		else {
			// Manual File Upload
			if (filename.indexOf("zip") > 0) {
				compressed = "Y";
			}
			else {
				compressed = "N";
			}

			//Create EBT Event use FSM as eventtype
			ebtEventsRowId = utilityFunctions.createEbtEvent(eveId, "FSM",
					filename, userId,
					comments, fileInfo.transId);
		}


		//@Todo Log JAM Message [Correction on EMCS-459]
		utilityFunctions.logJAMMessage(eveId, "I", msgStep,
				"Receiving Forward Sales Contract file:" +
						filename + ", Uploaded by: " + uploadUser + ", Upload Method: " +
						uploadMethod, "");

		// Return from service
		variableMap.put("ebtEventsRowId", ebtEventsRowId);
		variableMap.put("compressed", compressed);
		variableMap.put("comments", comments);
		variableMap.put("uploadUser", uploadUser);
		variableMap.put("filename", filename);
		variableMap.put("eveId", eveId);
		variableMap.put("userId", userId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("uploadTime", uploadTime);
		logger.info("Returning from service "+msgStep+" - ( ebtEventsRowId :" + ebtEventsRowId
				+ " compressed :" + compressed
				+ " uploadUser :" + uploadUser
				+ " uploadUser :" + uploadUser
				+ " filename :" + filename
				+ " eveId :" + eveId
				+ " userId :" + userId
				+ " fileInfo :" + fileInfo
				+ " uploadTime :" + uploadTime + ")");
		return variableMap;
	}

	@Transactional
	public void sendACKToMSSL(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String uploadMethod = (String) variableMap.get("uploadMethod");

		String activityName = "sendACKToMSSL()";
		logger.log(Priority.INFO, "Input Parameters sendACKToMSSL - uploadMethod :" + uploadMethod +
				" eveId :" + eveId + " fileInfo :" + fileInfo + " ebtEventsRowId :" + ebtEventsRowId);

		msgStep = PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN + "." + activityName;

		logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");

		if (uploadMethod .equalsIgnoreCase(UPLOAD_METHOD_EBT)) {
			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Sending ACK to MSSL, Transaction ID: " + fileInfo.transId,
					"");

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			try {
				if (propertiesMap.get("FILE_TO_MSSL_BY_AQ").equals("N")) {
					// If start
					int res = 0;
					logger.log(Priority.INFO, "[EMC] Calling SEND_MSSL_ACK_UNIX_CALL: " + " ebtEventsRowId :" + ebtEventsRowId + "eveId :" + eveId);

					SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
							.withSchemaName("SEBO")
							.withFunctionName("send_mssl_ack_unix_call");
					Map<String, Object> inMap = new HashMap<String, Object>();
					inMap.put("p_ebe_id", "");
					inMap.put("p_eve_id", "");
					jdbcCall.execute(inMap);
					
					logger.log(Priority.INFO, logPrefix + "Result of calling PL/SQL SEND_MSSL_ACK_UNIX_CALL(): " + res);

					if (res != 0) {
						logger.log(Priority.WARN, logPrefix + "Failed to send Acknowledge to MSSL.");

						// Log JAM Message
						utilityFunctions.logJAMMessage(eveId, "E", msgStep, "Failed to send Acknowledge to MSSL.", "");
					}
				}
				else if (propertiesMap.get("FILE_TO_MSSL_BY_AQ").equals("Y")) {
					//if start AQ Concept Flag
					// Send ACK via AQ
					StringBuffer sbuf = new StringBuffer();

					// Create the ACK file
					sbuf = sbuf.append("<TransactionAcknowledgement>" + "\n");
					sbuf = sbuf.append("<TransactionId>" + fileInfo.transId + "</TransactionId>" + "\n");
					sbuf = sbuf.append("<UserId>" + "MC" + "</UserId>" + "\n");
					sbuf = sbuf.append("<SendingPartyType>" + "SE" + "</SendingPartyType>" + "\n");
					sbuf = sbuf.append("</TransactionAcknowledgement>");

					logger.log(Priority.INFO, "[Forward Sales Contract File] " + msgStep + " , Sending Acknowledgement to MSSL using Oracle AQ : fileContentNames=" + sbuf.toString());

					// Write to USAP File
					String ackFileName = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK") + "_" + fileInfo.transId + ".txt";
					File ackFile;
					boolean successFileWrite = false;
					ackFile = new File(UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR") + ackFileName);
					AlertNotification alert;
					try (FileWriter fileWriter = new FileWriter(ackFile)) {
						fileWriter.write(sbuf.toString());
						successFileWrite = true;
						fileWriter.close();
					}

					if (successFileWrite == false) {
						logger.log(Priority.INFO, "[Forward Sales Contract File] " + msgStep + " , Error creating or writing to Acknowledgement File: " + ackFile.getPath() + ".");

						logger.log(Priority.INFO, "[Forward Sales Contract File] " + msgStep + " , Try sending this Acknowledgement using Oracle AQ ignoring the ERROR.");

					}

					if (successFileWrite == true) {
						logger.log(Priority.INFO, "[Forward Sales Contract File] " + msgStep + " - Acknowledgement content saved into file: " + ackFile.getPath() + ".");

						logger.log(Priority.INFO, "[Forward Sales Contract File] " + msgStep + " , Now sending this Acknowledgement using Oracle AQ.");
					}

					alert = new AlertNotification();
					alert.importance = "HIGH";
					alert.jmsType = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK"); //"Transaction Acknowledgement";
					alert.content = sbuf.toString();
					alert.noticeType = "Sending Acknowledgement and USAP Data To MSSL";
					alert.destination = propertiesMap.get("MSSL_DESTINATION_URL");//BusinessParameters.MSSL_DESTINATION_URL;
					alert.ackEbeEveId = eveId;
					alert.ackEbeId = ebtEventsRowId;
					alert.ackDBUpdate = true;
					alertNotificationImpl.sendAckUsapToMSSLviaAQ(alert);

					// Log JAM Message
					utilityFunctions.logJAMMessage(eveId, "I", msgStep,
							"[Forward Sales Contract File] Successfully sent Acknowledgement to MSSL using Oracle AQ, Transaction ID: " + fileInfo.transId,
							"");
				}
				//(AQ)
				// AQ Concept Flag If End
			}
			catch (Exception e) {
				//(AQ)
				if (propertiesMap.get("FILE_TO_MSSL_BY_AQ").equals("N")) {
					logger.log(Priority.WARN, logPrefix + "[Forward Sales Contract File] Failed to send Acknowledgement to MSSL using Unix and Oracle program.");

					// Log JAM Message
					utilityFunctions.logJAMMessage(eveId, "E", msgStep,
							"[Forward Sales Contract File] Failed to send Acknowledgement to MSSL using Unix and Oracle program.",
							"");
				}
				else if (propertiesMap.get("FILE_TO_MSSL_BY_AQ").equals("Y")) {
					logger.log(Priority.WARN, logPrefix + "[Forward Sales Contract File] Failed to send Acknowledgement to MSSL using Oracle AQ.");

					// Log JAM Message
					utilityFunctions.logJAMMessage(eveId, "E", msgStep,
							"[Forward Sales Contract File] Failed to send Acknowledgement to MSSL using Oracle AQ.",
							"");
				}
				//(AQ)
			}
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public Map<String, Object> validateFSCFile(Map<String, Object> variableMap) {

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
		Date uploadTime = (Date) variableMap.get("uploadTime");

		try {

			String activityName = "validateFSCFile()";
			msgStep = PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN + "." + activityName;
			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters validateFSCFile - fileContentName :" + fileContentName +
					" filename :" + filename + " uploadMethod :" + uploadMethod + " fileInfo :" + fileInfo + " eveId :" + eveId + " ebtEventId :" + ebtEventId + " csvFileValidator :" + csvFileValidator.toString() + " strFirstSettDate :" + strFirstSettDate + " strLastSettDate :" + strLastSettDate + " uploadTime :" + uploadTime + "  :" + compressed + " ebtEventsRowId :" + ebtEventsRowId);

			String content = null;
			if (fileContentName != null) {
				content = utilityFunctions.fileToString(fileContentName);
			}

			String zippedContent = null;
			try {
				if (uploadMethod.toUpperCase().equals(UPLOAD_METHOD_EBT)) {
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
				logger.error("Exception "+e.getMessage());
				logger.log(Priority.INFO, e.getMessage());
				logger.log(Priority.INFO, logPrefix + "Invalid zip file content.");

				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "E", msgStep, "Invalid zip file content", "");

				throw new FSCUploadException(1, 1, "Invalid zip file content", msgStep);
			}

			// /////////////////////////////////////////////
			// Validate File
			// /////////////////////////////////////////////
			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Validating Forward Sales Contract File Format",
					"");

			ebtEventId = "FSC_CONTRACT_INPUT_PREPARATION";
			if (csvFileValidatorImpl.isFilenameEmpty(filename)) {
				throw new FSCUploadException(1, 1, "Filename is empty", msgStep);
			}

			csvFileValidator.setCsv_column_count(BusinessParameters.FSC_CONTRACT_CSV_NUM_COLS);
			CSVReader csvReader = null;
			StringReader sReader = new StringReader(content);
			csvReader = new CSVReader(sReader);
			int validNum = csvFileValidatorImpl.readFileData(filename, BusinessParameters.FSC_CONTRACT_CSV_NUM_COLS, csvReader, csvFileValidator);

			if (validNum != 0) {
				throw new FSCUploadException(1, 1, csvFileValidator.getMessage(), msgStep);
			}

			// Log JAM Message [Correction on EMCS-459]
			//@Todo
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Validating Forward Sales Contract File Format: Forward Sales Contract File Format is Valid",
					"");

			// /////////////////////////////////////////////
			// Send Email Notification
			// /////////////////////////////////////////////
			List<String> line;
			int totalLines = csvFileValidator.csvFileData.size();
			line = csvFileValidator.getCsvFileData().get(1);

			// skip the header line at index 0
			strFirstSettDate = String.valueOf(line.get(3));
			line = csvFileValidator.getCsvFileData().get(totalLines - 1);
			strLastSettDate = String.valueOf(line.get(3));
			if (uploadMethod.toUpperCase().equals(UPLOAD_METHOD_EBT)) {
				// Send Notification if the file received via EBT
				Map<String, String> propertiesMap = UtilityFunctions.getProperties();
				AlertNotification alert = new AlertNotification();
				alert.businessModule = "Forward Sales Contract File Upload via " + uploadMethod.toUpperCase();
				alert.recipients = propertiesMap.get("FILE_UPLOAD_EBT_EMAIL");//BusinessParameters.FILE_UPLOAD_EBT_EMAIL;
				alert.subject = "Forward Sales Contract File for " + strFirstSettDate + " to " +
						strLastSettDate + " is received via EBT.";

				alert.content = "Forward Sales Contract File is received via EBT on " + utilityFunctions.getddMMMyyyyhhmmss(uploadTime);
				alert.noticeType = "Forward Sales Contract Input Preparation";
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
			ForwardSalesContractUploader fscUploader = new ForwardSalesContractUploader();
  			objFscUploaderImpl.validateForwardSalesContracts(csvFileValidator, eveId, fscUploader);
			objFscUploaderImpl.uploadForwardSalesContracts(csvFileValidator, eveId, fscUploader);

		}
		catch (FSCUploadException fscUploadException) {
			throw fscUploadException;
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());

			logger.log(Priority.FATAL, logPrefix + "Exception when updating database in Verify and Load Forward Sales Contract Data. " + e.getMessage());

			final FSCUploadException fscUploadException = new FSCUploadException(1, 1, e.getMessage(), msgStep);
			throw fscUploadException;
		}

		logger.info("Return from service - ebtEventId :" + ebtEventId + " csvFileValidator :" + csvFileValidator + " strFirstSettDate :" + strFirstSettDate + " strLastSettDate :" + strLastSettDate + " compressed :" + compressed);
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("csvFileValidator", csvFileValidator);
		variableMap.put("strFirstSettDate", strFirstSettDate);
		variableMap.put("strLastSettDate", strLastSettDate);
		variableMap.put("compressed", compressed);
		return variableMap;
	}

	@Transactional
	public void updateEBTEvent(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");

		msgStep = PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN + ".updateEBTEvent()";

		logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventsRowId : " + ebtEventsRowId
				+ " eveId : " + eveId + ")");

		// Log JAM Message [Correction on EMCS-459]
		utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Upload Forward Sales Contract File successfully finished.", "");

		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(ebtEventsRowId, true);

		// Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(true, eveId);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void fscUploadException(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		FSCUploadException fscException = (FSCUploadException) variableMap.get("fscException");

		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventsRowId : " + ebtEventsRowId
				+ " eveId : " + eveId + ")");

		// 1. log exception into BPM log
		logger.log(Priority.FATAL, logPrefix + fscException.message);

		// 2. Log JAM Message
		String errorCode = Integer.toString(fscException.validationType) + "," + Integer.toString(fscException.validationNumber);

		utilityFunctions.logJAMMessage(eveId, "E", fscException.execStep,
				fscException.message, errorCode);

		// 3. Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(ebtEventsRowId, false);

		// 4. Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(false, eveId);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}


	@Transactional(readOnly = true)
	public void sendExceptionNotification(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String filename = (String) variableMap.get("filename");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		String uploadUser = (String) variableMap.get("uploadUser");
		FSCUploadException fscException = (FSCUploadException) variableMap.get("fscException");

		String activityName = "sendExceptionNotification";
		logger.log(Priority.INFO, logPrefix + " Starting Activity: " + PROCESS_NAME_FORWARD_SALES_CONTRACT_UPLOAD_MAIN + "." + activityName + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventsRowId : " + ebtEventsRowId
				+ " filename : " + filename
				+ " uploadMethod : " + uploadMethod
				+ " uploadUser : " + uploadUser + ")");

		StringBuilder content = new StringBuilder();
		content.append("File Name: " + filename + "\n\n");

		List<Map<String, Object>> eventList = utilityFunctions.getNemEbtEvents(ebtEventsRowId);
		for (Map eventMap : eventList) {
			content.append("File Upload Date and Time: " + eventMap.get("uploaded_date") + "\n\n");

			content.append("File Upload User: " + uploadUser + "\n\n");
			content.append("User Comments: " + eventMap.get("comments") + "\n\n");
			content.append("Validated Time: " + eventMap.get("validated_date") + "\n\n");
			content.append("Valid: " + eventMap.get("valid_yn") + "\n\n");
		}

		content.append("Error Message: " + fscException.message);
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		AlertNotification alertNotifier = new AlertNotification();
		alertNotifier.businessModule = "Forward Sales Contract File Upload via " + uploadMethod;
		alertNotifier.content = content.toString();
		alertNotifier.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;
		alertNotifier.subject = "Forward Sales Contract file upload failed";
		alertNotifier.noticeType = "Forward Sales Contract File Upload";

		alertNotificationImpl.sendEmail(alertNotifier);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

}
