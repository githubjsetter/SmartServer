package com.smart.adminclient.auth;

import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.util.DefaultNPParam;
import com.smart.server.servermanager.AdminRequestDispatcher;

public class AdminSendHelper {
    public static ServerResponse sendRequest(ClientRequest req) throws Exception{
        String url = DefaultNPParam.defaultappsvrurl;
        ServerResponse svrresp = null;
        if (DefaultNPParam.debug == 1) {
            svrresp = AdminRequestDispatcher.getInstance().process(req);
        } else {
            RemoteConnector rmtconn = new RemoteConnector();
            svrresp = rmtconn.submitRequest(url, req);
        }

        return svrresp;
    }

}
