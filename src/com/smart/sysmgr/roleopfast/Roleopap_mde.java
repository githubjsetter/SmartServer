package com.smart.sysmgr.roleopfast;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/*����"��ɫ������Ȩ����"�ܵ�ϸĿModel*/
public class Roleopap_mde extends CMdeModel{
	public Roleopap_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Roleopap_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Roleopap_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "roleopid";
	}
	public String getDetailRelatecolname() {
		return "roleopid";
	}
	public String getSaveCommandString() {
		return "Roleopap_mde.���ܹ�����Ȩ����";
	}
	
	@Override
	protected boolean isAllownodetail() {
		return true;
	}
	
	
}
