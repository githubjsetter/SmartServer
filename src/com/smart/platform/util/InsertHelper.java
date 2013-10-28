package com.smart.platform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.server.JdbcConnectWraper;

/**
 * insert 语句帮助
 * 
 * @author Administrator InsertHelper ih=new InsertHelper("表名");
 *         ih.bindParam("字段名",变量); ih.bindSequenece("字段名","序列号名");
 *         ih.bindSysdate("字段名");
 * 
 * in.executeInsert(con);执行插入
 */
public class InsertHelper {
	String tablename = "";

	Vector<String> colnames = new Vector<String>();
	Vector<String> vars = new Vector<String>();
	Vector<String> types = new Vector<String>();

	public InsertHelper(String tablename) {
		this.tablename = tablename;
	}

	/**
	 * 设置值
	 * 
	 * @param colname
	 *            列名
	 * @param varvalue
	 *            值
	 */
	public void bindParam(String colname, String varvalue) {
		colnames.add(colname);
		vars.add(varvalue);
		types.add("var");
	}

	public void bindDateParam(String colname, String varvalue) {
		colnames.add(colname);
		vars.add(varvalue);
		types.add("datevar");
	}

	/**
	 * 用序列号赋值
	 * 
	 * @param colname
	 *            列名
	 * @param sequencename
	 *            序列号名
	 */
	public void bindSequence(String colname, String sequencename) {
		colnames.add(colname);
		vars.add(sequencename);
		types.add("sequence");
	}

	/**
	 * 用系统日期设列值
	 * 
	 * @param colname
	 */
	public void bindSysdate(String colname) {
		colnames.add(colname);
		vars.add("sysdate");
		types.add("sysdate");
	}

	Category logger = Category.getInstance(InsertHelper.class);

	public void executeInsert(Connection con) throws Exception {
		StringBuffer sbcols = new StringBuffer();
		StringBuffer sbvars = new StringBuffer();

		Enumeration<String> en = types.elements();
		Enumeration<String> en1 = colnames.elements();
		Enumeration<String> en2 = vars.elements();

		for (int col = 0; en1.hasMoreElements() && en2.hasMoreElements(); col++) {
			String type = en.nextElement();
			String colname = en1.nextElement();
			String value = en2.nextElement();

			if (col > 0) {
				sbcols.append(",");
				sbvars.append(",");
			}
			sbcols.append(colname);

			if (type.equals("sysdate")) {
				sbvars.append("sysdate");
			} else if (type.equals("sequence")) {
				sbvars.append(value + ".nextval");
			} else if (type.equals("datevar")) {
				if(value.length()==19){
					sbvars.append("to_date(?,'yyyy-mm-dd hh24:mi:ss')");
				}else{
					sbvars.append("to_date(?,'yyyy-mm-dd')");
				}
			} else {
				sbvars.append("?");
			}
		}

		String sql = "insert into " + tablename + "(" + sbcols.toString()
				+ ")values(" + sbvars.toString() + ")";
		logger.debug(sql);

		PreparedStatement c1 = null;
		try {
			c1=con.prepareStatement(sql);
			en = types.elements();
			en2 = vars.elements();

			for (int col = 1; en.hasMoreElements() && en2.hasMoreElements(); ) {
				String type = en.nextElement();
				String value = en2.nextElement();

				if (type.equals("sysdate")) {
					continue;
				} else if (type.equals("sequence")) {
					continue;
				} else {
					c1.setString(col++, value);
				}
			}
			int irowcount=c1.executeUpdate();
			logger.debug("inserted rowcount="+irowcount);

		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}

	public static void main(String[] argv) {
		InsertHelper ih = new InsertHelper("mytable");
		ih.bindSequence("id", "mytable_seq");
		ih.bindSysdate("credate");
		ih.bindParam("name", "新名字");

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
			
			ih.executeInsert(con);
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
