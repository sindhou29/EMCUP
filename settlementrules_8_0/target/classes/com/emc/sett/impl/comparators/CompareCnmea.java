package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Cnmea;

public class CompareCnmea implements Comparator<Cnmea>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Cnmea o1, Cnmea o2) {
		return (o1.getKey()).compareTo((o2.getKey()));
	}
}
