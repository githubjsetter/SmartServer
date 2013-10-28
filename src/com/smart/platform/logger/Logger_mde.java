package com.smart.platform.logger;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/*����"��ѯ������������־"�ܵ�ϸĿModel*/
public class Logger_mde extends CMdeModel{
	public Logger_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Logger_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Logger_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "seqid";
	}
	public String getDetailRelatecolname() {
		return "seqid";
	}
	public String getSaveCommandString() {
		return "���������������־";
	}
}
