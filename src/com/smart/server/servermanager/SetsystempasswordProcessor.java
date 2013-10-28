package com.smart.server.servermanager;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.dbcp.DBConnectPoolFactory;

/**
 * 设置oracle system的密码，存在$APPDIR/dbcp/system.properties中
 * @author Administrator
 *
 */
public class SetsystempasswordProcessor extends RequestProcessorAdapter{
	static String COMMAND="npadmin:setsystempassword";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand)req.commandAt(1);
		String systempassword=pcmd.getValue("systempassword");
		DBConnectPoolFactory.setSystempassword(systempassword);
		DBConnectPoolFactory.getInstance().createSystempool();
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
	
}
