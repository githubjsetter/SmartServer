package com.smart.platform.gui.ste;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Category;

import com.smart.client.download.MThreadDownloadhelper;
import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestDispatch;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;
import com.smart.platform.util.StringUtil;

/**
 * 下载某个文件group中的某个文件
 * 
 * @author Administrator
 * 
 */
public class RecordFileDownloader {
	Category logger = Category.getInstance(RecordFileDownloader.class);
	boolean running = false;

	public void stopDownload() {
		running = false;
	}

	/**
	 * 下载记录附件
	 * 
	 * @param filegroupid
	 * @param filename
	 * @param outfile
	 * @param progdlg
	 * @return
	 * @throws Exception
	 */
	public boolean downloadFile(String filegroupid, String filename,
			File outfile) throws Exception {
		MThreadDownloadhelper dlh = new MThreadDownloadhelper();
		ParamCommand param = new ParamCommand();
		param.addParam("filegroupid", filegroupid);
		param.addParam("downloadtype", "RECORDFILE");
		param.addParam("filename", filename);

		return dlh.download(null, "下载附件" + filename, "np:download", param,
				outfile.getParentFile());
	}

	/**
	 * 删除文件
	 * @param filegroupid
	 * @param filename
	 * @param outfile
	 * @return
	 * @throws Exception
	 */
	public static void deleteFile(String filegroupid, String filename) throws Exception {
		ParamCommand param = new ParamCommand();
		param.addParam("filegroupid", filegroupid);
		param.addParam("filename", filename);
		ClientRequest req=new ClientRequest("np:deletefile");
		req.addCommand(param);
		ServerResponse resp=SendHelper.sendRequest(req);
		String scmd=resp.getCommand();
		if(!scmd.startsWith("+OK")){
			throw new Exception(scmd);
		}
	}

	
/*
	public boolean downloadFile_old(String filegroupid, String filename,
			File outfile, CProgressDialog progdlg) throws Exception {
		int startpos = 0;
		progdlg.setStatus("下载文件" + filename);
		long filesize = -1;
		running = true;
		while (running) {
			ClientRequest req = new ClientRequest();
			req.addCommand(new StringCommand("np:download"));
			ParamCommand param = new ParamCommand();
			req.addCommand(param);
			param.addParam("filegroupid", filegroupid);
			param.addParam("downloadtype", "RECORDFILE");
			param.addParam("filename", filename);
			param.addParam("startpos", String.valueOf(startpos));

			RemoteConnector rmtconn = new RemoteConnector();
			String url = DefaultNPParam.defaultappsvrurl;
			ServerResponse svrresp = null;
			try {
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
			} catch (Exception e) {
				logger.error("ERROR", e);
				throw new Exception(e.getMessage());
			}

			ParamCommand respparam = (ParamCommand) svrresp.commandAt(1);
			boolean hasmore = respparam.getValue("hasmore").equals("true");
			filesize = Long.parseLong(respparam.getValue("filesize"));

			BinfileCommand filecmd = (BinfileCommand) svrresp.commandAt(2);
			FileOutputStream fout = null;
			try {
				if (startpos == 0) {
					fout = new FileOutputStream(outfile);
				} else {
					fout = new FileOutputStream(outfile, true);
				}
				byte[] data = filecmd.getBindata();
				fout.write(data);
				startpos += data.length;

				if (filesize > 0) {
					double percent = 100.0 * (double) startpos
							/ (double) filesize;
					progdlg.setProgValue((int) percent);
				}

			} finally {
				if (fout != null) {
					fout.close();
				}
			}
			if (!hasmore) {
				break;
			}
		}
		if (startpos != filesize) {
			outfile.delete();
		}
		return startpos == filesize;
	}
	*/
}
