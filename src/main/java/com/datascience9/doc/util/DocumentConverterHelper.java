package com.datascience9.doc.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class DocumentConverterHelper {
	public static String getFileNameWithNewExtension(String fileName, String newExtension) {
		return fileName.substring(0, fileName.lastIndexOf(".") + 1) + newExtension;
	}
	
	public static String getFileNameWithoutExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	/**
	 * get text from paragraph
	 * @param list
	 * @return
	 */
	public static Deque<String> getTextFromParagraph(Elements list) {
		List<String> result = list.stream()
		.filter(e -> "p".equalsIgnoreCase(e.tagName()))
		.filter(e -> StringUtils.isNotEmpty(e.text()))
		.filter(e -> !e.text().startsWith("AMSC"))
		.map(e -> e.text())
		.collect(Collectors.toList());
		
		Deque<String> queue = new ArrayDeque<>(result);
		return queue;
	}
	
	
	
	/**
	 * Get  from an element
	 * @param element
	 * @return
	 */
	public static List<String> getImageFromHtml(Element element) {
		List<Element> images = findElement(element, "img");
		return images.stream().map(e -> e.attr("src")).collect(Collectors.toList());
	}
	
	/**
	 * Find element by tag name recursively
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static List<Element> findElement(Element root, String tagName) {
		List<Element> result = new ArrayList<>();
		if (tagName.equalsIgnoreCase(root.tagName())) {
			result.add(root);
		}
		else root.children().forEach(e -> result.addAll(findElement(e, tagName)));
		return result;
	}
	
	/**
	 * Find element by tag name recursively
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static List<String> findElementTextByTagName(Element root, String tagName) {
		List<String> result = new ArrayList<>();
		if (tagName.equalsIgnoreCase(root.tagName())) {
			result.add(root.text());
		}
		else root.children().forEach(e -> result.addAll(findElementTextByTagName(e, tagName)));
		return result;
	}
	
	/**
	 * Find element with text recursively
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static List<Element> findElementHasText(Element root) {
		List<Element> result = new ArrayList<>();
		if (root.children().isEmpty() && !StringUtils.isEmpty(root.text())) {
			result.add(root);
		}
		else root.children().forEach(e -> result.addAll(findElementHasText(e)));
		return result;
	}
	
	/**
	 * Find element with text recursively
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static List<String> findAllText(Element root) {
		List<String> result = new ArrayList<>();
		if (root.children().isEmpty() && !StringUtils.isEmpty(root.text())) {
			result.add(root.text());
		}
		else root.children().forEach(e -> result.addAll(findAllText(e)));
		return result;
	}
}
