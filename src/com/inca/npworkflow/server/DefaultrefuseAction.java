package com.inca.npworkflow.server;

import java.sql.Connection;

import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfdefine;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

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
