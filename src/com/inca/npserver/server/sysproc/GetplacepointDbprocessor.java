package com.inca.npserver.server.sysproc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;

/**
 * ���ڿͻ���ȡ�õ�ǰ��Ա��placepointid
 * 
 * @author Administrator
 * 
 */
public class GetplacepointDbprocessor extends RequestProcessorAdapter {
	String COMMAND = "npclient:getplacepointid";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}
		Connection con = null;
		String sql = "select placepointid,placepointname,storageid,storagename,sthouseid "
				+ " from GPCS_placepoint_v where placepointid in ( "
				+ " select companyid from pub_company start with companyid = "
				+ " (select deptid from pub_employee where employeeid = ?) "
				+ " connect by prior companyid = parentcompanyid)";
		try {
			con = getConnection();
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(userinfo.getUserid());
			DBTableModel dbmodel = sh.executeSelect(con, 0, 1000);
			DataCommand dcmd = new DataCommand();
			dcmd.setDbmodel(dbmodel);
			logger.debug("get placepointid,userid=" + userinfo.getUserid()
					+ ", placepoint dbmodel rowcount=" + dbmodel.getRowCount());
			resp.addCommand(new StringCommand("+OK"));
			resp.addCommand(dcmd);
		} catch (Exception e) {
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		return 0;
	}

}