package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.common.Wfdefine;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * ��ͨ��
 * @author user
 *
 */
public class DefaultrefuseAction implements WfnodeActionIF{

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_REFUSEPRIOR;
	}

	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		//����״̬
		// ���µ���״̬
		WfEngine.getInstance().setApproveResult(con,
				nodeinstance.getWfnodeinstanceid(),
				nodeinstance.getNodedefine().getNodename(), "", "", false, "�Զ��ܾ�");
		
	}

}
