package com.datascience9.doc.pdf;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Test;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.preprocessing.Doc2Html;
import com.datascience9.doc.preprocessing.HtmlSanitizer;
import com.datascience9.doc.transform.MilStdTransfomer;

public class TestConvertXML2PDF {
	@Test
	public void testGeneratePDF() throws Exception {
		PDFGenerator generator = null;
		Optional<PDFGenerator> option = PDFGeneratorFactory.getGenerator(ConstantHelper.STANDARD_PRACTICE);
		if (!option.isPresent()) {
			System.out.println("Invalid PDF generator: STANDARD_PRACTICE");
			return;
		}
		generator = option.get();
		Path input = Paths.get("./test/MIL-STD-101C.doc");
		Path output = Paths.get("./test/MIL-STD-101C/");
		
		Doc2Html converter = new Doc2Html();
		converter.extractTextFromFile(input, output);
		
		new HtmlSanitizer()
		.cleanHtml(Paths.get("./test/MIL-STD-101C/MIL-STD-101C.html"));
		
		MilStdTransfomer transformer = new MilStdTransfomer("./src/main/templates/std962.stg");
		transformer.transformFile(Paths.get("./test/MIL-STD-101C/clean.html"));
	
		generator.generatePDF(new File("./test/MIL-STD-101C/result.xml"), new File("./test/MIL-STD-101C.pdf"));
	}
}
