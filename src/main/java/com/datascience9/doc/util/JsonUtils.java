package com.datascience9.doc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonUtils {
	private final static Logger logger = Logger.getLogger(JsonUtils.class.getName());
	public static void write2File(Path output, Object o) {
		try {
  		if (!Files.exists(output, LinkOption.NOFOLLOW_LINKS)) {
  			Files.createDirectories(output.getParent(), FileUtils.generateStandardFileAttributes());
  		}
  		Writer writer = new FileWriter(output.toFile());
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    gson.toJson(o, writer);
	    writer.flush();
	    writer.close();
		} catch (Exception ex) {
			logger.severe("Unable to write object to json [" + ex.getMessage() + "]");
		}
	}
	
	public static String write2String(Object o) {
		try {
	    Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	    return gson.toJson(o);
		} catch (Exception ex) {
			logger.severe("Unable to write object to json [" + ex.getMessage() + "]");
			return "";
		}
	}
	
	public static JsonObject parseJson(Path inputFile) {
		try {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(new BufferedReader(new FileReader(inputFile.toFile())), JsonObject.class);
		} catch (Exception ex) {
			logger.severe("Unable to read from [" + ex.getMessage() + "]");
			return null;
		}
	}
	
}
