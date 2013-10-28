package com.smart.workflow.common;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Category;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.server.WfDataitemManager;
import com.smart.workflow.server.WfEngine;

/**
 * ���ʵ��
 * 
 * @author user
 * 
 */
public class Wfnodeinstance {
	Category logger = Category.getInstance(Wfnodeinstance.class);
	/**
	 * ���ʵ��ID
	 */
	String wfnodeinstanceid = "";

	/**
	 * ��㶨��
	 */
	Wfnodedefine nodedefine = null;

	/**
	 * ʵ��ID
	 */
	String wfinstanceid = "";

	/**
	 * ���ID
	 */
	String wfnodeid = "";


	/**
	 * ������ʱ��
	 */
	String startdate = "";

	/**
	 * �������ʱ��
	 */
	String enddate = "";

	/**
	 * ������
	 */
	String employeeid = "";

	/**
	 * �������
	 */
	String approvemessage = "";

	
	/**
	 * ������־.
	 * 1=���˲���
	 * 2=�������.
	 */
	String refflag="";
	
	/**
	 * ������Ϣ.�����������������
	 */
	String refmessage="";
	
	/**
	 * �������¼��Ӧ��ԭ���ID
	 */
	String refnodeinstanceid="";
	
	/**
	 * �������������?
	 * 
	 * @param con
	 * @param wfinstance
	 * @return
	 * @throws Exception
	 */
	public boolean calcCondexpr(Connection con, Wfinstance wfinstance)
			throws Exception {
		String nodecond = nodedefine.entercond;
		if (nodecond == null || nodecond.length() == 0)
			return true;
		WfDataitemManager datamgr = wfinstance.getDataitemmgr();
		boolean ret = datamgr.calcNodeCond(con, wfinstance.wfdefine, wfinstance
				.getPkvalue(), nodecond);
		return ret;
	}


	/**
	 * ��������
	 * 
	 * @return
	 */
	public String getProcresultForupdate(Connection con) {
		String sql = "select actionresult from np_wf_node_instance where wfnodeinstanceid=? for update";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfnodeinstanceid);
		try {
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			if (dm.getRowCount() == 0)
				return "";
			return dm.getItemValue(0, "actionresult");
		} catch (Exception e) {
			logger.error("error", e);
			return "";
		}
	}

	/**
	 * ������. �����java�Զ���,���д���.������˹�,�ȴ��˹��������.
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void process(Connection con, Wfinstance wfinstance) throws Exception {
		WfnodeActionIF nodeaction = nodedefine.getNodeaction();
		try {
			nodeaction.process(con, wfinstance, this);
			con.commit();
		} catch (Exception e) {
			con.rollback();
			logger.error("error", e);
		}
	}

	/**
	 * ����������������Զ�ͨ��
	 * 
	 * @throws Exception
	 */
	public void autoPass(Connection con) {
		// ����startdateΪϵͳ����
		try {
			String sql = "update np_wf_node_instance set startdate=sysdate where wfnodeinstanceid=?";

			UpdateHelper uh = new UpdateHelper(sql);
			uh.bindParam(getWfnodeinstanceid());
			uh.executeUpdate(con);
			String approvemsg = "����,�Զ�ͨ��";
			// �����������������ͨ������
			WfEngine.getInstance().setApproveResult(con, getWfnodeinstanceid(),
					nodedefine.getNodename(),"", "", true, approvemsg);

			con.commit();
		} catch (Exception e) {
			// donn;t rollback here
			logger.error("error", e);
		}
	}

	/**
	 * �����ݿ��м��ؽ��ʵ��
	 * 
	 * @param con
	 * @param wfnodeinstanceid
	 * @return
	 * @throws Exception
	 */
	public static Wfnodeinstance loadFromDB(Connection con,
			String wfnodeinstanceid) throws Exception {
		Wfnodeinstance nodeinst = new Wfnodeinstance();
		String sql = "select * from np_wf_node_instance where wfnodeinstanceid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfnodeinstanceid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() == 0)
			throw new Exception("û���ҵ�����ʵ�����wfnodeinstanceid="
					+ wfnodeinstanceid);
		nodeinst.wfnodeinstanceid = dm.getItemValue(0, "wfnodeinstanceid");
		nodeinst.wfinstanceid = dm.getItemValue(0, "wfinstanceid");
		nodeinst.wfnodeid = dm.getItemValue(0, "wfnodeid");
		nodeinst.startdate = dm.getItemValue(0, "startdate");
		nodeinst.enddate = dm.getItemValue(0, "enddate");
		nodeinst.employeeid = dm.getItemValue(0, "employeeid");
		nodeinst.approvemessage = dm.getItemValue(0, "approvemessage");
		nodeinst.refflag = dm.getItemValue(0, "refflag");
		nodeinst.refmessage = dm.getItemValue(0, "refmessage");
		nodeinst.refnodeinstanceid = dm.getItemValue(0, "refnodeinstanceid");

		// ����node
		//nodeinst.nodedefine = Wfnodedefine.loadFromDB(con, nodeinst.wfnodeid);
		nodeinst.nodedefine=WfEngine.getNodedefine(nodeinst.wfnodeid);

		return nodeinst;
	}

	public Wfnodedefine getNodedefine() {
		return nodedefine;
	}

	public String getWfnodeinstanceid() {
		return wfnodeinstanceid;
	}


	public String getApprovemessage() {
		return approvemessage;
	}


	public String getWfinstanceid() {
		return wfinstanceid;
	}


	public String getRefflag() {
		return refflag;
	}


	public String getRefmessage() {
		return refmessage;
	}


	public String getRefnodeinstanceid() {
		return refnodeinstanceid;
	}

}
