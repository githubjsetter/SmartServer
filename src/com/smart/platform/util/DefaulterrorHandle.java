package com.smart.platform.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Category;


public class DefaulterrorHandle   implements UncaughtExceptionHandler{
	Category logger=Category.getInstance(DefaulterrorHandle.class);
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("�߳�:"+t+" δ����Ĵ��� ERROR:",e);
	}

}
