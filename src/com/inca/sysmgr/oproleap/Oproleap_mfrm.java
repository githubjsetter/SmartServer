package com.inca.sysmgr.oproleap;

import org.apache.log4j.Category;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.runop.Oplauncher;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.CSteModelListenerAdaptor;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;
import com.inca.npbi.client.design.BIReportFrame;
import com.inca.npx.ste.ReportframeGeneral;
import com.inca.npx.ste.SteframeGeneral;

public class Oproleap_mfrm extends MMdeFrame {

	Category logger = Category.getInstance(Oproleap_mfrm.class);

	public Oproleap_mfrm() {
		super("功能角色授权");
	}

	@Override
	protected CMdeModel createMde() {
		Roleopap_frm f = new Roleopap_frm();
		f.pack();
		return f.getCreatedMdemodel();
	}

	@Override
	protected String getMderelatecolname() {
		return "opid";
	}

	@Override
	protected String getStepkcolname() {
		return "opid";
	}

	@Override
	protected CSteModel getStemodel() {
		Op_frm f = new Op_frm();
		f.pack();
		f.getCreatedStemodel().addActionListener(new Stelistener());
		return f.getCreatedStemodel();
	}

	class Stelistener extends CSteModelListenerAdaptor {

		@Override
		public void on_click(int row, int col) {
			super.on_click(row, col);
			if (row >= 0) {
				String opid = stemodel.getItemValue(row, "opid");
				String opname = stemodel.getItemValue(row, "opname");
				Roleopap_mde rmde = (Roleopap_mde) mdemodel;
				rmde.setRelateopid(opid);
				rmde.setRelateopname(opname);
			}
		}

		@Override
		public int on_actionPerformed(String command) {
			if ("设置授权".equals(command)) {
				setupAp();
				return 0;
			}
			return super.on_actionPerformed(command);
		}

	}

	void setupAp() {
		int ct = 0;
		stemodel.commitEdit();
		mdemodel.getMasterModel().commitEdit();
		mdemodel.getDetailModel().commitEdit();
		ct += stemodel.getModifiedDbmodel().getRowCount();
		ct += mdemodel.getModifiedRowCount();

		if (ct != 0) {
			saveAll();
		}

		int row = stemodel.getRow();
		if (row < 0) {
			warnMessage("提示", "请先查询列出功能后再设置授权.");
			return;
		}

		int mrow = mdemodel.getMasterModel().getRow();
		if (row < 0) {
			warnMessage("提示", "请先查询列出功能,并查询或新增角色后再授权");
			return;
		}

		String opid = stemodel.getItemValue(row, "opid");
		String opclassname = stemodel.getItemValue(row, "classname");
		String roleid = mdemodel.getMasterModel().getItemValue(mrow, "roleid");

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
			} else if (frm instanceof BIReportFrame) {
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

	public static void main(String[] argv) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Oproleap_mfrm w = new Oproleap_mfrm();
		w.pack();
		w.setVisible(true);

	}
}
