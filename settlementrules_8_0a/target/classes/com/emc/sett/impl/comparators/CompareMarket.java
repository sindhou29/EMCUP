package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Market;

public class CompareMarket implements Comparator<Market>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Market o1, Market o2) {
		return (o1.getKey()).compareTo((o2.getKey()));
	}
}
