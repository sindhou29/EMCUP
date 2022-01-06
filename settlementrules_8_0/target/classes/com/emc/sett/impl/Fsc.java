package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.FscT;

public class Fsc extends FscT {

	public void initInput(String [] line) {
		
		String fsc_fsp = line[0];
		String fsc_fsq = line[1];
		String fsc_name = line[2];
		String fsc_account = line[3];
		int interval = Integer.parseInt(line[4]);
		String fsc_interval = String.format("%1$02d", interval);
		String fsc_breach_flag = line[5];
		
		this.fsp = fsc_fsp.length() > 0? new BigDecimal(fsc_fsp): null;
		this.fsq = fsc_fsq.length() > 0? new BigDecimal(fsc_fsq): null;
		this.contractName = fsc_name;
		this.accountId = fsc_account;
		this.periodId = fsc_interval;
		this.breached = fsc_breach_flag.equals("1");
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return periodId + accountId + contractName;
	}

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String fsc_fsp = line[0];
		String fsc_fsq = line[1];
		String fsc_name = line[2];
		String fsc_account = line[3];
		int interval = Integer.parseInt(line[4]);
		String fsc_interval = String.format("%1$02d", interval);
		String fsc_breach_flag = line[5];
		String fsc_fsrp = line[6];
		String fsc_fssc = line[7];
		
		this.fsp = fsc_fsp.length() > 0? new BigDecimal(fsc_fsp): null;
		this.fsq = fsc_fsq.length() > 0? new BigDecimal(fsc_fsq): null;
		this.contractName = fsc_name;
		this.accountId = fsc_account;
		this.periodId = fsc_interval;
		this.breached = fsc_breach_flag.equals("1");
		this.fsrp = fsc_fsrp.length() > 0? new BigDecimal(fsc_fsrp): null;
		this.fssc = fsc_fssc.length() > 0? new BigDecimal(fsc_fssc): null;
	}
	
	public String PFCheck(Fsc in) {

		String result = null;

		if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";

		return result;
	}
	
	public String RSCheck(Fsc in) {

		String result = null;

		if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";

		return result;
	}
	
	public String equal(Fsc in) {

		String result = null;

		if (this.fsrp != null && in.fsrp != null) if (this.fsrp.compareTo(in.fsrp) != 0) result = "fsrp value mismatch";
		if (this.fssc != null && in.fssc != null) if (this.fssc.compareTo(in.fssc) != 0) result = "fssc value mismatch";

		return result;
	}

	public String toInputString() {
		
		String result = (this.fsp != null? this.fsp.toString(): "") + "," +
				(this.fsq != null? this.fsq.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId + "," +
				(this.breached == true? "1": "0");
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.fsp != null? this.fsp.toString(): "") + "," +
				(this.fsq != null? this.fsq.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId + "," +
				(this.breached == true? "1": "0") + "," +
				(this.fsrp != null? this.fsrp.toString(): "") + "," +
				(this.fssc != null? this.fssc.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"fsp," +
			"fsq," +
			"contractName," +
			"accountId," +
			"periodId," +
			"breached";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"fsp," +
			"fsq," +
			"contractName," +
			"accountId," +
			"periodId," +
			"breached," +
			"fsrp," +
			"fssc";

		return header;
	}
}
