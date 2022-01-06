package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Ftr;

public class CompareFtr implements Comparator<Ftr>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Ftr o1, Ftr o2) {
		return (o1.getContractName()+o1.getNodeId()+o1.getPeriodId()).compareTo((o2.getContractName()+o2.getNodeId()+o2.getPeriodId()));
	}
}
