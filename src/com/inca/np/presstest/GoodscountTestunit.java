package com.inca.np.presstest;

import com.inca.np.auth.Userruninfo;
import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SendHelper;

public class GoodscountTestunit  extends Presstestunit{

	public GoodscountTestunit() {
		super();
	}

	@Override
	public boolean test() throws Exception {
		//µÇÂ¼
		ClientRequest req=new ClientRequest("npclient:login");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("userid", "0");
		paramcmd.addParam("password", MD5Helper.MD5("x"));
		paramcmd.addParam("mac", "00-00-00-00-00-00(192.9.200.1)");
		ServerResponse resp=SendHelper.sendRequest(req);
		if(!resp.getCommand().startsWith("+OK")){
			throw new Exception(resp.getCommand());
		}
		ParamCommand pcmd=(ParamCommand) resp.commandAt(1);
		Userruninfo userruninfo = new Userruninfo();
		userruninfo.setAuthstring(pcmd.getValue("authstring"));
		RemoteConnector.setAuthstring(pcmd.getValue("authstring"));
		
		
		RemotesqlHelper sh=new RemotesqlHelper();
		String sql="select goodsid,goodsname from pub_goods";
		sh.doSelect(sql, 0, 300);
		
		return true;
	}

}
