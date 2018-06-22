package com.datascience9.doc.pdf;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.datascience9.doc.ConfigurationHelper;
import com.datascience9.doc.MainClass;

public class TestPerformance {
	
	public static void main(String[] s) {
		Runnable thread1 = new TestPerformanceRunnable("thread1");
		Runnable thread2 = new TestPerformanceRunnable("thread2");
		new Thread(thread1).start();
		new Thread(thread2).start();
	}
	
	@Test
	public void testPerformance1() throws Exception {
		long startTime = System.currentTimeMillis();
		Path input = Paths.get(ConfigurationHelper.getInputDir());
	  Path output = Paths.get(ConfigurationHelper.getOutputDir());
	  
	  MainClass.extractDoc2Html(input, output);
		MainClass.analyze(output, output);
		MainClass.analyzeMeta(output, output);
		MainClass.transform2XML(output, output);
		MainClass.generatePDF(output, output);
		long stopTime = System.currentTimeMillis();
		System.out.println("Converting MS Word document to PDF takes: " + (stopTime - startTime) + " milliseconds");
		
	}
}
