package com.smart.adminclient.fullscan;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/*功能"查询fullscan"总单细目Model*/
public class Fullscan_mde extends CMdeModel{
	public Fullscan_mde(CFrame frame, String title) {
		super(frame, title);
	}
	protected CMasterModel createMastermodel() {
		return new Fullscan_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Fullscan_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "sql_text";
	}
	public String getDetailRelatecolname() {
		return "sql_text";
	}
	public String getSaveCommandString() {
		return "查询fullscan";
	}
}
