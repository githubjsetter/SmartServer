package com.smart.server.server.sysproc;

import java.sql.Connection;
import java.sql.SQLException;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;

/**
 * MAC地址请求
 * 
 * @author Administrator
 * 
 */
public class MacrequestProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:macrequest";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse svrresp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String mac = pcmd.getValue("mac");
		int p = mac.indexOf("(");
		if (p > 0) {
			mac = mac.substring(0, p);
		}
		if(mac.length()<12){
			svrresp.addCommand(new StringCommand("-ERROR:mac地址不合法"));
			return 0;
		}

		String memo = pcmd.getValue("memo");
		String fromip = userinfo.getRemoteip();

		Connection con = null;
		try {
			con = this.getConnection();
			SelectHelper sh = new SelectHelper(
					"select * from np_mac where mac=?");
			sh.bindParam(mac);
			DBTableModel macmodel = sh.executeSelect(con, 0, 1);
			if (macmodel.getRowCount() == 1) {
				svrresp
						.addCommand(new StringCommand(
								"+OK:你的网卡地址验证已经通过了,不需要再申请"));
				return 0;
			}

			sh = new SelectHelper("select * from np_mac_req where mac=?");
			sh.bindParam(mac);
			macmodel = sh.executeSelect(con, 0, 1);
			if (macmodel.getRowCount() == 0) {

				InsertHelper ih = new InsertHelper("np_mac_req");
				ih.bindSequence("seqid", "np_mac_seq");
				ih.bindSysdate("credate");
				ih.bindParam("mac", mac);
				ih.bindParam("fromip", fromip);
				ih.bindParam("memo", memo);
				ih.executeInsert(con);
				con.commit();
				svrresp
						.addCommand(new StringCommand("+OK:你的网卡地址验证请求已提交,稍候来查询"));
				return 0;
			} else {
				String approveflag = macmodel.getItemValue(0, "approveflag");
				if (approveflag.equals("0")) {
					svrresp
							.addCommand(new StringCommand("-ERROR:你的网卡地址验证被拒绝了"));
					return 0;
				} else if (approveflag.equals("1")) {
					svrresp.addCommand(new StringCommand(
							"+OK:你的网卡地址验证已经通过了,不需要再申请"));
					return 0;
				} else {
					svrresp.addCommand(new StringCommand(
							"-ERROR:你的网卡地址验证正在审核中,请稍候再试"));
					return 0;
				}
			}

		} catch (Exception e) {
			// 如果修改数据库 发生错误 rollback
			con.rollback();
			logger.error("ERROR", e);
			svrresp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
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