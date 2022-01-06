package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.BrqT;
import com.emc.sett.model.ReserveT;

public class Reserve extends ReserveT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String reserve_brq_purchased = line[0];
		String reserve_brq_sold = line[1];
		String reserve_grq = line[2];
		String reserve_lrq = line[3];
		String reserve_mrp = line[4];
		String reserve_rcc = line[5];
		String reserve_rsc = line[6];
		String reserve_rsd = line[7];
		String reserve_factor = line[8];
		String reserve_name = line[9];
		String reserve_node = line[10];
		int interval = Integer.parseInt(line[11]);
		String reserve_interval = String.format("%1$02d", interval);
		String reserve_rsq = line[12];
		String reserve_facility_rsc = line[13];
		String reserve_account = line[14];
		
		this.brqPurchased = reserve_brq_purchased.length() > 0? new BigDecimal(reserve_brq_purchased): null;
		this.brqSold = reserve_brq_sold.length() > 0? new BigDecimal(reserve_brq_sold): null;
		this.grq = reserve_grq.length() > 0? new BigDecimal(reserve_grq): null;
		this.lrq = reserve_lrq.length() > 0? new BigDecimal(reserve_lrq): null;
		this.mrp = reserve_mrp.length() > 0? new BigDecimal(reserve_mrp): null;
		this.name = reserve_name;
		this.node = reserve_node;
		this.periodId = reserve_interval;
		this.accountId = reserve_account;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return name + node + periodId;
	}
	
	public String getNodeKey() {
		return node + periodId;
	}
	
	public String getRsvClsKey() {
		return name + periodId;
	}
    
    public BigDecimal sumPurchasedBrqOverAllSettlementAccounts() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.purchasedContracts != null) {
	    	for (BrqT contract : this.purchasedContracts) {
	    		if (contract.getPurchased() != null) {
		    		result = result.add(contract.getPurchased());
	    		}
	    	}
		}
    	
        return result;
    }
	
    public BigDecimal sumSoldBrqOverAllSettlementAccounts() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.soldContracts != null) {
	    	for (BrqT contract : this.soldContracts) {
	    		if (contract.getSold() != null) {
		    		result = result.add(contract.getSold());
	    		}
	    	}
		}
    	
        return result;
    }
    
	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String reserve_brq_purchased = line[0];
		String reserve_brq_sold = line[1];
		String reserve_grq = line[2];
		String reserve_lrq = line[3];
		String reserve_mrp = line[4];
		String reserve_acctg_rcc = line[5];
		String reserve_acctg_rsc = line[6];
		String reserve_acctg_rsd = line[7];
		String reserve_factor = line[8];
		String reserve_name = line[9];
		String reserve_node = line[10];
		int interval = Integer.parseInt(line[11]);
		String reserve_interval = String.format("%1$02d", interval);
		String reserve_rsq = line[12];
		String reserve_facility_rsc = line[13];
		String reserve_account = line[14];
		
		this.brqPurchased = reserve_brq_purchased.length() > 0? new BigDecimal(reserve_brq_purchased): null;
		this.brqSold = reserve_brq_sold.length() > 0? new BigDecimal(reserve_brq_sold): null;
		this.grq = reserve_grq.length() > 0? new BigDecimal(reserve_grq): null;
		this.lrq = reserve_lrq.length() > 0? new BigDecimal(reserve_lrq): null;
		this.mrp = reserve_mrp.length() > 0? new BigDecimal(reserve_mrp): null;
		this.accountingRcc = reserve_acctg_rcc.length() > 0? new BigDecimal(reserve_acctg_rcc): null;
		this.accountingRsc = reserve_acctg_rsc.length() > 0? new BigDecimal(reserve_acctg_rsc): null;
		this.accountingRsd = reserve_acctg_rsd.length() > 0? new BigDecimal(reserve_acctg_rsd): null;
		this.factor = reserve_factor.length() > 0? new BigDecimal(reserve_factor): null;
		this.name = reserve_name;
		this.node = reserve_node;
		this.periodId = reserve_interval;
		this.rsq = reserve_rsq.length() > 0? new BigDecimal(reserve_rsq): null;
		this.facilityRsc = reserve_facility_rsc.length() > 0? new BigDecimal(reserve_facility_rsc): null;
		this.accountId = reserve_account;
	}
	
	public String PFCheck(Reserve in) {

		String result = null;

		if ((this.accountingRcc == null && in.accountingRcc != null) || (this.accountingRcc != null && in.accountingRcc == null)) result = "accountingRcc missing value";
		if ((this.accountingRsc == null && in.accountingRsc != null) || (this.accountingRsc != null && in.accountingRsc == null)) result = "accountingRsc missing value";
		if ((this.accountingRsd == null && in.accountingRsd != null) || (this.accountingRsd != null && in.accountingRsd == null)) result = "accountingRsd missing value";
		if ((this.factor == null && in.factor != null) || (this.factor != null && in.factor == null)) result = "factor missing value";
		if ((this.rsq == null && in.rsq != null) || (this.rsq != null && in.rsq == null)) result = "rsq missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";

		return result;
	}
	
	public String RSCheck(Reserve in) {

		String result = null;

		if ((this.accountingRcc == null && in.accountingRcc != null) || (this.accountingRcc != null && in.accountingRcc == null)) result = "accountingRcc missing value";
		if ((this.accountingRsc == null && in.accountingRsc != null) || (this.accountingRsc != null && in.accountingRsc == null)) result = "accountingRsc missing value";
		if ((this.accountingRsd == null && in.accountingRsd != null) || (this.accountingRsd != null && in.accountingRsd == null)) result = "accountingRsd missing value";
		//if ((this.factor == null && in.factor != null) || (this.factor != null && in.factor == null)) result = "factor missing value";
		if ((this.rsq == null && in.rsq != null) || (this.rsq != null && in.rsq == null)) result = "rsq missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";

		return result;
	}
	
	public String equal(Reserve in) {

		String result = null;

		if (this.accountingRcc != null && in.accountingRcc != null) if (this.accountingRcc.compareTo(in.accountingRcc) != 0) result = "accountingRcc value mismatch";
		if (this.accountingRsc != null && in.accountingRsc != null) if (this.accountingRsc.compareTo(in.accountingRsc) != 0) result = "accountingRsc value mismatch";
		if (this.accountingRsd != null && in.accountingRsd != null) if (this.accountingRsd.compareTo(in.accountingRsd) != 0) result = "accountingRsd value mismatch";
		if (this.factor != null && in.factor != null) if (this.factor.compareTo(in.factor) != 0) result = "factor value mismatch";
		if (this.rsq != null && in.rsq != null) if (this.rsq.compareTo(in.rsq) != 0) result = "rsq value mismatch";
		if (this.facilityRsc != null && in.facilityRsc != null) if (this.facilityRsc.compareTo(in.facilityRsc) != 0) result = "facilityRsc value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.brqPurchased != null? this.brqPurchased.toString(): "") + "," +
				(this.brqSold != null? this.brqSold.toString(): "") + "," +
				(this.grq != null? this.grq.toString(): "") + "," +
				(this.lrq != null? this.lrq.toString(): "") + "," +
				(this.mrp != null? this.mrp.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				(this.factor != null? this.factor.toString(): "") + "," +
				this.name + "," +
				this.node + "," +
				this.periodId + "," +
				(this.rsq != null? this.rsq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				this.accountId;
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.brqPurchased != null? this.brqPurchased.toString(): "") + "," +
				(this.brqSold != null? this.brqSold.toString(): "") + "," +
				(this.grq != null? this.grq.toString(): "") + "," +
				(this.lrq != null? this.lrq.toString(): "") + "," +
				(this.mrp != null? this.mrp.toString(): "") + "," +
				(this.accountingRcc != null? this.accountingRcc.toString(): "") + "," +
				(this.accountingRsc != null? this.accountingRsc.toString(): "") + "," +
				(this.accountingRsd != null? this.accountingRsd.toString(): "") + "," +
				(this.factor != null? this.factor.toString(): "") + "," +
				this.name + "," +
				this.node + "," +
				this.periodId + "," +
				(this.rsq != null? this.rsq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				this.accountId;
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"brqPurchased," +
			"brqSold," +
			"grq," +
			"lrq," +
			"mrp," +
			"rcc," +
			"rsc," +
			"rsd," +
			"factor," +
			"name," +
			"node," +
			"periodId," +
			"rsq," +
			"facilityRsc," +
			"accountId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"brqPurchased," +
			"brqSold," +
			"grq," +
			"lrq," +
			"mrp," +
			"accountingRcc," +
			"accountingRsc," +
			"accountingRsd," +
			"factor," +
			"name," +
			"node," +
			"periodId," +
			"rsq," +
			"facilityRsc," +
			"accountId";

		return header;
	}
}
