/**
 *
 */
package com.emc.settlement.backend.fileupload;


import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.CsvFileValidatorImpl;
import com.emc.settlement.common.MMVolumeUploaderImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.MMVolumeUploadException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.opencsv.CSVReader;
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
public class MMVolumeFileUpload {

	private static final Logger logger = Logger.getLogger(MMVolumeFileUpload.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	CsvFileValidatorImpl csvFileValidatorImpl;

	@Autowired
	MMVolumeUploaderImpl mmVolumeUploaderImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AlertNotificationImpl alertNotification;

	String msgStep = null;
	String logPrefix = "[MMV] ";

	@Transactional
	public Map<String, Object> captureMetaData(Map<String, Object> variableMap) {

		String eveId = null;
		Date uploadTime = null;
		String userId = null;
		String uploadUser = (String) variableMap.get("uploadUser");
		String ebtEventsRowId = null;
		String fileName = (String) variableMap.get("fileName");
		String comments = (String) variableMap.get("comments");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String uploadMethod = (String) variableMap.get("uploadMethod");

		final String activityName = "captureMetaData()";
		msgStep = BusinessParameters.PROCESS_NAME_MM_FILE_UPLOAD + "." + activityName;
		logPrefix = "[MMV] ";

		logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
		logger.info("Input parameters - eveId :"+eveId+" uploadTime :"+uploadTime+" userId :"+userId+" uploadUser :"+uploadUser+" ebtEventsRowId :"+ebtEventsRowId+" fileName :"+fileName+" comments :"+comments+" fileInfo :"+fileInfo+" uploadMethod :"+uploadMethod);

		//Create JAM Event
		eveId = utilityFunctions.createJAMEvent("EXE", "MM VOLUME FILE UPLOAD");
		uploadTime = new Date();

		//get User Id - Designated EMC settlement user shall upload the file using this module
		userId = utilityFunctions.getUserId(uploadUser);

		if (userId == null) {
			final MMVolumeUploadException mmVolumeUploadException = new MMVolumeUploadException( 1,  1,  "User Name: " + uploadUser + " is not valid.",  msgStep);
			throw mmVolumeUploadException;
		}

		//Create EBT Event use MMM as eventtype
		ebtEventsRowId = utilityFunctions.createEbtEvent( eveId,  "MMM", fileName,  userId,  comments,  fileInfo.transId);

		//Log JAM Message
		utilityFunctions.logJAMMessage( eveId, "I",  msgStep, "Receiving MM Volume file:" +
				fileName + ", Uploaded by: " + uploadUser + ", Upload Method: " +
				uploadMethod,  "");

		// Return from service -
		variableMap.put("eveId", eveId);
		variableMap.put("userId", userId);
		variableMap.put("uploadTime", uploadTime);
		variableMap.put("ebtEventsRowId", ebtEventsRowId);
		logger.info("Returning from service "+msgStep+" - ( eveId :" + eveId
				+ " userId :" + userId
				+ " uploadTime :" + uploadTime
				+ " ebtEventsRowId :" + ebtEventsRowId + ")");
		return variableMap;
	}

	@Transactional
	public void validateMMFile(Map<String, Object> variableMap) {

		String fileContent = (String) variableMap.get("fileContentName");
		String eveId = (String) variableMap.get("eveId");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String fileName = (String) variableMap.get("fileName");
		CsvFileValidator csvFileValidator = (CsvFileValidator) variableMap.get("csvFileValidator");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");

		final String activityName = "validateMMFile()";
		try
		{
			msgStep = BusinessParameters.PROCESS_NAME_MM_FILE_UPLOAD + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileContent : " + fileContent
					+ " eveId : " + eveId
					+ " ebtEventId : " + ebtEventId
					+ " fileName : " + fileName
					+ " csvFileValidator : " + csvFileValidator
					+ " ebtEventsRowId : " + ebtEventsRowId + ")");

			String content = null;
			if(fileContent != null){
				content = utilityFunctions.fileToString( fileContent);
			}

			// Validate File
			// Log JAM Message
			utilityFunctions.logJAMMessage( eveId,  "I",  msgStep, "Validating MM Volume File Format",  "");

			int MMV_CSV_NUM_COLS = 4;
			ebtEventId = "MM_VOLUME_INPUT_PREPARATION";

			if (csvFileValidatorImpl.isFilenameEmpty( fileName)) {
				throw new MMVolumeUploadException( 1,  1,  "FileName is empty", msgStep);
			}


			CSVReader csvReader = null;
			StringReader sReader = new StringReader( content);
			csvReader = new CSVReader(sReader);
			int validNum = csvFileValidatorImpl.readFileData( fileName,  MMV_CSV_NUM_COLS, csvReader, csvFileValidator);

			if (validNum != 0) {
				throw new MMVolumeUploadException( 1,  1,  csvFileValidator.message, msgStep);
			}

			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId,  "I",  msgStep, "Validating MM Volume File Format: MM Volume File Format is Valid","");

			// Store file into database
			utilityFunctions.storeStringIntoDbClob(ebtEventsRowId, content);

			// Store file into database
			mmVolumeUploaderImpl.validateMMVolumeData(csvFileValidator, eveId);

			mmVolumeUploaderImpl.uploadMMVolumeData(csvFileValidator, eveId);

		} catch (MMVolumeUploadException mmVolumeUploadException) {
			throw mmVolumeUploadException;
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			logger.log(Priority.INFO,logPrefix + "Exception when updating database in Verify and Load MM Volume Data. " + e.getMessage());
			throw new MMVolumeUploadException(1, 1, e.getMessage(),msgStep);
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void updateEBTEvent(Map<String, Object> variableMap)
	{
		String eveId = (String) variableMap.get("eveId");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");

		final String activityName = "updateEBTEvent()";
		msgStep = BusinessParameters.PROCESS_NAME_MM_FILE_UPLOAD + "." + activityName;

		logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " ebtEventsRowId : " + ebtEventsRowId + ")");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eveId, "I",  msgStep,  "Upload MM Volume File successfully finished.",  "");

		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent( ebtEventsRowId,  true);

