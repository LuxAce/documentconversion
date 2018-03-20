package com.datascience9.doc.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ReportEvent extends PdfPageEventHelper 
{
  
  public void onOpenDocument(PdfWriter writer, Document document) {
  }       
  
  public void onStartPage(PdfWriter writer, com.lowagie.text.Document document) {
//  	document.setHeader(getHeader(courseName));
  }

  public void onEndPage(PdfWriter writer, com.lowagie.text.Document document) {
//	  document.setFooter(getFooter(document));
//	  drawRectangle(document, writer);
  }

  public void onCloseDocument(PdfWriter writer, Document document) {
//     tpl.beginText();
//     tpl.setFontAndSize(helv, 12);
//     tpl.setTextMatrix(0, 0);
//     tpl.showText("" + (writer.getPageNumber() - 1));
//     tpl.endText();
  }
}
