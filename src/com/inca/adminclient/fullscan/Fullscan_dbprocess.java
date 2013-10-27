package com.inca.adminclient.fullscan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.server.ServerContext;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMdeModel;

/*功能"查询fullscan"应用服务器处理*/
public class Fullscan_dbprocess extends RequestProcessorAdapter {
	ScanThread scanthread = null;
	DBTableModel fullscandbmodel = null;
	int searchedsqlct = 0;

	public Fullscan_dbprocess() {
		Fullscan_mde mde=new Fullscan_mde(null,"");
		Fullscan_master master = new Fullscan_master(null, mde);
		fullscandbmodel = master.getDBtableModel();
	}

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询fullscan") && !strcmd.equals("explainplan")) {
			return -1;
		}

		if (strcmd.equals("查询fullscan")) {
			synchronized (this) {
				if (scanthread == null) {
					scanthread = new ScanThread();
					scanthread.start();
				}
			}

			resp.addCommand(new StringCommand("+OK"));
			ParamCommand paramcmd = new ParamCommand();
			paramcmd.addParam("searchedsqlct", String.valueOf(searchedsqlct));
			resp.addCommand(paramcmd);

			if (fullscandbmodel.getRowCount() >= 0) {
				DataCommand datacmd = new DataCommand();
				datacmd.setDbmodel(fullscandbmodel);
				resp.addCommand(datacmd);
			}
		} else if (strcmd.equals("explainplan")) {
			StringCommand cmd1=(StringCommand) req.commandAt(1);
			String sql_text=cmd1.getString();
			//开始分析,并返回
			DBTableModel dbmodel=null;
			try{
				dbmodel=explain(sql_text);
			}catch(Exception e){
				logger.error("ERROR",e);
				resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
				return 0;
			}
			DBTableModel dtlmodel=new Fullscan_detail(null,null).getDBtableModel();
			dtlmodel.appendDbmodel(dbmodel);
			
			resp.addCommand(new StringCommand("+OK"));
			DataCommand datacmd=new DataCommand();
			datacmd.setDbmodel(dtlmodel);
			resp.addCommand(datacmd);
		}
		return 0;

	}

	DBTableModel explain(String sql_text) throws Exception{
		Connection con = null;
		PreparedStatement c1 = null;
		String stateid = String.valueOf(System.currentTimeMillis()
				+ statementid++);

		String sql = "explain plan " + "SET STATEMENT_ID = '" + stateid
				+ "' for " + sql_text;
		Statement st=null;
		try {
			con = this.getConnection();
			st=con.createStatement();
			st.execute(sql);
			
			//查询结果
			sql="select '','',statement_id,timestamp,remarks,operation,options,object_node,object_owner,"+
			" object_name,object_instance,object_type,optimizer,search_columns,id,parent_id, "+
			" position,cost from plan_table where STATEMENT_ID=?" +
			" order by id,parent_id,position";
			c1=con.prepareStatement(sql);
			c1.setString(1, stateid);
			ResultSet rs=c1.executeQuery();
			return DBModel2Jdbc.createFromRS(rs);
			
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
				}
			}
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
			if (con != null) {
				try {
					con.rollback();
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	void searchFullscan() {
		searchedsqlct = 0;

		Connection con = null;
		PreparedStatement c1 = null;
		try {
			ServerContext sc=new ServerContext("查询fullscan");
			ServerContext.regServercontext(sc);
			
			con = this.getConnection();
			String sql = "select 'lineno',\n" + "SQL_TEXT,\n"
					+ "SHARABLE_MEM,\n" + "PERSISTENT_MEM,\n"
					+ "RUNTIME_MEM,\n" + "SORTS,\n" + "EXECUTIONS,\n"
					+ "USERS_EXECUTING,\n" + "LOADS,\n" + "FIRST_LOAD_TIME,\n"
					+ "PARSE_CALLS,\n" + "DISK_READS,\n" + "BUFFER_GETS,\n"
					+ "ROWS_PROCESSED,\n" + "COMMAND_TYPE,\n"
					+ "OPTIMIZER_MODE,\n" + "OPTIMIZER_COST,\n" + "ACTION,\n"
					+ "CPU_TIME,\n" + "ELAPSED_TIME " + " from sys.v_$sql ";
			c1 = con.prepareCall(sql);
			ResultSet rs = c1.executeQuery();
			DBTableModel dbmodel = DBModel2Jdbc.createFromRS(rs);
			for (int i = 0; i < dbmodel.getRowCount(); i++) {
				searchedsqlct++;
				String sql_text = dbmodel.getItemValue(i, "SQL_TEXT");
				if (isFullscan(con, sql_text)) {
					if (sql_text.toLowerCase().indexOf("where") < 0)
						continue;// 没有where就算了
					if (!appended(sql_text)) {
						fullscandbmodel.appendRecord(dbmodel.getRecordThunk(i));
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
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	boolean appended(String sql_text) {
		for (int r = 0; r < fullscandbmodel.getRowCount(); r++) {
			if (sql_text.equals(fullscandbmodel.getItemValue(r, "sql_text"))) {
				return true;
			}
		}
		return false;
	}

	int statementid = 0;

	/**
	 * 用explain 来检查sql是不是full scan的
	 * 
	 * @param con
	 * @param sql_text
	 * @return
	 */
	boolean isFullscan(Connection con, String sql_text) {
		String stateid = String.valueOf(System.currentTimeMillis()
				+ statementid++);

		String sql = "explain plan " + "SET STATEMENT_ID = '" + stateid
				+ "' for " + sql_text;
		Statement c1 = null;
		PreparedStatement c2 = null;
		try {
			c1 = con.createStatement();
			c1.execute(sql);
			// 现在查一下有没有fullscan
			sql = "select count(*) ct from plan_table where STATEMENT_ID=? and options like '%FULL%'";
			c2 = con.prepareCall(sql);
			c2.setString(1, stateid);
			ResultSet rs = c2.executeQuery();
			rs.next();
			int ct = rs.getInt(1);
			return ct > 0;
		} catch (SQLException e) {
			// logger.error(sql+" ERROR:",e);
			return false;
		} finally {
			try {
				// 因为我们并不真想需要在plan_table中保存结果,所以rollback
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
			if (c2 != null) {
				try {
					c2.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	class ScanThread extends Thread {
		public void run() {
			searchFullscan();
			synchronized (Fullscan_dbprocess.this) {
				scanthread = null;
			}
		}

	}

	/*
	 * public static void main(String argv[]){ DefaultNPParam.debug=1;
	 * ClientRequest req=new ClientRequest("查找fullscan"); Fullscan_dbprocess
	 * p=new Fullscan_dbprocess(); p.setServerContext( new
	 * ServerContext("1","查找fullscan")); try { p.process(null, req, new
	 * ServerResponse()); while(true) Thread.sleep(1000); } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } }
	 */
}
