package com.smart.platform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 更新帮助类
 * @author Administrator
 *
 */
public class UpdateHelper {
	String sql=null;
	Vector<String> vars=new Vector<String>();
	
	/**
	 * @param sql 修改的sql语句
	 */
	public UpdateHelper(String sql){
		this.sql=sql;
	}
	
	/**
	 * 绑定参数
	 * @param v
	 */
	public void bindParam(String v){
		vars.add(v);
	}
	
	/**
	 * 执行修改
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public void executeUpdate(Connection con) throws Exception{
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
		UpdateHelper sh = new UpdateHelper("update mytable set name=? where id=?");
		sh.bindParam("新名称3");
		sh.bindParam("3");

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
			
			sh.executeUpdate(con);
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