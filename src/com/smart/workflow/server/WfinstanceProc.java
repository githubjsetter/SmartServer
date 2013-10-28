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
 * ������ʵ�����д�������
 * 
 * @author user
 * 
 */
public class WfinstanceProc {
	Category logger = Category.getInstance(WfinstanceProc.class);

	/**
	 * ����һ������ʵ��
	 * 
	 * @param wfinstance
	 */
	public void procInstance(Wfinstance wfinstance) {
		logger.debug("��ʼ��������" + wfinstance.getWfdefine().getWfname() + "ʵ��ID="
				+ wfinstance.getWfinstanceid());
		Connection con = null;
		try {
			con = WfEngine.getConnection();
			int stage = wfinstance.getCurrentstage();
			for (;;) {
				String wfstatus = wfinstance.getWfstatus();
				if (!wfstatus.equals("open")) {
					logger
							.debug("����" + wfinstance.getWfdefine().getWfname()
									+ "ʵ��ID=" + wfinstance.getWfinstanceid()
									+ "�ѹر�,����");
					return;
				}

				// �ȴ���һ����ֱ�����еļ���������
				Wfstageinstance wfstageinstance = wfinstance
						.getStageinstance(stage);
				if (wfstageinstance == null) {
					// ˵�����̽�����.
					logger.debug("����" + wfinstance.getWfdefine().getWfname()
							+ "ʵ��ID=" + wfinstance.getWfinstanceid()
							+ "���м��Ľ�㴦����,����");
					wfinstance.closeWorkflow(con);
					return;
				}

				// �����⼶���еĽ��
				procOnestage(con, wfinstance, stage);

				// ����⼶�ǲ��ǻ���û�д�����Ľ��,���û����,����ѭ��.�������,���ر�����
				boolean stageresult = true;
				int needhumancount = 0;
				Enumeration<Wfnodeinstance> en = wfstageinstance
						.getNodeinstance();
				while (en.hasMoreElements()) {
					Wfnodeinstance nodeinstance = en.nextElement();
					if (nodeinstance.getRefnodeinstanceid().length() > 0) {
						// �Ǳ����������,����Ҫ�ܽ��,����
						continue;
					}
					try {
						String nodeactionresult = nodeinstance
								.getProcresultForupdate(con);
						if (nodeactionresult.length() == 0) {
							if (nodeinstance.getNodedefine().getActiontype()
									.equals(WfnodeActionIF.ACTIONTYPE_HUMAN)) {
								// ˵�����н����Ҫ�˹�����,����
								logger.debug("����"
										+ wfinstance.getWfdefine().getWfname()
										+ "ʵ��ID="
										+ wfinstance.getWfinstanceid()
										+ "�н��"
										+ nodeinstance.getNodedefine()
												.getNodename() + "��Ҫ�˹�����");
								needhumancount++;
							}
						}
						if (nodeactionresult.equals("0")) {
							// ������κ�һ����㷵��ֵΪ0,��������״̬Ϊfalse
							if(!nodeinstance.getNodedefine().getActiontype()
									.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA)
									 && !nodeinstance.getNodedefine().getActiontype()
										.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL)){
							stageresult = false;
							}
						}
					} finally {
						// �����actionresult������
						con.rollback();
					}
				}
				if (stageresult) {
					// ������1,����ѭ��
					if (needhumancount > 0) {
						// ��Ҫ�˹�����
						return;
					}
					stage++;
				} else {
					// ����.
					logger.debug("����" + wfinstance.getWfdefine().getWfname()
							+ "ʵ��ID=" + wfinstance.getWfinstanceid()
							+ "�н�������ܾ�,���̹ر�");
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
	 * �����ĳ�����ܾ�,Ҫִ��ĳ���ľܾ�����
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

			// ִ�оܾ����sql��java��
			// ������
			logger.debug("���ܾ̾���" + wfinstance.getWfdefine().getWfname()
					+ "������" + nodeinstance.getNodedefine().getNodename());
			nodeinstance.process(con, wfinstance);

		}
	}

	/**
	 * ����һ�����н��
	 */
	void procOnestage(Connection con, Wfinstance wfinstance, int stage)
			throws Exception {
		logger.debug("����" + wfinstance.getWfdefine().getWfname() + "�����"
				+ stage + "��");
		Wfstageinstance wfstageinstance = wfinstance.getStageinstance(stage);

		Enumeration<Wfnodeinstance> en = wfstageinstance.getNodeinstance();
		while (en.hasMoreElements()) {
			Wfnodeinstance nodeinstance = en.nextElement();
			if (nodeinstance.getProcresultForupdate(con).length() > 0) {
				// �����״̬,˵���Ѿ��������.
				continue;
			}

			String actiontype = nodeinstance.getNodedefine().getActiontype();
			if (WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA.equals(actiontype)
					|| WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL
							.equals(actiontype)) {
				continue;
			}
			if (!nodeinstance.calcCondexpr(con, wfinstance)) {
				logger.debug("����" + wfinstance.getWfdefine().getWfname() + "���"
						+ nodeinstance.getNodedefine().getNodename()
						+ "�������������Զ�ͨ��");
				nodeinstance.autoPass(con);
				continue;
			}
			// ������
			logger.debug("����" + wfinstance.getWfdefine().getWfname() + "������"
					+ nodeinstance.getNodedefine().getNodename());
			nodeinstance.process(con, wfinstance);
		}
	}

}
