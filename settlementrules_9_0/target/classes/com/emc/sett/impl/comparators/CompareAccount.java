package com.emc.sett.impl.comparators;

import java.io.Serializable;
import java.util.Comparator;

import com.emc.sett.impl.Account;

public class CompareAccount implements Comparator<Account>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int compare(Account o1, Account o2) {
		String o1Key = (o1.isMsslAccount() == true? "0"+o1.getDisplayTitle() : "1"+o1.getDisplayTitle());
		String o2Key = (o2.isMsslAccount() == true? "0"+o2.getDisplayTitle() : "1"+o2.getDisplayTitle());
		// sort by mssl and account name
		return (o1Key).compareTo((o2Key));
	}
}
