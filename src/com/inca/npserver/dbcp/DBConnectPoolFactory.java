package com.inca.npserver.dbcp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.ServerContext;
import com.inca.npserver.server.sysproc.CurrentappHelper;

public class DBConnectPoolFactory {
	private static DBConnectPoolFactory dbcpf = null;
	Category logger = Category.getInstance(DBConnectPoolFactory.class);

	public static DBConnectPoolFactory getInstance() {
		if (dbcpf == null) {
			dbcpf = new DBConnectPoolFactory();
		}
		return dbcpf;
	}

	Vector<DBConnectPool> pools = new Vector<DBConnectPool>();
	/**
	 * oracle system用户的pool
	 */
	DBConnectPool systempool = null;

	private DBConnectPoolFactory() {
		initPool();
	}

	/**
	 * 从webapp/WEB-INF/dbcp目录中找dbcp0.properties ,dbcp1.properties等
	 */
	synchronized void initPool() {
		clearPools();
		File appdir = CurrentappHelper.guessAppdir();
		File dbcpdir = new File(appdir, "dbcp");
		if(new File(appdir,"WEB-INF/dbcp").exists()){
			dbcpdir = new File(appdir, "WEB-INF/dbcp");
		}
		File fs[] = dbcpdir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if (f.getName().startsWith("dbcp")
					&& f.getName().endsWith(".properties")) {
				DBConnectPool dbcp = new DBConnectPool(f);
				pools.add(dbcp);
			}
		}

		createSystempool();
	}
	
	public synchronized void createSystempool(){
		if(systempool!=null){
			systempool.clear();
		}
		DBConnectPool masterpool = null;
		Enumeration<DBConnectPool>en=pools.elements();
		while(en.hasMoreElements()){
			DBConnectPool dbcp=en.nextElement();
			if (dbcp.getName().equals("主连接")) {
				masterpool = dbcp;
				break;
			}
		}
		
		// 创建system用户的pool。算法为找主连接。将用户密码改为system和system密码。
		if (masterpool != null) {
			Properties tmpprops = masterpool.getProps();
			Properties props=new Properties();
			props.putAll(tmpprops);
			
			
			props.setProperty("username", "SYSTEM");
			props.setProperty("password", getSystempassword());
			props.setProperty("maxActive", "10");
			props.setProperty("maxIdle", "10");
			props.setProperty("maxWait", "10000");
			systempool = new DBConnectPool(props);
		}

	}

	synchronized void clearPools() {
		while (pools.size() > 0) {
			DBConnectPool dbcp = pools.elementAt(0);
			dbcp.clear();
			pools.remove(0);
		}
	}

	public synchronized void reload() {
		initPool();
	}

	/**
	 * 取得数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() throws Exception {
		// 取得背景
		ServerContext sc = ServerContext.getServercontext();
		// Userruninfo userinfo=sc.getUserinfo();
		// 由userinfo取得数据库连接池名
		if (pools.size() == 0) {
			throw new Exception("没有配置数据库连接池，请用系统npadmin进行配置");
		}
		// 在没有启用人员和连接池关系前，应该使用第0个连接池
		DBConnectPool pool = pools.elementAt(0);
		return pool.getConnection();
	}

	/**
	 * 取得SYSTEM用户连接。
	 * 
	 * @return
	 * @throws Exception
	 */
	public Connection getSysconnection() throws Exception {
		// 取得背景
		if (systempool == null) {
			throw new Exception("请用系统npadmin进行配置连接池和SYSTEM用户密码");
		}
		return systempool.getConnection();
	}

	public DBConnectPool getDefaultpool(){
		return pools.elementAt(0);
	}
	
	public Vector<DBConnectPool> getPools() {
		return pools;
	}


	public static void savePropfile(DBTableModel dbmodel, int row) {
		File appdir = CurrentappHelper.guessAppdir();
		File dbcpdir = new File(appdir, "WEB-INF/dbcp");
		dbcpdir.mkdirs();
		String name = dbmodel.getItemValue(row, "name");

		File f = new File(dbcpdir, "dbcp_" + name + ".properties");
		f.getParentFile().mkdirs();
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(f);
			Properties prop = new Properties();
			prop.put("name", name);
			prop.put("driverClassName", dbmodel.getItemValue(row,
					"driverClassName"));
			prop.put("url", dbmodel.getItemValue(row, "url"));
			prop.put("username", dbmodel.getItemValue(row, "username"));
			prop.put("password", dbmodel.getItemValue(row, "password"));
			prop.put("maxActive", dbmodel.getItemValue(row, "maxActive"));
			prop.put("maxIdle", dbmodel.getItemValue(row, "maxIdle"));
			prop.put("maxWait", dbmodel.getItemValue(row, "maxWait"));

			prop.store(fout, "dbcp config");
		} catch (Exception e) {
			Category.getInstance(DBConnectPoolFactory.class).error("error", e);
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static boolean removeDbcp(String name) {
		File appdir = CurrentappHelper.guessAppdir();
		File dbcpdir = new File(appdir, "dbcp");
		if(new File(appdir,"WEB-INF/dbcp").exists()){
			dbcpdir = new File(appdir, "WEB-INF/dbcp");
		}

		File f = new File(dbcpdir, "dbcp_" + name + ".properties");
		return f.delete();
	}

	/**
	 * 
	 * @return
	 */
	public static String getSystempassword() {
		File appdir = CurrentappHelper.guessAppdir();
		File dbcpdir = new File(appdir, "dbcp");
		if(new File(appdir,"WEB-INF/dbcp").exists()){
			dbcpdir = new File(appdir, "WEB-INF/dbcp");
		}

		File f = new File(dbcpdir, "system.properties");
		if (!f.exists())
			return "manager";
		InputStream in = null;
		try {
			in = new FileInputStream(f);
			Properties props = new Properties();
			props.load(in);
			String systempassword = props.getProperty("systempassword", "");
			if (systempassword.length() == 0)
				return "";
			BASE64Decoder b64d = new BASE64Decoder();
			byte[] bytepassword = b64d.decodeBuffer(new ByteArrayInputStream(
					systempassword.getBytes()));
			return new String(bytepassword);
		} catch (Exception e) {
			Category.getInstance(DBConnectPoolFactory.class).error("error", e);
			return "manager";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public static void setSystempassword(String systempassword) {
		File appdir = CurrentappHelper.guessAppdir();
		File dbcpdir = new File(appdir, "WEB-INF/dbcp");
		dbcpdir.mkdirs();
		File f = new File(dbcpdir, "system.properties");
		OutputStream out = null;
		InputStream in = null;
		try {
			Properties props = new Properties();
			if (f.exists()) {
				in = new FileInputStream(f);
				props.load(in);
				in.close();
				in=null;
			}
			BASE64Encoder b64e = new BASE64Encoder();
			String b64password = b64e.encode(systempassword.getBytes());
			props.setProperty("systempassword", b64password);
			out=new FileOutputStream(f);
			props.store(out, "systempassword");
		} catch (Exception e) {
			Category.getInstance(DBConnectPoolFactory.class).error("error", e);
			return ;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

	}


	public static void main(String[] args) {
		setSystempassword("Abc");
		System.out.println(getSystempassword());
		DBConnectPoolFactory dbcf = DBConnectPoolFactory.getInstance();
		try {
			Connection con = dbcf.getConnection();
			System.out.println(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
