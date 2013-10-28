package com.smart.adminclient.dbcp;

import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.smart.adminclient.auth.AdminSendHelper;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

public class Dbcpste extends CSteModel {
	Category logger = Category.getInstance(Dbcpste.class);

	public Dbcpste(CFrame frame) throws HeadlessException {
		super(frame, "数据库连接池配置");
	}

	@Override
	protected int on_beforeNew() {
		if (dbmodel.getRowCount() >= 1) {
			warnMessage("提示", "目前只允许定义一个连接池");
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
				"设置oracle system用户密码，用于进行数据库系统管理", "manager");
		if(systempassword==null)return;
		
		ClientRequest req=new ClientRequest("npadmin:setsystempassword");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("systempassword", systempassword);
		try {
			ServerResponse resp=AdminSendHelper.sendRequest(req);
			if(resp.getCommand().startsWith("+OK")==false){
				errorMessage("错误",resp.getCommand());
				return;
			}
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("错误",e.getMessage());
		}
	}

}
