package com.inca.np.presstest;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;

public class LongtimesqlTestunit extends Presstestunit{

	public LongtimesqlTestunit() {
		super();
		ClientRequest req=new ClientRequest("npclient:testlongtimesql");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		
		//ºŸ…Ësql”√ ±5√Î
		pcmd.addParam("sleeptime","500");
		
		
		reqs.add(req);
		
	}

}
