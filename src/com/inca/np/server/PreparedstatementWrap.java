package com.inca.np.server;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Category;

public class PreparedstatementWrap implements PreparedStatement {

	ServerContext context = null;
	PreparedStatement stat = null;

	long starttime = 0;
	long endtime = 0;
	String logsql = "";

	HashMap<Integer, String> parammap = new HashMap<Integer, String>();

	public PreparedstatementWrap(ServerContext context, PreparedStatement stat,
			String sql) {
		super();
		this.context = context;
		this.stat = stat;
		this.logsql = sql;
		context.incPreparedstatementcount();
		starttime = System.currentTimeMillis();
	}

	public void addBatch() throws SQLException {
		stat.addBatch();
	}

	public void clearParameters() throws SQLException {
		stat.clearParameters();

	}

	public boolean execute() throws SQLException {
		try {
			boolean ret = stat.execute();
			return ret;
		} catch (SQLException sqle) {
			logErrorsql(sqle.getMessage());
			throw sqle;
		}
	}

	public ResultSet executeQuery() throws SQLException {
		try {
			ResultSet rs = stat.executeQuery();
			return rs;
		} catch (SQLException sqle) {
			logErrorsql(sqle.getMessage());
			throw sqle;
		}
	}

	public int executeUpdate() throws SQLException {
		try {
			int ret = stat.executeUpdate();
			return ret;
		} catch (SQLException sqle) {
			logErrorsql(sqle.getMessage());
			throw sqle;
		}
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return stat.getMetaData();
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return stat.getParameterMetaData();
	}

