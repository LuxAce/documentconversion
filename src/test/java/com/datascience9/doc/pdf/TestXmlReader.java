package com.datascience9.doc.pdf;

import java.io.File;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class TestXmlReader {
	@Test
	public void testReadXml() {
		try {
  		Document document = Jsoup.parse(
  				new File("/media/paul/workspace/pdftest/AB2514D4D9E142C59A62D353F58EB9C5/result.xml"), "UTF-8");
  		
  		 document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
  
  		Elements docEles = document.select("doc");
  		
  		Element docEle = docEles.first();
  		Element selfCover = docEle.select("selfcover").first();
  		System.out.println(selfCover);
  		System.out.println(selfCover.select("superseding").html());
  		System.out.println("Print image " + selfCover.select("img"));
  		
//  		Elements sections = docEle.select("section");
//  		sections.stream().forEach(section -> {
//  			section.select("page").stream().forEach(page -> {
//  				page.select("div").stream().forEach(div -> {
//  					div.children().stream().forEach(child -> {
//  						if ("p".equals(child.tagName())) {
//  							if (child.select("span") != null && !child.select("span").isEmpty()) {
//  								System.out.println(child.select("span").first().attr("class"));
//  							}
//  						}
//  					});
//  				});
//  			});
//  		});
  		
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}
