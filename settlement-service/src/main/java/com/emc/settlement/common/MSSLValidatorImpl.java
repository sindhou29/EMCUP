package com.emc.settlement.common;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.exceptions.ValidateDataException;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MSSLValidatorImpl {
	
	protected static final Logger logger = Logger.getLogger(MSSLValidatorImpl.class);
	
@Autowired
UtilityFunctions utilityFunctions;
@Autowired
private JdbcTemplate jdbcTemplate;

	public boolean validateMsslQuantity(String qtyType, String  standingVersion, Date settlementDate, String msslQuantityVersion, String logPrefix, PeriodNumber pd )
	{
	    String msgStep = "MSSLValidatorImpl.validateMsslQuantity()";
		try{

	    logger.log(Priority.INFO,logPrefix + "Starting Function " + msgStep + " ...");

	    String sqlCommand;
	    String sqlCommand1;
	    int rowcnt = 0;
	    String msg;
	    String nodeOrSAC;
	    String dontDoAnything;  //ByPassforSPServices Added
	    
	    boolean DREffDate = utilityFunctions.isAfterDRCAPEffectiveDate(settlementDate);  //ByPassforSPServices Added
	    
	     if  (qtyType.equals("WLQ") && !(DREffDate)) {
	       dontDoAnything = "Dont Do Anything";
	     }
	     else if (qtyType.equals("WDQ") && !(DREffDate)) {
	       dontDoAnything = "Dont Do Anything";	     
	     }
	     else {  //ByPassforSPServices Added

			    switch (qtyType) {
			    case "WLQ":   // DRCAP Phase 2 WLQ will have Node Type Load
			        // DRCAP Phase 2 WLQ will have Node Type Load
			        sqlCommand = "SELECT distinct nde.id, nde.version, nde.name " + 
			                     "FROM NEM.nem_nodes nde, NEM.nem_settlement_quantities sqty " + 
			                     "WHERE nde.node_type = 'L' AND settlement_date = ? AND sqty.version = ? " + 
			                     "AND sqty.nde_id = nde.id AND sqty.nde_version = nde.version AND sqty.quantity_type = ?";
			        nodeOrSAC = "Node"; 
			        break;   
			    case "IEQ":
			        sqlCommand = "SELECT distinct nde.id, nde.version, nde.name " + 
			                     "FROM NEM.nem_nodes nde, NEM.nem_settlement_quantities sqty " + 
			                     "WHERE settlement_date = ? AND sqty.version = ? " + 
			                     "AND sqty.nde_id = nde.id AND sqty.nde_version = nde.version AND sqty.quantity_type = ?";
			        nodeOrSAC = "Node";
			        break;
			    case "IIQ":
			        sqlCommand = "SELECT distinct nde.id, nde.version, nde.name " + 
			                     "FROM NEM.nem_nodes nde, NEM.nem_settlement_quantities sqty " + 
			                     "WHERE nde.node_type = 'I' AND settlement_date = ? AND sqty.version = ? " + 
			                     "AND sqty.nde_id = nde.id AND sqty.nde_version = nde.version AND sqty.quantity_type = ?";
			        nodeOrSAC = "Node";
			        break;
			    default:
			        sqlCommand = "SELECT distinct sac.id, sac.version, sac.display_title " + 
			                     "FROM NEM_SETTLEMENT_ACCOUNTS sac, NEM_SETTLEMENT_QUANTITIES sqty " + 
			                     "WHERE sqty.settlement_date = ? AND sqty.VERSION = ? " + 
			                     "AND sqty.sac_id = sac.ID AND sqty.sac_version = sac.VERSION AND sqty.quantity_type = ?";
			        nodeOrSAC = "SAC";
			        break;
			    }
			
			    //if (qtyType.equals("IEQ" || qtyType.equals("IIQ") {  // DRCAP Phase 2 WLQ will have Node Type Load
			    if (qtyType.equals("IEQ") || qtyType.equals("IIQ") || qtyType.equals("WLQ")) {  // DRCAP Phase 2 WLQ will have Node Type Load
			        sqlCommand1 = "SELECT count(*) count FROM " + 
			                      "(SELECT count(period) as p0, sum(period) as p1, sum(period*period) as p2, " + 
			                      "avg(period*period*period) as p3 FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
			                      "WHERE settlement_date = ? " + 
			                      "AND version = ? AND quantity_type = ? " + 
			                      "AND nde_id = ? AND nde_version = ? AND period between 1 and " + pd.total + 
			                      ") WHERE p0=" + pd.total + " and p1=" + pd.sum + " and p2=" + pd.sum2 + " and p3=" + pd.avg3;
			    }
			    else {
			        sqlCommand1 = "SELECT count(*) count FROM " + 
			                      "(SELECT count(period) as p0, sum(period) as p1, sum(period*period) as p2, " + 
			                      "avg(period*period*period) as p3 FROM NEM.NEM_SETTLEMENT_QUANTITIES " + 
			                      "WHERE settlement_date = ? " + 
			                      "AND version = ? AND quantity_type = ? " + 
			                      "AND sac_id = ? AND sac_version = ? AND period between 1 and " + pd.total + 
			                      ") WHERE p0=" + pd.total + " and p1=" + pd.sum + " and p2=" + pd.sum2 + " and p3=" + pd.avg3;
			    }
			
			    // Check Quantity value for each period	
				String strSacId = null;
				try {
					Object[] params = new Object[3];
					params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					params[1] =  msslQuantityVersion;
					params[2] =  qtyType;
					List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
					for (Map row : list) {
						String name = (String)row.get("name");
						logger.log(Priority.INFO,logPrefix + "Validating " + nodeOrSAC + ": " + name);						
						Object[] params1 = new Object[5];
						params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
						params[1] =  msslQuantityVersion;
						params[2] =  qtyType;
						params[3] =  (String)row.get("ID");
						params[4] =  (String)row.get("version");
						List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand1, params1);
						for (Map row1 : list1) {
							rowcnt = (Integer)row1.get("count");							
						}						
				        if (rowcnt < 1) {
				            msg = qtyType + ": Data not valid for " + nodeOrSAC + ": " + name;
				
				            logger.log(Priority.INFO,logPrefix + msg);
				
				            throw new ValidateDataException( msg, msgStep);
				            
				        }
					}
				} catch (Exception e) {
					logger.error("Exception "+e.getMessage());
				}			    
			    logger.log(Priority.INFO,logPrefix + qtyType + " data are valid.");
	    } //ByPassforSPServices Added
		}catch(Exception e)
		{
			logger.error("Exception "+e.getMessage());
		}
	    return true;	
	}
}
