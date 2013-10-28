package com.smart.workflow.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.JdbcConnectWraper;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.DeleteHelper;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;
import com.smart.server.dbcp.DBConnectPoolFactory;
import com.smart.workflow.client.ErrormessageDbmodel;
import com.smart.workflow.client.WfnodedataDbmodel;
import com.smart.workflow.client.WfnodeinstanceDbmodel;
import com.smart.workflow.common.WfDataitem;
import com.smart.workflow.common.Wfdefine;
import com.smart.workflow.common.Wfinstance;
import com.smart.workflow.common.Wfnodedefine;
import com.smart.workflow.common.Wfnodeinstance;

/**
 * ��������
 * 
 * @author user
 * 
 */
public class WfEngine {
	private static WfEngine instance = null;
	boolean loaded = false;
	Category logger = Category.getInstance(WfEngine.class);
	/**
	 * Ϊ�˱��ⷴ������,����wfdefinecache
	 */
	static HashMap<String, Wfdefine> wfdefinecache = new HashMap<String, Wfdefine>();
	static HashMap<String, Wfnodedefine> wfnodedefinecache = new HashMap<String, Wfnodedefine>();

	long lockwaittimeout = 60000;// 60��
	/**
	 * ����ͬ���Ķ���
	 */
	Object queuescanlockobject = new Object();
	Object instscanlockobject = new Object();

	public static WfEngine getInstance() {
		if (instance == null) {
			instance = new WfEngine();

		}
		return instance;
	}

	private WfEngine() {

		Connection con = null;
		try {
			con = getConnection();
			synchronized (wfdefines) {
				if (!loaded) {
					loadWfdefineFromDB(con);
					loaded = true;
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
				}
		}

		ScanqueueThread scanthread = new ScanqueueThread();
		scanthread.setDaemon(true);
		scanthread.setPriority(1);
		scanthread.start();

		ScanInstanceThread instscanthread = new ScanInstanceThread();
		instscanthread.setDaemon(true);
		instscanthread.setPriority(1);
		instscanthread.start();
	}

	/**
	 * ���̶��弯
	 */
	Vector<Wfdefine> wfdefines = new Vector<Wfdefine>();

	/**
	 * ��������
	 * 
	 * @param tablename
	 *            ����
	 * @param pkvalue
	 *            ������
	 * @return 1����������. 0 û������.
	 */
	public int startWorkflow(String tablename, String pkvalue) {
		Connection con = null;
		int startcount = 0;
		try {
			con = getConnection();
			synchronized (wfdefines) {
				if (!loaded) {
					loadWfdefineFromDB(con);
					loaded = true;
				}
			}

			// ɨ����Ҫ����������
			Enumeration<Wfdefine> en = wfdefines.elements();
			while (en.hasMoreElements()) {
				Wfdefine wfdefine = en.nextElement();
				if (wfdefine.getTablename().equalsIgnoreCase(tablename)) {
					if (alreadyStart(con, wfdefine.getWfid(), tablename,
							pkvalue)) {
						continue;
					} else {
						// �ǲ��Ƿ���������?
						WfDataitemManager dataitemmgr = WfDataitemManager
								.loadFromDB(con, wfdefine.getWfid());
						boolean condresult = dataitemmgr.calcWfCond(con,
								wfdefine, pkvalue);
						if (!condresult) {
							// �������������
							continue;
						}

						// ��������������
						startWorkflow(con, wfdefine, pkvalue);

						// ������1
						startcount++;
					}
				}
			}

		} catch (Exception e) {
			logger.error("error", e);
			return -1;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return startcount;
	}

	/**
	 * ��������,�ȴ�������ʵ����
	 * 
	 * @param con
	 * @param wfdefine
	 * @param tablename
	 * @param pkvalue
	 * @throws Exception
	 */
	public void startWorkflow(Connection con, Wfdefine wfdefine, String pkvalue)
			throws Exception {
		Wfinstance wfinstance = null;
		// ���ݿ����Ƿ�����ʵ������,�����,�����,û���½�
		String sql = "select wfinstanceid from np_wf_instance where wfid=? and"
				+ " pkvalue=?";
		logger.debug(sql);
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfdefine.getWfid());
		sh.bindParam(pkvalue);
		DBTableModel dbmodel = sh.executeSelect(con, 0, 1);
		logger.debug("begin load wfinstance");
		if (dbmodel.getRowCount() == 0) {
			// ����ʵ��
			wfinstance = new Wfinstance(wfdefine, pkvalue);
			String instanceid = wfinstance.newinst2db(con);
			wfinstance = Wfinstance.loadFromDB(con, instanceid);
		} else {
			String instanceid = dbmodel.getItemValue(0, "wfinstanceid");
			wfinstance = Wfinstance.loadFromDB(con, instanceid);
		}

		// ��ʼһ���̴߳���
		ProcThread t = new ProcThread(wfinstance);
		// t.start();
		// 2008-09-22 ��Ҫ�����߳���.
		t.run();
	}

	class ProcThread extends Thread {
		Wfinstance wfinstance = null;

		ProcThread(Wfinstance wfinstance) {
			this.wfinstance = wfinstance;
		}

		public void run() {
			WfinstanceProc proc = new WfinstanceProc();
			proc.procInstance(wfinstance);
		}
	}

	/**
	 * ĳ����ĳ�����ǲ����Ѿ���������?
	 * 
	 * @param con
	 * @param sourcetable
	 *            ��
	 * @param pkvalue
	 *            ����
	 * @return true������.��Ҫ�ظ�
	 * @throws Exception
	 */
	boolean alreadyStart(Connection con, String wfid, String tablename,
			String pkvalue) throws Exception {
		String sql = "select count(*) ct from np_wf_instance where"
				+ " wfid = ? and pkvalue=? and exists("
				+ " select * from np_wf_define where"
				+ " np_wf_define.wfid=np_wf_instance.wfid and"
				+ " np_wf_define.tablename=upper(?))";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfid);
		sh.bindParam(pkvalue);
		sh.bindParam(tablename);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		String ct = dm.getItemValue(0, "ct");
		return !ct.equals("0");
	}

