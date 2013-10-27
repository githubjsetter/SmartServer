package com.inca.np.anyprint.impl;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.server.RequestProcessorAdapter;

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
