package com.datascience9.doc.parse;

import java.nio.file.Paths;

import org.junit.Test;

import com.datascience9.doc.preprocessing.HtmlSanitizer;

public class TestCleanHtml {
	@Test
	public void testCleanHtml() throws Exception {
		new HtmlSanitizer()
		.cleanHtml(Paths.get("./test/MIL-STD-101C/MIL-STD-101C.html"));
	}
}
