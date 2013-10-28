package com.smart.platform.gui.control;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.smart.platform.gui.ste.Querycond;


public class CHovbaseGeneral extends CHovBase {
	Category logger = Category.getInstance(CHovbaseGeneral.class);

	public CHovbaseGeneral(File configfile) throws HeadlessException {
		super();
		this.configfile = configfile;
		// 从configfile读出所需要的配置
		try {
			readDesc();
			readViewname();
			readDefaultsql();
			readQuerycond();
			readColumns();
		} catch (Exception e) {
			logger.error("error", e);
			throw new HeadlessException(e.getMessage());
		}
	}

	@Override
	protected TableModel createTablemodel() {
		return tablemodel;
	}

	@Override
	public String getDefaultsql() {
		return defaultsql;
	}

	@Override
	public Querycond getQuerycond() {
		return querycond;
	}
	
	public String getDesc(){
		return hovdesc;
	}

	
}
