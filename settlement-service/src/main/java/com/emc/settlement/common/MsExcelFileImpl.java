package com.emc.settlement.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.pojo.MsExcelFile;
@Component
public class MsExcelFileImpl {

	protected static final Logger logger = Logger.getLogger(MsExcelFileImpl.class);

	public boolean cellIdToColRow(String cellId, Integer[] cells)
	{
		/*
		 * translates cellId e.g. C12 into row index, column index
		 */
		// set default values
		int rowIndex = -1;
		int colIndex = -1;
		char ch;
		Character[] colStr = new Character[cellId.length()-1];
		int startOfDigit = -1;

		// first char must be [A-Z]
		if (!Character.isLetter(cellId.charAt(0))) {
			logger.log(Priority.INFO, "[EMC] MsExcelFile.cellIdToColRow() -- Invalid cellId " + cellId);

			return false;
		}

		// keep the first char (row identifier)
		colStr[0] = cellId.charAt(0);
		int i = 1;

		while (i < cellId.length()) {
			ch = cellId.charAt(i);

			if (Character.isLetter(ch)) {
				colStr[colStr.length - 1] = ch;
			} else if (Character.isDigit(ch) && startOfDigit == -1) {
				startOfDigit = i;

				break;
			} else {
				// not digit or letter, we have an invalid cell identifier
				logger.log(Priority.WARN, "[EMC] MsExcelFile.cellIdToColRow() -- Invalid cellId " + cellId);

				return false;
			}

			i = i + 1;
		}

		if (startOfDigit == -1) {
			logger.log(Priority.WARN, "[EMC] MsExcelFile.cellIdToColRow() -- Invalid cellId " + cellId);

			return false;
		}

		rowIndex = Integer.parseInt(cellId.substring(startOfDigit)) - 1;
		Character a = new Character('A');
		int asciiA = a.charValue();
		i = 0;
		double val = 0.0;

		while (i < startOfDigit) {
			if (i == startOfDigit - 1) {
				val = val + (colStr[i].charValue() - asciiA);
			} else {
				// val = val + ((colStr[i].charValue() - asciiA) *
				// asciiA.pow(colStr.length - i - 1));
				val = val + ((colStr[i].charValue() - asciiA) * Math.pow(asciiA, (startOfDigit - i - 1)));
			}

			i = i + 1;
		}

		// colIndex = Int.intValue(value : substring(cellId, first : 0, last :
		// startOfDigit))
		colIndex = (int) val;
		cells[0] = rowIndex;
		cells[1] = colIndex;

		return true;

	}


