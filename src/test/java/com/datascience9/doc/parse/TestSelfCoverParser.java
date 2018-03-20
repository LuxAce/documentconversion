package com.datascience9.doc.parse;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import com.datascience9.doc.transform.STD962DSelfCoverParser;
import com.datascience9.doc.transform.SelfCoverParser;

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
	
//	@Test 
	public void testSelfCoverParserDoc15() throws Exception {
  	Path input = Paths.get("/media/paul/workspace/pdftest/334566B59C9340B78ED202A31F4E7B15/clean.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
  	parser.parse(doc.body().select("div").first());
  	
  	assertEquals("expect measurement", "NOT MEASUREMENT SENSITIVE", parser.getSelfCover().getSystemId());
  	assertEquals("expect to have id", "MIL-STD-171F", parser.getSelfCover().getRevision().getId());
  	assertEquals("expect to have image", "DEPARTMENT OF DEFENSE", parser.getSelfCover().getHeading().get(0));
  	assertEquals("expect to have image", "s208.JPEG", parser.getSelfCover().getImageStr());
  	System.out.println(parser.getSelfCover());
	}
	
	@Test 
	public void testSelfCoverParserDoc67() throws Exception {
  	Path input = Paths.get("/media/paul/workspace/pdftest/170035D8112C47D2ACD37519BE4E9967/clean.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
  	parser.parse(doc.body().select("div").first());
  	System.out.println(parser.getSelfCover());
  	assertEquals("expect measurement", "INCH-POUND", parser.getSelfCover().getSystemId());
  	assertEquals("expect to have id", "MIL-STD-202H", parser.getSelfCover().getRevision().getId());
  	assertEquals("expect to have image", "DEPARTMENT OF DEFENSE", parser.getSelfCover().getHeading().get(0));
  	assertEquals("expect to have image", "s193.JPEG", parser.getSelfCover().getImageStr());
  	
	}
	//15E602AA7958426ABB0C8036FE73B538
	@Test 
	public void testSelfCoverParserDoc38() throws Exception {
  	Path input = Paths.get("/media/paul/workspace/pdftest/15E602AA7958426ABB0C8036FE73B538/clean.html");
  	Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  	STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
  	parser.parse(doc.body().select("div").first());
  	System.out.println(parser.getSelfCover());
  	assertEquals("expect measurement", "INCH---POUND", parser.getSelfCover().getSystemId());
  	assertEquals("expect to have id", "MIL---STD---101C", parser.getSelfCover().getRevision().getId());
  	assertEquals("expect to have image", "DEPARTMENT OF DEFENSE", parser.getSelfCover().getHeading().get(0));
  	assertEquals("expect to have image", "s204.JPEG", parser.getSelfCover().getImageStr());
  	
	}
}
