package com.smart.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.sysmgr.op.Op_hov;
import com.smart.sysmgr.roleop.Role_hov;

public class Role_ste extends CMasterModel{
	

	public Role_ste(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTablename() {
		return "np_role_op_v";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	public void doNew() {
		if(on_beforeNew()!=0){
			return;
		}
		Roleopap_mde rmde=(Roleopap_mde)mdemodel;
		String opid=rmde.getRelateopid();
		String opname=rmde.getRelateopname();
		
		//批量选择
		Role_hov hov=new Role_hov();
		DBTableModel result=hov.showDialog(this.getParentFrame(),"选择角色","","","");
		if(result==null)return;
		String roleid=result.getItemValue(0, "roleid");
		String rolename=result.getItemValue(0, "rolename");

		DBTableModel mstmodel=this.getDBtableModel();
		if(isExistsRoleid(mstmodel,roleid))return;

			
			super.doNew();
			int newrow=mstmodel.getRowCount()-1;
			mstmodel.setItemValue(newrow, "roleid",roleid);
			mstmodel.setItemValue(newrow, "rolename",rolename);
			mstmodel.setItemValue(newrow, "opid",opid);
			mstmodel.setItemValue(newrow, "opid",opid);
			
			
		tableChanged();
		resizeTable();

	}
	
	boolean isExistsRoleid(DBTableModel dbmodel,String opid){
		for(int i=0;i<dbmodel.getRowCount();i++){
			if(dbmodel.getItemValue(i, "roleid").equals(opid))return true;
		}
		return false;
	}

}
