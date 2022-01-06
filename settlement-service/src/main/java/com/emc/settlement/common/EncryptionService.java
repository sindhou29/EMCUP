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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;


public class EncryptionService {

	public static final String EMCKey="EMCKey";

	/**
	* This method is used by the EncryptionApp to encrypt the string.
	* @parm - strToEncrypt string to encrypt
	* @parm - encryptionKey encryption key
	* @return string
	*/
	public static String encryptString(String strToEncrypt,String encryptionKey) throws Exception
	{

		String encryptedString = "";
		EncryptionEngine engine = new EncryptionEngine(EncryptionEngine.DES_ENCRYPTION_SCHEME, encryptionKey );
		try {
			synchronized (EncryptionService.class) {
				encryptedString = engine.encrypt( strToEncrypt );
			}

		}
		catch(Exception ex){
			throw new Exception ("EncryptionService: Exception getting the encryption method="+ex.getMessage());
		}
		finally
		{
			engine = null;
		}
	 	return encryptedString;
	}

	/**
	* This method is used by the EncryptionService to decrypt the string.
	* @parm - encryptedStr encrypted string
	* @parm - encryptionKey encryption key
	* @return string
	*/
	public static String decryptString(String encryptedStr,String encryptionKey) throws Exception{

		String decryptedString = "";
		EncryptionEngine engine = new EncryptionEngine(EncryptionEngine.DES_ENCRYPTION_SCHEME, encryptionKey );
		try{
			synchronized (EncryptionService.class) {
			decryptedString = engine.decrypt( encryptedStr );
			}
		}
		catch(Exception ex){
			throw new Exception ("EncryptionService: Exception getting the decryption method="+ex.getMessage());
		}
		finally
		{
			engine = null;
		}
	 	return decryptedString;
	}
	/**
	* This method returns the value for the EncryptionKey from the properties supplied.
	* @parm - propsApps name of the Property from where the value will be returned.
	* @return String
	*/
	public static String getEncryptionKeyValue(EncryptionProperties propsApps) throws IOException {

		String  EMCDecryptKeyValue="";
		try{
			EMCDecryptKeyValue=(String) propsApps.getPropertyValue(EMCKey);
		}
		catch(Exception ex){
			throw new IOException("EncryptionService: EMC Key is not specifed in the file AppsPasswordConfig file. "+ex.getMessage());
		}
		return EMCDecryptKeyValue;
	}
}