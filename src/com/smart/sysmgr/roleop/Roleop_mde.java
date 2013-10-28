package com.smart.sysmgr.roleop;


import org.apache.log4j.Category;

import com.smart.bi.client.design.BIReportFrame;
import com.smart.extension.mde.CMdeModelAp;
import com.smart.extension.ste.ReportframeGeneral;
import com.smart.extension.ste.SteframeGeneral;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Oplauncher;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;

/*功能"角色管理"总单细目Model*/
public class Roleop_mde extends CMdeModelAp {
	Category logger = Category.getInstance(Roleop_mde.class);

	public Roleop_mde(CFrame frame, String title) {
		super(frame, title);
	}

	protected CMasterModel createMastermodel() {
		return new Roleop_master(frame, this);
	}

	protected CDetailModel createDetailmodel() {
		return new Roleop_detail(frame, this);
	}

	public String getMasterRelatecolname() {
		return "roleid";
	}

	public String getDetailRelatecolname() {
		return "roleid";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.roleop.Roleop_mde.保存角色功能";
	}

	@Override
	protected int on_beforemodifymaster(int row) {
		return 0;
	}

	@Override
	protected boolean isAllownodetail() {
		return true;
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("setupap")) {
			onsetAp();
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	protected void onsetAp() {
		CSteModel mmodel = getMasterModel();
		CSteModel dmodel = getDetailModel();
		String roleid, opclassname;
		if (mmodel.getRow() < 0) {
			warnMessage("提示", "请选一个角色");
			return;
		}
		if (dmodel.getRow() < 0) {
			warnMessage("提示", "请选一个功能");
			return;
		}
		roleid = mmodel.getItemValue(mmodel.getRow(), "roleid");
		opclassname = dmodel.getItemValue(dmodel.getRow(), "classname");
		String opid = dmodel.getItemValue(dmodel.getRow(), "opid");

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

	}

}
