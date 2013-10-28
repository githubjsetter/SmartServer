package com.smart.server.server.sysproc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.JdbcConnectWraper;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SelectHelper;
import com.smart.server.dbcp.DBConnectPoolFactory;
import com.smart.server.prod.LicenseManager;

/**
 * 将已授权的MAC加载到内存.
 * 
 * @author Administrator
 * 
 */
public class MacManager {
	HashMap<String, String> macmap = new HashMap<String, String>();
	Category logger = Category.getInstance(MacManager.class);

	private static MacManager inst = null;

	public static synchronized MacManager getInst() {
		if (inst == null) {
			inst = new MacManager();
			inst.reload();
		}
		return inst;
	}

	public boolean isHas(String mac) {
		synchronized (macmap) {
			logger.debug("max license ="
					+ LicenseManager.getInst().getMaxClient() + ",mac=" + mac
					+ " ,has mac=" + (macmap.get(mac) != null));
			return macmap.get(mac) != null;
		}
	}

	public void reload() {
		DBTableModel macmodel = null;
		Connection con = null;
		try {
			con = getConnection();
			LicenseManager lm = LicenseManager.getInst();
			SelectHelper sh = new SelectHelper(
					"select mac from np_mac order by approvedate desc");
			logger.debug("lm.getMaxClient()="+lm.getMaxClient());
			macmodel = sh.executeSelect(con, 0, lm.getMaxClient());
			logger.debug("macmodel.getrowcount()="+macmodel.getRowCount());

		} catch (Exception e) {
			logger.error("error", e);
			return;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

		synchronized (macmap) {
			macmap.clear();
			for (int r = 0; r < macmodel.getRowCount(); r++) {
				String mac = macmodel.getItemValue(r, "mac");
				logger.debug("macmap put "+mac);
				macmap.put(mac, mac);
			}
			logger.debug("macmap.size()="+macmap.size());
		}

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
			 * ds.getConnection(); con.setAutoCommit(false); return con;
			 */
			Connection con = DBConnectPoolFactory.getInstance().getConnection();
			con.setAutoCommit(false);
			return con;

		}
	}

}
