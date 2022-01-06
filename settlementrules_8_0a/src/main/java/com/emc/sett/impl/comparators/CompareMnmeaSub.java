package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.MnmeaSub;

public class CompareMnmeaSub implements Comparator<MnmeaSub>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(MnmeaSub o1, MnmeaSub o2) {
		if(o2.getSettDate() != null && o1.getSettDate() != null) {
			// SATSHARP-245, sort by date in reverse order
			//return o2.getSettDate().compare(o1.getSettDate());
			// PROD1SHARP-40, sort date in ascending order and group by run type
			String k1 = o1.getRunType()+o1.getSettDate().toString();
			String k2 = o2.getRunType()+o2.getSettDate().toString();
			return k1.compareTo(k2);
		} else {
			return (o2.getRerunId()).compareTo(o1.getRerunId());
		}
	}
}
