package com.smart.platform.client;

import java.io.File;
import java.util.Enumeration;

import org.apache.log4j.Category;

import com.smart.platform.filedb.DirHelper;
import com.smart.platform.util.DefaultNPParam;

public class SystemexitThread extends Thread{
	Category logger=Category.getInstance(SystemexitThread.class);
	public void run(){
		DefaultNPParam.systemrunning=false;
		logger.debug("��������ļ�");
		DirHelper.clearFileonexit();
		logger.info("ϵͳ�˳�System exit");
	}
}
