package com.smart.adminclient.modulemgr;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.smart.adminclient.auth.AdminSendHelper;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;

public class ModulemgrSte extends CSteModel {

	Category logger = Category.getInstance(ModulemgrSte.class);

	public ModulemgrSte(CFrame frame) throws HeadlessException {
		super(frame, "产品模块管理");
	}

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return "np_module";
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return "nothing";
	}

	@Override
	protected void loadDBColumnInfos() {
		ModuleDbmodel modulemodel = new ModuleDbmodel();
		formcolumndisplayinfos = modulemodel.getDisplaycolumninfos();
	}

	@Override
	protected CStetoolbar createToolbar() {
		// TODO Auto-generated method stub
		return new Toolbar(this);
	}

	@Override
	public void doQuery() {
		ClientRequest req = new ClientRequest("npserver:getprodinfo");
		AdminSendHelper sender = new AdminSendHelper();
		try {
			setWaitCursor();
			this.getDBtableModel().clearAll();
			ServerResponse resp = sender.sendRequest(req);
			StringCommand cmd0 = (StringCommand) resp.commandAt(0);
			String msg = cmd0.getString();
			if (!msg.startsWith("+OK")) {
				errorMessage("ERROR", msg);
				return;
			}
			DataCommand cmd1 = (DataCommand) resp.commandAt(1);
			DBTableModel proddbmodel = cmd1.getDbmodel();
			// getDBtableModel().bindMemds(proddbmodel);

			DataCommand cmd2 = (DataCommand) resp.commandAt(2);
			DBTableModel modulemgrdbmodel = cmd2.getDbmodel();
			getDBtableModel().bindMemds(modulemgrdbmodel);

			getDBtableModel().sort(new String[] { "prodname", "modulename" },
					true);
			getSumdbmodel().fireDatachanged();
			tableChanged();
			table.autoSize();

		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("ERROR", e.getMessage());
		} finally {
			setDefaultCursor();
		}
	}

	/**
	 * 安装模块
	 */
	void installModulefile() {
		InstallModuleDlg dlg = new InstallModuleDlg();
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;

		File licensefile = new File(dlg.getFilepath());

		Moduleuploader mu = new Moduleuploader();
		setWaitCursor();
		// 上传文件
		try {
			if(!mu.installModule(licensefile)){
				errorMessage("ERROR", mu.getErrormessage());
				return;
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("ERROR", e.getMessage());
			return;
		} finally {
			setDefaultCursor();
		}
		doQuery();
		JOptionPane.showMessageDialog(frame, "模块已安装，请重新启动服务器。");
	}

	@Override
	protected int on_actionPerformed(String command) {
		if ("upload".equals(command)) {
			installModulefile();
		}
		return super.on_actionPerformed(command);
	}

	@Override
	public int on_beforeclose() {
		return 0;
	}

	@Override
	protected int on_beforedel(int row) {
		return -1;
	}

	@Override
	protected int on_beforemodify(int row) {
		return -1;
	}

	@Override
	protected int on_beforeNew() {
		return -1;
	}

	@Override
	public int on_beforesave() {
		return -1;
	}

	class Toolbar extends CStetoolbar {

		public Toolbar(ActionListener l) {
			super(l);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean isUsebutton(String actionname) {
			return false;
		}

		@Override
		protected void createOtherButton(ActionListener listener) {
			JButton btn;
			btn = new JButton("查询已安装模块");
			btn.setActionCommand("查询");
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);

			btn = new JButton("安装产品模块");
			btn.setActionCommand("upload");
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);

			btn = new JButton("退出");
			btn.setActionCommand(CSteModel.ACTION_EXIT);
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);

		}

	}

	JFileChooser fc = null;

	class InstallModuleDlg extends CDialog {

		private JTextField textFilepath;

		InstallModuleDlg() {
			super(ModulemgrSte.this.getParentFrame(), "安装产品模块", true);
			init();
			this.localCenter();
		}

		void init() {
			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			JPanel midpanel = createMidpanel();
			cp.add(midpanel, BorderLayout.CENTER);

			JPanel bottompanel = createBottompanel();
			cp.add(bottompanel, BorderLayout.SOUTH);
		}

		private JPanel createMidpanel() {
			JPanel jp = new JPanel();
			JLabel lb = new JLabel("模块安装文件");
			jp.add(lb);

			textFilepath = new JTextField(40);
			jp.add(textFilepath);

			JButton btn = new JButton("...");
			btn.setActionCommand("choose");
			btn.addActionListener(this);
			jp.add(btn);

			return jp;
		}

		private JPanel createBottompanel() {
			JPanel jp = new JPanel();

			JButton btn = new JButton("安装");
			btn.setActionCommand("install");
			btn.addActionListener(this);
			jp.add(btn);

			btn = new JButton("取消");
			btn.setActionCommand("cancel");
			btn.addActionListener(this);
			jp.add(btn);
			return jp;
		}

		void chooseFile() {
			if (fc == null) {
				fc = new JFileChooser(new File("."));
				fc.setFileFilter(new ZipfileFilter());
			}
			int ret = fc.showOpenDialog(this);
			if (ret != JFileChooser.APPROVE_OPTION) {
				return;
			}

			try {
				checkModuleInstallfile(fc.getSelectedFile());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "警告",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			textFilepath.setText(fc.getSelectedFile().getAbsolutePath());
		}

		boolean ok = false;
		String filepath = null;

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("choose")) {
				chooseFile();
			} else if (cmd.equals("install")) {
				ok = true;
				filepath = textFilepath.getText();
				this.dispose();
			} else if (cmd.equals("cancel")) {
				ok = false;
				this.dispose();
			} else {
				super.actionPerformed(e);
			}
		}

		public boolean isOk() {
			return ok;
		}

		public String getFilepath() {
			return filepath;
		}
	}

	void checkModuleInstallfile(File f) throws Exception {
		boolean foundinstallinfo = false;
		ZipFile zipfile = new ZipFile(f);
		Enumeration<ZipEntry> en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry entry = en.nextElement();
			if (entry.getName().equals("installinfo")) {
				foundinstallinfo = true;
				break;
			}
		}
		if (!foundinstallinfo) {
			throw new Exception("不是一个模块安装文件");
		}
	}

	class ZipfileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().toLowerCase().endsWith(".zip"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "产品模块安装文件(*.zip)";
		}

	}
}
