package com.datascience9.doc.pdf;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class TestPdfGeneration {
	
	@Test
	public void testCssStyleSheet() throws Exception {
		Optional<CSSStyleSheet> optional = PDFUtil.getCssStyleSheet(
				Paths.get("./test/style.css"));
		CSSStyleSheet stylesheet = optional.get();
		CSSRuleList rulelists = stylesheet.getCssRules();
		for (int i = 0 ; i < rulelists.getLength(); i++) {
			CSSRule rule = rulelists.item(i);
			String text = rule.getCssText();
			String ruleName = text.substring(1,  text.indexOf("{")).trim();
			String styleStr = text.substring(text.indexOf("{") + 1, text.length() -1);
			String[] styles = styleStr.split(";");
			List<String> list = Arrays.asList(styles);
			
			Map<String, String> style = new HashMap<>();
			list.stream().forEach(str -> {
				int index = str.indexOf(":");
				String key = str.substring(0, index).trim();
				String value = str.substring(index + 1).trim();
				//strange case due to css parser
				if (value.contains("E")) {
					String[] tokens = value.split(" ");
					value = tokens[0].trim() + tokens[1].trim();
				}
				style.put(key, value);
			});
			
			System.out.println(ruleName + ":" + style.toString());
		}
	}
	
	@Test
	public void testgetColorFromRGB() throws Exception {
		Color color = PDFUtil.getColorFromRGB("rgb(0, 51, 101)");
		assertEquals("must match green rgb values", 51, color.getGreen());
		assertEquals("must match blue rgb values", 101, color.getBlue());
		assertEquals("must match red rgb values", 0, color.getRed());
	}
	
	@Test
	public void testGetCssAsMap() {
		Path xmlFile = Paths.get("./test/MIL-STD-171.xml");
		Map<String, Map<String, String>> map = PDFUtil.getCssStyleSheetAsMap(
				Paths.get(xmlFile.getParent().toString(), "style.css"));
		
		assertEquals("found css ", "0.6145833in", map.get("r110").get("height"));
	}
	
	@Test
	public void testCreateNewFont() {
	// creation of the document with a certain size and certain margins
    // may want to use PageSize.LETTER instead
    Document document = new Document(PageSize.A4, 50, 50, 50, 50);
    try {
        // creation of the different writers
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("./test/CustomFontsStyle.pdf"));
        final String NEWLINE = "\n";
        document.open();
        Phrase phrase = new Phrase();

        BaseFont baseFont3 = BaseFont.createFont("font/arial.ttf", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        Font font2 = new Font(baseFont3, 12);

        document.add(new Paragraph("Custom Xenotron Font: ", font2));

        phrase.add(NEWLINE);

        document.add(phrase);

        document.close();

    }
    catch (Exception ex) {
        System.err.println(ex.getMessage());
    }
	}
}
