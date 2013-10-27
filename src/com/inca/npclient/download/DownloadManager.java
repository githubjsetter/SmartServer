package com.inca.npclient.download;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.filedb.DirHelper;
import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Hovdesc;
import com.inca.np.gui.ste.Zxhovdownloader;
import com.inca.np.gui.ste.Zxzipdownloader;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MD5Helper;
import com.inca.npclient.system.Clientframe;
import com.inca.npclient.system.NpclientParam;
import com.inca.npserver.server.sysproc.CurrentappHelper;

public class DownloadManager {

	/**
	 * 模块信息MAP,KEY为modulename,value is moduleinfo
	 */
	HashMap<String, Moduleinfo> modulemap = new HashMap<String, Moduleinfo>();
	private static DownloadManager inst = null;

	private DownloadManager() {

	}

	private URLClassLoader classloader = null;
	Category logger = Category.getInstance(DownloadManager.class);

	public static synchronized DownloadManager getInst() {
		if (inst == null) {
			inst = new DownloadManager();
			inst.downloadModuleinfo();
		}
		return inst;
	}

	boolean initok = false;

	void downloadModuleinfo() {
		Moduledownloader dl = new Moduledownloader();
		try {
			DBTableModel moduledbmodel = dl.downloadModulelist();
			modulemap.clear();
			for (int r = 0; r < moduledbmodel.getRowCount(); r++) {
				Moduleinfo minfo = new Moduleinfo();
				minfo.prodname = moduledbmodel.getItemValue(r, "prodname");
				minfo.modulename = moduledbmodel.getItemValue(r, "modulename");
				minfo.moduleengname = moduledbmodel.getItemValue(r, "engname");
				minfo.clientjar = moduledbmodel.getItemValue(r, "clientjar");
				minfo.clientjarmd5 = moduledbmodel.getItemValue(r,
						"clientjarmd5");
				modulemap.put(minfo.prodname + ":" + minfo.modulename, minfo);
			}

			initok = true;
		} catch (Exception e) {
			logger.error("ERROR", e);
			initok = false;
		}
	}

	class Moduleinfo {
		String prodname;
		String modulename;
		String moduleengname;
		String clientjar;
		String clientjarmd5;
	}

	public boolean isInitok() {
		return initok;
	}

	static String getVersion(String fn) {
		int p = fn.lastIndexOf(".");
		fn = fn.substring(0, p);
		p = fn.indexOf("-");
		if (p < 0)
			return "";
		return fn.substring(p + 1);
	}

	/**
	 * 检查模块的客户端文件
	 * 
	 * @param prodname
	 * @param modulename
	 * @return 同名同MD5,RETURN FALSE
	 */
	public boolean isNeeddownload(String prodname, String modulename) {
		// 考虑到服务器会动态更新模块的包，所以每次都检查一下
		downloadModuleinfo();
		Moduleinfo minfo = modulemap.get(prodname + ":" + modulename);
		if (minfo == null)
			return true;
		if (minfo.clientjar.length() == 0) {
			return true;
		}
		// String svrversion = getVersion(minfo.clientjar);
		// String moduleengname = minfo.moduleengname;
		File libdir = new File(CurrentdirHelper.getappDir(), "lib");

		File wantfile = new File(libdir, minfo.clientjar);
		logger.debug("isNeeddownload(),wantfile=" + wantfile.getName());
		if (!wantfile.exists()) {
			logger.debug("isNeeddownload(),wantfile=" + wantfile.getName()
					+ " not exists ,need download");
			return true;
		}
		String md5 = MD5Helper.MD5(wantfile);
		if (!md5.equals(minfo.clientjarmd5)) {
			logger.debug("isNeeddownload(),wantfile="
					+ wantfile.getAbsolutePath() + " md5=" + md5
					+ ",server md5=" + minfo.clientjarmd5 + ",need download");
			return true;
		}
		logger.debug("isNeeddownload(),wantfile=" + wantfile.getAbsolutePath()
				+ " md5=" + md5 + ",server md5=" + minfo.clientjarmd5
				+ ",not need download");
		return false;
	}

