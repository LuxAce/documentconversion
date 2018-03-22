package com.datascience9.doc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.datascience9.doc.analysis.DocumentTypeAnalyzer;
import com.datascience9.doc.gui.MainJFrame;
import com.datascience9.doc.metaanalysis.MetaAnalysisProcessor;
import com.datascience9.doc.metaanalysis.MilStdDocumentAnalyzer;
import com.datascience9.doc.pdf.Milstd962Xml2Pdf;
import com.datascience9.doc.pdf.PDFGenerator;
import com.datascience9.doc.pdf.PDFGeneratorProcessor;
import com.datascience9.doc.preprocessing.Doc2Html;
import com.datascience9.doc.preprocessing.HtmlSanitizer;
import com.datascience9.doc.transform.Html2XmlProcessor;
import com.datascience9.doc.transform.MilStdTransfomer;
import com.datascience9.doc.util.LoggingUtil;
import com.datascience9.doc.util.URLUtil;

public class MainClass {
	static Logger logger = LoggingUtil.getLogger(MainClass.class.getName());
	
	public static void main(String[] args) {
		run(args);
	}
	
	private static void run(String[] args) {
	// create the Options
    Options options = MainClassHelper.generateOptions();
		
    // parse the command line arguments
    CommandLine line = MainClassHelper.generateCommandLine(options, args);

    Path input = Paths.get(ConfigurationHelper.getInputDir());
    Path output = Paths.get(ConfigurationHelper.getOutputDir());

    if (null == line) {
      logger.severe("Invalid input!!!. Please try again");
      return;
    }
	    
	  	if( line.hasOption( "all") ) {
	  		extractDoc2Html(input, output);
	  		logger.info("the extraction is complete... and analysis phase is starting ...");
	  		analyze(output, output);
	  		logger.info("the analysis is complete... and meta analysis phase is starting ...");
	  		analyzeMeta(output, output);
	  		logger.info("the meta analysis is complete... and transformation phase is starting ...");
	  		transform2XML(output, output);
	  		logger.info("the transformation is complete... and PDf generation phase is starting ...");
	  		generatePDF(output, output);
	  		logger.info("the program is complete.  Please view the output folder");
	  	} else if (line.hasOption( "pre") ) {
	  		extractDoc2Html(input, output);
	  		logger.info("the pre-processing phase is complete.  Please view the output folder");
	  	} else if (line.hasOption( "analyze") ) {
	  		analyze(output, output);
	  		logger.info("the Analysis phase is complete.  Please view the output folder");
	  	} else if (line.hasOption( "meta") ) {
	  		analyzeMeta(output, output);
	  		logger.info("the Meta Analysis phase is complete.  Please view the output folder");
	  	} else if (line.hasOption( "transform") ) {
	  		transform2XML(output, output);
	  		logger.info("The transforamtion to XML phase is complete.  Please view the output folder");
	  	} else if (line.hasOption( "pdf") ) {
	  		generatePDF(output, output);
	  		logger.info("The PDF generation phase is complete.  Please view the output folder");
	  	} else if (line.hasOption( "properties") ) {
	  		ConfigurationHelper.showProperties();
	  	} else if (line.hasOption( "gui") ) {
	  		MainJFrame.launch();
	  	} 
	  	else if (line.hasOption( "validate") && line.getOptionValue( "validate") != null) {
	  		String urlStr = line.getOptionValue( "validate");
	  		boolean result = URLUtil.checkReference(urlStr);
	  		if (result) logger.info("Valid URL " + urlStr);
	  		else logger.info("Invalid URL " + urlStr);
	  	} else if (line.hasOption( "help") ) {
	  		MainClassHelper.printUsage(options);
	  	}
	}
	
	public static void analyze(Path input, Path output) {
		DocumentTypeAnalyzer analyzer = new DocumentTypeAnalyzer();
		try {
			analyzer.sanitizer(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot extract Word 2 HTML for " + input, ex);
		}
	}
	
	public static void analyzeMeta(Path input, Path output) {
		MetaAnalysisProcessor processor = new MetaAnalysisProcessor();
		try {
			processor.analyze(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot analyze meta from " + input , ex);
		}
	}
	
	public static void extractDoc2Html(Path input, Path output) {
		Doc2Html extractor = new Doc2Html();
		try {
			extractor.extract(input, output);
			new HtmlSanitizer().sanitizer(output, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot extract Word 2 HTML " , ex);
		}
	}
	
	public static void transform2XML(Path input, Path output) {
		Html2XmlProcessor processor = new Html2XmlProcessor();
		try {
			processor.transform(output, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot trasform 2 XML " , ex);
		}
	}
	
	public static void generatePDF(Path input, Path output) {
		PDFGeneratorProcessor processor = new PDFGeneratorProcessor();
		try {
			processor.generate(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot generate PDF for " , ex);
		}
	}
   
   public static void analyze(Path input, Path output, Logger logger) {
		DocumentTypeAnalyzer analyzer = new DocumentTypeAnalyzer(logger);
		try {
			analyzer.sanitizer(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot extract Word 2 HTML for " + input, ex);
		}
	}
	
	public static void analyzeMeta(Path input, Path output, Logger logger) {
		MetaAnalysisProcessor processor = new MetaAnalysisProcessor(logger);
		try {
			processor.analyze(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot analyze meta from " + input , ex);
		}
	}
	
	public static void extractDoc2Html(Path input, Path output, Logger logger) {
		Doc2Html extractor = new Doc2Html(logger);
		try {
			extractor.extract(input, output);
			new HtmlSanitizer(logger).sanitizer(output, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot extract Word 2 HTML " , ex);
		}
	}
	
	public static void transform2XML(Path input, Path output, Logger logger) {
		Html2XmlProcessor processor = new Html2XmlProcessor(logger);
		try {
			processor.transform(output, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot trasform 2 XML " , ex);
		}
	}
	
	public static void generatePDF(Path input, Path output, Logger logger) {
		PDFGeneratorProcessor processor = new PDFGeneratorProcessor(logger);
		try {
			processor.generate(input, output);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot generate PDF for " , ex);
		}
	}
}
