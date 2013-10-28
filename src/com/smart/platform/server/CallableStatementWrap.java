package com.smart.platform.server;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
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
import java.util.Map;

import org.apache.log4j.Category;

public class CallableStatementWrap implements CallableStatement {

	ServerContext context = null;
	CallableStatement stat = null;

	long starttime = 0;
	long endtime = 0;
	String logsql = "";
	HashMap<Integer, String> parammap = new HashMap<Integer, String>();

	public CallableStatementWrap(ServerContext context, CallableStatement stat,
			String sql) {
		super();
		this.context = context;
		this.stat = stat;
		this.logsql = sql;
		context.incCallstatementcount();
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
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
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
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		stat.setByte(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		stat.setBytes(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
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
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		stat.setDate(parameterIndex, x, cal);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		stat.setDouble(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		stat.setFloat(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		stat.setInt(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		stat.setLong(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
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
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		stat.setObject(parameterIndex, x, targetSqlType);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		stat.setObject(parameterIndex, x, targetSqlType, scale);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setRef(int i, Ref x) throws SQLException {
		stat.setRef(i, x);

	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		stat.setShort(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setString(int parameterIndex, String x) throws SQLException {
		stat.setString(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		stat.setTime(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		stat.setTime(parameterIndex, x, cal);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		stat.setTimestamp(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		stat.setTimestamp(parameterIndex, x, cal);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		stat.setURL(parameterIndex, x);
		parammap.put(new Integer(parameterIndex), String.valueOf(x));

	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stat.setUnicodeStream(parameterIndex, x, length);

	}

	public void addBatch(String sql) throws SQLException {
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
		context.logtime(logsql, JdbcMonitorInfo.TYPE_CALLABLESTATEMENT, endtime
				- starttime);
		context.decCallstatementcount();
		usersqlMonitor();
	}

	public boolean execute(String sql) throws SQLException {
		boolean ret = stat.execute(sql);
		return ret;
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		boolean ret = stat.execute(sql, autoGeneratedKeys);
		return ret;
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		boolean ret = stat.execute(sql, columnIndexes);
		return ret;
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		boolean ret = stat.execute(sql, columnNames);
		return ret;
	}

	public int[] executeBatch() throws SQLException {
		int rets[] = stat.executeBatch();
		return rets;
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		ResultSet rs = stat.executeQuery(sql);
		return rs;
	}

	public int executeUpdate(String sql) throws SQLException {
		int ret = stat.executeUpdate(sql);
		return ret;
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		int ret = stat.executeUpdate(sql, autoGeneratedKeys);
		return ret;
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		int ret = stat.executeUpdate(sql, columnIndexes);
		return ret;
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
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

	public Array getArray(int i) throws SQLException {
		return stat.getArray(i);
	}

	public Array getArray(String parameterName) throws SQLException {
		return stat.getArray(parameterName);
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return stat.getBigDecimal(parameterIndex);
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return stat.getBigDecimal(parameterName);
	}

	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		return stat.getBigDecimal(parameterIndex, scale);
	}

	public Blob getBlob(int i) throws SQLException {
		return stat.getBlob(i);
	}

	public Blob getBlob(String parameterName) throws SQLException {
		return stat.getBlob(parameterName);
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		return stat.getBoolean(parameterIndex);
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		return stat.getBoolean(parameterName);
	}

	public byte getByte(int parameterIndex) throws SQLException {
		return stat.getByte(parameterIndex);
	}

	public byte getByte(String parameterName) throws SQLException {
		return stat.getByte(parameterName);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return stat.getBytes(parameterIndex);
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		return stat.getBytes(parameterName);
	}

	public Clob getClob(int i) throws SQLException {
		return stat.getClob(i);
	}

	public Clob getClob(String parameterName) throws SQLException {
		return stat.getClob(parameterName);
	}

	public Date getDate(int parameterIndex) throws SQLException {
		return stat.getDate(parameterIndex);
	}

	public Date getDate(String parameterName) throws SQLException {
		return stat.getDate(parameterName);
	}

	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return stat.getDate(parameterIndex, cal);
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return stat.getDate(parameterName, cal);
	}

	public double getDouble(int parameterIndex) throws SQLException {
		return stat.getDouble(parameterIndex);
	}

	public double getDouble(String parameterName) throws SQLException {
		return stat.getDouble(parameterName);
	}

	public float getFloat(int parameterIndex) throws SQLException {
		return stat.getFloat(parameterIndex);
	}

	public float getFloat(String parameterName) throws SQLException {
		return stat.getFloat(parameterName);
	}

	public int getInt(int parameterIndex) throws SQLException {
		return stat.getInt(parameterIndex);
	}

	public int getInt(String parameterName) throws SQLException {
		return stat.getInt(parameterName);
	}

	public long getLong(int parameterIndex) throws SQLException {
		return stat.getLong(parameterIndex);
	}

	public long getLong(String parameterName) throws SQLException {
		return stat.getLong(parameterName);
	}

	public Object getObject(int parameterIndex) throws SQLException {
		return stat.getObject(parameterIndex);
	}

	public Object getObject(String parameterName) throws SQLException {
		return stat.getObject(parameterName);
	}

	public Object getObject(int i, Map<String, Class<?>> map)
			throws SQLException {
		return stat.getObject(i, map);
	}

	public Object getObject(String parameterName, Map<String, Class<?>> map)
			throws SQLException {
		return stat.getObject(parameterName, map);
	}

	public Ref getRef(int i) throws SQLException {
		return stat.getRef(i);
	}

	public Ref getRef(String parameterName) throws SQLException {
		return stat.getRef(parameterName);
	}

	public short getShort(int parameterIndex) throws SQLException {
		return stat.getShort(parameterIndex);
	}

	public short getShort(String parameterName) throws SQLException {
		return stat.getShort(parameterName);
	}

	public String getString(int parameterIndex) throws SQLException {
		return stat.getString(parameterIndex);
	}

	public String getString(String parameterName) throws SQLException {
		return stat.getString(parameterName);
	}

	public Time getTime(int parameterIndex) throws SQLException {
		return stat.getTime(parameterIndex);
	}

	public Time getTime(String parameterName) throws SQLException {
		return stat.getTime(parameterName);
	}

	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return stat.getTime(parameterIndex, cal);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return stat.getTime(parameterName, cal);
	}

	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return stat.getTimestamp(parameterIndex);
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return stat.getTimestamp(parameterName);
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws SQLException {
		return stat.getTimestamp(parameterIndex, cal);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		return stat.getTimestamp(parameterName, cal);
	}

	public URL getURL(int parameterIndex) throws SQLException {
		return stat.getURL(parameterIndex);
	}

	public URL getURL(String parameterName) throws SQLException {
		return stat.getURL(parameterName);
	}

	public void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		stat.registerOutParameter(parameterIndex, sqlType);
	}

	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		stat.registerOutParameter(parameterName, sqlType);
	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws SQLException {
		stat.registerOutParameter(parameterIndex, sqlType, scale);
	}

	public void registerOutParameter(int paramIndex, int sqlType,
			String typeName) throws SQLException {
		stat.registerOutParameter(paramIndex, sqlType, typeName);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		stat.registerOutParameter(parameterName, sqlType, scale);

	}

	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		stat.registerOutParameter(parameterName, sqlType, typeName);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length)
			throws SQLException {
		stat.setAsciiStream(parameterName, x, length);
	}

	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		stat.setBigDecimal(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		stat.setBinaryStream(parameterName, x, length);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		stat.setBoolean(parameterName, x);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		stat.setByte(parameterName, x);
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		stat.setBytes(parameterName, x);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		stat.setCharacterStream(parameterName, reader, length);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		stat.setDate(parameterName, x);
	}

	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		stat.setDate(parameterName, x, cal);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		stat.setDouble(parameterName, x);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		stat.setFloat(parameterName, x);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		stat.setInt(parameterName, x);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		stat.setLong(parameterName, x);
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		stat.setNull(parameterName, sqlType);
	}

	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		stat.setNull(parameterName, sqlType, typeName);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		stat.setObject(parameterName, x);
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		stat.setObject(parameterName, x, targetSqlType);
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		stat.setObject(parameterName, x, targetSqlType, scale);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		stat.setShort(parameterName, x);
	}

	public void setString(String parameterName, String x) throws SQLException {
		stat.setString(parameterName, x);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		stat.setTime(parameterName, x);
	}

	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		stat.setTime(parameterName, x, cal);
	}

	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		stat.setTimestamp(parameterName, x);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		stat.setTimestamp(parameterName, x, cal);
	}

	public void setURL(String parameterName, URL val) throws SQLException {
		stat.setURL(parameterName, val);
	}

	public boolean wasNull() throws SQLException {
		return stat.wasNull();
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

	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String parameterName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String parameterName, Clob x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String parameterName, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNString(String parameterName, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setRowId(String parameterName, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
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

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * void logtime(String method) { Category
	 * logger=Category.getInstance(CallableStatementWrap.class); long
	 * usetime=endtime-starttime;
	 * logger.debug("sql执行时间:"+usetime+"\t函数:"+method+"()\tsql:"+logsql); }
	 */
}
