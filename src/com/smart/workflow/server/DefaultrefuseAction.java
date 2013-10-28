package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.common.Wfdefine;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * 不通过
 * @author user
 *
 */
public class DefaultrefuseAction implements WfnodeActionIF{

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_REFUSEPRIOR;
	}

	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		//回填状态
		// 更新单据状态
		WfEngine.getInstance().setApproveResult(con,
				nodeinstance.getWfnodeinstanceid(),
				nodeinstance.getNodedefine().getNodename(), "", "", false, "自动拒绝");
		
	}

}
