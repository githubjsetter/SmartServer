package com.smart.platform.gui.design;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

/**
 * –Ú¡–∫≈HOV
 * @author Administrator
 *
 */
public class Seqnamehov extends CHovBase{

	public Seqnamehov() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("sequence_name","varchar","–Ú¡–∫≈");
		col.setUppercase(true);
		cols.add(col);
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		String sql="select sequence_name from seq order by sequence_name";
		return sql;
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("sequence_name","varchar","–Ú¡–∫≈");
		col.setUppercase(true);
		Querycondline ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		// TODO Auto-generated method stub
		return new String[]{"sequence_name"};
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return "—°‘Ò–Ú¡–∫≈";
	}

}
