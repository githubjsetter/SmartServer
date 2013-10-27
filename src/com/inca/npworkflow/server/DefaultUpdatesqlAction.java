package com.inca.npworkflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.common.Wfinstance;
import com.inca.npworkflow.common.WfnodeActionIF;
import com.inca.npworkflow.common.Wfnodeinstance;

/**
 * 执行update sql的action
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

		// 取sql
		String updatesql = nodeinstance.getNodedefine().getUpdatesql();
		// 进行计算
		WfDataitemManager mgr = wfinstance.getDataitemmgr();
		updatesql = mgr.calcExpr(con, wfinstance.getWfdefine(), wfinstance
				.getPkvalue(), updatesql);
		logger.debug("update sql:" + updatesql);
		// 执行
		uh = new UpdateHelper(updatesql);
		uh.executeUpdate(con);

		// 设置actionresult成功

		sql = "update np_wf_node_instance set actionresult='1' ,enddate=sysdate where "
				+ " wfnodeinstanceid=?";
		uh = new UpdateHelper(sql);
		uh.bindParam(nodeinstance.getWfnodeinstanceid());
		uh.executeUpdate(con);
		// 通知InstanceScanthread要启动处理.by wwh 20080922
		WfEngine.getInstance().notifyInstanceScan();

	}
}
