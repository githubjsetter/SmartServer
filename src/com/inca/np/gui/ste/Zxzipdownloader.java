package com.inca.np.gui.ste;

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

import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.gui.control.CDialog;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SendHelper;
import com.inca.npclient.system.Clientframe;

public class Zxzipdownloader  {
	Category logger=Category.getInstance(Zxzipdownloader.class);
	boolean ok=false;
	String errormessage="";
	File downloadedfile=null;

	/**
	 * 下载专项程序ZIP包
	 * @param opid
	 * @throws Exception
	 */
	public File downloadZxzip(String opid) throws Exception{
		//DownloadinfoDlg progdlg =new DownloadinfoDlg("下载专项文件");
		//progdlg.pack();
		//progdlg.setMessage("下载专项功能号"+opid);
		logger.debug("开始下载opid="+opid+"的专项");
		Object lockobject=new Object();
		synchronized (lockobject) {
			DownloadThread t=new DownloadThread(opid,lockobject);
			t.start();
			lockobject.wait();
			logger.debug("下载opid="+opid+"的专项结束,result="+ok);
		}
		//progdlg.setVisible(true);
		if(!ok)return null;
		return downloadedfile;
	}
	
	
	class DownloadThread extends Thread {
		//DownloadinfoDlg dlg = null;
		Object lockobject=null;
		String opid;

		DownloadThread(String opid,Object lockobject/* DownloadinfoDlg dlg*/) {
			this.opid = opid;
			//this.dlg = dlg;
			this.lockobject=lockobject;
		}

		public void run() {
			try {
				Clientframe.setStatus("正在下载"+opid+"的专项");
				cancelflag=false;
				ok=false;
				downloadedfile = downloadZxzip(opid /*,dlg*/);
				if(downloadedfile==null){
					ok=false;
				}
				ok = true;
			} catch (Exception e) {
				logger.error("ERROR", e);
				downloadedfile=null;
				ok = false;
				errormessage = e.getMessage();
			}finally{
				Clientframe.setStatus("已下载"+opid+"的专项");
			}
/*			if (dlg != null) {
				dlg.setVisible(false);
				dlg.dispose();
			}
*/
			synchronized (lockobject) {
				lockobject.notifyAll();
			}
		}
		
		File downloadZxzip(String opid) throws Exception{
			File dir=CurrentdirHelper.getZxdir();
			File clientfile=new File(dir,opid+".zip");
			String clientmd5="";
			
			if(clientfile.exists()){
				clientmd5=MD5Helper.MD5(clientfile);
			}
			
			int startpos=0;
			for(;!cancelflag;){
				ClientRequest req=new ClientRequest("npclient:downloadzxzip");
				ParamCommand pcmd=new ParamCommand();
				req.addCommand(pcmd);
				pcmd.addParam("opid",opid);
				pcmd.addParam("clientmd5",clientmd5);
				pcmd.addParam("startpos",String.valueOf(startpos));
				
				ServerResponse resp=SendHelper.sendRequest(req);
				
				//Thread.sleep(5000);
				
				
				String respstr=resp.getCommand();
				if(respstr.startsWith("-ERROR")){
					logger.error(respstr);
					return null;
					//throw new Exception(respstr);
				}
				
				ParamCommand respcmd=(ParamCommand) resp.commandAt(1);
				int length=Integer.parseInt(respcmd.getValue("length"));
				String finished=respcmd.getValue("finished");
				
				if(length==0){
					break;
				}
				int  totallength=Integer.parseInt(respcmd.getValue("totallength"));
				FileOutputStream fout=null;
				try{
					clientfile.getParentFile().mkdirs();
					fout=new FileOutputStream(clientfile,startpos!=0);
					BinfileCommand bcmd=(BinfileCommand) resp.commandAt(2);
					fout.write(bcmd.getBindata());
				}finally{
					if(fout!=null){
						fout.close();
					}
				}
				startpos+=length;
				
				//float percent=(float)startpos/(float)totallength * 100f;
				//dlg.setPercent((int)percent);
				if(finished.equals("true"))break;
			}
			return clientfile;
		}
		
	}

	boolean cancelflag = false;
	
	public File getDownloadfile(){
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
	
	public static void main(String[] args) {
		Zxzipdownloader zxdl=new Zxzipdownloader();
		try {
			zxdl.downloadZxzip("10000");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
