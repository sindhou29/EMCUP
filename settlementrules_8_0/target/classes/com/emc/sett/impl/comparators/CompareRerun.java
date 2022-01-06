package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Rerun;

public class CompareRerun implements Comparator<Rerun>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Rerun o1, Rerun o2) {
		String o1Key = (o1.getName()+o1.getGstRate().toString()+(o1.isTaxable() ? "1" : "0"));
		String o2Key = (o2.getName()+o2.getGstRate().toString()+(o2.isTaxable() ? "1" : "0"));
		return (o1Key).compareTo((o2Key));
	}
}
