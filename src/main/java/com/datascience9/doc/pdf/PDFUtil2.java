package com.datascience9.doc.pdf;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class PDFUtil2
{
//	public static final String HEADER = "";
//	public static final Font FONT_DEFAULT = new Font (Font.HELVETICA, 10, Font.NORMAL);
//  public static final Font FONT_BOLD = new Font(Font.HELVETICA, 8, Font.BOLD);
//  public static final Font FONT_BOLD_UNDERLN = new Font (Font.HELVETICA, 8, Font.BOLD|Font.UNDERLINE);
//  public static final Font FONT_REGULAR = new Font (Font.HELVETICA, 8, Font.NORMAL);
//  public static final Font FONT_VERY_SMALL = new Font(Font.HELVETICA, 6, Font.NORMAL);
//  public static final Paragraph NEWLINE = new Paragraph(" " , FONT_VERY_SMALL);
//  public static final Font FONT_HEADER = new Font(Font.HELVETICA, 9, Font.ITALIC);
	  
  final static Logger logger = Logger.getLogger(PDFUtil2.class.getName());
  
  public static PdfPTable createFixColumnTable(Element tableElement) {
  	List<Element> rows = null;
  	Element tbody = tableElement.select("tbody").first();
  	if (null == tbody) {
  		rows = tableElement.select("tr");
  	} else {
  		rows = tbody.select("tr");
  	}
  	
  	int cols = findRowWithMaxCol(tableElement);
  	PdfPTable table = new PdfPTable(cols);
  	table.setWidthPercentage(100);
  	table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
  	table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
  	return table;
  }
  
  public static TableMetaData getTableMetaData(Element tr, Map<String, Map<String, String>> stylemap) {
  	TableMetaData meta = new TableMetaData();
  	List<Element> tds = tr.select("td");
  	if (0 == tds.size()) {
  		logger.severe("Row cannot have zero column!!! " + tr.html());
  		return null;
  	}
  	
  	meta.numberOfCols = tds.size();
  	List<Float> cols = 
  	tds.stream().map(td -> {
  		String styleName = td.attr("class").toString();
  		Map<String, String> styles = stylemap.get(styleName);
  		return PDFUtil.convertInches2Pixel(Float.valueOf(styles.get("width").replace("in", "")));
  	})
  	.collect(Collectors.toList());
  	
  	meta.cols = new float[cols.size()];
  	for (int i = 0 ; i < cols.size(); i++) {
  		meta.cols[i] = cols.get(i);
  	}
  	
  	return meta;
  }
  
//  public static void setTableCellStyle(PdfPCell cell, Map<String, String> styles) {
//  	styles.keySet().stream()
//  	.forEach(styleName -> {
//  		if ("width".equals(styleName)) {
//  			//already take care in table
//  		} else if ("padding-start".equals(styleName)) {
//  			cell.setPaddingLeft(convertInches2Pixel(Float.valueOf(styles.get(styleName).replace("in", ""))));
//  		} else if ("padding-end".equals(styleName)) {
//  			cell.setPaddingRight(convertInches2Pixel(Float.valueOf(styles.get(styleName).replace("in", ""))));
//  		} else if ("border-bottom".equals(styleName)) {
//  			cell.setBorder(Rectangle.BOTTOM);
//  			setTableCellBorderStyle(cell, styles.get(styleName));
//  		} else if ("border-top".equals(styleName)) {
//  			cell.setBorder(Rectangle.TOP);
//  			setTableCellBorderStyle(cell, styles.get(styleName));
//  		}
//  	});
//  }
  
  //border-bottom:1.0pt solid black
//  public static void setTableCellBorderStyle(PdfPCell cell, String css) {
//  	String[] tokens = css.split(" ");
//  	if (tokens.length != 3) {
//  		logger.severe("invalid border style:" + css);
//  		return;
//  	}
//  	cell.setBorderColor(convertColorCss2Itext(tokens[tokens.length-1]));
//  }
  public static boolean isFixColumnTable(Element table) {
  	List<Element> rows = null;
  	Element tbody = table.select("tbody").first();
  	if (null == tbody) {
  		rows = table.select("tr");
  	} else {
  		rows = tbody.select("tr");
  	}
  	int firstrow = rows.get(0).select("td").size();
  	for (int i = 1; i < rows.size(); i++) {
  		int nextrow = rows.get(i).select("d").size();
  		if (firstrow != nextrow) return false;
  	}
  	return true;
  }
  
  public static int findRowWithMaxCol(Element table) {
  	List<Element> rows = null;
  	Element tbody = table.select("tbody").first();
  	if (null == tbody) {
  		rows = table.select("tr");
  	} else {
  		rows = tbody.select("tr");
  	}
  	return rows.stream().mapToInt(row -> {
  		return row.select("td").size();
  	}).max().orElseThrow(NoSuchElementException::new);
  }
  
//  public static float convertInches2Pixel(float inches) {
//  	return inches*96.000000000001F;
//  }
  /**
   * Since arial is not avaiable by default
   * need to load it from a file called arial.ttf
   * which is under font/
   * @return
   */
//  public static Font getArialFont(float size) {
//  	try {
//    	BaseFont baseFont3 = BaseFont.createFont("font/arial.ttf", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
//      return new Font(baseFont3, size);
//  	} catch (Exception ex) {
//  		logger.log(Level.SEVERE, "Cannot load Arial font from ./font/arial.ttf", ex);
//  		return FONT_DEFAULT;
//  	}
//  }
  
//  /**
//   * Since arial is not avaiable by default
//   * need to load it from a file called arial.ttf
//   * which is under font/
//   * @return
//   */
//  public static Font geTimeNewRomanFont(float size) {
//  	try {
//    	BaseFont baseFont3 = BaseFont.createFont("font/times_wew_roman.ttf", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
//      return new Font(baseFont3, size);
//  	} catch (Exception ex) {
//  		logger.log(Level.SEVERE, "Cannot load Time New Romain font from ./font/times_wew_roman.ttf", ex);
//  		return PDFUtil.FONT_DEFAULT;
//  	}
//  }
  
//  public static void setParagraphStyle(Paragraph p, Map<String, String> styles) {
//  	styles.keySet().stream()
//  	.forEach(styleName -> {
//  		if ("text-align".equals(styleName)) {
//  			p.setAlignment(convertTextAlignmentCss2ItextStyle(styles.get(styleName)));
//  		} else if ("margin-left".equals(styleName)) {
//  			String inchesStr = styles.get(styleName);
//  			float inches = Float.valueOf(inchesStr.substring(0,  inchesStr.length()-2));
//  			p.setIndentationLeft(inches);
//  		} else if ("text-indent".equals(styleName)) {
//  			String inchesStr = styles.get(styleName);
//  			try {
//    			float inches = convertInches2Pixel(Float.valueOf(inchesStr.replace("in", "")));
//    			p.setFirstLineIndent(Math.max(0f, inches));
//  			} catch (Exception ex) {
//  				logger.log(Level.SEVERE, "Cannot convert in to pixel:" + styleName + ":" + inchesStr, ex);
//  			}
//  		} else if ("margin-right".equals(styleName)) { 
//  			String inchesStr = styles.get(styleName);
//  			float inches = Float.valueOf(inchesStr.substring(0,  inchesStr.length()-2));
//  			p.setIndentationRight(convertInches2Pixel(inches));
//  		} else if ("font-family".equals(styleName)) { 
//  			if (styles.get("font-size") != null) {
//    			float fontSize = getParagraphFontSize(p, styles.get("font-size").replace("pt", ""));
//    			p.setFont(convertFontCss2ItextStyle(styles.get(styleName), fontSize));
//  			} else {
//  				p.setFont(convertFontCss2ItextStyle(styles.get(styleName), 12));
//  			}
//  		} else if ("font-size".equals(styleName)) { 
//  			//skip.  Already used in the previous if else statement
//  		} else if ("hyphenate".equals(styleName)) { 
//  			//skip for now
//  		} else if ("min-width".equals(styleName)) { 
//  			//skip for now
//  		} else {
//  			logger.severe("Not support style name " + styleName);
//  		}
//  	});
//  }
  
//  public static void setChunkStyle(Paragraph p, Chunk chunk, Map<String, String> styles) {
//  	styles.keySet().stream()
//  	.forEach(styleName -> {
//  		if ("font-family".equals(styleName)) { 
//  			chunk.setFont(convertFontCss2ItextStyle(styles.get(styleName), 12));
//  		} else if ("font-size".equals(styleName)) { 
//  			float fontSize = getChunkFontSize(p, chunk, styles.get("font-size"));
//  			Font currentFont = chunk.getFont();
//  			currentFont.setSize(fontSize);
//  			chunk.setFont(currentFont);
//  		} else if ("text-decoration".equals(styleName)) { 
//  			if ("underline".equals(styles.get(styleName))) {
//  				chunk.setUnderline(1, -1);
//  			} else {
//  				logger.severe("Unsupport text-decoration style: " + styles.get(styleName));
//  			}
//  		} else if ("color".equals(styleName)) { 
//  			Font currentFont = chunk.getFont();
//  			String colorStr = styles.get(styleName);
//  			currentFont.setColor(convertColorCss2Itext(colorStr));
//  			chunk.setFont(currentFont);
//  		} else if ("font-weight".equals(styleName)) { 
//  			Font currentFont = chunk.getFont();
//  			String css = styles.get(styleName);
//  			currentFont.setStyle(getFontWeight(css));
//  			chunk.setFont(currentFont);
//  		} else if ("vertical-align".equals(styleName)) { 
//  			String css = styles.get(styleName);
//  			chunk.setTextRise(getVerticalAlign(css));
//  		} else if ("display".equals(styleName)) { 
//  			//skip for now
//  		} else if ("text-indent".equals(styleName)) {
//  			String inchesStr = styles.get(styleName);
//  			//skip for now
//  		} else if ("min-width".equals(styleName)) { 
//  			//skip for now
//  		} else {
//  			logger.severe("Not support style name " + styleName);
//  		}
//  	});
//  }
  
//  public static int getFontWeight(String css) {
//  	if ("bold".equals(css)) return Font.BOLD;
//		if ("italic".equals(css)) return Font.ITALIC;
//		else {
//			logger.info("Not support font-weight name " + css);
//			return Font.NORMAL;
//		}
//  }
//  
//  public static int getVerticalAlign(String css) {
//  	if ("super".equals(css)) return 5;
//		if ("sub".equals(css)) return -5;
//		else {
//			logger.info("Not support vertical align name " + css);
//			return 0;
//		}
//  }
  
//  public static float getParagraphFontSize(Paragraph p, String fontSizeStr) {
//  	Font currentFont = p.getFont();
//  	if ("x-small".equals(fontSizeStr)) return currentFont.getSize() - 2f;
//  	else if ("smaller".equals(fontSizeStr)) return currentFont.getSize() - 1f;
//  	else if ("medium".equals(fontSizeStr)) return currentFont.getSize();
//  	else if ("large".equals(fontSizeStr)) return currentFont.getSize() + 1f;
//  	else if ("larger".equals(fontSizeStr)) return currentFont.getSize() + 1f;
//  	else {
//  		try {
//  			return Integer.valueOf(fontSizeStr);
//  		} catch (Exception ex) {
//  			logger.log(Level.SEVERE, "Cannot convert font size:" + fontSizeStr, ex);
//  			return currentFont.getSize();
//  		}
//  	}
//  }
//  
//  public static float getChunkFontSize(Paragraph p, Chunk c, String fontSizeStr) {
//  	Font parentFont = p.getFont();
//  	Font currentFont = c.getFont();
//  	if ("x-small".equals(fontSizeStr)) return parentFont.getSize() - 2f;
//  	else if ("smaller".equals(fontSizeStr)) return parentFont.getSize() - 1f;
//  	else if ("medium".equals(fontSizeStr)) return parentFont.getSize();
//  	else if ("large".equals(fontSizeStr)) return parentFont.getSize() + 1f;
//  	else if ("larger".equals(fontSizeStr)) return parentFont.getSize() + 1f;
//  	else {
//  		try {
//  			return Integer.valueOf(fontSizeStr.replace("pt", ""));
//  		} catch (Exception ex) {
//  			logger.log(Level.SEVERE, "Cannot convert font size:" + fontSizeStr, ex);
//  			return currentFont.getSize();
//  		}
//  	}
//  }
//  
//  public static Color convertColorCss2Itext(String css) {
//  	if ("red".equals(css)) return Color.red;
//  	if ("green".equals(css)) return Color.green;
//  	if ("blue".equals(css)) return Color.blue;
//  	if ("black".equals(css)) return Color.black;
//  	if (css.startsWith("rgb")) return getColorFromRGB(css);
//  	else {
//  		logger.severe("Unsupport color " + css);
//  		return Color.black;
//  	}
//  }
//  
//  public static Color getColorFromRGB(String css) {
//  	String rgb = css.substring(4, css.length()-1);
//  	String[] values = rgb.split(",");
//  	if (values.length != 3) {
//  		logger.severe("Invalid RGB:" + rgb);
//  		return Color.black;
//  	}
//  	
//  	return new Color(Integer.valueOf(values[0].trim())
//  			, Integer.valueOf(values[1].trim())
//  			, Integer.valueOf(values[2].trim()));
//  }
//  
//  public static int convertTextAlignmentCss2ItextStyle(String css) {
//  	if ("start".equals(css)) return Rectangle.ALIGN_LEFT;
//  	else if ("end".equals(css)) return Rectangle.ALIGN_RIGHT;
//  	else if ("center".equals(css)) return Rectangle.ALIGN_CENTER;
//  	else if ("justify".equals(css)) return Rectangle.ALIGN_JUSTIFIED;
//  	else {
//  		logger.severe("Unsupport alignment name " + css);
//  		return 0;
//  	}
//  }
//  
//  public static Font convertFontCss2ItextStyle(String fontName, float fontSize) {
//  	if ("Arial".equals(fontName)) return getArialFont(fontSize);
//  	else if ("Times New Roman".equals(fontName)) return geTimeNewRomanFont(fontSize);
//  	else {
//  		logger.severe("Unsupport font name " + fontName);
//  		
//  		return FONT_DEFAULT;
//  	}
//  }
//  
//  public static void addEmptyLines2Document(Document document, int lines) {
//  	IntStream.range(0, lines).boxed().forEach(x -> {
//			try {
//				document.add(new Paragraph("\n"));
//			} catch (DocumentException e) { e.printStackTrace();}
//		}); 
//  }
//  
//  public static Optional<CSSStyleSheet> getCssStyleSheet(Path path) {
//  	try {
//	  	InputSource source = new InputSource(new FileReader(path.toFile()));
//	  	CSSOMParser parser = new CSSOMParser(new SACParserCSS1());
//	  	CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
//	  	return Optional.of(sheet);
//  	} catch (Exception ex) {
//  		logger.log(Level.SEVERE, "cannot read css stylesheet", ex);
//  		return Optional.empty();
//  	}
//  }
///**
//   * use in getProductBasedOnListOfProduct
//   */
//  public static HeaderFooter getHeader(String product, String text)
//  {
//    return getHeader(product, HEADER, text);
//  }
//
//  /**
//   * use in getProductBasedOnCriteria
//   */
//  public static HeaderFooter getHeader(String product, String firstline, String text) 
//  {
//    HeaderFooter header = null;
//    try
//    {
//      Phrase phrase = new Phrase();
//
//      phrase.add(new Chunk(firstline, new Font(Font.HELVETICA, 11, Font.BOLDITALIC)));
//      phrase.add(Chunk.NEWLINE);
//      if (StringUtils.isNotEmpty(text))
//      {
//        phrase.add(new Chunk(text, FONT_HEADER));
//        phrase.add(Chunk.NEWLINE);
//      }
//      phrase.add(new Chunk(product, FONT_REGULAR));
//
//      header = new HeaderFooter(phrase, false);
//      header.setBorder(Rectangle.TOP + Rectangle.BOTTOM);
//      }
//      catch(Exception e) { System.err.println(e); }
//      return header;
//  }
//
//  
//  public static HeaderFooter getFooter(Document d) 
//  {
//    HeaderFooter footer = null;
//    try {
//    Phrase phrase = new Phrase();
//    DateFormat formatter =
//      DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
//    Date date = new Date(System.currentTimeMillis());
//    String result = formatter.format(date);
//    phrase.add(new Chunk(result, FONT_REGULAR));
//    phrase.add(new Chunk("                                                                                                                                                                                                                                                                                 Page: " + (d.getPageNumber() + 1), FONT_REGULAR));
//    Rectangle rect = d.getPageSize();
//    float right = rect.getRight();
//
//    footer = new HeaderFooter(phrase, false);
//    footer.setBorder(Rectangle.TOP);
//    }
//    catch (Exception e) { System.err.println(e); }
//    return footer;
//  }
//
//	/**
//	 * parse list of style d2 :[ margin: 0.98888886in 1in 0in 1in ]
//	 * to map<String, map<String, String>>
//	 * @param path
//	 * @return
//	 */
//	public static Map<String, Map<String, String>> getCssStyleSheetAsMap(Path path) {
//		Map<String, Map<String, String>> map = new HashMap<>();
//		try {
//			Optional<CSSStyleSheet> option = getCssStyleSheet(path);
//			CSSStyleSheet sheet = option.get();
//			CSSRuleList rulelists = sheet.getCssRules();
//			for (int i = 0 ; i < rulelists.getLength(); i++) {
//				CSSRule rule = rulelists.item(i);
//				String text = rule.getCssText();
//				String ruleName = text.substring(1,  text.indexOf("{")).trim();
//				String styleStr = text.substring(text.indexOf("{") + 1, text.length() -1);
//				String[] styles = styleStr.split(";");
//				List<String> list = Arrays.asList(styles);
//				Map<String, String> style = new HashMap<>();
//				list.stream().forEach(str -> {
//					int index = str.indexOf(":");
//					String key = str.substring(0, index).trim();
//					String value = str.substring(index + 1).trim();
//					//strange case due to css parser
//					if (value.contains("E")) {
//						String[] tokens = value.split(" ");
//						value = tokens[0].trim() + tokens[1].trim();
//					}
//					style.put(key, value);
//				});
//				map.put(ruleName, style);
//				
//			}
//		} catch (Exception ex) {
//			logger.log(Level.SEVERE, "cannot read css stylesheet", ex);
//		}
//		return map;
//	}
}
