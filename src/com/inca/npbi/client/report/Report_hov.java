package com.inca.npbi.client.report;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Report_hov extends CHovBase{
	

	public Report_hov() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("reportid","number","����ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("reportno","varchar","�������");
		cols.add(col);
		col=new DBColumnDisplayInfo("reportname","varchar","��������");
		cols.add(col);
		
		
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "select reportid,reportno,reportname from npbi_report_def" +
				" order by reportno,reportid";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		Querycondline ql=null;
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("reportno","varchar","�������");
		col.setUppercase(true);
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("reportname","varchar","��������");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("reportid","number","����ID");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"reportid","reportno","reportname"};
	}

	public String getDesc() {
		return "NPBI����HOV";
	}

}
