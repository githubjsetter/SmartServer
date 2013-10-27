package com.inca.npclient.system;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.env.Configer;
import com.inca.np.filedb.DirHelper;
import com.inca.np.gui.control.CComboBox;
import com.inca.np.gui.control.CComboBoxModel;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CPassword;
import com.inca.np.gui.control.CPlainTextField;
import com.inca.np.gui.control.CUpperTextField;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SendHelper;
import com.inca.npclient.Startnpclient;
import com.inca.npclient.download.DownloadManager;
import com.inca.npclient.download.LauncherManager;

public class LoginDialog extends CDialog {
	private JComboBox textIp;

	private CUpperTextField textUserid;
	private CPassword password;
	private JTabbedPane tabbedpane;
	private CComboBox cbDept;
	private CComboBox cbRole;

	private CComboBox cbMacs;

	public LoginDialog() {
		super(Startnpclient.startdlg, "系统登录", true);
		initDialog();
		DefaultNPParam.logindlg = this;
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	public LoginDialog(Frame parent) throws HeadlessException {
		super((Frame) parent, "系统登录", true);
		initDialog();
		DefaultNPParam.logindlg = this;
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 250);
	}

	/**
	 * 人员登录
	 */
	private void initDialog() {
		Container cp = getContentPane();
		JPanel mainp = new JPanel();
		cp.add(mainp);
		mainp.setLayout(new BorderLayout());

		tabbedpane = new JTabbedPane();
		mainp.add(tabbedpane, BorderLayout.CENTER);

		// loginpanel

		JPanel loginPanel = createLoginPanel();
		JPanel rolePanel = createRolePanel();

		tabbedpane.add("登录", loginPanel);
		tabbedpane.add("部门角色", rolePanel);

		tabbedpane.setEnabledAt(0, true);
		tabbedpane.setEnabledAt(1, false);

		localScreenCenter();

		loadConfigvalue();
	}

	Configer config = new Configer(new File("conf/sysclient.properties"));

	private void loadConfigvalue() {
		iparraylist = new ArrayList<String>();
		String curip = config.get("ip");
		if (curip == null)
			curip = "";
		if (curip.length() > 0) {
			iparraylist.add(curip);
		}

		File f = new File("conf/serverip");
		if (f.exists()) {
			try {
				BufferedReader rd = new BufferedReader(new FileReader(f));

				for (int i = 0; i < 10; i++) {
					String line = rd.readLine();
					if (line == null)
						break;
					if (line.length() == 0) {
						continue;
					}
					if (line.equals(curip))
						continue;
					iparraylist.add(line);
				}

				rd.close();
				String ips[] = new String[iparraylist.size()];
				iparraylist.toArray(ips);
				IpcomboboxModel model = new IpcomboboxModel(ips);
				textIp.setModel(model);

				if (iparraylist.size() > 0)
					textIp.setSelectedIndex(0);

			} catch (Exception e) {
				logger.error("error", e);
			}
		} else {

			String ip = config.get("ip");
			if (ip != null) {
				textIp.setSelectedItem(ip);
			}
		}
		f = new File("conf/webcontext");
		if (f.exists()) {
			try {
				BufferedReader rd = new BufferedReader(new FileReader(f));
				DefaultNPParam.prodcontext = rd.readLine();
				rd.close();
			} catch (Exception e) {
				logger.error("error", e);
			}
		}

		String userid = config.get("userid");
		if (userid != null) {
			textUserid.setText(userid);
		}
	}

