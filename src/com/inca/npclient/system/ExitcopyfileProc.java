package com.inca.npclient.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.filedb.DirHelper;

/**
 * 系统退出时拷贝文件。
 * 
 * @author Administrator
 * 
 */
public class ExitcopyfileProc implements SystemexitProcessIF {
	Category logger = Category.getInstance(ExitcopyfileProc.class);
	static Vector<Copyfileinfo> cpfileinfos = new Vector<Copyfileinfo>();

	public ExitcopyfileProc() {
		super();
		// 把常用类加到内存
		new DirHelper();
		new CurrentdirHelper();

	}

	public void process() {
		DirHelper.clearFileonexit();

		Enumeration<Copyfileinfo> en = cpfileinfos.elements();
		while (en.hasMoreElements()) {
			Copyfileinfo cpinfo = en.nextElement();
			logger.debug("exit copy file " + cpinfo.srcfile.getAbsolutePath()
					+ " to " + cpinfo.targetfile.getAbsolutePath());
			copyFile(cpinfo.srcfile, cpinfo.targetfile, cpinfo.deletesrc);
		}
	}

	void copyFile(File srcfile, File targetfile, boolean deletesrc) {
		byte[] buf = new byte[102400];
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(srcfile);
			targetfile.getParentFile().mkdirs();
			fout = new FileOutputStream(targetfile);
			int rd;
			while ((rd = fin.read(buf)) > 0) {
				fout.write(buf, 0, rd);
			}
		} catch (Exception e) {
		} finally {
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (deletesrc) {
			srcfile.delete();
		}
	}

	public static void addCopyfile(File srcfile, File targetfile,
			boolean deletesrc) {
		Copyfileinfo info = new Copyfileinfo();
		info.srcfile = srcfile;
		info.targetfile = targetfile;
		info.deletesrc = deletesrc;
		cpfileinfos.add(info);
	}

	static class Copyfileinfo {
		File srcfile;
		File targetfile;
		boolean deletesrc = false;
	}

}
