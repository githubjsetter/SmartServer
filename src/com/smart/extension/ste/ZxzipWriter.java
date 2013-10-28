package com.smart.extension.ste;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;

public class ZxzipWriter {
	Category logger = Category.getInstance(ZxzipWriter.class);

	public void writeSteFile(Zxconfig config, File zipfile) throws Exception {

		String opid = config.opid;
		// 生成临时的文件
		File tempconfig = null;
		File tempdbmodel = null;
		ZipOutputStream zout = null;
		try {
			tempconfig = File.createTempFile("temp", "config");
			PrintWriter out = new PrintWriter(new FileWriter(tempconfig));
			out.println("optype:stegeneral");
			out.println("opid:" + opid);
			out.println("opcode:" + config.opcode);
			out.println("opname:" + config.opname);
			out.println("groupname:" + config.groupname);
			out.println("tablename:" + config.tablename);
			out.println("viewname:" + config.viewname);
			out.println("pkcolname:" + config.pkcolname);
			out.close();

			// 生成model文件
			tempdbmodel = File.createTempFile("temp", "model");
			genColumnmodel(config.tablename, config.viewname, config.pkcolname,
					tempdbmodel);

			zout = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry zentry = new ZipEntry("config");
			zout.putNextEntry(zentry);
			write2zipfile(tempconfig, zout);
			zout.closeEntry();

			zentry = new ZipEntry("ste.model");
			zout.putNextEntry(zentry);
			write2zipfile(tempdbmodel, zout);
			zout.closeEntry();

		} finally {
			if (tempconfig != null) {
				tempconfig.delete();
				tempconfig.deleteOnExit();
			}
			if (tempdbmodel != null) {
				tempdbmodel.delete();
				tempdbmodel.deleteOnExit();
			}

			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void writeReportFile(Zxconfig config, File zipfile) throws Exception {

		String opid = config.opid;
		// 生成临时的文件
		File tempconfig = null;
		File tempdbmodel = null;
		ZipOutputStream zout = null;
		try {
			tempconfig = File.createTempFile("temp", "config");
			PrintWriter out = new PrintWriter(new FileWriter(tempconfig));
			out.println("optype:reportgeneral");
			out.println("opid:" + opid);
			out.println("opcode:" + config.opcode);
			out.println("opname:" + config.opname);
			out.println("groupname:" + config.groupname);
			out.println("tablename:" + config.tablename);
			out.println("viewname:" + config.viewname);
			out.println("pkcolname:" + config.pkcolname);
			out.close();

			// 生成model文件
			tempdbmodel = File.createTempFile("temp", "model");
			genColumnmodel(config.tablename, config.viewname, config.pkcolname,
					tempdbmodel);

			zout = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry zentry = new ZipEntry("config");
			zout.putNextEntry(zentry);
			write2zipfile(tempconfig, zout);
			zout.closeEntry();

			zentry = new ZipEntry("ste.model");
			zout.putNextEntry(zentry);
			write2zipfile(tempdbmodel, zout);
			zout.closeEntry();

		} finally {
			if (tempconfig != null) {
				tempconfig.delete();
				tempconfig.deleteOnExit();
			}
			if (tempdbmodel != null) {
				tempdbmodel.delete();
				tempdbmodel.deleteOnExit();
			}

			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void writeMdeFile(Zxconfig config, File zipfile) throws Exception {

		String opid = config.opid;
		// 生成临时的文件
		File tempconfig = null;
		File tempdbmodel = null;
		File tempdbmodel1 = null;
		ZipOutputStream zout = null;
		try {
			tempconfig = File.createTempFile("temp", "config");
			PrintWriter out = new PrintWriter(new FileWriter(tempconfig));
			out.println("optype:mdegeneral");
			out.println("opid:" + opid);
			out.println("opcode:" + config.opcode);
			out.println("opname:" + config.opname);
			out.println("tablename:" + config.tablename);
			out.println("viewname:" + config.viewname);
			out.println("pkcolname:" + config.pkcolname);
			out.println("tablename1:" + config.tablename1);
			out.println("viewname1:" + config.viewname1);
			out.println("pkcolname1:" + config.pkcolname1);
			out.println("fkcolname1:" + config.fkcolname1);
			out.close();

			// 生成model文件
			tempdbmodel = File.createTempFile("temp", "model");
			genColumnmodel(config.tablename, config.viewname, config.pkcolname,
					tempdbmodel);
			tempdbmodel1 = File.createTempFile("temp", "model");
			genColumnmodel(config.tablename1, config.viewname1,
					config.pkcolname1, tempdbmodel1);

			zout = new ZipOutputStream(new FileOutputStream(zipfile));
			ZipEntry zentry = new ZipEntry("config");
			zout.putNextEntry(zentry);
			write2zipfile(tempconfig, zout);
			zout.closeEntry();

			zentry = new ZipEntry("ste.model");
			zout.putNextEntry(zentry);
			write2zipfile(tempdbmodel, zout);
			zout.closeEntry();

			zentry = new ZipEntry("ste1.model");
			zout.putNextEntry(zentry);
			write2zipfile(tempdbmodel1, zout);
			zout.closeEntry();

		} finally {
			if (tempconfig != null) {
				tempconfig.delete();
				tempconfig.deleteOnExit();
			}
			if (tempdbmodel != null) {
				tempdbmodel.delete();
				tempdbmodel.deleteOnExit();
			}

			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	void genColumnmodel(String tablename, String viewname, String pkcolname,
			File outf) throws Exception {

		// 查表列
		HashMap<String, DBColumnDisplayInfo> tablecolmap = new HashMap<String, DBColumnDisplayInfo>();
		String sql = "select c.cname,c.coltype,n.cntitle from col c,sys_column_cn n where tname='"
				+ tablename.toUpperCase()
				+ "' and '"
				+ tablename.toUpperCase()+"'=n.tablename(+)"
				+ " and c.cname=n.colname(+) order by colno";

		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel colsdbmodel = sqlh.doSelect(sql, 0, 1000);
		if (colsdbmodel.getRowCount() == 0) {
			throw new Exception("找不到表" + tablename);
		}
		for (int r = 0; r < colsdbmodel.getRowCount(); r++) {
			String cname = colsdbmodel.getItemValue(r, "cname").toLowerCase();
			String coltype = colsdbmodel.getItemValue(r, "coltype");
			coltype = dbcoltype2coltype(coltype);
			String cntitle = colsdbmodel.getItemValue(r, "cntitle");
			cntitle=cntitle.replaceAll("\\s","");
			if (cntitle == null || cntitle.length() == 0)
				cntitle = cname;
			DBColumnDisplayInfo dispinfo = new DBColumnDisplayInfo(cname,
					coltype, cntitle);
			if (cname.equalsIgnoreCase(pkcolname)) {
				dispinfo.setIspk(true);
			}
			tablecolmap.put(cname, dispinfo);
		}

		// 查询视图列
		Vector<DBColumnDisplayInfo> viewcols = new Vector<DBColumnDisplayInfo>();
		sql = "select c.cname,c.coltype,n.cntitle from col c,sys_column_cn n where tname='"
				+ viewname.toUpperCase()
				+ "' and '"
				+ viewname.toUpperCase()+"'=n.tablename(+) "
				+ " and c.cname=n.colname(+) order by colno";

		colsdbmodel = sqlh.doSelect(sql, 0, 1000);
		if (colsdbmodel.getRowCount() == 0) {
			throw new Exception("找不到视图" + viewname);
		}

		for (int r = 0; r < colsdbmodel.getRowCount(); r++) {
			String cname = colsdbmodel.getItemValue(r, "cname").toLowerCase();
			String coltype = colsdbmodel.getItemValue(r, "coltype");
			coltype = dbcoltype2coltype(coltype);
			String cntitle = colsdbmodel.getItemValue(r, "cntitle");
			if (cntitle == null || cntitle.length() == 0)
				cntitle = cname;
			DBColumnDisplayInfo dispinfo = tablecolmap.get(cname);
			if (dispinfo == null) {

				dispinfo = new DBColumnDisplayInfo(cname, coltype, cntitle);
				// 视图有，但表没有
				dispinfo.setReadonly(true);
				dispinfo.setFocusable(false);
				dispinfo.setUpdateable(false);

			}
			viewcols.add(dispinfo);
		}

		for(int i=2;i<viewcols.size();i+=3){
			viewcols.elementAt(i).setLinebreak(true);
		}

		// 写到文件
		DBColumnInfoStoreHelp.writeFile(viewcols, outf);
	}

	String dbcoltype2coltype(String dbcoltype) {
		dbcoltype = dbcoltype.toLowerCase();
		if (dbcoltype.startsWith("number")) {
			return DBColumnDisplayInfo.COLTYPE_NUMBER;
		} else if (dbcoltype.startsWith("date")) {
			return DBColumnDisplayInfo.COLTYPE_DATE;
		} else {
			return DBColumnDisplayInfo.COLTYPE_VARCHAR;
		}

	}

	void write2zipfile(File tempfile, ZipOutputStream zout) throws Exception {
		int buflen = 102400;
		byte[] buf = new byte[buflen];
		InputStream in = null;
		try {
			in = new FileInputStream(tempfile);
			int rd;
			while ((rd = in.read(buf)) > 0) {
				zout.write(buf, 0, rd);
			}
		} finally {
			if (in != null)
				in.close();
		}
	}

}
