package com.inca.npserver.timer;

import java.sql.Connection;

import com.inca.np.server.JdbcConnectWraper;
import com.inca.np.server.ServerContext;
import com.inca.npserver.dbcp.DBConnectPoolFactory;

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
