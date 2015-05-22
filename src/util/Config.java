package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Config {
	
	private static Config instance;
	private static final String FILE = "config.properties";
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	private Properties properties;
	
	
	private Config () {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE);
 
		if (inputStream != null) {
			try {
				properties = new Properties();
				properties.load(inputStream);
			} catch (IOException e) {
				logger.info("property file '" + FILE + "' is not following .properties file syntax");
			}
		} else {
			logger.info("property file '" + FILE + "' not found in the classpath");
		}
	}

	public static Config getInstance () {
		if (Config.instance == null) {
			Config.instance = new Config();
		}
		return Config.instance;
	}
	
	public String getValue(String key) {
		return properties.getProperty(key);
	}

}
