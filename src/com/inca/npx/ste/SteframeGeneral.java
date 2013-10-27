package com.inca.npx.ste;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class SteframeGeneral extends Steframe {
	Category logger = Category.getInstance(SteframeGeneral.class);
	String opname="";
	String tablename="";
	String viewname="";
	
	File zxzipfile=null;
	
	public SteframeGeneral(File zxfile) throws HeadlessException {
		super();
		if(zxfile==null){
			throw new HeadlessException("没有找到专项文件");
		}
		if(!zxfile.exists()){
			logger.error("找不到专项文件"+zxfile.getAbsolutePath());
			JOptionPane.showMessageDialog(this,"找不到专项文件"+zxfile.getAbsolutePath());
			throw new HeadlessException();
		}
		// 从文件中读取信息.
		this.zxzipfile=zxfile;
		try {
			readFile(zxfile);
		} catch (Exception e) {
			logger.error("ERROR",e);
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
				InputStream in=zipfile.getInputStream(ze);
				readConfig(in);
				in.close();
			}
		}
	}

	void writeFile(File outf, InputStream in) throws Exception{
		FileOutputStream fout=null;
		try {
			fout=new FileOutputStream(outf);
			int buflen=102400;
			byte[] buf=new byte[buflen];
			int rd;
			while((rd=in.read(buf))>0){
				fout.write(buf, 0, rd);
			}
		}finally{
			if(fout!=null){
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
					opid=value;
				} else if (name.equals("opname")) {
					opname=value;
				} else if (name.equals("tablename")) {
					tablename=value;
				} else if (name.equals("viewname")) {
					viewname=value;
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
	protected CSteModel getStemodel() {
		CSteModelGeneral ste=new CSteModelGeneral(this,opname,opid,viewname,zxzipfile);
		return ste;
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		
		File zxfile=new File("d:/npdev/build/classes/专项开发/10000.zip");
		
		SteframeGeneral steframe=new SteframeGeneral(zxfile);
		steframe.pack();
		steframe.setVisible(true);
	}
}
