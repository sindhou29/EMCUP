package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Brq;

public class CompareBrq implements Comparator<Brq>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Brq o1, Brq o2) {
		return (o1.getAccountId()+o1.getPeriodId()).compareTo((o2.getAccountId()+o2.getPeriodId()));
	}
}
