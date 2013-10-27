package com.inca.npserver.server.sysproc;

import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.prod.ModuleManager;
import com.inca.npserver.prod.ModuleManager.Jarmd5info;

/**
 * 下载NPSERVER CLIENT启动需要的JAR包和路径SETPATH.CMD
 * @author Administrator
 *
 */
public class DownloadLauncherProcessor  extends RequestProcessorAdapter {
	static String COMMAND = "npclient:downloadlaunchers";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		DBTableModel dbmodel=new DBTableModel(createCols());
		ModuleManager mm = ModuleManager.getInst();
		
		Enumeration<Jarmd5info> en=mm.getLauncherjars().elements();
		while(en.hasMoreElements()){
			Jarmd5info md5info=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r,"jarfilename",md5info.jarfilename);
			dbmodel.setItemValue(r,"md5",md5info.md5);
			logger.info("launcher jar="+md5info.jarfilename);
		}
		resp.addCommand(new StringCommand("+OK"));
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dbmodel);
		resp.addCommand(dcmd);
		
		return 0;
	}
	
	static Vector<DBColumnDisplayInfo>  createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("jarfilename","varchar");
		cols.add(col);
		col=new DBColumnDisplayInfo("md5","varchar");
		cols.add(col);
		return cols;
	}
}