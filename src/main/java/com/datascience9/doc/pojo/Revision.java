package com.datascience9.doc.pojo;

import java.util.ArrayList;
import java.util.List;

public class Revision {
	String id;
	String date;
	String withChange;
	String section6;
	List<Revision> supercedes = new ArrayList<>();
	
	public String toString() {
		 StringBuilder builder = new StringBuilder();
		 builder.append(id + "\n" + ((null != withChange) ? withChange  + "\n": "") + date).append("\n");
		 
		 supercedes.stream().forEach(e -> builder.append(e.toString()).append("\n"));
		 return builder.toString();
	 }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWithChange() {
		return withChange;
	}

	public void setWithChange(String withChange) {
		this.withChange = withChange;
	}

	public List<Revision> getSupercedes() {
		return supercedes;
	}

	public void setSupercedes(List<Revision> supercedes) {
		this.supercedes = supercedes;
	}

	public String getSection6() {
		return section6;
	}

	public void setSection6(String section6) {
		this.section6 = section6;
	}
}
