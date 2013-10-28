package com.smart.server.install;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class Installtester {
	public static void main(String[] args) {
		InstallinfoReader ir=new InstallinfoReader();
		try {
			Installinfo installinfo = ir.read(new File("testdata/installinfo"));
			Connection con=getCon();
			InstallinfoDB.install(con,installinfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static Connection getCon()throws Exception{
		String dbip="192.9.200.1";
		String dbname="data";
		String dbuser="npserver";
		String dbpass="npserver";
		
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@"+dbip+":1521:"+dbname;

        Connection con = DriverManager.getConnection(url, dbuser, dbpass);
        con.setAutoCommit(false);
        return con;
		
	}
}
