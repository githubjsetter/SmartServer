package com.smart.server.timer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Category;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.JdbcConnectWraper;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;
import com.smart.server.dbcp.DBConnectPoolFactory;
import com.smart.server.server.sysproc.CurrentappHelper;

public class TimerManager {
	boolean reload=false;
	private static TimerManager instance = null;
	Category logger = Category.getInstance(TimerManager.class);

	Vector<ServertimerIF> timers = new Vector<ServertimerIF>();

	public static TimerManager getInstance() {
		if (instance == null) {
			instance = new TimerManager();
		}
		return instance;
	}

	private TimerManager() {
	}

	public void loadTimer() {

		Timerthread t = new Timerthread();
		t.setDaemon(true);
		t.start();

	}

	void searchJarfile(File file, Vector<String> classnames) {
		try {
			JarFile jarfile = new JarFile(file);
			Enumeration<JarEntry> en = jarfile.entries();
			while (en.hasMoreElements()) {
				JarEntry jarentry = en.nextElement();
				String fn = jarentry.getName();
				if (!fn.endsWith(".class")) {
					continue;
				}
				fn = fn.substring(0, fn.length() - 6);
				fn = fn.replace("/", ".");
				classnames.add(fn);
			}
		} catch (IOException e) {
			logger.error("error", e);
		}
	}

	class Timerthread extends Thread {
		
