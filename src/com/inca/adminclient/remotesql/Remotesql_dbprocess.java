package com.inca.adminclient.remotesql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.SqlCommand;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * 查询,并检查是不是主键
 * 
 * @author Administrator
 * 
 */
public class Remotesql_dbprocess extends RequestProcessorAdapter {
	Category logger = Category.getInstance(Remotesql_dbprocess.class);

	protected String svrcommand = "selectwithpk";

	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		CommandBase cmd = req.commandAt(0);
		if (!(cmd instanceof StringCommand)) {
			return -1;
		}

		StringCommand strcmd = (StringCommand) cmd;
		if (!strcmd.getString().equals(svrcommand)) {
			return -1;
		}

		cmd = req.commandAt(1);
		if (!(cmd instanceof SqlCommand)) {
			return -1;
		}

		SqlCommand sqlcmd = (SqlCommand) cmd;
		String sql = sqlcmd.getSql();

		sql = filteSql(userinfo, sql);

		int startrow = sqlcmd.getStartrow();
		int maxrowcount = sqlcmd.getMaxrowcount();

		StringCommand respstrcmd = null;
		DataCommand datacmd = new DataCommand();

		DBTableModel memds = null;

		Connection con = null;
		PreparedStatement c1 = null;
		try {
			//logger.info("begin getConnection()");
			con = getConnection();
			//logger.info("got Connection()");
			logger.debug(sql);
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			//logger.info("begin createFromRS");
			memds = DBModel2Jdbc.createFromRS(rs, startrow, maxrowcount);
			datacmd.setDbmodel(memds);

			ParamCommand paramcmd=new ParamCommand();
			if(startrow==0){
				searchPK(con, sql, memds,paramcmd);
			}

			respstrcmd = new StringCommand("+OK");
			resp.addCommand(respstrcmd);
			resp.addCommand(datacmd);
			resp.addCommand(paramcmd);
			//logger.info("processed");
		} catch (Exception e) {
			logger.error("SelectProcessor,sql=" + sql, e);
			respstrcmd = new StringCommand("-ERROR " + e.getMessage());
			resp.addCommand(respstrcmd);
		} finally {
			if (c1 != null) {
				c1.close();
			}
			if (con != null) {
				con.close();
			}
		}
		return 0;

	}

	/**
	 * 分析sql,提取from后面的,如果是表,查询主键,并在memds中的相关列,设置为主键
	 * 
	 * @param con
	 * @param sql
	 * @param memds
	 */
	void searchPK(Connection con, String sql, DBTableModel memds,ParamCommand paramcmd) {
		String s = sql.toLowerCase();
		Pattern pattern = Pattern.compile("\\bfrom\\b");
		Matcher m = pattern.matcher(s);
		if (!m.find())
			return;
		int p1 = m.end();
		pattern = Pattern.compile("\\b\\w{1,}\\b");
		m = pattern.matcher(sql);
		if (!m.find(p1))
			return;
		String tablename = sql.substring(m.start(), m.end());
		String pkname = searchPkname(con, tablename);
		if (pkname == null || pkname.length() == 0) {
			return;
		}
		
		//查询主键列
		if(searchPknamecol(con,tablename,pkname,memds)){
			paramcmd.addParam("tablename",tablename);
		}
	}

	String searchPkname(Connection con, String tablename) {
		String sql = "select constraint_name from user_constraints where table_name=? and constraint_type='P'";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			c1.setString(1, tablename.toUpperCase());
			ResultSet rs = c1.executeQuery();
			if (rs.next()) {
				return rs.getString("constraint_name");
			}
			return "";
		} catch (Exception e) {
			logger.error("ERROR", e);
			return "";
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	boolean searchPknamecol(Connection con, String tablename,String pkname,DBTableModel dbmodel) {
		String sql = "select column_name from user_cons_columns " +
				" where table_name=? and constraint_name=?";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			c1.setString(1, tablename.toUpperCase());
			c1.setString(2, pkname.toUpperCase());
			ResultSet rs = c1.executeQuery();
			Vector<String> pkcolnames=new Vector<String>(); 
			while (rs.next()) {
				pkcolnames.add(rs.getString("column_name"));
			}
			if(pkcolnames.size()==1){
				//设置主键列
				String pkcolname=pkcolnames.elementAt(0);
				Enumeration<DBColumnDisplayInfo> en=dbmodel.getDisplaycolumninfos().elements();
				while(en.hasMoreElements()){
					DBColumnDisplayInfo colinfo=en.nextElement();
					if(colinfo.getColname().equalsIgnoreCase(pkcolname)){
						colinfo.setIspk(true);
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static void main(String[] argv) {
		Remotesql_dbprocess app = new Remotesql_dbprocess();
		String sql = "select * from bms_sa_doc,bms_sa_dtl";
		//app.searchPK(null, sql, null);
	}

	/**
	 * 可对sql进行过滤
	 * 
	 * @param sql
	 * @return
	 */
	protected String filteSql(Userruninfo userinfo, String sql) {
		return sql;
	}
}
