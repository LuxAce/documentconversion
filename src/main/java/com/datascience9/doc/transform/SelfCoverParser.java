package com.datascience9.doc.transform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

public abstract class SelfCoverParser {
	public static final String[] formatter = {
			"dd MMM yyyy",
			"dd MMMM yyyy",
	};
	
	public final static String[] SYSTEM_IDENT = {
			"NOT MEASUREMENT",
			"INCH-",
			"METRIC",
	};
	
	public final static String[] CHANGES = 
		{
			"AMENDMENT",
			"W/CHANGE",
			"W/AMENDMENT",
		};
	
	public final static String[] STANDARD = {
			"INTERFACE STANDARD",
			"DESIGN CRITERIA STANDARD",
			"TEST METHOD STANDARD",
			"STANDARD PRACTICE",
			"MANUFACTURING PROCESS STANDARD",
	};
	
	public final static String[] DEPARTMENT_STANDARD = {
			"DEPARTMENT OF DEFENSE",
	};
	
	public final static String[] DISTRIBUTION = {
			"DISTRIBUTION STATEMENT",
	};
	
	public final static String[] PERFORMANCESPEC = {
			"PERFORMANCE SPECIFICATION SHEET",
	};
	
	public final static String[] AMS = {
			"AMS"
	};
	
	public final static String[] FSC = {
			"FSC",
			"FSG",
	};
	
	public final static String[] IMAGES = {
			".JPEG",
			".JPG",
			".PNG",
	};
	
	protected STGroup stGroup;
	protected String templateFile;
	
	public abstract void parse(org.jsoup.nodes.Element html);
	
	
	public String parseForKeyWord(Deque<String> queue, String keyword) {
		final Deque<String> local = new ArrayDeque<>(queue);
		while (!local.isEmpty()) {
			String token = local.pop();
			if (token.startsWith(keyword)) return token;
		}
		return null;
	}
	
	public String parseEqualArrayKeyWord(Deque<String> queue, String[] keyword) {
		final Deque<String> local = new ArrayDeque<>(queue);
		while (!local.isEmpty()) {
			String token = local.pop();
			if (Arrays.stream(keyword).anyMatch(str -> str.equals(token))) return token;
		}
		return null;
	}
	
	public String parseStartsWithArrayKeyWord(Deque<String> queue, String[] keyword) {
		final Deque<String> local = new ArrayDeque<>(queue);
		while (!local.isEmpty()) {
			String token = local.pop();
			if (Arrays.stream(keyword).anyMatch(str -> token.startsWith(str))) return token;
		}
		return null;
	}
	
	public String parseEndsWithArrayKeyWord(Deque<String> queue, String[] keyword) {
		final Deque<String> local = new ArrayDeque<>(queue);
		while (!local.isEmpty()) {
			String token = local.pop();
			if (Arrays.stream(keyword).anyMatch(str -> token.toUpperCase().endsWith(str))) return token;
		}
		return null;
	}
	
	public static boolean isDate(String str) {
		if (StringUtils.isEmpty(str)) return false;
		Optional<Boolean> result = Arrays.stream(formatter)
		.map(dateStr -> {
  		try {
  			DateFormat fmt = new SimpleDateFormat(dateStr, Locale.US);
        Date date = fmt.parse(str);
        return true;
  		} catch (Exception ex) { System.err.println("invalid date " + dateStr); }
  		return false;
		}).findFirst();
		return result.get();
	}
	
	public static boolean isIdentifier(String str) {
		return StringUtils.isNotEmpty(str) && str.startsWith("MIL");
	}
	
	public static boolean isImage(String str) {
		return Arrays.stream(IMAGES).anyMatch(x -> str.toUpperCase().endsWith(x));
	}
	
	public static boolean isChanged(String str) {
		return (Arrays.stream(CHANGES).anyMatch(x -> str.toUpperCase().startsWith(x)));
	}
	
	public static boolean isSection6(String str) {
		return str.contains("(See 6");
	}
	
	public static boolean isStandard(String str) {
		return (Arrays.stream(STANDARD).anyMatch(x -> str.contains(x)));
	}
	
	public static boolean isMeasurementSystemId(String str) {
		return (Arrays.stream(SYSTEM_IDENT).anyMatch(x -> str.startsWith(x)));
	}
	
	public static boolean isDepartmentStandard(String str) {
		return (Arrays.stream(DEPARTMENT_STANDARD).anyMatch(x -> str.contains(x)));
	}
	
	public static boolean isDistribution(String str) {
		return (Arrays.stream(DISTRIBUTION).anyMatch(x -> str.contains(x)));
	}
	
	protected STGroup getStringTemplateFolder() {
		if (null == stGroup) {
			stGroup = new STGroupFile(templateFile, '$', '$');
			stGroup.registerRenderer(String.class, new StringRenderer());
		}
		return stGroup;
	}
}
