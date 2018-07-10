package com.datascience9.doc.parse;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.datascience9.doc.transform.STD962DSelfCoverParser;
import com.datascience9.doc.transform.SelfCoverParser;
import com.datascience9.doc.util.DocumentConverterHelper;

public class TestSelfCoverParser {
	
	@Test 
	public void testDatePattern() throws Exception {
		String test = "31 May 2011";
		assertEquals("expect pass", true, SelfCoverParser.isDate(test));
		
		test = "19 November 2001";
		assertEquals("expect pass", true, SelfCoverParser.isDate(test));
	}
	
	@Test 
	public void testIsChange() throws Exception {
		String test = "w/CHANGE ";
		assertEquals("expect pass", true, SelfCoverParser.isChanged(test));
	}
	
	@Test
	public void testStripTagFromHtml() throws Exception {
		Path input = Paths.get("./test/parsing/MIL-STD-1791C.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	
  	List<String> tds = STD962DSelfCoverParser.getAMSC(doc.body().select("div").first());
  	assertEquals("found the ASM", "AMSC N/A", tds.get(0));
  	assertEquals("found the ASM", "FSC 1510", tds.get(1));
	}
	
	@Test
	public void testParseMILSTD1791C() throws Exception {
		Path input = Paths.get("./test/parsing/MIL-STD-1791C.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
  	parser.parse(doc.body().select("div").first());
  	assertEquals("expect to have id", "MIL-STD-1791C", parser.getSelfCover().getRevision().getId());
  	assertEquals("expect to have title", 2, parser.getSelfCover().getTitle().size());
  	assertEquals("expect to have title", "DESIGNING FOR INTERNAL AERIAL DELIVERY", parser.getSelfCover().getTitle().get(0));
  	assertEquals("expect to have title", "IN FIXED WING AIRCRAFT", parser.getSelfCover().getTitle().get(1));
	}
	
	@Test
	public void testParseMILSTD101C() throws Exception {
		Path input = Paths.get("./test/MIL-STD-101C/clean.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
  	parser.parse(doc.body().select("div").first());
  	assertEquals("expect to have id", "MIL---STD---101C", parser.getSelfCover().getRevision().getId());
  	assertEquals("expect to have id", "INCH---POUND", parser.getSelfCover().getSystemId());
  	assertEquals("expect to have title", 2, parser.getSelfCover().getTitle().size());
  	assertEquals("expect to have title", "COLOR CODE FOR PIPELINES AND", parser.getSelfCover().getTitle().get(0));
  	assertEquals("expect to have title", "FOR COMPRESSED GAS CYLINDERS", parser.getSelfCover().getTitle().get(1));
	}
}
