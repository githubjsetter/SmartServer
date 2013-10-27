package com.inca.npworkflow.server;

import java.sql.Connection;

import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfdefine;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

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
