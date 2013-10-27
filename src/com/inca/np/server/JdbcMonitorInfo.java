package com.inca.np.server;

/**
 * 服务器上JDBC运行的统计监视信息
 * @author Administrator
 *
 */
public class JdbcMonitorInfo {
	public static final String TYPE_CONNECTION="connection";
	public static final String TYPE_STATEMENT="statement";
	public static final String TYPE_PREPAREDSTATEMENT="preparedstatement";
	public static final String TYPE_CALLABLESTATEMENT="callablestatement";
	
	
	String sql="";
	String type="";
	
	/**
	 * 执行次数
	 */
	int executecount=0;
	
	long totalusetime=0;
	
	long maxusetime=0;
	
	public JdbcMonitorInfo(String type){
		this.type=type;
	}

	public JdbcMonitorInfo(String sql,String type){
		this.sql=sql;
		this.type=type;
	}

	public void setSql(String sql){
		this.sql=sql;
	}
	
	public void logtime(long time){
		executecount++;
		totalusetime+=time;
		if(time>maxusetime)maxusetime=time;
	}

	public String getSql() {
		return sql;
	}

	public String getType() {
		return type;
	}

	public int getExecutecount() {
		return executecount;
	}

	public long getTotalusetime() {
		return totalusetime;
	}

	public long getMaxusetime() {
		return maxusetime;
	}
	
}
