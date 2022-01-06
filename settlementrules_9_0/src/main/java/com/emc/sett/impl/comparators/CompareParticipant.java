package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Participant;

public class CompareParticipant implements Comparator<Participant>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Participant o1, Participant o2) {
		return (o1.getKey()).compareTo((o2.getKey()));
	}
}
