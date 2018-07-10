package com.datascience9.doc.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Milstd962Xml2Pdf extends PDFGeneratorImpl {

//	private SAXReader reader;
	private com.lowagie.text.Document outdoc;
	private Path input;
	Map<String, Map<String, String>> cssStyles;
	
	public Milstd962Xml2Pdf() {
//		reader = new SAXReader();
	}
	
	@Override
	public void generatePDF(File inputfile, File outputfile) throws Exception {
    OutputStream out = null;
    PdfWriter writer = null;

    logger.info("generate PDF for " + inputfile + " to " + outputfile);

    cssStyles = PDFUtil.getCssStyleSheetAsMap(Paths.get(inputfile.toPath().getParent().toString(), "style.css"));
    input = inputfile.toPath();

    Document document = Jsoup.parse(inputfile, "UTF-8");
    Element root = document.select("doc").first();
    
    outdoc = new com.lowagie.text.Document(PageSize.A4);
    out = new FileOutputStream(outputfile);
    
    writer = MilStd962Xml2PdfHelper2.createDocument(outdoc, root, out);
    writer.setFooter(PDFUtil.getFooter(outdoc));
//    ReportEvent events = new ReportEvent();
//    writer.setPageEvent(events);
    outdoc.open();
    
    createSelfCoverPage(writer, root);
    
    createBody(root);
    
    outdoc.close();
    writer.flush();
    writer.close();
	}
	
	private void createSelfCoverPage(PdfWriter writer, Element root) throws Exception
	{
		Element selfcover = root.select("selfcover").first();
		writer.setPageEvent(new MilStd962PageEvent(selfcover));
		String system_identification = selfcover.select("system_identification").first().html();
		
		outdoc.add(MilStd962Xml2PdfHelper.createMeasurementSystemId(system_identification));
		
		PDFUtil.addEmptyLines2Document(writer, outdoc, 1);
		
		outdoc.add(MilStd962Xml2PdfHelper2.createRevision(selfcover.select("revision").first()));
		
		outdoc.add(MilStd962Xml2PdfHelper2.createHeading(selfcover));
		
		outdoc.add(MilStd962Xml2PdfHelper2.createTitle(selfcover));
		
		
		Optional<Image> logo = MilStd962Xml2PdfHelper2.createLogo(input, selfcover.select("img").first().attr("src").toString());
		if (logo.isPresent()) {
			PDFUtil.addEmptyLines2Document(writer, outdoc, 1);
			outdoc.add(logo.get());
		}

		PDFUtil.addEmptyLines2Document(writer, outdoc, 100);
		outdoc.add(MilStd962Xml2PdfHelper2.createAsmSection(selfcover));
		outdoc.add(MilStd962Xml2PdfHelper2.createDistributionStatement(selfcover));

		outdoc.newPage();
		writer.setPageEvent(null);
	}
	
	private void createBody(Element root) {
		
		List<Element> sections = root.select("sec");
		sections.forEach(section -> createSection(section));
	}
	
	private void createSection(Element section) {
		final float leading = ("contents".equalsIgnoreCase(section.attr("name"))) ? 1.15f : 1.5f;
		List<Element> pages = section.select("page");
		pages.forEach(page -> createPage(page, leading));
	}
	

	private void createPage(Element page, float leading) {
		List<Element> divs = page.select("div");
		divs.forEach(div -> createDiv(div, leading));
		outdoc.newPage();
	}
	
	private void createDiv(Element div, float leading) {
		List<Element> children = div.children();
		children.stream().forEach(e -> {
			if ("p".equalsIgnoreCase(e.tagName())) {
				Paragraph para = createParagraph(e, leading);
  			try {
  				outdoc.add(para);
    		} catch (Exception ex) {
    			logger.log(Level.SEVERE, "Cannot add paragraph into document ", ex);
    		}
			} else if ("table".equalsIgnoreCase(e.tagName())) {
				List<PdfPTable> tables = createTables(e);
				tables.stream().forEach(t -> {
					try {
						outdoc.add(t);
					} catch (Exception ex) {}
				});
			} 
			else {
				logger.info("createDiv : Not support yet " + e.tagName());
			}
		});
	}
	
	private Paragraph createParagraph(Element p, float leading) {
		final Paragraph para = (p.children().isEmpty())
				? new Paragraph(p.text())
				: new Paragraph();
				
		String styleName = p.attr("class");
		if (StringUtils.isEmpty(styleName)) logger.info("this paragraph does not have a style!!!!");
		else {
			Map<String, String> styles = cssStyles.get(styleName);
			if (null == styles) {
				logger.severe("style cannot found [" + styleName + "]");
			} else {
				PDFUtil.setParagraphStyle(para, styles);
			}		
		}
		List<Element> elements = p.children();
		List<com.lowagie.text.Element> children = 
		elements.stream()
  		.map(e ->createParagraphChild(para, e))
  		.filter(result -> (null != result))
  		.collect(Collectors.toList());
		
		children.stream().forEach(ch -> para.add(ch));
		
		para.setLeading(leading * para.getFont().getSize());
		return para;
	}
	
	private com.lowagie.text.Element createParagraphChild(Paragraph para, Element e) {
		if ("span".equalsIgnoreCase(e.tagName())) {
			return createChunk(para, e);
		} 
		if ("img".equalsIgnoreCase(e.tagName())) {
			Optional<Image> logo = MilStd962Xml2PdfHelper2.createLogo(input, e.attr("src"));
			if (logo.isPresent()) return logo.get();
			else return null;
		} 
		if ("a".equals(e.tagName())) {
			//skip for now
//			PdfOutline bookmark = new PdfOu
			return null;
		}
		else {
			logger.warning("Not support element in pagraph " + e.tagName());
			return null;
		}
	}
	
	private Chunk createChunk(Paragraph para, Element span) {
		Chunk chunk = new Chunk(span.html().replace("&nbsp;", " "));
		String styleName = span.attr("class");
		if (StringUtils.isNotEmpty(styleName)) { 
    	Map<String, String> styles = cssStyles.get(styleName);
			if (null == styles) {
				logger.severe("createChunk: style cannot found [" + styleName + "]");
			} else {
				PDFUtil.setChunkStyle(para, chunk, styles);
			}		
    }
		return chunk;
	}
	
	private List<PdfPTable> createTables(Element tableEle) {
			List<Element> rows = null;
	  	Element tbody = tableEle.select("tbody").first();
	  	if (null == tbody) {
	  		rows = tableEle.select("tr");
	  	} else {
	  		rows = tbody.select("tr");
	  	}
	  	
	  	return
	  			rows.stream().map(row -> createTable(row))
	  			.filter(table -> table != null)
	  			.collect(Collectors.toList());
	}
	
	private PdfPTable createTable(Element row) {
		TableMetaData meta = PDFUtil2.getTableMetaData(row, cssStyles);
		if (null == meta) return null;
		
		PdfPTable table = new PdfPTable(meta.cols);
  	table.setWidthPercentage(100);
  	table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
  	table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
  	
  	List<Element> cols = row.select("td");
  	
  	List<PdfPCell> cells = cols.stream().map(col -> createTableCell(col))
  	.collect(Collectors.toList());
  	
  	cells.stream().forEach(cell -> table.addCell(cell));
  	
  	return table;
	}
	
	public PdfPCell createTableCell(Element td) {
  	PdfPCell cell = new PdfPCell();
  	cell.setBorder(Rectangle.NO_BORDER);
  	if (td.attr("class") != null) {
  		String styleName = td.attr("class");
			Map<String, String> styles = cssStyles.get(styleName);
			if (null == styles) {
				logger.severe("style cannot found [" + styleName + "]");
			} else {
				PDFUtil.setTableCellStyle(cell, styles);
			}		
  	}
  	
  	List<Element> paragraphs = td.select("p");
  	paragraphs.stream().forEach(p -> cell.addElement(createParagraph(p, 1f)));
  	
  	return cell;
  }
	
	public static void main(String[] s) throws Exception {
		new Milstd962Xml2Pdf()
		.generatePDF(Paths.get("/media/paul/workspace/pdftest/AB2514D4D9E142C59A62D353F58EB9C5/result.xml").toFile()
				, Paths.get("/media/paul/workspace/pdftest/AB2514D4D9E142C59A62D353F58EB9C5/result.pdf").toFile());
	}

}
