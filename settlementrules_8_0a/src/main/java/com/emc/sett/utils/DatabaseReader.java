/*
 * Copyright (c) 2018 Energy Market Company Pte. Ltd.
 * 
 * All rights reserved
 * DatabaseReader.java
 * Version:
 *   0.1 2018/03/01
 * 
 * Revisions:
 *   Converted from Branch 5 BPM codes
 */
package com.emc.sett.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.sett.common.BRQType;
import com.emc.sett.common.BilateralContract;
import com.emc.sett.common.DRCompliantType;
import com.emc.sett.common.ErrorAdjustment;
import com.emc.sett.common.ForwardSalesContract;
import com.emc.sett.common.MarketParticipantType;
import com.emc.sett.common.SettlementRunException;
import com.emc.sett.common.VestingContract;
import com.emc.sett.impl.Account;
import com.emc.sett.impl.Adjustment;
import com.emc.sett.impl.Bilateral;
import com.emc.sett.impl.Brq;
import com.emc.sett.impl.Cnmea;
import com.emc.sett.impl.Facility;
import com.emc.sett.impl.Fsc;
import com.emc.sett.impl.Ftr;
import com.emc.sett.impl.Market;
import com.emc.sett.impl.Mnmea;
import com.emc.sett.impl.MnmeaSub;
import com.emc.sett.impl.CnmeaSettRe;
import com.emc.sett.impl.Participant;
import com.emc.sett.impl.Period;
import com.emc.sett.impl.Rerun;
import com.emc.sett.impl.Reserve;
import com.emc.sett.impl.RsvClass;
import com.emc.sett.impl.SettlementData;
import com.emc.sett.impl.Tvc;
import com.emc.sett.impl.Vesting;

import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

/*
 * 
 * @author Tse Hing Chuen
 */
public class DatabaseReader {
	
    private static final Logger logger = LoggerFactory.getLogger(DatabaseReader.class);
    
    //private static SimpleDateFormat qdf = new SimpleDateFormat("dd-MMM-yyyy");	// SHARPPR-2 and 4, replace with Java 8 API
    private static int totalPeriod = 48;
    
    private static final String DATETIME_FORMAT_STR = "dd-MMM-yyyy";

	public static void readGlobal(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readGlobal()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		
		try {
			conn = ds.getConnection();
	
		    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");
		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                msgStep, "Generating Entity CSV File: Global", "");
		    
	        // ***************************************
	        // Get Total Period
		    totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");
	
		    // ************** parameters *************
		    data.getGlobal().setRunType(params.runType);
	    	data.getGlobal().setStandingVersion(runPackage.standingVersion);
	    	
		    GregorianCalendar calender = new GregorianCalendar();
		    calender.setTime(params.sqlSettlementDate);
	    	data.getGlobal().setTradingDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));
	    	
	    	data.getGlobal().setRunId(params.runId);
	
	        // get the cutoff size
	    	data.getGlobal().setCsz(BigDecimal.valueOf(UtilityFunctions.getSysParamNum(ds, "CMWH")));
	
	        // get number of days in that month	
	        int daysOfMonth = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
	        data.getGlobal().setDim(BigDecimal.valueOf(daysOfMonth));
	
	        // get the emcBgt, emcPriceCap and psoBgt
	        calender.set(Calendar.DAY_OF_MONTH, 1);
	        Date firstDay = new Date(calender.getTime().getTime());
			ZonedDateTime fd = ZonedDateTime.of(firstDay.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
	        
	        // Fixes for null values
	        data.getGlobal().setTotalTte(BigDecimal.ZERO);
	        data.getGlobal().setTotalEmcAdm(BigDecimal.ZERO);
	        data.getGlobal().setTotalPsoAdm(BigDecimal.ZERO);
	        data.getGlobal().setTotalTteSet(false);
	        data.getGlobal().setTotalEmcAdmSet(false);
	        data.getGlobal().setTotalPsoAdmSet(false);
	        
	        String sqlCommand = "SELECT fee_code, amount FROM NEM.NEM_SETTLEMENT_FEES_BUDGET WHERE month=?";
			stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(firstDay));
			stmt.setString(1, sqlFormatter.format(fd));
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
	            String feeCode = rs.getString("fee_code");
	
	            // logMessage "feeCode value is : " + feeCode using severity = WARNING
	            if (feeCode.equals("EMCADJST")) {
	            	data.getGlobal().setEmcBudget(new BigDecimal(rs.getString("amount")));
	
	                // logMessage "emcBgt value is : " + emcBgt using severity = WARNING
	            }
	
	            // new added for [ITSM15890]
	            if (feeCode.equals("EMCPRCAP")) {
	            	data.getGlobal().setEmcAdmPriceCap(new BigDecimal(rs.getString("amount")));
	
	                // logMessage "emcPriceCap value is : " + emcPriceCap using severity = WARNING
	            }
	
	            // end for [ITSM15890]
	            if (feeCode.equals("PSOADMIN")) {
	            	data.getGlobal().setPsoBudget(new BigDecimal(rs.getString("amount")));
	
	                // logMessage "psoBgt value is : " + psoBgt using severity = WARNING
	            }
	        }
			rs.close();
			stmt.close();
			
			data.getGlobal().setPriceCapEffective(true);
	
	        // get the MEUC, add fix due to SATSHARP-286
	        sqlCommand = "select version, value from NEM.NEM_MEUC where settlement_month=? and expired_date > sysdate and approval_status='A'";
	        //sqlCommand = "select version, value from NEM.NEM_MEUC where settlement_month=? and approval_status='A'";
			stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(firstDay));
			stmt.setString(1, sqlFormatter.format(fd));
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
            	data.getGlobal().setMeuc(new BigDecimal(rs.getString("value")));
	
	            // logMessage "meuc value is : " + meuc using severity = WARNING
	        }
			rs.close();
			stmt.close();
	
	        // get all the GST values
	        sqlCommand = "select name, value, id from NEM.NEM_GST_CODES where version=?";
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, runPackage.standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
	            String name = rs.getString("name");
	
	            if (name.equals("VN")) {
	            	data.getGlobal().setNonInputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setNonInputGstCode(rs.getString("id"));
	
	                // logMessage " nonInputGST value is : " + nonInputGST using severity = WARNING
	            }
	
	            if (name.equals("AN")) {
	            	data.getGlobal().setNonOutputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setNonOutputGstCode(rs.getString("id"));
	
	                // logMessage " nonOutputGST value is : " + nonOutputGST using severity = WARNING
	            }
	
	            if (name.equals("V0")) {
	            	data.getGlobal().setZeroInputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setZeroInputGstCode(rs.getString("id"));
	
	                // logMessage "[EMC] zeroInputGST value is : " + zeroInputGST
	            }
	
	            if (name.equals("A0")) {
	            	data.getGlobal().setZeroOutputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setZeroOutputGstCode(rs.getString("id"));
	
	                // logMessage " zeroOutputGST value is : " + zeroOutputGST using severity = WARNING
	            }
	
	            if (name.equals("VC")) {
	            	data.getGlobal().setInputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setInputGstCode(rs.getString("id"));
	
	                // logMessage " inputGST value is : " + inputGST using severity = WARNING
	            }
	
	            if (name.equals("AC")) {
	            	data.getGlobal().setOutputGstRate(new BigDecimal(rs.getString("value")));
	            	data.getGlobal().setOutputGstCode(rs.getString("id"));
	
	                // logMessage " outputGST value is : " + outputGST using severity = WARNING
	            }
	        }
			rs.close();
			stmt.close();
	        
	        //[ITSM-16708] FSC implementation - retrieve values for fsc scheme startdate and enddate
	        Date day = UtilityFunctions.getSysParamTime(ds, "FSC_EFF_START_DATE");
	        calender.setTime(day);
	    	data.getGlobal().setFscStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));
	        
	        day = UtilityFunctions.getSysParamTime(ds, "FSC_EFF_END_DATE");
	        calender.setTime(day);
	    	data.getGlobal().setFscEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));
	    	
	    		
	        // [ITSM 17002] 7.1.05 - start
	        //boolean IS_IGS_Effective_bool = UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(settDate : settlementParam.sqlSettlementDate);
	    	params.isWMEPRoundedFeeEffDate = UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(ds, params.sqlSettlementDate);
	        data.getGlobal().setIgsEffective(params.isWMEPRoundedFeeEffDate);

	        // [ITSM 17002] 7.1.05 - end
	    	
	        // write the content of the csv file
