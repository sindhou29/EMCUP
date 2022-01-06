package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class NemAccount  implements Serializable{

	public String acgId;
	public String acgName;
	public String nodeId;
	public String sacDisplayTitle;
	public String sacId;
	
	public String getAcgId() {
		return acgId;
	}
	public void setAcgId(String acgId) {
		this.acgId = acgId;
	}
	public String getAcgName() {
		return acgName;
	}
	public void setAcgName(String acgName) {
		this.acgName = acgName;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getSacDisplayTitle() {
		return sacDisplayTitle;
	}
	public void setSacDisplayTitle(String sacDisplayTitle) {
		this.sacDisplayTitle = sacDisplayTitle;
	}
	public String getSacId() {
		return sacId;
	}
	public void setSacId(String sacId) {
		this.sacId = sacId;
	}
	
	
}
