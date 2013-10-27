package com.inca.npworkflow.client;

import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class SelectaTablecolumnHov extends CHovBase{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("cname","varchar","����");
		cols.add(col);

		col=new DBColumnDisplayInfo("cntitle","varchar","��������");
		cols.add(col);

		DBTableModel db=new DBTableModel(cols);
		return db;
	}

	@Override
	public String getDefaultsql() {
		String sql = "select col.cname,col.coltype,\n" + "sys_column_cn.cntitle \n"
		+ "from col,sys_column_cn\n" + "where \n"
		+ "col.tname=sys_column_cn.tablename(+)\n"
		+ "and col.cname=sys_column_cn.colname(+)\n"+
		"order by col.colno";
		return sql;
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();

		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("cname","varchar","����");
		col.setUppercase(true);
		Querycondline ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"cname"};
	}

	public String getDesc() {
		return "����һ������е�HOV";
	}

	@Override
	protected boolean autoSelect() {
		return true;
	}

}
