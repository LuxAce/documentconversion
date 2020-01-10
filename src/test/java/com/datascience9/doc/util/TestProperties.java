package com.datascience9.doc.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.datascience9.doc.ConfigurationHelper;

public class TestProperties {
	
	@Test
	public void testGetSourcePath() {
		System.out.println(ConfigurationHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}
	
	@Test
	public void testReadProperties() {
		assertEquals("expect to equals", "input", ConfigurationHelper.getInputDir());
		assertEquals("expect to equals", "output", ConfigurationHelper.getOutputDir());
		assertNotEquals("expect Not equals", "test3", ConfigurationHelper.getOutputDir());
	}
	
//	@Test
//	public void testUpdateProperties() {
//		String prev = ConfigurationHelper.getInputDir();
//		String test = "test10";
//		ConfigurationHelper.updateInputDir(test);
//		assertEquals("expect to equals", test, ConfigurationHelper.getInputDir());
//		ConfigurationHelper.updateInputDir(prev);
//	}
//	
//	@Test
//	public void testUpdateOutputDir() {
//		String prev = ConfigurationHelper.getOutputDir();
//		String test = "test11";
//		ConfigurationHelper.updateOutputDir(test);
//		assertEquals("expect to equals", test, ConfigurationHelper.getOutputDir());
//		ConfigurationHelper.updateOutputDir(prev);
//	}
}
