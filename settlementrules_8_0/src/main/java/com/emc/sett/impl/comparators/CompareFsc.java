package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Fsc;

public class CompareFsc implements Comparator<Fsc>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Fsc o1, Fsc o2) {
		return (o1.getContractName()+o1.getAccountId()+o1.getPeriodId()).compareTo((o2.getContractName()+o2.getAccountId()+o2.getPeriodId()));
	}
}
