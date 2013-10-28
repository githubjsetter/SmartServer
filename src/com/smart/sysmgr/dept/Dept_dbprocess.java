package com.smart.sysmgr.dept;
import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"部门管理"应用服务器处理*/
public class Dept_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Dept_ste(null);
	}
	protected String getTablename() {
		return "pub_company";
	}
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_beforesave(con, userrininfo, dbmodel, row);
		String companyopcode=dbmodel.getItemValue(row, "companyopcode");
		if(companyopcode==null || companyopcode.length()==0){
			throw new Exception("操作码必须输入");
		}
		dbmodel.setItemValue(row, "companypinyin",companyopcode);
		dbmodel.setItemValue(row, "companyno",companyopcode);
		dbmodel.setItemValue(row, "REFERENCEDCOUNT","0");
	}
	
	
}