	public static Connection getConnection() throws Exception {
		if (DefaultNPParam.debug == 1) {
			return getTestCon();
		} else {
			/*
			 * if (ic == null) ic = new InitialContext(); DataSource ds =
			 * (DataSource) ic.lookup(dburl); Connection con =
			 * ds.getConnection();
			 */
			Connection con = DBConnectPoolFactory.getInstance().getConnection();
			con.setAutoCommit(false);
			ServerContext svrcontext = ServerContext.getServercontext();
			if (svrcontext == null) {
				svrcontext = new ServerContext();
				ServerContext.regServercontext(svrcontext);
			}
			JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
			return conwrap;
		}
	}

	public static Connection getTestCon() throws Exception {
		String dbip = DefaultNPParam.debugdbip;
		String dbname = DefaultNPParam.debugdbsid;
		String dbuser = DefaultNPParam.debugdbusrname;
		String dbpass = DefaultNPParam.debugdbpasswd;

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		ServerContext svrcontext = ServerContext.getServercontext();
		if (svrcontext == null) {
			svrcontext = new ServerContext();
			ServerContext.regServercontext(svrcontext);
		}
		JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
		return conwrap;
	}

	/**
	 * �����ݿ��м������̶���
	 * 
	 * @param con
	 * @throws Exception
	 */
	void loadWfdefineFromDB(Connection con) throws Exception {
		if (loaded)
			return;
		String sql = "select wfid from np_wf_define where usestatus=1";
		SelectHelper sh = new SelectHelper(sql);
		DBTableModel dm = sh.executeSelect(con, 0, 5000);
		for (int i = 0; i < dm.getRowCount(); i++) {
			String wfid = dm.getItemValue(i, "wfid");
			Wfdefine wf = Wfdefine.loadFromDB(con, wfid);
			wfdefines.add(wf);
			putWfdefine(wfid, wf);
		}
	}

