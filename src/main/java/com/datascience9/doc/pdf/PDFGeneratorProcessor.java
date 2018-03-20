package com.datascience9.doc.pdf;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.datascience9.doc.ConstantHelper;
import com.datascience9.doc.transform.Html2XmlTransfomer;
import com.datascience9.doc.transform.Html2XmlTransformerFactory;
import com.datascience9.doc.util.FileUtils;
import com.datascience9.doc.util.LoggingUtil;

public class PDFGeneratorProcessor  {
	protected Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	Map<String, String> analysisResult;
	
	public PDFGeneratorProcessor() {
		analysisResult = FileUtils.readFile2MapOfString(ConstantHelper.ANALYSIS_RESULT, Charset.defaultCharset());
	}
	public void generate(Path input, Path output) throws Exception {
		
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.filter(path -> analysisResult.get(path.toFile().getName()) != null)
		.filter(path -> !"UNKNOWN".equals(analysisResult.get(path.toFile().getName())))
		.forEach(dir -> {
			try {
				generateFromDir(dir, output);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private void generateFromDir(Path dir, Path output) throws Exception {
		Optional<Path> result = 
		Files.list(dir)
		.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
				&&  files.toFile().getName().equals("result.xml"))
		.findFirst();
		
		if (result.isPresent()) {
			try {
				Optional<PDFGenerator>  option = 
						PDFGeneratorFactory.getGenerator(analysisResult.get(result.get().getParent().toFile().getName()));
				if (option.isPresent()) {
					option.get().generatePDF(result.get().toFile(), 
							Paths.get(result.get().toFile().getParentFile().getAbsolutePath(), "result.pdf").toFile());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
