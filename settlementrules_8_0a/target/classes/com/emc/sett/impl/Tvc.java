package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.TvcT;

public class Tvc extends TvcT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String tvc_account = line[0];
		int interval = Integer.parseInt(line[1]);
		String tvc_interval = String.format("%1$02d", interval);
		String tvc_name = line[2];
		String tvc_tvp = line[3];
		String tvc_tvq = line[4];
		String tvc_vcrp = line[5];
		String tvc_vcsc = line[6];
		
		this.accountId = tvc_account;
		this.periodId = tvc_interval;
		this.contractName = tvc_name;
		this.tvp = tvc_tvp.length() > 0? new BigDecimal(tvc_tvp): null;
		this.tvq = tvc_tvq.length() > 0? new BigDecimal(tvc_tvq): null;
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
		
		String tvc_account = line[0];
		int interval = Integer.parseInt(line[1]);
		String tvc_interval = String.format("%1$02d", interval);
		String tvc_name = line[2];
		String tvc_tvp = line[3];
		String tvc_tvq = line[4];
		String tvc_vcrp = line[5];
		String tvc_vcsc = line[6];
		
		this.accountId = tvc_account;
		this.periodId = tvc_interval;
		this.contractName = tvc_name;
		this.tvp = tvc_tvp.length() > 0? new BigDecimal(tvc_tvp): null;
		this.tvq = tvc_tvq.length() > 0? new BigDecimal(tvc_tvq): null;
		this.vcrp = tvc_vcrp.length() > 0? new BigDecimal(tvc_vcrp): null;
		this.vcsc = tvc_vcsc.length() > 0? new BigDecimal(tvc_vcsc): null;
	}
	
	public String PFCheck(Tvc in) {

		String result = null;

		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		return result;
	}
	
	public String RSCheck(Tvc in) {

		String result = null;

		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		return result;
	}
	
	public String equal(Tvc in) {

		String result = null;

		if (this.vcrp != null && in.vcrp != null) if (this.vcrp.compareTo(in.vcrp) != 0) result = "vcrp value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = this.accountId + "," +
				this.periodId + "," +
				this.contractName + "," +
				(this.tvp != null? this.tvp.toString(): "") + "," +
				(this.tvq != null? this.tvq.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = this.accountId + "," +
				this.periodId + "," +
				this.contractName + "," +
				(this.tvp != null? this.tvp.toString(): "") + "," +
				(this.tvq != null? this.tvq.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"accountId," +
			"periodId," +
			"contractName," +
			"tvp," +
			"tvq," +
			"vcrp," +
			"vcsc";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"accountId," +
			"periodId," +
			"contractName," +
			"tvp," +
			"tvq," +
			"vcrp," +
			"vcsc";

		return header;
	}
}
