package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.BilateralT;

public class Bilateral extends BilateralT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String bilateral_baq_purchased = line[0];
		String bilateral_baq_sold = line[1];
		String bilateral_bfq_purchased = line[2];
		String bilateral_bfq_sold = line[3];
		String bilateral_bif_purchased = line[4];
		String bilateral_bif_sold = line[5];
		String bilateral_biq_purchased = line[6];
		String bilateral_biq_sold = line[7];
		String bilateral_retailer_weq = line[8];
		String bilateral_generator_ieq = line[9];
		String bilateral_bwf_purchased = line[10];
		String bilateral_bwf_sold = line[11];
		String bilateral_bwq_purchased = line[12];
		String bilateral_bwq_sold = line[13];
		String bilateral_name = line[14];
		String bilateral_account = line[15];
		int interval = Integer.parseInt(line[16]);
		String bilateral_interval = String.format("%1$02d", interval);
		
		this.baqPurchased = bilateral_baq_purchased.length() > 0? new BigDecimal(bilateral_baq_purchased): null;
		this.baqSold = bilateral_baq_sold.length() > 0? new BigDecimal(bilateral_baq_sold): null;
		this.bfqPurchased = bilateral_bfq_purchased.length() > 0? new BigDecimal(bilateral_bfq_purchased): null;
		this.bfqSold = bilateral_bfq_sold.length() > 0? new BigDecimal(bilateral_bfq_sold): null;
		this.bifPurchased = bilateral_bif_purchased.length() > 0? new BigDecimal(bilateral_bif_purchased): null;
		this.bifSold = bilateral_bif_sold.length() > 0? new BigDecimal(bilateral_bif_sold): null;
//		this.biqPurchased = bilateral_biq_purchased.length() > 0? new BigDecimal(bilateral_biq_purchased): null;
//		this.biqSold = bilateral_biq_sold.length() > 0? new BigDecimal(bilateral_biq_sold): null;
		this.retailerWeq = bilateral_retailer_weq.length() > 0? new BigDecimal(bilateral_retailer_weq): null;
		this.generatorIeq = bilateral_generator_ieq.length() > 0? new BigDecimal(bilateral_generator_ieq): null;
		this.bwfPurchased = bilateral_bwf_purchased.length() > 0? new BigDecimal(bilateral_bwf_purchased): null;
		this.bwfSold = bilateral_bwf_sold.length() > 0? new BigDecimal(bilateral_bwf_sold): null;
