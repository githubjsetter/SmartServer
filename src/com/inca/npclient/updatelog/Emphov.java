package com.inca.npclient.updatelog;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Emphov   extends CHovBase {
	public Emphov() throws HeadlessException {
		super();
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo editor = null;

		editor = new DBColumnDisplayInfo("employeeid", "number", "��ԱID", true);
		editor.setIspk(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("employeename", "varchar", "��Ա����",
				false);
		tablecolumndisplayinfos.add(editor);
		

		editor = new DBColumnDisplayInfo("deptid", "number", "����ID", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("opcode", "varchar", "��Ա������", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		
		editor = new DBColumnDisplayInfo("pinyin", "varchar", "��Աƴ��", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);

		return new DBTableModel(tablecolumndisplayinfos);
	}

	@Override
	public String getDefaultsql() {
		return "select * from pub_employee";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "��Ա������", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "��Ա����",
				false);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "��ԱID", true);
		colinfo.setIspk(true);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	public String[] getColumns() {
		return new String[] { "employeeid", "opcode", "employeename", "pinyin",
				 "deptid" };
	}

	public String getDesc() {
		return "ѡ����Ա(ȫ��)";
	}
	
/*	JCheckBox stopbox= new JCheckBox();
	@Override
	protected JPanel buildQuerypanel(Querycond arg0) {
		JPanel jp=super.buildQuerypanel(arg0);
		jp.add(stopbox);
		jp.add(new JLabel("��ͣ��"));
		//stopbox = new JCheckBox();
		return jp;
	}
*/}
