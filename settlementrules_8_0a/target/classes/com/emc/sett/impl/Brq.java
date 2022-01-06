package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.BrqT;

public class Brq extends BrqT {

	public void initInput(String [] line) {
		
		String brq_purchased = line[0];
		String brq_sold = line[1];
		String brq_name = line[2];
		String brq_account = line[3];
		int interval = Integer.parseInt(line[4]);
		String brq_interval = String.format("%1$02d", interval);
		String brq_node = line[5];
		String brq_reserve = line[6];
		
		this.purchased = brq_purchased.length() > 0? new BigDecimal(brq_purchased): null;
		this.sold = brq_sold.length() > 0? new BigDecimal(brq_sold): null;
		this.contractName = brq_name;
		this.accountId = brq_account;
		this.periodId = brq_interval;
		this.node = brq_node;
		this.reserveClass = brq_reserve;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return reserveClass + node + periodId;
	}
	
	public boolean isPurchaseContract() {
		return purchased.signum() != 0 && sold.signum() == 0;
	}
	
	public boolean isSellingContract() {
		return purchased.signum() == 0 && sold.signum() != 0;
	}
	
	public boolean isEmptyContract() {
		return purchased.signum() == 0 && sold.signum() == 0;
	}
	
	public String toInputString() {
		
		String result = (this.purchased != null? this.purchased.toString(): "") + "," +
				(this.sold != null? this.sold.toString(): "") + "," +
				(this.contractName != null? this.contractName: "") + "," +
				(this.accountId != null? this.accountId: "") + "," +
				(this.periodId != null? this.periodId: "") + "," +
				(this.node != null? this.node: "") + "," +
				(this.reserveClass != null? this.reserveClass: "");
		
		return result;
	}
	
	public String toOutputString() {
		
		String result = (this.purchased != null? this.purchased.toString(): "") + "," +
				(this.sold != null? this.sold.toString(): "") + "," +
				(this.contractName != null? this.contractName: "") + "," +
				(this.accountId != null? this.accountId: "") + "," +
				(this.periodId != null? this.periodId: "") + "," +
				(this.node != null? this.node: "") + "," +
				(this.reserveClass != null? this.reserveClass: "");
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"purchased," +
			"sold," +
			"contractName," +
			"accountId," +
			"periodId," +
			"node," +
			"reserveClass";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"purchased," +
			"sold," +
			"contractName," +
			"accountId," +
			"periodId," +
			"node," +
			"reserveClass";

		return header;
	}
}