	public int checkColNumber(String sheetName, String titleCellId, MsExcelFile excelFile)
	{
			/*
			returns total column number of given worksheet name*/
		try {
			HSSFSheet sheet = null;
			int sheetIndex = - 1;

			if (sheetName != null) {
				int i = excelFile.worksheetName.indexOf(sheetName);

				if (i == - 1) {
					logger.log(Priority.WARN,"[EMC] MsExcelFile.getCellLocation() -- worksheet " + sheetName +
							" not found");

					return - 1;
				}
				else {
					sheet = excelFile.worksheet.get(i);
				}
			}

			// titleRow as Int = (Int) substring(titleCellId, first : 1, last : 1)
			Integer[] cells = new Integer[2];
//		    Integer colIndex = new Integer(0);
			boolean result;
			result = this.cellIdToColRow( titleCellId, cells);

			logger.log(Priority.INFO,"[EMC] Title Row: " + cells[0]);

			int k = 0;
			boolean found = false;
			HSSFRow row = sheet.getRow(cells[0]);
			int totalCol = 0;

			if (row != null) {
				int lastCell = row.getLastCellNum();
				int j = 0;

				while (j <= lastCell && ! found) {
					HSSFCell cell = row.getCell(j);

					if (cell != null) {
						switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_STRING:
								if (String.valueOf(cell.getRichStringCellValue()).length() > 0) {
									totalCol = totalCol + 1;
								}
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								throw new Exception( "Invalid Title column");
								// - delete break;
							case HSSFCell.CELL_TYPE_FORMULA:
								if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
									if (String.valueOf(cell.getRichStringCellValue()).length() > 0) {
										totalCol = totalCol + 1;
									}
								}
								else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
									throw new Exception( "Invalid Title column");
								}
								break;
						}

						// end case
					}

					// cell is not null
					j = j + 1;
				}
			}

			return totalCol;
		}
		catch (Exception e) {
			return - 1;
		}

	}

	public void colRowToCellId(int colIndex, int rowIndex, String[] cellIds)
	{
		/*
		Convert colIndex, rowIndex back into cellIds
		Handles only column number up to colIndex 701 (ZZ)*/
		int A = 65;

		// ASCII value for capital A
		String sCol = "";
		int iRemain = 0;

		if (colIndex > 701) {
			logger.log(Priority.WARN,"[EMC] MsExcelFile.colRowToCellId() -- colIndex exceeded MAXIMUM value of 701");

			return ;
		}

		if (colIndex < 26) {
			//TODO MURALI String.valueOfChar
			sCol = getFromAsciiCode(colIndex + 65);
		}
		else {
			int iVal = Math.abs(colIndex / 26);
			iRemain = colIndex % 26;
			sCol = getFromAsciiCode(iVal + 64) +
					getFromAsciiCode(iRemain + 65);
		}

		cellIds[0] = sCol + String.valueOf(rowIndex + 1);

//		return cellIds;
	}

	private String getFromAsciiCode(int i) {
		Character ch = (char)(i);
		return Character.toString(ch);
	}

	public HSSFSheet findWorksheet(String name, MsExcelFile excelFile)
	{
		/*
		return worksheet object matching the sheetname*/
		if (excelFile.worksheet.size() < 0) {
			return null;
		}

		return excelFile.workbook.getSheet(name);
	}

	public boolean getCellLocation(String[] sheetNames, Object cellValue, String[] cellIds, MsExcelFile excelFile)
	{
		/*
		returns worksheet name and cellIds (e.g. C23) of cell containing @param cellValue
		if @param sheetNames is not null, then search only within specified worksheet, else
		search all worksheets in workbook*/
		List<HSSFSheet> sheets = new ArrayList<HSSFSheet>();
		int sheetIndex = - 1;

		if (sheetNames != null && sheetNames[0]!=null) {
			int i =  excelFile.worksheetName.indexOf(sheetNames[0]);

			if (i == - 1) {
				logger.log(Priority.WARN,"[EMC] MsExcelFile.getCellLocation() -- worksheet " + sheetNames +
						" not found");

				return false;
			}
			else {
				sheets.add(0, excelFile.worksheet.get(i));
			}
		}
		else {
			sheets = excelFile.worksheet;
		}

		int k = 0;
		boolean found = false;

		while (k < sheets.size() && ! found) {
			int i = sheets.get(k).getFirstRowNum();
			int lastRow = sheets.get(k).getLastRowNum();

			// rowIndex as Int
			int colIndex = - 1;

			// search for cellValue
			while (i <= lastRow && ! found) {
				HSSFRow row = sheets.get(k).getRow( i);

				if (row != null) {
					int lastCell = row.getLastCellNum();
					int j = 0;

					while (j <= lastCell && ! found) {
						HSSFCell cell = row.getCell(j);

						if (cell != null) {
							switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_STRING:
									if (String.valueOf(cell.getRichStringCellValue()).toUpperCase().equals(((String) cellValue).toUpperCase())) {
										this.colRowToCellId(j, i, cellIds);
										found = true;
										break;
									}
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									if (HSSFDateUtil.isCellDateFormatted(cell)) {
										if (cell.getDateCellValue() == cellValue) {
											this.colRowToCellId( j,  i, cellIds);
											found = true;
											break;
										}
									}
									else {
										if (String.valueOf(cell.getNumericCellValue()).equals(((String) cellValue))) {
											this.colRowToCellId(j,  i, cellIds);
											found = true;
											break;
										}
									}
									break;
								case HSSFCell.CELL_TYPE_FORMULA:
									if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
										if (String.valueOf(cell.getRichStringCellValue()).toUpperCase().equals(((String) cellValue).toUpperCase())) {
											this.colRowToCellId(j, i, cellIds);
											found = true;
											break;
										}
									}
									else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
										if (String.valueOf(cell.getNumericCellValue()).equals(((String) cellValue))) {
											this.colRowToCellId(j, i, cellIds);
											found = true;
											break;
										}
									}
									break;
							}

							// end case
						}

						// cell is not null
						j = j + 1;
					}
				}

				// row is not null
				i = i + 1;
			}

			k = k + 1;
		}

		if (! found) {
			// column heading 'name' not found
			logger.log(Priority.INFO,"[EMC] MsExcelFile.getCellLocation() -- cell value [" + cellValue +
					"] not found");
		}
		else {
			// case where @param sheetNames is null and we iterate through all the sheets to look
			// for 'cellValue'
			sheetNames[0] = excelFile.workbook.getSheetName(excelFile.workbook.getSheetIndex(sheets.get(k - 1)));
		}

		return found;

	}

	public Object getCellValue(String sheetName, String cellId, String dFormat, MsExcelFile excelFile)
	{
		/*
		returns cell value for specified cellId
		Cells are specified using Column-Row identifier
		e.g. B2 denotes cell in column B row 2, AA23 denotes cell in column AA row 23*/
		HSSFSheet sheet;
		sheet = findWorksheet(sheetName, excelFile);

		if (sheet == null) {
			logger.log(Priority.WARN,"[EMC] MsExcelFile.getCellValue() -- cannot find worksheet: " + sheetName);

			return null;
		}

//		int rowIndex = 0;
//		int colIndex = 0;
		Integer[] cells = new Integer[2];
		Boolean validCell = this.cellIdToColRow(cellId, cells);

		if (! validCell || cells[0] == - 1 || cells[1] == - 1) {
			logger.log(Priority.WARN,"[EMC] MsExcelFile.getCellValue() -- Invalid cellID: " + cellId);

			return null;
		}
		else {
			logger.log(Priority.INFO,"[EMC] MsExcelFile.getCellValue() -- CellID=" + cellId +
					", cells[0]=" + cells[0] + ", cells[1]=" + cells[1]);
		}

		HSSFRow row;
		row = sheet.getRow(cells[0]);

		if (row == null) {
			logger.log(Priority.INFO,"[EMC] MsExcelFile.getCellValue() -- row is null");

			return null;
		}

		Object value = null;
		HSSFCell cell = row.getCell(cells[1]);

		if (cell != null) {
			switch (cell.getCellType()) {
				case HSSFCell.CELL_TYPE_NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat fmt = new SimpleDateFormat(dFormat);
						value = fmt.format(cell.getDateCellValue());
					}
					else {
						value = cell.getNumericCellValue();
					}
					break;
				case HSSFCell.CELL_TYPE_STRING:
					value = cell.getRichStringCellValue();
					break;
				case HSSFCell.CELL_TYPE_FORMULA:
					value = cell.getCellFormula();
					break;
			}
		}

		return value;

	}

	public Object[] getColumnValues(String[] sheetNames, String columnName, int numRows, MsExcelFile excelFile)
	{
		/*
		search workbook for first occuramce of cell containing column heading @param 'columnName'
		if found, return the column of cell data values under column heading

		@param sheetName - worksheet name; if specified, only search specified worksheet
		@param columnName - column name
		@param numRows - if > 0, then number of cell data items to return; else read values until first null cell/data value

		@return  Any[] - calling program need to cast the Any[] into its appropriate data type

		NOTE:	For date columns, Any[] contains Java.Util.Date objects*/
		String[] cellIds = new String[1];
		boolean found = this.getCellLocation(sheetNames, columnName, cellIds, excelFile);

		if (! found) {
			logger.log(Priority.INFO,"[EMC] MsExcelFile.getColumnValues() -- columnName" + columnName +
					" not found");

			return null;
		}

		HSSFSheet sheet;
		sheet = findWorksheet(sheetNames[0], excelFile);
//		int colIndex = 0;
//		int rowIndex = 0;
		Integer[] cells = new Integer[2];
		found = this.cellIdToColRow(cellIds[0], cells);

		if (! found) {
			logger.log(Priority.INFO,"[EMC] MsExcelFile.getColumnValues() -- invalid cellId" + cellIds[0]);

			return null;
		}

		int i = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		Object[] values = new Object[numRows];


		// dummy value
		i = cells[0] + 1;
		int j =0;

		// discard column header row, fetch starting from next row
		while (i <= lastRow) {
			HSSFRow row = sheet.getRow(i);

			if (row != null) {
				HSSFCell cell = row.getCell(cells[1]);

				if (cell != null) {
					Object cellData;

					switch (cell.getCellType()) {
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								cellData = cell.getDateCellValue().clone();
								values[j++] = cellData;
							}
							else {
								cellData = cell.getNumericCellValue();
								values[j++] = cellData;
							}
							break;
						case HSSFCell.CELL_TYPE_STRING:
							cellData = String.valueOf(cell.getRichStringCellValue());
							values[j++] = cellData;
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
		                /*cellData = cell.getCellFormula();
		                values[j++] = cellData;*/
							if(cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
								cellData = String.valueOf(cell.getRichStringCellValue());
								values[j++] = cellData;
							} else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									cellData = cell.getDateCellValue().clone();
									values[j++] = cellData;
								}
								else {
									cellData = cell.getNumericCellValue();
									values[j++] = cellData;
								}
							}
							break;
					}
				}
				else {
					logger.log(Priority.INFO,"[EMC] MsExcelFile.getColumnValues() -- cell @ row " + i + " is null");

					// if caller did not specify how many rows to process, stop futther processing if cell has null value
					if (numRows <= 0) {
						break;
					}
				}

				// cell is not null
			}
			else {
				// stop futther processing on getting null row
				logger.log(Priority.INFO,"[EMC] MsExcelFile.getColumnValues() -- row " + i + " is null");

				break;
			}

			// row is not null
			// take into account the dummy value at values[0], therefore == length(values) - 1 below
			if (numRows > 0 && j == numRows) {
				break;
			}

			i = i + 1;
		}

		//TODO MURALI values.delete(0);

		// remove dummy value
		if (values.length == 0) {
			values = null;

			logger.log(Priority.INFO,"[EMC] MsExcelFile.getColumnData() -- no data found");
		}

		return values;

	}


	public boolean initializeWBookFromBinary(String fileContent, MsExcelFile excelFile)
	{
		//fileContent = bytes;

		// fin as Java.Io.FileInputStream = Java.Io.FileInputStream("d:\\temp\\pso.xls")
		// fin.read(fileContent)
		// poifs as POI.Org.Apache.Poi.Poifs.Filesystem.POIFSFileSystem = POI.Org.Apache.Poi.Poifs.Filesystem.POIFSFileSystem(fin)
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] bytes = decoder.decode(fileContent);
		ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
		POIFSFileSystem poifs;
		try {
			poifs = new POIFSFileSystem(bIn);
			excelFile.workbook = new HSSFWorkbook(poifs);
			excelFile.numSheets = excelFile.workbook.getNumberOfSheets();
			/* 	Store sheets and sheet names */
			int i = 0;

			while (i < excelFile.numSheets) {
				// display "MsExcelFile.initializeWBookFromBinary() -- fetching sheet #" + i
				HSSFSheet sheet = excelFile.workbook.getSheetAt(i);

				if (sheet == null) {
					logger.log(Priority.INFO,"MsExcelFile.initializeWBookFromBinary() -- Sheet #" + i + " is null");
				}

				excelFile.worksheet.add(i, sheet);
				excelFile.worksheetName.add(i,  excelFile.workbook.getSheetName(i));
				i = i + 1;
			}
		} catch (IOException e) {
			logger.error("Exception "+e.getMessage());
		}

		return true;

	}


}
