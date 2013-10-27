package com.inca.sysmgr.roleopfast;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.CSteModelListenerAdaptor;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SendHelper;
import com.inca.npx.ste.Apinfo;
import com.inca.npx.ste.ApinfoDbmodel;

public class Roleopap_mframe extends MMdeFrame {
	Category logger = Category.getInstance(Roleopap_mframe.class);

	@Override
	protected CMdeModel createMde() {
		Roleopap_frame f = new Roleopap_frame();
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
		Role_frm f = new Role_frm();
		f.pack();
		f.getCreatedStemodel().addActionListener(new Stelistener());
		return f.getCreatedStemodel();
	}

	class Stelistener extends CSteModelListenerAdaptor {

		@Override
		public int on_actionPerformed(String command) {
			if ("����������Ȩ".equals(command)) {
				batchsetupAp();
				return 0;
			}
			return super.on_actionPerformed(command);
		}

	}

	/**
	 * ����������Ȩ.
	 */
	void batchsetupAp() {
		int ct = 0;
		stemodel.commitEdit();
		mdemodel.getMasterModel().commitEdit();
		mdemodel.getDetailModel().commitEdit();
		ct += stemodel.getModifiedDbmodel().getRowCount();
		ct += mdemodel.getModifiedRowCount();

		if (ct != 0) {
			saveAll();
		}

		int mrow = stemodel.getRow();
		if (mrow < 0) {
			warnMessage("��ʾ", "ѡ���ܺ�������(��Ctrl��Shift��ѡ");
			return;
		}
		String roleid = stemodel.getItemValue(mrow, "roleid");

		int rows[] = mdemodel.getMasterModel().getTable().getSelectedRows();
		if (rows.length == 0) {
			warnMessage("��ʾ", "ѡ���ܺ�������(��Ctrl��Shift��ѡ");
			return;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rows.length; i++) {
			int r = rows[i];
			String opid = mdemodel.getMasterModel().getItemValue(r, "opid");
			if (sb.length() > 0)
				sb.append(":");
			sb.append(opid);
		}
		String opids = sb.toString();

		BatchapDlg apdlg = new BatchapDlg(this, "����������Ȩ");
		apdlg.pack();
		apdlg.setVisible(true);
		if (!apdlg.getOk())
			return;

		Vector<Apinfo> aps = apdlg.getApinfos();

		ApinfoDbmodel apmodel = new ApinfoDbmodel();
		Enumeration<Apinfo> en = aps.elements();
		while (en.hasMoreElements()) {
			Apinfo apinfo = en.nextElement();
			apmodel.appendRow();
			int r = apmodel.getRowCount() - 1;
			apmodel.setItemValue(r, "apname", apinfo.getApname());
			apmodel.setItemValue(r, "aptype", apinfo.getAptype());
			apmodel.setItemValue(r, "apvalue", apinfo.getApvalue());
		}

		ClientRequest req = new ClientRequest("np:����������Ȩ����");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("opids", opids);
		paramcmd.addParam("roleid", roleid);

		DataCommand datacmd = new DataCommand();
		req.addCommand(datacmd);
		datacmd.setDbmodel(apmodel);

		// �ύ����
		try {
			ServerResponse svrresp = SendHelper.sendRequest(req);
			StringCommand respcmd = (StringCommand) svrresp.commandAt(0);
			if (!respcmd.getString().startsWith("+OK")) {
				errorMessage("����", respcmd.getString());
				return;
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("����", e.getMessage());
			return;
		}

		//ˢ��
		mdemodel.clearDetailCache();
		int rr=mdemodel.getMasterModel().getRow();
		mdemodel.getMasterModel().setRow(rr);
	}

	@Override
	protected int getHorizontalsize() {
		return 300;
	}

	public static void main(String[] argv) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Roleopap_mframe f = new Roleopap_mframe();
		f.pack();
		f.setVisible(true);
	}

}
