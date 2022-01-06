package com.emc.sett.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.MnmeaT;

public class Mnmea extends MnmeaT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String mnmea_gmee = line[0];
		String mnmea_gmef = line[1];
		String mnmea_lmee = line[2];
		String mnmea_lmef = line[3];
		String mnmea_nmea = line[4];
		String mnmea_rerun_id = line[5];
		int interval = Integer.parseInt(line[6]);
		String mnmea_interval = String.format("%1$02d", interval);
		String mnmea_run_type = line[7];
		String mnmea_sett_date = line[8];
		
		this.settId = mnmea_rerun_id;
		this.periodId = mnmea_interval;
		this.runType = mnmea_run_type;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(mnmea_sett_date));
	    this.settDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return settId + periodId;
	}
    
    public BigDecimal sumIncludedGmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getGmee() != null) {
		    		result = result.add(adj.getGmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedGmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getGmef() != null) {
		    		result = result.add(adj.getGmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedLmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getLmee() != null) {
		    		result = result.add(adj.getLmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedLmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getLmef() != null) {
		    		result = result.add(adj.getLmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getNmea() != null) {
		    		result = result.add(adj.getNmea());
	    		}
	    	}
		}
    	
        return result;
    }

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String mnmea_gmee = line[0];
		String mnmea_gmef = line[1];
		String mnmea_lmee = line[2];
		String mnmea_lmef = line[3];
		String mnmea_nmea = line[4];
		String mnmea_rerun_id = line[5];
		int interval = Integer.parseInt(line[6]);
		String mnmea_interval = String.format("%1$02d", interval);
		String mnmea_run_type = line[7];
		String mnmea_sett_date = line[8];
		
		this.gmee = mnmea_gmee.length() > 0? new BigDecimal(mnmea_gmee): null;
		this.gmef = mnmea_gmef.length() > 0? new BigDecimal(mnmea_gmef): null;
		this.lmee = mnmea_lmee.length() > 0? new BigDecimal(mnmea_lmee): null;
		this.lmef = mnmea_lmef.length() > 0? new BigDecimal(mnmea_lmef): null;
		this.nmea = mnmea_nmea.length() > 0? new BigDecimal(mnmea_nmea): null;
		this.settId = mnmea_rerun_id;
		this.periodId = mnmea_interval;
		this.runType = mnmea_run_type;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(mnmea_sett_date));
	    this.settDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
	}
	
	public String PFCheck(Mnmea in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String RSCheck(Mnmea in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String equal(Mnmea in) {

		String result = null;

		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.gmef != null && in.gmef != null) if (this.gmef.compareTo(in.gmef) != 0) result = "gmef value mismatch";
		if (this.lmee != null && in.lmee != null) if (this.lmee.compareTo(in.lmee) != 0) result = "lmee value mismatch";
		if (this.lmef != null && in.lmef != null) if (this.lmef.compareTo(in.lmef) != 0) result = "lmef value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";

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
				this.settId + "," +
				this.periodId + "," +
				this.runType + "," +
				(this.settDate != null? tdStr: "");
		
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
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				this.settId + "," +
				this.periodId + "," +
				this.runType + "," +
				(this.settDate != null? tdStr: "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"nmea," +
			"settId," +
			"periodId," +
			"runType," +
			"settDate";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"nmea," +
			"settId," +
			"periodId," +
			"runType," +
			"settDate";

		return header;
	}
}
