package com.smart.platform.anyprint.impl;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * @deprecated
 * @author Administrator
 *
 */
public class DstoreDbprocessor extends RequestProcessorAdapter{
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		String upcmd=req.getCommand();
		if(!upcmd.equals("npclient:listprintds") && !upcmd.equals("npclient:downloadprintds")
				&& !upcmd.equals("npclient:downloadprintds")){
			return -1;
		}
		
		if(upcmd.equals("npclient:listprintds")){
			listPrintds(userinfo,req,resp);
		}else if(upcmd.equals("npclient:downloadprintds")){
			downloadprintds(userinfo,req,resp);
		}
		return 0;
	}
	
	void listPrintds(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp){
		
	}

	void downloadprintds(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp){
		
	}
}
