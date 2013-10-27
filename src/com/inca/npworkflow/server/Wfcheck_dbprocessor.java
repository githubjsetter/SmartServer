package com.inca.npworkflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * 检查流程定义表达式
 * @author user
 *
 */
public class Wfcheck_dbprocessor extends RequestProcessorAdapter{
	Category logger=Category.getInstance(Wfcheck_dbprocessor.class);
	
	/**
	 * 检查是否正确
	 */
	static String COMMAND="npworkflow:检查流程表达式";
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String wfid=pcmd.getValue("wfid");

		Connection con=null;
		try {
			con=getConnection();
			DBTableModel dm=WfEngine.getInstance().checkWfexpr(con, wfid);
			resp.addCommand(new StringCommand("+OK"));
			DataCommand dcmd=new DataCommand();
			dcmd.setDbmodel(dm);
			resp.addCommand(dcmd);
		} catch (Exception e) {
			logger.error("Error",e);
			con.rollback();
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
			return 0;
		}finally{
			if(con!=null)con.close();
		}
		
		
		return 0;
	}

}
