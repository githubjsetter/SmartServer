package com.inca.np.demo.extend;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

public class Demo_dbprocess extends RequestProcessorAdapter{
	Category logger=Category.getInstance(Demo_dbprocess.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd0=(StringCommand)req.commandAt(0);
		if(!cmd0.getString().equals("np:服务请求DEMO1")){
			return -1;
		}
		
		ParamCommand paramcmd=(ParamCommand)req.commandAt(1);
		String value1=paramcmd.getValue("参数1");
		String value2=paramcmd.getValue("参数2");
		String value3=paramcmd.getValue("参数3");
		
		long clienttime=0;
		try{
			clienttime=Long.parseLong(value3);
		}catch(Exception e){
			logger.error("ERROR",e);
		}
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){
			
		}
		
		int result = (int)(clienttime%2);
		if(result==0){
			resp.addCommand(new StringCommand("+OK我已经处理完成了"));
		}else{
			resp.addCommand(new StringCommand("-ERROR:你系统时间ms为奇数."));
		}
		
		
		return 0;
	}

}
