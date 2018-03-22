package com.datascience9.doc.preprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.datascience9.doc.util.DocumentConverterHelper;
import com.datascience9.doc.util.LoggingUtil;

public class HtmlSanitizer {
	Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	Logger developerLogger = LoggingUtil.getDeveloperLogger(this.getClass().getName());
   
   public HtmlSanitizer() {}
   
   public HtmlSanitizer(Logger logger) {
     this.logger = logger;
   }
   
	public void sanitizer(Path input, Path output) throws Exception {
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.forEach(dir -> {
			try {
				Files.list(dir)
				.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
						&& files.toFile().getName().endsWith(".html")
						&& !files.toFile().getName().equals("clean.html")
						&& !files.toFile().getName().equals("transform.html"))
				.forEach(path -> {
					cleanHtml(path, output);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public String cleanHtml(Path input, Path output) {
		developerLogger.info("Sanitizing .." + input);
		StringBuilder builder = new StringBuilder("<html>");
		try {
  		Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  		builder.append(doc.head());
  		
  		doc.body().children().stream().forEach(e -> cleanElement(e));
  		builder.append(doc.body());
  		builder.append("</html>");
  		
  		File outputDir = new File(output.toFile(), input.toFile().getParentFile().getName());
  		outputDir.mkdirs();
  		File outputFile = new File(outputDir, "clean.html");
  		DocConverter.writeStringToFile(outputFile.toPath(), builder.toString());
  		return outputFile.getAbsolutePath();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return null;
		}
	}
	
	private void cleanElement(Element e) {
		if ("p".equalsIgnoreCase(e.tagName())
				&& (StringUtils.isEmpty(e.text())
						&& e.childNodeSize() == 0)) {
			e.remove();
		} else if ("br".equalsIgnoreCase(e.tagName())) {
			e.remove();
		} else if ("p".equalsIgnoreCase(e.tagName())
				&& e.toString().contains("Source: https://assist.dla.mil")) {
			e.remove();
		} else if ("p".equalsIgnoreCase(e.tagName())
				&& e.toString().contains("Check the source to verify that this is the current version")) {
			e.remove();
		} else if ("p".equalsIgnoreCase(e.tagName())
				&& e.select("img").size() > 0) {
				e.select("img").first().append("</img>");
		} else {
			List<Element> children = e.children();
			children.stream().forEach(child -> cleanElement(child));
		}
	}
	
	public static void main(String[] s) throws Exception {
		new HtmlSanitizer()
		.sanitizer(Paths.get("/media/paul/workspace/pdftest/")
				, Paths.get("/media/paul/workspace/pdftest/"));
	}
}
