package com.naranja;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openxml4j.exceptions.InvalidFormatException;

public class XLSToCSVFiles {
	public static void convertExcelToCSV(String fileName, String destinationCSVFileName, boolean allColumns)
			throws InvalidFormatException, IOException, EncryptedDocumentException,
			org.apache.poi.openxml4j.exceptions.InvalidFormatException {

		BufferedWriter output = new BufferedWriter(new FileWriter(destinationCSVFileName));

		InputStream is = new FileInputStream(fileName);

		Workbook wb = WorkbookFactory.create(is);

		Sheet sheet = wb.getSheetAt(0);

		int maxColumns = 1;
		if (allColumns) {
			maxColumns = sheet.getRow(0).getLastCellNum();
		}

		for (Row row : sheet) {

			int minCol = 0; // row.getFirstCellNum()
			int maxCol = maxColumns;

			for (int i = minCol; i < maxCol; i++) {

				Cell cell = row.getCell(i);
				String buf = "";
				if (i > 0) {
					buf = ",";
				}

				if (cell == null) {
					output.write(buf);
				} else {

					String v = null;

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						v = cell.getRichStringCellValue().getString();
						if("Alias".equals(v)) {
							v = "usuario"; 
						}
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							v = cell.getDateCellValue().toString();
						} else {
							v = String.valueOf(cell.getNumericCellValue());
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						v = String.valueOf(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						v = cell.getCellFormula();
						break;
					default:
					}

					if (v != null) {
						buf = buf + toCSV(v);
					}
					output.write(buf);
					// System.out.print(buf);
				}
			}

			output.write("\n");
		}
		is.close();
		output.close();
	}

	/*
	 * </strong> Escape the given value for output to a CSV file. Assumes the value
	 * does not have a double quote wrapper.
	 * 
	 * @return
	 */
	public static String toCSV(String value) {

		String v = null;
		boolean doWrap = false;

		if (value != null) {

			v = value;

			if (v.contains("\"")) {
				v = v.replace("\"", "\"\""); // escape embedded double quotes
				doWrap = true;
			}

			if (v.contains(",") || v.contains("\n")) {
				doWrap = true;
			}

			if (doWrap) {
				v = "\"" + v + "\""; // wrap with double quotes to hide the comma
			}
		}

		return v;
	}

}