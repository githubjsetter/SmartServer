package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.workflow.common.Wfinstance;

public class Getcallopinfo_dbprocessor extends RequestProcessorAdapter {
	static String COMMAND = "npserver:工作流查询调用功能";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!req.getCommand().equals(COMMAND)) {
			return RequestProcessorAdapter.NOTPROCESS;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String wfnodeinstanceid = pcmd.getValue("wfnodeinstanceid");

		Connection con = null;
		try {
			con = getConnection();
			String sql = "select callopid,callcond from np_wf_define where wfid in(\n"
					+ "select wfid from np_wf_node,np_wf_node_instance\n"
					+ "where np_wf_node.wfnodeid=np_wf_node_instance.wfnodeid and wfnodeinstanceid=?)\n";

			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(wfnodeinstanceid);
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			if (dm.getRowCount() < 1) {
				resp.addCommand(new StringCommand("-ERROR:没有找到结点实例定义"));
				return 0;
			}

			String callopid = dm.getItemValue(0, "callopid");
			String callcond = dm.getItemValue(0, "callcond");

			sql = "select wfinstanceid from np_wf_node_instance where wfnodeinstanceid=?";
			sh = new SelectHelper(sql);
			sh.bindParam(wfnodeinstanceid);
			dm = sh.executeSelect(con, 0, 1);
			String instanceid = dm.getItemValue(0, "wfinstanceid");
			Wfinstance wfinst = Wfinstance.loadFromDB(con, instanceid);

			String callcondresult = "";
			try {
				callcondresult = wfinst.getDataitemmgr().fillParam(con,
						wfinst.getWfdefine(), wfinst.getPkvalue(), callcond);
			} catch (Exception e) {
				String s=e.getMessage();
				int p=s.indexOf("错误的表达式:");
				if(p>=0){
					p+="错误的表达式:".length();
				}
				callcondresult=s.substring(p);
			}
			
			sql="select * from np_op where opid=?";
			sh=new SelectHelper(sql);
			sh.bindParam(callopid);
			dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()<1){
				resp.addCommand(new StringCommand("-ERROR:没有找到功能ID="+callopid));
				return 0;
			}
			String classname=dm.getItemValue(0, "classname");
			String opname=dm.getItemValue(0, "opname");
			String groupname=dm.getItemValue(0, "groupname");
			String prodname=dm.getItemValue(0, "prodname");
			String modulename=dm.getItemValue(0, "modulename");
			
			resp.addCommand(new StringCommand("+OK"));
			ParamCommand resppcmd=new ParamCommand();
			resppcmd.addParam("callopid", callopid);
			resppcmd.addParam("classname", classname);
			resppcmd.addParam("prodname", prodname);
			resppcmd.addParam("modulename", modulename);
			resppcmd.addParam("opname", opname);
			resppcmd.addParam("groupname", groupname);
			resppcmd.addParam("callcond", callcondresult);
			resp.addCommand(resppcmd);
			return 0;

		} catch (Exception e) {
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return super.process(userinfo, req, resp);
	}

}
