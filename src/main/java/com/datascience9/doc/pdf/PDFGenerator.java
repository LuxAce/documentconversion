package com.datascience9.doc.pdf;

import java.io.File;
import java.nio.file.Path;

public interface PDFGenerator {
	public void generate(Path input, Path output) throws Exception;
	public void generatePDF(File inputfile, File outputfile) throws Exception;
	
}
