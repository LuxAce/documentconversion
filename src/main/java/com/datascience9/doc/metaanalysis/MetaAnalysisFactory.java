package com.datascience9.doc.metaanalysis;

import java.util.Optional;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.analysis.HtmlAnalyzer;

public class MetaAnalysisFactory {
	public static Optional<HtmlAnalyzer> getAnalyzer(String type) {
		if (ConstantHelper.STANDARD_PRACTICE.equals(type)) {
			return Optional.of(new MilStdDocumentAnalyzer());
		} else {
			return Optional.empty();
		}
	}
}
