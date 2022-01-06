package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CLASS)
public class SettlementRunParams implements Serializable {
	private static final long serialVersionUID = -1;

	public Date settlementDate;
	public java.sql.Date sqlSettlementDate;
	public String runType = "P";
	public String runFrom;
	public String runUser;
	public String comment;
	public String runId;
	public String runPkgId;
	public Date runDate;
	public java.sql.Date sqlRunDate;
	public String mainEveId;
	public String runEveId;
	public Date toSettlementDate;
	public java.sql.Date sqlToSettlementDate;
	public Date fromSettlementDate;
	public java.sql.Date sqlFromSettlementDate;
	public String schEveId;
	public boolean ignoreScheduledRunCheck = false;
	public boolean isFSCEffective = false;
	public boolean isWMEPRoundedFeeEffDate = false;

	public boolean testingMode = false;
	public boolean regressionMode = false;
	public String regressionData = "";
	public String csvStorage = "";
	public boolean mceDataLoadByController = false;

	public boolean isMceDataLoadByController() {
		return mceDataLoadByController;
	}

	public void setMceDataLoadByController(boolean mceDataLoadByController) {
		this.mceDataLoadByController = mceDataLoadByController;
	}

	public SettlementRunParams() {
	}

	public SettlementRunParams(SettlementRunParams params) {
		this.settlementDate = params.settlementDate;
		this.sqlSettlementDate = params.sqlSettlementDate;
		this.runType = params.runType;
		this.runFrom = params.runFrom;
		this.runUser = params.runUser;
		this.comment = params.comment;
		this.runId = params.runId;
		this.runPkgId = params.runPkgId;
		this.runDate = params.runDate;
		this.sqlRunDate = params.sqlRunDate;
		this.mainEveId = params.mainEveId;
		this.runEveId = params.runEveId;
		this.toSettlementDate = params.toSettlementDate;
		this.fromSettlementDate = params.fromSettlementDate;
		this.sqlToSettlementDate = params.sqlToSettlementDate;
		this.sqlFromSettlementDate = params.sqlFromSettlementDate;
		this.schEveId = params.schEveId;
		this.ignoreScheduledRunCheck = params.ignoreScheduledRunCheck;
		this.isFSCEffective = params.isFSCEffective;
		this.isWMEPRoundedFeeEffDate = params.isWMEPRoundedFeeEffDate;

		this.testingMode = params.testingMode;
		this.regressionMode = params.regressionMode;
		this.regressionData = params.regressionData;
		this.csvStorage = params.csvStorage;
		this.mceDataLoadByController = params.mceDataLoadByController;
	}

	public java.sql.Date getSqlRunDate() {
		return sqlRunDate;
	}

	public void setSqlRunDate(java.sql.Date sqlRunDate) {
		this.sqlRunDate = sqlRunDate;
	}

	public java.sql.Date getSqlToSettlementDate() {
		return sqlToSettlementDate;
	}

	public void setSqlToSettlementDate(java.sql.Date sqlToSettlementDate) {
		this.sqlToSettlementDate = sqlToSettlementDate;
	}

	public java.sql.Date getSqlFromSettlementDate() {
		return sqlFromSettlementDate;
	}

	public void setSqlFromSettlementDate(java.sql.Date sqlFromSettlementDate) {
		this.sqlFromSettlementDate = sqlFromSettlementDate;
	}

	public java.sql.Date getSqlSettlementDate() {
		return sqlSettlementDate;
	}

	public void setSqlSettlementDate(java.sql.Date sqlSettlementDate) {
		this.sqlSettlementDate = sqlSettlementDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	public String getRunFrom() {
		return runFrom;
	}

	public void setRunFrom(String runFrom) {
		this.runFrom = runFrom;
	}

	public String getRunUser() {
		return runUser;
	}

	public void setRunUser(String runUser) {
		this.runUser = runUser;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getRunPkgId() {
		return runPkgId;
	}

	public void setRunPkgId(String runPkgId) {
		this.runPkgId = runPkgId;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public String getMainEveId() {
		return mainEveId;
	}

	public void setMainEveId(String mainEveId) {
		this.mainEveId = mainEveId;
	}

	public String getRunEveId() {
		return runEveId;
	}

	public void setRunEveId(String runEveId) {
		this.runEveId = runEveId;
	}

	public Date getToSettlementDate() {
		return toSettlementDate;
	}

	public void setToSettlementDate(Date toSettlementDate) {
		this.toSettlementDate = toSettlementDate;
	}

	public Date getFromSettlementDate() {
		return fromSettlementDate;
	}

	public void setFromSettlementDate(Date fromSettlementDate) {
		this.fromSettlementDate = fromSettlementDate;
	}

	public String getSchEveId() {
		return schEveId;
	}

	public void setSchEveId(String schEveId) {
		this.schEveId = schEveId;
	}

	public boolean isIgnoreScheduledRunCheck() {
		return ignoreScheduledRunCheck;
	}

	public void setIgnoreScheduledRunCheck(boolean ignoreScheduledRunCheck) {
		this.ignoreScheduledRunCheck = ignoreScheduledRunCheck;
	}

	public boolean isFSCEffective() {
		return isFSCEffective;
	}

	public void setFSCEffective(boolean isFSCEffective) {
		this.isFSCEffective = isFSCEffective;
	}

	public boolean isWMEPRoundedFeeEffDate() {
		return isWMEPRoundedFeeEffDate;
	}

	public void setWMEPRoundedFeeEffDate(boolean isWMEPRoundedFeeEffDate) {
		this.isWMEPRoundedFeeEffDate = isWMEPRoundedFeeEffDate;
	}

	public boolean isTestingMode() {
		return testingMode;
	}

	public void setTestingMode(boolean testingMode) {
		this.testingMode = testingMode;
	}

	public boolean isRegressionMode() {
		return regressionMode;
	}

	public void setRegressionMode(boolean regressionMode) {
		this.regressionMode = regressionMode;
	}

	public String getRegressionData() {
		return regressionData;
	}

	public void setRegressionData(String regressionData) {
		this.regressionData = regressionData;
	}

	public String getCsvStorage() {
		return csvStorage;
	}

	public void setCsvStorage(String csvStorage) {
		this.csvStorage = csvStorage;
	}
}
