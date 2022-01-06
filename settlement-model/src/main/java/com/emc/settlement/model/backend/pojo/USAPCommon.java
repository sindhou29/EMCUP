package com.emc.settlement.model.backend.pojo;
import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;


public class USAPCommon  implements Serializable{

	public String eveId;
	public int rowNo;
	public String runDate;
	public String settDate;
	public String settRunId="value";
	public Date settlementDate;
	public String usapCompression="N";
	public String usapContentFormat="CSV";
	public String usapDbResourceId="EMC_DB";
	public String usapFilePath="d:\\OraBPMStudioHome\\log\\";
	public String usapFilenamePrefix="02251";
	public String usapSendingPartyType="SE";
	public int usapTransactionId=0;
	public String usapUserId="MSSLtoSpecify";
	
	public String getEveId() {
		return eveId;
	}
	public void setEveId(String eveId) {
		this.eveId = eveId;
	}
	public int getRowNo() {
		return rowNo;
	}
	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}
	public String getRunDate() {
		return runDate;
	}
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}
	public String getSettDate() {
		return settDate;
	}
	public void setSettDate(String settDate) {
		this.settDate = settDate;
	}
	public String getSettRunId() {
		return settRunId;
	}
	public void setSettRunId(String settRunId) {
		this.settRunId = settRunId;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getUsapCompression() {
		return usapCompression;
	}
	public void setUsapCompression(String usapCompression) {
		this.usapCompression = usapCompression;
	}
	public String getUsapContentFormat() {
		return usapContentFormat;
	}
	public void setUsapContentFormat(String usapContentFormat) {
		this.usapContentFormat = usapContentFormat;
	}
	public String getUsapDbResourceId() {
		return usapDbResourceId;
	}
	public void setUsapDbResourceId(String usapDbResourceId) {
		this.usapDbResourceId = usapDbResourceId;
	}
	public String getUsapFilePath() {
		return usapFilePath;
	}
	public void setUsapFilePath(String usapFilePath) {
		this.usapFilePath = usapFilePath;
	}
	public String getUsapFilenamePrefix() {
		return usapFilenamePrefix;
	}
	public void setUsapFilenamePrefix(String usapFilenamePrefix) {
		this.usapFilenamePrefix = usapFilenamePrefix;
	}
	public String getUsapSendingPartyType() {
		return usapSendingPartyType;
	}
	public void setUsapSendingPartyType(String usapSendingPartyType) {
		this.usapSendingPartyType = usapSendingPartyType;
	}
	public int getUsapTransactionId() {
		return usapTransactionId;
	}
	public void setUsapTransactionId(int usapTransactionId) {
		this.usapTransactionId = usapTransactionId;
	}
	public String getUsapUserId() {
		return usapUserId;
	}
	public void setUsapUserId(String usapUserId) {
		this.usapUserId = usapUserId;
	}
	
	protected static final Logger logger = Logger.getLogger(USAPCommon.class);
	
	public void initializeDbItem()
	{
		this.settRunId = null;
		this.settDate = null;
		this.eveId = null;
		this.runDate = null;
	}
}
