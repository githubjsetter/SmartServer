package com.smart.adminclient.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.log4j.Category;

import com.smart.adminclient.auth.AdminrepasswdDlg;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.RunopManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.filesync.UploaderFrame;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.runop.Opgroup;
import com.smart.platform.gui.runop.Opnode;
import com.smart.platform.gui.runop.RunlogPanel;
import com.smart.platform.gui.runop.Runmessage;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.StringUtil;

/**
 * 客户端Frame
 * @author Administrator
 *
 */
public class AdminClientframe extends CFrame {
	private JTree tree;
	private ListOppane listpanel;
	private JList list;
	private RunlogPanel runlogPanel;
	private JLabel lbstatus;
	private JLabel lbmem;
	private JButton btnlogin;
	private JComponent activecomp;
	private Opnode selectedopnode;
	private JFrame selectedfrm;
	Category logger = Category.getInstance(AdminClientframe.class);
	boolean ok=false;

	public AdminClientframe() {
		super("NPServer Admin");
		init();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		RunopManager.setMainmenuframe(this);
		Dimension scrsize=Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int)scrsize.getWidth(),(int)scrsize.getHeight()-25));
	}
	
	
	private void init() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(createToolpane(), BorderLayout.NORTH);

		JTabbedPane tabbedpane = new JTabbedPane();
		tabbedpane.setFocusable(false);
		cp.add(tabbedpane, BorderLayout.CENTER);

		// JPanel oppanel = new JPanel();

		JScrollPane treepane = createTreePanel();
		listpanel = new ListOppane();

		JSplitPane splitpane = new JSplitPane();
		// splitpane.setLeftComponent(leftpanel);
		// splitpane.setRightComponent(rightpanel);

		splitpane.setLeftComponent(treepane);
		splitpane.setRightComponent(listpanel);
		splitpane.setDividerLocation(240);

		tabbedpane.add("选择功能", splitpane);
		// oppanel.add(splitpane, BorderLayout.CENTER);
		// splitpane.setPreferredSize(new Dimension(1000, 600));

		addEnterkeyListener();

		tree.setSelectionInterval(1, 1);

		tree.putClientProperty("JTree.lineStyle", "Horizontal");
		// DefaultTreeCellRenderer tree.
		DefaultTreeCellRenderer treecellrender = (DefaultTreeCellRenderer) tree
				.getCellRenderer();
		treecellrender.setLeafIcon(UIManager.getIcon("Tree.closedIcon"));

		/*
		 * setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
		 * setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
		 * setOpenIcon(UIManager.getIcon("Tree.openIcon"));
		 */

		// 功能运行
		runlogPanel = new RunlogPanel();
		tabbedpane.add("网络日志", runlogPanel);

		JPanel statuspanel = createStatusPane();
		cp.add(statuspanel, BorderLayout.SOUTH);

	}

	JPanel createStatusPane() {
		JPanel jp = new JPanel();
		// BoxLayout layout = new BoxLayout(jp, BoxLayout.X_AXIS);
		// jp.setLayout(layout);
		CFormlayout layout = new CFormlayout(2, 2);
		jp.setLayout(layout);

		lbstatus = new JLabel();
		// lbstatus.setPreferredSize(new Dimension(600, 27));
		lbstatus.setHorizontalAlignment(JLabel.LEFT);
		jp.add(lbstatus);

		lbmem = new JLabel("mem");
		// lbmem.setPreferredSize(new Dimension(600, 27));
		lbmem.setHorizontalAlignment(JLabel.RIGHT);
		jp.add(lbmem);
		return jp;
	}

	void showStatus(String msg) {
		lbstatus.setText(msg);
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
							list.requestFocus();
							if (list.getModel().getSize() > 0) {
								list.setSelectedIndex(0);
							}
						}
					};
					SwingUtilities.invokeLater(runner);
				} else if (activecomp != null && activecomp == list) {
					onOK();
				}
			}
		}
	}

	JScrollPane createTreePanel() {
		// Opgroup topgroup = Opgroup.createDemo();

		Opgroup topgroup = Adminops.createAdminOps();
		if (topgroup == null) {
			topgroup = new Opgroup("未定义");
		}

		DefaultMutableTreeNode toptreenode = new DefaultMutableTreeNode(
				topgroup);
		DefaultMutableTreeNode runningoptreecode = new DefaultMutableTreeNode(
				"正在运行的功能");
		toptreenode.add(runningoptreecode);
		addNode(toptreenode, topgroup);

		tree = new JTree(toptreenode);
		tree.addTreeSelectionListener(new TreeselectionHandle());
		tree.addFocusListener(new FocusHandle());

		JScrollPane jp = new JScrollPane(tree);
		return jp;
	}

	class ListOppane extends JPanel {
		final static int LIST_OPS = 0;
		final static int LIST_RUNNINGOPS = 1;

		int listmode = LIST_OPS;

		public ListOppane() {
			this.setLayout(new BorderLayout());

			String ops[] = { "" };
			list = new JList(ops);
			list.setRequestFocusEnabled(true);
			JScrollPane sp = new JScrollPane(list);
			this.add(sp, BorderLayout.CENTER);

			list.addListSelectionListener(new ListListenerHandle());
			list.addMouseListener(new MouseHandle());

			list.addFocusListener(new FocusHandle());

		}

		public void listRunningframe(Vector<JFrame> frms) {
			listmode = LIST_RUNNINGOPS;
			DefaultListModel model = new DefaultListModel();
			Enumeration<JFrame> en = frms.elements();
			while (en.hasMoreElements()) {
				JFrame frm = en.nextElement();
				model.addElement(frm.getTitle());
			}
			list.setModel(model);
			list.invalidate();
			list.validate();
		}

		public void setOps(Vector<Opnode> ops) {
			listmode = LIST_OPS;
			DefaultListModel model = new DefaultListModel();
			Enumeration<Opnode> en = ops.elements();
			while (en.hasMoreElements()) {
				Opnode opcode = en.nextElement();
				model.addElement(opcode);
			}
			list.setModel(model);
		}

		class ListListenerHandle implements ListSelectionListener {

			public void valueChanged(ListSelectionEvent e) {
				JList jl = (JList) e.getSource();
				int index = jl.getSelectedIndex();
				if (index < 0) {
					return;
				}
				Object o = list.getModel().getElementAt(index);
				if (o instanceof Opnode) {
					selectedopnode = (Opnode) list.getModel().getElementAt(
							index);
				} else {
					selectedopnode = null;
					//selectedfrm = frametable.elementAt(index);
				}
			}
		}

		public void onRunningopchanged() {
			if (LIST_RUNNINGOPS == listmode) {
				listRunningframe(frametable);
			}
		}
	}

	class MouseHandle implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				onOK();
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

		/*
		 * if (DefaultNPParam.develop == 1) { JButton btnupload = new
		 * JButton("上传更新"); btnupload.setActionCommand("upload");
		 * btnupload.addActionListener(new ButtonEventHandle());
		 * btnupload.setFocusable(false); tb.add(btnupload); tb.addSeparator(); }
		JButton btndownload = new JButton("下载更新");
		btndownload.setActionCommand("download");
		btndownload.addActionListener(new ButtonEventHandle());
		btndownload.setFocusable(false);
		tb.add(btndownload);
		 */

		tb.addSeparator(new Dimension(40, 0));

		JButton btnrepasswd = new JButton("重设密码");
		btnrepasswd.setActionCommand("repassword");
		btnrepasswd.addActionListener(new ButtonEventHandle());
		btnrepasswd.setFocusable(false);
		tb.add(btnrepasswd);
