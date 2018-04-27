package it.epocaricerca.geologia.ejb.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class DatabasePropertiesUtil {

	private static String PROPERTIES_FILE = "database.properties";
	private static org.apache.commons.configuration.Configuration configuration;
	private static Logger logger = Logger.getLogger(SupportoMareggiatePropertiesUtil.class.getName());
	
	private static org.apache.commons.configuration.Configuration getConfiguration() {
		if (configuration == null) {
			try {
				configuration = new PropertiesConfiguration(PROPERTIES_FILE);
			} catch (ConfigurationException e) {
				logger.error(e.getMessage());
			}
		}
		return configuration;
	}
	
	public static String getGeomFromText() {
		return getConfiguration().getString("database.geomFromText");
	}
	
	// AGGIUNTA MARUCCI:
	public static String getGeomTransform() {
		return getConfiguration().getString("database.geomTransform");
	}
	public static String getGeomSrid3857() {
		return getConfiguration().getString("database.geomSrid3857");
	}
	public static String getGeomSrid32632() {
		return getConfiguration().getString("database.geomSrid32632");
	}
	// FINE
	
	public static String getNextVal() {
		return getConfiguration().getString("database.nextval");
	}
	
	public static String getTextFromGeom() {
		return getConfiguration().getString("database.textFromGeom");
	}
	
	
	
	
}
