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
 * 自定义数据项管理器
 * 
 * @author user
 * 
 */
public class WfDataitemManager {
	Category logger = Category.getInstance(WfDataitemManager.class);
	/**
	 * 流程ID
	 */
	String wfid = "";

	/**
	 * 基表数据源
	 */
	DBTableModel basedbmodel = null;
	/**
	 * 一个流程的所有数据项.
	 */
	Vector<WfDataitem> dataitems = new Vector<WfDataitem>();

	public DBTableModel getBasedbmodel() {
		return basedbmodel;
	}

	/**
	 * 计算流程入口表达式.
	 * 
	 * @param wfdefine
	 * @return true 表示可以进入
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
	 * 计算结点入口表达式
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
	 * 计算表达式condexpr的值
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
				throw new Exception("没有找到" + viewname + ","
						+ wfdefine.getPkcolname() + "=" + pkvalue);
		}
		
	}

	/**
	 * 计算表达式.
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
		// 查询基表
		prepareBasedata(con,wfdefine,pkvalue);
		// 对每个数据项进行处理.
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
			// 是不是列名?
			String dataitemresult = null;
			boolean iscol = basedbmodel.getColumninfo(dataitemname) != null;
			if (!iscol) {
				WfDataitem dataitem = getDataitem(dataitemname);
				// 计算值
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
		logger.debug("替换后expr=" + expr);
		// 再对表达式进行计算
		Dataitemcalcer exprcalcer = new Dataitemcalcer(basedbmodel);
		expr = exprcalcer.calc(0, expr);

		return expr;
	}

	/**
	 * 填写{}中的参数.
	 * @param con
	 * @param wfdefine
	 * @param pkvalue
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	public String fillParam(Connection con, Wfdefine wfdefine, String pkvalue,
			String expr) throws Exception {
		// 查询基表
		prepareBasedata(con,wfdefine,pkvalue);
		// 对每个数据项进行处理.
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
			// 是不是列名?
			String dataitemresult = null;
			boolean iscol = basedbmodel.getColumninfo(dataitemname) != null;
			if (!iscol) {
				WfDataitem dataitem = getDataitem(dataitemname);
				// 计算值
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
		logger.debug("替换参数后expr=" + expr);

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
		throw new Exception("没有定义数据项" + dataitemname);
	}

	/**
	 * 从数据库中加载一个流程数据项.
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
	 * 计算某个数据项的值
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
		//取得数据项定义

		WfDataitem dataitem = getDataitemByid(dataitemid);
		if(dataitem==null)return "";
		return dataitem.calcValue(con, this, wfdefine, pkvalue);
	}
	
	/**
	 * 由数据项ID查询数据项定义
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
