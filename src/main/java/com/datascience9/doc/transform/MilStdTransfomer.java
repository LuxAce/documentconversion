package com.datascience9.doc.transform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.stringtemplate.v4.ST;

import com.datascience9.doc.analysis.MilStdDocumentAnalyzer;
import com.datascience9.doc.pojo.DocDivision;
import com.datascience9.doc.pojo.MilStd962DSelfCover;
import com.datascience9.doc.util.FileUtils;

public class MilStdTransfomer extends Html2XmlTransfomer {

	int currentPage = 1;
	String currentSectionName = "";
	
	public MilStdTransfomer(String templateFile) {
		super(templateFile);
	}

	@Override
	protected void transformFile(Path input) {
		ST st = getStringTemplateFolder().getInstanceOf("doc");
		try {
			Document doc = Jsoup.parse(input.toFile(), "UTF-8");
			MilStd962DSelfCover selfCover = generateSelfCover(doc.body().select("div").first());
			
			ST selfCoverST = getStringTemplateFolder().getInstanceOf("selfcover");
			selfCoverST.add("selfcover", selfCover);
			st.add("selfcover", selfCoverST.render());
			st.add("docName",  selfCover.getRevision().getId());
			doc.body().select("div").first().remove();//remove first page
	
			List<DocDivision> divs = getDivs(doc.body().select("div"));
			
			String sections = transformDivs(divs);
			st.add("sections", sections);

			Path output = Paths.get(input.toFile().getParentFile().getAbsolutePath(), "result.xml");
			
//			logger.info("write to file " + output);

			FileUtils.writeStringTemplateToFile(output, st);
			
		} catch (Exception ex) {ex.printStackTrace(System.out); }
	}
	
	private MilStd962DSelfCover generateSelfCover(Element div) {
		ST st = getStringTemplateFolder().getInstanceOf("selfcover");
		STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
		parser.parse(div);
		return parser.getSelfCover();
	}
	
	/**
	 * collect meta data about each div
	 * help to merge, to create page and sectionss
	 * @param elements
	 * @return
	 */
	private List<DocDivision> getDivs(List<Element> elements) {
		return
		elements.stream().map(e -> {
			DocDivision div = new DocDivision();
			div.setHtml(e.toString());
			if (MilStdDocumentAnalyzer.doesDivContainPageNumber(e)) {
				int page = MilStdDocumentAnalyzer.getDivPageNumber(e);
				div.setPageNumber(page);
				this.currentPage = page;
			} else {
				div.setPageNumber(currentPage);
			}
			String localSectionName = MilStdDocumentAnalyzer.getSectionName(e);
			
			if ("unknown".equals(localSectionName)) {
				div.setSectionName(this.currentSectionName);
			}
			else {
				div.setSectionName(localSectionName);
				this.currentSectionName = localSectionName;
			}
			return div;
		})
		.collect(Collectors.toList());
	}
	
	/**
	 * merge div if applicable
	 * create pages
	 * create sections
	 * @param divs
	 * @return
	 */
	private String transformDivs(List<DocDivision> divs) {
		ST st = getStringTemplateFolder().getInstanceOf("sections");
		List<String> sections = new ArrayList<>();
		List<String> pages = new ArrayList<>();
		List<String> subdivs = new ArrayList<>();
		
		Deque<DocDivision> queue = new ArrayDeque<>(divs);
		
		DocDivision current = queue.pop();
		
		
		while (!queue.isEmpty()) {
			DocDivision next = queue.pop();
			subdivs.add(current.getHtml());
			
			if (next.getPageNumber() == current.getPageNumber()) {
				current = next;
				continue;
			}
			
			if (next.getPageNumber() != current.getPageNumber()) {
				ST page = getStringTemplateFolder().getInstanceOf("page");
				page.add("divs", subdivs);
				page.add("number", current.getPageNumber());
				pages.add(page.render());
				subdivs.clear();
			} 
			
			//next section
			if (!current.getSectionName().equals(next.getSectionName())) {
				ST section = getStringTemplateFolder().getInstanceOf("section");
				section.add("pages", pages);
				section.add("name", current.getSectionName());
				sections.add(section.render());
				pages.clear();
				subdivs.clear();
			}
			current = next;
		}
		
		if (!subdivs.isEmpty() ) {
			ST page = getStringTemplateFolder().getInstanceOf("page");
			page.add("divs", subdivs);
			page.add("number", current.getPageNumber());
			pages.add(page.render());
			subdivs.clear();
		}
		
		if (!pages.isEmpty()) {
			ST section = getStringTemplateFolder().getInstanceOf("section");
			section.add("pages", pages);
			section.add("name", current.getSectionName());
			sections.add(section.render());
			pages.clear();
			subdivs.clear();
		}
		st.add("sections", sections);
		return st.render();
	}
	
	public static void main(String[] s) throws Exception {
		new MilStdTransfomer("./src/main/templates/std962.stg")
		.transformFile(Paths.get("/media/paul/workspace/pdftest/334566B59C9340B78ED202A31F4E7B15/clean.html"));
		
//		new MilStdTransfomer("./src/main/templates/std962.stg")
//		.transform(Paths.get("/media/paul/workspace/pdftest/")
//				, Paths.get("/media/paul/workspace/pdftest/"));
	}

}
