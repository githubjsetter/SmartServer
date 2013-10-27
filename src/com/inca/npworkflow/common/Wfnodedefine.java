package com.inca.npworkflow.common;

import java.sql.Connection;
import java.util.Vector;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SelectHelper;
import com.inca.npworkflow.server.DefaultHumanAction;
import com.inca.npworkflow.server.DefaultPassAction;
import com.inca.npworkflow.server.DefaultRefuseUpdatesqlAction;
import com.inca.npworkflow.server.DefaultUpdatesqlAction;

/**
 * ���̽��
 * 
 * @author user
 * 
 */
public class Wfnodedefine {
	/**
	 * ���ID
	 */
	String wfnodeid = "";

	/**
	 * ����ID
	 */
	String wfid = "";

	/**
	 * �������
	 */
	String nodename = "";

	/**
	 * ��ɫID����
	 */
	Vector<String> roleids = new Vector<String>();

	/**
	 * ��ԱID����
	 */
	Vector<String> employeeids = new Vector<String>();

	/**
	 * ����ͨ���õ�״̬
	 */
	String passstatus = "1";

	/**
	 * 
	 */
	String refusestatus = "0";

	/**
	 * �������
	 */
	String entercond = "";

	/**
	 * ����
	 */
	int stage = 0;

	/**
	 * ��������.WfnodeActionIF��ACTIONTYPE
	 */
	String actiontype = "";
	
	/**
	 * java����
	 */
	String classname="";

	/**
	 * update sql���
	 */
	String updatesql="";

	/**
	 * ����
	 */
	WfnodeActionIF nodeaction = null;

	public String getUpdatesql() {
		return updatesql;
	}

	public WfnodeActionIF getNodeaction() {
		return nodeaction;
	}

	public static Wfnodedefine loadFromDB(Connection con, String nodeid)
			throws Exception {
		Wfnodedefine node = new Wfnodedefine();
		String sql = "select * from np_wf_node where wfnodeid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(nodeid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() == 0)
			throw new Exception("û���ҵ����̽�㶨��nodeid=" + nodeid);

		node.wfnodeid = dm.getItemValue(0, "wfnodeid");
		node.wfid = dm.getItemValue(0, "wfid");
		node.nodename = dm.getItemValue(0, "nodename");
		sql="select roleid from np_wf_node_roleid where wfnodeid=?";
		sh = new SelectHelper(sql);
		sh.bindParam(node.wfnodeid);
		DBTableModel roleids=sh.executeSelect(con, 0, 1000);
		for(int i=0;i<roleids.getRowCount();i++){
			String roleid=roleids.getItemValue(i, "roleid");
			node.roleids.add(roleid);
		}
		
		sql="select employeeid from np_wf_node_employeeid where wfnodeid=?";
		sh = new SelectHelper(sql);
		sh.bindParam(node.wfnodeid);
		DBTableModel employeeids=sh.executeSelect(con, 0, 1000);
		for(int i=0;i<employeeids.getRowCount();i++){
			String employeeid=employeeids.getItemValue(i, "employeeid");
			node.employeeids.add(employeeid);
		}


		try {
			node.stage = Integer.parseInt(dm.getItemValue(0, "stage"));
		} catch (Exception e) {
			node.stage = 0;
		}
		node.entercond = dm.getItemValue(0, "entercond");
		node.passstatus = dm.getItemValue(0, "passstatus");
		node.refusestatus = dm.getItemValue(0, "refusestatus");
		node.actiontype = dm.getItemValue(0, "actiontype");
		node.classname= dm.getItemValue(0, "classname");
		node.updatesql= dm.getItemValue(0, "updatesql");
		
		if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_HUMAN)) {
			node.nodeaction=new DefaultHumanAction();
		} else if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_UPDATESQL)) {
			node.nodeaction=new DefaultUpdatesqlAction();
		} else if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL)) {
			node.nodeaction=new DefaultRefuseUpdatesqlAction();
		} else if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_JAVA)||
				node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_REFUSE_JAVA)) {
			Class clazz=Class.forName(node.classname);
			node.nodeaction=(WfnodeActionIF) clazz.newInstance();
		} else if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_PASS)) {
			node.nodeaction=new DefaultPassAction();
		} else if (node.actiontype.equals(WfnodeActionIF.ACTIONTYPE_REFUSEALL)) {
		} else if (node.actiontype
				.equals(WfnodeActionIF.ACTIONTYPE_REFUSEPRIOR)) {
		} else {
			throw new Exception("����actiontype="+node.actiontype);
		}

		return node;
	}

	
	public String getWfnodeid() {
		return wfnodeid;
	}

	public String getActiontype() {
		return actiontype;
	}

	public String getPassstatus() {
		return passstatus;
	}

	public String getRefusestatus() {
		return refusestatus;
	}

	public String getNodename() {
		return nodename;
	}

	public Vector<String> getRoleids() {
		return roleids;
	}

	public Vector<String> getEmployeeids() {
		return employeeids;
	}

	public String getWfid() {
		return wfid;
	}

	public int getStage() {
		return stage;
	}

	public String getEntercond() {
		return entercond;
	}

}
