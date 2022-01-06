package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;

public class SACInfo  implements Serializable{
	
	public String embeddedGen;
	public String externalId;
	public String retailerId;
	public String sacId;
	public String sacType;
	
	
	public String getEmbeddedGen() {
		return embeddedGen;
	}
	public void setEmbeddedGen(String embeddedGen) {
		this.embeddedGen = embeddedGen;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getRetailerId() {
		return retailerId;
	}
	public void setRetailerId(String retailerId) {
		this.retailerId = retailerId;
	}
	public String getSacId() {
		return sacId;
	}
	public void setSacId(String sacId) {
		this.sacId = sacId;
	}
	public String getSacType() {
		return sacType;
	}
	public void setSacType(String sacType) {
		this.sacType = sacType;
	}
	

}
