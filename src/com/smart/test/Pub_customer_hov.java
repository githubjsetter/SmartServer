package com.smart.test;


import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.util.DefaultNPParam;

public class Pub_customer_hov  extends CHovBase {
	public Pub_customer_hov() throws HeadlessException {
		super();
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo editor = null;
		editor = new DBColumnDisplayInfo("customid", "number", "�ͻ�ID", true);
		editor.setIspk(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("customname", "varchar", "�ͻ�����", false);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("customopcode", "varchar", "�ͻ�������",
				false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("customno", "varchar", "�ͻ��������", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("custompinyin", "varchar", "�ͻ�ƴ��",
				false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("corpcode", "varchar", "��֯����", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("usestatus", "number", "״̬", false);
		editor.setEditcomptype("combobox");
		editor.setSystemddl("PUB_CUSTOM_USESTATUS");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("defaultinvoicetype", "number",
				"ȱʡ��Ʊ����", false);
		editor.setEditcomptype("combobox");
		editor.setSystemddl("PUB_INVTYPE");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("delivermethod", "number", "�ͻ�����",
				true);
		editor.setEditcomptype("combobox");
		editor.setSystemddl("PUB_CUSTOM_DELIVERMODE");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("defaulttranmethodid", "number",
				"���䷽ʽ", true);
		editor.setEditcomptype("combobox");
		editor.setSystemddl("BMS_TRANSPORT_METHOD");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("tranpriority", "number", "���伶��", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("fmopcode", "varchar", "��Ҳ�����", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("fmname", "varchar", "���", false);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("fmrate", "number", "����", true);
		editor.setNumberscale(4);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("fmid", "number", "���ID", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("creditflag", "number", "�Ƿ�������ö��",
				true);
		editor.setEditcomptype("checkbox");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("credit", "number", "���ö��", true);
		editor.setNumberscale(2);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("recmoney", "number", "Ƿ����", true);
		editor.setNumberscale(2);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("creditdaysflag", "number",
				"�Ƿ������������", true);
		editor.setEditcomptype("checkbox");
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("creditdays", "number", "��������", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("recdate", "date", "����Ƿ������", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("lowpriceflag", "number", "�Ƿ��������޼�",
				true);
		editor.setEditcomptype("checkbox");
		tablecolumndisplayinfos.add(editor);
		return new DBTableModel(tablecolumndisplayinfos);
	}


	@Override
	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();
		DBColumnDisplayInfo colinfo = null;
		colinfo = new DBColumnDisplayInfo("customopcode", "varchar", "�ͻ�������",
				false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));
		colinfo = new DBColumnDisplayInfo("customname", "varchar", "�ͻ�����",
				false);
		querycond.add(new Querycondline(querycond, colinfo));
		colinfo = new DBColumnDisplayInfo("customid", "number", "�ͻ�ID", true);
		colinfo.setIspk(true);
		querycond.add(new Querycondline(querycond, colinfo));
		return querycond;
	}

	public String[] getColumns() {
		return new String[] { "customid", "customopcode", "customname",
				"custompinyin", "customno", "corpcode", "creditflag", "credit",
				"recmoney", "creditdaysflag", "creditdays", "recdate",
				"lowpriceflag", "defaultinvoicetype", "delivermethod",
				"defaulttranmethodid", "tranpriority", "fmopcode", "fmname",
				"fmid", "fmrate" };
	}

	public String getDesc() {
		return "ѡ��ͻ�";
	}

	JCheckBox stopbox = new JCheckBox();

	@Override
	protected JPanel buildQuerypanel(Querycond arg0) {
		JPanel jp = super.buildQuerypanel(arg0);
		jp.add(stopbox);
		jp.add(new JLabel("��ͣ��"));
		// stopbox = new JCheckBox();
		return jp;
	}
	
	

	@Override
	public String getDefaultsql() {
		return "select * from pub_customer_v ";
	}

	@Override
	protected String getOtherwheres() {
		String wheres=super.getOtherwheres();
		if(wheres.length()>0){
			wheres +=" and ";
		}
		if (stopbox.isSelected()) {
			wheres+=" usestatus in (0,1)";
		}else{
			wheres+=" usestatus =1";
		}
		return wheres;

	}


	
	public static void main(String args[]) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Pub_customer_hov hov = new Pub_customer_hov();
		hov.showDialog(null, "");
	}
}
