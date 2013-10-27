package com.inca.npworkflow.server;

import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.DeleteHelper;
import com.inca.np.util.InsertHelper;

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
