package com.smart.platform.anyprint.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.anyprint.Datasource;

/**
 * 打印数据源文件管理
 * 
 * @author Administrator
 * 
 */
public class DatasourceStorer {
	static String postfix = ".printds";

	/**
	 * 文件名以.printds结束
	 * 
	 * @param dir
	 * @return
	 */
	public static String[] listDatasource(java.io.File dir) {
		ArrayList<String> namear = new ArrayList<String>();
		File fs[] = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if (f.isFile()) {
				String fn = f.getName();
				if (fn.endsWith(postfix)) {
					namear.add(fn.substring(0, fn.length() - postfix.length()));
				}
			}
		}
		String names[] = new String[namear.size()];
		namear.toArray(names);
		return names;
	}

	public static void saveDs(File dir, String dsname,
			Vector<Datasource> dstable) throws Exception {
		File outf = new File(dir, dsname + postfix);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outf));
			Enumeration<Datasource> en = dstable.elements();
			while (en.hasMoreElements()) {
				Datasource ds = en.nextElement();
				ds.write(out);
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static Vector<Datasource> loadDs(File dir, String dsname)
			throws Exception {
		BufferedReader rd = null;
		try {
			File f = new File(dir, dsname + postfix);
			rd = new BufferedReader(new FileReader(f));
			return Datasource.read(rd);
		} finally {
			if (rd != null)
				rd.close();
		}
	}
}
