package com.datascience9.doc.pojo;

public class DocDivision {
	String html;
	int pageNumber;
	String sectionName;
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	@Override
	public String toString() {
		return "DocDivision [pageNumber=" + pageNumber + ", sectionName=" + sectionName + "]";
	}
}
