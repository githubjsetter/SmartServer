package com.smart.server.server.sysproc;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 得取connection, 根据ParamCommand参数sleeptime延时，再释放conneciton
 * @author Administrator
 *
 */
public class TestlongtimesqlProcessor extends RequestProcessorAdapter{
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!req.getCommand().equals(COMMAND)){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		long sleeptime=0;
		try {
			sleeptime=Long.parseLong(pcmd.getValue("sleeptime"));
		} catch (Exception e) {
			
		}
		Connection con=null;
		try {
			con=getConnection();
			Thread.sleep(sleeptime);
		} finally{
			if(con!=null){
				con.close();
			}
		}
		
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}

	static String COMMAND="npclient:testlongtimesql";
}
