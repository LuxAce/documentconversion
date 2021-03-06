package com.datascience9.doc.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

import com.datascience9.doc.metaanalysis.MilStdDocMetaClass;
import com.datascience9.doc.util.LoggingUtil;

public abstract class HtmlAnalyzer {
	protected Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	protected Logger developerLogger = LoggingUtil.getDeveloperLogger(this.getClass().getName());
	protected MilStdDocMetaClass metaClass = null;
	
	public MilStdDocMetaClass getMetaClass() {
		return metaClass;
	}

	public HtmlAnalyzer() {
		metaClass = new MilStdDocMetaClass();
	}
	
	public void sanitize(Path input, Path output) throws Exception {
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.forEach(dir -> {
			try {
				Files.list(dir)
				.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
						&&  files.toFile().getName().equals("clean.html"))
				.forEach(path -> collectMeta(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		afterProcess();
	}
	
	public void sanitizeFile(Path input, Path output) throws Exception {
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.forEach(dir -> {
			try {
				Files.list(dir)
				.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
						&&  files.toFile().getName().equals("clean.html"))
				.forEach(path -> collectMeta(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		afterProcess();
	}
	
	public abstract void collectMeta(Path input);
	
	protected void afterProcess() {}
	
	public abstract void collectStatistics(Document doc);
}
