package com.smart.workflow.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.log4j.Category;

import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.WfnodeActionIF;
import com.smart.workflow.common.Wfnodedefine;
import com.smart.workflow.common.Wfnodeinstance;
import com.smart.workflow.common.Wfstageinstance;

/**
 * 对流程实例进行处理器。
 * 
 * @author user
 * 
 */
public class WfinstanceProc {
	Category logger = Category.getInstance(WfinstanceProc.class);

	/**
	 * 处理一个流程实例
	 * 
	 * @param wfinstance
	 */
	public void procInstance(Wfinstance wfinstance) {
		logger.debug("开始处理流程" + wfinstance.getWfdefine().getWfname() + "实例ID="
				+ wfinstance.getWfinstanceid());
		Connection con = null;
		try {
			con = WfEngine.getConnection();
			int stage = wfinstance.getCurrentstage();
			for (;;) {
				String wfstatus = wfinstance.getWfstatus();
				if (!wfstatus.equals("open")) {
					logger
							.debug("流程" + wfinstance.getWfdefine().getWfname()
									+ "实例ID=" + wfinstance.getWfinstanceid()
									+ "已关闭,返回");
					return;
				}

				// 先处理一级，直到所有的级都处理完
				Wfstageinstance wfstageinstance = wfinstance
						.getStageinstance(stage);
				if (wfstageinstance == null) {
					// 说明流程结束了.
					logger.debug("流程" + wfinstance.getWfdefine().getWfname()
							+ "实例ID=" + wfinstance.getWfinstanceid()
							+ "所有级的结点处理完,返回");
					wfinstance.closeWorkflow(con);
					return;
				}

				// 运行这级所有的结点
				procOnestage(con, wfinstance, stage);

				// 检查这级是不是还有没有处理完的结点,如果没有了,继续循环.如果还有,返回本函数
				boolean stageresult = true;
				int needhumancount = 0;
				Enumeration<Wfnodeinstance> en = wfstageinstance
						.getNodeinstance();
				while (en.hasMoreElements()) {
					Wfnodeinstance nodeinstance = en.nextElement();
					if (nodeinstance.getRefnodeinstanceid().length() > 0) {
						// 是被请求参批的,不需要管结果,继续
						continue;
					}
					try {
						String nodeactionresult = nodeinstance
								.getProcresultForupdate(con);
						if (nodeactionresult.length() == 0) {
							if (nodeinstance.getNodedefine().getActiontype()
									.equals(WfnodeActionIF.ACTIONTYPE_HUMAN)) {
								// 说明还有结点需要人工处理,返回
								logger.debug("流程"
										+ wfinstance.getWfdefine().getWfname()
										+ "实例ID="
										+ wfinstance.getWfinstanceid()
										+ "有结点"
										+ nodeinstance.getNodedefine()
												.getNodename() + "需要人工处理");
								needhumancount++;
							}
						}
						if (nodeactionresult.equals("0")) {
							// 如果有任何一个结点返回值为0,整个级的状态为false
							if(!nodeinstance.getNodedefine().getActiontype()
									.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA)
									 && !nodeinstance.getNodedefine().getActiontype()
										.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL)){
							stageresult = false;
							}
						}
					} finally {
						// 解除对actionresult的锁定
						con.rollback();
					}
				}
				if (stageresult) {
					// 将级加1,继续循环
					if (needhumancount > 0) {
						// 需要人工处理
						return;
					}
					stage++;
				} else {
					// 返回.
					logger.debug("流程" + wfinstance.getWfdefine().getWfname()
							+ "实例ID=" + wfinstance.getWfinstanceid()
							+ "有结点审批拒绝,流程关闭");
					wfinstance.closeWorkflow(con);
					processRefuseAction(con, wfinstance, stage);
					return;
				}
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {

			}
			logger.error("error", e);
			return;
		} finally {
			try {
				wfinstance.saveDB(con);
				con.commit();
			} catch (Exception e1) {
				logger.error("error", e1);
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	/**
	 * 如果在某级被拒绝,要执行某级的拒绝后处理
	 * 
	 * @param con
	 * @wfinstance
	 * @param stage
	 */
	protected void processRefuseAction(Connection con, Wfinstance wfinstance,
			int stage) throws Exception {
		Wfstageinstance wfstageinstance = wfinstance.getStageinstance(stage);

		Enumeration<Wfnodeinstance> en = wfstageinstance.getNodeinstance();
		while (en.hasMoreElements()) {
			Wfnodeinstance nodeinstance = en.nextElement();
			String actiontype = nodeinstance.getNodedefine().getActiontype();
			if (!WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA.equals(actiontype)
					&& !WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL
							.equals(actiontype)) {
				continue;
			}

			// 执行拒绝后的sql或java类
			// 处理结点
			logger.debug("流程拒绝后" + wfinstance.getWfdefine().getWfname()
					+ "处理结点" + nodeinstance.getNodedefine().getNodename());
			nodeinstance.process(con, wfinstance);

		}
	}

	/**
	 * 处理一级所有结点
	 */
	void procOnestage(Connection con, Wfinstance wfinstance, int stage)
			throws Exception {
		logger.debug("流程" + wfinstance.getWfdefine().getWfname() + "处理第"
				+ stage + "级");
		Wfstageinstance wfstageinstance = wfinstance.getStageinstance(stage);

		Enumeration<Wfnodeinstance> en = wfstageinstance.getNodeinstance();
		while (en.hasMoreElements()) {
			Wfnodeinstance nodeinstance = en.nextElement();
			if (nodeinstance.getProcresultForupdate(con).length() > 0) {
				// 如果有状态,说明已经处理过了.
				continue;
			}

			String actiontype = nodeinstance.getNodedefine().getActiontype();
			if (WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA.equals(actiontype)
					|| WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL
							.equals(actiontype)) {
				continue;
			}
			if (!nodeinstance.calcCondexpr(con, wfinstance)) {
				logger.debug("流程" + wfinstance.getWfdefine().getWfname() + "结点"
						+ nodeinstance.getNodedefine().getNodename()
						+ "不满足条件，自动通过");
				nodeinstance.autoPass(con);
				continue;
			}
			// 处理结点
			logger.debug("流程" + wfinstance.getWfdefine().getWfname() + "处理结点"
					+ nodeinstance.getNodedefine().getNodename());
			nodeinstance.process(con, wfinstance);
		}
	}

}
