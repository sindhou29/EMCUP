package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Vesting;

public class CompareVesting implements Comparator<Vesting>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Vesting o1, Vesting o2) {
		return (o1.getContractName()+o1.getAccountId()+o1.getPeriodId()).compareTo((o2.getContractName()+o2.getAccountId()+o2.getPeriodId()));
	}
}
