package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;

public class PeriodNumber  implements Serializable{

	public int total;
	public int sum;
	public int sum2;
	public int avg3;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public int getSum2() {
		return sum2;
	}
	public void setSum2(int sum2) {
		this.sum2 = sum2;
	}
	public int getAvg3() {
		return avg3;
	}
	public void setAvg3(int avg3) {
		this.avg3 = avg3;
	}
}
