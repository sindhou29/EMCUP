package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.CnmeaSettReT;

public class CnmeaSettRe extends CnmeaSettReT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String nmeagrp_gmee = line[0];
		String nmeagrp_gmef = line[1];
		String nmeagrp_lmee = line[2];
		String nmeagrp_lmef = line[3];
		int interval = Integer.parseInt(line[4]);
		String nmeagrp_interval = String.format("%1$02d", interval);
		String nmeagrp_str_id = line[5];
		String nmeagrp_account = line[6];
		String nmeagrp_version = line[7];
		String nmeagrp_required = line[8];
		String nmeagrp_v_gmee = line[9];
		String nmeagrp_a_gmef = line[10];
		String nmeagrp_a_lmee = line[11];
		String nmeagrp_a_lmef = line[12];
		String nmeagrp_nmea = line[13];
		String nmeagrp_a_nmea = line[14];
		
		this.periodId = nmeagrp_interval;
		this.settId = nmeagrp_str_id;
		this.accountId = nmeagrp_account;
		this.version = nmeagrp_version;
		this.nmeaRequired = nmeagrp_required.equals("1");
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return accountId + periodId + version;
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
    
    public BigDecimal sumIncludedIpGstGmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getIpGstGmee() != null) {
		    		result = result.add(adj.getIpGstGmee());
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
    
    public BigDecimal sumIncludedOpGstGmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstGmef() != null) {
		    		result = result.add(adj.getOpGstGmef());
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
    
    public BigDecimal sumIncludedOpGstLmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstLmee() != null) {
		    		result = result.add(adj.getOpGstLmee());
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
    
    public BigDecimal sumIncludedOpGstLmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstLmef() != null) {
		    		result = result.add(adj.getOpGstLmef());
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
    
    public BigDecimal sumIncludedOpGstNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getIpGstNmea() != null) {
		    		result = result.add(adj.getIpGstNmea());
	    		}
	    		if (adj.getOpGstNmea() != null) {
		    		result = result.subtract(adj.getOpGstNmea());
	    		}
	    	}
		}
    	
        return result;
    }

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String nmeagrp_gmee = line[0];
		String nmeagrp_gmef = line[1];
		String nmeagrp_lmee = line[2];
		String nmeagrp_lmef = line[3];
		int interval = Integer.parseInt(line[4]);
		String nmeagrp_interval = String.format("%1$02d", interval);
		String nmeagrp_str_id = line[5];
		String nmeagrp_account = line[6];
		String nmeagrp_version = line[7];
		String nmeagrp_required = line[8];
		String nmeagrp_v_gmee = line[9];
		String nmeagrp_a_gmef = line[10];
		String nmeagrp_a_lmee = line[11];
		String nmeagrp_a_lmef = line[12];
		String nmeagrp_nmea = line[13];
		String nmeagrp_a_nmea = line[14];
		
		this.gmee = nmeagrp_gmee.length() > 0? new BigDecimal(nmeagrp_gmee): null;
		this.gmef = nmeagrp_gmef.length() > 0? new BigDecimal(nmeagrp_gmef): null;
		this.lmee = nmeagrp_lmee.length() > 0? new BigDecimal(nmeagrp_lmee): null;
		this.lmef = nmeagrp_lmef.length() > 0? new BigDecimal(nmeagrp_lmef): null;
		this.periodId = nmeagrp_interval;
		this.settId = nmeagrp_str_id;
		this.accountId = nmeagrp_account;
		this.version = nmeagrp_version;
		this.nmeaRequired = nmeagrp_required.equals("1");
		this.ipGstGmee = nmeagrp_v_gmee.length() > 0? new BigDecimal(nmeagrp_v_gmee): null;
		this.opGstGmef = nmeagrp_a_gmef.length() > 0? new BigDecimal(nmeagrp_a_gmef): null;
		this.opGstLmee = nmeagrp_a_lmee.length() > 0? new BigDecimal(nmeagrp_a_lmee): null;
		this.opGstLmef = nmeagrp_a_lmef.length() > 0? new BigDecimal(nmeagrp_a_lmef): null;
		this.nmea = nmeagrp_nmea.length() > 0? new BigDecimal(nmeagrp_nmea): null;
		this.opGstNmea = nmeagrp_a_nmea.length() > 0? new BigDecimal(nmeagrp_a_nmea): null;
	}
	
	public String PFCheck(CnmeaSettRe in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";

		return result;
	}
	
	public String RSCheck(CnmeaSettRe in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";

		return result;
	}
	
	public String equal(CnmeaSettRe in) {

		String result = null;

		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.gmef != null && in.gmef != null) if (this.gmef.compareTo(in.gmef) != 0) result = "gmef value mismatch";
		if (this.lmee != null && in.lmee != null) if (this.lmee.compareTo(in.lmee) != 0) result = "lmee value mismatch";
		if (this.lmef != null && in.lmef != null) if (this.lmef.compareTo(in.lmef) != 0) result = "lmef value mismatch";
		if (this.ipGstGmee != null && in.ipGstGmee != null) if (this.ipGstGmee.compareTo(in.ipGstGmee) != 0) result = "ipGstGmee value mismatch";
		if (this.opGstGmef != null && in.opGstGmef != null) if (this.opGstGmef.compareTo(in.opGstGmef) != 0) result = "opGstGmef value mismatch";
		if (this.opGstLmee != null && in.opGstLmee != null) if (this.opGstLmee.compareTo(in.opGstLmee) != 0) result = "opGstLmee value mismatch";
		if (this.opGstLmef != null && in.opGstLmef != null) if (this.opGstLmef.compareTo(in.opGstLmef) != 0) result = "opGstLmef value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";
		if (this.opGstNmea != null && in.opGstNmea != null) if (this.opGstNmea.compareTo(in.opGstNmea) != 0) result = "opGstNmea value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				this.periodId + "," +
				this.settId + "," +
				this.accountId + "," +
				this.version + "," +
				(this.nmeaRequired == true? "1": "0") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				this.periodId + "," +
				this.settId + "," +
				this.accountId + "," +
				this.version + "," +
				(this.nmeaRequired == true? "1": "0") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"periodId," +
			"settId," +
			"accountId," +
			"version," +
			"nmeaRequired," +
			"ipGstGmee," +
			"opGstGmef," +
			"opGstLmee," +
			"opGstLmef," +
			"nmea," +
			"opGstNmea";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"periodId," +
			"settId," +
			"accountId," +
			"version," +
			"nmeaRequired," +
			"ipGstGmee," +
			"opGstGmef," +
			"opGstLmee," +
			"opGstLmef," +
			"nmea," +
			"opGstNmea";

		return header;
	}
}
