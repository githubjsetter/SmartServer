package com.inca.npserver.servermanager;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

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
