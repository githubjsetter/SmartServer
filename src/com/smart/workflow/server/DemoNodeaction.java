package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodeinstance;

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
