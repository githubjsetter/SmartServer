package com.smart.platform.demo.extend;

import org.apache.log4j.Category;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.util.SendHelper;

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
