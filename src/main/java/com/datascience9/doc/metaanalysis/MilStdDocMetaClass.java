package com.datascience9.doc.metaanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.datascience9.doc.analysis.DocMetaClass;

public class MilStdDocMetaClass extends DocMetaClass {

	static final String[] requiredSections = 
		{
				"FOREWORD",
				"CONTENTS",
				"SCOPE",
				"APPLICABLE DOCUMENTS",
				"DEFINITIONS",
				"GENERAL REQUIREMENTS",
				"DETAILED REQUIREMENTS",
				"NOTES",
				"APPENDIX",
		};
	
	
	Set<String> sectionNames = new TreeSet<>();
	List<String> missingSections = new ArrayList<>();
	String docType;
	String docId;
	String systemId;
	
	public MilStdDocMetaClass() {
		missingSections.addAll(Arrays.asList(requiredSections));
	}
	
	public static String getForeword() { return requiredSections[0]; }
	public static String getContents() { return requiredSections[1]; }
	public static String getScope() { return requiredSections[2]; }
	public static String getApplicableDocuments() { return requiredSections[3]; }
	public static String getdefinitions() { return requiredSections[4]; }
	public static String getGeneralRequirements() { return requiredSections[5]; }
	public static String getDetailedRequirements() { return requiredSections[6]; }
	public static String getNotes() { return requiredSections[7]; }
	public static String getAppendix() { return requiredSections[8]; }
	
	public void addSectionName(String name) {
		this.sectionNames.add(name);
		int index = missingSections.indexOf(name);
		if (index >=0) missingSections.remove(index);
	}

	@Override
	public String toString() {
		return "MilStdDocMetaClass [sectionNames=" + sectionNames + ", missingSections=" + missingSections + ", docType="
		    + docType + ", docId=" + docId + ", systemId=" + systemId + ", getNumberOfPages()=" + getNumberOfPages() + "]";
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	

}
