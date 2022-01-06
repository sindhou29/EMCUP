package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Adjustment;

public class CompareAdjustment implements Comparator<Adjustment>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Adjustment o1, Adjustment o2) {
		String o1Key = (o1.getRunType()+o1.getSettDate().toString()+o1.getPeriodId()+o1.getName());
		String o2Key = (o2.getRunType()+o2.getSettDate().toString()+o2.getPeriodId()+o2.getName());
		return (o1Key).compareTo((o2Key));
	}
}
