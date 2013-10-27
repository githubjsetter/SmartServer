package com.inca.adminclient.serverinfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.util.StringUtil;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;

/*功能"系统信息查询"应用服务器处理*/
public class Sessionlock_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Sessionlock_ste.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询服务器锁库")) {
			return -1;
		}

		Sessionlock_ste ste = new Sessionlock_ste(null);
		DBTableModel dbmodel = ste.getDBtableModel();
		Connection con = null;
		try {
			con = getSysConnection();
			querySessionlock(con, dbmodel);

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

	void querySessionlock(Connection con, DBTableModel dbmodel)
			throws Exception {
		String sql = "  SELECT V$LOCKED_OBJECT.SESSION_ID,"
				+ "V$LOCKED_OBJECT.ORACLE_USERNAME,   "
				+ "V$LOCKED_OBJECT.OS_USER_NAME,   "
				+ "V$LOCKED_OBJECT.PROCESS,   "
				+ "V$LOCKED_OBJECT.LOCKED_MODE, "
				+ "V$LOCKED_OBJECT.OBJECT_ID,   " + " all_objects.OBJECT_NAME,   "
				+ " all_objects.OBJECT_TYPE  , "
				+ "v$session.serial# SERIALNO,v$session.LOCKWAIT "
				+ " FROM V$LOCKED_OBJECT,  v$session ,all_objects"
				+ " WHERE     v$session.sid = V$LOCKED_OBJECT.SESSION_ID  and "+
				"  V$LOCKED_OBJECT.OBJECT_ID=all_objects.OBJECT_ID";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				int row=dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(row, "SESSION_ID", rs.getString("SESSION_ID"));
				dbmodel.setItemValue(row, "ORACLE_USERNAME", rs.getString("ORACLE_USERNAME"));
				dbmodel.setItemValue(row, "OS_USER_NAME", rs.getString("OS_USER_NAME"));
				dbmodel.setItemValue(row, "PROCESS", rs.getString("PROCESS"));
				dbmodel.setItemValue(row, "LOCKED_MODE", rs.getString("LOCKED_MODE"));
				dbmodel.setItemValue(row, "OBJECT_ID", rs.getString("OBJECT_ID"));
				dbmodel.setItemValue(row, "OBJECT_NAME", rs.getString("OBJECT_NAME"));
				dbmodel.setItemValue(row, "OBJECT_TYPE", rs.getString("OBJECT_TYPE"));
				dbmodel.setItemValue(row, "SERIALNO", rs.getString("SERIALNO"));
				dbmodel.setItemValue(row, "LOCKWAIT", rs.getString("LOCKWAIT"));
				dbmodel.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
			}
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}
}