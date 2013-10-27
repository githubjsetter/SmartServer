package com.inca.npserver.server.sysproc;

import java.sql.Connection;
import java.sql.SQLException;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;
import com.inca.npserver.prod.LicenseManager;
import com.inca.npserver.prod.Licenseinfo;

/**
 * 选择了当前的ROLEID
 * 
 * @author Administrator
 * 
 */
public class SetroleProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:setroleid";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse svrresp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String roleid = pcmd.getValue("roleid");
		userinfo.setRoleid(roleid);
		userinfo.setRolename(pcmd.getValue("rolename"));
		userinfo.setDeptid(pcmd.getValue("deptid"));
		userinfo.setDeptname(pcmd.getValue("deptname"));

		Connection con = null;
		String sql = "select * from np_op where opid in (select opid from np_role_op where roleid=? )";
		if (userinfo.getUserid().equals("0")) {
			sql += "  or (np_op.prodname='npserver' and np_op.modulename='系统管理')";
		}
		sql += " order by sortno,opid";

		//logger.debug(sql);

		LicenseManager lm = LicenseManager.getInst();
		try {
			con = this.getConnection();
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(roleid);
			DBTableModel opsdbmodel = sh.executeSelect(con, 0, 10000);
			logger.debug("opsdbmodel rowcount=" + opsdbmodel.getRowCount());
			// 去掉未授权的
			for (int r = 0; r < opsdbmodel.getRowCount(); r++) {
				String prodname = opsdbmodel.getItemValue(r, "prodname");
				String modulename = opsdbmodel.getItemValue(r, "modulename");
				if (!prodname.equals("专项开发")) {
					Licenseinfo linfo = lm.getLicense(prodname, modulename);
					if (linfo == null) {
						logger.debug("remove not license op opname="
								+ opsdbmodel.getItemValue(r, "opname"));
						opsdbmodel.removeRow(r);
						r--;
					}
				}
			}
			svrresp.addCommand(new StringCommand("+OK"));
			DataCommand dcmd = new DataCommand();
			dcmd.setDbmodel(opsdbmodel);
			svrresp.addCommand(dcmd);

		} catch (Exception e) {
			logger.error("ERROR", e);
			svrresp.addCommand(new StringCommand("-ERROR" + e.getMessage()));
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