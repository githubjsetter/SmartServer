package com.smart.server.dbcp;


import java.util.Enumeration;

import com.smart.adminclient.dbcp.DbcpDbmodel;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 列出有多少个数据库连接池。
 * @author Administrator
 *
 */
public class ListdbcpDbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npclient:listdbcp";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		DbcpDbmodel dbmodel=new DbcpDbmodel();
		Enumeration<DBConnectPool>en=DBConnectPoolFactory.getInstance().getPools().elements();
		while(en.hasMoreElements()){
			DBConnectPool pool=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "name",pool.getName());
			dbmodel.setItemValue(r, "driverClassName",pool.getDriverclassname());
			dbmodel.setItemValue(r, "url",pool.getUrl());
			dbmodel.setItemValue(r, "username",pool.getUsername());
			dbmodel.setItemValue(r, "password",pool.getPassword());
			dbmodel.setItemValue(r, "maxActive",pool.getMaxActive());
			dbmodel.setItemValue(r, "maxIdle",pool.getMaxIdle());
			dbmodel.setItemValue(r, "maxWait",pool.getMaxWait());
			dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_SAVED);
		}
		resp.addCommand(new StringCommand("+OK"));
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dbmodel);
		resp.addCommand(dcmd);
		return 0;
	}
	
	
}
