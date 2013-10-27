package com.inca.npserver.dbcp;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.server.sysproc.MacManager;

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