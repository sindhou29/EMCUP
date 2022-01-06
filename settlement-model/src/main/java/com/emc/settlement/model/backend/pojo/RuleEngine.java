package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class RuleEngine  implements Serializable{

	public String haleyRuleVersion;
	public String haleyRuleDir;
	public String haleyBinDir;
	public String haleyConfDir;
	public String haleyLibDir;
	public String reserveOutputDir;
	public String acctStmtInputDir;
	public String acctStmtOutputDir;
	public String dataInputDir;
	public String dataOutputDir;
	public String haleyLogDir;
	
	public String getHaleyRuleVersion() {
		return haleyRuleVersion;
	}
	public void setHaleyRuleVersion(String haleyRuleVersion) {
		this.haleyRuleVersion = haleyRuleVersion;
	}
	public String getHaleyRuleDir() {
		return haleyRuleDir;
	}
	public void setHaleyRuleDir(String haleyRuleDir) {
		this.haleyRuleDir = haleyRuleDir;
	}
	public String getHaleyBinDir() {
		return haleyBinDir;
	}
	public void setHaleyBinDir(String haleyBinDir) {
		this.haleyBinDir = haleyBinDir;
	}
	public String getHaleyConfDir() {
		return haleyConfDir;
	}
	public void setHaleyConfDir(String haleyConfDir) {
		this.haleyConfDir = haleyConfDir;
	}
	public String getHaleyLibDir() {
		return haleyLibDir;
	}
	public void setHaleyLibDir(String haleyLibDir) {
		this.haleyLibDir = haleyLibDir;
	}
	public String getReserveOutputDir() {
		return reserveOutputDir;
	}
	public void setReserveOutputDir(String reserveOutputDir) {
		this.reserveOutputDir = reserveOutputDir;
	}
	public String getAcctStmtInputDir() {
		return acctStmtInputDir;
	}
	public void setAcctStmtInputDir(String acctStmtInputDir) {
		this.acctStmtInputDir = acctStmtInputDir;
	}
	public String getAcctStmtOutputDir() {
		return acctStmtOutputDir;
	}
	public void setAcctStmtOutputDir(String acctStmtOutputDir) {
		this.acctStmtOutputDir = acctStmtOutputDir;
	}
	public String getDataInputDir() {
		return dataInputDir;
	}
	public void setDataInputDir(String dataInputDir) {
		this.dataInputDir = dataInputDir;
	}
	public String getDataOutputDir() {
		return dataOutputDir;
	}
	public void setDataOutputDir(String dataOutputDir) {
		this.dataOutputDir = dataOutputDir;
	}
	public String getHaleyLogDir() {
		return haleyLogDir;
	}
	public void setHaleyLogDir(String haleyLogDir) {
		this.haleyLogDir = haleyLogDir;
	}
	
}
