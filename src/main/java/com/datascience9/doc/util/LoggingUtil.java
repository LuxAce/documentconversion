package com.datascience9.doc.util;

import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingUtil {
	private static final String HANDLE_FILE_NAME = "documentconversion.log";
	private static final String DEVELOPER = "developer.log";
	private static FileHandler fileHandler;
	private static FileHandler developerFileHandler;
	static {
		try {
			
//			System.setProperty("java.util.logging.SimpleFormatter.format","%5$s %n");
			fileHandler = new FileHandler(HANDLE_FILE_NAME, true);
			fileHandler.setFormatter(new SimpleFormatter());
			
			developerFileHandler = new FileHandler(DEVELOPER, true);
			developerFileHandler.setFormatter(new SimpleFormatter());

		} catch (Exception ex) {
			System.err.println("log file NOT FOUND:" + HANDLE_FILE_NAME);
		}
	}
	
	private static FileHandler getFileHandler() {
		Objects.requireNonNull(fileHandler);
		return fileHandler;
	}
	
	private static FileHandler getDeveloperFileHandler() {
		Objects.requireNonNull(developerFileHandler);
		return developerFileHandler;
	}
	
//	private static FileHandler getAnalysisFileHandler() {
//		Objects.requireNonNull(analysisFileHandler);
//		return analysisFileHandler;
//	}
	
	public static Logger getLogger(String className) {
		Logger logger = Logger.getLogger(className);
		logger.addHandler(getFileHandler());
		return logger;
	}
	
	public static Logger getDeveloperLogger(String className) {
		Logger logger = Logger.getLogger(className);
		logger.addHandler(getDeveloperFileHandler());
		return logger;
	}
	
//	public static Logger getAnalysisLogger(String className) {
//		Logger logger = Logger.getLogger(className);
//		logger.addHandler(getAnalysisFileHandler());
//		return logger;
//	}
}
