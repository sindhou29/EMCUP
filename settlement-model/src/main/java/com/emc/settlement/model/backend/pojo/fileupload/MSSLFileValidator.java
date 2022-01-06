/**
 * 
 */
package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;
import java.util.Date;

import java.util.Map;


import org.apache.log4j.Logger;



/**
 * @author DWTN1561
 *
 */
public class MSSLFileValidator  implements Serializable{
	
	protected static final Logger logger = Logger.getLogger(MSSLFileValidator.class);

	/**
	 * 
	 */
	public MSSLFileValidator() {
		// TODO Auto-generated constructor stub
	}
	
	public String logPrefix;
	public String lastAccount;
	public String lastNodeName;
	public String lastNodeId;
	public String lastSacId;
	public String standingVersion;
	public String fileType = "DMF";
	public int sumP;
	public int sumP2;
	public int totalP;
	public MSSLCorrectedHeader cmHeader = new MSSLCorrectedHeader();
	public boolean priceNeuDateChecked;
	public boolean newCMFFormat=true;
	public boolean ntEffective;
	public boolean drEffective;
	public Date drEffectiveDate;
	public Date priceNeuDate;
	public Date ntEffectiveDate;
	public Date settlementDate;
	public Map<String,Integer> total;
	public Map<String,Integer> square;
	
	public String getLogPrefix() {
		return logPrefix;
	}

	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}

	public String getLastAccount() {
		return lastAccount;
	}

	public void setLastAccount(String lastAccount) {
		this.lastAccount = lastAccount;
	}

	public String getLastNodeName() {
		return lastNodeName;
	}

	public void setLastNodeName(String lastNodeName) {
		this.lastNodeName = lastNodeName;
	}

	public String getLastNodeId() {
		return lastNodeId;
	}

	public void setLastNodeId(String lastNodeId) {
		this.lastNodeId = lastNodeId;
	}

	public String getLastSacId() {
		return lastSacId;
	}

	public void setLastSacId(String lastSacId) {
		this.lastSacId = lastSacId;
	}

	public String getStandingVersion() {
		return standingVersion;
	}

	public void setStandingVersion(String standingVersion) {
		this.standingVersion = standingVersion;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getSumP() {
		return sumP;
	}

	public void setSumP(int sumP) {
		this.sumP = sumP;
	}

	public int getSumP2() {
		return sumP2;
	}

	public void setSumP2(int sumP2) {
		this.sumP2 = sumP2;
	}

	public int getTotalP() {
		return totalP;
	}

	public void setTotalP(int totalP) {
		this.totalP = totalP;
	}

	public MSSLCorrectedHeader getCmHeader() {
		return cmHeader;
	}

	public void setCmHeader(MSSLCorrectedHeader cmHeader) {
		this.cmHeader = cmHeader;
	}

	public boolean isPriceNeuDateChecked() {
		return priceNeuDateChecked;
	}

	public void setPriceNeuDateChecked(boolean priceNeuDateChecked) {
		this.priceNeuDateChecked = priceNeuDateChecked;
	}

	public boolean isNewCMFFormat() {
		return newCMFFormat;
	}

	public void setNewCMFFormat(boolean newCMFFormat) {
		this.newCMFFormat = newCMFFormat;
	}

	public boolean isNtEffective() {
		return ntEffective;
	}

	public void setNtEffective(boolean ntEffective) {
		this.ntEffective = ntEffective;
	}

	public boolean isDrEffective() {
		return drEffective;
	}

	public void setDrEffective(boolean drEffective) {
		this.drEffective = drEffective;
	}

	public Date getDrEffectiveDate() {
		return drEffectiveDate;
	}

	public void setDrEffectiveDate(Date drEffectiveDate) {
		this.drEffectiveDate = drEffectiveDate;
	}

	public Date getPriceNeuDate() {
		return priceNeuDate;
	}

	public void setPriceNeuDate(Date priceNeuDate) {
		this.priceNeuDate = priceNeuDate;
	}

	public Date getNtEffectiveDate() {
		return ntEffectiveDate;
	}

	public void setNtEffectiveDate(Date ntEffectiveDate) {
		this.ntEffectiveDate = ntEffectiveDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public Map<String,Integer> getTotal() {
		return total;
	}

	public void setTotal(Map<String,Integer>  total) {
		this.total = total;
	}

	public Map<String,Integer>  getSquare() {
		return square;
	}

	public void setSquare(Map<String,Integer>  square) {
		this.square = square;
	}

}
