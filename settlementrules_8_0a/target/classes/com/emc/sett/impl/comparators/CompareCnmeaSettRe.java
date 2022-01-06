package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.CnmeaSettRe;

public class CompareCnmeaSettRe implements Comparator<CnmeaSettRe>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(CnmeaSettRe o1, CnmeaSettRe o2) {
		return (o1.getVersion()+o1.getAccountId()+o1.getPeriodId()).compareTo((o2.getVersion()+o2.getAccountId()+o2.getPeriodId()));
	}
}
