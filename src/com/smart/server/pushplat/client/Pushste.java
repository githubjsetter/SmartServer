package com.smart.server.pushplat.client;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFormatTextField;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTextArea;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Hovdefine;
import com.smart.platform.util.SendHelper;
import com.smart.server.pushplat.common.Pushinfo;

public class Pushste extends CSteModel {

	File file = null;
	String postfix = ".nppush";
	Category logger = Category.getInstance(Pushste.class);

	public Pushste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		DBColumnDisplayInfo col = this.getDBColumnDisplayInfo("callopid");
		Hovdefine hovdef = new Hovdefine(
				"com.inca.npworkflow.client.CallopHov", "callopid");
		hovdef.putColpair("opid", "callopid");
		hovdef.putColpair("opname", "callopname");
		col.setHovdefine(hovdef);

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
	protected int on_actionPerformed(String command) {
		if ("打开文件".equals(command)) {
			doOpen();
			return 0;
		} else if ("上传".equals(command)) {
			upload();
			return 0;
		} else if (command.startsWith("当前")){
			int row=getRow();
			if(row>=0){
				CTextArea text=(CTextArea) dbmodel.getColumninfo("wheres").getEditComponent();
				text.getTextarea().replaceSelection("<"+command+">");
			}
			return 0;
		} else {
			return super.on_actionPerformed(command);
		}
	}

	void upload() {
		JFileChooser fc = new JFileChooser(new File("."));
		if (file != null) {
			fc.setCurrentDirectory(file.getParentFile());
			fc.setSelectedFile(file);
		}
		fc.setFileFilter(new PushfileFilter());
		fc.setMultiSelectionEnabled(true);
		if (fc.showOpenDialog(getParentFrame()) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File fs[] = fc.getSelectedFiles();
		for (int i = 0; i < fs.length; i++) {
			if(!uploadFile(fs[i])){
				break;
			}
		}
		infoMessage("成功", "上传成功");
	}

	boolean uploadFile(File f) {
		FileInputStream fin =null;
		try {
			fin = new FileInputStream(f);
			int length = (int) f.length();
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			int totalsend = 0;
			boolean cancelflag = false;
			while (!cancelflag && length > 0) {
				ClientRequest req = new ClientRequest("npserver:上传推送文件");
				ParamCommand pcmd = new ParamCommand();
				req.addCommand(pcmd);
				int rd = fin.read(buf);
				pcmd.addParam("filename", f.getName());
				pcmd.addParam("length", String.valueOf(rd));
				pcmd.addParam("startpos", String.valueOf(totalsend));
				totalsend += rd;
				length -= rd;
				pcmd.addParam("finished", length == 0 ? "true" : "false");
				BinfileCommand bincmd = new BinfileCommand(buf, 0, rd);
				req.addCommand(bincmd);
				ServerResponse resp = SendHelper.sendRequest(req);
				// Thread.sleep(3000);
				String resultcmd = resp.getCommand();
				if (!resultcmd.startsWith("+OK")){
					logger.error(resultcmd);
					errorMessage("错误", resultcmd);
					return false;
				}
			}
			return true;
		} catch(Exception e) {
			logger.error("error",e);
			errorMessage("错误", e.getMessage());
			return false;
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}

	}

	void doOpen() {
		JFileChooser fc = null;
		if (file == null) {
			fc = new JFileChooser(new File("."));
		} else {
			fc = new JFileChooser(file.getParentFile());
			fc.setSelectedFile(file);
		}
		fc.setFileFilter(new PushfileFilter());
		int ret = fc.showOpenDialog(getParentFrame());
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		file = fc.getSelectedFile();

		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new FileReader(file));
			Vector<Pushinfo> infos = Pushinfo.readPushinfos(rd);
			dbmodel.clearAll();
			Enumeration<Pushinfo> en = infos.elements();
			while (en.hasMoreElements()) {
				Pushinfo info = en.nextElement();
				int r = dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(r, "pushid", info.getPushid());
				dbmodel.setItemValue(r, "pushname", info.getPushname());
				dbmodel.setItemValue(r, "groupname", info.getGroupname());
				dbmodel.setItemValue(r, "level", String
						.valueOf(info.getLevel()));
				dbmodel.setItemValue(r, "callopid", info.getCallopid());
				dbmodel.setItemValue(r, "callopname", info.getCallopname());
				dbmodel.setItemValue(r, "wheres", info.getWheres());
				dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_SAVED);
			}
			tableChanged();
		} catch (Exception e) {
			logger.error("Error", e);
			errorMessage("错误", e.getMessage());
			return;
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int doSave() {
		if (on_beforesave() != 0)
			return -1;
		if (file == null) {
			JFileChooser fc = new JFileChooser(new File("."));
			fc.setFileFilter(new PushfileFilter());
			int ret = fc.showSaveDialog(getParentFrame());
			if (ret != JFileChooser.APPROVE_OPTION)
				return -1;
			file = fc.getSelectedFile();
		}
		if (!file.getName().endsWith(postfix)) {
			file = new File(file.getParentFile(), file.getName() + postfix);
		}
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(file));
			out.println("<pushs>");
			for (int r = 0; r < dbmodel.getRowCount(); r++) {
				Pushinfo pushinfo = createPushinfo(dbmodel, r);
				pushinfo.write(out);
			}
			out.println("</pushs>");
			out.close();
			out = null;
			for (int i = 0; i < dbmodel.getRowCount(); i++) {
				dbmodel.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}
			tableChanged();
			infoMessage("保存成功", "保存成功");
		} catch (Exception e) {
			logger.error("Error", e);
			errorMessage("错误", e.getMessage());
			return -1;
		} finally {
			if (out != null) {
				out.close();
			}
		}

		return 0;
	}

	private Pushinfo createPushinfo(DBTableModel dbmodel, int r) {
		Pushinfo info = new Pushinfo();
		info.setPushid(dbmodel.getItemValue(r, "pushid"));
		info.setPushname(dbmodel.getItemValue(r, "pushname"));
		info.setGroupname(dbmodel.getItemValue(r, "groupname"));
		info.setCallopid(dbmodel.getItemValue(r, "callopid"));
		info.setCallopname(dbmodel.getItemValue(r, "callopname"));
		int level = 3;
		try {
			level = Integer.parseInt(dbmodel.getItemValue(r, "level"));
		} catch (Exception e) {

		}
		info.setLevel(level);
		info.setWheres(dbmodel.getItemValue(r, "wheres"));

		return info;
	}

	class PushfileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().endsWith(postfix))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "推送平台定义文件(*.nppush)";
		}

	}

	@Override
	public void doDel(int row) {
		dbmodel.setdbStatus(row, RecordTrunk.DBSTATUS_NEW);
		super.doDel(row);
	}

	@Override
	protected JPanel createSecondtoolbar() {
		JPanel jp=new JPanel();
		JButton btn;
		btn=new JButton("当前人员ID");
		btn.setActionCommand("当前人员ID");
		btn.addActionListener(this);
		jp.add(btn);

		btn=new JButton("当前部门ID");
		btn.setActionCommand("当前部门ID");
		btn.addActionListener(this);
		jp.add(btn);

		btn=new JButton("当前角色ID");
		btn.setActionCommand("当前角色ID");
		btn.addActionListener(this);
		jp.add(btn);

		return jp;
	}	
	
	
}
