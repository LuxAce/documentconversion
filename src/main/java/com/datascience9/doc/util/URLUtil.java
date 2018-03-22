package com.datascience9.doc.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLUtil {
	static Logger logger = LoggingUtil.getDeveloperLogger(URLUtil.class.getName());
	public static boolean checkReference(String urlStr) {
		try {
  		URL url = new URL(urlStr);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
  
      int code = connection.getResponseCode();
      System.out.println("Response code of the object is "+code);
      if (code==200) return true;
      else return false;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Unable to connect to " + urlStr);
			return false;
		}
	}
}
