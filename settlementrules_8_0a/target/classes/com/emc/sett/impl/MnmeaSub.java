package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.MnmeaSubT;

public class MnmeaSub extends MnmeaSubT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String mnmeasub_gmee = line[0];
		String mnmeasub_gmef = line[1];
		String mnmeasub_lmee = line[2];
		String mnmeasub_lmef = line[3];
		String mnmeasub_nmea = line[4];
		String mnmeasub_rerun_id = line[5];
		String mnmeasub_str_id = line[6];
		
		this.rerunId = mnmeasub_rerun_id;
		this.settId = mnmeasub_str_id;
	}
	
	public String getKey() {
		return settId + rerunId;
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
		
		String mnmeasub_gmee = line[0];
		String mnmeasub_gmef = line[1];
		String mnmeasub_lmee = line[2];
		String mnmeasub_lmef = line[3];
		String mnmeasub_nmea = line[4];
		String mnmeasub_rerun_id = line[5];
		String mnmeasub_str_id = line[6];
		
		this.gmee = mnmeasub_gmee.length() > 0? new BigDecimal(mnmeasub_gmee): null;
		this.gmef = mnmeasub_gmef.length() > 0? new BigDecimal(mnmeasub_gmef): null;
		this.lmee = mnmeasub_lmee.length() > 0? new BigDecimal(mnmeasub_lmee): null;
		this.lmef = mnmeasub_lmef.length() > 0? new BigDecimal(mnmeasub_lmef): null;
		this.nmea = mnmeasub_nmea.length() > 0? new BigDecimal(mnmeasub_nmea): null;
		this.rerunId = mnmeasub_rerun_id;
		this.settId = mnmeasub_str_id;
	}
	
	public String PFCheck(MnmeaSub in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String RSCheck(MnmeaSub in) {

		String result = null;

		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";

		return result;
	}
	
	public String equal(MnmeaSub in) {

		String result = null;

		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.gmef != null && in.gmef != null) if (this.gmef.compareTo(in.gmef) != 0) result = "gmef value mismatch";
		if (this.lmee != null && in.lmee != null) if (this.lmee.compareTo(in.lmee) != 0) result = "lmee value mismatch";
		if (this.lmef != null && in.lmef != null) if (this.lmef.compareTo(in.lmef) != 0) result = "lmef value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				this.rerunId + "," +
				this.settId;
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				this.rerunId + "," +
				this.settId;
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"nmea," +
			"rerunId," +
			"settId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"gmee," +
			"gmef," +
			"lmee," +
			"lmef," +
			"nmea," +
			"rerunId," +
			"settId";

		return header;
	}
}
