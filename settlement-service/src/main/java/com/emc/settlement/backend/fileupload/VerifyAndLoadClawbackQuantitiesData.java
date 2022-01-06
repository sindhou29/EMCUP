/**
 *
 */
package com.emc.settlement.backend.fileupload;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.ClawbackFileValidatorImpl;
import com.emc.settlement.common.PavPackageImpl;
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
public class VerifyAndLoadClawbackQuantitiesData {

	protected static final Logger logger = Logger.getLogger(VerifyAndLoadClawbackQuantitiesData.class);

	public String msgStep;
	public String logPrefix;

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	ClawbackFileValidatorImpl clawbackFileValidatorImpl;
	@Autowired
	PavPackageImpl pavPackageImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AlertNotificationImpl alertNotification;

	@Transactional
	public Map<String, Object> verifyAndLoadClawbackData(Map<String, Object> variableMap) throws ClawbackQuantitiesException {

		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String userId = null;
		String eventId = (String) variableMap.get("eventId");
		Boolean clwqEmpty = false;
		String sewOperationType = (String) variableMap.get("sewOperationType");
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		String logPrefix = (String) variableMap.get("logPrefix");

		final String activityName = "VerifyAndLoadClawbackData()";

		try{
			msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " fromSEW : " + fromSEW
					+ " ebtEventId : " + ebtEventId
					+ " eventId : " + eventId
					+ " sewOperationType : " + sewOperationType
					+ " sewUploadEventsID : " + sewUploadEventsID
					+ " logPrefix : " + logPrefix + ")");

			String clawbackFilename;

			String content = null;
			boolean isFormatWithHeader;
			String[] validSettlementDates;

			// Update File Approval Processing Status for SEW Upload
			if (fromSEW) {
				utilityFunctions.updateSEWApproveProcessStatus( ebtEventId, "Y");
			}

			// ////////////////////////////////////////////////////////////////////
			// 	Load Clawback File from NEM_SETTLEMENT_RAW_FILES
			// ////////////////////////////////////////////////////////////////////
			String sqlCmd = " select nee.filename, nee.uploaded_date, nee.uploaded_by, " +
					" nee.comments, nsr.raw_file from nem_ebt_events nee, " +
					" nem_settlement_raw_files nsr " +
					" where nsr.ebe_id = nee.id and nee.id = ? ";

			Object[] params = new Object[1];
			params[0] =  ebtEventId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, params);
			for (Map row : list) {
				clawbackFilename = (String)row.get("filename");
				fileInfo.filename = clawbackFilename;
				fileInfo.uploadTime = (Date)row.get("uploaded_date");

				// fileInfo.uploadUsername = String(row[3])
				userId = (String)row.get("uploaded_by");
				fileInfo.comments =(String)row.get("comments");
				content = (String)row.get("raw_file");

			}

			// this function ClawbackFileValidator.validateClawbackData will check TD+9 5PM
			List<ClawbackQuantities> myAcceptedClawbackList = new ArrayList<ClawbackQuantities>();

			if (! fromSEW) {
				// Manual Upload
				myAcceptedClawbackList = clawbackFileValidatorImpl.validateClawbackData(ebtEventId, eventId, content, fromSEW, clwqEmpty);
			}
			else if (fromSEW) {
				// SEW Approve the File
				// ITSM 15555 Added the code block to prevent any clawback data entry during Approval phase when the file having format or Data issue
				// but still SEW can approve it mistakenly
				// Start

				Integer successClawbackFileUpl = 0;
				sqlCmd = " select count(*) CNT from nem_ebt_events nee " +
						" where nee.event_type='CLS' and nee.UPLOAD_STATUS='A' and nee.id =? ";

				Object[] params1 = new Object[1];
				params1[0] =  ebtEventId;
				List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCmd, params1);
				for (Map row : list1) {
					successClawbackFileUpl = ((BigDecimal)row.get("CNT")).intValue();
				}

				// End
				if (successClawbackFileUpl == 1) {
					// ITSM 15555
					myAcceptedClawbackList = clawbackFileValidatorImpl.loadSEWApproveClawbackData(ebtEventId, content, fromSEW, clwqEmpty, eventId);
				}
				else {
					// ITSM 15555
					throw new Exception("SEWAPPROVE_DATA_INSERT_RECHECK");

					// ITSM 15555
				}

