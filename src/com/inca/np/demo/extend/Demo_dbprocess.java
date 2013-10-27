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
		if(!cmd0.getString().equals("np:��������DEMO1")){
			return -1;
		}
		
		ParamCommand paramcmd=(ParamCommand)req.commandAt(1);
		String value1=paramcmd.getValue("����1");
		String value2=paramcmd.getValue("����2");
		String value3=paramcmd.getValue("����3");
		
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
			resp.addCommand(new StringCommand("+OK���Ѿ����������"));
		}else{
			resp.addCommand(new StringCommand("-ERROR:��ϵͳʱ��msΪ����."));
		}
		
		
		return 0;
	}

}
