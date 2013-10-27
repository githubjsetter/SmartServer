package com.inca.adminclient.viewlog;

import java.awt.HeadlessException;
import java.io.File;
import java.util.Vector;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.SendHelper;

public class Viewlog_ste extends CSteModel {

	public Viewlog_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected void loadDBColumnInfos() {
		formcolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("行号", "行号", "行号");
		formcolumndisplayinfos.add(col);

		col = new DBColumnDisplayInfo("filename", "varchar", "文件名");
		formcolumndisplayinfos.add(col);

		col = new DBColumnDisplayInfo("filelength", "number", "文件大小");
		formcolumndisplayinfos.add(col);

	}

	@Override
	public void doQuery() {
		try {
			setWaitCursor();
			ClientRequest req = new ClientRequest("npclient:getloggerinfo");
			ServerResponse resp = AdminSendHelper.sendRequest(req);
			DataCommand dcmd = (DataCommand) resp.commandAt(2);
			dbmodel.clearAll();
			dbmodel.bindMemds(dcmd.getDbmodel());
			for (int i = 0; i < dbmodel.getRowCount(); i++) {
				dbmodel.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}
			dbmodel.sort("filename:asc");
			tableChanged();
			table.autoSize();
		} catch (Exception e) {
			errorMessage("错误", e.getMessage());
			return;
		} finally {
			setDefaultCursor();
		}
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("下载日志文件")) {
			try {
				downloadFile();
			} catch (Exception e) {
				errorMessage("下载错误", e.getMessage());
			}
			return 0;
		}else if(command.equals("查看日志文件")){
			viewFile();
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	void viewFile(){
		int row=getRow();
		if(row<0)return;

		String filename = getItemValue(row, "filename");
		Viewlogfile_frame f=new Viewlogfile_frame();
		f.pack();
		f.setVisible(true);
		f.viewFilepar(filename);

	}
	
	void downloadFile() throws Exception {
		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			String filename = getItemValue(row, "filename");
			downloadLogfile(filename);
		}
		File dir=new File("logs");
		String cmd="explorer "+dir.getAbsolutePath();
		Runtime.getRuntime().exec(cmd);
	}

	void downloadLogfile(String filename) throws Exception {
		LoggerfileDownloader dl = new LoggerfileDownloader();
		dl.downloadLoggerfile(filename);
	}

	@Override
	public void on_doubleclick(int row, int col) {
		super.on_doubleclick(row, col);
		try {
			viewFile();
		} catch (Exception e) {
			errorMessage("下载错误", e.getMessage());
		}
	}

}
