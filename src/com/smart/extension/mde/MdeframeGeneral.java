package com.smart.extension.mde;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.smart.extension.ste.CSteModelGeneral;
import com.smart.extension.ste.SteframeGeneral;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DefaultNPParam;

public class MdeframeGeneral extends MdeFrame {
	Category logger = Category.getInstance(SteframeGeneral.class);

	String opname = "";
	String tablename = "";
	String viewname = "";
	String pkcolname = "";
	String tablename1 = "";
	String viewname1 = "";
	String pkcolname1 = "";
	String fkcolname1 = "";

	File zxzipfile = null;

	public MdeframeGeneral(File zxfile) throws HeadlessException {
		super();
		if(zxfile==null){
			throw new HeadlessException("没有找到专项文件");
		}
		// 从文件中读取信息.
		this.zxzipfile = zxfile;
		try {
			readFile(zxfile);
		} catch (Exception e) {
			logger.error("ERROR", e);
			return;
		}
		this.setTitle(opname);
		initControl();
	}

	void readFile(File zxfile) throws Exception {
		ZipFile zipfile = new ZipFile(zxfile);
		Enumeration<ZipEntry> en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if (ze.getName().equals("config")) {
				InputStream in = zipfile.getInputStream(ze);
				readConfig(in);
				in.close();
			}
		}
	}

	void writeFile(File outf, InputStream in) throws Exception {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(outf);
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			int rd;
			while ((rd = in.read(buf)) > 0) {
				fout.write(buf, 0, rd);
			}
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
	}

	void readConfig(InputStream in) {
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(in, "gbk"));
			String line;
			while ((line = rd.readLine()) != null) {
				int p = line.indexOf(":");
				if (p < 0)
					continue;
				String name = line.substring(0, p);
				String value = line.substring(p + 1);

				if (name.equals("opid")) {
					opid = value;
				} else if (name.equals("opname")) {
					opname = value;
				} else if (name.equals("tablename")) {
					tablename = value;
				} else if (name.equals("viewname")) {
					viewname = value;
				} else if (name.equals("pkcolname")) {
					pkcolname = value;
				} else if (name.equals("tablename1")) {
					tablename1 = value;
				} else if (name.equals("viewname1")) {
					viewname1 = value;
				} else if (name.equals("pkcolname1")) {
					pkcolname1 = value;
				} else if (name.equals("fkcolname1")) {
					fkcolname1 = value;
				}
			}
		} catch (Exception e) {
			logger.error("E", e);
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new CMdeModelGeneral(this, opname, zxzipfile, opid, viewname,
				pkcolname, viewname1, fkcolname1);
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		
		File zxfile = new File("d:/npdev/build/classes/专项开发/10001.zip");

		MdeframeGeneral steframe = new MdeframeGeneral(zxfile);
		steframe.pack();
		steframe.setVisible(true);
	}

}

