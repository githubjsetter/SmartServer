package com.smart.server.servermanager;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.servermanager.AdminManager;

public class AdminloginProcessor extends RequestProcessorAdapter {
	Category logger = Category.getInstance(AdminloginProcessor.class);
	static String COMMAND = "npserver:adminlogin";

	/**
	 * �����ļ� ����np:fileupload ParamCommand filename �ļ��� filegroupid ��������������
	 * 
	 * @param req
	 * @return -1 ʧ�� 0 �ɹ� 1 �ɹ�,��ȫ���ϴ����
	 * @throws Exception
	 */

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
		String password = cmd1.getValue("password");
		String adminpassword=AdminpasswordManager.getAdminpassword();
		if(adminpassword.length()>0){
			if(!adminpassword.equals(password)){
				resp.addCommand(new StringCommand("-ERROR:admin�������"));
				return 0;
			}
		}
		// �Ȱ��ɹ�����
		Userruninfo logonuser = AdminManager.authUser(req);
		resp.addCommand(new StringCommand("+OK"));
		
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("userid","admin");
		pcmd.addParam("username","admin");
		pcmd.addParam("authstring",logonuser.getAuthstring());
		resp.addCommand(pcmd);

		return 0;
	}
}