package com.emc.sett.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.emc.sett.model.AdjustmentT;

public class Adjustment extends AdjustmentT {

	public Adjustment() {

	}

	public Adjustment(Adjustment from) {
		
		this.gmee = from.gmee;
		this.gmef = from.gmef;
		this.lmee = from.lmee;
		this.lmef = from.lmef;
		this.nmea = from.nmea;
		this.gstRate = from.gstRate;
		this.opGstGmef = from.opGstGmef;
		this.opGstLmee = from.opGstLmee;
		this.opGstLmef = from.opGstLmef;
		this.opGstNmea = from.opGstNmea;
		this.name = from.name;
		this.runType = from.runType;
	    this.settDate = from.settDate;
		this.settId = from.settId;
		this.ipGstGmee = from.ipGstGmee;
		this.ipGstNmea = from.ipGstNmea;
		this.periodId = from.periodId;
		this.adjRequired = from.adjRequired;
		this.version = from.version;
		this.taxable = from.taxable;
	}
	
	@SuppressWarnings("unused")
	public void initInput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String rs_adj_gmee = line[0];
		String rs_adj_gmef = line[1];
		String rs_adj_lmee = line[2];
		String rs_adj_lmef = line[3];
		String rs_adj_nmea = line[4];
		String rs_adj_gst_rate = line[5];
		String rs_adj_a_gmef = line[6];
		String rs_adj_a_lmee = line[7];
		String rs_adj_a_lmef = line[8];
		String rs_adj_a_nmea = line[9];
		String rs_adj_name = line[10];
		String rs_adj_run_type = line[11];
		String rs_adj_sett_date = line[12];
		String rs_adj_str_id = line[13];
		String rs_adj_total_gmee = line[14];
		String rs_adj_total_gmef = line[15];
		String rs_adj_total_lmee = line[16];
		String rs_adj_total_lmef = line[17];
		String rs_adj_total_nmea = line[18];
		String rs_adj_v_gmee = line[19];
		String rs_adj_v_nmea = line[20];
		int interval = Integer.parseInt(line[21]);
		String rs_adj_interval = String.format("%1$02d", interval);
		String rs_adj_required = line[22];
		String rs_adj_version = line[23];
		String rs_adj_gst_taxable = line[24];
		
