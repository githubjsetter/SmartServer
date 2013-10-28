package com.smart.platform.gui.design;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;

/**
 * ѡ����
 * @author Administrator
 *
 */
public class SelectcolHov extends CMultiHov{

	DBTableModel colsdbmodel=null;
	public SelectcolHov(DBTableModel colsdbmodel) {
		super();
		this.colsdbmodel=colsdbmodel;
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		col=new DBColumnDisplayInfo("cname","varchar","����");
		cols.add(col);
		col=new DBColumnDisplayInfo("coltype","varchar","����");
		cols.add(col);
		col=new DBColumnDisplayInfo("cntitle","varchar","������");
		cols.add(col);
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "";
	}

	@Override
	public Querycond getQuerycond() {
		// TODO Auto-generated method stub
		return new Querycond();
	}

	public String[] getColumns() {
		// TODO Auto-generated method stub
		return new String[0];
	}

	public String getDesc() {
		return "";
	}

	@Override
	protected void doQuery() {
		this.dlgdbmodel.bindMemds(colsdbmodel);
		dlgtable.tableChanged(new TableModelEvent(dlgtable.getModel()));
		dlgtable.autoSize();
	}
	
}
