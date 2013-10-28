package com.smart.bi.client.report;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;

public class Calccolumn_hov  extends CMultiHov{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("name","varchar","������");
		cols.add(col);
		col=new DBColumnDisplayInfo("desc","varchar","˵��");
		cols.add(col);
		col=new DBColumnDisplayInfo("expr","varchar","���ʽ");
		cols.add(col);
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"expr"};
	}

	public String getDesc() {
		return "����hov";
	}

	String msg[][]={
			{"������","update ���set�Ӿ�","��1=��ʾʽ1,��2=���ʽ2"},
	};
	
	@Override
	protected void doQuery() {
		DBTableModel dm=(DBTableModel) table.getModel();
		dm.clearAll();
		for(int i=0;i<msg.length;i++){
			int newrow=dm.getRowCount()-1;
			dm.appendRow();
			dm.setItemValue(newrow, "name", msg[i][0]);
			dm.setItemValue(newrow, "desc", msg[i][1]);
			dm.setItemValue(newrow, "expr", msg[i][2]);
		}
		dlgtable.tableChanged(new TableModelEvent(dlgtable.getModel()));
		dlgtable.autoSize();
		dlgtable.getSelectionModel().setSelectionInterval(0, 0);
	}

	public static void main(String[] args) {
		Calccolumn_hov hov=new Calccolumn_hov();
		hov.showDialog((Frame)null,"");
	}

	@Override
	protected boolean autoSelect() {
		return true;
	}
	
}