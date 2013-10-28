package com.smart.workflow.server;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.design.SelecthovHov;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

/**
 * 请求参审
 * @author user
 *
 */
public class Reqref_dbprocessor  extends RequestProcessorAdapter{
	static String COMMAND="npserver:请求参批";

	/**
	 * 请求参审.
	 * 在np_wf_node_instance记录refflag=1. 
	 * 插入np_wf_node_instance表记录. 记录refmessage,refnodeinstanceid
	 * 在当前结点表np_wf_node_current插入记录
	 * 在当前结点人员表Np_wf_node_current_employeeid中插入记录
	 * */
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		if(DefaultNPParam.debug==1){
			if(userinfo.getUserid().length()==0){
				userinfo.setUserid("0");
			}
		}
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String wfnodeinstanceid=pcmd.getValue("wfnodeinstanceid");
		String refemployeeid=pcmd.getValue("refemployeeid");
		String refmessage=pcmd.getValue("refmessage");
		
		Connection con = null;
		PreparedStatement ic=null;
		try {
			con = getConnection();
			String sql="select refflag,wfnodeid from np_wf_node_instance where wfnodeinstanceid=? for update";
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(wfnodeinstanceid);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()==0){
				resp.addCommand(new StringCommand("-ERROR:找不到"));
				return 0;
			}
			String refflag=dm.getItemValue(0, "refflag");
			if(refflag.equals("1") || refflag.equals("2")){
				resp.addCommand(new StringCommand("-ERROR:已请求参批,不能多次请求"));
				return 0;
			}
			sql="update np_wf_node_instance set refflag=1 where wfnodeinstanceid=?";
			UpdateHelper uh=new UpdateHelper(sql);
			uh.bindParam(wfnodeinstanceid);
			uh.executeUpdate(con);
			
			//sh=new SelectHelper("select * from np_wf_node_instance where wfnodeinstanceid=?");
			//sh.bindParam(wfnodeinstanceid);
			//dm=sh.executeSelect(con, 0, 1);
			//String wfinstanceid=dm.getItemValue(0, "wfinstanceid");
			String wfnodeid=dm.getItemValue(0, "wfnodeid");
			
			String newwfnodeinstanceid=DBModel2Jdbc.getSeqvalue(con, "Np_wf_node_instance_seq");
			//插入np_wf_node_instance
			sql="insert into np_wf_node_instance(wfnodeinstanceid,Wfinstanceid,Wfnodeid," +
					"Startdate,employeeid,refmessage,refnodeinstanceid)" +
					" select ?,Wfinstanceid,Wfnodeid,sysdate,?,?,?" +
					"  from np_wf_node_instance where wfnodeinstanceid=?";
			ic=con.prepareStatement(sql);
			int col=1;
			ic.setString(col++, newwfnodeinstanceid);
			ic.setString(col++, refemployeeid);
			ic.setString(col++, refmessage);
			ic.setString(col++, wfnodeinstanceid);
			ic.setString(col++, wfnodeinstanceid);
			ic.executeUpdate();
			
			//插入当前
			InsertHelper ih=new InsertHelper("np_wf_node_current");
			ih.bindParam("wfnodeinstanceid",newwfnodeinstanceid);
			ih.executeInsert(con);
			
			//插入结点人员
			ih=new InsertHelper("np_wf_node_ref_employeeid");
			ih.bindParam("employeeid",refemployeeid);
			ih.bindParam("wfnodeinstanceid",newwfnodeinstanceid);
			ih.executeInsert(con);
			
			con.commit();
		} catch (Exception e) {
			con.rollback();
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (ic != null) {
				ic.close();
			}
			if (con != null) {
				con.close();
			}
		}
		
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
}
