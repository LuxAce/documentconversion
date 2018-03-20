package com.datascience9.doc.util;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class StringTemplateUtils {
	
	public static STGroup getSTGroup(String templateFile) {
		if (!Files.exists(Paths.get(templateFile), LinkOption.NOFOLLOW_LINKS)) {
			throw new IllegalArgumentException("template file NOT found!!! [" + templateFile + "]");
		}
		STGroup stgroup = new STGroupFile(templateFile);
		stgroup.registerRenderer(Number.class, new NumberRenderer());
		return stgroup;
	}
}
