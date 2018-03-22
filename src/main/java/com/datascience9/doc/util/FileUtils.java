package com.datascience9.doc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stringtemplate.v4.ST;

public class FileUtils {

	private static Logger logger = LoggingUtil.getDeveloperLogger(FileUtils.class.getName());

	public static void writeStringToFile(Path path, String s) throws Exception {

		if (null == path)
			throw new IllegalArgumentException("Input file required!");

		BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));
		writer.write(s);
		writer.flush();
		writer.close();
	}

	public static void writeStringTemplateToFile(Path path, ST st) {
		if (null == path)
			throw new IllegalArgumentException("Input path required!");
		try {

			/**
			 * create new directory if parent dir not exists
			 */
			File test = path.toFile();
			if (!test.getParentFile().exists())
				test.getParentFile().mkdirs();

			BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));
			writer.write(st.render());
			writer.flush();
			writer.close();
		} catch (Exception ex) {
			logger.severe("Cannot writeStringTemplateToFile:[" + ex.getMessage() + "]");
		}
	}

	public static void writeListOfStringToFile(File filename, List<String> list) throws Exception {

		if (null == filename)
			throw new IllegalArgumentException("Input file required!");

		if (!filename.exists()) filename.getParentFile().mkdirs();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

		for (String s : list) {
			writer.write(s);
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}
	
	public static void writeMap2File(Path filename, Map<String, String> map) throws Exception {

		if (null == filename) throw new IllegalArgumentException("Input file required!");

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename.toFile()));

		for (String s : map.keySet()) {
			writer.write(s + "=" + map.get(s));
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	public static List<String> readFiletoListOfString(String path, Charset encoding) throws Exception {
		return Files.readAllLines(Paths.get(path), encoding);
	}
	
	public static Map<String, String> readFile2MapOfString(String path, Charset encoding) {
		Map<String, String> result = new HashMap<>();
		try {
			List<String> list = readFiletoListOfString(path, encoding);
			return (Map<String, String>)list.stream()
			.map(str -> str.split("="))
			.collect(Collectors.toMap(tokens -> tokens[0], tokens -> tokens[1]));
			
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot read file to map " + path, ex);
		}
		return result;
	}

	public static String readFile2String(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
