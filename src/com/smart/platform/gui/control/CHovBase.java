package com.smart.platform.gui.control;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.SelecttabviewHov;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.filedb.FiledbManager;
import com.smart.platform.filedb.FiledbSearchCond;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.gui.ste.Hovdesc;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.gui.ste.Zxhovdownloader;
import com.smart.platform.gui.ui.CTableheadUI;
import com.smart.platform.selfcheck.DBColumnChecker;
import com.smart.platform.selfcheck.DBColumnUppercaseChecker;
import com.smart.platform.selfcheck.SelfcheckConstants;
import com.smart.platform.selfcheck.SelfcheckError;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.ZipHelper;

/**
 * HOV基类
 */
public abstract class CHovBase implements Hovdesc {

	public final static String ACTION_QUERY = "query";
	public final static String ACTION_STOPRETRIEVE = "stopretrieve";
	public final static String ACTION_OK = "ok";
	public final static String ACTION_CANCEL = "cancel";

	public final static int hovfetchmaxrow = 100;

	// private JButton buttonStopretrieve;
	/**
	 * select语句
	 */
	protected String defaultsql = "";

	/**
	 * 查询条件
	 */
	protected Querycond querycond = null;

	/**
	 * 状态条
	 */
	protected JLabel lbstatus = null;
	/**
	 * 用于浮动窗口的table
	 */
	protected CTable table = null;

	/**
	 * 用于对话窗口的table
	 */
	protected CTable dlgtable = null;

	/**
	 * 选择结果.true选择成功
	 */
	protected boolean result = false;

	/**
	 * 查询数据类
	 */
	RetrieveWorker retrieveworker = null;

	/**
	 * HOV浮动窗口
	 */
	protected HovWindow hovwindow = null;

	/**
	 * 传统HOV模态对话窗口
	 */
	protected HovDialog hovdialog = null;

	/**
	 * 调用HOV的窗口
	 */
	Window parentwindow = null;

	/**
	 * 数据源
	 */
	protected DBTableModel tablemodel;
	protected Sumdbmodel sumdbmodel = null;

	/**
	 * 是否使用对话窗口
	 */
	boolean usedlgwin = false;

	/**
	 * 是否已初始化
	 */
	private boolean inited = false;

	/**
	 * 是否使用本地文件查询 如果该值为true,将在本地文件locafile:File 中进行查询
	 */
	protected boolean usefile = false;

	/**
	 * 本地文件名. 将使用目录filedb下的filename做为数据源
	 */
	protected String filename = "";

	/**
	 * hov回调接口。
	 */
	protected Hovcallback hovcallback = null;

	Category logger = Category.getInstance(CHovBase.class);

	/**
	 * 允许多次选择,记录现在行号
	 */
	protected int startrow = 0;

	/**
	 * 是否还有记录
	 */
	protected boolean hasmore;

	JButton btnmore;

	/*
	 * protected CHovBase(Frame owner, String title) throws HeadlessException {
	 * //super(owner, title, true); super((Frame)null); this.defaultsql =
	 * getDefaultsql(); this.querycond = getQuerycond(); initWindow(); }
	 * 
	 * protected CHovBase(Dialog owner, String title) throws HeadlessException {
	 * //super(owner, title, true); super((Dialog)owner); this.defaultsql =
	 * getDefaultsql(); this.querycond = getQuerycond(); initWindow(); }
	 */

	/**
	 * 构造
	 */
	public CHovBase() throws HeadlessException {
		// 20070727 删除initwindow.因为在DefaultNPParam 中创建时不该打开窗口
		// 删除initWindow();

	}

	/**
	 * 创建GUI控件
	 */
	protected void initWindow() {
		Zxhovdownloader zxhovdl = new Zxhovdownloader();
		File zxzipfile = null;
		File tmpfile = null;
		try {
			String classname = getClass().getName();
			zxzipfile = zxhovdl.downloadZxzip(classname);

			if (zxzipfile == null) {
				if (getDefaultConfigfilepath() == null) {
					configfile = null;
				} else {
					configfile = new File(getDefaultConfigfilepath());
				}
			} else {
				tmpfile = File.createTempFile("hovconfig", ".model");
				ZipHelper.extractFile(zxzipfile, "hov.model", tmpfile);
				configfile = tmpfile;
			}
		} catch (Exception e) {
			logger.error("error", e);
		}

		if (configfile != null) {
			Reader rd = null;
			try {
				rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);

				if (rd != null) {
					rd.close();
					createFromConfigfile();
				}
			} catch (Exception e) {
			}
		}
		if (tmpfile != null) {
			tmpfile.delete();
		}

		if (this.defaultsql == null || this.defaultsql.length() == 0) {
			this.defaultsql = getDefaultsql();
		}
		if (querycond == null) {
			querycond = getQuerycond();
		}
		hovwindow = new HovWindow(parentwindow);
		hovwindow.setBackground(Color.LIGHT_GRAY);

		Container cp = hovwindow.getContentPane();
		cp.setLayout(new BorderLayout());
		createDatapanel(cp);

		JPanel bottompane = createBottompanel();
		cp.add(bottompane, BorderLayout.SOUTH);

		JComponent ccp = (JComponent) hovwindow.getContentPane();
		InputMap inputMap = ccp
				.getInputMap(JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		KeyStroke esckey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		inputMap.put(esckey, ACTION_CANCEL);
		ccp.getActionMap().put(ACTION_CANCEL, new HovAction(ACTION_CANCEL));
	}

	/**
	 * 返回查询select语句,必要重载
	 * 
	 * @return
	 */
	public abstract String getDefaultsql();

	/**
	 * 返回查询条件,必要重载
	 * 
	 * @return
	 */
	public abstract Querycond getQuerycond();

