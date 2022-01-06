/**
 *
 */
package com.emc.settlement.common;

import static com.emc.settlement.model.backend.constants.BusinessParameters.FILE_VALIDATION;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.fileupload.MSSL;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrectedHeader;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLFileValidator;
import com.emc.settlement.model.backend.pojo.fileupload.SACInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author DWTN1561
 *
 */
@Component
public class MSSLFileValidatorImpl {

	protected static final Logger logger = Logger.getLogger(MSSLFileValidatorImpl.class);

	/**
	 *
	 */
	public MSSLFileValidatorImpl() {
		// TODO Auto-generated constructor stub
	}

	SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat( "dd MMM yyyy");
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	PavPackageImpl pavPackageImpl;
	@Autowired
	MSSLCorrectedHeaderImpl cmHeaderImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public void initializeMSSLFileValidator(MSSLFileValidator validator, String fileTypeArg) throws Exception {
		validator.fileType = fileTypeArg;

		// Get the Price Neutrilization date
		validator.priceNeuDate = utilityFunctions.getSysParamTime("PN_EFFECTIVE_DATE");

		// Get Period Numbers
		validator.totalP = ((int) utilityFunctions.getSysParamNum("NO_OF_PERIODS"));
		validator.sumP = ((int) utilityFunctions.getSysParamNum("SUM_OF_PERIODS"));
		validator.sumP2 = ((int) utilityFunctions.getSysParamNum("SUM_SQUARE_OF_PERIODS"));
	}


	public String getNodeId(String standingVer, String nodeName)
	{
		try {

			// Get Node ID by Node Name
			String sqlNodeID = "select ID from NEM.NEM_NODES " +
					"where VERSION=? and NAME=?";
			String nodeId = null;

			nodeId = jdbcTemplate.queryForObject(sqlNodeID, new Object[] {Integer.parseInt(standingVer), nodeName}, String.class);

			return nodeId;
		}
		catch (DataAccessException e) {
			logger.log(Priority.INFO,"[EMC] Exception in MSSLFileValidator.getNodeId() : " + e.getMessage());
			return null;
		}/*finally {
			if(pstmt != null)UtilityFunctions.close(pstmt);
			if(rs != null)UtilityFunctions.close(rs);
		}*/
	}

