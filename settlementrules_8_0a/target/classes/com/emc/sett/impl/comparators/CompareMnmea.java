package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Mnmea;

public class CompareMnmea implements Comparator<Mnmea>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Mnmea o1, Mnmea o2) {
		// sort by run type, sett date and period
		return (o1.getRunType()+o1.getSettDate().toString()+o1.getPeriodId()).compareTo((o2.getRunType()+o2.getSettDate().toString()+o2.getPeriodId()));
	}
}