	/*
	 * void clearNotneed(String prodname, String modulename) { Moduleinfo minfo =
	 * modulemap.get(prodname + ":" + modulename); if (minfo == null) return;
	 * String moduleengname = minfo.moduleengname; File libdir = new
	 * File("lib"); DirHelper.clearOlderfile(libdir, moduleengname); }
	 */
	/**
	 * 检查是否需要下载
	 * 
	 * @param modulename
	 * @return 是否下载或下载是否成功 public boolean downloadIfNeed(String prodname, String
	 *         modulename) { if (!isNeeddownload(prodname, modulename)) return
	 *         false; return downloadModule(prodname, modulename, false); }
	 */

	String errormessage = "";

	/**
	 * 下载模块
	 * 
	 * @param modulename
	 */
	public boolean downloadModule(String prodname, String modulename,
			boolean withdlg) {
		logger.debug("开始下载模块" + modulename);
		Moduledownloader mdl = new Moduledownloader();
		File downloadedfile = mdl.downloadModule(prodname, modulename, withdlg,
				false);
		if (downloadedfile == null) {
			errormessage = mdl.getErrormessage();
		} else {
			DirHelper.clearOtherVersionOnexit(downloadedfile);
		}
		if (downloadedfile != null) {
			addLibjarfile(downloadedfile);
			ClassLoader newclassloader = getClassloader();
			Thread.currentThread().setContextClassLoader(newclassloader);
			DefaultNPParam.classloader = newclassloader;

		}

		return downloadedfile != null;
	}

	public String getErrormessage() {
		return errormessage;
	}

	private String getClientjar(String prodname, String modulename) {
		Moduleinfo minfo = modulemap.get(prodname + ":" + modulename);
		if (minfo == null)
			return null;
		return minfo.clientjar;
	}

	public void addLibjarfile(File f) {
		URL u;
		try {
			u = f.toURL();
			if (classloaderurlmap.get(u) == null) {
				classloaderurlmap.put(u, u);
			}
		} catch (MalformedURLException e) {
			logger.error("ERROR", e);
		}
	}

	private HashMap<URL, URL> classloaderurlmap = new HashMap<URL, URL>();

