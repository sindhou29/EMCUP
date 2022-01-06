package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Facility;

public class CompareFacility implements Comparator<Facility>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Facility o1, Facility o2) {
		return (o1.getKey()).compareTo((o2.getKey()));
	}
}
