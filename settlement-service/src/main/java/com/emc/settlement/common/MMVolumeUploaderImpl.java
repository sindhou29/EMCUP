package com.emc.settlement.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.exceptions.MMVolumeUploadException;
import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.emc.settlement.model.backend.pojo.fileupload.MMVolume;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MMVolumeUploaderImpl {

	protected static final Logger logger = Logger.getLogger(MMVolumeUploaderImpl.class);

	public MMVolumeUploaderImpl() {
		super();
	}

	public String logPrefix;
	public String msgStep;

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private PavPackageImpl pavPackageImpl;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataValidatorImpl objDataValidatorImpl;

	public void validateMMVolumeData(CsvFileValidator csvFileValidator, String eveId) throws Exception
	{
		msgStep = "FileUpload.MMVolumeUploader.validateMMVolumeDate()";

		logger.log(Priority.INFO,logPrefix + "Starting Method: " + msgStep + " ...");

		/**********
		 Points to note for MM Volume (MMV) data validation:
		 a.	Name: Settlement Account name as provided by EMA.
		 b.	Settlement Account: Unique Settlement Account ID registered with EMC.
		 c.	Start Date: Format dd-Mon-yyyy or dd-Mon-yy. Start date when the MM Volume figure shall be effective for the account.
		 d.	Start Date shall be any date within the FSC tenure.
		 f.	MM Volume: MM volume of the account. Precision shall be 1dp.
		 7.	If there is any issue in the file format or content, complete file shall be rejected. An alert email shall be generated for EMC MO Team
		 10.	If the Settlement Account present in the MMV File is not available in the Standing Data corresponding to the start date,
		 system will NOT process the uploaded file and generate a warning to user. Checking for subsequent dates shall be covered via alerts
		 ********* */

		String JAVA_SETT_DATE_FORMAT = "dd-MMM-yyyy";
		//String ebtEventId = "MM_VOLUME_INPUT_PREPARATION";

		String msslSettAccount = utilityFunctions.getSysParamVarChar( "MSSL SETTLEMENT ACCOUNT");
		MMVolumeUploadException mmvException = new MMVolumeUploadException( 0,  2,"",  msgStep);
		List<String> line;

		// MMVolume retrieved from CSV file as string format
		String strMMVolume;

		MMVolume mmv = new MMVolume();

		// sacId retrieved nem_settlement_accounts database
		String strSacId;

		String strStandingVersion = "x";


		Date startDate = null;

		Date pStartDate = null;

		// true if this is the first csv record in the input file
		int rowNum = 1;

		// skip the header line at index 0
		int totalLines = csvFileValidator.csvFileData.size();

		while (rowNum < totalLines) {

			strMMVolume = null;
			line = csvFileValidator.csvFileData.get(rowNum);

			mmv.settlementAccountName = String.valueOf(line.get(0));
			mmv.settlementAccountId = String.valueOf(line.get(1));
			mmv.startDate = String.valueOf(line.get(2));
			strMMVolume = String.valueOf(line.get(3));

			if (strMMVolume.length() > 0) {
				mmv.mmVolume = Double.parseDouble(strMMVolume);
			}

			mmv.eveId = eveId;

			// validation #1 - Contract Name should not be empty
			if (mmv.settlementAccountName.length() <= 0) {
				mmvException.validationNumber = 1;
				mmvException.message = "Account Name is empty (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #2 - Contract Name length should not exceed 30
			if (mmv.settlementAccountName.length() > 30) {
				mmvException.validationNumber = 2;
				mmvException.message = "Account Name " + mmv.settlementAccountName + " length exceeded 30 characters (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #3 - Settlement Account should not be empty
			if (mmv.settlementAccountId.length() <= 0) {
				mmvException.validationNumber = 3;
				mmvException.message = "Settlement Account is empty (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #4 - Settlement Date should not be empty
			if (mmv.startDate.length() <= 0) {
				mmvException.validationNumber = 4;
				mmvException.message = "Settlement Date is empty (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			//@Todo validation #5 - Settlement Date format should be DD-MMM-YYYY
			if (! objDataValidatorImpl.isValidSettDateFormat( mmv.startDate)) {
				mmvException.validationNumber = 5;
				mmvException.message = "Settlement Date " + mmv.startDate +
						" not in DD-MMM-YYYY format (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #6 - Settlement Date should be a valid Settlement Date
			if (! objDataValidatorImpl.isValidDate( mmv.startDate, JAVA_SETT_DATE_FORMAT)) {
				mmvException.validationNumber = 6;
				mmvException.message = "Start Date " + mmv.startDate +
						" is not a valid date (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			startDate = utilityFunctions.stringToDate( mmv.startDate, "dd-MMM-yyyy");

			//validation# BR00001 time bound fsc tenure settlement date should be after FSC effective start date
			if(!utilityFunctions.isAfterFSCEffectiveStartDate( startDate)){
				mmvException.validationNumber = 7;
				mmvException.message = "FSC Settlement Date " + mmv.startDate +
						" is before FSC Scheme Effective Start Date "
						+new SimpleDateFormat("dd-MMM-yyyy").format(utilityFunctions.getSysParamTime( "FSC_EFF_START_DATE"))
						+" (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			//validation# BR00001 time bound fsc tenure settlement date should be before FSC Effective end date
			if(!utilityFunctions.isBeforeFSCEffectiveEndDate(startDate)){
				mmvException.validationNumber = 8;
				mmvException.message = "FSC Settlement Date " + mmv.startDate +
						" is after FSC Scheme Effective End Date "
						+ new SimpleDateFormat("dd-MMM-yyyy").format(utilityFunctions.getSysParamTime("FSC_EFF_END_DATE"))
						+" (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			//@Todo this validation not required
			//validation #19 - MSSL Settlement Account should be valid for the Start Date
			// get Standing Version
			strStandingVersion =  pavPackageImpl.getStandingVersion( startDate);
			mmv.sacId = objDataValidatorImpl.getSacIdByDisplayTitle( msslSettAccount,  strStandingVersion);

			if (mmv.sacId == null) {
				mmvException.validationNumber = 9;
				mmvException.message = "MSSL Settlement Account: " + msslSettAccount +
						" is not valid for the Start Date " + mmv.startDate +
						" (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #7 cont - invalid settlement account (external Id in nem_settlement_accounts)
			strSacId = objDataValidatorImpl.getSacIdByExternalId( mmv.settlementAccountId, strStandingVersion);

			if (strSacId == null) {
				mmvException.validationNumber = 10;
				mmvException.message = "ID " + mmv.settlementAccountId +
						" not found in system for Start date " +
						mmv.startDate + " (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// validation #11 - invalid Settlement Date should not be prior to existing start date for this account
			//KMER CO_G Start date 02-Apr-2014 (line 2) cannot be prior to its existing MM Volume start date 04-Apr-2014
			pStartDate = getStartDateForAccount(strSacId);
			if(pStartDate != null && startDate.compareTo(pStartDate) < 0){
				mmvException.validationNumber = 11;
				mmvException.message = "ID " + mmv.settlementAccountId +
						" Start date " +
						mmv.startDate + " (line " + (rowNum + 1) + "). cannot be prior to its existing MM Volume start date "+new SimpleDateFormat("dd-MMM-yyyy").format(pStartDate);
				throw mmvException;
			}

			// validation #12 - MM Volume should not be empty
			if (strMMVolume.length() < 0) {
				mmvException.validationNumber = 12;
				mmvException.message = "MM Volume is empty (line " + (rowNum + 1) + ").";
				throw mmvException;
			}

			// completed validation routines for current line, increment line counter
			rowNum = rowNum + 1;
		}


		logger.log(Priority.INFO,"Validating MM Volume Data: MM Volume Data is Valid");

		// Log JAM Message
		utilityFunctions.logJAMMessage(eveId,  "I", msgStep, "Validating MM Volume Data: MM Volume Data is Valid", "");
	}



	public void uploadMMVolumeData(CsvFileValidator csvFileValidator, String eveId) throws Exception
	{
		try{
			msgStep = "FileUpload.MMVolumeUploader.uploadMMVolumeData()";

			logger.log(Priority.INFO, logPrefix + "Starting Method: " + msgStep + " ...");

			utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Inserting MM Volume Data into Database", "");

			MMVolumeUploadException mmvException = new MMVolumeUploadException(0, 2, "", msgStep);
			// 2 = DATA VALIDATION
			mmvException.validationType = 2;

			// 0 = success, else is an exception
			mmvException.validationNumber = 0;

			mmvException.message = "";

			int csvLineNum;
			List<String> line;
			MMVolume mmv = new MMVolume();

			csvLineNum = 1;

			String sqlMMVolume = "INSERT INTO NEM.NEM_FSC_SAC_MM_VOLUMES "
					+ " ( id, START_DATE, END_DATE, SAC_ID, VERSION, EVE_ID, VOLUME, FSC_EFF_START_DATE, FSC_EFF_END_DATE ) "
					+ " VALUES ( SYS_GUID(),TO_DATE(?, 'DD-MON-YYYY'),TO_DATE(?, 'DD-MON-YYYY'),?,?,?,?,?,? ) ";

			// String sqlMMGetMaxVersion = "select MAX(TO_NUMBER (version)), start_date from
			// NEM.NEM_FSC_SAC_MM_VOLUMES where SAC_ID = ? and start_date between trunc(?)
			// and trunc(?) " +
			// " group by start_date ";

			String sqlMMGetMaxVersion = "SELECT version, start_date FROM NEM.NEM_FSC_SAC_MM_VOLUMES outer_1 WHERE version = (SELECT MAX (TO_NUMBER (inner_1.version)) "
					+ " FROM NEM.NEM_FSC_SAC_MM_VOLUMES inner_1 WHERE inner_1.sac_id = outer_1.sac_id) AND outer_1.sac_id = ? and outer_1.start_date between trunc(?) and trunc(?) ";

			String sqlMMUpdateEndDate = "update NEM.NEM_FSC_SAC_MM_VOLUMES set end_date = TO_DATE(?, 'DD-MON-YYYY') where sac_id= ? and version = ? ";

			Date fscEffectiveStartDate = utilityFunctions.getSysParamTime("FSC_EFF_START_DATE");

			Date fscEffectiveEndDate = utilityFunctions.getSysParamTime("FSC_EFF_END_DATE");

			line = csvFileValidator.csvFileData.get(1);

			// skip the header line at index 0
			String strFirstSettDate = String.valueOf(line.get(2));
			Date firstSettDate = utilityFunctions.stringToDate(strFirstSettDate, "dd-MMM-yyyy");
			Date startDate;
			String sacPrevVersion = null;
			Date sacPrevStartDate = null;
			String endDate = "01-Jan-3000";

			// get version from PavPackage for SETTLEMENT_FSC_MM_VOLUMES
			String theNextPackageVersion = pavPackageImpl.createMMVolumePackage(firstSettDate);
			int packageVersion = (int) Double.parseDouble(theNextPackageVersion);

			while (csvLineNum < csvFileValidator.csvFileData.size()) {

				line = csvFileValidator.csvFileData.get(csvLineNum);

				mmv.settlementAccountName = String.valueOf(line.get(0));
				mmv.settlementAccountId = String.valueOf(line.get(1));
				mmv.startDate = String.valueOf(line.get(2));
				mmv.mmVolume = Double.parseDouble(line.get(3));

				startDate = utilityFunctions.stringToDate(mmv.startDate, "dd-MMM-yyyy");

				mmv.standingVersion = pavPackageImpl.getStandingVersion(startDate);

				mmv.sacId = objDataValidatorImpl.getSacIdByExternalId(mmv.settlementAccountId, mmv.standingVersion);

				// check if mm volume is present for this account
				// sacPrevVersion = null;
				Object[] params = new Object[3];
				params[0] =  mmv.sacId;
				params[1] =  utilityFunctions.convertUDateToSDate(fscEffectiveStartDate);
				params[2] =  utilityFunctions.convertUDateToSDate(fscEffectiveEndDate);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlMMGetMaxVersion, params);
				for (Map row : list) {
					sacPrevVersion = (String) row.get("version");
					sacPrevStartDate = (Date) row.get("start_date");
				}

				// record already exists for this account so update end date as startdate-1
				if (sacPrevVersion != null) {

					// if startdate for account's existing record is same as startdate from input
					// file then set it as enddate for the previous version
					if (sacPrevStartDate.compareTo(startDate) == 0) {
						endDate = new SimpleDateFormat("dd-MMM-yyyy").format(sacPrevStartDate);
					} else {
						// compute enddate = startdate - 1
						endDate = utilityFunctions.computeDueDate(mmv.startDate, "dd-MMM-yyyy", -1);
					}

					// update the previous records endDate
					Object[] params1 = new Object[3];
					params1[0] =  endDate;
					params1[1] =  mmv.sacId;
					params1[2] =  sacPrevVersion;
					jdbcTemplate.update(sqlMMUpdateEndDate, params1);

				}

				// insert record for mm volume
				Object[] params1 = new Object[8];
				params1[0] =  mmv.startDate;
				params1[1] =  "01-Jan-3000";
				params1[2] =  mmv.sacId;
				params1[3] =  String.valueOf(packageVersion);
				params1[4] =  eveId;
				params1[5] =  mmv.mmVolume;
				params1[6] =  utilityFunctions.convertUDateToSDate(fscEffectiveStartDate);
				params1[7] =  utilityFunctions.convertUDateToSDate(fscEffectiveEndDate);
				jdbcTemplate.update(sqlMMVolume, params1);
				// completed validation routines for current line, increment line counter
				csvLineNum = csvLineNum + 1;
			}
			logger.log(Priority.INFO, "**MMV Insert Successful " + (csvLineNum - 1));
			utilityFunctions.logJAMMessage(eveId, "I", msgStep,
					"Inserted " + (csvLineNum - 1) + " rows for MM Volume Data: ", "");
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
	}


	public Date getStartDateForAccount(String accSacId)
	{
		String sqlMMGetMaxVersion = "SELECT start_date FROM NEM.NEM_FSC_SAC_MM_VOLUMES outer_1 WHERE version = (SELECT MAX (TO_NUMBER (inner_1.version)) "
				+ " FROM NEM.NEM_FSC_SAC_MM_VOLUMES inner_1 WHERE inner_1.sac_id = outer_1.sac_id) AND outer_1.sac_id = ? and outer_1.start_date between trunc(?) and trunc(?) ";

		Date sacPrevStartDate = null;

		Date fscEffectiveStartDate = utilityFunctions.getSysParamTime("FSC_EFF_START_DATE");

		Date fscEffectiveEndDate = utilityFunctions.getSysParamTime("FSC_EFF_END_DATE");

		try {
			Map<String, Object> map = jdbcTemplate.queryForMap(sqlMMGetMaxVersion, new Object[] {accSacId,
					utilityFunctions.convertUDateToSDate(fscEffectiveStartDate),
					utilityFunctions.convertUDateToSDate(fscEffectiveEndDate)});
			sacPrevStartDate = (Date) map.get("start_date");
		}
		catch (DataAccessException e) {
			logger.log(Priority.ERROR, "[EMC] getStartDateForAccount() - Empty Result for Query:  "+sqlMMGetMaxVersion+", Message: " + e.getMessage());
//			e.printStackTrace();
		}

		return sacPrevStartDate;
	}

}
