package com.inca.np.demo.extend;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.CDefaultProgress;
import com.inca.np.gui.control.CFrame;
import com.inca.np.util.SendHelper;

public class DemoClient {
	Category logger=Category.getInstance(DemoClient.class);
	public void demo1(CFrame frame){
		ClientRequest req = new ClientRequest();
		req.addCommand(new StringCommand("np:服务请求DEMO1"));
		
		ParamCommand cmd2 = new ParamCommand();
		req.addCommand(cmd2);
		cmd2.addParam("参数1","值1");	
		cmd2.addParam("参数2","值2");	
		cmd2.addParam("参数3",String.valueOf(System.currentTimeMillis()));
		
		CDefaultProgress prog=new CDefaultProgress(frame);
		prog.appendMessage("正在发向服务器发送请求");
		//发送请求
		ServerResponse svrresp=null;
		try{
			svrresp = SendHelper.sendRequestWithThread(req,prog);
		}catch(Exception e){
			logger.error("发送请求失败",e);
			return;
		}
		
		StringCommand respcmd0 = (StringCommand) svrresp.commandAt(0);
		if(respcmd0.getString().startsWith("+OK")){
			//显示处理成功
			logger.info("处理成功");
		}else{
			//显示失败
			logger.error("处理失败,原因:"+respcmd0.getString());
		}
		
		
	}
}
