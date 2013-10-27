package com.inca.npserver.log;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.inca.npserver.server.sysproc.CurrentappHelper;

public class NpLogManager {
	
	private static NpLogManager instance=null;
	Rollfileappend appender=null;
	Logger approotlogger=null;
	
	private NpLogManager(){
		PatternLayout pl=new PatternLayout("%d-%-5r %-5p [%c] (%t:%x) %m%n");
		File appdir=CurrentappHelper.guessAppdir();
		File logdir=new File(appdir,"logs");
		logdir.mkdirs();
		File logfile=new File(logdir,"npserver_app.log");
		
		try {
			appender=new Rollfileappend(pl,logfile.getAbsolutePath(),"'.'yyyy-MM-dd");
		} catch (IOException e) {
			Category.getInstance(NpLogManager.class).error("error",e);
			appender=null;
			return;
		}
		
		//µÇ¼Çappender
		approotlogger = Logger.getLogger("com.inca");
		approotlogger.addAppender(appender);
		approotlogger.setLevel(Level.DEBUG);
		
	}
	public static NpLogManager getInstance(){
		if(instance==null){
			instance=new NpLogManager();
		}
		return instance;
	}
	public Logger getApprootlogger() {
		return approotlogger;
	}
	
	
	
	
}
