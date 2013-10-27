package com.inca.npworkflow.common;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;


import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DBHelper;
import com.inca.np.util.DeleteHelper;
import com.inca.np.util.InsertHelper;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;
import com.inca.npworkflow.client.ErrormessageDbmodel;
import com.inca.npworkflow.server.WfDataitemManager;
import com.inca.npworkflow.server.WfEngine;
import com.inca.npx.ap.Aphelper;

/**
 * 流程实例.启动的流程.
 * 
 * @author user
 * 
 */
public class Wfinstance {
	Category logger = Category.getInstance(Wfinstance.class);
	/**
	 * 实例ID
	 */
	String wfinstanceid = "";

	/**
	 * 流程ID
	 */
	String wfid = "";
	/**
	 * 流程的定义
	 */
	Wfdefine wfdefine = null;

	/**
	 * 流程实例级
	 */
	Vector<Wfstageinstance> stageinstances = new Vector<Wfstageinstance>();

	/**
	 * 流程对应的基表的主键值.
	 */
	String pkvalue = "";

	/**
	 * 当前的级.从0起.
	 */
	int currentstage = 0;

	String wfstatus = "open";

	/**
	 * 自定义数据管理器.
	 */
	WfDataitemManager dataitemmgr = null;

	public Wfdefine getWfdefine() {
		return wfdefine;
	}

	public Wfinstance() {

	}

	public WfDataitemManager getDataitemmgr() {
		return dataitemmgr;
	}

	public String getWfstatus() {
		return wfstatus;
	}

	public void setWfstatus(String wfstatus) {
		this.wfstatus = wfstatus;
	}

	public int getCurrentstage() {
		return currentstage;
	}

	public void setCurrentstage(int currentstage) {
		this.currentstage = currentstage;
	}

	/**
	 * 创建实例
	 * 
	 * @param wfdefine
	 * @param pkvalue
	 */
	public Wfinstance(Wfdefine wfdefine, String pkvalue) {
		super();
		this.wfdefine = wfdefine;
		this.pkvalue = pkvalue;
	}

	/**
	 * 保存到数据库中。并commit db
	 * 
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public String newinst2db(Connection con) throws Exception {
		wfinstanceid = DBModel2Jdbc.getSeqvalue(con, "np_wf_instance_seq");
		InsertHelper ih = new InsertHelper("np_wf_instance");
		ih.bindParam("wfinstanceid", wfinstanceid);
		ih.bindParam("wfid", wfdefine.wfid);
		ih.bindSysdate("credate");
		ih.bindParam("pkvalue", pkvalue);
		ih.bindParam("wfstatus", "open");
		ih.bindParam("currentstage", "0");
		ih.executeInsert(con);

		// 将相关的结点也插入,生成结点实例
		Enumeration<WfnodeStage> enstage = wfdefine.getStages().elements();
		while (enstage.hasMoreElements()) {
			WfnodeStage nodestage = enstage.nextElement();
			Enumeration<Wfnodedefine> ennode = nodestage.getNodes();
			while (ennode.hasMoreElements()) {
				Wfnodedefine node = ennode.nextElement();
				ih = new InsertHelper("np_wf_node_instance");
				String wfnodeinstanceid = DBModel2Jdbc.getSeqvalue(con,
						"np_wf_node_instance_seq");
				ih.bindParam("wfnodeinstanceid", wfnodeinstanceid);
				ih.bindParam("wfinstanceid", wfinstanceid);
				ih.bindParam("wfnodeid", node.getWfnodeid());
				ih.executeInsert(con);
			}

		}

		con.commit();
		return wfinstanceid;
	}

	/**
	 * 关闭流程.提交db
	 */
	public void closeWorkflow(Connection con) throws Exception {
		wfstatus = "close";
		UpdateHelper uh = new UpdateHelper("update np_wf_instance set "
				+ " wfstatus=? where wfinstanceid=?");
		uh.bindParam(wfstatus);
		uh.bindParam(wfinstanceid);
		uh.executeUpdate(con);

		//删除当前结点
		String sql = "delete np_wf_node_current where \n"
				+ "wfnodeinstanceid in( \n"
				+ "select wfnodeinstanceid from np_wf_instance, \n"
				+ "np_wf_node_instance where \n"
				+ "np_wf_instance.wfinstanceid=? and np_wf_instance.wfstatus='close' \n"
				+ "and np_wf_instance.wfinstanceid=np_wf_node_instance.wfinstanceid)";
		DeleteHelper dh=new DeleteHelper(sql);
		dh.bindParam(wfinstanceid);
		dh.executeDelete(con);
		con.commit();
	}

