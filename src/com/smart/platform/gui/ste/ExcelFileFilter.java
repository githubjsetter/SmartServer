package com.smart.platform.gui.ste;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExcelFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory())return true;
		if (f.getName().toLowerCase().endsWith(".xls"))
			return true;
		return false;
	}

	@Override
	public String getDescription() {
		return "ExcelÎÄ¼þ (*.xls)";
	}

}
