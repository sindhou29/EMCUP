package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.ReserveT;
import com.emc.sett.model.RsvClassT;

public class RsvClass extends RsvClassT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String class_name = line[0];
		int interval = Integer.parseInt(line[1]);
		String class_interval = String.format("%1$02d", interval);
		String class_rsc = line[2];
		
		this.reserveClass = class_name;
		this.periodId = class_interval;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return reserveClass + periodId;
	}
    
    public BigDecimal sumAllReservesRsc() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.reserves != null) {
	    	for (ReserveT rsv : this.reserves) {
	    		if (rsv.getRsc() != null) {
		    		result = result.add(rsv.getRsc());
	    		}
	    	}
		}
    	
        return result;
    }
	
	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String class_name = line[0];
		int interval = Integer.parseInt(line[1]);
		String class_interval = String.format("%1$02d", interval);
		String class_rsc = line[2];
		
		this.reserveClass = class_name;
		this.periodId = class_interval;
		this.rsc = class_rsc.length() > 0? new BigDecimal(class_rsc): null;
	}
	
	public String PFCheck(RsvClass in) {

		String result = null;

		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";

		return result;
	}
	
	public String RSCheck(RsvClass in) {

		String result = null;

		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";

		return result;
	}
	
	public String equal(RsvClass in) {

		String result = null;

		if (this.rsc != null && in.rsc != null) if (this.rsc.compareTo(in.rsc) != 0) result = "rsc value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = this.reserveClass + "," +
				this.periodId + "," +
				(this.rsc != null? this.rsc.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = this.reserveClass + "," +
				this.periodId + "," +
				(this.rsc != null? this.rsc.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"reserveClass," +
			"periodId," +
			"rsc";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"reserveClass," +
			"periodId," +
			"rsc";

		return header;
	}
}