/*
		btnlogin = new JButton("重新登录");
		btnlogin.setActionCommand("login");
		btnlogin.addActionListener(new ButtonEventHandle());
		btnlogin.setFocusable(false);
		tb.add(btnlogin);

		tb.addSeparator();

		JButton btnfreemem = new JButton("清理内存");
		btnfreemem.addActionListener(new ButtonEventHandle());
		btnfreemem.setActionCommand("freemem");
		btnfreemem.setFocusable(false);
		tb.add(btnfreemem);
*/
		JButton btncancel = new JButton("退出系统");
		btncancel.addActionListener(new ButtonEventHandle());
		btncancel.setActionCommand("close");
		btncancel.setFocusable(false);
		tb.add(btncancel);

		jp.add(tb, BorderLayout.CENTER);

		JPanel infopanel = new JPanel();
		BoxLayout box = new BoxLayout(infopanel, BoxLayout.X_AXIS);
		infopanel.setLayout(box);

		Userruninfo user = ClientUserManager.getCurrentUser();
		StringBuffer sb = new StringBuffer();
/*		sb.append("操作员：" + user.getUsername());
		sb.append("    ");
		sb.append("部门：" + user.getDeptname());
		sb.append("    ");
		sb.append("角色：" + user.getRolename());
		sb.append("    ");
		sb.append("门店：" + user.getPlacepointname());
		sb.append("    ");
		sb.append("逻辑日：" + user.getUseday());
		sb.append("    ");
		sb.append("班次：" + user.getBanci());
		sb.append("    ");
*/
		sb.append("您做为平台服务器管理员登录");
		lbinfo = new JLabel(sb.toString());
		lbinfo.setPreferredSize(new Dimension((int) scrsize.getWidth(), 25));
		lbinfo.setAlignmentX(JLabel.LEFT);
		infopanel.add(lbinfo);
		jp.add(infopanel, BorderLayout.SOUTH);

		return jp;
	}

	/**
	 * 设置上部的人员显示信息
	 * @param s
	 */
	public void setInfoMessage(String s){
		lbinfo.setText(s);
	}

	class ButtonEventHandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("ok")) {
				onOK();
			} else if (cmd.equals("login")) {
				onLogin();
			} else if (cmd.equals("close")) {
				onClose();
			} else if (cmd.equals("upload")) {
				onUpload();
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
		AdminrepasswdDlg dlg = new AdminrepasswdDlg(this);
		dlg.pack();
		dlg.setVisible(true);

	}

	void onLogin() {
		
		if(getRunningopCount()>0){
			JOptionPane.showMessageDialog(this, "还有功能正在运行,请关闭所有功能后再重新登录", "提示",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		JDialog dlg = DefaultNPParam.logindlg;
		dlg.pack();
		dlg.setVisible(true);
	}

	Vector<JFrame> frametable = new Vector<JFrame>();
	HashMap<String, JFrame> framemap = new HashMap<String, JFrame>();

	/**
	 * 取正在运行的功能数量
	 * @return
	 */
	public int getRunningopCount(){
		return framemap.size();
	}
	
	void onOK() {
		// 选择功能
		if (selectedopnode != null) {
			String classname = selectedopnode.getClassname();
			JFrame runfrm = framemap.get(classname);
			if (runfrm != null) {
				runfrm.setState(JFrame.NORMAL);
				runfrm.toFront();
				runfrm.requestFocus();
				return;
			}
			try {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				Class<?> aClass = Class.forName(classname);
				Object instance = aClass.newInstance();
				if (instance instanceof JFrame) {
					JFrame frm = (JFrame) instance;
					framemap.put(classname, frm);
					frametable.add(frm);
					frm.pack();
					frm.addWindowListener(new WinstateListener(classname));
					frm.setVisible(true);
					listpanel.onRunningopchanged();
					frm = null;
				}
				instance = null;
			} catch (Exception e) {
				logger.error("ERROR", e);
				return;
			} finally {
				this.setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			// System.out.println("selected " + selectedopnode);
			ok = true;
		} else if (selectedfrm != null) {
			selectedfrm.setState(JFrame.NORMAL);
			selectedfrm.toFront();
			selectedfrm.requestFocus();
		}
	}

	class WinstateListener extends WindowAdapter {
		String classname = null;

		WinstateListener(String classname) {
			this.classname = classname;
		}
		
		

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			super.windowClosed(e);
			frametable.remove(framemap.remove(classname));
			listpanel.onRunningopchanged();
		}

	}

	void onClose() {
		if(!canClose())return;
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

	class TreeselectionHandle implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			Object lastcomp = path.getLastPathComponent();
			if (lastcomp instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) lastcomp;
				Object o = treenode.getUserObject();
				if (o instanceof Opgroup) {
					Opgroup groupnode = (Opgroup) treenode.getUserObject();
					listpanel.setOps(groupnode.getOpnodes());
				} else {
					listpanel.listRunningframe(frametable);
				}
				// Enumeration<Opnode> en = groupnode.getOpnodes().elements();

			}
		}
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
					Thread.sleep(5000);
				} catch (InterruptedException e) {

				}
			}
		}

	}

	SimpleDateFormat datef = new SimpleDateFormat("HH:mm:ss");
	private JLabel lbinfo;

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
	}

	protected boolean canClose() {
		if (framemap.size() > 0) {
			String msg = "还有"+String.valueOf(framemap.size())+"功能在运行,继续退出?";
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
			if(!canClose())return;
		}
		super.processWindowEvent(e);
	}
	

	public static void main(String[] argv) {
		new DefaultNPParam();
		AdminClientframe dlg = new AdminClientframe();
		dlg.pack();
		dlg.setVisible(true);
	}
}