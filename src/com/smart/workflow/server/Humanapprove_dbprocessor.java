package com.smart.workflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * �������˹��������
 * 
 * @author user
 * 
 */
public class Humanapprove_dbprocessor extends RequestProcessorAdapter {
	static String COMMAND = "npserver:����������";
	Category logger = Category.getInstance(Humanapprove_dbprocessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		// ���д������Ľ��ʵ��ID
		if (!req.getCommand().equals(COMMAND)) {
			return -1;
		}

		Connection con = null;
		try {
			con = getConnection();
			DataCommand dcmd = (DataCommand) req.commandAt(1);
			DBTableModel dm = dcmd.getDbmodel();
			for (int r = 0; r < dm.getRowCount(); r++) {
				String wfnodeinstanceid = dm
						.getItemValue(r, "wfnodeinstanceid");
				Wfnodeinstance nodeinst=Wfnodeinstance.loadFromDB(con, wfnodeinstanceid);
				String nodename=nodeinst.getNodedefine().getNodename();
				WfEngine wfengine = WfEngine.getInstance();
				String approveflag = dm.getItemValue(r, "approveflag");
				String approvemsg = dm.getItemValue(r, "approvemsg");
				try {
					wfengine.setApproveResult(con, wfnodeinstanceid,nodename, userinfo
							.getUserid(), userinfo.getUsername(), approveflag
							.equals("1"), approvemsg);

					con.commit();
				} catch (Exception e) {
					con.rollback();
					logger.error("error", e);
					dm.setItemValue(r, "treateresult", "-ERROR:"
							+ e.getMessage());
					continue;
				}

				dm.setItemValue(r, "treateresult", "+OK����ɹ�");
				con.commit();
			}
			resp.addCommand(new StringCommand("+OK"));
			DataCommand respdcmd = new DataCommand();
			resp.addCommand(respdcmd);
			respdcmd.setDbmodel(dm);
		} catch (Exception e) {
			logger.error("error", e);
			if (con != null)
				con.rollback();
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}

		}

		return 0;
	}

}
