package com.naranja;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ConfigurationProperties;

public class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	private static final String WAS_CREATED_SUCCESSFULLY = " was created successfully.";

	public static void main(String[] args) {
		generateCSVFromXLS();
		executeSIDPowerShell();
	}

	private static void generateCSVFromXLS() {
		String fileName = ConfigurationProperties.getPropertyValue("sourceFileProcess1");
		String destinationCSVFileName = ConfigurationProperties.getPropertyValue("sourceFileProcess2");

		try {
			XLSToCSVFiles.convertExcelToCSV(fileName, destinationCSVFileName, false);
			LOG.info("The CSV file: " + destinationCSVFileName + WAS_CREATED_SUCCESSFULLY);

		} catch (EncryptedDocumentException | InvalidFormatException
				| org.apache.poi.openxml4j.exceptions.InvalidFormatException | IOException e) {
			LOG.error("generateCSVFromXLS exception: " + e.getMessage());
		}
	}

	private static void executeSIDPowerShell() {
		try {
			PowershellUtil.processPowerShell();
		} catch (IOException e) {
			LOG.error("PowerShell exception: " + e.getMessage());
		}
	}

}
