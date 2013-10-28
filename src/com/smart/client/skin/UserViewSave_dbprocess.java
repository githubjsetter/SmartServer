package com.smart.client.skin;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

public class UserViewSave_dbprocess extends RequestProcessorAdapter {
	public int process(Userruninfo userruninfo, ClientRequest req,
			ServerResponse serverresponse) throws Exception {
		Connection conn = null;
		if (!"保存自定义界面".equals(req.getCommand()))
			return -1;

		ParamCommand pc = (ParamCommand) req.commandAt(1);
		String lastmodify = pc.getValue("lastmodify");
		String userid = pc.getValue("userid");
		String roleId = pc.getValue("roleId");
		String opid = pc.getValue("opid");
		String schemeName = pc.getValue("schemeName");
		String isdefault = pc.getValue("isdefault");
		String modelcount = pc.getValue("modelcount");
		String userviewid = pc.getValue("userviewid");

		int count = Integer.valueOf(modelcount).intValue();
		String className = "";
		String expr = "";
		DataCommand dc;
		DBTableModel db;
		UpdateHelper uh;
		InsertHelper insert;
		PreparedStatement psexpr = null;
		PreparedStatement psedtl = null;
		try {
			conn = getConnection();

			if (isdefault.equals("1")) {
				// 同一个功能，同一个用户，只能有一个默认方案
				uh = new UpdateHelper(
						"update Np_user_view set isdefault=0 where opid=? and userid=? ");
				uh.bindParam(opid);
				uh.bindParam(userid);
				uh.executeUpdate(conn);
			}

			if (null == userviewid || "".equals(userviewid)) {

				SelectHelper sh = new SelectHelper(
						"select * from Np_user_view where opid=? and userid=? and schemeName=?");
				sh.bindParam(opid);
				sh.bindParam(userid);
				sh.bindParam(schemeName);
				DBTableModel dm = sh.executeSelect(conn, 0, 1);

				if (dm != null && dm.getRowCount() > 0) {
					userviewid = dm.getItemValue(0, "userviewid");
					uh = new UpdateHelper(
							" update Np_user_view set lastmodify=?,schemeName=?,isdefault=? where userviewid=?");
					uh.bindParam(lastmodify);
					uh.bindParam(schemeName);
					uh.bindParam(isdefault);
					uh.bindParam(userviewid);

					uh.executeUpdate(conn);

				} else {
					userviewid = DBModel2Jdbc.getSeqvalue(conn,
							"Np_user_view_seq");
					insert = new InsertHelper("Np_user_view");

					insert.bindParam("lastmodify", lastmodify);
					insert.bindParam("userid", userid);
					insert.bindParam("roleId", roleId);
					insert.bindParam("opid", opid);
					insert.bindParam("schemeName", schemeName);
					insert.bindParam("isdefault", isdefault);
					insert.bindParam("userviewid", userviewid);
					insert.executeInsert(conn);
				}

			} else {

				SelectHelper sh = new SelectHelper(
						"select * from Np_user_view where userviewid=? ");
				sh.bindParam(userviewid);
				DBTableModel dm = sh.executeSelect(conn, 0, 1);

				if (dm != null && dm.getRowCount() > 0) {

					uh = new UpdateHelper(
							" update Np_user_view set lastmodify=?,schemeName=?,isdefault=? where userviewid=?");
					uh.bindParam(lastmodify);
					uh.bindParam(schemeName);
					uh.bindParam(isdefault);
					uh.bindParam(userviewid);

					uh.executeUpdate(conn);
				} else {
					userviewid = DBModel2Jdbc.getSeqvalue(conn,
							"Np_user_view_seq");
					insert = new InsertHelper("Np_user_view");

					insert.bindParam("lastmodify", lastmodify);
					insert.bindParam("userid", userid);
					insert.bindParam("roleId", roleId);
					insert.bindParam("opid", opid);
					insert.bindParam("schemeName", schemeName);
					insert.bindParam("isdefault", isdefault);
					insert.bindParam("userviewid", userviewid);
					insert.executeInsert(conn);
				}
			}

			// 把明细删除掉重新插入
			uh = new UpdateHelper(
					"delete from np_user_view_dtl where  userviewid =?");
			uh.bindParam(userviewid);
			uh.executeUpdate(conn);

			// 把排序表达式删除掉重新插入
			uh = new UpdateHelper(
					"delete from np_user_view_expr where  userviewid =?");
			uh.bindParam(userviewid);
			uh.executeUpdate(conn);

			psexpr = conn
					.prepareStatement("insert into np_user_view_expr(classname,userviewid,orderexpr,userviewexprid) values(?,?,?,Np_user_view_expr_seq.nextval)");
			psedtl = conn
					.prepareStatement("insert into np_user_view_dtl(classname,userviewid,colname,colwidth,orders,userviewdtlid) values(?,?,?,?,?,Np_user_view_dtl_seq.nextval)");

			for (int i = 0; i < count; i++) {
				className = pc.getValue("classname," + i);
				expr = pc.getValue("expr," + i);
				dc = (DataCommand) req.commandAt(i + 2);
				db = dc.getDbmodel();

				if (!"".equals(expr)) {
					psexpr.setObject(1, className);
					psexpr.setObject(2, userviewid);
					psexpr.setObject(3, expr);
					psexpr.addBatch();
				}

				for (int j = 0; j < db.getRowCount(); j++) {

					psedtl.setObject(1, className);
					psedtl.setObject(2, userviewid);
					psedtl.setObject(3, db.getItemValue(j, "colname"));
					psedtl.setObject(4, db.getItemValue(j, "colwidth"));
					psedtl.setObject(5, db.getItemValue(j, "orders"));
					psedtl.executeUpdate();
					// psedtl.addBatch();
				}
			}

			psexpr.executeBatch();
			psexpr.close();
			psexpr = null;
			psedtl.executeBatch();
			psedtl.close();
			psedtl = null;

			conn.commit();

			serverresponse.addCommand(new StringCommand("+OK"));
			serverresponse.addCommand(new StringCommand(userviewid));
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
			if (psexpr != null) {
				psexpr.close();
				psexpr = null;
			}
			if (psedtl != null) {
				psedtl.close();
				psedtl = null;
			}

		}

		return 0;
	}
}