		// Update JAM_EVENTS
		utilityFunctions.updateJAMEvent( true, eveId);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void mmFileUploadException(Map<String, Object> variableMap) {

		MMVolumeUploadException mmVolumeException = (MMVolumeUploadException) variableMap.get("mmVolumeException");
		String eveId = (String) variableMap.get("eveId");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");

		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( mmVolumeException : " + mmVolumeException
				+ " eveId : " + eveId
				+ " ebtEventsRowId : " + ebtEventsRowId + ")");

		// 1. log exception into BPM log
		logger.log(Priority.INFO, logPrefix + mmVolumeException.message);

		// 2. Log JAM Message
		String errorCode = Integer.toString(mmVolumeException.validationType) + "," + Integer.toString(mmVolumeException.validationNumber);

		utilityFunctions.logJAMMessage(eveId, "E", mmVolumeException.execStep, mmVolumeException.message, errorCode);

		// 3. Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(ebtEventsRowId, false);

		// 4. Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(false, eveId);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void sendExceptionNotification(Map<String, Object> variableMap) {
		logger.log(Priority.INFO, logPrefix + " Starting Activity: " + BusinessParameters.PROCESS_NAME_MM_FILE_UPLOAD + ".sendExceptionNotification" + " ...");

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String fileName = (String) variableMap.get("fileName");
		String uploadUser = (String) variableMap.get("uploadUser");
		MMVolumeUploadException mmVolumeException = (MMVolumeUploadException) variableMap.get("mmVolumeException");
		String uploadMethod = (String) variableMap.get("uploadMethod");

		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventsRowId : " + ebtEventsRowId
				+ " fileName : " + fileName
				+ " uploadUser : " + uploadUser
				+ " mmVolumeException : " + mmVolumeException
				+ " uploadMethod : " + uploadMethod + ")");

		List<Map<String, Object>> eventList = utilityFunctions.getNemEbtEvents(ebtEventsRowId);

		StringBuilder content = new StringBuilder();
		content.append("File Name: " + fileName + "\n\n");

		for(Map eventMap : eventList) {
			content.append("File Upload Date and Time: " + eventMap.get("uploaded_date") + "\n\n");
			content.append("File Upload User: " + uploadUser + "\n\n");
			content.append("User Comments: " + eventMap.get("comments") + "\n\n");
			content.append("Validated Time: " + eventMap.get("validated_date") + "\n\n");
			content.append("Valid: " + eventMap.get("valid_yn") + "\n\n");
		}

		content.append("Error Message: " + mmVolumeException.message);

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		
		AlertNotification alertNotifier = new AlertNotification();
		alertNotifier.businessModule = "MM Volume File Upload via " + uploadMethod;
		alertNotifier.content = content.toString();
		alertNotifier.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;
		alertNotifier.subject = "MM Volume file upload failed";
		alertNotifier.noticeType = "MM Volume File Upload";

		alertNotification.sendEmail(alertNotifier);

		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

}
