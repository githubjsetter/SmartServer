package com.smart.bi.server;

import java.sql.Connection;
import java.sql.DriverManager;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SelectHelper;

/**
 * 数据源信息
 * 
 * @author user
 * 
 */
public class Dsinfo {
	String dstype = "oracle";
	String ip;
	String dbname;
	String username;
	String password;
	int port = 1521;

	public String getDstype() {
		return dstype;
	}

	public void setDstype(String dstype) {
		this.dstype = dstype;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Connection getConnect() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbname;

		Connection con = DriverManager.getConnection(url, username, password);
		con.setAutoCommit(false);
		return con;
	}

	public void loadFromdb(Connection con, String dsid) throws Exception {
		String sql = "select * from npbi_ds where dsid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(dsid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		this.dbname = dm.getItemValue(0, "dbname");
		this.dstype = dm.getItemValue(0, "dbtype");
		this.ip = dm.getItemValue(0, "dbip");
		try {
			this.port = Integer.parseInt(dm.getItemValue(0, "dbport"));
		} catch (Exception e) {
		}
		this.username = dm.getItemValue(0, "dbusername");
		this.password = dm.getItemValue(0, "password");
	}

}
