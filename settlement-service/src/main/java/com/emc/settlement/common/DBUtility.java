package com.emc.settlement.common;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBUtility {
	
	static public final String EMC_DATASOURCE_JNDI = "emc.nems.jndi";
	
	static public DataSource getDatasource(String jndiName) throws NamingException{
		Context ctx = new InitialContext();
		return (DataSource) ctx.lookup (jndiName);
	}
	
	static public DataSource getDatasource() throws NamingException{
		String jndiName = System.getProperty(EMC_DATASOURCE_JNDI);
		return getDatasource(jndiName);
	}

}
