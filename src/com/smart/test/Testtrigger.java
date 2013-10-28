package com.smart.test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.smart.platform.util.InsertHelper;

public class Testtrigger {

	void dotest(){
		Connection con = null;
		try {
			con = getTestCon();
			InsertHelper ih = new InsertHelper("test1");
			ih.bindParam("id", "1");
			ih.bindParam("name", "name1");
			ih.executeInsert(con);
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public static void main(String[] args) {
		MathContext mc = new MathContext(12,
				RoundingMode.HALF_UP);
		BigDecimal realmoney=new BigDecimal("41.8");
		BigDecimal rate=new BigDecimal("71");
		BigDecimal m = realmoney.multiply(rate,mc);
		System.out.println(m.toPlainString());
		m = m.divide(new BigDecimal("100"), mc);
		System.out.println(m.toPlainString());

		
		//Testtrigger t=new Testtrigger();
		//t.dotest();
	}

	String dbip = "192.9.200.47";
	String dbname = "orcl";
	String dbuser = "npserver";
	String dbpass = "npserver";

	private Connection getTestCon() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		return con;
	}

}
