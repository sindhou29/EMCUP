package com.emc.sett.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.sql.DataSource;

import com.emc.sett.common.SettlementRunException;

public class CSVInputer {
	
	public DataSource ds = null;
	public Date settDate = null;
	public String mcPricePkgVer = null;
	public String msslQtyPkgVer = null;
	public int totalPeriod = 48;
	
    //private static SimpleDateFormat qdf = new SimpleDateFormat("dd-MMM-yyyy");
    
    private static final String DATETIME_FORMAT_STR = "dd-MMM-yyyy";
    
	private HashMap<Integer, BigDecimal> llcp = new HashMap<Integer, BigDecimal>();
	private HashMap<Integer, BigDecimal> lmfp = new HashMap<Integer, BigDecimal>();
	
	private HashMap<Integer, BigDecimal> eq = new HashMap<Integer, BigDecimal>();
	
	private HashMap<String, HashMap<Integer, BigDecimal>> lwcq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lweq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lwmq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lwfq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lwpq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lwdq = new HashMap<String, HashMap<Integer, BigDecimal>>();
	private HashMap<String, HashMap<Integer, BigDecimal>> lieq = new HashMap<String, HashMap<Integer, BigDecimal>>();

	// get interval IXQ
	public void readAllIXQs() throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt =  null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			ZonedDateTime zdt = ZonedDateTime.of(settDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
			String sqlsd = sqlFormatter.format(zdt);
			
			// Quantity Query join with NODES
			String sqlQty = "SELECT nde.SAC_ID,sq.period,sq.QUANTITY_TYPE,SUM(sq.QUANTITY) FROM NEM.NEM_SETTLEMENT_QUANTITIES sq, NEM.NEM_NODES nde " + 
			"WHERE sq.SETTLEMENT_DATE = ? AND sq.sac_id is null AND " + 
			"sq.VERSION = ? AND nde.ID = sq.nde_id AND nde.VERSION = sq.nde_version " +
			"GROUP BY nde.SAC_ID,sq.QUANTITY_TYPE,sq.period ORDER BY SAC_ID,QUANTITY_TYPE,PERIOD ";
	        stmt = conn.prepareStatement(sqlQty);
			//stmt.setString(1, qdf.format(settDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, msslQtyPkgVer);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				String sac = rs.getString(1);
				String pd = rs.getString(2);
				String qType = rs.getString(3);
				BigDecimal qty = rs.getBigDecimal(4);
				
				switch (qType) {
				case "IEQ":
					fillIEQ(sac, pd, qty);
					break;
				default:
					break;
				}
			}
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

	// get the interval_LCP
	public HashMap<Integer, BigDecimal> getIntervalLCP() {
		return llcp;
	}

	// get the interval_MFP
	public HashMap<Integer, BigDecimal> getIntervalMFP() {
		return lmfp;
	}

	// get the interval_MFP
	public void readAllPrices() throws Exception {
		
		BigDecimal temp_interval_LCP = null;
		HashMap<String, BigDecimal> temp_LCP_array = new HashMap<String, BigDecimal>();
		BigDecimal temp_interval_MFP = null;
		int pos = 1;
		int count = 0;

		Connection conn = null;
		PreparedStatement stmt =  null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			ZonedDateTime zdt = ZonedDateTime.of(settDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
			String sqlsd = sqlFormatter.format(zdt);
			
			// create empty Quantities
		    for (int i = 1; i <= totalPeriod; i++) {
		        eq.put(i, BigDecimal.ZERO);
		    }
			
			// LCP Price Query
			String sqlLCP = "SELECT NVL(c1.SYR_LCP, 0), c1.TRADING_PERIOD FROM NEM.NEM_NON_MCE_DR_CALCULATIONS c1 " + 
			"WHERE c1.RESULT_TYPE = 'SYR' AND c1.TRADING_DATE = ? AND c1.PKG_VERSION = " + 
			"(SELECT MAX(to_number(PKG_VERSION)) FROM NEM.NEM_NON_MCE_DR_CALCULATIONS c2 " + 
			" WHERE c2.RESULT_TYPE=c1.RESULT_TYPE AND c2.TRADING_DATE=c1.TRADING_DATE AND c2.TRADING_PERIOD=c1.TRADING_PERIOD) " + 
			"ORDER BY TRADING_PERIOD ";
	        stmt = conn.prepareStatement(sqlLCP);
			//stmt.setString(1, qdf.format(settDate));
			stmt.setString(1, sqlsd);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
			    temp_interval_LCP = rs.getBigDecimal(1);
			    String period = rs.getString(2);
			    temp_LCP_array.put(period, temp_interval_LCP);
			}
	
		    for (int i = 1; i <= totalPeriod; i++) {
			    String interval = String.valueOf(i);
			    temp_interval_LCP = temp_LCP_array.get(interval);
	
			    if (temp_interval_LCP == null) {
			        temp_interval_LCP = BigDecimal.ZERO;
			    }
	
			    llcp.put(i, temp_interval_LCP);
			}
		    rs.close();
		    stmt.close();
			
			// MFP Price Query
			String sqlPrice = "SELECT PRICE FROM NEM.NEM_SETTLEMENT_PRICES " + 
			"WHERE SETTLEMENT_DATE = ? AND PRICE_TYPE = ? and VERSION = ? " + 
			"ORDER BY PERIOD ";
	        stmt = conn.prepareStatement(sqlPrice);
			//stmt.setString(1, qdf.format(settDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, "MFP");
			stmt.setString(3, mcPricePkgVer);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
			    temp_interval_MFP = rs.getBigDecimal(1);
			    lmfp.put(pos, temp_interval_MFP);
	
			    pos = pos + 1;
			    count = count + 1;
			}
	