	private JPanel createLoginPanel() {
		JPanel gridjp = new JPanel();

		Dimension textsize = new Dimension(114, 27);

		GridBagLayout g = new GridBagLayout();
		gridjp.setLayout(g);

		JLabel lb = new JLabel("服务器地址");
		lb.setHorizontalAlignment(JLabel.RIGHT);

		gridjp.add(lb, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		textIp = new JComboBox();
		textIp.setEditable(true);
		textIp.setSelectedItem("127.0.0.1");
		textIp.setPreferredSize(textsize);
		addEnterkeyTraver(textIp);
		gridjp.add(textIp, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		lb = new JLabel("用户ID");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		gridjp.add(lb, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		textUserid = new CUpperTextField();
		textUserid.setText("");
		textUserid.setPreferredSize(textsize);
		addEnterkeyTraver(textUserid);
		gridjp.add(textUserid, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		lb = new JLabel("密码");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		gridjp.add(lb, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		password = new CPassword(10);
		password.setPreferredSize(textsize);
		addEnterkeyTraver(password);
		gridjp.add(password, new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		lb = new JLabel("网卡地址");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		gridjp.add(lb, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		cbMacs = new CComboBox(getMacaddress());
		cbMacs.setPreferredSize(textsize);
		addEnterkeyTraver(cbMacs);
		gridjp.add(cbMacs, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		JButton btn;
		btn = new JButton("入网申请");
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setActionCommand("macrequest");
		btn.addActionListener(new ActionHandle());
		addEnterkeyTraver(btn);
		gridjp.add(btn, new GridBagConstraints(3, 5, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 12), 0, 0));

		JPanel toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());

		JButton btnok = new JButton("登录");
		btnok.setActionCommand("login");
		btnok.addActionListener(new ActionHandle());
		toolbar.add(btnok);
		addEnterkeyConfirm(btnok);

		/*
		 * JButton btnoffline = new JButton("离线登录");
		 * btnoffline.setActionCommand("offlinelogin");
		 * btnoffline.addActionListener(new ActionHandle());
		 * toolbar.add(btnoffline); addEnterkeyConfirm(btnoffline);
		 */
		JButton btncancel = new JButton("取消");
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(new ActionHandle());
		toolbar.add(btncancel);
		addEnterkeyTraver(btncancel);

		JButton btnnetsetup = new JButton("通讯设置");
		btnnetsetup.setActionCommand("netsetup");
		btnnetsetup.addActionListener(new ActionHandle());
		toolbar.add(btnnetsetup);
		addEnterkeyTraver(btnnetsetup);

		/*
		 * JButton btndownload = new JButton("重新下载");
		 * btndownload.setActionCommand("download");
		 * btndownload.addActionListener(new ActionHandle());
		 * toolbar.add(btndownload); addEnterkeyTraver(btndownload);
		 */
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(gridjp, BorderLayout.CENTER);
		jp.add(toolbar, BorderLayout.SOUTH);

		return jp;
	}

	/**
	 * 显示当前的门店的角色 <p/> //门店 <p/> //职务（角色）
	 * 
	 * @return
	 */
	private JPanel createRolePanel() {
		JPanel gridjp = new JPanel();

		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);

		gridjp.setLayout(g);

		c.gridwidth = GridBagConstraints.RELATIVE;
		JLabel lb = new JLabel("选择部门");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(lb, c);
		gridjp.add(lb);

		cbDept = new CComboBox();
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(cbDept, c);
		gridjp.add(cbDept);
		addEnterkeyTraver(cbDept);

		c.gridwidth = GridBagConstraints.RELATIVE;
		lb = new JLabel("选择角色");
		lb.setHorizontalAlignment(JLabel.RIGHT);
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(lb, c);
		gridjp.add(lb);

		cbRole = new CComboBox();
		// cbBanci.setEnabled(false); // 初始化为不可用
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(cbRole, c);
		gridjp.add(cbRole);
		addEnterkeyTraver(cbRole);

		// setDefaultbanci();

		JPanel bottompane = new JPanel();
		bottompane.setLayout(new FlowLayout());

		JButton btnok = new JButton("确定");
		btnok.setActionCommand("selectrole");
		btnok.addActionListener(new ActionHandle());
		bottompane.add(btnok);
		addEnterkeyConfirm(btnok);

		JButton btncancel = new JButton("取消");
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(new ActionHandle());
		bottompane.add(btncancel);
		addEnterkeyTraver(btncancel);

		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(gridjp, BorderLayout.CENTER);
		jp.add(bottompane, BorderLayout.SOUTH);

		return jp;
	}

	protected void localScreenCenter() {
		Dimension screensize = this.getToolkit().getScreenSize();
		Dimension size = this.getPreferredSize();
		double x = (screensize.getWidth() - size.getWidth()) / 2;
		double y = (screensize.getHeight() - size.getHeight()) / 2;

		this.setLocation((int) x, (int) y);
	}

	/*
	 * public Dimension getPreferredSize() { return new Dimension(300, 400); }
	 */

	public void setVisible(boolean b) {
		textUserid.requestFocus();
		/*
		 * if(Startnpclient.startdlg!=null){ Startnpclient.startdlg.dispose(); }
		 */super.setVisible(b);
	}

	private Category logger = Category.getInstance(LoginDialog.class);

	private ArrayList<String> iparraylist;

	class ActionHandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("login")) {
				try {
					setWaitCursor();
					doLogin();
				} catch (Exception e1) {
					logger.error("login error", e1);
				} finally {
					setDefaultCursor();
				}
			} else if (cmd.equals("cancel")) {
				LoginDialog.this.dispose();
			} else if (cmd.equals("selectrole")) {
				try {
					setWaitCursor();
					doSelectrole();
					if (Startnpclient.startdlg != null) {
						Startnpclient.startdlg.dispose();
					}
				} catch (Exception e1) {
					logger.error("login error", e1);
				} finally {
					setDefaultCursor();
				}
			} else if (cmd.equals("offlinelogin")) {
				doOfflinelogin();
			} else if (cmd.equals("download")) {
				String shellcmd = "explorer http://" + textIp.getSelectedItem()
						+ "/ngpcs/ngpcs.jnlp";
				try {
					Runtime.getRuntime().exec(shellcmd);
					Thread.sleep(3000);
					System.exit(0);
				} catch (Exception she) {
					logger.error(shellcmd + " ERROR", she);
				}
			} else if (cmd.equals("macrequest")) {
				macRequest();
			} else if (cmd.equals("netsetup")) {
				netSetup();
			}
		}

	}

	/**
	 * 网络设置，设置代理。
	 */
	void netSetup() {
		NetsetupDlg dlg = new NetsetupDlg(this, config);
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * 向服务器发送请求
	 */
	void macRequest() {
		String memo = JOptionPane
				.showInputDialog(this, "输入您的身份和申请理由", "我是....");
		if (memo == null)
			return;
		String url = "http://" + textIp.getSelectedItem() + "/"
				+ DefaultNPParam.prodcontext + "/clientrequest.do";
		DefaultNPParam.defaultappsvrurl = url;
		ClientRequest req = new ClientRequest("npclient:macrequest");
		ParamCommand pcmd = new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("memo", memo);
		pcmd.addParam("mac", (String) cbMacs.getSelectedItem());

		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			ServerResponse resp = SendHelper.sendRequest(req);
			String cmd = resp.getCommand();
			if (cmd.startsWith("+OK")) {
				JOptionPane.showMessageDialog(this, cmd, "申请成功",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, cmd, "申请失败",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "失败",
					JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	private void doSelectrole() throws Exception {
		String roleid = cbRole.getValue();

		if (roleid == null) {
			// 初始安装时,没有任何ROLEID
			roleid = "";
		} else {
			if (roleid.length() == 0) {
				JOptionPane.showMessageDialog(this, "请选择一个角色", "提示",
						JOptionPane.WARNING_MESSAGE);
				return;

			}
		}
		Userruninfo currentUser = ClientUserManager.getCurrentUser();
		currentUser.setRoleid(roleid);
		currentUser.setRolename((String) cbRole.getSelectedItem());

		currentUser.setDeptid(cbDept.getValue());
		currentUser.setDeptname((String) cbDept.getSelectedItem());

		// 需要通知服务器。
		ClientRequest req = new ClientRequest("npclient:setroleid");
		ParamCommand paramcmd = new ParamCommand();
		paramcmd.addParam("roleid", currentUser.getRoleid());
		paramcmd.addParam("rolename", currentUser.getRolename());
		paramcmd.addParam("deptid", currentUser.getDeptid());
		paramcmd.addParam("deptname", currentUser.getDeptname());
		req.addCommand(paramcmd);

		ServerResponse svrresp = SendHelper.sendRequest(req);

		DBTableModel opdbmodel = null;
		StringCommand cmd = (StringCommand) svrresp.commandAt(0);
		if (cmd.getString().startsWith("+OK")) {
			DataCommand dcmd = (DataCommand) svrresp.commandAt(1);
			opdbmodel = dcmd.getDbmodel();
			NpopManager.getInst().build(opdbmodel);
			tabbedpane.setEnabledAt(0, true);
			tabbedpane.setEnabledAt(1, false);
			tabbedpane.setSelectedIndex(0);
			LoginDialog.this.dispose();
			Clientframe dlg = new Clientframe(null);
			dlg.pack();
			dlg.setVisible(true);

			// 启动ping thread
			DefaultNPParam.startPingthread();
		} else {
			warnMessage("错误", "选择部门没有成功" + cmd.getString());
		}
	}

	private void doOfflinelogin() {
		// 启动ping
		/*
		 * String url = "http://" + textIp.getText() + "/" +
		 * DefaultNPParam.prodcontext + "/clientrequest.do";
		 * DefaultNPParam.defaultappsvrurl = url;
		 * DefaultNPParam.startPingthread(); OpArrayList<String> opnames = new
		 * OpArrayList<String>(0, true); DefaultNPParam.topopgroup =
		 * NgpcsOps.createNgpcsOps(opnames);
		 * 
		 * Userruninfo currentUser = ClientUserManager.getCurrentUser();
		 * Configer conf = new Configer(new File("conf/ngpcs.properties"));
		 * String palcepointid = conf.get("placepointid"); String useday =
		 * conf.get("useday"); currentUser.setPlacepointid(palcepointid); int
		 * tmpuseday = 0; try { tmpuseday = Integer.parseInt(useday); } catch
		 * (Exception e) { e.printStackTrace(); logger.error("错误", e);
		 * JOptionPane.showMessageDialog(this, "逻辑日错误!"); return; }
		 * currentUser.setUseday(tmpuseday); String banci = conf.get("banci");
		 * currentUser.setBanci(banci); String userid = conf.get("userid");
		 * currentUser.setUserid(userid); this.dispose(); SelectopFrame dlg =
		 * new SelectopFrame(null); dlg.pack(); dlg.setVisible(true);
		 */
	}

	private void doLogin() throws Exception {
		config.put("ip", (String) textIp.getSelectedItem());
		config.put("userid", textUserid.getText());

		// 保存
		File f = new File("conf/serverip");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(f));
			String curip = (String) textIp.getSelectedItem();
			out.println(curip);

			for (int i = 0; i < iparraylist.size(); i++) {
				String ip = iparraylist.get(i);
				if (!ip.equals(curip)) {
					out.println(ip);
				}
			}

		} finally {
			if (out != null) {
				out.close();
			}
		}

		String v = config.get("useproxy");
		if ("true".equals(v)) {
			Properties prop = System.getProperties();
			prop.put("http.proxyHost", config.get("proxyip"));
			prop.put("http.proxyPort", config.get("proxyport"));
		} else {
			Properties prop = System.getProperties();
			prop.remove("http.proxyHost");
			prop.remove("http.proxyPort");
		}
		config.saveConfigfile();

		StringCommand logincmd = new StringCommand("npclient:login");
		ParamCommand paramcmd = new ParamCommand();
		paramcmd.addParam("userid", textUserid.getText());
		paramcmd.addParam("password", MD5Helper.MD5(new String(password
				.getPassword())));
		paramcmd.addParam("mac", (String) cbMacs.getSelectedItem());

		String url = "http://" + textIp.getSelectedItem() + "/"
				+ DefaultNPParam.prodcontext + "/clientrequest.do";
		DefaultNPParam.defaultappsvrurl = url;

		// 启动NPSERVER客户端自动更新MODEL
		if (DefaultNPParam.develop == 0) {
			try {
				if (downloadLauncherjar()) {
					logger.info("从服务器更新了运行环境,需要重新启动.");
					SystemexitThread.addExitproc(new ExitRestartProc());
					JOptionPane.showMessageDialog(this, "从服务器更新了运行环境,需要重新启动.");
					System.exit(0);
					return;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "下载运行环境模块失败"
						+ e.getMessage() + "，请再试");
				return;
			}
		}
		// 2008-7-15 加下载pubsrv
		try {
			DownloadManager.getInst().prepareModulejar("pubsrv", "公共部件管理单据初始化");
		} catch (Exception pubee) {
			logger.error("error", pubee);
		}

		logger.info("check launcher file ok,send login command");
		File libdir = new File("lib");
		DirHelper.clearZerosizefile(libdir);

		ClientRequest req = new ClientRequest();
		req.addCommand(logincmd);
		req.addCommand(paramcmd);

		ServerResponse svrresp = null;
		try {
			svrresp = SendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("login error", e);
			warnMessage("登录失败", e.getMessage());
			return;
		}

		StringCommand cmd = (StringCommand) svrresp.commandAt(0);
		String s = cmd.getString();
		if (s.startsWith("+")) {

			// System.out.println(s);
			// 登录成功

			ParamCommand userinfocmd = (ParamCommand) svrresp.commandAt(1);
			Userruninfo userruninfo = new Userruninfo();
			userruninfo.setUserid(userinfocmd.getValue("userid"));
			userruninfo.setUsername(userinfocmd.getValue("username"));
			userruninfo.setDeptid(userinfocmd.getValue("deptid"));
			userruninfo.setDeptname(userinfocmd.getValue("deptname"));
			userruninfo.setRoleid(userinfocmd.getValue("roleid"));
			userruninfo.setAuthstring(userinfocmd.getValue("authstring"));
			String strexternal=userinfocmd.getValue("extern");
			if(strexternal==null)strexternal="false";
			userruninfo.setExternal(strexternal.equals("true"));

			ClientUserManager.setCurrentUser(userruninfo);

			// 设置好这次通信用的authstring
			RemoteConnector.setAuthstring(userinfocmd.getValue("authstring"));

			// 部门
			DataCommand datacmd = (DataCommand) svrresp.commandAt(2);
			DBTableModel deptmodel = datacmd.getDbmodel();
			if (deptmodel.getRowCount() > 0) {
				userruninfo.setDeptname(deptmodel.getItemValue(0, "deptname"));
				CComboBoxModel cbmodel = new CComboBoxModel(deptmodel,
						"deptid", "deptname");
				cbDept.setModel(cbmodel);
				cbDept.setSelectedIndex(1);
			}
			cbDept.setEnabled(false);

			datacmd = (DataCommand) svrresp.commandAt(3);
			DBTableModel rolemodel = datacmd.getDbmodel();
			boolean autoselectrole = false;
			if (rolemodel.getRowCount() == 1) {
				autoselectrole = true;

			}

			CComboBoxModel cbmodel = new CComboBoxModel(rolemodel, "roleid",
					"rolename");
			cbRole.setModel(cbmodel);
			if (rolemodel.getRowCount() > 1) {
				cbRole.setSelectedIndex(1);
			}

			// entry
			ClientUserManager.getCurrentUser().getEntryid();

			tabbedpane.setSelectedIndex(1);
			tabbedpane.setEnabledAt(0, false);
			tabbedpane.setEnabledAt(1, true);

			if (autoselectrole) {
				doSelectrole();
			}
		} else {
			// 登录失败
			warnMessage("登录失败", s);
			return;

		}

	}

	protected void enterkeyConfirm() {
		if (tabbedpane.getSelectedIndex() == 0) {
			try {
				doLogin();
			} catch (Exception e) {
				logger.error("error", e);
			}
		} else {
			try {
				doSelectrole();
			} catch (Exception e) {
				logger.error("error", e);
			}
		}
	}

	protected void warnMessage(String title, String msg) {
		JOptionPane.showMessageDialog(this, msg, title,
				JOptionPane.WARNING_MESSAGE);

	}

	public static void startRun() {
		main(new String[0]);
	}

	void setWaitCursor() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	void setDefaultCursor() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public static String[] getMacaddress() {
		//String debugs[]=new String[1];
		//debugs[0]="00-13-77-FA-3E-C1";
		//if(true)return debugs;
		
		
		Category logger = Category.getInstance(LoginDialog.class);
		Pattern macpat=Pattern.compile("[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]-[0-9A-F][0-9A-F]");

		Runtime rt = Runtime.getRuntime();
		try {
			Process proc = rt.exec("ipconfig /all");
			InputStream in = null;
			in = proc.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(in,
					"gbk"));
			
			//File f=new File("c://a//badmac.txt");
			//BufferedReader rd=new BufferedReader(new FileReader(f));
			
			String line = null;
			String mac = null;
			String ipaddress = null;
			int p;
			ArrayList<String> ar = new ArrayList<String>();
			while ((line = rd.readLine()) != null) {
				Matcher m=macpat.matcher(line);
				if(!m.find()){
					continue;
				}
				
				String tmpmac=line.substring(m.start(),m.end());
				ar.add(tmpmac);

/*				if (line.indexOf("Physical Address") >= 0) {
					mac = line.substring("Physical Address".length()).trim();
					p = mac.indexOf(":") + 1;
					mac = mac.substring(p).trim();
				} else if (line.indexOf("物理地址") >= 0) {
					mac = line.substring("物理地址".length()).trim();
					p = mac.indexOf(":") + 1;
					mac = mac.substring(p).trim();
				}
				if (line.indexOf("IP Address") >= 0) {
					ipaddress = line.substring("IP Address".length()).trim();
					p = ipaddress.indexOf(":") + 1;
					ipaddress = ipaddress.substring(p).trim();
					// System.out.println(ipaddress+","+mac);
					ar.add(mac + "(" + ipaddress + ")");
				} else if (line.indexOf("IPv4 地址") >= 0) {
					ipaddress = line.substring("IPv4 地址".length()).trim();
					p = ipaddress.indexOf(":") + 1;
					ipaddress = ipaddress.substring(p).trim();
					// System.out.println(ipaddress+","+mac);
					ar.add(mac + "(" + ipaddress + ")");
				}
*/
				}

			String macs[] = new String[ar.size()];
			ar.toArray(macs);
			return macs;
		} catch (Exception e) {
			logger.error("ERROR", e);
			return null;
		}

	}

	/**
	 * 下载客户端.
	 * 
	 * @return true说明更新过了,需要重启.
	 */
	boolean downloadLauncherjar() throws Exception {
		LauncherManager lm = LauncherManager.getInst();
		lm.loadLuncherjars();
		return lm.isHasdownload();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (Startnpclient.startdlg != null
				&& Startnpclient.startdlg.isVisible()) {
			javax.swing.JFrame tmpframe = Startnpclient.startdlg;
			Startnpclient.startdlg = null;
			tmpframe.dispose();
		}
	}

	/*
	 * boolean downloadNpserverclient() { // 下载模块信息 DownloadManager dlm =
	 * DownloadManager.getInst(); if (!dlm.isNeeddownload("npserver",
	 * "npserver")) return true;
	 * 
	 * boolean ret = dlm.downloadModule("npserver", "npserver", true); if (!ret) {
	 * JOptionPane.showMessageDialog(this, "下载npserver客户端失败:" +
	 * dlm.getErrormessage()); } return ret; }
	 */
	public static void main(String[] args) {
		LoginDialog dlg = new LoginDialog();
		dlg.pack();
		dlg.setVisible(true);
	}

	class IpcomboboxModel extends DefaultComboBoxModel {
		IpcomboboxModel(String[] ips) {
			super(ips);
		}
	}

}
