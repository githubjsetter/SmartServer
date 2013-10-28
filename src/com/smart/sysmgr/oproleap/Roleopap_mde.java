package com.smart.sysmgr.oproleap;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

public class Roleopap_mde extends CMdeModel{

	String relateopid="";
	String relateopname="";
	
	public Roleopap_mde(CFrame frame, String title) {
		super(frame, title);
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Opap_ste(getParentFrame(),"��Ȩ",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Role_ste(getParentFrame(),"��ɫ",this);
	}

	@Override
	public String getDetailRelatecolname() {
		return "roleopid";
	}

	@Override
	public String getMasterRelatecolname() {
		return "roleopid";
	}

	@Override
	public String getSaveCommandString() {
		return "oproleap.�����ɫ������Ȩ����";
	}

	public String getRelateopid() {
		return relateopid;
	}

	public void setRelateopid(String relateopid) {
		this.relateopid = relateopid;
	}

	public String getRelateopname() {
		return relateopname;
	}

	public void setRelateopname(String relateopname) {
		this.relateopname = relateopname;
	}

	@Override
	protected boolean isAllownodetail() {
		return true;
	}

	
}
