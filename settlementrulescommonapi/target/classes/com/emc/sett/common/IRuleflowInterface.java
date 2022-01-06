package com.emc.sett.common;

import javax.sql.DataSource;

import org.kie.api.runtime.KieRuntime;

import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;


/*****************************************************************
*
* FOR ENERGY MARKET COMPANY
* BY ORANGE BUSINESS SERVICES 
* COPYRIGHT 2018
* see license.txt
* 
* @author Tse Hing Chuen
* 
* $Id: IRuleflowInterface.java  $
* 
* Class to define the methods used by the Settlement Ruleflow
* 
*
******************************************************************/
public interface IRuleflowInterface {

	/**
	 * @param ds
	 */
	public void setDataSource(DataSource ds);

	/**
	 * @return
	 */
	public String getVersionString();

	/**
	 * This function shall return a 2 dimension array, which describes number of
	 * rulesets, ruleflow groups and kie sessions that packaged inside this JAR file.
	 * 
	 * e.g. return {{"reserve","ksession-reserve"},{"acct_stmt","ksession-acct_stmt"}};
	 * 
	 * @return
	 */
	public String [][] getRuleflowGroups();

	/**
	 * @param logPrefix
	 * @param runPackage
	 * @param params
	 * @param ds
	 * @return
	 * @throws Exception
	 */
	public AbstractSettlementData prepareData(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, DataSource ds) throws Exception;
	
	/**
	 * @param params
	 * @param data
	 * @return
	 */
	public AbstractSettlementData generateDataCSV(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, DataSource ds) throws Exception;
	
	/**
	 * @param params
	 * @param data
	 * @return
	 */
	public AbstractSettlementData readInputCSV(String logPrefix, SettRunPkg runPackage, SettlementRunParams params) throws Exception;
	
	/**
	 * @param kie
	 * @param data
	 * @param ruleflowGroup
	 * @return
	 */
	public void populateFacts(KieRuntime kie, AbstractSettlementData data, String ruleflowGroup) throws Exception;
	
	/**
	 * @param params
	 * @param data
	 * @return
	 */
	public boolean generateInputCSV(SettlementRunParams params, AbstractSettlementData data);
	
	/**
	 * @param params
	 * @param data
	 * @return
	 */
	public boolean generateOutputCSV(SettlementRunParams params, AbstractSettlementData data);
	
	/**
	 * @param logPrefix
	 * @param runPackage
	 * @param params
	 * @param alert
	 * @param data
	 * @param ds
	 * @throws Exception
	 */
	public AlertNotification storeData(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, AlertNotification alert, AbstractSettlementData data, DataSource ds) throws Exception;
	
	/**
	 * @param logPrefix
	 * @param runPackage
	 * @param params
	 * @param data
	 * @param ds
	 * @throws Exception
	 */
	public void writeResultsDB(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, AbstractSettlementData data, DataSource ds) throws Exception;
	
	/**
	 * @param params
	 * @param data
	 * @return
	 */
	public boolean compareOutputCSV(SettlementRunParams params, AbstractSettlementData data);
}
