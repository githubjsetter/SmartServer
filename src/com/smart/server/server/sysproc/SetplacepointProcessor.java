package com.smart.server.server.sysproc;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 设置门店id,保管帐id等
 * @author Administrator
 *
 */
public class SetplacepointProcessor extends RequestProcessorAdapter{
	static String COMMAND="npclient:setplacepointid";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand()))return -1;
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		userinfo.setPlacepointid(pcmd.getValue("placepointid"));
		userinfo.setStorageid(pcmd.getValue("storageid"));
		userinfo.setSthouseid(pcmd.getValue("sthouseid"));
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
	
	
}
