package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataValidator  implements Serializable{

	public String settlement_DATE_FORMAT="DD-MON-YYYY";
	public String business_DAY_VALUE="B";
	public String message;
	public String sacId;
	public String sacVersion;
	public String sellingPTPID;
	public String sellingPTPName="";
	public String inputSellingPTPName="";
	public boolean isValid;
	
	
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getSettlement_DATE_FORMAT() {
		return settlement_DATE_FORMAT;
	}
	public void setSettlement_DATE_FORMAT(String settlement_DATE_FORMAT) {
		this.settlement_DATE_FORMAT = settlement_DATE_FORMAT;
	}
	public String getBusiness_DAY_VALUE() {
		return business_DAY_VALUE;
	}
	public void setBusiness_DAY_VALUE(String business_DAY_VALUE) {
		this.business_DAY_VALUE = business_DAY_VALUE;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSacId() {
		return sacId;
	}
	public void setSacId(String sacId) {
		this.sacId = sacId;
	}
	public String getSacVersion() {
		return sacVersion;
	}
	public void setSacVersion(String sacVersion) {
		this.sacVersion = sacVersion;
	}
	public String getSellingPTPID() {
		return sellingPTPID;
	}
	public void setSellingPTPID(String sellingPTPID) {
		this.sellingPTPID = sellingPTPID;
	}
	public String getSellingPTPName() {
		return sellingPTPName;
	}
	public void setSellingPTPName(String sellingPTPName) {
		this.sellingPTPName = sellingPTPName;
	}
	public String getInputSellingPTPName() {
		return inputSellingPTPName;
	}
	public void setInputSellingPTPName(String inputSellingPTPName) {
		this.inputSellingPTPName = inputSellingPTPName;
	}

}
