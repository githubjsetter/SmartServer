package com.smart.workflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * ������̶�����ʽ
 * @author user
 *
 */
public class Wfcheck_dbprocessor extends RequestProcessorAdapter{
	Category logger=Category.getInstance(Wfcheck_dbprocessor.class);
	
	/**
	 * ����Ƿ���ȷ
	 */
	static String COMMAND="npworkflow:������̱��ʽ";
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
