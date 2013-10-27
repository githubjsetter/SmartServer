package com.inca.npserver.pushplat.server;

import java.util.Vector;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.pushplat.common.Pushinfo;

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
