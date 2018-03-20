package documentconversion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class Pdf2Text {
	
	public static void main(String[] s) throws Exception {
		extract(Paths.get("/media/paul/workspace/pdftest/"), Paths.get("./"));
	}
	
	public static void extract(Path input, Path output) throws Exception {
		Files.list(input)
//		.filter(f -> f.getFileName().endsWith(".pdf"))
		.forEach(f -> extractText(f, output));
	}
	
	static void extractText(Path input, Path output )
	{
		boolean sort = false;
		int startPage = 1;
		 int endPage = Integer.MAX_VALUE;
		 PDDocument document = null;
		 
		 File outputFile = new File(output.toFile(), input.toFile().getName());
		 
		 System.out.println("start extracting " + input);
		 OutputStreamWriter writer = null;
		 try {
			 writer = new OutputStreamWriter(new FileOutputStream( outputFile ) );
			 PDFTextStripper stripper = new PDFTextStripper();
        document = PDDocument.load( input.toFile() );
        stripper.setSortByPosition( sort );
        stripper.setStartPage( startPage );
        stripper.setEndPage( endPage );
        stripper.writeText( document, writer );
        document.close();
        writer.flush();
        writer.close();
		 } catch (Exception ex) { ex.printStackTrace(System.out);}
	}
}
