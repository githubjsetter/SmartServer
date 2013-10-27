package com.inca.npserver.prod;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Category;

import com.inca.np.filedb.DirHelper;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SelectHelper;
import com.inca.npclient.system.NpclientParam;
import com.inca.npserver.dbcp.DBConnectPoolFactory;
import com.inca.npserver.server.sysproc.CurrentappHelper;

/**
 * 管理运行的模块
 * 
 * @author Administrator
 * 
 */
public class ModuleManager {
	static private ModuleManager inst = null;
	Category logger = Category.getInstance(ModuleManager.class);
	Vector<Jarmd5info> launcherjars = new Vector<Jarmd5info>();

	public static synchronized ModuleManager getInst() {
		if (inst == null) {
			inst = new ModuleManager();
			inst.loadModulefromDB();
		}
		return inst;
	}

	private ModuleManager() {

	}

	Vector<Moduleinfo> modules = new Vector<Moduleinfo>();

	/**
	 * 启动需要的JARS
	 * 
	 * @return
	 */
	public Vector<Jarmd5info> getLauncherjars() {
		return launcherjars;
	}

	public void loadModulefromDB() {
		File libdir = CurrentappHelper.getLibrarydir();
		DirHelper.clearZerosizefile(libdir);

		Vector<Moduleinfo> tmps = new Vector<Moduleinfo>();
		LicenseManager lm = LicenseManager.getInst();
		Connection con = null;
		SelectHelper sh = null;
		try {
			con = getConnection();
			Enumeration<Licenseinfo> en = lm.getLicenseinfos().elements();
			while (en.hasMoreElements()) {
				Licenseinfo linfo = en.nextElement();
				String prodname = linfo.getProdname();
				Enumeration<String> en1 = linfo.getModules().elements();
				while (en1.hasMoreElements()) {
					String modulename = en1.nextElement();

					sh = new SelectHelper(
							"select modulename,prodname,engname,version from np_module where "
									+ " prodname=?  and modulename=?");
					sh.bindParam(prodname);
					sh.bindParam(modulename);
					DBTableModel moduleinfos = sh.executeSelect(con, 0, 1);
					if (moduleinfos.getRowCount() == 0) {
						continue;
					}
					Moduleinfo moduleinfo = new Moduleinfo();
					moduleinfo.modulename = modulename;
					moduleinfo.prodname = prodname;
					moduleinfo.moduleengname = moduleinfos.getItemValue(0,
							"engname");
					moduleinfo.version = moduleinfos.getItemValue(0, "version");
					setClientjarfile(moduleinfo);

					logger.info("loaded module " + moduleinfo.prodname + "-"
							+ moduleinfo.modulename);
					tmps.add(moduleinfo);
				}
			}

			/*
			 * //加npserver Moduleinfo moduleinfo=new Moduleinfo();
			 * moduleinfo.modulename="npserver"; moduleinfo.prodname="npserver";
			 * moduleinfo.moduleengname="npserver"; moduleinfo.version="5.0.00";
			 * setClientjarfile(moduleinfo); tmps.add(moduleinfo);
			 */
			synchronized (modules) {
				modules.removeAllElements();
				modules.addAll(tmps);
			}

		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

		// 查询文件
		if (DefaultNPParam.develop == 0) {
			buildSupportJars();
		}
	}

	public Vector<Moduleinfo> getModules() {
		return modules;
	}

	/**
	 * 在WEB-INF/lib目录下找
	 * 
	 * @param moduleinfo
	 */
	void setClientjarfile(Moduleinfo moduleinfo) {
		ArrayList<String> ar = new ArrayList<String>();
		File libdir = CurrentappHelper.getLibrarydir();
		File fs[] = libdir.listFiles();
		if (fs == null)
			return;

		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if (f.getName().startsWith(moduleinfo.moduleengname + "_c")) {
				ar.add(f.getName());
			}
		}
		// 如果有多个同名文件,只保留版本最大的那个.
		if (ar.size() == 0)
			return;
		int maxindex = 0;
		String maxversion = DirHelper.getVersion(ar.get(0));
		for (int i = 1; i < ar.size(); i++) {
			String version = DirHelper.getVersion(ar.get(i));
			if (version.compareTo(maxversion) > 0) {
				File delfile = new File(libdir, ar.get(maxindex));
				DirHelper.clearFile(delfile);
				maxindex = i;
				maxversion = version;
			}
		}
		moduleinfo.clientjar = ar.get(maxindex);
		File jarfile = new File(libdir, moduleinfo.clientjar);
		moduleinfo.clientjarmd5 = MD5Helper.MD5(jarfile);
		return;
	}

