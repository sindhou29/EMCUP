package com.emc.settlement.model.backend.pojo.fileupload;


import java.io.Serializable;
import java.util.List;


public class MSSLCorrectedHeaderDetail  implements Serializable{

	public String name;
	public List<String> periods;
	public List<DateRange> ranges;
	public List<String> datesFromCorrectedData;
	public String type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
	public List<DateRange> getRanges() {
		return ranges;
	}
	public void setRanges(List<DateRange> ranges) {
		this.ranges = ranges;
	}
	public List<String> getDatesFromCorrectedData() {
		return datesFromCorrectedData;
	}
	public void setDatesFromCorrectedData(List<String> datesFromCorrectedData) {
		this.datesFromCorrectedData = datesFromCorrectedData;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
