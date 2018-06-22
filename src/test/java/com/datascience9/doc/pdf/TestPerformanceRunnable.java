package com.datascience9.doc.pdf;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.datascience9.doc.ConfigurationHelper;
import com.datascience9.doc.MainClass;

public class TestPerformanceRunnable implements Runnable {

	private String name;
	public TestPerformanceRunnable(String name) {
		this.name = name;
	}
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		Path input = Paths.get(ConfigurationHelper.getInputDir());
	  Path output = Paths.get(ConfigurationHelper.getOutputDir());
		MainClass.extractDoc2Html(input, output);
		MainClass.analyze(output, output);
		MainClass.analyzeMeta(output, output);
		MainClass.transform2XML(output, output);
		MainClass.generatePDF(output, output);
		long stopTime = System.currentTimeMillis();
		System.out.println("Thread " + name + ". Converting MS Word document to PDF takes: " + (stopTime - startTime) + " seconds");

	}

}
