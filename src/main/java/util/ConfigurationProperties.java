package util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationProperties {

	private static final String CONFIGURATION_PROPERTIES = "configuration/configuration.properties";
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProperties.class);

	public static String getPropertyValue(String property) {
		String propertyValue = null;
		try {
			Properties prop = PropertiesUtils.load(CONFIGURATION_PROPERTIES);
			propertyValue = prop.getProperty(property);

		} catch (IOException ioe) {
			LOG.error("getSourceFolder exception for key:  " + property + "; Message: " + ioe.getMessage());
		}

		return propertyValue;
	}

}
