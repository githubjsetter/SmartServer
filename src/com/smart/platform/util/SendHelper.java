package com.smart.platform.util;

import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.server.server.Server;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-13
 * Time: 17:57:30
 * To change this template use File | Settings | File Templates.
 */
public class SendHelper {
    public static ServerResponse sendRequest(ClientRequest req) throws Exception{
        String url = DefaultNPParam.defaultappsvrurl;
        ServerResponse svrresp = null;
        if (DefaultNPParam.debug == 1 || DefaultNPParam.runonserver) {
            svrresp = Server.getInstance().process(req);
        } else {
            RemoteConnector rmtconn = new RemoteConnector();
            svrresp = rmtconn.submitRequest(url, req);
        }

        return svrresp;
    }
    
    public static ServerResponse sendRequestWithThread(ClientRequest req,CDefaultProgress prog) throws Exception{
    	SubmitRequestThread reqt=new SubmitRequestThread(req,prog);
    	reqt.setDaemon(true);
    	reqt.start();
    	prog.show();
    	if(reqt.isOk()){
    		return reqt.getSvrresp();
    	}else{
    		throw reqt.getError();
    	}
    }
    
    
}
