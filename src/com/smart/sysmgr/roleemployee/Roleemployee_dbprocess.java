package com.smart.sysmgr.roleemployee;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
/*功能"角色管理"应用服务器处理*/
public class Roleemployee_dbprocess extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Roleemployee_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_role";
	}
	protected String getDetailtablename() {
		return "np_employee_role";
	}
}
