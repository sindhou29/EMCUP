package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

//import com.fasterxml.jackson.annotation.JsonAnyGetter;
//import com.fasterxml.jackson.annotation.JsonAnySetter;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CLASS)
public class SettRunPkg implements Serializable {

	private static final long serialVersionUID = -1;
 
	public String mcPricePkgId;
	public String mcPricePkgVer;
	public String mcQtyPkgId;
	public String mcQtyPkgVer;
	public String msslQtyPkgId;
	public String msslQtyPkgVer;
	public String settInputPkgId;
	public String settInputPkgVer ;
	public String settRunPkgId;
	public String settRunPkgVer;
	public String standingVersion;
	public Date settlementDate;
	public java.sql.Date sqlSettlementDate;
	
	public SettRunPkg() {
	}
	
	public SettRunPkg(SettRunPkg params) {
		this.mcPricePkgId = params.mcPricePkgId;
		this.mcPricePkgVer = params.mcPricePkgVer;
		this.mcQtyPkgId = params.mcQtyPkgId;
		this.mcQtyPkgVer = params.mcQtyPkgVer;
		this.msslQtyPkgId = params.msslQtyPkgId;
		this.msslQtyPkgVer = params.msslQtyPkgVer;
		this.settInputPkgId = params.settInputPkgId;
		this.settInputPkgVer = params.settInputPkgVer;
		this.settlementDate = params.settlementDate;
		this.sqlSettlementDate = params.sqlSettlementDate;
		this.settRunPkgId = params.settRunPkgId;
		this.settRunPkgVer = params.settRunPkgVer;
		this.standingVersion = params.standingVersion;
	}
	
	public java.sql.Date getSqlSettlementDate() {
		return sqlSettlementDate;
	}

	public void setSqlSettlementDate(java.sql.Date sqlSettlementDate) {
		this.sqlSettlementDate = sqlSettlementDate;
	}

	public String getMcPricePkgId() {
		return mcPricePkgId;
	}
	public void setMcPricePkgId(String mcPricePkgId) {
		this.mcPricePkgId = mcPricePkgId;
	}
	public String getMcPricePkgVer() {
		return mcPricePkgVer;
	}
	public void setMcPricePkgVer(String mcPricePkgVer) {
		this.mcPricePkgVer = mcPricePkgVer;
	}
	public String getMcQtyPkgId() {
		return mcQtyPkgId;
	}
	public void setMcQtyPkgId(String mcQtyPkgId) {
		this.mcQtyPkgId = mcQtyPkgId;
	}
	public String getMcQtyPkgVer() {
		return mcQtyPkgVer;
	}
	public void setMcQtyPkgVer(String mcQtyPkgVer) {
		this.mcQtyPkgVer = mcQtyPkgVer;
	}
	public String getMsslQtyPkgId() {
		return msslQtyPkgId;
	}
	public void setMsslQtyPkgId(String msslQtyPkgId) {
		this.msslQtyPkgId = msslQtyPkgId;
	}
	public String getMsslQtyPkgVer() {
		return msslQtyPkgVer;
	}
	public void setMsslQtyPkgVer(String msslQtyPkgVer) {
		this.msslQtyPkgVer = msslQtyPkgVer;
	}
	public String getSettInputPkgId() {
		return settInputPkgId;
	}
	public void setSettInputPkgId(String settInputPkgId) {
		this.settInputPkgId = settInputPkgId;
	}
	public String getSettInputPkgVer() {
		return settInputPkgVer;
	}
	public void setSettInputPkgVer(String settInputPkgVer) {
		this.settInputPkgVer = settInputPkgVer;
	}
	public String getSettRunPkgId() {
		return settRunPkgId;
	}
	public void setSettRunPkgId(String settRunPkgId) {
		this.settRunPkgId = settRunPkgId;
	}
	public String getSettRunPkgVer() {
		return settRunPkgVer;
	}
	public void setSettRunPkgVer(String settRunPkgVer) {
		this.settRunPkgVer = settRunPkgVer;
	}
	public String getStandingVersion() {
		return standingVersion;
	}
	public void setStandingVersion(String standingVersion) {
		this.standingVersion = standingVersion;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	
}
