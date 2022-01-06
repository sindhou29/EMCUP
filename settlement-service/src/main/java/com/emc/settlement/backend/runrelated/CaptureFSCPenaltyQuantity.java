/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class CaptureFSCPenaltyQuantity implements Serializable{

	/**
	 * 
	 */
	public CaptureFSCPenaltyQuantity() {

	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(CaptureFSCPenaltyQuantity.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;

    String logPrefix ="[EMC]";
    String msgStep = "";
    String service_name = "CaptureFSCPenaltyQuantity";
    @Transactional
    public Map<String, Object> isFSCEffective(Map<String, Object> variableMap)
	{

		String eveId = (String)variableMap.get("eveId");
		Boolean isFSCEffective = (Boolean)variableMap.get("isFSCEffective");
		Date settlementDate = (Date)variableMap.get("settlementDate");
		String settlementRunId = (String)variableMap.get("settlementRunId");
		String settlementType = (String)variableMap.get("settlementType");
		SettlementRunException exception = (SettlementRunException)variableMap.get("exception");
		Boolean validSettRun = (Boolean)variableMap.get("validSettRun");
    	
    	logger.info("Input params isFSCEffective() - eveId :"+eveId+" isFSCEffective :"+isFSCEffective+" validSettRun :"+validSettRun+" settlementDate :"+settlementDate+" settlementRunId :"+settlementRunId);
		try{
		//Create JAM Event
	    eveId = utilityFunctions.createJAMEvent("EXE", "FSC PENALTY QUANTITY CAPTURE");
		
		msgStep = service_name+".IsFSCEffective()";

		//get FSC Effective Flag

		
			if(utilityFunctions.isAfterFSCEffectiveStartDate(settlementDate) && 
					utilityFunctions.isBeforeFSCEffectiveEndDate(settlementDate)){
				isFSCEffective = true;
			}else{
				isFSCEffective = false;
			}
			logger.log(Priority.INFO,logPrefix + msgStep + " Is FSC Effective-->"+isFSCEffective+" settRunId --> "+settlementRunId);
			
			String sqlCommand = "SELECT count(*) " + 
		                     " FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " + 
		                     " WHERE SR.EVE_ID = EVE.ID and " + 
		                     " SR.id=? AND EVE.COMPLETED = 'Y' AND EVE.SUCCESS = 'Y' " ;
		                     
			validSettRun = false;                  
			int rec_cnt = 0;

			Object[] params = new Object[1];
			params[0] =  settlementRunId;
			rec_cnt = jdbcTemplate.queryForObject(sqlCommand, params, Integer.class);
			
		    if(rec_cnt  > 0){
		    	validSettRun = true;
		    }
		    
			if(isFSCEffective && validSettRun){
			
				utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Capturing FSC Penalty Qty ", "");
			}
		}catch(SettlementRunException e){
			logger.error("Exception "+e.getMessage());
			logger.log(Priority.INFO,"[EMC] Failed to get FSC EFFECTIVE FLAG.");
			throw new SettlementRunException(e.getMessage(), msgStep);
		}  
		catch(Exception e){
			logger.error("Exception "+e.getMessage());
			logger.log(Priority.INFO,"[EMC] Failed to get FSC EFFECTIVE FLAG.");
			throw new SettlementRunException(e.getMessage(), msgStep);
		}  
		logger.info("Return from service - eveId :"+eveId+" isFSCEffective :"+isFSCEffective+" validSettRun :"+validSettRun);
		variableMap.put("eveId", eveId);
		variableMap.put("isFSCEffective", isFSCEffective);
		variableMap.put("validSettRun", validSettRun);
		variableMap.put("settlementDate",settlementDate);
		variableMap.put("settlementRunId",settlementRunId);
		variableMap.put("settlementType", settlementType);
		variableMap.put("exception", exception);
		
		return variableMap;
	}
    
    @Transactional
    public void captureFSCPenaltyQty(Map<String, Object> variableMap)
	{
		String eveId = (String)variableMap.get("eveId");
		Date settlementDate = (Date)variableMap.get("settlementDate"); 
		String settlementRunId = (String)variableMap.get("settlementRunId");
		String settlementType = (String)variableMap.get("settlementType");
		
		
		/*FSC Implementation - insert data in NEM_SETTLEMENT_FSC_PENALTY_QTY
		If FSC scheme effective then
		capture the settlement account’s first forward sales quantity as PQ on 
		the very first period on the first day of final settlement run for 
		FSC contract for the MP. The data is first captured for Prelim Run 
		then updated if any delta happened in the F run. The data will be captured 
		based on authorized run and will be updated until the Final run is completed. 
		R and S runs result will not be considered.
		*/
    	logger.info("Input params captureFSCPenaltyQty() - eveId :"+eveId+" settlementDate :"+settlementDate+" settlementRunId :"+settlementRunId+" settlementType :"+settlementType);
		try{
		
			msgStep = service_name+".captureFSCPenaltyQty()" ;
		
		    logger.log(Priority.INFO,logPrefix + "Starting Activity " + msgStep + " ...");
		
			//Test Params
			//String settlementType = "F";
			//Time settlementDate = '2014-03-28';
			//String settlementRunId = "FBF14206117700EEE0430A019724B929";
			
			
			String insertPenQtyQry = "INSERT INTO NEM.NEM_SETTLEMENT_FSC_PENALTY_QTY SELECT SYS_GUID (), sac_sold_id, settlement_date, STR_ID, " +
									 " Run_Type, version, Quantity, created_date, eve_id FROM (SELECT DISTINCT " +
									 " fc_out.sac_sold_id sac_sold_id, fc_out.settlement_date settlement_date, ? STR_ID, " +
									 " ? Run_Type, (SELECT NVL (MAX (TO_NUMBER (fc_pen_qty_inner4.version)), 0) + 1 " +
									 " FROM NEM.NEM_SETTLEMENT_FSC_PENALTY_QTY fc_pen_qty_inner4 WHERE fc_pen_qty_inner4.sac_id = fc_out.sac_sold_id) " +
		               			     " version, fcp.QUANTITY Quantity, SYSDATE created_date, ? eve_id FROM NEM.NEM_FSC_CONTRACT_PARAMS fcp, " +
		               			     " NEM.NEM_FSC_CONTRACTS fc_out WHERE fcp.fc_id = fc_out.id AND fcp.FC_ID IN (SELECT fc.ID FROM NEM.NEM_FSC_CONTRACTS fc " +
		               			     " WHERE fc.SETTLEMENT_DATE = ? AND (fc.EXTERNAL_ID, fc.CREATED_DATE) IN (  SELECT fc1.EXTERNAL_ID, " +
		               			     " MAX (fc1.CREATED_DATE) FROM NEM.NEM_FSC_CONTRACTS fc1 WHERE fc1.SAC_SOLD_ID = fc.SAC_SOLD_ID AND fc1.CREATED_DATE <= SYSDATE " +
		               			     " AND fc1.SETTLEMENT_DATE = ? GROUP BY fc1.EXTERNAL_ID) AND fc.SAC_SOLD_ID = fc_out.sac_sold_id AND ( (FC.SETTLEMENT_DATE <= " +
		               			     " (SELECT fc_pen_qty_inner.settlement_date FROM NEM.NEM_SETTLEMENT_FSC_PENALTY_QTY fc_pen_qty_inner WHERE fc_pen_qty_inner.sac_id = " +
		               			     " fc_out.sac_sold_id AND fc_pen_qty_inner.version = (SELECT MAX (TO_NUMBER ( fc_pen_qty_inner1.version)) FROM NEM.NEM_SETTLEMENT_FSC_PENALTY_QTY fc_pen_qty_inner1 " +
		               			     " WHERE fc_pen_qty_inner1.sac_id = fc_out.sac_sold_id AND fc_pen_qty_inner1.settlement_date BETWEEN nem.nem$util.get_sp_dt ('FSC_EFF_START_DATE') " +
		               			     " AND nem.nem$util.get_sp_dt ('FSC_EFF_END_DATE')) AND fc_pen_qty_inner.settlement_date BETWEEN nem.nem$util.get_sp_dt ('FSC_EFF_START_DATE') " +
		               			     " AND nem.nem$util.get_sp_dt ('FSC_EFF_END_DATE'))) OR (NOT EXISTS (SELECT 1  FROM NEM.NEM_SETTLEMENT_FSC_PENALTY_QTY fc_pen_qty_inner3 " +
		               			     " WHERE fc_pen_qty_inner3.sac_id = fc_out.sac_sold_id AND fc_pen_qty_inner3.settlement_date BETWEEN nem.nem$util.get_sp_dt ('FSC_EFF_START_DATE') " +
		               			     " AND nem.nem$util.get_sp_dt ('FSC_EFF_END_DATE') AND ROWNUM = 1)))) AND fcp.settlement_period = 1 AND fc_out.settlement_date BETWEEN nem.nem$util.get_sp_dt ( " +
		               			     " 'FSC_EFF_START_DATE') AND nem.nem$util.get_sp_dt ('FSC_EFF_END_DATE')) ";
		
		
			int num_rows = 0;
			
			//logger.log(Priority.INFO,"Number of rows inserted for Pen Qty --> "+num_rows);   
			Object[] params = new Object[5];
			params[0] =  settlementRunId;
			params[1] =  settlementType;
			params[2] =  eveId;
			params[3] =  utilityFunctions.convertUDateToSDate(settlementDate);
			params[4] =  utilityFunctions.convertUDateToSDate(settlementDate);
			num_rows = jdbcTemplate.update(insertPenQtyQry, params);
			
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, "Inserted " + num_rows + " rows for Capture Penalty Quantity: ", "");
		} 
		catch (SettlementRunException e) {		
			logger.log(Priority.INFO,""+e.getMessage());
		    logger.log(Priority.INFO,"[EMC] Failed to update FSC Penalty Qty ");
		    throw new SettlementRunException(e.getMessage(), msgStep);
		    //exceptionHandler(eveId, e);
		}
		catch (Exception e) {		
			logger.log(Priority.INFO,""+e.getMessage());
		    logger.log(Priority.INFO,"[EMC] Failed to update FSC Penalty Qty ");
		    throw new SettlementRunException(e.getMessage(), msgStep);
		    //exceptionHandler(eveId, e);
		}
	}
    
    @Transactional
    public void updateEvent(Map<String, Object> variableMap)
	{
	    msgStep = service_name+".updateJAMEvent()";

	    String eveId = (String)variableMap.get("eveId");
	    
	    logger.log(Priority.INFO,logPrefix + "Starting Activity: " + msgStep + " ...");

	    // Log JAM Message 
	    utilityFunctions.logJAMMessage(eveId, "I", service_name, "Capture FSC Penalty Quantity Successfully Finished.","");

	    // Update JAM_EVENTS
	    utilityFunctions.updateJAMEvent(true, eveId);
	}
    
    @Transactional
    public void exceptionHandler(Map<String, Object> variableMap)
	{
    	String eveId = (String)variableMap.get("eveId");
    	Object exception = (Object)variableMap.get("exception");
    	// BPM Log
		try {
			logger.error("[EMC] Exception Occurred While Capturing FSC Penalty Quantity!!! " + exception.toString());

			if (eveId != null) {
				// Update JAM_EVENTS table
				utilityFunctions.updateJAMEvent(false, eveId);

				// Log JAM Message
				utilityFunctions.logJAMMessage(eveId, "W", service_name,
						"Abnormal termination of Capture FSC Penalty Quantity Process.", "");
			}
		}catch (Exception e)
    	{
    		logger.error("[EMC] Exception " + e.getMessage());
    	}
	}

}
