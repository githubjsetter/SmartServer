package com.inca.npclient.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.log4j.Category;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.RepasswordDialog;
import com.inca.np.auth.RunopManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.client.RemoteConnector;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.filesync.UploaderFrame;
import com.inca.np.gui.control.CFormlayout;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.runop.Opgroup;
import com.inca.np.gui.runop.Opnode;
import com.inca.np.gui.runop.RunlogPanel;
import com.inca.np.gui.runop.Runmessage;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.Zxzipdownloader;
import com.inca.np.image.IconFactory;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.StringUtil;
import com.inca.npbi.client.design.BIReportFrame;
import com.inca.npclient.download.DownloadManager;
import com.inca.npclient.system.tabheadw.TRootpane;
import com.inca.npserver.pushplat.client.BackgroundRunner;
import com.inca.npserver.pushplat.client.Pushpane;
import com.inca.npx.mde.MdeframeGeneral;
import com.inca.npx.ste.ReportframeGeneral;
import com.inca.npx.ste.SteframeGeneral;

public class Clientframe extends CFrame {
	private static Clientframe instance = null;
	private JTree tree;
	private JList list;
	private JComponent activecomp;
	private boolean ok = false;
	private RunlogPanel runlogPanel;
	private JLabel lbstatus;
	private JLabel lbmem;
	private JButton btnlogin;
	SimpleDateFormat datef = new SimpleDateFormat("HH:mm:ss");
	private JLabel lbinfo;
	private DBTableModel opdbmodel;
	private CTable optable;
	private Vector<Tabpage> tabpages = new Vector<Tabpage>();

	/*
	 * Vector<JFrame> frametable = new Vector<JFrame>(); HashMap<String,
	 * JFrame> framemap = new HashMap<String, JFrame>();
	 */

	Vector<Opnode> runningop = new Vector<Opnode>();

	Category logger = Category.getInstance(Clientframe.class);

	public Clientframe(Frame owner) throws HeadlessException {
		super("NP FRAME");

		if (getRootPane() instanceof TRootpane) {
			TRootpane rp = (TRootpane) getRootPane();
			rp.init();
		}

		// System.out.println("SelectopFrame instance="+instance);
		if (instance != null) {
			instance.dispose();
		}
		instance = this;
		initFrame();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension scrsize = this.getToolkit().getScreenSize();
		this.setPreferredSize(new Dimension((int) scrsize.getWidth(),
				(int) scrsize.getHeight() - 25));
		this.setLocation(0, 0);

		RunopManager.setMainmenuframe(this);

		StatusThread t = new StatusThread();
		t.setDaemon(true);
		t.start();
	}

	private void initFrame() {
		Container cp = this.getContentPane();
		JComponent jcp = (JComponent) cp;

		setHotkey((JComponent) cp);
		// cp.setLayout(new BorderLayout());

		FrameLayoutMgr forml = new FrameLayoutMgr();
		cp.setLayout(forml);

		navpane = new NavigatePanel();
		add(navpane, BorderLayout.CENTER);
		oldjp = navpane;

		Tabpage tabpage = new Tabpage();
		tabpage.opid = "0";
		tabpage.oprootpane = oldjp;
		tabpages.add(tabpage);
	}

	JPanel createStatusPane() {
		JPanel jp = new JPanel();
		// BoxLayout layout = new BoxLayout(jp, BoxLayout.X_AXIS);
		// jp.setLayout(layout);
		CFormlayout layout = new CFormlayout(2, 2);
		jp.setLayout(layout);

		lbstatus = new JLabel("");
		// lbstatus.setPreferredSize(new Dimension(200, 27));
		lbstatus.setHorizontalAlignment(JLabel.LEFT);
		jp.add(lbstatus);

		lbmem = new JLabel("mem");
		// lbmem.setPreferredSize(new Dimension(600, 27));
		lbmem.setHorizontalAlignment(JLabel.RIGHT);
		jp.add(lbmem);
		return jp;
	}

