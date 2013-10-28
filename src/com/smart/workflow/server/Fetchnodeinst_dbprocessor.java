package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DefaultNPParam;

/**
 * ≤È—Ø¥˝…Û µ¿˝
 * @author user
 *
 */
public class Fetchnodeinst_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npserver:≤È—Ø¥˝…Û≈˙";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		if(DefaultNPParam.debug==1){
			if(userinfo.getUserid().length()==0){
				userinfo.setUserid("0");
				userinfo.setRoleid("0");
			}
		}
		
		Connection con=null;
		try {
			con=getConnection();
			logger.debug("!!!!!!!!!!!userinfo deptid="+userinfo.getDeptid());
			DBTableModel dm=WfEngine.getInstance().fetchNodeinstanceByemployee(con, userinfo);
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
