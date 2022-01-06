package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class VestingContract  implements Serializable{

	public Double vesting_HP;
	public Double vesting_HQ;
	public String vesting_name;
	
	public Double getVesting_HP() {
		return vesting_HP;
	}
	public void setVesting_HP(Double vesting_HP) {
		this.vesting_HP = vesting_HP;
	}
	public Double getVesting_HQ() {
		return vesting_HQ;
	}
	public void setVesting_HQ(Double vesting_HQ) {
		this.vesting_HQ = vesting_HQ;
	}
	public String getVesting_name() {
		return vesting_name;
	}
	public void setVesting_name(String vesting_name) {
		this.vesting_name = vesting_name;
	}
}
