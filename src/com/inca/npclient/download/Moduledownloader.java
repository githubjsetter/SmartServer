package com.inca.npclient.download;

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
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SendHelper;
import com.inca.np.util.StringUtil;
import com.sun.jndi.toolkit.ctx.StringHeadTail;

/**
 * 向服务器发送请求,下载模块信息
 * 
 * @author Administrator
 * 
 */
public class Moduledownloader {
	Category logger = Category.getInstance(Moduledownloader.class);

	public DBTableModel downloadModulelist() throws Exception {
		logger.info("begin downloadmodules");
		ClientRequest req = new ClientRequest("npclient:downloadmodules");
		try {
			ServerResponse resp = SendHelper.sendRequest(req);
			StringCommand cmd = (StringCommand) resp.commandAt(0);
			String result = cmd.getString();
			if (!result.startsWith("+OK")) {
				throw new Exception(result);
			}

			DataCommand cmd1 = (DataCommand) resp.commandAt(1);

			DBTableModel moduledbmodel = cmd1.getDbmodel();
			return moduledbmodel;

		} catch (Exception e) {
			logger.error("ERROR", e);
			throw e;
		}
	}

	boolean ok = false;
	String errormessage = "";
	boolean usecache = false;

	/**
	 * 
	 * @param prodname
	 * @param modulename
	 * @param withdlg
	 *            显示进度对话框？
	 * @param usecache
	 *            是否在lib目录下建立一个cache目录，将文件下载到cache中
	 * @return
	 */
	public File downloadModule(String prodname, String modulename,
			boolean withdlg, boolean usecache) {
		String cmd = "npclient:downloadmodulefile";
		ParamCommand pcmd = new ParamCommand();
		pcmd.addParam("prodname", prodname);
		pcmd.addParam("modulename", modulename);
		File outdir=null;
		if(usecache){
			outdir=new File(CurrentdirHelper.getLibdir(),"cache");
		}else{
			outdir=CurrentdirHelper.getLibdir();
		}
		MThreadDownloadhelper dl = new MThreadDownloadhelper();
		boolean ret = dl.download(null, "下载" + prodname + "模块" + modulename,
				cmd, pcmd, outdir);
		if (!ret) {
			ok = false;
			errormessage=dl.getErrormessage();
			return null;
		}
		ok = true;
		return dl.getDownloadfile();
	}

	/*
	 * DownloadinfoDlg dlg = null; cancelflag = false; this.usecache=usecache;
	 * downloadedfile = null; if (withdlg) { dlg = new DownloadinfoDlg("下载模块" +
	 * modulename + "客户端"); dlg.pack(); DownloadThread t = new
	 * DownloadThread(prodname, modulename, dlg); t.start();
	 * dlg.setVisible(true); return downloadedfile; } else { try {
	 * downloadedfile = downloadModule(prodname, modulename, null); ok = true;
	 * return downloadedfile; } catch (Exception e) { logger.error("ERROR", e);
	 * ok = false; errormessage = e.getMessage(); return null; } }
	 */

