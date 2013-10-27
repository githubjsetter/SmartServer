package com.inca.np.logger;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.control.CFrame;

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
