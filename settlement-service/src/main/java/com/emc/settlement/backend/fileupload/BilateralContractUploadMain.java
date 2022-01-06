/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.BILATERAL_CONTRACT_CSV_NUM_COLS;

import java.io.StringReader;
import java.util.Map;

import com.emc.settlement.common.CsvFileValidatorImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.BilateralContractUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author DWTN1561
 *
 */
@Service
public class BilateralContractUploadMain {

	/**
	 *
	 */
	public BilateralContractUploadMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */

	private static final Logger logger = Logger.getLogger(BilateralContractUploadMain.class);

	private String msgStep;

	private String logPrefix;

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	CsvFileValidatorImpl csvFileValidatorImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public Map<String, Object> captureFileUploadParameters(Map<String, Object> variableMap) throws BilateralContractUploadException {

		try {

			String comments = (String) variableMap.get("comments");
			String eveId = null;
			String fileContentName = (String) variableMap.get("fileContentName");
			UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
			String filename = (String) variableMap.get("filename");
			Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
			String inputPTPID = (String) variableMap.get("inputPTPID");
			String sewUploadEventsId = (String) variableMap.get("sewUploadEventsId");
			String uploadMethod = (String) variableMap.get("uploadMethod");
			String uploadUser = (String) variableMap.get("uploadUser");
			String userId = null;
			String fileContent = null;
			String ebtEventsRowId = null;


			logger.log(Priority.INFO, "Input Parameters for captureFileUploadParameters  ( comments : " + comments + " fileContentName : " + fileContentName + " fileInfo : "
					+ fileInfo + " filename : " + filename + " fromSEW : " + fromSEW + " inputPTPID : " + inputPTPID +
					" sewUploadEventsId : " + sewUploadEventsId + " uploadMethod : " + uploadMethod + " uploadUser : " + uploadUser + ")");
			final String activityName = "captureFileUploadParameters";
			String msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

			logPrefix = "[" + fileInfo.fileType + "] "; //ITSM 17062 BIL Changes

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

			fileContent = utilityFunctions.fileToString(fileContentName);

			// User ID  -- for File from SEW it should be SYSTEM/SSRSYSTEM user as we cant access SEW User details
			try {
				userId = utilityFunctions.getUserId(uploadUser);
			}
			catch (EmptyResultDataAccessException e) {
				throw new BilateralContractUploadException(-1, 1, "User name " + uploadUser + " is not valid", msgStep);
			}

			if (userId == null) {
				throw new BilateralContractUploadException(-1, 1, "User name " + uploadUser + " is not valid", msgStep);
			}

			// Create JAM Event
			eveId = utilityFunctions.createJAMEvent("EXE", "BILATERAL CONTRACT UPLOAD");

			//ITSM 17062 BIL Changes -- Start
			// Create EBT Event
			ebtEventsRowId = utilityFunctions.createEbtEvent(eveId, fileInfo.fileType, filename, userId, comments, "");

			if (fromSEW) {

				// Update NEM_EBT_EVENTS table
				utilityFunctions.updateSEWEventId(sewUploadEventsId, ebtEventsRowId);
			}

			logger.info(logPrefix + "Create EBT Event success with ID (Bilateral File Upload): " + ebtEventsRowId);

			//ITSM 17062 BIL Changes -- End
			// Log JAM Message [Correction on EMCS-457]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Receiving Bilateral Contract file: " + filename + ", Uploaded by: " +
							//uploadUser + ", Upload Method: " + uploadMethod,      //ITSM 17062 BIL Changes -- commented
							uploadUser + ", Upload Method: " + (fromSEW ? "SEW" : "Manual"),    //ITSM 17062 BIL Changes
					"");

			variableMap.put("ebtEventsRowId", ebtEventsRowId);
			variableMap.put("fileContent", fileContent);
			variableMap.put("eveId", eveId);
			variableMap.put("userId", userId);
			logger.info("Returning from service "+msgStep+" - (ebtEventsRowId :" + ebtEventsRowId + " fileContent :" + fileContent + " eveId :" + eveId + " userId :" + userId + ")");
		}
		catch (BilateralContractUploadException e) {
			throw e;
		}
		catch (DataAccessException e) {
			throw new BilateralContractUploadException(-1, 1, e.getMessage(), msgStep);
		}

