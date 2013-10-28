package com.smart.workflow.server;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.server.RequestProcessorAdapter;

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
