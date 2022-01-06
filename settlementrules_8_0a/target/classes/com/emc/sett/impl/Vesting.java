package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.VestingT;

public class Vesting extends VestingT {

	public void initInput(String [] line) {
		
		String vesting_hp = line[0];
		String vesting_hq = line[1];
		String vesting_name = line[2];
		String vesting_account = line[3];
		int interval = Integer.parseInt(line[4]);
		String vesting_interval = String.format("%1$02d", interval);
		
		this.hp = vesting_hp.length() > 0? new BigDecimal(vesting_hp): null;
		this.hq = vesting_hq.length() > 0? new BigDecimal(vesting_hq): null;
		this.contractName = vesting_name;
		this.accountId = vesting_account;
		this.periodId = vesting_interval;
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
		
		String vesting_hp = line[0];
		String vesting_hq = line[1];
		String vesting_name = line[2];
		String vesting_account = line[3];
		int interval = Integer.parseInt(line[4]);
		String vesting_interval = String.format("%1$02d", interval);
		String vesting_vcrp = line[5];
		String vesting_vcsc = line[6];
		
		this.hp = vesting_hp.length() > 0? new BigDecimal(vesting_hp): null;
		this.hq = vesting_hq.length() > 0? new BigDecimal(vesting_hq): null;
		this.contractName = vesting_name;
		this.accountId = vesting_account;
		this.periodId = vesting_interval;
		this.vcrp = vesting_vcrp.length() > 0? new BigDecimal(vesting_vcrp): null;
		this.vcsc = vesting_vcsc.length() > 0? new BigDecimal(vesting_vcsc): null;
	}
	
	public String PFCheck(Vesting in) {

		String result = null;

		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		return result;
	}
	
	public String RSCheck(Vesting in) {

		String result = null;

		//if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		return result;
	}
	
	public String equal(Vesting in) {

		String result = null;

		if (this.vcrp != null && in.vcrp != null) if (this.vcrp.compareTo(in.vcrp) != 0) result = "vcrp value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.hp != null? this.hp.toString(): "") + "," +
				(this.hq != null? this.hq.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId;
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.hp != null? this.hp.toString(): "") + "," +
				(this.hq != null? this.hq.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"hp," +
			"hq," +
			"contractName," +
			"accountId," +
			"periodId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"hp," +
			"hq," +
			"contractName," +
			"accountId," +
			"periodId," +
			"vcrp," +
			"vcsc";

		return header;
	}
}
