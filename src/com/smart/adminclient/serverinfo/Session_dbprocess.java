package com.smart.adminclient.serverinfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

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
public class Session_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Session_ste.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询服务器连接")) {
			return -1;
		}

		Session_ste ste = new Session_ste(null);
		DBTableModel dbmodel = ste.getDBtableModel();
		Connection con = null;
		try {
			con = getSysConnection();
			querySession(con, dbmodel);

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

	void querySession(Connection con, DBTableModel dbmodel)
			throws Exception {
		String sql = "SELECT SID,"+   
         "SERIAL# SERIALNO,"+   
         "USERNAME,"+   
         "COMMAND,"+   
         "SCHEMANAME,"+   
         "STATUS,"+   
         "LOCKWAIT,"+   
         "MACHINE,"+   
         "PROGRAM,"+   
         "MODULE "+ 
         " FROM V$SESSION";  
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				int row=dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(row, "SID", rs.getString("SID"));
				dbmodel.setItemValue(row, "SERIALNO", rs.getString("SERIALNO"));
				dbmodel.setItemValue(row, "USERNAME", rs.getString("USERNAME"));
				dbmodel.setItemValue(row, "COMMAND", rs.getString("COMMAND"));
				dbmodel.setItemValue(row, "SCHEMANAME", rs.getString("SCHEMANAME"));
				dbmodel.setItemValue(row, "STATUS", rs.getString("STATUS"));
				dbmodel.setItemValue(row, "LOCKWAIT", rs.getString("LOCKWAIT"));
				dbmodel.setItemValue(row, "MACHINE", rs.getString("MACHINE"));
				dbmodel.setItemValue(row, "PROGRAM", rs.getString("PROGRAM"));
				dbmodel.setItemValue(row, "MODULE", rs.getString("MODULE"));
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}
}