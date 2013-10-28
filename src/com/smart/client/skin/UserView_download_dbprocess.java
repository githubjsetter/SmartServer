package com.smart.client.skin;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;

public class UserView_download_dbprocess extends RequestProcessorAdapter {
	public int process(Userruninfo userruninfo, ClientRequest req,
			ServerResponse serverresponse) throws Exception {
		Connection conn = null;
		if (!"自定义界面-下载方案".equals(req.getCommand()))
			return -1;

		ParamCommand pc = (ParamCommand) req.commandAt(1);
		
		String userviewid = pc.getValue("userviewid");

		try {
			conn = getConnection();
			
			SelectHelper sh = new SelectHelper("  select  userviewdtlid ,userviewid ,classname ,colname ,colwidth ,isshow ,orders  from np_user_view_dtl where userviewid=? order by orders asc ");
		
			sh.bindParam(userviewid);
			
			DataCommand dc  = new DataCommand();
			dc.setDbmodel(sh.executeSelect(conn, 0, 500));
			
			sh = new SelectHelper("  select userviewexprid, userviewid, classname, orderexpr  from np_user_view_expr where userviewid=? ");
			sh.bindParam(userviewid);
			sh.executeSelect(conn, 0, 50);
			
			DataCommand dc2 = new DataCommand();
			dc2.setDbmodel(sh.executeSelect(conn, 0, 50));
			
			serverresponse.addCommand(new StringCommand("+OK"));
			serverresponse.addCommand(dc);
			serverresponse.addCommand(dc2);
			
			conn.commit();
		} catch (Exception e) {
			serverresponse.addCommand(new StringCommand("-ERROR"+e.getMessage()));
		} finally {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}

		return 0;
	}
}
