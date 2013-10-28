package com.smart.platform.presstest;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.util.MD5Helper;
import com.smart.platform.util.SendHelper;

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
