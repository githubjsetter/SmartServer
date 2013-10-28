package com.smart.server.servermanager;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

public class AdminrepasswdProcessor extends RequestProcessorAdapter{
	static String COMMAND="npadmin:repassword";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		ParamCommand pcmd=(ParamCommand)req.commandAt(1);
		String oldpassword=pcmd.getValue("oldpassword");
		String newpassword=pcmd.getValue("newpassword");
		
		String adminpassword=AdminpasswordManager.getAdminpassword();
		if(adminpassword.length()>0){
			if(!adminpassword.equals(oldpassword)){
				resp.addCommand(new StringCommand("-ERROR:原密码错误"));
				return 0;
			}
		}
		AdminpasswordManager.writePassword(newpassword);
		resp.addCommand(new StringCommand("+OK:密码修改成功"));
		return 0;
	}

	
}
