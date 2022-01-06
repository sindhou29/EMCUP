package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.AbstractMap;

public class RestRequest  implements Serializable{

	
	AbstractMap.SimpleEntry<String, Object> runPackage;
	
	AbstractMap.SimpleEntry<String, Object> runParams;
	
	AbstractMap.SimpleEntry<String, Object> alert;

	
	public AbstractMap.SimpleEntry<String, Object> getRunPackage() {
		return runPackage;
	}

	public void setRunPackage(AbstractMap.SimpleEntry<String, Object> runPackage) {
		this.runPackage = runPackage;
	}

	public AbstractMap.SimpleEntry<String, Object> getRunParams() {
		return runParams;
	}

	public void setRunParams(AbstractMap.SimpleEntry<String, Object> runParams) {
		this.runParams = runParams;
	}
	
	public AbstractMap.SimpleEntry<String, Object> getAlert() {
		return alert;
	}

	public void setAlert(AbstractMap.SimpleEntry<String, Object> alert) {
		this.alert = alert;
	}
}