	/**
	 * 以级的实例
	 * 
	 * @param stage
	 * @return
	 */
	public Wfstageinstance getStageinstance(int stage) {
		if (stage < 0 || stage > stageinstances.size() - 1) {
			return null;
		}
		return stageinstances.elementAt(stage);
	}

	public void saveDB(Connection con) throws Exception {
	}

	public static Wfinstance loadFromDB(Connection con, String instanceid)
			throws Exception {
		Category logger = Category.getInstance(Wfinstance.class);
		Wfinstance inst = new Wfinstance();
		String sql = "select * from np_wf_instance where wfinstanceid=?";
		logger.debug(sql);
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(instanceid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		logger.debug("dm.getrowcount()=" + dm.getRowCount());
		if (dm.getRowCount() == 0)
			throw new Exception("没有找到流程实例instanceid=" + instanceid);
		inst.wfinstanceid = dm.getItemValue(0, "wfinstanceid");
		inst.wfid = dm.getItemValue(0, "wfid");
		inst.pkvalue = dm.getItemValue(0, "pkvalue");
		inst.wfstatus = dm.getItemValue(0, "wfstatus");

		inst.wfdefine = WfEngine.getWfdefine(inst.wfid);

		try {
			inst.currentstage = Integer.parseInt(dm.getItemValue(0,
					"currentstage"));
		} catch (Exception e) {
			inst.currentstage = 0;
		}

		// 加载数据
		// 加载数据管理器
		logger.debug("WfDataitemManager.loadFromDB,wfinst=" + inst.wfid);
		inst.dataitemmgr = WfDataitemManager.loadFromDB(con, inst.wfid);

		// 加载结点实例
		sql = "select wfnodeinstanceid from np_wf_node_instance where Wfinstanceid=?";
		logger.debug(sql);
		sh = new SelectHelper(sql);
		sh.bindParam(instanceid);
		dm = sh.executeSelect(con, 0, 5000);
		for (int i = 0; i < dm.getRowCount(); i++) {
			String wfnodeinstanceid = dm.getItemValue(i, "wfnodeinstanceid");
			logger.debug("Wfnodeinstance.loadFromDB,wfnodeinstanceid="
					+ wfnodeinstanceid);
			Wfnodeinstance nodeinst = Wfnodeinstance.loadFromDB(con,
					wfnodeinstanceid);

			int stage = nodeinst.nodedefine.stage;

			while (inst.stageinstances.size() < stage + 1) {
				inst.stageinstances.add(new Wfstageinstance());
			}
			Wfstageinstance wfstageinst = inst.stageinstances.elementAt(stage);
			wfstageinst.nodeinstances.add(nodeinst);
		}

		logger.debug("wfinstance loaded");
		return inst;
	}

	public String getPkvalue() {
		return pkvalue;
	}

	public String getWfinstanceid() {
		return wfinstanceid;
	}

	/**
	 * 取数据项
	 * 
	 * @param dataitemid
	 * @return
	 */
	public WfDataitem getDataiteminfo(String dataitemid) {
		return dataitemmgr.getDataitemByid(dataitemid);
	}

	public ErrormessageDbmodel checkWfexpr(Connection con) throws Exception {
		ErrormessageDbmodel errdm = new ErrormessageDbmodel();
		try {
			dataitemmgr.calcWfCond(con, wfdefine, pkvalue);
		} catch (Exception e) {
			logger.error("error", e);
			int r = errdm.getRowCount();
			errdm.appendRow();
			errdm.setItemValue(r, "datatype", "流程入口表达式错误");
			errdm.setItemValue(r, "errormessage", e.getMessage());
		}

		// 检查入口表达式
		String summaryexpr = wfdefine.getSummaryexpr();
		try {
			dataitemmgr.calcExpr(con, wfdefine, pkvalue, summaryexpr);
		} catch (Exception e) {
			int r = errdm.getRowCount();
			errdm.appendRow();
			errdm.setItemValue(r, "datatype", "流程摘要表达式有误");
			errdm.setItemValue(r, "errormessage", e.getMessage());
		}

		// 检查每个数据项
		Enumeration<WfDataitem> en = dataitemmgr.getDataitems().elements();
		while (en.hasMoreElements()) {
			WfDataitem dataitem = en.nextElement();
			try {
				dataitem.calcValue(con, dataitemmgr, wfdefine, pkvalue);
			} catch (Exception e) {
				logger.error("error", e);
				int r = errdm.getRowCount();
				errdm.appendRow();
				errdm.setItemValue(r, "datatype", "自定义数据项"
						+ dataitem.getDataitemname() + "错误");
				errdm.setItemValue(r, "errormessage", e.getMessage());
			}
		}

		// 结点入口表达式
		Enumeration<WfnodeStage> enstage = wfdefine.getStages().elements();
		while (enstage.hasMoreElements()) {
			WfnodeStage stage = enstage.nextElement();
			Enumeration<Wfnodedefine> ennodes = stage.getNodes();
			while (ennodes.hasMoreElements()) {
				Wfnodedefine nodedefine = ennodes.nextElement();
				String entercond = nodedefine.getEntercond();
				try {
					dataitemmgr.calcExpr(con, wfdefine, pkvalue, entercond);
				} catch (Exception e) {
					int r = errdm.getRowCount();
					errdm.appendRow();
					errdm.setItemValue(r, "datatype", "结点"
							+ nodedefine.getNodename() + "入口条件错误");
					errdm.setItemValue(r, "errormessage", e.getMessage());
				}

			}
		}

		return errdm;
	}

	public static ErrormessageDbmodel checkWfexpr(Connection con, String wfid,
			String pkvalue) throws Exception {

		Wfinstance wfinst = new Wfinstance();
		wfinst.wfinstanceid = "";
		wfinst.wfid = wfid;
		wfinst.pkvalue = pkvalue;
		wfinst.wfstatus = "open";

		wfinst.wfdefine = WfEngine.getWfdefine(wfid);

		wfinst.currentstage = 0;
		// 加载数据
		// 加载数据管理器
		wfinst.dataitemmgr = WfDataitemManager.loadFromDB(con, wfid);

		return wfinst.checkWfexpr(con);
	}
	
	/**
	 * 是否有数据授权.查询基表,用np_wf_role_ap中的where条件.
	 * 如果有返回就满足授权.否则就没有授权.
	 * @param con
	 * @param roleid
	 * @return
	 */
	public boolean filterAp(Connection con,String roleid,Userruninfo userinfo) {
		String sql="select apvalue from np_wf_role_ap where\n" +
				" wfid=? and roleid=?";
		try {
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(wfid);
			sh.bindParam(roleid);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			//
			if(dm.getRowCount()==0)return true;
			String wheres=dm.getItemValue(0, "apvalue");
			if(wheres.length()==0)return true;
			
			//填写当前部门等
			wheres=Aphelper.filterApwheres(wheres,userinfo);
			
			//现在要根据wheres来判断
			sql="select count(*) ct from ";
			sql+=wfdefine.getTablename();
			sql+=" where "+wfdefine.getPkcolname()+"=?";
			sql=DBHelper.addWheres(sql, wheres);
			logger.debug(sql);
			
			sh=new SelectHelper(sql);
			sh.bindParam(pkvalue);
			
			dm=sh.executeSelect(con, 0, 1);
			String ct=dm.getItemValue(0, "ct");
			if(ct.equals("0"))return false;
			return true;
			
			
			
		} catch (Exception e) {
			logger.error("Error", e);
			return false;
		} finally {
			if (con != null) {
			}
		}
	}
}
