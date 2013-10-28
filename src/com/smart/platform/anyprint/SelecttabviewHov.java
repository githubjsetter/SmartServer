package com.smart.platform.anyprint;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

public class SelecttabviewHov  extends CHovBase {
	public SelecttabviewHov() throws HeadlessException {
		super();
	}

	public String getDefaultsql() {
		return "select tname,cnname from tab,sys_table_cn where tabtype in('TABLE','VIEW') and "
				+ " tab.tname = sys_table_cn.tablename(+) order by tname";
	}

	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();

		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "表、视图名", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "中文名", false);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "表、视图名", false);
		infos.add(colinfo);
		
		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "中文名", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}
	
	class Buttonhandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
		}

	}

	

	public String getDesc() {
		return "选择表、视图";
	}

	public String[] getColumns() {
		return new String[] { "tname" };
	}
}