				// ITSM 15555
			}

			ClawbackQuantities myFirstAcceptedClawback = ((ClawbackQuantities) myAcceptedClawbackList.get(0));
			Date mySettlementDate = myFirstAcceptedClawback.settlementDate;

			// check the TD+5 5PM and TD+9 5PM
			if (fromSEW) {
				clawbackFileValidatorImpl.validateFileSumbissionDeadline(sewUploadEventsID, mySettlementDate, fromSEW, sewOperationType);
			}

			int version;
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,  "Inserting Non Providing Facilities Data into Database", "");

			clwqEmpty = ( myAcceptedClawbackList.get(0)!=null && myAcceptedClawbackList.get(0).getRecordType().equalsIgnoreCase("EMPTY")); 
			if (clwqEmpty == true) {
				myFirstAcceptedClawback.sewUploadEventsID = sewUploadEventsID;

				// Update NEM_EBT_EVENTS table set comments to EMPTY for empty type clawback data
				utilityFunctions.updateEBTEventComments(ebtEventId, "EMPTY");

				String sqlClawback = " INSERT INTO NEM.NEM_CLAWBACK_QUANTITIES " +
						" (ID, VERSION, SETTLEMENT_DATE, PERIOD, RECORD_TYPE, " +
						"  NDE_ID, NDE_VERSION, ANCILLARY_TYPE, SEW_UPLOAD_EVENTS_ID) " +
						" Values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

				logger.log(Priority.INFO,logPrefix + "EMPTY record type found, empty type Clawback data will be inserted into NEM_CLAWBACK_QUANTITIES. EBT ID: " + ebtEventId + ", Event ID: " + eventId);

				String theNextPackageVersion = pavPackageImpl.createClawbackPackage(mySettlementDate);
				version = Integer.parseInt(theNextPackageVersion);

				Object[] params1 = new Object[9];
				params1[0] =  utilityFunctions.getEveId();
				params1[1] =  version;
				params1[2] =  utilityFunctions.convertUDateToSDate(myFirstAcceptedClawback.settlementDate);
				params1[3] =  myFirstAcceptedClawback.period;
				params1[4] =  myFirstAcceptedClawback.recordType;
				params1[5] =  myFirstAcceptedClawback.ndeId;
				params1[6] =  myFirstAcceptedClawback.ndeVersion;
				params1[7] =  myFirstAcceptedClawback.ancillaryType;
				params1[8] =  myFirstAcceptedClawback.sewUploadEventsID;
				jdbcTemplate.update(sqlClawback, params1);
			}
			else {
				// after the validation succeeded insert the clawback data into NEM_CLAWBACK_QUANTITIES
				String sqlClawback = " INSERT INTO NEM.NEM_CLAWBACK_QUANTITIES " +
						" (ID, VERSION, SETTLEMENT_DATE, PERIOD, RECORD_TYPE, " +
						"  NDE_ID, NDE_VERSION, ANCILLARY_TYPE, SEW_UPLOAD_EVENTS_ID) " +
						" Values ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

				logger.log(Priority.INFO,logPrefix + "Start Inserting Clawback for EBT ID: " +
						ebtEventId + ", Event ID: " + eventId);

				String theNextPackageVersion = pavPackageImpl.createClawbackPackage(mySettlementDate);
				version = (int) Double.parseDouble(theNextPackageVersion);

				for (ClawbackQuantities validClawback : myAcceptedClawbackList) {
					validClawback.settlementDate = mySettlementDate;
					validClawback.sewUploadEventsID = sewUploadEventsID;

					Object[] params1 = new Object[9];
					params1[0] =  utilityFunctions.getEveId();
					params1[1] =  version;
					params1[2] =  utilityFunctions.convertUDateToSDate(validClawback.settlementDate);
					params1[3] =  validClawback.period;
					params1[4] =  validClawback.recordType;
					params1[5] =  validClawback.ndeId;
					params1[6] =  validClawback.ndeVersion;
					params1[7] =  validClawback.ancillaryType;
					params1[8] =  validClawback.sewUploadEventsID;
					jdbcTemplate.update(sqlClawback, params1);
				}

				logger.log(Priority.INFO,logPrefix + "End Inserting Clawback Data for EBT ID: " + ebtEventId + ", Event ID: " + eventId + ", Record Count: " + myAcceptedClawbackList.size());

			}

			utilityFunctions.logJAMMessage(eventId, "I", msgStep, "Inserted " + myAcceptedClawbackList.size() + " rows " +
							"for Settlement Date: " + new SimpleDateFormat("dd MMM yyyy").format(myFirstAcceptedClawback.settlementDate) +
							" with new Non Providing Facilities Pkg Version: " + version,
					"");
		} catch(SQLException sqlException) {
			sqlException(sqlException, msgStep);
		} catch (ClawbackQuantitiesException clawbackQuantitiesException) {
			throw clawbackQuantitiesException;
		}
		catch (Exception e) {
			logger.log(Priority.INFO,logPrefix + "Exception when updating database in Verify and Load Clawback Data. " + e.getMessage());

			// ITSM 15555 Added the below code
			// Start
			if (e.getMessage().equals("SEWAPPROVE_DATA_INSERT_RECHECK")) {
				// ITSM 15555 Added the code
				String errClawBkMsg = null;
				String sqlCmd = " SELECT text||'. Contact EMC HelpDesk.' TEXT  FROM (SELECT * FROM nem.jam_messages  WHERE eve_id =? " +
						" AND severity = 'E'  ORDER BY seq) WHERE ROWNUM < 2 ";

				Object[] params = new Object[1];
				params[0] =  eventId;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, params);
				for (Map row : list) {
					errClawBkMsg = (String)row.get("TEXT");
				}


				if (errClawBkMsg == null) {
					errClawBkMsg = "File Approval has issues. Contact EMC HelpDesk.";
				}

				// End
				ClawbackQuantitiesException clawbackQuantitiesException = new ClawbackQuantitiesException("SEWAPPROVE_DATA_INSERT_RECHECK",
						0, 0, errClawBkMsg,
						msgStep);
				throw clawbackQuantitiesException;
			}
			else {
				ClawbackQuantitiesException clawbackQuantitiesException = new ClawbackQuantitiesException("DATA INSERT", 0, 0,
						e.getMessage(), msgStep);
				throw clawbackQuantitiesException;
			}

			// End
		}

		variableMap.put("fromSEW", fromSEW);
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("userId", userId);
		variableMap.put("eventId", eventId);
		variableMap.put("clwqEmpty", clwqEmpty);
		variableMap.put("sewUploadEventsID", sewUploadEventsID);
		variableMap.put("sewOperationType", sewOperationType);
		logger.info("Returning from service "+msgStep+" - ( fromSEW :" + fromSEW
				+ " ebtEventId :" + ebtEventId
				+ " fileInfo :" + fileInfo
				+ " userId :" + userId
				+ " eventId :" + eventId
				+ " clwqEmpty :" + clwqEmpty
				+ " sewUploadEventsID :" + sewUploadEventsID
				+ " sewOperationType :" + sewOperationType + ")");
		return variableMap;
	}

	@Transactional
	public void updateEvent(Map<String, Object> variableMap)
	{
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String eventId = (String) variableMap.get("eventId");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		String logPrefix = (String) variableMap.get("logPrefix");


		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + ".updateEvent()";

		logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fromSEW : " + fromSEW
				+ " ebtEventId : " + ebtEventId
				+ " eventId : " + eventId
				+ " sewUploadEventsID : " + sewUploadEventsID
				+ " logPrefix : " + logPrefix + ")");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eventId,  "I", msgStep,
				"Upload Non Providing Facilities File successfully finished.",
				"");


		// Update SEW_UPLOAD_EVENTS table
		try {
			if (fromSEW) {
				utilityFunctions.updateSEWFileProcessingStatus("APP", "", sewUploadEventsID, "Success");

				// Update File Approval Outcome Status for SEW Approve
				utilityFunctions.updateSEWApprovalOutcome(ebtEventId, true);
			}
		}
		catch (Exception e) {
			throw new ClawbackQuantitiesException("UPLOAD SEW",
					0, 0, e.getMessage(),
					msgStep);
		}

		// Update EBT Event
		utilityFunctions.updateEBTEvent( ebtEventId, true);

		// Update JAM Event
		utilityFunctions.updateJAMEvent(true, eventId);

		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void updateFailEvent(Map<String, Object> variableMap)
	{

		String ebtEventId = (String) variableMap.get("ebtEventId");
		String eventId = (String) variableMap.get("eventId");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String sewUploadEventsID = (String) variableMap.get("sewUploadEventsID");
		ClawbackQuantitiesException clawbackException = (ClawbackQuantitiesException) variableMap.get("clawbackException");
		String logPrefix = (String) variableMap.get("logPrefix");

		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + ".updateFailEvent()";
		
		logger.log(Priority.INFO,logPrefix + "Starting Activity  updateEfailEvent()");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " fromSEW : " + fromSEW
				+ " ebtEventId : " + ebtEventId
				+ " eventId : " + eventId
				+ " sewUploadEventsID : " + sewUploadEventsID
				+ " clawbackException : " + clawbackException
				+ " logPrefix : " + logPrefix + ")");

		try {
			logger.log(Priority.INFO,logPrefix + "Clawback Data Exception: VALTYPE - " + clawbackException.validationType +
					", VALNUM - " + clawbackException.validationNumber + ", ROWNUM: " +
					clawbackException.rowNumber + ", MSG: " + clawbackException.errorMsg);

//			String errorCode = clawbackException.validationType + "," + (String.valueOf(clawbackException.validationNumber));
			String errorCode = clawbackException.validationType;
			String msg;

			if (clawbackException.validationType.equals("DATA INSERT")) {
				msg = "Validating Non Providing Facilities Data: " + clawbackException.errorMsg + " line(" + String.valueOf(clawbackException.rowNumber) + ")";
			}
			else if (clawbackException.validationType.equals("CLAWBACK_UPLOAD_CUTOFF_TIME_VALIDATION")) {
				msg = "Validating Non Providing Facilities file Upload Cut Off Time from SEW: " + clawbackException.errorMsg;
			}
			else if (clawbackException.validationType.equals("SEWAPPROVE_DATA_INSERT_RECHECK")) {
				// ITSM 15555
				msg = clawbackException.errorMsg;
			}
			else {
				msg = "Validating Non Providing Facilities File/Data: " + clawbackException.errorMsg + " line(" + String.valueOf(clawbackException.rowNumber) + ")";
			}

			if (fromSEW) {
				utilityFunctions.updateSEWFileProcessingStatus( "APP", msg, sewUploadEventsID, "Approve Failed");

				// Update File Approval Outcome Status for SEW Approve
				utilityFunctions.updateSEWApprovalOutcome( ebtEventId, false);
			}

			// Update EBT Event
			utilityFunctions.updateEBTEvent( ebtEventId,  false);

			// Update JAM Event
			utilityFunctions.updateJAMEvent( false,  eventId);

			utilityFunctions.logJAMMessage( eventId,  "E", clawbackException.execStep,
					msg,  errorCode);

			utilityFunctions.logJAMMessage( eventId,  "E", BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + ".updateEfailEvent()",
					"Non Providing Facilities Data (" + fileInfo.filename + ") not uploaded due to error",
					errorCode);
		}
		catch (Exception e) {
			logger.log(Priority.INFO,logPrefix + "Exception in Activity " + BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + ".updateEfailEvent() " + e.getMessage());
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void alertNotify(Map<String, Object> variableMap)
	{

		String eventId = (String) variableMap.get("eventId");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		ClawbackQuantitiesException clawbackException = (ClawbackQuantitiesException) variableMap.get("clawbackException");
		String logPrefix = (String) variableMap.get("logPrefix");

		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_CLAWBACKQUANTITIES_DATA + ".alertNotify()";
		
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " eventId : " + eventId
				+ " clawbackException : " + clawbackException
				+ " eventId : " + eventId
				+ " logPrefix : " + logPrefix + ")");

		// ITSM 15386 Changes for Null Pointer Exception during sending upload failure email
		String fileInfoSettlementDateStr;

		if (fileInfo.settlementDate == null) {
			fileInfoSettlementDateStr = "-";
		}
		else {
			fileInfoSettlementDateStr = new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")).format(fileInfo.settlementDate);
		}

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		// end
		AlertNotification alert = new AlertNotification();
		alert.businessModule = "Verify And Load Non Providing Facilities Data";
		alert.content = "Filename: " + fileInfo.filename + ";\n\n" +
				"File upload time: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")).format(fileInfo.uploadTime) + ";\n\n" +
				"Settlement Date: " + fileInfoSettlementDateStr + ";\n\n" +
				"File upload user: " + fileInfo.uploadUsername + ";\n\n" +
				"User Comments: " + fileInfo.comments + ";\n\n" +
				"Validated time: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")).format(new Date()) + ";\n\n" +
				"Valid: N;\n\n" +
				"Error Message: " + clawbackException.errorMsg + " line(" + String.valueOf(clawbackException.rowNumber) + "). Event ID: " + eventId;
		alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//BusinessParameters.FILE_UPLOAD_FAIL_EMAIL;
		alert.subject = "Non Providing Facilities File Upload Failed";
		alert.noticeType = "PSO Non Providing Facilities Data Input Preparation";
		alertNotification.sendEmail(alert);

		// end    // ITSM 15555
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	public void sqlException(SQLException sqlException, String msgStep) {
		ClawbackQuantitiesException clawbackException = new ClawbackQuantitiesException();
		clawbackException.errorMsg = sqlException.getMessage();
		clawbackException.execStep = msgStep;
		clawbackException.rowNumber = 0;
		clawbackException.validationNumber = 0;
		clawbackException.validationType = "SQL EXCEPTION";
		throw clawbackException;
	}
}
