package com.inca.npworkflow.server;

import java.sql.Connection;
import java.util.Vector;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.InsertHelper;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodedefine;
import com.inca.npworkflow.common.Wfnodeinstance;

/**
 * 人工处理
 * 
 * @author user
 * 
 */
public class DefaultHumanAction implements WfnodeActionIF {

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_HUMAN;
	}

	public void process(Connection con, Wfinstance wfinstance,Wfnodeinstance nodeinstance)
			throws Exception {
		// 进入日期置为系统时间
		// 放在当前表.
		//Wfnodedefine nodedefine = nodeinstance.getNodedefine();
		// 在当前表中也放,做为任务push到客户端
		String wfnodeinstanceid = nodeinstance.getWfnodeinstanceid();
		String sql="select count(*) from np_wf_node_current where wfnodeinstanceid=?";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(wfnodeinstanceid);
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		String ct=dm.getItemValue(0, 0);
		if(!ct.equals("0")){
			//说明已经启动了人工任务啊.
			return;
		}
		InsertHelper ih = new InsertHelper("np_wf_node_current");
		ih.bindParam("wfnodeinstanceid", wfnodeinstanceid);
		ih.executeInsert(con);
		

		UpdateHelper uh=new UpdateHelper("update np_wf_node_instance set startdate=sysdate\n" +
				" where wfnodeinstanceid=?");
		uh.bindParam(wfnodeinstanceid);
		uh.executeUpdate(con);
	}

}
