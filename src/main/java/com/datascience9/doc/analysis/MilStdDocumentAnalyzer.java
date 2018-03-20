package com.datascience9.doc.analysis;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.datascience9.doc.pojo.DocTypeEnum;
import com.datascience9.doc.transform.SelfCoverParser;
import com.datascience9.doc.util.DocumentConverterHelper;
import com.datascience9.doc.util.JsonUtils;

public class MilStdDocumentAnalyzer extends HtmlAnalyzer {
	MilStdDocMetaClass metaClass = new MilStdDocMetaClass();
	
	int currentPage = 0;
	
	@Override
	public void collectMeta(Path input) {
		logger.log(Level.FINER, "Start analyzing .." + input);
		try {
  		Document doc = Jsoup.parse(input.toFile(), "UTF-8");
  		collectStatistics(doc);
  		
  		Path output = Paths.get(input.getParent().toString(), "doc.json");
  		JsonUtils.write2File(output, metaClass);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
	
	
	private void collectStatistics(Document doc) {
		if (!doesBodyContainOnlyDiv(doc.body())) {
			developerLogger.info("document has other elements besides div");
			return;
		}
		
		doc.body().children().stream().forEach(e -> {
			if (doesDivContainPageNumber(e)) {
				int page = getDivPageNumber(e);
				metaClass.setNumberOfPages(page);
			} 
			metaClass.addSectionName(getSectionName(e));
		});
		
		analyzeSelfCoverPage(doc.body().children().first());
	}
	
	private void analyzeSelfCoverPage(Element div) {
		Deque<String> queue = DocumentConverterHelper.stripTag(div.children());
		boolean foundId = false;
		
		if (queue.isEmpty()) return;
		while (!queue.isEmpty()) {
			String nextToken = queue.pop();
			
			if (Arrays.stream(SelfCoverParser.SYSTEM_IDENT).anyMatch(str -> str.equals(queue.peek()))) {
				metaClass.setSystemId(nextToken);
				continue;
			}
			
			if (nextToken.startsWith("MIL-")) {
				if (!foundId) {
					metaClass.setDocId(nextToken);
					foundId = true;
				}
				continue;
			}
			
			if (Arrays.stream(SelfCoverParser.DEPARTMENT_STANDARD).anyMatch(x -> x.equals(nextToken))) {
				metaClass.setDocType(DocTypeEnum.MILSTD962D.toString());
				continue;
			} 
			
			if (Arrays.stream(SelfCoverParser.PERFORMANCESPEC).anyMatch(x -> x.equals(nextToken))) {
				metaClass.setDocType(DocTypeEnum.PERFORMANCE_SPECIFICATION_SHEET.toString());
				continue;
			} 
			
			break;
		}
		
	}
	
	private boolean doesBodyContainOnlyDiv(Element body) {
		return body.children().stream().allMatch(div -> "div".equals(div.tagName()));
	}
	
	public static void main(String[] s) throws Exception {
		new MilStdDocumentAnalyzer()
		.sanitizer(Paths.get("/media/paul/workspace/pdftest/")
				, Paths.get("/media/paul/workspace/pdftest/"));
	}
	
	class Division {
		@Override
		public String toString() {
			return "Division [page=" + page + ", sectionName=" + sectionName + "]";
		}

		Element text;
		int page;
		String sectionName;

	}
	
	public static String getSectionName(Element div) {
		Deque<String> queue = DocumentConverterHelper.stripTag(div.getAllElements());
		List<String> list = new ArrayList<>(queue);
		if (isPreamble(list)) return "PREAMPLE";
		if (isForeWord(list)) return MilStdDocMetaClass.getForeword();
		if (isContent(list)) return MilStdDocMetaClass.getContents();
		if (isScope(list)) return MilStdDocMetaClass.getScope();
		if (isReference(list)) return "REFERENCES";
		if (isApplicable(list)) return MilStdDocMetaClass.getApplicableDocuments();
		if (isDefinition(list)) return MilStdDocMetaClass.getdefinitions();
		if (isGeneralRequirement(list)) return MilStdDocMetaClass.getGeneralRequirements();
		if (isDetailedRequirement(list)) return MilStdDocMetaClass.getDetailedRequirements();
		if (isNote(list)) return MilStdDocMetaClass.getNotes();
		if (isAppendix(list)) return getAppendixName(list);
		return "unknown";
	}
	
	private static boolean isPreamble(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("This standard is approved for use by")
											|| str.contains("within the distribution limitations noted at the bottom of the cover"));
	}
	
	private static boolean isForeWord(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("FOREWORD"));
	}

	private static boolean isContent(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("CONTENTS"));
	}
	
	private static boolean isScope(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("SCOPE"));
	}
	
	private static boolean isReference(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("REFERENCED DOCUMENTS"));
	}
	
	private static boolean isApplicable(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("APPLICABLE DOCUMENTS"));
	}
	
	
	private static boolean isDefinition(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("DEFINITIONS"));
	}
	
	private static boolean isGeneralRequirement(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("GENERAL REQUIREMENTS"));
	}
	
	private static boolean isDetailedRequirement(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("DETAILED REQUIREMENTS"));
	}
	
	private static boolean isNote(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.contains("NOTES"));
	}
	
	private static boolean isAppendix(List<String> list) {
		return list
				.stream()
				.anyMatch(str -> str.startsWith("APPENDIX"));
	}
	
	private static String getAppendixName(List<String> list) {
		Optional<String> name =
				list
				.stream()
				.filter(str -> str.startsWith("APPENDIX"))
				.findFirst();
		return name.get();
	}
	
	public static boolean doesDivContainPageNumber(Element div) {
		return div.children()
				.stream()
				.anyMatch(e -> e.getElementsByTag("a") != null
						&&   e.getElementsByTag("a").attr("name") != null
						&&   e.getElementsByTag("a").attr("name").contains("page"));
	}
	
	public static int getDivPageNumber(Element div) {
		Optional<Element> result = 
		div.children()
		.stream()
		.filter(e -> e.getElementsByTag("a") != null
						&&   e.getElementsByTag("a").attr("name") != null
						&&   e.getElementsByTag("a").attr("name").contains("page"))
		.findFirst();
		
		if (!result.isPresent()) return -1;
		
		Element p = result.get();
		String a = p.getElementsByTag("a").attr("name");
		return Integer.parseInt(a.substring("page".length()));
	}
	
	
}
