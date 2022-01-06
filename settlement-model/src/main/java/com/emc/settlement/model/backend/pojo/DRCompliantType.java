package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class DRCompliantType  implements Serializable{

	public Double lcq;
	public Double oiec;
	public Double siec;
	public Double wlq;
	
	public String compliantFlag;

	public Double getLcq() {
		return lcq;
	}

	public void setLcq(Double lcq) {
		this.lcq = lcq;
	}

	public Double getOiec() {
		return oiec;
	}

	public void setOiec(Double oiec) {
		this.oiec = oiec;
	}

	public Double getSiec() {
		return siec;
	}

	public void setSiec(Double siec) {
		this.siec = siec;
	}

	public Double getWlq() {
		return wlq;
	}

	public void setWlq(Double wlq) {
		this.wlq = wlq;
	}

	public String getCompliantFlag() {
		return compliantFlag;
	}

	public void setCompliantFlag(String compliantFlag) {
		this.compliantFlag = compliantFlag;
	}
}
