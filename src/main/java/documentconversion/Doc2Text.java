package documentconversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import com.datascience9.doc.preprocessing.DocConverter;
import com.datascience9.doc.util.DocumentConverterHelper;

public class Doc2Text extends DocConverter {
	
	@Override
	public void extractText(Path input, Path output ) {
		
		try {
			InputStream inputStream = new FileInputStream (input.toFile());
			HWPFDocument wordDocument = new HWPFDocument (inputStream);
			WordExtractor wordExtractor = new WordExtractor(wordDocument);
			
      String[] paragraphText = wordExtractor.getParagraphText();
      
      StringBuilder builder = new StringBuilder();
      
      Arrays.stream(paragraphText)
      .filter(paragraph -> !StringUtils.isEmpty(paragraph.trim()))
      .forEach(paragraph -> builder.append(paragraph).append("\n"));
      
      File outputFile = new File(output.toFile(), 
      		DocumentConverterHelper.getFileNameWithNewExtension(input.toFile().getName(), "txt"));
      System.out.println("Start processing ..." + input.toString() + " to " + outputFile.getAbsolutePath());
      writeStringToFile(outputFile.toPath(), builder.toString());
      
      wordExtractor.close();
      
		} catch (Exception ex) { ex.printStackTrace(System.out); }
	}
	
	public static void main (String[] args) throws Throwable {
		DocConverter converter = new Doc2Text();
		converter.extract(Paths.get("/media/paul/workspace/pdftest/"), Paths.get("/media/paul/workspace/pdftest/"));
  }
}
