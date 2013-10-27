package com.inca.npworkflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class SelecttableHov  extends CHovBase {
	public SelecttableHov() throws HeadlessException {
		super();
	}

	public String getDefaultsql() {
		return "select tname,cnname from tab,sys_table_cn where tabtype='TABLE' and "
				+ " tab.tname = sys_table_cn.tablename(+) order by tname";
	}

	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();

		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "表名", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "中文名", false);
		querycond.add(new Querycondline(querycond, colinfo));
		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "表名", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "中文名", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "选择表";
	}

	public String[] getColumns() {
		return new String[] { "tname" };
	}

}