	void buildSupportJars() {
		launcherjars.removeAllElements();
		File libdir = CurrentappHelper.getLibrarydir();
		Jarmd5info md5info = new Jarmd5info();
		md5info.jarfilename = "log4j-1.2.8.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		md5info = new Jarmd5info();
		md5info.jarfilename = "skinlf.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		md5info = new Jarmd5info();
		md5info.jarfilename = "poi-3.0-rc4-20070503.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		md5info = new Jarmd5info();
		md5info.jarfilename = "jcommon-1.0.0-rc1.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		md5info = new Jarmd5info();
		md5info.jarfilename = "jfreechart-1.0.0-rc1.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		md5info = new Jarmd5info();
		md5info.jarfilename = "apachezip.jar";
		md5info.md5 = MD5Helper.MD5(new File(libdir, md5info.jarfilename));
		launcherjars.add(md5info);

		// 再加入最新的npcommon和npserver_c
		File npcommonjar = getNewestjarfile("npcommon");
		md5info = new Jarmd5info();
		md5info.jarfilename = npcommonjar.getName();
		md5info.md5 = MD5Helper.MD5(npcommonjar);
		launcherjars.add(md5info);

		File npserver_c = getNewestjarfile("npserver_c");
		md5info = new Jarmd5info();
		md5info.jarfilename = npserver_c.getName();
		md5info.md5 = MD5Helper.MD5(npserver_c);
		logger.info("npserver_c=" + npserver_c.getAbsolutePath() + ",md5="
				+ md5info.md5);
		launcherjars.add(md5info);

		genClientsetpathcmd();

		File clientsetpathcmd = new File(libdir, "setpath.cmd");
		md5info = new Jarmd5info();
		md5info.jarfilename = clientsetpathcmd.getName();
		md5info.md5 = MD5Helper.MD5(clientsetpathcmd);
		launcherjars.add(md5info);

	}

	public boolean isLauncherjar(String filename) {
		Enumeration<Jarmd5info> en = launcherjars.elements();
		while (en.hasMoreElements()) {
			Jarmd5info md5info = en.nextElement();
			if (md5info.jarfilename.equals(filename))
				return true;
		}
		return false;
	}

	/**
	 * 生成客户端的setpath.cmd文件
	 */
	void genClientsetpathcmd() {
		File libdir = CurrentappHelper.getLibrarydir();
		PrintWriter out = null;
		try {
			File f = new File(libdir, "setpath.cmd");
			out = new PrintWriter(new FileWriter(f));
			out.println("set JAVA_OPTS=%JAVA_OPTS% -Xms64M -Xmx1024M -Xincgc -verbose:gc");
			out.println("set CP=classes;");
			out.println("set CP=conf;%CP%");
			Enumeration<Jarmd5info> en = launcherjars.elements();
			while (en.hasMoreElements()) {
				Jarmd5info md5info = en.nextElement();
				out.println("set CP=lib\\" + md5info.jarfilename + ";%CP%");
			}
			out.println("set CP=lib\\npbichart-2.3.1.jar;%CP%");
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	File getNewestjarfile(String prefix) {
		File targetfile = null;
		File libdir = CurrentappHelper.getLibrarydir();
		File fs[] = libdir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if (!f.getName().startsWith(prefix))
				continue;
			if (targetfile == null) {
				targetfile = f;
				continue;
			}
			String v1 = DirHelper.getVersion(targetfile.getName());
			String v2 = DirHelper.getVersion(f.getName());

			if (v2.compareTo(v1) > 0) {
				targetfile = f;
			}
		}
		return targetfile;
	}

	String dbip = DefaultNPParam.debugdbip;
	String dbname = DefaultNPParam.debugdbsid;
	String dbuser = DefaultNPParam.debugdbusrname;
	String dbpass = DefaultNPParam.debugdbpasswd;

	private Connection getTestCon() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;
		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		return con;
	}

	protected String dburl = "java:comp/env/oracle/db";

	InitialContext ic = null;

	protected Connection getConnection() throws Exception {
		if (DefaultNPParam.debug == 1) {
			return getTestCon();
		} else {
			/*
			 * if (ic == null) ic = new InitialContext(); DataSource ds =
			 * (DataSource) ic.lookup(dburl); Connection con =
			 * ds.getConnection();
			 */
			Connection con = DBConnectPoolFactory.getInstance().getConnection();
			con.setAutoCommit(false);
			return con;
		}
	}

	public String getJarfilename(String prodname, String modulename) {
		logger.debug("getJarfilename,modelssize="+modules.size());
		if(modules.size()==0){
			loadModulefromDB();
		}
		Enumeration<Moduleinfo> en = modules.elements();
		while (en.hasMoreElements()) {
			Moduleinfo minfo = en.nextElement();
			if (minfo.prodname.equals(prodname)
					&& minfo.modulename.equals(modulename)) {
				return minfo.clientjar;
			}
		}
		logger.error("not found module prodname="+prodname+",modulename="+modulename);
		return null;
	}

	public static class Jarmd5info {
		public String jarfilename;
		public String md5;
	}

	public static void main(String[] args) {

		new NpclientParam();
		DefaultNPParam.debug = 1;
		ModuleManager mm = ModuleManager.getInst();
		mm.loadModulefromDB();
	}
}