	public SACInfo getSACId(String standingVer, String extId)
	{
		try{

			// Get SAC ID by EXTERNAL_ID
			String sqlSacID = "select ID, EMBEDDED_GEN, RETAILER_ID, SAC_TYPE from NEM.NEM_SETTLEMENT_ACCOUNTS " +
					"where VERSION=? and EXTERNAL_ID=?";

			// sacId as String = null
			SACInfo sac = new SACInfo();
			sac.externalId = extId;
			sac.sacId = null;

			Object[] params = new Object[2];
			params[0] =  standingVer;
			params[1] =  extId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlSacID, params);
			for (Map row : list) {
				sac.sacId = (String) row.get("ID");
				sac.embeddedGen = (String) row.get("EMBEDDED_GEN");
				sac.retailerId = (String) row.get("RETAILER_ID");
				sac.sacType = (String) row.get("SAC_TYPE");
				break;
			}
			

			return sac;
		}
		catch (Exception e) {
			logger.log(Priority.INFO,"[EMC] Exception in MSSLFileValidator.getSACId() : " + e.getMessage());

			return null;
		}

	}

	public MSSL  validateLine(MSSLCorrectedHeader cmHeader, String[] line,
			int lineNum,
			MSSLFileValidator validator,
			String logPrefix) throws Exception
	{
		String msgStep = "MSSLFileValidator.validateLine()";

		// logger.log(Priority.INFO,logPrefix + "Starting Function " + msgStep + " ...");
		// **************** exception type mapping ********************
		// 4101 => Settlement Date is empty or not valid
		// 4102 => Settlement Date has one line which value is differ from the first/header line of the file
		// 4103 => Period is empty
		// 4104 => Period is not valid
		// 4105 => Quantity type is empty
		// 4106 => Quantity type is not valid among valid among \"IEQ\", \"WEQ\", \"IIQ\", \"WCQ\" and \"WPQ\"
		// 4107 => Node ID is empty when Quantity Type is \"IEQ\" or \"IIQ\"
		// 4108 => Settlement Account Id is empty when Quantity Type is \"IEQ\" or \"IIQ\"
		// 4109 => Node Id is empty when Quantity Type is \"WEQ\",\"WCQ\"
		// 4110 => Settlement Account Id is empty when Quantity Type is \"WEQ\",\"WCQ\"
		// 4111 => the data is empty For quantity type WEQ, WCQ or WPQ, each settlement date and settlement account combination
		// 4112 => the data is empty For quantity type IIQ or IEQ, each settlement date and settlement account and node combination
		// 4113 => the Settlment Account is not valid
		// 4114 => the Node should be valid
		// 4115 => Quantity is empty
		// 4116 => Guantity type is not a numeric value
		// 4117 =>
		// 4118 =>
		// 4119 =>
		// 4120 =>
		// 4121 => there are at least two different settlement date in the metering file
		// 4122 => date format is not DD-MMM-YYY
		// 4123 =>
		// 4124 => quantity type is WPQ, but the EMBEDDED_GEN column from NEM_SETTLEMENT_ACCOUNTS is not \"Y\"
		// 4125 =>
		// 4126 => quantity type is not WPQ when EMBEDDED_GEN column is \"Y\" and RETAILER_ID column is \"Y\"
		// 4127 => quantity type is not among \"WPQ\", \"WCQ\", \"WEQ\" when EMBEDDED_GEN column is \"Y\" and RETAILER_ID column is \"N\"
		// 4128 => quantity type is not among \"WPQ\", \"WCQ\", \"WEQ\" when EMBEDDED_GEN column is \"N\" and RETAILER_ID column is \"Y\"
		// 4129 => quantity type is not among  \"WCQ\", \"WEQ\" when EMBEDDED_GEN column is \"N\" and RETAILER_ID column is \"N\"
		// DRCAP PHASE2 START => DR related quantity type  \"WDQ\", \"WLQ\" ADDED
		// ************************************************************
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		dateFormat.setLenient(false);

		String settDateStr;
		MSSL x = new MSSL();
		SACInfo sac = new SACInfo();

		boolean DummyDREffDate = utilityFunctions.isDummyDREffectiveDate(validator.settlementDate);  //ByPassforSPServices Added
		Date  dummyDREffectiveDate = utilityFunctions.getSysParamTime("DUMMY_DR_EFFECTIVE_DATE"); //ByPassforSPServices Added

		// DRCAP PHASE2 START

		//logger.log(Priority.INFO,settlementDate + "settlementDate: " );

		//    logger.log(Priority.INFO,drEffectiveDate + "drEffectiveDate: " );

		// TODO Remove null check GANESH
		if (validator.settlementDate != null && validator.settlementDate.compareTo(validator.drEffectiveDate) < 0) {
			if (DummyDREffDate) {//ByPassforSPServices Added
				validator.drEffective = true;//ByPassforSPServices Added
				validator.drEffectiveDate = dummyDREffectiveDate; //ByPassforSPServices Added
			}//ByPassforSPServices Added
			else {//ByPassforSPServices Added
				validator.drEffective = false;
			}//ByPassforSPServices Added
		}
		else {
			validator.drEffective = true;
		}
		// DRCAP PHASE2 END

		// Get Authorised S-Run
		String sqlAuthSRun = "SELECT 1 FROM NEM.nem_settlement_run_status_v " +
				"WHERE run_type = 'S' AND authorised = 'A' AND settlement_date = ?";

		// ---------------------------------
		//        Starting Validation
		// ---------------------------------
		if (line.length != 6) {
			throw new MsslException(FILE_VALIDATION, 0, lineNum + 1,
					"Number of columns of the MSSL file should be 6 instead of " +
							line.length + ".", msgStep);
		}

		// 4.1.00 Settlement Date should not be empty.
		if (String.valueOf(line[1]).length() < 1) {
			throw new MsslException("DATA_VALIDATION", 4101, lineNum + 1,
					"Settlement Date should not be empty.", msgStep);
		}

		settDateStr = String.valueOf(line[1]);

		// 4.1.01 Settlement Data must be valid.
		try {
			x.settlementDate = dateFormat.parse(String.valueOf(line[1]));
		}
		catch (ParseException e) {
			logger.log(Priority.INFO,logPrefix + "Settlement Date format wrong (" +
					String.valueOf(line[1]) + ").");

			throw new MsslException("DATA_VALIDATION", 4101, lineNum + 1,
					"Settlement Date (" + String.valueOf(line[1]) + ") is not in format (dd-MMM-yyyy).",
					msgStep);
		}

		if (validator.settlementDate == null || !validator.settlementDate.equals(x.settlementDate)) {
			// New Settlement Date
			// 4.1.02 The settlment date is differ from the first line/header line of the file.
			if (validator.fileType.equals("DMF") && validator.settlementDate != null) {
				logger.log(Priority.INFO,logPrefix + "Found different Settlement Date.");

				throw new MsslException("DATA_VALIDATION", 4102, lineNum + 1,
						"The Settlment Date (" + String.valueOf(line[1]) +
								") is different from the first line/header line of the file.",
						msgStep);
			}

			validator.settlementDate = x.settlementDate;

			// logger.log(Priority.INFO,logPrefix + "The first SettlementDate is " + settlementDate.format(mask : "dd MMM yyyy"));
			try {
				validator.standingVersion = pavPackageImpl.getStandingVersion(validator.settlementDate);

				// logger.log(Priority.INFO,logPrefix + "The Standing Version is: " + standingVersion);
			}
			catch (Exception e) {
				logger.log(Priority.INFO,logPrefix + "Exception in PavPackage.getStandingVersion()...");

				throw new MsslException("DATA_VALIDATION", 4101, lineNum + 1,
						e.getMessage(), msgStep);
			}



			// 4.1.19 Authorized second settlement re-run should not exist for the Settlement Date
			int rowcnt = 0;

			try{
				Object[] params = new Object[2];
				params[0] =  utilityFunctions.convertUDateToSDate(validator.settlementDate);
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlAuthSRun, params[0]);
				for (Map row : list) {
					rowcnt = rowcnt + 1;
				}
			}catch(Exception e)
			{
				logger.error("Exception "+e.getMessage());
			}

			if (rowcnt > 0) {
				logger.log(Priority.INFO,logPrefix + "Found Authorized Second Re-run for Settlement Date: " +
						sdf.format(x.settlementDate));

				throw new MsslException("DATA_VALIDATION", 4119, lineNum + 1,
						"Authorized second settlement re-run should not exist for the Settlement Date.",
						msgStep);
			}

			if (validator.fileType.equals("CMF")) {
				validator.lastAccount = "";
				validator.lastSacId = "";
				validator.lastNodeName = "";
				validator.lastNodeId = "";
			}

			validator.priceNeuDateChecked = false;

			// EG Method
			if (validator.settlementDate.compareTo(validator.ntEffectiveDate) < 0) {
				validator.ntEffective = false;
			}
			else {
				validator.ntEffective = true;
			}

			// DRCAP PHASE2 START

			//logger.log(Priority.INFO,settlementDate + "settlementDate11: " );

			//    logger.log(Priority.INFO,drEffectiveDate + "drEffectiveDate111: " );

			if (validator.settlementDate.compareTo(validator.drEffectiveDate) < 0) {
				if (DummyDREffDate) {//ByPassforSPServices Added
					validator.drEffective = true;//ByPassforSPServices Added
					validator.drEffectiveDate = dummyDREffectiveDate; //ByPassforSPServices Added
				}//ByPassforSPServices Added
				else {//ByPassforSPServices Added
					validator.drEffective = false;
				}//ByPassforSPServices Added
			}
			else {
				validator.drEffective = true;
			}
			// DRCAP PHASE2 END
		}

		// 4.1.03 Period should not be empty
		String str = String.valueOf(line[2]).trim();

		if (str.length() < 1) {
			logger.log(Priority.INFO,logPrefix + "Period is empty.");

			throw new MsslException("DATA_VALIDATION", 4103, lineNum + 1,
					"Period should not be empty.", msgStep);
		}

		// get the Period
		try {
			x.period = Integer.valueOf(str);

			// logMessage "x.period is:" + x.period using severity = WARNING
		}
		catch (Exception e) {
			logger.log(Priority.INFO,logPrefix + "Period: " + str + " is not valid.");

			throw new MsslException("DATA_VALIDATION", 4104, lineNum + 1,
					"Period must be valid. (" + str + ")", msgStep);
		}

		// 4.1.04 Period must be valid
		if (x.period < 1 || x.period > 48) {
			logger.log(Priority.INFO,logPrefix + "Period: " + str + " is not valid.");

			throw new MsslException("DATA_VALIDATION", 4104, lineNum + 1,
					"Period (" + x.period + ") must between 1 to 48.",
					msgStep);
		}

		// 4.1.05 Quantity Type should not be empty
		x.quantityType = String.valueOf(line[0]).trim();

		if (x.quantityType.length() < 1) {
			logger.log(Priority.INFO,logPrefix + "Quantity Type is empty.");

			throw new MsslException("DATA_VALIDATION", 4105, lineNum + 1,
					"Quantity Type should not be empty.", msgStep);
		}

		// 4.1.15 Quantity should not be empty.
		if (String.valueOf(line[3]).trim().length() < 1) {
			logger.log(Priority.INFO,logPrefix + "Quantity is empty.");

			throw new MsslException("DATA_VALIDATION", 4115, lineNum + 1,
					"Quantity should not be empty.", msgStep);
		}

		// 4.1.16 Quantity must be a numeric value
		try {
			x.quantity = Double.parseDouble(String.valueOf(line[3]));
		}
		catch (Exception e) {
			logger.log(Priority.INFO,logPrefix + "Quantity " + line[3] + " is invalid.");

			throw new MsslException("DATA_VALIDATION", 4116, lineNum + 1,
					"Quantity must be a numeric value. (" + String.valueOf(line[3]) + ")",
					msgStep);
		}

		// Settlement Account ID should not be empty except for "IEQ" and "IIQ"
		// 4.1.08, 4.1.10 Settlement Account ID should not be empty
		if (String.valueOf(line[5]).length() < 1) {
			x.externalId = "";
			if (!x.quantityType.equals("IEQ") && !x.quantityType.equals("IIQ") && !x.quantityType.equals("WLQ")) { //DRCAP PHASE2
				logger.log(Priority.INFO,logPrefix + "If Quantity Type is " + x.quantityType +
						", then Account ID should not be empty.");

				throw new MsslException("DATA_VALIDATION", 4110, lineNum + 1,
						"If Quantity Type is " + x.quantityType + ", then Account ID should not be empty.",
						msgStep);
			}
		}
		else {
			x.externalId = String.valueOf(line[5]).trim();

			if (!validator.lastAccount.equals(x.externalId)) {
				// get Account ID
				sac = this.getSACId( validator.standingVersion,  x.externalId);

				// 4.1.13 Settlement Account should be valid.
				if ((sac == null || sac.sacId == null) && x.externalId.length() > 1) {
					logger.log(Priority.INFO,logPrefix + "Settlement Account: " + x.externalId +
							" is not valid.");

					throw new MsslException("DATA_VALIDATION", 4113, lineNum + 1,
							"Settlement Account should be valid. (" + x.externalId + ")",
							msgStep);
				}

				x.sacId = sac.sacId;
			}
			else {
				x.sacId = validator.lastSacId;
			}
		}

		// DRCAP PHASE2 START

		//logger.log(Priority.INFO,"drEffective value" +drEffective);

		if ((x.quantityType.equals("WDQ") || x.quantityType.equals("WLQ")) && validator.drEffective == false) {
			// DRCAP PHASE2 If Quantity Type is WLQ or WDQ, then Settlement Date should be greater or
			// equal to DR Effective Date

			//logger.log(Priority.INFO,"Inside WDQ and WLQ");

			logger.log(Priority.INFO,logPrefix + "Settlement Date: " + sdf1.format(validator.settlementDate) +
					" is earlier than DR Effective Date: " + sdf1.format(validator.drEffectiveDate));

			throw new MsslException("DATA_VALIDATION", 8123, lineNum + 1,
					"If Quantity Type is WDQ or WLQ, then Settlement Date should be greater " +
							"or equal to the DR Effective Date (" +
							sdf1.format(validator.drEffectiveDate) + ").",
					msgStep);
		}
		// DRCAP PHASE2 END

		if (validator.drEffective) {
			// Settlement Date >= DR Effective Date
			// 4.1.7 If Quantity type is "IEQ" or "IIQ", then Node ID should not be empty
			switch (x.quantityType) {
				case "IEQ":
				case "WLQ":   // DRCAP Phase 2
				case "IIQ":
					if (String.valueOf(line[4]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node ID is empty.");

						throw new MsslException("DATA_VALIDATION", 8107, lineNum + 1,
								"If Quantity Type is IEQ or IIQ or WLQ, then Node name should not be empty.", //DRCAP PHASE2
								msgStep);
					}

					if (String.valueOf(line[5]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is not empty.");

						throw new MsslException("DATA_VALIDATION", 8108, lineNum + 1,
								"Settlement Account is not empty (" + String.valueOf(line[5]) + ") for Quantity Type IEQ or IIQ or WLQ",
								msgStep);
					}

					x.nodeName = String.valueOf(line[4]).trim();

					if (validator.lastNodeName == null || !validator.lastNodeName.equals(x.nodeName)) {
						// get Node ID
						x.nodeId = this.getNodeId( validator.standingVersion,  x.nodeName);

						// 4.1.14 Node Id should be valid
						if (x.nodeId == null && x.nodeName.length() > 1) {
							logger.log(Priority.INFO,logPrefix + "Node: " + x.nodeName + " is not valid.");

							throw new MsslException("DATA_VALIDATION", 8114,
									lineNum + 1, "Node name should be valid. (" + x.nodeName + ")",
									msgStep);
						}
					}
					else {
						x.nodeId = validator.lastNodeId;
					}

					x.sacId = null;
					x.externalId = "";
					break;

				case "WEQ":
				case "WFQ":
				case "WMQ":
				case "WPQ":
				case "WDQ":
					//case "WLQ":
					// 4.1.09 && 4.1.10 If Quantity Type is "WEQ" or "WFQ" or "WMQ" or "WPQ" or "WDQ" or "WLQ"
					// the node Id should be empty and Settlment Account ID should not be emtpy
					if (String.valueOf(line[4]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node name is not empty.");

						throw new MsslException("DATA_VALIDATION", 8109, lineNum + 1,
								"If Quantity Type is WEQ or WFQ or WMQ or WPQ or WDQ, then Node name should be empty. (" +
										String.valueOf(line[4]) + ")", msgStep);
					}

					if (String.valueOf(line[5]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is empty.");

						throw new MsslException("DATA_VALIDATION", 8108, lineNum + 1,
								"If Quantity Type is WEQ or WFQ or WMQ or WPQ or WDQ, then Settlement Account should not be empty.",
								msgStep);
					}

					x.nodeName = null;
					x.nodeId = null;
					break;
				default:
					logger.log(Priority.INFO,logPrefix + "Quantity Type: " + x.quantityType + " is not valid.");

					throw new MsslException("DATA_VALIDATION", 8106, lineNum + 1,
							"Quantity Type must be valid. (" + x.quantityType + ")",
							msgStep);
					//break;// - Not reachable
			}
		}
		else if (validator.ntEffective) {
			// Settlement Date >= NT Effective Date
			// 4.1.7 If Quantity type is "IEQ" or "IIQ", then Node ID should not be empty
			switch (x.quantityType) {
				case "IEQ":
				case "IIQ":
					if (String.valueOf(line[4]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node ID is empty.");

						throw new MsslException("DATA_VALIDATION", 4107, lineNum + 1,
								"If Quantity Type is IEQ or IIQ, then Node name should not be empty.",
								msgStep);
					}

					if (String.valueOf(line[5]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is not empty.");

						throw new MsslException("DATA_VALIDATION", 4108, lineNum + 1,
								"Settlement Account is not empty (" + String.valueOf(line[5]) + ") for Quantity Type IEQ or IIQ",
								msgStep);
					}

					x.nodeName = String.valueOf(line[4]).trim();

					if (validator.lastNodeName != x.nodeName) {
						// get Node ID
						x.nodeId = this.getNodeId( validator.standingVersion,  x.nodeName);

						// 4.1.14 Node Id should be valid
						if (x.nodeId == null && x.nodeName.length() > 1) {
							logger.log(Priority.INFO,logPrefix + "Node: " + x.nodeName + " is not valid.");

							throw new MsslException("DATA_VALIDATION", 4114,
									lineNum + 1, "Node name should be valid. (" + x.nodeName + ")",
									msgStep);
						}
					}
					else {
						x.nodeId = validator.lastNodeId;
					}

					x.sacId = null;
					x.externalId = "";
					break;

				case "WEQ":
				case "WFQ":
				case "WMQ":
				case "WPQ":


					// 4.1.09 && 4.1.10 If Quantity Type is "WEQ" or "WFQ" or "WMQ" or "WPQ"
					// the node Id should be empty and Settlment Account ID should not be emtpy
					if (String.valueOf(line[4]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node name is not empty.");

						throw new MsslException("DATA_VALIDATION", 4109, lineNum + 1,
								"If Quantity Type is WEQ or WFQ or WMQ or WPQ , then Node name should be empty. (" +
										String.valueOf(line[4]) + ")", msgStep);
					}

					if (String.valueOf(line[5]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is empty.");

						throw new MsslException("DATA_VALIDATION", 4108, lineNum + 1,
								"If Quantity Type is WEQ or WFQ or WMQ or WPQ or WDQ, then Settlement Account should not be empty.",
								msgStep);
					}

					x.nodeName = null;
					x.nodeId = null;
					break;
				default:
					logger.log(Priority.INFO,logPrefix + "Quantity Type: " + x.quantityType + " is not valid.");

					throw new MsslException("DATA_VALIDATION", 4106, lineNum + 1,
							"Quantity Type must be valid. (" + x.quantityType + ")",
							msgStep);
					//break;// - Not reachable
			}
		}
		else {
			// Settlement Date < NT Effective Start Date
			// 4.1.7 If Quantity type is "IEQ" or "IIQ", then Node ID should not be empty
			switch (x.quantityType) {
				case "IEQ":
				case "IIQ":
					if (String.valueOf(line[4]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node ID is empty.");

						throw new MsslException("DATA_VALIDATION", 4107, lineNum + 1,
								"If Quantity Type is IEQ or IIQ, then Node name should not be empty.",
								msgStep);
					}

					if (String.valueOf(line[5]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is not empty.");

						throw new MsslException("DATA_VALIDATION", 4108, lineNum + 1,
								"Settlement Account is not empty (" + String.valueOf(line[5]) + ") for Quantity Type IEQ or IIQ",
								msgStep);
					}

					x.nodeName = String.valueOf(line[4]).trim();

					if (validator.lastNodeName != x.nodeName) {
						// get Node ID
						x.nodeId = this.getNodeId( validator.standingVersion,  x.nodeName);

						// 4.1.14 Node Id should be valid
						if (x.nodeId == null && x.nodeName.length() > 1) {
							logger.log(Priority.INFO,logPrefix + "Node: " + x.nodeName + " is not valid.");

							throw new MsslException("DATA_VALIDATION", 4114,
									lineNum + 1, "Node name should be valid. (" + x.nodeName + ")",
									msgStep);
						}
					}
					else {
						x.nodeId = validator.lastNodeId;
					}

					x.sacId = null;
					x.externalId = "";
					break;
				case "WEQ":
				case "WCQ":
				case "WPQ":
					// 4.1.09 && 4.1.10 If Quantity Type is "WEQ" or "WCQ" or "WPQ"
					// the node Id should be empty and Settlment Account ID should not be emtpy
					if (String.valueOf(line[4]).length() > 0) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Node name is not empty.");

						throw new MsslException("DATA_VALIDATION", 4109, lineNum + 1,
								"If Quantity Type is WEQ or WCQ or WPQ, then Node name should be empty. (" +
										String.valueOf(line[4]) + ")", msgStep);
					}

					if (String.valueOf(line[5]).length() < 1) {
						logger.log(Priority.INFO,logPrefix + "Quantity Type is " + x.quantityType +
								" but Settlement Account is empty.");

						throw new MsslException("DATA_VALIDATION", 4108, lineNum + 1,
								"If Quantity Type is WEQ or WCQ or WPQ, then Settlement Account should not be empty.",
								msgStep);
					}

					x.nodeName = null;
					x.nodeId = null;
					break;
				default:
					logger.log(Priority.INFO,logPrefix + "Quantity Type: " + x.quantityType + " is not valid.");

					throw new MsslException("DATA_VALIDATION", 4106, lineNum + 1,
							"Quantity Type must be valid. (" + x.quantityType + ")",
							msgStep);
					//break;// - Not reachable
			}
		}



		// logMessage logPrefix + "x.settlementDate.format dd-MMM-yyyy " + x.settlementDate.format("dd-MMM-yyyy")
		String key = settDateStr + x.externalId + x.nodeName + x.quantityType;

		if (validator.total.get(key) == null) {
			validator.total.put(key, 0);
			validator.square.put(key, 0);
		}

		validator.total.put(key, (int)validator.total.get(key) + x.period);
		validator.square.put(key, (int)validator.square.get(key)  + x.period * x.period);

		if (validator.ntEffective) {
			// Settlement Date >= NT Effective Start Date
			// 4.1.17, 4.1.18 no longer valid
			if ((x.quantityType.equals("WPQ")) && (validator.priceNeuDateChecked == false)) {
				// 4.1.23 If Quantity Type is WPQ, then Settlement Date should be greater or equal to
				// the Price Neutralization Date
				// 4.1.25 If Quantity Type is WFQ or WMQ, then Settlement Date should be greater or equal to
				// the NT Effective Date
				if (validator.settlementDate.compareTo(validator.priceNeuDate) < 0) {
					logger.log(Priority.INFO,logPrefix + "Settlement Date: " + sdf1.format(validator.settlementDate) +
							" is earlier than Price Neutralization Date: " + sdf1.format(validator.priceNeuDate));

					throw new MsslException("DATA_VALIDATION", 4123, lineNum + 1,
							"If Quantity Type is WPQ, then Settlement Date should be greater " +
									"or equal to the Price Neutralization Date (" +
									sdf1.format(validator.priceNeuDate) + ").",
							msgStep);
				}

				validator.priceNeuDateChecked = true;
			}
		}
		else {
			// Settlement Date < NT Effective Date
			// 4.1.17, 4.1.18 no longer valid
			if ((x.quantityType.equals("WPQ") || x.quantityType.equals("WCQ")) && validator.priceNeuDateChecked == false) {
				// 4.1.23 If Quantity Type is WPQ, then Settlement Date should be greater or equal to
				// the Price Neutralization Date
				// 4.1.25 If Quantity Type is WCQ, then Settlement Date should be greater or equal to
				// the Price Neutralization Date
				if (validator.settlementDate.compareTo(validator.priceNeuDate) < 0) {
					logger.log(Priority.INFO,logPrefix + "Settlement Date: " + sdf1.format(validator.settlementDate) +
							" is earlier than Price Neutralization Date: " + sdf1.format(validator.priceNeuDate));

					if (x.quantityType.equals("WPQ")) {
						throw new MsslException("DATA_VALIDATION", 4123,
								lineNum + 1, "If Quantity Type is WPQ, then Settlement Date should be greater " +
								"or equal to the Price Neutralization Date (" +
								sdf1.format(validator.priceNeuDate) + ").",
								msgStep);
					}
					else {
						throw new MsslException("DATA_VALIDATION", 4125,
								lineNum + 1, "If Quantity Type is WCQ, then Settlement Date should be greater " +
								"or equal to the Price Neutralization Date (" +
								sdf1.format(validator.priceNeuDate) + ").",
								msgStep);
					}
				}

				validator.priceNeuDateChecked = true;
			}
		}

		// 4.1.26
		if (x.quantityType.equals("WFQ") || x.quantityType.equals("WMQ")) {
			// 4.1.26 If Quantity Type is WFQ or WMQ, then Settlement Date should be greater or
			// equal to NT Effective Start Date
			if (validator.settlementDate.compareTo(validator.ntEffectiveDate) < 0) {
				logger.log(Priority.INFO,logPrefix + "Settlement Date: " + sdf1.format(validator.settlementDate) +
						" is earlier than NT Effective Start Date: " + sdf1.format(validator.ntEffectiveDate));
			}
		}

		if (validator.lastAccount != x.externalId || validator.lastNodeName != x.nodeName) {
			// 4.1.24 if Quantity Type is WPQ, then the value for the EMBEDDED_GEN column in NEM_SETTLEMENT_ACCOUNTS
			// for the particular Settlement Account should be "Y"
			if (x.quantityType.equals("WPQ")) {
				if (sac.embeddedGen!= null && !sac.embeddedGen.equals("Y")) {
					logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is not Y");

					throw new MsslException("DATA_VALIDATION", 4124, lineNum + 1,
							"If Quantity Type is WPQ, then the value for the EMBEDDED_GEN column in NEM_SETTLEMENT_ACCOUNTS for the particular Settlement Account should be Y",
							msgStep);
				}
			}

			if (validator.ntEffective) {
				// Settlement Date >= NT Effective Start Date
				// 4.1.27, 4.1.28, 4.1.29 and 4.1.30 only apply to EG (sacType = "E")
				if (sac.sacType != null && sac.sacType.equals("E")) {
					// 4.1.27 For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table,
					// if the value of the EMBEDDED_GEN column is 'Y' and RETAILER_ID column is 'Y',
					// expect only WPQ of EG.
					if (sac.embeddedGen.equals("Y")) {
						if (sac.retailerId != null && !x.quantityType.equals("WPQ")) {
							logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is Y and RETAILER_ID is Y but Quantity Type is not WPQ.");

							throw new MsslException("DATA_VALIDATION", 4127,
									lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
									" if the value of the EMBEDDED_GEN column is Y and RETAILER_ID column is Y," +
									" expect only WPQ of EG.", msgStep);
						}
						else if (sac.retailerId == null) {
							if (x.quantityType.charAt( 0) != 'W') {
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is Y and RETAILER_ID is N but Quantity Type is not WPQ or WCQ or WEQ.");

								throw new MsslException("DATA_VALIDATION", 4128,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is Y and RETAILER_ID column is N," +
										" expect only WPQ, WFQ, WMQ or WEQ of EG.",
										msgStep);
							}
						}
					}
					else {
						// embeddedGen = "N"
						// 4.1.29
						if (sac.retailerId != null) {
							if (x.quantityType.charAt( 0) == 'W') {
								// WPQ, WEQ, WFQ, WMQ
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is N and RETAILER_ID is Y but Quantity Type is " + x.quantityType);

								throw new MsslException("DATA_VALIDATION", 4129,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is N and RETAILER_ID column is Y," +
										" do NOT expect WPQ, WFQ, WMQ and WEQ of EG.",
										msgStep);
							}
						}
						else {
							// sac.retailerId = null
							if (!x.quantityType.equals("WFQ") && !x.quantityType.equals("WMQ") && !x.quantityType.equals("WEQ")) {
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is N and RETAILER_ID is N but Quantity Type is " + x.quantityType);

								throw new MsslException("DATA_VALIDATION", 4130,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is N and RETAILER_ID column is N," +
										" expect WFQ, WMQ and WEQ of EG.",
										msgStep);
							}
						}
					}
				}
			}
			else {
				// Settlement Date < NT Effective Start Date
				// 4.1.26, 4.1.27, 4.1.28 and 4.1.29 only apply to EG (sacType = "E")
				if (sac.sacType != null && sac.sacType.equals("E")) {
					// 4.1.26 For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table,
					// if the value of the EMBEDDED_GEN column is 'Y' and RETAILER_ID column is 'Y',
					// expect only WPQ of EG.
					if (sac.embeddedGen.equals("Y")) {
						if (sac.retailerId != null && !x.quantityType.equals("WPQ")) {
							logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is Y and RETAILER_ID is Y but Quantity Type is not WPQ.");

							throw new MsslException("DATA_VALIDATION", 4126,
									lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
									" if the value of the EMBEDDED_GEN column is Y and RETAILER_ID column is Y," +
									" expect only WPQ of EG.", msgStep);
						}
						else if (sac.retailerId == null) {
							if (x.quantityType.charAt( 0) != 'W') {
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is Y and RETAILER_ID is N but Quantity Type is not WPQ or WCQ or WEQ.");

								throw new MsslException("DATA_VALIDATION", 4127,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is Y and RETAILER_ID column is N," +
										" expect only WPQ, WCQ or WEQ of EG.",
										msgStep);
							}
						}
					}
					else {
						// embeddedGen = "N"
						// 4.1.28
						if (sac.retailerId != null) {
							if (x.quantityType.charAt( 0) == 'W') {
								// WPQ, WEQ, WCQ
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is N and RETAILER_ID is Y but Quantity Type is " + x.quantityType);

								throw new MsslException("DATA_VALIDATION", 4128,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is N and RETAILER_ID column is Y," +
										" do NOT expect WPQ, WCQ and WEQ of EG.",
										msgStep);
							}
						}
						else {
							// sac.retailerId = null
							if (!x.quantityType.equals("WCQ") && !x.quantityType.equals("WEQ")) {
								logger.log(Priority.INFO,logPrefix + "EMBBEDED_GEN is N and RETAILER_ID is N but Quantity Type is " + x.quantityType);

								throw new MsslException("DATA_VALIDATION", 4129,
										lineNum + 1, "For that particular Settlement Account in NEM_SETTLEMENT_ACCOUNTS table," +
										" if the value of the EMBEDDED_GEN column is N and RETAILER_ID column is N," +
										" expect WCQ and WEQ of EG.", msgStep);
							}
						}
					}
				}
			}

			if (validator.fileType.equals("CMF")) {
				// 03092009 validate by header info++
				if (validator.newCMFFormat) {
					if (x.externalId != null && !x.externalId.trim().equals("")) {
						cmHeaderImpl.validate(cmHeader, sdf.format(x.settlementDate),
								x.externalId.trim(), lineNum + 1,
								"A", msgStep, validator.standingVersion);
					}

					if (x.nodeName != null && !x.nodeName.trim().equals("")) {
						try {
							cmHeaderImpl.validate(cmHeader, sdf.format(x.settlementDate),
									x.nodeName.trim(),  lineNum + 1,
									"N", msgStep,
									validator.standingVersion);
						}
						catch (Exception e) {
							throw new MsslException("DATA_VALIDATION", 4114,
									lineNum, e.getMessage(),
									msgStep);
						}
					}
				}
				else {
					// Old Corrected Metering File format (without header)
					if (x.externalId != null && !x.externalId.trim().equals("")) {
						cmHeaderImpl.addDetail(cmHeader, sdf.format(x.settlementDate),
								x.externalId.trim(), lineNum + 1,
								"A");

						// account
					}

					if (x.nodeName != null && !x.nodeName.trim().equals("")) {
						String sacName = utilityFunctions.getSACByNodeName( x.nodeName, validator.standingVersion);
						cmHeaderImpl.addDetail(cmHeader, sdf.format(x.settlementDate),
								sacName, lineNum + 1,
								"N");

						// node
					}
				}

				// ++03092009 validate by header info
			}
		}

		validator.lastAccount = x.externalId;
		validator.lastSacId = x.sacId;
		validator.lastNodeName = x.nodeName;
		validator.lastNodeId = x.nodeId;
		x.standingVersion = validator.standingVersion;

		return x;
	}

	public void validateTotal(String logPrefix, MSSLFileValidator validator ) throws MsslException
	{
		String msgStep = "MSSLFileValidator.validateTotal()";

		int totalWFQ;
		int totalWMQ;
		String keyStr2;
		String k2Str;
		boolean found;

		logger.log(Priority.INFO,logPrefix + "Starting Function " + msgStep + " ...");

		// 4.1.11 For quantity type WEQ, WCQ or WPQ, each settlement date and settlement account combination,
		// there should be data for each 48 periods.
		String keyStr;
		String qType;
		String saMnn;
		String settDateStr = "";
		int x;
		String msg;
		boolean ieqExist = false;
		boolean iiqExist = false;

		// wcqExist as Bool = false
		boolean weqExist = false;

		// wpqExist as Bool = false
		Set keys = validator.total.keySet();
		for(Object key1 : keys) {
			keyStr = ((String) key1);
			qType = keyStr.substring(keyStr.length() - 3, keyStr.length());
			saMnn = keyStr.substring(11, keyStr.length() - 3);
			settDateStr = keyStr.substring(0, 11);
			x = validator.total.get(keyStr);

			if (x < validator.sumP) {
				// 1 + ... + 48
				msg = qType + ": Data missing for some periods for SA/MNN: " + saMnn + ", Settlement Date: " + settDateStr;

				logger.log(Priority.INFO,logPrefix + msg);

				throw new MsslException("DATA_VALIDATION", 4111, 0,
						msg, msgStep);
			}
			else if (x > validator.sumP) {
				msg = qType + ": Duplidated Data for some periods for SA/MNN: " + saMnn + ", Settlement Date: " + settDateStr;

				logger.log(Priority.INFO,logPrefix + msg);

				throw new MsslException("DATA_VALIDATION", 4111, 0,
						msg, msgStep);
			}

			if (!ieqExist && qType.equals("IEQ")) {
				ieqExist = true;
			}
			else if (!iiqExist && qType.equals("IIQ")) {
				iiqExist = true;

				// 		elseif qType = "WCQ" then
				// 			wcqExist = true
			}
			else if (!weqExist && qType.equals("WEQ")) {
				weqExist = true;

				// 		elseif qType = "WPQ" then
				// 			wpqExist = true
			}

			if (qType.equals("WFQ")) {
				totalWFQ = validator.total.get(keyStr);
				keyStr2 = keyStr.replace( "WFQ", "WMQ");
				Set<String> keys2 = validator.total.keySet();
				found = false;

				for (Object k2 : keys2) {
					k2Str = ((String) k2);

					if (k2Str.equalsIgnoreCase(keyStr2)) {
						found = true;

						break;
					}
				}

				if (found == false || totalWFQ != validator.total.get(keyStr2)) {
					msg = "All WFQ quantities are available, but expected all WMQ quantities are not available for Settlement Account: " + saMnn +
							" and Settlement Date: " + settDateStr;

					// expected wfq wmq
					logger.log(Priority.INFO,logPrefix + msg);

					throw new MsslException("DATA_VALIDATION", 4130, 0,
							msg, msgStep);
				}
			}
			else if (qType.equals("WMQ")) {
				totalWMQ = validator.total.get(keyStr);
				keyStr2 = keyStr.replace("WMQ","WFQ");
				Set<String> keys2 = validator.total.keySet();
				found = false;

				for (Object k2 : keys2) {
					k2Str = ((String) k2);

					if (k2Str.equalsIgnoreCase(keyStr2)) {
						found = true;

						break;
					}
				}

				if (found == false || totalWMQ != validator.total.get(keyStr2)) {
					msg = "All WMQ quantities are available, but expected all WFQ quantities are not available for Settlement Account: " + saMnn +
							" and Settlement Date: " + settDateStr;

					logger.log(Priority.INFO,logPrefix + msg);

					throw new MsslException("DATA_VALIDATION", 4130, 0,
							msg, msgStep);
				}
			}
		}

		// IEQ and IIQ must exists
		if (ieqExist == false) {
			msg = "IEQ data not found for Settlement Date: " + settDateStr;

			logger.log(Priority.INFO,logPrefix + msg);

			throw new MsslException("DATA_VALIDATION", 4112, 0,
					msg, msgStep);
		}

		if (iiqExist == false) {
			msg = "IIQ data not found for Settlement Date: " + settDateStr;

			logger.log(Priority.INFO,logPrefix + msg);

			throw new MsslException("DATA_VALIDATION", 4112, 0,
					msg, msgStep);
		}

		// WCQ, WEQ, WPQ must exists
		// 	if wcqExist = false then
		// 		msg = "WCQ data not found for Settlement Date: " + settDateStr
		// 		logMessage logPrefix + msg using severity = WARNING
		// 		throw new MsslException("DATA_VALIDATION", 4111, 0, msg, msgStep)
		// 	end
		if (weqExist == false) {
			msg = "WEQ data not found for Settlement Date: " + settDateStr;

			logger.log(Priority.INFO,logPrefix + msg);

			throw new MsslException("DATA_VALIDATION", 4111, 0,
					msg, msgStep);
		}

		// 	if wpqExist = false then
		// 		msg = "WPQ data not found for Settlement Date: " + settDateStr
		// 		logMessage logPrefix + msg using severity = WARNING
		// 		throw new MsslException("DATA_VALIDATION", 4111, 0, msg, msgStep)
		// 	end
		keys = validator.square.keySet();

		for (Object key1 : keys) {
			keyStr = ((String) key1);
			qType = keyStr.substring(keyStr.length() - 3, keyStr.length());
			saMnn = keyStr.substring(11, keyStr.length() - 3);
			settDateStr = keyStr.substring(0, 11);
			x = validator.square.get(keyStr);

			if (x < validator.sumP2) {
				// 1^2 + 2^2 + ... 48^2
				msg = qType + ": Data missing for some period for SA/MNN: " + saMnn + ", Settlement Date: " + settDateStr;

				logger.log(Priority.INFO,logPrefix + msg);

				throw new MsslException("DATA_VALIDATION", 4111, 0,
						msg, msgStep);
			}
			else if (x > validator.sumP2) {
				msg = qType + ": Duplidated Data for some period for SA/MNN: " + saMnn + ", Settlement Date: " + settDateStr;

				logger.log(Priority.INFO,logPrefix + msg);

				throw new MsslException("DATA_VALIDATION", 4111, 0,
						msg, msgStep);
			}
		}
	}

}
