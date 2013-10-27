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
	 * ģ����ϢMAP,KEYΪmodulename,value is moduleinfo
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
	 * ���ģ��Ŀͻ����ļ�
	 * 
	 * @param prodname
	 * @param modulename
	 * @return ͬ��ͬMD5,RETURN FALSE
	 */
	public boolean isNeeddownload(String prodname, String modulename) {
		// ���ǵ��������ᶯ̬����ģ��İ�������ÿ�ζ����һ��
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
	 * ����Ƿ���Ҫ����
	 * 
	 * @param modulename
	 * @return �Ƿ����ػ������Ƿ�ɹ� public boolean downloadIfNeed(String prodname, String
	 *         modulename) { if (!isNeeddownload(prodname, modulename)) return
	 *         false; return downloadModule(prodname, modulename, false); }
	 */

	String errormessage = "";

	/**
	 * ����ģ��
	 * 
	 * @param modulename
	 */
	public boolean downloadModule(String prodname, String modulename,
			boolean withdlg) {
		logger.debug("��ʼ����ģ��" + modulename);
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
	 * ��鲢����ģ�����. ׼����classloader
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
				throw new Exception("���ز�Ʒ:" + prodname + ",ģ��:" + modulename
						+ " �ͻ����ļ�ʧ��" + getErrormessage() + ",�޷�����");
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
				File dir = new File(CurrentappHelper.getClassesdir(), "ר���");
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
		// ʵ��hov
		Class clazz;
		try {
			clazz = Class.forName(hovclassname, true,
					DefaultNPParam.classloader);
		} catch (ClassNotFoundException e) {
			// ��������
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
				throw new Exception("�޷�������" + hovclassname + ":"
						+ ee.getMessage());
			}
		}

		try {
			CHovBase hov = (CHovBase) clazz.newInstance();
			return hov;
		} catch (Exception e) {
			throw new Exception("HOV��" + hovclassname + "ʵ����ʧ��:"
					+ e.getMessage());
		}
	}

	public CHovBase downloadProdhov(String hovclassname) throws Exception {
		// ʵ��hov
		Class clazz;
		try {
			clazz = Class.forName(hovclassname, true,
					DefaultNPParam.classloader);
		} catch (ClassNotFoundException e) {
			// ��������
			String sql = "select prodname,modulename from np_hov where classname='"
					+ hovclassname + "'";
			RemotesqlHelper sqlh = new RemotesqlHelper();
			DBTableModel dbmodel = sqlh.doSelect(sql, 0, 1);
			if (dbmodel.getRowCount() == 0) {
				throw new Exception("�޷�����HOV��" + hovclassname
						+ "�����ݿ���Ҳû���ҵ���Ǽ���Ϣ");
			}
			String prodname = dbmodel.getItemValue(0, "prodname");
			String modulename = dbmodel.getItemValue(0, "modulename");
			return downloadProdhov(hovclassname, prodname, modulename);
		}

		try {
			CHovBase hov = (CHovBase) clazz.newInstance();
			return hov;
		} catch (Exception e) {
			throw new Exception("HOV��" + hovclassname + "ʵ����ʧ��:"
					+ e.getMessage());
		}
	}

	public CHovBase downloadZxhov(String hovname) throws Exception {
		Zxhovdownloader zxhovdl = new Zxhovdownloader();
		File zxzipfile = null;
		try {
			zxzipfile = zxhovdl.downloadZxzip(hovname);
			if (zxzipfile == null) {
				throw new Exception("����ר��HOVʧ��" + zxhovdl.getErrormessage());
			}
		} catch (Exception e) {
			throw new Exception("����ר��HOVʧ��" + e.getMessage());
		}

		try {
			CHovBase hov = zxhovdl.createHovfromzxzipfile(zxzipfile);
			return hov;
		} catch (Exception e) {
			throw new Exception("����ר��HOVʧ��" + e.getMessage());
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
