/*
 *
 * Copyright (c) 2003-2007 Energy Market Company Pte Ltd.
 * 9 Raffles Place, #22-01 Republic Plaza, Singapore 048619
 * All rights reserved.
 *
 * File Name : EncryptionProperties.java
 * Version : 0.1
 *
 * Revision Log :
 * Version     Modified By      Modified Date    Description
 *    0.1        Nikhil         Aug 26, 2008      IntialDraft
 */
package com.emc.settlement.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class EncryptionProperties {

	private String propertyFileName=null;
	private Properties props=null;
	public final String CONFIG_FILE="AppsPasswordConfig.ini";
    public static final String EMC_WEBAPP_DEPLOY_PATH_NAME =  "/app/properties/webapp-deploy/";

	/**
	* This is the constructor method loads the file
	* @param -
	* @return none
	*/
	public EncryptionProperties() throws IOException{
		try{
			props = new Properties();
			InputStream newIniStream = new FileInputStream(EMC_WEBAPP_DEPLOY_PATH_NAME + CONFIG_FILE);
			props.load(newIniStream);
		}catch(Exception e){
			throw new IOException("EncryptionProperties(): Error in loading properties file. Details: " + e.getMessage());
		}
	}
	/**
	* This method is used by the EncryptionApp.
	* @param - inFileName
	* @return none
	*/
	public EncryptionProperties(String inFileName) throws Exception{
	    try{
			props = new Properties();
			this.propertyFileName=inFileName;
			InputStream newIniStream = new FileInputStream(inFileName);
			props.load(newIniStream);
	    }catch(Exception e){
			throw new Exception("EncryptionProperties(String inFileName): Error in loading properties file. Details: " + e.getMessage());
		}
	}
	/**
	* This method sets a property key value pair into the properties array.
	* @param - propName name of the Property whose value to be set.
	* @param - propValue value of the Property whose value is to be set.
	* @return none
	*/
	public void setPropertyValue(String propName,String propValue){
		if(props != null){
			props.setProperty(propName,propValue);
		}
	}

	/**
	* This method returns the value for the propertyName supplied.
	* @param - propName name of the Property whose value to be returned.
	* @return String
	*/
	public String getPropertyValue(String propName){
		if(props != null){
			return (String)props.get( propName );
		}
		return null;
	}
	/**
    * This method returns all the property Names in the resource file.
    * @param none
    * @return - Enumeration of the property names.
    */
    public Enumeration getPropNames(){
		if (props != null)
		   return props.propertyNames();

		return null;
	}
}
