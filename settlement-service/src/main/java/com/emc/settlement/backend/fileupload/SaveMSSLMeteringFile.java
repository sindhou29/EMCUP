/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.FILE_VALIDATION;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author DWTN1561
 *
 */
//@Log4j
@Service
public class SaveMSSLMeteringFile implements Serializable{

	private String msgStep;

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	AlertNotificationImpl alertImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TransactionTemplate transactionTemplate;

	protected static final Logger log = Logger.getLogger(SaveMSSLMeteringFile.class);

	/**
	 *
	 */
	public SaveMSSLMeteringFile() {
	}

	@Transactional
	public Map<String, Object> captureMetaData(Map<String, Object> variableMap) throws MsslException {

		final String activityName = "CaptureMetadata()";
		String eventId = null;
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		Boolean fromEBT = (Boolean) variableMap.get("fromEBT");
		String userId = null;
		String ebtEventId = null;
		String fileContentName = (String) variableMap.get("fileContentName");
		String logPrefix = (String) variableMap.get("logPrefix");
		try {
			msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + "." + activityName;


			log.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			log.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " fromEBT : " + fromEBT
					+ " fileContentName : " + fileContentName
					+ " logPrefix : " + logPrefix + ")");

			// Create JAM Event
			eventId = utilityFunctions.createJAMEvent("EXE", "MSSL UPLOAD FILE");
			fileInfo.uploadTime = new Date();
			String zippedContent = null;
			String content;

			// read file into content
			content = utilityFunctions.fileToString(fileContentName);

			// 3.2 Uploaded file should not be of zero bytes
			if (content.length() < 1) {
				throw new MsslException(FILE_VALIDATION, 3102, 0,
						"Uploaded file should not be of zero bytes.",
						msgStep);
			}

			if (fromEBT) {
				fileInfo.uploadUsername = "SYSTEM";
				fileInfo.comments = "";
				fileInfo.transId = null;
				fileInfo.contentFormat = null;
				utilityFunctions.getMSSLMetaData(content, fileInfo);

				// Auto-generate filename for Upload via EBT
				fileInfo.filename = fileInfo.fileType.toLowerCase() + "_" + utilityFunctions.getyyyyMMdd_HHmmss(fileInfo.uploadTime);

				if (fileInfo.compressed != null && fileInfo.compressed.toUpperCase().equalsIgnoreCase("Y")) {
					fileInfo.filename = fileInfo.filename + ".zip";
				}
				else {
					fileInfo.filename = fileInfo.filename + ".csv";
				}
			}
			else {
				// Manual File Upload
				// 3.1 File name should not by empty
				if (fileInfo.filename == null || fileInfo.filename.length() < 1) {
					throw new MsslException(FILE_VALIDATION, 3101, 0,
							"File Name should not be empty.", msgStep);
				}

				if (fileInfo.filename.indexOf("zip") > 0) {
					fileInfo.compressed = "Y";
				}
				else {
					fileInfo.compressed = "N";
				}
			}

			// Log JAM Message
			String msg;

			if (fileInfo.fileType.equals("CMF") || fileInfo.fileType.equals("CTR")) {
				zippedContent = content;
				if (fileInfo.compressed.equals("Y") && zippedContent.length() > UtilityFunctions.getIntProperty("MAX_EBT_FILE_SIZE")) {
					throw new MsslException(FILE_VALIDATION, 3103, 0,
							"[UCM-3]: uploaded file size exceed limitation: " +
									UtilityFunctions.getIntProperty("MAX_EBT_FILE_SIZE"), msgStep);
				}
				else if (fileInfo.compressed.equalsIgnoreCase("N") && content.length() > UtilityFunctions.getIntProperty("MAX_EBT_FILE_SIZE")) {
					throw new MsslException(FILE_VALIDATION, 3103, 0,
							"[UCM-3]: uploaded file size exceed limitation: " +
									UtilityFunctions.getIntProperty("MAX_EBT_FILE_SIZE"), msgStep);
				}

				msg = "Receiving Corrected MSSL File: ";
			}
			else {
				msg = "Receiving Daily MSSL File: ";
			}

			msg = msg + fileInfo.filename + ", Uploaded by: " + fileInfo.uploadUsername +
					", Upload Method: " + (fromEBT ? "EBT" : "Manual");
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					msg, "");

