package com.inca.sysmgr.dept;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.demo.hov.Pub_company_hov;
import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.util.DefaultNPParam;

public class Depthov extends CHovBase {

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("companyname",
				"varchar", "部门名称");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("companyopcode", "varchar", "操作码");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("companyid", "number", "部门ID");
		colinfos.add(col);
		return new DBTableModel(colinfos);
	}

	@Override
	public String getDefaultsql() {
		return "Select companyid,companyopcode,companyname from pub_company where "
				+ " nvl(selfflag,0)=1";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond = new Querycond();

		DBColumnDisplayInfo col = new DBColumnDisplayInfo("companyopcode",
				"varchar", "操作码");
		col.setUppercase(true);
		Querycondline ql = new Querycondline(cond, col);
		cond.add(ql);

		col = new DBColumnDisplayInfo("companyname", "varchar", "部门名称");
		ql = new Querycondline(cond, col);
		cond.add(ql);

		col = new DBColumnDisplayInfo("companyid", "number", "部门ID");
		ql = new Querycondline(cond, col);
		cond.add(ql);
		return cond;
	}

	public String[] getColumns() {
		return new String[] { "companyid", "companyopcode", "companyname" };
	}

	public String getDesc() {
		return "选择部门";
	}

	@Override
	protected String getCondcolname(String invokecolname) {
		if (invokecolname.equals("companyname")) {
			return "companyopcode";
		}
		return super.getCondcolname(invokecolname);
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Depthov hov = new Depthov();
		hov.showDialog((Frame) null, "select dept");
	}

}
