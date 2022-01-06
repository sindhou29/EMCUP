/**
 * 
 */
package com.emc.settlement.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.exceptions.VestingContractUploadException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.DataValidator;
import com.emc.settlement.model.backend.pojo.fileupload.VestingContract;
import com.emc.settlement.model.backend.pojo.fileupload.VestingContractUploader;

/**
 * @author DWTN1561
 *
 */

@Component
public class VestingContractUploaderImpl {

	protected static final Logger logger = Logger.getLogger(VestingContractUploaderImpl.class);
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	CsvFileValidatorImpl csvFileValidatorImpl;
	@Autowired
	AlertNotificationImpl alertNotificationImpl;
	@Autowired
	VestingContractUploaderImpl vstUploaderImpl;
	@Autowired
	DataValidatorImpl dataValidatorImpl;
	@Autowired
	VestingContractImpl vcImpl;    
	@Autowired
	private JdbcTemplate jdbcTemplate;
    
	public void uploadVestingContracts(String logPrefix,
			String eveId,
			CsvFileValidator csvFileValidator,
			VestingContractUploader vcUploader)
	{
	    String msgStep = "VestingContractUploaderImpl.uploadVestingContracts()";

	    logger.log(Priority.INFO,logPrefix + "Starting Method: " + msgStep + " ...");
	    logger.info("Input parameters : eveId :" + eveId + " csvFileValidator :"+csvFileValidator+" vcUploader :" +vcUploader);
	    try{

	    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
	                                   "Inserting Vesting Contract Data into Database", 
	                                   "");

	    VestingContractUploadException vcException = new VestingContractUploadException();
	    vcException.validationType = 2;

	    // 2 = DATA VALIDATION
	    vcException.validationNumber = 0;

	    // 0 = success, else is an exception
	    vcException.message = "";
	    String vcId = null;
	    int csvLineNum;
	    List<String> line;
	    VestingContract vc = new VestingContract();

	    // define an array to associate vcId value with elements in vcDbInsert[]
	    String[] vcIdArray;

	    // variable to store BVP value only first occurance as it is same for rest of the file DRCAP-Phase2-RahulRaghu
	    // vdecBVP as Decimal
	    // onlyOnceStoreBVP as Int = 0
	    vcImpl.initializeDbItem(vc);

	    csvLineNum = 1;
	    String prevSettDate = "01-JAN-1800";
	    String prevExternalId = "XXXXXXXXXXXX";
	    String prevAccount = "-";
	    int recPtr;
	    int iIndex;

	    // [ITSM-12670]
	    String[] params;
	    String firstSettDate = null;
	    String lastSettDate = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	    dateFormat.setLenient(false);

	    Date dateToday = new Date();
	    String date = dateFormat.format(dateToday);
	    DataValidator datavalidator = new DataValidator();

	    // [ITSM-12670]
	    // [ITSM-12670] While adding the data in NEM_VESTING_CONTRACTS, the CONTRACT_TYPE will be set to 'VEQ'
	    // for the Allocated Vesting Data. Tendered Vesting Data, the CONTRACT_TYPE will be set to 'TEQ'
	    // [ITSM-15086] LNG Vesting Data, the CONTRACT_TYPE will be set to 'LEQ'
	    String sqlVC = "INSERT INTO NEM.nem_vesting_contracts " + 
	    "( id, External_id, Contract_type, name, Sac_sold_id, " + 
	    "Sac_purchased_id, Created_date, Eve_id, Settlement_date ) " + 
	    "VALUES ( ?,?,?,?,?,?,TO_DATE(?, 'dd MON yyyy hh24:mi:ss'),?,TO_DATE(?, 'DD-MON-YYYY') )";
	    String sqlParam = "INSERT INTO NEM.nem_vesting_contract_params " + 
	    "( id, settlement_period, quantity, price, vc_id, created_date ) " + 
	    "VALUES ( SYS_GUID(),?,?,?,?, SYSDATE ) ";
	    String sqlSAC = "SELECT Id FROM nem_settlement_accounts " + 
	    "WHERE External_Id = ? AND VERSION = ?";

	    while (csvLineNum < csvFileValidator.csvFileData.size()) {
	        vcImpl.initializeDbItem(vc);

	        line = csvFileValidator.csvFileData.get(csvLineNum);
	        vc.externalId = String.valueOf(line.get(0));
	        vc.name = String.valueOf(line.get(1));
	        vc.settlementAccount = String.valueOf(line.get(2));
	        vc.settlementDate = String.valueOf(line.get(3));
	        vc.period = Integer.parseInt(line.get(4));
	        vc.price = Double.parseDouble(line.get(5));
	        Double x = Double.parseDouble(line.get(6));
	        vc.quantity = x / 1000.0;
	        iIndex = 0;

	        // [ITSM-12670]
	        if (csvLineNum == 1) {
	            firstSettDate = vc.settlementDate;
	        }
	        else if (csvLineNum == (csvFileValidator.csvFileData.size() - 1)) {
	            lastSettDate = vc.settlementDate;
	        }

	        // new record for vesting contract
	        // retrieve sacPurchaseId value from delimited string: "externalId,settlementDate,sacPurchaseId"
	        if (!vc.externalId.equalsIgnoreCase(prevExternalId)  || !vc.settlementAccount.equalsIgnoreCase(prevAccount) || !vc.settlementDate.equalsIgnoreCase(prevSettDate)) {
	            vc.sacPurchaseId = null;
	            recPtr = 0;

	            while (recPtr < vcUploader.vcDbInsert.size()) {
	                String rec = vcUploader.vcDbInsert.get(recPtr);
	                String[] recArr = rec.split(",");
	                if (rec.indexOf(vc.externalId) == 0 && rec.indexOf(vc.settlementDate) >= 0) {
	                    vc.sacPurchaseId = recArr[2];
	                    vc.standingVersion = recArr[3];

	                    break;
	                }
	                else {
	                    recPtr = recPtr + 1;
	                }
	            }

	            if (vc.sacPurchaseId == null) {
	                logger.log(Priority.INFO,"[EMC] validateAndUploadData.uploadVestingContracts() -- " + 
	                "missing sacPurchaseId for externalId=" + vc.externalId + 
	                ", settDate=" + vc.settlementDate + " in dumpArray: " + utilityFunctions.dumpArray(vcUploader.vcDbInsert));

	                vcException.validationNumber = - 1;
	                vcException.message = "Missing sac_purchase_id value for Reference: " + 
	                vc.externalId + ", SettlementDate: " + vc.settlementDate;
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // settDate as Time = parse(dateFormat, vc.settlementDate) 
	            // standingVersion as String = PavPackage.getStandingVersion(settlementDate : settDate)

				Object[] params1 = new Object[2];
				params1[0] =  vc.settlementAccount;
				params1[1] =  vc.standingVersion;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlSAC, params1);
				for (Map row : list) {
					vc.sacSoldId = (String)row.get(1);
				}

	            prevExternalId = vc.externalId;
	            prevSettDate = vc.settlementDate;
	            prevAccount = vc.settlementAccount;
	            vcId = utilityFunctions.getEveId();

	            // [ITSM-12670] - contract_type = 'VEQ' for allocated vesting data, 'TEQ' for tendered vesting data
	            iIndex = vc.externalId.indexOf("-");
	            boolean isAllocated = String.valueOf(vc.externalId.charAt(iIndex + 1)).matches("-?\\d+");
	            //boolean isAllocated = String.valueOf(vc.externalId.charAt(iIndex + 1)).matches("^[a-zA-Z\\d-_]+$");

	            Object[] params2 = new Object[9];
				params2[0] =  vcId;
				params2[1] =  vc.externalId;
	            if (isAllocated == true) {
	                params2[2] =  "VEQ";
	                // Store the first occurance BVP price DRCAP-Phase2-RahulRaghu 
	                // if onlyOnceStoreBVP = 0 then
	                // 	vdecBVP = vc.price  
	                // 	onlyOnceStoreBVP = onlyOnceStoreBVP + 1  
	                // end
	            }
	            else {
	                // [ITSM-15086] Check for LNG Vesting
	                if (dataValidatorImpl.isLngVesting(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	                    params2[2] =  "LEQ";
	                }
	                else {
	                    params2[2] =  "TEQ";
	                }
	            }

	            // vc.settlementAccount
				params2[3] =  vc.name;
				params2[4] =  (String) vcUploader.sacSoldId.get(vc.settlementAccount);
				params2[5] =  vc.sacPurchaseId;
				params2[6] =  date;
				params2[7] =  eveId;
				params2[8] =  vc.settlementDate;
				jdbcTemplate.update(sqlVC, params2);
	        }
			Object[] params1 = new Object[4];
			params1[0] =  vc.period;
			params1[1] =  vc.quantity;
			params1[2] =  vc.price;
			params1[3] =  vcId;
			jdbcTemplate.update(sqlParam, params1);
	        // completed validation routines for current line, increment line counter 
	        csvLineNum = csvLineNum + 1;
	    }

	    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
	                                   "Inserted " + (csvLineNum - 1) + " rows for Settlement Date: " + firstSettDate + 
	                                   " to " + lastSettDate, "");

	    // return vdecBVP    // Added a return decimal variable to represent BVP price DRCAP-Phase2-RahulRaghu  
	    
	    }catch(Exception e)
	    {
	    	logger.error("Exception "+e.getMessage());
	    }
	}

	public void validateVestingContracts(String logPrefix, 
			CsvFileValidator csvFileValidator,
			String eveId,
			VestingContractUploader vcUploader) throws Exception
	{
	    String msgStep = "VestingContractUploaderImpl.validateVestingContract()";

	    logger.log(Priority.INFO,logPrefix + "Starting Method: " + msgStep + " ...");
	    try{

	    /*  
	                    		Points to note for Vesting Contract (VC) data validation:
	                    		There are 48 records in a complete set of vc records for periods 1 - 48
	                    		All 48 records in a set will have the SAME externalId, Name, SettlementAccount and SettlementDate.
	                    		Each vc csv file will contain only one set of 48 records
	                    		The data set will be saved into database only if all 48 records are valid
	                    		Each settlement date can have multiple sets of vc records having different externalIds, but all records 
	                    		must have same price for the same period and settlement date.
	                    		********* */
	    int VC_MAX_PERIOD_PER_FILE = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
	    String JAVA_SETT_DATE_FORMAT = "dd-MMM-yyyy";
	    String ebtEventId = "VESTING_CONTRACT_INPUT_PREPARATION";
	    String msslSettAccount = utilityFunctions.getSysParamVarChar("MSSL SETTLEMENT ACCOUNT");
	    VestingContractUploadException vcException = new VestingContractUploadException( 0,  2, "", msgStep);
	    List<String> line;
	    String strPeriod;

	    // period retrieved from CSV file as string format
	    String strPrice;

	    // price retrieved from CSV file as string format
	    String strQuantity;

	    // quantity retrieved from CSV file as string format
	    String strSacId;

	    // sacId retrieved nem_settlement_accounts database
	    String strLastSettDate = "x";
	    String strStandingVersion = "x";
	    String strComparedPrice = null;

	    // [ITSM-12670] store price to validate same price for same settlement date
	    String strWarningVestData = null;

	    // [ITSM-12670] store multiple settlement account for one vesting contract
	    String strWarningSettAcct = null;

	    // [ITSM-12670] store multiple settlement account for one vesting contract
	    String strVestDDate = null;

	    // [ITSM-12670] store the first date for mulitple vesting data for one settlement account
	    List<String> strVestData = new ArrayList<String>();

	    // [ITSM-12670] store mulitple vesting data for one settlement account
	    // strVestTendered as String[]	// [ITSM-15086] will not be used in LNG Vesting
	    // [ITSM-12670] store array of tendered dates and account for one settlement account with at least one allocated vesting data
	    // strVestAllocated as String[]	// [ITSM-15086] will not be used in LNG Vesting
	    // [ITSM-15086] store the first date for multiple LNG data for one settlement account
	    List<String> strLngData = new ArrayList<String>();

	    // [ITSM-12670] store array of allocated dates and account for one settlement account with at least one allocated vesting data
	    List<String>  strMultiSettAcct = new ArrayList<String>();

	    // [ITSM-12670] store multiple settlement account for one vesting contract 
	    int iUploadtimes = 1;

	    // [ITSM-12670] count num of times for multiple settlement account for one vesting contract	
	    int iIndex = 0;

	    // [ITSM-12670] check the index of the 3rd character of the reference
	    List<Integer> csvPeriod = new ArrayList<Integer>();

	    // [ITSM-15086] Get the LNG Vesting effective date
	    Date LngEffectiveDate = utilityFunctions.getSysParamTime("LNG_VEST_EFFECTIVE_DATE");

	    // to keep track of missing periods or duplicate periods
	    Date settlementDate = null;
	    VestingContract vc = new VestingContract();
	    VestingContract vcValid = new VestingContract();

	    // to keep validated values to speed up subsequent validations
	    vcImpl.initializeDbItem(vcValid);

	    boolean isValidItem;
	    DataValidator datavalidator;
	    String sqlGetSacId = "SELECT Id FROM nem_settlement_accounts " + 
	    "WHERE External_Id = ? AND VERSION = ?";
	    String sqlGetMsslSacId = "SELECT id, external_id FROM  nem_settlement_accounts" + 
	    " WHERE display_title = ? and version = ? ";
	    String sqlStdVer = "select to_char(max(version)) version " + 
	    "from NEM.NEM_STANDING_VERSIONS_MV " + 
	    "where trunc(?) between effective_date and end_date";
	    boolean firstRecord;

	    // true if this is the first csv record in the input file	
	    int rowNum = 1;

	    // skip the header line at index 0
	    int totalLines = csvFileValidator.csvFileData.size();

	    while (rowNum < totalLines) {
	        vcImpl.initializeDbItem(vc);
	    	
	        strPeriod = null;
	        strPrice = null;
	        strQuantity = null;
	        line = csvFileValidator.csvFileData.get(rowNum);

	        // externalId maps to 'Reference' in Header line
	        vc.externalId = String.valueOf(line.get(0));
	        vc.name = String.valueOf(line.get(1));
	        vc.settlementAccount = String.valueOf(line.get(2));
	        vc.settlementDate = String.valueOf(line.get(3));
	        strPeriod = String.valueOf(line.get(4));
	        strPrice = String.valueOf(line.get(5));
	        strQuantity = String.valueOf(line.get(6));

	        if (strPeriod.length() > 0) {
	            vc.period = Integer.parseInt(strPeriod);
	        }

	        if (strPrice.length() > 0) {
	            vc.price = Double.parseDouble(strPrice);
	        }

	        if (strQuantity.length() > 0) {
	            vc.quantity = Double.parseDouble(strQuantity);
	        }

	        vc.eveId = eveId;

	        // ---- BEGIN Validation Process for a csv record
	        firstRecord = rowNum % VC_MAX_PERIOD_PER_FILE == 1;

	        // Check if vesting contract parent data has changed ...
	        if (!vc.externalId.equalsIgnoreCase(vcValid.externalId)  || !vc.name.equalsIgnoreCase(vcValid.name) || !vc.settlementDate.equalsIgnoreCase(vcValid.settlementDate) || !vc.settlementAccount.equalsIgnoreCase(vcValid.settlementAccount)) {
	            // Begin validation for 'static' data in csv file
	            // Static data need only be validated once as they remain the same for all 48 periods
	            // Static data are: externalId, contract name, settlement account, settlement date
	            // validation #1 - reference not empty
	            if (vc.externalId.length() <= 0) {
	                vcException.validationNumber = 1;
	                vcException.message = "Reference is empty (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #1 - reference less than 12 char
	            if (vc.externalId.length() > 12) {
	                vcException.validationNumber = 1;
	                vcException.message = "Reference " + vc.externalId + " length exceeded 12 characters (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #2 - [ITSM-12670] reference's 3rd to last character should be letter 'T' or a numeric value
	            // validation #2 - [ITSM-15086] reference's 3rd to last character can be letter 'L' as well
	            iIndex = vc.externalId.indexOf( "-");

	            if (! dataValidatorImpl.isNumeric(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	                if (! dataValidatorImpl.isLngVesting(String.valueOf(vc.externalId.charAt(iIndex + 1))) && ! dataValidatorImpl.isTenderedVesting(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	                    vcException.validationNumber = 2;
	                    vcException.message = "The 3rd to the last character of the Reference " + vc.externalId + 
	                    " is neither numeric value nor letter 'L' nor letter 'T' (line " + rowNum + ").";
	                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                }
	            }

	            // validation #3 - contract name not empty or more than 12 char length
	            if (vc.name.length() <= 0) {
	                vcException.validationNumber = 3;
	                vcException.message = "Contract Name is empty (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #4 - contract name less than 30 char
	            if (vc.name.length() > 30) {
	                vcException.validationNumber = 4;
	                vcException.message = "Contract Name " + vc.name + " length exceeded 30 characters (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #5 - settlement account not empty
	            if (vc.settlementAccount.length() <= 0) {
	                vcException.validationNumber = 5;
	                vcException.message = "Settlement Account is empty (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #6 - settlement date not empty and must be valid Settlement/Business date
	            if (vc.settlementDate.length() <= 0) {
	                vcException.validationNumber = 6;
	                vcException.message = "Settlement Date is empty (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #7 - settlement date is in DD-MON-YYYY format 
	            if (! dataValidatorImpl.isValidSettDateFormat(vc.settlementDate)) {
	                vcException.validationNumber = 7;
	                vcException.message = "Settlement Date " + vc.settlementDate + 
	                " not in DD-MMM-YYYY format (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #8 - settlement date must be valid date
	            if (! dataValidatorImpl.isValidDate(vc.settlementDate, JAVA_SETT_DATE_FORMAT)) {
	                vcException.validationNumber = 8;
	                vcException.message = "Settlement Date " + vc.settlementDate + 
	                " is not a valid date (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #26 - [ITSM-15086] accepts LNG Vesting data only after the LNG Vesting effective date
	            if (dataValidatorImpl.isLngVesting(String.valueOf(vc.externalId.charAt(iIndex + 1))) && utilityFunctions.stringToDate(vc.settlementDate, "dd-MMM-yyyy").compareTo(LngEffectiveDate) < 0) {
	                vcException.validationNumber = 26;
	                vcException.message = "LNG Vesting Contract Settlement Date " + vc.settlementDate + 
	                " is before LNG Vesting Contract Effective Date " + LngEffectiveDate + " (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            // validation #9 - MSSL Settlement Account is not valid for a Settlement Date
	            // get Standing Version
	            if (!vc.settlementDate.equalsIgnoreCase(strLastSettDate)) {
	                strLastSettDate = vc.settlementDate;
	                settlementDate = utilityFunctions.stringToDate(vc.settlementDate, "dd-MMM-yyyy");

	    			Object[] params = new Object[1];
	    			params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
	    			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlStdVer, params);
	    			for (Map row : list) {
	                    if ((String)row.get("version") != null) {
	                        strStandingVersion = (String)row.get("version");
	                    }
	                }

	                if (strStandingVersion == null) {
	                    throw new Exception("Error getting standing current version !!!");
	                }

	                vc.standingVersion = strStandingVersion;
	                vc.sacPurchaseId = null;

	    			Object[] params1 = new Object[2];
	    			params1[0] =  msslSettAccount;
	    			params1[1] =  strStandingVersion;
	    			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlGetMsslSacId, params1);
	    			for (Map row : list1) {
	                    vc.sacPurchaseId = (String)row.get("id");
	                }

	                if (vc.sacPurchaseId == null) {
	                    vcException.validationNumber = 9;
	                    vcException.message = "MSSL Settlement Account: " + msslSettAccount + 
	                    " is not valid for the Settlement Date " + vc.settlementDate + 
	                    " (line " + (rowNum + 1) + ").";
	                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                }
	            }

	            // validation #10 - invalid settlement account (external Id in nem_settlement_accounts)
	            strSacId = null;
    			Object[] params1 = new Object[2];
    			params1[0] =  vc.settlementAccount;
    			params1[1] =  strStandingVersion;
    			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlGetSacId, params1);
    			for (Map row : list1) {
    				strSacId = (String)row.get("Id");
                }

	            if (strSacId == null) {
	                vcException.validationNumber = 10;
	                vcException.message = "ID " + vc.settlementAccount + 
	                " not found in system for Settlement date " + 
	                vc.settlementDate + " (line " + (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }

	            vcUploader.sacSoldId.put(vc.settlementAccount, strSacId);

	            if (rowNum != 1) {
	                // validation #19 - one vesting contract (vc) only for one settlement account (Sac)
	                // one vesting contract cannot be for more than one settlement account (Sac)
	                // [ITSM-12670] : send an alert via e-mail to the settlement team, do not throw exception
	                boolean isDifferent = false;
	                boolean isMultiSett = false;

	                if (strMultiSettAcct != null && strMultiSettAcct.size() > 0) {
	                    {
	                        int j = 0;

	                        while (j <= strMultiSettAcct.size() - 1) {
	                            // If same reference number is found, check whether it is the same settlement account
	                            if (strMultiSettAcct.get(j).indexOf(vc.externalId) != - 1) {
	                                // If same settlement account is found for same reference number, exit
	                                if (vc.settlementAccount.compareTo(strMultiSettAcct.get(j).substring(strMultiSettAcct.get(j).indexOf(",") + 2)) != 0) {
	                                    isDifferent = true;

	                                    // settlement account is different
	                                    isMultiSett = true;
	                                    iIndex = j;

	                                    break;
	                                }
	                            }
	                            else {
	                                isDifferent = true;

	                                // reference number is different
	                            }

	                            j = j + 1;
	                        }
	                    }

	                    // If two different settlement accounts is found for same reference number 
	                    // or different reference number is found, saved it into the array
	                    if (isDifferent == true) {
	                        strMultiSettAcct.add(strMultiSettAcct.size(), vc.externalId + ", " + vc.settlementAccount);

	                        if (isMultiSett == true) {
	                            iUploadtimes = iUploadtimes + 1;

	                            if (strWarningSettAcct == null && strWarningSettAcct.length() == 0) {
	                                strWarningVestData = vc.externalId;
	                                strWarningSettAcct = strMultiSettAcct.get(iIndex).substring(strMultiSettAcct.get(iIndex).indexOf(",") + 2) + 
	                                                     ", " + vc.settlementAccount;
	                            }
	                            else {
	                                // Verify for any duplicate data in the array	
	                                if (strWarningVestData.indexOf(vc.externalId) == - 1) {
	                                    strWarningVestData = strWarningVestData + ", " + vc.externalId;
	                                }

	                                if (strWarningSettAcct.indexOf(vc.settlementAccount) == - 1) {
	                                    strWarningSettAcct = strWarningSettAcct + ", " + vc.settlementAccount;
	                                }
	                            }
	                        }
	                    }
	                }
	                else {
	                    strMultiSettAcct.add(strMultiSettAcct.size(), vc.externalId + ", " + vc.settlementAccount); 
	                }

	                // validation #20 - [ITSM-12670] Only for Allocated Vesting Data Validation
	                // [ITSM-15086] One Settlement Account cannot have more than one set of Allocated (Base) Vesting Data
	                boolean isMultiVest = false;
	                iIndex = vc.externalId.indexOf("-");

	                if (dataValidatorImpl.isNumeric(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	                    if (strVestData != null && strVestData.size() > 0) {
	                        {
	                            int j = 0;

	                            while (j <= strVestData.size() - 1) {
	                                // if similar settlement account and same settlement date is found
	                                if (strVestData.get(j).indexOf(vc.settlementAccount) != - 1 && strVestData.get(j).indexOf(vc.settlementDate) != - 1) {
	                                    // but different reference number, throw exception
	                                    if (vc.externalId.compareTo(strVestData.get(j).substring(strVestData.get(j).indexOf(",") + 2)) != 0) {
	                                        isMultiVest = true;
	                                        iIndex = j;

	                                        break;
	                                    }
	                                }

	                                j = j + 1;
	                            }
	                        }

	                        // for j in 0 .. length(strVestData)-1 do
	                        if (isMultiVest == true) {
	                            vcException.validationNumber = 20;
	                            vcException.message = "One Settlement Account " + vc.settlementAccount + " cannot have more than one Allocated Vesting " + 
	                            "Data " + strVestData.get(iIndex).substring(0, strVestData.get(iIndex).indexOf(",")) + ", " + vc.externalId + " for a Settlement Date " + 
	                            vc.settlementDate + " (line " + rowNum + ").";
	                            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                        }
	                        else {
	                            strVestData.add(strVestData.size(),vc.externalId + ", " + vc.settlementAccount + "(" + vc.settlementDate + ")");
	                        }
	                    }
	                    else {
	                        // if strVestData is not null and length(strVestData) > 0 then 
                            strVestData.add(strVestData.size(),vc.externalId + ", " + vc.settlementAccount + "(" + vc.settlementDate + ")");
	                        iIndex = vcValid.externalId.indexOf("-");

	                        if (dataValidatorImpl.isNumeric(String.valueOf(vcValid.externalId.charAt(iIndex + 1)))) {
	                            if (vc.externalId != vcValid.externalId || vc.settlementAccount != vcValid.settlementAccount || vc.settlementDate != vcValid.settlementDate) {
	                                if (vc.externalId != vcValid.externalId && vc.settlementAccount == vcValid.settlementAccount && vc.settlementDate == vcValid.settlementDate) {
	                                    vcException.validationNumber = 20;
	                                    vcException.message = "One Settlement Account " + vc.settlementAccount + " cannot have more than one Allocated Vesting " + 
	                                    "Data " + vcValid.externalId + ", " + vc.externalId + " for a Settlement Date " + vc.settlementDate + " (line " + rowNum + ").";
	                                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                                }
		                            strVestData.add(strVestData.size(),vcValid.externalId + ", " + vcValid.settlementAccount + "(" + vcValid.settlementDate + ")");
	                            }

	                            // if vc.externalId != vcValid.externalId or
	                        }

	                        // if isNumeric(datavalidator, value: charAt(vcValid.externalId, position: iIndex + 1))
	                    }

	                    // if vc.externalId != vcValid.externalId or 
	                }

	                // a Settlement account cannot have more than one set of LNG Vesting Data, but can have both Base and LNG together.
	                isMultiVest = false;

	                if (dataValidatorImpl.isLngVesting(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	                    if (strLngData != null && strLngData.size() > 0) {
	                        {
	                            int j = 0;

	                            while (j <= strLngData.size() - 1) {
	                                // if similar settlement account and same settlement date is found
	                                if (strLngData.get(j).indexOf(vc.settlementAccount) != - 1 && strLngData.get(j).indexOf(vc.settlementDate) != - 1) {
	                                    // but different reference number, throw exception
	                                    if (vc.externalId.compareTo(strLngData.get(j).substring(strLngData.get(j).indexOf(",") + 2)) != 0) {
	                                        isMultiVest = true;
	                                        iIndex = j;

	                                        break;
	                                    }
	                                }

	                                j = j + 1;
	                            }
	                        }

	                        // for j in 0 .. length(strLngData)-1 do
	                        if (isMultiVest == true) {
	                            vcException.validationNumber = 20;
	                            vcException.message = "One Settlement Account " + vc.settlementAccount + " cannot have more than one LNG Vesting " + 
	                            "Data " + strLngData.get(iIndex).substring( 0, strLngData.get(iIndex).indexOf(",")) + ", " + vc.externalId + " for a Settlement Date " + 
	                            vc.settlementDate + " (line " + rowNum + ").";
	                            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                        }
	                        else {
	                            strLngData.add(strLngData.size(), vc.externalId + ", " + vc.settlementAccount + "(" + vc.settlementDate + ")");
	                        }
	                    }
	                    else {
	                        // if strLngData is not null and length(strLngData) > 0 then 
	                        strLngData.add(strLngData.size(), vc.externalId + ", " + vc.settlementAccount + "(" + vc.settlementDate + ")");
	                        iIndex = vcValid.externalId.indexOf("-");

	                        if (dataValidatorImpl.isLngVesting(String.valueOf(vcValid.externalId.charAt(iIndex + 1)))) {
	                            if (vc.externalId != vcValid.externalId || vc.settlementAccount != vcValid.settlementAccount || vc.settlementDate != vcValid.settlementDate) {
	                                if (vc.externalId != vcValid.externalId && vc.settlementAccount == vcValid.settlementAccount && vc.settlementDate == vcValid.settlementDate) {
	                                    vcException.validationNumber = 20;
	                                    vcException.message = "One Settlement Account " + vc.settlementAccount + " cannot have more than one LNG Vesting " + 
	                                    "Data " + vcValid.externalId + ", " + vc.externalId + " for a Settlement Date " + vc.settlementDate + " (line " + rowNum + ").";
	                                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                                }
	                                strLngData.add(strLngData.size(), vcValid.externalId + ", " + vcValid.settlementAccount + "(" + vcValid.settlementDate + ")");
	                            }

	                            // if vc.externalId != vcValid.externalId or
	                        }

	                        // if isLngVesting(datavalidator, charAt(vcValid.externalId, position : iIndex + 1))
	                    }

	                    // if vc.externalId != vcValid.externalId or 
	                }

	                // validation #21 - [ITSM-12670] One settlement account should have at least one allocated vesting data
	                // [ITSM-15086] remove this check due to Base and LNG contracts are now optional
	                // chk 4 48 total recs
	                // validation #22 - each Settlement account (Sac) has only VC_MAX_PERIOD_PER_FILE unique periods for each settlement date (Sd)
	                // logic: sort csvPeriod array and check that sequence is in asc order from 0 - VC_MAX_PERIOD_PER_FILE
	                // 		  array value (period) does not match array index implies duplicate/missing period
	                int duplicate_period = vcImpl.isValidNumOfPeriods(vc, csvPeriod,VC_MAX_PERIOD_PER_FILE);

	                if (duplicate_period != 0) {
	                    if (duplicate_period != 1) {
	                        // duplicate period
	                        vcException.validationNumber = 22;
	                        vcException.message = "Settlement Account has duplicate period " + duplicate_period + 
	                        " for the Settlement Date (" + vcValid.settlementDate + ") and Reference (" + vc.externalId + "). ";
	                        throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                    }
	                    else {
	                        // missing period
	                        vcException.validationNumber = 22;
	                        vcException.message = "Reference " + vc.externalId + " Settlement date " + vcValid.settlementDate + 
	                        vc.message + " (line " + rowNum + ").";
	                        throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                    }
	                }

	                // validation #23 - contiguous settlement date check - flag a warning
	                if (vcValid.externalId == vc.externalId && vcValid.name == vc.name && vcValid.settlementAccount == vc.settlementAccount && vcValid.settlementDate != vc.settlementDate) {
	                    String nextSettDate = utilityFunctions.computeDueDate(vcValid.settlementDate, JAVA_SETT_DATE_FORMAT, 1);

	                    if (nextSettDate.compareTo(vc.settlementDate) < 0) {
	                        vcException.validationNumber = 23;
	                        vcException.message = "Settlement Dates are not contiguous for Vesting Contract Reference: " + 
	                        vc.externalId + ", Settlement account: " + vc.settlementAccount + 
	                        ", Date1: " + vcValid.settlementDate + 
	                        ", Date2: " + vc.settlementDate + 
	                        " (line " + (rowNum + 1) + ").";

	                        logger.log(Priority.INFO,vcException.message);

	                        // Log JAM Message
	                        String errorCode = (String.valueOf(vcException.validationType) + "," + String.valueOf(vcException.validationNumber));
	                        utilityFunctions.logJAMMessage(eveId, "E", msgStep, vcException.message, errorCode);

	                        // reset the validation number to normal
	                        vcException.validationNumber = 0;
	                    }
	                }

	                // clear out csvPeriod for next set of 48 records
	                csvPeriod.clear();
	            }

	            // rownum != 1
	            // new record - store the values into array for use in next activity 
	            if (vcValid.externalId != vc.externalId || vcValid.settlementDate != vc.settlementDate) {
	            	vcUploader.vcDbInsert.add(vc.externalId + "," + vc.settlementDate + "," + vc.sacPurchaseId + "," + vc.standingVersion);
	            }

	            // store the validated static data 
	            vcValid.externalId = vc.externalId;
	            vcValid.name = vc.name;
	            vcValid.settlementAccount = vc.settlementAccount;
	            vcValid.settlementDate = vc.settlementDate;
	        }

	        // Check if vesting contract parent data has changed ...
	        // validate price, quantity, period
	        // validation #11 - period not empty
	        if (strPeriod.length() <= 0) {
	            vcException.validationNumber = 11;
	            vcException.message = "Period is empty (line " + (rowNum + 1) + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validation #12 - period within range of 1 to VC_MAX_PERIOD_PER_FILE
	        if (dataValidatorImpl.exceedNumericRange((double)vc.period, (double)1.0, (double)VC_MAX_PERIOD_PER_FILE)) {
	            vcException.validationNumber = 12;
	            vcException.message = "Period must between 1 to 48. (" + vc.period + ") (line " + (rowNum + 1) + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validion #13 - price not empty
	        if (strPrice.length() <= 0) {
	            vcException.validationNumber = 13;
	            vcException.message = "Contract Price is empty (line " + (rowNum + 1) + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validation #14 - [ITSM-12670] Contract Price can be negative, positive and zero 	
	        // validation #14 - price is positive numeric
	        // if not isPositiveValue(datavalidator, item : vc.price) then 
	        // 	vcException.validationNumber = 14
	        // 	vcException.message = "Contract Price " + vc.price 
	        // 			+ " cannot be negative, SAC Id " + vc.settlementAccount
	        // 			+ " Settlement date " + vc.settlementDate
	        // 			+ " Period " + vc.period + " (line " + rowNum + ")."
	        // 	throw new vcException
	        // end
	        // validation #15 - quantity not empty 
	        if (strQuantity.length() <= 0) {
	            vcException.validationNumber = 15;
	            vcException.message = "Vesting Contract Quantity is empty (line " + (rowNum + 1) + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validation #16 - [ITSM-12670] vesting contract quantity must be a positive numeric value or zero
	        // validation #16 - quantity is positive numeric
	        if (! dataValidatorImpl.isPositiveValue(vc.quantity)) {
	            vcException.validationNumber = 16;
	            vcException.message = "Vesting Contract Quantity " + vc.quantity + 
	            " cannot be negative, SAC Id " + vc.settlementAccount + 
	            " Settlement date " + vc.settlementDate + 
	            " Period " + vc.period + " (line " + (rowNum + 1) + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validation #17 - price must be same for all records with same settlement date (Sd) and period
	        iIndex = vc.externalId.indexOf("-");

	        if (dataValidatorImpl.isNumeric(String.valueOf(vc.externalId.charAt(iIndex + 1)))) {
	            if (strComparedPrice == null) {
	                strComparedPrice = strPrice;
	            }
	            else {
	                if (strComparedPrice.compareTo(strPrice) != 0) {
	                    vcException.validationNumber = 17;
	                    vcException.message = "Different Contract Prices found for the same Settlement Date " + 
	                    vc.settlementDate + " and price1=" + strComparedPrice + ", price2=" + strPrice + " (line " + rowNum + ").";
	                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                }
	            }
	        }

	        if (! vcImpl.isSameVcPriceForSdPeriod(vc, eveId)) {
	            vcException.validationNumber = 17;
	            vcException.message = "Different Contract Prices found for the same Settlement Date " + 
	            vc.settlementDate + " and " + vc.message + " (line " + rowNum + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }

	        // validation #18 - A Sac cannot have more than one period for settlement date (i.e. no duplicate periods)
	        // logic: if records exist, validation fails. Else, check that there are no csv entries for current period
	        // logic: check against array 
	        if (! vc.isFirstVcForSd) {
	            if (vc.vcExistingSacForSd.indexOf(vc.settlementAccount) != - 1) {
	                // vesting contract already uploaded for this account
	                vcException.validationNumber = 18;
	                vcException.message = "Settlement Account has more than one same period for a Settlement Date (line " + 
	                (rowNum + 1) + ").";
	                throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	            }
	            else {
	                if (csvPeriod.indexOf(vc.period) != - 1) {
	                    // period already defined in previous csv record
	                    vcException.validationNumber = 18;
	                    vcException.message = "Settlement Account has more than one same period for a Settlement Date (line " + 
	                    (rowNum + 1) + ").";
	                    throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	                }
	                else {
	                    // is a new period, add it into array. Array is used to check for duplicates in subsequent records
	                    csvPeriod.add(csvPeriod.size(), vc.period);
	                }
	            }
	        }
	        else {
	            csvPeriod.add(csvPeriod.size(), vc.period);
	        }

	        // completed validation routines for current line, increment line counter 
	        rowNum = rowNum + 1;
	    }

	    // while rowNum < length(csvFileValidator.csvFileData) do
	    // continue validation #22 (if only 1 set of 48 periods) - duplicate period or missing period
	    int duplicate_period = vcImpl.isValidNumOfPeriods(vc, csvPeriod, VC_MAX_PERIOD_PER_FILE);

	    if (duplicate_period != 0) {
	        if (duplicate_period != 1) {
	            // duplicate period
	            vcException.validationNumber = 22;
	            vcException.message = "Settlement Account has duplicate period " + duplicate_period + 
	            " for the Settlement Date (" + vcValid.settlementDate + ") and Reference (" + vc.externalId + "). ";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }
	        else {
	            // missing period
	            vcException.validationNumber = 22;
	            vcException.message = "Reference " + vc.externalId + " Settlement date " + vcValid.settlementDate + 
	            vc.message + " (line " + rowNum + ").";
	            throw new VestingContractUploadException(vcException.validationNumber, vcException.validationType, vcException.message, vcException.execStep);
	        }
	    }

	    // [ISTM-12670] continue from validation #19 - one vesting contract cannot be for more than one settlement account (Sac)
	    if (strWarningSettAcct != null && strWarningSettAcct.length() > 0) {
	        vcException.validationNumber = 19;
	        vcException.message = "One Vesting Contract Reference " + strWarningVestData + 
	        " cannot be for more than One Settlement Account: " + strWarningSettAcct;

	        logger.log(Priority.INFO,vcException.message);

	        // Log JAM Message
	        String errorCode = (String.valueOf(vcException.validationType) + "," + String.valueOf(vcException.validationNumber));
	        utilityFunctions.logJAMMessage(eveId, "E", msgStep, vcException.message, errorCode);

	        AlertNotification alert = new AlertNotification();
	        alert.businessModule = "Vesting Contract Input Preparation";
	        alert.recipients = "emcsettlement@emcsg.com";
	        alert.subject = "Vesting File file upload warning";
	        alert.content = "Vesting Contract Reference " + strWarningVestData + " was uploaded " + 
	        iUploadtimes + " times for the following Settlement Accounts: " + strWarningSettAcct;
	        alert.noticeType = "Vesting Contract Input Preparation";
	        alertNotificationImpl.sendEmail(alert);
	    }

	    // [ITSM-12670] continue from validation #21: One Settlement Account should have at least one Allocated Vesting Data
	    // [ITSM-15086] remove as Base and LNG vesting are now optional
	    //    if content.length > 0 then
	    //        clear strVestTendered
	    // 
	    //        vcException.validationNumber = 21
	    //        vcException.message = "One Settlement Account (" + content + ") should have at least one Allocated Vesting " + 
	    //        "Data for the Settlement Date (" + contentDate + ")."
	    //        throw new vcException
	    //    end
	    logger.log(Priority.INFO,"Validating Vesting Contract Data: Vesting Contract Data are Valid");

	    // Log JAM Message
	    utilityFunctions.logJAMMessage( eveId, "I", msgStep, "Validating Vesting Contract Data: Vesting Contract Data are Valid", 
	                                   "");
    }catch(VestingContractUploadException vsce) {
    	vsce.printStackTrace();
    	throw vsce;
	}catch(Exception e)
	{
		logger.error("Exception "+e.getMessage());
		throw e;
    }
	}
}
