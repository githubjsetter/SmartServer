package com.smart.workflow.server;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * @deprecated
 * ������������
 * @author user
 *
 */
public class RestartWfinstance_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="Wfinst_mde.������������ʵ��";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		// TODO Auto-generated method stub
		return super.process(userinfo, req, resp);
	}
}
