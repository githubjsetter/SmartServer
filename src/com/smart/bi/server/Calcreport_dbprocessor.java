package com.smart.bi.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;




import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 计算报表实例
 * 
 * @author user
 * 
 */
public class Calcreport_dbprocessor extends RequestProcessorAdapter {
	static String COMMAND = "npbi.计算报表实例";
	Category logger = Category.getInstance(Calcreport_dbprocessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!COMMAND.equals(req.getCommand()))
			return -1;
		DataCommand dcmd = (DataCommand) req.commandAt(1);
		Vector<String> instanceidmap = new Vector<String>();
		DBTableModel dm = dcmd.getDbmodel();
		for (int i = 0; i < dm.getRowCount(); i++) {
			String instanceid = dm.getItemValue(i, "instanceid");
			instanceidmap.add(instanceid);
		}
		Runthread t=new Runthread(instanceidmap);
		t.start();
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}

	class Runthread extends Thread {
		Vector<String> instanceidmap = null;

		public Runthread(Vector<String> instanceidmap) {
			super();
			this.instanceidmap = instanceidmap;
		}

		public void run() {
			Connection con = null;
			try {
				con = Dsengine.getInstance().getConnection();
				Enumeration<String> en = instanceidmap.elements();
				while (en.hasMoreElements()) {
					String instanceid = en.nextElement();
					Dsengine.getInstance().runReport(con, instanceid);
				}

			} catch (Exception e) {
				logger.error("Error", e);

			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
}