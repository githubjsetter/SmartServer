package com.inca.sysmgr.macreq;

import java.sql.Connection;
import java.sql.SQLException;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.DeleteHelper;
import com.inca.np.util.InsertHelper;
import com.inca.np.util.SelectHelper;
import com.inca.npserver.prod.LicenseManager;
import com.inca.npserver.server.sysproc.MacManager;

public class MacreqApproveProcessor extends RequestProcessorAdapter{
	static String COMMAND="com.inca.sysmgr.macreq.Macreq_ste.审批通过";
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String seqid=pcmd.getValue("seqid");
		
		Connection con = null;
		try {
			con = this.getConnection();
			SelectHelper sh=new SelectHelper("select * from np_mac_req where seqid=?");
			sh.bindParam(seqid);
			DBTableModel dbmodel=sh.executeSelect(con, 0, 1);
			if(dbmodel.getRowCount()==0){
				resp.addCommand(new StringCommand("-ERROR:找不到seqid="+seqid));
				return 0;
			}
			
			//插入,再检查数量
			InsertHelper ih=new InsertHelper("np_mac");
			ih.bindParam("seqid",dbmodel.getItemValue(0, "seqid"));
			ih.bindDateParam("credate",dbmodel.getItemValue(0, "credate"));
			ih.bindSysdate("approvedate");
			ih.bindParam("mac",dbmodel.getItemValue(0, "mac"));
			ih.bindParam("memo",dbmodel.getItemValue(0, "memo"));
			ih.executeInsert(con);
			
			LicenseManager lm=LicenseManager.getInst();
			int maxclient=lm.getMaxClient();
			
			sh=new SelectHelper("select count(*) ct from np_mac");
			DBTableModel ctmodel=sh.executeSelect(con, 0, 1);
			int ct=Integer.parseInt(ctmodel.getItemValue(0, "ct"));
			if(ct>maxclient){
				con.rollback();
				resp.addCommand(new StringCommand("-ERROR:超过了最大"+maxclient+"个用户数的限制"));
				return 0;
			}

			DeleteHelper dh=new DeleteHelper("delete np_mac_req where seqid=?");
			dh.bindParam(seqid);
			dh.executeDelete(con);
			
			con.commit();
			MacManager.getInst().reload();
			resp.addCommand(new StringCommand("+OK"));

		} catch (Exception e) {
			con.rollback();
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return 0;
	}

}
