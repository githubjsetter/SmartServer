package com.inca.np.gui.design;

import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMultiHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Selecthovmhov  extends CMultiHov{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("hovname","varchar","HOV����");
		cols.add(col);
		col=new DBColumnDisplayInfo("classname","varchar","HOV����");
		cols.add(col);
		col=new DBColumnDisplayInfo("prodname","varchar","��Ʒ��");
		cols.add(col);
		col=new DBColumnDisplayInfo("modulename","varchar","ģ����");
		cols.add(col);

		col=new DBColumnDisplayInfo("hovid","number","HOVID");
		cols.add(col);

		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "select * from np_hov order by hovname";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		Querycondline ql;
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("hovname","varchar","HOV����");
		ql=new Querycondline(cond,col);
		cond.add(ql);

		col=new DBColumnDisplayInfo("classname","varchar","HOV����");
		ql=new Querycondline(cond,col);
		cond.add(ql);

		col=new DBColumnDisplayInfo("hovid","number","HOVID");
		ql=new Querycondline(cond,col);
		cond.add(ql);

		return cond;
	}

	public String[] getColumns() {
		return new String[]{"hovid","hovname","classname","prodname","modulename"};
	}

	public String getDesc() {
		return "ѡ��HOV��HOV";
	}

}
