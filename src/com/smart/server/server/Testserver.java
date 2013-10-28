package com.smart.server.server;

import java.sql.Connection;
import java.sql.DriverManager;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.util.DefaultNPParam;

public class Testserver {
	public static void main(String[] args) {
		try {
			DefaultNPParam.debug=1;
			DefaultNPParam.debugdbip="192.9.200.1";
			DefaultNPParam.debugdbsid="data";
			DefaultNPParam.debugdbusrname="npserver";
			DefaultNPParam.debugdbpasswd="npserver";
			
			ClientRequest req=new ClientRequest("产品1命令:保存单据1数据");
			ServerResponse resp=Server.getInstance().process(req);
			StringCommand cmd0=(StringCommand)resp.commandAt(0);
			System.out.println(cmd0.getString());
		} catch (Exception e) {
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
