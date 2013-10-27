package com.inca.npserver.server.sysproc;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

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