	/*
	 * class DownloadThread extends Thread { DownloadinfoDlg dlg = null; String
	 * prodname; String modulename;
	 * 
	 * DownloadThread(String prodname, String modulename, DownloadinfoDlg dlg) {
	 * this.prodname = prodname; this.modulename = modulename; this.dlg = dlg; }
	 * 
	 * public void run() { try { downloadedfile = downloadModule(prodname,
	 * modulename, dlg); ok = true; } catch (Exception e) {
	 * logger.error("ERROR", e); ok = false; errormessage = e.getMessage(); } if
	 * (dlg != null) { dlg.setVisible(false); dlg.dispose(); }
	 *  } }
	 * 
	 * boolean cancelflag = false;
	 * 
	 * File downloadedfile = null;
	 * 
	 * public File getDownloadfile() { return downloadedfile; }
	 */
	/**
	 * 从服务器下载一个模块的客户端文件
	 * 
	 * @param modulename
	 * @throws Exception
	 *             File downloadModule(String prodname, String modulename,
	 *             DownloadinfoDlg dlg) throws Exception { File jarfile = null;
	 *             int startpos = 0; if (dlg != null) {
	 *             dlg.setMessage("开始下载....."); } boolean finished = false; int
	 *             downloadedlen = 0; File libdir = new
	 *             File(CurrentdirHelper.getappDir(), "lib"); libdir.mkdirs();
	 *             do {
	 * 
	 * ServerResponse resp = sendRequest(prodname,modulename,startpos);
	 * if(resp==null){ throw new Exception("下载失败"); }
	 * 
	 * if (cancelflag) { ok = false; errormessage = "用户取消"; throw new
	 * Exception("用户取消"); }
	 * 
	 * if (!resp.getCommand().startsWith("+OK")) { throw new
	 * Exception(resp.getCommand()); } ParamCommand resppcmd = (ParamCommand)
	 * resp.commandAt(1); String filename = resppcmd.getValue("filename"); if
	 * (jarfile == null) { if(usecache){ jarfile = new File(libdir,
	 * "cache/"+filename); }else{ jarfile = new File(libdir, filename); }
	 * jarfile.getParentFile().mkdirs(); }
	 * 
	 * int len = Integer.parseInt(resppcmd.getValue("length")); long totallen =
	 * Long.parseLong(resppcmd.getValue("totallength")); finished =
	 * "true".equals(resppcmd.getValue("finished")); BinfileCommand bfc =
	 * (BinfileCommand) resp.commandAt(2); downloadedlen += len; float percent =
	 * (float) downloadedlen / (float) totallen * 100.0f; if (dlg != null) {
	 * String msg = "已下载" + StringUtil.bytes2string(downloadedlen);
	 * dlg.setMessage(msg); dlg.setPercent((int) percent); }
	 * 
	 * FileOutputStream fout = null; try { logger.debug("moduledownloader jar
	 * file=" + jarfile.getAbsolutePath()); fout = new FileOutputStream(jarfile,
	 * startpos > 0); fout.write(bfc.getBindata(), 0, len); logger.debug("writen
	 * file size=" + len); } finally { if (fout != null) { fout.close(); } }
	 * startpos += len; } while (!finished); return jarfile; }
	 * 
	 * ServerResponse sendRequest(String prodname, String modulename, int
	 * startpos) throws Exception { ClientRequest req = new
	 * ClientRequest("npclient:downloadmodulefile"); ParamCommand pcmd = new
	 * ParamCommand(); pcmd.addParam("prodname", prodname);
	 * pcmd.addParam("modulename", modulename); pcmd.addParam("startpos",
	 * String.valueOf(startpos)); req.addCommand(pcmd);
	 * 
	 * ServerResponse resp = null; int maxretrycount = 5; for (int t = 0;
	 * !cancelflag && t < maxretrycount; t++) { try { resp =
	 * SendHelper.sendRequest(req); break; } catch (Exception e) {
	 * logger.error("error", e); resp=null; Thread.sleep(1000); continue; } }
	 * 
	 * return resp; }
	 * 
	 * class DownloadinfoDlg extends CDialog { private JLabel lbmsg; private
	 * JProgressBar progbar;
	 * 
	 * DownloadinfoDlg(String title) { super((Frame) null, title, true); init();
	 * localCenter(); }
	 * 
	 * public void setPercent(int percent) { progbar.setValue(percent); }
	 * 
	 * void init() { Container cp = getContentPane(); cp.setLayout(new
	 * BorderLayout());
	 * 
	 * lbmsg = new JLabel("准备下载"); cp.add(lbmsg, BorderLayout.NORTH);
	 * 
	 * progbar = new JProgressBar(0, 100); cp.add(progbar, BorderLayout.CENTER);
	 * progbar.setPreferredSize(new Dimension(300, 15));
	 * progbar.setStringPainted(true);
	 * 
	 * JPanel bottompanel = credateBottompanel(); cp.add(bottompanel,
	 * BorderLayout.SOUTH); }
	 * 
	 * JPanel credateBottompanel() { JPanel jp = new JPanel(); JButton btn; btn =
	 * new JButton("取消下载"); jp.add(btn); btn.setActionCommand("cancel");
	 * btn.addActionListener(this); return jp; }
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { if
	 *           ("cancel".equals(e.getActionCommand())) { cancelflag = true; ok =
	 *           false; errormessage = "用户取消"; setVisible(false); dispose(); } }
	 * 
	 * public void setMessage(String msg) { lbmsg.setText(msg); } }
	 */

	public static void main(String[] args) {
		Moduledownloader mdl = new Moduledownloader();
		try {
			File dlfile = mdl.downloadModule("launcher", "npcommon-5.0.03.jar",
					true, true);
			if (dlfile != null) {
				System.out.println("下载成功");
			} else {
				System.err.println("下载失败:" + mdl.getErrormessage());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getErrormessage() {
		return errormessage;
	}
}
