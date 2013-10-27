package com.inca.npworkflow.server;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SelectHelper;
import com.inca.npworkflow.common.WfDataitem;
import com.inca.npworkflow.common.Wfdefine;

/**
 * �Զ��������������
 * 
 * @author user
 * 
 */
public class WfDataitemManager {
	Category logger = Category.getInstance(WfDataitemManager.class);
	/**
	 * ����ID
	 */
	String wfid = "";

	/**
	 * ��������Դ
	 */
	DBTableModel basedbmodel = null;
	/**
	 * һ�����̵�����������.
	 */
	Vector<WfDataitem> dataitems = new Vector<WfDataitem>();

	public DBTableModel getBasedbmodel() {
		return basedbmodel;
	}

	/**
	 * ����������ڱ��ʽ.
	 * 
	 * @param wfdefine
	 * @return true ��ʾ���Խ���
	 */
	public boolean calcWfCond(Connection con, Wfdefine wfdefine, String pkvalue)
			throws Exception {
		String condexpr = wfdefine.getCondexpr();
		if(condexpr==null||condexpr.length()==0){
			return true;
		}
		return calcCondExpr(con, wfdefine, pkvalue, condexpr);
	}

	/**
	 * ��������ڱ��ʽ
	 * 
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @return
	 * @throws Exception
	 */
	public boolean calcNodeCond(Connection con, Wfdefine wfdefine,
			String pkvalue, String nodecond) throws Exception {
		return calcCondExpr(con, wfdefine, pkvalue, nodecond);
	}

	/**
	 * ������ʽcondexpr��ֵ
	 * 
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @param condexpr
	 * @return
	 */
	public boolean calcCondExpr(Connection con, Wfdefine wfdefine, String pkvalue,
			String condexpr) throws Exception {
		String s = calcExpr(con, wfdefine, pkvalue, condexpr);
		logger.debug("wfid="+wfdefine.getWfid()+",pkvalue="+pkvalue+",condexpr="+condexpr+",result is "+s);
		return s.equals("1");
	}
	
	void prepareBasedata(Connection con,Wfdefine wfdefine,String pkvalue) throws Exception{
		if (basedbmodel == null) {
			String viewname = wfdefine.getViewname();
			if (viewname.length() == 0) {
				viewname = wfdefine.getTablename();
			}
			String sql = "select * from " + viewname + " where "
					+ wfdefine.getPkcolname() + "=?";
			logger.debug(sql);
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(pkvalue);
			basedbmodel = sh.executeSelect(con, 0, 1);
			if (basedbmodel.getRowCount() == 0)
				throw new Exception("û���ҵ�" + viewname + ","
						+ wfdefine.getPkcolname() + "=" + pkvalue);
		}
		
	}

