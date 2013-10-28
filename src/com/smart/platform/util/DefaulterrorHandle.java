package com.smart.platform.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Category;


public class DefaulterrorHandle   implements UncaughtExceptionHandler{
	Category logger=Category.getInstance(DefaulterrorHandle.class);
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("线程:"+t+" 未捕获的错误 ERROR:",e);
	}

}
