package com.smart.sysmgr.roleemployee;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/*����"��ɫ����"�ܵ�ϸĿModel*/
public class Roleemployee_mde extends CMdeModel{
	public Roleemployee_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Roleemployee_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Roleemployee_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "roleid";
	}
	public String getDetailRelatecolname() {
		return "roleid";
	}
	public String getSaveCommandString() {
		return "com.inca.sysmgr.roleemployee.Roleemployee_mde.������Ա��ɫ��Ȩ";
	}
	@Override
	protected int on_beforemodifymaster(int row) {
		return 0;
	}
	@Override
	protected boolean isAllownodetail() {
		return true;
	}
	
}
