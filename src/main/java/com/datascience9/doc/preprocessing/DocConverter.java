package com.datascience9.doc.preprocessing;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.datascience9.doc.util.LoggingUtil;

public abstract class DocConverter {
	protected Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	protected Logger developerLogger = LoggingUtil.getDeveloperLogger(this.getClass().getName());
	
	/**
	 * convert MS Word to HTML
	 * @param input dir
	 * @param output dir
	 * @throws Exception
	 */
	public void extract(Path input, Path output) throws Exception {
		Files.list(input)
		.filter(f -> f.toFile().getName().endsWith(".doc"))
		.forEach(f -> extractText(f, output));
	}
	
	/**
	 * extract text from a word document.
	 * @param input dir.
	 * @param output -  Outputdir = output + input
	 */
	public abstract void extractText(Path input, Path output );
	
	/**
	 * Convert MS word document to an html file
	 * @param input file
	 * @param output dir
	 */
	public abstract void extractTextFromFile(Path input, Path output );
	
	/**
	 * Write string to a file.
	 * @param s - input string.
	 * @param path - output file
	 */
	public static void writeStringToFile(Path path, String s) throws Exception {

		if (null == path)
			throw new IllegalArgumentException("Input file required!");

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
		    new FileOutputStream(path.toFile()), "UTF-8"))) {
			writer.write(s);
			writer.flush();
		}
	}
}
