package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class SettlementResultType  implements Serializable{

	public String id;
	public String gstVersion;
	public String standingVersion;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGstVersion() {
		return gstVersion;
	}
	public void setGstVersion(String gstVersion) {
		this.gstVersion = gstVersion;
	}
	public String getStandingVersion() {
		return standingVersion;
	}
	public void setStandingVersion(String standingVersion) {
		this.standingVersion = standingVersion;
	}
	
}
