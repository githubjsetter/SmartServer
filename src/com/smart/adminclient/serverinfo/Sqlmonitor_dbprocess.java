package com.smart.adminclient.serverinfo;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.JdbcMonitorInfo;
import com.smart.platform.server.RequestDispatch;
import com.smart.platform.server.RequestProcessIF;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.DecimalHelper;

/*功能"系统信息查询"应用服务器处理*/
public class Sqlmonitor_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Listlogin_ste.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询服务器sql执行情况")) {
			return -1;
		}

		Sqlmonitor_ste ste = new Sqlmonitor_ste(null);
		DBTableModel dbmodel = ste.getDBtableModel();

		getSqlmonitorinfos(dbmodel);
		dbmodel.sort(new String[]{"avgusetime","executecount"}, false);

		resp.addCommand(new StringCommand("+OK"));

		DataCommand datacmd = new DataCommand();
		resp.addCommand(datacmd);
		datacmd.setDbmodel(dbmodel);

		return 0;

	}

	void getSqlmonitorinfos(DBTableModel dbmodel) {
		RequestDispatch reqdisp = RequestDispatch.getInstance();
			HashMap<String, JdbcMonitorInfo> infomap = ServerContext.getJdbcMonitormap();
			Iterator<JdbcMonitorInfo> it = infomap.values().iterator();
			while (it.hasNext()) {
				JdbcMonitorInfo info = it.next();
				RecordTrunk rec=dbmodel.appendRow();
				int row = dbmodel.getRowCount() - 1;
				dbmodel.setItemValue(row, "sql", info.getSql());
				dbmodel.setItemValue(row, "executecount", String.valueOf(info.getExecutecount()));
				dbmodel.setItemValue(row, "totalusetime", String.valueOf(info
						.getTotalusetime()));
				dbmodel.setItemValue(row, "maxusetime", String.valueOf(info
						.getMaxusetime()));
				String avgusetime = DecimalHelper.divide(String.valueOf(info
						.getTotalusetime()), String.valueOf(info
						.getExecutecount()), 2);
				dbmodel.setItemValue(row, "avgusetime", avgusetime);
				rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
			}
	}

}