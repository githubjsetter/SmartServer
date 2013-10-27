package com.inca.np.util;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.CDefaultProgress;

public class SubmitRequestThread extends Thread{
	ClientRequest req;
	CDefaultProgress prog;
	ServerResponse svrresp=null;
	Exception error=null;
	boolean ok=false;
	
	public SubmitRequestThread(ClientRequest req, CDefaultProgress prog) {
		super();
		this.req = req;
		this.prog = prog;
	}


	public void run(){
		try{
			ok=false;
			prog.appendMessage("正在向服务器发送请求,等待响应...");
			svrresp=SendHelper.sendRequest(req);
			ok=true;
			StringCommand respcmd0=(StringCommand)svrresp.commandAt(0);
			if(respcmd0.getString().startsWith("+OK")){
				prog.messageBox("成功:", "处理成功");
			}else{
				prog.messageBox("失败:", respcmd0.getString());
			}
		}catch(Exception e){
			error=e;
			ok=false;
		}
	}


	public ServerResponse getSvrresp() {
		return svrresp;
	}


	public Exception getError() {
		return error;
	}


	public boolean isOk() {
		return ok;
	}
	
	

}
