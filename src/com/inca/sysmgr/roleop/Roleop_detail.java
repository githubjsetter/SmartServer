package com.inca.sysmgr.roleop;

import java.awt.HeadlessException;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.sysmgr.op.Op_hov;

/*����"��ɫ����"ϸ��Model*/
public class Roleop_detail extends CDetailModel{
	public Roleop_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "����", mdemodel);
		
		/*DBColumnDisplayInfo col=getDBColumnDisplayInfo("opid");
		Hovdefine hovdefine=new Hovdefine("com.inca.sysmgr.op.Op_hov", "opid");
		hovdefine.getColpairmap().put("opid","opid");
		hovdefine.getColpairmap().put("opname","opname");
		hovdefine.setUsecontext("����ѯ");
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
		//����ѡ��
		Op_hov hov=new Op_hov();
		DBTableModel result=hov.showDialog(this.getParentFrame(),"ѡ����","","","");
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

		//�����ܵ��޸�
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
