package com.datascience9.doc.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

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
		
		Deque<String> queue = DocumentConverterHelper.stripTag(div.children());

//		System.out.println(queue.toString());
		
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
			
			parseImage(queue);
			
			parseAms(queue);
			
			parseFsc(queue);
			
			parseDistributionStatement(queue);
			break;
		}
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
				&& !queue.peek().contains("AMSC")
				&& !isImage(queue.peek())
				&& !queue.peek().contains(queue.peek().toLowerCase())) {
			titles.add(queue.pop());
		}
		return titles;
	}
	
	private void parseDistributionStatement(Deque<String> queue) {
		selfCover.setDistribution(parseStartsWithArrayKeyWord(queue, DISTRIBUTION));
	}
	
	private void parseImage(Deque<String> queue) {
		selfCover.setImageStr(parseEndsWithArrayKeyWord(queue, IMAGES));
	}
	
	private void parseAms(Deque<String> queue) {
		selfCover.setAms(parseStartsWithArrayKeyWord(queue, AMS));
	}
	
	private void parseFsc(Deque<String> queue) {
		selfCover.setFsc(parseStartsWithArrayKeyWord(queue, FSC));
	}


	public MilStd962DSelfCover getSelfCover() {
		return selfCover;
	}
	
}
