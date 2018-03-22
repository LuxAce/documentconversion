package com.datascience9.doc;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.datascience9.doc.util.LoggingUtil;

public class MainClassHelper {
	static Logger logger = LoggingUtil.getDeveloperLogger(MainClassHelper.class.getName());
	
	/**
	 * "Parsing" stage of command-line processing demonstrated with
	 * Apache Commons CLI.
	 *
	 * @param options Options from "definition" stage.
	 * @param commandLineArguments Command-line arguments provided to application.
	 * @return Instance of CommandLine as parsed from the provided Options and
	 *    command line arguments; may be {@code null} if there is an exception
	 *    encountered while attempting to parse the command line options.
	 */
	public static CommandLine generateCommandLine(
	   final Options options, final String[] commandLineArguments)
	{
	   final CommandLineParser cmdLineParser = new DefaultParser();
	   CommandLine commandLine = null;
	   try
	   {
	      commandLine = cmdLineParser.parse(options, commandLineArguments);
	   }
	   catch (ParseException parseException)
	   {
	  	 logger.severe(
	           "Unrecognize option "
	         + Arrays.toString(commandLineArguments) );
       printUsage(options);
	   }
	   return commandLine;
	}
	 
	/**
	 * "Definition" stage of command-line parsing with Apache Commons CLI.
	 * @return Definition of command-line options.
	 */
	public static Options generateOptions()
	{
		final Option allOption = Option.builder("all")
	      .required(false)
	      .hasArg(false)
	      .desc("Run the transformation.")
	      .build();
	   
		final Option preprocessingOption = Option.builder("pre")
	      .required(false)
	      .hasArg(false)
	      .desc("Run the transformation.")
	      .build();
		
	   final Option analysisOption = Option.builder("analyze")
	      .required(false)
	      .hasArg(false)
	      .desc("Analyze the document and categorize the document types.")
	      .build();
	   
	   final Option metaAnalysisOption = Option.builder("meta")
		      .required(false)
		      .hasArg(false)
		      .desc("Analyze the document and categorize the document types.")
		      .build();
	   
	   final Option transformationOption = Option.builder("transform")
	      .required(false)
	      .hasArg(false)
	      .desc("Transform document to XML.")
	      .build();
	   
	   final Option pdfOption = Option.builder("pdf")
		      .required(false)
		      .hasArg(false)
		      .desc("Transform XML to PDF.")
		      .build();
	   
	   final Option propertiesOption = Option.builder("properties")
		      .required(false)
		      .hasArg(false)
		      .desc("Transform XML to PDF.")
		      .build();
	   
      final Option guiOption = Option.builder("gui")
		      .required(false)
		      .hasArg(false)
		      .desc("Launch GUI.")
		      .build();
      
	   final Option validateOption = Option.builder("validate")
		      .required(false)
		      .hasArg(true)
		      .desc("validate URL.")
		      .build();
	   
	   final Option helpOption = Option.builder("help")
		      .required(false)
		      .hasArg(false)
		      .desc("Show usage.")
		      .build();
	   
	   final Options options = new Options();
	   options.addOption(allOption);
	   options.addOption(preprocessingOption);
	   options.addOption(analysisOption);
	   options.addOption(metaAnalysisOption);
	   options.addOption(transformationOption);
	   options.addOption(pdfOption);
	   options.addOption(propertiesOption);
      options.addOption(guiOption);
	   options.addOption(validateOption);
	   options.addOption(helpOption);
	   return options;
	}
	
	/**
	 * Generate usage information with Apache Commons CLI.
	 *
	 * @param options Instance of Options to be used to prepare
	 *    usage formatter.
	 * @return HelpFormatter instance that can be used to print
	 *    usage information.
	 */
	public static void printUsage(final Options options)
	{
	   final HelpFormatter formatter = new HelpFormatter();
	   final String syntax = "Main";
	   System.out.println("\n=====");
	   System.out.println("USAGE");
	   System.out.println("=====");
	   final PrintWriter pw  = new PrintWriter(System.out);
	   formatter.printUsage(pw, 80, syntax, options);
	   pw.flush();
	}
	
}
