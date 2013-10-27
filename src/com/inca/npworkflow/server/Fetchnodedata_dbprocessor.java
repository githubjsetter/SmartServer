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
import com.inca.np.util.DefaultNPParam;

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