			if (temp_interval_MFP == null) {
			    temp_interval_MFP = BigDecimal.ZERO;
	
			    for (int i = 1; i <= totalPeriod; i++) {
			        lmfp.put(i, temp_interval_MFP);
	
			        count = count + 1;
			    }
			}
		    rs.close();
		    stmt.close();
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

		// logMessage "interval's MFP count is" + count  using severity = WARNING
	}
	
	// get the interval_WXQ
	public HashMap<Integer, BigDecimal> getIntervalXXQ(String sac, String qType) throws Exception {
		
		HashMap<Integer, BigDecimal> wxq = null;
		
		switch (qType) {
		case "WCQ":
			wxq = lwcq.get(sac);
			break;
		case "WEQ":
			wxq = lweq.get(sac);
			break;
		case "WMQ":
			wxq = lwmq.get(sac);
			break;
		case "WFQ":
			wxq = lwfq.get(sac);
			break;
		case "WPQ":
			wxq = lwpq.get(sac);
			break;
		case "WDQ":
			wxq = lwdq.get(sac);
			break;
		case "IEQ":
			wxq = lieq.get(sac);
			break;
		default:
			break;
		}
		
		if (wxq == null) {
			return eq;
		} else {
			if (wxq.size() != totalPeriod) {
			    throw new SettlementRunException("Incorrect number of " + qType + " for account " + sac, CSVInputer.class + ".getIntervalXXQ()");
			}
			return wxq;
		}
	}
	
	// get the interval_WXQ
	public void readAllWXQs() throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt =  null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			ZonedDateTime zdt = ZonedDateTime.of(settDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
			String sqlsd = sqlFormatter.format(zdt);
			
			// Quantity Query
			String sqlQty = "SELECT SAC_ID,PERIOD,QUANTITY_TYPE,QUANTITY FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
			"WHERE SETTLEMENT_DATE = ? AND SAC_ID IS NOT NULL AND " + 
			"VERSION = ? ORDER BY SAC_ID,QUANTITY_TYPE,PERIOD ";
	        stmt = conn.prepareStatement(sqlQty);
			//stmt.setString(1, qdf.format(settDate));
			stmt.setString(1, sqlsd);
			stmt.setString(2, msslQtyPkgVer);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				String sac = rs.getString(1);
				String pd = rs.getString(2);
				String qType = rs.getString(3);
				BigDecimal qty = rs.getBigDecimal(4);
				
				switch (qType) {
				case "WCQ":
					fillWCQ(sac, pd, qty);
					break;
				case "WEQ":
					fillWEQ(sac, pd, qty);
					break;
				case "WMQ":
					fillWMQ(sac, pd, qty);
					break;
				case "WFQ":
					fillWFQ(sac, pd, qty);
					break;
				case "WPQ":
					fillWPQ(sac, pd, qty);
					break;
				case "WDQ":
					fillWDQ(sac, pd, qty);
					break;
				default:
					break;
				}
			}
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
	
	// get the interval_WCQ
	private void fillWCQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lwcq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lwcq.put(sac, array);
		}
	}
	
	// get the interval_WEQ
	private void fillWEQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lweq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lweq.put(sac, array);
		}
	}
	
	// get the interval_WMQ
	private void fillWMQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lwmq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lwmq.put(sac, array);
		}
	}
	
	// get the interval_WFQ
	private void fillWFQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lwfq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lwfq.put(sac, array);
		}
	}
	
	// get the interval_WPQ
	private void fillWPQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lwpq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lwpq.put(sac, array);
		}
	}
	
	// get the interval_WDQ
	private void fillWDQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lwdq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lwdq.put(sac, array);
		}
	}
	
	// get the interval_IEQ
	private void fillIEQ(String sac, String pd, BigDecimal qty) throws Exception {
		
		HashMap<Integer, BigDecimal> array = lieq.get(sac);
		if (array != null) {
			array.put(Integer.parseInt(pd), qty);
		} else {
			array = new HashMap<Integer, BigDecimal>();
			array.put(Integer.parseInt(pd), qty);
			lieq.put(sac, array);
		}
	}
}
