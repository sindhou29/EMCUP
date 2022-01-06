package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;
import java.util.Date;

public class MSSLCorrected  implements Serializable{

	public String nodeId;
	public int period;
	public Double quantity;
	public String quantityType;
	public String sacId;
	public Date settlementDate;
	
	public MSSLCorrected() {
		// TODO Auto-generated constructor stub
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getQuantityType() {
		return quantityType;
	}

	public void setQuantityType(String quantityType) {
		this.quantityType = quantityType;
	}

	public String getSacId() {
		return sacId;
	}

	public void setSacId(String sacId) {
		this.sacId = sacId;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

}