	public void showStatus(String msg) {
		if (EventQueue.isDispatchThread()) {
			lbstatus.setText(msg);
		} else {
			final String smsg = msg;
			Runnable r = new Runnable() {
				public void run() {
					showStatus(smsg);
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}

	public static void setStatus(String status) {
		if (instance == null)
			return;
		instance.showStatus(status);
	}

	String EVENT_ENTERKEY = "vkenter";

	void addEnterkeyListener() {
		JComponent cp = (JComponent) getContentPane();
		InputMap inputmap = cp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		inputmap.put(vkenter, EVENT_ENTERKEY);
		cp.getActionMap().put(EVENT_ENTERKEY, new DialogAction(EVENT_ENTERKEY));

	}

	class DialogAction extends AbstractAction {
		public DialogAction(String name) {
			super(name);
			this.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(EVENT_ENTERKEY)) {
				if (activecomp != null && activecomp == tree) {
					Runnable runner = new Runnable() {
						public void run() {
							/*
							 * list.requestFocus(); if
							 * (list.getModel().getSize() > 0) {
							 * list.setSelectedIndex(0); }
							 */
							optable.requestFocus();
							if (optable.getRowCount() > 0) {
								optable.getSelectionModel()
										.setSelectionInterval(0, 0);
							}
						}
					};
					SwingUtilities.invokeLater(runner);
				} else if (activecomp != null && activecomp == optable) {
					runOp();
				}
			}
		}
	}

	JScrollPane createTreePanel() {
		// Opgroup topgroup = Opgroup.createDemo();

		Opgroup topgroup = NpopManager.getInst().getTopgroup();
		if (topgroup == null) {
			topgroup = new Opgroup("未定义");
		}

		DefaultMutableTreeNode toptreenode = new DefaultMutableTreeNode(
				topgroup);
		// DefaultMutableTreeNode runningoptreecode = new
		// DefaultMutableTreeNode(
		// "正在运行功能");
		// toptreenode.add(runningoptreecode);
		addNode(toptreenode, topgroup);

		tree = new JTree(toptreenode);
		tree.addTreeSelectionListener(new TreeselectionHandle());
		tree.addFocusListener(new FocusHandle());

		JScrollPane jp = new JScrollPane(tree);
		return jp;
	}

	class MouseHandle implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				runOp();
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	class FocusHandle implements FocusListener {

		public void focusGained(FocusEvent e) {
			activecomp = (JComponent) e.getSource();
			// System.out.println("focusGained "+e.getSource());
		}

		public void focusLost(FocusEvent e) {
		}
	}

	/**
	 * 设置上部的人员显示信息
	 * 
	 * @param s
	 */
	public void setInfoMessage(String s) {
		lbinfo.setText(s);
	}

	class ButtonEventHandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("ok")) {
				runOp();
			} else if (cmd.equals("login")) {
				onLogin();
			} else if (cmd.equals("close")) {
				onClose();
			} else if (cmd.equals("repassword")) {
				onRepassword();
			} else if (cmd.equals("freemem")) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Runtime.getRuntime().gc();
				checkStatus();
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

	}

	/**
	 * 重设密码
	 */
	void onRepassword() {
		RepasswordDialog dlg = new RepasswordDialog(this);
		dlg.pack();
		dlg.setVisible(true);

	}

