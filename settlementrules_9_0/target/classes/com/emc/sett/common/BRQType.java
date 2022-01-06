package com.emc.sett.common;

import java.math.BigDecimal;

public class BRQType {
	
	public BRQType (BigDecimal purchased, BigDecimal sold, String purchaseId, String sellingId, String contract) {
		
		this.purchased = purchased;
		this.sold = sold;
		this.purchaseId = purchaseId;
		this.sellingId = sellingId;
		this.contract = contract;
	}

	public BigDecimal purchased = null;
	public BigDecimal sold = null;
	public String purchaseId = "";
	public String sellingId = "";
	public String contract = "";
}
