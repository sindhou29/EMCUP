/**
 * 
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.DISPLAY_DATE_FORMAT_3;
import static com.emc.settlement.model.backend.constants.BusinessParameters.FILE_VALIDATION;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.MSSLCorrectedHeaderImpl;
import com.emc.settlement.common.MSSLFileValidatorImpl;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.UploadFileInfo;
import com.emc.settlement.model.backend.pojo.fileupload.MSSL;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrectedHeader;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLFileValidator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * @author DWTN1561
 *
 */
@Service
public class VerifyAndLoadMSSLMeteringData implements Serializable {

	protected static final Logger logger = Logger.getLogger(VerifyAndLoadMSSLMeteringData.class);
	
	public String msgStep;
	public String logPrefix;

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private PavPackageImpl pavPackageImpl;

	@Autowired
	private MSSLCorrectedHeaderImpl cmHeaderImpl;

	@Autowired
	private MSSLFileValidatorImpl validatorImpl;

	@Autowired
	AlertNotificationImpl alertNotificationImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TransactionTemplate transactionTemplate;

	public VerifyAndLoadMSSLMeteringData() {
	}

	@Transactional
	public Map<String, Object> updateProcessedStatus(Map<String, Object> variableMap)
	{
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String ebtEventId = (String) variableMap.get("ebtEventId");

		logPrefix = "[" + fileInfo.fileType + "] ";
		String ntEffectiveDate = (String) variableMap.get("ntEffectiveDate");
		String drEffectiveDate = (String) variableMap.get("drEffectiveDate");
		try {
			msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".updateProcessedStatus()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " ebtEventId : " + ebtEventId + ")");

			// ////////////////////////////////////////////////////////////////////
			// Update Processed status
			// ////////////////////////////////////////////////////////////////////
			utilityFunctions.updateSEWApproveProcessStatus(ebtEventId, "Y");

			// Get Parameter: NT_EFFECTIVE_DATE
			ntEffectiveDate = utilityFunctions.getddMMMyyyyhhmmss(utilityFunctions.getSysParamTime("NT_EFFECTIVE_DATE"));

			// Get DR Effective Start Date
			drEffectiveDate = utilityFunctions.getddMMMyyyyhhmmss(utilityFunctions.getSysParamTime("DR_EFFECTIVE_DATE"));

			// ntEffectiveDate = NT_EFFECTIVE_DATE_TEST
			logger.log(Priority.INFO, logPrefix + "NT_EFFECTIVE_DATE is: "
					+ ntEffectiveDate);
		}
		catch (Exception e) {
			logger.error("Exception " + e.getMessage());
		}

