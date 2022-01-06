package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;

public class MMVolume  implements Serializable{

	public String settlementAccountName;
	public String settlementAccountId;
	public String startDate;
	public String endDate;
	public Double mmVolume;
	public String version;
	public String eveId;
	public String standingVersion;
	public String sacId;
	
	public String getSettlementAccountName() {
		return settlementAccountName;
	}
	public void setSettlementAccountName(String settlementAccountName) {
		this.settlementAccountName = settlementAccountName;
	}
	public String getSettlementAccountId() {
		return settlementAccountId;
	}
	public void setSettlementAccountId(String settlementAccountId) {
		this.settlementAccountId = settlementAccountId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public Double getMmVolume() {
		return mmVolume;
	}
	public void setMmVolume(Double mmVolume) {
		this.mmVolume = mmVolume;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getEveId() {
		return eveId;
	}
	public void setEveId(String eveId) {
		this.eveId = eveId;
	}
	public String getStandingVersion() {
		return standingVersion;
	}
	public void setStandingVersion(String standingVersion) {
		this.standingVersion = standingVersion;
	}
	public String getSacId() {
		return sacId;
	}
	public void setSacId(String sacId) {
		this.sacId = sacId;
	}

}
