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
import com.smart.platform.util.DefaultNPParam;

/**
 * 上行结点实例ID,返回相关决策数据
 * @author user
 *
 */
public class Fetchnodedata_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npserver:查询决策依据数据";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		if(DefaultNPParam.debug==1){
			if(userinfo.getUserid().length()==0){
				userinfo.setUserid("0");
			}
		}
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String wfnodeinstanceid=pcmd.getValue("wfnodeinstanceid");
		
		Connection con=null;
		try {
			con=getConnection();
			DBTableModel dm=WfEngine.getInstance().fetchNodeinstanceData(con, wfnodeinstanceid);
			resp.addCommand(new StringCommand("+OK"));
			DataCommand dcmd=new DataCommand();
			dcmd.setDbmodel(dm);
			resp.addCommand(dcmd);
			
		} catch (Exception e) {
			logger.error("error",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		}finally{
			if(con!=null){
				con.close();
			}
		}

		return 0;
	}
	
}
