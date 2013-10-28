package com.smart.server.server.sysproc;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

public class SetentryProcessor  extends RequestProcessorAdapter{
	static String COMMAND="npclient:setentryid";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand()))return -1;
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String entryid=pcmd.getValue("entryid");
		logger.info("set user="+userinfo.getUserid()+"'s entryid="+entryid);
		userinfo.setEntryid(entryid);
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
	
	
}
