package com.inca.npworkflow.server;

import java.sql.Connection;

import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

public class DemoNodeaction implements WfnodeActionIF{

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_JAVA;
	}

	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		System.err.println("node action demo,nodeinstanceid="+nodeinstance.getWfnodeinstanceid());
		// 更新单据状态
		WfEngine.getInstance().setApproveResult(con,
				nodeinstance.getWfnodeinstanceid(),
				nodeinstance.getNodedefine().getNodename(), "", "JAVA", true, "JAVA程序通过");

	}

}
