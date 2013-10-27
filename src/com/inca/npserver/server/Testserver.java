package com.inca.npserver.server;

import java.sql.Connection;
import java.sql.DriverManager;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.util.DefaultNPParam;

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
