package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

public class SettlementRunInfo  implements Serializable{
	
	public String runId;
	public String runEveId;
	public String runType;
	public String completed;
	public String success;
	public String authStatus;
	public String settDateStr;
	public String runDateStr;

	public Date runDate;
	public Date settlementDate;
	
	
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}
	public String getRunEveId() {
		return runEveId;
	}
	public void setRunEveId(String runEveId) {
		this.runEveId = runEveId;
	}
	public String getRunType() {
		return runType;
	}
	public void setRunType(String runType) {
		this.runType = runType;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
	public String getSettDateStr() {
		return settDateStr;
	}
	public void setSettDateStr(String settDateStr) {
		this.settDateStr = settDateStr;
	}
	public String getRunDateStr() {
		return runDateStr;
	}
	public void setRunDateStr(String runDateStr) {
		this.runDateStr = runDateStr;
	}
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
}
