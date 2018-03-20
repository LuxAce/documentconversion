package com.datascience9.doc.util;

import java.util.logging.Logger;

import org.junit.Test;

public class TestLogging {
	@Test
	public void testLoggingConfig() {
		System.out.println(System.getProperty("user.home"));
		System.setProperty("java.util.logging.config.file", "logging.properties");
		System.out.println(System.getProperty("java.util.logging.config.file"));//, "./logging.properties");
		
		Logger logger = LoggingUtil.getLogger(this.getClass().getName());
		
		logger.info("this is a logging test");
		

	}
}
