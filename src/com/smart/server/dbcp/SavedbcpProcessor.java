package com.smart.server.dbcp;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.server.sysproc.MacManager;

public class SavedbcpProcessor  extends RequestProcessorAdapter{
	static String COMMAND="npclient:savedbcp";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		DataCommand dcmd=(DataCommand) req.commandAt(1);
		DBTableModel dbmodel=dcmd.getDbmodel();
		
		for(int i=0;i<dbmodel.getRowCount();i++){
			int dbstatus=dbmodel.getdbStatus(i);
			if(dbstatus==RecordTrunk.DBSTATUS_NEW){
				DBConnectPoolFactory.savePropfile(dbmodel, i);
			}else if(dbstatus==RecordTrunk.DBSTATUS_MODIFIED){
				DBConnectPoolFactory.savePropfile(dbmodel, i);
			}else if(dbstatus==RecordTrunk.DBSTATUS_DELETE){
				DBConnectPoolFactory.removeDbcp(dbmodel.getItemValue(i, "name"));
			}
		}
		DBConnectPoolFactory.getInstance().reload();
		MacManager.getInst().reload();
		resp.addCommand(new StringCommand("+OK"));
		DataCommand respcmd=new DataCommand();
		resp.addCommand(respcmd);
		return 0;
	}
	
}