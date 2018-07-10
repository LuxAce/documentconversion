package com.datascience9.doc.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestRegexMatcher {
	@Test
	public void testRegexMatch() throws Exception {
		String regex = "^[A-Z0-9]{1,2}[.](.*)";
		String regex1 = "^[A-Z]-.*";
		String regex2 = "^[VXI]{1,3}[.]*.*";
		assertTrue("1.1.2 blah blah".matches(regex));
		assertTrue("A1.1".matches(regex));
		assertTrue("2A.".matches(regex));
		assertTrue("A-I".matches(regex1));
		assertTrue("A-1".matches(regex1));
		assertFalse("A1".matches(regex1));
		assertTrue("1.".matches(regex));
		assertTrue("9.".matches(regex));
		assertTrue("11.".matches(regex));
		assertTrue("1.1".matches(regex));
		assertTrue("V.".matches(regex2));
		assertTrue("V.".matches(regex2));
		assertTrue("VI.".matches(regex2));
		assertTrue("XI.".matches(regex2));
		assertTrue("V".matches(regex2));
		assertTrue("VI".matches(regex2));
		assertTrue("A-III.".matches(regex1));
		assertTrue("5.13.11.3".matches(regex));
		
		
		
		
	}
}
