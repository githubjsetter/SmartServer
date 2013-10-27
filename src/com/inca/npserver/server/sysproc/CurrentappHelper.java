package com.inca.npserver.server.sysproc;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Category;

public class CurrentappHelper {
	public static File getLibrarydir() {
		return new File(guessAppdir(), "WEB-INF/lib");
	}

	public static File getClassesdir() {
		return new File(guessAppdir(), "WEB-INF/classes");
	}

	// static Category logger= Category.getInstance(CurrentappHelper.class);
	public static File guessAppdir() {
		URL url = CurrentappHelper.class.getResource("CurrentappHelper.class");
		// logger.info("CurrentappHelper.class url="+url);

		if (url == null) {
			return new File(".");
		}

		String strurl = url.toString();
		int p = strurl.indexOf("/WEB-INF/");

		String s;
		if (p >= 0) {
			s = strurl.substring(0, p);
		} else {
			// /D:/npserver/build/classes/com/inca/npserver/server/sysproc/CurrentappHelper.class
			//在开发环境中， 找build/class
			p = strurl.indexOf("build/classes/com/inca/npserver");
			if(p<0)return new File(".");
			s = strurl.substring(0,p);
		}
		if (s.startsWith("jar:")) {
			s = s.substring(4);
		}

		if (s.startsWith("file:")) {
			s = s.substring(5);
		}
		return new File(s);
	}

}
