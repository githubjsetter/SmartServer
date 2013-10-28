package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Category;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.CHovbaseGeneral;
import com.smart.platform.util.MD5Helper;
import com.smart.platform.util.SendHelper;
import com.smart.platform.util.ZipHelper;

public class Zxhovdownloader {
	Category logger = Category.getInstance(Zxhovdownloader.class);
	boolean ok = false;
	String errormessage = "";
	File downloadedfile = null;

	/**
	 * 下载专项程序ZIP包
	 * 
	 * @param opid
	 * @throws Exception
	 */
	public File downloadZxzip(String hovname) throws Exception {
		DownloadinfoDlg progdlg = new DownloadinfoDlg("下载专项HOV文件");
		progdlg.pack();
		progdlg.setMessage("下载专项HOV" + hovname);
		DownloadThread t = new DownloadThread(hovname, progdlg);
		t.start();
		progdlg.setVisible(true);
		if (!ok)
			return null;
		return downloadedfile;
	}

	public CHovBase createHovfromzxzipfile(File zxzipfile) throws Exception {
		File hovfile = null;
		try {
			hovfile = File.createTempFile("temp", ".hov");
			ZipHelper.extractFile(zxzipfile, "hov.model", hovfile);
			CHovbaseGeneral hov = new CHovbaseGeneral(hovfile);
			return hov;
		} finally {
			if (hovfile != null) {
				hovfile.delete();
			}
		}
	}

	class DownloadThread extends Thread {
		DownloadinfoDlg dlg = null;
		String hovname;

		DownloadThread(String hovname, DownloadinfoDlg dlg) {
			this.hovname = hovname;
			this.dlg = dlg;
		}

		public void run() {
			try {
				cancelflag = false;
				ok = false;
				downloadedfile = downloadZxzip(hovname, dlg);
				ok = true;
			} catch (Exception e) {
				logger.error("ERROR", e);
				ok = false;
				errormessage = e.getMessage();
			}
			if (dlg != null) {
				dlg.setVisible(false);
				dlg.dispose();
			}

		}

		File downloadZxzip(String hovname, DownloadinfoDlg progdlg)
				throws Exception {

			File dir = new File(CurrentdirHelper.getZxdir(), "HOV");
			dir.mkdirs();
			File clientfile = new File(dir, hovname + ".zip");
			String clientmd5 = "not exists";

			if (clientfile.exists()) {
				clientmd5 = MD5Helper.MD5(clientfile);
			}

			int startpos = 0;
			for (; !cancelflag;) {
				ClientRequest req = new ClientRequest("npclient:下载专项HOV");
				ParamCommand pcmd = new ParamCommand();
				req.addCommand(pcmd);
				pcmd.addParam("hovname", hovname);
				pcmd.addParam("clientmd5", clientmd5);
				pcmd.addParam("startpos", String.valueOf(startpos));

				ServerResponse resp = SendHelper.sendRequest(req);

				// Thread.sleep(5000);

				String respstr = resp.getCommand();
				if (respstr.startsWith("-ERROR"))
					throw new Exception(respstr);

				ParamCommand respcmd = (ParamCommand) resp.commandAt(1);
				int length = Integer.parseInt(respcmd.getValue("length"));
				String finished = respcmd.getValue("finished");

				if (length == 0) {
					break;
				}
				int totallength = Integer.parseInt(respcmd
						.getValue("totallength"));
				FileOutputStream fout = null;
				try {
					clientfile.getParentFile().mkdirs();
					fout = new FileOutputStream(clientfile, startpos != 0);
					BinfileCommand bcmd = (BinfileCommand) resp.commandAt(2);
					fout.write(bcmd.getBindata());
				} finally {
					if (fout != null) {
						fout.close();
					}
				}
				startpos += length;

				float percent = (float) startpos / (float) totallength * 100f;
				dlg.setPercent((int) percent);
				if (finished.equals("true"))
					break;
			}
			return clientfile;
		}

	}

	boolean cancelflag = false;

	public File getDownloadfile() {
		return downloadedfile;
	}

	class DownloadinfoDlg extends CDialog {
		private JLabel lbmsg;
		private JProgressBar progbar;

		DownloadinfoDlg(String title) {
			super((Frame) null, title, true);
			init();
			localCenter();
		}

		public void setPercent(int percent) {
			progbar.setValue(percent);
		}

		void init() {
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());

			lbmsg = new JLabel("");
			cp.add(lbmsg, BorderLayout.NORTH);

			progbar = new JProgressBar(0, 100);
			cp.add(progbar, BorderLayout.CENTER);
			progbar.setPreferredSize(new Dimension(300, 15));

			JPanel bottompanel = credateBottompanel();
			cp.add(bottompanel, BorderLayout.SOUTH);
		}

		JPanel credateBottompanel() {
			JPanel jp = new JPanel();
			JButton btn;
			btn = new JButton("取消下载");
			jp.add(btn);
			btn.setActionCommand("cancel");
			btn.addActionListener(this);
			return jp;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("cancel".equals(e.getActionCommand())) {
				cancelflag = true;
				ok = false;
				errormessage = "用户取消";
				setVisible(false);
				dispose();
			}
		}

		public void setMessage(String msg) {
			lbmsg.setText(msg);
		}
	}

	public String getErrormessage() {
		return errormessage;
	}

	public static void main(String[] args) {
		Zxhovdownloader zxdl = new Zxhovdownloader();
		try {
			zxdl.downloadZxzip("23");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
