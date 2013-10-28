package com.smart.sysmgr.roleop;

import java.awt.HeadlessException;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Hovdefine;
import com.smart.sysmgr.op.Op_hov;

/*功能"角色管理"细单Model*/
public class Roleop_detail extends CDetailModel{
	public Roleop_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "功能", mdemodel);
		
		/*DBColumnDisplayInfo col=getDBColumnDisplayInfo("opid");
		Hovdefine hovdefine=new Hovdefine("com.inca.sysmgr.op.Op_hov", "opid");
		hovdefine.getColpairmap().put("opid","opid");
		hovdefine.getColpairmap().put("opname","opname");
		hovdefine.setUsecontext("仅查询");
		col.setHovdefine(hovdefine);*/
	}

	public String getTablename() {
		return "np_role_op_v";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doNew() {
		//批量选择
		Op_hov hov=new Op_hov();
		DBTableModel result=hov.showDialog(this.getParentFrame(),"选择功能","","","");
		if(result==null)return;
		DBTableModel dtlmodel=this.getDBtableModel();
		CTable table=hov.getDlgtable();
		DBTableModel tablemodel=(DBTableModel) table.getModel();
		int rows[]=table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			String opid=tablemodel.getItemValue(row,"opid");
			if(isExistsopid(dtlmodel,opid))continue;
			dtlmodel.appendRow();
			int newrow=dtlmodel.getRowCount()-1;
			dtlmodel.setItemValue(newrow, "opid",opid);
			dtlmodel.setItemValue(newrow, "opname",tablemodel.getItemValue(row,"opname"));
		}
		tableChanged();
		resizeTable();

		//触发总单修改
		CSteModel mmodel=mdemodel.getMasterModel();
		if(mmodel.getDBtableModel().getdbStatus(mmodel.getRow())==RecordTrunk.DBSTATUS_SAVED){
			mmodel.getDBtableModel().setdbStatus(mmodel.getRow(), RecordTrunk.DBSTATUS_MODIFIED);
		}

	}
	
	boolean isExistsopid(DBTableModel dbmodel,String opid){
		for(int i=0;i<dbmodel.getRowCount();i++){
			if(dbmodel.getItemValue(i, "opid").equals(opid))return true;
		}
		return false;
	}
}
