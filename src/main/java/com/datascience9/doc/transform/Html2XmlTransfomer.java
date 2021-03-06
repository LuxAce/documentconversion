package com.datascience9.doc.transform;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import com.datascience9.doc.util.LoggingUtil;

public abstract class Html2XmlTransfomer {
	protected Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	protected STGroup stGroup;
	protected String templateFile;
	Map<String, String> analysisResult;
	
	public Html2XmlTransfomer(String templateFile) {
		this.templateFile = templateFile;
	}
	
	protected STGroup getStringTemplateFolder() {
		if (null == stGroup) {
			stGroup = new STGroupFile(templateFile, '$', '$');
			stGroup.registerRenderer(String.class, new StringRenderer());
		}
		return stGroup;
	}
	
	protected abstract void transformFile(Path inputFile) ;
	
	protected boolean validate(Path inputFile) {
		BufferedInputStream reader = null;
		try {
			reader = new BufferedInputStream(new FileInputStream(inputFile.toFile()));
			
			Document doc = Jsoup.parse(reader, "UTF-8", "", Parser.xmlParser());
			
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
