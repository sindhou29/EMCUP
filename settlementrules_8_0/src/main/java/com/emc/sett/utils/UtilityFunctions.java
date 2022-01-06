/*
 * Copyright (c) 2018 Energy Market Company Pte. Ltd.
 * 
 * All rights reserved
 * UtilityFunctions.java
 * Version:
 *   0.1 2018/03/01
 * 
 * Revisions:
 *   Converted from Branch 4 BPM codes
 */
package com.emc.sett.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 
 * @author Tse Hing Chuen
 */
public class UtilityFunctions {
	
    private static final Logger logger = LoggerFactory.getLogger(UtilityFunctions.class);
	
	public static void logJAMMessage(DataSource ds, String eventId, String severity, String execStep, String text, String errorCode) {

		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ds.getConnection();

			logger.info("[JAM] Exec Step: " + execStep + ", Message Type: " + severity + 
			    ", Text: " + text + 
			    ", Event Id: " + eventId);

		    String sqlCommand = "INSERT INTO NEM.JAM_MESSAGES ( ID, ERROR_CODE, SEQ, SEVERITY, " + 
			    "TEXT, MESSAGE_DATE, EXECUTION_STEP, EVE_ID ) " + 
			    "VALUES ( SYS_GUID(),?,get_mstimestamp,?,?,SYSDATE,?,? )";
		    stmt = conn.prepareCall(sqlCommand);
		    stmt.setString(1, errorCode);
		    stmt.setString(2, severity);
		    stmt.setString(3, text);
		    stmt.setString(4, execStep);
		    stmt.setString(5, eventId);
		    stmt.execute();
		} catch (Exception e) {
			logger.error("[JAM] Log JAM Messages failed. Message Type: " + severity + 
		    ", Time: " + (new Timestamp(System.currentTimeMillis())).toString() + ", Details: ", e);	// + e.toString());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static double getSysParamNum(DataSource ds, String paramName) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    double num = 0;
		    int rowcnt = 0;
		    logger.info("[EMC] Starting Function getSysParamNum() for: " + paramName);
	
			conn = ds.getConnection();
			
		    String sqlCommand = "SELECT number_value FROM NEM.aps_system_parameters WHERE upper(name) = upper(?) ";
		    stmt = conn.prepareCall(sqlCommand);
		    stmt.setString(1, paramName);
		    stmt.execute();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        rowcnt = rowcnt + 1;
		        num = rs.getDouble(1);
	
		        break;
		    }
	
		    if (rowcnt == 0) {
		        throw new Exception("Can not find the " + paramName + " in the system (APS_SYSTEM_PARAMETERS)");
		    }
	
		    logger.info("[EMC] Value of " + paramName + ": " + num);
	
		    return num;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Date getSysParamTime(DataSource ds, String paramName) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			Date tm = null;
			//logger.info("[EMC] Starting UtilityFunctions.getSysParamTime() for: " + paramName);
			
			conn = ds.getConnection();
			
		    String sqlCommand = "SELECT date_value FROM NEM.aps_system_parameters" + 
		    	    " WHERE upper(name) = upper(?) ";
		    stmt = conn.prepareCall(sqlCommand);
		    stmt.setString(1, paramName);
		    stmt.execute();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        tm = rs.getDate(1);
	
