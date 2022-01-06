/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.PINK;
import org.apache.poi.hssf.util.HSSFColor.SKY_BLUE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.CashFlow;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class CashFlowReconciliation implements Serializable{

	/**
	 * 
	 */
	public CashFlowReconciliation() {

	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(CashFlowReconciliation.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    PavPackageImpl pavPackageImpl;

    String logPrefix ="[EMC]";
    String msgStep = "";
    String service_name = "CashFlowReconciliation";
    
    @Transactional
    public Map<String, Object> checkRunStatus(Map<String, Object> variableMap)
	{

		Date settlementDate = (Date)variableMap.get("settlementDate"); 
		String settlementRunId = (String)variableMap.get("settlementRunId");
		Boolean runSuccess = (Boolean)variableMap.get("runSuccess");
		Boolean ntEffective = (Boolean)variableMap.get("ntEffective");
		
		try{
		    msgStep =  service_name+".checkRunStatus()" ;

		    logger.log(Priority.INFO,"[EMC] Starting Activity: " + msgStep + " ...");

		    runSuccess = false;

		    if (settlementRunId != null && settlementRunId.length() > 1) {
		        String sqlCmd = "SELECT count(*) count from NEM.nem_settlement_runs nsr, " + 
		        "NEM.jam_events e WHERE nsr.eve_id = e.id and nsr.id = ? " + 
		        "and completed = 'Y' and success = 'Y'";
		        int count = 0;

				Object[] params = new Object[1];
				params[0] =  settlementRunId;
				count = jdbcTemplate.queryForObject(sqlCmd, params, Integer.class);
		        if (count > 0) {
		            runSuccess = true;
		        }
		    }

		    ntEffective = utilityFunctions.isAfterNTEffectiveDate(settlementDate);
		}catch (SettlementRunException e) {
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		logger.info("Returning from Service -  runSuccess :"+runSuccess+"  ntEffective : "+ntEffective);
		variableMap.put("runSuccess", runSuccess);
		variableMap.put("ntEffective", ntEffective);
		return variableMap;
	}
	
    @Transactional
	public Map<String, Object> generateExcelsheet(Map<String, Object> variableMap)
	{

		String eveId = (String)variableMap.get("eveId"); 
		Date settlementDate = (Date)variableMap.get("settlementDate"); 
		String settlementRunId = (String)variableMap.get("settlementRunId");
		String standingVersion = (String)variableMap.get("standingVersion");
		Date drEffectiveDate = (Date)variableMap.get("drEffectiveDate"); 
		
		List<CashFlow> negCashFlowArray = (ArrayList<CashFlow>)variableMap.get("negCashFlowArray");
		List<CashFlow> posCashFlowArray = (ArrayList<CashFlow>)variableMap.get("posCashFlowArray");
		
		if(negCashFlowArray == null) negCashFlowArray = new ArrayList<CashFlow>();
		if(posCashFlowArray == null) posCashFlowArray = new ArrayList<CashFlow>();
		
		
		try{
		    msgStep = service_name+".generateExcelsheet()";

		    logger.log(Priority.INFO,"[EMC] Starting Activity: " + msgStep + " ...eveId : "+eveId+"  settlementRunId : "+settlementRunId+"  settlementDate : "+settlementDate);

		    //DRCAP CHANGES START
		    boolean isdrEffective = false;
		    //DRCAP CHANGES END 
		    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		    // str_id as String = "68B89C7FDCB73C1AE0440003BAD9F123"
		    String REPORT_SECTION = "MAIN%";

		    // standing_version as String = "325"
		    String cashfn = UtilityFunctions.getProperty("CASHFLOW_BASE_DIR") + "cfr_" + df.format(settlementDate) + ".xls";
		    //String cashfn = "D:\\temp\\app\\log\\application\\" + "cfr_" + df.format(settlementDate) + ".xls";

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
		                                   "Generating Cashflow Excelsheet: " + cashfn, 
		                                   "");
		    
		    // Get Standing Version
		    standingVersion = pavPackageImpl.getStandingVersion(settlementDate);
		    FileOutputStream fos = new FileOutputStream(cashfn);
		    HSSFWorkbook wb = new HSSFWorkbook();
		    HSSFSheet sheet;
		    HSSFRow row;
		    HSSFCell cell;
		    sheet = wb.createSheet();
		    int rowIndex = 0;
		    int columnIndex = 0;
		    row = sheet.createRow(rowIndex);
		    cell = row.createCell(columnIndex);
		    cell.setCellValue("Sett Date:");

		    HSSFCellStyle cellStyle;
		    HSSFFont font;

		    SimpleDateFormat df1 =  new SimpleDateFormat("dd-MMM-yyyy");
		    // 	dateToday as Java.Util.Date = Java.Util.Date
		    // 	date as String = dateFormat.format(dateToday)
		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);
		    cell.setCellValue(df1.format(settlementDate));

		    rowIndex = rowIndex + 1;
		    columnIndex = 1;
		    row = sheet.createRow(rowIndex);
		    
		    
		    
		    //-----------------------------------------------DRCAP CHANGES START-------------------------------------------------//
		    
		      // Get DR Effective Start Date
		        drEffectiveDate = utilityFunctions.getSysParamTime("DR_EFFECTIVE_DATE");
		    
		     // DR Phase 2 FR 2.4.2.10.1
		        if (settlementDate.compareTo(drEffectiveDate) < 0) {
		            isdrEffective = false;
		        }
		        else {
		            isdrEffective = true;
		        }
		    
		    
		     if(isdrEffective)
			{
		   	//String[] drTitle = { "NESC", "NRSC", "NFSC", "NTSC", "VCSC", "HEUR","HLCU", "MEUC", "NMEA", "NEAA", "NEAD", "EMCAD", "PSOAD", "Total", "Grand Total" };
		   	  String[] drTitle = { "NESC", "NRSC", "NFSC", "NTSC", "VCSC", "LCSC","HEUC", "MEUC", "NMEA", "NEAA", "NEAD", "EMCAD", "PSOAD", "Total", "Grand Total" };
			   	for (int i = 0; i <= drTitle.length - 1; i++) {
			        cellStyle = wb.createCellStyle();
			        font = wb.createFont();
			        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			        cellStyle.setFont(font);
			        cell = row.createCell(columnIndex);
			        cell.setCellStyle(cellStyle);
			        cell.setCellValue(drTitle[i]);
			
			        columnIndex = columnIndex + 1;
			    }
		   	}else
		   	{
		    String[] title = { "NESC", "NRSC", "NFSC", "NTSC", "VCSC", "HEUC", "MEUC", "NMEA", "NEAA", "NEAD", "EMCAD", "PSOAD", "Total", "Grand Total" };
		    
		     for (int i = 0; i <= title.length - 1; i++) {
		        cellStyle = wb.createCellStyle();
		        font = wb.createFont();
		        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		        cellStyle.setFont(font);
		        cell = row.createCell(columnIndex);
		        cell.setCellStyle(cellStyle);
		        cell.setCellValue(title[i]);

		        columnIndex = columnIndex + 1;
		    }
		    }
		    
		    //-----------------------------------------DRCAP CHANGES END--------------------------------------------------------//
		    
		    
		    rowIndex = rowIndex + 1;
		    negCashFlowArray = new ArrayList<CashFlow>();

		    posCashFlowArray = new ArrayList<CashFlow>();

		    String sqlCommand;
		    int indexPos = 0;
		    int indexNeg = 0;
		    sqlCommand = "select ID, RETAILER_ID, EXTERNAL_ID from NEM.NEM_SETTLEMENT_ACCOUNTS " + 
		                 "where VERSION = ? ";
		    String sqlAcctStmt = "SELECT COLUMN_1, COLUMN_5 FROM NEM.NEM_ACCOUNT_STATEMENTS " + 
		    "WHERE STR_ID =? AND REPORT_SECTION LIKE ? AND SAC_ID =? ";
		    try {
				//Object[] params = new Object[1];
				//params[0] =  standingVersion;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, standingVersion);
				for (Map row1 : list) {

			        String account_name = (String)row1.get("EXTERNAL_ID");
			        String account_id = (String)row1.get("ID");
			        CashFlow x = new CashFlow();
			        logger.log(Priority.INFO, "account_name : "+account_name+"  account_id : "+account_id+"  REPORT_SECTION :"+REPORT_SECTION);
					Object[] params1 = new Object[3];
					params1[0] =  settlementRunId;
					params1[1] =  REPORT_SECTION;
					params1[2] =  account_id;
					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlAcctStmt, params1);
					for (Map row2 : list1) {
			            String column1 = (String)row2.get("COLUMN_1");
			            String column5 = (String)row2.get("COLUMN_5");
			            logger.log(Priority.INFO, "column1 : "+column1+"  column5 : "+column5);
			            if (column1 != null && column1.indexOf("NESC") != - 1) {
			                try {
			                    x.nesc = Double.parseDouble(column5);

			                    // display "x.nesc: " + x.nesc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NESC");
			                }
			            }

			            if (column1 != null && column1.indexOf("NRSC") != - 1) {
			                try {
			                    x.nrsc = Double.parseDouble(column5);

			                    // display "x.nrsc: "+ x.nrsc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NRSC");
			                }
			            }

			            if (column1 != null && column1.indexOf("NFSC") != - 1) {
			                try {
			                    x.nfsc = Double.parseDouble(column5);

			                    // display "x.nfsc: "+ x.nfsc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NFSC");
			                }
			            }

			            if (column1 != null && column1.indexOf("NTSC") != - 1) {
			                try {
			                    x.ntsc = Double.parseDouble(column5);

			                    // display "x.ntsc: " + x.ntsc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NTSC");
			                }
			            }

			            if (column1 != null && column1.indexOf("VCSC") != - 1) {
			                try {
			                    x.vcsc = Double.parseDouble(column5);

			                    // display "x.vcsc: "+ x.vcsc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing VCSC");
			                }
			            }

			           //----------------------------DRCAP CHANGES START------------------------------------------//
						
						 if(isdrEffective)
						{
						
				            //if (column1.indexOf("HEUR") != - 1) {
				            //    try {
				            //        x.heur = ((Decimal) column5);
				
				            //    }
				            //    catch (Exception e) {
				            //        logger.log(Priority.INFO,"Error when parsing HEUR", WARNING);
				            //    }
				            //}
				           //if (column1.indexOf("HLCU") != - 1) {
				           //     try {
				           //         x.hlcu = ((Decimal) column5);
				           //     }
				           //     catch (Exception e) {
				           //         logger.log(Priority.INFO,"Error when parsing HLCU", WARNING);
				           //     }
				           // }
							 if (column1 != null && column1.indexOf("LCSC") != - 1) {
				                try {
				                    x.lcsc = Double.parseDouble(column5);
				                }
				                catch (Exception e) {
				                    logger.log(Priority.WARN,"Error when parsing LCSC");
				                }
				            }
						}
						//else
						//{
							 if (column1 != null && column1.indexOf("HEUC") != - 1) {
				                try {
				                    x.heuc = Double.parseDouble(column5);
				
				                }
				                catch (Exception e) {
				                    logger.log(Priority.WARN,"Error when parsing HEUC");
				                }
				            }
						//}
					 //--------------------------------------DRCAP CHANGES END ------------------------------------- //

			            if (column1 != null && column1.indexOf("MEUC") != - 1) {
			                try {
			                    x.meuc = Double.parseDouble(column5);

			                    // display "x.meuc: "+ x.meuc
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing MEUC");
			                }
			            }

			            if (column1 != null && column1.indexOf("NMEA") != - 1) {
			                try {
			                    x.nmea = Double.parseDouble(column5);

			                    // display "x.nmea: "+ x.nmea
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NMEA");
			                }
			            }

			            if (column1 != null && column1.indexOf("NEAA") != - 1) {
			                try {
			                    x.neaa = Double.parseDouble(column5);

			                    // display "x.neaa: "+ x.neaa
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NEAA");
			                }
			            }

			            if (column1 != null && column1.indexOf("NEAD") != - 1) {
			                try {
			                    x.nead = Double.parseDouble(column5);

			                    // display "x.neaa: " + x.neaa
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing NEAD");
			                }
			            }

			            if (column1 != null && column1.indexOf("EMCADMIN") != - 1) {
			                try {
			                    x.emcad = Double.parseDouble(column5);

			                    // display "x.emcad"+ x.emcad
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing EMCAD");
			                }
			            }

			            if (column1 != null && column1.indexOf("PSOADMIN") != - 1) {
			                try {
			                    x.psoad = Double.parseDouble(column5);
			                    // display "x.psoad: "+ x.psoad
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing PSOAD");
			                }
			            }

			            if (column1 != null && column1.indexOf("Total") != - 1) {
			                try {
			                    x.total = Double.parseDouble(column5);

			                    // display "x.total: "+ x.total
			                }
			                catch (Exception e) {
			                    logger.log(Priority.WARN,"Error when parsing TOTAL");
			                }
			            }
			        }


			        x.acc_name = account_name;
			        x = this.checkEmptyValue(x);

			        if (x.total != null && x.total < 0) {
			            negCashFlowArray.add(indexNeg, x);

			            indexNeg = indexNeg + 1;
			        }
			        else {
			            posCashFlowArray.add(indexPos, x);

			            indexPos = indexPos + 1;
			        }
			    }
				
		}catch(Exception e)
		{
			logger.error("Exception "+e.getMessage());
			e.printStackTrace();
		}


		    Double nescTotal =  new Double(0.00);
		    Double nrscTotal =  new Double(0.00);
		    Double nfscTotal =  new Double(0.00);
		    Double ntscTotal =  new Double(0.00);
		    Double vcscTotal =  new Double(0.00);
		    Double heucTotal =  new Double(0.00);
		     //-----------------DRCAP CHANGES START------------------------//
		    //Decimal heurTotal = 0.00;
		    //Decimal hlcuTotal = 0.00;
		    Double lcscTotal =  new Double(0.00);
		    //--------------DRCAP CHANGES END---------------------------//
		    Double meucTotal =  new Double(0.00);
		    Double nmeaTotal =  new Double(0.00);
		    Double neaaTotal =  new Double(0.00);
		    Double neadTotal =  new Double(0.00);
		    Double emcadTotal =  new Double(0.00);
		    Double psoadTotal =  new Double(0.00);
		    cellStyle = wb.createCellStyle();
		    font = wb.createFont();
		    font.setColor(HSSFFont.COLOR_RED);
		    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("(#,##0.00_)[Red](#,##0.00)"));

		    cellStyle.setFont(font);
		    Double negTotal = new Double(0.00);

		    for (int j = 0; j <= negCashFlowArray.size() - 1; j++) {
		        columnIndex = 0;
		        row = sheet.createRow( rowIndex);
		        cell = row.createCell( columnIndex);
		        cell.setCellValue( negCashFlowArray.get(j).acc_name);

		        rowIndex = rowIndex + 1;
		        columnIndex = columnIndex + 1;
		        cell = row.createCell( columnIndex);

		        if (negCashFlowArray.get(j).nesc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).nesc));

		        columnIndex = columnIndex + 1;
		        nescTotal = roundAvoid(nescTotal) + roundAvoid(negCashFlowArray.get(j).nesc);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).nrsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).nrsc));

		        columnIndex = columnIndex + 1;
		        nrscTotal =  roundAvoid(nrscTotal) + roundAvoid(negCashFlowArray.get(j).nrsc);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).nfsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).nfsc));

		        columnIndex = columnIndex + 1;
		        nfscTotal =  roundAvoid(nfscTotal) + roundAvoid(negCashFlowArray.get(j).nfsc);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).ntsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).ntsc));

		        columnIndex = columnIndex + 1;
		        ntscTotal =  roundAvoid(ntscTotal) + roundAvoid(negCashFlowArray.get(j).ntsc);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).vcsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).vcsc));

		        columnIndex = columnIndex + 1;
		        vcscTotal =  roundAvoid(vcscTotal) + roundAvoid(negCashFlowArray.get(j).vcsc);
		        cell = row.createCell(columnIndex);
		        // -----------------------------DRCAP CHANGES START ----------------------------------//
				 if(isdrEffective){
				 
				 	//if(negCashFlowArray[j].heur < 0) {
		            //	cell.cellStyle = cellStyle;
		        	//}

		        	//cell.setCellValue(value : negCashFlowArray[j].heur);

					//columnIndex = columnIndex + 1;
			        //heurTotal = heurTotal + negCashFlowArray[j].heur;
			        //cell = row.createCell(columnIndex : columnIndex);
			
			
			        //if (negCashFlowArray[j].hlcu < 0) {
			        //    cell.cellStyle = cellStyle;
			        //}
			
			        //cell.setCellValue(value : negCashFlowArray[j].hlcu);
			        
			        //columnIndex = columnIndex + 1;
			        //hlcuTotal = hlcuTotal + negCashFlowArray[j].hlcu;
			        //cell = row.createCell(columnIndex : columnIndex);

				 	if(negCashFlowArray.get(j).lcsc == null || negCashFlowArray.get(j).lcsc < 0) {
		            	cell.setCellStyle(cellStyle);
		        	}

				 	if(negCashFlowArray.get(j).lcsc == null) {
				 		cell.setCellValue(0.0);
				 		lcscTotal =  lcscTotal + 0.0;
				 	}else {
				 		cell.setCellValue(roundAvoid(negCashFlowArray.get(j).lcsc));
				 		lcscTotal =  roundAvoid(lcscTotal) + roundAvoid(negCashFlowArray.get(j).lcsc);
				 	}
		        	//cell.setCellValue(negCashFlowArray.get(j).lcsc == null ? 0.0 : negCashFlowArray.get(j).lcsc);

					columnIndex = columnIndex + 1;
			        //lcscTotal = lcscTotal + (negCashFlowArray.get(j).lcsc == null ? 0.0 : negCashFlowArray.get(j).lcsc);
			        cell = row.createCell(columnIndex);
			        
		        }
		        //else
		        //{
			       
			        if (negCashFlowArray.get(j).heuc == null || negCashFlowArray.get(j).heuc < 0) {
			            cell.setCellStyle(cellStyle);
			        }
			
				 	if(negCashFlowArray.get(j).heuc == null) {
				 		cell.setCellValue(0.0);
				 		heucTotal =  heucTotal + 0.0;
				 	}else {
				 		cell.setCellValue(roundAvoid(negCashFlowArray.get(j).heuc));
				 		heucTotal =  roundAvoid(heucTotal) + roundAvoid(negCashFlowArray.get(j).heuc);
				 	}
				 	
			        //cell.setCellValue(negCashFlowArray.get(j).heuc == null ? 0.0 : negCashFlowArray.get(j).heuc);
			        
			        columnIndex = columnIndex + 1;
			        //heucTotal = heucTotal + (negCashFlowArray.get(j).heuc == null ? 0.0 : negCashFlowArray.get(j).heuc);
			        cell = row.createCell(columnIndex);	        	
		        //}
				
				//--------------------------- DRCAP CHANGES END---------------------------//

		        if (negCashFlowArray.get(j).meuc == null || negCashFlowArray.get(j).meuc < 0) {
		            cell.setCellStyle(cellStyle);
		        }
		        
			 	if(negCashFlowArray.get(j).meuc == null) {
			 		cell.setCellValue(0.0);
			 		meucTotal =  meucTotal + 0.0;
			 	}else {
			 		cell.setCellValue(roundAvoid(negCashFlowArray.get(j).meuc));
			 		meucTotal =  roundAvoid(meucTotal) + roundAvoid(negCashFlowArray.get(j).meuc);
			 	}
			 	
		        //cell.setCellValue(negCashFlowArray.get(j).meuc == null ? 0.0 : negCashFlowArray.get(j).meuc);

		        columnIndex = columnIndex + 1;
		        //meucTotal = meucTotal + (negCashFlowArray.get(j).meuc == null ? 0.0 : negCashFlowArray.get(j).meuc);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).nmea < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).nmea));

		        columnIndex = columnIndex + 1;
		        nmeaTotal =  roundAvoid(nmeaTotal) + roundAvoid(negCashFlowArray.get(j).nmea);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).neaa < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).neaa));

		        columnIndex = columnIndex + 1;
		        neaaTotal =  roundAvoid(neaaTotal) + roundAvoid(negCashFlowArray.get(j).neaa);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).nead < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).nead));

		        columnIndex = columnIndex + 1;
		        neadTotal =  roundAvoid(neadTotal) + roundAvoid(negCashFlowArray.get(j).nead);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).emcad < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).emcad));

		        columnIndex = columnIndex + 1;
		        emcadTotal =  roundAvoid(emcadTotal) + roundAvoid(negCashFlowArray.get(j).emcad);
		        cell = row.createCell(columnIndex);

		        if (negCashFlowArray.get(j).psoad < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).psoad));

		        columnIndex = columnIndex + 1;
		        psoadTotal =  roundAvoid(psoadTotal) + roundAvoid(negCashFlowArray.get(j).psoad);
		        cell = row.createCell(columnIndex);
		        if (negCashFlowArray.get(j).total < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(negCashFlowArray.get(j).total));

		        columnIndex = columnIndex + 1;
		        negTotal =  roundAvoid(negTotal) + roundAvoid(negCashFlowArray.get(j).total);
		    }

		    HSSFCellStyle cellStyle1;
		    HSSFFont font1;
		    cellStyle1 = wb.createCellStyle();
		    font1 = wb.createFont();
		    font1.setColor(HSSFFont.COLOR_RED);
		    font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		    cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

		    cellStyle1.setFont(font1);
		    cellStyle1.setFillForegroundColor(SKY_BLUE.index);

		    cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		    cell = row.createCell(columnIndex);
		    cell.setCellStyle(cellStyle1);

		    cell.setCellValue(negTotal.doubleValue());

		    rowIndex = rowIndex + 1;
		    Double posTotal =  new Double(0.00);

		    for (int j = 0; j <= posCashFlowArray.size() - 1; j++) {
		        columnIndex = 0;
		        row = sheet.createRow(rowIndex);
		        cell = row.createCell(columnIndex);
		        cell.setCellValue(posCashFlowArray.get(j).acc_name);

		        rowIndex = rowIndex + 1;
		        columnIndex = columnIndex + 1;
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).nesc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).nesc));

		        columnIndex = columnIndex + 1;
		        nescTotal =  roundAvoid(nescTotal) + roundAvoid(posCashFlowArray.get(j).nesc);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).nrsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).nrsc));

		        columnIndex = columnIndex + 1;
		        nrscTotal =  roundAvoid(nrscTotal) + roundAvoid(posCashFlowArray.get(j).nrsc);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).nfsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).nfsc));

		        columnIndex = columnIndex + 1;
		        nfscTotal =  roundAvoid(nfscTotal) + roundAvoid(posCashFlowArray.get(j).nfsc);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).ntsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).ntsc));

		        columnIndex = columnIndex + 1;
		        ntscTotal =  roundAvoid(ntscTotal) + roundAvoid(posCashFlowArray.get(j).ntsc);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).vcsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).vcsc));

		        columnIndex = columnIndex + 1;
		        vcscTotal =  roundAvoid(vcscTotal) + roundAvoid(posCashFlowArray.get(j).vcsc);
		        cell = row.createCell(columnIndex);
		       //----------------------------DRCAP CHANGES START ------------------------------//
				if(isdrEffective)
				{
				
		        //if (posCashFlowArray[j].heur < 0) {
		        //    cell.cellStyle = cellStyle;
		        //}

		        //cell.setCellValue(value : posCashFlowArray[j].heur);
		        
		        //columnIndex = columnIndex + 1;
		        //heurTotal = heurTotal + posCashFlowArray[j].heur;
		        //cell = row.createCell(columnIndex : columnIndex);
		        
		        
		        //if (posCashFlowArray[j].hlcu < 0) {
		        //    cell.cellStyle = cellStyle;
		        //}

		        //cell.setCellValue(value : posCashFlowArray[j].hlcu);
		        
		        //columnIndex = columnIndex + 1;
		        //hlcuTotal = hlcuTotal + posCashFlowArray[j].hlcu;
		        //cell = row.createCell(columnIndex : columnIndex);
		        
		        if (posCashFlowArray.get(j).lcsc == null || posCashFlowArray.get(j).lcsc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

			 	if(posCashFlowArray.get(j).lcsc == null) {
			 		cell.setCellValue(0.0);
			 		lcscTotal =  lcscTotal + 0.0;
			 	}else {
			 		cell.setCellValue(roundAvoid(posCashFlowArray.get(j).lcsc));
			 		lcscTotal =  roundAvoid(lcscTotal) + roundAvoid(posCashFlowArray.get(j).lcsc);
			 	}
			 	
		        //cell.setCellValue(posCashFlowArray.get(j).lcsc == null ? 0.0 : posCashFlowArray.get(j).lcsc);
		        
		        columnIndex = columnIndex + 1;
		        //lcscTotal = lcscTotal + (posCashFlowArray.get(j).lcsc == null ? 0.0 : posCashFlowArray.get(j).lcsc);
		        cell = row.createCell(columnIndex);
		        
		        }
		        //else
		        //{
		        
		        if (posCashFlowArray.get(j).heuc == null || posCashFlowArray.get(j).heuc < 0) {
		            cell.setCellStyle(cellStyle);
		        }

			 	if(posCashFlowArray.get(j).heuc == null) {
			 		cell.setCellValue(0.0);
			 		heucTotal =  heucTotal + 0.0;
			 	}else {
			 		cell.setCellValue(roundAvoid(posCashFlowArray.get(j).heuc));
			 		heucTotal =  roundAvoid(heucTotal) + roundAvoid(posCashFlowArray.get(j).heuc);
			 	}

		        //cell.setCellValue(posCashFlowArray.get(j).heuc == null ? 0.0 : posCashFlowArray.get(j).heuc);
		        
		        columnIndex = columnIndex + 1;
		        //heucTotal = heucTotal + (posCashFlowArray.get(j).heuc == null ? 0.0 : posCashFlowArray.get(j).heuc);
		        cell = row.createCell(columnIndex);
		        	
		        //}
				//-------------------------------------DRCAP CHANGES END --------------------------------//

		        if (posCashFlowArray.get(j).meuc == null || posCashFlowArray.get(j).meuc < 0) {
		            cell.setCellStyle(cellStyle);
		        }
		        
			 	if(posCashFlowArray.get(j).meuc == null) {
			 		cell.setCellValue(0.0);
			 		meucTotal =  meucTotal + 0.0;
			 	}else {
			 		cell.setCellValue(roundAvoid(posCashFlowArray.get(j).meuc));
			 		meucTotal =  roundAvoid(meucTotal) + roundAvoid(posCashFlowArray.get(j).meuc);
			 	}


		        //cell.setCellValue(posCashFlowArray.get(j).meuc == null ? 0.0 : posCashFlowArray.get(j).meuc);

		        columnIndex = columnIndex + 1;
		        //meucTotal = meucTotal + (posCashFlowArray.get(j).meuc == null ? 0.0 : posCashFlowArray.get(j).meuc);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).nmea < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).nmea));

		        columnIndex = columnIndex + 1;
		        nmeaTotal =  roundAvoid(nmeaTotal) + roundAvoid(posCashFlowArray.get(j).nmea);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).neaa < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).neaa));

		        columnIndex = columnIndex + 1;
		        neaaTotal =  roundAvoid(neaaTotal) + roundAvoid(posCashFlowArray.get(j).neaa);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).nead < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).nead));

		        columnIndex = columnIndex + 1;
		        neadTotal =  roundAvoid(neadTotal) + roundAvoid(posCashFlowArray.get(j).nead);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).emcad < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).emcad));

		        columnIndex = columnIndex + 1;
		        emcadTotal =  roundAvoid(emcadTotal) + roundAvoid(posCashFlowArray.get(j).emcad);
		        cell = row.createCell(columnIndex);

		        if (posCashFlowArray.get(j).psoad < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).psoad));

		        columnIndex = columnIndex + 1;
		        psoadTotal =  roundAvoid(psoadTotal) + roundAvoid(posCashFlowArray.get(j).psoad);
		        cell = row.createCell(columnIndex);
		        
		        if (posCashFlowArray.get(j).total < 0) {
		            cell.setCellStyle(cellStyle);
		        }

		        cell.setCellValue(roundAvoid(posCashFlowArray.get(j).total));

		        columnIndex = columnIndex + 1;
		        posTotal =  roundAvoid(posTotal) + roundAvoid(posCashFlowArray.get(j).total);
		    }

		    cell = row.createCell(columnIndex);
		    cell.setCellStyle(cellStyle1);

		    cell.setCellValue(posTotal);

		    columnIndex = 0;
		    row = sheet.createRow(rowIndex);
		    cell = row.createCell(columnIndex);
		    cell.setCellValue("Total");

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (nescTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(nescTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (nrscTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(nrscTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (nfscTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(nfscTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (ntscTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(ntscTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (vcscTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(vcscTotal));
		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    //---------------------------DRCAP CHANGES START-----------------------//
				if(isdrEffective)
			{
		    
		    //if (heurTotal < 0) {
		    //    cell.setCellStyle(style : cellStyle);
		    //}

		    //cell.setCellValue(value : heurTotal);
		    
		 	//columnIndex = columnIndex + 1;
		    //cell = row.createCell(columnIndex : columnIndex);

		    //if (hlcuTotal < 0) {
		    //    cell.setCellStyle(style : cellStyle);
		    //}

		    //cell.setCellValue(value : hlcuTotal);
		    
		    //columnIndex = columnIndex + 1;
		    //cell = row.createCell(columnIndex : columnIndex);
		    
		    if (lcscTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(lcscTotal));
		    
		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);
			
			}
			//else
			//{
		    if (heucTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(heucTotal));
		    
		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);
		    
			//}
			//------------------------------DRCAP CHANGES END--------------------------//

		    if (meucTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(meucTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (nmeaTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(nmeaTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (neaaTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(neaaTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (neadTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(neadTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (emcadTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(emcadTotal));

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);

		    if (psoadTotal.intValue() < 0) {
		        cell.setCellStyle(cellStyle);
		    }

		    cell.setCellValue(roundAvoid(psoadTotal));

		    columnIndex = columnIndex + 1;
		    HSSFCellStyle cellStyle2;
		    HSSFFont font2;
		    cellStyle2 = wb.createCellStyle();
		    font2 = wb.createFont();
		    font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		    cellStyle2.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

		    cellStyle2.setFont(font2);
		    cellStyle2.setFillForegroundColor(PINK.index);

		    cellStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		    cell = row.createCell(columnIndex);
		    cell.setCellStyle(cellStyle2);

		    cell.setCellValue("EMC CLearing A/C");

		    columnIndex = columnIndex + 1;
		    cell = row.createCell(columnIndex);
		    cell.setCellStyle(cellStyle2);

		    cell.setCellValue(roundAvoid((posTotal + negTotal)));

		    // 	rowIndex = rowIndex + 1
		    // 	row = sheet.createRow(rowIndex)
		    // 	columnIndex = 10
		    // 	cell = row.createCell(columnIndex)
		    // 	cell.setCellValue("a negative balance denotes debit from EMC recovery account")
		    // 	rowIndex = rowIndex + 1
		    // 	row = sheet.createRow(rowIndex)
		    // 	columnIndex = 10
		    // 	cell = row.createCell(columnIndex)
		    // 	cell.setCellValue("a positive balance denotes credit to EMC recovery account")
		    wb.write(fos);

		    fos.close();

		    logger.log(Priority.INFO,"[EMC] Generate Cashflow Excelsheet Success.");

		    // Log Jam Message
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
		                                   "Cashflow Excelsheet successfully generated: " + cashfn, 
		                                   "");
		}
		catch (SettlementRunException e) {
			e.printStackTrace();
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		    logger.error("Exception "+e.getMessage());
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		catch (Exception e) {
			e.printStackTrace();
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		    logger.error("Exception "+e.getMessage());
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		variableMap.put("standingVersion",standingVersion);
		variableMap.put("drEffectiveDate",drEffectiveDate);
		variableMap.put("negCashFlowArray", negCashFlowArray);
		variableMap.put("posCashFlowArray",posCashFlowArray);
    	return variableMap;
	}
	
    @Transactional
	public void calculateCashFlow(Map<String, Object> variableMap)
	{

		String eveId = (String)variableMap.get("eveId"); 
		Date settlementDate = (Date)variableMap.get("settlementDate"); 
		String settlementRunId = (String)variableMap.get("settlementRunId");
		String standingVersion = (String)variableMap.get("standingVersion");
		
		try{
		    msgStep = service_name+".calculateCashFlow()" ;

		    logger.log(Priority.INFO, " Starting Activity: " + msgStep + " ...");

		    // Log JAM Message
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
		                                   "Starting Cash flow reconciliation", "");

		    String sqlCmd = "SELECT SUM(COLUMN_5) SUM " + 
		    " FROM NEM.NEM_ACCOUNT_STATEMENTS AST, NEM.NEM_SETTLEMENT_ACCOUNTS ACC " + 
		    " WHERE AST.SAC_ID=ACC.ID AND AST.SAC_VERSION=ACC.VERSION AND STR_ID=? AND " + 
		    " ACC.EXTERNAL_ID<>'EMC ADJ_A' AND " + 
		    " REPORT_SECTION='MAINTOTAL' GROUP BY STR_ID";
		    BigDecimal totalAmt = new BigDecimal(0);
		    BigDecimal macpAmt;
		    BigDecimal meusAmt;
		    String macpCodeId = null;
		    String meusCodeId = null;
		    String acctId = null;
		    String gstId = null;
		    String meusAlwaysZero = "No";
		    BigDecimal macpLockVer = new BigDecimal(0);
		    BigDecimal meusLockVer = new BigDecimal(0);

		    // Testing
		    // 	settlementRunId = "73ED2569BB8D5016E0440003BADB43D1"
		    // 	settlementDate = '2009-03-25'
		    // Testing
		    logger.log(Priority.INFO, "Calculating Cash flow for Settlement Run: " + settlementRunId);
			
			//Object[] params = new Object[1];
			//params[0] =  settlementRunId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCmd, settlementRunId);
			for (Map row : list) {
				totalAmt = (BigDecimal)row.get("SUM");
			}
		    logger.log(Priority.INFO,"[EMC] Total Cash Flow of Settlement Date " + utilityFunctions.getddMMMyyyy(settlementDate) + " is " + totalAmt);

		    // Get MACP Value
		    sqlCmd = "SELECT a.VALUE " + 
		             " from NEM_MACP a, NEM_MEUC b " + 
		             " where a.MEUC_ID = b.ID and a.MEUC_VERSION = b.VERSION " + 
		             " and SETTLEMENT_MONTH=? and APPROVAL_STATUS='A'";
		    macpAmt = new BigDecimal(0);
			
			//Object[] params1 = new Object[1];
			//params1[0] =  utilityFunctions.convertUDateToSDate(utilityFunctions.getFirstDateOfMonth(settlementDate));
			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCmd, utilityFunctions.convertUDateToSDate(utilityFunctions.getFirstDateOfMonth(settlementDate)));
			for (Map row : list1) {
				macpAmt = (BigDecimal)row.get("VALUE");
			}
		    logger.log(Priority.INFO,"[EMC] MACP Amount: " + macpAmt);

		    // Calculate MEUS Value
		    meusAmt = new BigDecimal(- totalAmt.doubleValue() - macpAmt.doubleValue());

		    // 	if meusAmt < 0 then
		    // 		meusAlwaysZero = getSysParamVarChar(UtilityFunctions, paramName : "MEUS_NO_NEGATIVE")
		    // 		if meusAlwaysZero = "Yes" then
		    // 			meusAmt = 0
		    // 		end
		    // 	end
		    logger.log(Priority.INFO,"[EMC] MEUS Amount: " + meusAmt);

		    // Get MACP Charge Code
		    sqlCmd = "SELECT ID FROM NEM.NEM_NON_PERIOD_CHARGE_CODES " + 
		             "WHERE SOLOMON_CODE='MACP'";
			List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCmd, new Object[] {});
			for (Map row : list2) {
				macpCodeId = (String)row.get("ID");
			}
		    // Get MEUS Charge Code
		    sqlCmd = "SELECT ID FROM NEM.NEM_NON_PERIOD_CHARGE_CODES " + 
		             "WHERE SOLOMON_CODE='MEUS'";

			List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlCmd, new Object[] {});
			for (Map row : list3) {
				meusCodeId = (String)row.get("ID");
			}

		    // Get Account Id for External Id = 'EMC REC_A'
		    sqlCmd = "SELECT ID FROM NEM.NEM_SETTLEMENT_ACCOUNTS " + 
		             "WHERE EXTERNAL_ID='EMC ADJ_A' AND VERSION=?";

			List<Map<String, Object>> list4 = jdbcTemplate.queryForList(sqlCmd, new Object[] {standingVersion});
			for (Map row : list4) {
				acctId = (String)row.get("ID");
				break;
			}

		    // Get GST ID for 'NON APPLICABLE INPUT TAX'
		    sqlCmd = "SELECT ID FROM NEM.NEM_GST_CODES WHERE NAME='VN' AND VERSION=?";

			List<Map<String, Object>> list5 = jdbcTemplate.queryForList(sqlCmd, new Object[] {standingVersion});
			for (Map row : list5) {
				gstId = (String)row.get("ID");
				break;
			}
		    // Get ID for user "SYSTEM"
		    String userId = utilityFunctions.getUserId("SYSTEM");

		    // Get Current Lock Version for MACP
		    sqlCmd = "SELECT max(LOCK_VERSION) VERSION FROM NEM.NEM_NON_PERIOD_CHARGES " + 
		             "WHERE NPC_TYPE='ACC' AND SAC_ID=? AND COMMENTS='MACP RECOVERED' " + 
		             "AND START_DATE=? AND END_DATE=? AND NCC_ID=?";
			Object[] params2 = new Object[4];
			params2[0] =  acctId;
			params2[1] =  utilityFunctions.convertUDateToSDate(settlementDate);
			params2[2] =  utilityFunctions.convertUDateToSDate(settlementDate);
			params2[3] =  macpCodeId;
			//macpLockVer = jdbcTemplate.queryForObject(sqlCmd, params2, Integer.class);
			List<Map<String, Object>> macpLockVerList = jdbcTemplate.queryForList(sqlCmd, params2);
			for (Map row : macpLockVerList) {
				macpLockVer = row.get("VERSION") != null ? (BigDecimal)row.get("VERSION") : new BigDecimal(0);
				break;
			}

		    // Get Current Lock Version for MEUS
		    sqlCmd = "SELECT max(LOCK_VERSION) VERSION FROM NEM.NEM_NON_PERIOD_CHARGES " + 
		             "WHERE NPC_TYPE='ACC' AND SAC_ID=? AND (COMMENTS='SURPLUS MEUS' OR " + 
		             "COMMENTS='DEFICIT MEUS' ) " + 
		             "AND START_DATE=? AND END_DATE=? AND NCC_ID=?";

			Object[] params3 = new Object[4];
			params3[0] =  acctId;
			params3[1] =  utilityFunctions.convertUDateToSDate(settlementDate);
			params3[2] =  utilityFunctions.convertUDateToSDate(settlementDate);
			params3[3] =  meusCodeId;
			//meusLockVer = jdbcTemplate.queryForObject(sqlCmd, params3, Integer.class);
			List<Map<String, Object>> meusLockVerList = jdbcTemplate.queryForList(sqlCmd, params3);
			for (Map row : meusLockVerList) {
				meusLockVer = row.get("VERSION") != null ? (BigDecimal)row.get("VERSION") : new BigDecimal(0);
				break;
			}

		    Date enteredDate = new Date();
		    String macpNPCId = utilityFunctions.getEveId();
		    String meusNPCId = utilityFunctions.getEveId();

		    // Insert into Non Period Charge
		    sqlCmd = "INSERT INTO NEM.NEM_NON_PERIOD_CHARGES (ID, NPC_TYPE, EXTERNAL_ID, NAME, CHARGE_DATE," + 
		             " ENTERED_DATE, CHARGE_FREQUENCY, CALCULATION_RULE, DEBIT_CREDIT, AMOUNT, START_DATE, END_DATE," + 
		             " COMMENTS, SAC_ID, GST_ID, APPROVAL_STATUS, APPROVAL_TIMESTAMP, APPROVER_ID, LOCK_VERSION, NCC_ID) " + 
		             " VALUES ( ?,'ACC','NPC:' || NPC_SEQ.NEXTVAL,?,?,?,'O','Y',?,?,?,?,?,?,?,?,SYSDATE,?,?,?)";

		    // NCC_ID
			Object[] params4 = new Object[15];
			params4[0] =  macpNPCId;
			params4[1] =  "MACP SD " + utilityFunctions.getddMMMyyyy(settlementDate);// NAME: MACP SD DD MMM YYYY
			params4[2] =  utilityFunctions.convertUDateToSDate(settlementDate);// CHARGE_DATE: Settlement Date
			params4[3] =  utilityFunctions.convertUDateToSDate(enteredDate);// ENTERED_DATE: Current Time
			params4[4] =  macpAmt.doubleValue() >= 0 ? "C" : "D";// DEBIT_CREDIT
			params4[5] =  macpAmt.abs(); // MACP AMOUNT
			params4[6] =  utilityFunctions.convertUDateToSDate(settlementDate);// START_DATE: Settlement Date
			params4[7] =  utilityFunctions.convertUDateToSDate(settlementDate); // END_DATE: Settlement Date?
			params4[8] =  "MACP RECOVERED";// COMMENTS: MACP RECOVERED ...
			params4[9] =  acctId;// SAC_ID: Account ID for "EMC REC_A"
			params4[10] =  gstId;// GST_ID: ID of NEM_GST_CODES with Descritpion = "NON APPLICABLE INPUT TAX"
			params4[11] =  "A";// Approved
			params4[12] =  userId;
			params4[13] =  macpLockVer.intValue() + 1;
			params4[14] =  macpCodeId;
			jdbcTemplate.update(sqlCmd, params4);

		    // NCC_ID
			Object[] params5 = new Object[15];
			params5[0] =  meusNPCId;
			params5[1] =  (meusAmt.doubleValue() >= 0 ? "SURPLUS" : "DEFICIT") + " MEUS SD " + utilityFunctions.getddMMMyyyy(settlementDate);// NAME: SURPLUS MEUS SD DD MMM YYYY
			params5[2] =  utilityFunctions.convertUDateToSDate(settlementDate);// CHARGE_DATE: Settlement Date
			params5[3] =  utilityFunctions.convertUDateToSDate(enteredDate);// ENTERED_DATE: Current Time
			params5[4] =  meusAmt.doubleValue() >= 0 ? "C" : "D";// DEBIT_CREDIT
			params5[5] =  meusAmt.abs(); // MACP AMOUNT
			params5[6] =  utilityFunctions.convertUDateToSDate(settlementDate);// START_DATE: Settlement Date
			params5[7] =  utilityFunctions.convertUDateToSDate(settlementDate); // END_DATE: Settlement Date?
			params5[8] =  (meusAmt.doubleValue() >= 0 ? "SURPLUS" : "DEFICIT") + " MEUS";// COMMENTS: Surplus MEUS
			params5[9] =  acctId;// SAC_ID: Account ID for "EMC REC_A"
			params5[10] =  gstId;// GST_ID: ID of NEM_GST_CODES with Descritpion = "NON APPLICABLE INPUT TAX"
			params5[11] =  "A";// Approved
			params5[12] =  userId;
			params5[13] =  meusLockVer.intValue() + 1;
			params5[14] =  meusCodeId;
			jdbcTemplate.update(sqlCmd, params5);

		    // Insert into Peridioc Results
		    String sqlPeriodic = "INSERT INTO NEM.NEM_PERIODIC_RESULTS (ID, CALCULATION_RESULT, " + 
		    "GST_AMOUNT, STR_ID, NPC_ID, SAC_ID, SAC_VERSION) VALUES (SYS_GUID(),?,?,?,?,?,?)";

			Object[] params6 = new Object[6];
			params6[0] =  macpAmt;// calculation_results
			params6[1] =  0;// gst_amount
			params6[2] =  settlementRunId;
			params6[3] =  macpNPCId;// NON_PERIOD_CHARGES.ID
			params6[4] =  acctId;
			params6[5] =  standingVersion;
			jdbcTemplate.update(sqlPeriodic, params6);

			Object[] params7 = new Object[6];
			params7[0] =  meusAmt;// calculation_results
			params7[1] =  0;// gst_amount
			params7[2] =  settlementRunId;
			params7[3] =  meusNPCId;// NON_PERIOD_CHARGES.ID
			params7[4] =  acctId;
			params7[5] =  standingVersion;
			jdbcTemplate.update(sqlPeriodic, params7);

		    logger.log(Priority.INFO,"[EMC] Cash flow reconciliation Success.");

		    // JAM Message
		    utilityFunctions.logJAMMessage(eveId, "I", msgStep, 
		                                   "Cash flow reconciliation success.", "");

		    // Update nem Account Statements
		    String sqlCmdMain = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAIN'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlCmdMainTotal = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAINTOTAL'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlGetSeq = "Select MAX(seq) SEQ from NEM.nem_account_statements " + 
		    "Where str_id = ? and report_section like ? and sac_id = ? ";
		    int seq = 0;
		
			Object[] params8 = new Object[3];
			params8[0] =  settlementRunId;
			params8[1] =  "MAIN%";
			params8[2] =  acctId;
			List<Map<String, Object>> list6 = jdbcTemplate.queryForList(sqlGetSeq, params8);
			for (Map row : list6) {
				seq = (row.get("SEQ") == null ? seq : (BigDecimal)row.get("SEQ")).intValue();
			}
			
			DecimalFormat decimalFormat = new DecimalFormat("#######.00");
		    String macpAmtStr = decimalFormat.format(macpAmt);
		    String meusAmtStr = decimalFormat.format(meusAmt);
		    String totalAmtStr = decimalFormat.format((- totalAmt.doubleValue()));

		    // Insert MACP value
		    seq = seq + 1;
			
			Object[] params9 = new Object[10];
			params9[0] =  seq;// calculation_results
			params9[1] =  "MACP - MACP SD " + utilityFunctions.getddMMMyyyy(settlementDate).toUpperCase();
			params9[2] =  macpAmtStr;
			params9[3] =  "0";// NON_PERIOD_CHARGES.ID
			params9[4] =  "0";
			params9[5] =  macpAmtStr;
			params9[6] =  settlementRunId;
			params9[7] =  acctId;
			params9[8] =  standingVersion;
			params9[9] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlCmdMain, params9);

		    // Insert MEUS value
		    seq = seq + 1;
			Object[] params10 = new Object[10];
			params10[0] =  seq;// calculation_results
			params10[1] =  "MEUS - " + (meusAmt.doubleValue() >= 0 ? "SURPLUS" : "DEFICIT") + " MEUS SD " + utilityFunctions.getddMMMyyyy(settlementDate).toUpperCase();
			params10[2] =  meusAmtStr;
			params10[3] =  "0";// NON_PERIOD_CHARGES.ID
			params10[4] =  "0";
			params10[5] =  meusAmtStr;
			params10[6] =  settlementRunId;
			params10[7] =  acctId;
			params10[8] =  standingVersion;
			params10[9] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlCmdMain, params10);

		    seq = seq + 1;
			
			Object[] params11 = new Object[10];
			params11[0] =  seq;// calculation_results
			params11[1] =  "Total Due (Owed)";
			params11[2] =  totalAmtStr;
			params11[3] =  "0";// NON_PERIOD_CHARGES.ID
			params11[4] =  "0";
			params11[5] =  totalAmtStr;
			params11[6] =  settlementRunId;
			params11[7] =  acctId;
			params11[8] =  standingVersion;
			params11[9] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlCmdMainTotal, params11);

		    String sqlOther = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHER'," + 
		    "?,?,?,'','','','','','','','','','',?,?,?,?)";
		    String sqlOtherTotal = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHERTOTAL'," + 
		    "?,'Total Other Charges for the Run',?,'','','','','','','','','','',?,?,?,?)";

			Object[] params12 = new Object[3];
			params12[0] =  settlementRunId;
			params12[1] =  "OTHER%";
			params12[2] =  acctId;
			List<Map<String, Object>> list7 = jdbcTemplate.queryForList(sqlGetSeq, params12);
			for (Map row : list7) {
				seq = (row.get("SEQ") == null ? seq : (BigDecimal)row.get("SEQ")).intValue();
			}
		    // Insert MACP value
		    seq = seq + 1;

			Object[] params13 = new Object[7];
			params13[0] =  seq;// calculation_results
			params13[1] =  "MACP - MACP SD " + utilityFunctions.getddMMMyyyy(settlementDate).toUpperCase();
			params13[2] =  macpAmtStr;
			params13[3] =  settlementRunId;
			params13[4] =  acctId;
			params13[5] =  standingVersion;
			params13[6] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlOther, params13);
		    // Insert MEUS value
		    seq = seq + 1;

			Object[] params14 = new Object[7];
			params14[0] =  seq;// calculation_results
			params14[1] =  "MEUS - " + (meusAmt.doubleValue() >= 0 ? "SURPLUS" : "DEFICIT") + " MEUS SD " + utilityFunctions.getddMMMyyyy(settlementDate).toUpperCase();
			params14[2] =  meusAmtStr;
			params14[3] =  settlementRunId;
			params14[4] =  acctId;
			params14[5] =  standingVersion;
			params14[6] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlOther, params14);
		    // Insert Total value
		    seq = seq + 1;

			Object[] params15 = new Object[6];
			params15[0] =  seq;// calculation_results
			params15[1] =  totalAmtStr;
			params15[2] =  settlementRunId;
			params15[3] =  acctId;
			params15[4] =  standingVersion;
			params15[5] =  utilityFunctions.convertUDateToSDate(settlementDate);
			jdbcTemplate.update(sqlOtherTotal, params15);
		}
		catch (SettlementRunException e) {
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		   e.printStackTrace();
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] Exception in <" + msgStep + ">: " + e.getMessage());
		   e.printStackTrace();
		    throw new SettlementRunException(e.getMessage(), msgStep);
		}
		
	}
    
    public CashFlow checkEmptyValue(CashFlow cashFlowObj)
	{

		if (cashFlowObj.emcad == null || cashFlowObj.emcad == 0.00) {
			cashFlowObj.emcad = 0.0;
		}

		if (cashFlowObj.heuc == null || cashFlowObj.heuc == 0.00) {
			cashFlowObj.heuc = 0.0;
		}

		if (cashFlowObj.meuc == null || cashFlowObj.meuc == 0.00) {
			cashFlowObj.meuc = 0.0;
		}

		if (cashFlowObj.neaa == null || cashFlowObj.neaa == 0.00) {
			cashFlowObj.neaa = 0.0;
		}

		if (cashFlowObj.nead == null || cashFlowObj.nead == 0.00) {
			cashFlowObj.nead = 0.0;
		}

		if (cashFlowObj.nesc == null || cashFlowObj.nesc == 0.00) {
			cashFlowObj.nesc = 0.0;
		}

		if (cashFlowObj.nfsc == null || cashFlowObj.nfsc == 0.00) {
			cashFlowObj.nfsc = 0.0;
		}

		if (cashFlowObj.nmea == null || cashFlowObj.nmea == 0.00) {
			cashFlowObj.nmea = 0.0;
		}

		if (cashFlowObj.nrsc == null || cashFlowObj.nrsc == 0.00) {
			cashFlowObj.nrsc = 0.0;
		}

		if (cashFlowObj.ntsc == null || cashFlowObj.ntsc == 0.00) {
			cashFlowObj.ntsc = 0.0;
		}

		if (cashFlowObj.psoad == null || cashFlowObj.psoad == 0.00) {
			cashFlowObj.psoad = 0.0;
		}

		if (cashFlowObj.total == null || cashFlowObj.total == cashFlowObj.heuc) //TODO cashFlowObj.heuc == total
		{
			cashFlowObj.total = 0.0;
		}

		if (cashFlowObj.vcsc == null || cashFlowObj.vcsc == 0.00) {
			cashFlowObj.vcsc = 0.0;
		}

		return cashFlowObj;

	}
    
    public static double roundAvoid(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }

}
