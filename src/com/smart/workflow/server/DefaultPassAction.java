package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.common.Wfdefine;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * �Զ�ͨ��
 * 
 * @author user
 * 
 */
public class DefaultPassAction implements WfnodeActionIF {

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_HUMAN;
	}

	/**
	 * �Զ�������״̬��Ϊͨ��.
	 */
	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		// ���µ���״̬
		WfEngine.getInstance().setApproveResult(con,
				nodeinstance.getWfnodeinstanceid(),
				nodeinstance.getNodedefine().getNodename(), "", "", true, "�Զ�ͨ��");
	}
}
