package com.datascience9.doc.transform;

import java.util.Optional;

import com.datascience9.doc.ConstantHelper;

public class Html2XmlTransformerFactory {
	public static Optional<Html2XmlTransfomer> getTransformer(String type) {
		if (ConstantHelper.STANDARD_PRACTICE.equals(type)) {
			return Optional.of(new MilStdTransfomer(ConstantHelper.STD_TEMPLATE_FILE));
		} else {
			return Optional.empty();
		}
	}
}
