package com.inca.npserver.servermanager;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.dbcp.DBConnectPoolFactory;

/**
 * ����oracle system�����룬����$APPDIR/dbcp/system.properties��
 * @author Administrator
 *
 */
public class SetsystempasswordProcessor extends RequestProcessorAdapter{
	static String COMMAND="npadmin:setsystempassword";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand)req.commandAt(1);
		String systempassword=pcmd.getValue("systempassword");
		DBConnectPoolFactory.setSystempassword(systempassword);
		DBConnectPoolFactory.getInstance().createSystempool();
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
	
}
