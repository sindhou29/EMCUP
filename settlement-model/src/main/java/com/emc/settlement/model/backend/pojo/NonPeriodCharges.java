package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class NonPeriodCharges  implements Serializable{

	public Double amount;
	public Double inputGst;
	public Double outputGst;
	public Double total;
	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getInputGst() {
		return inputGst;
	}
	public void setInputGst(Double inputGst) {
		this.inputGst = inputGst;
	}
	public Double getOutputGst() {
		return outputGst;
	}
	public void setOutputGst(Double outputGst) {
		this.outputGst = outputGst;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	
}
