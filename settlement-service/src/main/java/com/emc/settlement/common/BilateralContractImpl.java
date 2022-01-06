package com.emc.settlement.common;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BilateralContractImpl {	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private UtilityFunctions utilityFunctions;
	
	protected static final Logger logger = Logger.getLogger(BilateralContractImpl.class);
	
	public void autoAuthorizeBilateralFileSEW(String blsId, String eventID, String authorizerUserName) throws Exception
	{
		// ------------------------------------------------------
		// @param blsID: String
		// ------------------------------------------------------
		try{
		    String sysID;
		    String sqlCommand;
		    sysID = utilityFunctions.getEveId();

		    // logMessage "[EMC] EMC.BilateralContracts.autoAuthorizeBilateralFileSEW()" + " - BLS ID - " + blsId   //ITSM-17449-Suggested enhancement for BIL Phase 2  - commented
		    logger.log(Priority.INFO,"[EMC] EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() [Post ITSM 17449 - Used for All Upload Type]" + " - BLT ID - " + blsId);

		    // ITSM-17449-Suggested enhancement for BIL Phase 2
		    utilityFunctions.logJAMMessage( eventID,  "I",  "EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() [Post ITSM 17449 - Used for All Upload Type]", 
		                                    "Start Auto Authorizing Bilateral Contract Data [Post ITSM 17449 - Used for All Upload Type]" + " - BLT ID - " + blsId, 
		                                    "");

		    sqlCommand = "INSERT INTO nem.nem_bilateral_contracts " + 
		                 "SELECT ?, NVL(TO_NUMBER(version),0) + 1, NAME, EXTERNAL_ID, CONTRACT_TYPE, START_DATE,END_DATE, NDE_ID, ACG_ID, SAC_ID_PURCHASED_BY,SAC_ID_SOLD_BY, " + 
		                 "CREATED_DATE ,EXPIRED_DATE, LOCK_VERSION, 'A',SYSDATE, " + 
		                 "?, " + 
		                 "FRONTEND_USER_ID, ? FROM nem.nem_bilateral_contracts " + 
		                 "WHERE ID =? and LOCK_VERSION = 1 and APPROVAL_STATUS = 'W' and EXPIRED_DATE = to_date('01-Jan-3000','DD-MON-YYYY')";
		    
			Object[] params = new Object[4];
			params[0] =  sysID;
			params[1] =  utilityFunctions.getUserId( authorizerUserName);
			params[2] =  eventID;
			params[3] =  blsId;
			jdbcTemplate.update(sqlCommand, params);
		    
		    sqlCommand = "UPDATE nem.nem_bilateral_contracts " + 
		                 "SET EXPIRED_DATE = CREATED_DATE  , LOCK_VERSION = 99" + 
		                 "WHERE ID =? and LOCK_VERSION = 1 and APPROVAL_STATUS = 'W' and EXPIRED_DATE = to_date('01-Jan-3000','DD-MON-YYYY')";

		    Object[] params1 = new Object[1];
			params1[0] =  blsId;
			jdbcTemplate.update(sqlCommand, params1);
		    
		    sqlCommand = "INSERT INTO nem.nem_bilateral_parameters" + 
		                 "(id, version, period, value, Blt_id, Blt_version, Frontend_user_id ) " + 
		                 "SELECT SYS_GUID(), NVL(TO_NUMBER(blcsparam.version),0) + 1, blcsparam.period,blcsparam.value, " + 
		                 "blcsauthupdate.ID,blcsauthupdate.version,blcsparam.Frontend_user_id " + 
		                 "from nem.nem_bilateral_parameters blcsparam, nem.nem_bilateral_contracts blcs," + 
		                 "(SELECT blcsauth.ID, blcsauth.version from nem.nem_bilateral_contracts blcsauth where blcsauth.ID = ?" + 
		                 "and blcsauth.LOCK_VERSION = 1 and blcsauth.APPROVAL_STATUS = 'A' " + 
		                 "and blcsauth.EXPIRED_DATE = to_date('01-Jan-3000','DD-MON-YYYY')) blcsauthupdate " + 
		                 "where blcsparam.blt_id = ? and blcsparam.blt_id = blcs.id and blcsparam.blt_version = blcs.version and APPROVAL_STATUS = 'W'";

		    Object[] params2 = new Object[2];
			params2[0] =  sysID;
			params2[1] =  blsId;
			jdbcTemplate.update(sqlCommand, params2);

			logger.info("Inserting Bilateral Parameters: SysID: "+sysID+" blsId: "+blsId);
		    utilityFunctions.logJAMMessage( eventID,  "I",  "EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() [Post ITSM 17449 - Used for All Upload Type]", 
		                                    "SUCCESSFULLY Completed Auto Authorizing Bilateral Contract Data [Post ITSM 17449 - Used for All Upload Type]" + " - BLT ID - " + blsId, 
		                                    "");
		}
		catch (Exception e) {
		    // ITSM-17449-Suggested enhancement for BIL Phase 2  - commented
		    // logMessage "[[EMC] EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() - ERROR in Auto Authorizing Bilateral Contract Data" + 
		    // " Received from SEW" + " - BLS ID - " + blsId + " ::: EVENT ID - " + eventID + " ::: Time :" + 'now'.toTime
		    //    using severity = WARNING
		    // ITSM-17449-Suggested enhancement for BIL Phase 2
		    logger.log(Priority.WARN,"[[EMC] EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() [Post ITSM 17449 - Used for All Upload Type] - ERROR in Auto Authorizing Bilateral Contract Data" + 
		    " - BLT ID - " + blsId + " ::: EVENT ID - " + eventID + " ::: Time :" + new Date());

		    utilityFunctions.logJAMMessage( eventID,  "E",  "EMC.BilateralContracts.autoAuthorizeBilateralFileSEW() [Post ITSM 17449 - Used for All Upload Type]", 
		                                    "ERROR in Auto Authorizing Bilateral Contract Data" + " - BLT ID - " + blsId, 
		                                    "");

				throw new Exception("ERROR in Auto Authorizing Bilateral Contract Data " + " - BLT ID - " + blsId);

		}
	}
}