	void onLogin() {

		if (instance != null && instance.getRunningopCount() > 0) {
			JOptionPane.showMessageDialog(this, "还有功能正在运行,请关闭所有功能后再重新登录", "提示",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		JDialog dlg = DefaultNPParam.logindlg;
		dlg.pack();
		dlg.setVisible(true);
	}

	/**
	 * 取正在运行的功能数量
	 * 
	 * @return
	 */
	public int getRunningopCount() {
		return runningop.size();
	}

	public static Clientframe getClientframe() {
		return instance;
	}

	void runOp() {
		// 选择功能
		int row = optable.getRow();
		if (row < 0) {
			return;
		}

		String opid = opdbmodel.getItemValue(row, "opid");
		runOp(opid, false);
	}
	public COpframe runOp(String opid, boolean runbackground) {
		return runOp(opid,runbackground,true);
	}
	public COpframe runOp(String opid, boolean runbackground,boolean checkauth) {
		Opnode selectedopnode = NpopManager.getInst().getOpnode(opid);
		if (selectedopnode == null) {
			selectedopnode = this.getRunningopnode(opid);
		}
		// 检查是否已运行.如果运行切换到那页.
		if (!runbackground) {
			for (int i = 1; i < tabpages.size(); i++) {
				Tabpage tabpage = tabpages.elementAt(i);
				if (tabpage.opid.equals(opid)) {
					((TRootpane) getRootPane()).setActiveindex(i);
					onActiveIndex(i);
					return tabpage.opframe;
				}
			}
		}
		
		if(selectedopnode==null){
			if(checkauth){
				setStatus( "无权运行功能,opid="+opid);
				return null;
			}else{
				//要从服务器查询功能
				String sql="select opid,opname,classname,prodname,modulename from np_op where opid="+opid;
				RemotesqlHelper rsh=new RemotesqlHelper();
				try {
					DBTableModel  dm = rsh.doSelect(sql, 0, 1);
					if(dm.getRowCount()==0){
						logger.error("找不到opid="+opid+"的登记");
						return null;
					}
					String opname=dm.getItemValue(0, "opname");
					String classname=dm.getItemValue(0, "classname");
					String prodname=dm.getItemValue(0, "prodname");
					String modulename=dm.getItemValue(0, "modulename");
					selectedopnode=new Opnode(opid, opname);
					selectedopnode.setClassname(classname);
					selectedopnode.setProdname(prodname);
					selectedopnode.setModulename(modulename);
				} catch (Exception e) {
					logger.error("error",e);
					return null;
				}
			}
		}

		String classname = selectedopnode.getClassname();
		logger.debug("runopid="+opid+",runbackgroud="+runbackground+",classname="+classname);
		try {
			if (!runbackground) {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			// 是否是专项？
			if (classname.equals("stegeneral")
					|| classname.equals("mdegeneral")
					|| classname.equals("reportgeneral")) {
				// 下载STE专项
				Zxzipdownloader zxzipdl = new Zxzipdownloader();
				try {
					File zxzipfile = DownloadManager.getInst().getZxfile(opid);
					if (classname.equals("stegeneral")) {
						SteframeGeneral frm = new SteframeGeneral(zxzipfile);
						frm.pack();
						if (!runbackground) {
							// frm.setVisible(true);
							startRun(opid, frm);
						}
						return frm;
					} else if (classname.equals("mdegeneral")) {
						MdeframeGeneral frm = new MdeframeGeneral(zxzipfile);
						frm.pack();
						if (!runbackground) {
							// frm.setVisible(true);
							startRun(opid, frm);
						}
						return frm;
					} else if (classname.equals("reportgeneral")) {
						ReportframeGeneral frm = new ReportframeGeneral(
								zxzipfile);
						frm.pack();
						if (!runbackground) {
							// frm.setVisible(true);
							startRun(opid, frm);
						}
						return frm;
					}
				} catch (Exception e) {
					logger.error("ERROR", e);
					errorMessage("错误", e.getMessage());
					return null;
				}
			} else if ("bireport".equals(classname)) {
				BIReportFrame birptfrm = new BIReportFrame();
				if(runbackground){
					birptfrm.setAutoquery(false);
				}
				birptfrm.pack();
				birptfrm.setOpid(opid);
				if (!runbackground) {
					// birptfrm.setVisible(true);
					startRun(opid, birptfrm);
				}
				return birptfrm;
			}
			// 要加载一个新的类.需要检查模块的JAR文件
			Class<?> aClass = null;
			if (DefaultNPParam.develop == 0) {
				String prodname = selectedopnode.getProdname();
				String modulename = selectedopnode.getModulename();
				DownloadManager dlm = DownloadManager.getInst();
				try {
					dlm.prepareModulejar(prodname, modulename);
				} catch (Exception e) {
					logger.error("e", e);
					errorMessage("错误", e.getMessage());
					return null;
				}
			}
			aClass = Class.forName(classname, true, DefaultNPParam.classloader);
			Object instance = aClass.newInstance();
			COpframe frm = (COpframe) instance;
			if(ClientUserManager.getCurrentUser().isExternal() && !frm.isExternal()){
				if(runbackground==false){
					warnMessage("外部人员无权使用本功能", "你是外部人员,无权使用内部的功能");
				}
				return null;
			}
			frm.setOpid(selectedopnode.getOpid());
			frm.pack();
			if (!runbackground) {
				// frm.setVisible(true);
				startRun(opid, frm);
			}
			logger.debug("Return new frame="+frm);
			return frm;

		} catch (ClassNotFoundException cnfe) {
			logger.error("ERROR", cnfe);
			errorMessage("加载功能失败", "找不到类：" + cnfe.getMessage()
					+ "。可能是模块未安装或未授权");
			return null;
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("加载功能失败", e.getMessage());
			return null;
		} finally {
			if (!runbackground) {
				this.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	public BIReportFrame runBIreport(String opid, boolean runbackground,boolean autoquery) {
		Opnode selectedopnode = NpopManager.getInst().getOpnode(opid);
		if (selectedopnode == null) {
			selectedopnode = this.getRunningopnode(opid);
		}
		// 检查是否已运行.如果运行切换到那页.
		if (!runbackground) {
			for (int i = 1; i < tabpages.size(); i++) {
				Tabpage tabpage = tabpages.elementAt(i);
				if (tabpage.opid.equals(opid)) {
					((TRootpane) getRootPane()).setActiveindex(i);
					onActiveIndex(i);
					return (BIReportFrame)tabpage.opframe;
				}
			}
		}

		String classname = selectedopnode.getClassname();
		logger.debug("runopid="+opid+",runbackgroud="+runbackground+",classname="+classname);
		try {
			if (!runbackground) {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
				BIReportFrame birptfrm = new BIReportFrame();
				birptfrm.setAutoquery(autoquery);
				birptfrm.pack();
				birptfrm.setOpid(opid);
				if (!runbackground) {
					// birptfrm.setVisible(true);
					startRun(opid, birptfrm);
				}
				return birptfrm;

		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("加载功能失败", e.getMessage());
			return null;
		} finally {
			if (!runbackground) {
				this.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	public void startRun(String opid, COpframe frm) {
		logger.debug("startRun ,opid="+opid+",frm="+frm);
		Opnode opnode = NpopManager.getInst().getOpnode(opid);
		if (opnode == null) {
			opnode = new Opnode(opid, opid);
		}
		opnode.setRunningframe(frm);
		runningop.add(opnode);
		RemoteConnector.setActiveopid(opid);
		String classname = frm.getClass().getName();
		frm.addWindowListener(new WinstateListener(classname));

		// 加tab页
		addPage(opid, opnode.getOpname(), frm);
		onActiveIndex(tabpages.size() - 1);
	}

	void addPage(String opid, String opname, COpframe frm) {
		Tabpage page = new Tabpage();
		tabpages.add(page);
		page.opid = opid;
		page.opname = opname;
		page.opframe = frm;
		TRootpane rootp = (TRootpane) getRootPane();
		rootp.addTab(opid, opname);

		frm.getContentPane().doLayout();
		page.oprootpane = (JPanel) frm.getContentPane();
		page.oprootpane.setVisible(false);
		getContentPane().add(page.oprootpane);
	}

	public Opnode getRunningopnode(String opid) {
		Enumeration<Opnode> en = runningop.elements();
		while (en.hasMoreElements()) {
			Opnode opnode = en.nextElement();
			if (opnode.getOpid().equals(opid)) {
				return opnode;
			}
		}
		return null;
	}

	class WinstateListener extends WindowAdapter {
		String classname = null;

		WinstateListener(String classname) {
			this.classname = classname;
		}

		public void windowActivated(WindowEvent e) {
			Object src = e.getSource();
			String opid = "";
			if (src instanceof COpframe) {
				opid = ((COpframe) src).getOpid();
				RemoteConnector.setActiveopid(opid);
			}
		}

		@Override
		public void windowClosed(WindowEvent e) {
			Object src = e.getSource();
			String opid = null;
			if (src instanceof COpframe) {
				opid = ((COpframe) src).getOpid();
			}

			// TODO Auto-generated method stub
			super.windowClosed(e);

			closeTablImpl(opid);
			if (opid != null) {
				Opnode opnode = NpopManager.getInst().getOpnode(opid);
				if (opnode != null) {
					opnode.setRunningframe(null);
				}
				opnode = getRunningopnode(opid);
				if (opnode != null) {
					opnode.setRunningframe(null);
					runningop.removeElement(opnode);
					if (showrunningop)
						showRunningop();
				}
			}
		}

	}

	void onClose() {
		if (!canClose())
			return;
		// 选择功能
		ok = false;
		this.dispose();
		System.exit(0);
	}

	// 上传更新程序
	void onUpload() {
		UploaderFrame frm = new UploaderFrame();
		frm.pack();
		frm.setVisible(true);

	}

	class TableMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				runOp();
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	class TreeselectionHandle implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			Object lastcomp = path.getLastPathComponent();
			if (lastcomp instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) lastcomp;
				Object o = treenode.getUserObject();
				if (o instanceof Opgroup) {
					Opgroup groupnode = (Opgroup) treenode.getUserObject();
					showOps(groupnode);
				} else {
					showRunningop();
				}
			}
		}
	}

	boolean showrunningop = false;
	private QuickopDlg quickopdlg;
	private NavigatePanel navpane;

	void showRunningop() {
		showrunningop = true;
		opdbmodel.clearAll();
		Enumeration<Opnode> en = runningop.elements();
		while (en.hasMoreElements()) {
			Opnode opnode = en.nextElement();
			int r = opdbmodel.getRowCount();
			opdbmodel.appendRow();
			opdbmodel.setItemValue(r, "行号", String.valueOf(r + 1));
			opdbmodel.setItemValue(r, "opid", opnode.getOpid());
			opdbmodel.setItemValue(r, "opname", opnode.getOpname());
			opdbmodel.setItemValue(r, "prodname", opnode.getProdname());
			opdbmodel.setItemValue(r, "modulename", opnode.getModulename());
		}
		optable.tableChanged(new TableModelEvent(opdbmodel));
		optable.autoSize();
	}

	void showOps(Opgroup groupnode) {
		showrunningop = false;
		opdbmodel.clearAll();
		Enumeration<Opnode> en = groupnode.getOpnodes().elements();
		while (en.hasMoreElements()) {
			Opnode opnode = en.nextElement();
			int r = opdbmodel.getRowCount();
			opdbmodel.appendRow();
			opdbmodel.setItemValue(r, "行号", String.valueOf(r + 1));
			opdbmodel.setItemValue(r, "opid", opnode.getOpid());
			opdbmodel.setItemValue(r, "opname", opnode.getOpname());
			opdbmodel.setItemValue(r, "prodname", opnode.getProdname());
			opdbmodel.setItemValue(r, "modulename", opnode.getModulename());
		}
		optable.tableChanged(new TableModelEvent(opdbmodel));
		optable.autoSize();
	}

	private void addNode(DefaultMutableTreeNode parenttreenode,
			Opgroup parentgroup) {
		Enumeration<Opgroup> en = parentgroup.getSubgroups().elements();
		while (en.hasMoreElements()) {
			Opgroup subgroup = en.nextElement();
			DefaultMutableTreeNode subtreenode = new DefaultMutableTreeNode(
					subgroup);
			parenttreenode.add(subtreenode);
			addNode(subtreenode, subgroup);
		}
	}

	public void runMessage(Runmessage runMessage) {
		runlogPanel.runMessage(runMessage);
	}

	class StatusThread extends Thread {

		public StatusThread() {
			this.setDaemon(true);
		}

		long t1 = System.currentTimeMillis();

		public void run() {
			while (true) {
				// 30秒
				if (System.currentTimeMillis() - t1 >= 60 * 1000) {
					Runtime.getRuntime().gc();
					t1 = System.currentTimeMillis();
				}
				checkStatus();

				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {

				}
			}
		}

	}

	void checkStatus() {
		long maxtime = 35 * 1000;
		long difftime = System.currentTimeMillis()
				- DefaultNPParam.lastrecvsvrresptime;
		if (difftime <= maxtime) {
			showStatus("联机.ping " + DefaultNPParam.pingtime + "毫秒");
		} else {
			String lasttime = "";
			if (DefaultNPParam.lastrecvsvrresptime > 0) {
				datef.format(DefaultNPParam.lastrecvsvrresptime);
			}
			showStatus("断线. 最后收到服务器响应时间:" + lasttime);
			DefaultNPParam.online = false;
			btnlogin.setEnabled(true);
		}
		BigDecimal maxm = new BigDecimal(Runtime.getRuntime().maxMemory());
		BigDecimal tolm = new BigDecimal(Runtime.getRuntime().totalMemory());
		BigDecimal frem = new BigDecimal(Runtime.getRuntime().freeMemory());
		BigDecimal usem = tolm.subtract(frem);

		StringBuffer memsb = new StringBuffer();
		memsb.append("使用内存:");
		memsb.append(StringUtil.bytes2string(usem));
		memsb.append(",总内存:");
		memsb.append(StringUtil.bytes2string(tolm));
		memsb.append(",最大内存:");
		memsb.append(StringUtil.bytes2string(maxm));
		lbmem.setText(memsb.toString());

		logger.info(memsb.toString());
	}

	protected boolean canClose() {
		if (runningop.size() > 0) {
			String msg = "还有" + String.valueOf(runningop.size())
					+ "功能在运行,继续退出?";
			int ret = JOptionPane.showConfirmDialog(this, msg, "警告",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return true;

	}

	protected void processWindowEvent(WindowEvent e) {
		// WINDOW_ICONIFIED WINDOW_DEICONIFIED
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (!canClose())
				return;
		}
		super.processWindowEvent(e);
	}

	DBTableModel createOpdbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = null;
		col = new DBColumnDisplayInfo("行号", "行号", "行号");
		col.setDbcolumn(false);
		cols.add(col);

		col = new DBColumnDisplayInfo("opname", "number", "功能名");
		cols.add(col);

		col = new DBColumnDisplayInfo("prodname", "number", "产品名");
		cols.add(col);

		col = new DBColumnDisplayInfo("modulename", "number", "模块名");
		cols.add(col);

		col = new DBColumnDisplayInfo("opid", "number", "功能ID");
		cols.add(col);

		return new DBTableModel(cols);
	}

	@Override
	protected JRootPane createRootPane() {
		TRootpane rp = new TRootpane();
		rp.setOpaque(true);
		return rp;
	}

	public static void main(String[] argv) {
		new DefaultNPParam();
		Clientframe dlg = new Clientframe(null);
		dlg.pack();
		dlg.setVisible(true);
	}

	public void quickOp() {
		if (quickopdlg == null) {
			Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.getActiveWindow();
			if (w == null) {
				quickopdlg = new QuickopDlg(this);
			} else {
				if (w instanceof CFrame) {
					quickopdlg = new QuickopDlg((CFrame) w);
				} else {
					quickopdlg = new QuickopDlg(null);
				}
			}
			quickopdlg.pack();
		}
		quickopdlg.setVisible(true);

		if (!quickopdlg.isOk())
			return;
		String opid = quickopdlg.getOpid();
		if (opid == null)
			return;
		runOp(opid, false);
	}

	class NavigatePanel extends JPanel {
		private JButton btnconfig;

		public NavigatePanel() {
			setLayout(new BorderLayout());
			Container cp = (Container) this;
			cp.add(createToolpane(), BorderLayout.NORTH);

			// JTabbedPane tabbedpane = new JTabbedPane();
			// tabbedpane.setFocusable(false);
			// cp.add(tabbedpane, BorderLayout.CENTER);

			// JPanel oppanel = new JPanel();

			JScrollPane treepane = createTreePanel();
			// treepane.setBorder(BorderFactory.createEmptyBorder());
			opdbmodel = createOpdbmodel();
			optable = new CTable(opdbmodel);
			optable.setReadonly(true);
			optable.addMouseListener(new TableMouseListener());
			optable.addFocusListener(new FocusHandle());

			// 要删除optable的回车向下
			InputMap im = optable
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
					"donothing");

			JSplitPane splitpane = new JSplitPane();
			splitpane.setDividerSize(1);
			splitpane.setBorder(BorderFactory.createEmptyBorder());
			BasicSplitPaneUI spui = (BasicSplitPaneUI) splitpane.getUI();
			spui.getDivider().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			// BasicSplitPaneUI spui=new BasicSplitPaneUI();
			// splitpane.setUI(spui);
			// BasicSplitPaneDivider bspd=spui.getDivider();
			// splitpane.setBorder(new Splitpanebd());

			treepane.setMinimumSize(treepane.getPreferredSize());

			splitpane.setLeftComponent(treepane);
			// splitpane.setRightComponent(listpanel);

			JSplitPane rightsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			rightsplit.setBorder(BorderFactory.createEmptyBorder());
			rightsplit.setDividerSize(0);
			spui = (BasicSplitPaneUI) rightsplit.getUI();
			spui.getDivider().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			splitpane.setRightComponent(rightsplit);

			// JPanel tablep = new JPanel();
			// BoxLayout bl = new BoxLayout(tablep, BoxLayout.Y_AXIS);
			// tablep.setLayout(bl);
			// JLabel lbqk = new JLabel("Ctrl+Y快速调用功能");
			// lbqk.setOpaque(true);
			// lbqk.setBackground(Color.yellow);
			// lbqk.setHorizontalAlignment(JLabel.LEFT);
			// lbqk.setPreferredSize(new Dimension(600, 20));
			// lbqk.setMinimumSize(new Dimension(600, 20));
			// tablep.add(lbqk);
			// tablep.add(new JScrollPane(optable));
			splitpane.setDividerLocation(240);

			rightsplit.setLeftComponent(new JScrollPane(optable));
			rightsplit.setDividerLocation(840);

			// 推送平台
			Pushpane pushpane = new Pushpane();
			pushpane.setBorder(BorderFactory.createEtchedBorder());
			rightsplit.setRightComponent(pushpane);
			BackgroundRunner.runThread(pushpane);

			// tabbedpane.add("选择功能", splitpane);
			cp.add(splitpane, BorderLayout.CENTER);
			// oppanel.add(splitpane, BorderLayout.CENTER);
			// splitpane.setPreferredSize(new Dimension(1000, 600));

			addEnterkeyListener();

			tree.setSelectionInterval(1, 1);

			tree.putClientProperty("JTree.lineStyle", "Horizontal");
			// DefaultTreeCellRenderer tree.
			DefaultTreeCellRenderer treecellrender = (DefaultTreeCellRenderer) tree
					.getCellRenderer();
			treecellrender.setLeafIcon(UIManager.getIcon("Tree.closedIcon"));

			// 功能运行
			runlogPanel = new RunlogPanel();
			// tabbedpane.add("网络日志", runlogPanel);

			JPanel statuspanel = createStatusPane();
			cp.add(statuspanel, BorderLayout.SOUTH);
		}

		class Splitpanebd extends BasicBorders.SplitPaneBorder {

			public Splitpanebd() {
				super(Color.red, Color.green);
			}

		}

		JPanel createToolpane() {
			JPanel jp = new JPanel();
			jp.setLayout(new BorderLayout());

			Dimension scrsize = this.getToolkit().getScreenSize();

			CToolbar tb = new CToolbar();
			tb.setPreferredSize(new Dimension((int) scrsize.getWidth(), 30));

			JButton btnok = new JButton("运行功能");
			btnok.setActionCommand("ok");
			btnok.addActionListener(new ButtonEventHandle());
			btnok.setFocusable(false);
			tb.add(btnok);

			tb.addSeparator();

			tb.addSeparator(new Dimension(40, 0));

			JButton btnrepasswd = new JButton("重设密码");
			btnrepasswd.setActionCommand("repassword");
			btnrepasswd.addActionListener(new ButtonEventHandle());
			btnrepasswd.setFocusable(false);
			tb.add(btnrepasswd);

			btnlogin = new JButton("重新登录");
			btnlogin.setActionCommand("login");
			btnlogin.addActionListener(new ButtonEventHandle());
			btnlogin.setFocusable(false);
			tb.add(btnlogin);

			tb.addSeparator();

			/*
			 * JButton btnfreemem = new JButton("清理内存");
			 * btnfreemem.addActionListener(new ButtonEventHandle());
			 * btnfreemem.setActionCommand("freemem");
			 * btnfreemem.setFocusable(false); tb.add(btnfreemem);
			 */
			JButton btncancel = new JButton("退出系统");
			btncancel.addActionListener(new ButtonEventHandle());
			btncancel.setActionCommand("close");
			btncancel.setFocusable(false);
			tb.add(btncancel);

			// jp.add(tb, BorderLayout.CENTER);

			JPanel infopanel = new JPanel();
			BoxLayout box = new BoxLayout(infopanel, BoxLayout.X_AXIS);
			infopanel.setLayout(box);

			Userruninfo user = ClientUserManager.getCurrentUser();
			StringBuffer sb = new StringBuffer();
			sb.append("　操作员：" + user.getUsername());
			sb.append("    ");
			sb.append("部门：" + user.getDeptname());
			sb.append("    ");
			sb.append("角色：" + user.getRolename());
			sb.append("    ");
			/*
			 * sb.append("门店：" + user.getPlacepointname()); sb.append(" ");
			 * sb.append("逻辑日：" + user.getUseday()); sb.append(" ");
			 * sb.append("班次：" + user.getBanci()); sb.append(" ");
			 */

			btnconfig = new JButton(IconFactory.icconfig);
			btnconfig.addMouseListener(new ConfigbtnMousehandler());
			btnconfig.setMargin(new Insets(0, 0, 0, 0));
			Dimension btnsize = new Dimension(38, 27);
			btnconfig.setPreferredSize(btnsize);
			btnconfig.setMaximumSize(btnsize);
			btnconfig.setMinimumSize(btnsize);

			btnconfig.setFocusable(false);
			infopanel.add(btnconfig);

			lbinfo = new JLabel(sb.toString());
			// lbinfo.setPreferredSize(new Dimension((int) scrsize.getWidth(),
			// 25));
			lbinfo.setAlignmentX(JLabel.LEFT);
			infopanel.add(lbinfo);

			infopanel.setBackground(new Color(222, 234, 247));

			jp.add(infopanel, BorderLayout.CENTER);

			return jp;
		}

		class ConfigbtnMousehandler implements MouseListener {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
				JPopupMenu optionmenu = new JPopupMenu("设置");
				JMenuItem menuitem = new JMenuItem("重设密码");
				menuitem.setActionCommand("repassword");
				menuitem.addActionListener(new ButtonEventHandle());
				optionmenu.add(menuitem);

				menuitem = new JMenuItem("重新登录");
				menuitem.setActionCommand("login");
				menuitem.addActionListener(new ButtonEventHandle());
				optionmenu.add(menuitem);

				menuitem = new JMenuItem("退出");
				menuitem.setActionCommand("close");
				menuitem.addActionListener(new ButtonEventHandle());
				optionmenu.add(menuitem);

				optionmenu.show(btnconfig, e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e) {
			}

		}
	}

	/**
	 * 每个标签页
	 * 
	 * @author user
	 * 
	 */
	class Tabpage {
		String opid = "";
		String opname = "";
		COpframe opframe;
		JPanel oprootpane;
	}

	JPanel oldjp = null;

	private int activeindex = 0;

	/**
	 * 激活某页
	 * 
	 * @param index
	 */
	public void onActiveIndex(int index) {
		logger.debug("client frame, setactiveindex = " + index);
		if (activeindex == index)
			return;
		activeindex = index;

		Container cp = getContentPane();
		JComponent memoldjp = oldjp;
		if (oldjp != null) {
			oldjp.setVisible(false);
			oldjp = null;
		}
		final Tabpage page = tabpages.elementAt(index);
		oldjp = page.oprootpane;

		if (page.opframe != null) {
			setframeHotkey(page.opframe);
		}
		RemoteConnector.setActiveopid(page.opid);

		SwingUtilities.invokeLater(new HideshowRunnable(memoldjp,
				page.oprootpane));
	}

	class HideshowRunnable implements Runnable {
		JComponent hidecomp;
		JComponent viscomp;

		HideshowRunnable(JComponent hidecomp, JComponent viscomp) {
			this.hidecomp = hidecomp;
			this.viscomp = viscomp;
		}

		public void run() {
			if (hidecomp != null) {
				hidecomp.setVisible(false);
			}
			if (viscomp != null) {
				viscomp.setVisible(true);
			}
			getContentPane().doLayout();
			viscomp.doLayout();
			viscomp.requestFocus();

		}
	}

	private int memcloseindex = -1;
	/**
	 * 记录在关闭时的活动页
	 */
	private int memactiveindex = -1;

	/**
	 * 关闭标签
	 * 
	 * @param closeindex
	 * @param activeindex
	 */
	public void onCloseIndex(String opid, int closeindex, int activeindex) {
		logger.debug("onCloseIndex,opid=" + opid + ",closeindex=" + closeindex
				+ ",activeindex=" + activeindex);
		if (closeindex == 0) {
			// 关闭所有功能.退出
			this.memcloseindex = closeindex;
			this.memactiveindex = activeindex;
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else {
			// 根据opid,检查是不是已经关了?
			boolean found = false;
			Tabpage tabpage = null;
			for (int i = 1; i < tabpages.size(); i++) {
				tabpage = tabpages.elementAt(i);
				if (opid.equals(tabpage.opid)) {
					found = true;
					break;
				}
			}
			if (!found) {
				// 已被关了.防双击关闭钮.
				return;
			}
			this.memcloseindex = closeindex;
			this.memactiveindex = activeindex;

			tabpage.opframe.dispatchEvent(new WindowEvent(tabpage.opframe,
					WindowEvent.WINDOW_CLOSING));

		}

	}

	/**
	 * 关闭窗口
	 */
	public void closeTablImpl(String opid) {
		if (opid == null)
			return;
		logger.debug("closeTablImpl opid=" + opid);
		// 关闭标签
		boolean found = false;
		for (int i = 1; i < tabpages.size(); i++) {
			Tabpage tabinfo = tabpages.elementAt(i);
			if (tabinfo.opid.equals(opid)) {
				memcloseindex = i;
				found = true;
				break;
			}
		}
		logger.debug("closeTablImpl opid=" + opid + ",found=" + found
				+ ",memcloseindex=" + memcloseindex);
		if (!found)
			return;

		if (memcloseindex < 0) {
			return;
		}

		if (memactiveindex < 0) {
			memactiveindex = memcloseindex;
		}

		tabpages.remove(memcloseindex);
		TRootpane rootp = (TRootpane) getRootPane();
		rootp.closeTab(memcloseindex);

		if (memactiveindex > memcloseindex) {
			memactiveindex--;
		} else if (memactiveindex == memcloseindex) {
			if (memactiveindex > tabpages.size() - 1) {
				memactiveindex = tabpages.size() - 1;
			}
		}
		if (memactiveindex >= 0) {
			rootp.setActiveindex(memactiveindex);
			activeindex = -1;
			onActiveIndex(memactiveindex);
		}
		memcloseindex = memactiveindex = -1;
	}

	void setframeHotkey(JFrame frm) {
		JComponent jcp = (JComponent) frm.getContentPane();
		InputMap im = jcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = jcp.getActionMap();

		JComponent thisjcp = (JComponent) this.getContentPane();
		thisjcp.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, im);
		thisjcp.setActionMap(am);
	}

	class FrameLayoutMgr extends FlowLayout {
		public FrameLayoutMgr() {
			super(FlowLayout.LEFT);
		}

		@Override
		public void layoutContainer(Container target) {
			// logger.debug("layoutContainer target"+target);
			int w = Clientframe.this.getWidth() - 10;
			int h = Clientframe.this.getHeight();
			h -= 48;
			for (int i = 0; i < tabpages.size(); i++) {
				Tabpage tabpage = tabpages.elementAt(i);
				if (tabpage.oprootpane.isVisible()) {
					tabpage.oprootpane.setBounds(0, 0, w, h);
					break;
				}
			}
		}

	}

	public void onPopupmenu(Point point) {
		JPopupMenu popmenu = new JPopupMenu();
		JMenuItem menuitem = new JMenuItem("运行新功能 Ctrl+Y");
		menuitem.setActionCommand("quickop");
		menuitem
				.addActionListener(new MenuHandler(menuitem.getActionCommand()));
		popmenu.add(menuitem);
		popmenu.addSeparator();
		menuitem = new JMenuItem("导航");
		menuitem.setActionCommand("nav");
		menuitem
				.addActionListener(new MenuHandler(menuitem.getActionCommand()));
		popmenu.add(menuitem);
		if (tabpages.size() > 1) {
			// 列出已有的功能.
			for (int i = 1; i <= tabpages.size() - 1; i++) {
				Tabpage tabpage = tabpages.elementAt(i);
				menuitem = new JMenuItem(tabpage.opname);
				menuitem.setActionCommand("active_" + tabpage.opid);
				menuitem.addActionListener(new MenuHandler(menuitem
						.getActionCommand()));
				popmenu.add(menuitem);
			}
		}
		// 弹出菜单
		TRootpane trootp = (TRootpane) getRootPane();
		popmenu.show(trootp.getTitlepane(), point.x, point.y);
	}

	class MenuHandler extends AbstractAction {

		public MenuHandler(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			TRootpane rootp = (TRootpane) getRootPane();
			String cmd = e.getActionCommand();
			if (cmd.equals("quickop")) {
				quickOp();
			} else if (cmd.equals("nav")) {
				onActiveIndex(0);
				rootp.setActiveindex(0);
			} else if (cmd.startsWith("active_")) {
				String targetopid = cmd.substring("active_".length());
				for (int i = 1; i < tabpages.size(); i++) {
					Tabpage tabpage = tabpages.elementAt(i);
					if (tabpage.opid.equals(targetopid)) {
						rootp.setActiveindex(i);
						onActiveIndex(i);
						break;
					}
				}

			}
		}

	}
}
