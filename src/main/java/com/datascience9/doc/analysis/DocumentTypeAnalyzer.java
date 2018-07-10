package com.datascience9.doc.analysis;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.util.FileUtils;
import java.util.logging.Logger;

public class DocumentTypeAnalyzer extends HtmlAnalyzer {
	Map<String, String> analysis = new HashMap<>();
	
   public DocumentTypeAnalyzer() {}
   
   public DocumentTypeAnalyzer(Logger logger) {
     this.logger = logger;
   }
   
	@Override
	public void collectMeta(Path input) {
		developerLogger.log(Level.FINE, "Start categorizing .." + input.getParent().toFile().getName());
		
		try {
  		Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  		Element body = doc.body();
  		Elements divs = body.select("div");
  		
  		int type = -1;
  		for (int i = 0; i < Math.min(2, divs.size()) ; i++) {
  			Elements paragraphs = divs.get(i).select("p");
    		for (Element p : paragraphs ) {
    			if (p.text().replace(" ", "").contains(ConstantHelper.STANDARD_PRACTICE)) {
      			type = 0;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.DEFENSE_SPECIFICATION)) {
      			type = 1;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.ISA)) {
      			type = 2;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.DID)) {
      			type = 3;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.DEFENSE_HANDBOOK)) {
      			type = 4;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.FEDERAL_SPECIFICATION)) {
      			type = 5;
      		} else if (p.text().replace(" ", "").toUpperCase().contains(ConstantHelper.PERFORMANCE_SPECIFICATION)) {
      			type = 6;
      		}
    		}
  		}

  		if (type == -1) analysis.put(input.getParent().toFile().getName(), "UNKNOWN");
  		else {
  			analysis.put(input.getParent().toFile().getName(), ConstantHelper.TYPES[type]);
  		}
  		
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	@Override
	protected void afterProcess() {
		write2File(Paths.get(ConstantHelper.ANALYSIS_RESULT), analysis);
	}
	
	private void write2File(Path output, Map<String, String> analysis) {
		try {
			FileUtils.writeMap2File(output, analysis);
		} catch (Exception ex) {
			developerLogger.log(Level.SEVERE, "Cannot write analysis result to file " + output, ex);
		}
	}

	@Override
	public void collectStatistics(Document doc) {
		// TODO Auto-generated method stub
		
	}

}
