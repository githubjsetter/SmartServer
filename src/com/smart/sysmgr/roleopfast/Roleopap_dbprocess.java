package com.smart.sysmgr.roleopfast;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
/*����"��ɫ������Ȩ����"Ӧ�÷���������*/
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
