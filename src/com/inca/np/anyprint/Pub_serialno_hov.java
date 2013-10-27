package com.inca.np.anyprint;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Pub_serialno_hov  extends CHovBase {
	public Pub_serialno_hov() throws HeadlessException {
		super();
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo editor = null;

		editor = new DBColumnDisplayInfo("serialnoname", "varchar", "序列号名", false);
		tablecolumndisplayinfos.add(editor);


		editor = new DBColumnDisplayInfo("serialnoid", "number", "序列号ID", false);
		tablecolumndisplayinfos.add(editor);

		return new DBTableModel(tablecolumndisplayinfos);
	}

	@Override
	public String getDefaultsql() {
		return "select serialnoid,serialnoname from pub_serialno ";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("serialnoname", "varchar", "序列号名", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("serialnoid", "number", "序列号ID", false);
		colinfo.setIspk(true);
		querycond.add(new Querycondline(querycond, colinfo));


		return querycond;
	}

	public String[] getColumns() {
		// TODO Auto-generated method stub
		return new String[] { "serialnoid", "serialnoname" };
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return "选择外部序列号";
	}

	@Override
	protected boolean autoSelect() {
		//自动查询
		return true;
	}

}
