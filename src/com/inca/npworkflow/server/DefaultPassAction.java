package com.inca.npworkflow.server;

import java.sql.Connection;

import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfdefine;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

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
