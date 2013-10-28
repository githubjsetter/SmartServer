package com.smart.server.pushplat.server;

import java.util.Vector;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.pushplat.common.Pushinfo;

/**
 * 下载推送定义
 * @author user
 *
 */
public class Downloadpush_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npserver:下载推送";
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		Vector<Pushinfo> infos=PushManager.getAllpushinfo();
		DBTableModel dbmodel=PushManager.toDbmodel(infos);
		
		resp.addCommand(new StringCommand("+OK"));
		DataCommand dcmd=new DataCommand();
		resp.addCommand(dcmd);
		dcmd.setDbmodel(dbmodel);
		
		return 0;
	}
	
}
