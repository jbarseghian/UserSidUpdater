package com.naranja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ConfigurationProperties;

public class PowershellUtil {
	private static final Logger LOG = LoggerFactory.getLogger(PowershellUtil.class);
	private static final String WAS_CREATED_SUCCESSFULLY = " was created successfully.";

	public static void processPowerShell() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		String csvFile = ConfigurationProperties.getPropertyValue("sourceFileProcess2");
		String command = "Import-CSV " + csvFile;
		// command = "Import-CSV " + csvFile+ " | ForEach-Object {get-aduser -identity
		// $_.usuario -properties sid | select sAMAccountName,sid} ";
		Process proc = runtime.exec("cmd.exe /c powershell.exe  " + command);
		generateUserInforUpdatesSQLScript(proc);
	}

	private static void generateUserInforUpdatesSQLScript(Process proc) {
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		StringBuilder sqlScript = new StringBuilder();
		long count = buildUpdateSQL(isr, sqlScript);
		generateSQLFile(sqlScript, count);
	}

	private static long buildUpdateSQL(InputStreamReader isr, StringBuilder sqlScript) {
		long count = 0;
		String line = "";
		String cvsSplitBy = ",";
		String comma = ", ";
		String tilde = "'";

		try (BufferedReader br = new BufferedReader(isr)) {
			System.out.println(br.readLine());
			System.out.println(br.readLine());
			System.out.println(br.readLine());
			while ((line = br.readLine()) != null) {
				if (!"".equals(line.trim())) {
					String[] user = line.split(cvsSplitBy);
					sqlScript.append("UPDATE USERINFO SET SID = ");
					sqlScript.append(tilde).append(user[0]).append(tilde); // user[1]
					sqlScript.append(comma).append("NetworkDomain = ");
					sqlScript.append(tilde).append(ConfigurationProperties.getPropertyValue("networkDomain"))
							.append(tilde);
					sqlScript.append(comma).append("NetworkAlias = ");
					sqlScript.append(tilde).append(user[0]).append(tilde);
					sqlScript.append(" WHERE ID = ");
					sqlScript.append(tilde).append(user[0]).append(tilde);
					sqlScript.append("; \n");
					count++;
				}
			}

		} catch (IOException e) {
			LOG.error("generateUserInforUpdatesSQLScript exception: " + e.getMessage());
		}
		return count;
	}

	private static void generateSQLFile(StringBuilder result, long count) {
		try {

			String sglFile = null;
			try {
				sglFile = ConfigurationProperties.getPropertyValue("sourceFileProcess3");
			} catch (Exception e) {
				LOG.error("generateUserInforUpdatesSQLScript exception: " + e.getMessage());
			}
			File file = new File(sglFile);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(result.toString());
			fileWriter.flush();
			fileWriter.close();

			LOG.info("List of updates:\n" + result.toString());
			LOG.info("\nTotal of updates: " + count);
			LOG.info("The sql file: " + sglFile + WAS_CREATED_SUCCESSFULLY);
		} catch (IOException e) {
			LOG.error("generateUserInforUpdatesSQLScript exception: " + e.getMessage());
		}
	}

}