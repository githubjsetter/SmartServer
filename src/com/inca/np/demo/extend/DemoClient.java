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
		req.addCommand(new StringCommand("np:��������DEMO1"));
		
		ParamCommand cmd2 = new ParamCommand();
		req.addCommand(cmd2);
		cmd2.addParam("����1","ֵ1");	
		cmd2.addParam("����2","ֵ2");	
		cmd2.addParam("����3",String.valueOf(System.currentTimeMillis()));
		
		CDefaultProgress prog=new CDefaultProgress(frame);
		prog.appendMessage("���ڷ����������������");
		//��������
		ServerResponse svrresp=null;
		try{
			svrresp = SendHelper.sendRequestWithThread(req,prog);
		}catch(Exception e){
			logger.error("��������ʧ��",e);
			return;
		}
		
		StringCommand respcmd0 = (StringCommand) svrresp.commandAt(0);
		if(respcmd0.getString().startsWith("+OK")){
			//��ʾ����ɹ�
			logger.info("����ɹ�");
		}else{
			//��ʾʧ��
			logger.error("����ʧ��,ԭ��:"+respcmd0.getString());
		}
		
		
	}
}
