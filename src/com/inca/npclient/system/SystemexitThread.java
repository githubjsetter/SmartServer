package com.inca.npclient.system;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.filedb.DirHelper;
import com.inca.np.util.DefaultNPParam;

public class SystemexitThread extends Thread {
	Category logger=Category.getInstance(SystemexitThread.class);
	
	public SystemexitThread() {
		super();
		addExitproc(new ExitcopyfileProc());
	}

	static Vector<SystemexitProcessIF> exitprocs=new Vector<SystemexitProcessIF>();
	public void run(){
		logger.info("System exit,process clean....");
		DefaultNPParam.systemrunning=false;
		Enumeration<SystemexitProcessIF> en =exitprocs.elements();
		while(en.hasMoreElements()){
			SystemexitProcessIF proc=en.nextElement();
			proc.process();
		}
	}
	
	public static void addExitproc(SystemexitProcessIF proc){
		exitprocs.add(proc);
	}
}
