package com.inca.npclient.skin;

import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;

public class UserView_select_dbprocess extends RequestProcessorAdapter {
	public int process(Userruninfo userruninfo, ClientRequest req,
			ServerResponse serverresponse) throws Exception {
		Connection conn = null;
		if (!"自定义界面-查询界面方案".equals(req.getCommand()))
			return -1;

		ParamCommand pc = (ParamCommand) req.commandAt(1);
		String userid = pc.getValue("userid");
		
		//为空的话，查询所有的方案，否则查询该名称相应的方案
		String schemename = pc.getValue("schemename");
		
		String opid = pc.getValue("opid");

		try {
			conn = getConnection();

			SelectHelper sh = null;
			int max=100;
			if ("".equals(schemename)) {
				sh = new SelectHelper(
						"  select userviewid, schemename, lastmodify,  isdefault, userid ,opid ,roleid  from np_user_view where userid=? and opid=? ");
				sh.bindParam(userid);
				sh.bindParam(opid);
			} else {
				max=1;
				sh = new SelectHelper(
						"  select userviewid, schemename, lastmodify,  isdefault, userid ,opid ,roleid  from np_user_view where userid=? and opid=? and schemename=? ");
				sh.bindParam(userid);
				sh.bindParam(opid);
				sh.bindParam(schemename);
			}

			DBTableModel db = sh.executeSelect(conn, 0, max);

			serverresponse.addCommand(new StringCommand("+OK"));
			DataCommand dc = new DataCommand();
			dc.setDbmodel(db);
			serverresponse.addCommand(dc);

			conn.commit();

		} catch (Exception e) {
			serverresponse.addCommand(new StringCommand("-ERROR"
					+ e.getMessage()));
		} finally {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}

		return 0;
	}
}
