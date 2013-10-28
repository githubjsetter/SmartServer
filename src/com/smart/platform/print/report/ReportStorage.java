package com.smart.platform.print.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.smart.extension.ste.ZxmodifyUploadHelper;
import com.smart.platform.demo.ste.Pub_goods_frame;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.print.drawable.PPage;
import com.smart.platform.print.drawable.PReport;
import com.smart.platform.util.ZipHelper;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-27 Time: 15:49:25
 * 报表样式的存储。 <report> name=xxxx <pagehead> <labelcell> expr= x= y= width= height=
 * font=,, </labelcell> </pagehead> <p/> <p/> </report>
 */
public class ReportStorage {
	private static void writeReport(PReport report, String reportname,
			PrintWriter out) throws Exception {
		out.println("<report>");
		out.println("name=" + reportname);

		report.getPage().writeReport(out);

		out.println("</report>");
		out.flush();
	}

	private static void readReport(PReport report, BufferedReader in)
			throws Exception {
		PPage page = new PPage(report);
		page.readReport(in);
		report.setPage(page);
	}

	public static PReport loadReport(String opid, String reportname)
			throws Exception {
		File zxzipfile = new File(CurrentdirHelper.getZxdir(), opid + ".zip");
		if(!zxzipfile.exists())return null;
		String reportfn = opid + "_" + reportname + ".rpt";

		File tempfile = null;
		try {
			tempfile = File.createTempFile("temp", ".rpt");
			ZipHelper.extractFile(zxzipfile, reportfn, tempfile);
			if (tempfile.length() == 0)
				return null;
			BufferedReader rd = new BufferedReader(new FileReader(tempfile));
			PReport rpt = new PReport();
			readReport(rpt, rd);
			rd.close();
			return rpt;
		} finally {
			if (tempfile != null) {
				tempfile.delete();
			}
		}
	}

	/**
	 * 存在专项zip文件中， zip中的命名为 opid_报表名.rpt
	 * 
	 * @param opid
	 * @param reportname
	 * @param report
	 * @throws Exception
	 */
	public static void saveReport(String opid, String reportname, PReport report)
			throws Exception {
		Category logger = Category.getInstance(ReportStorage.class);
		File zxzipfile = new File(CurrentdirHelper.getZxdir(), opid + ".zip");
		String reportfn = opid + "_" + reportname + ".rpt";

		File outf = null;
		try {
			outf = File.createTempFile("temp", ".rpt");
			PrintWriter out = new PrintWriter(new FileWriter(outf));
			writeReport(report, reportname, out);
			out.close();
			ZipHelper.replaceZipfile(zxzipfile, reportfn, outf);

			// 上传
			ZxmodifyUploadHelper zxmu = new ZxmodifyUploadHelper();
			zxmu.uploadZxfile(opid, zxzipfile);
		} finally {
			if (outf != null) {
				outf.delete();
			}
		}

		/*
		 * // 在程序目录中也加一个 if (new File("src").exists()) { classname =
		 * obj.getClass().getName(); String path = classname.replaceAll("\\.",
		 * "/"); int pp = path.lastIndexOf("/"); path = path.substring(0, pp +
		 * 1); outf = new File("src/" + path + opid + "_" + reportname +
		 * ".rpt"); outf.getParentFile().mkdirs(); out = new PrintWriter(new
		 * FileWriter(outf)); writeReport(report, reportname, out); out.close(); }
		 */
	}

	/*
	 * private static File getObjectDir(Object obj) throws Exception { String
	 * classname = obj.getClass().getName(); int p = classname.lastIndexOf(".");
	 * if (p >= 0) { classname = classname.substring(p + 1); }
	 *  // 如果是个目录，就 URL classfileurl = obj.getClass().getResource(classname +
	 * ".class");
	 * 
	 * if (classfileurl.toString().indexOf("!") < 0) { // 是个目录 String filepath =
	 * classfileurl.toString().substring( "file:".length()); File f = new
	 * File(filepath); File dir = f.getParentFile(); return dir; } else { String
	 * filepath = classfileurl.toString(); p = filepath.indexOf("!"); filepath =
	 * filepath.substring(0, p); if (filepath.startsWith("jar:")) filepath =
	 * filepath.substring(4); if (filepath.startsWith("file:")) filepath =
	 * filepath.substring(5); File f = new File(filepath); File dir =
	 * f.getParentFile(); return dir;
	 *  }
	 *  }
	 */
	public static String[] getSavedreportNames(String opid) throws Exception {

		ArrayList ar = new ArrayList();
		File zxzipfile = new File(CurrentdirHelper.getZxdir(), opid + ".zip");

		if (zxzipfile.exists() && zxzipfile.length() > 0) {
			ZipFile zf = new ZipFile(zxzipfile);
			Enumeration<ZipEntry> en = zf.getEntries();
			while (en.hasMoreElements()) {
				ZipEntry zentry = en.nextElement();
				String entryname = zentry.getName();
				if (entryname.startsWith(opid + "_")
						&& entryname.endsWith(".rpt")) {
					int p=entryname.indexOf("_");
					String reportname=entryname.substring(p+1);
					reportname=reportname.substring(0,reportname.length() - ".rpt".length());
					ar.add(reportname);
				}
			}
		}
		String names[] = new String[ar.size()];
		ar.toArray(names);
		return names;

	}

	public static void main(String argv[]) {
		PReport rpt = new PReport();
		rpt.createDebugReport();

		File f = new File("test.rpt");
		try {
			/*
			 * PrintWriter out = new PrintWriter(new FileWriter(f));
			 * writeReport(rpt,"测试报表存储",out); out.close();
			 */

			/*
			 * BufferedReader in = new BufferedReader(new FileReader(f));
			 * PReport rpt1=new PReport(); PPage page=new PPage(rpt1);
			 * page.readReport(in); rpt1.setPage(page);
			 * 
			 * 
			 * File outf=new File("test1.rpt"); PrintWriter out = new
			 * PrintWriter(new FileWriter(outf));
			 * writeReport(rpt1,"测试报表存储",out); out.close();
			 * 
			 */
			Pub_goods_frame ste = new Pub_goods_frame();

		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
	}
}
