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

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "����ͼ��", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "������", false);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("tname", "varchar", "����ͼ��", false);
		infos.add(colinfo);
		
		colinfo = new DBColumnDisplayInfo("cnname", "varchar", "������", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}
	
	class Buttonhandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
		}

	}

	

	public String getDesc() {
		return "ѡ�����ͼ";
	}

	public String[] getColumns() {
		return new String[] { "tname" };
	}
}

