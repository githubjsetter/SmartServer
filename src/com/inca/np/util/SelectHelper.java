package com.inca.np.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.gui.control.DBTableModel;


/**
 * 查询辅助
 * @author Administrator
 *
 */
public class SelectHelper {
	String sql=null;
	Vector<String> vars=new Vector<String>();
	
	public SelectHelper(String sql){
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
	 * 查询
	 * @param con
	 * @param startrow  开始行
	 * @param maxrow  最多行
	 * @return
	 * @throws Exception
	 */
	public DBTableModel executeSelect(Connection con,int startrow,int maxrow) throws Exception{
		PreparedStatement c1 = null;
		try {
			c1=con.prepareCall(sql);
			Enumeration<String> en = vars.elements();
			for(int col=1;en.hasMoreElements();){
				String v=en.nextElement();
				c1.setString(col++,v);
			}
			ResultSet rs=c1.executeQuery();
			return DBModel2Jdbc.createFromRS(rs,startrow,maxrow);
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}
	
	public static void main(String[] argv) {
		SelectHelper sh = new SelectHelper("select * from mytable where id=?");
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
			
			DBTableModel dbmodel=sh.executeSelect(con,0,1);
			System.out.println(dbmodel.getRowCount());
			
		} catch (Exception e) {
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
