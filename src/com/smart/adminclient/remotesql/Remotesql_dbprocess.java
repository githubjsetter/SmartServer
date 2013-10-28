package com.smart.adminclient.remotesql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.SqlCommand;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * ��ѯ,������ǲ�������
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
	 * ����sql,��ȡfrom�����,����Ǳ�,��ѯ����,����memds�е������,����Ϊ����
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
		
		//��ѯ������
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
				//����������
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
	 * �ɶ�sql���й���
	 * 
	 * @param sql
	 * @return
	 */
	protected String filteSql(Userruninfo userinfo, String sql) {
		return sql;
	}
}
