package com.smart.platform.logger;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.util.DefaultNPParam;

public class JdbcLogger extends JDBCAppender {

	String prodname="np";
	
	
	public JdbcLogger() {
		super();
		//System.out.println("!!!!!!!!!!!!!!!!!   JdbcLogger start ,prodname="+prodname);
	}

	@Override
	public void append(LoggingEvent event) {
		if(event.getLevel().toInt()!=Level.ERROR_INT){
			return;
		}
		
		Connection con=null;
		PreparedStatement c1=null;
		PreparedStatement c2=null;
		try
		{
			con=mygetConnection();
			String seqid=DBModel2Jdbc.getSeqvalue(con,"np_error_seq");
			//插入总单
			String sql="insert into np_error(Seqid,credate,prodname,classname,errormsg)values(" +
					"?,sysdate,?,?,?)";
			c1=con.prepareStatement(sql);
			c1.setString(1,seqid);
			c1.setString(2,max(prodname,20));
			c1.setString(3, max(event.getLoggerName(),60));
			c1.setString(4, max(event.getRenderedMessage(),100));
			c1.executeUpdate();
			
			//插入细单
			sql="insert into np_error_dtl (dtlid,seqid,dtlmsg)values(" +
			"np_error_dtl_seq.nextval,?,?)";
			c2=con.prepareStatement(sql);
			
			String throwmsgs[]=event.getThrowableStrRep();
			
			for(int i=0;throwmsgs!=null && i<throwmsgs.length;i++){
				c2.setString(1,seqid);
				c2.setString(2,max(throwmsgs[i],200));
				c2.executeUpdate();
			}
			con.commit();
			
		}catch(Exception e){
			try {
				if(con!=null)con.rollback();
			} catch (SQLException e1) {
			}
			//这里不能再调log4j了
			e.printStackTrace();
		}finally{
			if(c1!=null){
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
		//System.out.println("append "+event);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	protected void closeConnection(Connection con) {
		// TODO Auto-generated method stub
		super.closeConnection(con);
	}

	@Override
	protected void execute(String sql) throws SQLException {
	}


	@Override
	protected Connection getConnection() throws SQLException {
		return null;
	}


	
	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public void f1() throws Exception{
		if(true)throw new Exception("f1 error");
	}
	
    protected String dburl="java:comp/env/oracle/db";

    protected Connection mygetConnection() throws Exception{
        if(DefaultNPParam.debug==1){
            return getTestCon();
        }else{
            InitialContext ic=new InitialContext();
            DataSource ds = (DataSource) ic.lookup(dburl);
            return ds.getConnection();
        }
    }
    String dbip=DefaultNPParam.debugdbip;
    String dbname=DefaultNPParam.debugdbsid;
    String dbuser=DefaultNPParam.debugdbusrname;
    String dbpass=DefaultNPParam.debugdbpasswd;

    private Connection getTestCon() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@"+dbip+":1521:"+dbname;

        Connection con = DriverManager.getConnection(url, dbuser, dbpass);
        con.setAutoCommit(false);
        return con;

    }
    
    String max(String s,int len){
    	int curlen=0;
    	for(int i=0;i<s.length();i++){
    		char c=s.charAt(i);
        	StringBuffer sb=new StringBuffer();
        	sb.append(c);
    		int addlen=0;
    		try {
				addlen = sb.toString().getBytes("gbk").length;
			} catch (UnsupportedEncodingException e) {
			}
    		if(addlen + curlen > len){
    			return s.substring(0,i);
    		}
    		curlen+=addlen;
    	}
    	return s;
    }
	
	public static void main(String[] argv) {
		Category logger = Category.getInstance(JdbcLogger.class);
		//logger.info("Test jdbc");
		try{
			JdbcLogger app=new JdbcLogger();
			String maxs = app.max("北京上海", 5);
			System.out.println(maxs);
			app.f1();
		}catch(Exception e){
			logger.error("error",e);
		}
	}
}
