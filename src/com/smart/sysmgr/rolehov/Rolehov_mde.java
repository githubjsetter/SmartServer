package com.smart.sysmgr.rolehov;

import java.util.HashMap;
import java.util.Vector;

import com.smart.extension.ap.Aphelper;
import com.smart.extension.ste.ApIF;
import com.smart.extension.ste.Apinfo;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.Sumdbmodel;
import com.smart.platform.gui.design.Selecthovmhov;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.sysmgr.hov.HovapDlg;
import com.smart.sysmgr.roleop.Role_hov;

public class Rolehov_mde  extends CMdeModel{
	HashMap<String,Apinfo> apinfomap=null;

	public Rolehov_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Rolehov_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Rolehov_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "roleid";
	}
	public String getDetailRelatecolname() {
		return "roleid";
	}
	public String getSaveCommandString() {
		return "Rolehov_mde.保存hov授权";
	}
	@Override
	protected int on_beforemodifymaster(int row) {
		return -1;
	}
	@Override
	protected boolean isAllownodetail() {
		return true;
	}

	@Override
	protected int on_actionPerformed(String command) {
		if("增加HOV".equals(command)){
			addHov();
			return 0;
		}
		return super.on_actionPerformed(command);
	}
	/*
	void setupDtlhovap(){
		int r=getDetailModel().getRow();
		if(r<0){
			warnMessage("提示","先查询角色并新增HOV,再双击细目HOV或再点本按钮");
			return;
		}
		String hovid=getDetailModel().getItemValue(r, "hovid");
		String classname=getDetailModel().getItemValue(r, "classname");
		setHovap(hovid,classname);
	}
	*/

	public void addHov(){
		// 批量选择
		Selecthovmhov hov = new Selecthovmhov();
		DBTableModel result = hov.showDialog(this.getParentFrame(), "选择HOV");
		if (result == null) {
			return;
		}
		DBTableModel dtlmodel = getDetailModel().getDBtableModel();
		CTable table = hov.getDlgtable();
		DBTableModel tablemodel = (DBTableModel) table.getModel();
		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			String hovname = tablemodel.getItemValue(row, "hovname");
			if (isExistshovname(dtlmodel, hovname))
				continue;
			dtlmodel.appendRow();
			int newrow = dtlmodel.getRowCount() - 1;
			dtlmodel.setItemValue(newrow, "hovname", hovname);
			dtlmodel.setItemValue(newrow, "classname", tablemodel.getItemValue(
					row, "classname"));
			dtlmodel.setItemValue(newrow, "prodname", tablemodel.getItemValue(
					row, "prodname"));
			dtlmodel.setItemValue(newrow, "modulename", tablemodel
					.getItemValue(row, "modulename"));
			dtlmodel.setItemValue(newrow, "hovid", tablemodel.getItemValue(row,
					"hovid"));
			dtlmodel.setItemValue(newrow,"aptype","data");
			dtlmodel.setItemValue(newrow,"apname","wheres");
		}
		getDetailModel().getSumdbmodel().fireDatachanged();
		getDetailModel().tableChanged();
		getDetailModel().getTable().autoSize();
		CSteModel mmodel = getMasterModel();
		if (mmodel.getDBtableModel().getdbStatus(mmodel.getRow()) == RecordTrunk.DBSTATUS_SAVED) {
			mmodel.getDBtableModel().setdbStatus(mmodel.getRow(),
					RecordTrunk.DBSTATUS_MODIFIED);
		}
	}

	boolean isExistshovname(DBTableModel dbmodel, String hovname) {
		for (int i = 0; i < dbmodel.getRowCount(); i++) {
			if (dbmodel.getItemValue(i, "hovname").equals(hovname))
				return true;
		}
		return false;
	}
/*	public void setHovap(String hovid,String hovclassname) {
		int mrow=getMasterModel().getRow();
		String roleid=getMasterModel().getItemValue(mrow, "roleid");
	
		
		try{
			StringBuffer hovidsb=new StringBuffer();
			apinfomap=Aphelper.downloadHovAp(hovclassname,roleid,hovidsb);
		}catch(Exception e){
			errorMessage("错误",e.getMessage());
			return;
		}
		
		//弹出窗口输入条件
		HovapDlg setupdlg=new HovapDlg(this.getParentFrame(),"设置HOV授权属性",new ApifImpl());
		setupdlg.pack();
		setupdlg.setVisible(true);
		if(!setupdlg.getOk()){
			return;
		}
	

	}
	class ApifImpl implements ApIF{

		public Apinfo getApinfo(String apname) {
			return apinfomap.get(apname);
		}

		public String getApvalue(String apname) {
			Apinfo info=getApinfo(apname);
			if(info==null)return "";
			return info.getApvalue();
		}

		public String getAutoprintplan() {
			// TODO Auto-generated method stub
			return null;
		}

		public Vector<Apinfo> getParamapinfos() {
			// TODO Auto-generated method stub
			return null;
		}

		public Vector<String> getPrintplans() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isDevelopCandelete() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isDevelopCanmodify() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isDevelopCannew() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isDevelopCanquery() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isDevelopCansave() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setAutoprintplan(String planname) {
			// TODO Auto-generated method stub
			
		}

		public void setPrintplans(Vector<String> plans) {
			// TODO Auto-generated method stub
			
		}
		
	}
*/	
}
