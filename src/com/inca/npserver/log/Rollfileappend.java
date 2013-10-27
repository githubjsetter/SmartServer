package com.inca.npserver.log;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

public class Rollfileappend extends DailyRollingFileAppender{

	public Rollfileappend(Layout layout, String filename, String datePattern)
			throws IOException {
		super(layout, filename, datePattern);
	}

}
