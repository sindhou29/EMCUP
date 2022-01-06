package com.emc.settlement.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.pojo.DataValidator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataValidatorImpl {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	PavPackageImpl pavPackage;
	
	protected static final Logger logger = Logger.getLogger(DataValidatorImpl.class);
		
	public boolean exceedNumericRange(Double item, Double minValue, Double maxValue)
	{
		boolean isValid;
		double itemValue = item;
		isValid = (itemValue < minValue) || (itemValue > maxValue);

		return isValid;
	}
	
	public DataValidator getMatchingParticipantDtl(String inputPTPID, String displayTitle)
	{
		logger.log(Priority.INFO, "input parameters getMatchingParticipantDtl - inputPTPID :" + inputPTPID
				+ " displayTitle :" + displayTitle);
		DataValidator retDataValidator = new DataValidator();
		/*
		 * The procedure will return valid input selling participant name, matching
		 * participant name and selling participant ID based on the settlement Account
		 * ID, Settlement Account Version and input parameter Participant ID sent into
		 * the procedure.
		 *
		 * @param sacID
		 *
		 * @param ptpID
		 *
		 * @param stdVersion
		 *
		 * @return Boolean
		 */
		// this.sellingPTPID = null;
		String sellingPTPName = null;
		// this.inputSellingPTPName = null;
		// boolean isValid = false;

		// String sqlCommand1 = "SELECT name FROM nem.nem_participants" +
		// " WHERE id ='" + inputPTPID + "'" +
		// " AND version='" + this.sacVersion + "'";
		// Fuego.Sql.DynamicSQL myDynamic2;
		// myDynamic2 = DynamicSQL();
		// foreach (row in myDynamic2.executeQuery(sentence : sqlCommand1, implname :
		// CommonValue.nemDB)) {
		// this.inputSellingPTPName = String.valueOf(o : row[1]);
		// }

		String sqlCommand3 = "SELECT ptp.name NAME FROM nem.nem_participants ptp, nem.nem_settlement_accounts sac "
				+ " WHERE sac.external_id = '" + displayTitle + "'" +
				// " AND sac.version = '" + inputSACVersion + "'" +
				" AND sac.ptp_id = ptp.id" + " AND sac.ptp_version = ptp.version" + " AND ptp.ID='" + inputPTPID
				+ "'" + " AND rownum=1";
		logger.log(Priority.INFO, sqlCommand3);

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand3);
		for (Map recordMap : resultList) {
			sellingPTPName = (String) recordMap.get("NAME");
		}

		logger.log(Priority.INFO, "sellingPTPName :" + sellingPTPName);
		if (sellingPTPName == null) {
			retDataValidator.setSellingPTPName("");
			retDataValidator.isValid = false;
			return retDataValidator;
		} else {
			retDataValidator.setSellingPTPName(sellingPTPName);
			retDataValidator.isValid = true;
			return retDataValidator;
		}


	}
	
	
	public String getSacIdByDisplayTitle(String displayTitle, String standingVersion)
	{
		
		String strSacId = null;
		//Achilles - Added to get SacID based on display_title and  version
		String sqlGetMsslSacId = "SELECT id, external_id FROM  nem_settlement_accounts" + 
		    " WHERE display_title = ? and version = ? ";


			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlGetMsslSacId, displayTitle, standingVersion);
			for (Map recordMap: resultList) {
				strSacId = (String) recordMap.get("ID");
			}

		return strSacId;		
	}
	
	public String getSacIdByExternalId(String externalId, String standingVersion)
	{
		String sqlGetSacId = "SELECT Id FROM nem_settlement_accounts " + 
			    "WHERE External_Id = ? AND VERSION = ?";
			
			String strSacId = null;

				Object[] params = new Object[2];
				params[0] = externalId;
				params[1] = standingVersion;
				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlGetSacId, params);
				for (Map recordMap : resultList) {
					strSacId = (String) recordMap.get("ID");
					break;
				}
			return strSacId;
	}
	
	
	public DataValidator getValidSacDetails(String startDate, String endDate, String displayTitle)
	{
		
		DataValidator retDataValidator = new DataValidator();
		/*
        The procedure will return a valid settlement account id and version
        based on the display title, start and end dates sent into the procedure.
        
			@param displayTitle
			@param startDate to be in Oracle date format 'DD-MON-RRRR'
			@param endDate  to be in Oracle date format 'DD-MON-RRRR'
			@param eveId
			
			@return Boolean*/
		String sacId = null;
		String sacVersion = null;
		boolean isValid = false;
		String startVersion = pavPackage.getCurrVersionPkt( "STANDING", startDate, null);
		String endVersion = pavPackage.getCurrVersionPkt( "STANDING", endDate, null);
		
		if (endVersion == null || startVersion == null) {
			retDataValidator.isValid = false;
			return retDataValidator;
		}
		
		sacVersion = endVersion;
		String sqlCommand = "SELECT id FROM nem_settlement_accounts" + 
		" WHERE external_id = '" + displayTitle + "'" + 
		" AND TO_NUMBER(version) BETWEEN " + startVersion + " AND " + endVersion + 
		" GROUP BY id";
		sacId = null;

		logger.log(Priority.INFO, sqlCommand);
		 
		/*pstmt = conn.prepareStatement(sqlCommand);
		rs = pstmt.executeQuery();
		while(rs.next()) {
			sacId = rs.getString(1);

		}*/

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
		for(Map recordMap : resultList) {
			sacId = (String) recordMap.get("ID");
			break;
		}

		logger.log(Priority.INFO, "sacId : "+sacId+" startVersion :"+startVersion +" sacVersion : "+sacVersion);
		retDataValidator.setSacId(sacId);
		retDataValidator.setSacVersion(sacVersion);
		retDataValidator.isValid = sacId != null;
		logger.log(Priority.INFO, "retDataValidator.isValid :"+retDataValidator.isValid);
		/*} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null) UtilityFunctions.close(rs);
			if(pstmt != null) UtilityFunctions.close(pstmt);
		}*/
		return retDataValidator;
		
	}
	
	public boolean isLngVesting(String value)
	{
		// sItem as String = value.capitalize(string : String(value))
		// The contract type indicator must be in upper case
		// because the database will accept upper case only.
		String sItem = value;
		boolean isCharacter = sItem.matches( "L");
		return isCharacter;		
	}
	
	public boolean isNumeric(String value)
	{
		/*
		Verify if @param value is numeric using regular expression:  */
		String sItem = value;
	
		// validate that @param item is in 'dd-MMM-yyyy' format
		// NOTE: function only validate for correct format, does not verify that date is a valid date
	//	 	Numeric value check: optional +- prefix, followed by 1 or more numeric digits, 
	//	 					optional decimal pt with max 2 decimal places
	//	 	Valid inputs are:  123, -12345, +123, 12.3, -0.34, +123.45
		// isValid as Bool = sItem.match('/^[-+]?\d+(\.\d{1,2})?$/') is not null
		// this is a more generic check for EMC numeric values
		// valid inputs are: -.23 , -0.23 , +123.33333 , -12233 , 123434.01 etc
		//boolean isValid = sItem.matches("/^[-+]?\\d*(\\.\\d+)?$/") ;
		boolean isValid = sItem.matches("^[-+]?\\d+(\\.\\d+)?$") ;
		return isValid;		
	}
	
	public boolean isPositiveValue(Double item)
	{
		boolean isValid;
		isValid = item >= 0;

		// [ITSM-12670]
		return isValid;		
	}
	
	public boolean isTenderedVesting(String value)
	{
		// sItem as String = value.capitalize(string : String(value))
		// The contract type indicator must be in upper case
		// because the database will accept upper case only.
		String sItem = value;
		boolean isCharacter = sItem.matches("T");
		return isCharacter;		
	}
	
	public boolean isValidBusinessDate(String item)
	{
		boolean isValid = false;
		String BUSINESS_DAY_VALUE = "B";
		String settlement_DATE_FORMAT="DD-MON-YYYY";

		// build SQL to retrieve SETTLEMENT_DATE from NEM_SETTLEMENT_RUNS table
		String sqlSettRunCommand = "SELECT DAY_TYPE FROM NEM_SETTLEMENT_CALENDAR ";
		sqlSettRunCommand = sqlSettRunCommand + "WHERE SETTLEMENT_DATE = TO_DATE('" + item + "','" + 
		                    settlement_DATE_FORMAT + "') ";
		int rowcount = 0;
		String dayType = "";

		

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlSettRunCommand);
		for (Map recordMap : resultList) {
			dayType = (String) recordMap.get("DAY_TYPE");
			isValid = dayType.equalsIgnoreCase(BUSINESS_DAY_VALUE);
			break;
		}

		/*} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null) UtilityFunctions.close(rs);
			if(pstmt != null) UtilityFunctions.close(pstmt);
		}*/

		return isValid;		
	}
	
	public boolean isValidDate(String item, String javaDateFormat)
	{
		/* 
		Validate if particular date is a valid date string
		@param item = the date string
		@param javaDateFormat = the format string for @param item

		@return True if date is valid, else False
		
		Logic:  Converts @param item into a date variable, then output the date variable as a 
				string date using the same format as specified in @param javaDateFormat; date is
				valid if output string date matches @param item*/
		boolean isValid;
		Date tm = null;
		{
		    DateFormat df = new SimpleDateFormat(javaDateFormat);
		    try {
				tm = df.parse(item);
			} catch (ParseException e) {
				logger.error("ParseException "+e.getMessage());
			}
	
		    // tm is null when date is invalid 
		    if (tm != null) {
		        // parser always generates some date value even when input contains invalid values
		        // when the generated date (tm) matches original input string (item), only then is the date valid  
		        isValid = df.format(tm).toUpperCase().equals(item.toUpperCase());
		    }
		    else {
		        isValid = false;
		    }
	
		    return isValid;
		}
		
	}
	
	public boolean isValidFSCReference(char value, String regExpValue)
	{
		// The contract type indicator must be in upper case
		// because the database will accept upper case only.
		String sItem = String.valueOf(value);
		boolean isCharacter = sItem.matches( regExpValue);

		return isCharacter;		
	}
	
	public boolean isValidSacExternalId(String item, String settDate, String eveId)
	{
		// @param externalId
		// @param settDate to be in Oracle date format 'DD-MON-RRRR'
		// @param eveId
		// sacId is this object's attribute to store settlement account Id, default it to null
		PavPackageImpl pavPackage = new PavPackageImpl();
		String sacId = null;
		String sacVersion = null;
		boolean isValid = false;

		String version = pavPackage.getCurrVersionPkt( "STANDING",  settDate, null);

		if (version == null) {
		    return false;
		}

		sacVersion = version;

		// retrieve ID using latest version number
		// 26082009 check id
		String sqlCommand = "SELECT Id FROM nem_settlement_accounts " + 
		"WHERE External_Id = '" + item + "' " + 
		"AND VERSION = '" + version + "'";

		// resultset should have only just 1 row or zero rows
		sacId = null;


		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
		for (Map recordMap : resultList) {
			sacId  = (String) recordMap.get("ID");
			break;
		}

		/*} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(rs != null) UtilityFunctions.close(rs);
			if(pstmt != null) UtilityFunctions.close(pstmt);
		}*/
		
		isValid = sacId != null;

		return isValid;
		
	}
	
	public boolean isValidSettDateFormat(String item)
	{
		String[] splitStr = item.trim().split("-");
		if (item.trim().matches("^\\d{1,2}-[a-zA-Z]{3}-\\d{2}$") || item.trim().matches("^\\d{1,2}-[a-zA-Z]{3}-\\d{4}$")) {
			String month = splitStr[1].trim().toUpperCase();
			if (Integer.parseInt(splitStr[0].trim()) < 1 || Integer.parseInt(splitStr[0].trim()) > 31)
				return false;
			if (month.contains("JAN") || month.contains("FEB") || month.contains("MAR") || month.contains("APR")
					|| month.contains("MAY") || month.contains("JUN") || month.contains("JUL") || month.contains("AUG")
					|| month.contains("SEP") || month.contains("OCT") || month.contains("NOV")
					|| month.contains("DEC")) {
				if ((month.contains("APR") || month.contains("JUN") || month.contains("SEP") || month.contains("NOV"))
						&& Integer.parseInt(splitStr[0]) == 31)
					return false;
				else if (month.contains("FEB")) {
					boolean isleap = (Integer.parseInt(splitStr[2].trim()) % 4 == 0
							&& (Integer.parseInt(splitStr[2].trim()) % 100 != 0 || Integer.parseInt(splitStr[2].trim()) % 400 == 0));
					if (Integer.parseInt(splitStr[0].trim()) > 29 || (Integer.parseInt(splitStr[0].trim()) == 29 && !isleap))
						return false;
				}

			} else
				return false;
		} else
			return false;
			
		return true;
	
	}
	
	public String isValidSettlementAccount(String item, String settDate)
	{
		PavPackageImpl pavPackage = new PavPackageImpl();
		String version = pavPackage.getCurrVersionPkt( "STANDING",  settDate, null);

		//retrieve ID using latest version number
		String sqlCommand = "SELECT id, external_id FROM  nem_settlement_accounts" + 
		" WHERE version = '" + version + "' AND display_title = '" + item + "'";
		
		//resultset should have only just 1 row or zero rows
		String id = null;	
		/*pstmt = conn.prepareStatement(sqlCommand);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			id  = rs.getString(1);
		}*/

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand);
		for (Map recordMap : resultList) {
			id  = (String) recordMap.get("ID");
			break;
		}

		if (id == null) {
		id = "";
		}
		
		return id;		
	}
	
	public int numberOfDecimalDigits(String value)
	{
		/*
		returns number of decimal digits found after the decimal point*/
		String sItem = ((String) value);
		int numDecimals;
		int pos = sItem.indexOf(".");
	
		if (pos < 0) {
		    numDecimals = 0;
		}
		else {
		    numDecimals = sItem.length() - pos - 1;
		}
	
		return numDecimals;		
	}

}
