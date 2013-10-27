package com.inca.npserver.servermanager;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.servermanager.AdminManager;

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