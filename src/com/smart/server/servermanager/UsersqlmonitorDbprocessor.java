package com.smart.server.servermanager;

import java.util.Vector;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.UsersqlMonitor;

/**
 * ���ӻ�ȥ����ĳ���û���sql���
 * @author user
 *
 */
public class UsersqlmonitorDbprocessor  extends RequestProcessorAdapter {

	static String SVRCOMMAND = "npserver:addusersqlmonitor";
	static String SVRCOMMAND1 = "npserver:fetchusersqlmonitor";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!SVRCOMMAND.equals(req.getCommand()) && !SVRCOMMAND1.equals(req.getCommand())) {
			return -1;
		}
		
		if(SVRCOMMAND1.equals(req.getCommand())){
			fetchSqlinfo(userinfo,req,resp);
			return 0;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String action=pcmd.getValue("action");
		String userid=pcmd.getValue("userid");
		
		if(action.equals("add")){
			UsersqlMonitor.getInstance().addUsermonitor(userid);
		}else{
			UsersqlMonitor.getInstance().removeUsermonitor(userid);
		}
		
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}

	void fetchSqlinfo(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) {
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String userid=pcmd.getValue("userid");
		DBTableModel dm=UsersqlMonitor.getInstance().getUsersqlinfos(userid);
		resp.addCommand(new StringCommand("+OK"));
		DataCommand dcmd=new DataCommand();
		resp.addCommand(dcmd);
		dcmd.setDbmodel(dm);
		return;
		
	
	}
}