	/**
	 * ������ʽ.
	 * 
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public String calcExpr(Connection con, Wfdefine wfdefine, String pkvalue,
			String expr) throws Exception {
		// ��ѯ����
		prepareBasedata(con,wfdefine,pkvalue);
		// ��ÿ����������д���.
		for (;;) {
			int p1 = 0;
			int p2 = 0;
			p1 = expr.indexOf("{", p1);
			if (p1 < 0)
				break;
			p1++;
			p2 = expr.indexOf("}", p1);
			if (p2 < 0)
				break;

			String dataitemname = expr.substring(p1, p2);
			// �ǲ�������?
			String dataitemresult = null;
			boolean iscol = basedbmodel.getColumninfo(dataitemname) != null;
			if (!iscol) {
				WfDataitem dataitem = getDataitem(dataitemname);
				// ����ֵ
				dataitemresult = dataitem.calcValue(con, this, wfdefine,
						pkvalue);
				String datatype = dataitem.getDatatype();
				if (datatype.equals("varchar")) {
					dataitemresult = "\"" + dataitemresult + "\"";
				}else{
					if(dataitemresult==null||dataitemresult.length()==0){
						dataitemresult="0";
					}
				}
			} else {
				dataitemresult = basedbmodel.getItemValue(0, dataitemname);
				if (basedbmodel.getColumninfo(dataitemname).getColtype()
						.equals("number")) {
					if(dataitemresult==null||dataitemresult.length()==0){
						dataitemresult="0";
					}
				} else {
					dataitemresult = "\"" + dataitemresult + "\"";
				}
			}
			expr = expr.substring(0, p1 - 1) + dataitemresult
					+ expr.substring(p2 + 1);
		}
		logger.debug("�滻��expr=" + expr);
		// �ٶԱ��ʽ���м���
		Dataitemcalcer exprcalcer = new Dataitemcalcer(basedbmodel);
		expr = exprcalcer.calc(0, expr);

		return expr;
	}

	/**
	 * ��д{}�еĲ���.
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public String fillParam(Connection con, Wfdefine wfdefine, String pkvalue,
			String expr) throws Exception {
		// ��ѯ����
		prepareBasedata(con,wfdefine,pkvalue);
		// ��ÿ����������д���.
		for (;;) {
			int p1 = 0;
			int p2 = 0;
			p1 = expr.indexOf("{", p1);
			if (p1 < 0)
				break;
			p1++;
			p2 = expr.indexOf("}", p1);
			if (p2 < 0)
				break;

			String dataitemname = expr.substring(p1, p2);
			// �ǲ�������?
			String dataitemresult = null;
			boolean iscol = basedbmodel.getColumninfo(dataitemname) != null;
			if (!iscol) {
				WfDataitem dataitem = getDataitem(dataitemname);
				// ����ֵ
				dataitemresult = dataitem.calcValue(con, this, wfdefine,
						pkvalue);
				String datatype = dataitem.getDatatype();
				if (datatype.equals("varchar")) {
					dataitemresult = "\"" + dataitemresult + "\"";
				}else{
					if(dataitemresult==null||dataitemresult.length()==0){
						dataitemresult="0";
					}
				}
			} else {
				dataitemresult = basedbmodel.getItemValue(0, dataitemname);
				if (basedbmodel.getColumninfo(dataitemname).getColtype()
						.equals("number")) {
					if(dataitemresult==null||dataitemresult.length()==0){
						dataitemresult="0";
					}
				} else {
					dataitemresult = "\"" + dataitemresult + "\"";
				}
			}
			expr = expr.substring(0, p1 - 1) + dataitemresult
					+ expr.substring(p2 + 1);
		}
		logger.debug("�滻������expr=" + expr);

		return expr;
	}

	WfDataitem getDataitem(String dataitemname) throws Exception {
		Enumeration<WfDataitem> en = dataitems.elements();
		while (en.hasMoreElements()) {
			WfDataitem di = en.nextElement();
			if (di.getDataitemname().equalsIgnoreCase(dataitemname)) {
				return di;
			}
		}
		throw new Exception("û�ж���������" + dataitemname);
	}

	/**
	 * �����ݿ��м���һ������������.
	 * 
	 * @param con
	 * @param wfid
	 * @return
	 * @throws Exception
	 */
	public static WfDataitemManager loadFromDB(Connection con, String wfid)
			throws Exception {
		WfDataitemManager dm = new WfDataitemManager();
		dm.wfid = wfid;

		String sql = "select dataitemid from np_wf_dataitem where wfid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(wfid);
		DBTableModel dbmodel = sh.executeSelect(con, 0, 1000);
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String dataitemid = dbmodel.getItemValue(r, "dataitemid");
			WfDataitem dataitem = WfDataitem.loadFromDB(con, dataitemid);
			dm.dataitems.add(dataitem);
		}
		return dm;
	}

	/**
	 * ����ĳ���������ֵ
	 * 
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @param dataitemid
	 * @throws Exception
	 */
	public String calcDataitemvalue(Connection con, Wfdefine wfdefine,
			String pkvalue, String dataitemid) throws Exception {
		prepareBasedata(con,wfdefine,pkvalue);
		//ȡ���������

		WfDataitem dataitem = getDataitemByid(dataitemid);
		if(dataitem==null)return "";
		return dataitem.calcValue(con, this, wfdefine, pkvalue);
	}
	
	/**
	 * ��������ID��ѯ�������
	 * @param dataitemid
	 * @return
	 */
	public WfDataitem getDataitemByid(String dataitemid){
		Enumeration<WfDataitem> en=dataitems.elements();
		while(en.hasMoreElements()){
			WfDataitem dataitem=en.nextElement();
			if(dataitem.getDataitemid().equals(dataitemid)){
				return dataitem;
			}
		}
		return null;
		
	}

	public Vector<WfDataitem> getDataitems() {
		return dataitems;
	}
	
}
