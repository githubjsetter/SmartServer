package com.smart.sysmgr.emproleop;

import org.apache.log4j.Category;

import com.smart.bi.client.design.BIReportFrame;
import com.smart.extension.ste.ReportframeGeneral;
import com.smart.extension.ste.SteframeGeneral;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MMdeFrame;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Oplauncher;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListenerAdaptor;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;
import com.smart.sysmgr.oproleap.Roleopap_mde;

public class Empopap_mfrm extends MMdeFrame{

	Category logger=Category.getInstance(Empopap_mfrm.class);
	
	public Empopap_mfrm() {
		super("人员角色功能授权设置");
	}

	@Override
	protected CMdeModel createMde() {
		Opap_frm f=new Opap_frm();
		f.pack();
		return f.getCreatedMdemodel();
	}

	@Override
	protected String getMderelatecolname() {
		return "roleid";
	}

	@Override
	protected String getStepkcolname() {
		return "roleid";
	}

	@Override
	protected CSteModel getStemodel() {
		Employeerole_frm f=new Employeerole_frm();
		f.pack();
		f.getCreatedStemodel().addActionListener(new Stelistener());
		return f.getCreatedStemodel();
	}

	class Stelistener extends CSteModelListenerAdaptor{
		@Override
		public int on_actionPerformed(String command) {
			if("设置授权".equals(command)){
				setupAp();
				return 0;
			}
			return super.on_actionPerformed(command);
		}
		
	}
	
	void setupAp(){
		int ct = 0;
		stemodel.commitEdit();
		mdemodel.getMasterModel().commitEdit();
		mdemodel.getDetailModel().commitEdit();
		ct += stemodel.getModifiedDbmodel().getRowCount();
		ct += mdemodel.getModifiedRowCount();

		if (ct != 0) {
			saveAll();
		}

		int row=stemodel.getRow();
		if(row<0){
			warnMessage("提示", "请先查询列出人员角色后再设置");
			return;
		}
		
		int mrow=mdemodel.getMasterModel().getRow();
		if(row<0){
			warnMessage("提示", "请先进行角色定义和人员授权,再查询列出人员角色后设置");
			return;
		}
		
		
		String roleid=stemodel.getItemValue(row, "roleid");
		String opid = mdemodel.getMasterModel().getItemValue(mrow, "opid");
		try {
			COpframe frm = Oplauncher.loadOp(opid);
			if (frm instanceof Steframe) {
				((Steframe) frm).setupAp(roleid);
			} else if (frm instanceof MdeFrame) {
				((MdeFrame) frm).setupAp(roleid);
			} else if (frm instanceof SteframeGeneral) {
				((SteframeGeneral) frm).setupAp(roleid);
			} else if (frm instanceof ReportframeGeneral) {
				((ReportframeGeneral) frm).setupAp(roleid);
			}else if (frm instanceof BIReportFrame) {
				((BIReportFrame) frm).setupAp(roleid);
			}
			if (frm != null) {
				frm.dispose();
			}
		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("错误", e.getMessage());
		}

		
		mdemodel.clearDetailCache();
		mdemodel.getMasterModel().setRow(mrow);

	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
		
		Empopap_mfrm f=new Empopap_mfrm();
		f.pack();
		f.setVisible(true);
	}
	
}
