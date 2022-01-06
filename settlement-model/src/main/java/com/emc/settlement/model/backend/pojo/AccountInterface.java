package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountInterface  implements Serializable{

	public String calcCode;
	public BigDecimal calculationTotal;
	public String clientId;
	public String description;
	public String eveId;
	public BigDecimal gstAmount;
	public String gstCode;
	public String npcId;
	public Date runDate;
	public String runId;
	public String runType;
	public String sacId;
	public String sacVersion;
	public Date settlementDate;
	public String srtId;
	public String srtVersion;
	
	public String getCalcCode() {
		return calcCode;
	}
	public void setCalcCode(String calcCode) {
		this.calcCode = calcCode;
	}
	public BigDecimal getCalculationTotal() {
		return calculationTotal;
	}
	public void setCalculationTotal(BigDecimal calculationTotal) {
		this.calculationTotal = calculationTotal;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEveId() {
		return eveId;
	}
	public void setEveId(String eveId) {
		this.eveId = eveId;
	}
	public BigDecimal getGstAmount() {
		return gstAmount;
	}
	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}
	public String getGstCode() {
		return gstCode;
	}
	public void setGstCode(String gstCode) {
		this.gstCode = gstCode;
	}
	public String getNpcId() {
		return npcId;
	}
	public void setNpcId(String npcId) {
		this.npcId = npcId;
	}
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}
	public String getRunType() {
		return runType;
	}
	public void setRunType(String runType) {
		this.runType = runType;
	}
	public String getSacId() {
		return sacId;
	}
	public void setSacId(String sacId) {
		this.sacId = sacId;
	}
	public String getSacVersion() {
		return sacVersion;
	}
	public void setSacVersion(String sacVersion) {
		this.sacVersion = sacVersion;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getSrtId() {
		return srtId;
	}
	public void setSrtId(String srtId) {
		this.srtId = srtId;
	}
	public String getSrtVersion() {
		return srtVersion;
	}
	public void setSrtVersion(String srtVersion) {
		this.srtVersion = srtVersion;
	}
	
	
	
	public void initializeDbItem()
	{
		calcCode = null;
		calculationTotal = new BigDecimal(0.00000);
		clientId = null;
		description = null;
		eveId = null;
		gstAmount = new BigDecimal(0.00000);
		gstCode = null;
		npcId = null;
		runId = null;
		runType = null;
		sacId = null;
		sacVersion = null;
		settlementDate = null;
		srtId = null;
		srtVersion = null;
		runDate = null;
	}
	
}
