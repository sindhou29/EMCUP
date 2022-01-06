package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Reserve;

public class CompareReserve implements Comparator<Reserve>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Reserve o1, Reserve o2) {
		return (o1.getPeriodId()+o1.getNode()+o1.getName()).compareTo((o2.getPeriodId()+o2.getNode()+o2.getName()));
	}
}
