package com.inca.np.gui.ste;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestDispatch;
import com.inca.np.util.DefaultNPParam;

/**
 * 上传记录附件
 * 
 * @author Administrator
 * 
 */
public class RecordfileUploader {
	Category logger=Category.getInstance(RecordfileUploader.class);

	int uploadfilecount=0;
	
	public int getUploadfilecount() {
		return uploadfilecount;
	}

	/**
	 * 上传记录的文件
	 * 
	 * @param dbmodel
	 * @param row
	 * @throws Exception
	 */
	public boolean uploadFile(DBTableModel dbmodel) throws Exception {
		uploadfilecount=0;
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			RecordTrunk rec = dbmodel.getRecordThunk(r);
			if (rec.getWantuploadfiles().size() > 0) {
				filegroupid = null;
				if(!uploadFile(dbmodel, r))return false;
				// 设置filegroupid
				dbmodel.setItemValue(r, "filegroupid", filegroupid);
				uploadfilecount++;
			}
		}
		return true;
	}

	String filegroupid = null;
	private CProgressDialog progdlg;

	protected boolean uploadFile(DBTableModel dbmodel, int row) throws Exception {
		RecordTrunk rec = dbmodel.getRecordThunk(row);
		filegroupid = dbmodel.getItemValue(row, "filegroupid");
		Enumeration<File> en = rec.getWantuploadfiles().elements();
		while (en.hasMoreElements()) {
			File file = en.nextElement();
			progdlg = new CProgressDialog((Frame)null, "上传文件");
			progdlg.pack();
			UploadThread t = new UploadThread(file);
			t.start();
			progdlg.setVisible(true);
			if(!progdlg.ok){
				t.running=false;
				return false;
			}
		}
		return true;
	}

	class UploadThread extends Thread {
		File file;
		boolean running=false;

		UploadThread(File f) {
			this.file = f;
		}

		public void run() {
			try {
				running=true;
				uploadFile(file);
			} catch (Exception e) {
				logger.error("ERROR",e);
				progdlg.ok=false;
				progdlg.dispose();
		        JOptionPane.showMessageDialog(null, "上传错误"+e.getMessage(),
		                "失败", JOptionPane.ERROR_MESSAGE);
			}finally{
				progdlg.ok=true;
				progdlg.dispose();
			}
		}
		
		/**
		 * 上传文件
		 * 
		 * @param file
		 * @throws Exception
		 */
		void uploadFile(File file) throws Exception {
			progdlg.setStatus("上传文件"+file.getName());
			FileInputStream fin = null;
			// 每次发送100K数据
			int blocksize = 102400;
			try {
				fin = new FileInputStream(file);
				byte[] buffer = new byte[blocksize];

				int sentsize = 0;
				int filelen = (int) file.length();

				DecimalFormat decf = new DecimalFormat("0.00");
				while (running && sentsize < filelen) {
					int rdlen = fin.read(buffer);
					double rate = (double) sentsize / (double) file.length()
							* 100.0;
					progdlg.setProgValue((int)rate);
					/*
					progress.appendMessage("已发送"
							+ StringUtil.bytes2string(sentsize) + ",完成"
							+ decf.format(rate) + "%");
					*/
					sendData(file.getName(), buffer, 0, rdlen, sentsize,
							(sentsize + rdlen) >= filelen);
					sentsize += rdlen;
				}
			} finally {
				if (fin != null) {
					fin.close();
				}
			}

		}
		void sendData(String filename, byte[] buffer, int offset, int len,
				int filepos, boolean finished) throws Exception {
			ClientRequest req = new ClientRequest();
			req.addCommand(new StringCommand("np:uploadfile"));

			ParamCommand paramcmd = new ParamCommand();
			req.addCommand(paramcmd);
			paramcmd.addParam("filename", filename);
			paramcmd.addParam("uploadtype", "RECORDFILE");
			if (filegroupid != null && filegroupid.length() > 0) {
				paramcmd.addParam("filegroupid", filegroupid);
			}
			paramcmd.addParam("startpos", String.valueOf(filepos));
			paramcmd.addParam("length", String.valueOf(len));
			paramcmd.addParam("finished", finished ? "true" : "false");

			BinfileCommand bincmd = new BinfileCommand(buffer, offset, len);
			req.addCommand(bincmd);

			RemoteConnector rmtconn = new RemoteConnector();
			String url = DefaultNPParam.defaultappsvrurl;
			ServerResponse svrresp = null;
			if (DefaultNPParam.debug == 1) {
				svrresp = RequestDispatch.getInstance().process(req);
			} else {
				svrresp = rmtconn.submitRequest(url, req);
			}

			StringCommand cmd0 = (StringCommand) svrresp.commandAt(0);
			String respstatus = cmd0.getString();
			if (respstatus.startsWith("-ERROR")) {
				throw new Exception(respstatus);
			}
			ParamCommand cmd1 = (ParamCommand) svrresp.commandAt(1);
			filegroupid = cmd1.getValue("filegroupid");
		}
		
	}



	/**
	 * 取一个文件组下面的文件信息
	 * 
	 * @param filegroupid
	 * @return
	 * @throws Exception
	 */
	public DBTableModel browserFilegroup(String filegroupid) throws Exception {
		ClientRequest req = new ClientRequest("np:browsefilegroup");
		ParamCommand paramcmd = new ParamCommand();
		paramcmd.addParam("filegroupid", filegroupid);
		req.addCommand(paramcmd);

		RemoteConnector rmtconn = new RemoteConnector();
		String url = DefaultNPParam.defaultappsvrurl;
		ServerResponse svrresp = null;
		if (DefaultNPParam.debug == 1) {
			svrresp = RequestDispatch.getInstance().process(req);
		} else {
			svrresp = rmtconn.submitRequest(url, req);
		}

		StringCommand cmd0 = (StringCommand) svrresp.commandAt(0);
		String respstatus = cmd0.getString();
		if (respstatus.startsWith("-ERROR")) {
			throw new Exception(respstatus);
		}
		DataCommand cmd1 = (DataCommand) svrresp.commandAt(1);
		return cmd1.getDbmodel();

	}

	public static void main(String argv[]) {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("id", "number");
		colinfos.add(col);
		col = new DBColumnDisplayInfo("name", "varchar");
		colinfos.add(col);
		col = new DBColumnDisplayInfo("filegroupid", "number");
		colinfos.add(col);

		DBTableModel dbmodel = new DBTableModel(colinfos);
		dbmodel.appendRow();
		dbmodel.setItemValue(0, "id", "1");
		dbmodel.setItemValue(0, "name", "name1");
		RecordTrunk rec = dbmodel.getRecordThunk(0);
		rec.addWantuploadfile(new File("maven.xml"));
		rec.addWantuploadfile(new File("project.xml"));

		RecordfileUploader rfu = new RecordfileUploader();
		try {
			// rfu.uploadFile(dbmodel);
			DBTableModel filedbmodel = rfu.browserFilegroup("3");
			for (int i = 0; i < filedbmodel.getRowCount(); i++) {
				String fn = filedbmodel.getItemValue(i, "filename");
				String fsize = filedbmodel.getItemValue(i, "filesize");
				String mdate = filedbmodel.getItemValue(i, "modifydate");
				System.out.println(fn + "\t" + fsize + "\t" + mdate);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
