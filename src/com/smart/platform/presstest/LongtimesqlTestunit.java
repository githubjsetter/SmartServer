package com.smart.platform.presstest;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;

public class LongtimesqlTestunit extends Presstestunit{

	public LongtimesqlTestunit() {
		super();
		ClientRequest req=new ClientRequest("npclient:testlongtimesql");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		
		//����sql��ʱ5��
		pcmd.addParam("sleeptime","500");
		
		
		reqs.add(req);
		
	}

}
