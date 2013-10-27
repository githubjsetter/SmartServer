package com.inca.npworkflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

/**
 * ִ��update sql��action
 * 
 * @author user
 * 
 */
public class DefaultUpdatesqlAction implements WfnodeActionIF {

	Category logger = Category.getInstance(DefaultUpdatesqlAction.class);

	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_UPDATESQL;
	}

	public void process(Connection con, Wfinstance wfinstance,
			Wfnodeinstance nodeinstance) throws Exception {
		String sql = "update np_wf_node_instance set startdate=sysdate where "
				+ " wfnodeinstanceid=?";
		UpdateHelper uh = new UpdateHelper(sql);
		uh.bindParam(nodeinstance.getWfnodeinstanceid());
		uh.executeUpdate(con);

		// ȡsql
		String updatesql = nodeinstance.getNodedefine().getUpdatesql();
		// ���м���
		WfDataitemManager mgr = wfinstance.getDataitemmgr();
		updatesql = mgr.calcExpr(con, wfinstance.getWfdefine(), wfinstance
				.getPkvalue(), updatesql);
		logger.debug("update sql:" + updatesql);
		// ִ��
		uh = new UpdateHelper(updatesql);
		uh.executeUpdate(con);

		// ����actionresult�ɹ�

		sql = "update np_wf_node_instance set actionresult='1' ,enddate=sysdate where "
				+ " wfnodeinstanceid=?";
		uh = new UpdateHelper(sql);
		uh.bindParam(nodeinstance.getWfnodeinstanceid());
		uh.executeUpdate(con);
		// ֪ͨInstanceScanthreadҪ��������.by wwh 20080922
		WfEngine.getInstance().notifyInstanceScan();

	}
}
