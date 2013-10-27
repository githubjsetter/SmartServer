package com.inca.sysmgr.roleopfast;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.control.CFrame;

/*功能"角色功能授权定义"总单细目Model*/
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
		return "Roleopap_mde.保管功能授权属性";
	}
	
	@Override
	protected boolean isAllownodetail() {
		return true;
	}
	
	
}
