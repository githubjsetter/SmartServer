package com.inca.npserver.server.sysproc;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

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
