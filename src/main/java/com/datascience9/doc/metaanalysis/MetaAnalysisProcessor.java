package com.datascience9.doc.metaanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.analysis.HtmlAnalyzer;
import com.datascience9.doc.util.FileUtils;
import com.datascience9.doc.util.LoggingUtil;
import java.util.logging.Level;

public class MetaAnalysisProcessor {
  protected Logger logger = LoggingUtil.getDeveloperLogger(this.getClass().getName());
  Map<String, String> analysisResult;
	
  public MetaAnalysisProcessor() {}
  
  public MetaAnalysisProcessor(Logger logger) {
    this.logger = logger;
  }
  
	public void analyze(Path input, Path output) throws Exception {
		analysisResult = FileUtils.readFile2MapOfString(ConstantHelper.ANALYSIS_RESULT, Charset.defaultCharset());
		
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.filter(path -> analysisResult.get(path.toFile().getName()) != null)
		.filter(path -> !"UNKNOWN".equals(analysisResult.get(path.toFile().getName())))
		.forEach(dir -> {
			try {
				Files.list(dir)
				.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
						&&  files.toFile().getName().equals("clean.html"))
				.forEach(path -> {
					Optional<HtmlAnalyzer>  option = 
							MetaAnalysisFactory.getAnalyzer(analysisResult.get(path.getParent().toFile().getName()));
					
					if (option.isPresent()) {
						option.get().collectMeta(path);
					}
				});
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "ERROR analyze", ex);
			}
		});
	}
}
