package com.inca.npserver.server.sysproc;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

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
