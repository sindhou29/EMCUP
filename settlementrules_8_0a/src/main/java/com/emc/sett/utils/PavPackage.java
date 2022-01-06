package com.emc.sett.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PavPackage {
	
    private static final Logger logger = LoggerFactory.getLogger(PavPackage.class);
	
	public static String getStandingVersion(DataSource ds, Date settlementDate) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    String standingVer = null;
		    //logger.info("[EMC] Starting Method: EMC.PavPackage.getStandingVersion() ...");
			
			conn = ds.getConnection();
			
		    String sqlCommand = "select to_char(max(version)) " + 
		    	    "from NEM.NEM_STANDING_VERSIONS_MV " + 
		    	    "where trunc(?) between effective_date and end_date";
		    stmt = conn.prepareCall(sqlCommand);
			stmt.setDate(1, settlementDate);
		    stmt.execute();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				
		        if (rs.getString(1) != null) {
					standingVer = rs.getString(1);
					//logger.info("[EMC] Standing version is: " + standingVer + " for SD: " + settlementDate.toString());
		        }
		    }

		    if (standingVer == null) {
		        throw new Exception("Error getting standing current version !!!");
		    }
		    
		    return standingVer;
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
}