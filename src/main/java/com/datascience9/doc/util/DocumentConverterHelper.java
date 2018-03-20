package com.datascience9.doc.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.datascience9.doc.transform.SelfCoverParser;

public abstract class DocumentConverterHelper {
	public static String getFileNameWithNewExtension(String fileName, String newExtension) {
		return fileName.substring(0, fileName.lastIndexOf(".") + 1) + newExtension;
	}
	
	public static String getFileNameWithoutExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	public static Deque<String> stripTag(Elements list) {
		List<String> result = list.stream()
		.filter(e -> StringUtils.isNotEmpty(e.text())
						||   e.toString().contains("<img "))
		.map(e -> {
			if (StringUtils.isEmpty(e.text())) {
				return e.select("img").attr("src");
			} else return e.text();
		})
		.collect(Collectors.toList());
		
//		//add img back if applicable
//		List<String> img = list.stream()
//				.filter(e -> e.toString().contains("<img"))
//				.map(e -> e.select("img").attr("src"))
//				.collect(Collectors.toList());
//		result.addAll(img);
		Deque<String> queue = new ArrayDeque<>(result);
		return queue;
	}
}
