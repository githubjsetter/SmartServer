package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DeleteHelper;
import com.smart.platform.util.InsertHelper;

public class Savewfap_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npserver:保存工作流授权属性"; 
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand)req.commandAt(1);
		String wfid=pcmd.getValue("wfid");
		
		DataCommand dcmd=(DataCommand)req.commandAt(2);
		Connection con = null;
		try {
			con = getConnection();
			String sql="delete np_wf_role_ap where wfid=?";
			DeleteHelper dh=new DeleteHelper(sql);
			dh.bindParam(wfid);
			dh.executeDelete(con);

			DBTableModel dm=dcmd.getDbmodel();
			for(int r=0;r<dm.getRowCount();r++){
				String roleid=dm.getItemValue(r, "roleid");
				String apname=dm.getItemValue(r, "apname");
				String apvalue=dm.getItemValue(r, "apvalue");
				if(apvalue.length()==0)continue;
				
				InsertHelper ih=new InsertHelper("np_wf_role_ap");
				ih.bindSequence("wfroleid","np_wf_role_ap_seq");
				ih.bindParam("wfid",wfid);
				ih.bindParam("roleid",roleid);
				ih.bindParam("apname",apname);
				ih.bindParam("apvalue",apvalue);
				ih.executeInsert(con);
			}
			con.commit();
			resp.addCommand(new StringCommand("+OK"));
			return 0;
			
		} catch (Exception e) {
			con.rollback();
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}
		
		return 0;
	}

}
