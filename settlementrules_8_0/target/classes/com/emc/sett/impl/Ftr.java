package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.FtrT;

public class Ftr extends FtrT {

	public void initInput(String [] line) {
		
		String ftr_ftq = line[0];
		String ftr_name = line[1];
		String ftr_node = line[2];
		int interval = Integer.parseInt(line[3]);
		String ftr_interval = String.format("%1$02d", interval);
		
		this.ftq = ftr_ftq.length() > 0? new BigDecimal(ftr_ftq): null;
		this.contractName = ftr_name;
		this.nodeId = ftr_node;
		this.periodId = ftr_interval;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return periodId + nodeId;
	}
	
	public String toInputString() {
		
		String result = (this.ftq != null? this.ftq.toString(): "") + "," +
				(this.contractName != null? this.contractName: "") + "," +
				(this.nodeId != null? this.nodeId: "") + "," +
				(this.periodId != null? this.periodId: "");
		
		return result;
	}
	
	public String toOutputString() {
		
		String result = (this.ftq != null? this.ftq.toString(): "") + "," +
				(this.contractName != null? this.contractName: "") + "," +
				(this.nodeId != null? this.nodeId: "") + "," +
				(this.periodId != null? this.periodId: "");
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"ftq," +
			"contractName," +
			"nodeId," +
			"periodId";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"ftq," +
			"contractName," +
			"nodeId," +
			"periodId";

		return header;
	}
}
