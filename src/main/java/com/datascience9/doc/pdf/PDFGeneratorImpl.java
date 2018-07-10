package com.datascience9.doc.pdf;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import com.datascience9.doc.util.LoggingUtil;

public abstract class PDFGeneratorImpl implements PDFGenerator {
	protected Logger logger = LoggingUtil.getLogger(this.getClass().getName());
	
   public PDFGeneratorImpl() {}
   
   public PDFGeneratorImpl(Logger logger) {
     this.logger = logger;
   }
   
   public void setLogger(Logger logger) { this.logger = logger; }
   
  /**
   * generate PDFs from a directory
   */
	public void generate(Path input, Path output) throws Exception {
		Files.list(input)
		.filter(f -> Files.isDirectory(f, LinkOption.NOFOLLOW_LINKS))
		.forEach(dir -> {
			try {
				generateFromDir(dir, output);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * generate PDFs from a directory
	 * @param dir
	 * @param output
	 * @throws Exception
	 */
	private void generateFromDir(Path dir, Path output) throws Exception {
		Optional<Path> result = 
		Files.list(dir)
		.filter(files -> Files.isRegularFile(files, LinkOption.NOFOLLOW_LINKS)
				&&  files.toFile().getName().equals("result.xml"))
		.findFirst();
		
		if (result.isPresent()) {
			try {
				generatePDF(result.get().toFile(), 
						Paths.get(result.get().toFile().getParentFile().getAbsolutePath(), "result.pdf").toFile());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
