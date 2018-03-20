package com.datascience9.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.datascience9.doc.util.LoggingUtil;

public class ConfigurationHelper {
	private static Logger logger = LoggingUtil.getDeveloperLogger(ConfigurationHelper.class.getName());
	private static final String INPUTDIR = "inputdir";
	private static final String OUTPUTDIR = "outputdir";
	
	private static void createPropertyFile() {
		Properties props = new Properties();
		try (FileOutputStream out = new FileOutputStream("application.properties")) {
  		props.setProperty(INPUTDIR, "input");
  		props.setProperty(OUTPUTDIR, "output");
  		new File("./input").mkdirs();
  		new File("./output").mkdirs();
  		props.store(out, null);
  		out.close();
  	} catch (Exception ex) {
  		logger.log(Level.SEVERE, "cannot create properties file", ex);
  	}
	}
	
	public static Properties getProperties() {
		if (!(new File("./application.properties").exists())) {
			createPropertyFile();
		}
		
		Properties props = new Properties();
			try (FileInputStream in = new FileInputStream("application.properties")) {
	  		props.load(in);
	  		in.close();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "cannot load properties file", ex);
			}
		return props;
	}
	
	public static void showProperties() {
		Properties props = getProperties();
		Map<String, String> map = (Map)props;
		System.out.println("Application properties:");
		map.keySet().stream().forEach(key -> System.out.println(key + ":" + map.get(key)));
	}
	
	public static String getInputDir() {
		return getProperties().getProperty("inputdir");
	}
	
	public static String getOutputDir() {
		return getProperties().getProperty("outputdir");
	}
	
	public static void updateInputDir(String dir) {
		updateProperty(INPUTDIR, dir);
	}
	
	public static void updateOutputDir(String dir) {
		updateProperty(OUTPUTDIR, dir);
	}
	
	private static void updateProperty(String property, String value) {
		Properties props = getProperties();
		try (FileOutputStream out = new FileOutputStream("application.properties")) {
  		props.setProperty(property, value);
  		props.store(out, null);
  		out.close();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "cannot update properties file", ex);
		}
	}
}