		        break;
		    }

		    if (tm == null) {
		        throw new Exception("Can not find the " + paramName + " in the system (APS_SYSTEM_PARAMETERS)");
		    }

		    //logger.info("[EMC] Value of " + paramName + ": " + tm.toString());
		    
		    return tm;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getSysParamVarChar(DataSource ds, String paramName) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String retStr = null;
			
			conn = ds.getConnection();
			
		    String sqlCommand = "SELECT character_value FROM NEM.aps_system_parameters" + 
		    	    " WHERE UPPER(NAME) = UPPER(?) ";
		    stmt = conn.prepareCall(sqlCommand);
		    stmt.setString(1, paramName);
		    stmt.execute();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				retStr = rs.getString(1);
	
		        break;
		    }

		    if (retStr == null) {
		        throw new Exception("Can not find " + paramName + " in aps_system_parameters table.");
		    }
		    
		    return retStr;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getUserId(DataSource ds, String name) throws Exception {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    String id = null;
		    logger.info("[EMC] Starting Method EMC.UtilityFunctions.getUserId() ...");
	
			conn = ds.getConnection();
			
		    String sqlCommand = "SELECT Id FROM SEFO.SEF_USERS WHERE UPPER(user_name) = UPPER(?)";
		    stmt = conn.prepareCall(sqlCommand);
		    stmt.setString(1, name);
		    stmt.execute();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        id = rs.getString(1);
	
		        logger.info("[EMC] User ID: " + id + " for User: " + name);
		    }
	
		    if (id == null) {
		        throw new Exception("ID not found in SEF_USERS for: " + name);
		    }
	
		    return id;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isAfterWMEPunroundedEMCfeesEffectiveDate(DataSource ds, Date settDate) {
		
		try {
			Date WMEPunroundedEMCFEEsEffDate = UtilityFunctions.getSysParamTime(ds, "WMEP_UNROUNDED_ADMFEE_EFFDATE");
	
			if (settDate.compareTo(WMEPunroundedEMCFEEsEffDate) >= 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// Get Parameter: NETAFP_EFF_START_DATE
	// RM00000510-Net AFP for Residential Consumers Embed with IGS
	public static boolean isAfterNetAFPRuleChgEffDate(DataSource ds, Date settDate) {
		
		try {
			Date netAFPRuleEffectiveStartDate = UtilityFunctions.getSysParamTime(ds, "MOD_GEN_FEQ_EFFECT_DATE");
	
			if (settDate.compareTo(netAFPRuleEffectiveStartDate) >= 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static String trimZeroDecimal(BigDecimal val) {
		
		String ss = "";
		
		if (val != null) {
			if (val.signum() == 0) {
				ss = "0";
			} else {
				if (val.abs().intValue() == 0) {
					ss = UtilityFunctions.trimZeroDecimal(val.stripTrailingZeros().toPlainString());
				} else {
					ss = val.stripTrailingZeros().toPlainString();
				}
			}
		}
		
		return ss;
	}

	//This trimZeroDecimal(String) SHALL NOT BE CALLED DIRECTLY
	//=========================================================
	//it MUST be called from trimZeroDecimal(BigDecimal)
	//=========================================================
	public static String trimZeroDecimal(String sinArg) {
		
		String ss = sinArg.replace("0.", ".");
		//Please note that the above line of code will replace "0." with "."
		//Example: "0.123" ==> ".123"     "170.99" ==> "17.99" 
		//BE MINDFUL TO NOT CALL THIS trimZeroDecimal(String) METHOD DIRECTLY
		
		if (ss.equals(sinArg)) {
			ss = sinArg.replace("-0.", "-.");
		}
		if (ss.equals(sinArg)) {
			ss = sinArg.replace(",0.", ",.");
		}
		if (ss.equals(sinArg)) {
			ss = sinArg.replace(",-0.", ",-.");
		}
		return ss;
	}

	public static String trimFormatedDecimal(BigDecimal val) {
		
		// -------------------------------------------
		// 1) replace "0.x" to ".x" in number string
		// 2) if number is negative, use add ()
		// -------------------------------------------
		if (val != null) {
			String s = null;
			if (val.abs().intValue() == 0) {
				s = val.abs().stripTrailingZeros().toPlainString().replace("0.", ".");
			} else {
				s = val.abs().stripTrailingZeros().toPlainString();
			}
		    String ss = val.signum() == -1 ? "(" + s + ")" : s;

		    return ss;
		} else {
		    return "";
		}
	}

	public static String formatDecimal(BigDecimal val) {
		
		// -------------------------------------------
		// 1) replace "0.x" to ".x" in number string
		// 2) if number is negative, use add ()
		// -------------------------------------------
		if (val != null) {
			String s = null;
			if (val.signum() == 0) {
				s = "0";
			} else {
				if (val.abs().intValue() == 0) {
					s = val.abs().toPlainString().replace("0.", ".");
				} else {
					s = val.abs().toPlainString();
				}
			}
		    String ss = val.signum() == -1 ? "(" + s + ")" : s;

		    return ss;
		} else {
		    return "";
		}
	}

	// start of UAT2SHARP-251
	public static boolean isFSCEffDate(DataSource ds, Date settDate) {
		
		try {
			if(UtilityFunctions.isAfterFSCEffectiveStartDate(ds, settDate) && 
					UtilityFunctions.isBeforeFSCEffectiveEndDate(ds, settDate)){
					return true;
				}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static boolean isAfterFSCEffectiveStartDate(DataSource ds, Date settDate) {
		
		try {
			Date fscEffectiveStartDate = UtilityFunctions.getSysParamTime(ds, "FSC_EFF_START_DATE");

			if (settDate.compareTo(fscEffectiveStartDate) >= 0) {
			    return true;
			}
			else {
			    return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isBeforeFSCEffectiveEndDate(DataSource ds, Date settDate) {
		
		try {
			Date fscEffectiveEndDate = UtilityFunctions.getSysParamTime(ds, "FSC_EFF_END_DATE");

			if (settDate.compareTo(fscEffectiveEndDate) <= 0) {
			    return true;
			}
			else {
			    return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	// end of UAT2SHARP-251
}