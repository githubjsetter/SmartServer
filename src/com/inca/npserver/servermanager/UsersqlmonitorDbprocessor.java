package com.inca.npserver.servermanager;

import java.util.Vector;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.server.UsersqlMonitor;

/**
 * 增加或去掉对某个用户的sql监控
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
