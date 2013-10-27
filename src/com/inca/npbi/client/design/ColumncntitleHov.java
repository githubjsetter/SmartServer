package com.inca.npbi.client.design;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMdeHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;


public class ColumncntitleHov  extends CMdeHov{


	@Override
	protected DBTableModel createDetailTablemodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("cname", "varchar", "列名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("cntitle", "varchar", "中文名");
		col.setReadonly(true);
		cols.add(col);

		return new DBTableModel(cols);
	}

	@Override
	protected String getDetailcolname() {
		return "tname";
	}

	@Override
	protected String getDetailtablename() {
		return "col";
	}

	@Override
	protected String getMastercolname() {
		return "tname";
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("tname", "varchar", "表名");
		col.setUppercase(true);
		cols.add(col);
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "select tname from tab";
	}

	
	@Override
	protected int getTableselectionmode() {
		return ListSelectionModel.SINGLE_SELECTION;
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond = new Querycond();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("tname", "varchar", "表名");
		col.setUppercase(true);
		Querycondline ql = new Querycondline(cond, col);
		cond.add(ql);
		return cond;
	}

	public String[] getColumns() {
		return new String[] { "cname" };
	}

	public String getDesc() {
		return "返回表表名";
	}

	@Override
	protected String buildDetailSelectsql(String masterv) {
		String sql = "select col.cname,\n" + "sys_column_cn.cntitle \n"
				+ "from col,sys_column_cn\n" + "where \n"
				+ "col.tname=sys_column_cn.tablename(+)\n"
				+ "and col.cname=sys_column_cn.colname(+)\n"
				+ "and col.tname='"+masterv.toUpperCase()+"'\n"+
				"order by col.colno";
		return sql;
	}

	public static void main(String[] argv) {
		ColumncntitleHov hov = new ColumncntitleHov();
		DBTableModel rs=hov.showDialog((Frame) null, "选择表");
		if(rs!=null){
			System.out.println(rs.getItemValue(0, "tname"));
		}
	}
}
