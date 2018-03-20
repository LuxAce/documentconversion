package com.datascience9.doc.pdf;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Element;

import com.datascience9.doc.util.LoggingUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class MilStd962Xml2PdfHelper {
	final static Logger logger = LoggingUtil.getLogger(MilStd962Xml2PdfHelper.class.getName());
	
	public static PdfWriter createDocument(Document outdoc, Element root, OutputStream out) throws Exception
	{
		PdfWriter writer = PdfWriter.getInstance(outdoc, out);
		writer.open();
//		outdoc.addAuthor(ApplicationUtil.getProjectProperties().getProperty("application.author", "datascience9.com"));
//		outdoc.addCreator(ApplicationUtil.getProjectProperties().getProperty("application.creator", "datascience9.com"));
		return writer;
	}
	
	public static Paragraph createDistributionStatement(Element selfcover) {
		String distribution = selfcover.elementText("distribution");
		int index = distribution.indexOf(".");
		String sub = distribution.substring(0,  index + 1);
		String sub2 = distribution.substring(index + 1);
		
		Chunk chunk1 = new Chunk(sub);
		chunk1.setUnderline(1, -1);
		
		Chunk chunk2 = new Chunk(sub2);
		
		Paragraph para = new Paragraph();
		para.add(chunk1);
		para.add(chunk2);
		
		para.setAlignment(Rectangle.ALIGN_LEFT);
		return para;
	}
	
	public static PdfPTable createMeasurementSystemId(String id) {
		PdfPTable table = new PdfPTable(new float[] { 3, 1 });

		table.setWidthPercentage(100);
		PdfPCell cell1 = new PdfPCell(new Paragraph(""));
		cell1.setBorder(Rectangle.NO_BORDER);
		
		PdfPCell cell2 = new PdfPCell(new Paragraph(id));
		cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		table.addCell(cell1);
		table.addCell(cell2);
		return table;
	}
	
	/**
	 * create revision block using table
	 * @param revision
	 * @return
	 */
	public static PdfPTable createRevision(Element revision) {
		PdfPTable table = new PdfPTable(new float[] { 3, 1 });

		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		
		PdfPCell cell1 = new PdfPCell(new Paragraph(""));
		PdfPCell cell2 = new PdfPCell(new Paragraph(revision.elementText("id")));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(cell1);
		table.addCell(cell2);
		
		cell1 = new PdfPCell(new Paragraph(""));
		cell2 = new PdfPCell(new Paragraph(revision.elementText("date")));
		
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		
		
		if (revision.element("superseding") != null) {
			cell2.setBorder(Rectangle.BOTTOM);
			cell2.setBorderWidth(2f);
		}
		table.addCell(cell1);
		table.addCell(cell2);
		
		cell1 = new PdfPCell(new Paragraph(""));
		cell2 = new PdfPCell(new Paragraph(revision.elementText("superseding")));
		
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(cell1);
		table.addCell(cell2);
		return table;
	}
	
	public static Optional<Image> createLogo(Path input, String img) {
		
		Path imagePath = Paths.get(input.getParent().toString(), img);
		try {
  		Image image = Image.getInstance(imagePath.toString());
  		image.setAlignment(Rectangle.ALIGN_CENTER);
  		return Optional.of(image);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create logo from " + img, ex);
			return Optional.empty();
		}
		
	}
	
	public static Paragraph createHeading(Element selfcover) {
		Paragraph para = new Paragraph(selfcover.elementText("heading"));
		para.setAlignment(Rectangle.ALIGN_CENTER);return para;
	}
	
	public static  Paragraph createTitle(Element selfcover) {
		Paragraph para = new Paragraph(selfcover.elementText("title"));
		para.setAlignment(Rectangle.ALIGN_CENTER);return para;
	}
	
	public static PdfPTable createAsmSection(Element selfcover) {
		PdfPTable table = new PdfPTable(new float[] { 3, 1 });

		table.setWidthPercentage(100);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		
		PdfPCell cell1 = new PdfPCell(new Paragraph(selfcover.elementText("ams")));
		PdfPCell cell2 = new PdfPCell(new Paragraph(""));
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		
		table.addCell(cell1);
		table.addCell(cell2);
		
		return table;
	}
}