		this.gmee = rs_adj_gmee.length() > 0? new BigDecimal(rs_adj_gmee): null;
		this.gmef = rs_adj_gmef.length() > 0? new BigDecimal(rs_adj_gmef): null;
		this.lmee = rs_adj_lmee.length() > 0? new BigDecimal(rs_adj_lmee): null;
		this.lmef = rs_adj_lmef.length() > 0? new BigDecimal(rs_adj_lmef): null;
		this.nmea = rs_adj_nmea.length() > 0? new BigDecimal(rs_adj_nmea): null;
		this.gstRate = rs_adj_gst_rate.length() > 0? new BigDecimal(rs_adj_gst_rate): null;
		this.opGstGmef = rs_adj_a_gmef.length() > 0? new BigDecimal(rs_adj_a_gmef): null;
		this.opGstLmee = rs_adj_a_lmee.length() > 0? new BigDecimal(rs_adj_a_lmee): null;
		this.opGstLmef = rs_adj_a_lmef.length() > 0? new BigDecimal(rs_adj_a_lmef): null;
		this.opGstNmea = rs_adj_a_nmea.length() > 0? new BigDecimal(rs_adj_a_nmea): null;
		this.name = rs_adj_name;
		this.runType = rs_adj_run_type;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(rs_adj_sett_date));
	    this.settDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
		this.settId = rs_adj_str_id;
		this.ipGstGmee = rs_adj_v_gmee.length() > 0? new BigDecimal(rs_adj_v_gmee): null;
		this.ipGstNmea = rs_adj_v_nmea.length() > 0? new BigDecimal(rs_adj_v_nmea): null;
		this.periodId = rs_adj_interval;
		this.adjRequired = rs_adj_required.equals("1");
		this.version = rs_adj_version;
		this.taxable = rs_adj_gst_taxable.equals("1");
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return settId + periodId + name;
	}

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String rs_adj_gmee = line[0];
		String rs_adj_acctg_gmef = line[1];
		String rs_adj_acctg_lmee = line[2];
		String rs_adj_acctg_lmef = line[3];
		String rs_adj_nmea = line[4];
		String rs_adj_gst_rate = line[5];
		String rs_adj_a_gmef = line[6];
		String rs_adj_a_lmee = line[7];
		String rs_adj_a_lmef = line[8];
		String rs_adj_a_nmea = line[9];
		String rs_adj_name = line[10];
		String rs_adj_run_type = line[11];
		String rs_adj_sett_date = line[12];
		String rs_adj_str_id = line[13];
		String rs_adj_total_gmee = line[14];
		String rs_adj_total_gmef = line[15];
		String rs_adj_total_lmee = line[16];
		String rs_adj_total_lmef = line[17];
		String rs_adj_total_nmea = line[18];
		String rs_adj_v_gmee = line[19];
		String rs_adj_v_nmea = line[20];
		int interval = Integer.parseInt(line[21]);
		String rs_adj_interval = String.format("%1$02d", interval);
		String rs_adj_required = line[22];
		String rs_adj_version = line[23];
		String rs_adj_gst_taxable = line[24];
		
		this.gmee = rs_adj_gmee.length() > 0? new BigDecimal(rs_adj_gmee): null;
		this.accountingGmef = rs_adj_acctg_gmef.length() > 0? new BigDecimal(rs_adj_acctg_gmef): null;
		this.accountingLmee = rs_adj_acctg_lmee.length() > 0? new BigDecimal(rs_adj_acctg_lmee): null;
		this.accountingLmef = rs_adj_acctg_lmef.length() > 0? new BigDecimal(rs_adj_acctg_lmef): null;
		this.nmea = rs_adj_nmea.length() > 0? new BigDecimal(rs_adj_nmea): null;
		this.gstRate = rs_adj_gst_rate.length() > 0? new BigDecimal(rs_adj_gst_rate): null;
		this.opGstGmef = rs_adj_a_gmef.length() > 0? new BigDecimal(rs_adj_a_gmef): null;
		this.opGstLmee = rs_adj_a_lmee.length() > 0? new BigDecimal(rs_adj_a_lmee): null;
		this.opGstLmef = rs_adj_a_lmef.length() > 0? new BigDecimal(rs_adj_a_lmef): null;
		this.opGstNmea = rs_adj_a_nmea.length() > 0? new BigDecimal(rs_adj_a_nmea): null;
		this.name = rs_adj_name;
		this.runType = rs_adj_run_type;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(rs_adj_sett_date));
	    this.settDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
		this.settId = rs_adj_str_id;
		this.totalGmee = rs_adj_total_gmee.length() > 0? new BigDecimal(rs_adj_total_gmee): null;
		this.totalGmef = rs_adj_total_gmef.length() > 0? new BigDecimal(rs_adj_total_gmef): null;
		this.totalLmee = rs_adj_total_lmee.length() > 0? new BigDecimal(rs_adj_total_lmee): null;
		this.totalLmef = rs_adj_total_lmef.length() > 0? new BigDecimal(rs_adj_total_lmef): null;
		this.totalNmea = rs_adj_total_nmea.length() > 0? new BigDecimal(rs_adj_total_nmea): null;
		this.ipGstGmee = rs_adj_v_gmee.length() > 0? new BigDecimal(rs_adj_v_gmee): null;
		this.ipGstNmea = rs_adj_v_nmea.length() > 0? new BigDecimal(rs_adj_v_nmea): null;
		this.periodId = rs_adj_interval;
		this.adjRequired = rs_adj_required.equals("1");
		this.version = rs_adj_version;
		this.taxable = rs_adj_gst_taxable.equals("1");
	}
	
	public String PFCheck(Adjustment in) {

		String result = null;

		if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";

		return result;
	}
	
	public String RSCheck(Adjustment in) {

		String result = null;

		if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";

		return result;
	}
	
	public String equal(Adjustment in) {

		String result = null;

		if (this.accountingGmef != null && in.accountingGmef != null) if (this.accountingGmef.compareTo(in.accountingGmef) != 0) result = "accountingGmef value mismatch";
		if (this.accountingLmee != null && in.accountingLmee != null) if (this.accountingLmee.compareTo(in.accountingLmee) != 0) result = "accountingLmee value mismatch";
		if (this.accountingLmef != null && in.accountingLmef != null) if (this.accountingLmef.compareTo(in.accountingLmef) != 0) result = "accountingLmef value mismatch";
		if (this.opGstNmea != null && in.opGstNmea != null) if (this.opGstNmea.compareTo(in.opGstNmea) != 0) result = "opGstNmea value mismatch";
		if (this.totalGmee != null && in.totalGmee != null) if (this.totalGmee.compareTo(in.totalGmee) != 0) result = "totalGmee value mismatch";
		if (this.totalGmef != null && in.totalGmef != null) if (this.totalGmef.compareTo(in.totalGmef) != 0) result = "totalGmef value mismatch";
		if (this.totalLmee != null && in.totalLmee != null) if (this.totalLmee.compareTo(in.totalLmee) != 0) result = "totalLmee value mismatch";
		if (this.totalLmef != null && in.totalLmef != null) if (this.totalLmef.compareTo(in.totalLmef) != 0) result = "totalLmef value mismatch";
		if (this.totalNmea != null && in.totalNmea != null) if (this.totalNmea.compareTo(in.totalNmea) != 0) result = "totalNmea value mismatch";
		if (this.ipGstNmea != null && in.ipGstNmea != null) if (this.ipGstNmea.compareTo(in.ipGstNmea) != 0) result = "ipGstNmea value mismatch";

		return result;
	}
		
	public String toInputString() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tdStr = "";
		
		if (this.settDate != null) {
			Calendar d = this.settDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			tdStr = formatter.format(d.getTime());
		}
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.gstRate != null? this.gstRate.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				this.name + "," +
				this.runType + "," +
				(this.settDate != null? tdStr: "") + "," +
				this.settId + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				this.periodId + "," +
				(this.adjRequired == true? "1": "0") + "," +
				this.version + "," +
				(this.taxable == true? "1": "0");
		
		return result;
	}
	
	public String toOutputString() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tdStr = "";
		
		if (this.settDate != null) {
			Calendar d = this.settDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			tdStr = formatter.format(d.getTime());
		}
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.accountingGmef != null? this.accountingGmef.toString(): "") + "," +
				(this.accountingLmee != null? this.accountingLmee.toString(): "") + "," +
				(this.accountingLmef != null? this.accountingLmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.gstRate != null? this.gstRate.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				this.name + "," +
				this.runType + "," +
				(this.settDate != null? tdStr: "") + "," +
				this.settId + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				this.periodId + "," +
				(this.adjRequired == true? "1": "0") + "," +
				this.version + "," +
				(this.taxable == true? "1": "0");
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"nmea," +
			"gstRate," +
			"opGstGmef," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstNmea," +
			"name," +
			"runType," +
			"settDate," +
			"settId," +
			"totalGmee," +
			"totalGmef," +
			"totalLmee," +
			"totalLmef," +
			"totalNmea," +
			"ipGstGmee," +
			"ipGstNmea," +
			"periodId," +
			"adjRequired," +
			"version," +
			"taxable";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"gmee," +
			"accountingGmef," +
			"accountingLmee," +
			"accountingLmef," +
			"nmea," +
			"gstRate," +
			"opGstGmef," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstNmea," +
			"name," +
			"runType," +
			"settDate," +
			"settId," +
			"totalGmee," +
			"totalGmef," +
			"totalLmee," +
			"totalLmef," +
			"totalNmea," +
			"ipGstGmee," +
			"ipGstNmea," +
			"periodId," +
			"adjRequired," +
			"version," +
			"taxable";

		return header;
	}
}
