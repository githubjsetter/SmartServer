package com.smart.server.server.sysproc;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.log.NpLogManager;

/**
 * 取log4j当前logger的level和logger文件情况
 * @author Administrator
 *
 */
public class GetloggerinfoProcessor extends RequestProcessorAdapter{
	static String COMMAND="npclient:getloggerinfo";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand()))return -1;
		
		NpLogManager nplogm=NpLogManager.getInstance();
		Logger rootlogger=nplogm.getApprootlogger();
		//取级别
		Level level=rootlogger.getLevel();
		int ilevel=0;
		if(level.equals(Level.DEBUG)){
			ilevel=0;
		}else if(level.equals(Level.WARN)){
			ilevel=1;
		}else if(level.equals(Level.INFO)){
			ilevel=2;
		}else if(level.equals(Level.ERROR)){
			ilevel=3;
		}else if(level.equals(Level.FATAL)){
			ilevel=4;
		}
		
		//查看日志文件
		File logdir=new File(CurrentappHelper.guessAppdir(),"logs");
		if(!logdir.exists())logdir.mkdirs();
		DBTableModel dm=createSwapdm();
		File fs[]=logdir.listFiles();
		for(int i=0;i<fs.length;i++){
			if(fs[i].isDirectory())continue;
			String fn=fs[i].getName();
			if(!fn.startsWith("npserver"))continue;
			int row=dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(row, "filename", fn);
			dm.setItemValue(row, "filelength", String.valueOf(fs[i].length()));
		}
		
		resp.addCommand(new StringCommand("+OK"));
		ParamCommand pcmd=new ParamCommand();
		resp.addCommand(pcmd);
		pcmd.addParam("level",String.valueOf(ilevel));
		
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dm);
		resp.addCommand(dcmd);
		
		return 0;
	}
	
	DBTableModel createSwapdm(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		col=new DBColumnDisplayInfo("filename","varchar");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("filelength","number");
		cols.add(col);
		
		return new DBTableModel(cols);
	}
	
}
