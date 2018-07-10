package com.datascience9.doc.transform;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.util.FileUtils;
import com.datascience9.doc.util.LoggingUtil;

public class Html2XmlProcessor {
	protected Logger logger = LoggingUtil.getDeveloperLogger(this.getClass().getName());
	Map<String, String> analysisResult;
	
   public Html2XmlProcessor() {}
   
   public Html2XmlProcessor(Logger logger) {
     this.logger = logger;
   }
	
   /**
    * convert HTML to XML
    * @param input dir
    * @param output dir
    * @throws Exception
    */
	public void transform(Path input, Path output) throws Exception {
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
					Optional<Html2XmlTransfomer>  option = 
							Html2XmlTransformerFactory.getTransformer(analysisResult.get(path.getParent().toFile().getName()));
					
					if (option.isPresent()) {
						option.get().transformFile(path);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public void transformFile(Path input, Path output) throws Exception {
		Optional<Html2XmlTransfomer>  option = 
				Html2XmlTransformerFactory.getTransformer(ConstantHelper.STANDARD_PRACTICE);
		
		if (option.isPresent()) {
			option.get().transformFile(input);
		}
			
	}
}
