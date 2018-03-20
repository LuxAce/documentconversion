package com.datascience9.doc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class GeneralUtils {

	public static String getFileNameWithoutExtension(String oldName) {
		int index = oldName.lastIndexOf(".");
		if (index > 0)
			return oldName.substring(0, index);
		else
			return oldName;
	}

	public static String buildFieldNameFromCobolName(String oldName) {
		String[] tokens = oldName.split("-");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			builder.append(tokens[i].substring(0, 1).toLowerCase() + tokens[i].substring(1));
		}
		return builder.toString();
	}

	public static String buildClassNameFromCobolName(String oldName) {
		oldName = oldName.replace("-", "_");
		oldName = oldName.replace("_", "_");
		String[] tokens = oldName.split("_");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			builder.append(tokens[i].substring(0, 1).toUpperCase() + tokens[i].substring(1).toLowerCase());
		}
		return builder.toString();
	}

	public static String buildFieldNameFromClassName(String oldName) {
		if (oldName.length() == 1)
			return oldName.toLowerCase();
		return oldName.substring(0, 1).toLowerCase() + oldName.substring(1);
	}

	public static boolean isStringNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}
}
