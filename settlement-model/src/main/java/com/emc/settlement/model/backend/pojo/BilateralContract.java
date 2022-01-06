package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

public class BilateralContract  implements Serializable{

	public Double bilateral_BAQ_purchased;
	public Double bilateral_BAQ_sold;
	public Double bilateral_BFQ_purchased;
	public Double bilateral_BFQ_sold;
	public Double bilateral_BIF_purchased;
	public Double bilateral_BIF_sold;
	public Double bilateral_BIQ_purchased;
	public Double bilateral_BIQ_sold;
	public Double bilateral_BWF_purchased;
	public Double bilateral_BWF_sold;
	public Double bilateral_BWQ_purchased;
	public Double bilateral_BWQ_sold;
	public Double bilateral_IEQ_generated;
	public Double bilateral_IEQ_sold;

	public String bilateral_account;
	public String bilateral_interval;
	public String bilateral_name;
	
	
	
	public Double getBilateral_BAQ_purchased() {
		return bilateral_BAQ_purchased;
	}
	public void setBilateral_BAQ_purchased(Double bilateral_BAQ_purchased) {
		this.bilateral_BAQ_purchased = bilateral_BAQ_purchased;
	}
	public Double getBilateral_BAQ_sold() {
		return bilateral_BAQ_sold;
	}
	public void setBilateral_BAQ_sold(Double bilateral_BAQ_sold) {
		this.bilateral_BAQ_sold = bilateral_BAQ_sold;
	}
	public Double getBilateral_BFQ_purchased() {
		return bilateral_BFQ_purchased;
	}
	public void setBilateral_BFQ_purchased(Double bilateral_BFQ_purchased) {
		this.bilateral_BFQ_purchased = bilateral_BFQ_purchased;
	}
	public Double getBilateral_BFQ_sold() {
		return bilateral_BFQ_sold;
	}
	public void setBilateral_BFQ_sold(Double bilateral_BFQ_sold) {
		this.bilateral_BFQ_sold = bilateral_BFQ_sold;
	}
	public Double getBilateral_BIF_purchased() {
		return bilateral_BIF_purchased;
	}
	public void setBilateral_BIF_purchased(Double bilateral_BIF_purchased) {
		this.bilateral_BIF_purchased = bilateral_BIF_purchased;
	}
	public Double getBilateral_BIF_sold() {
		return bilateral_BIF_sold;
	}
	public void setBilateral_BIF_sold(Double bilateral_BIF_sold) {
		this.bilateral_BIF_sold = bilateral_BIF_sold;
	}
	public Double getBilateral_BIQ_purchased() {
		return bilateral_BIQ_purchased;
	}
	public void setBilateral_BIQ_purchased(Double bilateral_BIQ_purchased) {
		this.bilateral_BIQ_purchased = bilateral_BIQ_purchased;
	}
	public Double getBilateral_BIQ_sold() {
		return bilateral_BIQ_sold;
	}
	public void setBilateral_BIQ_sold(Double bilateral_BIQ_sold) {
		this.bilateral_BIQ_sold = bilateral_BIQ_sold;
	}
	public Double getBilateral_BWF_purchased() {
		return bilateral_BWF_purchased;
	}
	public void setBilateral_BWF_purchased(Double bilateral_BWF_purchased) {
		this.bilateral_BWF_purchased = bilateral_BWF_purchased;
	}
	public Double getBilateral_BWF_sold() {
		return bilateral_BWF_sold;
	}
	public void setBilateral_BWF_sold(Double bilateral_BWF_sold) {
		this.bilateral_BWF_sold = bilateral_BWF_sold;
	}
	public Double getBilateral_BWQ_purchased() {
		return bilateral_BWQ_purchased;
	}
	public void setBilateral_BWQ_purchased(Double bilateral_BWQ_purchased) {
		this.bilateral_BWQ_purchased = bilateral_BWQ_purchased;
	}
	public Double getBilateral_BWQ_sold() {
		return bilateral_BWQ_sold;
	}
	public void setBilateral_BWQ_sold(Double bilateral_BWQ_sold) {
		this.bilateral_BWQ_sold = bilateral_BWQ_sold;
	}
	public Double getBilateral_IEQ_generated() {
		return bilateral_IEQ_generated;
	}
	public void setBilateral_IEQ_generated(Double bilateral_IEQ_generated) {
		this.bilateral_IEQ_generated = bilateral_IEQ_generated;
	}
	public Double getBilateral_IEQ_sold() {
		return bilateral_IEQ_sold;
	}
	public void setBilateral_IEQ_sold(Double bilateral_IEQ_sold) {
		this.bilateral_IEQ_sold = bilateral_IEQ_sold;
	}
	public String getBilateral_account() {
		return bilateral_account;
	}
	public void setBilateral_account(String bilateral_account) {
		this.bilateral_account = bilateral_account;
	}
	public String getBilateral_interval() {
		return bilateral_interval;
	}
	public void setBilateral_interval(String bilateral_interval) {
		this.bilateral_interval = bilateral_interval;
	}
	public String getBilateral_name() {
		return bilateral_name;
	}
	public void setBilateral_name(String bilateral_name) {
		this.bilateral_name = bilateral_name;
	}
}
