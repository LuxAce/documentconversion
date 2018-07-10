package com.datascience9.doc.transform;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import com.datascience9.doc.ConstantHelper;

public class TestConvertHtml2Xml {

	@Test
	public void testconvertHtml2Xml2() {
		Optional<Html2XmlTransfomer> option = Html2XmlTransformerFactory.getTransformer(ConstantHelper.STANDARD_PRACTICE);
		if (!option.isPresent()) return;
		
		Html2XmlTransfomer transformer = option.get();
		transformer.transformFile(Paths.get("./test/MIL-STD-101C/clean.html"));
	
	}
	
	@Test
	public void testconvertHtml2Xml() {
		Optional<Html2XmlTransfomer> option = Html2XmlTransformerFactory.getTransformer(ConstantHelper.STANDARD_PRACTICE);
		if (!option.isPresent()) return;
		
		Html2XmlTransfomer transformer = option.get();
		transformer.transformFile(Paths.get("./test/MIL-STD-171F/clean.html"));
	
	}
}
