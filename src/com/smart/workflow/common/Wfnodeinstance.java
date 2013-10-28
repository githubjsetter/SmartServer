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
 * 结点实例
 * 
 * @author user
 * 
 */
public class Wfnodeinstance {
	Category logger = Category.getInstance(Wfnodeinstance.class);
	/**
	 * 结点实例ID
	 */
	String wfnodeinstanceid = "";

	/**
	 * 结点定义
	 */
	Wfnodedefine nodedefine = null;

	/**
	 * 实例ID
	 */
	String wfinstanceid = "";

	/**
	 * 结点ID
	 */
	String wfnodeid = "";


	/**
	 * 进入结点时间
	 */
	String startdate = "";

	/**
	 * 处理完成时间
	 */
	String enddate = "";

	/**
	 * 处理人
	 */
	String employeeid = "";

	/**
	 * 审批意见
	 */
	String approvemessage = "";

	
	/**
	 * 参批标志.
	 * 1=请人参批
	 * 2=参批完成.
	 */
	String refflag="";
	
	/**
	 * 参审信息.参审请求或参审结果意见
	 */
	String refmessage="";
	
	/**
	 * 参审结点记录对应的原结点ID
	 */
	String refnodeinstanceid="";
	
	/**
	 * 符合入口条件吗?
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
	 * 锁库检查检查
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
	 * 处理结点. 如果是java自动类,自行处理.如果是人工,等待人工结果继续.
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
	 * 不符合入口条件，自动通过
	 * 
	 * @throws Exception
	 */
	public void autoPass(Connection con) {
		// 设置startdate为系统日期
		try {
			String sql = "update np_wf_node_instance set startdate=sysdate where wfnodeinstanceid=?";

			UpdateHelper uh = new UpdateHelper(sql);
			uh.bindParam(getWfnodeinstanceid());
			uh.executeUpdate(con);
			String approvemsg = "免审,自动通过";
			// 不满足入口条件，按通过处理
			WfEngine.getInstance().setApproveResult(con, getWfnodeinstanceid(),
					nodedefine.getNodename(),"", "", true, approvemsg);

			con.commit();
		} catch (Exception e) {
			// donn;t rollback here
			logger.error("error", e);
		}
	}

	/**
	 * 从数据库中加载结点实例
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
			throw new Exception("没有找到流程实例结点wfnodeinstanceid="
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

		// 加载node
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
