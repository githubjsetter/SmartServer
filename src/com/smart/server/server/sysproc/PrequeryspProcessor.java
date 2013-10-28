package com.smart.server.server.sysproc;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 执行查询前存储过程
 * 
 * @author Administrator
 * 
 */
public class PrequeryspProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:execprequerystoreproc";
	protected Category logger = Category.getInstance(PrequeryspProcessor.class);

	@Override
	public int process(Userruninfo userrininfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String wheres = pcmd.getValue("wheres");
		String procname = pcmd.getValue("procname");

		CallableStatement call = null;
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "{call " + procname + "(?,?,?,?)}";
			call = con.prepareCall(sql);
			call.setString(1, wheres);
			call.setString(2, userrininfo.getUserid());
			call.setString(3, userrininfo.getRoleid());
			call.registerOutParameter(4, java.sql.Types.VARCHAR);
			call.execute();

			String otherwheres = call.getString(4);
			resp.addCommand(new StringCommand("+OK"));
			ParamCommand resppcmd=new ParamCommand();
			resp.addCommand(resppcmd);
			resppcmd.addParam("otherwheres",otherwheres);
			
		} catch (Exception e) {
			logger.error("error", e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		} finally {
			if (call != null) {
				call.close();
			}
			if (con != null) {
				con.close();
			}
		}

		return 0;
	}
}