package com.smart.workflow.client;

import java.awt.HeadlessException;

import com.smart.platform.anyprint.SelectcolumnHov;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 数据定义编辑
 * 
 * @author user
 * 
 */
public class Dataitemedit_ste extends CSteModel {
	String wfid = "1";
	String basetablename = "bms_sa_doc";


	public Dataitemedit_ste(CFrame frame, String title)
			throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_wf_dataitem";
	}

	@Override
	public String getSaveCommandString() {
		return "Dataitemedit_ste.保存数据项定义";
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
	}
	

	@Override
	protected int on_new(int row) {
		setItemValue(row, "wfid",wfid);
		return super.on_new(row);
	}

	@Override
	protected String getOtherWheres() {
		return "Wfid=" + wfid;
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("加入基表列")) {
			addTablecolumn();
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	void addTablecolumn() {
		SelectcolumnHov hov = new SelectcolumnHov();
		hov.showDialog(getParentFrame(), "选择表列", "tname", basetablename
				.toUpperCase(), "tname='" + basetablename.toUpperCase() + "'");
		CTable dtltable = hov.getDtltable();
		DBTableModel dtldm = (DBTableModel) dtltable.getModel();

		int rows[] = dtltable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			String cname = dtldm.getItemValue(row, "cname");
			String cntitle = dtldm.getItemValue(row, "cntitle");
			String coltype = dtldm.getItemValue(row, "coltype");
			if (coltype.startsWith("VARCHAR")) {
				coltype = DBColumnDisplayInfo.COLTYPE_VARCHAR;
			} else if (coltype.startsWith("DATE")) {
				coltype = DBColumnDisplayInfo.COLTYPE_DATE;
			} else {
				coltype = DBColumnDisplayInfo.COLTYPE_NUMBER;
			}
			if (cntitle.length() == 0)
				cntitle = cname;
			int newrow = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(newrow, "wfid", wfid);
			dbmodel.setItemValue(newrow, "dataitemname", cntitle);
			dbmodel.setItemValue(newrow, "datatype", coltype);
			dbmodel.setItemValue(newrow, "columnname", "{" + cname + "}");
			dbmodel.setItemValue(newrow, "comefrom", "column");
		}
		sumdbmodel.fireDatachanged();
		tableChanged();
		table.autoSize();

	}

	public String getBasetablename() {
		return basetablename;
	}

	public void setBasetablename(String basetablename) {
		this.basetablename = basetablename;
	}

	@Override
	protected String getEditablecolumns(int row) {
		if (row < 0 || row > dbmodel.getRowCount() - 1)
			return "nothing";
		String comefrom = getItemValue(row, "comefrom");
		if (comefrom.equals("column")) {
			return "dataitemname,datatype,comefrom,columnname";
		} else if (comefrom.equals("sql")) {
			return "dataitemname,datatype,comefrom,sql";
		} else if (comefrom.equals("java")) {
			return "dataitemname,datatype,comefrom,classname";
		} else {
			return super.getEditablecolumns(row);
		}
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		if (colname.equalsIgnoreCase("comefrom")) {
			bindDataSetEnable(row);
		}
		super.on_itemvaluechange(row, colname, value);
	}

	@Override
	protected void doExit() {
		if(getModifiedDbmodel().getRowCount()>0){
			warnMessage("提示","请先保存");
			return;
		}
		getParentFrame().setVisible(false);
	}

}
