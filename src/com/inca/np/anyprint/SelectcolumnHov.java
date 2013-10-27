package com.inca.np.anyprint;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JSplitPane;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMdeHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class SelectcolumnHov  extends CMdeHov {

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

		col = new DBColumnDisplayInfo("coltype", "varchar", "类型");
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
		return "返回表列名. 表名.列名[,\n表名.列名]";
	}

	@Override
	protected int getSplitDirection() {
		return JSplitPane.HORIZONTAL_SPLIT;
	}

	@Override
	protected String buildDetailSelectsql(String masterv) {
		String sql = "select col.cname,col.coltype,\n" + "sys_column_cn.cntitle \n"
				+ "from col,sys_column_cn\n" + "where \n"
				+ "col.tname=sys_column_cn.tablename(+)\n"
				+ "and col.cname=sys_column_cn.colname(+)\n"
				+ "and col.tname='"+masterv.toUpperCase()+"'\n"+
				"order by col.colno";
		return sql;
	}
	
	/**
	 * 如果hov选中,返回 表名.列名[,\n表名.列名]
	 * .没选中返回null
	 * @return
	 */
	public String getTablecolumns(){
		StringBuffer sb=new StringBuffer();
		int mr=dlgtable.getRow();
		if(mr<0)return null;
		DBTableModel dbmodel=(DBTableModel)dlgtable.getModel();
		if(mr>dbmodel.getRowCount()-1)return null;
		String tname=dbmodel.getItemValue(mr, "tname");
		
		//取列
		int dtlrows[]=dtltable.getSelectedRows();
		if(dtlrows==null || dtlrows.length==0)return null;
		DBTableModel dtlmodel=(DBTableModel)dtltable.getModel();
		
		for(int i=0;i<dtlrows.length;i++){
			int dr=dtlrows[i];
			if(dr>=0 && dr<dtlmodel.getRowCount()){
				String cname=dtlmodel.getItemValue(dr, "cname");
				if(sb.length()>0){
					sb.append(",\n");
				}
				sb.append(tname+"."+cname);
			}
		}
		
		return sb.toString();
		
	}

	public static void main(String[] argv) {
		SelectcolumnHov hov = new SelectcolumnHov();
		hov.showDialog((Frame) null, "选择表列");
		String cols=hov.getTablecolumns();
		System.out.println(cols);
	}
}
