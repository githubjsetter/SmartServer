package com.inca.sysmgr.roleemployee;

import java.awt.HeadlessException;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.sysmgr.employee.Employee_hov;

/*功能"角色管理"细单Model*/
public class Roleemployee_detail extends CDetailModel{
	public Roleemployee_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "人员", mdemodel);
	}

	public String getTablename() {
		return "np_employee_role_v";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doNew() {
		//批量选择
		Employee_hov hov=new Employee_hov();
		DBTableModel result=hov.showDialog(this.getParentFrame(),"选择人员");
		if(result==null){
			return;
		}
		DBTableModel dtlmodel=this.getDBtableModel();
		CTable table=hov.getDlgtable();
		DBTableModel tablemodel=(DBTableModel) table.getModel();
		int rows[]=table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			String employeeid=tablemodel.getItemValue(row,"employeeid");
			if(isExistsempid(dtlmodel,employeeid))continue;
			dtlmodel.appendRow();
			int newrow=dtlmodel.getRowCount()-1;
			dtlmodel.setItemValue(newrow, "employeeid",employeeid);
			dtlmodel.setItemValue(newrow, "employeename",tablemodel.getItemValue(row,"employeename"));
			dtlmodel.setItemValue(newrow, "opcode",tablemodel.getItemValue(row,"opcode"));
			dtlmodel.setItemValue(newrow, "deptid",tablemodel.getItemValue(row,"deptid"));
			dtlmodel.setItemValue(newrow, "deptname",tablemodel.getItemValue(row,"deptname"));
		}
		tableChanged();
		resizeTable();
		
		//触发总单修改
		CSteModel mmodel=mdemodel.getMasterModel();
		if(mmodel.getDBtableModel().getdbStatus(mmodel.getRow())==RecordTrunk.DBSTATUS_SAVED){
			mmodel.getDBtableModel().setdbStatus(mmodel.getRow(), RecordTrunk.DBSTATUS_MODIFIED);
		}
	}

	boolean isExistsempid(DBTableModel dbmodel,String employeeid){
		for(int i=0;i<dbmodel.getRowCount();i++){
			if(dbmodel.getItemValue(i, "employeeid").equals(employeeid))return true;
		}
		return false;
	}

	
}
