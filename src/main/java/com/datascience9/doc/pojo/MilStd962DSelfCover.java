package com.datascience9.doc.pojo;

import java.util.ArrayList;
import java.util.List;

public class MilStd962DSelfCover {
	String systemId;
	Revision revision = null;
	List<String> heading = new ArrayList<>();
	List<String> title = new ArrayList<>();;
	String ams;
	String distribution;
	String imageStr;
	String federalSupplyClass;
	String fsc;
	
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public Revision getRevision() {
		return revision;
	}
	public void setRevision(Revision revision) {
		this.revision = revision;
	}
	public List<String> getHeading() {
		return heading;
	}
	
	public List<String> getTitle() {
		return title;
	}

	public String getDistribution() {
		return distribution;
	}
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	public String getImageStr() {
		return imageStr;
	}
	public void setImageStr(String imageStr) {
		this.imageStr = imageStr;
	}
	public String getAms() {
		return ams;
	}
	public void setAms(String ams) {
		this.ams = ams;
	}
	public String getFederalSupplyClass() {
		return federalSupplyClass;
	}
	public void setFederalSupplyClass(String federalSupplyClass) {
		this.federalSupplyClass = federalSupplyClass;
	}
	public String getFsc() {
		return fsc;
	}
	public void setFsc(String fsc) {
		this.fsc = fsc;
	}
	@Override
	public String toString() {
		return "MilStd962DSelfCover [systemId=" + systemId + ", revision=" + revision + ", heading=" + heading + ", title="
		    + title + ", ams=" + ams + ", distribution=" + distribution + ", imageStr=" + imageStr + ", federalSupplyClass="
		    + federalSupplyClass + ", fsc=" + fsc + "]";
	}
}
