package com.inca.npworkflow.client;

import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class CallopHov  extends CHovBase{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo>colinfos=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;

		col=new DBColumnDisplayInfo("opname","varchar","������");
		colinfos.add(col);
		
		col=new DBColumnDisplayInfo("opcode","varchar","������");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("prodname","varchar","��Ʒ");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("modulename","varchar","ģ����");
		colinfos.add(col);
		
		col=new DBColumnDisplayInfo("opid","number","����ID");
		colinfos.add(col);
		return new DBTableModel(colinfos);
	}

	@Override
	public String getDefaultsql() {
		return "Select opid,opcode,opname,prodname,modulename from np_op";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();

		DBColumnDisplayInfo col=new DBColumnDisplayInfo("opcode","varchar","������");
		col.setUppercase(true);
		Querycondline ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("opname","varchar","������");
		ql=new Querycondline(cond,col);
		cond.add(ql);

		col=new DBColumnDisplayInfo("opid","number","����ID",true);
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("prodname","varchar","��Ʒ��",false);
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("modulename","varchar","ģ����",true);
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"opid","opcode","opname"};
	}

	public String getDesc() {
		return "ѡ����";
	}


}
