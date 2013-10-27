package com.inca.sysmgr.roleop;

import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.util.DefaultNPParam;

/**
 * ��ѡ��ɫ
 * @author Administrator
 *
 */
public class Role_hov  extends CHovBase{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo>colinfos=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		
		col=new DBColumnDisplayInfo("rolename","varchar","��ɫ");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("opcode","varchar","������");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("roleid","varchar","��ɫID");
		colinfos.add(col);
		

		return new DBTableModel(colinfos);
	}

	@Override
	public String getDefaultsql() {
		return "select roleid,opcode,rolename from np_role ";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();

		DBColumnDisplayInfo col=new DBColumnDisplayInfo("opcode","varchar","������");
		col.setUppercase(true);
		Querycondline ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("rolename","varchar","��ɫ");
		ql=new Querycondline(cond,col);
		cond.add(ql);

		col=new DBColumnDisplayInfo("roleid","number","��ɫID");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"roleid","rolename","opcode"};
	}

	public String getDesc() {
		return "ѡ���ɫ";
	}
/*
	@Override
	protected String getCondcolname(String invokecolname) {
		if(invokecolname.equals("companyname")){
			return "companyopcode";
		}
		return super.getCondcolname(invokecolname);
	}

*/	
}
