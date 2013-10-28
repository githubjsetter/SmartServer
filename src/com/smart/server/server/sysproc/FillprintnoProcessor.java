package com.smart.server.server.sysproc;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

/**
 * 填写打印单号
 * 
 * @author Administrator
 * 
 */
public class FillprintnoProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:填写打印单号";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!COMMAND.equals(req.getCommand()))
			return -1;
		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String serialnoid = pcmd.getValue("serialnoid");
		String tablename = pcmd.getValue("tablename");
		String fillcolname = pcmd.getValue("fillcolname");
		String pkcolname = pcmd.getValue("pkcolname");
		String dbmodelvalues = pcmd.getValue("dbmodelvalues");
		String printflagcolname = pcmd.getValue("printflagcolname");
		String printmanidcolname = pcmd.getValue("printmanidcolname");
		String printdatecolname = pcmd.getValue("printdatecolname");
		String tablename1 = pcmd.getValue("tablename1");
		String pkcolname1 = pcmd.getValue("pkcolname1");
		String dbmodelvalues1 = pcmd.getValue("dbmodelvalues1");
		//禁止再打印
		String strforbidreprint = pcmd.getValue("forbidreprint");
		boolean forbidreprint=strforbidreprint!=null && strforbidreprint.equals("true");
		
		if (tablename1 == null)
			tablename1 = "";
		if (pkcolname1 == null)
			pkcolname1 = "";
		if (dbmodelvalues1 == null)
			dbmodelvalues1 = "";

		Connection con = null;
		try {
			con = getConnection();
			String sql = "";
			UpdateHelper uh = null;
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String busidate = fmt.format(new java.util.Date());
			String serialno = null;
			
			String vs[] = dbmodelvalues.split(":");
			for (int i = 0; tablename.length() > 0 && pkcolname.length() > 0
					&& fillcolname.length() > 0 && i < vs.length; i++) {
				String v = vs[i];
				if (v.length() == 0)
					continue;
				// 取原值，如果为空，产生新的号
				sql = "select " + fillcolname + " from " + tablename
						+ " where " + pkcolname + "=?";
				SelectHelper sh = new SelectHelper(sql);
				sh.bindParam(v);
				DBTableModel dm = sh.executeSelect(con, 0, 1);
				if (dm.getRowCount() == 0)
					continue;
				String dbv = dm.getItemValue(0, 0);
				if (dbv.length() > 0)
					continue;
				// 生成序列号
				try {
					if (serialno == null) {
						serialno = SerialnoHelper.getSerialno(con, serialnoid,
								busidate, userinfo.getEntryid());
					}
					sql = "update " + tablename + " set " + fillcolname
							+ "=? where " + pkcolname + "=?";

					uh = new UpdateHelper(sql);
					uh.bindParam(serialno);
					uh.bindParam(v);
					uh.executeUpdate(con);
				} catch (Exception e) {
					logger.error("error sql:" + sql, e);
				}
			}

			vs = dbmodelvalues1.split(":");
			for (int i = 0; i < vs.length; i++) {
				String v = vs[i];
				if (v.length() == 0)
					continue;

				if (printflagcolname != null && printflagcolname.length() > 0
						&& tablename1.length() > 0 && pkcolname1.length() > 0) {
					if(forbidreprint){
						//检查再打印
						sql = "select count(*) ct  from " + tablename1 +" where " + printflagcolname+"='1'"+
						 " and " + pkcolname1 + "=? ";
						SelectHelper sh=new SelectHelper(sql);
						sh.bindParam(v);
						DBTableModel dm1=sh.executeSelect(con, 0,1	);
						String strct=dm1.getItemValue(0, "ct");
						if(strct.equals("1")){
							con.rollback();
							resp.addCommand(new StringCommand("-ERROR:已打印.没有再打印授权,不能打"));
							return 0;
						}
					}
					
					
					sql = "update " + tablename1 + " set " + printflagcolname
							+ "=1 where " + pkcolname1 + "=?";
					uh = new UpdateHelper(sql);
					uh.bindParam(v);
					try {
						uh.executeUpdate(con);
					} catch (Exception e) {
						logger.error("error sql:" + sql, e);
					}
				}
				if (printmanidcolname != null && printmanidcolname.length() > 0
						&& tablename1.length() > 0 && pkcolname1.length() > 0) {
					sql = "update " + tablename1 + " set " + printmanidcolname
							+ "=? where " + pkcolname1 + "=?";
					uh = new UpdateHelper(sql);
					uh.bindParam(userinfo.getUserid());
					uh.bindParam(v);
					try {
						uh.executeUpdate(con);
					} catch (Exception e) {
						logger.error("error sql:" + sql, e);
					}
				}
				if (printdatecolname != null && printdatecolname.length() > 0
						&& tablename1.length() > 0 && pkcolname1.length() > 0) {
					sql = "update " + tablename1 + " set " + printdatecolname
							+ "=sysdate where " + pkcolname1 + "=?";
					uh = new UpdateHelper(sql);
					uh.bindParam(v);
					try {
						uh.executeUpdate(con);
					} catch (Exception e) {
						logger.error("error sql:" + sql, e);
					}
				}

			}
			con.commit();
			resp.addCommand(new StringCommand("+OK"));
			ParamCommand respcmd = new ParamCommand();
			resp.addCommand(respcmd);
			if (serialno == null)
				serialno = "";
			respcmd.addParam("serialno", serialno);

			return 0;
		} catch (Exception e) {
			con.rollback();
			logger.error("error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null)
				con.close();
		}
	}
}
