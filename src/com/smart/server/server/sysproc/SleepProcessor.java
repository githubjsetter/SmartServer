package com.smart.server.server.sysproc;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 用于性能测试,服务器修改参数毫秒
 * @author user
 *
 */
public class SleepProcessor extends RequestProcessorAdapter{
	static String COMMAND="nptest:sleep";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!req.getCommand().equals(COMMAND)){
			return -1;
		}
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String sleeptime=pcmd.getValue("sleeptime");
		Thread.sleep(Long.parseLong(sleeptime));
		resp.addCommand(new StringCommand("+OK"));
		return super.process(userinfo, req, resp);
	}
}
