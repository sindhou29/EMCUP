/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.DataValidatorImpl;
import com.emc.settlement.common.MsExcelFileImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.BudgetException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.DataValidator;
import com.emc.settlement.model.backend.pojo.MsExcelFile;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class EMCPSOBudgetUpload {

	private String[] sheetNames;


	/**
	 *
	 */
	public EMCPSOBudgetUpload() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(EMCPSOBudgetUpload.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private AlertNotificationImpl notificationImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MsExcelFileImpl msExcelFileImpl;

	@Autowired
	private DataValidatorImpl dataValidatorImpl;

	@Autowired
	private AlertNotificationImpl alertNotification;

	private String msgStep;
	private String logPrefix = "[EMCPSO] ";

	@Transactional
	public Map<String, Object> captureFileUploadParameters(Map<String, Object> variableMap)
	{

		try {

			String comments = (String) variableMap.get("comments");
			String createdDateStr = null;
			String ebtEventId = (String) variableMap.get("ebtEventId");
			String ebtEventsRowId = null;
			String eveId = null;
			String fileContent = null;
			String fileContentName = (String) variableMap.get("fileContentName");
			String filename = (String) variableMap.get("filename");
			Integer nextVersionNum = null;
			String uploadUser = (String) variableMap.get("uploadUser");
			String userId = null;
			BudgetException budgetException = new BudgetException();

			String activityName = "captureFileUploadParameters()";
			msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( comments : " + comments
					+ " ebtEventId : " + ebtEventId
					+ " fileContentName : " + fileContentName
					+ " filename : " + filename
					+ " uploadUser : " + uploadUser + ")");

			budgetException.execStep = msgStep;
			userId = utilityFunctions.getUserId(uploadUser);

			if (userId == null) {
				budgetException.validationNumber = 6;
				budgetException.message = "User Name " + uploadUser + " is not valid.";
				throw new BudgetException(budgetException.validationNumber, 0, budgetException.message, msgStep, 0);
			}

			// Create JAM Event
			eveId = utilityFunctions.createJAMEvent("EXE", "EMC-PSO UPLOAD FILE");

			logger.log(Priority.INFO,logPrefix + "Created JAM Event with ID: " + eveId);

			// Log JAM Message [Correction on EMCS-460]
			utilityFunctions.logJAMMessage(eveId, "I",  msgStep, "Uploading EMC/PSO file: " + filename +
					", Uploaded by: " + uploadUser + ", Upload Method: Manual", "");

			fileContent = utilityFunctions.base64Encode(fileContentName);

			// Initialize instance variable budetException
			budgetException.validationType = 1;

			// 1 = FILE VALIDATION
			budgetException.validationNumber = 0;

			// 0 = success
			budgetException.message = "";

			// Create EBT Event
			ebtEventsRowId = utilityFunctions.createEbtEvent(eveId, "SFB",filename, userId, comments, "");

			logger.log(Priority.INFO,logPrefix + "Create EBT Event success with ID: " + ebtEventId);

			// assign empty string to @createdDateStr - this will be used during uploading to determine if @createdDateStr
			// has been initialised
			createdDateStr = "";
			nextVersionNum = - 1;

			variableMap.put("userId", userId);
			variableMap.put("eveId", eveId);
			variableMap.put("fileContent", fileContent);
			variableMap.put("createdDateStr", createdDateStr);
			variableMap.put("nextVersionNum", nextVersionNum);
			variableMap.put("ebtEventsRowId", ebtEventsRowId);
			variableMap.put("ebtEventId", ebtEventId);
			logger.info("Returning from service "+msgStep+" - ( userId :" + userId
					+ " eveId :" + eveId
					+ " fileContent :" + fileContent
					+ " createdDateStr :" + createdDateStr
					+ " nextVersionNum :" + nextVersionNum
					+ " ebtEventsRowId :" + ebtEventsRowId
					+ " ebtEventId :" + ebtEventId + ")");
			return variableMap;
		}
		catch(BudgetException e) {
			logger.log(Priority.INFO,logPrefix + "Exception in <" + msgStep + "> " + e.getMessage());
			throw e;
		}


	}

	@Transactional
	public Map<String, Object> validateFile(Map<String, Object> variableMap)
	{

		try {

			String comments = (String) variableMap.get("comments");
			String ebtEventId = "EMC_BUDGET_FILE_UPLOAD";
			String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
			String eveId = (String) variableMap.get("eveId");
			String fileContent = (String) variableMap.get("fileContent");
			String filename = (String) variableMap.get("filename");
			Boolean foundEMC;
			Boolean foundPSO;
			Boolean insertedDateRange;
			BudgetException budgetException = new BudgetException();

			String activityName = "validateFile()";
			MsExcelFile excelFile = new MsExcelFile();

			msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + activityName;
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( comments : " + comments
					+ " eveId : " + eveId
					+ " ebtEventId : " + ebtEventId
					+ " fileContent : " + fileContent
					+ " filename : " + filename + ")");

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");

			budgetException.execStep = msgStep;

			int BUDGET_NUM_COLS = 3;
			budgetException.validationType = 2;

			// 1 = Data Validation
			budgetException.validationNumber = 0;

			// 0 = success, else is exception
			if (comments == null || comments.length() <= 0) {
				budgetException.validationNumber = 7;
				budgetException.message = "User Comments is empty.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			if (filename == null || filename.length() <= 0) {
				budgetException.validationNumber = 5;
				budgetException.message = "Filename is empty.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			if (fileContent.length() == 0) {
				budgetException.validationNumber = 8;
				budgetException.message = "File size is zero bytes.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			// check column header for EMC or PSO budget file
			// if check is successful, the index of the worksheet, row and column is returned as 'out' parameters
			String[] cellIds = new String[1];
			String[] sheetNames = new String[1];

			if (! msExcelFileImpl.initializeWBookFromBinary( fileContent, excelFile)) {
				budgetException.validationNumber = 1;
				budgetException.message = "File is not in Excel format.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			// search for column name 'EMC FEE_A' or 'PWR SYS_O' in the workbook to determine budget type
			String[] columnNames = { "No", "Month", "EMC FEE_A" };
			foundEMC = msExcelFileImpl.getCellLocation( sheetNames,  columnNames[2],  cellIds, excelFile);

			if (! foundEMC) {
				budgetException.validationNumber = 3;
				budgetException.message = "Column header not found for EMC FEE_A.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			int col = msExcelFileImpl.checkColNumber(sheetNames[0], cellIds[0], excelFile);

			// TODO GANESH change to 3
			if (col != 9) {
				budgetException.validationNumber = 2;
				budgetException.message = "Column Number for sheet: [" + sheetNames[0] +
						"] should be 3 instead of " + col;
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			// check for PSO budget
			columnNames[2] = "PWR SYS_O";
			sheetNames[0] = null;
			foundPSO = msExcelFileImpl.getCellLocation( sheetNames, columnNames[2],  cellIds, excelFile);

			if (! foundPSO) {
				budgetException.validationNumber = 3;
				budgetException.message = "Column header not found for PWR SYS_O.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			col = msExcelFileImpl.checkColNumber( sheetNames[0],  cellIds[0], excelFile);

			// TODO GANESH change to 3
			if (col != 9) {
				budgetException.validationNumber = 2;
				budgetException.message = "Column Number for sheet: [" + sheetNames[0] +
						"] should be 3 instead of " + col;
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			insertedDateRange = false;

			logger.log(Priority.INFO,logPrefix + "EMC/PSO Budget File is Valid");

			// Log JAM Message [Correction on EMCS-460]
			utilityFunctions.logJAMMessage(eveId,  "I", msgStep, "Validating EMC/PSO Budget File Format: EMC/PSO Budget File Format is Valid",
					"");

			// Insert raw file into database
			utilityFunctions.storeStringIntoDbClob( ebtEventsRowId,  fileContent.toString());

			variableMap.put("insertedDateRange", insertedDateRange);
			variableMap.put("foundPSO", foundPSO);
			variableMap.put("foundEMC", foundEMC);
			variableMap.put("ebtEventId", ebtEventId);
			logger.info("Return from service - insertedDateRange :"+insertedDateRange+" foundPSO :"+foundPSO+" foundEMC :"+foundEMC+" ebtEventId :"+ebtEventId);
			return variableMap;

		} catch (BudgetException e) {
			throw e;
		}

	}

	@Transactional(readOnly = true)
	public Map<String, Object> getBudgetData(Map<String, Object> variableMap)
	{

		try {

			List<Double> cellAmount = null;
			List<Double> cellSerialNo = null;
			List<Date> cellMonth = null;
			String ebtEventId = (String) variableMap.get("ebtEventId");
			String fileContent = (String) variableMap.get("fileContent");
			BudgetException budgetException = new BudgetException();

			String activityName = "getBudgetData()";
			msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventId : " + ebtEventId
					+ " fileContent : " + fileContent + ")");

			budgetException.execStep = msgStep;

			/*
				Fetch values in cell columns into arrays
				May be performed for either EMC or PSO date depending on value in ebtEventId
			*/
			MsExcelFile excelFile = new MsExcelFile();
			String[] cellIds = new String[1];
			String[] sheetNames = new String[1];
			boolean found;
			String[] columnNames = { "No", "Month", "EMC FEE_A" };

			if (ebtEventId.equals("PSO_BUDGET_FILE_UPLOAD")) {
				columnNames[2] = "PWR SYS_O";
			}

			// not valid Excel file if workbook cannot be initialized
			if (! msExcelFileImpl.initializeWBookFromBinary(fileContent, excelFile)) {
				budgetException.validationNumber = 1;
				budgetException.message = "File is not in Excel format.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			found = msExcelFileImpl.getCellLocation( sheetNames,  columnNames[2],  cellIds, excelFile);

			// store cell values into arrays for validation
			Object[] cellValues;
			cellSerialNo = new ArrayList<Double>();

			cellValues = msExcelFileImpl.getColumnValues( sheetNames,  columnNames[0],12, excelFile);

			if (cellValues == null) {
				budgetException.validationNumber = 2;
				budgetException.message = "Number of columns is not 3 - missing column: " + columnNames[0];
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}
			else {
				Integer i = 0;

				for (Object v : cellValues) {
					cellSerialNo.add((Double) v);
					i = i + 1;
				}
			}

			cellValues = null;

			cellMonth = new ArrayList<Date>();

			cellValues = msExcelFileImpl.getColumnValues( sheetNames,  columnNames[1],12, excelFile);

			if (cellValues == null) {
				budgetException.validationNumber = 2;
				budgetException.message = "Number of columns is not 3 - missing column: " + columnNames[1];
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}
			else {
				int i = 0;

				for (Object v : cellValues) {
					cellMonth.add((Date) v);
					i = i + 1;
				}
			}

			cellValues = null;

			cellAmount = new ArrayList<Double>();

			cellValues = msExcelFileImpl.getColumnValues( sheetNames,  columnNames[2], 12, excelFile);

			if (cellValues == null) {
				budgetException.validationNumber = 2;
				budgetException.message = "Number of columns is not 3 - missing column: " + columnNames[2];
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}
			else {
				int i = 0;

				for (Object v : cellValues) {
					cellAmount.add((Double) v);
					i = i + 1;
				}
			}

			cellValues = null;
			excelFile = null;

			logger.log(Priority.INFO,logPrefix + "EMC/PSO Budget Data Extracted.");

			variableMap.put("cellSerialNo", cellSerialNo);
			variableMap.put("cellMonth", cellMonth);
			variableMap.put("cellAmount", cellAmount);
			logger.info("Return from service - cellSerialNo :"+cellSerialNo+" cellMonth :"+cellMonth.toString()+" cellAmount :"+cellAmount.toString());
			return variableMap;

		} catch (BudgetException e) {
			throw e;
		}

	}

	@Transactional(readOnly = true)
	public Map<String, Object> validateBudgetData(Map<String, Object> variableMap)
	{

		try {

			List<Double> amount = new ArrayList<Double>();
			List<Double> cellAmount = (List<Double>) variableMap.get("cellAmount");
			List<Double> cellSerialNo = (List<Double>) variableMap.get("cellSerialNo");
			List<Date> cellMonth = (List<Date>) variableMap.get("cellMonth");
			String ebtEventId = (String) variableMap.get("ebtEventId");
			String eveId = (String) variableMap.get("eveId");
			List<Date> month = new ArrayList<Date>();
			BudgetException budgetException = new BudgetException();

			String activityName = "validateBudgetData()";
			msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for captureFileUploadParameters  ( cellAmount : " + cellAmount
					+ " cellSerialNo : " + cellSerialNo
					+ " cellMonth : " + cellMonth
					+ " ebtEventId : " + ebtEventId
					+ " eveId : " + eveId + ")");

			budgetException.execStep = msgStep;

			// Initialize instance variable budetException
			budgetException.validationType = 2;

			// 1 = FILE VALIDATION
			budgetException.validationNumber = 0;

			// 0 = success
			budgetException.message = "";

			if (cellMonth.size() < 12) {
				budgetException.validationNumber = 5;
				budgetException.message = "Number of Months is not 12, only " + cellMonth.size() + " Months found.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}
			else if (cellAmount.size() < 12) {
				budgetException.validationNumber = 5;
				budgetException.message = "Number of Amount values is not 12, only " + cellAmount.size() + " Amount values found.";
				throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
			}

			SimpleDateFormat ftm = new SimpleDateFormat("MMM-yy");
			int i = 0;

			// specify the expected month sequence beginning at 3 (Mar) and ending at 2 (Feb)
			Date currentMonth = new Date();

			while (i < 12) {
				if (cellAmount.get(i) == null) {
					budgetException.validationNumber = 1;
					budgetException.message = "Amount is empty (No. " + cellSerialNo.get(i) + ").";
					throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
				}

				if (! dataValidatorImpl.isNumeric(String.valueOf(cellAmount.get(i)))) {
					budgetException.validationNumber = 2;
					budgetException.message = "Amount " + cellAmount.get(i) + " is not a valid numeric number (No. " + ((double) cellSerialNo.get(i)) + ").";
					throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
				}

				// store the validated amount
				amount.add(i, ((Double) cellAmount.get(i)));

				Date cellDate = cellMonth.get(i);
				if (cellDate == null) {
					budgetException.validationNumber = 3;
					budgetException.message = "Month is empty (No. " + cellSerialNo.get(i) + ").";
					throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
				}

				if (! dataValidatorImpl.isValidDate( ((String) cellDate.toString()),  "E MMM dd HH:mm:ss z yyyy")) {
					budgetException.validationNumber = 4;
					budgetException.message = "Month " + cellDate + " is invalid (No. " + ((double) cellSerialNo.get(i)) + ").";
					throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
				}

				month.add(i, ((Date) cellDate));

				if (i > 0) {
					if (! (month.get(i).getMonth() == currentMonth.getMonth() && month.get(i).getYear() == currentMonth.getYear())) {
						budgetException.validationNumber = 6;
						budgetException.message = "Month is not in ascending order, month1: " +
								ftm.format(month.get(i-1)) + ", month2: " + ftm.format( month.get(i)) +
								" (No. " + ((double) cellSerialNo.get(i)) + ")";
						throw new BudgetException(budgetException.validationNumber, budgetException.validationType,budgetException.message, msgStep, 0 );
					}
				}

				// compute next month's cellDate for next validation loop
				Calendar cal = Calendar.getInstance();
				cal.setTime(month.get(i));
				cal.add(Calendar.MONTH, 1);
				currentMonth = cal.getTime();
				i = i + 1;
			}

			logger.log(Priority.INFO,logPrefix + "EMC/PSO Data is Valid");

			// Log JAM Message [Correction on EMCS-460]
			if (ebtEventId.equals("EMC_BUDGET_FILE_UPLOAD")) {
				utilityFunctions.logJAMMessage( eveId, "I",  msgStep, "Validating EMC Data: EMC Data is Valid", "");
			}
			else {
				utilityFunctions.logJAMMessage( eveId,  "I",  msgStep,  "Validating PSO Data: PSO Data is Valid",  "");
			}

			variableMap.put("cellSerialNo", cellSerialNo);
			variableMap.put("cellMonth", cellMonth);
			variableMap.put("cellAmount", cellAmount);
			variableMap.put("amount", amount);
			variableMap.put("month", month);
			logger.info("Return from service - cellSerialNo :"+cellSerialNo+" cellMonth :"+cellMonth.toString()+" cellAmount :"+cellAmount.toString()+" amount :"+amount+" month :"+month);
			return variableMap;

		} catch (BudgetException e) {
			throw e;
		}

	}

	@Transactional
	public Map<String, Object> uploadBudgetData(Map<String, Object> variableMap)
	{

		List<Double> amount = (List<Double>) variableMap.get("amount");
		String createdDateStr = (String) variableMap.get("createdDateStr");
		String ebtEventId = (String) variableMap.get("ebtEventId");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		String filename = (String) variableMap.get("filename");
		Boolean insertedDateRange = (Boolean) variableMap.get("insertedDateRange");
		List<Date> month = (List<Date>) variableMap.get("month");
		Integer nextVersionNum = (Integer) variableMap.get("nextVersionNum");
		String userId = (String) variableMap.get("userId");
		BudgetException budgetException = new BudgetException();

		try{


			String activityName = "uploadBudgetData()";
			msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + activityName;

			logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
			logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( amount : " + amount
					+ " ebtEventId : " + ebtEventId
					+ " eveId : " + eveId
					+ " createdDateStr : " + createdDateStr
					+ " filename : " + filename
					+ " insertedDateRange : " + insertedDateRange
					+ " month : " + month
					+ " nextVersionNum : " + nextVersionNum
					+ " userId : " + userId
					+ " ebtEventsRowId : " + ebtEventsRowId + ")");

			budgetException.execStep = msgStep;

			budgetException.validationType = 2;

			// 2 = DATA VALIDATION
			budgetException.validationNumber = 0;

			// 0 = success, else is an exception
			budgetException.message = "";
			String recoveryExternalId;
			String feeCode;

			if (ebtEventId.equals("EMC_BUDGET_FILE_UPLOAD")) {
				feeCode = "EMCADMIN";
				recoveryExternalId = "EMC REC_A";
			}
			else {
				feeCode = "PSOADMIN";
				recoveryExternalId = "PWR SYS_O";
			}


			DataValidator dataValidator = dataValidatorImpl.getValidSacDetails( utilityFunctions.getddMMMyyyyHyphen(new Date()), utilityFunctions.getddMMMyyyyHyphen(new Date()),  recoveryExternalId);
			if (dataValidator.sacId == null) {
				budgetException.validationNumber = - 1;
				budgetException.message = "Cannot find Recovery Sac ID for External Id: " + recoveryExternalId;
				throw budgetException;
			}

			String sqlCommand;

			Date firstMonth = month.get(0);
			String startDateStr = utilityFunctions.getddMMMyyyyHyphen(utilityFunctions.firstDayOfMonth(firstMonth)).toUpperCase();

			Date lastMonth = month.get(11);
			String endDateStr = utilityFunctions.getddMMMyyyyHyphen(utilityFunctions.lastDayOfMonth(lastMonth)).toUpperCase();
			String dateRangeCondition = "Month BETWEEN TO_DATE('" + startDateStr + "', 'DD-MON-YYYY') " +
					"AND TO_DATE('" + endDateStr + "', 'DD-MON-YYYY') ";

			if (nextVersionNum < 0) {
				sqlCommand = "SELECT NVL(MAX(TO_NUMBER(version)),0) + 1 MAX from NEM.nem_settlement_fees_budget_dtl " +
						" WHERE (Fee_code = 'EMCADMIN' or Fee_code = 'PSOADMIN') AND " + dateRangeCondition;

				// -- fetch existing data for this set of budget from database (if any)
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
				for (Map row : list) {
					BigDecimal bg = (BigDecimal) row.get("MAX");
					nextVersionNum = bg.intValue();
				}
			}

			sqlCommand = "SELECT NVL(MAX(TO_NUMBER(version)),0) MAX from NEM.nem_settlement_fees_budget_dtl " +
					" WHERE Fee_code = '" + feeCode + "' AND " + dateRangeCondition;
			int versionNum = 0;

			// -- fetch existing data for this set of budget from database (if any)
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				versionNum = ((BigDecimal)row.get("MAX")).intValue();
			}

			// expire the old records
			if (versionNum > 0) {
				sqlCommand = "UPDATE NEM.nem_settlement_fees_budget_dtl SET Expired_date = SYSDATE, Lock_version = 99" +
						" WHERE version = '" + String.valueOf(versionNum) +
						"' AND Fee_code = '" + feeCode +
						"' AND " + dateRangeCondition;
				jdbcTemplate.update(sqlCommand, new Object[] {});
			}
			else {
				// no existing records for date range
				versionNum = 1;
			}

			// create the ebt_events_data_range_details ONLY ONCE when uploading both EMC and PSO budget data
			if (! insertedDateRange) {
				String ebtEventsDtlId = utilityFunctions.getEveId();
				sqlCommand = "INSERT INTO nem_ebt_events_dtl( Id, Ebe_id, Sac_id, Sac_version ) VALUES ( '" +
						ebtEventsDtlId + "', '" + ebtEventsRowId + "', NULL, NULL)";

				jdbcTemplate.update(sqlCommand, new Object[] {});

				Date lastDateOfMonth = utilityFunctions.lastDayOfMonth(lastMonth);

				// ----------
				sqlCommand = "INSERT INTO nem_ebt_event_date_range_dtl( Id, Ebt_events_dtl_id, Start_date, End_date, Run_type )" +
						" VALUES ( SYS_GUID(), '" + ebtEventsDtlId + "', " +
						" TO_DATE('" + startDateStr + "', 'DD-MON-YYYY')," +
						" TO_DATE('" + utilityFunctions.getddMMMyyyyHyphen(lastDateOfMonth) + "', 'DD-MON-YYYY'), NULL)";

				jdbcTemplate.update(sqlCommand, new Object[] {});

				insertedDateRange = true;
			}

			// NOTE: rows inserted into table nem_settlement_fee_budget_dtl from same Excel file for
			// both EMC and PSO nust have SAME created_date timestamp, which is initialised in @createdDate below
			// This is a Front-end display requirement
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
			if (createdDateStr.length() == 0) {
				createdDateStr = sdf.format(new Date());
			}

			sqlCommand = "INSERT INTO NEM.nem_settlement_fees_budget_dtl ( Fee_code, " +
					"Month, Amount, Recovery_Sac_Id, Created_date, Version, Frontend_user_id)" +
					" VALUES ( ?,?,?,?,?,?,? )";
			int i = 0;

			// dateStr as String = null
			while (i < 12) {
				// dateStr = toUpperCase(format(dateFmt, arg1 : month[i]))

				Object[] params = new Object[7];
				params[0] =  feeCode;
				params[1] =  utilityFunctions.convertUDateToSDate(month.get(i));
				params[2] =  amount.get(i);
				params[3] =  dataValidator.sacId;
				params[4] =  utilityFunctions.convertUDateToSDate(new Date());
				params[5] =  nextVersionNum;
				params[6] =  userId;
				jdbcTemplate.update(sqlCommand, params);
				
				i = i + 1;
			}

			logger.log(Priority.INFO,logPrefix + "EMC/PSO Budget Data Insterted");

			// Log JAM Message  [Correction on EMCS-460]
			if (ebtEventId.equals("EMC_BUDGET_FILE_UPLOAD")) {
				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Inserting EMC Budget Data into Database",
						"");

				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Inserted " + i + " rows for Settlement Dates: " +
								utilityFunctions.getddMMMyyyy(firstMonth) + " to " + utilityFunctions.getddMMMyyyy(lastMonth) +
								" with New EMC Budget Version: " + nextVersionNum,
						"");
			}
			else {
				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Inserting PSO Budget Data into Database",
						"");

				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Inserted " + i + " rows for Settlement Dates: " +
								utilityFunctions.getddMMMyyyy(firstMonth) + " to " + utilityFunctions.getddMMMyyyy(lastMonth) +
								" with New PSO Budget Version: " + nextVersionNum,
						"");
			}
			logger.info("Return from service - nextVersionNum :"+nextVersionNum+" insertedDateRange :"+insertedDateRange+" createdDateStr :"+createdDateStr);
			variableMap.put("nextVersionNum", nextVersionNum);
			variableMap.put("insertedDateRange", insertedDateRange);
			variableMap.put("createdDateStr", createdDateStr);

		} catch (BudgetException e) {
			throw e;
		} catch (Exception e) {
			budgetException.message = e.getMessage();
			sqlException(e, eveId, ebtEventsRowId, ebtEventId, filename);
		}
		return variableMap;
	}

	@Transactional
	public void updateEBTEvent(Map<String, Object> variableMap)
	{

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");

		msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + ".updateEBTEvent()";

		logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " ebtEventsRowId : " + ebtEventsRowId + ")");

		// Log JAM Message  [Correction on EMCS-460]
		utilityFunctions.logJAMMessage( eveId,  "I",  msgStep ,"Upload EMC/PSO file successfully finished.", "");

		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEvent( ebtEventsRowId,  true);

		// Update JAM_EVENTS
		utilityFunctions.updateJAMEvent(true, eveId);
		logger.log(Priority.INFO, "Returning from service "+msgStep);

	}

	public void sqlException(Exception e, String eveId, String ebtEventsRowId, String ebtEventId, String filename) {

		BudgetException budgetException = new BudgetException();
		budgetException.message = e.getMessage() + "\n"
				+ e.getStackTrace().toString();
		throw budgetException;
	}

	@Transactional
	public void handleException(Map<String, Object> variableMap) {

		String ebtEventId = (String) variableMap.get("ebtEventId");
		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		BudgetException budgetException = new BudgetException();

		msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + "handleException";
		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( ebtEventId : " + ebtEventId
				+ " eveId : " + eveId
				+ " ebtEventsRowId : " + ebtEventsRowId + ")");

		logger.warn(logPrefix + budgetException.message);

		String errorCode = Integer.toString(budgetException.validationType) + "," + Integer.toString(budgetException.validationNumber);

		// Log JAM Message
		utilityFunctions.logJAMMessage( eveId, "E", budgetException.execStep, budgetException.message, errorCode);

		// Update NEM_EBT_EVENTS table
		utilityFunctions.updateEBTEvent(ebtEventsRowId, false);

		// Update JAM_EVENTS table
		utilityFunctions.updateJAMEvent(false, eveId);

		String sqlCommand = "INSERT INTO jam_messages (id, seq, severity, Message_date, text, Execution_step, "
				+ "Error_code, Generated_date, notification, Eve_id, Submission_id) "
				+ " VALUES (SYS_GUID(), get_mstimestamp, 'E', SYSDATE, '" + budgetException.message + "', '" + ebtEventId
				+ "', '" + errorCode + "', SYSDATE, 'F', '" + eveId + "', NULL) ";

		jdbcTemplate.update(sqlCommand);
		
		// Update NEM_EBT_EVENTS
		utilityFunctions.updateEBTEventValid(ebtEventsRowId, false);
		
		// Update JAM_EVENTS COMPLETED = Y and SUCCESS = N
		utilityFunctions.updateJAMEventCompleted(eveId);
		
		logger.log(Priority.INFO, "Returning from service "+msgStep);
	}

	@Transactional
	public void sendExceptionNotification(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String eveId = (String) variableMap.get("eveId");
		String filename = (String) variableMap.get("filename");
		BudgetException budgetException = (BudgetException) variableMap.get("budgetException");

		msgStep = PROCESS_NAME_EMC_PSO_BUDGET_UPLOAD + "." + "sendExceptionNotification";
		logger.info(logPrefix + "Starting Activity " + msgStep + " ...");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " filename : " + filename
				+ " ebtEventsRowId : " + ebtEventsRowId + ")");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Sending Alert Notification", "");


		List<Map<String, Object>> eventList = utilityFunctions.getNemEbtEvents(ebtEventsRowId);

		StringBuilder content = new StringBuilder();
		content.append("File Name: " + filename + "\n");
		for(Map m : eventList) {
			content.append("File Upload Date and Time: " + m.get("uploaded_date") + "\n");
			content.append("Settlement Date: " + m.get("settlement_date") + "\n");
			content.append("File Upload User: " + m.get("uploaded_by") + "\n");
			content.append("User Comments: " + m.get("comments") + "\n");
			content.append("Validated Time: " + m.get("validated_date") + "\n");
			content.append("Valid: " + m.get("valid_yn") + "\n");
		}

		content.append("Error Message: " + budgetException.message);

		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		AlertNotification alertNotifier =new AlertNotification();

		alertNotifier.businessModule = "EMC/PSO Budget File Upload";
		alertNotifier.content = content.toString();
		alertNotifier.recipients = propertiesMap.get("EMCPSO_UPLOAD_FAIL_EMAIL");//EMCPSO_UPLOAD_FAIL_EMAIL;
		alertNotifier.sender = "emcadmin@emcsg.com";
		alertNotifier.subject = "EMC/PSO Budget file upload failed";
		alertNotifier.noticeType = "EMC/PSO Budget File Upload";

		alertNotification.sendEmail(alertNotifier);

		logger.log(Priority.INFO, "Returning from service "+msgStep);

	}

}
