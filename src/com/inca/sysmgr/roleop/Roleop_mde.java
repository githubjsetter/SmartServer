package com.inca.sysmgr.roleop;


import org.apache.log4j.Category;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.runop.Oplauncher;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.npbi.client.design.BIReportFrame;
import com.inca.npx.mde.CMdeModelAp;
import com.inca.npx.ste.ReportframeGeneral;
import com.inca.npx.ste.SteframeGeneral;

/*����"��ɫ����"�ܵ�ϸĿModel*/
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
		return "com.inca.sysmgr.roleop.Roleop_mde.�����ɫ����";
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
			warnMessage("��ʾ", "��ѡһ����ɫ");
			return;
		}
		if (dmodel.getRow() < 0) {
			warnMessage("��ʾ", "��ѡһ������");
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
			errorMessage("����", e.getMessage());
		}

	}

}