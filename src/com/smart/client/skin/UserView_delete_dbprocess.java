package com.smart.client.skin;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

public class UserView_delete_dbprocess extends RequestProcessorAdapter {
	public int process(Userruninfo userruninfo, ClientRequest req,
			ServerResponse serverresponse) throws Exception {
		Connection conn = null;
		if (!"�Զ������-ɾ�����淽��".equals(req.getCommand()))
			return -1;

		ParamCommand pc = (ParamCommand) req.commandAt(1);
		String userid = pc.getValue("userid");

		// Ϊ�յĻ�����ѯ���еķ����������ѯ��������Ӧ�ķ���
		String schemename = pc.getValue("schemename");

		String opid = pc.getValue("opid");
		try {
			conn = getConnection();

			SelectHelper sh = null;

			sh = new SelectHelper(
					"  select userviewid, schemename, lastmodify,  isdefault, userid ,opid ,roleid  from np_user_view where userid=? and opid=? and schemename=? ");
			sh.bindParam(userid);
			sh.bindParam(opid);
			sh.bindParam(schemename);

			DBTableModel db = sh.executeSelect(conn, 0, 1);

			if (db.getRowCount() == 1) {
				String userviewid = db.getItemValue(0, "userviewid");

				UpdateHelper uh = new UpdateHelper(
						"delete from np_user_view_dtl where userviewid=? ");
				uh.bindParam(userviewid);
				uh.executeUpdate(conn);
				
				uh = new UpdateHelper(
						"delete from np_user_view_expr where userviewid=? ");
				uh.bindParam(userviewid);
				uh.executeUpdate(conn);
				
				uh = new UpdateHelper(
						"delete from np_user_view where userviewid=? ");
				uh.bindParam(userviewid);
				uh.executeUpdate(conn);
			}

			serverresponse.addCommand(new StringCommand("+OK"));
			DataCommand dc = new DataCommand();
			dc.setDbmodel(db);
			serverresponse.addCommand(dc);
			conn.commit();

		} catch (Exception e) {
			serverresponse.addCommand(new StringCommand("-ERROR"
					+ e.getMessage()));
			if (conn != null) {
				conn.rollback();
			}
		} finally {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}

		return 0;
	}
}
