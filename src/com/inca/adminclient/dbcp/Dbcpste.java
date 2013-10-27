package com.inca.adminclient.dbcp;

import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CLinenoDisplayinfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.SendHelper;

public class Dbcpste extends CSteModel {
	Category logger = Category.getInstance(Dbcpste.class);

	public Dbcpste(CFrame frame) throws HeadlessException {
		super(frame, "���ݿ����ӳ�����");
	}

	@Override
	protected int on_beforeNew() {
		if (dbmodel.getRowCount() >= 1) {
			warnMessage("��ʾ", "Ŀǰֻ������һ�����ӳ�");
			return -1;
		}
		return super.on_beforeNew();
	}

	@Override
	public void doQuery() {
		ClientRequest req = new ClientRequest("npclient:listdbcp");
		try {
			ServerResponse resp = AdminSendHelper.sendRequest(req);
			if (!resp.getCommand().startsWith("+OK")) {
				logger.error(resp.getCommand());
				return;
			}
			DataCommand dcmd = (DataCommand) resp.commandAt(1);
			DBTableModel rmtdbmodel = dcmd.getDbmodel();
			dbmodel.clearAll();
			dbmodel.bindMemds(rmtdbmodel);
			sumdbmodel.fireDatachanged();
			this.tableChanged();
			table.autoSize();
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "npclient:savedbcp";
	}

	@Override
	protected void on_aftersave() {
		doQuery();
	}

	@Override
	protected int on_actionPerformed(String command) {
		if ("SYSTEMPASSWORD".equals(command)) {
			setSystempassword();
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	void setSystempassword() {
		String systempassword = JOptionPane.showInputDialog(
				"����oracle system�û����룬���ڽ������ݿ�ϵͳ����", "manager");
		if(systempassword==null)return;
		
		ClientRequest req=new ClientRequest("npadmin:setsystempassword");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("systempassword", systempassword);
		try {
			ServerResponse resp=AdminSendHelper.sendRequest(req);
			if(resp.getCommand().startsWith("+OK")==false){
				errorMessage("����",resp.getCommand());
				return;
			}
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("����",e.getMessage());
		}
	}

}
