package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.RsvClass;

public class CompareRsvClass implements Comparator<RsvClass>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(RsvClass o1, RsvClass o2) {
		return (o1.getKey()).compareTo((o2.getKey()));
	}
}
