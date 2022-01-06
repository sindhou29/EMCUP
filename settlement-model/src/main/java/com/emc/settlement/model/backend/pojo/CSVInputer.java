package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Priority;

public class CSVInputer  implements Serializable{

	public String dbpath;
	public String mcPricePkgVer;
	public String msslQtyPkgVer;
	public String sqlLCP;
	public String sqlPrice;
	public String sqlQty;
	public String sqlQty2;

	public int interval_MFP_count;
	public int totalPeriod;

	public Date settDate;

	
	public CSVInputer() {
		super();
	}
	
	
	public String getDbpath() {
		return dbpath;
	}

	public void setDbpath(String dbpath) {
		this.dbpath = dbpath;
	}

	public String getMcPricePkgVer() {
		return mcPricePkgVer;
	}

	public void setMcPricePkgVer(String mcPricePkgVer) {
		this.mcPricePkgVer = mcPricePkgVer;
	}

	public String getMsslQtyPkgVer() {
		return msslQtyPkgVer;
	}

	public void setMsslQtyPkgVer(String msslQtyPkgVer) {
		this.msslQtyPkgVer = msslQtyPkgVer;
	}

	public String getSqlLCP() {
		return sqlLCP;
	}

	public void setSqlLCP(String sqlLCP) {
		this.sqlLCP = sqlLCP;
	}

	public String getSqlPrice() {
		return sqlPrice;
	}

	public void setSqlPrice(String sqlPrice) {
		this.sqlPrice = sqlPrice;
	}

	public String getSqlQty() {
		return sqlQty;
	}

	public void setSqlQty(String sqlQty) {
		this.sqlQty = sqlQty;
	}

	public String getSqlQty2() {
		return sqlQty2;
	}

	public void setSqlQty2(String sqlQty2) {
		this.sqlQty2 = sqlQty2;
	}

	public int getInterval_MFP_count() {
		return interval_MFP_count;
	}

	public void setInterval_MFP_count(int interval_MFP_count) {
		this.interval_MFP_count = interval_MFP_count;
	}

	public int getTotalPeriod() {
		return totalPeriod;
	}

	public void setTotalPeriod(int totalPeriod) {
		this.totalPeriod = totalPeriod;
	}

	public Date getSettDate() {
		return settDate;
	}

	public void setSettDate(Date settDate) {
		this.settDate = settDate;
	}
	
	
}
