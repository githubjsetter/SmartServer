package com.smart.server.timer;

import java.sql.Connection;

import com.smart.platform.server.JdbcConnectWraper;
import com.smart.platform.server.ServerContext;
import com.smart.server.dbcp.DBConnectPoolFactory;

public abstract class TimerAdapter implements ServertimerIF{

	public abstract String getName() ;

	public abstract long getSecond() ;

	public abstract String getType() ;

	public abstract void onTimer() ;
	
	protected Connection getConnection() throws Exception {
		Connection con = DBConnectPoolFactory.getInstance().getConnection();
		con.setAutoCommit(false);
		ServerContext svrcontext = new ServerContext("timer");
		// logger.info("getconnection,svrcontext="+svrcontext);
		JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
		return conwrap;

	}

}