			// Get User ID
			userId = utilityFunctions.getUserId(fileInfo.uploadUsername);
			fileInfo.comments = fileInfo.comments.replace("'", "''");

			// Create EBT Event
			ebtEventId = utilityFunctions.createEbtEvent(eventId, fileInfo.fileType,
					fileInfo.filename, userId,
					fileInfo.comments, fileInfo.transId);

			log.info(logPrefix + "Create EBT Event success with ID: " + ebtEventId);
		}
		catch (MsslException msslException) {
			throw msslException;
		}
		catch (Exception e) {
			log.info(logPrefix + "Exception in <" + msgStep + "> " + e.getMessage());

			throw new MsslException(FILE_VALIDATION, 0, 0, e.getMessage(),
					msgStep);
		}

		//Returning from service
		variableMap.put("eventId", eventId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("fromEBT", fromEBT);
		variableMap.put("userId", userId);
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("fileContentName", fileContentName);
		log.info("Returning from service -  eventId :" + eventId + " fileInfo :" + fileInfo + " fromEBT :" + fromEBT + " userId :" + userId + " ebtEventId :" + ebtEventId + " fileContentName :" + fileContentName);
		return variableMap;
	}

	@Transactional
	public Map<String, Object>  sendACKToMSSL(Map<String, Object> variableMap)
	{
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		Boolean fromEBT = (Boolean) variableMap.get("fromEBT");
		String eventId = (String) variableMap.get("eventId");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String logPrefix = (String) variableMap.get("logPrefix");

		final String activityName = "sendACKToMSSL()";

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + "." + activityName;

		log.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
		log.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " fromEBT : " + fromEBT
				+ " eventId : " + eventId
				+ " ebtEventId : " + ebtEventId
				+ " logPrefix : " + logPrefix + ")");

		if (fromEBT)

		{
			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Sending ACK to MSSL, Transaction ID: " + fileInfo.transId,
					"");

			try {
				//BPM 2.6.07 (AQ)
				if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("N")) {
					// BPM 2.6 NON AQ Concept Flag If Start
					int res = 0;

					SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
							.withSchemaName("SEBO")
							.withFunctionName("SEND_MSSL_ACK_UNIX_CALL");
					Map<String, Object> inMap = new HashMap<String, Object>();
					inMap.put("p_ebe_id", ebtEventId);
					inMap.put("p_eve_id", eventId);
					jdbcCall.execute(inMap);

					if (res != 0) {
						log.log(Priority.INFO, logPrefix + "Failed to send Acknowledgement to MSSL using Unix and Oracle program.");

						// Log JAM Message
						utilityFunctions.logJAMMessage(eventId, "E",
								msgStep, "Failed to send Acknowledgement to MSSL using Unix and Oracle program.",
								"");
					}
				}
				//BPM 2.6.07 (AQ)
				//BPM 2.6.07 (AQ)
				else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("Y")) {
					// BPM 2.6 AQ Concept Flag If Start
					// Send ACK via AQ
					StringBuffer sbuf = new StringBuffer();

					// Create the ACK file
					sbuf = sbuf.append("<TransactionAcknowledgement>" + "\n");

					sbuf = sbuf.append("<TransactionId>" + fileInfo.transId + "</TransactionId>" + "\n");
					sbuf = sbuf.append("<UserId>" + "MC" + "</UserId>" + "\n");
					sbuf = sbuf.append("<SendingPartyType>" + "SE" + "</SendingPartyType>" + "\n");
					sbuf = sbuf.append("</TransactionAcknowledgement>");

					log.log(Priority.INFO, "[Usage / Adjusted Usage File] " + msgStep + " , Sending Acknowledgement to MSSL using Oracle AQ : fileContents=" + sbuf.toString());

					// Write to USAP File
					String ackFileName = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK") + "_" + fileInfo.transId + ".txt"; //"Transaction Acknowledgement" + "_" + fileInfo.transId + ".txt";
					boolean successFileWrite = false;
					BufferedWriter bw = null;
					FileWriter fw = null;
					try {
						fw = new FileWriter(UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR") + ackFileName);
						bw = new BufferedWriter(fw);
						bw.write(sbuf.toString());
						successFileWrite = true;
					}
					catch (IOException e) {
						log.error("Exception " + e.getMessage());
					}
					finally {
						if (bw != null)
							bw.close();
						if (fw != null)
							fw.close();
					}

					if (successFileWrite == false) {
						log.log(Priority.INFO, "[Usage / Adjusted Usage File] " + msgStep + " , Error creating or writing to Acknowledgement File: " + UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR") + ackFileName + ".");

						log.log(Priority.INFO, "[Usage / Adjusted Usage File] " + msgStep + " , Try sending this Acknowledgement using Oracle AQ ignoring the ERROR.");

					}

					if (successFileWrite == true) {
						log.log(Priority.INFO, "[Usage / Adjusted Usage File] " + msgStep + " - Acknowledgement content saved into file: " + UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("ACK_FILE_BASE_DIR") + ackFileName + ".");

						log.log(Priority.INFO, "[Usage / Adjusted Usage File] " + msgStep + " , Now sending this Acknowledgement using Oracle AQ.");
					}

					Map<String, String> propertiesMap = UtilityFunctions.getProperties();
					AlertNotification alert = new AlertNotification();

					alert.importance = "HIGH";

					alert.jmsType = UtilityFunctions.getProperty("JMS_TYPE_FILE_ACK"); //"Transaction Acknowledgement";
					alert.content = sbuf.toString();
					alert.noticeType = "Sending Acknowledgement and USAP Data To MSSL";
					alert.destination = propertiesMap.get("MSSL_DESTINATION_URL");//BusinessParameters.MSSL_DESTINATION_URL;
					alert.ackEbeEveId = eventId;
					alert.ackEbeId = ebtEventId;
					alert.ackDBUpdate = true;
					alertImpl.sendAckUsapToMSSLviaAQ(alert);

					// Log JAM Message
					utilityFunctions.logJAMMessage(eventId, "I",
							msgStep, "[Usage / Adjusted Usage File] Successfully sent Acknowledgement to MSSL using Oracle AQ, Transaction ID: " + fileInfo.transId,
							"");
				}
				//BPM 2.6.07 (AQ)
				// BPM 2.6 AQ Concept Flag If End
			}
			catch (Exception e) {
				//BPM 2.6.07 (AQ)
				if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("N")) {
					log.log(Priority.INFO, logPrefix + "[Usage / Adjusted Usage File] Failed to send Acknowledgement to MSSL using Unix and Oracle program. Exception: " + e.getMessage());
				}
				else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("Y")) {
					log.log(Priority.INFO, logPrefix + "[Usage / Adjusted Usage File] Failed to send Acknowledgement to MSSL using Oracle AQ. Exception: " + e.getMessage());
				}

				log.log(Priority.INFO, logPrefix + "[Usage / Adjusted Usage File] Stack trace: " + e.getMessage());

				// Log JAM Message
				if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("N")) {
					utilityFunctions.logJAMMessage(eventId, "E",
							msgStep, "[Usage / Adjusted Usage File] Failed to send Acknowledge to MSSL using Unix and Oracle program. Exception: " + e.getMessage(),
							"");
				}
				else if (UtilityFunctions.getProperty("FILE_TO_MSSL_BY_AQ").equalsIgnoreCase("Y")) {
					utilityFunctions.logJAMMessage(eventId, "E",
							msgStep, "[Usage / Adjusted Usage File] Failed to send Acknowledge to MSSL using Oracle AQ. Exception: " + e.getMessage(),
							"");
				}
				//BPM 2.6.07 (AQ)
			}
		}

		//Returning from service
		variableMap.put("eventId", eventId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("fromEBT", fromEBT);
		variableMap.put("ebtEventId", ebtEventId);
		log.info("Returning from service - eventId :" + eventId + " fileInfo :" + fileInfo + " fromEBT :" + fromEBT + " ebtEventId :" + ebtEventId);
		return variableMap;

	}

	@Transactional
	public Map<String, Object> storeMeteringFile(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String fileContentName = (String) variableMap.get("fileContentName");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		Boolean fromEBT = (Boolean) variableMap.get("fromEBT");
		String eventId = (String) variableMap.get("eventId");
		String logPrefix = (String) variableMap.get("logPrefix");


		String activityName = "storeMeteringFile()";
		msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + "." + activityName;

		log.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
		log.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " fileContentName : " + fileContentName
				+ " fromEBT : " + fromEBT
				+ " eventId : " + eventId
				+ " ebtEventId : " + ebtEventId
				+ " logPrefix : " + logPrefix + ")");

		// read file into content
		String content = utilityFunctions.fileToString(fileContentName);
		String zippedContent = null;

		try {
			if (fromEBT) {
				content = utilityFunctions.getMSSLContent(content);

				// If compressed, unzip
				if (fileInfo.compressed != null && fileInfo.compressed.toUpperCase().equalsIgnoreCase("Y")) {
					zippedContent = content;
					content = utilityFunctions.base64Decode(content);
				}
			}
			else {
				// Manual File Upload
				// If compressed, unzip
				if (fileInfo.compressed != null && fileInfo.compressed.toUpperCase().equalsIgnoreCase("Y")) {
					zippedContent = utilityFunctions.base64Encode(content.getBytes());
					content = utilityFunctions.base64Decode(zippedContent);
				}
			}
		}
		catch (IOException e) {
			log.info(logPrefix + "Exception in <" + msgStep + "> " + e.getMessage());
			throw new MsslException(FILE_VALIDATION, 0, 0, e.getMessage(),
					msgStep);
		}

		// Store fileupload file into NEM_SETTLEMENT_RAW_FILES table
		log.log(Priority.INFO, logPrefix + "Storing raw file with EBE_ID: " + ebtEventId);

		if (fileInfo.compressed != null && fileInfo.compressed.equalsIgnoreCase("Y")) {
			utilityFunctions.storeStringIntoDbClob(ebtEventId, zippedContent);
		}
		else {
			utilityFunctions.storeStringIntoDbClob(ebtEventId, content);
		}

		log.log(Priority.INFO, logPrefix + "MSSL file format is Valid.");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eventId, "I", msgStep, "Validating MSSL File Format : MSSL File Format is Valid", "");

		log.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}

	public void sqlException(SQLException sqlException, String msgStep, String activityName, UploadFileInfo fileInfo, String eventId, String ebtEventId, boolean fromEBT) {
		MsslException msslException = new MsslException();
		msslException.errorMsg = sqlException.getMessage();
		msslException.execStep = msgStep;
		msslException.rowNumber = 0;
		msslException.validationNumber = 0;
		msslException.validationType = "SQL EXCEPTION";
		throw msslException;
	}

	@Transactional
	public void updateEvent(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		MsslException msslException = (MsslException) variableMap.get("msslException");
		String logPrefix = (String) variableMap.get("logPrefix");
		String eventId = (String) variableMap.get("eventId");
		String ebtEventId = (String) variableMap.get("ebtEventId");

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + ".alertNotify()";
		
		log.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep);
		log.log(Priority.INFO,
				"Input Parameters for " + msgStep + "  ( fileInfo : " + fileInfo + " msslException : "
						+ msslException + " logPrefix : " + logPrefix + " eventId : " + eventId
						+ " ebtEventId : " + ebtEventId + ")");
		try {
			if(msslException != null)
			{

				log.log(Priority.INFO,
						logPrefix + "MSSL Exception: VALTYPE - " + msslException.validationType + ", VALNUM - "
								+ msslException.validationNumber + ", ROWNUM: " + msslException.rowNumber + ", MSG: "
								+ msslException.errorMsg);

				String errorCode = msslException.validationType + ","
						+ Integer.toString(msslException.validationNumber);

				String msg;
				String filetype = (fileInfo.fileType.substring(0, 1).equals("C")) ? "Corrected" : "Daily";
				if (msslException.validationType.equals(FILE_VALIDATION)) {
					msg = "Validating " + filetype + " MSSL File: " + msslException.errorMsg + " line("
							+ (msslException.rowNumber) + ")";
				} else {
					msg = "Validating " + filetype + " MSSL Data: " + msslException.errorMsg + " line("
							+ (msslException.rowNumber) + ")";
				}

				utilityFunctions.logJAMMessage(eventId, "E", msslException.execStep, msg, errorCode);

				// Update EBT Event
				utilityFunctions.updateEBTEvent(ebtEventId, false);

				// Update JAM Event
				utilityFunctions.updateJAMEvent(false, eventId);

				utilityFunctions.logJAMMessage(eventId, "E", BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE,
						"MSSL " + filetype + " Metering Data (" + fileInfo.filename + ") not uploaded due to error",
						errorCode);

			}
		}
		catch (Exception e) {
			log.log(Priority.ERROR, logPrefix + "Exception in Activity " + msgStep + ". " + e.getMessage());
		}
		log.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> alertNotify(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		MsslException msslException = (MsslException) variableMap.get("msslException");
		String eventId = (String) variableMap.get("eventId");

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + ".alertNotify()";
		
		log.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " msslException : " + msslException
				+ " eventId : " + eventId + ")");

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		AlertNotification alert = new AlertNotification();
		alert.businessModule = "Receive MSSL Metering File";
		alert.content = "Filename: " + fileInfo.filename + ";\n\n" +
				"File fileupload time: " + format(fileInfo.uploadTime, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")) + ";\n\n" +
				"Settlement Date: " + ((fileInfo.settlementDate == null) ? "-" : format(fileInfo.settlementDate, UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"))) + ";\n\n" +
				"File fileupload user: " + fileInfo.uploadUsername + ";\n\n" +
				"User Comments: " + fileInfo.comments + ";\n\n" +
				"Validated time: " + format(Calendar.getInstance().getTime(), UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")) + ";\n\n" +
				"Valid: N;\n\n" +
				"Error Message: " + msslException.errorMsg + " line(" + Integer.toString(msslException.rowNumber) + "). Event ID: " + eventId;
		alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;

		if (fileInfo.fileType.equals("CTR") || fileInfo.fileType.equals("CMF")) {
			alert.subject = "Corrected Metering file upload failed";
		}
		else {
			alert.subject = "Daily Metering file upload failed";
		}

		alert.noticeType = "MSSL Metering Data Input Preparation";

		alertImpl.sendEmail(alert);

		log.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}
	
	@Transactional
	public void updateOtherException(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		MsslException msslException = (MsslException) variableMap.get("msslException");
		String logPrefix = (String) variableMap.get("logPrefix");
		String eventId = (String) variableMap.get("eventId");
		String ebtEventId = (String) variableMap.get("ebtEventId");

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE + ".updateOtherException()";

		log.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep);
		log.log(Priority.INFO,
				"Input Parameters for " + msgStep + "  ( fileInfo : " + fileInfo + " msslException : "
						+ msslException + " logPrefix : " + logPrefix + " eventId : " + eventId
						+ " ebtEventId : " + ebtEventId + ")");

		try {
			if(msslException != null)
			{

				log.log(Priority.INFO,
						logPrefix + "MSSL Exception: VALTYPE - " + msslException.validationType + ", VALNUM - "
								+ msslException.validationNumber + ", ROWNUM: " + msslException.rowNumber + ", MSG: "
								+ msslException.errorMsg);

				String errorCode = msslException.validationType + ","
						+ Integer.toString(msslException.validationNumber);

				String msg;
				String filetype = (fileInfo.fileType.substring(0, 1).equals("C")) ? "Corrected" : "Daily";
				if (msslException.validationType.equals(FILE_VALIDATION)) {
					msg = "Validating " + filetype + " MSSL File: " + msslException.errorMsg + " line("
							+ (msslException.rowNumber) + ")";
				} else {
					msg = "Validating " + filetype + " MSSL Data: " + msslException.errorMsg + " line("
							+ (msslException.rowNumber) + ")";
				}

				utilityFunctions.logJAMMessage(eventId, "E", msslException.execStep, msg, errorCode);

				// Update EBT Event
				utilityFunctions.updateEBTEvent(ebtEventId, false);

				// Update JAM Event
				utilityFunctions.updateJAMEvent(false, eventId);

				utilityFunctions.logJAMMessage(eventId, "E", BusinessParameters.PROCESS_NAME_SAVE_MSSL_METERING_FILE,
						"MSSL " + filetype + " Metering Data (" + fileInfo.filename + ") not uploaded due to error",
						errorCode);

			}
		}
		catch (Exception e) {
			log.log(Priority.ERROR, logPrefix + "Exception in Activity " + msgStep + ". " + e.getMessage());
		}
		log.log(Priority.INFO, "Returning from service "+msgStep);
	}

}
