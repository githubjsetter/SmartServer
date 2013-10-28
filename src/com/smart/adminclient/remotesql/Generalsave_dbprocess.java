package com.smart.adminclient.remotesql;

import java.sql.Connection;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ResultCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * ͨ�ñ���.
 * ���з��ͱ�����dbmodel,�����񱣴浽���ݿ���
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
			resp.addCommand(new StringCommand("-ERROR����ʧ��:" + e.getMessage()));
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
	 * @param con ����
	 * @param userinfo �û���Ϣ
	 * @param dbmodel ����Դ
	 * @param commit true��ÿһ����¼����ɹ����ύ��false���ύ
	 * @return ���
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
				throw new Exception(this.getClass().getName() + "�Լ�ʧ��:"
						+ checks);
			}
*/			
		}

		return resultcmd;

	}

}
