package com.datascience9.doc.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Milstd962Xml2PdfOld extends PDFGeneratorImpl {
	public static final Font FONT_TITLE = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD, Color.blue); //new Font(Font.HELVETICA, 18, Font.BOLD, Color.blue);
	public static final Font FONT_HEADER = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD, Color.blue);//new Font(Font.HELVETICA, 10, Font.BOLD, Color.blue);
	public static final Font FONT_SMALL = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD, Color.black);//new Font(Font.HELVETICA, 10, Font.BOLD, Color.black);
	public static final Font FONT_ANSWER= FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, Color.red);//new Font(Font.HELVETICA, 12, Font.BOLD, Color.red);
	public static final Font FONT_QUESTION= FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, Color.blue);//new Font(Font.HELVETICA, 14, Font.BOLD, Color.blue);
	public static final Font FONT_CHOICE= FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL, Color.black);
	public static final Font FONT_REG= FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL, Color.black);
	
	final public Logger logger = Logger.getLogger(this.getClass().getName());
	
	private SAXReader reader;
	private com.lowagie.text.Document outdoc;
	private Path input;
	Map<String, Map<String, String>> cssStyles;
	
	public Milstd962Xml2PdfOld() {
		reader = new SAXReader();
	}
	
	@Override
	public void generatePDF(File inputfile, File outputfile) throws Exception {
		OutputStream out = null;
		PdfWriter writer = null;
		
		cssStyles = PDFUtil.getCssStyleSheetAsMap(Paths.get(inputfile.toPath().getParent().toString(), "style.css"));
		input = inputfile.toPath();
		
		org.dom4j.Document document = reader.read(inputfile);
    Element root = document.getRootElement();
    
    outdoc = new com.lowagie.text.Document(PageSize.A4);
    out = new FileOutputStream(outputfile);
		writer = MilStd962Xml2PdfHelper.createDocument(outdoc, root, out);
		writer.setFooter(PDFUtil.getFooter(outdoc));
    ReportEvent events = new ReportEvent();
    writer.setPageEvent(events);
    outdoc.open();
    
    createSelfCoverPage(root);
    
    createBody(root);
    
    outdoc.close();
    writer.flush();
    writer.close();
	}
	
	private void createSelfCoverPage(Element root) throws Exception
	{
		Element selfcover = root.element("selfcover");
		String system_identification = selfcover.elementText("system_identification");
		
		outdoc.add(MilStd962Xml2PdfHelper.createMeasurementSystemId(system_identification));
		
		PDFUtil.addEmptyLines2Document(outdoc, 1);
		
		outdoc.add(MilStd962Xml2PdfHelper.createRevision(selfcover.element("revision")));
		
		outdoc.add(MilStd962Xml2PdfHelper.createHeading(selfcover));
		
		outdoc.add(MilStd962Xml2PdfHelper.createTitle(selfcover));
		
		Optional<Image> logo = MilStd962Xml2PdfHelper.createLogo(input, selfcover.elementText("img"));
		if (logo.isPresent()) outdoc.add(logo.get());

		PDFUtil.addEmptyLines2Document(outdoc, 16);
		outdoc.add(MilStd962Xml2PdfHelper.createAsmSection(selfcover));
		outdoc.add(MilStd962Xml2PdfHelper.createDistributionStatement(selfcover));

		outdoc.newPage();
	}
	
	private void createBody(Element root) {
		
		List<Element> sections = root.elements("section");
		sections.forEach(section -> createSection(section));
	}
	
	private void createSection(Element section) {
		List<Element> pages = section.elements("page");
		pages.forEach(page -> createPage(page));
	}
	

	private void createPage(Element page) {
		List<Element> divs = page.elements("div");
		divs.forEach(div -> createDiv(div));
		outdoc.newPage();
	}
	
	private void createDiv(Element div) {
//		String html = div.toString();
//		Document doc = Jsoup.parseBodyFragment(html);
//		org.jsoup.nodes.Element body = doc.body();
//		body.children()
		List<Element> children = div.elements();
		children.stream().forEach(e -> {
			if ("p".equalsIgnoreCase(e.getName())) {
				Paragraph para = createParagraph(e);
  			try {
  				outdoc.add(para);
    		} catch (Exception ex) {
    			logger.log(Level.SEVERE, "Cannot add paragraph into document ", ex);
    		}
			} else if ("table".equalsIgnoreCase(e.getName())) {
				List<PdfPTable> tables = createTables(e);
				tables.stream().forEach(t -> {
					try {
						outdoc.add(t);
					} catch (Exception ex) {}
				});
			} else {
				logger.info("createDiv : Not support yet " + e.getName());
			}
		});
	}
	
	private Paragraph createParagraph(Element p) {
		final Paragraph para = (!"".equals(p.getTextTrim()))
				? new Paragraph(p.getTextTrim())
				: new Paragraph();
				
		Attribute attr = p.attribute("class");
		if (null == attr) logger.info("this paragraph does not have a style!!!!");
		else {
			String styleName = attr.getValue();
			Map<String, String> styles = cssStyles.get(styleName);
			if (null == styles) {
				logger.severe("style cannot found [" + styleName + "]");
			} else {
				PDFUtil.setParagraphStyle(para, styles);
			}		
		}
		List<Element> elements = p.elements();
		List<Chunk> chunks = elements.stream()
		.filter(ele -> "span".equals(ele.getName()))
		.map(span -> {
			Chunk chunk = createChunk(para, span);
			return chunk;
		})
		.collect(Collectors.toList());
		
		chunks.stream().forEach(ch -> para.add(ch));
		
		return para;
	}
	
	private Chunk createChunk(Paragraph para, Element span) {
		Chunk chunk = new Chunk(span.getText());
		Attribute attr = span.attribute("class");
		if (null == attr) { 
		//logger.info("this span does not have a style!!!!");}
			
		} 
    else {
    	String styleName = attr.getValue();
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
	  	Element tbody = tableEle.element("tbody");
	  	if (null == tbody) {
	  		rows = tableEle.elements("tr");
	  	} else {
	  		rows = tbody.elements("tr");
	  	}
	  	
	  	return
	  			rows.stream().map(row -> createTable(row))
	  			.collect(Collectors.toList());
	}
	
	private PdfPTable createTable(Element row) {
		TableMetaData meta = PDFUtil.getTableMetaData(row, cssStyles);
		PdfPTable table = new PdfPTable(meta.cols);
  	table.setWidthPercentage(100);
  	table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
  	table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
  	
  	List<Element> cols = row.elements("td");
  	
  	List<PdfPCell> cells = cols.stream().map(col -> createTableCell(col))
  	.collect(Collectors.toList());
  	
  	cells.stream().forEach(cell -> table.addCell(cell));
  	
  	return table;
	}
	
	public PdfPCell createTableCell(Element td) {
  	PdfPCell cell = new PdfPCell();
  	cell.setBorder(Rectangle.NO_BORDER);
  	if (td.attribute("class") != null) {
  		String styleName = td.attribute("class").getValue();
			Map<String, String> styles = cssStyles.get(styleName);
			if (null == styles) {
				logger.severe("style cannot found [" + styleName + "]");
			} else {
				PDFUtil.setTableCellStyle(cell, styles);
			}		
  	}
  	
  	List<Element> paragraphs = td.elements("p");
  	paragraphs.stream().forEach(p -> cell.addElement(createParagraph(p)));
  	
  	return cell;
  }
	
	public static void main(String[] s) throws Exception {
		new Milstd962Xml2PdfOld()
		.generatePDF(Paths.get("/media/paul/workspace/pdftest/334566B59C9340B78ED202A31F4E7B15/result.xml").toFile()
				, Paths.get("/media/paul/workspace/pdftest/334566B59C9340B78ED202A31F4E7B15/result.pdf").toFile());
		
//		new Milstd962Xml2PdfOld()
//		.generate(Paths.get("/media/paul/workspace/pdftest/")
//				, Paths.get("/media/paul/workspace/pdftest/"));
	}

}
