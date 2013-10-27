package com.inca.npserver.server.sysproc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.inca.np.auth.UserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessIF;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;

/**
 * 人员登录 上行:opcode 密码 下行:部门 角色
 * 
 * @author Administrator
 * 
 */
public class LoginProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:login";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse svrresp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand cmd2 = (ParamCommand) req.commandAt(1);
		String userid = cmd2.getValue("userid");
		String password = cmd2.getValue("password");
		String mac = cmd2.getValue("mac");
		int p = mac.indexOf("(");
		if (p > 0) {
			mac = mac.substring(0, p);
		}
		// 登录
		Userruninfo userruninfo = new Userruninfo();
		Connection con = null;
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;
		PreparedStatement c3 = null;
		PreparedStatement c4 = null;
		String sql = "select employeeid,employeename,deptid,webpass from pub_employee where employeeid=?"
				+ " and nvl(usestatus,0)<>0";
		try {
			con = getConnection();
			c1 = con.prepareStatement(sql);
			c1.setString(1, userid);
			ResultSet rs = c1.executeQuery();
			if (!rs.next()) {
				loginFailure(svrresp, "-ERROR:没有这个用户ID");
				return RequestProcessIF.PROCESSED;
			}
			String webpass = rs.getString("webpass");
			if (webpass != null && webpass.length() > 0
					&& !webpass.equals(password)) {
				// 检查密码失败
				loginFailure(svrresp, "-ERROR:密码错误");
				return RequestProcessIF.PROCESSED;
			}
			// 是外部人员吗?
			boolean extern = isExtern(con, userid);

			// 登录成功
			if (!extern && !userid.equals("0")) {
				if (!checkMac(mac)) {
					logger.error("没有找到mac=" + mac + "的授权");
					loginFailure(svrresp, "-ERROR:你的机器没有授权,请先使用入网申请");
					return 0;
				} else {
					// 记录mac更新
					sql = "update np_mac set lastdate=sysdate,lastip=?,lastemployeeid=?,lastemployeename=?"
							+ " where mac=?";
					UpdateHelper uh = new UpdateHelper(sql);
					uh.bindParam(req.getRemoteip());
					uh.bindParam(userid);
					uh.bindParam(rs.getString("employeename"));
					uh.bindParam(mac);

					try {
						uh.executeUpdate(con);
						con.commit();
					} catch (Exception e) {
						con.rollback();
						logger.error("error", e);
					}
				}
			}

			userruninfo.setUserid(userid);
			userruninfo.setUsername(rs.getString("employeename"));
			userruninfo.setLogindatetime(System.currentTimeMillis());
			userruninfo.setDeptid(rs.getString("deptid"));
			userruninfo.setExternal(extern);
			// userruninfo.setDeptname(rs.getString("deptname"));

			if (DefaultNPParam.depttable_use_pub_dept) {
				sql = "select deptid,deptname from pub_dept where deptid=?";
			} else {
				sql = "select companyid deptid,companyname deptname from pub_company where companyid=?";
			}
			c3 = con.prepareStatement(sql);
			c3.setString(1, rs.getString("deptid"));
			ResultSet rsc = c3.executeQuery();
			DBTableModel deptmodel = DBModel2Jdbc.createFromRS(rsc);
			DataCommand deptdata = new DataCommand();
			deptdata.setDbmodel(deptmodel);

			// 设置用户
			String authstring = UserManager.genAuthstring(userruninfo);
			userruninfo.setAuthstring(authstring);

			ParamCommand paramcmd = new ParamCommand();
			paramcmd.addParam("userid", userruninfo.getUserid());
			paramcmd.addParam("username", userruninfo.getUsername());
			paramcmd.addParam("deptid", userruninfo.getDeptid());
			paramcmd.addParam("deptname", userruninfo.getDeptname());
			paramcmd.addParam("authstring", authstring);
			paramcmd.addParam("extern", extern ? "true" : "false");

			// 查询一下可用的角色
			sql = "select roleid,rolename from np_role ";
			if (!userruninfo.getUserid().equals("0")) {
				sql += " where roleid in("
						+ " select roleid from np_employee_role where employeeid=?) ";
			} else {
				sql += " where (roleid=0 or roleid in("
						+ " select roleid from np_employee_role where employeeid=?) )";
			}
			sql += " order by rolename ";

			c4 = con.prepareStatement(sql);
			int col = 1;
			c4.setString(col++, userruninfo.getUserid());
			rs = c4.executeQuery();
			DBTableModel rolemodel = DBModel2Jdbc.createFromRS(rs);
			DataCommand roledata = new DataCommand();
			roledata.setDbmodel(rolemodel);

			svrresp.addCommand(new StringCommand("+OK：登录成功"));
			svrresp.addCommand(paramcmd);
			svrresp.addCommand(deptdata);
			svrresp.addCommand(roledata);

			UserManager.putLoginok(authstring, userruninfo);
			return RequestProcessIF.PROCESSED;

		} catch (Exception e) {
			logger.error("login error", e);
			loginFailure(svrresp, "-ERROR:" + e.getMessage());
			return RequestProcessIF.PROCESSED;
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {

				}
			}
			if (c2 != null) {
				try {
					c2.close();
				} catch (SQLException e) {

				}
			}
			if (c3 != null) {
				try {
					c3.close();
				} catch (SQLException e) {

				}
			}
			if (c4 != null) {
				try {
					c4.close();
				} catch (SQLException e) {

				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {

				}
			}
		}
	}

	/**
	 * 是外部人员吗?
	 * 
	 * @param con
	 * @param userid
	 * @return
	 */
	private boolean isExtern(Connection con, String userid) {
		try {
			String sql = "select nvl(selfflag,0) selfflag from pub_employee where employeeid=?";
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(userid);
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			if (dm.getRowCount() == 0)
				return false;
			String selfflag = dm.getItemValue(0, "selfflag");
			return selfflag.equals("1");
		} catch (Exception e) {
			logger.error("error", e);
			return false;
		}
	}

	MacManager macm = null;

	boolean checkMac(String mac) {
		if (macm == null) {
			macm = MacManager.getInst();
		}
		return macm.isHas(mac);
	}

	void loginFailure(ServerResponse svrresp, String errormsg) {
		svrresp.addCommand(new StringCommand(errormsg));
	}
}