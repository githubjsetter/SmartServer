package com.inca.np.server;

import java.util.HashMap;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;

/**
 * ���������ô�����
 * 
 * @author Administrator
 * 
 */
public class ServerContext {
	int jdbccount = 0;
	int preparedstatementcount = 0;
	int statementcount = 0;
	int callstatementcount = 0;

	String command;
	Userruninfo userinfo;

	/**
	 * һ���߳̿��ܵ��ö�� requestprocessor
	 */
	int threadcount = 1;

	static HashMap<String, JdbcMonitorInfo> infomap = new HashMap<String, JdbcMonitorInfo>();

	public ServerContext() {

	}

	public ServerContext(String command) {
		super();
		this.command = command;
		threadcount = 1;
	}

	public String getCommand() {
		return command;
	}

	public Userruninfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(Userruninfo userinfo) {
		this.userinfo = userinfo;
	}

	public void incConnect() {
		jdbccount++;
	}

	public void decConnect() {
		if (jdbccount > 0)
			jdbccount--;
	}

	public void incPreparedstatementcount() {
		preparedstatementcount++;
	}

	public void decPreparedstatementcount() {
		if (preparedstatementcount > 0)
			preparedstatementcount--;
	}

	public void incStatementcount() {
		statementcount++;
	}

	public void decStatementcount() {
		if (statementcount > 0)
			statementcount--;
	}

	public void incCallstatementcount() {
		callstatementcount++;
	}

	public void decCallstatementcount() {
		if (callstatementcount > 0)
			callstatementcount--;
	}

	public int getJdbccount() {
		return jdbccount;
	}

	public int getPreparedstatementcount() {
		return preparedstatementcount;
	}

	public int getStatementcount() {
		return statementcount;
	}

	public int getCallstatementcount() {
		return callstatementcount;
	}

	public void check(Category logger) {
		String cmd = getCommand();
		if (getJdbccount() != 0) {
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "�����ݿ�����û�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		if (this.getPreparedstatementcount() != 0) {
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��PreparedStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
		}

		if (this.getStatementcount() != 0) {
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��Statementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
		}

		if (this.getCallstatementcount() != 0) {
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
			logger.error("!!!!!!!!!!!!!!!!!!!��������:" + cmd
					+ "��CallableStatementû�йر�!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	/**
	 * 
	 * @param sql
	 * @param type
	 * @param time
	 */
	public void logtime(String sql, String type, long time) {
		synchronized (infomap) {
			JdbcMonitorInfo info = infomap.get(sql);
			if (info == null) {
				info = new JdbcMonitorInfo(sql, type);
				infomap.put(sql, info);
			}
			info.logtime(time);
		}
	}

	public static synchronized HashMap<String, JdbcMonitorInfo> getJdbcMonitormap() {
		return infomap;
	}

	/**
	 * ���߳�ID����ServerContext��ϵ
	 */
	private static HashMap<String, ServerContext> threadservercontextmap = new HashMap<String, ServerContext>();

	public static synchronized void regServercontext(ServerContext sc) {
		ServerContext oldsc = getServercontext();
		if (oldsc != null) {
			oldsc.incThreadcount();
		} else {

			threadservercontextmap.put(String.valueOf(Thread.currentThread()
					.getId()), sc);
		}
	}

	public static synchronized ServerContext getServercontext() {
		return threadservercontextmap.get(String.valueOf(Thread.currentThread()
				.getId()));
	}

	public static synchronized ServerContext releaseServercontext() {
		ServerContext oldsc = getServercontext();
		if (oldsc != null) {
			oldsc.decThreadcount();
			if (oldsc.getThreadcount() == 0) {
				return threadservercontextmap.remove(String.valueOf(Thread
						.currentThread().getId()));
			}
			return oldsc;

		} else {
			return null;
		}
	}

	private void incThreadcount() {
		threadcount++;
	}

	private void decThreadcount() {
		threadcount--;
	}

	private int getThreadcount() {
		return threadcount;
	}

}
