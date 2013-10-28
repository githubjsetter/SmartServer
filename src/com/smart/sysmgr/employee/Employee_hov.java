package com.smart.sysmgr.employee;

import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

public class Employee_hov extends CMultiHov{

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo>colinfos=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("employeename","varchar","姓名");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("opcode","varchar","操作码");
		colinfos.add(col);

		col=new DBColumnDisplayInfo("deptname","varchar","部门");
		colinfos.add(col);
		
		col=new DBColumnDisplayInfo("employeeid","number","人员ID");
		colinfos.add(col);
		col=new DBColumnDisplayInfo("deptid","number","部门ID");
		colinfos.add(col);

		return new DBTableModel(colinfos);
	}

	@Override
	public String getDefaultsql() {
		return "Select employeeid,opcode,employeename,deptid,deptname from pub_employee_v where " +
				" nvl(usestatus,0)=1";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();

		DBColumnDisplayInfo col=new DBColumnDisplayInfo("opcode","varchar","操作码");
		col.setUppercase(true);
		Querycondline ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("employeename","varchar","姓名",true);
		ql=new Querycondline(cond,col);
		cond.add(ql);

		col=new DBColumnDisplayInfo("employeeid","number","人员ID");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("deptid","number","部门ID");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"employeeid","opcode","employeename"};
	}

	public String getDesc() {
		return "选择人员(多选)";
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
