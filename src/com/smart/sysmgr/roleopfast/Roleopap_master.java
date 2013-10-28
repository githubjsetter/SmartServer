package com.smart.sysmgr.roleopfast;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.sysmgr.op.Op_hov;

import java.awt.*;

/*功能"角色功能授权定义"总单Model*/
public class Roleopap_master extends CMasterModel{
	public Roleopap_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "角色", mdemodel);
	}

	public String getTablename() {
		return "np_role_op_v";
	}

	public String getSaveCommandString() {
		return null;
	}
	

	@Override
	public void doNew() {
		if(on_beforeNew()!=0){
			return;
		}
		//批量选择
		Op_hov hov=new Op_hov();
		DBTableModel result=hov.showDialog(this.getParentFrame(),"选择功能","","","");
		if(result==null)return;
		DBTableModel mstmodel=this.getDBtableModel();
		CTable table=hov.getDlgtable();
		DBTableModel tablemodel=(DBTableModel) table.getModel();
		int rows[]=table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			String opid=tablemodel.getItemValue(row,"opid");
			if(isExistsopid(mstmodel,opid))continue;
			
			super.doNew();
			int newrow=mstmodel.getRowCount()-1;
			mstmodel.setItemValue(newrow, "opid",opid);
			mstmodel.setItemValue(newrow, "opname",tablemodel.getItemValue(row,"opname"));
		}
		tableChanged();
		resizeTable();

	}
	
	boolean isExistsopid(DBTableModel dbmodel,String opid){
		for(int i=0;i<dbmodel.getRowCount();i++){
			if(dbmodel.getItemValue(i, "opid").equals(opid))return true;
		}
		return false;
	}

}