	public void setArray(int i, Array x) throws SQLException {
		stat.setArray(i, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stat.setAsciiStream(parameterIndex, x, length);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		stat.setBigDecimal(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stat.setBinaryStream(parameterIndex, x, length);

	}

	public void setBlob(int i, Blob x) throws SQLException {
		stat.setBlob(i, x);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		stat.setBoolean(parameterIndex, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		stat.setByte(parameterIndex, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		stat.setBytes(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		stat.setCharacterStream(parameterIndex, reader, length);
	}

	public void setClob(int i, Clob x) throws SQLException {
		stat.setClob(i, x);
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		stat.setDate(parameterIndex, x);
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		stat.setDate(parameterIndex, x, cal);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		stat.setDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		stat.setFloat(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		stat.setInt(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		stat.setLong(parameterIndex, x);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		stat.setNull(parameterIndex, sqlType);
	}

	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		stat.setNull(paramIndex, sqlType, typeName);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		stat.setObject(parameterIndex, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		stat.setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		stat.setObject(parameterIndex, x, targetSqlType, scale);

	}

	public void setRef(int i, Ref x) throws SQLException {
		stat.setRef(i, x);

	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		stat.setShort(parameterIndex, x);

	}

	public void setString(int parameterIndex, String x) throws SQLException {
		stat.setString(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		stat.setTime(parameterIndex, x);

	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		stat.setTime(parameterIndex, x, cal);

	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		stat.setTimestamp(parameterIndex, x);

	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		stat.setTimestamp(parameterIndex, x, cal);

	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		stat.setURL(parameterIndex, x);

	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stat.setUnicodeStream(parameterIndex, x, length);

	}

	public void addBatch(String sql) throws SQLException {
		logsql = sql;
		stat.addBatch();

	}

	public void cancel() throws SQLException {
		stat.cancel();

	}

	public void clearBatch() throws SQLException {
		stat.clearBatch();

	}

	public void clearWarnings() throws SQLException {
		stat.clearWarnings();

	}

	public void close() throws SQLException {
		stat.close();
		endtime = System.currentTimeMillis();
		context.logtime(logsql, JdbcMonitorInfo.TYPE_PREPAREDSTATEMENT, endtime
				- starttime);
		context.decPreparedstatementcount();
		usersqlMonitor();
		// logFullinfo();
	}

	public boolean execute(String sql) throws SQLException {
		logsql = sql;
		boolean ret = stat.execute(sql);
		return ret;
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		logsql = sql;
		boolean ret = stat.execute(sql, autoGeneratedKeys);
		return ret;
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		logsql = sql;
		boolean ret = stat.execute(sql, columnIndexes);
		return ret;
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		logsql = sql;
		boolean ret = stat.execute(sql, columnNames);
		return ret;
	}

	public int[] executeBatch() throws SQLException {
		int rets[] = stat.executeBatch();
		return rets;
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		logsql = sql;
		ResultSet rs = stat.executeQuery(sql);
		return rs;
	}

	public int executeUpdate(String sql) throws SQLException {
		logsql = sql;
		int ret = stat.executeUpdate(sql);
		return ret;
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		logsql = sql;
		int ret = stat.executeUpdate(sql, autoGeneratedKeys);
		return ret;
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		logsql = sql;
		int ret = stat.executeUpdate(sql, columnIndexes);
		return ret;
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		logsql = sql;
		int ret = stat.executeUpdate(sql, columnNames);
		return ret;
	}

	public Connection getConnection() throws SQLException {
		return stat.getConnection();
	}

	public int getFetchDirection() throws SQLException {
		return stat.getFetchDirection();
	}

	public int getFetchSize() throws SQLException {
		return stat.getFetchSize();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return stat.getGeneratedKeys();
	}

	public int getMaxFieldSize() throws SQLException {
		return stat.getMaxFieldSize();
	}

	public int getMaxRows() throws SQLException {
		return stat.getMaxRows();
	}

	public boolean getMoreResults() throws SQLException {
		return stat.getMoreResults();
	}

	public boolean getMoreResults(int current) throws SQLException {
		return stat.getMoreResults(current);
	}

	public int getQueryTimeout() throws SQLException {
		return stat.getQueryTimeout();
	}

	public ResultSet getResultSet() throws SQLException {
		return stat.getResultSet();
	}

	public int getResultSetConcurrency() throws SQLException {
		return stat.getResultSetConcurrency();
	}

	public int getResultSetHoldability() throws SQLException {
		return stat.getResultSetHoldability();
	}

	public int getResultSetType() throws SQLException {
		return stat.getResultSetType();
	}

	public int getUpdateCount() throws SQLException {
		return stat.getUpdateCount();
	}

	public SQLWarning getWarnings() throws SQLException {
		return stat.getWarnings();
	}

	public void setCursorName(String name) throws SQLException {
		stat.setCursorName(name);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		stat.setEscapeProcessing(enable);
	}

	public void setFetchDirection(int direction) throws SQLException {
		stat.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException {
		stat.setFetchSize(rows);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		stat.setMaxFieldSize(max);
	}

	public void setMaxRows(int max) throws SQLException {
		stat.setMaxRows(max);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		stat.setQueryTimeout(seconds);
	}

	protected String buildSqlparamstring() {
		LinkedList<Integer> sortkey = new LinkedList(parammap.keySet());
		Collections.sort(sortkey);
		Iterator<Integer> it = sortkey.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			Integer keyindex = (Integer) it.next();
			String paramvalue = parammap.get(keyindex);
			sb.append(paramvalue + ",");
		}
		return sb.toString();
	}

	protected void usersqlMonitor() {
		if (context == null || context.getUserinfo() == null)
			return;
		String userid = context.getUserinfo().getUserid();

		String paramstring = buildSqlparamstring();
		UsersqlMonitor usersqlm = UsersqlMonitor.getInstance();
		long usetime = System.currentTimeMillis() - starttime;
		usersqlm.addlog(userid, logsql, paramstring, usetime);
	}

	protected void logErrorsql(String errmsg) {
		String paramstring = buildSqlparamstring();
		StringBuffer sb = new StringBuffer();
		sb.append("!!!!!!!!!!!!!!ERROR SQL!!!!!!!!!!!!!!\n");
		sb.append(errmsg+"\n");
		sb.append("sql:\n" + logsql + "\n");
		sb.append("param:\n" + paramstring);
		Category.getInstance(PreparedstatementWrap.class).error(sb.toString());
	}

	/**
	 * 记录sql和参数
	 * 
	 * @deprecated
	 */
	protected void logFullinfo() {
		if (logsql.toLowerCase().indexOf("bms_st_io_doc") < 0)
			return;
		LinkedList<Integer> sortkey = new LinkedList(parammap.keySet());
		Collections.sort(sortkey);
		Iterator<Integer> it = sortkey.iterator();
		StringBuffer sb = new StringBuffer();
		sb.append("Exec sql=" + logsql + ",参数:\r\n");
		while (it.hasNext()) {
			Integer keyindex = (Integer) it.next();
			String paramvalue = parammap.get(keyindex);
			sb.append(keyindex.intValue() + "," + paramvalue + "\r\n");
		}
		Category logger = Category.getInstance(PreparedstatementWrap.class);
		logger.info(sb.toString());
	}

	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