	/**
	 * ��ѯĳ���˴�����Ľ��ʵ��
	 * 
	 * @param con
	 * @param employeeid
	 * @return
	 * @throws Exception
	 */
	public WfnodeinstanceDbmodel fetchNodeinstanceByemployee(Connection con,
			Userruninfo userinfo) throws Exception {
		WfnodeinstanceDbmodel dbmodel = new WfnodeinstanceDbmodel();
		// ��ѯ���ݿ�
		String cols = "np_wf_node_instance.wfnodeinstanceid,np_wf_node_instance.startdate\n";
		String sql = "select  "
				+ cols
				+ " from np_wf_node_instance,np_wf_node_current,np_wf_node_roleid where \n"
				+ "np_wf_node_instance.wfnodeinstanceid=np_wf_node_current.wfnodeinstanceid\n"
				+ "and np_wf_node_instance.wfnodeid=np_wf_node_roleid.wfnodeid\n"
				+ " and  np_wf_node_roleid.roleid=? and nvl(np_wf_node_instance.refnodeinstanceid,0)=0\n"
				+ "union \n"
				+ "select  "
				+ cols
				+ " from np_wf_node_instance,np_wf_node_current,np_wf_node_employeeid where \n"
				+ "np_wf_node_instance.wfnodeinstanceid=np_wf_node_current.wfnodeinstanceid\n"
				+ "and np_wf_node_instance.wfnodeid=np_wf_node_employeeid.wfnodeid\n"
				+ " and  np_wf_node_employeeid.employeeid=?\n"
				+ "union \n"
				+ "select  "
				+ cols
				+ " from np_wf_node_instance,np_wf_node_current,np_wf_node_ref_employeeid where \n"
				+ "np_wf_node_instance.wfnodeinstanceid=np_wf_node_current.wfnodeinstanceid\n"
				+ "and np_wf_node_instance.wfnodeinstanceid=np_wf_node_ref_employeeid.wfnodeinstanceid\n"
				+ " and  np_wf_node_ref_employeeid.employeeid=?\n";

		logger.debug(sql);
		// for debug, simple sql
		// sql = "select " + cols + " from np_wf_node_instance where \n"
		// + "wfnodeinstanceid in( \n"
		// + "select wfnodeinstanceid from np_wf_node_current)\n";

		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			c1.setString(1, userinfo.getRoleid());
			c1.setString(2, userinfo.getUserid());
			c1.setString(3, userinfo.getUserid());

			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				String startdate = rs.getString("startdate");
				if (startdate == null)
					startdate = "";
				if (startdate.length() > 19)
					startdate = startdate.substring(0, 19);

				// ��������������ժҪ���д���
				String wfnodeinstanceid = rs.getString("wfnodeinstanceid");
				int row = dbmodel.getRowCount();
				dbmodel.appendRow();
				try {
					logger.debug("userinfo deptid=" + userinfo.getDeptid());
					boolean ret = appendTomodel(con, dbmodel, row,
							wfnodeinstanceid, startdate, userinfo);
					if (!ret)
						dbmodel.removeRow(row);
				} catch (Exception e) {
					logger.error("error", e);
					dbmodel.removeRow(row);
				}

			}
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}

		return dbmodel;
	}

	/**
	 * ��ѯʵ��ժҪ
	 * 
	 * @param con
	 * @param dbmodel
	 * @param wfinstanceid
	 */
	boolean appendTomodel(Connection con, WfnodeinstanceDbmodel dbmodel,
			int row, String wfnodeinstanceid, String startdate,
			Userruninfo userinfo) throws Exception {
		Wfnodeinstance nodeinst = Wfnodeinstance.loadFromDB(con,
				wfnodeinstanceid);
		Wfnodedefine nodedefine = nodeinst.getNodedefine();
		Wfinstance wfinst = Wfinstance.loadFromDB(con, nodeinst
				.getWfinstanceid());
		if (!wfinst.filterAp(con, userinfo.getRoleid(), userinfo)) {
			// û����Ȩ.
			return false;
		}
		Wfdefine wfdefine = wfinst.getWfdefine();

		String summaryexpr = wfdefine.getSummaryexpr();
		String pkvalue = wfinst.getPkvalue();
		String summary = wfinst.getDataitemmgr().calcExpr(con, wfdefine,
				pkvalue, summaryexpr);
		dbmodel.setItemValue(row, "wfid", wfdefine.getWfid());
		dbmodel.setItemValue(row, "wfname", wfdefine.getWfname());
		dbmodel.setItemValue(row, "wfnodeid", nodedefine.getWfnodeid());
		dbmodel.setItemValue(row, "wfnodename", nodedefine.getNodename());
		dbmodel.setItemValue(row, "summary", summary);
		dbmodel.setItemValue(row, "startdate", startdate);
		dbmodel.setItemValue(row, "wfnodeinstanceid", nodeinst
				.getWfnodeinstanceid());
		dbmodel.setItemValue(row, "refflag", nodeinst.getRefflag());
		dbmodel.setItemValue(row, "refmessage", nodeinst.getRefmessage());
		dbmodel.setItemValue(row, "refnodeinstanceid", nodeinst
				.getRefnodeinstanceid());
		dbmodel.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
		return true;
	}

	/**
	 * �˹��������,��������״̬Ϊͨ����ܾ�.�ύ���ݿ�.
	 * 
	 * @param con
	 * @param wfnodeinstanceid
	 *            ���ʵ��ID
	 * @param employeeid
	 *            ������Ա
	 * @param result
	 *            �������
	 * @param approvemsg
	 *            �������
	 * @throws Exception
	 */

	public void setApproveResult(Connection con, String wfnodeinstanceid,
			String nodename, String employeeid, String employeename,
			boolean approveflag, String approvemsg) throws Exception {
		// ����Ƿ��Ѵ������
		String sql = "select wfstatus from np_wf_instance where wfinstanceid=( \n"
				+ "select wfinstanceid from np_wf_node_instance where wfnodeinstanceid=?) for update";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfnodeinstanceid);
		DBTableModel checkdm = sh.executeSelect(con, 0, 1);
		if (checkdm.getRowCount() == 0) {
			con.rollback();
			throw new Exception("û���ҵ����ʵ��" + wfnodeinstanceid);
		}
		// ���״̬
		String wfstatus = checkdm.getItemValue(0, "wfstatus");
		if (wfstatus.equals("close")) {
			con.rollback();
			throw new Exception("�����ѹر���,wfnodeinstanceid=" + wfnodeinstanceid);
		}

		sql = "select actionresult from np_wf_node_instance where wfnodeinstanceid=? for update";
		sh = new SelectHelper(sql);
		sh.bindParam(wfnodeinstanceid);
		checkdm = sh.executeSelect(con, 0, 1);
		if (checkdm.getRowCount() == 0) {
			con.rollback();
			throw new Exception("û���ҵ����ʵ��" + wfnodeinstanceid);
		}
		// ���״̬
		String actionresult = checkdm.getItemValue(0, "actionresult");
		if (actionresult.length() > 0) {
			con.rollback();
			throw new Exception("���������ˣ����ʵ��=" + wfnodeinstanceid);
		}

		// ���½������״̬,�������,ɾ����ǰnp_wf_node_current-xxxx
		sql = "update np_wf_node_instance set employeeid=?,actionresult=? ,"
				+ " enddate=sysdate ,Approvemessage=?"
				+ " where wfnodeinstanceid=?";
		UpdateHelper uh = new UpdateHelper(sql);
		uh.bindParam(employeeid);
		uh.bindParam(approveflag ? "1" : "0");
		uh.bindParam(approvemsg);
		uh.bindParam(wfnodeinstanceid);
		uh.executeUpdate(con);

		Wfnodeinstance nodeinst = Wfnodeinstance.loadFromDB(con,
				wfnodeinstanceid);
		Wfinstance wfinst = Wfinstance.loadFromDB(con, nodeinst
				.getWfinstanceid());
		String pkvalue = wfinst.getPkvalue();
		// �ǲ��Ǳ����������?
		boolean isrefed = nodeinst.getRefnodeinstanceid().length() > 0;
		String newstatus = "";
		if (approveflag) {
			newstatus = nodeinst.getNodedefine().getPassstatus();
		} else {
			newstatus = nodeinst.getNodedefine().getRefusestatus();
		}

		Wfdefine wfdefine = wfinst.getWfdefine();

		String date = DBModel2Jdbc.getSysdatetime(con);

		if (!isrefed) {
			String fillapprovemsg = date + "���:" + nodename + ",��Ա:"
					+ employeename + ".�������:";
			fillapprovemsg += approveflag ? "ͨ��" : "�ܾ�";
			fillapprovemsg += ".";
			if (approvemsg.length() > 0) {
				fillapprovemsg += "�������:" + approvemsg;
			}
			wfdefine.fillBasestatus(con, pkvalue, newstatus, fillapprovemsg);
		} else {
			// ��Ҫ�������������
			String refmessage = date + ":" + employeename + "����:";
			refmessage += approveflag ? "ͨ��" : "�ܾ�";
			refmessage += ".";
			if (approvemsg.length() > 0) {
				refmessage += "�������:" + approvemsg;
			}

			String orgrefnodeinstanceid = nodeinst.getRefnodeinstanceid();
			UpdateHelper uh1 = new UpdateHelper(
					"update np_wf_node_instance set refflag=2,refmessage=? where wfnodeinstanceid=?");
			uh1.bindParam(refmessage);
			uh1.bindParam(orgrefnodeinstanceid);
			uh1.executeUpdate(con);

			sql = "delete np_wf_node_ref_employeeid where employeeid=? and wfnodeinstanceid=?";
			DeleteHelper dh = new DeleteHelper(sql);
			dh.bindParam(employeeid);
			dh.bindParam(nodeinst.getWfnodeinstanceid());
			dh.executeDelete(con);

		}

		sql = "delete np_wf_node_current where wfnodeinstanceid=?";
		DeleteHelper dh = new DeleteHelper(sql);
		dh.bindParam(wfnodeinstanceid);
		dh.executeDelete(con);
		logger.debug(sql);

		con.commit();

		/**
		 * ������ǰ����.
		 */
		notifyInstanceScan();
	}

	/**
	 * ȡĳ�����ʵ���ľ�����������
	 * 
	 * @param con
	 * @param wfnodeinstanceid
	 * @return
	 */
	public WfnodedataDbmodel fetchNodeinstanceData(Connection con,
			String wfnodeinstanceid) throws Exception {
		WfnodedataDbmodel resultdbmodel = new WfnodedataDbmodel();
		Wfnodeinstance nodeinst = Wfnodeinstance.loadFromDB(con,
				wfnodeinstanceid);
		Wfinstance wfinst = Wfinstance.loadFromDB(con, nodeinst
				.getWfinstanceid());
		Wfdefine wfdefine = wfinst.getWfdefine();
		WfDataitemManager datamgr = wfinst.getDataitemmgr();
		String wfnodeid = nodeinst.getNodedefine().getWfnodeid();
		// ����ǰ���������
		String sql = "select nodename,enddate,employeename,approvemessage from np_wf_node_instance_v "
				+ " where stage<? and wfinstanceid=? order by enddate desc";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(String.valueOf(nodeinst.getNodedefine().getStage()));
		sh.bindParam(wfinst.getWfinstanceid());
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() > 0) {
			for (int i = 0; i < dm.getRowCount(); i++) {
				String nodename = dm.getItemValue(i, "nodename");
				String employeename = dm.getItemValue(i, "employeename");
				String enddate = dm.getItemValue(i, "enddate");
				String approvemessage = dm.getItemValue(i, "approvemessage");
				if ("�Զ�ͨ��".equals(approvemessage)) {
					continue;
				}
				String priormsg = enddate + " " + nodename + " " + employeename
						+ ":" + approvemessage;
				int newrow = resultdbmodel.getRowCount();
				resultdbmodel.appendRow();
				resultdbmodel.setItemValue(newrow, "wfnodeinstanceid",
						wfnodeinstanceid);
				resultdbmodel.setItemValue(newrow, "wfnodedataid", "");
				resultdbmodel.setItemValue(newrow, "dataname", "ǰ�����");
				resultdbmodel.setItemValue(newrow, "datavalue", priormsg);
				resultdbmodel.setdbStatus(newrow, RecordTrunk.DBSTATUS_SAVED);
				break;
			}

		}

		// ��ѯ��������
		sql = "select wfnodedataid,wfnodeid,dataitemid,sortno from np_wf_node_data"
				+ " where wfnodeid=?";

		sh = new SelectHelper(sql);
		sh.bindParam(wfnodeid);
		dm = sh.executeSelect(con, 0, 1000);
		for (int i = 0; i < dm.getRowCount(); i++) {
			String wfnodedataid = dm.getItemValue(i, "wfnodedataid");
			String dataitemid = dm.getItemValue(i, "dataitemid");
			String sortno = dm.getItemValue(i, "sortno");
			WfDataitem wfdataitem = wfinst.getDataiteminfo(dataitemid);

			String datavalue = datamgr.calcDataitemvalue(con, wfdefine, wfinst
					.getPkvalue(), dataitemid);
			int newrow = resultdbmodel.getRowCount();
			resultdbmodel.appendRow();
			resultdbmodel.setItemValue(newrow, "wfnodeinstanceid",
					wfnodeinstanceid);
			resultdbmodel.setItemValue(newrow, "wfnodedataid", wfnodedataid);
			resultdbmodel.setItemValue(newrow, "dataname", wfdataitem
					.getDataitemname());
			resultdbmodel.setItemValue(newrow, "datavalue", datavalue);
			resultdbmodel.setItemValue(newrow, "sortno", sortno);
			resultdbmodel.setdbStatus(newrow, RecordTrunk.DBSTATUS_SAVED);

		}

		// ��ѯ�����ļ���
		sql = "select nodename,enddate,employeename,approvemessage from np_wf_node_instance_v "
				+ " where stage>? and wfinstanceid=? order by wfnodeid asc";
		sh = new SelectHelper(sql);
		sh.bindParam(String.valueOf(nodeinst.getNodedefine().getStage()));
		sh.bindParam(wfinst.getWfinstanceid());
		dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() > 0) {
			String nodename = dm.getItemValue(0, "nodename");
			int newrow = resultdbmodel.getRowCount();
			resultdbmodel.appendRow();
			resultdbmodel.setItemValue(newrow, "wfnodeinstanceid",
					wfnodeinstanceid);
			resultdbmodel.setItemValue(newrow, "wfnodedataid", "");
			resultdbmodel.setItemValue(newrow, "dataname", "�¸����");
			resultdbmodel.setItemValue(newrow, "datavalue", nodename);
			resultdbmodel.setdbStatus(newrow, RecordTrunk.DBSTATUS_SAVED);

		}

		return resultdbmodel;
	}

	/**
	 * ɨ��ӿڱ�np_wf_queue��
	 * 
	 * @author user
	 * 
	 */
	class ScanqueueThread extends Thread {
		public void run() {
			System.out.println("!!!!!!!wf enginee started!!!!!");
			while (true) {
				scan();
				try {
					synchronized (queuescanlockobject) {
						queuescanlockobject.wait(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		void scan() {
			Connection con = null;
			PreparedStatement c1 = null;
			try {
				con = getConnection();
				String sql = "select queueid,tablename,pkvalue from np_wf_queue order by queueid";
				c1 = con.prepareStatement(sql);
				ResultSet rs = c1.executeQuery();
				while (rs.next()) {
					String queueid = rs.getString("queueid");
					startWorkflow(con, queueid);
					con.commit();
				}
			} catch (Exception e) {
				try {
					if (con != null)
						con.rollback();
				} catch (SQLException e1) {
				}
				logger.error("error", e);
				return;
			} finally {
				if (c1 != null) {
					try {
						c1.close();
					} catch (SQLException e) {
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
					}
				}
			}
		}

		void startWorkflow(Connection con, String queueid) throws Exception {
			String sql = "select tablename,pkvalue from np_wf_queue where queueid=? for update";
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(queueid);
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			if (dm.getRowCount() == 0)
				return;
			String tablename = dm.getItemValue(0, "tablename");
			String pkvalue = dm.getItemValue(0, "pkvalue");
			int ret = WfEngine.this.startWorkflow(tablename, pkvalue);
			if (ret >= 0) {
				// ɾ��
				sql = "delete np_wf_queue where queueid=? ";
				DeleteHelper dh = new DeleteHelper(sql);
				dh.bindParam(queueid);
				dh.executeDelete(con);
			}
		}
	}

	/**
	 * ���ɽӿڶ��б�,���ύ
	 * 
	 * @param con
	 * @param tablename
	 * @param pkvalue
	 * @throws Exception
	 */
	public void newQueue(Connection con, String tablename, String pkvalue)
			throws Exception {
		if(DefaultNPParam.debug==1){
			//Ϊ�˵���,���ύ.
			con.commit();
			startWorkflow(tablename.toUpperCase(),pkvalue);
			return;
		}
		
		
		InsertHelper ih = new InsertHelper("np_wf_queue");
		ih.bindSequence("queueid", "np_wf_queue_seq");
		ih.bindParam("tablename", tablename.toUpperCase());
		ih.bindParam("pkvalue", pkvalue);
		ih.executeInsert(con);
		synchronized (queuescanlockobject) {
			queuescanlockobject.notify();
		}
		logger.debug("newQueue,tablename=" + tablename + ",pkvalue=" + pkvalue);
	}

	public void notifyInstanceScan() {
		synchronized (instscanlockobject) {
			instscanlockobject.notify();
		}
	}

	/**
	 * ɨ��û��close��ʵ��,�ƽ�����
	 * 
	 * @author user
	 * 
	 */
	class ScanInstanceThread extends Thread {
		public void run() {
			while (true) {
				scanInstance();
				try {
					synchronized (instscanlockobject) {
						instscanlockobject.wait(lockwaittimeout);
					}
				} catch (InterruptedException e) {
				}
			}

		}

		void scanInstance() {
			Connection con = null;
			PreparedStatement c1 = null;
			try {
				con = getConnection();
				synchronized (wfdefines) {
					if (!loaded) {
						loadWfdefineFromDB(con);
						loaded = true;
					}
				}

				String sql = "select wfid,pkvalue from np_wf_instance where wfstatus='open'"
						+ " order by wfinstanceid";
				logger.debug(sql);
				c1 = con.prepareStatement(sql);
				ResultSet rs = c1.executeQuery();
				while (rs.next()) {
					String wfid = rs.getString("wfid");
					String pkvalue = rs.getString("pkvalue");
					logger.debug("load wfdefine,wfid=" + wfid + ",pkvalue="
							+ pkvalue);
					Wfdefine wfdefine = WfEngine.getWfdefine(wfid);
					if(wfdefine==null){
						continue;
					}
					logger.debug("start instance,wfid=" + wfid + ",pkvalue="
							+ pkvalue);
					WfEngine.this.startWorkflow(con, wfdefine, pkvalue);
					logger.debug("instance finished ,wfid=" + wfid
							+ ",pkvalue=" + pkvalue);
				}
			} catch (Exception e) {
				try {
					if (con != null)
						con.rollback();
				} catch (SQLException e1) {
				}
				logger.error("error", e);
				return;
			} finally {
				if (c1 != null) {
					try {
						c1.close();
					} catch (SQLException e) {
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
					}
				}
			}

		}
	}

	/**
	 * ���¼������̶���
	 */
	public void reset() {
		loaded = false;
	}

	/**
	 * ����������������ʽ ��ڱ��ʽ �����ڱ��ʽ
	 * 
	 * @param con
	 * @param wfid
	 * @param pkvalue
	 * @return
	 * @throws Exception
	 */
	public ErrormessageDbmodel checkWfexpr(Connection con, String wfid)
			throws Exception {
		Wfdefine wfdefine = WfEngine.getWfdefine(wfid);
		String tname = wfdefine.getTablename();
		String pkcolname = wfdefine.getPkcolname();
		if (tname.length() == 0 || pkcolname.length() == 0) {
			throw new Exception("�ȶ���û�����������ٲ���");
		}
		String sql = "select max(" + pkcolname + ") pkvalue from " + tname;
		SelectHelper sh = new SelectHelper(sql);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		String pkvalue = dm.getItemValue(0, "pkvalue");
		if (pkvalue.length() == 0) {
			throw new Exception("����" + tname + "û�м�¼,û������");
		}
		return Wfinstance.checkWfexpr(con, wfid, pkvalue);
	}

	/**
	 * ��cache�м���wfdefine
	 * 
	 * @param wfid
	 * @param wfdefine
	 */
	public static void putWfdefine(String wfid, Wfdefine wfdefine) {
		synchronized (wfdefinecache) {
			wfdefinecache.put(wfid, wfdefine);
			HashMap<String, Wfnodedefine> map = wfdefine.getNodedefineMap();
			wfnodedefinecache.putAll(map);
		}
	}

	public static Wfdefine getWfdefine(String wfid) {
		synchronized (wfdefinecache) {
			return wfdefinecache.get(wfid);
		}
	}

	public static void reloadWfdefine(String wfid) {
		Connection con = null;
		try {
			con = getConnection();
			Wfdefine wfdefine = Wfdefine.loadFromDB(con, wfid);
			putWfdefine(wfid, wfdefine);
		} catch (Exception e) {
			Category.getInstance(WfEngine.class).error("error", e);
			return;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static Wfnodedefine getNodedefine(String nodeid) {
		synchronized (wfnodedefinecache) {
			return wfnodedefinecache.get(nodeid);
		}
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		WfEngine.getInstance();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