		return variableMap;
	}


	@Transactional
	public void storeCSVFileIntoDatabase(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String fileContent = (String) variableMap.get("fileContent");

		logger.log(Priority.INFO, "Input Parameters for storeCSVFileIntoDatabase  ( ebtEventsRowId : " + ebtEventsRowId + " fileContent : " + fileContent + ")");

		final String activityName = "storeCSVFileIntoDatabase()";
		msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		// Log JAM Message [Correction on EMCS-457]
		utilityFunctions.storeStringIntoDbClob(ebtEventsRowId, fileContent);

		logger.info("Returning from service "+msgStep);

	}

	@Transactional
	public Map<String, Object> validateFile(Map<String, Object> variableMap) {

		try {
			CsvFileValidator csvFileValidator = (CsvFileValidator) variableMap.get("csvFileValidator");
			String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
			String eveId = (String) variableMap.get("eveId");
			String fileContent = (String) variableMap.get("fileContent");
			String filename = (String) variableMap.get("filename");
			Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
			String sewUploadEventsId = (String) variableMap.get("sewUploadEventsId");
			String uploadMethod = (String) variableMap.get("uploadMethod");
			String uploadUser = (String) variableMap.get("uploadUser");


			logger.log(Priority.INFO, "Input Parameters for validateFile  ( ebtEventsRowId : " + ebtEventsRowId + " csvFileValidator : " + csvFileValidator +
					" fileContent : " + fileContent + " filename : " + filename + " fromSEW : " + fromSEW + " sewUploadEventsId : " + sewUploadEventsId + " uploadUser : " + uploadUser + " uploadMethod : " + uploadMethod + ")");
			final String activityName = "validateFile()";
			msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

			BilateralContractUploadException blcException = new BilateralContractUploadException(0, 1, "", msgStep);


			if (csvFileValidatorImpl.isFilenameEmpty(filename) == true) {
				throw new BilateralContractUploadException(1, 1, "Filename is empty", msgStep);
			}

			csvFileValidator.setCsv_column_count(BILATERAL_CONTRACT_CSV_NUM_COLS);
			CSVReader csvReader = null;
			StringReader sReader = new StringReader(fileContent);
			csvReader = new CSVReader(sReader);
			blcException.setValidationNumber( csvFileValidatorImpl.readFileData(filename, BILATERAL_CONTRACT_CSV_NUM_COLS, csvReader, csvFileValidator));

			if (blcException.getValidationNumber() != 0) {
				throw new BilateralContractUploadException(1, 1, csvFileValidator.getMessage(), msgStep);
			}

			// Log JAM Message [Correction on EMCS-457]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Validating Bilateral Contract File Format: Bilateral Contract File Format is Valid",
					"");

			variableMap.put("csvFileValidator", csvFileValidator);
			logger.info("Returning from service "+msgStep+" - csvFileValidator :" + csvFileValidator);
		}
		catch (BilateralContractUploadException e) {
			throw e;
		}
		return variableMap;
	}

	@Transactional
	public void updateEBTEvent(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		String sewUploadEventsId = (String) variableMap.get("sewUploadEventsId");
		String uploadUser = (String) variableMap.get("uploadUser");

		logger.log(Priority.INFO, "Input Parameters for updateEBTEvent  ( ebtEventsRowId : " + ebtEventsRowId + " fromSEW : " + fromSEW +
				" sewUploadEventsId : " + sewUploadEventsId + " uploadUser : " + uploadUser + ")");

		final String activityName = "updateEBTEvent()";
		msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Upload Bilateral Contract file successfully finished.", "");

		//Start of ITSM 17062 BIL Changes
		// Update SEW_UPLOAD_EVENTS table
		if (fromSEW) {
			// Update File Approval Outcome Status for SEW Approve
			utilityFunctions.updateSEWFileUploadStatus(ebtEventsRowId, true);

			//ITSM 17002.18 commented as not required in BIL
			utilityFunctions.updateSEWProcessStatusAutoAuth("", sewUploadEventsId);
		}
		//End of ITSM 17062 BIL Changes

		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent(ebtEventsRowId, true);

		// Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(true, eveId);

		logger.info("Returning from service "+msgStep);
	}

	@Transactional
	public Map<String, Object> handleException(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String errorMsg = (String) variableMap.get("errorMsg");
		String eveId = (String) variableMap.get("eveId");
		String sewUploadEventsId = (String) variableMap.get("sewUploadEventsId");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		BilateralContractUploadException bilateralContractUploadException = (BilateralContractUploadException) variableMap.get("bilateralContractUploadException");

		final String activityName = "handleException()";
		msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

		logger.log(Priority.INFO, "Input Parameters for "+msgStep+" ( ebtEventsRowId :" + ebtEventsRowId + " eveId: " + eveId + " sewUploadEventsId :" + sewUploadEventsId +
		" fromSEW : "+ fromSEW + " bilateralContractUploadException : "+ bilateralContractUploadException);

		if(bilateralContractUploadException != null)
		{
			errorMsg = bilateralContractUploadException.getMessage();

			logger.warn(logPrefix + errorMsg);

			String errorCode = String.valueOf(bilateralContractUploadException.getValidationType()) + ","
					+ String.valueOf(bilateralContractUploadException.getValidationNumber());

			// Log JAM Message
			utilityFunctions.logJAMMessage(eveId, "E", msgStep, errorMsg, errorCode);

			if (fromSEW) {

				// ITSM 17002.18 For cases of auto authorizations
				utilityFunctions.updateSEWProcessStatusAutoAuth(errorMsg, sewUploadEventsId);

				// Update File Upload Outcome Status for SEW Approve
				utilityFunctions.updateSEWFileUploadStatus(ebtEventsRowId, false);
			}

			// Update NEM_EBT_EVENTS table
			utilityFunctions.updateEBTEvent(ebtEventsRowId, false);

			// Update JAM_EVENTS table
			utilityFunctions.updateJAMEvent(false, eveId);

		}
		variableMap.put("errorMsg", errorMsg);
		logger.info("Returning from service "+msgStep+" - errorMsg :" + errorMsg);
		return variableMap;
	}

	@Transactional
	public void sendExceptionNotification(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String errorMsg = (String) variableMap.get("errorMsg");
		String filename = (String) variableMap.get("filename");
		String uploadMethod = (String) variableMap.get("uploadMethod");
		String uploadUser = (String) variableMap.get("uploadUser");

		final String activityName = "sendExceptionNotification()";
		msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_UPLOAD_MAIN + "." + activityName;

		logger.log(Priority.INFO, "Input Parameters for "+msgStep+" ( ebtEventsRowId : " + ebtEventsRowId + " errorMsg : " + errorMsg + " filename : " + filename
				+ " uploadMethod : " + uploadMethod + " uploadUser : " + uploadUser + " ) ");

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		utilityFunctions.sendNotification(filename, uploadUser, uploadMethod, ebtEventsRowId, errorMsg);

		// For Web GUI to capture
		logger.info("End of Exception Notification");
		logger.info("Returning from service "+msgStep);

	}

}
