package com.smart.workflow.common;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;
import com.smart.workflow.server.WfDataitemManager;

/**
 * 工作流定义.
 * 
 * @author user
 * 
 */
public class Wfdefine {
	Category logger = Category.getInstance(Wfdefine.class);

	/**
	 * 流程定义ID
	 */
	String wfid = "";
	/**
	 * 流程名称
	 */
	String wfname = "";

	/**
	 * 对应单据的表名
	 */
	String tablename = "";

	/**
	 * 基表对应的视图.如果没定义,缺省为表名.
	 */
	String viewname = "";

	/**
	 * 对应单据表的主键列名
	 */
	String pkcolname = "";

	/**
	 * 回填审批信息字段.
	 */
	String messagecolname = "";

	/**
	 * 回填信息字段方式,append或overwrite
	 */
	String messagemethod = "";

	/**
	 * 状态字段名.
	 */
	String statuscolname = "";

	/**
	 * 流程启动的条件.sql select 语句的where条件.
	 */
	String condexpr = "";

	/**
	 * 摘要表达式.由该表达式计算生成摘要.
	 */
	String summaryexpr = "";

	/**
	 * 使用状态.0 停用,1启用
	 */
	int usestatus = 0;


	/**
	 * 级的集合.
	 */
	Vector<WfnodeStage> stages = new Vector<WfnodeStage>();

	/**
	 * 状态集合.每个状态是一个状态ID和名称的对应关系.
	 */
	Vector<Wfstatus> approvestatuses = new Vector<Wfstatus>();


	public String getWfid() {
		return wfid;
	}

	public void setId(String id) {
		this.wfid = id;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getPkcolname() {
		return pkcolname;
	}

	public void setPkcolname(String pkcolname) {
		this.pkcolname = pkcolname;
	}

	public String getCondexpr() {
		return condexpr;
	}

	public static Wfdefine loadFromDB(Connection con, String wfid)
			throws Exception {
		Category logger = Category.getInstance(Wfdefine.class);
		Wfdefine wf = new Wfdefine();
		String sql = "select * from np_wf_define where wfid=?";
		logger.debug(sql);
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		logger.debug("loadfromdb, dm.getrowcount()="+dm.getRowCount());
		if (dm.getRowCount() == 0)
			throw new Exception("没有找到流程定义wfid=" + wfid);
		wf.wfid = wfid;
		wf.wfname = dm.getItemValue(0, "wfname");
		wf.tablename = dm.getItemValue(0, "tablename");
		wf.viewname = dm.getItemValue(0, "viewname");
		wf.pkcolname = dm.getItemValue(0, "pkcolname");
		wf.statuscolname = dm.getItemValue(0, "statuscolname");
		wf.messagecolname = dm.getItemValue(0, "messagecolname");
		wf.condexpr = dm.getItemValue(0, "condexpr");
		wf.summaryexpr = dm.getItemValue(0, "summaryexpr");
		try {
			wf.usestatus = Integer.parseInt(dm.getItemValue(0, "usestatus"));
		} catch (Exception ie) {
			wf.usestatus = 0;
		}
		// 查询自定义审批状态
		logger.debug("begin load ApproveStatus");
		wf.loadApproveStatus(con);


		// 加载结点
		sql = "select wfnodeid from np_wf_node where wfid=?";
		logger.debug(sql);
		sh = new SelectHelper(sql);
		sh.bindParam(wfid);
		dm = sh.executeSelect(con, 0, 5000);
		logger.debug("loaded node count="+dm.getRowCount());

		for (int i = 0; i < dm.getRowCount(); i++) {
			String wfnodeid = dm.getItemValue(i, "wfnodeid");
			logger.debug("begin load node,wfnodeid="+wfnodeid);
			Wfnodedefine node = Wfnodedefine.loadFromDB(con, wfnodeid);
			int stage = node.stage;

			while (wf.stages.size() < stage + 1) {
				wf.stages.add(new WfnodeStage());
			}
			WfnodeStage wfstage = wf.stages.elementAt(stage);
			wfstage.nodes.add(node);
		}
		logger.debug("wfdefine loaded,return");
		return wf;
	}

	/**
	 * 查询自定义审批状态.
	 * 
	 * @param con
	 * @throws Exception
	 */
	void loadApproveStatus(Connection con) throws Exception {
		String sql = "select * from np_wf_approvestatus where wfid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfid);
		DBTableModel dm = sh.executeSelect(con, 0, 1000);
		approvestatuses.removeAllElements();
		for (int i = 0; i < dm.getRowCount(); i++) {
			Wfstatus approvestatus = new Wfstatus();
			approvestatus.wfapprovestatusid = dm.getItemValue(0,
					"wfapprovestatusid");
			approvestatus.statusid = dm.getItemValue(0, "statusid");
			approvestatus.statusname = dm.getItemValue(0, "statusname");
			approvestatus.wfid = wfid;
			approvestatuses.add(approvestatus);
		}
	}

	public String getViewname() {
		return viewname;
	}

	public Vector<WfnodeStage> getStages() {
		return stages;
	}

	/**
	 * 回填单据状态
	 * 
	 * @throws Exception
	 */
	public void fillBasestatus(Connection con, String pkvalue,
			String newstatus, String approvemsg) throws Exception {
		String sql = "update " + tablename + " set " + statuscolname + "=?"
				+ " where " + pkcolname + "=?";
		UpdateHelper uh = new UpdateHelper(sql);
		if (statuscolname.length() > 0 && newstatus.length()>0) {
			uh.bindParam(newstatus);
			uh.bindParam(pkvalue);
			logger.debug(sql);
			uh.executeUpdate(con);
		}

		sql = "update " + tablename + " set " + messagecolname + "=nvl("
				+ messagecolname + ",'')||?||'\n'" + " where " + pkcolname
				+ "=?";
		uh = new UpdateHelper(sql);
		uh.bindParam(approvemsg);
		uh.bindParam(pkvalue);
		if (messagecolname.length() > 0 && approvemsg.length()>0) {
			logger.debug(sql);
			try {
				uh.executeUpdate(con);
			} catch (Exception e) {
				logger.error("error", e);
			}
		}
	}

	public String getWfname() {
		return wfname;
	}

	public String getSummaryexpr() {
		return summaryexpr;
	}
	
	public HashMap<String, Wfnodedefine> getNodedefineMap(){
		HashMap<String, Wfnodedefine> nodedefinemap=new HashMap<String, Wfnodedefine>();
		Enumeration<WfnodeStage>en=stages.elements();
		while(en.hasMoreElements()){
			WfnodeStage stage=en.nextElement();
			Enumeration<Wfnodedefine>ennode=stage.getNodes();
			while(ennode.hasMoreElements()){
				Wfnodedefine nodedefine=ennode.nextElement();
				nodedefinemap.put(nodedefine.getWfnodeid(),nodedefine);
			}
		}
		return nodedefinemap;
	}

}
