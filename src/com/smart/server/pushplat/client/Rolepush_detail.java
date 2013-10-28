package com.smart.server.pushplat.client;

import java.awt.HeadlessException;

import javax.swing.event.TableModelEvent;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.util.SendHelper;

public class Rolepush_detail extends CDetailModel {

	public Rolepush_detail(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
		DBColumnDisplayInfo col = null;
		col = getDBColumnDisplayInfo("pushname");
		if (col == null) {
			col = new DBColumnDisplayInfo("pushname", "varchar", "推送名称");
			formcolumndisplayinfos.add(col);
		}
		col.setDbcolumn(false);
		col.setQueryable(false);
		col.setUpdateable(false);

	}

	@Override
	public String getTablename() {
		return "np_role_push";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	public void doNew() {
		// 选择push
		Push_hov hov = new Push_hov();
		DBTableModel result = hov.showDialog(getParentFrame(), "选择推送");
		if (result == null) {
			return;
		}
		CTable hovtable = hov.getDlgtable();
		DBTableModel hovdm = (DBTableModel) hovtable.getModel();
		int rows[] = hovtable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			String pushid = hovdm.getItemValue(row, "pushid");

			int targetrow = -1;
			for (int r = 0; r < dbmodel.getRowCount(); r++) {
				if (dbmodel.getItemValue(r, "pushid").equals(pushid)) {
					targetrow = r;
					break;
				}
			}
			if (targetrow == -1) {
				targetrow = dbmodel.getRowCount();
				dbmodel.appendRow();
			}
			dbmodel.setItemValue(targetrow, "pushid", pushid);
			dbmodel.setItemValue(targetrow, "pushname", hovdm.getItemValue(row,
					"pushname"));

		}
		int mr = mdemodel.getMasterModel().getRow();
		mdemodel.getMasterModel()
				.setdbStatus(mr, RecordTrunk.DBSTATUS_MODIFIED);

		tableChanged();
	}

	DBTableModel pushdm = null;

	@Override
	protected void on_retrieved() {
		super.on_retrieved();

		if (pushdm == null) {
			ClientRequest req = new ClientRequest("npserver:下载推送");
			try {
				ServerResponse resp = SendHelper.sendRequest(req);
				DataCommand dcmd = (DataCommand) resp.commandAt(1);
				pushdm = dcmd.getDbmodel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String pushid = dbmodel.getItemValue(r, "pushid");
			int pushdmrow = pushdm.searchColumnvalue("pushid", pushid);
			if (pushdmrow >= 0) {
				setItemValue(r, "pushname", pushdm.getItemValue(pushdmrow,
						"pushname"));
			}
		}

	}

}
