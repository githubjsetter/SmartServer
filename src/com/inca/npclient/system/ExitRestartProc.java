package com.inca.npclient.system;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Category;

import com.inca.npclient.download.LauncherManager;

public class ExitRestartProc implements SystemexitProcessIF {
	Category logger = Category.getInstance(ExitRestartProc.class);

	public void process() {
		restartVm();

	}

	void restartVm() {
		Properties props = System.getProperties();
		String java = props.getProperty("sun.boot.library.path") + "/javaw";
		String classpath = props.getProperty("java.class.path");
		String strdir = props.getProperty("user.dir");

		boolean haschart=false;
		Enumeration<String> en = LauncherManager.getInst().getLaunchjars()
				.elements();
		while (en.hasMoreElements()) {
			String jarname = en.nextElement();
			classpath = "lib\\" + jarname + ";" + classpath;
			if(jarname.indexOf("npbichart")>=0){
				haschart=true;
			}
		}
		if(!haschart){
			String jarname="npbichart-2.3.1.jar";
			classpath = "lib\\" + jarname + ";" + classpath;
		}

		String cmd = java + " -Xms64M -Xmx1024M -Xincgc -verbose:gc -cp \""
				+ classpath + "\" com.inca.npclient.Startnpclient";
		logger.info(cmd);

		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec(cmd, null, new File(strdir));
		} catch (Exception e) {
			logger.error("error", e);
		}

	}

}