/*	        String[] columnResults;

	        // only for FSC changes set the effective date flag
	        // original version
	        // if (haleyVersion >= 6.0) {
	        // Commented for [ITSM 17002] 7.1.05 Changes 
	        // columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1", fsc_scheme_startdate, fsc_scheme_enddate };
	        // Added for [ITSM 17002] 7.1.05 Changes 
	        // columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1", fsc_scheme_startdate, fsc_scheme_enddate, String.valueOf(o : IS_IGS_Effective) };    	
	        // }else {
	        // columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1" };    	
	        // }	
	        // original ends
	        if (haleyVersion >= 7.1) {
	            // Added for [ITSM 17002] 7.1.05 Changes 
	            columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1", fsc_scheme_startdate, fsc_scheme_enddate, String.valueOf(o : IS_IGS_Effective) };
	        }
	        else {
	            if (haleyVersion >= 6.0) {
	                columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1", fsc_scheme_startdate, fsc_scheme_enddate };
	            }
	            else {
	                columnResults = { CMWH_EMCADM, CMWH_PSOADM, String.valueOf(o : cmwh), String.valueOf(o : daysOfMonth), String.valueOf(o : emcBgt), String.valueOf(o : meuc), String.valueOf(o : psoBgt), String.valueOf(o : inputGST), String.valueOf(o : nonInputGST), String.valueOf(o : nonOutputGST), String.valueOf(o : outputGST), runType, settRunId, settlement_date, String.valueOf(o : totleTTE), String.valueOf(o : zeroInputGST), String.valueOf(o : zeroOutputGST), standingVersion, String.valueOf(o : totalEMCAdm), String.valueOf(o : totalPSOAdm), inputGSTCode, nonInputGSTCode, nonOutputGSTCode, outputGSTCode, zeroInputGSTCode, zeroOutputGSTCode, String.valueOf(o : emcPriceCap), "1" };
	            }
	        }*/

	        String msg = "Successfully Generated Entity CSV File: Global";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readNodesPF(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readNodesPF()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);

		try {
			conn = ds.getConnection();

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSVFile: Node and FTR", "");

		    // *************** csv input parameters **************
		    Facility master = new Facility();
		    master.setWeq(BigDecimal.ZERO);
		    master.setFcc(BigDecimal.ZERO);
		    
		    // ******** interactive parameters******
		    String version = runPackage.standingVersion;
		    String price_version = runPackage.mcPricePkgVer;
		    String mc_qty_version = runPackage.mcQtyPkgVer;
		    String mssl_qty_version = runPackage.msslQtyPkgVer;

		    // *************************************
		    // ******** testing parameters**********
		    int numberOfEntities = 0;

		    // **************************************************	
		    // ******* Queries used in this Method *******
		    // FTQ Contracts Query
		    String sqlFtq = "SELECT QUANTITY, SAC_ID, NDE_ID FROM NEM.NEM_FTQ_CONTRACTS " + 
		    "WHERE ? BETWEEN START_DATE AND END_DATE AND APPROVAL_STATUS = 'A'";

		    // Query LCQ
		    String sqlLCQ = "SELECT nde.NAME,drs.TRADING_PERIOD,NVL(drs.LCQ_USED_SETTLEMENT,0),NVL(drs.FCR_OIEC,0),NVL(drs.FCR_SIEC,0),NVL(drs.WLQ,0),NVL(drs.COMPLIANCE_STATUS,'U'),drs.INPUT_PKG_VERSION,drs.version,drs.str_id " + 
		    "FROM NEM.NEM_DR_COMPLIANCE_RESULTS drs, NEM.NEM_FACILITIES fct, NEM.NEM_NODES nde " + 
		    "WHERE drs.LOCK_VERSION = 1 AND drs.TRADING_DATE = ? AND drs.FCT_ID = fct.ID AND drs.FCT_VERSION = fct.VERSION AND fct.NDE_ID = nde.ID AND fct.NDE_VERSION = nde.VERSION";

		    // Query Node
		    String sqlNode = "SELECT ID,VERSION,NODE_TYPE,NAME,SAC_ID FROM NEM.NEM_NODES WHERE VERSION = ? AND NODE_TYPE <> 'S'";

		    // Query Facilities
		    // 7.1.01
		    // sqlFacility = "SELECT SECONDARY_RISK, FAILURE_PROBABILITY FROM NEM.NEM_FACILITIES WHERE VERSION = ? and NDE_ID = ?"
		    String sqlFacility = "SELECT fct.SECONDARY_RISK, fct.FAILURE_PROBABILITY,  nvl(gnt.GENERATION_SUB_TYPE,'NO Sub Type') GENERATION_SUB_TYPE, gnt.GENERATION_TYPE FROM NEM.NEM_FACILITIES fct, NEM.NEM_GENERATION_TYPES gnt WHERE fct.VERSION = ? and fct.NDE_ID = ? " + 
		                  "AND gnt.VERSION = ? AND fct.GNT_VERSION = gnt.VERSION AND fct.GNT_ID = gnt.ID";

		    // Query Quantities
		    String sqlQty = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND QUANTITY_TYPE = ? AND VERSION = ? ORDER BY PERIOD";

		    // Query Price
		    String sqlPrice = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND PRICE_TYPE = ? AND VERSION = ? ORDER BY PERIOD";

		    // *******************************************
		    // get all the FTQ contracts
		    HashMap<String, String> ftq_contract_list = new HashMap<String, String>();
			stmt = conn.prepareStatement(sqlFtq);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
		        ftq_contract_list.put(rs.getString(3), rs.getString(1));
		    }
			rs.close();
			stmt.close();

		    // get all the LCQ
		    String input_pkg_version = "";
		    String cc_version = "";

		    // DRSAT-240
		    String str_id = "";

		    // DRSAT-240
		    HashMap<String, DRCompliantType> lcq_list = new HashMap<String, DRCompliantType>();

			stmt = conn.prepareStatement(sqlLCQ);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
		        DRCompliantType dct = new DRCompliantType();
		        dct.lcq = rs.getBigDecimal(3);
		        dct.oiec = rs.getBigDecimal(4);
		        dct.siec = rs.getBigDecimal(5);
		        dct.wlq = rs.getBigDecimal(6);
		        dct.compliantFlag = rs.getString(7);
		        input_pkg_version = rs.getString(8);
		        cc_version = rs.getString(9);
		        str_id = rs.getString(10);
		        
		        lcq_list.put(rs.getString(1) + rs.getString(2), dct);
		    }
			rs.close();
			stmt.close();

		    logger.info(logPrefix + "DR Compliance INPUT data version: " + input_pkg_version + " ...");

		    // DRSAT-240
		    if (str_id == null || str_id.length() == 0) {
		    	logger.info(logPrefix + "Linking Compliance data to Settlement with version: " + cc_version + " ...");

				stmt = conn.prepareStatement("update nem.nem_dr_compliance_results set str_id = ? where trading_date = ? and version = ?");
				stmt.setString(1, params.runId);
				//stmt.setString(2, qdf.format(params.sqlSettlementDate));
				stmt.setString(2, sqlsd);
				stmt.setString(3, cc_version);
				stmt.executeQuery();
				
				stmt.close();
		    }
		    
            // Get Total Period
            //int totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");

		    // Loop for each Node
			stmt = conn.prepareStatement(sqlNode);
			stmt.setString(1, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
		        String node_id = rs.getString(1);
		        String account_id = rs.getString(5);

		        // logMessage "the node_id  is " + node_id using severity = WARNING
		        int count = 1;

		        // get the node_account
		        master.setAccountId(account_id != null? account_id: "");

		        // get node_SCU and node_SPF
		        PreparedStatement subStmt1 = conn.prepareStatement(sqlFacility);
		        subStmt1.setString(1, version);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, version);
		        subStmt1.executeQuery();
				ResultSet subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
			        String node_SCU = subRs1.getString(1);

		            if (node_SCU != null && node_SCU.equals("Y")) {
		            	master.setScu(true);
		            } else {
		            	master.setScu(false);		            	
		            }

		            master.setSpf(subRs1.getBigDecimal(2) != null? subRs1.getBigDecimal(2): BigDecimal.ZERO);

		            // 7.1.01
			        String node_gen_sub_type = subRs1.getString(3);

		            if (node_gen_sub_type != null && node_gen_sub_type.equals("AGGREGATE")) {
		                // So NO 5 MWH cut off applied for ONLY IGS Aggregate Facilities
		            	master.setNoCsz(true);
		            } else {
		                // 5 MWH cut off applied for ALL NON IGS Aggregate type Facilities
		            	master.setNoCsz(false);
		            }

		            // 7.1.01   
		            // logMessage "no_CSZ_Applicable " + no_CSZ_Applicable  using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        /*if (node_SCU == null) {
		            node_SCU_INT = 0;
		        }*/

		        // get the GFQ
		        HashMap<Integer, BigDecimal> node_GFQ_array = new HashMap<Integer, BigDecimal>();

		        subStmt1 = conn.prepareStatement(sqlQty);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "GFQ");
		        subStmt1.setString(4, mc_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
					BigDecimal node_GFQ = subRs1.getBigDecimal(1);

		            if (node_GFQ == null) {
		                node_GFQ = BigDecimal.ZERO;
		            }

		            node_GFQ_array.put(count, node_GFQ);

		            count = count + 1;

		            // logMessage "node_GFQ " + node_GFQ using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // get the IEQ
		        HashMap<Integer, BigDecimal> node_IEQ_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        subStmt1 = conn.prepareStatement(sqlQty);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "IEQ");
		        subStmt1.setString(4, mssl_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
					BigDecimal node_IEQ = subRs1.getBigDecimal(1);

		            if (node_IEQ == null) {
		            	node_IEQ = BigDecimal.ZERO;
		            }

		            node_IEQ_array.put(count, node_IEQ);

		            count = count + 1;

		            // logMessage "node_IEQ " + node_IEQ using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // get the WLQ
		        HashMap<Integer, BigDecimal> node_WLQ_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        subStmt1 = conn.prepareStatement(sqlQty);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "WLQ");
		        subStmt1.setString(4, mssl_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
					BigDecimal node_WLQ = subRs1.getBigDecimal(1);

		            if (node_WLQ == null) {
		            	node_WLQ = BigDecimal.ZERO;
		            }

		            node_WLQ_array.put(count, node_WLQ);

		            count = count + 1;
		        }
				subRs1.close();
				subStmt1.close();

		        // get the MEP
		        HashMap<Integer, BigDecimal> node_MEP_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        subStmt1 = conn.prepareStatement(sqlPrice);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "MEP");
		        subStmt1.setString(4, price_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();

		        // sqlCommand1 = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES WHERE SETTLEMENT_DATE = TO_DATE(?, 'YYYY-MM-DD') AND NDE_ID = ? AND PRICE_TYPE = ? AND VERSION = ? ORDER BY PERIOD"
				while (subRs1.next()) {
					BigDecimal node_MEP = subRs1.getBigDecimal(1);

		            if (node_MEP == null) {
		            	node_MEP = BigDecimal.ZERO;
		            }

		            node_MEP_array.put(count, node_MEP);

		            count = count + 1;

		            // logMessage "node_MEP " + node_MEP using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        String node_type = rs.getString(3);
		        String node_name = rs.getString(4);
		        master.setNodeType(node_type);
		        master.setFacilityId(node_name);

		        // get the SHD
		        HashMap<Integer, BigDecimal> node_SHD_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        subStmt1 = conn.prepareStatement(sqlQty);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "SHD");
		        subStmt1.setString(4, mc_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();

				while (subRs1.next()) {
					BigDecimal node_SHD = subRs1.getBigDecimal(1);

		            if (node_SHD == null) {
		            	node_SHD = BigDecimal.ZERO;
		            }

		            node_SHD_array.put(count, node_SHD);

		            count = count + 1;

		            // logMessage "node_SHD " + node_SHD using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // write the CSV files
	            // ***************************************
	            // Get Total Period
	            //int totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");
	            
	            for (int i = 1; i <= totalPeriod; i++) {
	            	
	            	Facility fct = new Facility(master);	// copy the default values
	            	fct.setPeriodId(i);

	                if (node_IEQ_array.containsKey(i) == false) {
	                    // logMessage "temp_node_IEQ is null"  using severity = WARNING
	                	fct.setIeq(BigDecimal.ZERO);
	                } else {
	                	fct.setIeq(node_IEQ_array.get(i));
	                }

	                if (node_GFQ_array.containsKey(i) == false) {
	                    // logMessage "temp_node_GFQ is null"  using severity = WARNING
	                	fct.setGfq(BigDecimal.ZERO);
	                } else {
	                	fct.setGfq(node_GFQ_array.get(i));
	                }

	                if (node_MEP_array.containsKey(i) == false) {
	                    // logMessage "temp_node_MEP is null"  using severity = WARNING
	                	fct.setMep(BigDecimal.ZERO);
	                } else {
	                	fct.setMep(node_MEP_array.get(i));
	                }

	                if (node_SHD_array.containsKey(i) == false) {
	                    // logMessage "temp_node_SHD is null"  using severity = WARNING
	                	fct.setShd(BigDecimal.ZERO);
	                }
	                else {
	                	fct.setShd(node_SHD_array.get(i));
	                }

	                //String temp_node_WLQ;

	                if (lcq_list.containsKey(node_name + String.valueOf(i)) == true) {
	                	DRCompliantType dr = lcq_list.get(node_name + String.valueOf(i));
	                	fct.setLcq(dr.lcq);
	                	fct.setOiec(dr.oiec);
	                	fct.setSiec(dr.siec);
                    	fct.setWlq(dr.wlq);
                    	fct.setComplFlag(dr.compliantFlag);
	                }
	                else {
	                	fct.setLcq(BigDecimal.ZERO);
	                	fct.setOiec(null);
	                	fct.setSiec(null);
                    	fct.setComplFlag("");

	                    if (node_WLQ_array.containsKey(i) == false) {
	                    	fct.setWlq(null);
	                    } else {
	                    	fct.setWlq(node_WLQ_array.get(i));
	                    }
	                }

	                // columnResults as String[] = [node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, node_delta_IEQ, node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, String.valueOf(o : temp_node_IEQ), String.valueOf(o : i), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                // node_acctg_rsc, node_acctg_rsd, node_type]    // 7.1.01
	                // 7.1.01 String[] columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, node_delta_IEQ, node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, String.valueOf(o : temp_node_IEQ), String.valueOf(o : i), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, node_acctg_rsc, node_acctg_rsd, node_type, String.valueOf(o : no_CSZ_Applicable) };
	                // 7.1.01
	                // only for IGS changes onwards set no CSZ Applicable flag for Aggregate Gen Type
	                /*String[] columnResults;

	                if (haleyVersion >= 8.0) {
	                    columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, node_delta_IEQ, node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, String.valueOf(o : temp_node_IEQ), String.valueOf(o : i), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, node_acctg_rsc, node_acctg_rsd, node_type, String.valueOf(o : no_CSZ_Applicable), node_LCQ, node_OIEC, node_SIEC, temp_node_WLQ, node_COMPFLAG };
	                }
	                else {
	                    if (haleyVersion >= 7.1) {
	                        columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, node_delta_IEQ, node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, String.valueOf(o : temp_node_IEQ), String.valueOf(o : i), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, node_acctg_rsc, node_acctg_rsd, node_type, String.valueOf(o : no_CSZ_Applicable) };
	                    }
	                    else {
	                        columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, node_delta_IEQ, node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, String.valueOf(o : temp_node_IEQ), String.valueOf(o : i), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, node_acctg_rsc, node_acctg_rsd, node_type };
	                    }
	                }*/

	                // 7.1.01
	                Ftr ftr = new Ftr();
	                if (ftq_contract_list.containsKey(node_id) == true && ftq_contract_list.get(node_id).length() > 0) {
	                	ftr.setFtq(new BigDecimal(ftq_contract_list.get(node_id)));
	                } else {
	                	ftr.setFtq(BigDecimal.ZERO);
	                }
	                ftr.setContractName("");
	                ftr.setNodeId(node_name);
	                ftr.setPeriodId(i);

	                data.getFacility().put(fct.getKey(), fct);
	                data.getFtr().put(ftr.getKey(), ftr);

	            }

		        // test whether it is 48 records
		        // test as Int = 0
		        // while length(node_GFQ_array) > 0 do
		        // temp_node_GFQ as Decimal= node_GFQ_array.first 
		        // node_GFQ_array.delete(node_GFQ_array.indexOf(temp_node_GFQ))
		        // test = test + 1
		        // end
		        numberOfEntities = numberOfEntities + 1;

		        // logMessage "numberOfEntities " + numberOfEntities using severity = WARNING
		    }
			rs.close();
			stmt.close();

		    String msg = "Successfully Generated Entity CSV File: Node and FTR";

		    logger.info(logPrefix + msg);

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readNodesRS(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readNodesRS()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);

		try {
			conn = ds.getConnection();
	
		    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Node and FTR", "");

		    // *************** csv input parameters **************
		    Facility master = new Facility();
		    master.setWeq(BigDecimal.ZERO);
		    master.setFcc(BigDecimal.ZERO);

		    // **************************************************	
		    // ******** interactive parameters******
		    String version = runPackage.standingVersion;
		    String price_version = runPackage.mcPricePkgVer;
		    String mc_qty_version = runPackage.mcQtyPkgVer;

		    // mssl_qty_version as String = settRunPkg.mssl_qty_pkg_ver
		    String rs_mssl_qty_version = runPackage.msslQtyPkgVer;

		    String lastRunMsslQtyVersion = "";
		    // *************************************
		    // ******** testing parameters**********
		    int numberOfEntities = 0;

		    // **************************************************	
		    // get the MSSL quantity version of:
		    // - Final Run (if current Run is R-Run) 
		    // - R-Run (if current Run is S-Run) for the Settlement Date
		    String sqlCommand = "SELECT MSSL_QTY_VERSION FROM NEM.NEM_SETTLEMENT_RUN_STATUS_V " + 
		                 "WHERE SETTLEMENT_DATE = ? AND RUN_TYPE = ? AND AUTHORISED = 'A'";
			stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, (params.runType.equalsIgnoreCase("R") == true? "F": "R"));		// Run type
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        lastRunMsslQtyVersion = rs.getString(1);

		        logger.info(logPrefix + params.sqlSettlementDate.toString() + "-Run MSSL Qty Version: " + lastRunMsslQtyVersion);
		    }
			rs.close();
			stmt.close();

		    // get all the FTQ contracts
		    HashMap<String, String> ftq_contract_list = new HashMap<String, String>();

		    sqlCommand = "SELECT QUANTITY,SAC_ID,NDE_ID FROM NEM.NEM_FTQ_CONTRACTS " + 
		              "WHERE START_DATE <= ? AND END_DATE >= ? " + 
		              "AND APPROVAL_STATUS = 'A'";
			stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			//stmt.setString(2, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        ftq_contract_list.put(rs.getString(3), rs.getString(1));
		    }
			rs.close();
			stmt.close();

            //int totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");
            
		    // from the NEM.NEM_NODES to generate the csv files
		    sqlCommand = "SELECT ID,VERSION,NODE_TYPE,NAME,SAC_ID " + 
		                 "FROM NEM.NEM_NODES WHERE VERSION = ? AND NODE_TYPE <> 'S'";
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        String node_id = rs.getString(1);
		        String account_id = rs.getString(5);

		        // logMessage "the node_id  is " + node_id using severity = WARNING
		        int count = 1;

		        // get the node_account
		        master.setAccountId(account_id != null? account_id: "");

		        // get node_SCU and node_SPF
		        sqlCommand = "SELECT SECONDARY_RISK, FAILURE_PROBABILITY " + 
		                      "FROM NEM.NEM_FACILITIES WHERE VERSION = ? and NDE_ID = ?";
		        
		        PreparedStatement subStmt1 = conn.prepareStatement(sqlCommand);
		        subStmt1.setString(1, version);
		        subStmt1.setString(2, node_id);
		        subStmt1.executeQuery();
				ResultSet subRs1 = subStmt1.getResultSet();

				while (subRs1.next()) {
			        String node_SCU = subRs1.getString(1);

		            if (node_SCU != null && node_SCU.equals("Y")) {
		            	master.setScu(true);
		            } else {
		            	master.setScu(false);		            	
		            }

		            master.setSpf(subRs1.getBigDecimal(2) != null? subRs1.getBigDecimal(2): BigDecimal.ZERO);

		            // logMessage "node_SCU " + node_SCU + " node_SPF " + node_SPF  using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        /*if (node_SCU == null) {
		            node_SCU_INT = 0;
		        }*/

		        // get the GFQ
		        HashMap<Integer, BigDecimal> node_GFQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
	                      	  "WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND " + 
		                      "QUANTITY_TYPE = ? AND VERSION = ? ORDER BY PERIOD";
		        subStmt1 = conn.prepareStatement(sqlCommand);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "GFQ");
		        subStmt1.setString(4, mc_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
					BigDecimal node_GFQ = subRs1.getBigDecimal(1);

		            if (node_GFQ == null) {
		                node_GFQ = BigDecimal.ZERO;
		            }

		            node_GFQ_array.put(count, node_GFQ);

		            count = count + 1;

		            // logMessage "node_GFQ " + node_GFQ using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // get the IEQ
		        HashMap<Integer, BigDecimal> node_delta_IEQ_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        sqlCommand = "SELECT rstq.quantity - pstq.quantity AS delta_ieq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.NDE_ID = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type " + 
		                      "AND rstq.nde_id = pstq.nde_id AND rstq.nde_version = pstq.nde_version " + 
		                      "ORDER BY rstq.PERIOD";
		        subStmt1 = conn.prepareStatement(sqlCommand);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "IEQ");
		        subStmt1.setString(4, rs_mssl_qty_version);
		        subStmt1.setString(5, lastRunMsslQtyVersion);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();
				
				while (subRs1.next()) {
					BigDecimal node_IEQ = subRs1.getBigDecimal(1);

		            if (node_IEQ == null) {
		            	node_IEQ = BigDecimal.ZERO;
		            }

		            node_delta_IEQ_array.put(count, node_IEQ);

		            count = count + 1;

		            // logMessage "node_IEQ " + node_IEQ using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // get the MEP
		        HashMap<Integer, BigDecimal> node_MEP_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        sqlCommand = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES " + 
	                      	  "WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND " + 
		                      "PRICE_TYPE = ? AND VERSION = ? ORDER BY PERIOD";
		        subStmt1 = conn.prepareStatement(sqlCommand);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "MEP");
		        subStmt1.setString(4, price_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();

				while (subRs1.next()) {
					BigDecimal node_MEP = subRs1.getBigDecimal(1);

		            if (node_MEP == null) {
		            	node_MEP = BigDecimal.ZERO;
		            }

		            node_MEP_array.put(count, node_MEP);

		            count = count + 1;

		            // logMessage "node_MEP " + node_MEP using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        String node_type = rs.getString(3);
		        String node_name = rs.getString(4);
		        master.setNodeType(node_type);
		        master.setFacilityId(node_name);

		        // get the SHD
		        HashMap<Integer, BigDecimal> node_SHD_array = new HashMap<Integer, BigDecimal>();
		        count = 1;

		        sqlCommand = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
	                          "WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND " + 
		                      "QUANTITY_TYPE = ? AND VERSION = ? ORDER BY PERIOD";
		        subStmt1 = conn.prepareStatement(sqlCommand);
				//subStmt1.setString(1, qdf.format(params.sqlSettlementDate));
		        subStmt1.setString(1, sqlsd);
		        subStmt1.setString(2, node_id);
		        subStmt1.setString(3, "SHD");
		        subStmt1.setString(4, mc_qty_version);
		        subStmt1.executeQuery();
				subRs1 = subStmt1.getResultSet();

				while (subRs1.next()) {
					BigDecimal node_SHD = subRs1.getBigDecimal(1);

		            if (node_SHD == null) {
		            	node_SHD = BigDecimal.ZERO;
		            }

		            node_SHD_array.put(count, node_SHD);

		            count = count + 1;

		            // logMessage "node_SHD " + node_SHD using severity = WARNING
		        }
				subRs1.close();
				subStmt1.close();

		        // write the CSV files
	            //int totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");
	            
	            for (int i = 1; i <= totalPeriod; i++) {
	            	
	            	Facility fct = new Facility(master);	// copy the default values
	                fct.setPeriodId(i);

	                if (node_delta_IEQ_array.containsKey(i) == false) {
	                    // logMessage "temp_node_IEQ is null"  using severity = WARNING
	                	fct.setDeltaIeq(BigDecimal.ZERO);
	                } else {
	                	fct.setDeltaIeq(node_delta_IEQ_array.get(i));
	                }

	                if (node_GFQ_array.containsKey(i) == false) {
	                    // logMessage "temp_node_GFQ is null"  using severity = WARNING
	                	fct.setGfq(BigDecimal.ZERO);
	                } else {
	                	fct.setGfq(node_GFQ_array.get(i));
	                }

	                if (node_MEP_array.containsKey(i) == false) {
	                    // logMessage "temp_node_MEP is null"  using severity = WARNING
	                	fct.setMep(BigDecimal.ZERO);
	                } else {
	                	fct.setMep(node_MEP_array.get(i));
	                }

	                if (node_SHD_array.containsKey(i) == false) {
	                    // logMessage "temp_node_SHD is null"  using severity = WARNING
	                	fct.setShd(BigDecimal.ZERO);
	                }
	                else {
	                	fct.setShd(node_SHD_array.get(i));
	                }

	                // 7.1.05 Changes
	                // String[] columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, String.valueOf(o : temp_node_delta_IEQ), node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, node_IEQ, String.valueOf(o : i + 1), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                // node_acctg_rsc, node_acctg_rsd, node_type };
	                // 7.1.05 Addition
	                // String[] columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, String.valueOf(o : temp_node_delta_IEQ), node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, node_IEQ, String.valueOf(o : i + 1), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                // node_acctg_rsc, node_acctg_rsd, node_type, "0" }; 
	                // 7.1.20
	                // only for IGS changes onwards set no CSZ Applicable flag for Aggregate Gen Type
	                /*String[] columnResults;

	                if (haleyVersion >= 8.0) {
	                    columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, String.valueOf(o : temp_node_delta_IEQ), node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, node_IEQ, String.valueOf(o : i + 1), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                                    node_acctg_rsc, node_acctg_rsd, node_type, "0", "", "", "", "", "" };
	                }
	                else {
	                    if (haleyVersion >= 7.1) {
	                        columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, String.valueOf(o : temp_node_delta_IEQ), node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, node_IEQ, String.valueOf(o : i + 1), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                                        node_acctg_rsc, node_acctg_rsd, node_type, "0" };
	                    }
	                    else {
	                        columnResults = { node_has_sz_idx, node_positive_inj, node_cntb_SRQ, node_PCU, String.valueOf(o : node_SCU_INT), node_account, String.valueOf(o : temp_node_delta_IEQ), node_FEQ_adj, node_FSC, node_GESCE, node_GESCN, node_GESCP, String.valueOf(o : temp_node_GFQ), node_GMEE, node_GMEF, node_GRQ, node_IEQ, String.valueOf(o : i + 1), node_IPF, node_IPW, node_LESDN, node_LESDP, node_LRQ, String.valueOf(o : temp_node_MEP), node_name, node_NEAA, node_NEGC, node_NEGC_sz_idx, node_NELC, node_NRSC, node_NTSC, node_size_index, node_RCC, node_RRS, node_RRS_factor, node_RSC, node_RSD, node_RTQ, node_RTS, String.valueOf(o : temp_node_SHD), String.valueOf(o : node_SPF), node_T_factor, node_VCRP, node_WEQ, node_facility_RSC, node_acctg_rcc, // THC, adds three more empty fields for accounting purposes
	                                        node_acctg_rsc, node_acctg_rsd, node_type };
	                    }
	                }*/

	                // 7.1.20
	                Ftr ftr = new Ftr();
	                if (ftq_contract_list.containsKey(node_id) == true && ftq_contract_list.get(node_id).length() > 0) {
	                	ftr.setFtq(new BigDecimal(ftq_contract_list.get(node_id)));
	                } else {
	                	ftr.setFtq(BigDecimal.ZERO);
	                }
	                ftr.setContractName("");
	                ftr.setNodeId(node_name);
	                ftr.setPeriodId(i);

	                data.getFacility().put(fct.getKey(), fct);
	                data.getFtr().put(ftr.getKey(), ftr);
	                
	            }

		        // test whether it is 48 records
		        // test as Int = 0
		        // while length(node_GFQ_array) > 0 do
		        // temp_node_GFQ as Decimal= node_GFQ_array.first 
		        // node_GFQ_array.delete(node_GFQ_array.indexOf(temp_node_GFQ))
		        // test = test + 1
		        // end
		        numberOfEntities = numberOfEntities + 1;

		        // logMessage "numberOfEntities " + numberOfEntities using severity = WARNING
		    }
			rs.close();
			stmt.close();

		    String msg = "Successfully Generated Entity CSV File: Node and FTR";

		    logger.info(logPrefix + msg);

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readAccounts(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readAccounts()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Account", "");

		    // FSC implementation - variable to set account breach effective date
		    String account_breachYN = "";

		    // **********passed in parameters******* 
		    String str_id = params.runId;

		    // *************************************
		    // write the column headers
		    String sqlCommand;

		    // **********interactive parameters*******
		    //account_str_id = str_id;
		    String version = runPackage.standingVersion;

		    // ***************************************
		    // get all accounts' id
		    HashMap<String, String> retailer = new HashMap<String, String>();
//		    StringBuffer allAccountIds = new StringBuffer();
		    
		    sqlCommand = "select ID,RETAILER_ID from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=? and RETAILER_ID is not null";
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
//		        String account_id = rs.getString(1);
//		        allAccountIds.append(account_id + ":");

		        // logMessage "the account Id is " + account_id using severity = WARNING
		        retailer.put(rs.getString(2), rs.getString(1));
		    }
			rs.close();
			stmt.close();

		    // get the entity account information
		    sqlCommand = "select ID,RETAILER_ID,EXTERNAL_ID,NET_SETTLEMENT,EMBEDDED_GEN,DISPLAY_TITLE,GST_REGISTERED, SAC_TYPE,PTP_ID from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=?";
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
				Account sac = new Account();
				sac.setRunId(str_id);
				
		        String account_id = rs.getString(1);

		        // logMessage "account_id is : " + account_id using severity = WARNING
		        String retailer_id = rs.getString(2);

		        // logMessage "retailer_id is : " + retailer_id using severity = WARNING
		        sac.setEgaRetailer(retailer.containsKey(account_id));

		        String external_id = rs.getString(3);
		        sac.setEmcAccount(external_id.equals("EMC FEE_A"));
		        sac.setMsslAccount(external_id.equals("SP SVCS_M"));
		        sac.setPsoAccount(external_id.equals("PWR SYS_O"));
		        sac.setIntertie(external_id.equals("INTERTIE"));

		        sac.setAccountId(account_id);
		        
		        String net_settlement = rs.getString(4);
		        sac.setNetSett(net_settlement.equalsIgnoreCase("Y"));

		        String embeded_gen = rs.getString(5);
		        sac.setPriceNeutralization(embeded_gen.equalsIgnoreCase("Y"));

		        sac.setUnderRetailer(retailer_id != null);

		        sac.setDisplayTitle(rs.getString(6));

		        // [EMCS-474]
		        String gst_registered = rs.getString(7);
		        sac.setTaxable(gst_registered.equalsIgnoreCase("Y"));

		        sac.setParticipantId(rs.getString(9));

		        // Ignore--FSC Implementation - retrieve breach date if the settlement account is a breach account on the given settlement date
		        // Ignore--Do not send breach flag to OPA. Breach flag is not relavant in FSSC calculations hence commenting the breach retrieval code. 
		        // All the accounts FSSC is calculated in OPA irrespective of whether the account is breach or not
		        // account_breachYN = UtilityFunctions.isFSCSettlementAccountBreached(ds, account_id, params.settlementDate);
		        sac.setBreached(account_breachYN.equalsIgnoreCase("Y"));	// TODO: it never get the value at all, result was account never breached.

		        /*String[] columnResults;

		            columnResults = { String.valueOf(o : account_MSSL), String.valueOf(o : account_net_sett), String.valueOf(o : account_pn), String.valueOf(o : account_EMC), String.valueOf(o : account_GST), String.valueOf(o : account_intertie), String.valueOf(o : account_PSO), String.valueOf(o : account_EGA_retailer), String.valueOf(o : account_under_retailer), account_ADMFEE, account_BESC, account_EMCADM, account_EUA, account_FCC, account_FSC, account_FSD, account_GESC, account_GMEE, account_HEUSA, account_inc_GMEE, account_inc_GMEF, account_inc_LMEE, account_inc_LMEF, account_inc_NMEA, account_v_ADMFEE, account_v_FSC, account_v_GESC, account_v_inc_GMEE, account_v_inc_NMEA, account_v_LESC, account_v_NASC, account_v_NESC, account_v_NFSC, account_v_NPSC, account_v_NRSC, account_v_RSC, account_v_stmt, account_LESD, account_MEUSA, account_name, account_NASC, account_NEAA, account_NEAD, account_NEGC, account_NELC, account_NESC, account_net_amt, account_NFSC, account_NPSC, account_NRSC, account_NTSC, account_a_EMCADM, account_a_FSD, account_a_GESC, account_a_HEUSA, account_a_inc_GMEF, account_a_inc_LMEE, account_a_inc_LMEF, account_a_inc_NMEA, account_a_LESC, account_a_MEUSA, account_a_NASC, account_a_NESC, account_a_NFSC, account_a_NPSC, account_a_NRSC, account_a_PSOADM, account_a_RSD, account_a_stmt, account_PSOADM, account_RCC, account_RSC, account_RSD, account_str_id, account_total_ADMFEE, account_total_amt, account_total_BESC, account_total_EMCADM, account_total_FCC, account_total_FSC, account_total_FSD, account_total_GESC, account_total_GMEE, account_total_HEUSA, account_total_LESC, account_total_MEUSA, account_total_NASC, account_total_NEAA, account_total_NEAD, account_total_NEGC, account_total_NELC, account_total_NESC, account_total_NFSC, account_total_NPSC, account_total_NRSC, account_total_NTSC, account_total_inc_GMEE, account_total_inc_GMEF, account_total_inc_LMEE, account_total_inc_LMEF, account_total_inc_NMEA, account_total_PSOADM, account_total_RCC, account_total_RSC, account_total_RSD, account_total_VCSC, account_VCSC, account_other_total, account_facility_RSC, account_disp_title, String.valueOf(o : account_breach_flag), account_mp };
		        }
		        else {
		            columnResults = { String.valueOf(o : account_MSSL), String.valueOf(o : account_net_sett), String.valueOf(o : account_pn), String.valueOf(o : account_EMC), String.valueOf(o : account_GST), String.valueOf(o : account_intertie), String.valueOf(o : account_PSO), String.valueOf(o : account_EGA_retailer), String.valueOf(o : account_under_retailer), account_ADMFEE, account_BESC, account_EMCADM, account_EUA, account_FCC, account_FSC, account_FSD, account_GESC, account_GMEE, account_HEUSA, account_inc_GMEE, account_inc_GMEF, account_inc_LMEE, account_inc_LMEF, account_inc_NMEA, account_v_ADMFEE, account_v_FSC, account_v_GESC, account_v_inc_GMEE, account_v_inc_NMEA, account_v_LESC, account_v_NASC, account_v_NESC, account_v_NFSC, account_v_NPSC, account_v_NRSC, account_v_RSC, account_v_stmt, account_LESD, account_MEUSA, account_name, account_NASC, account_NEAA, account_NEAD, account_NEGC, account_NELC, account_NESC, account_net_amt, account_NFSC, account_NPSC, account_NRSC, account_NTSC, account_a_EMCADM, account_a_FSD, account_a_GESC, account_a_HEUSA, account_a_inc_GMEF, account_a_inc_LMEE, account_a_inc_LMEF, account_a_inc_NMEA, account_a_LESC, account_a_MEUSA, account_a_NASC, account_a_NESC, account_a_NFSC, account_a_NPSC, account_a_NRSC, account_a_PSOADM, account_a_RSD, account_a_stmt, account_PSOADM, account_RCC, account_RSC, account_RSD, account_str_id, account_total_ADMFEE, account_total_amt, account_total_BESC, account_total_EMCADM, account_total_FCC, account_total_FSC, account_total_FSD, account_total_GESC, account_total_GMEE, account_total_HEUSA, account_total_LESC, account_total_MEUSA, account_total_NASC, account_total_NEAA, account_total_NEAD, account_total_NEGC, account_total_NELC, account_total_NESC, account_total_NFSC, account_total_NPSC, account_total_NRSC, account_total_NTSC, account_total_inc_GMEE, account_total_inc_GMEF, account_total_inc_LMEE, account_total_inc_LMEF, account_total_inc_NMEA, account_total_PSOADM, account_total_RCC, account_total_RSC, account_total_RSD, account_total_VCSC, account_VCSC, account_other_total, account_facility_RSC, account_disp_title, String.valueOf(o : account_breach_flag) };
		        }*/

		        //csvWriter.writeLine(values : columnResults);
		        
		        data.getAccount().put(sac.getKey(), sac);
		    }
			rs.close();
			stmt.close();
			
	        String msg = "Successfully Generated Entity CSV File: Account";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readReserves(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readReserves()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);

		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Reserve and BRQ", "");


		    // ********************** CSV parameters ****************
		    //boolean no_CSZ_Applicable = false;

		    // 7.1.05 - Renaming 5MWH cut off param CMWH as CSZ - this is to avoid confusion as term CMWH is commonly used in Fee calculations - CSZ is just an arbitary name
		    String node_gen_sub_type = "";


		    // ******** interactive parameters******
            //int totalPeriod = (int)UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS");
		    String version = runPackage.standingVersion;
		    String node_id = "";
		    String account_id = "";
		    String acg_id = "";
		    HashMap<String, BRQType> buyer_contract_list = new HashMap<String, BRQType>();
		    HashMap<String, BRQType> seller_contract_list = new HashMap<String, BRQType>();
		    String contract_key = "";

		    // get all ACG_ID and names
		    HashMap<String, String> ancillary_groups = new HashMap<String, String>();

		    String sqlACG = "select ID, NAME from NEM.NEM_ANCILLARY_GROUPS where VERSION=? AND ANCILLARY_TYPE=?";
			stmt = conn.prepareStatement(sqlACG);
			stmt.setString(1, version);
			stmt.setString(2, "RSV");
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
				ancillary_groups.put(rs.getString(1), rs.getString(2));
		    }
			rs.close();
			stmt.close();

		    // get all the bilateral reserve
		    String sqlBLC = "SELECT ID, VERSION, CONTRACT_TYPE, NDE_ID, SAC_ID_PURCHASED_BY, SAC_ID_SOLD_BY, ACG_ID, NAME " + 
		             "FROM NEM.NEM_BILATERAL_CONTRACTS WHERE ? BETWEEN START_DATE AND END_DATE AND EXPIRED_DATE >= ? " + 
		             "AND APPROVAL_STATUS = 'A' AND CONTRACT_TYPE = ?";
		    String sqlBLCParam = "SELECT VALUE,PERIOD FROM NEM.NEM_BILATERAL_PARAMETERS WHERE BLT_ID = ? and BLT_VERSION = ? order by PERIOD";
			stmt = conn.prepareStatement(sqlBLC);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			//stmt.setString(2, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
	        stmt.setString(2, sqlsd);
			stmt.setString(3, "Reserve");
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
		        String contract_id = rs.getString(1);
		        String contract_version = rs.getString(2);
		        String contract_type = rs.getString(3);
		        String contract_node = rs.getString(4);
		        String bilateral_buying_id = rs.getString(5);
		        String bilateral_selling_id = rs.getString(6);
		        String ACG_id = rs.getString(7);
		        String contract_name = rs.getString(8);

		        // display contract_name
		        PreparedStatement stmt1 = conn.prepareStatement(sqlBLCParam);
				stmt1.setString(1, contract_id);
				stmt1.setString(2, contract_version);
				stmt1.executeQuery();
				ResultSet rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            BigDecimal value = rs1.getBigDecimal(1);
		            String period = rs1.getString(2);

		            // contract_key = contract_node + ACG_id + period
		            // display "Contract Node: " + contract_node + ", ACG: " + ACG_id
		            if (bilateral_buying_id != null && bilateral_buying_id.trim().length() > 0) {
			            contract_key = bilateral_buying_id + contract_node + ACG_id + period;
			            BRQType buy = new BRQType(value, BigDecimal.ZERO, bilateral_buying_id, "", contract_name);
			            buyer_contract_list.put(contract_key, buy);
		            }
		            
		            if (bilateral_selling_id != null && bilateral_selling_id.trim().length() > 0) {
			            contract_key = bilateral_selling_id + contract_node + ACG_id + period;
			            BRQType sell = new BRQType(BigDecimal.ZERO, value, "", bilateral_selling_id, contract_name);
			            seller_contract_list.put(contract_key, sell);
		            }
		        }
				rs1.close();
				stmt1.close();
		    }
			rs.close();
			stmt.close();

		    // logMessage "BRQ done " + length(contract_list) using severity = WARNING
		    // get the reserve_MRP
		    HashMap<String, BigDecimal> MRP_list = new HashMap<String, BigDecimal>();

		    String sqlPrice = "SELECT PRICE, PERIOD, ACG_ID FROM NEM.NEM_SETTLEMENT_PRICES " + 
		               "WHERE SETTLEMENT_DATE = ? AND VERSION =? AND PRICE_TYPE =? ORDER BY ACG_ID,PERIOD";
			stmt = conn.prepareStatement(sqlPrice);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, runPackage.mcPricePkgVer);
			stmt.setString(3, "MRP");
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
				MRP_list.put(rs.getString(3) + rs.getString(2), rs.getBigDecimal(1));
		    }
			rs.close();
			stmt.close();

		    // from the NEM.NEM_NODES to generate the csv files
		    int node_count = 0;
		    int acg_count = 0;
		    int reserve_GRQ_count = 0;
		    int reserve_LRQ_count = 0;

		    // Query Node
		    String sqlNode = "SELECT ID,VERSION,NODE_TYPE,NAME,SAC_ID FROM NEM.NEM_NODES WHERE VERSION = ? and NODE_TYPE <> 'S'";

		    // Facility Query
		    // 7.1.01
		    // sqlFacility = "SELECT FACILITY_TYPE FROM NEM.NEM_FACILITIES WHERE VERSION = ? AND NDE_ID = ? "
		    String sqlFacility = "SELECT fct.FACILITY_TYPE,  nvl(gnt.GENERATION_SUB_TYPE,'NO Sub Type') GENERATION_SUB_TYPE, gnt.GENERATION_TYPE FROM NEM.NEM_FACILITIES fct, NEM.NEM_GENERATION_TYPES gnt " +
		    "WHERE fct.VERSION = ? and fct.NDE_ID = ? AND gnt.VERSION = ? AND fct.GNT_VERSION = gnt.VERSION AND fct.GNT_ID = gnt.ID";

		    // Quantity Query
		    String sqlQty1 = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
		              "WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND QUANTITY_TYPE = ? " + 
		              "AND VERSION = ? AND ACG_ID = ? ORDER BY PERIOD";

		    // Quantity Query
		    String sqlQty2 = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
		              "WHERE SETTLEMENT_DATE = ? AND NDE_ID = ? AND QUANTITY_TYPE = ? " + 
		              "AND VERSION = ? AND ACG_ID = ? AND SAC_ID = ? ORDER BY PERIOD";

			stmt = conn.prepareStatement(sqlNode);
			stmt.setString(1, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
		        node_id = rs.getString(1);
		        String node_type = rs.getString(3);

		        // get the reserve_node
		        String reserve_node = rs.getString(4);
		        account_id = rs.getString(5);
		        String facility_type = "";
		        boolean no_CSZ_Applicable = false;

		        // 7.1.05
		        node_gen_sub_type = "";

		        // 7.1.05		
		        PreparedStatement stmt1 = conn.prepareStatement(sqlFacility);
				stmt1.setString(1, version);
				stmt1.setString(2, node_id);
				stmt1.setString(3, version);
				stmt1.executeQuery();
				ResultSet rs1 = stmt1.getResultSet();

		        // 7.1.01
		        // facility_query = "SELECT FACILITY_TYPE FROM NEM.NEM_FACILITIES WHERE VERSION = ? AND NDE_ID = ? "
				while (rs1.next()) {
		            facility_type = rs1.getString(1);

		            // 7.1.01
		            node_gen_sub_type = rs1.getString(2);

		            no_CSZ_Applicable = (node_gen_sub_type != null && node_gen_sub_type.equals("AGGREGATE"));

		            // 7.1.01
		            // logMessage "Reserve no_CSZ_Applicable " + no_CSZ_Applicable  using severity = WARNING			
		        }
				rs1.close();
				stmt1.close();

				Iterator<Entry<String, String>> itr2 = ancillary_groups.entrySet().iterator();
				while (itr2.hasNext()) {
					Entry<String, String> entry =  itr2.next();
		            acg_id = entry.getKey();
		            String reserve_name = entry.getValue();
		            int pos = 1;

		            // get the reserve_GRQ
		            HashMap<Integer, BigDecimal> reserve_GRQ_array = new HashMap<Integer, BigDecimal>();
		            BigDecimal temp_reserve_GRQ;

		            if (facility_type.equals("UNT") == false) {
		                temp_reserve_GRQ = BigDecimal.ZERO;
		                {
		                    int i = 1;

		                    while (i <= totalPeriod) {
		                        reserve_GRQ_array.put(i, temp_reserve_GRQ);

		                        reserve_GRQ_count = reserve_GRQ_count + 1;
		                        i = i + 1;
		                    }
		                }
		            } else {
				        PreparedStatement stmt2 = conn.prepareStatement(sqlQty1);
						//stmt2.setString(1, qdf.format(params.sqlSettlementDate));
				        stmt2.setString(1, sqlsd);
				        stmt2.setString(2, node_id);
				        stmt2.setString(3, "GRQ");
				        stmt2.setString(4, runPackage.mcQtyPkgVer);
				        stmt2.setString(5, acg_id);
				        stmt2.executeQuery();
						ResultSet rs2 = stmt2.getResultSet();

						while (rs2.next()) {
		                    temp_reserve_GRQ = rs2.getBigDecimal(1);
		                    reserve_GRQ_array.put(pos, temp_reserve_GRQ);

		                    pos = pos + 1;
		                    reserve_GRQ_count = reserve_GRQ_count + 1;
		                }
						rs2.close();
						stmt2.close();
		            }

		            // sqlCommand4 as String
		            pos = 1;

		            // get the reserve_LRQ
		            HashMap<Integer, BigDecimal> reserve_LRQ_array = new HashMap<Integer, BigDecimal>();
		            BigDecimal temp_reserve_LRQ;

		            if (node_type.equals("L") == false) {
		                temp_reserve_LRQ = BigDecimal.ZERO;
		                {
		                    int j = 1;

		                    while (j <= totalPeriod) {
		                        reserve_LRQ_array.put(j, temp_reserve_LRQ);

		                        reserve_LRQ_count = reserve_LRQ_count + 1;
		                        j = j + 1;
		                    }
		                }
		            } else {
				        PreparedStatement stmt2 = conn.prepareStatement(sqlQty2);
						//stmt2.setString(1, qdf.format(params.sqlSettlementDate));
				        stmt2.setString(1, sqlsd);
				        stmt2.setString(2, node_id);
				        stmt2.setString(3, "LRQ");
				        stmt2.setString(4, runPackage.mcQtyPkgVer);
				        stmt2.setString(5, acg_id);
				        stmt2.setString(6, account_id);
				        stmt2.executeQuery();
						ResultSet rs2 = stmt2.getResultSet();

						while (rs2.next()) {
							temp_reserve_LRQ = rs2.getBigDecimal(1);
							reserve_LRQ_array.put(pos, temp_reserve_LRQ);
		                    
		                    pos = pos + 1;
		                    reserve_LRQ_count = reserve_LRQ_count + 1;
		                }
						rs2.close();
						stmt2.close();
		            }

	                int i = 1;

	                while (i <= totalPeriod) {
	                	
	                	Reserve rsv = new Reserve();
	                	rsv.setPeriodId(i);
	                	rsv.setName(reserve_name);
	                	rsv.setNode(reserve_node);
	                	rsv.setAccountId(account_id);
	                	
	                	rsv.setBrqPurchased(BigDecimal.ZERO);
	                	rsv.setBrqSold(BigDecimal.ZERO);
	                	
	                    if (reserve_GRQ_array.size() == 0) {
	                    	rsv.setGrq(BigDecimal.ZERO);
	                    } else {
	                    	rsv.setGrq(reserve_GRQ_array.get(i));
	                    }

	                    if (reserve_LRQ_array.size() == 0) {
	                    	rsv.setLrq(BigDecimal.ZERO);
	                    } else {
	                    	rsv.setLrq(reserve_LRQ_array.get(i));
	                    }

	                    rsv.setMrp(MRP_list.get(acg_id + String.valueOf(i)));

	                    // logMessage "columnResults2 is" + account_id +"/"+ node_id +"/"+ reserve_name  using severity = WARNING
	                    contract_key = node_id + acg_id + String.valueOf(i);
	                    //contract_key = account_id + acg_id + String.valueOf(i);

	                    // display "Contract Key: " + contract_key
	                    Brq brc = new Brq();
	                    brc.setPeriodId(i);
	                    brc.setAccountId(account_id);
	                    brc.setNode(reserve_node);
	                    brc.setReserveClass(reserve_name);
	                    brc.setPurchased(BigDecimal.ZERO);
	                    brc.setSold(BigDecimal.ZERO);
	                    brc.setContractName("");

	                    if (buyer_contract_list.get(contract_key) != null && account_id.equals(buyer_contract_list.get(contract_key).purchaseId)) {
                        	brc.setPurchased(buyer_contract_list.get(contract_key).purchased);
                        	brc.setContractName(buyer_contract_list.get(contract_key).contract);
	                    }

	                    if (seller_contract_list.get(contract_key) != null && account_id.equals(seller_contract_list.get(contract_key).sellingId)) {
                        	brc.setSold(seller_contract_list.get(contract_key).sold);
                        	brc.setContractName(seller_contract_list.get(contract_key).contract);
	                    }
	                    
	                    // 1. add the brq into the list of this reserve as well
                    	rsv.getSoldContracts().add(brc);
                    	rsv.getPurchasedContracts().add(brc);

	                    /*String[] columnResults2 = { brq_purchased, brq_sold, brq_name, account_id, String.valueOf(o : i), reserve_node, reserve_name };
	                    String[] columnResults = { String.valueOf(o : reserve_BRQ_purchased), String.valueOf(o : reserve_BRQ_sold), String.valueOf(o : reserve_GRQ), String.valueOf(o : reserve_LRQ), String.valueOf(o : reserve_MRP), reserve_RCC, reserve_RSC, reserve_RSD, reserve_factor, reserve_name, reserve_node, String.valueOf(o : i), reserve_RSQ, reserve_facility_RSC, account_id };
	                    csvWriter.writeLine(values : columnResults);

	                    csvWriter1.writeLine(values : columnResults2);

	                    if (reserve_GRQ_array.length != 0) {
	                        reserve_GRQ_array.delete(index : reserve_GRQ_array.indexOf(element : reserve_GRQ));
	                    }

	                    if (reserve_LRQ_array.length != 0) {
	                        reserve_LRQ_array.delete(index : reserve_LRQ_array.indexOf(element : reserve_LRQ));
	                    }*/
	                    
	                    data.getBrq().put(brc.getKey(), brc);
	                    data.getReserve().put(rsv.getKey(), rsv);
	                    
	                    data.getFacility().get(rsv.getNodeKey()).getReserves().add(rsv);
	                    data.getMarket().get(rsv.getPeriodId()).getReserves().add(rsv);
	                    data.getRsvClass().get(rsv.getRsvClsKey()).getReserves().add(rsv);

	                    i = i + 1;
	                }

		            // logMessage "reserve_MRP_count is" + reserve_MRP_count  using severity = WARNING
		            acg_count = acg_count + 1;
		        }

		        node_count = node_count + 1;

		        // logMessage "node_count is " + node_count  using severity = WARNING
		    }
			rs.close();
			stmt.close();

			
	        String msg = "Successfully Generated Entity CSV File: Reserve and BRQ";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readAdjustment(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readAdjustment()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Adjustment, CNMEA, MNMEA, MNMEASUB and NMEAGRP", "");

		    // ************** CSV Input Parameters *****************
		    Adjustment adj_master = new Adjustment();

		    // ITSM - 15932
		    // *****************************************************
		    // ********************** CSV parameters ****************
		    /*String cnmea_GMEE = "";
		    String cnmea_GMEF = "";
		    String cnmea_LMEE = "";
		    String cnmea_LMEF = "";
		    String cnmea_NMEA = "";*/
		    String cnmea_str_id = "";
		    String cnmea_account = "";

		    // ******************************************************
		    String nmeagrp_str_id = "";
		    String nmeagrp_account = "";

		    // ********************** CSV parameters ****************
		    /*String mnmea_GMEE = "";
		    String mnmea_GMEF = "";
		    String mnmea_LMEE = "";
		    String mnmea_LMEF = "";
		    String mnmea_NMEA = "";*/
		    String mnmea_rerun_id = "";

		    // mnmeasub_interval as String = ""
		    // mnmea_run_type as String = ""
		    // mnmea_sett_date as String = "" 
		    // ******************************************************
		    // ********************** CSV parameters ****************
		    /*String mnmeasub_GMEE = "";
		    String mnmeasub_GMEF = "";
		    String mnmeasub_LMEE = "";
		    String mnmeasub_LMEF = "";
		    String mnmeasub_NMEA = "";*/

		    // mnmeasub_rerun_id as String = ""
		    // mnmea_str_id as String = ""
		    // ******************************************************
		    HashMap<String, BigDecimal> gstVersions = new HashMap<String, BigDecimal>();
		    boolean nmeagrpWriteFlag = false;
		    
		    ArrayList<String> incRuns = new ArrayList<String>();

		    // ******** input parameters ***********
		    String input_str_id = params.runId;

		    // *************************************
	        //int totalPeriod = ((int) UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS"));
		    // ******** interactive parameters *****
		    String rerun_str_id = "";
		    String version = runPackage.standingVersion;

		    // ******** Queries used in this Method ********
		    // Rerun Query
		    String sqlRerun = "SELECT RERUN_STR_ID FROM NEM.NEM_SETTLEMENT_RERUN_INCS WHERE STR_ID = ?";

		    // GST Query
		    String sqlGst = "select distinct gst.value, gst.VERSION from nem_settlement_results res, " + 
		    "nem_settlement_result_types srt, nem_gst_codes gst " + 
		    "where res.STR_ID = ? and res.period = 1 and res.SRT_ID = srt.ID and " + 
		    "res.SRT_VERSION = srt.VERSION and srt.GST_ID = gst.ID and srt.GST_VERSION = gst.VERSION";

		    // Accounts Query
		    // sqlAcct as String = "select ID from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=? order by SAC_TYPE"
		    // DRSAT-507, Fix to enforce EMC FEE account is always on top row
		    String sqlAcct = "select ID,gst_registered from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=? and external_id = 'EMC FEE_A' " + 
		    "union all " + 
		    "select ID,gst_registered from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=? and  external_id <> 'EMC FEE_A'";

		    // Settlement Resunt Query
		    String sqlSettResult = "select res.SAC_ID, res.period, srt.name ITEM_NAME, res.calculation_result, " + 
		    "res.gst_amount, gst.name GST_TYPE, gst.value, res.settlement_date, nsr.run_type " + 
		    ", sac.gst_registered " + 
		    "from nem_settlement_results res, nem_settlement_runs nsr, " + 
		    "nem_settlement_result_types srt, nem_gst_codes gst " + 
		    ", nem.nem_settlement_accounts sac " + 
		    "where res.STR_ID = ? and res.SRT_ID = srt.ID and " + 
		    "nsr.id = res.STR_ID and res.SRT_VERSION = srt.VERSION and srt.GST_ID = gst.ID and " + 
		    "srt.GST_VERSION = gst.VERSION " + 
		    "and sac.id = res.sac_id and sac.version = res.sac_version " + 
		    "order by res.SAC_ID, res.period ";

		    // ITSM 15932	
		    // *********************************************
			stmt = conn.prepareStatement(sqlRerun);
			stmt.setString(1, input_str_id);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        rerun_str_id = rs.getString(1);
		        cnmea_str_id = rerun_str_id;
		        nmeagrp_str_id = rerun_str_id;
		        mnmea_rerun_id = rerun_str_id;
		        
		        incRuns.add(rerun_str_id);
			}
			rs.close();
			stmt.close();
			
			for (String id : incRuns) {
				
		        rerun_str_id = id;
		        cnmea_str_id = rerun_str_id;
		        nmeagrp_str_id = rerun_str_id;
		        mnmea_rerun_id = rerun_str_id;
				Date temp_settlement_date = null;
				String temp_run_type = null;

				HashMap<String, HashMap<Integer, ErrorAdjustment>> sacs = new HashMap<String, HashMap<Integer, ErrorAdjustment>>();
				
				stmt = conn.prepareStatement(sqlSettResult);
				stmt.setString(1, id);
				stmt.executeQuery();
	            rs = stmt.getResultSet();

				while (rs.next()) {
	                String sac = rs.getString(1);
	                String temp_period = rs.getString(2);
	                String temp_name = rs.getString(3);
	                BigDecimal temp_calculation_result = rs.getBigDecimal(4);
	                BigDecimal temp_gst_amount = rs.getBigDecimal(5);
	                String temp_gst_name = rs.getString(6);
	                BigDecimal temp_gst_value = rs.getBigDecimal(7);
	                temp_settlement_date = rs.getDate(8);
	                temp_run_type = rs.getString(9);
	                String rs_adj_account_id_taxable = rs.getString(10);
					
	                HashMap<Integer, ErrorAdjustment> pds = sacs.get(sac);
	                if (pds == null) {
	                	pds = new HashMap<Integer, ErrorAdjustment>();
	                	sacs.put(sac, pds);
	                }
	                
	                ErrorAdjustment ea = pds.get(Integer.parseInt(temp_period));
	                if (ea == null) {
	                	ea = new ErrorAdjustment();
	                	pds.put(Integer.parseInt(temp_period), ea);
	                }
	                ea.settDate = temp_settlement_date;
	                ea.runType = temp_run_type;
	                ea.taxable = rs_adj_account_id_taxable;
	                
	                if (temp_name.equals("GMEE")) {
	                    ea.gmee = temp_calculation_result;
	                }

	                if (temp_name.equals("GMEF")) {
	                	ea.gmef = temp_calculation_result;
	                }

	                if (temp_name.equals("LMEE")) {
	                	ea.lmee = temp_calculation_result;
	                }

	                if (temp_name.equals("LMEF")) {
	                	ea.lmef = temp_calculation_result;
	                }

	                if (temp_name.equals("NMEA")) {
	                	ea.nmea = temp_calculation_result;
	                }

	                ea.gstRate = temp_gst_value;

	                if (temp_name.equals("GMEF") && temp_gst_name.indexOf("A") == 0) {
	                	ea.aGmef = temp_gst_amount;
	                }

	                if (temp_name.equals("LMEE") && temp_gst_name.indexOf("A") == 0) {
	                	ea.aLmee = temp_gst_amount;
	                }

	                if (temp_name.equals("LMEF") && temp_gst_name.indexOf("A") == 0) {
	                	ea.aLmef = temp_gst_amount;
	                }

	                if (temp_name.equals("NMEA") && temp_gst_name.indexOf("A") == 0) {
	                	ea.aNmea = temp_gst_amount;
	                }

	                if (temp_name.equals("GMEE") && temp_gst_name.indexOf("V") == 0) {
	                	ea.vGmee = temp_gst_amount;
	                }

	                if (temp_name.equals("NMEA") && temp_gst_name.indexOf("V") == 0) {
	                	ea.vNmea = temp_gst_amount;
	                }
				}
				rs.close();
				stmt.close();

		        // get the GST rate
		        BigDecimal gst_rate = null;
		        PreparedStatement stmt1 = conn.prepareStatement(sqlGst);
		        stmt1.setString(1, rerun_str_id);
		        stmt1.executeQuery();
				ResultSet rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            gst_rate = rs1.getBigDecimal(1);
		            String rs_adj_version = rs1.getString(2);
		            
		            adj_master.setVersion(rs_adj_version);
		            nmeagrpWriteFlag = false;

		            if (gstVersions.containsKey(rs_adj_version) == false) {
		                gstVersions.put(rs_adj_version, gst_rate);
		                nmeagrpWriteFlag = true;
		            }
		        }
				rs1.close();
				stmt1.close();

		        if (gst_rate == null) {
		            logger.error(logPrefix + "GST Rate cannot find for Rerun ID: " + rerun_str_id);

		            throw new SettlementRunException("GST Rate cannot find for Rerun ID: " + rerun_str_id);
		        }

		        // sqlCommand1 as String
		        // get all accounts' id
		        stmt1 = conn.prepareStatement(sqlAcct);
		        stmt1.setString(1, version);
		        stmt1.setString(2, version);
		        stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
					adj_master.setAdjRequired(false);
		            String account_id = rs1.getString(1);
		            cnmea_account = account_id;
		            nmeagrp_account = account_id;
		            String txabl = rs1.getString(2);	// fix for sac don't have rerun records yet, problem is actually at rerun
		            
		            //HashMap<String, ErrorAdjustment> intervals = new HashMap<String, ErrorAdjustment>();
		            HashMap<Integer, ErrorAdjustment> intervals = sacs.get(account_id);
		            
		            if (intervals == null || intervals.size() == 0) {
		            	intervals = new HashMap<Integer, ErrorAdjustment>();
			            for (int index = 1; index <= totalPeriod; index++) {
			            	ErrorAdjustment ea = new ErrorAdjustment();
			            	ea.settDate = temp_settlement_date;
			                ea.runType = temp_run_type;
							// SATSHARP-238
			            	ea.taxable = txabl;	//"N"; // Follow readRerun() logic
							// End SATSHARP-238
			                intervals.put(index, ea);
			            }
		            	adj_master.setAdjRequired(false);
		            } else if (intervals.size() > 0) {
		            	adj_master.setAdjRequired(true);
		            }
	                adj_master.setName(account_id);
	    		    adj_master.setSettId(rerun_str_id);

		            // sqlCommand2 as String
		            // get the gst value
		            /*PreparedStatement stmt2 = conn.prepareStatement(sqlResult);
		            stmt2.setString(1, rerun_str_id);
		            stmt2.setString(2, account_id);
		            stmt2.executeQuery();
		            ResultSet rs2 = stmt2.getResultSet();

					while (rs2.next()) {
						adj_master.setAdjRequired(true);
		                String temp_period = rs2.getString(1);
		                String temp_name = rs2.getString(2);
		                BigDecimal temp_calculation_result = rs2.getBigDecimal(3);
		                BigDecimal temp_gst_amount = rs2.getBigDecimal(4);
		                String temp_gst_name = rs2.getString(5);
		                BigDecimal temp_gst_value = rs2.getBigDecimal(6);
		                Date temp_settlement_date = rs2.getDate(7);
		                String temp_run_type = rs2.getString(8);

		                if (temp_name.equals("GMEE")) {
		                    intervals.get(temp_period).gmee = temp_calculation_result;
		                }

		                if (temp_name.equals("GMEF")) {
		                	intervals.get(temp_period).gmef = temp_calculation_result;
		                }

		                if (temp_name.equals("LMEE")) {
		                	intervals.get(temp_period).lmee = temp_calculation_result;
		                }

		                if (temp_name.equals("LMEF")) {
		                	intervals.get(temp_period).lmef = temp_calculation_result;
		                }

		                if (temp_name.equals("NMEA")) {
		                	intervals.get(temp_period).nmea = temp_calculation_result;
		                }

		                intervals.get(temp_period).gstRate = temp_gst_value;

		                if (temp_name.equals("GMEF") && temp_gst_name.indexOf("A") == 0) {
		                	intervals.get(temp_period).aGmef = temp_gst_amount;
		                }

		                if (temp_name.equals("LMEE") && temp_gst_name.indexOf("A") == 0) {
		                	intervals.get(temp_period).aLmee = temp_gst_amount;
		                }

		                if (temp_name.equals("LMEF") && temp_gst_name.indexOf("A") == 0) {
		                	intervals.get(temp_period).aLmef = temp_gst_amount;
		                }

		                if (temp_name.equals("NMEA") && temp_gst_name.indexOf("A") == 0) {
		                	intervals.get(temp_period).aNmea = temp_gst_amount;
		                }

		                adj_master.setName(account_id);
		                adj_master.setRunType(temp_run_type);
		                
		    		    GregorianCalendar calender = new GregorianCalendar();
		    		    calender.setTime(temp_settlement_date);
		    		    adj_master.setSettDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));

		                // rs_adj_sett_date = String.valueOf(temp_settlement_date)
		    		    adj_master.setSettId(rerun_str_id);

		                if (temp_name.equals("GMEE") && temp_gst_name.indexOf("V") == 0) {
		                	intervals.get(temp_period).vGmee = temp_gst_amount;
		                }

		                if (temp_name.equals("NMEA") && temp_gst_name.indexOf("V") == 0) {
		                	intervals.get(temp_period).vNmea = temp_gst_amount;
		                }

		                // ITSM 15932 - Start
		                String rs_adj_account_id_taxable = rs2.getString(9);

		                adj_master.setTaxable(rs_adj_account_id_taxable.equalsIgnoreCase("Y"));

		                // ITSM 15932 - End					
		                //gst_count = gst_count + 1;
		            }
					rs2.close();
					stmt2.close();*/

		            for (int period = 1; period <= totalPeriod; period++) {
		            	
		                adj_master.setRunType(intervals.get(period).runType);
		    		    GregorianCalendar calender = new GregorianCalendar();
		    		    calender.setTime(intervals.get(period).settDate);
		    		    adj_master.setSettDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));
		                adj_master.setTaxable(intervals.get(period).taxable.equalsIgnoreCase("Y"));
		                
		            	Adjustment adj = new Adjustment(adj_master);
		            	
		                /*String[] columnResults = { 
		                		String.valueOf(o : intervals[interval].rs_adj_gmee), 
		                		String.valueOf(o : intervals[interval].rs_adj_gmef), 
		                		String.valueOf(o : intervals[interval].rs_adj_lmee), 
		                		String.valueOf(o : intervals[interval].rs_adj_lmef), 
		                		String.valueOf(o : intervals[interval].rs_adj_nmea), 
		                		gst_rate, 
		                		String.valueOf(o : intervals[interval].rs_adj_a_gmef), 
		                		String.valueOf(o : intervals[interval].rs_adj_a_lmee), 
		                		String.valueOf(o : intervals[interval].rs_adj_a_lmef), 
		                		String.valueOf(o : intervals[interval].rs_adj_a_nmea), 
		                		account_id, 
		                		rs_adj_run_type, 
		                		rs_adj_sett_date, 
		                		rs_adj_str_id, 
		                		rs_adj_total_GMEE, 
		                		rs_adj_total_GMEF, 
		                		rs_adj_total_LMEE, 
		                		rs_adj_total_LMEF, 
		                		rs_adj_total_NMEA, 
		                		String.valueOf(o : intervals[interval].rs_adj_v_gmee), 
		                		String.valueOf(o : intervals[interval].rs_adj_v_nmea), 
		                		interval, 
		                		rs_adj_required, 
		                		rs_adj_version, 
		                		String.valueOf(o : rs_adj_gst_taxable) };*/
		                adj.setGmee(intervals.get(period).gmee);
		                adj.setGmef(intervals.get(period).gmef);
		                adj.setLmee(intervals.get(period).lmee);
		                adj.setLmef(intervals.get(period).lmef);
		                adj.setNmea(intervals.get(period).nmea);
		                adj.setGstRate(gst_rate);
		                adj.setOpGstGmef(intervals.get(period).aGmef);
		                adj.setOpGstLmee(intervals.get(period).aLmee);
		                adj.setOpGstLmef(intervals.get(period).aLmef);
		                adj.setOpGstNmea(intervals.get(period).aNmea);
		                adj.setTotalGmee(null);
		                adj.setTotalGmef(null);
		                adj.setTotalLmee(null);
		                adj.setTotalLmef(null);
		                adj.setTotalNmea(null);
		                adj.setIpGstGmee(intervals.get(period).vGmee);
		                adj.setIpGstNmea(intervals.get(period).vNmea);
		            	adj.setPeriodId(String.valueOf(period));
		                //adj.setAccountId(account_id);
		                //adj.setRunType(value);
		                //csvWriter.writeLine(values : columnResults);
		            	
		            	data.getAdjustment().put(adj.getKey(), adj);
		    	    	
		            	// add it to....
		    	    	Rerun rr = data.getRerun().get(adj.getName()+adj.getGstRate().toString()+(adj.isTaxable() == true? "1": "0"));
		    	    	if (rr != null ) {
		    	    		rr.getAdjustments().add(adj);
		    	    	}
		    	    	data.getPeriod().get(adj.getName()+adj.getPeriodId()).getAdjustments().add(adj);
		            }

		            //String[] cnmeaResults = { cnmea_GMEE, cnmea_GMEF, cnmea_LMEE, cnmea_LMEF, cnmea_NMEA, cnmea_str_id, cnmea_account };
		            //csvWriter1.writeLine(values : cnmeaResults);
		            Cnmea cnmea = new Cnmea();
		            cnmea.setGmee(null);
		            cnmea.setGmef(null);
		            cnmea.setLmee(null);
		            cnmea.setLmef(null);
		            cnmea.setNmea(null);
		            cnmea.setSettId(cnmea_str_id);
		            cnmea.setAccountId(cnmea_account);
	            	data.getCnmea().put(cnmea.getKey(), cnmea);

		            if (nmeagrpWriteFlag) {
		                for (int i = 1; i <= totalPeriod; i++) {
		                    //String[] nmeagrpResults = { cnmea_GMEE, cnmea_GMEF, cnmea_LMEE, cnmea_LMEF, interval, nmeagrp_str_id, nmeagrp_account, rs_adj_version, rs_adj_required, "", "", "", "", "", "" };
		                    //nmeagrpCsvWriter.writeLine(values : nmeagrpResults);
		                	CnmeaSettRe grp = new CnmeaSettRe();
		                    grp.setGmee(null);
		                    grp.setGmef(null);
		                    grp.setLmee(null);
		                    grp.setLmef(null);
		                    grp.setPeriodId(String.valueOf(i));
		                    grp.setSettId(nmeagrp_str_id);
		                    grp.setAccountId(nmeagrp_account);
		                    grp.setVersion(adj_master.getVersion());
		                    grp.setNmeaRequired(adj_master.isAdjRequired());
		                    
		                    data.getCnmeaSettRe().put(grp.getKey(), grp);
		                }

		                //gstVersions.extend(rs_adj_version); // TODO: need to find out why
		            }

		            intervals.clear();

		            //account_count = account_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        for (int i = 1; i <= totalPeriod; i++) {
		            //String[] mnmeaResults = { mnmea_GMEE, mnmea_GMEF, mnmea_LMEE, mnmea_LMEF, mnmea_NMEA, mnmea_rerun_id, interval, rs_adj_run_type, rs_adj_sett_date };
		            //csvWriter2.writeLine(values : mnmeaResults);
		            
		            Mnmea mn = new Mnmea();
		            mn.setSettId(mnmea_rerun_id);
		            mn.setPeriodId(String.valueOf(i));
		            mn.setRunType(adj_master.getRunType());
		            mn.setSettDate(adj_master.getSettDate());
		            
		            data.getMnmea().put(mn.getKey(), mn);
		        }

		        //String[] mnmeasubResults = { mnmeasub_GMEE, mnmeasub_GMEF, mnmeasub_LMEE, mnmeasub_LMEF, mnmeasub_NMEA, mnmea_rerun_id, input_str_id };
		        //csvWriter3.writeLine(values : mnmeasubResults);
		        
		        MnmeaSub sub = new MnmeaSub();
		        sub.setRerunId(mnmea_rerun_id);
		        sub.setSettId(input_str_id);
		        // SATSHARP-245, for sorting
    		    GregorianCalendar calender = new GregorianCalendar();
    		    calender.setTime(temp_settlement_date);
    		    sub.setSettDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calender));
		        // end SATSHARP-245
    		    // PROD1SHARP-40, add to allow records to be sorted as per production NMEA report
    		    sub.setRunType(temp_run_type);
		        
		        data.getMnmeaSub().put(sub.getKey(), sub);

		        //rerun_count = rerun_count + 1;

		        // logMessage "the gst_count is " + gst_count using severity = WARNING
		    }
			
			Map<String, Adjustment> adjustments = data.getAdjustment();
			for (Map.Entry<String, Adjustment> entry : adjustments.entrySet()) {
				
				Adjustment adj = entry.getValue();
		    	data.getMnmeaSub().get(input_str_id+adj.getSettId()).getAdjustments().add(adj);
		    	data.getCnmea().get(adj.getSettId()+adj.getName()).getAdjustments().add(adj);
		    	data.getCnmeaSettRe().get(adj.getName()+adj.getPeriodId()+adj.getVersion()).getAdjustments().add(adj);
		    	data.getMnmea().get(adj.getSettId()+adj.getPeriodId()).getAdjustments().add(adj);
			}
			
	        String msg = "Successfully Generated Entity CSV File: Adjustment and CNMEA";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readMarket(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readMarket()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);

		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Market", "");


		    // ************** CSV Input Parameters *****************
		    // [EG]
		    // CSZ as String = "5"
	        // get the cutoff size
		    //BigDecimal csz = BigDecimal.valueOf(UtilityFunctions.getSysParamNum(ds, "CMWH"));

		    // 7.1.01
		    // ******** Queries used in this Method *******
		    // sqlPrice as String = "Select PRICE, PERIOD from NEM.NEM_SETTLEMENT_PRICES " + 
		    // "where version = ? and settlement_date = ? and PRICE_TYPE = ? ORDER BY PERIOD "
		    // 7.1.01
		    String sqlPrice = "SELECT price.settlement_date settlement_date, price.price price, price.period period, wmep.wmep wmep FROM " + 
		    "(Select pr.settlement_date, pr.PRICE, pr.PERIOD from NEM.NEM_SETTLEMENT_PRICES pr where pr.version = ? and pr.settlement_date = ? and pr.PRICE_TYPE = ? ) price, " + 
		    "(select ? settlement_date, prd.period_number period, nvl(settprice1.price,0) WMEP from ( " + 
		    "select distinct settprice.settlement_date, settprice.period, settprice.price  from nem.nem_settlement_prices settprice,  nem.nem_nodes nde, nem.nem_facilities fct, nem.nem_generation_types gnt where  settprice.version = ? " + 
		    "and settprice.settlement_date = ?  and settprice.nde_id = nde.id and settprice.nde_version= nde.version " + 
		    "and nde.version= ? and settprice.nde_id is not null and settprice.PRICE_TYPE = ? and nde.ID=fct.nde_id and nde.version = fct.nde_version and fct.gnt_id=gnt.id and fct.gnt_version = gnt.version and gnt.generation_sub_type = ?) settprice1, nem.def_periods prd " + 
		    " where settprice1.period (+) =prd.period_number ) wmep " + 
		    "WHERE   price.settlement_date = wmep.Settlement_date AND price.period = wmep.period ORDER BY price.period ";

		    // ********************************************
		    //market_str_id = params.runId;

		    // get the USP
			stmt = conn.prepareStatement(sqlPrice);
			stmt.setString(1, runPackage.mcPricePkgVer);
			//stmt.setString(2, qdf.format(params.sqlSettlementDate));
			stmt.setString(2, sqlsd);
			stmt.setString(3, "USP");
			//stmt.setString(4, qdf.format(params.sqlSettlementDate));     // 7.1.01
			stmt.setString(4, sqlsd);     // 7.1.01
			stmt.setString(5, runPackage.mcPricePkgVer);     // 7.1.01
			//stmt.setString(6, qdf.format(params.sqlSettlementDate));     // 7.1.01
			stmt.setString(6, sqlsd);     // 7.1.01
			stmt.setString(7, runPackage.standingVersion);     // 7.1.01
			stmt.setString(8, "MEP");     // 7.1.01
			stmt.setString(9, "AGGREGATE");     // 7.1.01
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        // market_USEP = Decimal.valueOf(value : row[1])    //7.1.01
				BigDecimal market_USEP = rs.getBigDecimal(2);           // 7.1.01
		        // market_number = Decimal.valueOf(value : row[2])  //7.1.01
		        String market_number = rs.getString(3);         // 7.1.01
		        BigDecimal market_wmep = rs.getBigDecimal(4);         // 7.1.01
		        //logMessage("Rahul here - market_number" + market_number + " - market_USEP " + market_USEP + " - market_wmep " + market_wmep, severity : WARNING);

		        // 7.1.01
		        //only for IGS changes onwards set WMEP flag
		        /*String[] columnResults;
		        if (haleyVersion >= 7.1) {
				        columnResults = { market_AFP, market_EGA_WEQ, market_HEUA, market_HEUC, market_PRQ, market_RSA, market_RSC, market_SRQ, market_TRQ, market_TTE, market_WEQ, String.valueOf(o : market_number), market_pcu_nodes, market_str_id, market_MEUC, String.valueOf(o : market_USEP), market_VRCP, market_FEQ, market_FSC, market_VCSC, market_HQ, String.valueOf(o : cmwh), market_WMQ// [EG]
				        , String.valueOf(o : market_wmep) };        
		        }
		        else {        	
				        columnResults = { market_AFP, market_EGA_WEQ, market_HEUA, market_HEUC, market_PRQ, market_RSA, market_RSC, market_SRQ, market_TRQ, market_TTE, market_WEQ, String.valueOf(o : market_number), market_pcu_nodes, market_str_id, market_MEUC, String.valueOf(o : market_USEP), market_VRCP, market_FEQ, market_FSC, market_VCSC, market_HQ, String.valueOf(o : cmwh), market_WMQ// [EG]
				        };        	
		        }
		        // 7.1.01
		        csvWriter.writeLine(values : columnResults);*/
		        
		        Market mkt = new Market();
		        mkt.setPeriodId(market_number);
		        mkt.setRunId(params.runId);
		        mkt.setUsep(market_USEP);
		        mkt.setWmep(market_wmep);
		        
				mkt.setMeuc(data.getGlobal().getMeuc());
		        
		        data.getMarket().put(mkt.getKey(), mkt);
		    }
			rs.close();
			stmt.close();
			
	        logger.info("Number of Market records = " + data.getMarket().size());

	        String msg = "Successfully Generated Entity CSV File: Market";
	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readRerun(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readRerun()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
                    msgStep, "Generating Entity CSV File: ReRun and MP", "");

			// ************** CSV Input Parameters *****************
			BigDecimal rerun_gst_rate;
			int rerun_gst_taxable = 0;
			
			// ITSM - 15932 Added GST Flag
			Date settlement_date;
			
			// ITSM - 15932 Added GST Flag
			// *****************************************************
			// DRCAP implementation - PTP_ID
			String account_mp = "";
			
			// ******** input parameters ***********
			HashMap<String, MarketParticipantType> ptp_list = new HashMap<String, MarketParticipantType>();
			String rerun_str_id = "";
			String version = runPackage.standingVersion;
			HashMap<String, BigDecimal> accountList = new HashMap<String, BigDecimal>();
			
			// ******** Queries used in this Method *******
			// Rerun Query
			// ITSM 15932 Changes
			// sqlRerun as String = "SELECT RERUN_STR_ID FROM NEM.NEM_SETTLEMENT_RERUN_INCS WHERE STR_ID = ?"
			String sqlRerun = "SELECT str.ID, str.SETTLEMENT_DATE FROM NEM.NEM_SETTLEMENT_RUNS str, NEM.NEM_SETTLEMENT_RERUN_INCS str_incs " + 
					"WHERE str_incs.RERUN_STR_ID = str.ID AND STR_ID = ? ";
			
			// END - ITSM 15932 Changes	                   
			// Account Query
			String sqlAcct = "select ID,EXTERNAL_ID,PTP_ID,GST_REGISTERED from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=?";
			
			// ITSM 15932 Changes
			String sqlRerunAcct = "SELECT sac.ID, sac.GST_REGISTERED FROM NEM.NEM_SETTLEMENT_ACCOUNTS sac " + 
					"WHERE sac.ID = ? AND sac.VERSION = ? ";
			
			// END - ITSM 15932 Changes	                   
			// Settlemen Result Query
			String sqlResult = "select distinct gst.value " + 
					"from nem_settlement_results res, nem_settlement_result_types srt, " + 
					"nem_gst_codes gst where res.STR_ID = ? and " + 
					"srt.NAME in ('GMEE','GMEF','LMEE','LMEF') and " + 
					"res.SRT_ID = srt.ID and res.SRT_VERSION = srt.VERSION and " + 
					"srt.GST_ID = gst.ID and srt.GST_VERSION = gst.VERSION";
			
			// *******************************************
			// sqlCommand = "SELECT RERUN_STR_ID FROM NEM.NEM_SETTLEMENT_RERUN_INCS WHERE STR_ID = ?"
			stmt = conn.prepareStatement(sqlRerun);
			stmt.setString(1, params.runId);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
				rerun_str_id = rs.getString(1);
				settlement_date = rs.getDate(2);
		        
				// ITSM - 15932 Added GST Flag
				
				// sqlCommand1 as String
				// get all accounts' id
		        PreparedStatement stmt1 = conn.prepareStatement(sqlAcct);
		        stmt1.setString(1, version);
		        stmt1.executeQuery();
				ResultSet rs1 = stmt1.getResultSet();
				
				while (rs1.next()) {
					
					// SATSHARP-238
					String taxi_flag = rs1.getString(4);
					// default value according to the sac in current run version
					if (taxi_flag.equalsIgnoreCase("Y")) {
					    rerun_gst_taxable = 1;
					} else {
					    rerun_gst_taxable = 0;
					}
					// End SATSHARP-238
					
					String account_id = rs1.getString(1);
					String external_id = rs1.getString(2);
					account_mp = rs1.getString(3);
					MarketParticipantType ptp = new MarketParticipantType();
					
					if (ptp_list.containsKey(account_mp) == false) {
						ptp.isEMC = external_id.equals("EMC FEE_A");
						
						ptp.isPSO = external_id.equals("PWR SYS_O");
						
						ptp.isMSSL = external_id.equals("SP SVCS_M");
					} else {
						 // for special accounts, set to true if not done yet
						 if (external_id.equals("EMC FEE_A")) {
							 ptp_list.get(account_mp).isEMC = true;
						 }
						
						 if (external_id.equals("PWR SYS_O")) {
							 ptp_list.get(account_mp).isPSO = true;
						 }
						
						 if (external_id.equals("SP SVCS_M")) {
							 ptp_list.get(account_mp).isMSSL = true;
						 }
					}
					
					// get the rerun_gst_name
					
					// 	    	if account_id == null then
					// 	    		rerun_gst_name = ""
					// 	    	end
					// ITSM - 15932 Added GST Flag
					String rerun_ver = PavPackage.getStandingVersion(ds, settlement_date);
			        PreparedStatement stmt2 = conn.prepareStatement(sqlRerunAcct);
			        stmt2.setString(1, account_id);
			        stmt2.setString(2, rerun_ver);
			        stmt2.executeQuery();
					ResultSet rs2 = stmt2.getResultSet();
					
					// ITSM - 15932 Added GST Flag
					while (rs2.next()) {
						 String account_id_taxable = rs2.getString(2);
						
						 if (account_id_taxable.equalsIgnoreCase("Y")) {
						     rerun_gst_taxable = 1;
						 } else {
						     rerun_gst_taxable = 0;
						 }
					}
					rs2.close();
					stmt2.close();
					
					// End - ITSM - 15932 Added GST Flag
					ptp.mpTaxable = (rerun_gst_taxable == 1);
					
					// sqlCommand2 as String
					// get the gst value
			        stmt2 = conn.prepareStatement(sqlResult);
			        stmt2.setString(1, rerun_str_id);
			        stmt2.executeQuery();
					rs2 = stmt2.getResultSet();
					
					while (rs2.next()) {
						 rerun_gst_rate = rs2.getBigDecimal(1);
						 ptp.gstRate = rerun_gst_rate;
						
						 // if accountList[account_id + String.valueOf(rerun_gst_rate)] == null then           // ITSM - 15932 
						 // accountList[account_id + String.valueOf(rerun_gst_rate)] = rerun_gst_rate      // ITSM - 15932 Added GST Flag
						 if (accountList.containsKey(account_id + String.valueOf(rerun_gst_rate) + String.valueOf(rerun_gst_taxable)) == false) {
						     // ITSM - 15932 Added GST Flag
						     accountList.put(account_id + String.valueOf(rerun_gst_rate) + String.valueOf(rerun_gst_taxable), rerun_gst_rate);
						
						     // ITSM - 15932 Added GST Flag	    			
						     /*String[] columnResults = { rerun_gst_name, String.valueOf(o : rerun_gst_rate), rerun_gst_a_inc_GMEF, rerun_gst_a_inc_LMEE, rerun_gst_a_inc_LMEF, rerun_gst_a_inc_NMEA, rerun_gst_v_inc_GMEE, rerun_gst_v_inc_NMEA, rerun_gst_inc_GMEE, rerun_gst_inc_GMEF, rerun_gst_inc_LMEE, rerun_gst_inc_LMEF, rerun_gst_inc_NMEA, rerun_gst_total_inc_GMEE, rerun_gst_total_inc_GMEF, rerun_gst_total_inc_LMEE, rerun_gst_total_inc_LMEF, rerun_gst_total_inc_NMEA, String.valueOf(o : rerun_gst_taxable) };
						     csvWriter.writeLine(values : columnResults);*/
						     
						     Rerun rerun = new Rerun();
						     rerun.setName(account_id);
						     rerun.setGstRate(rerun_gst_rate);
						     rerun.setTaxable(rerun_gst_taxable == 1);
						     
						     data.getRerun().put(rerun.getKey(), rerun);
						 }
						
						 break;
					}
					rs2.close();
					stmt2.close();
					
					if (ptp_list.containsKey(account_mp) == false) {
						 ptp_list.put(account_mp, ptp);
					}
				}
				rs1.close();
				stmt1.close();
				
			}
			rs.close();
			stmt.close();
			
			if (ptp_list.size() == 0) {
				stmt = conn.prepareStatement(sqlAcct);
				stmt.setString(1, version);
				stmt.executeQuery();
				rs = stmt.getResultSet();
				
				while (rs.next()) {
					String account_id = rs.getString(1);
					String external_id = rs.getString(2);
					account_mp = rs.getString(3);
					String account_id_taxable = rs.getString(4);
					
					if (ptp_list.containsKey(account_mp) == false) {
						 MarketParticipantType ptp = new MarketParticipantType();
						 ptp.settAccounts = account_id;
						
						 ptp.isEMC = external_id.equals("EMC FEE_A");
							
						 ptp.isPSO = external_id.equals("PWR SYS_O");
							
						 ptp.isMSSL = external_id.equals("SP SVCS_M");
							
						 ptp.mpTaxable = account_id_taxable.equalsIgnoreCase("Y");
						
						 ptp.gstRate = BigDecimal.ONE.negate();
						
						 // it means take from default
						 if (ptp_list.containsKey(account_mp) == false) {
						     ptp_list.put(account_mp, ptp);
						 }
					} else {
						 if (external_id.equals("EMC FEE_A")) {
						     ptp_list.get(account_mp).isEMC = true;
						 }
						
						 if (external_id.equals("PWR SYS_O")) {
							 ptp_list.get(account_mp).isPSO = true;
						 }
						
						 if (external_id.equals("SP SVCS_M")) {
							 ptp_list.get(account_mp).isMSSL = true;
						 }
					}
				}
				rs.close();
				stmt.close();
			}
			
			// get the entity mp information
			Iterator<Entry<String,MarketParticipantType>> itr2 = ptp_list.entrySet().iterator();
			while (itr2.hasNext()) {
				Entry<String,MarketParticipantType> entry = itr2.next();
	            String mp_id = entry.getKey();
	            MarketParticipantType ptp = entry.getValue();
				
				Participant mp = new Participant();
				mp.setParticipantId(mp_id);
				mp.setEmcAccount(ptp.isEMC);
				mp.setPsoAccount(ptp.isPSO);
				mp.setMsslAccount(ptp.isMSSL);
				mp.setTaxable(ptp.mpTaxable);
				mp.setRerunGstRate(ptp.gstRate);
				
				data.getParticipant().put(mp.getKey(), mp);
			}

			
	        String msg = "Successfully Generated Entity CSV File: ReRun and MP";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readIntervalPF(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readIntervalPF()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);

		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Inteval, Bilateral, Vesting, TVC and FSC", "");

		    // ********************** CSV parameters ****************
		    /*String interval_account = "";
		    String interval_BAQ_purchased = "";
		    String interval_BAQ_sold = "";
		    String interval_BEQ_purchased = "";
		    String interval_BEQ_sold = "";
		    String interval_BESC = "";
		    String interval_BFQ_purchased = "";
		    String interval_BFQ_sold = "";
		    String interval_BIF_purchased = "";
		    String interval_BIF_sold = "";
		    String interval_BWF_purchased = "";
		    String interval_BWF_sold = "";
		    String interval_delta_WCQ = "";
		    String interval_delta_WEQ = "";
		    String interval_comp_EMCA = "";
		    String interval_comp_PSOA = "";
		    String interval_EGA_IEQ = "";
		    String interval_EGA_WEQ = "";
		    String interval_EGA_WPQ = "";
		    String interval_EUA = "";
		    String interval_FCC = "";
		    String interval_FEQ = "";
		    String interval_FSC = "";
		    String interval_FSD = "";
		    String interval_GESC = "";
		    String interval_GESCE = "";
		    String interval_GESCN = "";
		    String interval_GESCP = "";
		    String interval_GMEE = "";
		    String interval_GMEF = "";
		    String interval_HEUA = "";
		    String interval_HEUSA = "";
		    String interval_IEQ = "";
		    String interval_IEQP = "";
		    String interval_imp_EMCA = "";
		    String interval_imp_PSOA = "";
		    String interval_inc_NMEA = "";
		    String interval_v_FSC = "";
		    String interval_v_GESC = "";
		    String interval_v_GESCN = "";
		    String interval_v_GMEE = "";
		    String interval_v_inc_NMEA = "";
		    String interval_v_LESD = "";
		    String interval_v_LESDN = "";
		    String interval_v_NASC = "";
		    String interval_v_NESC = "";
		    String interval_v_NMEA = "";
		    String interval_v_NPSC = "";
		    String interval_v_RSC = "";
		    String interval_LESD = "";
		    String interval_LESDN = "";
		    String interval_LESDP = "";
		    String interval_LMEE = "";
		    String interval_LMEF = "";
		    String interval_MEP = "";
		    String interval_MEUSA = "";
		    Decimal interval_MFP;
		    String interval_MRP = "";
		    String interval_NASC = "";
		    String interval_NEAA = "";
		    String interval_NEAD = "";
		    String interval_NEGC = "";
		    String interval_NEGC_IEQ = "";
		    String interval_NELC = "";
		    String interval_NESC = "";
		    String interval_NFSC = "";
		    String interval_NMEA = "";
		    String interval_nde_count = "";
		    String interval_NPSC = "";
		    String interval_NRSC = "";
		    String interval_NTSC = "";
		    int interval_number;
		    String interval_a_FSD = "";
		    String interval_a_GESC = "";
		    String interval_a_GESCE = "";
		    String interval_a_GESCP = "";
		    String interval_a_GMEF = "";
		    String interval_a_HEUSA = "";
		    String interval_a_inc_NMEA = "";
		    String interval_a_LESD = "";
		    String interval_a_LESDP = "";
		    String interval_a_LMEE = "";
		    String interval_a_LMEF = "";
		    String interval_a_MEUSA = "";
		    String interval_a_NASC = "";
		    String interval_a_NESC = "";
		    String interval_a_NMEA = "";
		    String interval_a_NPSC = "";
		    String interval_a_RSD = "";
		    String interval_RCC = "";
		    String interval_RSA = "";
		    String interval_RSC = "";
		    String interval_RSD = "";
		    String interval_max_IEQ = "";
		    String interval_total_BESC = "";
		    String interval_total_FSC = "";
		    String interval_total_FSD = "";
		    String interval_total_GESC = "";
		    String interval_total_GESCE = "";
		    String interval_total_GESCN = "";
		    String interval_total_GESCP = "";
		    String interval_total_GMEE = "";
		    String interval_total_GMEF = "";
		    String interval_total_HEUSA = "";
		    String interval_total_LESD = "";
		    String interval_total_LESDN = "";
		    String interval_total_LESDP = "";
		    String interval_total_LMEE = "";
		    String interval_total_LMEF = "";
		    String interval_total_MEUSA = "";
		    String interval_total_NASC = "";
		    String interval_total_NESC = "";
		    String interval_total_NFSC = "";
		    String interval_total_NMEA = "";
		    String interval_total_NPSC = "";
		    String interval_total_NRSC = "";
		    String interval_total_inc_NMEA = "";
		    String interval_total_RCC = "";
		    String interval_total_RSC = "";
		    String interval_total_RSD = "";
		    String interval_TTE = "";
		    String interval_USEP = "";
		    String interval_VCRP = "";
		    String interval_VCRPK = "";
		    String interval_VCSC = "";
		    String interval_VCSCK = "";
		    Decimal interval_WCQ;
		    Decimal interval_WEQ;
		    Decimal interval_WPQ;
		    Decimal interval_WFQ;

		    // [EG]
		    Decimal interval_WMQ;

		    // [EG]
		    String interval_delta_WFQ = "";

		    // [EG]
		    String interval_delta_WMQ = "";

		    // [EG]
		    String interval_facility_RSC = "";
		    String interval_GMEA = "";
		    String interval_LMEA = "";
		    String interval_a_LMEA = "";
		    String interval_a_GMEA = "";
		    String interval_EGA_id = "";
		    String interval_FEE_TOTAL = "";
		    String interval_imp_AFP = "";
		    String interval_imp_HEUC = "";
		    String interval_imp_MEUC = "";
		    String interval_imp_USEP = "";
		    Decimal temp_interval_IEQ;
		    String interval_gen_node = "0";

		    // 8.0.01 Changes    
		    // DRCAP
		    String interval_delta_WDQ = "";
		    Decimal interval_LCP;
		    Decimal interval_WDQ;
		    String interval_imp_HEUR = "";
		    String interval_imp_HLCU = "";*/

		    // ******************************************************
		    /*Java.Io.FileWriter fileWriter = FileWriter(arg1 : dataInputDir + "\\interval.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter = CSVWriter(writer : fileWriter);
		    Java.Io.FileWriter fileWriter1 = FileWriter(arg1 : dataInputDir + "\\bilateral.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter1 = CSVWriter(writer : fileWriter1);
		    Java.Io.FileWriter fileWriter2 = FileWriter(arg1 : dataInputDir + "\\vesting.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter2 = CSVWriter(writer : fileWriter2);
		    Java.Io.FileWriter fileWriter3 = FileWriter(arg1 : dataInputDir + "\\tvc.csv");

		    // [ITSM-12670]
		    CSV4J.Net.Sf.Csv4j.CSVWriter tvcWriter = CSVWriter(writer : fileWriter3);

		    // [ITSM-16708] Added for FSC Implementation
		    Java.Io.FileWriter fileWriter4 = FileWriter(arg1 : dataInputDir + "\\fsc.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter4 = CSVWriter(writer : fileWriter4);*/

		    // ************** CSV Input Parameters For Forward Sales Contract *****************
		    /*Decimal fsc_price;
		    Decimal fsc_quantity;
		    String fsc_id = "";
		    String fsc_externalId = "";
		    String fsc_selling_id = "";*/
		    String fsc_purchasing_id = "";
		    /*int fsc_interval;
		    String fsc_contract_type = "";*/

		    // FSC implementation place holder to store fsc data
		    HashMap<String, ForwardSalesContract> contractList_FSC = new HashMap<String, ForwardSalesContract>();
		    HashMap<String, String> accountList_fsc = new HashMap<String, String>();
		    HashMap<String, String> breachAccountList_fsc = new HashMap<String, String>();
		    
		    // ******** interactive parameters******
		    String version = runPackage.standingVersion;
		    //String node_id = "";
		    String account_id = "";
		    //String acg_id = "";

		    // ************** CSV Input Parameters For Vesting Contract *****************
		    BigDecimal mssl_HP = BigDecimal.ZERO;
		    /*Decimal vesting_HP;
		    Decimal vesting_HQ;
		    String vesting_name = "";
		    String vesting_selling_id = "";*/
		    String vesting_purchasing_id = "";
		    /*int vesting_interval;
		    String contract_type = "";*/

		    // [ITSM-12670]
		    // **************************************************************************
		    HashMap<String, VestingContract> contractList_vesting = new HashMap<String, VestingContract>();
		    HashMap<String, VestingContract> contractList_tvc = new HashMap<String, VestingContract>();

		    // [ITSN-12670]
		    HashMap<String, BilateralContract> contractList_bilateral = new HashMap<String, BilateralContract>();
		    HashMap<String, String> sellerList_bilateral = new HashMap<String, String>();
		    HashMap<String, String> buyerList_bilateral = new HashMap<String, String>();
		    HashMap<String, String> accountList_vesting = new HashMap<String, String>();

		    // [ITSN-15086]
		    String vesting_id = "";

		    // ******* Queries used in this Method *******
		    // Vesting Contract Query [ITSM-12670]
		    /* 5.1.01 Modify the vesting contract query to cater 120 tranches	
		            	sqlVest as String = "SELECT ID, EXTERNAL_ID, SAC_PURCHASED_ID, SAC_SOLD_ID, CONTRACT_TYPE "
		             		+ "FROM NEM_VESTING_CONTRACTS vc WHERE vc.settlement_date = ? "
		            		+ "AND vc.created_date = (SELECT MAX (vc1.created_date) "
		            		+ "FROM NEM_VESTING_CONTRACTS vc1 WHERE vc1.sac_sold_id = vc.sac_sold_id "
		            		+ "AND vc1.created_date <= ? AND vc1.settlement_date = ?) "
		            		+ "AND SAC_SOLD_ID IN (SELECT DISTINCT(nde.SAC_ID) "  
		                    + "FROM NEM_NODES nde "
		                    + "WHERE nde.VERSION = ?)"*/
		    // [BEGIN] 5.1.01 Modify the vesting contract query to cater 120 tranches 
		    /*	
		            	sqlVest as String = "SELECT vc.ID, vc.EXTERNAL_ID, vc.SAC_PURCHASED_ID, vc.SAC_SOLD_ID, vc.CONTRACT_TYPE, substr(vc.external_id,-3,3) tranchID "
		                     + "FROM NEM.NEM_VESTING_CONTRACTS vc WHERE vc.settlement_date = ?   AND vc.contract_type <> 'TEQ' "
		                     + "AND (vc.sac_sold_id, vc.contract_type, vc.created_date) in (SELECT vc1.sac_sold_id, vc1.contract_type, MAX (vc1.created_date) "
		                     + "FROM NEM.NEM_VESTING_CONTRACTS vc1 WHERE   vc1.contract_type <> 'TEQ' "
		                     + "AND vc1.settlement_date = ? "
		                     + "AND vc1.created_date <= ? "
		                     + "group by vc1.sac_sold_id, vc1.contract_type ) "
		                     + "AND SAC_SOLD_ID IN (SELECT DISTINCT(nde.SAC_ID) "
		                     + "FROM NEM.NEM_NODES nde "
		                     + "WHERE nde.VERSION = ?) "
		            	+ "union all "
		            	+ "SELECT vc.ID, vc.EXTERNAL_ID, vc.SAC_PURCHASED_ID, vc.SAC_SOLD_ID, vc.CONTRACT_TYPE, substr(vc.external_id,-3,3) tranchID "
		                     + "FROM NEM.NEM_VESTING_CONTRACTS vc WHERE vc.settlement_date = ?   AND vc.contract_type = 'TEQ' "
		                     + "AND (substr(vc.external_id,-3,3), vc.sac_sold_id, vc.contract_type, vc.created_date) in "
		                     + "(SELECT substr(vc1.external_id,-3,3), vc1.sac_sold_id, vc1.contract_type, MAX (vc1.created_date) "
		                     + "FROM NEM.NEM_VESTING_CONTRACTS vc1 WHERE   vc1.contract_type = 'TEQ' "
		                     + "AND vc1.settlement_date = ? "
		                     + "AND vc1.created_date <= ? "
		                     + "group by vc1.sac_sold_id, vc1.contract_type, substr(vc1.external_id,-3,3) ) "
		                     + "AND SAC_SOLD_ID IN (SELECT DISTINCT(nde.SAC_ID) "
		                     + "FROM NEM.NEM_NODES nde "
		                     + "WHERE nde.VERSION = ?) "*/
		    // UATSHARP-105, remove the check on "vc1.created_date <= ?"
		    String sqlVest = "SELECT vc.ID, vc.EXTERNAL_ID, vc.SAC_PURCHASED_ID, vc.SAC_SOLD_ID, vc.CONTRACT_TYPE " + 
		    "FROM NEM.NEM_VESTING_CONTRACTS vc WHERE vc.settlement_date = ? " + 
		    "AND (vc.external_id,  vc.created_date) in (SELECT vc1.external_id,   MAX (vc1.created_date) " + 
		    "FROM NEM.NEM_VESTING_CONTRACTS vc1 WHERE vc1.sac_sold_id = vc.sac_sold_id " + 
		    "AND vc1.settlement_date = ? " + 
		    "group by vc1.EXTERNAL_ID) " + 
		    "AND vc.SAC_SOLD_ID IN (SELECT DISTINCT(nde.SAC_ID) " + 
		    "FROM NEM.NEM_NODES nde " + 
		    "WHERE nde.VERSION = ?)";

		    // [END] 5.1.01 Modify the vesting contract query to cater 120 tranches 
		    // Vesting Contract Parameters Query
		    String sqlVestParam = "Select PRICE, QUANTITY From NEM_VESTING_CONTRACT_PARAMS " + 
		    "where VC_ID = ? ORDER BY SETTLEMENT_PERIOD";

		    // Bilateral Contract Query
		    // Commented and modified to resolve BIL selection (of latest contract) issue on 06 Dec 2010 by RO (EMC)
		    // String sqlBLC = "SELECT ID, VERSION, CONTRACT_TYPE, NDE_ID, SAC_ID_PURCHASED_BY, SAC_ID_SOLD_BY " + 
		    // "FROM NEM.NEM_BILATERAL_CONTRACTS " + 
		    // "WHERE ? BETWEEN START_DATE AND END_DATE AND EXPIRED_DATE >= ? AND APPROVAL_STATUS = 'A'";
		    // Modified query added to resolve BIL selection (of latest contract) issue on 06 Dec 2010 by RO (EMC)
		    //String sqlBLC = "SELECT BIL_OUTER.ID, BIL_OUTER.VERSION, BIL_OUTER.CONTRACT_TYPE, " + 
		   // "BIL_OUTER.NDE_ID, BIL_OUTER.SAC_ID_PURCHASED_BY, BIL_OUTER.SAC_ID_SOLD_BY " + 
		   // "FROM NEM.NEM_BILATERAL_CONTRACTS BIL_OUTER " + 
		  //  "WHERE ? BETWEEN BIL_OUTER.START_DATE AND BIL_OUTER.END_DATE AND " + 
		  //  "BIL_OUTER.EXPIRED_DATE >= ? AND BIL_OUTER.APPROVAL_STATUS = 'A' " + 
		  //  "AND BIL_OUTER.CREATED_DATE = (SELECT MAX(BIL_INNER.CREATED_DATE) " + 
		 //   "FROM NEM.NEM_BILATERAL_CONTRACTS BIL_INNER " + 
		  //  "WHERE ? BETWEEN BIL_INNER.START_DATE AND BIL_INNER.END_DATE " + 
		  //  "AND BIL_INNER.EXPIRED_DATE >= ? AND BIL_INNER.APPROVAL_STATUS = 'A' " + 
		  //  "AND BIL_INNER.SAC_ID_PURCHASED_BY=BIL_OUTER.SAC_ID_PURCHASED_BY " + 
		  //  "AND BIL_INNER.SAC_ID_SOLD_BY=BIL_OUTER.SAC_ID_SOLD_BY)";
		    //ITSM 17449 Merge with DRCAP 2
		    String sqlBLC = "SELECT BIL_OUTER.ID, BIL_OUTER.VERSION, BIL_OUTER.CONTRACT_TYPE, " + 
		    "BIL_OUTER.NDE_ID, BIL_OUTER.SAC_ID_PURCHASED_BY, BIL_OUTER.SAC_ID_SOLD_BY " + 
		    "FROM NEM.NEM_BILATERAL_CONTRACTS BIL_OUTER " + 
		    "WHERE ? BETWEEN BIL_OUTER.START_DATE AND BIL_OUTER.END_DATE AND " + 
		    "BIL_OUTER.EXPIRED_DATE >= ? AND BIL_OUTER.APPROVAL_STATUS = 'A' " + 
		    "AND BIL_OUTER.CREATED_DATE = (SELECT MAX(BIL_INNER.CREATED_DATE) " + 
		    "FROM NEM.NEM_BILATERAL_CONTRACTS BIL_INNER " + 
		    "WHERE ? BETWEEN BIL_INNER.START_DATE AND BIL_INNER.END_DATE " + 
		    "AND BIL_INNER.EXPIRED_DATE >= ? AND BIL_INNER.APPROVAL_STATUS = 'A' " + 
		    "AND BIL_INNER.CONTRACT_TYPE=BIL_OUTER.CONTRACT_TYPE " + 
		    "AND BIL_INNER.SAC_ID_PURCHASED_BY=BIL_OUTER.SAC_ID_PURCHASED_BY " + 
		    "AND BIL_INNER.SAC_ID_SOLD_BY=BIL_OUTER.SAC_ID_SOLD_BY)";  

		    // Bilateral Contract Parameters Query
		    //String sqlBLCParam = "SELECT VALUE FROM NEM.NEM_BILATERAL_PARAMETERS " + 
		    //"WHERE BLT_ID = ? and BLT_VERSION = ? and PERIOD = ?";
		    String sqlBLCParam = "SELECT VALUE, PERIOD FROM NEM.NEM_BILATERAL_PARAMETERS " + 
		    "WHERE BLT_ID = ? and BLT_VERSION = ? order by PERIOD";

		    // Settlement Account Query
		    // 8.0.01 Changes
		    // sqlAcct as String = "select ID,RETAILER_ID from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=?"
		    String sqlAcct = "select  sac.ID ID,sac.RETAILER_ID RETAILER_ID, decode(count(mep1.id),0,0,1) acc_mep_nde from NEM.NEM_SETTLEMENT_ACCOUNTS sac, (Select nsp.ID, nde.sac_id, nde.sac_version from nem.nem_settlement_prices nsp, nem.nem_nodes nde " + 
		    "where nsp.settlement_date = ? and nsp.price_type = 'MEP' and nsp.version = ? and NSP.NDE_ID = nde.id and nsp.nde_version = nde.version and nde.version = ?) mep1 where sac.VERSION=? " + 
		    "and mep1.sac_id (+) = sac.ID and mep1.sac_version (+) = sac.version group by  sac.ID,sac.RETAILER_ID ";

		    // Price Query
		    /*String sqlPrice = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES " + 
		    "WHERE SETTLEMENT_DATE = ? AND PRICE_TYPE = ? and VERSION = ? " + 
		    "ORDER BY PERIOD ";

		    // Quantity Query
		    String sqlQty = "SELECT QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
		    "WHERE SETTLEMENT_DATE = ? AND SAC_ID = ? AND " + 
		    "QUANTITY_TYPE = ? and VERSION = ? ORDER BY PERIOD ";

		    // Quantity Query join with NODES
		    String sqlQty2 = "SELECT SUM(sq.QUANTITY) FROM NEM.NEM_SETTLEMENT_QUANTITIES sq, NEM.NEM_NODES nde " + 
		    "WHERE sq.SETTLEMENT_DATE = ? AND nde.sac_id = ? AND sq.QUANTITY_TYPE = ? AND " + 
		    "sq.VERSION = ? AND nde.ID = sq.nde_id AND nde.VERSION = sq.nde_version GROUP BY sq.period ";*/

		    // FSC implementation Forward Sales Contract Query
		    String sqlFSC = "SELECT fc.ID, fc.EXTERNAL_ID, fc.SAC_PURCHASED_ID, fc.SAC_SOLD_ID, fc.CONTRACT_TYPE " + 
		    "FROM NEM.NEM_FSC_CONTRACTS fc WHERE fc.settlement_date = ? " + 
		    "AND (fc.external_id,  fc.created_date) in (SELECT fc1.external_id,   MAX (fc1.created_date) " + 
		    "FROM NEM.NEM_FSC_CONTRACTS fc1 WHERE fc1.sac_sold_id = fc.sac_sold_id " + 
		    "AND fc1.settlement_date = ? " + 
		    "group by fc1.EXTERNAL_ID) " + 
		    "AND fc.SAC_SOLD_ID IN (SELECT ID from NEM.NEM_SETTLEMENT_ACCOUNTS acc where acc.VERSION = ? and acc.External_ID <> 'SP SVCS_M' )";

		    // 8.0.01 Changes    
		    // FSC implementation Forward Sales Contract Parameters Query
		    String sqlFSCParam = "Select PRICE, QUANTITY From NEM_FSC_CONTRACT_PARAMS " + 
		    "where FC_ID = ? ORDER BY SETTLEMENT_PERIOD";
		    //int totalPeriod = ((int) UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS"));
		    
		    CSVInputer csvInputer = new CSVInputer();
		    csvInputer.settDate = params.sqlSettlementDate;
		    csvInputer.mcPricePkgVer = runPackage.mcPricePkgVer;
		    csvInputer.msslQtyPkgVer = runPackage.msslQtyPkgVer;
		    csvInputer.ds = ds;
		    csvInputer.totalPeriod = totalPeriod;
		    csvInputer.readAllPrices();
		    csvInputer.readAllWXQs();
		    csvInputer.readAllIXQs();

		    // ******************************************
		    // loop from the NEM.NEM_FSC_CONTRACT
		    //int accountBreach_Flag = 0;
		    String account_breachYN = "";
	        stmt = conn.prepareStatement(sqlFSC);
	        //stmt.setString(1, qdf.format(params.sqlSettlementDate));
	        stmt.setString(1, sqlsd);
	        //stmt.setString(2, qdf.format(params.sqlSettlementDate));
	        stmt.setString(2, sqlsd);
			stmt.setString(3, runPackage.standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        String fsc_id = rs.getString(1);
		        String fsc_externalId = rs.getString(2);
		        fsc_purchasing_id = rs.getString(3);
		        String fsc_selling_id = rs.getString(4);
		        String fsc_contract_type = rs.getString(5);
		        accountList_fsc.put(fsc_selling_id, fsc_selling_id);

		        // Ignore--set the breach flag for accounts at all interval level
		        // Ignore--FSC Implementation - retrieve breach date if the settlement account is a breach account on the given settlement date
		        // Do not send breach flag to OPA. Breach flag is not relavant in FSSC calculations hence commenting the breach retrieval code. 
		        // All the accounts FSSC is calculated in OPA irrespective of whether the account is breach or not
		        // account_breachYN = UtilityFunctions.isFSCSettlementAccountBreached(settlementAccount : fsc_selling_id, settDate : settlementParam.settlementDate);
		        breachAccountList_fsc.put(fsc_selling_id, account_breachYN);

		        // register the selling account
		        PreparedStatement stmt1 = conn.prepareStatement(sqlFSCParam);
		        stmt1.setString(1, fsc_id);
		        stmt1.executeQuery();
		        ResultSet rs1 = stmt1.getResultSet();

		        int count_fsc = 0;
				while (rs1.next()) {
		            count_fsc = count_fsc + 1;
		            
		            ForwardSalesContract fsc = new ForwardSalesContract();
		            fsc.price = rs1.getBigDecimal(1);
		            fsc.quantity = rs1.getBigDecimal(2);
		            fsc.externalId = fsc_externalId;

		            contractList_FSC.put((fsc_externalId + fsc_selling_id + String.valueOf(count_fsc)),  fsc);
		        }
				rs1.close();
				stmt1.close();
		    }
			rs.close();
			stmt.close();

		    // ******************************************
		    // loop from the NEM.NEM_VESTING_CONTRACT
	        stmt = conn.prepareStatement(sqlVest);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			//stmt.setDate(2, params.sqlRunDate);		    // UATSHARP-105, remove sqlRunDate from query
			//stmt.setString(2, qdf.format(params.sqlSettlementDate));
			stmt.setString(2, sqlsd);
			stmt.setString(3, runPackage.standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        vesting_id = rs.getString(1);
		        String vesting_name = rs.getString(2);
		        String vesting_selling_id = rs.getString(4);
		        vesting_purchasing_id = rs.getString(3);
		        String contract_type = rs.getString(5);
		        accountList_vesting.put(vesting_selling_id, vesting_selling_id);

		        // [ITSM-15086] register the selling account
		        PreparedStatement stmt1 = conn.prepareStatement(sqlVestParam);
		        stmt1.setString(1, vesting_id);
		        stmt1.executeQuery();
		        ResultSet rs1 = stmt1.getResultSet();

		        int count_vesting = 0;
				while (rs1.next()) {
		            count_vesting = count_vesting + 1;
		            
		            VestingContract vest = new VestingContract();
		            vest.hp = rs1.getBigDecimal(1);
		            vest.hq = rs1.getBigDecimal(2);
		            vest.name = vesting_name;

		            // remove the buyer as we need the seller only
		            // contractList_vesting[vesting_purchasing_id+ String.valueOf(count_vesting)] = VestingContract(vesting_HP:vesting_HP,vesting_HQ:vesting_HQ,vesting_name:vesting_name)
		            // display contractList_vesting[vesting_purchasing_id + String.valueOf(o : count_vesting)].vesting_HP+"/"+contractList_vesting[vesting_purchasing_id + String.valueOf(o : count_vesting)].vesting_HQ
		            // [ITSM-12670] CONTRACT_TYPE will be set to 'VEQ' for Allocated Vesting Data, and 'TEQ' for Tendered Vesting Data
		            // [ITSM-15086] Add CONTRACT_TYPE 'LEQ' for LNG Vesting Contract
		            if (contract_type.equals("VEQ") || contract_type.equals("LEQ")) {
		            	contractList_vesting.put((vesting_name + vesting_selling_id + String.valueOf(count_vesting)),  vest);
		            	
		                // fix for picking incorrect HP for MSSL
		            	// SATSHARP-268, enhancement to follow P-drive report, always take the MAX HP
		            	if (vest.hp.compareTo(mssl_HP) > 0) {
		            		mssl_HP = vest.hp;
		            	}
		            } else {
		            	contractList_tvc.put((vesting_name + vesting_selling_id + String.valueOf(count_vesting)),  vest);
		            }
		        }
				rs1.close();
				stmt1.close();
		    }
			rs.close();
			stmt.close();

		    // ********************** Bilateral parameters ****************
			BigDecimal bilateral_BAQ_purchased = BigDecimal.ZERO;
			BigDecimal bilateral_BAQ_sold = BigDecimal.ZERO;
			BigDecimal bilateral_BFQ_purchased = BigDecimal.ZERO;
			BigDecimal bilateral_BFQ_sold = BigDecimal.ZERO;
			BigDecimal bilateral_BIF_purchased = BigDecimal.ZERO;
			BigDecimal bilateral_BIF_sold = BigDecimal.ZERO;
			//BigDecimal bilateral_BIQ_purchased = BigDecimal.ZERO;
			//BigDecimal bilateral_BIQ_sold = BigDecimal.ZERO;
			BigDecimal bilateral_BRQ_purchased = BigDecimal.ZERO;
			BigDecimal bilateral_BRQ_sold = BigDecimal.ZERO;
			BigDecimal bilateral_BWF_purchased = BigDecimal.ZERO;
			BigDecimal bilateral_BWF_sold = BigDecimal.ZERO;
			//BigDecimal bilateral_BWQ_purchased = BigDecimal.ZERO;
			//BigDecimal bilateral_BWQ_sold = BigDecimal.ZERO;
		    //String bilateral_name = "";
		    String bilateral_selling_id = "";
		    String bilateral_buying_id = "";
		    //int bilateral_interval;

		    // ******************************************************
		    // ******** interactive parameters******
		    String bilateral_version = "";
		    String bilateral_contract_id = "";
		    String bilateral_contract_type = "";
		    //String bilateral_node_id = "";

	        stmt = conn.prepareStatement(sqlBLC);
			/*stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(2, qdf.format(params.sqlSettlementDate));
			stmt.setString(3, qdf.format(params.sqlSettlementDate));
			stmt.setString(4, qdf.format(params.sqlSettlementDate));*/
			stmt.setString(1, sqlsd);
			stmt.setString(2, sqlsd);
			stmt.setString(3, sqlsd);
			stmt.setString(4, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        bilateral_BAQ_purchased = BigDecimal.ZERO;
		        bilateral_BAQ_sold = BigDecimal.ZERO;
		        bilateral_BFQ_purchased = BigDecimal.ZERO;
		        bilateral_BFQ_sold = BigDecimal.ZERO;
		        bilateral_BIF_purchased = BigDecimal.ZERO;
		        bilateral_BIF_sold = BigDecimal.ZERO;
		        //bilateral_BIQ_purchased = BigDecimal.ZERO;
		        //bilateral_BIQ_sold = BigDecimal.ZERO;
		        bilateral_BRQ_purchased = BigDecimal.ZERO;
		        bilateral_BRQ_sold = BigDecimal.ZERO;
		        bilateral_BWF_purchased = BigDecimal.ZERO;
		        bilateral_BWF_sold = BigDecimal.ZERO;
		        //bilateral_BWQ_purchased = BigDecimal.ZERO;
		        //bilateral_BWQ_sold = BigDecimal.ZERO;
		        //bilateral_name = "";
		        bilateral_contract_id = rs.getString(1);
		        bilateral_version = rs.getString(2);
		        bilateral_contract_type = rs.getString(3);
		        //bilateral_node_id = rs.getString(4);
		        bilateral_selling_id = rs.getString(6);
		        bilateral_buying_id = rs.getString(5);
		        sellerList_bilateral.put((bilateral_buying_id + bilateral_contract_id), bilateral_selling_id);
		        buyerList_bilateral.put((bilateral_selling_id + bilateral_contract_id), bilateral_buying_id);
		        
		        {
		            //int period = 1;

		            //while (period <= totalPeriod) {
				        PreparedStatement stmt1 = conn.prepareStatement(sqlBLCParam);
				        stmt1.setString(1, bilateral_contract_id);
				        stmt1.setString(2, bilateral_version);
				        //stmt1.setString(3, String.valueOf(period));
				        stmt1.executeQuery();
				        ResultSet rs1 = stmt1.getResultSet();

						while (rs1.next()) {
							BigDecimal bilateral_value = rs1.getBigDecimal(1);
							String period = rs1.getString(2);

		                    if (bilateral_contract_type.equals("Energy")) {
		                        bilateral_BAQ_purchased = bilateral_value;
		                        bilateral_BAQ_sold = bilateral_value;
		                    }

		                    if (bilateral_contract_type.equals("Regulation")) {
		                        bilateral_BFQ_purchased = bilateral_value;
		                        bilateral_BFQ_sold = bilateral_value;
		                    }

		                    if (bilateral_contract_type.equals("Injection")) {
		                        bilateral_BIF_purchased = bilateral_value.setScale(3).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
		                        bilateral_BIF_sold = bilateral_value.setScale(3).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
		                    }

		                    if (bilateral_contract_type.equals("Reserve")) {
		                        bilateral_BRQ_purchased = bilateral_value;
		                        bilateral_BRQ_sold = bilateral_value;
		                    }

		                    if (bilateral_contract_type.equals("Load")) {
		                        bilateral_BWF_purchased = bilateral_value.setScale(3).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
		                        bilateral_BWF_sold = bilateral_value.setScale(3).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
		                    }
		                    
		                    BilateralContract sell  = new BilateralContract();
		                    sell.baqPurchased = BigDecimal.ZERO;
		                    sell.baqSold = bilateral_BAQ_sold;
		                    sell.bfqPurchased = BigDecimal.ZERO;
		                    sell.bfqSold = bilateral_BFQ_sold;
		                    sell.bifPurchased = BigDecimal.ZERO;
		                    sell.bifSold = bilateral_BIF_sold;
		                    sell.biqPurchased = null;	//BigDecimal.ZERO;
		                    sell.biqSold = null;	//bilateral_BIQ_sold;
		                    sell.bwfPurchased = BigDecimal.ZERO;
		                    sell.bwfSold = bilateral_BWF_sold;
		                    sell.bwqPurchased = null;	//BigDecimal.ZERO;
		                    sell.bwqSold = null;	//bilateral_BWQ_sold;
		                    sell.ieqSold = BigDecimal.ZERO;
		                    sell.ieqGenerated = BigDecimal.ZERO;
		                    sell.name = bilateral_contract_id;
		                    sell.account = bilateral_selling_id;
		                    sell.interval = period;	//String.valueOf(period);
		                    
		                    contractList_bilateral.put((bilateral_selling_id + bilateral_contract_id + period), sell);	//String.valueOf(period)), sell);
		                    
		                    BilateralContract buy  = new BilateralContract();
		                    buy.baqPurchased = bilateral_BAQ_purchased;
		                    buy.baqSold = BigDecimal.ZERO;
		                    buy.bfqPurchased = bilateral_BFQ_purchased;
		                    buy.bfqSold = BigDecimal.ZERO;
		                    buy.bifPurchased = bilateral_BIF_purchased;
		                    buy.bifSold = BigDecimal.ZERO;
		                    buy.biqPurchased = null;	//bilateral_BIQ_purchased;
		                    buy.biqSold = null;	//BigDecimal.ZERO;
		                    buy.bwfPurchased = bilateral_BWF_purchased;
		                    buy.bwfSold = BigDecimal.ZERO;
		                    buy.bwqPurchased = null;	//bilateral_BWQ_purchased;
		                    buy.bwqSold = null;	//BigDecimal.ZERO;
		                    buy.ieqSold = BigDecimal.ZERO;
		                    buy.ieqGenerated = BigDecimal.ZERO;
		                    buy.name = bilateral_contract_id;
		                    buy.account = bilateral_buying_id;
		                    buy.interval = period;	//String.valueOf(period);
		                    
		                    contractList_bilateral.put((bilateral_buying_id + bilateral_contract_id + period), buy);	//String.valueOf(period)), buy);
		                }
						rs1.close();
						stmt1.close();

		                //period = period + 1;
		            //}
		        }
		    }
			rs.close();
			stmt.close();

		    // begin Interval Process
		    // 8.0.01 Changes    
		    // params1 as String[]  
	        stmt = conn.prepareStatement(sqlAcct);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, runPackage.mcPricePkgVer);
			stmt.setString(3, version);
			stmt.setString(4, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
		    /*int interval_MFP_count = 0;
		    int interval_WCQ_count = 0;
		    int interval_WFQ_count = 0;

		    // [EG]
		    int interval_WMQ_count = 0;

		    // [EG]
		    int interval_WEQ_count = 0;
		    int interval_WPQ_count = 0;*/

			while (rs.next()) {
				
		        account_id = rs.getString(1);
		        String interval_EGA_id = rs.getString(2);
		        String interval_gen_node = rs.getString(3);

		        // 8.0.01 Changes
		        //pd.setAccountId(account_id);
		        //int pos = 0;

		        // get the interval_MFP
		        HashMap<Integer, BigDecimal> interval_MFP_array = csvInputer.getIntervalMFP();

		        // get the interval_WCQ
		        HashMap<Integer, BigDecimal> interval_WCQ_array = csvInputer.getIntervalXXQ(account_id, "WCQ");

		        // [EG]
		        // get the interval_WFQ
		        HashMap<Integer, BigDecimal> interval_WFQ_array = csvInputer.getIntervalXXQ(account_id, "WFQ");

		        // [EG]
		        // get the interval_WMQ
		        HashMap<Integer, BigDecimal> interval_WMQ_array = csvInputer.getIntervalXXQ(account_id, "WMQ");

		        // get the interval_WEQ
		        HashMap<Integer, BigDecimal> interval_WEQ_array = csvInputer.getIntervalXXQ(account_id, "WEQ");

		        // get the interval_WPQ
		        HashMap<Integer, BigDecimal> interval_WPQ_array = csvInputer.getIntervalXXQ(account_id, "WPQ");

		        // get the interval_LCP
		        HashMap<Integer, BigDecimal> interval_LCP_array = csvInputer.getIntervalLCP();

		        // get the interval_WDQ
		        HashMap<Integer, BigDecimal> interval_WDQ_array = csvInputer.getIntervalXXQ(account_id, "WDQ");

		        // get the interval_IEQ
		        HashMap<Integer, BigDecimal> interval_IEQ_array = csvInputer.getIntervalXXQ(account_id, "IEQ");
		        
		        BigDecimal temp_interval_IEQ = null;
		        BigDecimal interval_WEQ = null;
		        
		        {
		            int i = 1;

		            while (i <= totalPeriod) {
		            	
						Period pd = new Period();
						pd.setPeriodId(i);
				        pd.setEgaId(interval_EGA_id != null? interval_EGA_id: "");
				        pd.setGenNode(interval_gen_node.equals("1"));
				        pd.setAccountId(account_id);
				        
						pd.setTotalR(false);
						Market mkt = data.getMarket().get(pd.getPeriodId());
						pd.setUsep(mkt.getUsep());

		                if (interval_MFP_array.size() == 0) {
		                	pd.setMfp(BigDecimal.ZERO);
		                } else {
		                	pd.setMfp(interval_MFP_array.get(i));
		                }

		                if (interval_WCQ_array.size() == 0) {
		                	pd.setWcq(BigDecimal.ZERO);
		                } else {
		                	pd.setWcq(interval_WCQ_array.get(i));
		                }

		                // [EG]
		                if (interval_WFQ_array.size() == 0) {
		                	pd.setWfq(BigDecimal.ZERO);
		                } else {
		                	pd.setWfq(interval_WFQ_array.get(i));
		                }

		                // [EG]
		                if (interval_WMQ_array.size() == 0) {
		                	pd.setWmq(BigDecimal.ZERO);
		                } else {
		                	pd.setWmq(interval_WMQ_array.get(i));
		                }

		                if (interval_WEQ_array.size() == 0) {
		                	pd.setWeq(BigDecimal.ZERO);
		                	interval_WEQ = BigDecimal.ZERO;
		                } else {
		                	interval_WEQ = interval_WEQ_array.get(i);
		                	pd.setWeq(interval_WEQ);
		                }

		                if (interval_WPQ_array.size() == 0) {
		                	pd.setWpq(BigDecimal.ZERO);
		                } else {
		                	pd.setWpq(interval_WPQ_array.get(i));
		                }

		                if (interval_LCP_array.size() == 0) {
		                	pd.setLcp(BigDecimal.ZERO);
		                } else {
		                	pd.setLcp(interval_LCP_array.get(i));
		                }

		                if (interval_WDQ_array.size() == 0) {
		                	pd.setWdq(BigDecimal.ZERO);
		                } else {
		                	pd.setWdq(interval_WDQ_array.get(i));
		                }

		                if (interval_IEQ_array.size() == 0) {
		                	pd.setIeq(BigDecimal.ZERO);
		                	temp_interval_IEQ = BigDecimal.ZERO;
		                } else {
		                	temp_interval_IEQ = interval_IEQ_array.get(i);
		                	pd.setIeq(temp_interval_IEQ);
		                }
		                
		                data.getPeriod().put(pd.getKey(), pd);

		                // fixes for EGA
		                /*if (interval_EGA_id == null) {
		                    interval_EGA_id = "";
		                }*/

		                /*String[] columnResults;

		                if (haleyVersion >= 8.0) {
		                    columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, interval_delta_WCQ, interval_delta_WEQ, interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, String.valueOf(o : interval_WCQ), String.valueOf(o : interval_WEQ), String.valueOf(o : interval_WPQ), interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, String.valueOf(o : interval_WFQ), String.valueOf(o : interval_WMQ), String.valueOf(c : interval_delta_WMQ), String.valueOf(c : interval_delta_WFQ), interval_gen_node, interval_delta_WDQ, String.valueOf(o : interval_LCP), String.valueOf(o : interval_WDQ), interval_imp_HEUR, interval_imp_HLCU };
		                }
		                else {
		                    if (haleyVersion >= 7.2) {
		                        columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, interval_delta_WCQ, interval_delta_WEQ, interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, String.valueOf(o : interval_WCQ), String.valueOf(o : interval_WEQ), String.valueOf(o : interval_WPQ), interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, String.valueOf(o : interval_WFQ), String.valueOf(o : interval_WMQ), String.valueOf(c : interval_delta_WMQ), String.valueOf(c : interval_delta_WFQ), interval_gen_node };
		                    }
		                    else {
		                        columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, interval_delta_WCQ, interval_delta_WEQ, interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, String.valueOf(o : interval_WCQ), String.valueOf(o : interval_WEQ), String.valueOf(o : interval_WPQ), interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, String.valueOf(o : interval_WFQ), String.valueOf(o : interval_WMQ), String.valueOf(c : interval_delta_WMQ), String.valueOf(c : interval_delta_WFQ) };
		                    }
		                }

		                csvWriter.writeLine(values : columnResults);*/

		                if (account_id.equals(vesting_purchasing_id) == true) {
		                    // for MSSL, the HP will be the same for all accounts
		                    /*String[] columnResults3 = { String.valueOf(o : mssl_HP), "0", "", account_id, String.valueOf(o : i) };
		                    csvWriter2.writeLine(values : columnResults3);*/
		                    
		                    Vesting vesting = new Vesting();
		                    vesting.setHp(mssl_HP);
		                    vesting.setHq(BigDecimal.ZERO);
		                    vesting.setContractName("");
		                    vesting.setAccountId(account_id);
		                    vesting.setPeriodId(String.valueOf(i));
		                    
		                    data.getVesting().put(vesting.getKey(), vesting);
		                }

		                // else {
		                //  if (accountList_vesting[account_id] == null) {
		                //    String[] emptyVesting = { "0", "0", "", account_id, String.valueOf(o : i) };
		                //   csvWriter2.writeLine(values : emptyVesting);
		                // }
		                // }
		                // For FSC when the account is an MSSL account
		                if (account_id.equals(fsc_purchasing_id) == true) {
		                    // for MSSL, the FSP is zero
		                    /*String[] columnResults4 = { "0", "0", "", account_id, String.valueOf(o : i), "0" };
		                    csvWriter4.writeLine(values : columnResults4);*/
		                    
		                    Fsc fsc =  new Fsc();
		                    fsc.setFsp(BigDecimal.ZERO);
		                    fsc.setFsq(BigDecimal.ZERO);
		                    fsc.setContractName("");
		                    fsc.setAccountId(account_id);
		                    fsc.setPeriodId(String.valueOf(i));
		                    fsc.setBreached(false);
		                    
		                    data.getFsc().put(fsc.getKey(), fsc);
		                }

		                // else {		                // else {
		                //  if (accountList_fsc[account_id] == null) {
		                //    String[] emptyFSC = { "0", "0", "", account_id, String.valueOf(o : i) };
		                //   csvWriter4.writeLine(values : emptyFSC);
		                // }
		                // }
		                boolean hasContract = false;

						Iterator<Entry<String,String>> itr = sellerList_bilateral.entrySet().iterator();
						while (itr.hasNext()) {
							Entry<String,String> entry = itr.next();
				            String key = entry.getKey();
				            if (entry.getValue().equals(account_id) == true) {
				            	contractList_bilateral.get(account_id + key.substring(32) + String.valueOf(i)).ieqGenerated = temp_interval_IEQ;
				            	contractList_bilateral.get(key + String.valueOf(i)).ieqGenerated = temp_interval_IEQ;
		                        hasContract = true;
				            }
						}

						itr = buyerList_bilateral.entrySet().iterator();
						while (itr.hasNext()) {
							Entry<String,String> entry = itr.next();
				            String key = entry.getKey();
				            if (entry.getValue().equals(account_id) == true) {
				            	contractList_bilateral.get(account_id + key.substring(32) + String.valueOf(i)).ieqSold = interval_WEQ;
				            	contractList_bilateral.get(key + String.valueOf(i)).ieqSold = interval_WEQ;
		                        hasContract = true;
				            }
						}

		                if (hasContract == false) {
		                    // create an empty contract for this account
		                    BilateralContract empty  = new BilateralContract();
		                    empty.baqPurchased = BigDecimal.ZERO;
		                    empty.baqSold = BigDecimal.ZERO;
		                    empty.bfqPurchased = BigDecimal.ZERO;
		                    empty.bfqSold = BigDecimal.ZERO;
		                    empty.bifPurchased = BigDecimal.ZERO;
		                    empty.bifSold = BigDecimal.ZERO;
		                    empty.biqPurchased = null;	//BigDecimal.ZERO;
		                    empty.biqSold = null;	//BigDecimal.ZERO;
		                    empty.bwfPurchased = BigDecimal.ZERO;
		                    empty.bwfSold = BigDecimal.ZERO;
		                    empty.bwqPurchased = null;	//BigDecimal.ZERO;
		                    empty.bwqSold = null;	//BigDecimal.ZERO;
		                    empty.ieqSold = BigDecimal.ZERO;
		                    empty.ieqGenerated = BigDecimal.ZERO;
		                    empty.name = "";
		                    empty.account = account_id;
		                    empty.interval = String.valueOf(i);
		                    
		                    contractList_bilateral.put((account_id + String.valueOf(i)), empty);
		                }

		                i = i + 1;
		            }
		        }

		        // logMessage "interval_WCQ_count is" + interval_WCQ_count  using severity = WARNING
		    }
			rs.close();
			stmt.close();

			Iterator<Entry<String,BilateralContract>> itb = contractList_bilateral.entrySet().iterator();
			while (itb.hasNext()) {
				Entry<String,BilateralContract> entry = itb.next();
	            //String id = entry.getKey();
	            BilateralContract val = entry.getValue();
	            
	            Bilateral bc = new Bilateral();
	            bc.setBaqPurchased(val.baqPurchased);
	            bc.setBaqSold(val.baqSold);
	            bc.setBfqPurchased(val.bfqPurchased);
	            bc.setBfqSold(val.bfqSold);
	            bc.setBifPurchased(val.bifPurchased);
	            bc.setBifSold(val.bifSold);
	            bc.setBiqPurchased(val.biqPurchased);
	            bc.setBiqSold(val.biqSold);
	            bc.setBwfPurchased(val.bwfPurchased);
	            bc.setBwfSold(val.bwfSold);
	            bc.setBwqPurchased(val.bwqPurchased);
	            bc.setBwqSold(val.bwqSold);
	            bc.setRetailerWeq(val.ieqSold);
	            bc.setGeneratorIeq(val.ieqGenerated);
	            bc.setContractName(val.name);
	            bc.setAccountId(val.account);
	            bc.setPeriodId(val.interval);
	            
	            data.getBilateral().put(bc.getKey(), bc);
			}

		    // Added for Haley Rules [ITSM-15086]
			Iterator<Entry<String,VestingContract>> itv = contractList_vesting.entrySet().iterator();
			while (itv.hasNext()) {
				Entry<String,VestingContract> entry = itv.next();
	            String id = entry.getKey();
	            VestingContract val = entry.getValue();
		        String keys = id.replace(val.name, "");
		        String vc_account = keys.substring(0, 32);
		        String vc_interval = keys.replace(vc_account, "");
		        
		        Vesting vc = new Vesting();
		        vc.setHp(val.hp);
		        vc.setHq(val.hq);
		        vc.setContractName(val.name);
		        vc.setAccountId(vc_account);
		        vc.setPeriodId(vc_interval);
		        
		        data.getVesting().put(vc.getKey(), vc);
			}

		    // Added for Haley Rules [ITSM-12670]
			itv = contractList_tvc.entrySet().iterator();
			while (itv.hasNext()) {
				Entry<String,VestingContract> entry = itv.next();
	            String id = entry.getKey();
	            VestingContract val = entry.getValue();
		        String keys = id.replace(val.name, "");
		        String vc_account = keys.substring(0, 32);
		        String vc_interval = keys.replace(vc_account, "");
		        
		        Tvc vc = new Tvc();
		        vc.setTvp(val.hp);
		        vc.setTvq(val.hq);
		        vc.setContractName(val.name);
		        vc.setAccountId(vc_account);
		        vc.setPeriodId(vc_interval);
		        
		        data.getTVC().put(vc.getKey(), vc);
			}

		    // logMessage("breachAccountList_fsc -->"+breachAccountList_fsc);
		    // Added for Haley Rules fsc csv columns generation
			Iterator<Entry<String,ForwardSalesContract>> itf = contractList_FSC.entrySet().iterator();
			while (itf.hasNext()) {
				Entry<String,ForwardSalesContract> entry = itf.next();
	            String id = entry.getKey();
	            ForwardSalesContract val = entry.getValue();
		        String keys = id.replace(val.externalId, "");
		        String fs_account = keys.substring(0, 32);
		        String fs_interval = keys.replace(fs_account, "");
		        
		        // get breach flag
		        account_breachYN = breachAccountList_fsc.get(fs_account);
		        
		        Fsc sc = new Fsc();
		        sc.setFsp(val.price);
		        sc.setFsq(val.quantity);
		        sc.setContractName(val.externalId);
		        sc.setAccountId(fs_account);
		        sc.setPeriodId(fs_interval);
		        sc.setBreached(account_breachYN.equalsIgnoreCase("Y"));
		        
		        data.getFsc().put(sc.getKey(), sc);
			}

			
	        String msg = "Successfully Generated Entity CSV File: Interval, Bilateral, Vesting, TVC and FSC";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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

	public static void readIntervalRS(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readIntervalRS()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
		
		ZonedDateTime zdt = ZonedDateTime.of(params.sqlSettlementDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
		DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
		String sqlsd = sqlFormatter.format(zdt);
		    
		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity CSV File: Interval and Vesting", "");

		    // ********************** CSV parameters ****************
		    /*String interval_account = "";
		    String interval_BAQ_purchased = "";
		    String interval_BAQ_sold = "";
		    String interval_BEQ_purchased = "";
		    String interval_BEQ_sold = "";
		    String interval_BESC = "";
		    String interval_BFQ_purchased = "";
		    String interval_BFQ_sold = "";
		    String interval_BIF_purchased = "";
		    String interval_BIF_sold = "";
		    String interval_BWF_purchased = "";
		    String interval_BWF_sold = "";
		    Decimal interval_delta_WCQ;
		    Decimal interval_delta_WEQ;
		    String interval_comp_EMCA = "";
		    String interval_comp_PSOA = "";
		    String interval_EGA_IEQ = "";
		    String interval_EGA_WEQ = "";
		    String interval_EGA_WPQ = "";
		    String interval_EUA = "";
		    String interval_FCC = "";
		    String interval_FEQ = "";
		    String interval_FSC = "";
		    String interval_FSD = "";
		    String interval_GESC = "";
		    String interval_GESCE = "";
		    String interval_GESCN = "";
		    String interval_GESCP = "";
		    String interval_GMEE = "";
		    String interval_GMEF = "";
		    String interval_HEUA = "";
		    String interval_HEUSA = "";
		    String interval_IEQ = "";
		    String interval_IEQP = "";*/
		    BigDecimal interval_imp_EMCA = BigDecimal.ZERO;
		    BigDecimal interval_imp_PSOA = BigDecimal.ZERO;
		    /*String interval_inc_NMEA = "";
		    String interval_v_FSC = "";
		    String interval_v_GESC = "";
		    String interval_v_GESCN = "";
		    String interval_v_GMEE = "";
		    String interval_v_inc_NMEA = "";
		    String interval_v_LESD = "";
		    String interval_v_LESDN = "";
		    String interval_v_NASC = "";
		    String interval_v_NESC = "";
		    String interval_v_NMEA = "";
		    String interval_v_NPSC = "";
		    String interval_v_RSC = "";
		    String interval_LESD = "";
		    String interval_LESDN = "";
		    String interval_LESDP = "";
		    String interval_LMEE = "";
		    String interval_LMEF = "";
		    String interval_MEP = "";
		    String interval_MEUSA = "";
		    Decimal interval_MFP;
		    String interval_MRP = "";
		    String interval_NASC = "";
		    String interval_NEAA = "";
		    String interval_NEAD = "";
		    String interval_NEGC = "";
		    String interval_NEGC_IEQ = "";
		    String interval_NELC = "";
		    String interval_NESC = "";
		    String interval_NFSC = "";
		    String interval_NMEA = "";
		    String interval_nde_count = "";
		    String interval_NPSC = "";
		    String interval_NRSC = "";
		    String interval_NTSC = "";
		    int interval_number;
		    String interval_a_FSD = "";
		    String interval_a_GESC = "";
		    String interval_a_GESCE = "";
		    String interval_a_GESCP = "";
		    String interval_a_GMEF = "";
		    String interval_a_HEUSA = "";
		    String interval_a_inc_NMEA = "";
		    String interval_a_LESD = "";
		    String interval_a_LESDP = "";
		    String interval_a_LMEE = "";
		    String interval_a_LMEF = "";
		    String interval_a_MEUSA = "";
		    String interval_a_NASC = "";
		    String interval_a_NESC = "";
		    String interval_a_NMEA = "";
		    String interval_a_NPSC = "";
		    String interval_a_RSD = "";
		    String interval_RCC = "";
		    String interval_RSA = "";
		    String interval_RSC = "";
		    String interval_RSD = "";
		    String interval_max_IEQ = "";
		    String interval_total_BESC = "";
		    String interval_total_FSC = "";
		    String interval_total_FSD = "";
		    String interval_total_GESC = "";
		    String interval_total_GESCE = "";
		    String interval_total_GESCN = "";
		    String interval_total_GESCP = "";
		    String interval_total_GMEE = "";
		    String interval_total_GMEF = "";
		    String interval_total_HEUSA = "";
		    String interval_total_LESD = "";
		    String interval_total_LESDN = "";
		    String interval_total_LESDP = "";
		    String interval_total_LMEE = "";
		    String interval_total_LMEF = "";
		    String interval_total_MEUSA = "";
		    String interval_total_NASC = "";
		    String interval_total_NESC = "";
		    String interval_total_NFSC = "";
		    String interval_total_NMEA = "";
		    String interval_total_NPSC = "";
		    String interval_total_NRSC = "";
		    String interval_total_inc_NMEA = "";
		    String interval_total_RCC = "";
		    String interval_total_RSC = "";
		    String interval_total_RSD = "";
		    String interval_TTE = "";
		    String interval_USEP = "";
		    String interval_VCRP = "";
		    String interval_VCRPK = "";
		    String interval_VCSC = "";
		    String interval_VCSCK = "";
		    String interval_WCQ = "";
		    String interval_WEQ = "";
		    String interval_WPQ = "";
		    String interval_WFQ = "";

		    // [EG]
		    String interval_WMQ = "";

		    // [EG]
		    Decimal interval_delta_WFQ;

		    // [EG]
		    Decimal interval_delta_WMQ;

		    // [EG]
		    String interval_facility_RSC = "";
		    String interval_GMEA = "";
		    String interval_LMEA = "";
		    String interval_a_LMEA = "";
		    String interval_a_GMEA = "";
		    String interval_EGA_id = "";
		    String interval_FEE_TOTAL = "";
		    String interval_imp_AFP = "";
		    String interval_imp_HEUC = "";
		    String interval_imp_MEUC = "";
		    String interval_imp_USEP = "";
		    String interval_gen_node = "0";

		    // 8.0.01 Changes      
		    // DRCAP
		    Decimal interval_delta_WDQ;
		    String interval_LCP = "";
		    String interval_WDQ = "";
		    String interval_imp_HEUR = "";
		    String interval_imp_HLCU = "";*/

		    // ******************************************************
		    /*Java.Io.FileWriter fileWriter = FileWriter(arg1 : dataInputDir + "\\interval.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter = CSVWriter(writer : fileWriter);
		    Java.Io.FileWriter fileWriter1 = FileWriter(arg1 : dataInputDir + "\\bilateral.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter1 = CSVWriter(writer : fileWriter1);
		    Java.Io.FileWriter fileWriter2 = FileWriter(arg1 : dataInputDir + "\\vesting.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter2 = CSVWriter(writer : fileWriter2);
		    Java.Io.FileWriter fileWriter3 = FileWriter(arg1 : dataInputDir + "\\tvc.csv");

		    // [ITSM-12670]
		    CSV4J.Net.Sf.Csv4j.CSVWriter tvcWriter = CSVWriter(writer : fileWriter3);

		    // [ITSM-16708] Added for FSC Implementation
		    Java.Io.FileWriter fileWriter4 = FileWriter(arg1 : dataInputDir + "\\fsc.csv");
		    CSV4J.Net.Sf.Csv4j.CSVWriter csvWriter4 = CSVWriter(writer : fileWriter4);*/

		    // **********passed in parameters******* 	
		    // dateFormat as Java.Text.DateFormat = Java.Text.SimpleDateFormat("yyyy-MM-dd")
		    // input_date as String = dateFormat.format(settlementParam.settlement_date)
//		    Date input_date = params.sqlSettlementDate;
		    String input_type = params.runType;
		    
		    //int totalPeriod = ((int) UtilityFunctions.getSysParamNum(ds, "NO_OF_PERIODS"));

		    // run_type as String = ""
		    // *************************************
		    // ******** interactive parameters******
		    String version = runPackage.standingVersion;

		    // 	price_version as String = ""
		    // 	mc_qty_version as String = ""
		    // 	finalRunMsslQtyVersion as String = ""
		    String currMsslQtyVersion = runPackage.msslQtyPkgVer;
		    //String node_id = "";
		    String account_id = "";
		    //String acg_id = "";
		    String finalRunId = "";

		    // *************************************
		    // get the standing data version number
		    String sqlCommand = "SELECT effective_date, end_date, version " + 
		                 "FROM NEM.NEM_STANDING_VERSIONS_MV WHERE ? " + 
		                 "BETWEEN effective_date AND end_date";
	        stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(input_date));
			stmt.setString(1, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        version = rs.getString(3);

		        // logMessage "the version number is " + version using severity = WARNING
		    }
			rs.close();
			stmt.close();

		    // **************************************************	
		    // get the MSSL quantity version of:
		    // - Final Run (if current Run is R-Run) 
		    // - R-Run (if current Run is S-Run) for the Settlement Date
			String lastRunMsslQtyVersion = "";
			
		    sqlCommand = "SELECT MSSL_QTY_VERSION FROM NEM.NEM_SETTLEMENT_RUN_STATUS_V " + 
		                 "WHERE SETTLEMENT_DATE = ? AND RUN_TYPE = ? AND AUTHORISED = 'A'";
			stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, (params.runType.equalsIgnoreCase("R") == true? "F": "R"));
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        lastRunMsslQtyVersion = rs.getString(1);

		        //logger.info(logPrefix + params.sqlSettlementDate.toString() + "-Run MSSL Qty Version: " + lastRunMsslQtyVersion);
		    }
			rs.close();
			stmt.close();

		    // ************** CSV Input Parameters For EMCADMIN and PSOADMIN *****************
		    // [ITSM15890] remove the debit sign from the fees rate if found
		    if (input_type.equals("R") || input_type.equals("S")) {
		        sqlCommand = "SELECT UNIQUE replace(replace(upper(npc.name),'APPROX. ',''),' $/MW','') " + 
		                     "FROM nem_non_period_charges npc, nem_non_period_charge_codes ncc " + 
		                     "WHERE npc.charge_date = ? AND ncc.domain = 'GENERAL' AND ncc.solomon_code = ? " + 
		                     "AND npc.ncc_id = ncc.ID AND npc.npc_type = 'ACC' AND npc.approval_status = 'A'";
		        stmt = conn.prepareStatement(sqlCommand);
				//stmt.setString(1, qdf.format(params.sqlSettlementDate));
				stmt.setString(1, sqlsd);
		        stmt.setString(2, "EMCADMIN");
				stmt.executeQuery();
				rs = stmt.getResultSet();

				while (rs.next()) {
		            String adminFee = rs.getString(1);

		            if (adminFee.startsWith("(")) {
		                BigDecimal temp_imp_EMCA = new BigDecimal(adminFee.substring(1, adminFee.length() - 1));

		                // convert the Fees rate back to negative number
		                interval_imp_EMCA = temp_imp_EMCA.negate();
		            } else {
		                BigDecimal temp_imp_EMCA = new BigDecimal(adminFee);
		                interval_imp_EMCA = temp_imp_EMCA;
		            }
		        }
				rs.close();
				stmt.close();

		        sqlCommand = "SELECT UNIQUE replace(replace(upper(npc.name),'APPROX. ',''),' $/MW','') " + 
		                     "FROM nem_non_period_charges npc, nem_non_period_charge_codes ncc " + 
		                     "WHERE npc.charge_date = ? AND ncc.domain = 'GENERAL' AND ncc.solomon_code = ? " + 
		                     "AND npc.ncc_id = ncc.ID AND npc.npc_type = 'ACC' AND npc.approval_status = 'A'";
		        stmt = conn.prepareStatement(sqlCommand);
				//stmt.setString(1, qdf.format(params.sqlSettlementDate));
				stmt.setString(1, sqlsd);
		        stmt.setString(2, "PSOADMIN");
				stmt.executeQuery();
				rs = stmt.getResultSet();

				while (rs.next()) {
		            String adminFee = rs.getString(1);

		            if (adminFee.startsWith("(")) {
		            	BigDecimal temp_imp_PSOA = new BigDecimal(adminFee.substring(1, adminFee.length() - 1));

		                // convert the Fees rate back to negative number
		                interval_imp_PSOA = temp_imp_PSOA.negate();
		            } else {
		            	BigDecimal temp_imp_PSOA = new BigDecimal(adminFee);
		                interval_imp_PSOA = temp_imp_PSOA;
		            }
		        }
				rs.close();
				stmt.close();
		    }

		    // ************** CSV Input Parameters For calculated prices *****************
		    HashMap<String, BigDecimal> cal_afp_array = new HashMap<String, BigDecimal>();
		    HashMap<String, BigDecimal> cal_meuc_array = new HashMap<String, BigDecimal>();
		    HashMap<String, BigDecimal> cal_heuc_array = new HashMap<String, BigDecimal>();
		    HashMap<String, BigDecimal> cal_usep_array = new HashMap<String, BigDecimal>();
		    HashMap<String, BigDecimal> cal_heur_array = new HashMap<String, BigDecimal>();
		    HashMap<String, BigDecimal> cal_hlcu_array = new HashMap<String, BigDecimal>();

		    if (input_type.equals("R") || input_type.equals("S")) {
		        sqlCommand = "SELECT ID FROM NEM.NEM_SETTLEMENT_RUN_STATUS_V " + 
		                     "WHERE SETTLEMENT_DATE = ? AND RUN_TYPE = 'F' AND AUTHORISED = 'A'";
		        stmt = conn.prepareStatement(sqlCommand);
				//stmt.setString(1, qdf.format(params.sqlSettlementDate));
				stmt.setString(1, sqlsd);
				stmt.executeQuery();
				rs = stmt.getResultSet();

				while (rs.next()) {
		            finalRunId = rs.getString(1);

		            // display "Final Run MSSL Qty Version: " + finalRunId
		        }
				rs.close();
				stmt.close();

		        sqlCommand = "SELECT period, afp, meuc, heuc, usep, NVL(heur,0), NVL(hlcu,0) " + 
		                     "FROM NEM_SETTLEMENT_USAP_RESULTS WHERE str_id = ? order by period";
		        stmt = conn.prepareStatement(sqlCommand);
		        stmt.setString(1, finalRunId);
				stmt.executeQuery();
				rs = stmt.getResultSet();

				while (rs.next()) {
		            String temp_period = rs.getString(1);
		            cal_afp_array.put(temp_period, rs.getBigDecimal(2));
		            cal_meuc_array.put(temp_period, rs.getBigDecimal(3));
		            cal_heuc_array.put(temp_period, rs.getBigDecimal(4));
		            cal_usep_array.put(temp_period, rs.getBigDecimal(5));
		            cal_heur_array.put(temp_period, rs.getBigDecimal(6));
		            cal_hlcu_array.put(temp_period, rs.getBigDecimal(7));
		        }
				rs.close();
				stmt.close();
		    }

		    // display "11"
		    // ************** CSV Input Parameters For Vesting Contract *****************
		    /*Decimal vesting_HP;
		    Decimal vesting_HQ;
		    String vesting_name = "";
		    String vesting_selling_id = "";
		    String vesting_purchasing_id = "";
		    int vesting_interval;*/

		    // **************************************************************************
		    // display "22 : size of vesting contract: " + length(contractList_vesting)
		    // ********************** Bilateral parameters ****************
		    /*Decimal bilateral_BAQ_purchased = 0;
		    Decimal bilateral_BAQ_sold = 0;
		    Decimal bilateral_BFQ_purchased = 0;
		    Decimal bilateral_BFQ_sold = 0;
		    Decimal bilateral_BIF_purchased = 0;
		    Decimal bilateral_BIF_sold = 0;
		    Decimal bilateral_BIQ_purchased = 0;
		    Decimal bilateral_BIQ_sold = 0;
		    Decimal bilateral_BRQ_purchased = 0;
		    Decimal bilateral_BRQ_sold = 0;
		    Decimal bilateral_BWF_purchased = 0;
		    Decimal bilateral_BWF_sold = 0;
		    Decimal bilateral_BWQ_purchased = 0;
		    Decimal bilateral_BWQ_sold = 0;
		    String bilateral_name = "";
		    String bilateral_selling_id = "";
		    String bilateral_buying_id = "";
		    int bilateral_interval;*/

		    // ******************************************************
		    // ******** interactive parameters******
		    /*String bilateral_version = "";
		    String bilateral_contract_id = "";
		    String bilateral_contract_type = "";
		    String bilateral_node_id = "";*/

		    // *************************************
		    // begin Interval Process	
		    // 8.0.01 Changes
		    int interval_MFP_count = 0;
		    int interval_delta_WCQ_count = 0;
		    int interval_delta_WEQ_count = 0;
		    int interval_delta_WFQ_count = 0;
		    int interval_delta_WMQ_count = 0;
		    int interval_delta_WDQ_count = 0;

		    // sqlCommand1 = "select ID, RETAILER_ID from NEM.NEM_SETTLEMENT_ACCOUNTS where VERSION=?"
		    sqlCommand = "select  sac.ID ID,sac.RETAILER_ID RETAILER_ID, decode(count(mep1.id),0,0,1) acc_mep_nde from NEM.NEM_SETTLEMENT_ACCOUNTS sac, (Select nsp.ID, nde.sac_id, nde.sac_version from nem.nem_settlement_prices nsp, nem.nem_nodes nde " + 
		                  "where nsp.settlement_date = ? and nsp.price_type = 'MEP' and nsp.version = ? and NSP.NDE_ID = nde.id and nsp.nde_version = nde.version and nde.version = ?) mep1 where sac.VERSION=? " + 
		                  "and mep1.sac_id (+) = sac.ID and mep1.sac_version (+) = sac.version group by  sac.ID,sac.RETAILER_ID ";
	        stmt = conn.prepareStatement(sqlCommand);
			//stmt.setString(1, qdf.format(params.sqlSettlementDate));
			stmt.setString(1, sqlsd);
	        stmt.setString(2, runPackage.mcPricePkgVer);
	        stmt.setString(3, version);
	        stmt.setString(4, version);
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        account_id = rs.getString(1);
		        String interval_EGA_id = rs.getString(2);
		        String interval_gen_node = rs.getString(3);

		        // 8.0.01 Changes        
		        //String interval_account = account_id;
		        int pos = 1;

		        // //////////////////////////////////////
		        // get the interval_MFP
		        // //////////////////////////////////////
		        BigDecimal temp_interval_MFP = null;
		        HashMap<Integer, BigDecimal> interval_MFP_array = new HashMap<Integer, BigDecimal>();

		        String sqlCommand3 = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES " + 
		                      "WHERE SETTLEMENT_DATE = ? AND PRICE_TYPE = ? and " + 
		                      "VERSION = ? ORDER BY PERIOD ";
		        stmt1 = conn.prepareStatement(sqlCommand3);
		        //stmt1.setString(1, qdf.format(input_date));
		        stmt1.setString(1, sqlsd);
				stmt1.setString(2, "MFP");
				stmt1.setString(3, runPackage.mcPricePkgVer);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_MFP = rs1.getBigDecimal(1);
		            interval_MFP_array.put(pos, temp_interval_MFP);

		            pos = pos + 1;
		            interval_MFP_count = interval_MFP_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_MFP == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_MFP_array.put(i, BigDecimal.ZERO);

	                    interval_MFP_count = interval_MFP_count + 1;
		            }
		        }

		        // logMessage "interval_MFP_count is" + interval_MFP_count  using severity = WARNING
		        pos = 1;

		        // //////////////////////////////////////
		        // get the interval_delta_WCQ
		        // //////////////////////////////////////
		        BigDecimal temp_interval_delta_WCQ = null;
		        HashMap<Integer, BigDecimal> interval_delta_WCQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand3 = "SELECT rstq.quantity - pstq.quantity AS delta_weq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.sac_id = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type AND " + 
		                      "rstq.sac_id = pstq.sac_id AND rstq.sac_version = pstq.sac_version " + 
		                      "ORDER BY rstq.PERIOD";
		        stmt1 = conn.prepareStatement(sqlCommand3);
				//stmt1.setString(1, qdf.format(input_date));
				stmt1.setString(1, sqlsd);
				stmt1.setString(2, account_id);
				stmt1.setString(3, "WCQ");
				stmt1.setString(4, currMsslQtyVersion);
				stmt1.setString(5, lastRunMsslQtyVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_delta_WCQ = rs1.getBigDecimal(1);
		            interval_delta_WCQ_array.put(pos, temp_interval_delta_WCQ);

		            pos = pos + 1;
		            interval_delta_WCQ_count = interval_delta_WCQ_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_delta_WCQ == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_delta_WCQ_array.put(i, BigDecimal.ZERO);

	                    interval_delta_WCQ_count = interval_delta_WCQ_count + 1;
	                }
		        }

		        pos = 1;

		        // //////////////////////////////////////
		        // get the interval_delta_WEQ
		        // //////////////////////////////////////
		        BigDecimal temp_interval_delta_WEQ = null;
		        HashMap<Integer, BigDecimal> interval_delta_WEQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand3 = "SELECT rstq.quantity - pstq.quantity AS delta_weq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.sac_id = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type AND " + 
		                      "rstq.sac_id = pstq.sac_id AND rstq.sac_version = pstq.sac_version " + 
		                      "ORDER BY rstq.PERIOD";
		        stmt1 = conn.prepareStatement(sqlCommand3);
				//stmt1.setString(1, qdf.format(input_date));
				stmt1.setString(1, sqlsd);
				stmt1.setString(2, account_id);
				stmt1.setString(3, "WEQ");
				stmt1.setString(4, currMsslQtyVersion);
				stmt1.setString(5, lastRunMsslQtyVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_delta_WEQ = rs1.getBigDecimal(1);
		            interval_delta_WEQ_array.put(pos, temp_interval_delta_WEQ);

		            pos = pos + 1;
		            interval_delta_WEQ_count = interval_delta_WEQ_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_delta_WEQ == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_delta_WEQ_array.put(i, BigDecimal.ZERO);

	                    interval_delta_WEQ_count = interval_delta_WEQ_count + 1;
		            }
		        }

		        // //////////////////////////////////////
		        // get the interval_delta_WFQ
		        // //////////////////////////////////////
		        pos = 1;
		        BigDecimal temp_interval_delta_WFQ = null;
		        HashMap<Integer, BigDecimal> interval_delta_WFQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand3 = "SELECT rstq.quantity - pstq.quantity AS delta_weq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.sac_id = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type AND " + 
		                      "rstq.sac_id = pstq.sac_id AND rstq.sac_version = pstq.sac_version " + 
		                      "ORDER BY rstq.PERIOD";
		        stmt1 = conn.prepareStatement(sqlCommand3);
				//stmt1.setString(1, qdf.format(input_date));
				stmt1.setString(1, sqlsd);
				stmt1.setString(2, account_id);
				stmt1.setString(3, "WFQ");
				stmt1.setString(4, currMsslQtyVersion);
				stmt1.setString(5, lastRunMsslQtyVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_delta_WFQ = rs1.getBigDecimal(1);
		            interval_delta_WFQ_array.put(pos, temp_interval_delta_WFQ);

		            pos = pos + 1;
		            interval_delta_WFQ_count = interval_delta_WFQ_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_delta_WFQ == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_delta_WFQ_array.put(i, BigDecimal.ZERO);

	                    interval_delta_WFQ_count = interval_delta_WFQ_count + 1;
		            }
		        }

		        // //////////////////////////////////////
		        // get the interval_delta_WMQ
		        // //////////////////////////////////////
		        pos = 1;
		        BigDecimal temp_interval_delta_WMQ = null;
		        HashMap<Integer, BigDecimal> interval_delta_WMQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand3 = "SELECT rstq.quantity - pstq.quantity AS delta_weq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.sac_id = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type AND " + 
		                      "rstq.sac_id = pstq.sac_id AND rstq.sac_version = pstq.sac_version " + 
		                      "ORDER BY rstq.PERIOD";
		        stmt1 = conn.prepareStatement(sqlCommand3);
				//stmt1.setString(1, qdf.format(input_date));
				stmt1.setString(1, sqlsd);
				stmt1.setString(2, account_id);
				stmt1.setString(3, "WMQ");
				stmt1.setString(4, currMsslQtyVersion);
				stmt1.setString(5, lastRunMsslQtyVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_delta_WMQ = rs1.getBigDecimal(1);
		            interval_delta_WMQ_array.put(pos, temp_interval_delta_WMQ);

		            pos = pos + 1;
		            interval_delta_WMQ_count = interval_delta_WMQ_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_delta_WMQ == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_delta_WMQ_array.put(i, BigDecimal.ZERO);

	                    interval_delta_WMQ_count = interval_delta_WMQ_count + 1;
		            }
		        }

		        // //////////////////////////////////////
		        // get the interval_delta_WDQ
		        // //////////////////////////////////////
		        pos = 1;
		        BigDecimal temp_interval_delta_WDQ = null;
		        HashMap<Integer, BigDecimal> interval_delta_WDQ_array = new HashMap<Integer, BigDecimal>();

		        sqlCommand3 = "SELECT rstq.quantity - pstq.quantity AS delta_weq, rstq.period " + 
		                      "FROM NEM_SETTLEMENT_QUANTITIES pstq, NEM_SETTLEMENT_QUANTITIES rstq " + 
		                      "WHERE rstq.settlement_date = ? AND " + 
		                      "rstq.sac_id = ? AND rstq.quantity_type = ? AND rstq.VERSION = ? AND " + 
		                      "pstq.VERSION = ? AND rstq.settlement_date = pstq.settlement_date AND " + 
		                      "rstq.period = pstq.period AND rstq.quantity_type = pstq.quantity_type AND " + 
		                      "rstq.sac_id = pstq.sac_id AND rstq.sac_version = pstq.sac_version " + 
		                      "ORDER BY rstq.PERIOD";
		        stmt1 = conn.prepareStatement(sqlCommand3);
				//stmt1.setString(1, qdf.format(input_date));
				stmt1.setString(1, sqlsd);
				stmt1.setString(2, account_id);
				stmt1.setString(3, "WDQ");
				stmt1.setString(4, currMsslQtyVersion);
				stmt1.setString(5, lastRunMsslQtyVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();

				while (rs1.next()) {
		            temp_interval_delta_WDQ = rs1.getBigDecimal(1);
		            interval_delta_WDQ_array.put(pos, temp_interval_delta_WDQ);

		            pos = pos + 1;
		            interval_delta_WDQ_count = interval_delta_WDQ_count + 1;
		        }
				rs1.close();
				stmt1.close();

		        if (temp_interval_delta_WDQ == null) {
	                for (int i=1; i <= totalPeriod; i++) {
	                    interval_delta_WDQ_array.put(i, BigDecimal.ZERO);

	                    interval_delta_WDQ_count = interval_delta_WDQ_count + 1;
		            }
		        }

		        //pos = 0;
		        {
		            int i = 1;

		            while (i <= totalPeriod) {
		            	
						Period pd = new Period();
						pd.setPeriodId(i);
				        pd.setEgaId(interval_EGA_id != null? interval_EGA_id: "");
				        pd.setGenNode(interval_gen_node.equals("1"));
				        pd.setAccountId(account_id);
						
						pd.setTotalR(false);
						Market mkt = data.getMarket().get(pd.getPeriodId());
						pd.setUsep(mkt.getUsep());

		                if (interval_MFP_array.size() == 0) {
		                	pd.setMfp(BigDecimal.ZERO);
		                } else {
		                	pd.setMfp(interval_MFP_array.get(i));
		                }

		                if (interval_delta_WCQ_array.size() == 0) {
		                	pd.setDeltaWcq(BigDecimal.ZERO);
		                } else {
		                	pd.setDeltaWcq(interval_delta_WCQ_array.get(i));
		                }

		                if (interval_delta_WEQ_array.size() == 0) {
		                	pd.setDeltaWeq(BigDecimal.ZERO);
		                } else {
		                	pd.setDeltaWeq(interval_delta_WEQ_array.get(i));
		                }

		                if (interval_delta_WFQ_array.size() == 0) {
		                	pd.setDeltaWfq(BigDecimal.ZERO);
		                } else {
		                	pd.setDeltaWfq(interval_delta_WFQ_array.get(i));
		                }

		                if (interval_delta_WMQ_array.size() == 0) {
		                	pd.setDeltaWmq(BigDecimal.ZERO);
		                } else {
		                	pd.setDeltaWmq(interval_delta_WMQ_array.get(i));
		                }

		                /*if (interval_EGA_id == null) {
		                    interval_EGA_id = "";
		                }*/

		                if (cal_afp_array.size() == 0) {
		                	pd.setImpAfp(BigDecimal.ZERO);
		                } else {
		                	pd.setImpAfp(cal_afp_array.get(String.valueOf(i)));
		                }

		                if (cal_heuc_array.size() == 0) {
		                	pd.setImpHeuc(BigDecimal.ZERO);
		                } else {
		                	pd.setImpHeuc(cal_heuc_array.get(String.valueOf(i)));
		                }

		                if (cal_meuc_array.size() == 0) {
		                	pd.setImpMeuc(BigDecimal.ZERO);
		                } else {
		                	pd.setImpMeuc(cal_meuc_array.get(String.valueOf(i)));
		                }

		                if (cal_usep_array.size() == 0) {
		                	pd.setImpUsep(BigDecimal.ZERO);
		                } else {
		                	pd.setImpUsep(cal_usep_array.get(String.valueOf(i)));
		                }

		                // DRCAP
		                if (interval_delta_WDQ_array.size() == 0) {
		                	pd.setDeltaWdq(BigDecimal.ZERO);
		                } else {
		                	pd.setDeltaWdq(interval_delta_WDQ_array.get(i));
		                }

		                if (cal_heur_array.size() == 0) {
		                	pd.setImpHeur(BigDecimal.ZERO);
		                } else {
		                	pd.setImpHeur(cal_heur_array.get(String.valueOf(i)));
		                }

		                if (cal_hlcu_array.size() == 0) {
		                	pd.setImpHlcu(BigDecimal.ZERO);
		                } else {
		                	pd.setImpHlcu(cal_hlcu_array.get(String.valueOf(i)));
		                }
		                
		                pd.setImpEmca(interval_imp_EMCA);
		                pd.setImpPsoa(interval_imp_PSOA);
		                
		                data.getPeriod().put(pd.getKey(), pd);

		                /*String[] columnResults;

		                if (haleyVersion >= 8.0) {
		                    columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, String.valueOf(o : interval_delta_WCQ), String.valueOf(o : interval_delta_WEQ), interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i + 1), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, interval_WCQ, interval_WEQ, interval_WPQ, interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, interval_WFQ, // [EG]
		                                    interval_WMQ, String.valueOf(o : interval_delta_WMQ), String.valueOf(o : interval_delta_WFQ), interval_gen_node, String.valueOf(o : interval_delta_WDQ), interval_LCP, interval_WDQ, interval_imp_HEUR, interval_imp_HLCU };
		                }
		                else {
		                    if (haleyVersion >= 7.2) {
		                        columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, String.valueOf(o : interval_delta_WCQ), String.valueOf(o : interval_delta_WEQ), interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i + 1), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, interval_WCQ, interval_WEQ, interval_WPQ, interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, interval_WFQ, // [EG]
		                                        interval_WMQ, String.valueOf(o : interval_delta_WMQ), String.valueOf(o : interval_delta_WFQ), interval_gen_node };
		                    }
		                    else {
		                        columnResults = { interval_account, interval_BAQ_purchased, interval_BAQ_sold, interval_BEQ_purchased, interval_BEQ_sold, interval_BESC, interval_BFQ_purchased, interval_BFQ_sold, interval_BIF_purchased, interval_BIF_sold, interval_BWF_purchased, interval_BWF_sold, String.valueOf(o : interval_delta_WCQ), String.valueOf(o : interval_delta_WEQ), interval_comp_EMCA, interval_comp_PSOA, interval_EGA_IEQ, interval_EGA_WEQ, interval_EGA_WPQ, interval_EUA, interval_FCC, interval_FEQ, interval_FSC, interval_FSD, interval_GESC, interval_GESCE, interval_GESCN, interval_GESCP, interval_GMEE, interval_GMEF, interval_HEUA, interval_HEUSA, interval_IEQ, interval_IEQP, interval_imp_EMCA, interval_imp_PSOA, interval_inc_NMEA, interval_v_FSC, interval_v_GESC, interval_v_GESCN, interval_v_GMEE, interval_v_inc_NMEA, interval_v_LESD, interval_v_LESDN, interval_v_NASC, interval_v_NESC, interval_v_NMEA, interval_v_NPSC, interval_v_RSC, interval_LESD, interval_LESDN, interval_LESDP, interval_LMEE, interval_LMEF, interval_MEP, interval_MEUSA, String.valueOf(o : interval_MFP), interval_MRP, interval_NASC, interval_NEAA, interval_NEAD, interval_NEGC, interval_NEGC_IEQ, interval_NELC, interval_NESC, interval_NFSC, interval_NMEA, interval_nde_count, interval_NPSC, interval_NRSC, interval_NTSC, String.valueOf(o : i + 1), interval_a_FSD, interval_a_GESC, interval_a_GESCE, interval_a_GESCP, interval_a_GMEF, interval_a_HEUSA, interval_a_inc_NMEA, interval_a_LESD, interval_a_LESDP, interval_a_LMEE, interval_a_LMEF, interval_a_MEUSA, interval_a_NASC, interval_a_NESC, interval_a_NMEA, interval_a_NPSC, interval_a_RSD, interval_RCC, interval_RSA, interval_RSC, interval_RSD, interval_max_IEQ, interval_total_BESC, interval_total_FSC, interval_total_FSD, interval_total_GESC, interval_total_GESCE, interval_total_GESCN, interval_total_GESCP, interval_total_GMEE, interval_total_GMEF, interval_total_HEUSA, interval_total_LESD, interval_total_LESDN, interval_total_LESDP, interval_total_LMEE, interval_total_LMEF, interval_total_MEUSA, interval_total_NASC, interval_total_NESC, interval_total_NFSC, interval_total_NMEA, interval_total_NPSC, interval_total_NRSC, interval_total_inc_NMEA, interval_total_RCC, interval_total_RSC, interval_total_RSD, interval_TTE, interval_USEP, interval_VCRP, interval_VCRPK, interval_VCSC, interval_VCSCK, interval_WCQ, interval_WEQ, interval_WPQ, interval_facility_RSC, interval_GMEA, interval_LMEA, interval_a_LMEA, interval_a_GMEA, interval_EGA_id, interval_FEE_TOTAL, interval_imp_AFP, interval_imp_HEUC, interval_imp_MEUC, interval_imp_USEP, interval_WFQ, // [EG]
		                                        interval_WMQ, String.valueOf(o : interval_delta_WMQ), String.valueOf(o : interval_delta_WFQ) };
		                    }
		                }

		                csvWriter.writeLine(values : columnResults);

		                String[] columnResults3 = { "", "", "", account_id, String.valueOf(i) };
		                csvWriter2.writeLine(values : columnResults3);*/
		                
	                    Vesting vesting = new Vesting();
	                    vesting.setHp(null);
	                    vesting.setHq(null);
	                    vesting.setContractName("");
	                    vesting.setAccountId(account_id);
	                    vesting.setPeriodId(String.valueOf(i));
	                    
	                    data.getVesting().put(vesting.getKey(), vesting);

		                /*if (interval_MFP_array.length != 0) {
		                    interval_MFP_array.delete(index : interval_MFP_array.indexOf(element : interval_MFP));
		                }

		                if (interval_delta_WCQ_array.length != 0) {
		                    interval_delta_WCQ_array.delete(index : interval_delta_WCQ_array.indexOf(element : interval_delta_WCQ));
		                }

		                if (interval_delta_WEQ_array.length != 0) {
		                    interval_delta_WEQ_array.delete(index : interval_delta_WEQ_array.indexOf(element : interval_delta_WEQ));
		                }

		                if (interval_delta_WFQ_array.length != 0) {
		                    interval_delta_WFQ_array.delete(index : interval_delta_WFQ_array.indexOf(element : interval_delta_WFQ));
		                }

		                if (interval_delta_WMQ_array.length != 0) {
		                    interval_delta_WMQ_array.delete(index : interval_delta_WMQ_array.indexOf(element : interval_delta_WMQ));
		                }

		                if (interval_delta_WDQ_array.length != 0) {
		                    interval_delta_WDQ_array.delete(index : interval_delta_WDQ_array.indexOf(element : interval_delta_WDQ));
		                }*/

		                i = i + 1;
		            }
		        }

		        // logMessage "interval_delta_WCQ_count is" + interval_delta_WCQ_count
		    }
			rs.close();
			stmt.close();

			
	        String msg = "Successfully Generated Entity CSV File: Interval, Bilateral, Vesting, TVC and FSC";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
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

	public static void readClasses(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, SettlementData data, DataSource ds)
			throws Exception {

		String msgStep = "DatabaseReader.readClasses()";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		    
		try {
			conn = ds.getConnection();
	
			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
		                                   msgStep, "Generating Entity Class", "");

		    // get all ACG_ID
		    String sqlACG = "select ID, NAME, ANCILLARY_TYPE from NEM.NEM_ANCILLARY_GROUPS where VERSION=? AND ANCILLARY_TYPE=?";
	        stmt = conn.prepareStatement(sqlACG);
			stmt.setString(1, runPackage.standingVersion);
			stmt.setString(2, "RSV");
			stmt.executeQuery();
			rs = stmt.getResultSet();

			while (rs.next()) {
		        String acg_name = rs.getString(2);

		        for (int i = 1; i <= totalPeriod; i++) {
		            
		            RsvClass clz = new RsvClass();
		            clz.setReserveClass(acg_name);
		            clz.setPeriodId(String.valueOf(i));
		            
		            data.getRsvClass().put(clz.getKey(), clz);
		        }
		    }
			rs.close();
			stmt.close();

			
	        String msg = "Successfully Generated Entity CSV File: Class";

	        logger.info(logPrefix + msg);
	
	        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", 
	                                       msgStep, msg, "");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, params.runEveId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
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