	/**
	 * HOV Action处理.
	 * 
	 * @author Administrator
	 * 
	 */
	protected class HovAction extends AbstractAction {
		public HovAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (ACTION_OK.equals(e.getActionCommand())) {
				onOk();
			} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
				hide();
			} else if (ACTION_STOPRETRIEVE.equals(e.getActionCommand())) {
				stopRetrieve();
			} else if (ACTION_QUERY.equals(e.getActionCommand())) {
				doQuery();
			} else if ("more".equals(e.getActionCommand())) {
				retrieveMore();
			} else if (CDialog.ACTION_DESIGN.equals(e.getActionCommand())) {
				designHov();
			}
		}

	}

	protected boolean designHov() {
		String path = getDefaultConfigfilepath();
		if (path == null) {
			String classname = CHovBase.this.getClass().getName();
			configfile = new File(CurrentdirHelper.getZxdir(), "HOV/"
					+ classname + ".model");
		} else {
			configfile = new File(path);
		}
		for (int i = 0; i < dlgtable.getColumnCount(); i++) {
			TableColumn tc = dlgtable.getColumnModel().getColumn(i);
			int mindex = dlgtable.convertColumnIndexToModel(i);
			DBColumnDisplayInfo colinfo = tablemodel.getDisplaycolumninfos()
					.elementAt(mindex);
			int cw = tc.getWidth();
			// logger.debug(colinfo.getColname() + " width=" + cw);
			colinfo.setTablecolumnwidth(cw);
		}

		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(configfile));
			writeHov(out);
		} catch (Exception e) {
			logger.error("err", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}

		CHovsetupDlg dlg = new CHovsetupDlg(hovdialog, this, configfile);
		dlg.pack();
		dlg.setVisible(true);

		return dlg.isOk();
	}

	public void retrieveMore() {
		if (!hasmore)
			return;
		hasmore = false;
		setBtnmoreEnable();
		if (usefile) {
			searchFromfile();
			return;
		}

		if (retrieveworker == null) {
			retrieveworker = new RetrieveWorker();
		}
		if (retrieveworker.busy) {
			// 停止原来的,开一个新的
			retrieveworker.busy = false;
			retrieveworker = new RetrieveWorker();

			// lbstatus.setText("正在查询，不能重复查询。");
			/*
			 * JOptionPane.showMessageDialog(hovwindow,
			 * "正在查询，不能重复查询。可按\"停止\"钮停止查询。", "提示", JOptionPane.OK_OPTION);
			 */
			// return;
		}

		lbstatus.setText("开始查询.......");
		Thread t = new Thread(retrieveworker);
		t.start();
	}

	/**
	 * 执行查询
	 */
	protected void doQuery() {
		startrow = 0;
		hasmore = false;
		DBTableModel model = (DBTableModel) table.getModel();
		model.clearAll();

		setBtnmoreEnable();
		if (usefile) {
			searchFromfile();
			return;
		}

		if (retrieveworker == null) {
			retrieveworker = new RetrieveWorker();
		}
		if (retrieveworker.busy) {
			// 停止原来的,开一个新的
			retrieveworker.busy = false;
			retrieveworker = new RetrieveWorker();

			// lbstatus.setText("正在查询，不能重复查询。");
			/*
			 * JOptionPane.showMessageDialog(hovwindow,
			 * "正在查询，不能重复查询。可按\"停止\"钮停止查询。", "提示", JOptionPane.OK_OPTION);
			 */
			// return;
		}

		lbstatus.setText("开始查询.......");
		Thread t = new Thread(retrieveworker);
		t.start();
	}

	/**
	 * 从文件查询
	 */
	protected void searchFromfile() {
		FiledbSearchCond filedbconds[] = querycond.getFiledbCond();

		// 合并条件
		int totalsize = 0;
		if (filedbconds != null) {
			totalsize += filedbconds.length;
		}
		if (otherfileconds != null) {
			totalsize += otherfileconds.length;
		}

		FiledbSearchCond allconds[] = new FiledbSearchCond[totalsize];
		int index = 0;
		for (int i = 0; filedbconds != null && i < filedbconds.length; i++) {
			allconds[index++] = filedbconds[i];
		}
		for (int i = 0; otherfileconds != null && i < otherfileconds.length; i++) {
			allconds[index++] = otherfileconds[i];
		}

		FiledbManager filedb = FiledbManager.getInstance();
		DBTableModel model = (DBTableModel) tablemodel;
		model.clearAll();
		DBTableModel memds = null;
		try {
			memds = FiledbManager.searchFile(filename, allconds, 1000);
		} catch (Exception e) {
			logger.error("ERROR", e);
			JOptionPane.showMessageDialog(null, "错误", e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		model.bindMemds(memds);

		resizeTable();
		if (hovdialog != null && dlgtable != null) {
			if (dlgtable.getRowCount() == 2) {
				dlgtable.getSelectionModel().setSelectionInterval(0, 0);
				onOk();
				return;
			}
			if (dlgtable.getRowCount() > 0) {
				dlgtable.getSelectionModel().setSelectionInterval(0, 0);
				dlgtable.requestFocus();
			}
		}

		if (table.getRowCount() > 0) {
			table.getSelectionModel().setSelectionInterval(0, 0);
		}
		if (table.getRowCount() == 2) {
			hovlistener.on_hov(null, getResult());
		}

		on_retrieved();
		lbstatus.setText("查询到" + (model.getRowCount()) + "条记录");

	}

	/**
	 * 创建下部状态条
	 * 
	 * @return
	 */
	JPanel createBottompanel() {
		JPanel statuspane = new JPanel();

		lbstatus = new JLabel("请输入条件按回车");
		lbstatus.setPreferredSize(new Dimension(600, 20));
		statuspane.add(lbstatus);

		JPanel jp = new JPanel();

		jp.setLayout(new BorderLayout());
		// jp.add(toolpane, BorderLayout.NORTH);
		jp.add(statuspane, BorderLayout.SOUTH);

		return jp;
	}

	/**
	 * 键盘回车事件处理
	 * 
	 * @author Administrator
	 * 
	 */
	class HovKeylistener implements KeyListener {
		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				// 按回车了
				onConfirm(true);
			} else if (e.getKeyCode() == 27) {
				// ESC
				hide();
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

	/**
	 * 创建呈现数据panel,放置table
	 * 
	 * @param cp
	 */
	protected void createDatapanel(Container cp) {
		table = createTable();
		table.setReadonly(true);
		table.setRequestFocusEnabled(false);
		table.getTableHeader().setUI(new CTableheadUI());

		// Border border = BorderFactory.createEtchedBorder();
		// table.setBorder(border);
		table.setBackground(Color.WHITE);

		// 去掉enter下一行 ,F2编辑
		InputMap map = table.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));

		table.addKeyListener(new HovKeylistener());
		table.addMouseListener(getMouseListener());

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tablescrollpane = new JScrollPane(table);
		tablescrollpane.setBackground(Color.WHITE);
		cp.add(tablescrollpane, BorderLayout.CENTER);

	}

	/**
	 * 返回鼠标事件监听类
	 * 
	 * @return
	 */
	protected MouseListener getMouseListener() {
		return new HovMouseListener();
	}

	/**
	 * 鼠标事件监听类
	 * 
	 * @author Administrator
	 * 
	 */
	class HovMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				if (table.getSelectedRow() >= 0) {
					onConfirm(true);
				}
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

	/**
	 * 创建数据源,必须重载
	 * 
	 * @return
	 */
	protected abstract TableModel createTablemodel();

	/**
	 * 创建table
	 * 
	 * @return
	 */
	protected CTable createTable() {
		if (tablemodel == null) {
			tablemodel = (DBTableModel) createTablemodel();
			sumdbmodel = new Sumdbmodel((DBTableModel) tablemodel, null);
		}

		CTable table = new CTable(sumdbmodel);
		table.setRowHeight(27);

		Vector<DBColumnDisplayInfo> formcolumndisplayinfos = tablemodel
				.getDisplaycolumninfos();

		TableColumnModel cm = table.getColumnModel();
		for (int c = 0; c < cm.getColumnCount(); c++) {
			TableColumn column = cm.getColumn(c);
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(column.getModelIndex());
			if (colinfo.getTablecolumnwidth() <= 0) {
				column.setPreferredWidth(65);
			} else {
				column.setPreferredWidth(colinfo.getTablecolumnwidth());
			}
			PlainTablecellRender cellRenderer = new PlainTablecellRender(
					colinfo);
			column.setCellRenderer(cellRenderer);
		}

		return table;
	}

	/**
	 * 中止查询
	 */
	protected void stopRetrieve() {
		// buttonStopretrieve.setText("关闭");

		// 停止查询
		if (retrieveworker == null || !retrieveworker.busy) {
			// 关闭
			onConfirm(false);
		} else {
			// 停止查询
			setStatusMessage("用户中止查询");
			if (retrieveworker != null) {
				retrieveworker.busy = false;
				retrieveworker = null;
			}

		}
	}

	String queryerrormsg = "";

	/**
	 * 查询线程执行查询
	 * 
	 * @return
	 */
	DBTableModel execQuery() {
		String sql;
		String wheres = querycond.getHovWheres();
		String otherewheres = getOtherwheres();
		if (otherewheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and " + otherewheres;
			} else {
				wheres = otherewheres;
			}
		}
		sql = DBHelper.addWheres(defaultsql, wheres);
		// System.out.println("hov sql:" + sql);
		logger.debug("hov sql=" + sql);

		DBTableModel memds;
		try {
			queryerrormsg = "";
			memds = fetchDB(sql);
			hasmore = memds.hasmore();
			startrow += memds.getRowCount();
			return memds;
		} catch (Exception e) {
			queryerrormsg = e.getMessage();
			logger.error("ERROR", e);
			return null;
		}

		// /System.out.println("查询到记录"+memDS.getRowCount()+"条");

	}

	/**
	 * 返回附加的查询where条件
	 * 
	 * @return
	 */
	protected String getOtherwheres() {
		return otherwheres;
	}

	/**
	 * 自动调整table列宽
	 */
	void resizeTable() {
		// logger.debug("resizeTable ,begin call tableChanged()");
		table.tableChanged(new TableModelEvent(table.getModel()));
		// logger.debug("resizeTable ,begin call autoSize()");
		table.autoSize();
		// logger.debug("finished resizeTable()");

		if (hovdialog != null && dlgtable != null) {
			dlgtable.tableChanged(new TableModelEvent(dlgtable.getModel()));
			dlgtable.autoSize();
		}
	}

	/**
	 * 调用服务器的服务进行查询
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	private DBTableModel fetchDB(String sql) throws Exception {
		RemotesqlHelper sqlhelper = new RemotesqlHelper();
		return sqlhelper.doSelect(getSvrCommand(), sql, startrow,
				hovfetchmaxrow);
	}

	/**
	 * 返回服务器命令,固定为select
	 * 
	 * @return
	 */
	protected String getSvrCommand() {
		return "select";
	}

	/**
	 * HOV监听器
	 */
	private HovListener hovlistener;

	/**
	 * 确定hov
	 * 
	 * @param ok
	 *            true确定 false取消
	 */
	private void onConfirm(boolean ok) {
		if (ok) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				result = true;
			} else {
				result = false;
			}
		}

		// System.out.println("Return "+resultrec);
		hovlistener.on_hov(null, getResult());
	}

	/**
	 * 进行查询工作的类
	 */
	class RetrieveWorker implements Runnable {
		boolean busy = false;

		public void run() {
			// buttonStopretrieve.setText("中止查询");
			busy = true;
			lbstatus.setText("正在查询......");
			logger.debug("hov开始查询.....");
			DBTableModel memds = execQuery();
			logger.debug("查询结果memds=" + memds + ",busy=" + busy);
			if (memds != null) {
				logger.debug("查询到记录" + memds.getRowCount());
			}
			if (memds == null) {
				busy = false;
				lbstatus.setText("查询出错");
				// 不要对话框
				// errorMessage("出错",queryerrormsg);
				return;
			}
			if (busy) {
				logger.debug("begin bindmemds");
				// DBTableModel model = (DBTableModel) table.getModel();
				DBTableModel model = sumdbmodel;
				int oldrowcount = model.getRowCount();
				int oldrow = table.getRow();
				if (oldrow < 0)
					oldrow = 0;
				model.bindMemds(memds);
				// logger.debug("finished bindmemds,begin resize table");
				resizeTable();
				// logger.debug("finished resize table");
				if (hovdialog != null && hovdialog.isVisible()
						&& dlgtable != null) {
					if (autoReturn() && dlgtable.getRowCount() == 2) {
						dlgtable.getSelectionModel().setSelectionInterval(0, 0);
						onOk();
						busy = false;
						return;
					}
					if (dlgtable.getRowCount() > 1) {
						if (oldrowcount == 1) {
							dlgtable.getSelectionModel().setSelectionInterval(
									0, 0);
							dlgtable.scrollToCell(0, 0);
						} else if (oldrow < model.getRowCount() - 1) {
							dlgtable.getSelectionModel().setSelectionInterval(
									oldrow, oldrow);
							dlgtable.scrollToCell(oldrow, 0);
						}
						dlgtable.requestFocus();
						setBtnmoreEnable();
					}
				}

				if (table.getRowCount() > 1) {
					if (oldrowcount == 1) {
						table.getSelectionModel().setSelectionInterval(0, 0);
						table.scrollToCell(0, 0);
					} else {
						table.getSelectionModel().setSelectionInterval(oldrow,
								oldrow);
						table.scrollToCell(oldrow, 0);
					}
				}
				if (table.getRowCount() == 2) {
					if (hovlistener != null)
						hovlistener.on_hov(null, getResult());
				}

			}
			busy = false;
			logger.debug("begin call on_retrieved");
			on_retrieved();
			StringBuffer sb = new StringBuffer();
			sb.append("查询到记录" + (tablemodel.getRowCount()) + "条记录.");
			if (hasmore) {
				sb.append("F5查询更多");
			} else {
				sb.append("没有更多记录了");
			}
			lbstatus.setText(sb.toString());
			logger.debug(sb.toString());
		}
	}

	/**
	 * 如果一条记录是不是自动返回
	 * 
	 * @return true如果只有一条记录就自动选择返回. false不要自动选择返回
	 */
	protected boolean autoReturn() {
		return true;
	}

	/**
	 * 显示状态
	 * 
	 * @param msg
	 */
	void setStatusMessage(String msg) {
		lbstatus.setText(msg);
	}

	/**
	 * 附加的查询where条件
	 */
	private String otherwheres = "";

	/**
	 * 设置附加的查询where条件
	 * 
	 * @param otherwheres
	 */
	public void setOtherwheres(String otherwheres) {
		this.otherwheres = otherwheres;
	}

	/**
	 * 打开浮动窗口选择HOV.
	 * 
	 * @param colname
	 *            触发列名
	 * @param value
	 *            值
	 * @param listener
	 *            hov监听器
	 * @param otherwheres
	 *            附加的where条件
	 */
	public void doSelectHov(String colname, String value, HovListener listener,
			String otherwheres) {
		this.hovlistener = listener;
		this.otherwheres = otherwheres;
		this.usedlgwin = false;

		if (!inited) {
			initWindow();
			inited = true;
		}

		querycond.clearControl();

		if (value.length() > 0) {
			Querycondline condline = querycond
					.getQuerycondline(getCondcolname(colname));
			if (condline != null) {
				condline.setValue(value);
				doQuery();
			} else {
				logger.error("内部错误," + this.getClass().getName() + "被触发,触发列"
						+ colname + ",但是没有找到相关条件列");
			}
		}

		/*
		 * if (result) { //System.out.println("Return "+resultrec); DBTableModel
		 * memds = new DBTableModel(); DBTableModel model = (DBTableModel)
		 * table.getModel(); int row = table.getSelectedRow();
		 * memds.setDisplaycolumninfos(model.getDisplaycolumninfos());
		 * memds.addRecord(model.getRecordThunk(row)); return memds; } else {
		 * return null; }
		 */
	}

	/**
	 * 如果触发列和hov查询条件列名不一样,可以重载本函数 例如触发列为goodsopcode, 查询条件列为opcode
	 * 缺省返回invokecolname
	 * 
	 * @param invokecolname
	 * @return
	 */
	protected String getCondcolname(String invokecolname) {
		return invokecolname;
	}

	/**
	 * 调用控件
	 */
	JComponent caller = null;

	/**
	 * 自动设置hov浮动窗口位置
	 */
	private void autoSetlocation() {
		Point textpoint;
		try {
			textpoint = caller.getLocationOnScreen();
		} catch (Exception e) {
			return;
		}
		// textpoint.setLocation(textpoint.getX(), textpoint.getY());
		int compheight = caller.getHeight();

		Dimension screensize = hovwindow.getToolkit().getScreenSize();
		Dimension thissize = hovwindow.getPreferredSize();
		double x, y;
		x = screensize.getWidth() - thissize.getWidth();
		if (x > textpoint.getX()) {
			x = textpoint.getX();
		}
		if ((double) textpoint.y > screensize.getHeight() / 2.0) {
			// 放在上面
			double maxheight = textpoint.y;
			if (thissize.getHeight() > maxheight) {
				thissize.setSize(thissize.getWidth(), maxheight);
				hovwindow.setPreferredSize(thissize);
			}
			// 算y坐标
			y = textpoint.y - thissize.getHeight();
		} else {
			// 放在下面
			double maxheight = screensize.getHeight() - textpoint.y
					- compheight;
			if (thissize.getHeight() > maxheight) {
				thissize.setSize(thissize.getWidth(), maxheight);
				hovwindow.setPreferredSize(thissize);
			}
			// 算y坐标
			y = textpoint.y + compheight;

		}

		/*
		 * //要换算到parentwindow的位置。 Point windowpoint =
		 * parentwindow.getLocationOnScreen(); x = x - windowpoint.x; y = y -
		 * windowpoint.y;
		 */

		hovwindow.setLocation((int) x, (int) y);
	}

	/**
	 * 显示HOV浮动窗口
	 * 
	 * @param parentwindow
	 * @param caller
	 */
	public void show(Window parentwindow, JComponent caller) {
		if (parentwindow != this.parentwindow) {
			this.parentwindow = parentwindow;
			this.initWindow();
			this.inited = true;
		}
		if (hovwindow.isVisible()) {
			return;
		}
		this.caller = caller;
		autoSetlocation();
		hovwindow.pack();
		hovwindow.setVisible(true);
	}

	/**
	 * 以传统系统模态对话窗口方式选hov
	 * 
	 * @param parent
	 *            调用窗口
	 * @param title
	 *            标题
	 * @param colname
	 *            触发列名
	 * @param value
	 *            触发列值
	 * @param otherwheres
	 *            附加查询where条件
	 * @return null选择失败 选择成功返回仅选择的记录
	 */
	public DBTableModel showDialog(Frame parent, String title, String colname,
			String value, String otherwheres) {
		this.usedlgwin = true;
		if (!inited) {
			inited = true;
			initWindow();
		}
		querycond.clearControl();
		this.otherwheres = otherwheres;

		if (hovwindow != null && hovwindow.isVisible()) {
			hovwindow.setVisible(false);
		}

		if (hovdialog == null || hovdialog.getParent() != parent) {
			hovdialog = createHovdialog(parent, title);
		}
		reset();

		hovdialog.ok = false;
		hovdialog.pack();
		if (value.length() > 0) {
			Querycondline condline = querycond.getQuerycondline(colname);
			if (condline != null) {
				condline.setValue(value);
			}
		}
		// 如果有value，进行查询
		if (value.length() > 0 || autoSelect()) {
			doQuery();
		}

		hovdialog.setVisible(true);
		if (hovdialog.ok) {
			return getResult();
		} else {
			return null;
		}
	}

	boolean showdesignbtn = false;

	public void showDesignbtn(boolean b) {
		showdesignbtn = b;
	}

	/**
	 * 以传统系统模态对话窗口方式选hov
	 * 
	 * @param parent
	 *            调用对话窗口
	 * @param title
	 *            标题
	 * @param colname
	 *            触发列
	 * @param value
	 *            触发列值
	 * @param otherwheres
	 *            其它的查询where条件
	 * @return
	 */
	public DBTableModel showDialog(Dialog parent, String title, String colname,
			String value, String otherwheres) {
		this.usedlgwin = true;
		if (!inited) {
			inited = true;
			initWindow();
		}
		querycond.clearControl();
		this.otherwheres = otherwheres;

		if (hovwindow != null && hovwindow.isVisible()) {
			hovwindow.setVisible(false);
		}

		if (hovdialog == null || hovdialog.getParent() != parent) {
			hovdialog = createHovdialog(parent, title);
		}
		reset();
		hovdialog.ok = false;
		hovdialog.pack();
		if (value.length() > 0) {
			Querycondline condline = querycond.getQuerycondline(colname);
			if (condline != null) {
				condline.setValue(value);
			}
		}
		// 如果有value，进行查询
		if (value.length() > 0 || autoSelect()) {
			doQuery();
		}

		hovdialog.setVisible(true);
		if (hovdialog.ok) {
			return getResult();
		} else {
			return null;
		}
	}

	/**
	 * 以传统系统模态对话窗口方式选hov
	 * 
	 * @param parent
	 *            调用窗口
	 * @param title
	 *            标题
	 * @return
	 */
	public DBTableModel showDialog(Frame parent, String title) {
		this.usedlgwin = true;
		if (!inited) {
			inited = true;
			initWindow();
		}

		if (hovwindow != null && hovwindow.isVisible()) {
			hovwindow.setVisible(false);
		}

		if (hovdialog == null || hovdialog.getParent() != parent) {
			hovdialog = createHovdialog(parent, title);
		}
		reset();
		querycond.clearControl();
		hovdialog.ok = false;
		hovdialog.pack();

		if (autoSelect()) {
			doQuery();
		}

		hovdialog.setVisible(true);
		if (hovdialog.ok) {
			return getResult();
		} else {
			return null;
		}
	}

	/**
	 * 创建对话窗口
	 * 
	 * @param parent
	 * @param title
	 * @return
	 */
	protected HovDialog createHovdialog(Frame parent, String title) {
		return new HovDialog(parent, title);
	}

	/**
	 * 创建对话窗口
	 * 
	 * @param parent
	 * @param title
	 * @return
	 */
	protected HovDialog createHovdialog(Dialog parent, String title) {
		return new HovDialog(parent, title);
	}

	/**
	 * 隐藏浮动窗口或关闭对话窗口
	 */
	public void hide() {
		if (usedlgwin) {
			onCancel();
		} else {
			if (hovwindow != null && hovwindow.isVisible()) {
				hovwindow.setVisible(false);
			}
		}
	}

	/**
	 * 定位到下一行
	 */
	public void nextRow() {
		if (table == null || table.getRowCount() == 0)
			return;
		int row = table.getSelectedRow();
		row++;
		if (row >= table.getRowCount()) {
			row = 0;
		}
		table.addRowSelectionInterval(row, row);
		table.scrollToCell(row, 0);
	}

	/**
	 * 定位到上一行
	 */
	public void priorRow() {
		if (table == null || table.getRowCount() == 0)
			return;
		int row = table.getSelectedRow();
		row--;
		if (row < 0) {
			row = table.getRowCount() - 1;
		}
		table.addRowSelectionInterval(row, row);
		table.scrollToCell(row, 0);
	}

	/**
	 * 取返回结果.
	 * 
	 * @return null表示选择失败. 选择成功返回仅选择的记录
	 */
	public DBTableModel getResult() {

		DBTableModel resultmodel = new DBTableModel();
		DBTableModel model = (DBTableModel) table.getModel();
		int row;
		if (usedlgwin) {
			row = dlgtable.getSelectedRow();
		} else {
			row = table.getSelectedRow();
		}
		if (row < 0) {
			return null;
		}
		if (row > table.getModel().getRowCount() - 1) {
			return null;
		}
		resultmodel.setDisplaycolumninfos(model.getDisplaycolumninfos());
		resultmodel.addRecord(model.getRecordThunk(row));

		if (!this.oncheckResultdata(resultmodel)) {
			return null;
		}

		return resultmodel;
	}

	/**
	 * 供继承类检查数据.
	 * 
	 * @param resultdbmodel
	 *            HOV返回的结果
	 * @return 返回true,getResult()将返回选择的数据. 返回false, HOV不关闭,getResult()返回的结果为null
	 */
	protected boolean oncheckResultdata(DBTableModel resultdbmodel) {
		return true;
	}

	/**
	 * 是否显示了浮动窗口或对话窗口
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return hovwindow != null && hovwindow.isVisible();
	}

	/**
	 * 浮动窗口类
	 * 
	 * @author Administrator
	 * 
	 */
	class HovWindow extends JWindow {
		Dimension preferredsize = null;

		public HovWindow(Window owner) {
			super(owner);
		}

		public Insets getInsets() {
			return new Insets(2, 2, 2, 2);
		}

		public void setPreferredSize(Dimension preferredSize) {
			super.setPreferredSize(preferredSize);
			this.preferredsize = preferredSize;
		}

		public Dimension getPreferredSize() {
			if (preferredsize == null) {
				return super.getPreferredSize();
			} else {
				return preferredsize;
			}
		}
	}

	/**
	 * 取列名
	 */
	public String getClassname() {
		return this.getClass().getName();
	}

	/**
	 * 对话窗口类
	 */
	protected class HovDialog extends CDialog {
		public boolean ok = false;

		public HovDialog(Frame owner, String title) throws HeadlessException {
			super(owner, title, true);
			this.addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowClosing(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			initDialog();
		}

		public HovDialog(Dialog owner, String title) throws HeadlessException {
			super(owner, title, true);
			initDialog();
		}

		protected void initDialog() {
			Container cp = this.getContentPane();
			JPanel inputpane = buildQuerypanel(querycond);
			cp.add(inputpane, BorderLayout.NORTH);
			JPanel bottompane = createBottompanelDlg();
			cp.add(bottompane, BorderLayout.SOUTH);

			createDlgDatapanel(cp);
			this.localScreenCenter();

			InputMap dlginputmap = ((JComponent) cp)
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			KeyStroke vesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			dlginputmap.put(vesc, "dlgclose");
			((JComponent) cp).getActionMap().put("dlgclose",
					new CloseDlgAction());

			KeyStroke vkf10 = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0, false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkf10,
					ACTION_OK);
			((JComponent) cp).getActionMap().put(ACTION_OK,
					new HovAction(ACTION_OK));

			KeyStroke vkf5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkf5,
					"more");
			((JComponent) cp).getActionMap().put("more", new HovAction("more"));

			KeyStroke vkf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
			dlginputmap.put(vkf12, "clearcond");
			((JComponent) cp).getActionMap().put("clearcond",
					new ClearcondAction());

			/*
			 * KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
			 * false); dlginputmap.put(enter, ACTION_OK);
			 * ((JComponent)cp).getActionMap().put(ACTION_OK,new
			 * HovAction(ACTION_OK));
			 */

		}

		class ClearcondAction extends AbstractAction {
			public ClearcondAction() {
				super();
			}

			public void actionPerformed(ActionEvent e) {
				querycond.clearControl();
				if (querycond.size() > 0) {
					querycond.elementAt(0).getDbcolumndisplayinfo()
							.getEditComponent().requestFocus();
				}
			}
		}

		/**
		 * 创建对话窗口下部状态条
		 * 
		 * @return
		 */
		protected JPanel createBottompanelDlg() {
			JPanel toolpane = new JPanel();
			JButton buttonok = new JButton("确定(F10)");
			buttonok.setActionCommand(ACTION_OK);
			buttonok.addActionListener(new HovAction(ACTION_OK));
			toolpane.add(buttonok);

			btnmore = new JButton("更多(F5)");
			btnmore.setActionCommand("more");
			btnmore.addActionListener(new HovAction("more"));
			toolpane.add(btnmore);
			setBtnmoreEnable();

			addOtherbutton(toolpane);

			JButton btncancel = new JButton("取消(ESC)");
			btncancel.setActionCommand(ACTION_CANCEL);
			btncancel.addActionListener(new HovAction(ACTION_CANCEL));
			toolpane.add(btncancel);

			if (DefaultNPParam.develop == 1 || showdesignbtn) {
				JButton btndesign = new JButton("调整");
				btndesign.setActionCommand(ACTION_DESIGN);
				btndesign.addActionListener(new HovAction(ACTION_DESIGN));
				toolpane.add(btndesign);
			}

			/*
			 * buttonStopretrieve = new JButton("关闭");
			 * buttonStopretrieve.setActionCommand(ACTION_STOPRETRIEVE);
			 * buttonStopretrieve.addActionListener(new
			 * HovAction(ACTION_STOPRETRIEVE));
			 * toolpane.add(buttonStopretrieve);
			 */

			JPanel statuspane = new JPanel();

			lbstatus = new JLabel("请输入条件按回车,按F12重新输入条件");
			lbstatus.setPreferredSize(new Dimension(600, 20));
			statuspane.add(lbstatus);

			JPanel jp = new JPanel();

			jp.setLayout(new BorderLayout());
			jp.add(toolpane, BorderLayout.NORTH);
			jp.add(statuspane, BorderLayout.SOUTH);

			return jp;
		}

	}

	/**
	 * 关闭对话窗口Action
	 * 
	 * @author Administrator
	 * 
	 */
	class CloseDlgAction extends AbstractAction {
		public CloseDlgAction() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			onCancel();
		}
	}

	/**
	 * 生成查询条件Panel
	 * 
	 * @param querycond
	 * @return
	 */
	protected JPanel buildQuerypanel(Querycond querycond) {
		JComponent[] textcontrols = new JComponent[querycond.size()];

		JPanel querypanel = new JPanel();
		CFormlayout formlayout = new CFormlayout(3, 5);
		querypanel.setLayout(formlayout);

		int i = 0;
		Enumeration en = querycond.elements();
		while (en.hasMoreElements()) {
			Querycondline condline = (Querycondline) en.nextElement();
			String title = condline.getTitle();

			JLabel lb = new JLabel(title);
			lb.setPreferredSize(new Dimension(80, 27));
			querypanel.add(lb);

			JComponent text = condline.getCondEditcomp();
			text.setEnabled(true);
			InputMap im = text.getInputMap().getParent();
			if (im != null) {
				KeyStroke esckey = KeyStroke
						.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
				// System.out.println(im.get(esckey));
				im.remove(esckey);
			}

			addEnterkeyTraver(text);

			Dimension preferredSize = text.getPreferredSize();
			preferredSize.setSize(preferredSize.getWidth(), 27.0);
			text.setPreferredSize(preferredSize);
			querypanel.add(text);
			if (condline.isLinebreak()) {
				formlayout.addLayoutComponent(text, new CFormlineBreak());
			}

			textcontrols[i] = text;

			i++;
		}

		return querypanel;
	}

	protected void setBtnmoreEnable() {
		if (btnmore == null)
			return;
		btnmore.setEnabled(hasmore);
	}

	/**
	 * 可重载加入其它按钮
	 * 
	 * @param toolpane
	 */
	protected void addOtherbutton(JPanel toolpane) {
	}

	/**
	 * 生成对话窗口的数据Panel
	 * 
	 * @param cp
	 */
	protected void createDlgDatapanel(Container cp) {
		dlgtable = createTable();
		dlgtable.setReadonly(true);
		dlgtable.setRequestFocusEnabled(true);
		dlgtable.getTableHeader().setUI(new CTableheadUI());

		// 去掉enter下一行 ,F2编辑
		InputMap map = dlgtable.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));

		dlgtable.addKeyListener(getDlgHovKeylistener());
		dlgtable.addMouseListener(getDlgmouselistener());

		dlgtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dlgtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		dlgtablescrollpane = new JScrollPane(dlgtable);
		dlgtablescrollpane.setBackground(Color.WHITE);
		cp.add(dlgtablescrollpane, BorderLayout.CENTER);

	}

	/**
	 * 设置热键
	 * 
	 * @param comp
	 */
	protected void addEnterkeyTraver(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);

		comp.getInputMap(JComponent.WHEN_FOCUSED).put(enterkey, ACTION_QUERY);
		comp.getActionMap().put(ACTION_QUERY, new HovAction(ACTION_QUERY));

		KeyStroke esckey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		comp.getInputMap(JComponent.WHEN_FOCUSED).put(esckey, "dlgclose");
		comp.getActionMap().put("dlgclose", new CloseDlgAction());

	}

	/**
	 * 返回HOV键盘事件监听器
	 * 
	 * @return
	 */
	protected KeyListener getDlgHovKeylistener() {
		return new DlgHovKeylistener();
	}

	/**
	 * 键盘事件监听器类
	 * 
	 * @author Administrator
	 * 
	 */
	class DlgHovKeylistener implements KeyListener {
		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				// 按回车了
				onOk();
			} else if (e.getKeyCode() == 27) {
				// 按ESC
				onCancel();
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

	/**
	 * 返回鼠标事件监听器类
	 * 
	 * @return
	 */
	protected MouseListener getDlgmouselistener() {
		return new DlgHovMouseListener();
	}

	/**
	 * 鼠标事件监听器类
	 * 
	 * @author Administrator
	 * 
	 */
	class DlgHovMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				if (dlgtable.getSelectedRow() >= 0) {
					onOk();
				}
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

	/**
	 * 返回是否使用文件
	 * 
	 * @return
	 */
	public boolean isUsefile() {
		return usefile;
	}

	/**
	 * 设置是否使用文件
	 * 
	 * @param usefile
	 */
	public void setUsefile(boolean usefile) {
		this.usefile = usefile;
	}

	/**
	 * 返回文件名
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 设置文件名
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	FiledbSearchCond[] otherfileconds = null;
	private JScrollPane tablescrollpane;
	private JScrollPane dlgtablescrollpane;

	/**
	 * 设置从文件查询默认的条件
	 * 
	 * @param conds
	 */
	public void setOtherFilecond(FiledbSearchCond[] conds) {
		otherfileconds = conds;
	}

	/**
	 * 释放内存
	 */
	public void freeMemory() {
		if (querycond != null) {
			querycond.freeMemory();
			querycond = null;
		}
		lbstatus = null;

		if (table != null) {
			table.freeMemory();
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			dbmodel.clearAll();
			table.tableChanged(new TableModelEvent(table.getModel()));
			dbmodel.freeMemory();
			table = null;
		}
		if (dlgtable != null) {
			DBTableModel dbmodel = (DBTableModel) dlgtable.getModel();
			dbmodel.clearAll();
			dlgtable.tableChanged(new TableModelEvent(dlgtable.getModel()));
			dbmodel.freeMemory();
			dlgtable = null;
		}

		retrieveworker = null;

		if (hovwindow != null) {
			hovwindow.dispose();
			hovwindow = null;
		}
		if (hovdialog != null) {
			hovdialog.dispose();
			hovdialog = null;
		}

		parentwindow = null;

	}

	/**
	 * 自检
	 * 
	 * @return
	 */
	public String selfCheck() {
		if (!inited) {
			initWindow();
			inited = true;
		}

		// 检查列
		// 分析form列
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "检查失败";
		}

		// 查询条件列
		Vector<SelfcheckError> errors = new Vector<SelfcheckError>();

		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		for (int i = 0; i < querycond.getCondlinecount(); i++) {
			Querycondline ql = querycond.get(i);
			cols.add(ql.getDbcolumndisplayinfo());
			if (i == 0
					&& ql.getDbcolumndisplayinfo().getColname().toLowerCase()
							.endsWith("id")) {
				SelfcheckError error = new SelfcheckError("UI0005",
						SelfcheckConstants.UI0005);
				error.setMsg(ql.getDbcolumndisplayinfo().getColname()
						+ "不应该是第一个查询条件列");
				errors.add(error);
			}
		}
		DBColumnChecker.checkOrder(cols, errors);
		DBColumnUppercaseChecker.checkUppercase(cols, errors);

		// 表格列
		cols = ((DBTableModel) tablemodel).getDisplaycolumninfos();
		DBColumnChecker.checkOrder(cols, errors);

		Enumeration<SelfcheckError> en = errors.elements();
		while (en.hasMoreElements()) {
			SelfcheckError error = en.nextElement();
			out.print(this.getClass().getName() + ":");
			error.dump(out);
		}
		out.flush();

		String rets;
		try {
			rets = new String(bout.toByteArray(), "gbk");
			return rets;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "检查失败";
		}
	}

	/**
	 * 查询结束
	 */
	protected void on_retrieved() {

	}

	/**
	 * 清除数据
	 */
	protected void reset() {
		DBTableModel dbmodel = (DBTableModel) tablemodel;
		dbmodel.clearAll();
		if (dlgtable != null) {
			dlgtable.tableChanged(new TableModelEvent(dbmodel));
		}
		if (table != null) {
			table.tableChanged(new TableModelEvent(dbmodel));
		}
		if (lbstatus != null) {
			lbstatus.setText("请输入条件按回车,按F12重新输入条件");
		}
	}

	public void errorMessage(String title, String msg) {
		JOptionPane.showMessageDialog(null, msg, title,
				JOptionPane.ERROR_MESSAGE);

	}

	protected class PlainTablecellRender extends DefaultTableCellRenderer {
		JLabel lbnormal = null;
		JLabel lbbold = null;

		DBColumnDisplayInfo colinfo = null;

		public PlainTablecellRender(DBColumnDisplayInfo colinfo) {
			this.colinfo = colinfo;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (lbnormal == null) {
				lbnormal = new JLabel();
				lbnormal.setOpaque(true);
				lbbold = new JLabel();
				lbbold.setOpaque(true);
				Font normalfont = null;
				Font boldfont = null;
				Font font = table.getFont();

				String name = font.getFontName();
				int size = font.getSize();
				normalfont = new Font(name, Font.PLAIN, size);
				boldfont = new Font(name, Font.BOLD, size);

				lbnormal.setFont(normalfont);
				lbbold.setFont(boldfont);
			}

			JLabel lb = null;
			RecordTrunk rec = ((DBTableModel) sumdbmodel).getRecordThunk(row);
			if (rec.getSumflag() == RecordTrunk.SUMFLAG_SUMMARY) {
				lb = lbbold;
			} else {
				lb = lbnormal;
			}

			int modelIndex = table.getColumnModel().getColumn(column)
					.getModelIndex();
			Vector<DBColumnDisplayInfo> formcolumndisplayinfos = ((DBTableModel) sumdbmodel)
					.getDisplaycolumninfos();

			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(modelIndex);

			if (colinfo.getColtype().equals("number")) {
				lb.setHorizontalAlignment(JLabel.RIGHT);
			} else {
				lb.setHorizontalAlignment(JLabel.LEFT);
			}

			boolean islastrow = table.getRowCount() - 1 == row;

			if (isSelected && !islastrow) {
				lb.setForeground(table.getSelectionForeground());
				lb.setBackground(table.getSelectionBackground());
			} else {
				lb.setForeground(table.getForeground());
				if (row % 2 == 0) {
					lb.setBackground(table.getBackground());
				} else {
					lb.setBackground(secondbackcolor);
				}
				/*
				 * if (colinfo.isReadonly()) {
				 * lb.setBackground(readonlybackcolor); }
				 */
			}

			// 因为这句重载了整个函数
			// setFont(table.getFont());

			if (hasFocus) {
				Border border = null;
				if (isSelected) {
					border = UIManager
							.getBorder("Table.focusSelectedCellHighlightBorder");
				}
				if (border == null) {
					border = UIManager
							.getBorder("Table.focusCellHighlightBorder");
				}
				lb.setBorder(border);

				if (!isSelected && table.isCellEditable(row, column)) {
					Color col;
					col = UIManager.getColor("Table.focusCellForeground");
					if (col != null) {
						lb.setForeground(col);
					}
					col = UIManager.getColor("Table.focusCellBackground");
					if (col != null) {
						lb.setBackground(col);
					}
				}
			} else {
				lb.setBorder(new EmptyBorder(1, 1, 1, 1));
			}

			String newvalue = (String) value;
			// 数字format
			// if (colinfo.getColtype().equals("number")) {
			newvalue = colinfo.getFormatvalue(newvalue);
			// }

			// 是否有下拉？
			if (colinfo.getEditComponent() instanceof CComboBox) {
				DBTableModel cbdbmodel = colinfo.getCbdbmodel();
				if (cbdbmodel != null) {
					boolean bfind = false;
					for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
						if (cbdbmodel.getItemValue(r, "key").equals(newvalue)) {
							newvalue = cbdbmodel.getItemValue(r, "value");
							bfind = true;
							break;
						}
					}
					if (!bfind) {
						CComboBox ccb = (CComboBox) colinfo.getEditComponent();
						CComboBoxModel ccbmodel = (CComboBoxModel) ccb
								.getModel();
						for (int i = 0; i < ccbmodel.getSize(); i++) {
							if (newvalue.equals(ccbmodel.getKeyvalue(i))) {
								newvalue = (String) ccbmodel.getElementAt(i);
							}
						}
					}
				}
			} else if (colinfo.getEditComponent() instanceof CCheckBox) {
				if (newvalue == null || newvalue.length() == 0) {
					newvalue = "";
				} else {
					newvalue = newvalue.equals("1") ? "是" : "否";
				}
			} else if (rec.getSumflag() != RecordTrunk.SUMFLAG_SUMMARY
					&& colinfo.getColname().equalsIgnoreCase("filegroupid")) {
				newvalue = "附件";
			}

			lb.setText(newvalue);

			Color c = getCellColor(row, colinfo);
			if (c == null) {
				lb.setForeground(Color.BLACK);
			} else {
				lb.setForeground(c);
			}

			return lb;

		}

		public void freeMemory() {
			lbnormal = null;
			lbbold = null;
			colinfo = null;
		}
	}

	/**
	 * 重载本函数可设置单元格的颜色
	 * 
	 * @param row
	 *            行
	 * @param colinfo
	 *            列信息
	 * @return
	 */
	protected Color getCellColor(int row, DBColumnDisplayInfo colinfo) {
		return null;
	}

	/**
	 * 表格背景颜色,区别不同的行
	 */
	protected Color secondbackcolor = new Color(240, 240, 240);

	public DBTableModel getTablemodel() {
		return tablemodel;
	}

	/**
	 * 打开hov对话框就自动查询。缺省是false
	 * 
	 * @return
	 */
	protected boolean autoSelect() {
		return false;
	}

	public Hovcallback getHovcallback() {
		return hovcallback;
	}

	public void setHovcallback(Hovcallback hovcallback) {
		this.hovcallback = hovcallback;
	}

	/**
	 * 确定返回hov
	 */
	protected void onOk() {
		if (getResult() == null)
			return;
		dlgtable.confirm();
		hovdialog.ok = true;
		hovdialog.dispose();
	}

	/**
	 * 放弃hov
	 */
	protected void onCancel() {
		hovdialog.ok = false;
		hovdialog.dispose();
	}

	protected void readQuerycond() throws Exception {
		querycond = new Querycond();
		BufferedReader rd = null;
		try {
			String line;
			rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<querycond>")) {
					break;
				}
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</querycond>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				DBColumnDisplayInfo colinfo = DBColumnInfoStoreHelp
						.readOneColumn(line);

				if ((querycond.size() + 1) % 3 == 0) {
					colinfo.setLinebreak(true);
				} else {
					colinfo.setLinebreak(false);
				}

				Querycondline ql = new Querycondline(querycond, colinfo);
				querycond.add(ql);

			}

		} finally {
			if (rd != null) {
				rd.close();
			}
		}
	}

	protected void readColumns() throws Exception {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		ArrayList<String> ar = new ArrayList<String>();
		BufferedReader rd = null;
		try {
			String line;
			rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<columns>")) {
					break;
				}
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</columns>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				DBColumnDisplayInfo colinfo = DBColumnInfoStoreHelp
						.readOneColumn(line);
				cols.add(colinfo);
				if (colinfo.getTablecolumnwidth() < 0) {
					colinfo.setTablecolumnwidth(65);
				}
				ar.add(colinfo.getColname());
			}
			colnames = new String[ar.size()];
			ar.toArray(colnames);

		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		tablemodel = new DBTableModel(cols);
		sumdbmodel = new Sumdbmodel((DBTableModel) tablemodel, null);
	}

	protected void readDefaultsql() throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader rd = null;
		try {
			rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<defaultsql>")) {
					break;
				}
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</defaultsql>")) {
					break;
				}
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(line);
			}
		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		this.defaultsql = sb.toString();
	}

	protected void readDesc() throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader rd = null;
		try {
			String line;
			rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<hovdesc>")) {
					break;
				}
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</hovdesc>")) {
					break;
				}
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(line);
			}
		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		this.hovdesc = sb.toString();
	}

	protected void readViewname() throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader rd = null;
		try {
			String line;
			rd = DBColumnInfoStoreHelp.getReaderFromFile(configfile);
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<viewname>")) {
					break;
				}
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</viewname>")) {
					break;
				}
				if (sb.length() > 0)
					sb.append("\n");
				sb.append(line);
			}
		} finally {
			if (rd != null) {
				rd.close();
			}
		}
		this.viewname = sb.toString();
	}

	public String[] getColumns() {
		return colnames;
	}

	public abstract String getDesc();

	public void writeHov(PrintWriter out) throws Exception {
		out.println("<hovtype>");
		out.println("hovbase");
		out.println("</hovtype>");
		out.println("<querycond>");
		for (int i = 0; i < querycond.getCondlinecount(); i++) {
			Querycondline ql = querycond.get(i);
			DBColumnDisplayInfo colinfo = ql.getDbcolumndisplayinfo();
			DBColumnInfoStoreHelp.writeOneColumn(colinfo, out);
		}
		out.println("</querycond>");

		// 输出列
		out.println("<columns>");
		for (int i = 0; i < dlgtable.getColumnCount(); i++) {
			TableColumn tc = dlgtable.getColumnModel().getColumn(i);
			int mindex = dlgtable.convertColumnIndexToModel(i);
			DBColumnDisplayInfo colinfo = tablemodel.getDisplaycolumninfos()
					.elementAt(mindex);
			int cw = tc.getWidth();
			// logger.debug(colinfo.getColname() + " width=" + cw);
			colinfo.setTablecolumnwidth(cw);
		}

		Enumeration<DBColumnDisplayInfo> en = tablemodel
				.getDisplaycolumninfos().elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			DBColumnInfoStoreHelp.writeOneColumn(colinfo, out);
		}
		out.println("</columns>");

		out.println("<defaultsql>");
		out.println(defaultsql);
		out.println("</defaultsql>");

		out.println("<hovdesc>");
		out.println(hovdesc);
		out.println("</hovdesc>");

		out.println("<viewname>");
		out.println(viewname);
		out.println("</viewname>");

	}

	public String getViewname() {
		return viewname;
	}

	public void setViewname(String viewname) {
		this.viewname = viewname;
	}

	protected File configfile;
	protected String hovdesc;
	protected String viewname;
	protected String colnames[];

	/**
	 * 是否启用config文件?
	 */
	protected boolean useconfigfile = false;

	/**
	 * 检查下载专项
	 */
	protected void downloadZx() {

		// 先从服务器下载专项文件,如果没有,检查本地
		Zxhovdownloader zxhovdl = new Zxhovdownloader();
		File zxzipfile = null;
		File tempfile = null;
		try {
			String classname = this.getClass().getName();
			zxzipfile = zxhovdl.downloadZxzip(classname);
			if (zxzipfile != null) {
				tempfile = File.createTempFile("zxhov", ".zip");
				ZipHelper.extractFile(zxzipfile, "hov.model", tempfile);
				configfile = tempfile;
				createFromConfigfile();
				return;
			}
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if (tempfile != null) {
				tempfile.delete();
			}
		}
		String pathname = getDefaultConfigfilepath();
		logger.debug("加载model文件:" + pathname);
		configfile = new File(pathname);
		createFromConfigfile();
		return;

	}

	String getDefaultConfigfilepath() {
		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p > 0) {
			classname = classname.substring(p + 1);
		}

		// 本地是否有呢?
		URL url = getClass().getResource(classname + ".model");
		logger.debug("url=" + url);
		if (url == null) {
			logger.error(this.getClass().getName() + "加载" + classname
					+ ".model失败");
			return null;
		}
		String pathname = url.toString();
		logger.debug("pathname=" + pathname);
		return pathname;

	}

	protected void createFromConfigfile() {
		try {
			logger.debug("createFromConfigfile =" + configfile.getPath());
			readDesc();
			readViewname();
			readDefaultsql();
			readQuerycond();
			readColumns();
			useconfigfile = true;
		} catch (Exception e) {
			useconfigfile = false;
			logger.error("error", e);
		}

	}

	public DBColumnDisplayInfo getColinfo(String colname) {
		return tablemodel.getColumninfo(colname);
	}

	class CHovsetupDlg extends CDialog {
		private JTextField textHovname;
		private JTextField textViewname;
		private JTextArea textSql;
		private JPanel listquerycondpane;
		private JPanel listcolumnpane;
		Vector<DragableLabel> dragpanesquerycond = new Vector<DragableLabel>();
		Vector<DragableLabel> dragpanescolumn = new Vector<DragableLabel>();

		public CHovsetupDlg(Dialog frame, CHovBase hov, java.io.File configfile) {
			super(frame, "设置HOV", true);
			init();
			bind();
			this.localCenter();
			this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		}

		void bind() {
			try {
				// CHovbaseGeneral hov = new CHovbaseGeneral(configfile);
				// if (hov.getDesc().length() == 0) {
				// return;
				// }

				textHovname.setText(getDesc());
				textViewname.setText(getViewname());
				textSql.setText(defaultsql);
				textSql.getCaret().setDot(0);
				retrieveColumns(getViewname());

				// 绑定
				HashMap<String, String> condnamemap = new HashMap<String, String>();
				Querycond querycond = getQuerycond();
				for (int i = 0; i < querycond.getCondlinecount(); i++) {
					Querycondline ql = querycond.get(i);
					DBColumnDisplayInfo colinfo = ql.getDbcolumndisplayinfo();
					condnamemap.put(colinfo.getColname().toLowerCase(), "");
				}
				Enumeration<DragableLabel> en = dragpanesquerycond.elements();
				while (en.hasMoreElements()) {
					DragableLabel lb = en.nextElement();
					if (condnamemap.get(lb.getColname().toLowerCase()) != null) {
						lb.cb.setSelected(true);
					}
				}

				HashMap<String, String> colnamemap = new HashMap<String, String>();
				Enumeration<DBColumnDisplayInfo> en1 = getTablemodel()
						.getDisplaycolumninfos().elements();
				while (en1.hasMoreElements()) {
					DBColumnDisplayInfo colinfo = en1.nextElement();
					colnamemap.put(colinfo.getColname().toLowerCase(), "");
				}
				en = dragpanescolumn.elements();
				while (en.hasMoreElements()) {
					DragableLabel lb = en.nextElement();
					if (colnamemap.get(lb.getColname().toLowerCase()) != null) {
						lb.cb.setSelected(true);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void init() {
			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			cp.add(createToppane(), BorderLayout.NORTH);
			cp.add(createMidpane(), BorderLayout.CENTER);
			cp.add(createBottomPane(), BorderLayout.SOUTH);

		}

		JPanel createToppane() {
			JPanel jp = new JPanel();
			jp.setLayout(new FlowLayout());
			JLabel lb = new JLabel("HOV名称");
			jp.add(lb);
			textHovname = new JTextField(20);
			textHovname.setEditable(false);
			jp.add(textHovname);

			lb = new JLabel("表、视图名");
			jp.add(lb);
			textViewname = new JTextField(20);
			textViewname.setEditable(false);
			jp.add(textViewname);
			JButton btn;
			btn = new JButton("...");
			btn.setActionCommand("selectview");
			btn.addActionListener(this);
			jp.add(btn);

			return jp;
		}

		JPanel createMidpane() {
			JPanel jp = new JPanel();
			JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			jsp.setPreferredSize(new Dimension(600, 500));
			jp.add(jsp);
			jsp.setDividerLocation(65);

			JPanel sqlpane = new JPanel();
			CFormlayout formlayout = new CFormlayout(2, 2);
			sqlpane.setLayout(formlayout);

			textSql = new JTextArea(5, 80);
			textSql.setWrapStyleWord(true);
			textSql.setAutoscrolls(true);

			sqlpane.add(new JLabel("查询sql"));
			sqlpane.add(new JScrollPane(textSql));
			jsp.setLeftComponent(sqlpane);

			// 下部是tab页
			JTabbedPane jtp = new JTabbedPane();
			jsp.setRightComponent(jtp);

			listquerycondpane = createListcolumnPane();
			listcolumnpane = createListcolumnPane();
			jtp.add("查询条件设定", new JScrollPane(listquerycondpane));
			jtp.add("结果列设定", new JScrollPane(listcolumnpane));
			return jp;
		}

		JPanel createListcolumnPane() {
			JPanel jp = new JPanel();

			return jp;
		}

		JPanel createBottomPane() {
			JPanel jp = new JPanel();
			jp.setLayout(new FlowLayout());
			JButton btn = null;
			/*
			 * btn = new JButton("测试"); btn.setActionCommand("try");
			 * btn.addActionListener(this); jp.add(btn);
			 */
			btn = new JButton("保存");
			btn.setActionCommand("ok");
			btn.addActionListener(this);
			jp.add(btn);

			btn = new JButton("取消");
			btn.setActionCommand("cancel");
			btn.addActionListener(this);
			jp.add(btn);
			return jp;

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			// if (cmd.equals("try")) {
			// tryhov();
			if (cmd.equals("ok")) {
				onOk();
			} else if (cmd.equals("cancel")) {
				onCancel();
			} else if (cmd.equals("selectview")) {
				selectview();
			}

		}

		void selectview() {
			SelecttabviewHov hov = new SelecttabviewHov();
			DBTableModel result = hov.showDialog(this, "选择表视图", "", "", "");
			if (result == null) {
				return;
			}
			String viewname = result.getItemValue(0, "tname");
			textViewname.setText(viewname);
			if (textSql.getText().length() == 0) {
				textSql.setText("select * from " + viewname);
			}
			try {
				retrieveColumns(viewname);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return;
			}
		}

		boolean ok = false;

		public boolean isOk() {
			return ok;
		}

		void onOk() {
			if (!this.checkData())
				return;
			try {
				writeFile();
				ok = true;
				this.dispose();
				reload();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage());
				return;
			}
		}

		void onCancel() {
			ok = false;
			this.dispose();
		}

		void retrieveColumns(String viewname) throws Exception {
			Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
			colinfos.clear();
			dragpanescolumn.clear();

			// 先放上现在的列
			HashMap<String, String> curcolmap = new HashMap<String, String>();
			for (int i = 0; i < dlgtable.getColumnCount(); i++) {
				int modelindex = dlgtable.convertColumnIndexToModel(i);
				DBColumnDisplayInfo colinfo = tablemodel
						.getDisplaycolumninfos().elementAt(modelindex);
				colinfos.add(colinfo);
				curcolmap.put(colinfo.getColname().toLowerCase(), colinfo
						.getColname().toLowerCase());
			}

			DBTableModel colsdbmodel = null;

			if (viewname != null && viewname.length() > 0) {
				String sql = "select c.cname,c.coltype,n.cntitle from col c,sys_column_cn n where tname='"
						+ viewname.toUpperCase()
						+ "' and '"
						+ viewname.toUpperCase()
						+ "'=n.tablename(+)"
						+ " and c.cname=n.colname(+) order by colno";

				RemotesqlHelper sqlh = new RemotesqlHelper();
				colsdbmodel = sqlh.doSelect(sql, 0, 1000);
				if (colsdbmodel.getRowCount() == 0) {
					throw new Exception("找不到表视图" + viewname);
				}

				for (int r = 0; r < colsdbmodel.getRowCount(); r++) {
					String cname = colsdbmodel.getItemValue(r, "cname")
							.toLowerCase();
					if (curcolmap.get(cname) != null)
						continue;
					String coltype = colsdbmodel.getItemValue(r, "coltype");
					coltype = dbcoltype2coltype(coltype);
					String cntitle = colsdbmodel.getItemValue(r, "cntitle");
					cntitle = cntitle.replaceAll("\\s", "");
					if (cntitle == null || cntitle.length() == 0)
						cntitle = cname;
					DBColumnDisplayInfo colinfo = new DBColumnDisplayInfo(
							cname, coltype, cntitle);
					colinfos.add(colinfo);

					DBColumnDisplayInfo curcolinfo = getColinfo(cname);
					if (curcolinfo != null) {
						colinfo.setTablecolumnwidth(curcolinfo
								.getTablecolumnwidth());
						colinfo.setTitle(curcolinfo.getTitle());
					}
				}
			}

			Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				DragableLabel draglabel = new DragableLabel(colinfo, "column");
				dragpanescolumn.add(draglabel);
				if (curcolmap.get(colinfo.getColname().toLowerCase()) != null)
					draglabel.cb.setSelected(true);
				else
					draglabel.cb.setSelected(false);
				draglabel.setBorder(BorderFactory
						.createEtchedBorder(EtchedBorder.LOWERED));
			}

			drawColumnonPane(listcolumnpane, "column");

			colinfos.clear();
			dragpanesquerycond.clear();

			// 放上现在的查询条件
			curcolmap.clear();
			Enumeration<Querycondline> enq = querycond.elements();
			while (enq.hasMoreElements()) {
				Querycondline ql = enq.nextElement();
				String cname = ql.getColname();
				DBColumnDisplayInfo colinfo = ql.getDbcolumndisplayinfo();
				colinfos.add(colinfo);
				curcolmap.put(colinfo.getColname().toLowerCase(), colinfo
						.getColname().toLowerCase());
			}

			for (int r = 0; colsdbmodel != null
					&& r < colsdbmodel.getRowCount(); r++) {
				String cname = colsdbmodel.getItemValue(r, "cname")
						.toLowerCase();
				if (curcolmap.get(cname) != null)
					continue;
				String coltype = colsdbmodel.getItemValue(r, "coltype");
				coltype = dbcoltype2coltype(coltype);
				String cntitle = colsdbmodel.getItemValue(r, "cntitle");
				cntitle = cntitle.replaceAll("\\s", "");
				if (cntitle == null || cntitle.length() == 0)
					cntitle = cname;
				DBColumnDisplayInfo colinfo = new DBColumnDisplayInfo(cname,
						coltype, cntitle);
				colinfos.add(colinfo);

			}

			en = colinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				DragableLabel draglabel = new DragableLabel(colinfo,
						"querycond");
				dragpanesquerycond.add(draglabel);
				if (curcolmap.get(colinfo.getColname().toLowerCase()) != null)
					draglabel.cb.setSelected(true);
				else
					draglabel.cb.setSelected(false);
				draglabel.setBorder(BorderFactory
						.createEtchedBorder(EtchedBorder.LOWERED));

			}
			drawColumnonPane(listquerycondpane, "querycond");

		}

		void drawColumnonPane(JPanel jp, String type) {
			jp.removeAll();
			CFormlayout formlayout = new CFormlayout(2, 2);
			jp.setLayout(formlayout);

			Vector<DragableLabel> dragpanes = null;
			if (type.equals("querycond")) {
				dragpanes = dragpanesquerycond;
			} else {
				dragpanes = dragpanescolumn;
			}
			Enumeration<DragableLabel> en = dragpanes.elements();
			while (en.hasMoreElements()) {
				DragableLabel draglabel = en.nextElement();
				formlayout.addLayoutComponent(draglabel, new CFormlineBreak());
				draglabel.setLayout(formlayout);
				jp.add(draglabel);
			}
			jp.invalidate();
			jp.validate();
		}

		String dbcoltype2coltype(String dbcoltype) {
			dbcoltype = dbcoltype.toLowerCase();
			if (dbcoltype.startsWith("number")) {
				return DBColumnDisplayInfo.COLTYPE_NUMBER;
			} else if (dbcoltype.startsWith("date")) {
				return DBColumnDisplayInfo.COLTYPE_DATE;
			} else {
				return DBColumnDisplayInfo.COLTYPE_VARCHAR;
			}

		}

		class DragableLabel extends JPanel implements DropTargetListener,
				DragSourceListener, DragGestureListener, Transferable,
				MouseListener {
			DBColumnDisplayInfo colinfo;
			String type;
			JCheckBox cb = null;
			DropTarget dropTarget = new DropTarget(this, this);
			DragSource dragSource = DragSource.getDefaultDragSource();
			DataFlavor textPlainUnicodeFlavor = DataFlavor
					.getTextPlainUnicodeFlavor();

			public DragableLabel(DBColumnDisplayInfo colinfo, String type) {
				this.colinfo = colinfo;
				this.type = type;
				this.setLayout(new FlowLayout());
				cb = new JCheckBox();
				add(cb);
				cb.setSelected(colinfo.isQueryable());
				JLabel lb = new JLabel(colinfo.getColname() + "("
						+ colinfo.getTitle() + ")");
				add(lb);
				dragSource.createDefaultDragGestureRecognizer(this,
						DnDConstants.ACTION_MOVE, this);
				addMouseListener(this);
			}

			public String getColname() {
				return colinfo.getColname();
			}

			public boolean isQueryable() {
				return cb.isSelected();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(220, 30);
			}

			public void dragEnter(DropTargetDragEvent dtde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragOver(DropTargetDragEvent dtde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dropActionChanged(DropTargetDragEvent dtde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragExit(DropTargetEvent dte) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void drop(DropTargetDropEvent dtde) {
				Transferable transferable = dtde.getTransferable();
				try {
					String mvcolname = (String) transferable
							.getTransferData(textPlainUnicodeFlavor);
					System.out
							.println(mvcolname + "==>" + colinfo.getColname());
					insertBefore(mvcolname, colinfo.getColname(), type);

				} catch (UnsupportedFlavorException e) {
					e.printStackTrace(); // To change body of catch statement
					// use
					// File | Settings | File Templates.
				} catch (IOException e) {
					e.printStackTrace(); // To change body of catch statement
					// use
					// File | Settings | File Templates.
				}
			}

			public void dragEnter(DragSourceDragEvent dsde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragOver(DragSourceDragEvent dsde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dropActionChanged(DragSourceDragEvent dsde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragExit(DragSourceEvent dse) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragDropEnd(DragSourceDropEvent dsde) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void dragGestureRecognized(DragGestureEvent dge) {
				dge.startDrag(DragSource.DefaultCopyDrop, this, this);
			}

			public DataFlavor[] getTransferDataFlavors() {
				DataFlavor flavors[] = { textPlainUnicodeFlavor };
				return flavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				DataFlavor textPlainUnicodeFlavor = DataFlavor
						.getTextPlainUnicodeFlavor();
				if (flavor.equals(textPlainUnicodeFlavor)) {
					return true;
				}
				return false; // To change body of implemented methods use File
				// |
				// Settings | File Templates.
			}

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				return colinfo.getColname();
			}

			public void mouseClicked(MouseEvent e) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void mousePressed(MouseEvent e) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void mouseReleased(MouseEvent e) {
				// To change body of implemented methods use File | Settings |
				// File
				// Templates.
			}

			public void mouseEntered(MouseEvent e) {
				// frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				// frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		int findIndex(String colname, String type) {
			Vector<DragableLabel> dragpanes = null;
			if (type.equals("querycond")) {
				dragpanes = dragpanesquerycond;
			} else {
				dragpanes = dragpanescolumn;
			}
			Enumeration<DragableLabel> en = dragpanes.elements();
			int i = 0;
			for (i = 0; en.hasMoreElements(); i++) {
				DragableLabel lb = en.nextElement();
				if (lb.getColname().equals(colname)) {
					return i;
				}
			}
			return -1;
		}

		void insertBefore(String fromcolname, String tocolname, String type) {
			int delindex = findIndex(fromcolname, type);
			Vector<DragableLabel> dragpanes = null;
			if (type.equals("querycond")) {
				dragpanes = dragpanesquerycond;
			} else {
				dragpanes = dragpanescolumn;
			}
			DragableLabel lbfrom = dragpanes.elementAt(delindex);
			dragpanes.remove(delindex);

			int insertindex = findIndex(tocolname, type);
			dragpanes.insertElementAt(lbfrom, insertindex);

			// to-do
			if (type.equals("querycond")) {
				drawColumnonPane(listquerycondpane, "querycond");
			} else {
				drawColumnonPane(listcolumnpane, "column");
			}
		}

		/**
		 * 输出到文件. 先写到classes目录下. 再写到专项目录下. 再写src
		 * 
		 * @throws Exception
		 */
		void writeFile() throws Exception {
			String classname = CHovBase.this.getClass().getName();
			try {
				String path = configfile.getPath();
				if (path.startsWith("file:")) {
					path = path.substring("file:".length());
				}
				File outf = new File(path);
				outf.getParentFile().mkdirs();
				writeFile(outf);
			} catch (Exception e) {
				logger.error("error", e);
			}

			// 写专项目录
			configfile = new File(CurrentdirHelper.getZxdir(), "HOV/"
					+ classname + ".model");
			try {
				writeFile(configfile);
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			File outdir = new File("src");
			classname = classname.replaceAll("\\.", "/");
			try {
				File outf = new File(outdir.getPath() + "/" + classname
						+ ".model");
				if (outf.getParentFile().exists()) {
					writeFile(outf);
				}
			} catch (Exception e) {
				logger.error("error", e);
			}
		}

		void writeFile(File outfile) throws Exception {
			PrintWriter out = new PrintWriter(new FileWriter(outfile));

			out.println("<hovtype>");
			out.println("hovbase");
			out.println("</hovtype>");
			out.println("<querycond>");
			Enumeration<DragableLabel> en = dragpanesquerycond.elements();
			while (en.hasMoreElements()) {
				DragableLabel lb = en.nextElement();
				if (!lb.cb.isSelected())
					continue;
				DBColumnDisplayInfo colinfo = lb.colinfo;
				DBColumnInfoStoreHelp.writeOneColumn(colinfo, out);
			}
			out.println("</querycond>");

			// 输出列
			out.println("<columns>");
			en = dragpanescolumn.elements();
			while (en.hasMoreElements()) {
				DragableLabel lb = en.nextElement();
				if (!lb.cb.isSelected())
					continue;
				DBColumnDisplayInfo colinfo = lb.colinfo;
				DBColumnInfoStoreHelp.writeOneColumn(colinfo, out);
			}
			out.println("</columns>");

			out.println("<defaultsql>");
			out.println(textSql.getText());
			out.println("</defaultsql>");

			out.println("<hovdesc>");
			out.println(textHovname.getText());
			out.println("</hovdesc>");

			out.println("<viewname>");
			out.println(textViewname.getText());
			out.println("</viewname>");

			out.close();

		}

		boolean checkData() {
			if (textHovname.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "请输入HOV名称");
				return false;
			}

			/*
			 * if (this.textViewname.getText().length() == 0) {
			 * JOptionPane.showMessageDialog(this, "请选择表和视图"); return false; }
			 */
			if (this.textSql.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "请输入查询sql");
				return false;
			}

			boolean has = false;
			Enumeration<DragableLabel> en = dragpanesquerycond.elements();
			while (en.hasMoreElements()) {
				DragableLabel lb = en.nextElement();
				if (!lb.cb.isSelected())
					continue;
				has = true;
				break;
			}
			if (!has) {
				JOptionPane.showMessageDialog(this, "致少选择一列做为查询条件");
				return false;
			}

			has = false;
			en = dragpanescolumn.elements();
			while (en.hasMoreElements()) {
				DragableLabel lb = en.nextElement();
				if (!lb.cb.isSelected())
					continue;
				has = true;
				break;
			}
			if (!has) {
				JOptionPane.showMessageDialog(this, "致少选择一列做为数据结果列");
				return false;
			}

			return true;
		}
	}

	protected void reload() {
		createFromConfigfile();
		while (hovdialog.getContentPane().getComponentCount() > 0) {
			hovdialog.getContentPane().remove(0);
		}
		tablemodel = new DBTableModel(tablemodel.getDisplaycolumninfos());
		sumdbmodel = new Sumdbmodel((DBTableModel) tablemodel, null);
		createTable();
		hovdialog.initDialog();
		hovdialog.invalidate();
		hovdialog.repaint();
	}

	public File getConfigfile() {
		return configfile;
	}


	/**
	 * 进行扩展查询
	protected void doextQuery() {
		if (extqueryinfo == null || extqueryinfo.callopid == null)
			return;
		// 调用功能
		COpframe frm = Clientframe.getClientframe().runOp(
				extqueryinfo.callopid, true, false);
		if (frm == null) {
			logger.error("调用功能opid=" + extqueryinfo.callopid + "失败,无法使用扩展条件");
			return;
		}
		CSteModel stemodel = null;
		if (frm instanceof Steframe) {
			stemodel = ((Steframe) frm).getCreatedStemodel();
		} else if (frm instanceof MdeFrame) {
			((MdeFrame) frm).getCreatedMdemodel().getMasterModel();
		} else {
			logger.error("无法处理类 " + frm.getClass().getName());
			return;
		}
		CQueryDialog querydlg = stemodel.getQueryDialog();
		querydlg.setVisible(true);
		if (!querydlg.isConfirm())
			return;

		Querycond querycond = querydlg.getQuerycond();
		String wheres = querycond.getWheres();
		String pkcolname = stemodel.getDBtableModel().getPkcolname();
		StringBuffer sb = new StringBuffer();
		if (wheres.length() == 0) {
			CMessageDialog.warnMessage(hovdialog, "提示", "扩展条件不能为空,至少输入一个条件");
			extquerywheres = "";
			return;
		}

		sb.append(extqueryinfo.relatecolname + " in (select ");
		sb.append(pkcolname + " from ");
		sb.append(stemodel.getTablename() + " where " + wheres);
		sb.append(")");
		extquerywheres = sb.toString();
		logger.debug("extquerywheres=" + extquerywheres);
		onOk();
	}
	 */


}
