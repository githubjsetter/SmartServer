package com.inca.adminclient.modulemgr;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Category;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.CDialog;

public class Moduleuploader {
	Category logger=Category.getInstance(Moduleuploader.class);
	boolean cancelflag = false;
	boolean ok = false;
	String errormessage = "";

	public boolean installModule(File uploadfile){
		UploadprogressDialog progdlg=new UploadprogressDialog("安装模块");
		progdlg.setMessage("安装模块"+uploadfile.getName());
		progdlg.pack();
		UploadThread uploadthread=new UploadThread(uploadfile,progdlg);
		uploadthread.start();
		progdlg.setVisible(true);
		return ok;
	}
	
	class UploadThread extends Thread{
		File uploadfile = null;
		UploadprogressDialog progdlg=null;
		public UploadThread(File uploadfile,UploadprogressDialog progdlg) {
			this.uploadfile = uploadfile;
			this.progdlg=progdlg;
		}

		public void run() {
			try {
				cancelflag=false;
				ok=false;
				errormessage="";
				upload(uploadfile);
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

		void upload(File uploadfile)throws Exception{
			int bufsize=102400;
			int totallen=(int)uploadfile.length();
			byte[] buf=new byte[bufsize];
			FileInputStream fin=null;
			try {
				fin=new FileInputStream(uploadfile);
				int startpos=0;
				while(totallen>0){
					int rded=fin.read(buf);
					totallen-=rded;
					uploadFile(uploadfile,startpos,rded,buf,totallen==0);
					startpos += rded;
					float percent=(float)startpos / (float)uploadfile.length() * 100f;
					progdlg.setPercent((int)percent);
				}
				
			}finally{
				if(fin!=null){
					try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		void uploadFile(File licensefile, int startpos, int rded,byte[] data,boolean finished) throws Exception{
			ClientRequest req=new ClientRequest("npserver:uploadmodulefile");
			ParamCommand paramcmd=new ParamCommand();
			req.addCommand(paramcmd);
			paramcmd.addParam("length",String.valueOf(rded));
			paramcmd.addParam("startpos",String.valueOf(startpos));
			paramcmd.addParam("finished",finished?"true":"false");
			paramcmd.addParam("filename",licensefile.getName());
			
			BinfileCommand bfcmd=new BinfileCommand(data,0,rded);
			req.addCommand(bfcmd);
			
			AdminSendHelper sender=new AdminSendHelper();
			ServerResponse svrresp=sender.sendRequest(req);
			
			StringCommand cmd0=(StringCommand) svrresp.commandAt(0);
			String msg=cmd0.getString();
			
			if(!msg.startsWith("+OK")){
				throw new Exception(msg);
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
