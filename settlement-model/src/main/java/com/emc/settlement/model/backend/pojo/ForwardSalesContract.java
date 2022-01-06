package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class ForwardSalesContract  implements Serializable{

	public String fsc_externalId;
	public Double fsc_price;
	public Double fsc_quantity;
	
	public String getFsc_externalId() {
		return fsc_externalId;
	}
	public void setFsc_externalId(String fsc_externalId) {
		this.fsc_externalId = fsc_externalId;
	}
	public Double getFsc_price() {
		return fsc_price;
	}
	public void setFsc_price(Double fsc_price) {
		this.fsc_price = fsc_price;
	}
	public Double getFsc_quantity() {
		return fsc_quantity;
	}
	public void setFsc_quantity(Double fsc_quantity) {
		this.fsc_quantity = fsc_quantity;
	}
}
