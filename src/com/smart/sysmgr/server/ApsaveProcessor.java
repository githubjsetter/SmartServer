package com.smart.sysmgr.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 保存授权属性 ParamCommand 上传opid DataCommand 上传ApinfoDbmodel
 * 
 * @author Administrator
 * 
 */
public class ApsaveProcessor extends RequestProcessorAdapter {
	String COMMAND = "np:保存授权属性";

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
			// 从np_role_op表查询到roleopid,再由roleopid删除所有np_op_ap表相关记录.
			// 全部重新插入
			con=getConnection();
			String sql = "select roleopid from np_role_op where roleid=? and opid=?";
			c1 = con.prepareStatement(sql);
			c1.setString(1, roleid);
			c1.setString(2, opid);
			ResultSet rs = c1.executeQuery();
			if (!rs.next()) {
				resp.addCommand(new StringCommand("内部错误:找不到roleid=" + roleid
						+ " and opid=" + opid));
				return 0;
			}
			String roleopid = rs.getString("roleopid");
			// 删除
			sql = "delete np_op_ap where roleopid=?";
			c2 = con.prepareStatement(sql);
			c2.setString(1, roleopid);
			c2.executeUpdate();
			
			for(int r=0;r<apmodel.getRowCount();r++){
				apmodel.setItemValue(r,"roleopid",roleopid);
				apmodel.setdbStatus(r,RecordTrunk.DBSTATUS_NEW);
			}

			// 插入表np_op_ap
			DBModel2Jdbc.save2DB(con, userinfo, "np_op_ap", "np_op_ap", apmodel
					.getDisplaycolumninfos(), apmodel, null, false);
			resp.addCommand(new StringCommand("+OK:保存成功"));
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
