/**
 * 
 */
package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;
import java.util.Date;

/**
 * @author DWTN1561
 *
 */
public class ClawbackQuantities  implements Serializable{
	
	public String ancillaryType;
	public String id;
	public String ndeId;
	public String ndeVersion;
	public Integer period;
	public String recordType;
	public Date settlementDate;
	public String sewUploadEventsID;
	public String version;
	
	public String getAncillaryType() {
		return ancillaryType;
	}
	public void setAncillaryType(String ancillaryType) {
		this.ancillaryType = ancillaryType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNdeId() {
		return ndeId;
	}
	public void setNdeId(String ndeId) {
		this.ndeId = ndeId;
	}
	public String getNdeVersion() {
		return ndeVersion;
	}
	public void setNdeVersion(String ndeVersion) {
		this.ndeVersion = ndeVersion;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public Date getSettlementDate() {
		return settlementDate;
	}
	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	public String getSewUploadEventsID() {
		return sewUploadEventsID;
	}
	public void setSewUploadEventsID(String sewUploadEventsID) {
		this.sewUploadEventsID = sewUploadEventsID;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	

}
