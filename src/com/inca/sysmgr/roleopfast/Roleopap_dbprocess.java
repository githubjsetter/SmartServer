package com.inca.sysmgr.roleopfast;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.gui.mde.CMdeModel;
/*功能"角色功能授权定义"应用服务器处理*/
public class Roleopap_dbprocess extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Roleopap_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_role_op";
	}
	protected String getDetailtablename() {
		return "np_op_ap";
	}
}
