package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.common.Wfdefine;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * 自动通过
 * 
 * @author user
 * 
 */
public class DefaultPassAction implements WfnodeActionIF {

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_HUMAN;
	}

	/**
	 * 自动将审批状态置为通过.
	 */
	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		// 更新单据状态
		WfEngine.getInstance().setApproveResult(con,
				nodeinstance.getWfnodeinstanceid(),
				nodeinstance.getNodedefine().getNodename(), "", "", true, "自动通过");
	}
}
