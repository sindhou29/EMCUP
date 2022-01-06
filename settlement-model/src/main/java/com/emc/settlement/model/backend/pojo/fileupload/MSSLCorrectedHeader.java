package com.emc.settlement.model.backend.pojo.fileupload;


import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.emc.settlement.model.backend.pojo.DateRange;

public class MSSLCorrectedHeader  implements Serializable{

	
	protected static final Logger logger = Logger.getLogger(MSSLCorrectedHeader.class);
	
	public boolean isValid=false;
	public String content;
	public List<String> periods;
	public List<MSSLCorrectedHeaderDetail> details;
	public int lineNumber;
	public List<String> validSettlementDates;
	public DateRange fDateRange;
	public DateRange rDateRange;
	public DateRange sDateRange;
	public DateRange pDateRange;

	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
	public List<MSSLCorrectedHeaderDetail> getDetails() {
		return details;
	}
	public void setDetails(List<MSSLCorrectedHeaderDetail> details) {
		this.details = details;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public List<String> getValidSettlementDates() {
		return validSettlementDates;
	}
	public void setValidSettlementDates(List<String> validSettlementDates) {
		this.validSettlementDates = validSettlementDates;
	}
	public DateRange getfDateRange() {
		return fDateRange;
	}
	public void setfDateRange(DateRange fDateRange) {
		this.fDateRange = fDateRange;
	}
	public DateRange getrDateRange() {
		return rDateRange;
	}
	public void setrDateRange(DateRange rDateRange) {
		this.rDateRange = rDateRange;
	}
	public DateRange getsDateRange() {
		return sDateRange;
	}
	public void setsDateRange(DateRange sDateRange) {
		this.sDateRange = sDateRange;
	}
	public DateRange getpDateRange() {
		return pDateRange;
	}
	public void setpDateRange(DateRange pDateRange) {
		this.pDateRange = pDateRange;
	}

}