	public ClassLoader getClassloader() {
		URL urls[] = new URL[classloaderurlmap.size()];
		Iterator<URL> it = classloaderurlmap.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			urls[i] = it.next();
		}
		ClassLoader systemclassloader = ClassLoader.getSystemClassLoader();
		return new URLClassLoader(urls, systemclassloader);
	}

	/**
	 * 检查并下载模块的类. 准备好classloader
	 * 
	 * @param prodname
	 * @param modulename
	 * @throws Exception
	 */
	public void prepareModulejar(String prodname, String modulename)
			throws Exception {
		logger.debug("prepareModulejar prodname=" + prodname + ",modulename="
				+ modulename);
		if (isNeeddownload(prodname, modulename)) {
			boolean ret = downloadModule(prodname, modulename, true);
			if (!ret) {
				throw new Exception("下载产品:" + prodname + ",模块:" + modulename
						+ " 客户端文件失败" + getErrormessage() + ",无法运行");
			}

		}
		String clientjar = getClientjar(prodname, modulename);
		File libdir = CurrentdirHelper.getLibdir();
		File newjarfile = new File(libdir, clientjar);
		logger.info("newjarfile=" + newjarfile);

		addLibjarfile(newjarfile);
		ClassLoader newclassloader = getClassloader();
		Thread.currentThread().setContextClassLoader(newclassloader);
		DefaultNPParam.classloader = newclassloader;

	}

	/*
	 * zxzip map
	 */
	HashMap<String, String> zxzipmap = new HashMap<String, String>();

	public void clearZxfileCache(String opid){
		zxzipmap.remove(opid);
	}
	
	public File getZxfile(String opid) {
		if (opid == null)
			return null;
		String path = zxzipmap.get(opid);
		if (path != null && path.length() > 0 ) {
			if(path.equals("not exists")){
				return null;
			}else{
				return new File(path);
			}
		}
		Zxzipdownloader dl = new Zxzipdownloader();
		File zxfile = null;
		if (opid.length() > 0) {
			if (DefaultNPParam.runonserver) {
				File dir = new File(CurrentappHelper.getClassesdir(), "专项开发");
				zxfile = new File(dir, opid + ".zip");
			} else {

				try {
					zxfile = dl.downloadZxzip(opid);
				} catch (Exception e) {
					logger.error("error", e);
					zxfile = null;
				}finally{
				}
			}
		}

		if (zxfile == null) {
			path = "not exists";
		} else {
			path = zxfile.getAbsolutePath();
		}
		zxzipmap.put(opid, path);
		if (path.equals("not exists")) {
			return null;
		}
		return new File(path);
	}

	public CHovBase downloadProdhov(String hovclassname, String prodname,
			String modulename) throws Exception {
		// 实例hov
		Class clazz;
		try {
			clazz = Class.forName(hovclassname, true,
					DefaultNPParam.classloader);
		} catch (ClassNotFoundException e) {
			// 下载试试
			DownloadManager.getInst().prepareModulejar(prodname, modulename);
			/*
			 * boolean ret = DownloadManager.getInst().downloadModule(prodname,
			 * modulename, true); if (!ret) { throw new
			 * Exception(DownloadManager.getInst().getErrormessage()); }
			 */
			try {
				clazz = Class.forName(hovclassname, true,
						DefaultNPParam.classloader);
			} catch (ClassNotFoundException ee) {
				throw new Exception("无法加载类" + hovclassname + ":"
						+ ee.getMessage());
			}
		}

		try {
			CHovBase hov = (CHovBase) clazz.newInstance();
			return hov;
		} catch (Exception e) {
			throw new Exception("HOV类" + hovclassname + "实例化失败:"
					+ e.getMessage());
		}
	}

	public CHovBase downloadProdhov(String hovclassname) throws Exception {
		// 实例hov
		Class clazz;
		try {
			clazz = Class.forName(hovclassname, true,
					DefaultNPParam.classloader);
		} catch (ClassNotFoundException e) {
			// 下载找类
			String sql = "select prodname,modulename from np_hov where classname='"
					+ hovclassname + "'";
			RemotesqlHelper sqlh = new RemotesqlHelper();
			DBTableModel dbmodel = sqlh.doSelect(sql, 0, 1);
			if (dbmodel.getRowCount() == 0) {
				throw new Exception("无法加载HOV类" + hovclassname
						+ "，数据库中也没有找到其登记信息");
			}
			String prodname = dbmodel.getItemValue(0, "prodname");
			String modulename = dbmodel.getItemValue(0, "modulename");
			return downloadProdhov(hovclassname, prodname, modulename);
		}

		try {
			CHovBase hov = (CHovBase) clazz.newInstance();
			return hov;
		} catch (Exception e) {
			throw new Exception("HOV类" + hovclassname + "实例化失败:"
					+ e.getMessage());
		}
	}

	public CHovBase downloadZxhov(String hovname) throws Exception {
		Zxhovdownloader zxhovdl = new Zxhovdownloader();
		File zxzipfile = null;
		try {
			zxzipfile = zxhovdl.downloadZxzip(hovname);
			if (zxzipfile == null) {
				throw new Exception("下载专项HOV失败" + zxhovdl.getErrormessage());
			}
		} catch (Exception e) {
			throw new Exception("下载专项HOV失败" + e.getMessage());
		}

		try {
			CHovBase hov = zxhovdl.createHovfromzxzipfile(zxzipfile);
			return hov;
		} catch (Exception e) {
			throw new Exception("下载专项HOV失败" + e.getMessage());
		}

	}

	public static void main(String[] args) {
		new NpclientParam();
		try {
			DownloadManager dm = DownloadManager.getInst();
			System.out.println(dm.isInitok());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
