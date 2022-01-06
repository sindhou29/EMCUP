package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.CnmeaT;

public class Cnmea extends CnmeaT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String cnmea_gmee = line[0];
		String cnmea_gmef = line[1];
		String cnmea_lmee = line[2];
		String cnmea_lmef = line[3];
		String cnmea_nmea = line[4];
		String cnmea_str_id = line[5];
		String cnmea_account = line[6];
		
		this.settId = cnmea_str_id;
		this.accountId = cnmea_account;
	}
	
	public String getKey() {
		return settId + accountId;
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
	public void initOutput(String [] line) {
		
		String cnmea_gmee = line[0];
		String cnmea_acctg_gmef = line[1];
		String cnmea_acctg_lmee = line[2];
		String cnmea_acctg_lmef = line[3];
		String cnmea_nmea = line[4];
		String cnmea_str_id = line[5];
		String cnmea_account = line[6];
		
		this.gmee = cnmea_gmee.length() > 0? new BigDecimal(cnmea_gmee): null;
		this.accountingGmef = cnmea_acctg_gmef.length() > 0? new BigDecimal(cnmea_acctg_gmef): null;
		this.accountingLmee = cnmea_acctg_lmee.length() > 0? new BigDecimal(cnmea_acctg_lmee): null;
		this.accountingLmef = cnmea_acctg_lmef.length() > 0? new BigDecimal(cnmea_acctg_lmef): null;
		this.nmea = cnmea_nmea.length() > 0? new BigDecimal(cnmea_nmea): null;
		this.settId = cnmea_str_id;
		this.accountId = cnmea_account;
	}
	
	public String PFCheck(Cnmea in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String RSCheck(Cnmea in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String equal(Cnmea in) {

		String result = null;

		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.accountingGmef != null && in.accountingGmef != null) if (this.accountingGmef.compareTo(in.accountingGmef) != 0) result = "accountingGmef value mismatch";
		if (this.accountingLmee != null && in.accountingLmee != null) if (this.accountingLmee.compareTo(in.accountingLmee) != 0) result = "accountingLmee value mismatch";
		if (this.accountingLmef != null && in.accountingLmef != null) if (this.accountingLmef.compareTo(in.accountingLmef) != 0) result = "accountingLmef value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				this.settId + "," +
				this.accountId;
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.accountingGmef != null? this.accountingGmef.toString(): "") + "," +
				(this.accountingLmee != null? this.accountingLmee.toString(): "") + "," +
				(this.accountingLmef != null? this.accountingLmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				this.settId + "," +
				this.accountId;
		
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
			"accountId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"gmee," +
			"accountingGmef," +
			"accountingLmee," +
			"accountingLmef," +
			"nmea," +
			"settId," +
			"accountId";

		return header;
	}
}
