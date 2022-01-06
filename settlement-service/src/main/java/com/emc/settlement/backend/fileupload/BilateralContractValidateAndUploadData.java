/**
 *
 */
package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.BLC_JAVA_DATE_FORMAT;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.BilateralContractImpl;
import com.emc.settlement.common.DataValidatorImpl;
import com.emc.settlement.common.UploadBilateralContractImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.BilateralContractUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.DataValidator;
import com.emc.settlement.model.backend.pojo.fileupload.UploadBilateralContract;
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
public class BilateralContractValidateAndUploadData {

	/**
	 *
	 */
	public BilateralContractValidateAndUploadData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */

	protected static final Logger logger = Logger.getLogger(BilateralContractValidateAndUploadData.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UploadBilateralContractImpl blcImpl;

	@Autowired
	private DataValidatorImpl datavalidatorImpl;

	@Autowired
	private BilateralContractImpl bilateralContractImpl;

	public String msgStep;
	public String logPrefix;


	public Map<String, Object> validateBilateralContracts(Map<String, Object> variableMap) throws BilateralContractUploadException
	{

		try {

			String acgId = (String) variableMap.get("acgId");
			String contractName = (String) variableMap.get("contractName");
			CsvFileValidator csvFileValidator = (CsvFileValidator) variableMap.get("csvFileValidator");
			String eveId = (String) variableMap.get("eveId");
			String externalId = (String) variableMap.get("externalId");
			List<Map<Integer, Object>> first_row_array = new ArrayList<Map<Integer, Object>>();
			Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
			String inputPTPID = (String) variableMap.get("inputPTPID");
			Integer rowNum = (Integer) variableMap.get("rowNum");
			String sacPurchasedBy = (String) variableMap.get("sacPurchasedBy");
			String sacSoldBy = (String) variableMap.get("sacSoldBy");
			String userId = (String) variableMap.get("userId");

			final String activityName = "validateBilateralContracts()";

			logger.log(Priority.INFO,
					"Input Parameters ( rowNum : " + rowNum + " csvFileValidator : " + csvFileValidator + " contractName : "
							+ contractName + " inputPTPID : " + inputPTPID + " sacSoldBy : " + sacSoldBy
							+ " sacPurchasedBy : " + sacPurchasedBy + " acgId : " + acgId + " eveId : " + eveId
							+ " externalId : " + externalId + " fromSEW : " + fromSEW +" userId :"+userId+ ")");
			msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_VALIDATE_AND_UPLOAD_DATA + "." + activityName;

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");

			// Log JAM Message [Correction on EMCS-457]
			/*
			 * Points to note for Bilateral Contract (BLC) data validation: There are 48
			 * records in each complete set of blc The data set will be saved into database
			 * only if all 48 records are valid Each blc csv file can contain one set of
			 * record
			 */
			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
			/*
			 * Post this CR 1 BIL File can contain multiple contracts (each contract must be
			 * of 48 lines): ----- with same contract name ----- with same contract type
			 * ----- with same seller account ----- Different contract names in 1 File NOT
			 * Allowed ----- Different contract types in 1 File NOT Allowed ----- Different
			 * Seller Accounts in 1 File NOT Allowed Above is subject to following
			 * restriction: ----- following cannot be ***Repeated**** ----- combination of
			 * Contract Name + Start Date + End Date + Period + Seller and Buyer Settlement
			 * ID (for Each Row) ----- Start Date (for Each block of 48 periods data that
			 * constitutes 1 valid contract) ----- End Date (for Each block of 48 periods
			 * data that constitutes 1 valid contract) ----- combination of Contract Name +
			 * Start Date + End Date (for Each block of 48 periods data that constitutes 1
			 * valid contract) ----- For Each block of 48 periods data that constitutes 1
			 * contract will pass through current validations ----- Current 1 BIL File
			 * Containing 1 Contracts of 48 lines only (+ header) remains unaffected.
			 */
			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
			int BLC_MAX_PERIOD_PER_FILE = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
			int BLC_MIN_FRACTION = 0;
			int BLC_MAX_FRACTION = 100;

			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
			int first_row_array_idx = 1;
			int fileLines = 0;

			// to identify actual error line number in the file
			String contractNameConstant = "";

			// to store first line contract name
			String contractTypeConstant = "";

			// to store first line contract Type
			String sellerSacIDConstant = "";

			// to store first line Seller Acc ID
			String buyerSacIDConstant = "";

			// to store first line Buyer Acc ID //ITSM-17449-Suggested enhancement for BIL
			// Phase 2
			List<String> csvStartDate = new ArrayList<String>();

			// to keep track of Start Date
			List<String> csvEndDate = new ArrayList<String>();

			// to keep track of End Date
			rowNum = 1;

			// skip the header line at index 0 later in the for loop
			int totalLines = csvFileValidator.getCsvFileData().size();

			// quantity retrieved from CSV file as string format
			List csvPeriod = new ArrayList();

			while (rowNum < totalLines) {
				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
				BilateralContractUploadException blcException = new BilateralContractUploadException(0, 2, "", msgStep);
				List<String> line;
				String strPeriod;

				// period retrieved from CSV file as string format
				String strQuantity;

				// quantity retrieved from CSV file as string format

				// to keep track of missing periods or duplicate periods
				UploadBilateralContract blc = new UploadBilateralContract();
				UploadBilateralContract blcValid = new UploadBilateralContract();

				// to keep validated values to speed up subsequent validations
				blcValid.initializeDbItem();

				boolean firstRecord;

				for (int contract_block_i = 1; contract_block_i <= BLC_MAX_PERIOD_PER_FILE; contract_block_i++) {
					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
					blc.initializeDbItem();

					fileLines = rowNum + contract_block_i;

					// to identify error line number in the file //ITSM 17426 - N Contracts Block
					// per BIL File (BPM 8.1) - Added
					strPeriod = null;
					strQuantity = null;

					Map<Integer, List<String>> mapline = csvFileValidator.getCsvFileData();
					line = mapline.get(rowNum - 1 + contract_block_i);
					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
					blc.contractName = String.valueOf(line.get(0)).trim();
					blc.sellerAccount = String.valueOf(line.get(1)).trim();
					blc.buyerAccount = String.valueOf(line.get(2)).trim();
					blc.type = String.valueOf(line.get(3)).trim();
					blc.reserveGroup = String.valueOf(line.get(4)).trim();
					blc.startDate = String.valueOf(line.get(5)).trim();
					blc.endDate = String.valueOf(line.get(6)).trim();
					strPeriod = String.valueOf(line.get(7)).trim();
					strQuantity = String.valueOf(line.get(8)).trim();
					try {
						if (String.valueOf(line.get(7)).length() > 0) {
							blc.period = Integer.valueOf(String.valueOf(line.get(7)).trim());
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						blcException.setValidationNumber(10);
						blcException.message = "Period is invalid: " + String.valueOf(line.get(7)) + " (line "
								+ (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw blcException;
					}

					try {
						double x = Double.parseDouble(String.valueOf(line.get(8)).trim());

						if (String.valueOf(line.get(8)).length() > 0) {
							blc.quantity = Double.parseDouble(line.get(8).trim());
						} else {
							throw new Exception();
						}
					} catch (Exception e) {
						blcException.validationNumber = 10;
						blcException.message = "Quantity is invalid: " + String.valueOf(line.get(8)) + " (line "
								+ (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw blcException;
					}

					// ---- BEGIN Validation Process for a csv record
					firstRecord = contract_block_i % BLC_MAX_PERIOD_PER_FILE == 1;

					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
					// static data for current bilateral contract should not change for all 48
					// records
					// stop processing any more csv records when static data changes
					if (firstRecord) {
						if (blc.contractName.length() <= 0) {
							blcException.validationNumber = 1;
							blcException.message = "Contract Name is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;
						}

						if (blc.contractName.length() > 30) {
							blcException.validationNumber = 1;
							blcException.message = "Contract Name " + blc.contractName
									+ " length exceeded 30 characters (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;
						}

						if (blcImpl.contractNameExist(blc.contractName)) {
							blcException.validationNumber = 2;
							blcException.message = "Contract Name " + blc.contractName
									+ " same as existing contract in database (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;
						}

						// keep contract name for use later to expire old records
						contractName = blc.contractName;

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						if (rowNum <= 1) {
							contractNameConstant = contractName;

							// store the first line data as these are constant throughout the file
						}

						if (!contractNameConstant.equals(blc.contractName)) {
							blcException.validationNumber = 33;
							blcException.message = "Contract Name " + blc.contractName
									+ " is not SAME as expected Contract Name  " + contractNameConstant + " (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						if (blc.startDate.length() <= 0) {
							blcException.validationNumber = 6;
							blcException.message = "Start date is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (!datavalidatorImpl.isValidSettDateFormat(blc.startDate)) {
							blcException.validationNumber = 17;
							blcException.message = "Start date " + blc.startDate + " not in DD-MMM-YYYY format (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (!datavalidatorImpl.isValidDate(blc.startDate, BLC_JAVA_DATE_FORMAT)) {
							blcException.validationNumber = 17;
							blcException.message = "Start date " + blc.startDate + " is not a valid date (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (blc.endDate.length() <= 0) {
							blcException.validationNumber = 7;
							blcException.message = "End date is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (!datavalidatorImpl.isValidSettDateFormat(blc.endDate)) {
							blcException.validationNumber = 18;
							blcException.message = "End date " + blc.endDate + " not in DD-MMM-YYYY format (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (!datavalidatorImpl.isValidDate(blc.endDate, BLC_JAVA_DATE_FORMAT)) {
							blcException.validationNumber = 18;
							blcException.message = "End date " + blc.endDate + " is not a valid date (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						if (csvStartDate.toString().indexOf(blc.startDate) != -1) {
							// Start Date already exists in previous blcok of 48 csv records
							blcException.validationNumber = 36;
							blcException.message = "Bilateral Contract has duplicate Start Date " + blc.startDate
									+ " in the File across block of 48 csv records (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						} else if (csvStartDate.toString().indexOf(blc.endDate) != -1) {
							// End Date already exists in previous block of 48 csv records Start Date
							blcException.validationNumber = 41;
							blcException.message = "Bilateral Contract End Date " + blc.endDate
									+ " is Overlapping with Start Date of any previous Block of Contract Start Date (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						} else {
							// Add New Start Date into array. Array is used to check for duplicates in
							// subsequent 48 block of records
							csvStartDate.add(csvStartDate.size(), blc.startDate);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						if (csvEndDate.toString().indexOf(blc.endDate) != -1) {
							// End Date already exists in previous block of 48 csv records
							blcException.validationNumber = 37;
							blcException.message = "Bilateral Contract has duplicate End Date " + blc.endDate
									+ " in the File across block of 48 csv records (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						} else if (csvEndDate.toString().indexOf(blc.startDate) != -1) {
							// Start Date already exists in previous block of 48 csv records End Date
							blcException.validationNumber = 40;
							blcException.message = "Bilateral Contract Start Date " + blc.startDate
									+ " is Overlapping with End Date of any previous Block of Contract End Date (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						} else {
							// Add New End Date into array. Array is used to check for duplicates in
							// subsequent 48 block of records
							csvEndDate.add(csvEndDate.size(), blc.endDate);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						// compare endDate relative to startDate, get +ve interval if endDate >
						// startDate
						long elapseTime = utilityFunctions.stringToDate(blc.getEndDate(), BLC_JAVA_DATE_FORMAT).getTime()
								- utilityFunctions.stringToDate(blc.startDate, BLC_JAVA_DATE_FORMAT).getTime();
						if (elapseTime < 0) {
							blcException.validationNumber = 19;
							blcException.message = "Start date " + blc.startDate + " is after End date " + blc.endDate
									+ " (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						for (int startendDate_arr_idx = 1; startendDate_arr_idx <= csvStartDate.size(); startendDate_arr_idx++) {
							// Assume that start date and end date array length are same as this code
							// snippets are put after all the date validation are successful
							// compare relative to startDate, get +ve interval if startDate > any earlier
							// startDate for 48 block of contracts
							long elapseTime1 = utilityFunctions.stringToDate(blc.startDate, BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions
									.stringToDate(csvStartDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT)
									.getTime();

							// compare relative to endDate, get +ve interval if any earlier endDate for 48
							// block of contracts > startDate
							long elapseTime2 = utilityFunctions
									.stringToDate(csvEndDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions.stringToDate(blc.startDate, BLC_JAVA_DATE_FORMAT).getTime();

							// compare relative to startDate, get +ve interval if endDate > any earlier
							// startDate for 48 block of contracts
							long elapseTime3 = utilityFunctions.stringToDate(blc.endDate, BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions
									.stringToDate(csvStartDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT)
									.getTime();

							// compare relative to endDate, get +ve interval if any earlier endDate for 48
							// block of contracts > endDate
							long elapseTime4 = utilityFunctions
									.stringToDate(csvEndDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions.stringToDate(blc.endDate, BLC_JAVA_DATE_FORMAT).getTime();

							// compare relative to startDate, get +ve interval if any earlier startDate for
							// 48 block of contracts > startDate
							long elapseTime5 = utilityFunctions
									.stringToDate(csvStartDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions.stringToDate(blc.startDate, BLC_JAVA_DATE_FORMAT).getTime();

							// compare relative to endDate, get +ve interval if endDate > any earlier
							// endDate for 48 block of contracts - Note that Stand Alone this is a vlid case
							// unless not combined with case 5
							long elapseTime6 = utilityFunctions.stringToDate(blc.endDate, BLC_JAVA_DATE_FORMAT).getTime()
									- utilityFunctions.stringToDate(csvEndDate.get(startendDate_arr_idx - 1), BLC_JAVA_DATE_FORMAT)
									.getTime();

							// compare 0 interval if startDate = any earlier endDate for 48 block of
							// contracts
							// elapseTime7 as Interval = stringToDate(UtilityFunctions, dateStr :
							// blc.startDate,
							// dateFormat : "dd-MMM-yyyy") - stringToDate(UtilityFunctions, dateStr :
							// csvEndDate[startendDate_arr_idx-1],
							// dateFormat : "dd-MMM-yyyy")
							// compare 0 interval if startDate = any earlier endDate for 48 block of
							// contracts
							// elapseTime8 as Interval = stringToDate(UtilityFunctions, dateStr :
							// blc.endDate,
							// dateFormat : "dd-MMM-yyyy") - stringToDate(UtilityFunctions, dateStr :
							// csvStartDate[startendDate_arr_idx-1],
							// dateFormat : "dd-MMM-yyyy")
							// if (elapseTime1 > '0x' and elapseTime2 > '0x') or (elapseTime3 > '0x' and
							// elapseTime4 > '0x') or (elapseTime5 > '0x' and elapseTime6 > '0x') or
							// (elapseTime7 = '0x') or (elapseTime8 = '0x') then
							if ((elapseTime1 > 0 && elapseTime2 > 0) || (elapseTime3 > 0 && elapseTime4 > 0)
									|| (elapseTime5 > 0 && elapseTime6 > 0)) {
								blcException.validationNumber = 39;
								blcException.message = "Start date " + blc.startDate + " AND/OR End date " + blc.endDate
										+ " is Within/Overlapping with any earlier Block of 48 Contracts Start Date "
										+ csvStartDate.get(startendDate_arr_idx - 1) + " AND End date "
										+ csvEndDate.get(startendDate_arr_idx - 1) + " combinations (line " + (fileLines)
										+ ").";

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
								// with (fileLines)
								throw new BilateralContractUploadException(blcException.validationNumber, 0,
										blcException.message, msgStep);
							}
						}

						// end of for loop for iteration with index startendDate_arr_idx
						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						if (blc.sellerAccount.length() <= 0) {
							blcException.validationNumber = 3;
							blcException.message = "Seller Account is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						if (rowNum <= 1) {
							sellerSacIDConstant = blc.sellerAccount;

							// store the first line data as these are constant throughout the file
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						DataValidator objValidSacDetails = datavalidatorImpl.getValidSacDetails(blc.startDate,
								blc.endDate, blc.sellerAccount);
						DataValidator objpMatchingParticipantDtl = datavalidatorImpl
								.getMatchingParticipantDtl(inputPTPID, blc.sellerAccount);
						if (!objValidSacDetails.isValid()) {
							blcException.validationNumber = 11;
							blcException.message = "Seller Account " + blc.sellerAccount + " is not valid (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						} else if (!sellerSacIDConstant.equals(blc.sellerAccount)) {
							blcException.validationNumber = 35;
							blcException.message = "Seller Account " + blc.sellerAccount
									+ " is not SAME as expected Seller Account  " + sellerSacIDConstant + " (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						} else if (!objpMatchingParticipantDtl.isValid()) {
							blcException.validationNumber = 25;
							blcException.message = "Seller Account " + blc.sellerAccount + " is not valid (line "
									+ (fileLines) + ") - its Participant: "
									+ objpMatchingParticipantDtl.getInputSellingPTPName()
									+ " NOT matching with Selling Participant: "
									+ objpMatchingParticipantDtl.getSellingPTPName() + ".";
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						} else {
							sacSoldBy = (String) objValidSacDetails.getSacId();
							blcValid.sacVersion = (String) objValidSacDetails.getSacVersion();
						}

						if (blc.buyerAccount.length() <= 0) {
							blcException.validationNumber = 4;
							blcException.message = "Buyer Account is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;
						}

						// ITSM-17449-Suggested enhancement for BIL Phase 2 - Start
						if (rowNum <= 1) {
							buyerSacIDConstant = blc.buyerAccount;

							// store the first line data as these are constant throughout the file
						}

						// ITSM-17449-Suggested enhancement for BIL Phase 2 - End
						objValidSacDetails = datavalidatorImpl.getValidSacDetails(blc.startDate, blc.endDate,
								blc.buyerAccount);
						if (!objValidSacDetails.isValid()) {
							blcException.validationNumber = 12;
							blcException.message = "Buyer Account " + blc.buyerAccount + " is not valid (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);

							// ITSM-17449-Suggested enhancement for BIL Phase 2 - start
						} else if (!buyerSacIDConstant.equals(blc.buyerAccount)) {
							blcException.validationNumber = 45;
							blcException.message = "Buyer acount " + blc.buyerAccount
									+ " is not SAME as expected Buyer Account  " + buyerSacIDConstant + " (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw blcException;

							// ITSM-17449-Suggested enhancement for BIL Phase 2 - End
						} else {
							sacPurchasedBy = objValidSacDetails.getSacId();
						}

						if (blc.buyerAccount == blc.sellerAccount) {
							blcException.validationNumber = 13;
							blcException.message = "Buyer Account " + blc.buyerAccount
									+ " same as Seller Account (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (blc.type.length() <= 0) {
							blcException.validationNumber = 5;
							blcException.message = "Contract Type is empty (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						if (blc.validContractType.toString().indexOf(blc.type) == -1) {
							blcException.validationNumber = 14;
							blcException.message = "Contract Type " + blc.type + " is not valid (line " + (fileLines)
									+ ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						if (rowNum <= 1) {
							contractTypeConstant = blc.type;

							// store the first line data as these are constant throughout the file
						}

						if (!contractTypeConstant.equals(blc.type)) {
							blcException.validationNumber = 34;
							blcException.message = "Contract Type " + blc.type
									+ " is not SAME as expected Contract Type  " + contractTypeConstant + " (line "
									+ (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
						if (blc.type.toUpperCase().equals("RESERVE")) {
							if (blc.reserveGroup == null || blc.reserveGroup.length() <= 0) {
								blcException.validationNumber = 15;
								blcException.message = "No reserve group found for contract " + blc.contractName
										+ " with contract type " + blc.type + " (line " + (fileLines) + ").";

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
								// with (fileLines)
								throw new BilateralContractUploadException(blcException.validationNumber, 0,
										blcException.message, msgStep);
							}

							acgId = blcImpl.getAncillaryGroupId(blc.reserveGroup, blc.startDate);

							if (acgId == null) {
								blcException.validationNumber = 16;
								blcException.message = "Reserve group " + blc.reserveGroup + " not found for contract "
										+ blc.contractName + " (line " + (fileLines) + ")).";

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
								// with (fileLines)
								throw blcException;
							}

							if (!blcImpl.facilitiesExist(acgId, sacSoldBy, blcValid.sacVersion, eveId)) {
								blcException.validationNumber = 23;
								blcException.message = blc.sellerAccount + " does not own facilities that are"
										+ " reserve provider for reserve group " + blc.reserveGroup + "(line "
										+ (fileLines) + ").";

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
								// with (fileLines)
								throw new BilateralContractUploadException(blcException.validationNumber, 0,
										blcException.message, msgStep);
							}
						}

						Date startTime = utilityFunctions.stringToDate(blc.startDate, "dd-MMM-yyyy");

						Date cp66EffectiveDate = null;
						// Start Check the aps_system_parameters
						String ade2SqlCommand="";

						ade2SqlCommand = " select date_value from APS_SYSTEM_PARAMETERS where name = 'CR_DEFAULT_EFFECTIVE_DATE' ";


						List<Map<String, Object>> list = jdbcTemplate.queryForList(ade2SqlCommand, new Object[] {});
						for (Map row : list) {
							cp66EffectiveDate = (Date)row.get("date_value");
							break;
						}

						logger.log(Priority.INFO, "cp66EffectiveDate: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")).format(cp66EffectiveDate));
						logger.log(Priority.INFO, "startTime: " + new SimpleDateFormat(UtilityFunctions.getProperty("DISPLAY_DATE_FORMAT")).format(startTime));

						if (startTime.compareTo(cp66EffectiveDate) >= 0) {
							logger.log(Priority.INFO, "Follow new rule.");

							// ITSM-17449-Suggested enhancement for BIL Phase 2
							int blt_cut_off = 0;
							String upload_module = "X";
							int upload_cutoffTime = 0;

							if (fromSEW) {
								blt_cut_off = UtilityFunctions.getIntProperty("BLS_SUBMISSION_DEADLINE_CP66");

								// SEW Upload TD - 10CD
								upload_module = "S";
								upload_cutoffTime = UtilityFunctions.getIntProperty("BLS_CUTOFF_TIME_CP66");

								// BLS_CUTOFF_TIME;
							}
							else {
								blt_cut_off = UtilityFunctions.getIntProperty("BLC_SUBMISSION_DEADLINE_CP66");

								// MANUAL Upload TD -10CD + 1BD
								upload_module = "M";
								upload_cutoffTime = UtilityFunctions.getIntProperty("BLC_CUTOFF_TIME_CP66");

								// BLC_CUTOFF_TIME;
							}

							boolean FileUploadAllowed = utilityFunctions.getFileUploadAllowedAfterCP66(startTime,blt_cut_off, upload_module, upload_cutoffTime);

							if (! FileUploadAllowed) {
								// ITSM-17449-Suggested enhancement for BIL Phase 2
								if (fromSEW) {
									logger.log(Priority.INFO,  " Bilateral Contract with start date " + blc.startDate +
											" is submitted today after deadline TD(Start Date) " + blt_cut_off + " (Calendar Day) After " + upload_cutoffTime + "Hr. " +
											" (line " + fileLines + ").");

									// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
									blcException.validationNumber = 20;

									blcException.message = "Bilateral contract with start date " + blc.startDate +
											" is submitted today after deadline TD(Start Date) " + blt_cut_off + " (Calendar Day) After  " + upload_cutoffTime + "Hr. " +
											" (line " + fileLines + ").";

									// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
									throw blcException;
								}
								else {
									logger.log(Priority.INFO,  " Bilateral Contract with start date " + blc.startDate +
											" is submitted today after deadline TD(Start Date) " + blt_cut_off + " (Calendar Day) + 1 (Business Day) After " + upload_cutoffTime + "Hr." +
											" (line " + fileLines + ").");

									// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
									blcException.validationNumber = 20;

									blcException.message = "Bilateral contract with start date " + blc.startDate +
											" is submitted today after deadline TD(Start Date) " + blt_cut_off + " (Calendar Day) + 1 (Business Day) After  " + upload_cutoffTime + "Hr. " +
											" (line " + fileLines + ").";

									// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
									throw blcException;
								}
							}
						}
						else {
							logger.log(Priority.INFO,  "Follow old rule");

							// ITSM-17449-Suggested enhancement for BIL Phase 2
							int blt_cut_off_plus_1 = 0;
							String upload_module = "X";
							int upload_cutoffTime = 0;

							if (fromSEW) {
								blt_cut_off_plus_1 = UtilityFunctions.getIntProperty("BLS_SUBMISSION_DEADLINE") + 1;

								// SEW Upload TD + 4BD  PLUS 1 to implement &amp;lt;rownum logic
								upload_module = "S";
								upload_cutoffTime = UtilityFunctions.getIntProperty("BLS_CUTOFF_TIME");
							}
							else {
								blt_cut_off_plus_1 = UtilityFunctions.getIntProperty("BLC_SUBMISSION_DEADLINE") + 1;

								// MANUAL Upload TD + 5BD	PLUS 1 to implement &amp;lt;rownum logic
								upload_module = "M";
								upload_cutoffTime = UtilityFunctions.getIntProperty("BLC_CUTOFF_TIME");
							}

							boolean fileUploadAllowed = utilityFunctions.getFileUploadAllowedBeforeCP66( startTime,
									blt_cut_off_plus_1,
									upload_module,
									upload_cutoffTime);

							// ITSM-17449-Suggested enhancement for BIL Phase 2 - commented
							// contractExpired as Bool = startTime &amp;lt;= cutoffDate
							// ITSM-17449-Suggested enhancement for BIL Phase 2 - commented
							// if contractExpired then
							if (! fileUploadAllowed) {
								// ITSM-17449-Suggested enhancement for BIL Phase 2
								logger.log(Priority.INFO,  " Bilateral Contract with start date " + blc.startDate +
										" is submitted today after deadline TD(Start Date) + " + (blt_cut_off_plus_1 - 1) + " (Business Day) After " + upload_cutoffTime + "Hr. " +
										" (line " + fileLines + ").");

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
								blcException.validationNumber = 20;

								blcException.message = "Bilateral contract with start date " + blc.startDate +
										" is submitted today after deadline TD(Start Date) + " + (blt_cut_off_plus_1 - 1) + " (Business Day) After  " + upload_cutoffTime + "Hr. " +
										" (line " + fileLines + ").";

								// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1) with fileLines
								throw blcException;
							}
						}


						// store the validated values for 1st row - use to validate subsequent records
						blcValid.contractName = blc.contractName;
						blcValid.sellerAccount = blc.sellerAccount;
						blcValid.buyerAccount = blc.buyerAccount;
						blcValid.type = blc.type;
						blcValid.startDate = blc.startDate;
						blcValid.endDate = blc.endDate;

						// ITSM 17449 to increase external ID to include name
						externalId = blc.contractName + "_" + blc.startDate.substring(0, 2)
								+ blc.startDate.substring(3, 6) + blc.startDate.substring(9, 11);
						externalId = externalId.toUpperCase();

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
						Map<Integer, Object> mapData = new HashMap<Integer, Object>();
						mapData.put(0, blc.contractName);
						mapData.put(1, blc.sellerAccount);
						mapData.put(2, blc.buyerAccount);
						mapData.put(3, blc.type);
						mapData.put(4, blc.startDate);
						mapData.put(5, blc.endDate);
						mapData.put(6, externalId);
						mapData.put(7, blc.type);
						first_row_array.add(mapData);
						// ITSM-17449-Suggested enhancement for BIL Phase 2
						first_row_array_idx = first_row_array_idx + 1;

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
					}

					// End of First Row Condition
					// if firstRecord then
					// ASSUMPTION is each csv file contains only data for 1 single contract
					// Hence, if the below data has different values within same file, we flag as
					// invalid ??
					// NEED TO CONFIRM that the above assumption is true
					String errorItem = "";
					if (!blc.contractName.equalsIgnoreCase(blcValid.contractName)) {
						errorItem = "Contract Name";
					} else if (!blc.sellerAccount.equalsIgnoreCase(blcValid.sellerAccount)) {
						errorItem = "Seller Account";
					} else if (!blc.buyerAccount.equalsIgnoreCase(blcValid.buyerAccount)) {
						errorItem = "Buyer Account";
					} else if (!blc.type.equalsIgnoreCase(blcValid.type)) {
						errorItem = "Contract Type";
					} else if (!blc.startDate.equalsIgnoreCase(blcValid.startDate)) {
						errorItem = "Start Date";
					} else if (!blc.endDate.equalsIgnoreCase(blcValid.endDate)) {
						errorItem = "End Date";
					}

					if (errorItem.length() > 0) {
						blcException.validationNumber = -1;

						// unspecified exception
						blcException.message = "Upload file has more than one " + errorItem
								+ " in block of 48 csv records (1 contract) " + " (line " + (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw new BilateralContractUploadException(blcException.validationNumber, 0,
								blcException.message, msgStep);
					}

					if (strPeriod.length() <= 0) {
						blcException.validationNumber = 8;
						blcException.message = "Period is empty (line " + (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw new BilateralContractUploadException(blcException.validationNumber, 0,
								blcException.message, msgStep);
					}

					if (datavalidatorImpl.exceedNumericRange(Double.parseDouble(String.valueOf(blc.period)), 1.0,
							Double.parseDouble(String.valueOf(BLC_MAX_PERIOD_PER_FILE)))) {
						blcException.validationNumber = 9;
						blcException.message = "Period must between 1 to 48. (" + blc.period + ") (line " + (fileLines)
								+ ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw new BilateralContractUploadException(blcException.validationNumber, 0,
								blcException.message, msgStep);
					}

					if (csvPeriod.indexOf(blc.period) != -1) {
						// period already defined in previous csv record
						blcException.validationNumber = 24;
						blcException.message = "Bilateral Contract has duplicate period " + blc.period + " (line "
								+ (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw new BilateralContractUploadException(blcException.validationNumber, 0,
								blcException.message, msgStep);
					} else {
						// is a new period, add it into array. Array is used to check for duplicates in
						// subsequent records
						csvPeriod.add(csvPeriod.size(), blc.period);

					}

					if (strQuantity.length() <= 0) {
						blcException.validationNumber = 10;
						blcException.message = "Quantity is empty (line " + (fileLines) + ").";

						// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
						// with (fileLines)
						throw new BilateralContractUploadException(blcException.validationNumber, 0,
								blcException.message, msgStep);
					}

					String ucBlcType = blc.type.toUpperCase();

					if (ucBlcType.equals("LOAD") || ucBlcType.equals("INJECTION")) {
						if (datavalidatorImpl.exceedNumericRange(Double.parseDouble(String.valueOf(blc.quantity)),
								Double.parseDouble(String.valueOf(BLC_MIN_FRACTION)),
								Double.parseDouble(String.valueOf(BLC_MAX_FRACTION)))) {
							blcException.validationNumber = 21;
							blcException.message = "Contract type " + blc.type + " has invalid quantity: "
									+ blc.quantity + " (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}
					} else if (ucBlcType.equals("ENERGY") || ucBlcType.equals("RESERVE") || ucBlcType.equals("REGULATION")) {
						// if not ( exceedNumericRange(datavalidator, item : blc.quantity,
						// minValue : BLC_MIN_FRACTION, maxValue : BLC_MAX_FRACTION)
						// or blc.quantity == 0 ) then
						if (blc.quantity < 0) {
							blcException.validationNumber = 22;
							blcException.message = "Contract type " + blc.type + " has invalid quantity: "
									+ blc.quantity + " (line " + (fileLines) + ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Replace (rowNum + 1)
							// with (fileLines)
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}
					}

					// ASSUMPTION - csv file should only contain 1 set of BLC_MAX_PERIOD_PER_FILE
					// number of csv data records
					// Exit when number of csv records processed exceed BLC_MAX_PERIOD_PER_FILE
					// Excess record are ignored
					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
					// if csvPeriod.length = BLC_MAX_PERIOD_PER_FILE then
					// exit
					// end
					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
					// completed validation routines for current line, increment line counter
					// rowNum = rowNum + 1 //ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) -
					// Commented
					// end //ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Commented
				}

				// This is the end of For loop for 48 iteration using contract_block_i index
				// //ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
				int period = 1;
				{
					int i = 1;

					while (i <= csvPeriod.size()) {
						if (i != period) {
							blcException.validationNumber = 24;
							blcException.message = "Bilateral contract has missing periods (line " + (i + rowNum)
									+ ").";

							// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
							throw new BilateralContractUploadException(blcException.validationNumber, 0,
									blcException.message, msgStep);
						}

						period = period + 1;
						i = i + 1;
					}
				}

				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
				rowNum = rowNum + BLC_MAX_PERIOD_PER_FILE;

				// For the main top iteration, completed validation routines for current line,
				// increment line counter
			}

			// End of main top while loop
			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
			// Log JAM Message [Correction on EMCS-457]
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Validating Bilateral Contract Data: Bilateral Contract Data are Valid", "");

			variableMap.put("rowNum", rowNum);
			variableMap.put("csvFileValidator", csvFileValidator);
			variableMap.put("contractName", contractName);
			variableMap.put("inputPTPID", inputPTPID);
			variableMap.put("sacSoldBy", sacSoldBy);
			variableMap.put("sacPurchasedBy", sacPurchasedBy);
			variableMap.put("acgId", acgId);
			variableMap.put("eveId", eveId);
			variableMap.put("externalId", externalId);
			variableMap.put("fromSEW", fromSEW);
			variableMap.put("first_row_array", first_row_array);

			logger.log(Priority.INFO, "Returning from service "+msgStep +" - rowNum : " + rowNum + " csvFileValidator : " + csvFileValidator + " contractName : " + contractName + " inputPTPID : "
					+ inputPTPID + " sacSoldBy : " + sacSoldBy + " sacPurchasedBy : " + sacPurchasedBy + " acgId : " + acgId + " eveId : " + eveId + " externalId : " + externalId + " fromSEW : "
					+ fromSEW );

		} catch (BilateralContractUploadException e) {
			logger.log(Priority.ERROR, e.getMessage());
			throw e;
		} catch (Exception e) {
			throw new BilateralContractUploadException(0, 2, "", msgStep);
		}

		return variableMap;
	}

	@Transactional
	public void uploadBilateralContracts(Map<String, Object> variableMap)
	{

		try {

			String acgId = (String) variableMap.get("acgId");
			CsvFileValidator csvFileValidator = (CsvFileValidator) variableMap.get("csvFileValidator");
			String eveId = (String) variableMap.get("eveId");
			String externalId = (String) variableMap.get("externalId");
			List<Map<Integer, Object>> first_row_array = (List<Map<Integer, Object>>) variableMap.get("first_row_array");
			Integer rowNum = (Integer) variableMap.get("rowNum");
			String sacPurchasedBy = (String) variableMap.get("sacPurchasedBy");
			String sacSoldBy = (String) variableMap.get("sacSoldBy");
			String userId = (String) variableMap.get("userId");
			Integer versionNum = (Integer) variableMap.get("versionNum");

			logger.log(Priority.INFO,
					"Input Parameters uploadBilateralContracts ( rowNum : " + rowNum + " csvFileValidator : " + csvFileValidator + " externalId : "
							+ externalId + " first_row_array : " + first_row_array + " sacSoldBy : " + sacSoldBy
							+ " sacPurchasedBy : " + sacPurchasedBy + "versionNum : " + versionNum + " acgId : " + acgId
							+ " eveId : " + eveId + " userId : " + userId + ")");

			final String activityName = "uploadBilateralContracts()";

			msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_VALIDATE_AND_UPLOAD_DATA + "." + activityName;

			logger.log(Priority.INFO, logPrefix + "Starting Activity: " + msgStep + " ...");

			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
			int BLC_MAX_PERIOD_PER_FILE = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
			rowNum = 1;

			// skip the header line at index 0 later in the for loop
			// maxversionNum as Int = 0
			int totalLines = csvFileValidator.csvFileData.size();

			while (rowNum < totalLines) {
				externalId = String.valueOf(first_row_array.get(rowNum / BLC_MAX_PERIOD_PER_FILE).get(6));

				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
				BilateralContractUploadException blcException = new BilateralContractUploadException(0, 2, "", msgStep);
				String blcId;

				// BELOW TO BE REMOVED IN PRODUCTION!!!
				// default version no.
				// BLC_TEST_VERSION as String = "1"
				blcId = utilityFunctions.getEveId();
				int csvLineNum;
				List<String> line;
				UploadBilateralContract blc = new UploadBilateralContract();
				blc.initializeDbItem();

				Map<Integer, List<String>> mapline = csvFileValidator.getCsvFileData();
				line = mapline.get(rowNum);

				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
				String strPeriod = null;
				String strQuantity = null;
				blc.contractName = String.valueOf(line.get(0)).trim();
				blc.sellerAccount = String.valueOf(line.get(1)).trim();
				blc.buyerAccount = String.valueOf(line.get(2)).trim();
				blc.type = String.valueOf(line.get(3)).trim();
				blc.reserveGroup = String.valueOf(line.get(4)).trim();
				blc.startDate = String.valueOf(line.get(5)).trim();
				blc.endDate = String.valueOf(line.get(6)).trim();
				strPeriod = String.valueOf(line.get(7)).trim();
				strQuantity = String.valueOf(line.get(8)).trim();

				if (String.valueOf(line.get(7)).length() > 0) {
					blc.period = Integer.parseInt(line.get(7).trim());
				}

				if (String.valueOf(line.get(7)).length() > 0) {
					// Start of ITSM 17002.18 enhancements
					blc.quantity = Double.parseDouble(line.get(7).trim());

					// End of ITSM 17002.18 enhancements
				}

				String sqlCommand = "SELECT (NVL(MAX(TO_NUMBER(version)),0) + 1) CNT "
						+ "FROM nem.NEM_BILATERAL_CONTRACTS WHERE SAC_ID_SOLD_BY = '" + sacSoldBy + "'"
						+ "AND SAC_ID_PURCHASED_BY = '" + sacPurchasedBy + "'" + "AND CONTRACT_TYPE = '" + blc.type
						+ "'";

				// ITSM-17449-Suggested enhancement for BIL Phase 2
				versionNum = 1;

				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
				for (Map<String, Object> recordMap : resultList) {
					BigDecimal bg =  (BigDecimal) recordMap.get("CNT");
					versionNum = bg.intValue();
				}

				// Eve_id is NULL -- no exceptions
				String sqlBC = "INSERT INTO NEM.nem_bilateral_contracts "
						+ "( id, version, name, External_id, Contract_type, Start_date, "
						+ "End_date, Nde_id, Acg_id, Sac_id_purchased_by, Sac_id_sold_by, Eve_id, Frontend_user_id ) "
						+ "VALUES ( '" + blcId + "', '" + String.valueOf(versionNum) + "', '" + blc.contractName
						+ "', '" + externalId + "', '" + blc.type + "', TO_DATE('" + blc.startDate
						+ "', 'dd-MON-yyyy')," + " TO_DATE('" + blc.endDate + "', 'dd-MON-yyyy'), NULL, '" + ((acgId==null) ? "" : acgId)
						+ "', '" + sacPurchasedBy + "', '" + sacSoldBy + "', '" + eveId + "', '" + userId + "' )";

				jdbcTemplate.update(sqlBC);

				String sqlParam = "INSERT INTO nem_bilateral_parameters "
						+ "( id, version, period, value, Blt_id, Blt_version, Frontend_user_id ) "
						+ "VALUES ( SYS_GUID(),?,?,?,?,?,? )";
				csvLineNum = 1;

				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Commented
				// while csvLineNum < csvFileValidator.csvFileData.length do //ITSM 17426 - N
				// Contracts Block per BIL File (BPM 8.1) - Commented
				for (int contract_block_i = 1; contract_block_i <= BLC_MAX_PERIOD_PER_FILE; contract_block_i++) {
					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
					blc.period = 0;
					blc.quantity = null;

					// line = csvFileValidator.csvFileData[csvLineNum] //ITSM 17426 - N Contracts
					// Block per BIL File (BPM 8.1) - Commented
					// line = (String[]) csvFileValidator.csvFileData[rowNum - 1 +
					// contract_block_i];
					line = mapline.get(rowNum - 1 + contract_block_i);

					// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Added
					blc.period = Integer.parseInt(line.get(7).trim());
					blc.quantity = Double.parseDouble(line.get(8).trim());

					Object[] params = new Object[6];
					params[0] = String.valueOf(versionNum);
					params[1] = blc.period;
					params[2] = blc.quantity;
					params[3] = blcId;
					params[4] = String.valueOf(versionNum);
					params[5] = userId;
					jdbcTemplate.update(sqlParam, params);

					// completed validation routines for current line, increment line counter
					csvLineNum = csvLineNum + 1;
				}

				logger.log(Priority.INFO, logPrefix + "Bilateral Contract Data Inserted Successfully.");

				// Log JAM Message [Correction on EMCS-457]
				utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Inserting Bilateral Contract Data into Database",
						"");

				utilityFunctions.logJAMMessage(eveId, "I", msgStep,
						"Inserted " + csvLineNum + " rows for Start Date: " + blc.startDate + " End Date: "
								+ blc.endDate + " with New Version: " + String.valueOf(versionNum),
						"");

				// if fromSEW then //ITSM-17449-Suggested enhancement for BIL Phase 2 -
				// commented
				// Auto Authorise Bileteral from SEW
				bilateralContractImpl.autoAuthorizeBilateralFileSEW(blcId, eveId, "SYSTEM");

				// end //ITSM-17449-Suggested enhancement for BIL Phase 2 - commented
				// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - Start
				rowNum = rowNum + BLC_MAX_PERIOD_PER_FILE;

				// For the main top iteration
			}

			// End of main top while loop
			// ITSM 17426 - N Contracts Block per BIL File (BPM 8.1) - End
			logger.log(Priority.INFO, "Returning from service "+msgStep);
		} catch (SQLException sqle) {

			throw new BilateralContractUploadException(0, 2, sqle.getMessage(), msgStep);
		} catch (Exception e) {

			throw new BilateralContractUploadException(0, 2, e.getMessage(), msgStep);
		}

	}

	public Map<String, Object> handleException(Map<String, Object> variableMap) {

		String ebtEventsRowId = (String) variableMap.get("ebtEventsRowId");
		String errorMsg = null;
		String eveId = (String) variableMap.get("eveId");
		String sewUploadEventsId = (String) variableMap.get("sewUploadEventsId");
		Boolean fromSEW = (Boolean) variableMap.get("fromSEW");
		BilateralContractUploadException bilateralContractUploadException = (BilateralContractUploadException) variableMap.get("bilateralContractUploadException");

		final String activityName = "handleException()";
		msgStep = BusinessParameters.PROCESS_NAME_BILATERAL_CONTRACT_VALIDATE_AND_UPLOAD_DATA + "." + activityName;

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
		variableMap.put("bilateralContractUploadException", bilateralContractUploadException);
		variableMap.put("errorMsg", errorMsg);
		logger.info("Returning from service "+msgStep+" - bilateralContractUploadException :" + bilateralContractUploadException+ " : errorMsg - "+errorMsg);
		return variableMap;
	}
	
	public void testingException(Map<String, Object> variableMap) 
	{
		logger.info("Inside testingException ");
		NullPointerException exception = new NullPointerException();
		logger.info("Throwing  NullPointerException from testingException ");
		throw exception;

	}
}
