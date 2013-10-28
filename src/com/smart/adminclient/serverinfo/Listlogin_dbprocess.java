package com.smart.adminclient.serverinfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.auth.UserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.process.SteProcessor;
import com.smart.platform.util.StringUtil;

/*功能"系统信息查询"应用服务器处理*/
public class Listlogin_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Listlogin_ste.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询登录用户")) {
			return -1;
		}

		Listlogin_ste ste = new Listlogin_ste(null);
		DBTableModel dbmodel = ste.getDBtableModel();
		Connection con = null;
		try {
			con = getSysConnection();
			queryListlogin(con, dbmodel);

		} catch (Exception e) {
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
		}

		resp.addCommand(new StringCommand("+OK"));

		DataCommand datacmd = new DataCommand();
		resp.addCommand(datacmd);
		datacmd.setDbmodel(dbmodel);

		return 0;

	}

	void queryListlogin(Connection con, DBTableModel dbmodel)
			throws Exception {
		Vector<Userruninfo> infos=UserManager.listLoginuser();
		Enumeration<Userruninfo> en=infos.elements();
		while(en.hasMoreElements()){
			Userruninfo u=en.nextElement();
			int row=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "userid", u.getUserid());
			dbmodel.setItemValue(row, "username", u.getUsername());
			dbmodel.setItemValue(row, "logindatetime", long2date(u.getLogindatetime()));
			dbmodel.setItemValue(row, "lastaccesstime", long2date(u.getLastaccesstime()));
			dbmodel.setItemValue(row, "remoteip", u.getRemoteip());
			dbmodel.setItemValue(row, "deptid", u.getDeptid());
			dbmodel.setItemValue(row, "deptname", u.getDeptname());
			dbmodel.setItemValue(row, "roleid", u.getRoleid());
			dbmodel.setItemValue(row, "rolename", u.getRolename());
			dbmodel.setItemValue(row, "placepointid", u.getPlacepointid());
			dbmodel.setItemValue(row, "placepointname", u.getPlacepointname());
			dbmodel.setItemValue(row, "storageid", u.getStorageid());
			dbmodel.setItemValue(row, "storagename", u.getStoragename());
			dbmodel.setItemValue(row, "banci", u.getBanci());
			dbmodel.setItemValue(row, "sthouseid", u.getSthouseid());
			dbmodel.setItemValue(row, "sthousename", u.getSthousename());
			dbmodel.setItemValue(row, "useday", String.valueOf(u.getUseday()));
			dbmodel.setItemValue(row, "authstring", u.getAuthstring());
			dbmodel.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
		}
	}
	
	SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String long2date(long d){
		Date date=new Date(d);
		return fmt.format(date);
	}
}