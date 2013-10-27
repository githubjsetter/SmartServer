package com.inca.npworkflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.Wfnodeinstance;

/**
 * 工作流人工审批结果
 * 
 * @author user
 * 
 */
public class Humanapprove_dbprocessor extends RequestProcessorAdapter {
	static String COMMAND = "npserver:工作流审批";
	Category logger = Category.getInstance(Humanapprove_dbprocessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		// 上行待审批的结点实例ID
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

				dm.setItemValue(r, "treateresult", "+OK处理成功");
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
