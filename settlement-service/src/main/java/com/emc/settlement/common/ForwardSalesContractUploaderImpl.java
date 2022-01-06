/**
 * 
 */
package com.emc.settlement.common;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.exceptions.FSCUploadException;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.fileupload.ForwardSalesContract;
import com.emc.settlement.model.backend.pojo.fileupload.ForwardSalesContractUploader;

/**
 * @author DWTN1561
 *
 */
@Component
public class ForwardSalesContractUploaderImpl {

	/**
	 * 
	 */
	public ForwardSalesContractUploaderImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private DataValidatorImpl dataValidatorImpl;
	@Autowired
	private PavPackageImpl pavPackageImpl;
	@Autowired
	private AlertNotificationImpl alertNotificationImpl;
	@Autowired
	ForwardSalesContractImpl fscImpl;
	
	String msgStep = null;
	String logPrefix = null;
	
    @Autowired
	private JdbcTemplate jdbcTemplate;
	protected static final Logger logger = Logger.getLogger(ForwardSalesContractUploaderImpl.class);

	public void validateForwardSalesContracts(CsvFileValidator csvFileValidator, String eveId, ForwardSalesContractUploader fscUploader) throws Exception
	{
		{
		    msgStep = "FileUpload.ForwardSalesContractUploader.validateForwardSalesContract()";

		    logger.log(Priority.INFO,logPrefix + "Starting Method: " + msgStep + " ...");

		    /*  
		        	Points to note for Forward Sales Contract (FSC) data validation:
		        	There are 48 records in a complete set of FSC records for periods 1 - 48
		        	All 48 records in a set will have the SAME externalId, Name, SettlementAccount and SettlementDate.
		        	Each FSC csv file will contain only one set of 48 records
		        	The data set will be saved into database only if all 48 records are valid
		        	Each settlement date can have multiple sets of FSC records having different externalIds.
		        	********* */
		    int FSC_MAX_PERIOD_PER_FILE = ((int) utilityFunctions.getSysParamNum( "NO_OF_PERIODS"));
		    Date fscEffectiveStartDate = utilityFunctions.getSysParamTime( "FSC_EFF_START_DATE");
		    Date fscEffectiveEndDate = utilityFunctions.getSysParamTime( "FSC_EFF_END_DATE");
		    String JAVA_SETT_DATE_FORMAT = "dd-MMM-yyyy";
		    String ebtEventId = "FORWARDSALES_CONTRACT_INPUT_PREPARATION";
		    String msslSettAccount = utilityFunctions.getSysParamVarChar( "MSSL SETTLEMENT ACCOUNT");
		    FSCUploadException fscException = new FSCUploadException( 2,  0,  "", msgStep);
		    List<String> line;

		    // period retrieved from CSV file as string format
		    String strPeriod;

		    // price retrieved from CSV file as string format
		    String strPrice;

		    // quantity retrieved from CSV file as string format
		    String strQuantity;

		    // 8.0.01 Change FSC By All
		    String fscAllowed = "N";

		    // default is Other than Genco EG Retailer all other MPs can not submit FSC
		    // strLastSettDate_fscAllowed as String = "karen1"
		    String strLastSettAcc_fscAllowed = "karen2";

		    // sacId retrieved nem_settlement_accounts database
		    String strSacId;
		    String strLastSettDate = "x";
		    String strStandingVersion = "x";
		    String strComparedPrice = null;

		    // to keep track of missing periods or duplicate periods
		    List<Integer> csvPeriod = new ArrayList<Integer>();

		    // store reference id to validate FSC contract for same settlement date
		    String strWarningFSCData = null;

		    // store warning for multiple settlement account for one forward sales contract
		    String strWarningSettAcct = null;

		    // store mulitple FSC data for one settlement account
		    List<String> strFSCData = new ArrayList<String>();

		    // store multiple settlement account for one forward sales contract 
		    List<String> strMultiSettAcct = new ArrayList<String>();

		    // 
		    HashSet tradingDatesSet = new HashSet();

		    // count num of times for multiple settlement account for one FSC contract
		    int iUploadtimes = 1;
		    int iIndex = 0;

		    // to keep track of missing periods or duplicate periods
		    Date settlementDate = null;
		    ForwardSalesContract fsc = new ForwardSalesContract();
		    ForwardSalesContract fscValid = new ForwardSalesContract();
		    
		    // to keep validated values to speed up subsequent validations
		    fscImpl.initializeDbItem(fscValid);

		    boolean isValidItem;
		    boolean firstRecord;

		    // true if this is the first csv record in the input file	
		    int rowNum = 1;

		    // skip the header line at index 0
		    int totalLines = csvFileValidator.getCsvFileData().size();

		    while (rowNum < totalLines) {
		        fscImpl.initializeDbItem(fsc);

		        strPeriod = null;
		        strPrice = null;
		        strQuantity = null;
		        line = csvFileValidator.getCsvFileData().get(rowNum);

		        // externalId maps to 'Reference' in Header line
		        fsc.setExternalId(String.valueOf(line.get(0)));
		        fsc.setName(String.valueOf(line.get(1)));
		        fsc.setSettlementAccount(String.valueOf(line.get(2)));
		        fsc.setSettlementDate(String.valueOf(line.get(3)));
		        strPeriod = String.valueOf(line.get(4));
		        strPrice = String.valueOf(line.get(5));
		        strQuantity = String.valueOf(line.get(6));

		        if (strPeriod.length() > 0) {
		            fsc.setPeriod(Integer.parseInt(strPeriod));
		        }

		        if (strPrice.length() > 0) {
		            fsc.setPrice(Double.parseDouble(strPrice));
		        }

		        if (strQuantity.length() > 0) {
		            fsc.setQuantity(Double.parseDouble(strQuantity));
		        }

		        fsc.setEveId(eveId);

		        // ---- BEGIN Validation Process for a csv record
		        firstRecord = rowNum % FSC_MAX_PERIOD_PER_FILE == 1;

		        // 8.0.01 Changes FSC By All
		        // validation #19 cont - invalid settlement account (external Id in nem_settlement_accounts)
		        // strSacId = getSacIdByExternalId(datavalidator, externalId : fsc.settlementAccount, 
		        //           standingVersion : strStandingVersion)
		        // if strSacId = null then
		        //    fscException.validationNumber = 10
		        //    fscException.message = "ID " + fsc.settlementAccount + 
		        //    " not found in system for Settlement date " + 
		        //    fsc.settlementDate + " (line " + (rowNum + 1) + ")."
		        //    throw fscException
		        // end
		        // validation #21 cont - Checking Whether settlement account (SAC ID in nem_settlement_accounts) is allowed to submit FSC Contracts
		        // fscAllowed = EMC.UtilityFunctions.isFSCAllowedForSacId(sacId : strSacId, 
		        //           standingVersion : strStandingVersion)
		        // if fscAllowed = "N" then
		        //    fscException.validationNumber = 21
		        //    fscException.message = "ID " + fsc.settlementAccount + 
		        //    " is not Allowed to Submit FSC Contracts " + 
		        //    " (line " + (rowNum + 1) + ")."
		        //    throw fscException
		        // end
		        // 8.0.01  Changes FSC By All End        
		        // Check if FSC parent data has changed ...
		        if (!fsc.externalId.equalsIgnoreCase(fscValid.externalId) || !fsc.name.equalsIgnoreCase(fscValid.name) || !fsc.settlementDate.equalsIgnoreCase(fscValid.settlementDate) || !fsc.settlementAccount.equalsIgnoreCase(fscValid.settlementAccount)) {
		            // Begin validation for 'static' data in csv file
		            // Static data need only be validated once as they remain the same for all 48 periods
		            // Static data are: externalId, contract name, settlement account, settlement date
		            // validation #1 - FSC Reference should not be empty
		            if (fsc.externalId.length() <= 0) {
		                fscException.validationNumber = 1;
		                fscException.message = "Reference is empty (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #2 - FSC Reference length should not exceed 12
		            if (fsc.externalId.length() > 12) {
		                fscException.validationNumber = 2;
		                fscException.message = "Reference " + fsc.externalId + " length exceeded 12 characters (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #3 - The 3rd to the last character of the Reference should be a letter "F" 
		            iIndex = fsc.externalId.indexOf("-");

		            if (! dataValidatorImpl.isValidFSCReference( fsc.externalId.charAt( iIndex + 1),  "F")) {
		                fscException.validationNumber = 3;
		                fscException.message = "The 3rd to the last character of the Reference " + fsc.externalId + 
		                " is not letter 'F' (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #4 - Contract Name should not be empty 
		            if (fsc.name.length() <= 0) {
		                fscException.validationNumber = 4;
		                fscException.message = "Contract Name is empty (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #5 - Contract Name length should not exceed 30
		            if (fsc.name.length() > 30) {
		                fscException.validationNumber = 5;
		                fscException.message = "Contract Name " + fsc.name + " length exceeded 30 characters (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #6 - Settlement Account should not be empty
		            if (fsc.settlementAccount.length() <= 0) {
		                fscException.validationNumber = 6;
		                fscException.message = "Settlement Account is empty (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #7 - Settlement Date should not be empty
		            if (fsc.settlementDate.length() <= 0) {
		                fscException.validationNumber = 7;
		                fscException.message = "Settlement Date is empty (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // @Todo validation #8 - Settlement Date format should be DD-MMM-YYYY 
		            if (! dataValidatorImpl.isValidSettDateFormat( fsc.settlementDate)) {
		                fscException.validationNumber = 8;
		                fscException.message = "Settlement Date " + fsc.settlementDate + 
		                " not in DD-MMM-YYYY format (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #9 - Settlement Date should be a valid Settlement Date
		            if (! dataValidatorImpl.isValidDate( fsc.settlementDate, JAVA_SETT_DATE_FORMAT)) {
		                fscException.validationNumber = 9;
		                fscException.message = "Settlement Date " + fsc.settlementDate + 
		                " is not a valid date (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            settlementDate = utilityFunctions.stringToDate( fsc.settlementDate, "dd-MMM-yyyy");
		            tradingDatesSet.add(settlementDate);

		            // validation# BR00001 time bound fsc tenure settlement date should be after FSC effective start date
		            if (settlementDate.compareTo(fscEffectiveStartDate) < 0) {
		                fscException.validationNumber = 9;
		                fscException.message = "FSC Settlement Date " + fsc.settlementDate + " is before FSC Scheme Effective Start Date " + 
		                new SimpleDateFormat("dd-MMM-yyyy").format(fscEffectiveStartDate) + " (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation# BR00001 time bound fsc tenure settlement date should be before FSC Effective end date
		            if (settlementDate.compareTo(fscEffectiveEndDate) > 0) {
		                fscException.validationNumber = 9;
		                fscException.message = "FSC Settlement Date " + fsc.settlementDate + 
		                " is after FSC Scheme Effective End Date " + 
		                new SimpleDateFormat("dd-MMM-yyyy").format(fscEffectiveEndDate) + 
		                " (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // validation #19 - MSSL Settlement Account should be valid for the Settlement Date
		            // get Standing Version
		            if (!fsc.settlementDate.equalsIgnoreCase(strLastSettDate)) {
		                strLastSettDate = fsc.settlementDate;

		                //params[0] = settlementDate;

		                // get standing version
		                strStandingVersion = pavPackageImpl.getStandingVersion(settlementDate);

		                if (strStandingVersion == null) {
							throw new Exception("Error getting standing current version !!!");
		                }

		                fsc.standingVersion = strStandingVersion;
		                fsc.sacPurchaseId = dataValidatorImpl.getSacIdByDisplayTitle(msslSettAccount, strStandingVersion);

		                if (fsc.sacPurchaseId == null) {
		                    fscException.validationNumber = 9;
		                    fscException.message = "MSSL Settlement Account: " + msslSettAccount + 
		                    " is not valid for the Settlement Date " + fsc.settlementDate + 
		                    " (line " + (rowNum + 1) + ").";
		                    throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                }
		            }

		            // validation #19 cont - invalid settlement account (external Id in nem_settlement_accounts)
		            strSacId = dataValidatorImpl.getSacIdByExternalId( fsc.settlementAccount, strStandingVersion);

		            if (strSacId == null) {
		                fscException.validationNumber = 10;
		                fscException.message = "ID " + fsc.settlementAccount + 
		                " not found in system for Settlement date " + 
		                fsc.settlementDate + " (line " + (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }

		            // 8.0.01 Changes FSC By All Start            
		            // if fsc.settlementDate != strLastSettDate_fscAllowed && fsc.settlementAccount != strLastSettAcc_fscAllowed then
		            if (!fsc.settlementAccount.equalsIgnoreCase(strLastSettAcc_fscAllowed)) {
		                // strLastSettDate_fscAllowed = fsc.settlementDate
		                strLastSettAcc_fscAllowed = fsc.settlementAccount;

		                // clear params
		                // params[0] = settlementDate    
		                // get standing version
		                // strStandingVersion = PavPackage.getStandingVersion(settlementDate : settlementDate)
		                // if strStandingVersion = null then
		                //    throw Exception(arg1 : "Error getting standing current version while checking Allow FSC Submission !!!")
		                // end
		                // fscAllowed = EMC.UtilityFunctions.isFSCAllowedForSacId(sacId : strSacId, 
		                //           standingVersion : strStandingVersion)
		                fscAllowed = utilityFunctions.isFSCAllowedForSacId( strSacId, fsc.standingVersion);

		                // validation #30 cont - Checking Whether settlement account (SAC ID in nem_settlement_accounts) is allowed to submit FSC Contracts
		                if (fscAllowed.equals("N")) {
		                    fscException.validationNumber = 30;
		                    fscException.message = "ID " + fsc.settlementAccount + 
		                    " is not Allowed to Submit FSC Contracts " + 
		                    " (line " + (rowNum + 1) + ").";
		                    throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                }
		            }

		            // 8.0.01 Changes FSC By All End                           
		            fscUploader.sacSoldId.put(fsc.settlementAccount, strSacId);

		            // Begin if rowNum!=1
		            if (rowNum != 1) {
		                // validation #17 - A particular Settlement Account for a Settlement Date and period cannot have more than one FSC entry in the same FSC file.
		                // Only for Allocated FSC Data Validation
		                // @Todo include period
		                boolean isMultiFSC = false;

		                // iIndex = fsc.externalId.indexOf("-");
		                // sri commented as this check is not needed for FSC
		                // if (datavalidator.isNumeric(value : fsc.externalId.charAt(position : iIndex + 1))) {
		                if (strFSCData != null && strFSCData.size() > 0) {
		                    {
		                        int j = 0;

		                        while (j <= strFSCData.size() - 1) {
		                            // if similar settlement account and same settlement date is found
		                            if (strFSCData.get(j).indexOf(fsc.settlementAccount) != - 1 && strFSCData.get(j).indexOf(fsc.settlementDate) != - 1) {
		                                // but different reference number, throw exception
		                                if (fsc.externalId.compareTo(strFSCData.get(j).substring(strFSCData.get(j).indexOf(",") + 2)) != 0) {
		                                    isMultiFSC = true;
		                                    iIndex = j;

		                                    break;
		                                }
		                            }

		                            j = j + 1;
		                        }
		                    }

		                    // for j in 0 .. length(strFSCData)-1 do
		                    if (isMultiFSC == true) {
		                        fscException.validationNumber = 17;
		                        fscException.message = "One Settlement Account " + fsc.settlementAccount + " cannot have more than one FSC Entry" + 
		                        "Data " + strFSCData.get(iIndex).substring( 0,  strFSCData.get(iIndex).indexOf(",")) + ", " + fsc.externalId + " for a Settlement Date " + 
		                        fsc.settlementDate + " (line " + rowNum + ").";
		                        throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                    }
		                    else {
		                        strFSCData.add(strFSCData.size(), fsc.externalId + ", " + fsc.settlementAccount + "(" + fsc.settlementDate + ")");
		                    }
		                }
		                else {
		                    // if strFSCData is not null and length(strFSCData) > 0 then 
		                    strFSCData.add(strFSCData.size(), fsc.externalId + ", " + fsc.settlementAccount + "(" + fsc.settlementDate + ")");

		                    // iIndex = fscValid.externalId.indexOf("-");
		                    // sri commented as this check not needed for FSC
		                    // if (datavalidator.isNumeric(value : fscValid.externalId.charAt(position : iIndex + 1))) {
		                    if (fsc.externalId != fscValid.externalId || fsc.settlementAccount != fscValid.settlementAccount || fsc.settlementDate != fscValid.settlementDate) {
		                        if (fsc.externalId != fscValid.externalId && fsc.settlementAccount == fscValid.settlementAccount && fsc.settlementDate == fscValid.settlementDate) {
		                            fscException.validationNumber = 17;
		                            fscException.message = "One Settlement Account " + fsc.settlementAccount + " cannot have more than one FSC Entry " + 
		                            "Data " + fscValid.externalId + ", " + fsc.externalId + " for a Settlement Date " + fsc.settlementDate + " (line " + rowNum + ").";
		                            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                        }
		                        strFSCData.add(strFSCData.size(), fscValid.externalId + ", " + fscValid.settlementAccount + "(" + fscValid.settlementDate + ")");
		                    }

		                    // if fsc.externalId != fscValid.externalId or
		                    // }
		                    // if isNumeric(datavalidator, value: charAt(fscValid.externalId, position: iIndex + 1))
		                }

		                // if fsc.externalId != fscValid.externalId or 
		                // } //if dataValidator.isNumeric
		                // validation #20 - one forward sales (fsc) only for one settlement account (Sac)
		                // send an alert via e-mail to the settlement team, do not throw exception
		                boolean isDifferent = false;
		                boolean isMultiSett = false;

		                if (strMultiSettAcct != null && strMultiSettAcct.size() > 0) {
		                    {
		                        int j = 0;

		                        while (j <= strMultiSettAcct.size() - 1) {
		                            // If same reference number is found, check whether it is the same settlement account
		                            if (strMultiSettAcct.get(j).indexOf(fsc.externalId) != - 1) {
		                                // If same settlement account is found for same reference number, exit
		                                if (fsc.settlementAccount.compareTo( strMultiSettAcct.get(j).substring(strMultiSettAcct.get(j).indexOf(",") + 2)) != 0) {
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
		                        strMultiSettAcct.add(strMultiSettAcct.size(), fsc.externalId + ", " + fsc.settlementAccount);
		                        if (isMultiSett == true) {
		                            iUploadtimes = iUploadtimes + 1;

		                            if (strWarningSettAcct == null && strWarningSettAcct.length() == 0) {
		                                strWarningFSCData = fsc.externalId;
		                                strWarningSettAcct = strMultiSettAcct.get(iIndex).substring( strMultiSettAcct.get(iIndex).indexOf(",") + 2) + 
		                                                     ", " + fsc.settlementAccount;
		                            }
		                            else {
		                                // Verify for any duplicate data in the array	
		                                if (strWarningFSCData.indexOf(fsc.externalId) == - 1) {
		                                    strWarningFSCData = strWarningFSCData + ", " + fsc.externalId;
		                                }

		                                if (strWarningSettAcct.indexOf(fsc.settlementAccount) == - 1) {
		                                    strWarningSettAcct = strWarningSettAcct + ", " + fsc.settlementAccount;
		                                }
		                            }
		                        }
		                    }
		                }
		                else {
		                    strMultiSettAcct.add(strMultiSettAcct.size(), fsc.externalId + ", " + fsc.settlementAccount);
		                }

		                // validation #21 For each FSC Reference – Account – Settlement Date there should exactly be 48 records / periods
		                // chk 4 48 total recs
		                // validation #21 - each Settlement account (Sac) has only FSC_MAX_PERIOD_PER_FILE unique periods for each settlement date (Sd)
		                // logic: sort csvPeriod array and check that sequence is in asc order from 0 - FSC_MAX_PERIOD_PER_FILE
		                // 		  array value (period) does not match array index implies duplicate/missing period
		                int duplicate_period = fscImpl.isValidNumOfPeriods( csvPeriod, FSC_MAX_PERIOD_PER_FILE);

		                if (duplicate_period != 0) {
		                    if (duplicate_period != 1) {
		                        // duplicate period
		                        fscException.validationNumber = 21;
		                        fscException.message = "Settlement Account has duplicate period " + duplicate_period + 
		                        " for the Settlement Date (" + fscValid.settlementDate + ") and Reference (" + fsc.externalId + "). ";
		                        throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                    }
		                    else {
		                        // missing period
		                        fscException.validationNumber = 21;
		                        fscException.message = "Reference " + fsc.externalId + " Settlement date " + fscValid.settlementDate + 
		                        fsc.message + " (line " + rowNum + ").";
		                        throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                    }
		                }

		                // 	validation #22 For each FSC Reference, FSC Name, Settlement Account and Settlement Date combination, Settlement Dates should be contiguous. 
		                // (this is a warning - it does not skip the file load)
		                // validation #22 - contiguous settlement date check - flag a warning
		                if (fscValid.externalId.equalsIgnoreCase(fsc.externalId) && fscValid.name.equalsIgnoreCase(fsc.name) && fscValid.settlementAccount.equalsIgnoreCase(fsc.settlementAccount) && fscValid.settlementDate.equalsIgnoreCase(fsc.settlementDate)) {
		                    String nextSettDate = utilityFunctions.computeDueDate( fscValid.settlementDate, JAVA_SETT_DATE_FORMAT, 1);

		                    if (utilityFunctions.stringToDate(nextSettDate, JAVA_SETT_DATE_FORMAT).compareTo(utilityFunctions.stringToDate(fsc.settlementDate, JAVA_SETT_DATE_FORMAT)) < 0) {
		                        fscException.validationNumber = 22;
		                        fscException.message = "Settlement Dates are not contiguous for Forward Sales Contract Reference: " + 
		                        fsc.externalId + ", Settlement account: " + fsc.settlementAccount + 
		                        ", Date1: " + fscValid.settlementDate + 
		                        ", Date2: " + fsc.settlementDate + 
		                        " (line " + (rowNum + 1) + ").";

		                        logger.log(Priority.INFO,fscException.message);

		                        // Log JAM Message
		                        String errorCode = String.valueOf(fscException.validationType) + "," + String.valueOf(fscException.validationNumber);
		                        utilityFunctions.logJAMMessage(eveId, "E", 
		                                                       msgStep, fscException.message, 
		                                                       errorCode);

		                        // reset the validation number to normal
		                        fscException.validationNumber = 0;
		                    }
		                }

		                // clear out csvPeriod for next set of 48 records
		                csvPeriod.clear();
		            }

		            // rownum != 1
		            // End if rowNum!=1			
		            // new record - store the values into array for use in next activity 
		            if (!(fscValid.externalId != null && fscValid.externalId.equalsIgnoreCase(fsc.externalId))  || !(fscValid.settlementDate != null && fscValid.settlementDate.equalsIgnoreCase(fsc.settlementDate))) {
		            	fscUploader.fscDbInsert.add(fsc.externalId + "," + fsc.settlementDate + "," + fsc.sacPurchaseId + "," + fsc.standingVersion);
		            }

		            // store the validated static data 
		            fscValid.externalId = fsc.externalId;
		            fscValid.name = fsc.name;
		            fscValid.settlementAccount = fsc.settlementAccount;
		            fscValid.settlementDate = fsc.settlementDate;
		        }

		        // if (fsc.externalId != fscValid.externalId...
		        // End of if for - Check if FSC parent data has changed ...
		        // validate price, quantity, period
		        // validation #10 - Period should not be empty
		        if (strPeriod.length() <= 0) {
		            fscException.validationNumber = 10;
		            fscException.message = "Period is empty (line " + (rowNum + 1) + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validation #11 - Period must be valid (range 1 to 48) within range of 1 to FSC_MAX_PERIOD_PER_FILE
		        if (dataValidatorImpl.exceedNumericRange( Double.parseDouble(String.valueOf(fsc.period)),  1.0, Double.parseDouble(String.valueOf(FSC_MAX_PERIOD_PER_FILE)))) {
		            fscException.validationNumber = 11;
		            fscException.message = "Period must between 1 to 48. (" + fsc.period + ") (line " + (rowNum + 1) + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validion #12 - Contract Price should not be empty
		        if (strPrice.length() <= 0) {
		            fscException.validationNumber = 12;
		            fscException.message = "Contract Price is empty (line " + (rowNum + 1) + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validation #13 - Contract Price can be negative, positive and zero 	
		        if (! dataValidatorImpl.isNumeric(String.valueOf(fsc.price))) {
		            fscException.validationNumber = 13;
		            fscException.message = "Contract Price " + fsc.price + 
		            " is not numeric for, SAC Id " + fsc.settlementAccount + 
		            " Settlement date " + fsc.settlementDate + 
		            " Period " + fsc.period + " (line " + rowNum + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validation #14 - FSC Contract Quantity should not be empty
		        if (strQuantity.length() <= 0) {
		            fscException.validationNumber = 14;
		            fscException.message = "Forward Sales Contract Quantity is empty (line " + (rowNum + 1) + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validation #15 -  The FSC Quantity must be a positive numeric value or zero
		        if (! dataValidatorImpl.isPositiveValue(fsc.quantity)) {
		            fscException.validationNumber = 15;
		            fscException.message = "Forward Sales Contract Quantity " + fsc.quantity + 
		            " cannot be negative, SAC Id " + fsc.settlementAccount + 
		            " Settlement date " + fsc.settlementDate + 
		            " Period " + fsc.period + " (line " + (rowNum + 1) + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }

		        // validation #18 - A Settlement Account cannot have more the one period for a Settlement Date. (i.e. no duplicate periods)
		        // logic: if records exist, validation fails. Else, check that there are no csv entries for current period
		        // logic: check against array 
		        // @Todo
		        if (! fsc.isFirstVcForSd) {
		            if (fsc.vcExistingSacForSd.indexOf(fsc.settlementAccount) != - 1) {
		                // forward sales contract already uploaded for this account
		                fscException.validationNumber = 18;
		                fscException.message = "Settlement Account has more than one same period for a Settlement Date (line " + 
		                (rowNum + 1) + ").";
		                throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		            }
		            else {
		                if (csvPeriod.indexOf(fsc.period) != - 1) {
		                    // period already defined in previous csv record
		                    fscException.validationNumber = 18;
		                    fscException.message = "Settlement Account has more than one same period for a Settlement Date (line " + 
		                    (rowNum + 1) + ").";
		                    throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		                }
		                else {
		                    // is a new period, add it into array. Array is used to check for duplicates in subsequent records
		                    csvPeriod.add(csvPeriod.size(), fsc.period);
		                }
		            }
		        }
		        else {
		            csvPeriod.add(csvPeriod.size(), fsc.period);
		        }

		        // completed validation routines for current line, increment line counter 
		        rowNum = rowNum + 1;
		    }

		    // while rowNum < length(csvFileValidator.csvFileData) do
		    // continue from validation #21 (if only 1 set of 48 periods) - duplicate period or missing period
		    // For each FSC Reference – Account – Settlement Date there should exactly be 48 records / periods
		    int duplicate_period = fscImpl.isValidNumOfPeriods(csvPeriod,  FSC_MAX_PERIOD_PER_FILE);

		    if (duplicate_period != 0) {
		        if (duplicate_period != 1) {
		            // duplicate period
		            fscException.validationNumber = 21;
		            fscException.message = "Settlement Account has duplicate period " + duplicate_period + 
		            " for the Settlement Date (" + fscValid.settlementDate + ") and Reference (" + fsc.externalId + "). ";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }
		        else {
		            // missing period
		            fscException.validationNumber = 21;
		            fscException.message = "Reference " + fsc.externalId + " Settlement date " + fscValid.settlementDate + 
		            fsc.message + " (line " + rowNum + ").";
		            throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
		        }
		    }

		    // continue from validation #20 - One FSC Contract cannot be for more than one Settlement Account  (Sac)
		    /*If the upload is via EBT and this validation fails, send an alert via E-mail to the Settlement Team (emcsettlement@emcsg.com). 
		            If it is via front end, display a warning message. 
		            The alert/warning should be "FSC Reference <REF> was uploaded <NUM> times for the following Settlement Accounts: <SAC EXTERNAL IDs>" 
		            where <REF> - Reference Number, <NUM> - number of times duplicated and <SAC EXTERNAL IDs>- Settlement External IDs
		            */
		    // @Todo
		    if (strWarningSettAcct != null && strWarningSettAcct.length() > 0) {
		        fscException.validationNumber = 20;
		        fscException.message = "One Forward Sales Contract Reference " + strWarningFSCData + 
		        " cannot be for more than One Settlement Account: " + strWarningSettAcct;

		        logger.log(Priority.WARN,fscException.message);

		        // Log JAM Message
		        String errorCode = String.valueOf(fscException.validationType) + "," + String.valueOf(fscException.validationNumber);
		        utilityFunctions.logJAMMessage(eveId, "E", msgStep, 
		                                       fscException.message, errorCode);

		        AlertNotification alert = new AlertNotification();
		        alert.businessModule = "Forward Sales Contract Input Preparation";
		        alert.recipients = "emcsettlement@emcsg.com";
		        alert.subject = "Forward Sales File file upload warning";
		        alert.content = "Forward Sales Contract Reference " + strWarningFSCData + " was uploaded " + 
		        iUploadtimes + " times for the following Settlement Accounts: " + strWarningSettAcct;
		        alert.noticeType = "Forward Sales Contract Input Preparation";
		        alertNotificationImpl.sendEmail(alert);
		    }

		    // Validation #24. Each FSC contract reference must cover all trading dates and periods of the applicable quarter in the file.
		    // Commented validation for trading dates
		    // line = csvFileValidator.csvFileData[1];
		    // String strFirstSettDate = String.valueOf(line[3]);
		    // int missingTradingDates = fsc.hasCoveredAllDatesOfQuarter(tradingDatesSet, strFirstSettDate);
		    // if(missingTradingDates != 0){
		    // missing trading dates
		    //  fscException.validationNumber = 24;
		    // fscException.message = fsc.message;
		    // throw fscException;
		    // }
		    logger.log(Priority.INFO,"Validating Forward Sales Contract Data: Forward Sales Contract Data are Valid");

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Validating Forward Sales Contract Data: Forward Sales Contract Data are Valid","");
		}
		
	}

	public void uploadForwardSalesContracts(CsvFileValidator csvFileValidator, String eveId, ForwardSalesContractUploader fscUploader) throws Exception, FSCUploadException
	{
		msgStep = "FileUpload.ForwardSalesContractUploader.uploadForwardSalesContracts()";

		logger.log(Priority.INFO, logPrefix + "Starting Method: " + msgStep + " ...");

		try{
			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Inserting ForwardSales Contract Data into Database",
					"");

			FSCUploadException fscException = new FSCUploadException(2, 0, "", msgStep);

			// 2 = DATA VALIDATION
			fscException.validationType = 2;

			// 0 = success, else is an exception
			fscException.validationNumber = 0;
			fscException.message = "";
			String fscId = null;
			int csvLineNum;
			List<String> line;
			ForwardSalesContract fsc = new ForwardSalesContract();
			fscImpl.initializeDbItem(fsc);

			csvLineNum = 1;
			String prevSettDate = "01-JAN-1800";
			String prevExternalId = "XXXXXXXXXXXX";
			String prevAccount = "-";
			int recPtr;
			String firstSettDate = null;
			String lastSettDate = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			dateFormat.setLenient(false);

			Date dateToday = new Date();
			String date = dateFormat.format(dateToday);
			String sqlFSC = "INSERT INTO NEM.nem_fsc_contracts "
					+ "( id, External_id, Contract_type, name, Sac_sold_id, "
					+ "Sac_purchased_id, Created_date, Eve_id, Settlement_date ) "
					+ "VALUES ( ?,?,?,?,?,?,TO_DATE(?, 'dd MON yyyy hh24:mi:ss'),?,TO_DATE(?, 'DD-MON-YYYY') )";
			String sqlParam = "INSERT INTO NEM.nem_fsc_contract_params "
					+ "( id, settlement_period, quantity, price, fc_id, created_date ) "
					+ "VALUES ( SYS_GUID(),?,?,?,?, SYSDATE ) ";

			while (csvLineNum < csvFileValidator.getCsvFileData().size()) {
				fscImpl.initializeDbItem(fsc);

				line = csvFileValidator.getCsvFileData().get(csvLineNum);
				fsc.externalId = (String.valueOf(line.get(0)));
				fsc.name = (String.valueOf(line.get(1)));
				fsc.settlementAccount = (String.valueOf(line.get(2)));
				fsc.settlementDate = (String.valueOf(line.get(3)));
				fsc.period = (Integer.parseInt(line.get(4)));
				fsc.price = (Double.parseDouble(line.get(5)));
				Double x = Double.valueOf(line.get(6));// TODO MURALI - check Double<5>
				fsc.quantity = (x / 1000.0);

				if (csvLineNum == 1) {
					firstSettDate = fsc.getSettlementDate();
				} else if (csvLineNum == (csvFileValidator.getCsvFileData().size() - 1)) {
					lastSettDate = fsc.getSettlementDate();
				}

				// new record for forward sales contract
				// retrieve sacPurchaseId value from delimited string:
				// "externalId,settlementDate,sacPurchaseId"
				if (!fsc.getExternalId().equalsIgnoreCase(prevExternalId)
						|| !fsc.getSettlementAccount().equalsIgnoreCase(prevAccount)
						|| !fsc.getSettlementDate().equalsIgnoreCase(prevSettDate)) {
					fsc.setSacPurchaseId(null);
					recPtr = 0;

					while (recPtr < fscUploader.fscDbInsert.size()) {
						String rec = fscUploader.fscDbInsert.get(recPtr);
						String[] recArr = rec.split(",");

						if (rec.indexOf(fsc.externalId) == 0 && rec.indexOf(fsc.settlementDate) >= 0) {
							fsc.sacPurchaseId = recArr[2];
							fsc.standingVersion = recArr[3];

							break;
						} else {
							recPtr = recPtr + 1;
						}
					}

					if (fsc.sacPurchaseId == null) {
						logger.log(Priority.INFO,
								"[EMC] validateAndUploadData.uploadForwardSalesContracts() -- "
										+ "missing sacPurchaseId for externalId=" + fsc.externalId + ", settDate="
										+ fsc.settlementDate + " in dumpArray: " + fscUploader.fscDbInsert.toString());

						fscException.validationNumber = -1;
						fscException.message = "Missing sac_purchase_id value for Reference: " + fsc.externalId
								+ ", SettlementDate: " + fsc.settlementDate;
						throw new FSCUploadException(fscException.validationNumber, 0, fscException.message, msgStep);
					}
					fsc.sacSoldId = dataValidatorImpl.getSacIdByExternalId(fsc.settlementAccount, fsc.standingVersion);
					prevExternalId = fsc.externalId;
					prevSettDate = fsc.settlementDate;
					prevAccount = fsc.settlementAccount;
					fscId = utilityFunctions.getEveId();
					// fsc.settlementAccount
					
					Object[] params = new Object[9];
					params[0] =  fscId;
					params[1] =  fsc.externalId;
					params[2] =  "FSC";
					params[3] =  fsc.name;
					params[4] =  (String) fscUploader.sacSoldId.get(fsc.settlementAccount);
					params[5] =  fsc.sacPurchaseId;
					params[6] =  date;
					params[7] =  eveId;
					params[8] =  fsc.settlementDate;
					jdbcTemplate.update(sqlFSC, params);
				}

				Object[] params = new Object[4];
				params[0] =  fsc.period;
				params[1] =  fsc.quantity;
				params[2] =  fsc.price;
				params[3] =  fscId;
				jdbcTemplate.update(sqlParam, params);
				// completed validation routines for current line, increment line counter
				csvLineNum = csvLineNum + 1;
			}

			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Inserted " + (csvLineNum - 1)
					+ " rows for Settlement Date: " + firstSettDate + " to " + lastSettDate, "");
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
	}
	

}
