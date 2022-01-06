package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;

public class DateRange  implements Serializable{

	
	public String from;
	public String to;
	
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
}
