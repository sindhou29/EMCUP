package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class BRQ  implements Serializable{

	public String brq_name;
	public String brq_purchase_id;
	public String brq_selling_id;
	public String brq_sold;
	
	public Double brq_purchased;

	public String getBrq_name() {
		return brq_name;
	}

	public void setBrq_name(String brq_name) {
		this.brq_name = brq_name;
	}

	public String getBrq_purchase_id() {
		return brq_purchase_id;
	}

	public void setBrq_purchase_id(String brq_purchase_id) {
		this.brq_purchase_id = brq_purchase_id;
	}

	public String getBrq_selling_id() {
		return brq_selling_id;
	}

	public void setBrq_selling_id(String brq_selling_id) {
		this.brq_selling_id = brq_selling_id;
	}

	public String getBrq_sold() {
		return brq_sold;
	}

	public void setBrq_sold(String brq_sold) {
		this.brq_sold = brq_sold;
	}

	public Double getBrq_purchased() {
		return brq_purchased;
	}

	public void setBrq_purchased(Double brq_purchased) {
		this.brq_purchased = brq_purchased;
	}
}
