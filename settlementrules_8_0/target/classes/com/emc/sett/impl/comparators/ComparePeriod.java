package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Period;

public class ComparePeriod implements Comparator<Period>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Period o1, Period o2) {
		// sort by run account and period
		return (o1.getAccountId()+o1.getPeriodId()).compareTo((o2.getAccountId()+o2.getPeriodId()));
	}
}