//		this.bwqPurchased = bilateral_bwq_purchased.length() > 0? new BigDecimal(bilateral_bwq_purchased): null;
//		this.bwqSold = bilateral_bwq_sold.length() > 0? new BigDecimal(bilateral_bwq_sold): null;
		this.contractName = bilateral_name;
		this.accountId = bilateral_account;
		this.periodId = bilateral_interval;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return accountId + periodId + contractName;
	}

	//
	// for unit or regression testing
	//	
	public void initOutput(String [] line) {
		String bilateral_baq_purchased = line[0];
		String bilateral_baq_sold = line[1];
		String bilateral_bfq_purchased = line[2];
		String bilateral_bfq_sold = line[3];
		String bilateral_bif_purchased = line[4];
		String bilateral_bif_sold = line[5];
		String bilateral_biq_purchased = line[6];
		String bilateral_biq_sold = line[7];
		String bilateral_retailer_weq = line[8];
		String bilateral_generator_ieq = line[9];
		String bilateral_bwf_purchased = line[10];
		String bilateral_bwf_sold = line[11];
		String bilateral_bwq_purchased = line[12];
		String bilateral_bwq_sold = line[13];
		String bilateral_name = line[14];
		String bilateral_account = line[15];
		int interval = Integer.parseInt(line[16]);
		String bilateral_interval = String.format("%1$02d", interval);
		
		this.baqPurchased = bilateral_baq_purchased.length() > 0? new BigDecimal(bilateral_baq_purchased): null;
		this.baqSold = bilateral_baq_sold.length() > 0? new BigDecimal(bilateral_baq_sold): null;
		this.bfqPurchased = bilateral_bfq_purchased.length() > 0? new BigDecimal(bilateral_bfq_purchased): null;
		this.bfqSold = bilateral_bfq_sold.length() > 0? new BigDecimal(bilateral_bfq_sold): null;
		this.bifPurchased = bilateral_bif_purchased.length() > 0? new BigDecimal(bilateral_bif_purchased): null;
		this.bifSold = bilateral_bif_sold.length() > 0? new BigDecimal(bilateral_bif_sold): null;
		this.biqPurchased = bilateral_biq_purchased.length() > 0? new BigDecimal(bilateral_biq_purchased): null;
		this.biqSold = bilateral_biq_sold.length() > 0? new BigDecimal(bilateral_biq_sold): null;
		this.retailerWeq = bilateral_retailer_weq.length() > 0? new BigDecimal(bilateral_retailer_weq): null;
		this.generatorIeq = bilateral_generator_ieq.length() > 0? new BigDecimal(bilateral_generator_ieq): null;
		this.bwfPurchased = bilateral_bwf_purchased.length() > 0? new BigDecimal(bilateral_bwf_purchased): null;
		this.bwfSold = bilateral_bwf_sold.length() > 0? new BigDecimal(bilateral_bwf_sold): null;
		this.bwqPurchased = bilateral_bwq_purchased.length() > 0? new BigDecimal(bilateral_bwq_purchased): null;
		this.bwqSold = bilateral_bwq_sold.length() > 0? new BigDecimal(bilateral_bwq_sold): null;
		this.contractName = bilateral_name;
		this.accountId = bilateral_account;
		this.periodId = bilateral_interval;
	}
	
	public String PFCheck(Bilateral in) {

		String result = null;

		if ((this.biqPurchased == null && in.biqPurchased != null) || (this.biqPurchased != null && in.biqPurchased == null)) result = "biqPurchased missing value";
		if ((this.biqSold == null && in.biqSold != null) || (this.biqSold != null && in.biqSold == null)) result = "biqSold missing value";
		if ((this.bwqPurchased == null && in.bwqPurchased != null) || (this.bwqPurchased != null && in.bwqPurchased == null)) result = "bwqPurchased missing value";
		if ((this.bwqSold == null && in.bwqSold != null) || (this.bwqSold != null && in.bwqSold == null)) result = "bwqSold missing value";

		return result;
	}
	
	public String RSCheck(Bilateral in) {

		String result = null;

		if ((this.biqPurchased == null && in.biqPurchased != null) || (this.biqPurchased != null && in.biqPurchased == null)) result = "biqPurchased missing value";
		if ((this.biqSold == null && in.biqSold != null) || (this.biqSold != null && in.biqSold == null)) result = "biqSold missing value";
		if ((this.bwqPurchased == null && in.bwqPurchased != null) || (this.bwqPurchased != null && in.bwqPurchased == null)) result = "bwqPurchased missing value";
		if ((this.bwqSold == null && in.bwqSold != null) || (this.bwqSold != null && in.bwqSold == null)) result = "bwqSold missing value";

		return result;
	}
	
	public String equal(Bilateral in) {

		String result = null;

		if (this.biqPurchased != null && in.biqPurchased != null) if (this.biqPurchased.compareTo(in.biqPurchased) != 0) result = "biqPurchased value mismatch";
		if (this.biqSold != null && in.biqSold != null) if (this.biqSold.compareTo(in.biqSold) != 0) result = "biqSold value mismatch";
		if (this.bwqPurchased != null && in.bwqPurchased != null) if (this.bwqPurchased.compareTo(in.bwqPurchased) != 0) result = "bwqPurchased value mismatch";
		if (this.bwqSold != null && in.bwqSold != null) if (this.bwqSold.compareTo(in.bwqSold) != 0) result = "bwqSold value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.baqPurchased != null? this.baqPurchased.toString(): "") + "," +
				(this.baqSold != null? this.baqSold.toString(): "") + "," +
				(this.bfqPurchased != null? this.bfqPurchased.toString(): "") + "," +
				(this.bfqSold != null? this.bfqSold.toString(): "") + "," +
				(this.bifPurchased != null? this.bifPurchased.toString(): "") + "," +
				(this.bifSold != null? this.bifSold.toString(): "") + "," +
				(this.biqPurchased != null? this.biqPurchased.toString(): "") + "," +
				(this.biqSold != null? this.biqSold.toString(): "") + "," +
				(this.retailerWeq != null? this.retailerWeq.toString(): "") + "," +
				(this.generatorIeq != null? this.generatorIeq.toString(): "") + "," +
				(this.bwfPurchased != null? this.bwfPurchased.toString(): "") + "," +
				(this.bwfSold != null? this.bwfSold.toString(): "") + "," +
				(this.bwqPurchased != null? this.bwqPurchased.toString(): "") + "," +
				(this.bwqSold != null? this.bwqSold.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId;
		
		return result;
	}
	
	public String toOutputString() {
		
		String result = (this.baqPurchased != null? this.baqPurchased.toString(): "") + "," +
				(this.baqSold != null? this.baqSold.toString(): "") + "," +
				(this.bfqPurchased != null? this.bfqPurchased.toString(): "") + "," +
				(this.bfqSold != null? this.bfqSold.toString(): "") + "," +
				(this.bifPurchased != null? this.bifPurchased.toString(): "") + "," +
				(this.bifSold != null? this.bifSold.toString(): "") + "," +
				(this.biqPurchased != null? this.biqPurchased.toString(): "") + "," +
				(this.biqSold != null? this.biqSold.toString(): "") + "," +
				(this.retailerWeq != null? this.retailerWeq.toString(): "") + "," +
				(this.generatorIeq != null? this.generatorIeq.toString(): "") + "," +
				(this.bwfPurchased != null? this.bwfPurchased.toString(): "") + "," +
				(this.bwfSold != null? this.bwfSold.toString(): "") + "," +
				(this.bwqPurchased != null? this.bwqPurchased.toString(): "") + "," +
				(this.bwqSold != null? this.bwqSold.toString(): "") + "," +
				this.contractName + "," +
				this.accountId + "," +
				this.periodId;
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"baqPurchased," +
			"baqSold," +
			"bfqPurchased," +
			"bfqSold," +
			"bifPurchased," +
			"bifSold," +
			"biqPurchased," +
			"biqSold," +
			"retailerWeq," +
			"generatorIeq," +
			"bwfPurchased," +
			"bwfSold," +
			"bwqPurchased," +
			"bwqSold," +
			"contractName," +
			"accountId," +
			"periodId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"baqPurchased," +
			"baqSold," +
			"bfqPurchased," +
			"bfqSold," +
			"bifPurchased," +
			"bifSold," +
			"biqPurchased," +
			"biqSold," +
			"retailerWeq," +
			"generatorIeq," +
			"bwfPurchased," +
			"bwfSold," +
			"bwqPurchased," +
			"bwqSold," +
			"contractName," +
			"accountId," +
			"periodId";

		return header;
	}
}
