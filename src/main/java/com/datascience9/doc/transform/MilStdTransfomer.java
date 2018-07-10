package com.datascience9.doc.transform;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.stringtemplate.v4.ST;

import com.datascience9.doc.metaanalysis.MilStdDocumentAnalyzer;
import com.datascience9.doc.pojo.DocDivision;
import com.datascience9.doc.pojo.MilStd962DSelfCover;
import com.datascience9.doc.util.DocumentConverterHelper;
import com.datascience9.doc.util.FileUtils;

public class MilStdTransfomer extends Html2XmlTransfomer {
	static final String regex = "^[A-Z0-9]{1,2}[.](.*)";
	static final String regex1 = "^[A-Z]-.*";
	static final String regex2 = "^[VXI]{1,3}[.]*.*";
	MilStd962DSelfCover selfCover;
	List<TocSection> tocSections;
	public MilStdTransfomer(String templateFile) {
		super(templateFile);
	}

	/**
	 * top to botoom approach
	 * FOREWORD / FORWARD
	 * CONTENTS
	 * SCOPE
	 * ....
	 */
	@Override
	public void transformFile(Path input) {
		ST st = getStringTemplateFolder().getInstanceOf("doc");
		List<String> sections = new ArrayList<>();
		try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(input.toFile()))) {
			Document doc = Jsoup.parse(reader, "UTF-8", "", Parser.xmlParser());
			Deque<Element> queue = new ArrayDeque<>(doc.body().select("div"));
			selfCover = createSelfCover(queue.pop()); //cover page
			addCoverPage(st);

			//find foreword if any
			List<Element> forewords = new ArrayList<>();
			while (!queue.isEmpty() && !isContentSection(queue.peek())) {
				forewords.add(queue.pop());
			}
			sections.add(createForeword(forewords));
		
			//add toc
			List<Element> toc = new ArrayList<>();
			while (!queue.isEmpty() && isContentSection(queue.peek())) {
					toc.add(queue.pop());
			}
			sections.add(createTableOfContent(toc));

			sections.addAll(createAllRemainSections(queue));
			st.add("sections", sections);

			Path output = Paths.get(input.toFile().getParentFile().getAbsolutePath(), "result.xml");
			FileUtils.writeStringTemplateToFile(output, st);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		} 
	}

	private void addCoverPage(ST docST) {
		ST selfCoverST = getStringTemplateFolder().getInstanceOf("selfcover");
		selfCoverST.add("selfcover", selfCover);
		docST.add("selfcover", selfCoverST.render());
		docST.add("docName", selfCover.getRevision().getId());
	}
	
	/**
	 * Create a foreword section from Html DIV
	 * @param div
	 * @return
	 */
	private String createForeword(List<Element> divs) {
		ST st = getStringTemplateFolder().getInstanceOf("section");
		st.add("tag", "foreword");
		
		
		List<String> sub = divs.stream()
				.flatMap(div -> DocumentConverterHelper.findAllText(div).stream())
				.collect(Collectors.toList());
		
		List<String> subXML = sub.stream().map(s -> {
			ST paragraph = getStringTemplateFolder().getInstanceOf("paragraph");
			paragraph.add("id", encode(s));
			paragraph.add("text", s.replace("&", "&amp;"));
			return paragraph.render();
		}).collect(Collectors.toList());
		
		st.add("pages", subXML);
		return st.render();
	}
	
	
	private MilStd962DSelfCover createSelfCover(Element div) {
		STD962DSelfCoverParser parser = new STD962DSelfCoverParser();
		parser.parse(div);
		return parser.getSelfCover();
	}

	/**
	 * create toc from a list of HTML div
	 * @param list
	 * @return
	 */
	private String createTableOfContent(List<Element> divs) {
		ST sectionST = getStringTemplateFolder().getInstanceOf("section");
		this.tocSections = createTocSections(divs);
		List<String> xml = generateTocSectionXMLFromTocSections(this.tocSections);
		sectionST.add("name", "Table of Contents");
		sectionST.add("tag", "toc");
		sectionST.add("pages", xml);
		return sectionST.render();
	}
	
	private List<TocSection> createTocSections(List<Element> divs) {
		if (isContentTable(divs.get(0))) return createTocSectionsFromTable(divs);
		return createTocSectionsFromParagraph(divs);
	}
	
	private List<TocSection> createTocSectionsFromTable(List<Element> divs) {
		List<Element> tableRows = 
				divs
					.stream()
					.flatMap(e -> getAllTableRowsFromDiv(e).stream())
					.collect(Collectors.toList());

			return createTOCSectionsFromTableRows(tableRows);
	}
	
	private List<TocSection> createTocSectionsFromParagraph(List<Element> divs) {
		Deque<Element> paras = removeDivTags(divs);
		Deque<Element> clean = removeElementWithKeyword(paras, new String[] {"CONTENTS", "PARAGRAPH"});
		return createTOCSectionsFromPara(clean);
	}
	
	/**
	 * create a nested TOC sections based the natural numeric ordering
	 * 1. 
	 *   1.1
	 * 2. 
	 *   2.1
	 * @param rows
	 * @return
	 */
	private List<TocSection> createTOCSectionsFromPara(Deque<Element> queue) {
		List<TocSection> tocSections = new ArrayList<>();

		while (!queue.isEmpty()) {
			List<String> items = retrieveListOfItemsFromPara(queue);
			if (items.get(0).trim().startsWith("FOREWORD")
					|| items.get(0).trim().startsWith("FORWARD")) 
			{
				tocSections.add(new TocSection(null, items.get(0), items.get(items.size()-1)));
			} 
			//regular section with page number
			else if (items.get(0).matches(regex) 
					|| items.get(0).matches(regex1)
					|| items.get(0).matches(regex2)) 
			{
				TocSection sec = new TocSection(items.get(0), items.get(1), items.get(items.size()-1));
				if (!tocSections.isEmpty() 
						&& null != tocSections.get(tocSections.size()-1).number 
						&& sec.number.startsWith(tocSections.get(tocSections.size()-1).number)) {
					tocSections.get(tocSections.size()-1).add(sec);
				}
				else tocSections.add(sec);
			}
			//FIGURE and TABLE sections
			else if (items.get(0).startsWith("FIGURE")
					|| items.get(0).startsWith("TABLE")) 
			{
				TocSection sec = new TocSection(null, items.get(0), null);
				tocSections.add(sec);
				//add sub sections
				while (!queue.isEmpty() 
						&& (queue.peek().text().matches(regex) 
								|| queue.peek().text().matches(regex1)
								|| queue.peek().text().matches(regex2)
								|| queue.peek().text().startsWith(items.get(0)))) {
					if (queue.peek().text().startsWith(items.get(0))) {
						queue.pop(); //skip
						continue;
					}
					
					List<String> tds = retrieveListOfItemsFromPara(queue);
					TocSection local = new TocSection(tds.get(0), tds.get(1), tds.get(tds.size()-1));
					sec.add(local);
				}
			}
			else {
				tocSections.add( new TocSection(null, items.get(0), null));
			}
		}

		return tocSections;
	}
	
	/**
	 * Retrieve a list tds from a table row
	 * @param row
	 * @return
	 */
	private List<String> retrieveListOfItemsFromPara(Deque<Element>	queue) {
		Element current = queue.pop();
		
		List<String> items = DocumentConverterHelper.findElementHasText(current)
				.stream()
				.filter(td -> !StringUtils.isEmpty(td.text().trim()))
				.map(ele -> ele.text())
				.collect(Collectors.toList());
		
		while (!queue.isEmpty() && items.size() < 3 && items.get(items.size()-1).matches("[0-9]+")) {
			List<String> subItems = DocumentConverterHelper.findElementHasText(current)
					.stream()
					.filter(td -> !StringUtils.isEmpty(td.text().trim()))
					.map(ele -> ele.text())
					.collect(Collectors.toList());
			
			//concat following string to the previous str
			items.get(items.size() - 1).concat(subItems.get(0));
			
			//it is likely the end of the item
			if (subItems.size() > 1) items.add(subItems.get(subItems.size() - 1));
		}
		
		return items;
	}
	
	private List<String> createAllRemainSections(Deque<Element> queue) {
		List<String> result = new ArrayList<>();
		Deque<Element> allsub = removeDivTags(new ArrayList<Element>(queue));
		
		//select only top sections from the TOC
		Deque<TocSection> remainingSections = this.tocSections.stream()
				.filter(tocSection -> tocSection.number != null 
				&& (tocSection.number.matches("[0-9]+[.]")))
				.collect(Collectors.toCollection(ArrayDeque::new));
		
		System.out.println(remainingSections);
		
		while (!remainingSections.isEmpty() && !allsub.isEmpty()) {
			TocSection sec = remainingSections.pop();

			ST sectionST = getStringTemplateFolder().getInstanceOf("section");
			sectionST.add("name", sec.text.replace(".", ""));
			sectionST.add("tag", "sec");
			sectionST.add("pages", extractSections(sec, remainingSections.peek(), allsub));
			result.add(sectionST.render());
		}
			
		return result;
	}
	
	private List<String> extractSections(TocSection start, TocSection end, Deque<Element> queue) {
		List<String> result = new ArrayList<>();
		System.out.println(start + " " + end);
	
		while (!queue.isEmpty() 
				&& !compareText(start, queue.peek())) queue.pop();
		
		System.out.println("found section " + queue.peek());
		//add
		while (!queue.isEmpty() 
				&& !compareText(end, queue.peek())) result.add(queue.pop().toString());
		
//		System.out.println(result);
		return result;
	}
	
	private boolean compareText(TocSection section,  Element e) {
		if (null == section) return false;
		if (null == e.text()) return false;
		String[] tokens = section.text.split(" ");
		boolean result = e.text().trim().startsWith(section.number) && e.text().contains(tokens[0]);

		if (!result) {
			
			if (section.children.isEmpty()) return result;
			else {
				System.out.println("cannot find " + section.text + ", " + section.children.get(0));
				String[] subtokens = section.children.get(0).text.split(" ");
				result = e.text().trim().startsWith(section.children.get(0).number) 
						&& e.text().trim().toLowerCase().contains(subtokens[0].toLowerCase());
			}
		}
		return result;
	}
	
	/**
	 * remove <div> tags
	 * @param queue
	 * @return
	 */
	private Deque<Element> removeDivTags(List<Element> queue) {
		return queue.stream()
		.flatMap(e -> removeDivTags(e).stream())
		.collect(Collectors.toCollection(ArrayDeque::new));
	}
	
	/**
	 * remove div recursively
	 * @param e
	 * @return
	 */
	private List<Element> removeDivTags(Element e) {
		List<Element> result = new ArrayList<>();
		if (!"div".equalsIgnoreCase(e.tagName())) result.add(e);
		else result.addAll(removeDivTags(e.children()));
		return result;
	}
	
	/**
	 * get all table rows from a list of div
	 * exclude rows that do not start with [0-9].
	 * each table row is a section in the toc
	 * <tr class="r17"> 
     <td class="td8" colspan="3"> <p class="p2"> <span>PARAGRAPH</span> </p> </td>
     <td class="td6">  </td>
     <td class="td9"> <p class="p24"> <span>PAGE</span> </p> </td> 
    </tr> 
    .filter(s -> s.text().matches("[0-9](.*)") 
	 * @param divs
	 * @return
	 */
	private List<Element> getAllTableRowsFromDiv(Element div) {
			List<Element> children = DocumentConverterHelper.findElement(div, "tr");
			return children.stream()
					.filter(s -> !s.text().startsWith("PARAGRAPH"))
					.collect(Collectors.toList());
	}
	
	/**
	 * create a nested TOC sections based the natural numeric ordering
	 * 1. 
	 *   1.1
	 * 2. 
	 *   2.1
	 * @param rows
	 * @return
	 */
	private List<TocSection> createTOCSectionsFromTableRows(List<Element> rows) {
		List<TocSection> tocSections = new ArrayList<>();
		Deque<Element> queue = new ArrayDeque<>(rows);
		while (!queue.isEmpty()) {
			Element row = queue.pop();
			List<String> cleanTDs = retrieveListOfTdFromRow(row);
			
			if (row.text().trim().startsWith("FOREWORD")
					|| row.text().trim().startsWith("FORWARD")) 
			{
				tocSections.add(new TocSection(null, cleanTDs.get(0), cleanTDs.get(cleanTDs.size()-1)));
			} 
			//regular section with page number
			else if (row.text().matches(regex) 
					|| row.text().matches(regex1)
					|| row.text().matches(regex2)) 
			{
				TocSection sec = new TocSection(cleanTDs.get(0), cleanTDs.get(1), cleanTDs.get(cleanTDs.size()-1));
				if (!tocSections.isEmpty() 
						&& null != tocSections.get(tocSections.size()-1).number 
						&& sec.number.startsWith(tocSections.get(tocSections.size()-1).number)) {
					tocSections.get(tocSections.size()-1).add(sec);
				}
				else tocSections.add(sec);
			}
			//FIGURE and TABLE sections
			else if (row.text().startsWith("FIGURE")
					|| row.text().startsWith("TABLE")) 
			{
				TocSection sec = new TocSection(null, row.text(), null);
				tocSections.add(sec);
				//add sub sections
				while (!queue.isEmpty() 
						&& (queue.peek().text().matches(regex) 
								|| queue.peek().text().matches(regex1))
						|| queue.peek().text().matches(regex2)) {
					Element lookahead = queue.pop();
					List<String> tds = retrieveListOfTdFromRow(lookahead);
					TocSection local = new TocSection(tds.get(0), tds.get(1), tds.get(tds.size()-1));
					sec.add(local);
				}
			}
			else {
				tocSections.add( new TocSection(null, row.text(), null));
			}
		}

		return tocSections;
	}
	
	/**
	 * Retrieve a list tds from a table row
	 * @param row
	 * @return
	 */
	private List<String> retrieveListOfTdFromRow(Element row) {
		return DocumentConverterHelper.findElement(row, "td")
				.stream()
				.filter(td -> !StringUtils.isEmpty(td.text().trim()))
				.map(ele -> ele.text())
				.collect(Collectors.toList());
	}
	
	/**
	 * generate toc for XML
	 * @param tocSections
	 * @return
	 */
	private List<String> generateTocSectionXMLFromTocSections(List<TocSection> tocSections) {
		return tocSections.stream().map(toc -> {
			ST tocSection = getStringTemplateFolder().getInstanceOf("tocsection");
			if (null != toc.number) tocSection.add("id", toc.number);
			if (null != toc.page) tocSection.add("page", toc.page);
			tocSection.add("text", toc.text);
			tocSection.add("tag", "sec");
			if (!toc.children.isEmpty()) tocSection.add("sub", generateSubSections(toc.children));
			return tocSection.render();
		})
		.collect(Collectors.toList());
	}
	
	/**
	 * generate sub sections from a list of sub sections
	 * @param subs
	 * @return
	 */
	private List<String> generateSubSections(List<TocSection> subs) {
		List<String> result = new ArrayList<>();
		subs.forEach(toc -> {
			ST tocSection = getStringTemplateFolder().getInstanceOf("tocsection");
			tocSection.add("id", toc.number);
			tocSection.add("page", toc.page);
			tocSection.add("text", toc.text);
			tocSection.add("tag", "sub");
			result.add(tocSection.render());
		});
		return result;
	}
	
	public static String encode(final String text) {
		try {
			return new String(Base64.getEncoder()
			    .encode(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8))));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private boolean isContentSection(Element e) {
		return isContentTable(e) || isContentParagraph(e);
	}
	
	private boolean isContentParagraph(Element e) {
		if (e.select("p").size() < 1) return false;
		return  (e.select("p").first().text().contains("CONTENTS")
				|| (e.select("p").first().text().contains("PARAGRAPH")
						&& e.select("p").first().text().contains("PAGE")));
	}
	
	private boolean isContentTable(Element e) {
		String[] ISTABLES = { "CONTENTS", "PARAGRAPH", "TABLE", "FIGURE", "CONCLUDING MATERIAL"};
		if (e.select("table").size() < 1) return false;
		String text = e.select("table").select("tr").first().text();
		return Arrays.stream(ISTABLES).anyMatch(s -> text.contains(s));
	}

	private Deque<Element> removeElementWithKeyword(Deque<Element> queue, String[] keywords) {
		return queue.stream().filter(e -> e.hasText() && !Arrays.asList(keywords).contains(e.text()))
		.collect(Collectors.toCollection(ArrayDeque::new));
	}
}

class TocSection {
	@Override
	public String toString() {
		return "TocSection [number=" + number + ", text=" + text + ", page=" + page +  "]";
	}
	String number;
	String text;
	String page;
	
	public TocSection(String n, String t, String p) {
		this.number = n;
		this.text = t;
		this.page = p;
	}
	List<TocSection> children = new ArrayList();
	
	public void add(TocSection sec) { children.add(sec); }
}