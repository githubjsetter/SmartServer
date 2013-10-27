package com.inca.adminclient.remotesql;

import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ResultCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * 通用保存.
 * 上行发送表名和dbmodel,本服务保存到数据库中
 * @author Administrator
 *
 */
public class Generalsave_dbprocess extends RequestProcessorAdapter {
	Category logger = Category.getInstance(Generalsave_dbprocess.class);

	protected String svrcommand = "generalsave";

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
		
		ParamCommand paramcmd=(ParamCommand) req.commandAt(1);
		String tablename=paramcmd.getValue("tablename");
		
		DataCommand datacmd=(DataCommand) req.commandAt(2);
		DBTableModel dbmodel=datacmd.getDbmodel();
		
		Connection con = null;
		ResultCommand resultcmd = null;
		try {
			con = getConnection();
			resultcmd = doSave(con, userinfo, dbmodel, true,tablename);
			con.commit();
			resp.addCommand(new StringCommand("+OK"));
			resp.addCommand(resultcmd);
		} catch (Exception e) {
			con.rollback();
			logger.error("save", e);
			resp.addCommand(new StringCommand("-ERROR保存失败:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param con 连接
	 * @param userinfo 用户信息
	 * @param dbmodel 数据源
	 * @param commit true：每一条记录保存成功就提交；false不提交
	 * @return 结果
	 * @throws Exception
	 */
	public ResultCommand doSave(Connection con, Userruninfo userinfo,
			DBTableModel dbmodel, boolean commit,String tablename) throws Exception {
		Vector<DBColumnDisplayInfo> coldisplayinfos = dbmodel.getDisplaycolumninfos();
		ResultCommand resultcmd = DBModel2Jdbc.save2DB(con, userinfo,
				tablename, tablename, coldisplayinfos, dbmodel,
				null, commit);
		if (userinfo.isDevelop()) {
/*			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
			} catch (UnsupportedEncodingException e) {
				logger.error("error", e);
			}
			selfCheck(con,userinfo, resultcmd, out);
			out.flush();
			String checks = new String(bout.toByteArray(), "gbk");
			if (checks.length() > 0) {
				throw new Exception(this.getClass().getName() + "自检失败:"
						+ checks);
			}
*/			
		}

		return resultcmd;

	}

}
