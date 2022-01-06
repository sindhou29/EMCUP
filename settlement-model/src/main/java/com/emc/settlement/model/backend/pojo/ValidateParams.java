package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

public class ValidateParams  implements Serializable{

	public Date readyByDate;
	public Date readyByRundate;
	public String eveid;
	
	public Date getReadyByDate() {
		return readyByDate;
	}
	public void setReadyByDate(Date readyByDate) {
		this.readyByDate = readyByDate;
	}
	public Date getReadyByRundate() {
		return readyByRundate;
	}
	public void setReadyByRundate(Date readyByRundate) {
		this.readyByRundate = readyByRundate;
	}
	public String getEveid() {
		return eveid;
	}
	public void setEveid(String eveid) {
		this.eveid = eveid;
	}
}
