package com.inca.sysmgr.roleopfast;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.control.CFrame;

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
