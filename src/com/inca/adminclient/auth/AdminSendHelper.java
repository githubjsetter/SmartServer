package com.inca.adminclient.auth;

import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.util.DefaultNPParam;
import com.inca.npserver.servermanager.AdminRequestDispatcher;

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
