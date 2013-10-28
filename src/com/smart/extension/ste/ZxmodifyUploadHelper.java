package com.smart.extension.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Category;

import com.smart.client.download.DownloadManager;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.util.SendHelper;

/**
 * 上传专项调整
 * @author Administrator
 *
 */
public class ZxmodifyUploadHelper {
	Category logger=Category.getInstance(ZxmodifyUploadHelper.class);
	boolean cancelflag = false;
	boolean ok = false;
	String errormessage = "";
	String opid;

	public boolean uploadZxfile(String opid,File zxzipfile){
		this.opid=opid;
		DownloadManager.getInst().clearZxfileCache(opid);
		UploadprogressDialog progdlg=new UploadprogressDialog("上传专项调整文件");
		progdlg.setMessage("上传文件"+zxzipfile.getName());
		progdlg.pack();
		UploadThread uploadthread=new UploadThread(zxzipfile,progdlg);
		uploadthread.start();
		progdlg.setVisible(true);
		return ok;
	}
	
	
	
	class UploadThread extends Thread {
		File zxzipfile = null;
		UploadprogressDialog progdlg=null;
		public UploadThread(File zxzipfile,UploadprogressDialog progdlg) {
			this.zxzipfile = zxzipfile;
			this.progdlg=progdlg;
		}

		public void run() {
			try {
				cancelflag=false;
				ok=false;
				errormessage="";
				uploadZxfile(zxzipfile);
				ok=true;
			} catch (Exception e) {
				logger.error("error",e);
				errormessage=e.getMessage();
				ok=false;
			} finally{
				progdlg.setVisible(false);
				progdlg.dispose();
			}
		}

		void uploadZxfile(File zxzipfile) throws Exception {
			FileInputStream fin = null;
			try {
				int length = (int) zxzipfile.length();
				int buflen = 102400;
				byte[] buf = new byte[buflen];
				int totalsend = 0;
				fin = new FileInputStream(zxzipfile);
				while (!cancelflag && length > 0) {
					ClientRequest req = new ClientRequest("npdev:安装专项调整ZIP");
					ParamCommand pcmd = new ParamCommand();
					req.addCommand(pcmd);
					int rd = fin.read(buf);
					pcmd.addParam("opid", opid);
					pcmd.addParam("length", String.valueOf(rd));
					pcmd.addParam("startpos", String.valueOf(totalsend));
					totalsend += rd;
					length -= rd;
					pcmd.addParam("finished", length == 0 ? "true" : "false");
					BinfileCommand bincmd = new BinfileCommand(buf, 0, rd);
					req.addCommand(bincmd);
					float percent= (float)totalsend / (float)zxzipfile.length() * 100f;
					progdlg.setPercent((int)percent);
					ServerResponse resp = SendHelper.sendRequest(req);
					//Thread.sleep(3000);
					String resultcmd = resp.getCommand();
					if (!resultcmd.startsWith("+OK"))
						throw new Exception(resultcmd);
				}
			} finally {
				if (fin != null) {
					fin.close();
				}
			}
		}
	}

	class UploadprogressDialog extends CDialog {
		private JLabel lbmsg;
		private JProgressBar progbar;

		UploadprogressDialog(String title) {
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
			btn = new JButton("取消上传");
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

}
