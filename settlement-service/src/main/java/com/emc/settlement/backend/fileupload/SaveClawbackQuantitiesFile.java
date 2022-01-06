/**
 * 
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.CLW_UPLEFDATE_VALIDATION;
import static com.emc.settlement.model.backend.constants.BusinessParameters.FILE_VALIDATION;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.ClawbackFileValidatorImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.ClawbackQuantitiesException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.ClawbackQuantities;
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
public class SaveClawbackQuantitiesFile {

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private ClawbackFileValidatorImpl clawbackFileValidatorImpl;

	@Autowired
	private AlertNotificationImpl alertNotification;
	
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
	private static final Logger logger = Logger.getLogger(SaveClawbackQuantitiesFile.class);
	String msgStep = "";

	@Transactional
	public Map<String, Object> captureMetaData(Map<String, Object> variableMap) throws ClawbackQuantitiesException {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String eventId = null;
		String fileContentName = (String) variableMap.get("fileContentName");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String userId = null;
		String ebtEventId = null;
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		String logPrefix = (String) variableMap.get("logPrefix");

		final String activityName = "captureMetadata()";
		try{
			msgStep = BusinessParameters.PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE + "." + activityName;

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " fileContentName : " + fileContentName
					+ " fromSEW : " + fromSEW
					+ " sewUploadEventsID : " + sewUploadEventsID
					+ " logPrefix : " + logPrefix + ")");

			// Create JAM Event
			eventId = utilityFunctions.createJAMEvent("EXE", "CLAWBACK UPLOAD FILE");
			fileInfo.uploadTime = new Date();
			String content;

			// read file into content
			content = utilityFunctions.fileToString(fileContentName);

			// 3.2 Uploaded file should not be of zero bytes
			if (content.length() < 1) {
				throw new ClawbackQuantitiesException(FILE_VALIDATION, 3202, 0,
						"Uploaded file should not be of zero bytes (Non Providing Facilities File Upload).", msgStep);
			}

			content = clawbackFileValidatorImpl.standardiseContentLineSeparator(content);

			// File Upload
			// 3.1 File name should not by empty
			if (fileInfo.filename == null || fileInfo.filename.length() < 1) {
				throw new ClawbackQuantitiesException(FILE_VALIDATION, 3201, 0,
						"File Name should not be empty (Non Providing Facilities File Upload).", msgStep);
			}

			// Log JAM Message
			String msg = "Receiving Non Providing Facilities File: ";

			if (content.length() > UtilityFunctions.getIntProperty("MAX_CLAWBACK_FILE_SIZE")) {
				throw new ClawbackQuantitiesException(FILE_VALIDATION, 3203, 0,
						"[UCM-3]: uploaded file size exceed limitation (Non Providing Facilities File Upload): "
								+ UtilityFunctions.getIntProperty("MAX_CLAWBACK_FILE_SIZE"),
						msgStep);
			}

			msg = msg + fileInfo.filename + ", Uploaded by: " + fileInfo.uploadUsername + ", Upload Method: "
					+ (fromSEW ? "SEW" : "Manual");
			utilityFunctions.logJAMMessage(eventId, "I", msgStep, msg, "");

			// Get User ID -- for File from SEW it should be SYSTEM/SEWSYSTEM user as we
			// cant access SEW User details
			userId = utilityFunctions.getUserId(fileInfo.uploadUsername);
			fileInfo.comments = fileInfo.comments.replace("'", "''");

			// Create EBT Event
			ebtEventId = utilityFunctions.createEbtEvent(eventId, fileInfo.fileType, fileInfo.filename, userId,
					fileInfo.comments, "");

			if (fromSEW == true) {
				// Update NEM_EBT_EVENTS table
				utilityFunctions.updateSEWEventId(sewUploadEventsID, ebtEventId);
			}

			logger.log(Priority.INFO,logPrefix + "Create EBT Event success with ID (Clawback File Upload): " + ebtEventId);
		}catch(ClawbackQuantitiesException clawbackQuantitiesException) {
			throw clawbackQuantitiesException;
		}catch(Exception e) {
			logger.error("Exception "+e.getMessage());
			logger.info(logPrefix + "Exception in <" + msgStep + "> " + e.getMessage());
			final ClawbackQuantitiesException clawbackQuantitiesException = new ClawbackQuantitiesException(FILE_VALIDATION, 0, 0, e.getMessage(), msgStep);
			throw clawbackQuantitiesException;
		}

		// Return from service -
 		variableMap.put("eventId", eventId);
 		variableMap.put("fileInfo", fileInfo);
 		variableMap.put("fileContentName", fileContentName);
 		variableMap.put("fromSEW", fromSEW);
 		variableMap.put("userId", userId);
 		variableMap.put("ebtEventId", ebtEventId);
 		variableMap.put("sewUploadEventsID", sewUploadEventsID);
		logger.info("Returning from service "+msgStep+" - ( fromSEW :" + fromSEW
				+ " fileInfo :" + fileInfo
				+ " eventId :" + eventId
				+ " userId :" + userId
				+ " ebtEventId :" + ebtEventId
				+ " sewUploadEventsID :" + sewUploadEventsID
				+ " fileContentName :" + fileContentName + ")");
 		return variableMap;
	}

	@Transactional
	public Map<String, Object> storeClawbackFile(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String fileContentName = (String) variableMap.get("fileContentName");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		String eventId = (String) variableMap.get("eventId");
		String sewOperationType = (String) variableMap.get("sewOperationType");
		String logPrefix = (String) variableMap.get("logPrefix");

		final String activityName = "storeClawbackFile()";
		msgStep = BusinessParameters.PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE + "." + activityName;

		logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " fileContentName : " + fileContentName
				+ " ebtEventId : " + ebtEventId
				+ " fromSEW : " + fromSEW
				+ " sewUploadEventsID : " + sewUploadEventsID
				+ " eventId : " + eventId
				+ " sewOperationType : " + sewOperationType
				+ " logPrefix : " + logPrefix + ")");

		try{
			// read file into content
			String content = utilityFunctions.fileToString(fileContentName);
			content = clawbackFileValidatorImpl.standardiseContentLineSeparator(content);

			// Store upload file into NEM_SETTLEMENT_RAW_FILES table
			logger.log(Priority.INFO, logPrefix + "Storing Clawback File with EBE_ID: " + ebtEventId);

			// UATSHARP-240 (item-3) - Inserting in new transaction
			utilityFunctions.storeStringIntoDbClob(ebtEventId, content);

			logger.log(Priority.INFO, logPrefix + "Starting Clawback Data Verification Process ...");

			// at this point do only the validation without storing parsed data into
			// NEM_CLAWBACK_QUANTITIES
			// the clawback data will be loaded into NEM_CLAWBACK_QUANTITIES when the file
			// is authorized
			List<ClawbackQuantities> myAcceptedClawbackQuantityList;
			boolean myClwqEmpty = false;

			if (fromSEW) {
				myAcceptedClawbackQuantityList = clawbackFileValidatorImpl.validateClawbackData(ebtEventId, eventId,
						content, fromSEW, myClwqEmpty);
			}

			if (content == null || content.length() == 0) {
				throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4201, 1,
						"File content is empty (EBE_ID=" + ebtEventId + ", Event ID = " + eventId + ")", msgStep);

			}

			StringBuffer contentStringBuffer = new StringBuffer(content);
			int indexOfDate = contentStringBuffer.indexOf("DATE");
			int indexOfDateBR = contentStringBuffer.indexOf("\n", indexOfDate);

			if (indexOfDate < 0) {
				throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 2, "DATE is missing", msgStep);
			}

			Date mySettlementDate = null;
			String dateString = content.substring(indexOfDate, indexOfDateBR);
			List myDateTokenList = utilityFunctions.stringToTokenList(dateString);

			if (myDateTokenList.size() < 2) {
				throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 2, "Trading Date is missing",
						msgStep);
			} else {
				// ITSM 15555 Settlement Date must be valid.
				try {
					String settDateStr = ((String) myDateTokenList.get(1));

					if (settDateStr.length() != 8) {
						throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 2,
								"Invalid date format for DATE", msgStep);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
					mySettlementDate = dateFormat.parse(settDateStr);
					if(!settDateStr.equals(dateFormat.format(mySettlementDate))){
						throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 2,
								"Invalid date format for DATE", msgStep);
					}
				} catch (ParseException e) {
					throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 2,
							"Invalid date format for DATE", msgStep);
				}
			}

			// 2.7.05 start - Preventing Clawback File Upload before Clawback Effective Date
			boolean RSVREGZeroQtyDispEffDt = utilityFunctions.isAfterRSVREGZeroQtyDispEffDt(mySettlementDate);

			if (!RSVREGZeroQtyDispEffDt) {
				throw new ClawbackQuantitiesException(CLW_UPLEFDATE_VALIDATION, 4210, 0,
						"Non Providing Facilities File Uploaded for Settlement Date "
								+ new SimpleDateFormat("dd MMM yyyy").format(mySettlementDate)
								+ " which is earlier than Non Providing Facilities Change Effective Date "
								+ new SimpleDateFormat("dd MMM yyyy")
										.format(utilityFunctions.getSysParamTime("MPRPT_RSVREG_ZEROQTY_DISP_EFDT"))
								+ " in NEMS System. This File will not be Uploaded.",
						msgStep);

			}

			// 2.7.05 ends
			// check the TD+9 5pm
			if (fromSEW) {
				clawbackFileValidatorImpl.validateFileSumbissionDeadline(sewUploadEventsID, mySettlementDate, fromSEW,
						sewOperationType);
			}

			// Update NEM_EBT_EVENTS table
			utilityFunctions.updateSettlementDateEBTEvent(mySettlementDate,ebtEventId);
			// Verification Process is successful");
			if (fromSEW == false) {
				logger.log(Priority.INFO, "Starting Verify and Load Clawback Data Process ...");
			}

			if (fromSEW) {
				// Update EBT Event
				utilityFunctions.updateSEWFileUploadStatus(ebtEventId, true);
			}

		} catch(ClawbackQuantitiesException clawbackQuantitiesException) {

			throw clawbackQuantitiesException;
		} catch(Exception e) {
			
			logger.error("Exception "+e.getMessage());
		}

		variableMap.put("fromSEW", fromSEW);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("fileContentName", fileContentName);
		logger.info("Returning from service "+msgStep+" - ( fromSEW :" + fromSEW
				+ " fileInfo :" + fileInfo
				+ " fileContentName :" + fileContentName + ")");

 		return variableMap;
	}

	@Transactional
	public Map<String, Object> updateEvent(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		ClawbackQuantitiesException clawbackEx = (ClawbackQuantitiesException) variableMap.get("clawbackEx");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		String eventId = (String) variableMap.get("eventId");
		String logPrefix = (String) variableMap.get("logPrefix");

		String activityName = "updateEvent()";

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE + "." + activityName;
		try {
			logger.log(Priority.INFO, logPrefix + "Starting Activity " + activityName);
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " clawbackEx : " + clawbackEx
					+ " fromSEW : " + fromSEW
					+ " ebtEventId : " + ebtEventId
					+ " sewUploadEventsID : " + sewUploadEventsID
					+ " eventId : " + eventId
					+ " logPrefix : " + logPrefix + ")");

			logger.log(Priority.INFO, logPrefix + "Clawback File Exception: VALTYPE - " + clawbackEx.validationType +
					", VALNUM - " + clawbackEx.validationNumber + ", ROWNUM: " +
					clawbackEx.rowNumber + ", MSG: " + clawbackEx.errorMsg);

			String errorCode = clawbackEx.validationType + "," + Integer.toString(clawbackEx.validationNumber);
			String msg;

			if (clawbackEx.validationType.equals(FILE_VALIDATION)) {
				msg = "Validating Non Providing Facilities File: " + clawbackEx.errorMsg + " line(" + String.valueOf(
						clawbackEx.rowNumber)+")";
			} else if (clawbackEx.validationType.equals(CLW_UPLEFDATE_VALIDATION)) {
				msg = "Validating Non Providing Facilities File Upload Effective Date: " + clawbackEx.errorMsg;
			} else if (clawbackEx.validationType.equals("CLAWBACK_UPLOAD_CUTOFF_TIME_VALIDATION")) {
				msg = "Validating Non Providing Facilities File Upload Cut Off Time from SEW: " + clawbackEx.errorMsg;
			} else {
				msg = "Validating Non Providing Facilities File/Data: " + clawbackEx.errorMsg + " line(" + String.valueOf(
						clawbackEx.rowNumber)+")";
			}

			// Update EBT Event
			if (fromSEW) {
				// Update EBT Event
				utilityFunctions.updateSEWFileUploadStatus(ebtEventId, false);

				utilityFunctions.updateSEWFileProcessingStatus("UPL", msg, sewUploadEventsID,"");
			}

			utilityFunctions.updateEBTEvent(ebtEventId, false);

			// Update JAM Event
			utilityFunctions.updateJAMEvent(false, eventId);

			utilityFunctions.logJAMMessage(eventId, "E", clawbackEx.execStep, msg, errorCode);

			utilityFunctions.logJAMMessage(eventId, "E", BusinessParameters.PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE, "Non Providing Facilities File (" + fileInfo.filename + ") not uploaded due to error", errorCode);
		}
		catch (Exception e) {
			logger.log(Priority.INFO, logPrefix + "Exception in Activity " + activityName + ". " + e.getMessage());
		}
		variableMap.put("clawbackEx", clawbackEx);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}

	@Transactional
	public Map<String, Object> alertNotify(Map<String, Object> variableMap) {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		ClawbackQuantitiesException clawbackEx = (ClawbackQuantitiesException) variableMap.get("clawbackEx");
		String eventId = (String) variableMap.get("eventId");

		String activityName = "alertNotify()";

		msgStep = BusinessParameters.PROCESS_NAME_SAVE_CLAWBACK_QUANTITIES_FILE + "." + activityName;
		logger.log(Priority.INFO, "Input Parameters for " + msgStep + "  ( fileInfo : " + fileInfo + " clawbackEx : "
				+ clawbackEx + " eventId : " + eventId + ")");

		try {
			// ITSM 15386 Changes for Null Pointer Exception during sending upload failure
			// email
			String fileInfoSettlementDateStr;

			if (fileInfo.settlementDate == null) {
				fileInfoSettlementDateStr = "-";
			} else {
				fileInfoSettlementDateStr = format(fileInfo.settlementDate, UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"));
			}

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			// end
			AlertNotification alert = new AlertNotification();
			alert.businessModule = "Receive Non Providing Facilities File";
			alert.content = "Filename: " + fileInfo.filename + ";\n\n" + "File upload time: "
					+ format(fileInfo.uploadTime, UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")) + ";\n\n" + "Settlement Date: "
					+ fileInfoSettlementDateStr + ";\n\n" + "File upload user: " + fileInfo.uploadUsername + ";\n\n"
					+ "User Comments: " + fileInfo.comments + ";\n\n" + "Validated time: "
					+ format(new Date(), UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")) + ";\n\n" + "Valid: N;\n\n"
					+ "Error Message: " + clawbackEx.errorMsg + " line(" + String.valueOf(clawbackEx.rowNumber)
					+ "). Event ID: " + eventId;
			alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;

			alert.noticeType = "PSO Non Providing Facilities Data Input Preparation";
			alert.subject = "PSO Non Providing Facilities File Upload failed";
			alertNotification.sendEmail(alert);
		} catch (Exception e) {
			logger.log(Priority.ERROR, "Exception in Activity " + activityName + ". " + e.getMessage());
			e.printStackTrace();
		}
		variableMap.put("clawbackEx", clawbackEx);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}
}
