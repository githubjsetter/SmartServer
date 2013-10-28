package com.smart.platform.demo.communicate;

import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.SqlCommand;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;
import com.smart.server.server.Server;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-29 Time: 9:39:38
 * To change this template use File | Settings | File Templates.
 */
public class RemotesqlHelper {
	private RemoteConnector conn;

	public DBTableModel doSelect(String sql, int startrow, int maxrowcount)
	throws Exception {
		return doSelect("select", sql, startrow, maxrowcount);
	}

	public DBTableModel doSelect(String svrcmd, String sql, int startrow,
			int maxrowcount) throws Exception {
		if (DefaultNPParam.runonserver) {
			throw new Exception("在服务器端不支持RemotesqlHelper");
			/*
			Connection con = null;
			try {
				con = DBConnectPoolFactory.getInstance().getConnection();
				con.setAutoCommit(false);
				ServerContext svrcontext = ServerContext.getServercontext();
				JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext,
						con);
				SelectHelper sh=new SelectHelper(sql);
				DBTableModel result=sh.executeSelect(conwrap, startrow, maxrowcount);
				conwrap.close();
				return result;
			} finally {
				if (con != null) {
					con.close();
				}
			}
			 */
		}

		StringCommand cmd1 = new StringCommand(svrcmd);
		ClientRequest req = new ClientRequest();
		req.addCommand(cmd1);

		SqlCommand cmd2 = new SqlCommand(sql);
		cmd2.setStartrow(startrow);
		cmd2.setMaxrowcount(maxrowcount);
		req.addCommand(cmd2);
		if (conn == null) {
			conn = new RemoteConnector();
		}

		ServerResponse svrresp = null;
		if (DefaultNPParam.debug == 1) {
			svrresp = Server.getInstance().process(req);
		} else {
			svrresp = conn.submitRequest(DefaultNPParam.defaultappsvrurl, req);
		}

		if (svrresp.getCommandcount() == 0) {
			throw new Exception("无返回命令");
		} else {
			CommandBase tmp = svrresp.commandAt(0);
			if (!(tmp instanceof StringCommand)) {
				throw new Exception("返回第0个命令是不是StringCommand");
			} else {
				StringCommand cmd = (StringCommand) tmp;
				String svrreturnstr = cmd.getString();
				// System.out.println("服务器返回："+svrreturnstr);

				if (svrreturnstr.startsWith("+")) {
					DataCommand datacmd = (DataCommand) svrresp.commandAt(1);
					DBTableModel dbmodel = datacmd.getDbmodel();
					// System.out.println("返回记录:"+memds.getRowCount()+"条，hasmore="+memds.getHasmore());

					return dbmodel;

				} else {
					throw new Exception(sql + ",服务器返回错误:" + svrreturnstr);
				}
			}
		}
	}

	public int getRetrievesize() {
		if (conn != null) {
			return conn.getRetrievedSize();
		}
		return 0;
	}

	public int getInflatSize() {
		if (conn != null) {
			return conn.getInflatSize();
		}
		return 0;
	}

}
