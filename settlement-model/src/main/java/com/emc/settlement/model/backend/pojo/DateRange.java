package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

public class DateRange  implements Serializable{
	
	public Date startDate;
	public Date endDate;
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	

}
