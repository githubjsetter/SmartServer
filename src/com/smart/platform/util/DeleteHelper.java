package com.smart.platform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;


public class DeleteHelper {
	String sql=null;
	Vector<String> vars=new Vector<String>();
	
	/**
	 * @param sql É¾³ýµÄsqlÓï¾ä
	 */
	public DeleteHelper(String sql){
		this.sql=sql;
	}
	
	/**
	 * °ó¶¨²ÎÊý
	 * @param v
	 */
	public void bindParam(String v){
		vars.add(v);
	}
	
	/**
	 * É¾³ý
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public void executeDelete(Connection con) throws Exception{
		PreparedStatement c1 = null;
		try {
			c1=con.prepareCall(sql);
			Enumeration<String> en = vars.elements();
			for(int col=1;en.hasMoreElements();){
				String v=en.nextElement();
				c1.setString(col++,v);
			}
			c1.executeUpdate();
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}
	
	public static void main(String[] argv) {
		DeleteHelper sh = new DeleteHelper("delete mytable where id=?");
		sh.bindParam("2");

		String dbip = "192.9.200.145";
		String dbname = "data3";
		String dbuser = "ngpcs41";
		String dbpass = "ngpcs41";
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;
			con = DriverManager.getConnection(url, dbuser, dbpass);
			con.setAutoCommit(false);
			
			sh.executeDelete(con);
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}
	
}
