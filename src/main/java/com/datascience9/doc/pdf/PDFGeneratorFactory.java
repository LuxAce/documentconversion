package com.datascience9.doc.pdf;

import java.util.Optional;

import com.datascience9.doc.ConstantHelper;

public class PDFGeneratorFactory {
	public static Optional<PDFGenerator> getGenerator(String type) {
		if (ConstantHelper.STANDARD_PRACTICE.equals(type)) {
			return Optional.of(new Milstd962Xml2Pdf());
		} else {
			return Optional.empty();
		}
	}
}
