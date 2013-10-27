package com.inca.sysmgr.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * ������Ȩ���� ParamCommand �ϴ�opid DataCommand �ϴ�ApinfoDbmodel
 * 
 * @author Administrator
 * 
 */
public class ApsaveProcessor extends RequestProcessorAdapter {
	String COMMAND = "np:������Ȩ����";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!(req.commandAt(0) instanceof StringCommand))
			return -1;
		StringCommand cmd = (StringCommand) req.commandAt(0);
		if (!cmd.getString().equals(COMMAND))
			return -1;

		ParamCommand paramcmd = (ParamCommand) req.commandAt(1);
		String opid = paramcmd.getValue("opid");
		String roleid = paramcmd.getValue("roleid");

		DataCommand datacommand = (DataCommand) req.commandAt(2);
		DBTableModel apmodel = datacommand.getDbmodel();

		Connection con = null;
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;

		try {
			// ��np_role_op���ѯ��roleopid,����roleopidɾ������np_op_ap����ؼ�¼.
			// ȫ�����²���
			con=getConnection();
			String sql = "select roleopid from np_role_op where roleid=? and opid=?";
			c1 = con.prepareStatement(sql);
			c1.setString(1, roleid);
			c1.setString(2, opid);
			ResultSet rs = c1.executeQuery();
			if (!rs.next()) {
				resp.addCommand(new StringCommand("�ڲ�����:�Ҳ���roleid=" + roleid
						+ " and opid=" + opid));
				return 0;
			}
			String roleopid = rs.getString("roleopid");
			// ɾ��
			sql = "delete np_op_ap where roleopid=?";
			c2 = con.prepareStatement(sql);
			c2.setString(1, roleopid);
			c2.executeUpdate();
			
			for(int r=0;r<apmodel.getRowCount();r++){
				apmodel.setItemValue(r,"roleopid",roleopid);
				apmodel.setdbStatus(r,RecordTrunk.DBSTATUS_NEW);
			}

			// �����np_op_ap
			DBModel2Jdbc.save2DB(con, userinfo, "np_op_ap", "np_op_ap", apmodel
					.getDisplaycolumninfos(), apmodel, null, false);
			resp.addCommand(new StringCommand("+OK:����ɹ�"));
			con.commit();
		} catch (Exception e) {
			con.rollback();
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (c1 != null)
				c1.close();
			if (c2 != null)
				c2.close();
			if (con != null)
				con.close();
		}
		return 0;

	}

	Category logger = Category.getInstance(ApqueryProcessor.class);
}