		void loadClass(){
			// 从WEB-INF/classes/timer目录下找jar文件.并加载.
			logger.debug("start load timer");
			File classdir = CurrentappHelper.getClassesdir();
			File timerdir = new File(classdir, "timer");
			if (timerdir == null) {
				return;
			}

			Vector<File> jarfiles = new Vector<File>();
			File fs[] = timerdir.listFiles();
			for (int i = 0; fs != null && i < fs.length; i++) {
				File f = fs[i];
				if (!f.getName().endsWith(".jar")) {
					continue;
				}
				jarfiles.add(f);
			}

			Vector<String> classnames = new Vector<String>();
			ArrayList<URL> ar = new ArrayList<URL>();
			Enumeration<File> en = jarfiles.elements();
			while (en.hasMoreElements()) {
				File jarfile = en.nextElement();
				try {
					URL u = new URL("file:" + jarfile.getAbsolutePath());
					ar.add(u);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchJarfile(jarfile, classnames);

			}
			URL[] urls = new URL[ar.size()];
			ar.toArray(urls);

			Thread currentthread = Thread.currentThread();
			URLClassLoader cl = new URLClassLoader(urls, currentthread.getContextClassLoader());

			Enumeration<String> classnameen = classnames.elements();
			while (classnameen.hasMoreElements()) {
				String classname = classnameen.nextElement();
				try {
					Class clazz = cl.loadClass(classname);
					logger.debug("timer class is "+clazz);
					Object oo = clazz.newInstance();
					if (!(oo instanceof ServertimerIF)) {
						continue;
					}
					// System.out.println(clazz+" is timer");
					ServertimerIF timer = (ServertimerIF) oo;
					timers.add(timer);
					logger.info("Add timer "+timer);

				} catch (ClassNotFoundException e) {
					logger.error("error",e);
				} catch (InstantiationException e) {
					//logger.error("error",e);
				} catch (IllegalAccessException e) {
					//logger.error("error",e);
				}
			}
			reload=false;
		}

		public void run() {
			loadClass();
			
			for (;;) {
				try {
					Enumeration<ServertimerIF> en = timers.elements();
					while (en.hasMoreElements()) {
						ServertimerIF timer = en.nextElement();
						execTimer(timer);
						if(reload){
							loadTimer();
							return;
						}
					}
					Thread.sleep(1000L * 60L);
					if(reload){
						loadTimer();
						return;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	void execTimer(ServertimerIF timer) {
		logger.debug("check timer "+timer+",type="+timer.getType()+",second="+timer.getSecond());
		Connection con = null;
		try {
			con = getConnection();
			// 取最近一次执行
			String sql = "select max(credate) credate,to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') strsysdate " +
					" from np_timer_log where classname=?"
					+ " and credate>sysdate - 2";
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(timer.getClass().getName());
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			String lastcredate = "";
			String sysdate = "";
			lastcredate = dm.getItemValue(0, "credate");
			sysdate = dm.getItemValue(0, "strsysdate");
			if (lastcredate == null || lastcredate.length() == 0) {
				lastcredate = "1980-01-01 00:00:00";
			}
			Calendar lastcalendar = Calendar.getInstance();
			lastcalendar.setTime(datefmt.parse(lastcredate));

			Calendar nowcalender = Calendar.getInstance();
			nowcalender.setTime(datefmt.parse(sysdate));

			logger.debug("lastcredate="+lastcredate+",now="+sysdate);
			
			String type = timer.getType();
			if (type.equals(ServertimerIF.TYPE_EVERYDAY)) {
				// 每天一次
				if (nowcalender.get(Calendar.YEAR) == lastcalendar
						.get(Calendar.YEAR)
						&& nowcalender.get(Calendar.DAY_OF_YEAR) == lastcalendar
								.get(Calendar.DAY_OF_YEAR)) {
					// 今天执行过了.
					return;
				}
				// 如果今天没有执行过,到该执行的时间了吗?
				long execsec = timer.getSecond();
				long nowsec = nowcalender.get(Calendar.HOUR_OF_DAY) * 3600
						+ nowcalender.get(Calendar.MINUTE) * 60
						+ nowcalender.get(Calendar.SECOND);
				if (nowsec < execsec) {
					// 没有到执行时间呢.
					return;
				}
			} else if (type.equals(ServertimerIF.TYPE_LOOP)) {
				// 循环执行,检查到执行时间了吗?
				long execsec = timer.getSecond();
				if ((nowcalender.getTimeInMillis() - lastcalendar
						.getTimeInMillis()) / 1000l >= execsec) {
					// 该执行
				} else {
					return;
				}

			} else {
				// 不明类型
				logger.error("不明" + type + ",无法执行");
				return;
			}

			// 需要执行.先记录log
			InsertHelper ih = new InsertHelper("np_timer_log");
			String seqid = DBModel2Jdbc.getSeqvalue(con, "np_timer_log_seq");
			ih.bindParam("seqid", seqid);
			ih.bindSysdate("credate");
			ih.bindParam("classname", timer.getClass().getName());
			ih.bindParam("timername", timer.getName());
			ih.bindParam("timertype", timer.getType());
			ih.bindParam("timersec", String.valueOf(timer.getSecond()));
			ih.executeInsert(con);
			con.commit();

			// 考虑到可能执行时间长,长时间占用connection不合适.先释放了.
			con.close();
			con = null;

			long t1 = System.currentTimeMillis();
			timer.onTimer();
			t1 = System.currentTimeMillis() - t1;

			// 重新得到Connection
			con = getConnection();
			UpdateHelper uh = new UpdateHelper(
					"update np_timer_log set execsec=? where seqid=?");
			uh.bindParam(String.valueOf(t1));
			uh.bindParam(seqid);
			uh.executeUpdate(con);
			con.commit();

		} catch (Exception e) {
			logger.error("Error", e);
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	protected Connection getConnection() throws Exception {
		Connection con = DBConnectPoolFactory.getInstance().getConnection();
		con.setAutoCommit(false);
		ServerContext svrcontext = new ServerContext("timer");
		// logger.info("getconnection,svrcontext="+svrcontext);
		JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
		return conwrap;

	}
	
	public void reload(){
		reload=true;
	}

	public static void main(String[] args) {
		TimerManager tm = TimerManager.getInstance();
		tm.loadTimer();
		synchronized (tm) {
			try {
				tm.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