		variableMap.put("ntEffectiveDate", ntEffectiveDate);
		variableMap.put("drEffectiveDate", drEffectiveDate);
		logger.info("Returning from service "+msgStep+" - ( ntEffectiveDate :" + ntEffectiveDate
				+ " drEffectiveDate :" + drEffectiveDate + ")");
		return variableMap;
	}

	@Transactional
	public Map<String, Object> validateAndLoadCMFData(Map<String, Object> variableMap) throws MsslException
	{
		ArrayList<MSSL> cmfDataRejected = new ArrayList<MSSL>();
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");

		// Modified to Add exception handling - (source INCIDENT ITSM-24721 - CMF File Processing Error) - Part of BPM 2.5.4 - Added
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String userId = (String) variableMap.get("userId");
		String ntEffectiveDate = (String) variableMap.get("ntEffectiveDate");
		String drEffectiveDate = (String) variableMap.get("drEffectiveDate");
		String eventId = (String) variableMap.get("eventId");
		List<String> emailNotifyList = (List<String>) variableMap.get("emailNotifyList");
		Boolean fromEBT = (Boolean) variableMap.get("fromEBT");
		try {
			msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".validateLoadCMFData()";
			logPrefix = "[" + fileInfo.fileType + "] ";

			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " userId : " + userId
					+ " ntEffectiveDate : " + ntEffectiveDate
					+ " drEffectiveDate : " + drEffectiveDate
					+ " eventId : " + eventId
					+ " emailNotifyList : " + emailNotifyList
					+ " fromEBT : " + fromEBT
					+ " ebtEventId : " + ebtEventId + ")");

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");

			String cmfFilename;
			String cmBodyStr = null;
			List<MSSL> cmfDataAccepted = new ArrayList<MSSL>();
			String content = null;
			boolean isFormatWithHeader;
			MSSLCorrectedHeader cmHeader = new MSSLCorrectedHeader();
			cmHeader.periods = new ArrayList<String>();
			cmHeader.validSettlementDates = new ArrayList<String>();
			cmHeader.details = new ArrayList<>();
			List<String> validSettlementDates = new ArrayList<String>();

			String dontDoAnything;  //ByPassforSPServices Added
			boolean DREffDate = false;  //ByPassforSPServices Added

			// ////////////////////////////////////////////////////////////////////
			// 	Load MSSL Metering File from NEM_SETTLEMENT_RAW_FILES
			// ////////////////////////////////////////////////////////////////////

			String sqlCmd = "select nee.filename, nee.uploaded_date, nee.uploaded_by, " +
					"nee.comments, nsr.raw_file from nem_ebt_events nee, " +
					"nem_settlement_raw_files nsr " +
					"where nsr.ebe_id = nee.id and nee.id = ?";

			Object[] params = new Object[1];
			params[0] = ebtEventId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, params);
			for (Map row : list) {
				cmfFilename = (String) row.get("filename");
				fileInfo.uploadTime = (Date) row.get("uploaded_date");

				// fileInfo.uploadUsername = String(row[3])
				userId = (String) row.get("uploaded_by");
				fileInfo.comments = (String) row.get("comments");
				content = (String) row.get("raw_file");

				// If the file is zipped, unzip it
				if (cmfFilename.contains(".zip")) {
					content = utilityFunctions.base64Decode(content);
				}
			}

			// ////////////////////////////////////////////////////////////////////
			// 	Check Header
			// ////////////////////////////////////////////////////////////////////
			// *************seperate CMF header and body *********
			// *************parse header info ********************
			int idx0 = content.indexOf("ADJUSTMENT SUMMARY START");
			int idx1 = content.indexOf("Adjusted Period");
			int idx2 = content.indexOf("Affected SA/MNN");
			int idx3 = content.indexOf("ADJUSTMENT SUMMARY END");
			isFormatWithHeader = true;

			if (idx0 < 0 || idx1 < 0 || idx2 < 0 || idx3 < 0) {
				isFormatWithHeader = false;
			}

			if (isFormatWithHeader) {
				if (!(idx0 < idx1 && idx1 < idx2 && idx2 < idx3)) {
					throw new MsslException("CMF_DATA_VALIDATION", 4201,
							1, "[UCF-4]: Corrected Metering File not in valid format",
							msgStep);
				}

				// seperate header and body content
				String headerStr = content.substring(0, idx3 + "ADJUSTMENT SUMMARY END".length() + 1);

				// logMessage logPrefix + "headerStr" +  headerStr
				String bodyStr = content.substring(idx3 + "ADJUSTMENT SUMMARY END".length() + 1, content.length());

				// logMessage logPrefix + "bodyStr" +  bodyStr
				int tIdx = bodyStr.indexOf("\n");
				cmBodyStr = bodyStr.substring(tIdx + 1, bodyStr.length());

				// logMessage logPrefix + "trim bodyStr" +  bodyStr
				// construct header
				boolean success = cmHeaderImpl.parse(cmHeader, headerStr, msgStep);
			}

			if (!isFormatWithHeader) {
				cmBodyStr = content;
			}

			// ////////////////////////////////////////////////////////////////////
			// 	CMF Data Validation
			// ////////////////////////////////////////////////////////////////////
			cmfDataAccepted.clear();

			cmfDataRejected.clear();

			validSettlementDates.clear();

			int headerLineNumber = cmHeader.lineNumber + 1;
			StringReader strReader = new StringReader(cmBodyStr);
			CSVReader csvReader = new CSVReader(strReader);
			String[] line = csvReader.readNext();
			int idxAcp = 0;
			int idxRej = 0;
			int lineIdx = 0;
			MSSLFileValidator validator = new MSSLFileValidator();

			validatorImpl.initializeMSSLFileValidator(validator, "CMF");
			validator.logPrefix = logPrefix;
			validator.ntEffectiveDate = utilityFunctions.stringToDate(ntEffectiveDate, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
			validator.drEffectiveDate = utilityFunctions.stringToDate(drEffectiveDate, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
			validator.total = new HashMap<String, Integer>();
			validator.square = new HashMap<String, Integer>();

			cmHeaderImpl.calculateDateRanges(cmHeader, utilityFunctions.addDays(fileInfo.uploadTime, 1));

			validator.cmHeader = cmHeader;
			validator.newCMFFormat = isFormatWithHeader;

			int lineNo = 0;
			
			Date addedtime = utilityFunctions.get5PMTime();
		   
			while (line != null && line.length > 0) {
//				logger.log(Priority.INFO, "Validating LineNo.: " + (lineNo++) + " Line: " + line[0] + ", " + line[2] + ", " + line[4] + ", " + line[5]);
				MSSL x = validatorImpl.validateLine(cmHeader, line, lineIdx + headerLineNumber, validator, logPrefix);

				// after 5PM, check the next business day for the cut-off timings Issue
				if (fileInfo.uploadTime.compareTo(addedtime) > 0) {
					// check the x.settlementDate in the rejected date range

					if (x.settlementDate.compareTo(cmHeader.pDateRange.startDate) >= 0 && x.settlementDate.compareTo(cmHeader.pDateRange.endDate) <= 0) {
						x.runType = "P";
						cmfDataRejected.add(idxRej, x);

						idxRej = idxRej + 1;
					}
					else if (x.settlementDate.compareTo(cmHeader.fDateRange.startDate) >= 0 && x.settlementDate.compareTo(cmHeader.fDateRange.endDate) <= 0) {
						x.runType = "F";
						cmfDataRejected.add(idxRej, x);

						idxRej = idxRej + 1;
					}
					else if (x.settlementDate.compareTo(cmHeader.rDateRange.startDate) >= 0 && x.settlementDate.compareTo(cmHeader.rDateRange.endDate) <= 0) {
						x.runType = "R";
						cmfDataRejected.add(idxRej, x);

						idxRej = idxRej + 1;
					}
					else if (x.settlementDate.compareTo(cmHeader.sDateRange.startDate) >= 0 && x.settlementDate.compareTo(cmHeader.sDateRange.endDate) <= 0) {
						x.runType = "S";
						cmfDataRejected.add(idxRej, x);

						idxRej = idxRej + 1;
					}
					else {
						cmfDataAccepted.add(idxAcp, x);

						idxAcp = idxAcp + 1;

						// create approved settlement dates
						String tDate = utilityFunctions.getddMMMyyyyHyphen(x.settlementDate);

						if (validSettlementDates.indexOf(tDate) == -1) {
							validSettlementDates.add(tDate);
						}
					}
				}
				else {
					cmfDataAccepted.add(idxAcp, x);

					idxAcp = idxAcp + 1;

					// create approved settlement dates
					String tDate = utilityFunctions.getddMMMyyyyHyphen(x.settlementDate);

					if (validSettlementDates.indexOf(tDate) == -1) {
						validSettlementDates.add(tDate);
					}
				}

				lineIdx = lineIdx + 1;
				line = csvReader.readNext();

			}

			validatorImpl.validateTotal(logPrefix, validator);

			csvReader.close();

			strReader.close();

			logger.log(Priority.INFO, logPrefix + "MSSL " + fileInfo.fileType + " Data is valid.");

			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Validating Corrected Metering Data : Corrected Metering Data are Valid",
					"");

			// ////////////////////////////////////////////////////////////////////
			// 	Load CMF Data into database
			// ////////////////////////////////////////////////////////////////////
			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Inserting Corrected Metering Data into Database",
					"");

			// write header info to DB
			String validateResult = cmHeaderImpl.getIncompletedCorrectedData(cmHeader);

			if (validateResult != null) {
				logger.log(Priority.INFO, logPrefix + "Data is not completed at " + validateResult);

				throw new MsslException("DATA_VALIDATION", 4205,
						1, "Data is not completed at " + validateResult,
						msgStep);
			}
			else {
				emailNotifyList = cmHeaderImpl.writeToDB(cmHeader, ebtEventId, userId, fileInfo.uploadTime, msgStep);

				logger.log(Priority.INFO, logPrefix + "complete corrected data emailNotifyList " + emailNotifyList);
			}


			String sqlCommand;
			String pktId;
			int currVersion;
			String comment;
			Map<String, String> versionMap = new HashMap<String, String>();
			int idx = 1;

			logger.log(Priority.INFO, logPrefix + "fileupload data validSettlementDates " + validSettlementDates);

			for (String date : validSettlementDates) {
				// version as Int = currVersion + Int(get(map, date))
				// Create MSSL Package
				String version = pavPackageImpl.createMSSLPackage(utilityFunctions.stringToDate(date, DISPLAY_DATE_FORMAT_3));
				versionMap.put(date, version);
			}

			String sqlSettQty = "INSERT INTO NEM.NEM_SETTLEMENT_QUANTITIES " +
					"(ID, VERSION, SETTLEMENT_DATE, PERIOD, QUANTITY_TYPE, QUANTITY, " +
					"SAC_ID, SAC_VERSION, NDE_ID, NDE_VERSION) VALUES (sys_guid(),?,?,?,?,?,?,?,?,?)";
			try {
				int version;
				String lastExternalId = "";
				String lastNodeName = "";

				logger.log(Priority.INFO, logPrefix + "Start Inserting MSSL Quantity for Transaction ID: " +
						fileInfo.transId + ", Event ID: " + eventId);

				int count = 0;

				lineNo = 0;
				List<Object[]> batchParams = new ArrayList<>();
				for (MSSL mssl : cmfDataAccepted) {
//					logger.log(Priority.INFO, "Preparing MSSL for Insert: " + (lineNo++) + " Line: " + mssl.nodeName + ", " + mssl.quantityType + ", " + mssl.period);
					if (mssl.quantityType.equals("WLQ") || mssl.quantityType.equals("WDQ")) {
						DREffDate = utilityFunctions.isAfterDRCAPEffectiveDate(mssl.settlementDate);  //ByPassforSPServices Added
					}

					if (mssl.quantityType.equals("WLQ") && !(DREffDate)) {
						dontDoAnything = "Dont Do Anything";
					}
					else if (mssl.quantityType.equals("WDQ") && !(DREffDate)) {
						dontDoAnything = "Dont Do Anything";
					}
					else {  //ByPassforSPServices Added
						// version = currVersion + Int(map.get(mssl.settlementDate.format("dd-MMM-yyyy")))
						version = Integer.parseInt(versionMap.get(utilityFunctions.getddMMMyyyyHyphen(mssl.settlementDate)));

						Object[] params1 = new Object[9];
						params1[0] = version;
						params1[1] = utilityFunctions.convertUDateToSDate(mssl.settlementDate);
						params1[2] = mssl.period;
						params1[3] = mssl.quantityType;
						params1[4] = mssl.quantity;
						params1[5] = mssl.sacId;
						params1[6] = (mssl.sacId == null || mssl.sacId.equals("")) ? null : mssl.standingVersion;
						params1[7] = mssl.nodeId;
						params1[8] = (mssl.nodeId == null || mssl.nodeId.equals("")) ? null : mssl.standingVersion;
						batchParams.add(params1);
						count = count + 1;
					}  //ByPassforSPServices Added
				}
				jdbcTemplate.batchUpdate(sqlSettQty, batchParams);

				logger.log(Priority.INFO, logPrefix + "End Inserting MSSL Quantity for Transaction ID: " +
						fileInfo.transId + ", Event ID: " + eventId);

				logger.log(Priority.INFO, logPrefix + "Inserted " + count + " MSSL Quantity records.");
			}
			catch (Exception e) {
				logger.log(Priority.INFO, logPrefix + "Exception when updating database. " + e.getMessage());
				e.printStackTrace();
				throw new MsslException("DATA INSERT", 0, 0,
						e.getMessage(), msgStep);
			}

			if (cmfDataAccepted.size() > 0) {
				// Log JAM Message
				utilityFunctions.logJAMMessage(eventId, "I", msgStep,
						"Inserted " + cmfDataAccepted.size() + " rows of MSSL data.",
						"");
			}

			if (cmfDataRejected != null && cmfDataRejected.size() > 0) {
				logger.log(Priority.INFO, logPrefix + "store the data to file system");

				String dir = UtilityFunctions.getProperty("SHARED_DRIVE") + File.separator +"CMF" +File.separator+ "Pending";
				File f = new File(dir);

				if (f.exists() == false) {
					utilityFunctions.makeDirs(dir);
				}

				FileWriter fileWriter = null;
				CSVWriter csvWriter = null;
				String filename;
				String tempNodeId;
				String tempSacId;
				Date tempSettDate = null;
				String tempRunType = "";

				for (MSSL mssl : cmfDataRejected) {


					if (tempSettDate == null) {
						filename = dir + File.separator + "Pending-" +
								mssl.runType + "-" + utilityFunctions.getddMMyyyy(mssl.settlementDate) + ".csv";
						f = new File(filename);

						if (f.exists()) {
							f.delete();
						}

						fileWriter = new FileWriter(filename);
						csvWriter = new CSVWriter(fileWriter);
					}
					else if (tempSettDate.compareTo(mssl.settlementDate) != 0) {
						fileWriter.close();

						filename = dir + File.separator + "Pending-" +
								mssl.runType + "-" + utilityFunctions.getddMMyyyy(mssl.settlementDate) + ".csv";
						f = new File(filename);

						if (f.exists()) {
							f.delete();
						}

						fileWriter = new FileWriter(filename);
						csvWriter = new CSVWriter(fileWriter);
					}

					tempSettDate = mssl.settlementDate;
					tempRunType = mssl.runType;
					tempNodeId = mssl.nodeId;

					if (tempNodeId == null) {
						tempNodeId = "";
					}

					tempSacId = mssl.sacId;

					if (tempSacId == null) {
						tempSacId = "";
					}

					String[] columnResults = {mssl.quantityType, utilityFunctions.getddMMyyyy(mssl.settlementDate), String.valueOf(mssl.period), String.valueOf(mssl.quantity), tempNodeId, tempSacId};
					csvWriter.writeNext(columnResults);

				}

				fileWriter.close();

				// Log JAM Message
				utilityFunctions.logJAMMessage(eventId, "I", msgStep,
						"Store the rejected data to file " + dir,
						"");
			}

			logger.log(Priority.INFO, logPrefix + "MSSL Quantity table updated successfully.");

			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"MSSL Quantity table updated successfully.",
					"");
		}
		catch (SQLException sqlException) {
			sqlException(sqlException, msgStep, fileInfo, eventId, ebtEventId, fromEBT);
			sqlException.printStackTrace();
		}
		catch (MsslException msslException) {
			logger.log(Priority.ERROR, logPrefix + "MsslException when updating database. " + msslException.getMessage());
			msslException.printStackTrace();
			throw msslException;
		}
		catch (Exception e) {
			logger.log(Priority.INFO, logPrefix + "Exception when updating database in Verify and Load Corrected Metering Data. " + e.getMessage());
			e.printStackTrace();
			throw new MsslException("DATA INSERT", 0, 0,
					e.getMessage(), msgStep);
		}

		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("userId", userId);
		variableMap.put("cmfDataRejected", cmfDataRejected);
		variableMap.put("ntEffectiveDate", ntEffectiveDate);
		variableMap.put("drEffectiveDate", drEffectiveDate);
		variableMap.put("eventId", eventId);
		variableMap.put("emailNotifyList", emailNotifyList);
		logger.info("Return from service - ebtEventId :" + ebtEventId + " fileInfo :" + fileInfo + " userId :" + userId + " cmfDataRejected :" + cmfDataRejected .size()+
				" ntEffectiveDate :" + ntEffectiveDate + " drEffectiveDate :" + drEffectiveDate + " eventId :" + eventId + " emailNotifyList :" + emailNotifyList);
		return variableMap;
	}

	@Transactional
	public Map<String, Object> validateAndLoadDailyMeteringData(Map<String, Object> variableMap) throws MsslException
	{

		ArrayList<MSSL> msslData = new ArrayList<MSSL>();
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		logPrefix = (String) variableMap.get("logPrefix");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String userId = (String) variableMap.get("userId");
		String ntEffectiveDate = (String) variableMap.get("ntEffectiveDate");
		String drEffectiveDate = (String) variableMap.get("drEffectiveDate");
		String eventId = (String) variableMap.get("eventId");
		try {
			msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".validateAndLoadDailyMeteringData()";

			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
					+ " ebtEventId : " + ebtEventId
					+ " userId : " + userId
					+ " ntEffectiveDate : " + ntEffectiveDate
					+ " drEffectiveDate : " + drEffectiveDate
					+ " eventId : " + eventId + ")");

			String content = null;
			String dmFilename;

			String dontDoAnything;  //ByPassforSPServices Added
			boolean DREffDate = false;  //ByPassforSPServices Added

			// ////////////////////////////////////////////////////////////////////
			// 	Load MSSL Metering File from NEM_SETTLEMENT_RAW_FILES
			// ////////////////////////////////////////////////////////////////////
			String sqlCmd = "select nee.filename, nee.uploaded_date, nee.uploaded_by, " +
					"nee.comments, nsr.raw_file from nem_ebt_events nee, " +
					"nem_settlement_raw_files nsr " +
					"where nsr.ebe_id = nee.id and nee.id = ?";

			Object[] params = new Object[1];
			params[0] = ebtEventId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, params);
			for (Map row : list) {
				dmFilename = (String) row.get("filename");
				fileInfo.uploadTime = (Date) row.get("uploaded_date");

				userId = (String) row.get("uploaded_by");
				fileInfo.comments = (String) row.get("comments");
				content = (String) row.get("raw_file");

				// If the file is zipped, unzip it
				if (dmFilename.contains(".zip")) {
					content = utilityFunctions.base64Decode(content);
				}
			}

			// ////////////////////////////////////////////////////////////////////
			// 	File and Data Validation
			// ////////////////////////////////////////////////////////////////////
			StringReader strReader = new StringReader(content);
			CSVReader csvReader = new CSVReader(strReader);
			String[] line = csvReader.readNext();
			int idx = 0;
			MSSLFileValidator validator = new MSSLFileValidator();
			validatorImpl.initializeMSSLFileValidator(validator, "DMF");
			validator.logPrefix = logPrefix;
			validator.ntEffectiveDate = utilityFunctions.stringToDate(ntEffectiveDate, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
			validator.drEffectiveDate = utilityFunctions.stringToDate(drEffectiveDate, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT"));
			validator.total = new HashMap<>();
			validator.square = new HashMap<>();
			msslData.clear();
			int lineNo = 0;
			while (line != null && line.length > 0) {
//				logger.log(Priority.INFO, "Validating LineNo.: " + (lineNo++) + " Line: " + line[0] + ", " + line[2] + ", " + line[4] + ", " + line[5]);
				MSSL m = validatorImpl.validateLine(null, line, idx, validator, logPrefix);

				msslData.add(idx, m);

				idx = idx + 1;
				line = csvReader.readNext();

			}

			validatorImpl.validateTotal(logPrefix, validator);

			csvReader.close();

			strReader.close();

			logger.log(Priority.INFO, logPrefix + "MSSL Metering Data is valid.");

			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Validating Daily Metering Data : Daily Metering Data are Valid",
					"");

			// /////////////////////////////////////////////////////////////
			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Inserting Daily Metering Data into Database",
					"");

			String pktId;
			String currVersion;
			int updCount;
			String comment;

			// Create MSSL Package
			currVersion = pavPackageImpl.createMSSLPackage(msslData.get(0).settlementDate);

			logger.log(Priority.INFO, logPrefix + "MSSL Data row count: " + msslData.size() + ", CurrVersion: " + currVersion);

			// Insert Settlement Quantities
			String sqlSettQty = "INSERT INTO NEM.NEM_SETTLEMENT_QUANTITIES " +
					"(ID, VERSION, SETTLEMENT_DATE, PERIOD, QUANTITY_TYPE, QUANTITY, " +
					"SAC_ID, SAC_VERSION, NDE_ID, NDE_VERSION) VALUES (sys_guid(),?,?,?,?,?,?,?,?,?)";
			idx = 0;
			String lastExternalId = "";
			String lastNodeName = "";

			logger.log(Priority.INFO, logPrefix + "Start Inserting MSSL Quantity for Transaction ID: " +
					fileInfo.transId + ", Event ID: " + eventId);

			lineNo = 0;
			List<Object[]> batchParams = new ArrayList<Object[]>();
			Date effectiveDate = utilityFunctions.getSysParamTime("DR_EFFECTIVE_DATE");
			for (MSSL mssl : msslData) {
				logger.log(Priority.INFO, "Preparing MSSL for Insert: " + (idx + 1) + " Line: " + mssl.nodeName + ", " + mssl.quantityType + ", " + mssl.period);
				if (mssl.quantityType.equals("WLQ") || mssl.quantityType.equals("WDQ")) {
					DREffDate = utilityFunctions.isGivenDateAfterReferredDate(mssl.settlementDate, effectiveDate);  //ByPassforSPServices Added
				}

				if (mssl.quantityType.equals("WLQ") && !(DREffDate)) {
					dontDoAnything = "Dont Do Anything";
				}
				else if (mssl.quantityType.equals("WDQ") && !(DREffDate)) {
					dontDoAnything = "Dont Do Anything";
				}
				else {  //ByPassforSPServices Added
					Object[] params1 = new Object[9];
					params1[0] = currVersion;
					params1[1] = utilityFunctions.convertUDateToSDate(mssl.settlementDate);
					params1[2] = mssl.period;
					params1[3] = mssl.quantityType;
					params1[4] = mssl.quantity;
					params1[5] = mssl.sacId;
					params1[6] = (mssl.sacId == null || mssl.sacId.equals("")) ? null : mssl.standingVersion;
					params1[7] = mssl.nodeId;
					params1[8] = (mssl.nodeId == null || mssl.nodeId.equals("")) ? null : mssl.standingVersion;
					batchParams.add(params1);
					idx = idx + 1;
				}  //ByPassforSPServices Added
			}
			jdbcTemplate.batchUpdate(sqlSettQty, batchParams);

			logger.log(Priority.INFO, logPrefix + "End Inserting MSSL Quantity for Transaction ID: " +
					fileInfo.transId + ", Event ID: " + eventId);

			fileInfo.settlementDate = msslData.get(0).settlementDate;

			logger.log(Priority.INFO, logPrefix + "Inserted " + idx + " rows for New MSSL Version: " + currVersion);

			// Log JAM Message
			utilityFunctions.logJAMMessage(eventId, "I", msgStep,
					"Inserted " + idx + " rows for Settlement Date: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")).format(fileInfo.settlementDate) +
							" with New MSSL Pkg Version: " + currVersion,
					"");

			// update Settlement Date, and PKG_VERSION in NEM_EBT_EVENTS table
			utilityFunctions.updateEBTEventPkgVersion(fileInfo.settlementDate, currVersion, ebtEventId);

		}
		catch (MsslException e) {
			e.printStackTrace();
			throw e;
		}
		catch (Exception e) {
			logger.log(Priority.INFO, logPrefix + "Exception when updating database in Verify and Load Daily Metering Data. " + e.getMessage());
			e.printStackTrace();
			throw new MsslException("DATA INSERT", 0, 0,
					e.getMessage(), msgStep);
		}
		variableMap.put("ebtEventId", ebtEventId);
		variableMap.put("fileInfo", fileInfo);
		variableMap.put("userId", userId);
		variableMap.put("ntEffectiveDate", ntEffectiveDate);
		variableMap.put("drEffectiveDate", drEffectiveDate);
		variableMap.put("eventId", eventId);
		variableMap.put("msslData", msslData);
		logger.info("Return from service - ebtEventId :" + ebtEventId + " fileInfo :" + fileInfo + " userId :" + userId +
				" ntEffectiveDate :" + ntEffectiveDate + " drEffectiveDate :" + drEffectiveDate + " eventId :" + eventId + " msslData :" + msslData);
		return variableMap;
	}

	@Transactional
	public Map<String, Object> updateEvent(Map<String, Object> variableMap)
	{
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String eventId = (String) variableMap.get("eventId");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		ArrayList<MSSL> cmfDataRejected = (ArrayList<MSSL>) variableMap.get("cmfDataRejected");
		logPrefix = "[" + fileInfo.fileType + "] ";
		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".updateEvent()";

		logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( fileInfo : " + fileInfo
				+ " ebtEventId : " + ebtEventId
				+ " eventId : " + eventId + ")");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eventId, "I", msgStep,
				"Upload MSSL metering file successfully finished.",
				"");

		// Update EBT Event
		utilityFunctions.updateEBTEvent(ebtEventId, true);

		// Update JAM Event
		utilityFunctions.updateJAMEvent(true, eventId);

		// If received Metering file or Corrected Metering file after 17:00pm, send
		// email notification to Settlement Team
		
		Date addedtime = utilityFunctions.get5PMTime();
		
		if (fileInfo.uploadTime.compareTo(addedtime) > 0) {
			AlertNotification alert = new AlertNotification();
			alert.businessModule = "Receive MSSL Metering File";
			String corr = "";

			if (fileInfo.fileType.equals("CMF") || fileInfo.fileType.equals("CTR")) {
				corr = "Corrected ";
			}
			else {
				corr = "Daily ";
			}

			//ITSM 17002.17 enhancements starts
			String uploadType = "";

			if (fileInfo.fileType.equals("CMF")) {
				uploadType = "Manual Upload";
			}
			else if (fileInfo.fileType.equals("CTR")) {
				uploadType = "EBT";
			}
			else if (fileInfo.fileType.equals("DMF")) {
				uploadType = "Manual Upload";
			}
			else if (fileInfo.fileType.equals("MTR")) {
				uploadType = "EBT";
			}
			else {
				uploadType = "[Unknown Medium - Contact IT Support]";
			}
			//ITSM 17002.17 enhancements ends

			String settDateStr = "";
			String lastSettDate = "x";

			if (cmfDataRejected != null) {
				for (MSSL mssl : cmfDataRejected) {
					String settDate = utilityFunctions.getddMMMyyyy(mssl.settlementDate);

					if (!settDate.equalsIgnoreCase(lastSettDate)) {
						settDateStr = settDateStr + settDate + ", ";
						lastSettDate = settDate;
					}
				}
			}

			settDateStr = (settDateStr.length() > 2) ? settDateStr.substring(0, settDateStr.length() - 2) : "";

			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			
			
			if (settDateStr.length() > 9) {
				alert.content = corr + "Metering File for Settlement Date (" +
						//settDateStr + ") is received via EBT at timing " + fileInfo.uploadTime.format(mask : "HH:mm");
						settDateStr + ") is received via " + uploadType + " at timing " + new SimpleDateFormat("HH:mm").format(fileInfo.uploadTime);    //ITSM 17002.17 enhancements
				alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;
				alert.subject = corr + "Metering File for Settlement Date (" +
						//settDateStr + ") is received via EBT";
						settDateStr + ") is received via " + uploadType;    // ITSM 17002.17 enhancements
				alert.noticeType = "MSSL Metering Data Input Preparation";
				alertNotificationImpl.sendEmail(alert);
			}
		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}

	public void sqlException(SQLException sqlException, String msgStep, UploadFileInfo fileInfo, String eventId, String ebtEventId, boolean fromEBT) {

		MsslException msslException = new MsslException();
		msslException.errorMsg = sqlException.getMessage();
		msslException.execStep = msgStep;
		msslException.rowNumber = 0;
		msslException.validationNumber = 0;
		msslException.validationType = "SQL EXCEPTION";
		throw msslException;
	}

	@Transactional
	public Map<String, Object> updateFailEvent(Map<String, Object> variableMap) {

		MsslException msslException = (MsslException) variableMap.get("msslException");
		String logPrefix = (String) variableMap.get("logPrefix");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String eventId = (String) variableMap.get("eventId");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		
		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".updateFailEvent()";
		
		try {
			logger.log(Priority.INFO, logPrefix + "Starting Activity " + msgStep);

			logger.log(Priority.INFO, logPrefix + "MSSL Exception: VALTYPE - " + msslException.validationType +
					", VALNUM - " + msslException.validationNumber + ", ROWNUM: " +
					msslException.rowNumber + ", MSG: " + msslException.errorMsg);

			String errorCode = msslException.validationType + "," + Integer.toString(msslException.validationNumber);
			String msg;
			String filetype = fileInfo.fileType.substring( 0, 1).equals("C") ? "Corrected" : "Daily";

			if (msslException.validationType.equals(FILE_VALIDATION)) {
				msg = "Validating " + filetype + " MSSL File: " + msslException.errorMsg + " line(" + Integer.toString((msslException.rowNumber)) + ")";
			} else {
				msg = "Validating " + filetype + " MSSL Data: " + msslException.errorMsg + " line(" + Integer.toString((msslException.rowNumber)) + ")";
			}

			utilityFunctions.logJAMMessage(eventId,"E",msslException.execStep,msg,errorCode);

			// Update EBT Event
			utilityFunctions.updateEBTEvent(ebtEventId, false);

			// Update JAM Event
			utilityFunctions.updateJAMEvent(false, eventId);

			utilityFunctions.logJAMMessage(eventId,"E", BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA,
					"MSSL " + filetype + " Metering Data (" + fileInfo.filename + ") not uploaded due to error",
					errorCode);
		}
		catch (Exception e) {
			logger.log(Priority.ERROR, logPrefix + "Exception in Activity " + msgStep + ". " + e.getMessage());
		}
		variableMap.put("msslException", msslException);
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}

	@Transactional
	public Map<String, Object> alertNotify(Map<String, Object> variableMap) {

		MsslException msslException = (MsslException) variableMap.get("msslException");
		UploadFileInfo fileInfo = (UploadFileInfo) variableMap.get("fileInfo");
		String eventId = (String) variableMap.get("eventId");
		
		msgStep = BusinessParameters.PROCESS_NAME_VERIFY_AND_LOAD_MSSL_METERING_DATA + ".alertNotify()";
		
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( msslException : " + msslException
				+ " fileInfo : " + fileInfo
				+ " eventId : " + eventId + ")");
		// ITSM 15386 Changes for Null Pointer Exception during sending fileupload failure email
		String fileInfoSettlementDateStr;

		if (fileInfo.settlementDate == null) {
			fileInfoSettlementDateStr = "-";
		}
		else {
			fileInfoSettlementDateStr = format(fileInfo.settlementDate, UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT"));
		}

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		// end
		AlertNotification alert = new AlertNotification();
		alert.businessModule = "Receive MSSL Metering File";
		alert.content = "Filename: " + fileInfo.filename + ";\n\n" +
				"File fileupload time: " + format(fileInfo.uploadTime, UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")) + ";\n\n" +
				"Settlement Date: " + fileInfoSettlementDateStr + ";\n\n" +
				"File fileupload user: " + fileInfo.uploadUsername + ";\n\n" +
				"User Comments: " + fileInfo.comments + ";\n\n" +
				"Validated time: " + format(Calendar.getInstance().getTime(), UtilityFunctions.getProperty("DISPLAY_TIME_FORMAT")) + ";\n\n" +
				"Valid: N;\n\n" +
				"Error Message: " + msslException.errorMsg + " line(" + Integer.toString((msslException.rowNumber)) + "). Event ID: " + eventId;
		alert.recipients = propertiesMap.get("FILE_UPLOAD_FAIL_EMAIL");//FILE_UPLOAD_FAIL_EMAIL;

		if (fileInfo.fileType.equals("CTR") || fileInfo.fileType.equals("CMF")) {
			alert.subject = "Corrected Metering file upload failed";
		}
		else {
			alert.subject = "Daily Metering file upload failed";
		}

		alert.noticeType = "MSSL Metering Data Input Preparation";

		alertNotificationImpl.sendEmail(alert);

		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}

}
