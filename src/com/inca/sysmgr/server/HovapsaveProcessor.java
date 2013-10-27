package com.inca.sysmgr.server;

import java.sql.Connection;
import java.sql.PreparedStatement;

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

public class HovapsaveProcessor extends RequestProcessorAdapter {
	String COMMAND = "np:保存HOV授权属性";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!(req.commandAt(0) instanceof StringCommand))
			return -1;
		StringCommand cmd = (StringCommand) req.commandAt(0);
		if (!cmd.getString().equals(COMMAND))
			return -1;

		ParamCommand paramcmd = (ParamCommand) req.commandAt(1);
		String hovid = paramcmd.getValue("hovid");
		String roleid = paramcmd.getValue("roleid");

		DataCommand datacommand = (DataCommand) req.commandAt(2);
		DBTableModel apmodel = datacommand.getDbmodel();

		Connection con = null;
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;

		try {
			con=getConnection();
			// 删除
			String sql = "delete np_hov_ap where roleid=? and hovid=?";
			c2 = con.prepareStatement(sql);
			c2.setString(1, roleid);
			c2.setString(2, hovid);
			c2.executeUpdate();
			
			for(int r=0;r<apmodel.getRowCount();r++){
				apmodel.setdbStatus(r,RecordTrunk.DBSTATUS_NEW);
			}

			// 插入表np_op_ap
			DBModel2Jdbc.save2DB(con, userinfo, "np_hov_ap", "np_hov_ap", apmodel
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
