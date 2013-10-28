package com.smart.adminclient.fullscan;

import com.smart.adminclient.auth.AdminSendHelper;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.CQueryMastermodel;
import com.smart.platform.util.SendHelper;

import java.awt.*;

import org.apache.log4j.Category;

/*����"��ѯfullscan"�ܵ�Model*/
public class Fullscan_master extends CMasterModel {
	Category logger = Category.getInstance(Fullscan_master.class);

	public Fullscan_master(CFrame frame, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, "sql���", mdemodel);
	}

	public String getTablename() {
		return "xxx";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doQuery() {
		ClientRequest req = new ClientRequest("��ѯfullscan");
		ServerResponse resp = null;
		try {
			this.setWaitCursor();
			resp = AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("����", e.getMessage());
			return;
		} finally {
			this.setDefaultCursor();
		}

		StringCommand cmd0 = (StringCommand) resp.commandAt(0);
		if (!cmd0.getString().startsWith("+OK")) {
			errorMessage("����", cmd0.getString());
			return;
		}

		ParamCommand paramcmd = (ParamCommand) resp.commandAt(1);
		int searchedsqlct = 0;
		try {
			searchedsqlct = Integer
					.parseInt(paramcmd.getValue("searchedsqlct"));
		} catch (Exception e) {
		}

		if (resp.getCommandcount() == 3) {
			DataCommand cmd2 = (DataCommand) resp.commandAt(2);
			DBTableModel dbmodel = cmd2.getDbmodel();
			if(dbmodel.getRowCount()==0){
				this.warnMessage("��ʾ", "������ɨ��full scan�߳�,���Ժ�������ѯ");
			}
			this.setDBtableModel(dbmodel);
		}else{
			this.warnMessage("��ʾ", "������ɨ��full scan�߳�");
			return;
		}

	}

}
