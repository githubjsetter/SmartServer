package com.smart.server.dbcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Category;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory;

public class DBConnectPool {
	private BasicDataSource datasource=null;
	Category logger=Category.getInstance(DBConnectPool.class);
	
	File configfile=null;
	private Properties props;
	DBConnectPool(File configfile){
		this.configfile=configfile;
		createDatasource();
	}
	
	DBConnectPool(Properties props){
		this.props=props;
		try {
			datasource = (BasicDataSource) BasicDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			logger.error("error",e);
		}
	}
	
	public String getName(){
		return props.getProperty("name","");
	}

	public String getDriverclassname(){
		return props.getProperty("driverClassName","oracle.jdbc.driver.OracleDriver");
	}
	public String getUrl(){
		return props.getProperty("url","jdbc:oracle:thin:@192.168.1.1:1521:sid");
	}
	public String getUsername(){
		return props.getProperty("username","");
	}
	public String getPassword(){
		return props.getProperty("password","");
	}
	public String getMaxActive(){
		return props.getProperty("maxActive","100");
	}
	public String getMaxIdle(){
		return props.getProperty("maxIdle","10");
	}
	public String getMaxWait(){
		return props.getProperty("maxWait","10000");
	}
	
	synchronized void  createDatasource(){
		if(datasource==null){
			InputStream in=null;
			try{
				in=new FileInputStream(configfile);
				props = new Properties();
				props.load(in);
				datasource = (BasicDataSource) BasicDataSourceFactory.createDataSource(props);
			}
			catch(Exception e){
				
			}finally{
				if(in!=null){
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}
/*	        p.setProperty("driverClassName", "oracle.jdbc.driver.OracleDriver");
	        p.setProperty("url", "jdbc:oracle:thin:@192.9.200.1:1521:data");
	        p.setProperty("password", "xjxty");
	        p.setProperty("username", "xjxty");
	        p.setProperty("maxActive", "30");
	        p.setProperty("maxIdle", "10");
	        p.setProperty("maxWait", "1000");
	        p.setProperty("removeAbandoned", "false");
	        p.setProperty("removeAbandonedTimeout", "120");
	        p.setProperty("testOnBorrow", "true");
	        p.setProperty("logAbandoned", "true");
*/
		}
	}
	
	public synchronized Connection getConnection() throws Exception{
		Connection con=null;
		if(datasource==null){
			createDatasource();
		}
		//System.out.println("threadid="+Thread.currentThread().getId());
		if(datasource != null){
			con=datasource.getConnection();
		}
		if(con!=null)
		con.setAutoCommit(false);
		return con;
	}

	public void clear() {
		//Çå³ý
		if(datasource!=null){
			try {
				datasource.close();
			} catch (SQLException e) {
			}
		}
	}

	public Properties getProps() {
		return props;
	}

}
