package com.datascience9.doc.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;

import com.datascience9.doc.pojo.MilStd962DSelfCover;
import com.datascience9.doc.pojo.Revision;
import com.datascience9.doc.util.DocumentConverterHelper;

public class STD962DSelfCoverParser extends SelfCoverParser {

	private boolean debug = false;
	Element localDiv;
	MilStd962DSelfCover selfCover;
	
	public STD962DSelfCoverParser() {
		selfCover = new MilStd962DSelfCover();
	}
	
	public STD962DSelfCoverParser(boolean debug) {
		this();
		this.debug = debug;
	}
	
	@Override
	public void parse(org.jsoup.nodes.Element div) {
		parseSelfCover(div);
	}
	

	private void parseSelfCover(Element div) {
		//debug only
		this.localDiv = div;
		//debug
		
		Deque<String> queue = DocumentConverterHelper.getTextFromParagraph(div.children());
		List<String> images = DocumentConverterHelper.getImageFromHtml(div);
		List<String> asm = getAMSC(div);
		System.out.println(asm);
		while (!queue.isEmpty() ) {
			parseMeasurementIdentification(queue);
			selfCover.setRevision(parseRevision(queue));
			
			if (isDepartmentStandard(queue.peek())) {
				selfCover.getHeading().add(queue.pop());
			}
			
			if (isStandard(queue.peek())) {
				selfCover.getHeading().add(queue.pop());
			}
			selfCover.getTitle().addAll(parseTitle(queue));
			parseDistributionStatement(queue);
			break;
		}
		
		this.selfCover.setImageStr(images.get(0));
		this.selfCover.setAms(asm.get(0));
		this.selfCover.setFsc(asm.get(1));
	}
	/**
	 * A simple parser to parse a queue of tokens
	 * for revision
	 * @param queue
	 */
	private Revision parseRevision(Deque<String> queue) {

		Revision local = new Revision();
		if (isSection6(queue.peek())) {
			local.setSection6(queue.pop());
			return local;
		}
		
	 if (isIdentifier(queue.peek())) {
		 local.setId(queue.pop());
		 
		 if (isChanged(queue.peek().toUpperCase())) {
			 local.setWithChange(queue.pop());
		 }
		 if (!isDate(queue.peek())) {
			 System.err.println("Publication Date not found or invalid date:[" + queue.peek() + "]");
			 System.out.println(localDiv);
			 System.out.println(queue.toString());
			 return local;
		 }
		 local.setDate(queue.pop());
		 
		 //superseding
		 if (queue.peek().startsWith("SUPERSEDING")) {
			 queue.pop(); //skip superseding
			 local.getSupercedes().add(parseRevision(queue));
		 }
	 }
	 return local;
	}
	
	void parseMeasurementIdentification(Deque<String> queue) {
		String local = "";
		if (isMeasurementSystemId(queue.peek())) {
			local = queue.pop();
		}
	 if ("SENSITIVE".equals(queue.peek())) {
		local = local.concat("\n" + queue.pop());
	 }
	 selfCover.setSystemId(local);
	}
	
	private List<String> parseTitle(Deque<String> queue) {
		List<String> titles = new ArrayList<>();
		while (!queue.isEmpty() 
				&& !queue.peek().contains("DISTRIBUTION STATEMENT")) {
			titles.add(queue.pop());
		}
		return titles;
	}
	
	private void parseDistributionStatement(Deque<String> queue) {
		selfCover.setDistribution(parseStartsWithArrayKeyWord(queue, DISTRIBUTION));
	}

	public MilStd962DSelfCover getSelfCover() {
		return selfCover;
	}
	
	/**
	 * <td class="td1"> <p class="p5"> <span>AMSC N/A</span> </p> </td>
     <td class="td2"> <p class="p8"> <span>FSC 1510</span> </p> </td> 
     or
     <p class="p7"> <span class="s2">AMSC N/A</span><span class="s4"> </span><span class="s5">AREA PACK</span> </p>        
	 * @param element
	 * @return
	 */
	public static List<String> getAMSC(Element element) {
		if (DocumentConverterHelper.findElement(element, "table").isEmpty()) {
			Optional<Element> ams = element.children().stream()
					.filter(e -> "p".equalsIgnoreCase(e.tagName()))
					.filter(e -> e.hasText())
					.filter(e -> e.text().startsWith("AMSC"))
					.findFirst();
			if (!ams.isPresent()) throw new IllegalArgumentException("Cannot find AMS");
			return DocumentConverterHelper.findAllText(ams.get());
			
		} else {
  		List<Element> list = DocumentConverterHelper.findElement(element, "td");
  		return list.stream().map(e -> e.text()).collect(Collectors.toList());
		}
	}
	
}
