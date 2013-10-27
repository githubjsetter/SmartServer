package com.inca.npworkflow.server;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * @deprecated
 * 重新启动流程
 * @author user
 *
 */
public class RestartWfinstance_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="Wfinst_mde.重新启动流程实例";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		// TODO Auto-generated method stub
		return super.process(userinfo, req, resp);
	}
}
