package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class MarketParticipantType  implements Serializable{

	public String settAccounts;
	
	public int isEMC;
	public int isMSSL;
	public int isPSO;
	public int mpTaxable;
	
	public Double gstRate;

	public String getSettAccounts() {
		return settAccounts;
	}

	public void setSettAccounts(String settAccounts) {
		this.settAccounts = settAccounts;
	}

	public int getIsEMC() {
		return isEMC;
	}

	public void setIsEMC(int isEMC) {
		this.isEMC = isEMC;
	}

	public int getIsMSSL() {
		return isMSSL;
	}

	public void setIsMSSL(int isMSSL) {
		this.isMSSL = isMSSL;
	}

	public int getIsPSO() {
		return isPSO;
	}

	public void setIsPSO(int isPSO) {
		this.isPSO = isPSO;
	}

	public int getMpTaxable() {
		return mpTaxable;
	}

	public void setMpTaxable(int mpTaxable) {
		this.mpTaxable = mpTaxable;
	}

	public Double getGstRate() {
		return gstRate;
	}

	public void setGstRate(Double gstRate) {
		this.gstRate = gstRate;
	}
}
