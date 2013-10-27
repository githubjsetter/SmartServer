package com.inca.np.gui.ste;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTreeUI.SelectionModelPropertyChangeHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.inca.np.anyprint.Printplan;
import com.inca.np.auth.ClientUserManager;
import com.inca.np.client.RemoteConnector;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ResultCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.env.Configer;
import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.gui.control.CButton;
import com.inca.np.gui.control.CCheckBox;
import com.inca.np.gui.control.CComboBox;
import com.inca.np.gui.control.CComboBoxModel;
import com.inca.np.gui.control.CDefaultProgress;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CFormFocusTraversalPolicy;
import com.inca.np.gui.control.CFormatTextField;
import com.inca.np.gui.control.CFormlayout;
import com.inca.np.gui.control.CFormlineBreak;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CLabel;
import com.inca.np.gui.control.CLinenoDisplayinfo;
import com.inca.np.gui.control.CMessageDialog;
import com.inca.np.gui.control.CMultiheadTable;
import com.inca.np.gui.control.CProgressIF;
import com.inca.np.gui.control.CQueryDialog;
import com.inca.np.gui.control.CScrollPane;
import com.inca.np.gui.control.CStatusbar;
import com.inca.np.gui.control.CSteFormWindow; //import com.inca.np.gui.control.CSteFormWindow;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CTableLinenoRender;
import com.inca.np.gui.control.CTableMultiHeaderRender;
import com.inca.np.gui.control.CTextArea;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.DBTableModelEvent;
import com.inca.np.gui.control.GroupDBTableModel;
import com.inca.np.gui.control.SplitGroupInfo;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.gui.design.DesignFrame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.runop.Oplauncher;
import com.inca.np.gui.runop.Opnode;
import com.inca.np.gui.ste.QuerylinkInfo.Querycondinfo;
import com.inca.np.gui.ui.CTableMultiheadUI;
import com.inca.np.gui.ui.CTableheadUI;
import com.inca.np.print.drawable.PReport;
import com.inca.np.print.report.BasicReport;
import com.inca.np.rule.define.Rulebase;
import com.inca.np.rule.define.SortRule;
import com.inca.np.rule.enginee.Ruleenginee;
import com.inca.np.rule.setup.RuleRepository;
import com.inca.np.rule.setup.RulesetupMaindialog;
import com.inca.np.selfcheck.DBColumnChecker;
import com.inca.np.selfcheck.DBColumnComboboxChecker;
import com.inca.np.selfcheck.DBColumnEditableChecker;
import com.inca.np.selfcheck.DBColumnUppercaseChecker;
import com.inca.np.selfcheck.DBColumnhovChecker;
import com.inca.np.selfcheck.SelfcheckError;
import com.inca.np.util.DBHelper;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.ExcelHelper;
import com.inca.np.util.SendHelper;
import com.inca.np.util.SpecialProjectManager;
import com.inca.np.util.StringUtil;
import com.inca.np.util.ZipHelper;
import com.inca.npbi.client.design.ReportcanvasFrame;
import com.inca.npclient.download.DownloadManager;
import com.inca.npclient.system.Clientframe;
import com.inca.npserver.server.Server;
import com.inca.npx.ste.ZxmodifyUploadHelper;
import com.inca.npclient.skin.ChangeSkinDialog;
import com.inca.npclient.skin.ColInfo;
import com.inca.npclient.skin.SaveDialog;
import com.inca.npclient.skin.SkinHelper;
import com.inca.npclient.skin.SkinInfo;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-11 Time: 13:48:15
 * 单表编辑模块． 包括逻辑处理和图形控件
 */
public abstract class CSteModel extends CModelBase {
	/**
	 * 根panel
	 */
	protected Rootpanel rootpanel = new Rootpanel(new BorderLayout());

	public static final String ACTION_NEW = "新增";
	public static final String ACTION_MODIFY = "修改";
	public static final String ACTION_HIDEFORM = "隐藏";
	public static final String ACTION_UNDO = "撤消修改";
	public static final String ACTION_DEL = "删除";
	public static final String ACTION_QUERY = "查询";
	public static final String ACTION_REFRESH = "刷新";
	public static final String ACTION_SAVE = "保存";
	public static final String ACTION_EXPORT = "导出";
	public static final String ACTION_EXPORTAS = "导出为";

	public static final String ACTION_SEARCH = "文本搜索";
	public static final String ACTION_SEARCHNEXT = "搜索下一个";

	public static final String ACTION_NEXT = "下一行";
	public static final String ACTION_PRIOR = "上一行";
	public static final String ACTION_FIRST = "第一行";
	public static final String ACTION_LAST = "最后行";

	public static final String ACTION_SETUPUI = "界面设置";
	public static final String ACTION_SAVEUI = "保存界面";

	public static final String ACTION_SETUPRULE = "规则设置";

	public static final String ACTION_PRINTSETUP = "打印设置";
	public static final String ACTION_PRINT = "打印";
	public static final String ACTION_SELECTOP = "选择功能";

	public static final String ACTION_EXIT = "退出功能";

	public static final String ACTION_SELFCHECK = "自检";
	public static final String ACTION_DOCPRINT_PREFIX = "DOCPRINT_";

	public static final String ACTION_FILES = "附件";

	public static final String ACTION_SAVEFACE = "保存自定义界面";
	public static final String ACTION_SAVEASFACE = "另存自定义界面";
	public static final String ACTION_USEFACE = "使用自定义界面";
	public static final String ACTION_FACESORT = "界面排序";

	/**
	 * 是否初始化,调用了initcontrol()
	 */
	protected boolean controlinited = false;
	// protected JLabel lbstatus;

	/**
	 * 下部的状态条
	 */
	protected CStatusbar statusbar;

	/**
	 * @deprecated
	 */
	protected JMenuBar menubar;

	/**
	 * 工具条
	 */
	protected CStetoolbar toolbar = null;

	/**
	 * 表格
	 */
	protected CTable table;

	/**
	 * 最近一次查询的sql.用于dorequery()
	 */
	protected String lastselectsql = "";

	// protected Vector<DBColumnDisplayInfo> tablexcolumndisplayinfos = null;

	/**
	 * 卡片列的定义.
	 */
	protected Vector<DBColumnDisplayInfo> formcolumndisplayinfos = null;
	protected Vector<DBColumnDisplayInfo> orgdbmodelcols = null;

	/**
	 * 事件监听器列表
	 */
	protected Vector<CSteModelListener> actionListeners = new Vector<CSteModelListener>();
	/**
	 * 当前行．
	 */
	protected int currow = -1;

	/**
	 * 当前列
	 */
	protected int curcol = -1;

	/**
	 * 窗口是否已关
	 */
	protected boolean closed = false;

	Category logger = Category.getInstance(CSteModel.class);

	/**
	 * 查询开始时间
	 */
	protected long querystarttime;
	/**
	 * 查询条件
	 */
	// protected String wheres = "";
	/**
	 * 卡片Panel
	 */
	protected Steform form;

	/**
	 * 表格的滚动Panel
	 */
	protected CScrollPane tablescrollpane;

	/**
	 * 卡片窗口
	 */
	// protected CSteFormWindow steformwindow;
	/**
	 * 弹出菜单位置x
	 */
	protected int mousex = 0;
	/**
	 * 弹出菜单位置y
	 */
	protected int mousey = 0;

	/**
	 * 打印配置Frame
	 */
	protected PrintSetupFrame printsetupfrm;

	/**
	 * 表格窗口带合计行的model
	 */
	protected Sumdbmodel sumdbmodel;

	/**
	 * 真正的数据model,不带合计行
	 */
	protected DBTableModel dbmodel;

	/**
	 * 表格是否可编辑
	 */
	protected boolean tableeditable = false;

	protected Object vkupaction;
	protected Object vkdownaction;

	/**
	 * 表格背景颜色,区别不同的行
	 */
	protected Color secondbackcolor = new Color(240, 240, 240);

	/**
	 * 表格只读字段的颜色
	 */
	protected Color readonlybackcolor = new Color(220, 220, 220);

	/**
	 * 排序列
	 */
	protected String[] sortcolumns = null;

	/**
	 * 是否增序排序
	 */
	protected boolean sortasc = true;

	/**
	 * 规则引擎 add by wwh 20071126
	 */
	protected Ruleenginee ruleeng = null;

	/**
	 * 是否使用附件.初始时如果发现有列名为filegroupid,就置为true
	 */
	protected boolean useattachfile = false;

	/**
	 * 本次上传文件数量
	 */
	protected int uploadedfilecount = 0;

	/**
	 * 是否是专项开发的界面和规则的调整.缺省是FALSE
	 */
	protected boolean zxmodify = false;

	protected boolean usecrosstable = false;

	protected DBTableModel crossdbmodel = null;

	protected Sumdbmodel sumcrossdbmodel = null;

	protected CTable crosstable = null;

	/**
	 * 在专项zip文件中的名称 单表编辑和总单是ste.model,细单是ste1.model
	 */
	protected String modelnameinzxzip = "ste.model";

	/**
	 * 查询列的次序
	 */
	protected Vector<String> querycolumns = new Vector<String>();

	/**
	 * mosue click position
	 */
	protected Point mouseclickpoint;

	/**
	 * 专项调整zip
	 */
	protected File zxzipfile = null;

	/**
	 * 专项控制点开发的类
	 */
	protected ZxstejavaDelegate zxdelegate = null;

	/**
	 * 只显示卡片，不显示表格
	 */
	protected boolean showformonly = false;

	/**
	 * 用于multiste时，传入的hotkeylistener
	 */
	protected ActionListener hotkeylistener = null;

	protected boolean formvisible = false;

	/**
	 * 保存错误记录数
	 */
	int errorcount = 0;

	protected String ruleprefix = "";

	/**
	 * 所有支持的打印方案
	 */
	protected Vector<String> printplans = new Vector<String>();

	/**
	 * 查询是否用线程
	 */
	protected boolean usequerythread = true;

	/**
	 * 表是否已设置过列宽.
	 */
	protected boolean tableautosized = false;
	/**
	 * 必须查询的列.
	 */
	protected HashMap<String, String> querymustcolmap = new HashMap<String, String>();

	/**
	 * 界面方案
	 */
	private String schemeName = "默认界面方案";

	/**
	 * 用户视图ID
	 */
	private String userviewid = "";

	/**
	 * 是否缺省
	 */
	private String isdefaultscheme = "1";

	/**
	 * 最后修改时间
	 */
	private String lastmodify = "";

	/**
	 * 排序方法
	 */
	private String sortexpr = "";

	/**
	 * 自定义界面dbmodel
	 */
	private DBTableModel faceDbmodel = null;

	public CSteModel() {

	}

	/**
	 * 构造
	 * 
	 * @param frame
	 * @param title
	 * @throws HeadlessException
	 */
	public CSteModel(CFrame frame, String title) throws HeadlessException {
		this.frame = frame;
		this.title = title;
		if (frame != null) {
			setOpid(((COpframe) frame).getOpid());
		}
		// 加载专项
		initInitdelegate();
		this.loadDBColumnInfos();
		this.loadRuleenginee();
		this.loadPrintplan();
		if (ruleeng != null) {
			ruleeng.process(this, "设置下拉选择");
			ruleeng.process(this, "设置系统下拉选择");
			ruleeng.process(this, "设置SQL下拉选择");
			ruleeng.process(this, "表格可以编辑");
			try {
				ruleeng.processCalcColumn(this, "计算列", -1);
			} catch (Exception e) {
				logger.error("error", e);
			}
		}

		if (initdelegate != null) {
			initdelegate.on_init(this);
		}
		if (zxdelegate != null) {
			zxdelegate.on_init(this);
		}

		DBColumnDisplayInfo colinfo = getDBColumnDisplayInfo("filegroupid");
		useattachfile = colinfo != null;
	}

	/**
	 * 从专项文件中提取打印方案定义
	 */
	protected void loadPrintplan() {
		if (zxzipfile == null)
			return;
		// 从zxfile中找出ste.model
		File tempfile = null;
		try {
			tempfile = File.createTempFile("temp", ".properties");
			if (ZipHelper.extractFile(zxzipfile, "printplan.properties",
					tempfile)) {
				BufferedReader fin = null;
				try {
					fin = new BufferedReader(new FileReader(tempfile));
					// 第1行是方案清单
					String s = fin.readLine();
					if (s == null || s.length() == 0)
						return;
					String ss[] = s.split(":");
					printplans.clear();
					for (int i = 0; i < ss.length; i++)
						printplans.add(ss[i]);

					fin.close();
					fin = null;

				} finally {
					if (fin != null) {
						fin.close();
					}
				}
			}
		} catch (Exception e) {
			logger.error("e", e);
		} finally {
			if (tempfile != null) {
				tempfile.delete();
			}
		}

	}

	/**
	 * 从文件中读取列的定义
	 */
	protected void loadDBColumnInfos() {

		formcolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		formcolumndisplayinfos.add(new CLinenoDisplayinfo());

		if (zxzipfile != null) {
			// 从zxfile中找出ste.model
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".model");
				if (ZipHelper
						.extractFile(zxzipfile, modelnameinzxzip, tempfile)) {
					DBColumnInfoStoreHelp.readFile(this, tempfile);
					orgdbmodelcols = new Vector<DBColumnDisplayInfo>();
					orgdbmodelcols.addAll(formcolumndisplayinfos);
					return;
				}
			} catch (Exception e) {
				logger.error("e", e);
			} finally {
				if (tempfile != null) {
					tempfile.delete();
				}
			}
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}

		// 先找有没有专项.如果没有再使用产品的.
		URL url = getClass().getResource(classname + ".model");
		if (url == null) {
			logger.error(this.getClass().getName() + "加载" + classname
					+ ".model失败");
			return;
		}
		String pathname = url.toString();
		logger.debug("加载model文件:" + pathname);
		File f = new File(pathname);
		try {
			DBColumnInfoStoreHelp.readFile(this, f);
		} catch (Exception e) {
			logger.error("load " + url.toString(), e);
		}

		orgdbmodelcols = new Vector<DBColumnDisplayInfo>();
		orgdbmodelcols.addAll(formcolumndisplayinfos);
	}

	/**
	 * 加载最原始的,没有隐藏的列定义
	 * 
	 * @return
	 */
	public Vector<DBColumnDisplayInfo> loadOrgDBmodeldefine() {
		if (orgdbmodelcols == null) {
			orgdbmodelcols = new Vector<DBColumnDisplayInfo>();
			orgdbmodelcols.addAll(formcolumndisplayinfos);
		}

		return orgdbmodelcols;
	}

	protected void loadRuleenginee() {
		// 检查是不是有专项？
		BufferedReader rd = null;

		if (zxzipfile != null) {
			// 从zxfile中找出ste.model
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".model");
				if (ZipHelper.extractFile(zxzipfile, "ste.rule", tempfile)) {
					rd = null;
					rd = DBColumnInfoStoreHelp.getReaderFromFile(tempfile);
					Vector<Rulebase> rules = RuleRepository.loadRules(rd);
					rd.close();
					ruleeng = new Ruleenginee();
					ruleeng.setRuletable(rules);
					return;
				}
			} catch (Exception e) {
				logger.error("e", e);
			} finally {
				if (tempfile != null) {
					tempfile.delete();
				}
			}
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}
		URL url = this.getClass().getResource(classname + ".rule");
		if (url == null) {
			return;
		}
		String pathname = url.toString();
		logger.debug("加载rule文件:" + pathname);
		File f = new File(pathname);
		try {
			rd = DBColumnInfoStoreHelp.getReaderFromFile(f);
			Vector<Rulebase> rules = RuleRepository.loadRules(rd);
			ruleeng = new Ruleenginee();
			ruleeng.setRuletable(rules);
		} catch (Exception e) {
			logger.error("load " + url.toString(), e);
		} finally {
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
				}
		}

	}

	/**
	 * 取得列的定义
	 * 
	 * @param colname
	 * @return
	 */
	public DBColumnDisplayInfo getDBColumnDisplayInfo(String colname) {
		// 20090601 by wwh ,应返回原始的定义
		// if (crossdbmodel != null) {
		// return crossdbmodel.getColumninfo(colname);
		// }

		if (formcolumndisplayinfos == null)
			return null;
		Enumeration<DBColumnDisplayInfo> en = this.formcolumndisplayinfos
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			if (info.getColname().equalsIgnoreCase(colname)) {
				return info;
			}
		}
		if (!colname.equalsIgnoreCase("filegroupid")) {
			logger.error("CStemodel:getDBColumnDisplayInfo,找不到列名=" + colname);
		}
		return null;
	}

	/**
	 * 创建GUI控制,并放在Panel返回
	 * 
	 * @return
	 */
	public JPanel getRootpanel() {
		if (!controlinited) {
			initControl();
			controlinited = true;
		}
		return rootpanel;
	}

	/**
	 * 返回form:Steform
	 * 
	 * @return
	 */
	public Steform getForm() {
		return form;
	}

	/**
	 * 返回卡片窗口
	 * 
	 * @deprecated
	 * @return public CSteFormWindow getSteformwindow() { return null; // return
	 *         steformwindow; }
	 */

	/**
	 * 返回弹出菜单
	 * 
	 * @return
	 */
	public JPopupMenu getPopmenu() {
		return createPopmenu();
	}

	/**
	 * 创建弹出菜单
	 * 
	 * @return
	 */
	protected JPopupMenu createPopmenu() {
		// return SteControlFactory.createPopupmenu(this);
		JPopupMenu popmenu = new JPopupMenu("级联菜单");
		JMenuItem item;
		ActionListener actionListener = this;

		int row = getRow();
		if (ruleeng != null) {
			Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this,
					"级联查询");
			if (qlinfos != null && row >= 0) {
				Enumeration<QuerylinkInfo> en = qlinfos.elements();
				while (en.hasMoreElements()) {
					QuerylinkInfo qlinfo = en.nextElement();
					// 菜单
					item = new JMenuItem(qlinfo.querylinkname);
					item.setActionCommand("run");
					item.addActionListener(new QuerylinkMenuListener(row,
							qlinfo));
					popmenu.add(item);
				}

				popmenu.addSeparator();
			}
		}

		item = new JMenuItem("文本搜索  Ctrl+F");
		item.setActionCommand(CSteModel.ACTION_SEARCH);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);

		item = new JMenuItem("搜索下一条 Ctrl+K");
		item.setActionCommand(CSteModel.ACTION_SEARCHNEXT);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("第一条 Ctrl+HOME");
		item.setActionCommand(CSteModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("前一条");
		item.setActionCommand(CSteModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("后一条");
		item.setActionCommand(CSteModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("末一条 Ctrl+END");
		item.setActionCommand(CSteModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();
		/*
		 * item = new JMenuItem("保存");
		 * item.setActionCommand(CSteModel.ACTION_SAVE);
		 * item.addActionListener(actionListener); ; popmenu.add(item);
		 */
		item = new JMenuItem("导出");
		item.setActionCommand(CSteModel.ACTION_EXPORT);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("导出为");
		item.setActionCommand(CSteModel.ACTION_EXPORTAS);
		item.addActionListener(actionListener);
		popmenu.add(item);
		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("界面设计");
			item.setActionCommand(CSteModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}
		return popmenu;

	}

	/**
	 * 增加事件监听器
	 * 
	 * @param listenerListener
	 */
	public void addActionListener(CSteModelListener listenerListener) {
		actionListeners.add(listenerListener);
	}

	/**
	 * 删除事件监听器
	 * 
	 * @param listenerListener
	 */
	public void removeActionListener(CSteModelListener listenerListener) {
		actionListeners.removeElement(listenerListener);
	}

	/**
	 * 多线程查询,返回是否查询中
	 * 
	 * @return
	 */
	public boolean isquerying() {
		return this.getDBtableModel().isquerying();
	}

	/**
	 * 创建工具条
	 * 
	 * @return
	 */
	protected CStetoolbar createToolbar() {
		CStetoolbar stetoolbar = SteControlFactory.createStetoolbar(this);
		Enumeration<String> en = printplans.elements();
		while (en.hasMoreElements()) {
			String planname = en.nextElement();
			stetoolbar.addPrintmenu(planname, "DOCPRINT_" + planname);
		}
		return stetoolbar;
	}

	/**
	 * 返回HOV定义
	 * 
	 * @return
	 */
	public Vector<Hovdefine> getHovdefines() {
		Vector<Hovdefine> hovdefines = new Vector<Hovdefine>();
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			Hovdefine hovdefine = colinfo.getHovdefine();
			if (hovdefine != null) {
				hovdefines.add(hovdefine);
			}
		}

		return hovdefines;
	}

	/**
	 * 设置HOV定义
	 * 
	 * @param hovdefines
	 */
	public void setHovdefines(Vector<Hovdefine> hovdefines) {
		Enumeration<Hovdefine> en = hovdefines.elements();
		while (en.hasMoreElements()) {
			Hovdefine hovdefine = en.nextElement();
			String invokecolname = hovdefine.getInvokecolname();
			DBColumnDisplayInfo colinfo = getDBColumnDisplayInfo(invokecolname);
			if (colinfo == null) {
				logger.error("hov定义错误，没有找到触发列=" + invokecolname);
				continue;
			}
			colinfo.setHovdefine(hovdefine);
		}
	}

	/*
	 * public void setToolbar(CStetoolbar toolbar) { if (toolbar != null) {
	 * rootpanel.remove(toolbar); } rootpanel.add(toolbar, BorderLayout.NORTH);
	 * this.toolbar = toolbar; }
	 */

	/**
	 * 初始化，创建所有GUI控件.如工具条,表格,卡片窗口
	 */
	protected void initControl() {
		/*
		 * menubar = SteControlFactory.createJMenu(this);
		 * this.setJMenuBar(menubar);
		 */

		// setHotkey();
		rootpanel.setLayout(new BorderLayout());

		JPanel tooljp = new JPanel();
		CFormlayout tooljplayout = new ToolbarLayout();
		tooljp.setLayout(tooljplayout);
		rootpanel.add(tooljp, BorderLayout.NORTH);
		toolbar = createToolbar();
		if (toolbar != null) {
			tooljp.add(toolbar);
			tooljplayout.addLayoutComponent(toolbar, new CFormlineBreak());
		}
		JPanel secondtoolbar = createSecondtoolbar();
		if (secondtoolbar != null) {
			tooljp.add(secondtoolbar);
			tooljplayout
					.addLayoutComponent(secondtoolbar, new CFormlineBreak());
		}

		// 生成表格
		// table = createTable();

		tablescrollpane = new CScrollPane();
		tablescrollpane.setBorder(BorderFactory.createEmptyBorder());

		removeUpdownhotkey(tablescrollpane);
		tablescrollpane.addMouseListener(new ScrollpaneListener());
		rootpanel.add(tablescrollpane, BorderLayout.CENTER);

		this.recreateDBModel();

		form = createForm();
		// SwingUtilities.get

		// steformwindow = createFormwindow();
		// steformwindow.pack();
		// FocusTraversalPolicy focuspolicy =
		// steformwindow.getFocusTraversalPolicy();
		// steformwindow.setVisible(false);

		// 生成状态条
		statusbar = new CStatusbar();
		statusbar.setFloatable(false);
		rootpanel.add(statusbar, BorderLayout.SOUTH);
		statusbar.setStatus("就绪");

		controlinited = true;

		// 必须在 controlinited 后
		if (ruleeng != null) {
			ruleeng.process(this, "屏蔽新增");
			ruleeng.process(this, "屏蔽修改");
			ruleeng.process(this, "屏蔽删除");
			ruleeng.process(this, "屏蔽查询");
			ruleeng.process(this, "屏蔽保存");
			ruleeng.process(this, "屏蔽撤消");
			ruleeng.process(this, "屏蔽打印");
			ruleeng.process(this, "屏蔽选功能");
			ruleeng.process(this, "屏蔽退出");
			ruleeng.process(this, "工具条加按钮");
			ruleeng.process(this, "设置编辑控件大小");
		}

		if (initdelegate != null) {
			initdelegate.on_initControl();
		}

		loadDefaultSkin();

	}

	class ToolbarLayout extends CFormlayout {
		public ToolbarLayout() {
			super(0, 0);
		}

		@Override
		public void layoutContainer(Container parent) {
			super.layoutContainer(parent);
			for (int i = 0; i < parent.getComponentCount(); i++) {
				JComponent c = (JComponent) parent.getComponent(i);
				Dimension size = c.getSize();
				size.width = rootpanel.getWidth();
				c.setSize(size);
			}
		}

	}

	/**
	 * 设排序方法. 查询结束后被自动调用.
	 * 
	 * @param cols
	 *            排序列数组
	 * @param sortasc
	 *            增序
	 */
	public void setSort(String[] cols, boolean sortasc) {
		this.sortcolumns = cols;
		this.sortasc = sortasc;
	}

	/**
	 * 创建卡片窗口
	 * 
	 * @deprecated
	 * @return
	 */
	protected CSteFormWindow createFormwindow() {
		return null;
		/*
		 * ActionListener al=this; if(hotkeylistener!=null){ al=hotkeylistener;
		 * } CSteFormWindow steformwindow = new CSteFormWindow(getParentFrame(),
		 * form, al, title, !showformonly); if (toolbar != null) {
		 * steformwindow.setStetoolbar(this.toolbar); }
		 * steformwindow.setFocusTraversalPolicy(new CFormFocusTraversalPolicy(
		 * this)); return steformwindow;
		 */}

	/**
	 * 设置列定义
	 * 
	 * @param formcolumndisplayinfos
	 */
	public void setFormcolumndisplayinfos(
			Vector<DBColumnDisplayInfo> formcolumndisplayinfos) {
		this.formcolumndisplayinfos = formcolumndisplayinfos;
	}

	/**
	 * 取列的定义
	 * 
	 * @return
	 */
	public Vector<DBColumnDisplayInfo> getFormcolumndisplayinfos() {
		if (this.usecrosstable) {
			return crossdbmodel.getDisplaycolumninfos();
		}
		return formcolumndisplayinfos;
	}

	/**
	 * 为了兼容现有的代码,保留getTablecolumndisplayinfos
	 * 
	 * @return
	 * @deprecated
	 */
	public Vector<DBColumnDisplayInfo> getTablecolumndisplayinfos() {
		return formcolumndisplayinfos;
	}

	/**
	 * 创建卡片编辑Steform
	 * 
	 * @return
	 */
	protected Steform createForm() {
		if (formdelegate != null) {
			formdelegate.on_createForm(formcolumndisplayinfos);
		}
		Steform steform = new Steform(this);
		steform.setFocusTraversalPolicy(new CFormFocusTraversalPolicy(this));
		steform.setFocusCycleRoot(true);

		return steform;

	}

	/**
	 * 设置当前行
	 * 
	 * @param row
	 */
	public void setRow(int row) {
		currow = row;
		if (row >= 0) {
			table.addRowSelectionInterval(row, row);
			on_click(row, 0);
		}
	}

	/**
	 * 中止查询
	 */
	public void stopQuery() {
		/*
		 * if (querythread != null) { querythread.cancelRetrieve = true;
		 * querythread = null; }
		 */
		this.getDBtableModel().stopQuery();
		this.setStatusmessage("用户中止查询。");
		if (toolbar != null) {
			toolbar.setQuerybuttonText("查询");
		}
		on_retrieved();
	}

	/**
	 * 查询查询select语句.返回select * from 形式的sql
	 * 
	 * @param wheres
	 *            查询的where子句,不要以where 或 and开头
	 * @return
	 */
	public String buildSelectSql(String wheres) {
		StringBuffer sbcol = new StringBuffer();
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			if (!info.isDbcolumn()) {
				continue;
			}
			if (sbcol.length() > 0) {
				sbcol.append(",");
			}
			sbcol.append(info.getColname());
		}
		String sql = "select " + sbcol.toString() + " from " + getTablename();
		if (wheres.length() > 0) {
			sql = DBHelper.addWheres(sql, wheres);
		}
		return sql;

	}

	/**
	 * 必须重载的函数,返回表名
	 * 
	 * @return
	 */
	public abstract String getTablename();

	/**
	 * 重新生成dbmodel和table 如果对table进行设置,重载本函数
	 */
	public void recreateDBModel() {
		dbmodel = new DBTableModel(formcolumndisplayinfos);

		sumdbmodel = createSumdbmodel(dbmodel);
		dbmodel.setRetrievelistener(new RetrieveListener());
		if (table == null) {
			table = recreateTable(sumdbmodel);
		} else {
			table.setModel(sumdbmodel);
		}
		TableColumn column = table.getColumn("行号");
		column.setCellRenderer(new CTableLinenoRender(sumdbmodel));

		// 记录上下键导航的键值,取消回车导航 开关一次为了进行初始化.
		// enableTableUpdownkey(false);
		// 开启上下键导航
		// enableTableUpdownkey(true);

		// table.getAccessibleContext().addPropertyChangeListener(new
		// TablechangeListener());

		// hideColumn(table);

		currow = -1;
		if (form != null) {
			form.clearAll();
		}

		if (ruleeng != null) {
			ruleeng.process(this, "表格多选");
		}

	}

	/**
	 * 返回表格table
	 * 
	 * @return
	 */
	public CTable getTable() {
		if (this.usecrosstable) {
			return crosstable;
		}
		return table;
	}

	public void setTable(CTable table) {
		this.table = table;
		this.tablescrollpane.setViewportView(table);
	}

	public void recreateTable() {
		table = recreateTable(sumdbmodel);
	}

	/**
	 * 重新生成table
	 * 
	 * @param dbmodel
	 * @return
	 */
	protected CTable recreateTable(DBTableModel dbmodel) {
		// 建列
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		String[] tmpcols = tablecolumns;
		if (tmpcols == null) {
			ArrayList<String> ar = new ArrayList<String>();
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				ar.add(colinfo.getColname());
			}
			tmpcols = new String[ar.size()];
			ar.toArray(tmpcols);
		}

		for (int i = 0; i < tmpcols.length; i++) {
			String colname = tmpcols[i];
			// 求列序
			int j;
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			for (j = 0; en.hasMoreElements(); j++) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				/*
				 * 表格列显示不受dbcolumndisplayinfo的hide控制 20080522 if
				 * (colinfo.isHide()) { continue; }
				 */if (colinfo.getColname().equals(colname)) {
					TableColumn col = new TableColumn(j);
					col.setHeaderValue(colinfo.getTitle());
					if (colinfo.getTablecolumnwidth() >= 0) {
						col.setPreferredWidth(colinfo.getTablecolumnwidth());
					} else {
						col.setPreferredWidth(65);
					}
					cm.addColumn(col);
					break;
				}
			}
		}

		// 放内存
		if (table != null) {
			// 因为table在freememory时要翻放dbmodel,所以先做个空的
			Vector<DBColumnDisplayInfo> nousecols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("nouse",
					"varchar");
			nousecols.add(col);

			table.setModel(new DBTableModel(nousecols));
			table.freeMemory();
			table = null;
		}
		table = new CTableex(dbmodel, cm);

		// 去掉F2键编辑 enter下一行
		/*
		 * InputMap map = table.getInputMap(
		 * JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		 * map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		 * map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false));
		 * KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
		 * false); map.remove(vkenter);
		 */
		InputMap map = table
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false), "nothing");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false), "nothing");
		map
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
						"nothing");

		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

		// 回车往前
		map.put(vkenter, "selectNextColumnCell");

		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		map.put(vktab, "selectNextColumnCell");
		KeyStroke vkshifttab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
				Event.SHIFT_MASK, false);
		map.put(vkshifttab, "selectPreviousRowCell");
		ActionMap actionmap = table.getActionMap();
		actionmap.put("selectNextColumnCell", new SelectNextRowCellAction());
		actionmap.put("selectPreviousRowCell",
				new SelectPreviousRowCellAction());

		KeyStroke ctrlhome = KeyStroke.getKeyStroke(KeyEvent.VK_HOME,
				Event.CTRL_MASK, false);
		map.put(ctrlhome, CSteModel.ACTION_FIRST);
		actionmap.put(CSteModel.ACTION_FIRST, new SteActionListener(
				CSteModel.ACTION_FIRST, this));

		// ctrl+end 最后一条
		KeyStroke ctrlend = KeyStroke.getKeyStroke(KeyEvent.VK_END,
				Event.CTRL_MASK, false);
		map.put(ctrlend, CSteModel.ACTION_LAST);
		actionmap.put(CSteModel.ACTION_LAST, new SteActionListener(
				CSteModel.ACTION_LAST, this));

		KeyStroke ctrlf = KeyStroke.getKeyStroke(KeyEvent.VK_F,
				Event.CTRL_MASK, false);
		table.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlf,
				CSteModel.ACTION_SEARCH);
		actionmap
				.put(CSteModel.ACTION_SEARCH, new SearchHandler(ACTION_SEARCH));

		KeyStroke ctrlk = KeyStroke.getKeyStroke(KeyEvent.VK_K,
				Event.CTRL_MASK, false);
		table.getInputMap(JComponent.WHEN_FOCUSED).put(ctrlk,
				CSteModel.ACTION_SEARCHNEXT);
		actionmap.put(CSteModel.ACTION_SEARCHNEXT, new SearchHandler(
				ACTION_SEARCHNEXT));
		tablescrollpane.setViewportView(table);

		/*
		 * 20070912 不能在这里设置tablehead的宽度,否则左右滚动会乱的.设置高度在CTable中完成 JTableHeader th
		 * = table.getTableHeader(); Dimension size = th.getPreferredSize();
		 * size.setSize(size.getWidth(), 30); th.setPreferredSize(size);
		 */
		table.getTableHeader().setUI(new CTableheadUI());

		// 增加列的监听器
		table.getColumnModel().addColumnModelListener(
				new TableColumnModelHandel());

		table.setRowHeight(27);

		registryTableaction(table);
		table.addMouseListener(new TableMouseListener());
		table.addMouseMotionListener(new TableMouseMotionListener());
		setTableeditable(tableeditable);
		table.getSelectionModel().addListSelectionListener(
				new Tableselectionlistener());
		TableColumnModel tcm = table.getColumnModel();
		for (int c = 0; c < tcm.getColumnCount(); c++) {
			TableColumn column = tcm.getColumn(c);
			DBColumnDisplayInfo colinfo = this.formcolumndisplayinfos
					.elementAt(column.getModelIndex());
			PlainTablecellRender cellRenderer = new PlainTablecellRender(
					colinfo);
			column.setCellRenderer(cellRenderer);
		}

		resizeTable();
		if (dbmodel.isFixtablecolumnwidth() == false) {
			// 如果是非固定列宽,还要在查询后再resize
			setTableautosized(false);
		}
		return table;
	}

	class SelectPreviousRowCellAction extends AbstractAction {

		public SelectPreviousRowCellAction() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			editPrevious();
		}

	}

	class SelectNextRowCellAction extends AbstractAction {

		public SelectNextRowCellAction() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			editNext();
		}

	}

	/**
	 * 表格table的列监听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class TableColumnModelHandel implements TableColumnModelListener {

		public void columnAdded(TableColumnModelEvent e) {
		}

		public void columnRemoved(TableColumnModelEvent e) {
		}

		public void columnMoved(TableColumnModelEvent e) {
			if (printsetupfrm != null && printsetupfrm.isVisible()) {
				printsetupfrm.setDirty(true);
			}
		}

		public void columnMarginChanged(ChangeEvent e) {
			if (printsetupfrm != null && printsetupfrm.isVisible()) {
				printsetupfrm.setDirty(true);
			}
		}

		public void columnSelectionChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			if (table.isTablechanging())
				return;

			if (formcolumndisplayinfos == null) {
				// 如果已free memory,返回
				return;
			}

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			curcol = dm.getAnchorSelectionIndex();
			on_click(currow, curcol);

			/*
			 * Runnable r = new Runnable() { public void run() { //
			 * on_Focuscell(); } }; SwingUtilities.invokeLater(r);
			 */
			if (tableeditable && curcol >= 0) {
				int mindex = table.convertColumnIndexToModel(curcol);
				DBColumnDisplayInfo cinfo = formcolumndisplayinfos
						.elementAt(mindex);
				if (cinfo.getColtype().equals("行号") || cinfo.isHide()
						|| cinfo.isReadonly() || !cinfo.isCanedit()) {
				} else {
					table.editCellAt(currow, curcol);
				}
			}
		}
	}

	/*
	 * protected CTable createTable() { DBTableModel dbmodel = new
	 * DBTableModel(tablecolumndisplayinfos); dbmodel.setRetrievelistener(new
	 * RetrieveListener());
	 * 
	 * //建列 DefaultTableColumnModel cm = new DefaultTableColumnModel();
	 * Enumeration<DBColumnDisplayInfo> en = tablecolumndisplayinfos.elements();
	 * int i=0; for(i=0;en.hasMoreElements();i++) { DBColumnDisplayInfo colinfo
	 * = en.nextElement(); TableColumn col = new TableColumn(i);
	 * col.setHeaderValue(colinfo.getTitle()); cm.addColumn(col); }
	 * 
	 * 
	 * CTable table = new CTable(dbmodel,cm); table.setReadonly(true);
	 * TableColumn column = table.getColumn("行号"); column.setCellRenderer(new
	 * CTableLinenoRender(dbmodel));
	 * 
	 * if (table.getRowCount() > 0) { table.addRowSelectionInterval(0, 0); }
	 * 
	 * hideColumn(table);
	 * 
	 * //去掉F2键编辑 enter下一行 InputMap map =
	 * table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
	 * ).getParent(); map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0,
	 * false)); map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));
	 * 
	 * 
	 * return table; }
	 */

	/**
	 * 将表格一列的宽度设为0
	 * 
	 * @deprecated
	 */
	private void hideColumn(CTable table) {
		// 对于隐藏列最小宽度设为0
		int i = 0;
		TableColumnModel cm = table.getColumnModel();
		for (i = 0; i < cm.getColumnCount(); i++) {
			TableColumn tc = cm.getColumn(i);
			int mi = tc.getModelIndex();
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos.elementAt(mi);
			if (colinfo.isHide()) {
				tc.setMinWidth(0);
				tc.setPreferredWidth(0);
				tc.setWidth(0);
			}
		}
	}

	/**
	 * 左键点击表格的一个单元格
	 * 
	 * @param row
	 * @param col
	 */
	public void on_rclick(int row, int col) {
		if (row >= 0) {
			table.addRowSelectionInterval(row, row);
		}
		createPopmenu().show(table, (int) mouseclickpoint.getX(),
				(int) mouseclickpoint.getY());

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_rclick(row, col);
		}

		if (editdelegate != null) {
			editdelegate.on_rclick(this, row, col);
		}
		if (zxdelegate != null) {
			zxdelegate.on_rclick(this, row, col);
		}
	}

	/**
	 * 巡罗线程 class PatrolThread extends Thread { public void run() { while
	 * (!closed) { updateUI();
	 * <p/>
	 * try { Thread.sleep(100); } catch (InterruptedException e) { } } }
	 * <p/>
	 */

	/**
	 * 查询条件定义
	 */
	protected Querycond querycond = null;

	/**
	 * 取得可以查询的列
	 * 
	 * @return
	 */
	public Vector<DBColumnDisplayInfo> getCanquerycolinfos() {
		Vector<DBColumnDisplayInfo> querycols = new Vector<DBColumnDisplayInfo>();
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (!colinfo.isDbcolumn()) {
				continue;
			}
			String coltype = colinfo.getColtype();
			if (coltype.equalsIgnoreCase("number")
					|| coltype.equalsIgnoreCase("date")
					|| coltype.equalsIgnoreCase("varchar")) {
				querycols.add(colinfo);
			}
		}
		return querycols;
	}

	/**
	 * 取查询条件定义
	 * 
	 * @return
	 */
	protected Querycond getQuerycond() {
		if (querycond != null) {
			return querycond;
		}
		querycond = new Querycond();
		querycond.setHovcond(form);
		if (querycolumns == null || querycolumns.size() == 0) {
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (!colinfo.isQueryable()) {
					continue;
				}
				colinfo.setQuerymust(isQuerymustcol(colinfo.getColname()));
				String coltype = colinfo.getColtype();
				if (coltype.equalsIgnoreCase("number")
						|| coltype.equalsIgnoreCase("date")
						|| coltype.equalsIgnoreCase("varchar")) {
					querycond.add(new Querycondline(querycond, colinfo));
				}
			}
		} else {
			Enumeration<String> en = querycolumns.elements();
			while (en.hasMoreElements()) {
				String colname = en.nextElement();
				String subqueryopid = "";
				int p = colname.indexOf(",");
				if (p > 0) {
					subqueryopid = colname.substring(p + 1);
					colname = colname.substring(0, p);
				}
				DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(colname);
				if (colinfo == null) {
					logger.error("getQuerycond() 找不到列" + colname);
					continue;
				}
				if (!colinfo.isQueryable()) {
					continue;
				}
				colinfo.setQuerymust(isQuerymustcol(colinfo.getColname()));
				colinfo.setSubqueryopid(subqueryopid);

				String coltype = colinfo.getColtype();
				if (coltype.equalsIgnoreCase("number")
						|| coltype.equalsIgnoreCase("date")
						|| coltype.equalsIgnoreCase("varchar")) {
					querycond.add(new Querycondline(querycond, colinfo));
				}
			}
		}
		return querycond;
	}

	/**
	 * 返回查询条件定义
	 */
	public Querycond getCreatedquerycond() {
		return getQuerycond();
	}

	/**
	 * 输入查询条件对话框
	 */
	protected CQueryDialog querydlg = null;

	/**
	 * 执行查询.先弹出条件对话框,再创建查询线程进行查询
	 */
	public void doQuery() {
		if (0 != on_beforequery()) {
			return;
		}
		String wheres = doQueryreturnWheres();
		if (wheres == null)
			return;
		this.usecrosstable = false;

		doQuery(wheres);
	}

	/**
	 * 弹出查询窗口,返回仅由查询窗口确定的查询条件
	 * 
	 * @return 返回查询条件. 如果用户取消,返回null
	 */
	public String doQueryreturnWheres() {
		if (querydlg == null) {
			CFrame pframe = getParentFrame();
			String title = "";
			if (pframe != null) {
				title = pframe.getTitle();
			}
			querydlg = new CQueryDialog(getParentFrame(), title + "查询条件", this);
			getQuerycond();
			if (querydelegate != null) {
				Querycond newcond = querydelegate.on_query(querycond);
				if (newcond != null) {
					querycond = newcond;
				}
			}
			querydlg.initControl(querycond);
			querydlg.pack();
		}
		querydlg.setVisible(true);
		boolean confirm = querydlg.isConfirm();
		if (!confirm) {
			return null;
		}
		String wheres = querycond.getWheres();
		return wheres;
	}

	/**
	 * 查询前存储过程返回的wheres
	 */
	private String prequeryspwheres = null;

	public void doQuery(String wheres) {
		String otherwheres = getOtherWheres();
		if (otherwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += otherwheres;
		}

		if (ruleeng != null) {
			String s = ruleeng.processWhere(this, "附加查询条件");
			if (s != null && s.length() > 0) {
				if (wheres.length() > 0) {
					wheres = wheres + " and ";
				}
				wheres += s;
			}
		}

		// 是否有查询前store proc?
		if (ruleeng != null && prequeryspwheres == null) {
			String procname = ruleeng.processPrequerystoreproc(this,
					"查询前处理存储过程");
			if (procname != null && procname.length() > 0) {
				ClientRequest req = new ClientRequest(
						"npclient:execprequerystoreproc");
				ParamCommand pcmd = new ParamCommand();
				req.addCommand(pcmd);
				pcmd.addParam("procname", procname);
				pcmd.addParam("wheres", wheres);

				SendHelper sh = new SendHelper();
				try {
					ServerResponse resp = sh.sendRequest(req);
					if (!resp.getCommand().startsWith("+OK")) {
						logger.error("error:" + resp.getCommand());
						errorMessage("错误", resp.getCommand());
						return;
					}
					ParamCommand respcmd = (ParamCommand) resp.commandAt(1);
					prequeryspwheres = respcmd.getValue("otherwheres");

				} catch (Exception e) {
					logger.error("error", e);
					errorMessage("错误", e.getMessage());
					return;
				}
			} else {
				prequeryspwheres = "";
			}

		}

		if (prequeryspwheres != null && prequeryspwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += prequeryspwheres;
		}

		doRetrieve(wheres);
	}

	/**
	 * 返回完整的查询select sql
	 * 
	 * @return
	 */
	public String getFinalquerysql(String wheres) {
		String otherwheres = getOtherWheres();
		if (otherwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += otherwheres;
		}

		if (ruleeng != null) {
			String s = ruleeng.processWhere(this, "附加查询条件");
			if (s != null && s.length() > 0) {
				if (wheres.length() > 0) {
					wheres = wheres + " and ";
				}
				wheres += s;
			}
		}

		if (prequeryspwheres != null && prequeryspwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += prequeryspwheres;
		}

		String sql = buildSelectSql(wheres);
		String ob = getSqlOrderby();
		if (ob.length() > 0) {
			sql = sql + " " + ob;
		}

		return sql;
	}

	public void doRequery() {
		if (lastselectsql == null || lastselectsql.length() == 0)
			return;
		if (toolbar != null) {
			toolbar.setQuerybuttonText("停止");
		}
		querystarttime = System.currentTimeMillis();
		form.clearAll();
		currow = -1;

		this.setStatusmessage("开始进行查询..... ");
		on_retrievestart();
		this.getDBtableModel().doRetrieve(lastselectsql,
				DefaultNPParam.fetchmaxrow);
	}

	/**
	 * 根据where条件,调用buildSelectSql()合成select语句进行查询 创建查询线程,因此本函数返回并不意味着可以得到查询结果.
	 * 
	 * @param wheres
	 */
	public void doRetrieve(String wheres) {
		String sql = buildSelectSql(wheres);
		String ob = getSqlOrderby();
		if (ob.length() > 0) {
			sql = sql + " " + ob;
		}
		logger.info("执行查询：" + sql);
		lastselectsql = sql;

		if (toolbar != null) {
			toolbar.setQuerybuttonText("停止");
		}
		querystarttime = System.currentTimeMillis();
		form.clearAll();
		currow = -1;

		this.setStatusmessage("开始进行查询..... ");
		on_retrievestart();
		this.getDBtableModel().setUsequerythread(usequerythread);
		this.getDBtableModel().doRetrieve(sql, DefaultNPParam.fetchmaxrow);
	}

	/**
	 * 查询.sql为完整的select语句
	 * 
	 * @param sql
	 */
	public void doSqlselect(String sql) {
		logger.info("执行查询：" + sql);
		lastselectsql = sql;

		if (toolbar != null) {
			toolbar.setQuerybuttonText("停止");
		}
		querystarttime = System.currentTimeMillis();
		form.clearAll();
		currow = -1;

		this.setStatusmessage("开始进行查询..... ");
		on_retrievestart();
		this.getDBtableModel().doRetrieve(sql, DefaultNPParam.fetchmaxrow);
	}

	/**
	 * 表格自动设置宽度
	 */
	protected void resizeTable() {
		if (!isTableautosized()) {
			table.autoSize();
			setTableautosized(true);
		}
		table.redraw();
		/*
		 * for (int c = 0; c < table.getColumnCount(); c++) { int maxwidth = 20;
		 * for (int r = 0; r < table.getRowCount(); r++) { TableCellRenderer
		 * cellRenderer = table.getCellRenderer(r, c); Component comp =
		 * cellRenderer.getTableCellRendererComponent(table, table.getValueAt(r,
		 * c), false, false, r, c); int width = comp.getPreferredSize().width;
		 * if (width > maxwidth) { maxwidth = width; } }
		 * 
		 * TableColumn column = table.getColumnModel().getColumn(c);
		 * TableCellRenderer headerRenderer = column.getHeaderRenderer(); if
		 * (headerRenderer == null) { headerRenderer = new
		 * DefaultTableCellRenderer(); } Component headcomp =
		 * headerRenderer.getTableCellRendererComponent(table,
		 * column.getHeaderValue(), false, false, 0, 0); int headwidth =
		 * headcomp.getPreferredSize().width;
		 * 
		 * maxwidth = Math.max(maxwidth, headwidth); // maxwidth += PADDED;
		 * column.setPreferredWidth(maxwidth); }
		 */
	}

	/**
	 * 显示状态条信息
	 * 
	 * @param msg
	 */
	public void setStatusmessage(String msg) {
		statusbar.setStatus(msg);
	}

	/**
	 * resizeTable(); if (table.getRowCount() > 0) {
	 * table.addRowSelectionInterval(0, 0); on_tablerowchanged(0, 0,-1,-1); }
	 * toolbar.setQuerybuttonText("查询"); on_retrieved();
	 */

	/*
	 * class SteActionListener extends AbstractAction {
	 * 
	 * public SteActionListener(String name) { super(name);
	 * super.putValue(AbstractAction.ACTION_COMMAND_KEY, name); }
	 * 
	 * public void actionPerformed(ActionEvent e) {
	 * CSteModel.this.actionPerformed(e); } }
	 */
	/**
	 * 定位到下一行
	 */
	public void doNextRow() {
		if (table.getRowCount() == 0)
			return;
		int row = getRow();
		if (row >= table.getRowCount() - 1 - 1) {
			return;
		}
		table.setRowSelectionInterval(row + 1, row + 1);
		scrollToCell(row + 1, 0);
	}

	/**
	 * 定位到上一行
	 */
	public void doPriorRow() {
		if (table.getRowCount() == 0)
			return;
		int row = getRow();
		if (row == 0)
			return;
		table.setRowSelectionInterval(row - 1, row - 1);
		scrollToCell(row - 1, 0);

	}

	/**
	 * 定位到第0行
	 */
	public void doFirstRow() {
		if (table.getRowCount() == 0)
			return;
		// table.addRowSelectionInterval(0, 0);
		table.setRowSelectionInterval(0, 0);
		scrollToCell(0, 0);
	}

	/**
	 * 定位到最后一行
	 */
	public void doLastRow() {
		if (table.getRowCount() == 0)
			return;
		table.setRowSelectionInterval(table.getRowCount() - 1, table
				.getRowCount() - 1 - 1);
		scrollToCell(table.getRowCount() - 1, 0);

	}

	/**
	 * 返回某行的记录状态.
	 * 
	 * @param row
	 * @return RecordTrunk.DBSTATUS_XXXXX
	 */
	public int getdbStatus(int row) {
		return dbmodel.getdbStatus(row);
	}

	/**
	 * 设置某一行记录状态
	 * 
	 * @param row
	 * @param dbstatus
	 *            取值为RecordTrunk.DBSTATUS_XXXXX
	 */
	public void setdbStatus(int row, int dbstatus) {
		// DBTableModel model = (DBTableModel) table.getModel();
		dbmodel.setdbStatus(row, dbstatus);
		tableChanged(row);

	}

	/**
	 * 返回数据源dbmodel
	 * 
	 * @return
	 */
	public DBTableModel getDBtableModel() {
		if (this.usecrosstable) {
			return crossdbmodel;
		}

		if (dbmodel == null) {
			dbmodel = new DBTableModel(formcolumndisplayinfos);
		}
		return dbmodel;
	}

	/**
	 * 返回表格使用的带合计的数据源sumdbmodel
	 * 
	 * @return
	 */
	public Sumdbmodel getSumdbmodel() {
		if (this.usecrosstable) {
			return sumcrossdbmodel;
		}
		return sumdbmodel;
	}

	/**
	 * 设置表格table的数据源
	 * 
	 * @param dbmodel
	 */
	public void setDBtableModel(DBTableModel dbmodel) {
		this.dbmodel = dbmodel;
		sumdbmodel = createSumdbmodel(dbmodel);
		table.setModel(sumdbmodel);
		TableColumn column = table.getColumn("行号");
		column.setCellRenderer(new CTableLinenoRender(sumdbmodel));
		this.setStatusmessage("共" + this.getRowCount() + "条记录");
		doHideform();
		this.tableChanged();
		resizeTable();
	}

	/**
	 * 生成带合计或带分组合计的
	 * 
	 * @param dbmodel
	 * @return
	 */
	protected Sumdbmodel createSumdbmodel(DBTableModel dbmodel) {
		if (ruleeng != null) {
			Vector<SplitGroupInfo> gpinfos = ruleeng.processGroup(this, "分组");
			if (gpinfos != null && gpinfos.size() > 0) {
				return new GroupDBTableModel(this.getDBtableModel(), gpinfos);
			}
		}
		return new Sumdbmodel(dbmodel, getCalcsumcols());
	}

	/**
	 * 返回记录行数
	 * 
	 * @return
	 */
	public int getRowCount() {
		return dbmodel.getRowCount();
	}

	/**
	 * 新增记录
	 */
	public void doNew() {
		if (0 != on_beforeNew()) {
			return;
		}

		DBTableModel tablemodel = getDBtableModel();
		tablemodel.appendRow();
		int newrow = tablemodel.getRowCount() - 1;
		sumdbmodel.fireDatachanged();
		on_click(newrow, -1);
		scrollToCell(newrow, 1);
		on_new(newrow);

		tableChanged();

		scrollToCell(newrow, 1);
		table.addRowSelectionInterval(newrow, newrow);
		bindDataSetEnable(newrow);

		if (this.tableeditable) {
			table.editCellAt(newrow, getFirstEditableColumn(newrow));
		} else {
			// calcFormwindowsize();
			// steformwindow.setVisible(true);
			// steformwindow.onActive(true);
			showForm();
			form.onActive(true);
		}

	}

	/**
	 * 计算form窗口位置
	 */
	protected void calcFormwindowsize() {
		// 求tablescrollpane的位置
		JFrame frame = getParentFrame();
		JComponent p = tablescrollpane;
		Point location = p.getLocation();
		for (;;) {
			p = (JComponent) p.getParent();
			Point templ = p.getLocation();
			location.x = location.x + templ.x;
			location.y = location.y + templ.y;
			if (p == (JComponent) frame.getContentPane()) {
				break;
			}
		}
		Point locationscr = frame.getLocationOnScreen();
		location.x += locationscr.x;
		location.y += locationscr.y;

		Dimension size = tablescrollpane.getSize();
		location.x = location.x + 2;

		// steformwindow.setLocation(location);
		form.setPreferredSize(size);
		form.setSize(size);
		// steformwindow.setPreferredSize(size);
		// steformwindow.setSize(size);

		// steformwindow.setBounds(location.x, location.y, (int)size.getWidth(),
		// (int)size.getHeight());
	}

	/**
	 * 修改记录
	 */
	public void doModify() {
		int row = getRow();
		if (row < 0)
			return;
		if (row == table.getRowCount() - 1) {
			return;
		}

		bindDataSetEnable(row);
		if (this.tableeditable) {
			table.requestFocus();
			table.editCellAt(row, getFirstEditableColumn(row));
		} else {
			showForm();
			form.onActive(false);
			/*
			 * this.calcFormwindowsize(); steformwindow.setVisible(true);
			 * steformwindow.onActive(false);
			 */}

	}

	public void showForm() {
		if (EventQueue.isDispatchThread()) {
			this.tablescrollpane.setViewportView(form);
			formvisible = true;
		} else {
			Runnable r = new Runnable() {
				public void run() {
					tablescrollpane.setViewportView(form);
					formvisible = true;
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * this.calcFormwindowsize(); steformwindow.setVisible(true);
		 * steformwindow.onActive(true);
		 */}

	/**
	 * 撤消修改
	 */
	public void doUndo() {
		if (ruleeng != null) {
			int ret = ruleeng.process(this, "屏蔽撤消", 0);
			if (ret < 0)
				return;
		}

		int row = getRow();
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			if (0 != listener.on_beforeundo()) {
				return;
			}
		}

		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int r = rows[i];
			if (r >= 0 && r < dbmodel.getRowCount()) {
				doUndo(r);
			}
		}
		sumdbmodel.fireDatachanged();

		if (row >= 0 && row < getDBtableModel().getRowCount()) {
			bindDataSetEnable(row);
			setRow(row);
		} else {
			form.clearAll();
			setRow(-1);
		}
		tableChanged();
	}

	/**
	 * 撤消一行的修改
	 * 
	 * @param row
	 */
	public void doUndo(int row) {
		dbmodel.undo(row);
		// 触发itemchanged
		for (int i = 0; i < dbmodel.getColumnCount(); i++) {
			DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
					.elementAt(i);
			if (colinfo.getColtype().equals("行号"))
				continue;
			if (!colinfo.isDbcolumn())
				continue;
			this.on_itemvaluechange(row, colinfo.getColname(), dbmodel
					.getItemValue(row, i));
		}
	}

	/**
	 * 隐藏卡片窗口
	 * 
	 * @return
	 */
	public boolean doHideform() {
		if (EventQueue.isDispatchThread()) {
			commitEdit();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					commitEdit();
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!formvisible) {
			return true;
		}

		if (tableeditable) {
			return true;
		}
		if (form == null)
			return true;

		if (isShowformonly()) {
			return true;
		}

		if (dbmodel.getRowCount() == 0) {
			currow = -1;
		}
		if (currow >= 0) {
			int dbstatus = getdbStatus(currow);
			if (dbstatus == RecordTrunk.DBSTATUS_NEW
					|| dbstatus == RecordTrunk.DBSTATUS_MODIFIED) {
				if (on_checkrow(currow, getDBtableModel()) != 0)
					return false;

			}
		}

		if (EventQueue.isDispatchThread()) {
			doHideformImpl();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					doHideformImpl();
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	protected void doHideformImpl() {
		if (currow >= 0) {
			// tablechanged会改变currow by wwh 20070822
			int memrow = currow;
			table.tableChanged(new TableModelEvent(table.getModel()));
			currow = memrow;
			table.addRowSelectionInterval(currow, currow);
		}
		// steformwindow.setVisible(false);
		formvisible = false;

		tablescrollpane.setViewportView(table);
		table.requestFocus();
	}

	/**
	 * 放弃修改
	 * 
	 * @return
	 */
	public boolean doCancelform() {
		if (tableeditable) {
			return true;
		}
		if (form == null)
			return false;

		if (!form.cancelEdit())
			return false;
		// tablechanged会改变currow by wwh 20070822
		int memrow = currow;
		this.tableChanged();
		currow = memrow;
		if (currow >= 0) {
			table.addRowSelectionInterval(currow, currow);
		}
		// steformwindow.setVisible(false);
		formvisible = false;

		tablescrollpane.setViewportView(table);
		table.requestFocus();
		return true;
	}

	/**
	 * 删除记录
	 */
	public void doDel() {
		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int r = rows[i];
			if (r >= 0 && r < dbmodel.getRowCount()) {
				doDel(r);
			}
		}
		if (dbmodel.getRowCount() == 0) {
			currow = -1;
		}
	}

	/**
	 * 删除某行
	 * 
	 * @param row
	 */
	public void doDel(int row) {
		TableCellEditor tce = table.getCellEditor();
		if (tce != null) {
			if (!tce.stopCellEditing())
				return;
		}

		if (getdbStatus(row) != RecordTrunk.DBSTATUS_NEW
				&& on_beforedel(row) != 0) {
			return;
		}

		DBTableModel tablemodel = getDBtableModel();
		RecordTrunk record = tablemodel.getRecordThunk(row);
		if (record.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
			tablemodel.removeRow(row);
			// 删除后调用,这样计算细单之和才对
			on_del(row);
			if (row < dbmodel.getRowCount()) {
				setRow(row);
			} else {
				if (dbmodel.getRowCount() > 0) {
					row = dbmodel.getRowCount() - 1;
					setRow(row);
				} else {
					row = -1;
					currow = -1;
				}
			}
			if (row < 0) {
				form.clearAll();
				doHideform();
			} else {
				bindDataSetEnable(row);
			}
		} else {
			record.setDbstatus(RecordTrunk.DBSTATUS_DELETE);
			on_del(row);
		}
		sumdbmodel.fireDatachanged();
		tableChanged();
		if (row >= 0 && row < dbmodel.getRowCount()) {
			setRow(row);
		}
		form.fireStatuschanged();

	}

	/**
	 * 卡片窗口编辑后,调用本函数设置数据的值.
	 * 
	 * @param row
	 * @param colname
	 * @param value
	 */
	public void setFormfieldvalue(int row, String colname, String value) {
		if (tableeditable) {
			// 如果是表格直接编辑,不能从form来触发修改.by wwh 20070816
			return;
		}

		if (row < 0 || row > dbmodel.getRowCount() - 1) {
			return;
		}

		this.currow = row;
		String curv = getDBtableModel().getItemValue(row, colname);
		if (curv == null)
			curv = "";
		if (!curv.equals(value)) {
			getDBtableModel().setItemValue(row, colname, value);
			tableChanged(row);
			on_itemvaluechange(row, colname, value);
		}
		this.scrollToCell(row, 1);
	}

	/**
	 * 重载本函数,返回其它查询条件。 将与查询条件对话框的条件逻辑与操作
	 * 
	 * @return
	 */
	protected String getOtherWheres() {
		return "";
	}

	/**
	 * 返回sql 的order by
	 * 
	 * @return
	 */
	protected String getSqlOrderby() {
		return "";
	}

	/**
	 * 如果选hov有条件，在这里输入
	 * 
	 * @param row
	 *            触发hov的行。
	 * @param colname
	 *            触发hov的列名
	 */
	public String getHovOtherWheres(int row, String colname) {
		return "";
	}

	/**
	 * HOV返回填写值.
	 * 
	 * @param row
	 *            触发HOV的行
	 * @param colname
	 *            触发HOV的列名
	 * @param hovmodel
	 *            hov的返回数据,应该只有一行数据.
	 * @return
	 */
	public int on_hov(int row, String colname, DBTableModel hovmodel) {
		if (colname.equalsIgnoreCase("filegroupid")) {
			onFiles();
			return 0;
		}
		// 根据hovdefine设置值
		if (hovmodel == null || hovmodel.getRowCount() == 0)
			return -1;
		TableCellEditor tce = table.getCellEditor();

		setHovshowing(colname, false);

		DBTableModel dbmodel = this.getDBtableModel();
		DBColumnDisplayInfo colinfo = this.getDBColumnDisplayInfo(colname);
		if (colinfo == null) {
			return -1;
		}
		Hovdefine hovdefine = colinfo.getHovdefine();
		if (hovdefine == null) {
			return -1;
		}

		if (tableeditable && tce != null) {
			if (tce instanceof Tablecelleditor) {
				Tablecelleditor mytce = (Tablecelleditor) tce;
				if (mytce.colinfo.getColname().equalsIgnoreCase(colname)) {
					tce.stopCellEditing();
				}
			}
		}

		// 设置对应列的值
		Iterator it = hovdefine.colpairmap.keySet().iterator();
		while (it.hasNext()) {
			String hovcolname = (String) it.next();
			String dbcolname = hovdefine.colpairmap.get(hovcolname);
			if (dbcolname.length() == 0) {
				continue;
			}
			String hovvalue = hovmodel.getItemValue(0, hovcolname);
			dbmodel.setItemValue(row, dbcolname, hovvalue);
			// 如果非本列，通知值变化了。
			if (!dbcolname.equals(colname)) {
				this.on_itemvaluechange(row, dbcolname, hovvalue);
			}
		}
		sumdbmodel.fireDatachanged();
		tableChanged(row);
		bindDataSetEnable(row);

		// 如果表格是可编辑的,进入下一列
		if (tableeditable) {
			if (tce != null && tce instanceof Tablecelleditor) {
				// ((Tablecelleditor) tce).editNext();
				editNext();

				// 要setfocus

				final Tablecelleditor mytce = (Tablecelleditor) table
						.getCellEditor();
				Runnable r = new Runnable() {
					public void run() {
						mytce.colinfo.getEditComponent().requestFocus();
					}
				};
				SwingUtilities.invokeLater(r);
			}
		} else {
			// TODO next comp
			/*
			 * FocusTraversalPolicy ftp =
			 * steformwindow.getFocusTraversalPolicy(); final Component nextcmp
			 * = ftp.getComponentAfter(steformwindow,
			 * colinfo.getEditComponent()); if (nextcmp != null) { Runnable r =
			 * new Runnable() { public void run() { nextcmp.requestFocus(); } };
			 * SwingUtilities.invokeLater(r); }
			 */}

		if (editdelegate != null) {
			int ret = editdelegate.on_hov(this, row, colname);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_hov(this, row, colname);
			if (ret != 0)
				return ret;
		}

		return 0; // 成功
	}

	/**
	 * 清除一个hov设定的所有列值的
	 * 
	 * @param row
	 * @param colname
	 * @param hovmodel
	 * @return
	 */
	public int on_clearhov(int row, String colname) {
		TableCellEditor tce = table.getCellEditor();
		setHovshowing(colname, false);

		DBTableModel dbmodel = this.getDBtableModel();
		DBColumnDisplayInfo colinfo = this.getDBColumnDisplayInfo(colname);
		if (colinfo == null) {
			return -1;
		}
		Hovdefine hovdefine = colinfo.getHovdefine();
		if (hovdefine == null) {
			return -1;
		}

		if (tableeditable && tce != null) {
			if (tce instanceof Tablecelleditor) {
				Tablecelleditor mytce = (Tablecelleditor) tce;
				if (mytce.colinfo.getColname().equalsIgnoreCase(colname)) {
					tce.stopCellEditing();
				}
			}
		}

		// 设置对应列的值
		Iterator it = hovdefine.colpairmap.keySet().iterator();
		while (it.hasNext()) {
			String hovcolname = (String) it.next();
			String dbcolname = hovdefine.colpairmap.get(hovcolname);
			if (dbcolname.length() == 0) {
				continue;
			}
			String hovvalue = "";
			dbmodel.setItemValue(row, dbcolname, hovvalue);
			// 如果非本列，通知值变化了。
			if (!dbcolname.equals(colname)) {
				this.on_itemvaluechange(row, dbcolname, hovvalue);
			}
		}
		sumdbmodel.fireDatachanged();
		tableChanged(row);
		bindDataSetEnable(row);

		// 如果表格是可编辑的,进入下一列
		if (tableeditable) {
			if (tce != null && tce instanceof Tablecelleditor) {
				// ((Tablecelleditor) tce).editNext();
				editNext();
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_hov(this, row, colname);
			if (ret != 0)
				return ret;
		}

		if (zxdelegate != null) {
			int ret = zxdelegate.on_hov(this, row, colname);
			if (ret != 0)
				return ret;
		}

		return 0; // 成功
	}

	/**
	 * 取当前行
	 * 
	 * @return
	 */
	public int getRow() {
		return currow;
	}

	/**
	 * 设置保存后,服务器返回的保存结果
	 * 
	 * @param results
	 */
	public void setLineresults(Vector<RecordTrunk> results) {
		getDBtableModel().setLineresults(results);
		getDBtableModel().clearDeleted();
		sumdbmodel.fireDatachanged();
		// donot call tablechanged()! 20080506
		// tableChanged();
		// currow = -1;

		// call tablechanged(row) every row
		Enumeration<RecordTrunk> en = results.elements();
		while (en.hasMoreElements()) {
			RecordTrunk lineresult = (RecordTrunk) en.nextElement();
			try {
				int row = Integer.parseInt(lineresult.getValueAt(0));
				table.tableChanged(new TableModelEvent(table.getModel(), row));
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存数据
	 */
	public int doSave() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽保存", 0) < 0)
				return -1;
		}

		if (!showformonly && !doHideform()) {
			return -1;
		}

		if (0 != on_beforesave()) {
			return -1;
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			int ret = listener.on_beforesave();
			if (ret != 0) {
				return ret;
			}
		}

		try {
			if (useattachfile && !uploadFiles()) {
				warnMessage("警告", "因用户取消了上传文件,没有继续保存");
				return -1;
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("错误", e.getMessage());
			return -1;
		}

		DBTableModel saveds = getModifiedDbmodel();
		if (saveds.getRowCount() == 0) {
			if (useattachfile && uploadedfilecount > 0) {
				dbmodel.resetWantuploadfiles();
				infoMessage("提示", "附件文件已上传");
				return 0;
			} else {
				infoMessage("提示", "没有数据需要保存");
				return -1;
			}
		}

		CDefaultProgress progress = null;

		progress = new CDefaultProgress(this.getParentFrame());
		/*
		 * if (steformwindow.isVisible()) { progress = new
		 * CDefaultProgress(steformwindow); } else { }
		 */
		SaveThread t = new SaveThread(progress);
		t.start();
		progress.show();

		on_save();
		en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_save();
		}

		return 0;
	}

	/**
	 * 保存后
	 */
	protected void on_save() {

	}

	/**
	 * 重新设置了formdbcolumndisplayinfos和tabledbcolumndisplayinfos的定义
	 */
	public void fireDBColumnChanged() {
		/*
		 * // 原有的列 ArrayList<String> ar = new ArrayList<String>(); for (int i =
		 * 0; tablecolumns != null && i < tablecolumns.length; i++) { String cn
		 * = tablecolumns[i]; DBColumnDisplayInfo colinfo =
		 * getDBColumnDisplayInfo(cn); if (colinfo != null) { ar.add(cn); } } //
		 * 新加的列 Enumeration<DBColumnDisplayInfo> en =
		 * formcolumndisplayinfos.elements(); while (en.hasMoreElements()) {
		 * DBColumnDisplayInfo colinfo = en.nextElement(); boolean has = false;
		 * for (int i = 0; tablecolumns != null && i < tablecolumns.length; i++)
		 * { if (colinfo.getColname().equalsIgnoreCase(tablecolumns[i])) { has =
		 * true; break; } } if (!has) { ar.add(colinfo.getColname()); } }
		 * tablecolumns = new String[ar.size()]; ar.toArray(tablecolumns); //
		 * 考虑会删列加列，重建table table = null; recreateDBModel();
		 */
		// 强制保存吧
		saveUI();
		/*
		 * // 重新读出 loadDBColumnInfos();
		 */
		form = createForm();
		/*
		 * if (steformwindow != null) { steformwindow.setVisible(false);
		 * steformwindow.dispose(); } steformwindow = createFormwindow();
		 * steformwindow.pack(); steformwindow.setVisible(false);
		 */
	}

	/**
	 * 保存后.
	 */
	protected void on_aftersave() {

	}

	/**
	 * 保存到服务器。本函数是同步函数。
	 * 
	 * @param progress
	 *            状态指标接口
	 */
	public boolean savetoserver(CProgressIF progress) {
		if (on_beforesave() != 0)
			return false;

		doHideform();
		// 将数据保存进行提交.
		StringCommand cmd1 = new StringCommand(getSaveCommandString());
		DataCommand cmd2 = new DataCommand();
		DBTableModel saveds = getModifiedDbmodel();
		cmd2.setDbmodel(saveds);

		ClientRequest req = new ClientRequest();
		req.addCommand(cmd1);
		req.addCommand(cmd2);

		if (!lockData()) {
			warnMessage("不要重复提交", "不要重复提交");
			return false;
		}
		errorcount = 0;
		try {
			if (progress != null)
				progress.appendMessage("开始保存，正在提交数据,等待服务器响应......");
			RemoteConnector rmtconn = new RemoteConnector();
			String url = DefaultNPParam.defaultappsvrurl;
			ServerResponse svrresp = null;
			if (DefaultNPParam.debug == 1) {
				svrresp = Server.getInstance().process(req);
			} else {
				svrresp = rmtconn.submitRequest(url, req);
			}

			if (progress != null)
				progress.appendMessage("收到响应，正在处理结果....");

			CommandBase tmpcmd = svrresp.commandAt(0);
			if (tmpcmd instanceof StringCommand) {
				if (progress != null)
					// 20080529 这里要叫保存结果,不要叫保存失败
					progress.messageBox("保存结果", ((StringCommand) tmpcmd)
							.getString());
				on_aftersave();
				return false;
			}

			ResultCommand cmd = (ResultCommand) svrresp.commandAt(0);
			setLineresults(cmd.getLineresults());

			// 定位到行
			RecordTrunk rec = cmd.getLineresults().elementAt(0);
			int row = Integer.parseInt(rec.getValueAt(0));
			if (row >= 0 && row < getDBtableModel().getRowCount()) {
				setRow(row);
				bindDataSetEnable(row);
			} else {
				form.clearAll();
				// doHideform();
			}

			// 显示失败数量．
			Enumeration<RecordTrunk> en1 = cmd.getLineresults().elements();
			while (en1.hasMoreElements()) {
				RecordTrunk lineResult = en1.nextElement();
				if (lineResult.getSaveresult() != 0) {
					errorcount++;
				}
			}
			if (errorcount == 0) {
				dbmodel.resetWantuploadfiles();
				if (progress != null)
					progress.messageBox("保存成功", "保存成功"
							+ cmd.getLineresultCount() + "条记录");
			} else {
				if (progress != null)
					progress.messageBox("保存部分成功", "保存成功"
							+ (cmd.getLineresultCount() - errorcount)
							+ "条记录,失败" + errorcount + "条记录");
				return false;
			}

		} catch (Exception e) {
			logger.error("保存错误", e);
			if (progress != null)
				progress.messageBox("保存失败", "数据保存异常．错误原因:" + e.getMessage());
			return false;
		} finally {
			unloakData();
		}
		on_aftersave();
		return true;
	}

	/**
	 * 保存线程
	 * 
	 * @author Administrator
	 * 
	 */
	protected class SaveThread extends Thread {
		CProgressIF progress = null;

		public SaveThread(CProgressIF progress) {
			this.progress = progress;
		}

		public void run() {
			savetoserver(progress);
		}
	}

	/**
	 * 保存后调用，参数为保存结果
	 * 
	 * @param errorct
	 *            错误数量
	 */
	protected void on_saved(int errorct) {

	}

	/**
	 * 新增前检查
	 * 
	 * @return 非0不能新增
	 */
	protected int on_beforeNew() {
		if (ruleeng != null) {
			int ret = ruleeng.process(this, "屏蔽新增", 0);
			if (ret < 0)
				return -1;
		}

		// 先确认现在的
		form.commitEdit();
		if (!table.confirm()) {
			return -1;
		}

		// 加行检查
		if (currow >= 0 && currow < table.getRowCount() - 1) {
			int dbstatus = this.getDBtableModel().getdbStatus(currow);
			if (dbstatus == RecordTrunk.DBSTATUS_NEW
					|| dbstatus == RecordTrunk.DBSTATUS_MODIFIED) {
				int ret = on_checkrow(currow, getDBtableModel());
				if (ret != 0) {
					return -1;
				}
			}
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			int ret = listener.on_beforenew();
			if (ret != 0) {
				return ret;
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforeNew(this);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforeNew(this);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 新增一条记录后
	 * 
	 * @param row
	 * @return
	 */
	protected int on_new(int row) {
		if (ruleeng != null) {
			ruleeng.process(this, "设置初值", row);
		}
		// 设初值
		Enumeration<DBColumnDisplayInfo> en1 = formcolumndisplayinfos
				.elements();
		while (en1.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en1.nextElement();
			String initvalue = colinfo.getInitvalue();
			if (initvalue != null && initvalue.length() > 0) {
				if (initvalue.equals("now")) {
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					initvalue = df.format(new Date());
				}
				setItemValue(row, colinfo.getColname(), initvalue);
			}
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_new(row);
		}
		// steformwindow.onActive(true);
		form.onActive(true);

		if (editdelegate != null) {
			int ret = editdelegate.on_new(this, row);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_new(this, row);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 查询前检查
	 * 
	 * @return 非0不能查询
	 */
	protected int on_beforequery() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽查询", 0) < 0)
				return -1;
		}
		if (getDBtableModel().getModifiedData().getRowCount() > 0) {
			String msg = "数据修改没有保存,如果继续查询会丢失修改的数据,还继续查询吗?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				return -1;
			}
		}

		if (!table.confirm()) {
			return -1;
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			if (0 != listener.on_beforequery()) {
				return -1;
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforequery(this);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforequery(this);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 确认编辑框的编辑.并中止编辑.
	 */
	public void commitEdit() {
		// BasicTableUI
		// FormattedTextFieldUI

		if (this.tableeditable && table != null) {
			TableCellEditor tce = table.getCellEditor();
			if (tce != null) {
				tce.stopCellEditing();
			}
			// }

		} else {
			if (form != null)
				form.commitEdit();
		}
	}

	/**
	 * 保存前检查
	 * 
	 * @return 非0不能保存
	 */
	public int on_beforesave() {
		commitEdit();

		DBTableModel model = getDBtableModel();

		for (int row = 0; row < model.getRowCount(); row++) {
			int dbstatus = getdbStatus(row);
			if (dbstatus == RecordTrunk.DBSTATUS_NEW
					|| dbstatus == RecordTrunk.DBSTATUS_MODIFIED) {
				if (on_checkrow(row, this.getDBtableModel()) != 0)
					return -1;

			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforesave(this);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforesave(this);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	protected void on_close() {
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_close();
		}
	}

	/**
	 * 关闭功能窗口前检查
	 * 
	 * @return 非0不能关闭
	 */
	public int on_beforeclose() {
		if (dbmodel == null)
			return 0;
		if (getDBtableModel().getModifiedData().getRowCount() > 0) {
			String msg = "退出未保存,是不是要保存?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				if (0 != doSave()) {
					return -1;
				}
			} else if (ret == JOptionPane.NO_OPTION) {
				return 0;
			} else if (ret == JOptionPane.CANCEL_OPTION) {
				return -1;
			}
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			int ret = listener.on_beforeclose();
			if (ret != 0) {
				return ret;
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforeclose(this);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforeclose(this);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 保存
	 */
	/*
	 * public void doSave() { //将数据同步 if(0 != prepareSave()){ return; }
	 * 
	 * MemDS saveds = getModifiedDbmodel();
	 * 
	 * //将数据保存进行提交. StringCommand cmd1 = new
	 * StringCommand(getSaveCommandString()); DataCommand cmd2 = new
	 * DataCommand(); cmd2.addMemds(saveds);
	 * 
	 * ClientRequest req = new ClientRequest(); req.addCommand(cmd1);
	 * req.addCommand(cmd2);
	 * 
	 * RemoteConnector rmtconn = new RemoteConnector();
	 * 
	 * String url = RemotesqlHelper.defaulturl;
	 * 
	 * try { ServerResponse svrresp = rmtconn.submitRequest(url, req);
	 * ResultCommand cmd = (ResultCommand) svrresp.commandAt(0);
	 * getDBtableModel().setLineresults(cmd.getLineresults());
	 * 
	 * //显示失败数量． int errorct = 0; Enumeration<LineResult> en =
	 * cmd.getLineresults().elements(); while (en.hasMoreElements()) {
	 * LineResult lineResult = en.nextElement(); if (lineResult.getResult() !=
	 * 0) { errorct++; } }
	 * 
	 * 
	 * if (errorct == 0) { infoMessage("保存成功", "保存成功" + cmd.getLineresultCount()
	 * + "条记录"); } else { warnMessage("有保存失败的记录", "保存成功" +
	 * (cmd.getLineresultCount() - errorct) + "条记录,失败" + errorct + "条记录"); } }
	 * catch (Exception e) { logger.error("保存错误", e); errorMessage("保存失败",
	 * "数据保存异常．错误原因:" + e.getMessage()); } finally { unloakData(); } }
	 */

	/**
	 * 取修改过的记录
	 * 
	 * @return
	 */
	public DBTableModel getModifiedDbmodel() {
		// 置行号
		for (int i = 0; i < dbmodel.getRowCount(); i++) {
			dbmodel.setValueAt(String.valueOf(i), i, 0);
		}

		DBTableModel saveds = dbmodel.getModifiedData();
		return saveds;
	}

	/**
	 * 检查一行
	 * 
	 * @param row
	 * @return 0表示无误。
	 */
	public int checkrow(int row) {
		return on_checkrow(row, dbmodel);
	}

	/**
	 * 检查一行
	 * 
	 * @param row
	 * @param model
	 * @return 非0没有通过检查,有错误
	 */
	protected int on_checkrow(int row, DBTableModel model) {
		if (model.getdbStatus(row) == RecordTrunk.DBSTATUS_SAVED) {
			// 如果没有修改,就不要查
			return 0;
		}
		Enumeration<DBColumnDisplayInfo> en1 = formcolumndisplayinfos
				.elements();
		while (en1.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en1.nextElement();
			if (colinfo.getRowcheck().getChecktype().equals(
					DBColumnDisplayInfo.ROWCHECK_NOTNULL)) {
				String v = model.getItemValue(row, colinfo.getColname());
				if (v == null || v.length() == 0) {
					String msg = "数据项\"" + colinfo.getTitle() + "\"必须输入。";
					String addmsg = colinfo.getRowcheck().getInfomessage();
					if (addmsg.length() > 0) {
						msg += addmsg;
					}
					warnMessage("数据项必须输入", msg);
					boolean editable = true;
					if (getdbStatus(row) != RecordTrunk.DBSTATUS_NEW
							&& on_beforemodify(row) != 0) {
						editable = false;
					}

					table.addRowSelectionInterval(row, row);
					bindDataSetEnable(row);
					if (!tableeditable) {
						showForm();
						colinfo.getEditComponent().requestFocus();
					}
					return -1;
				}
			}

		}

		if (ruleeng != null) {
			String msg = ruleeng.processRowcheck(this, "行表达式检查", row);
			if (msg != null && msg.length() > 0) {
				warnMessage("数据需要检查", msg);
				return -1;
			}
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			int ret = listener.on_checkrow(row, model);
			if (0 != ret)
				return ret;
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_checkrow(this, row);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_checkrow(this, row);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 表格table当前聚焦的单元格发生了变化
	 * 
	 * @param newrow
	 *            当前的行
	 * @param newcol
	 *            当前的列
	 * @param oldrow
	 * @param oldcol
	 */
	public void on_tablerowchanged(int newrow, int newcol, int oldrow,
			int oldcol) {
		/*
		 * 因为新的取当前行机制,删除下面三行 20070830 if (this.currow == newrow) { return; }
		 */
		if (newrow < 0)
			return;
		this.currow = newrow;
		this.curcol = newcol;

		// if (steformwindow.isVisible()) {
		if (oldrow >= 0)
			on_modify(oldrow);
		if (newrow >= 0)
			on_beforemodify(newrow);
		// }

		// scrollToCell(newrow, newcol);
		if (currow >= 0 && currow < table.getRowCount() - 1) {
			boolean editable = true;
			if (getdbStatus(currow) != RecordTrunk.DBSTATUS_NEW
					&& on_beforemodify(currow) != 0) {
				editable = false;
			}

			form.setbindingvalue(true);
			try {
				for (int c = 0; c < dbmodel.getColumnCount(); c++) {
					String colname = dbmodel.getColumnDBName(c);
					String value = dbmodel.getItemValue(currow, colname);
					if (ruleeng != null) {
						ruleeng.processItemvaluechanged(this, ruleprefix
								+ "设置动态下拉选择", currow, colname, value);
					}
					resetDdldbmodel(newrow);
				}

			} catch (Exception e1) {
				logger.error("error", e1);
			} finally {
				form.setbindingvalue(false);
			}
			bindDataSetEnable(newrow);
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_tablerowchanged(newrow, newcol, oldrow, oldcol);
		}

		if (editdelegate != null) {
			editdelegate.on_tablerowchanged(this, newrow);
		}

		if (zxdelegate != null) {
			zxdelegate.on_tablerowchanged(this, newrow);
		}
	}

	/**
	 * 根据动态下拉，重新设置编辑CComboBox的model
	 * 
	 * @param newrow
	 */
	protected void resetDdldbmodel(int newrow) {
		boolean setflag = false;
		for (int c = 0; c < dbmodel.getColumnCount(); c++) {
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos.elementAt(c);
			JComponent comp = colinfo.getEditComponent();
			if (comp instanceof CComboBox) {
				CComboBoxModel ccbmodel = dbmodel.getRecordThunk(newrow)
						.getColumnddlmodel(colinfo.getColname());
				if (ccbmodel != null) {
					((CComboBox) comp).setModel(ccbmodel);
					setflag = true;
				}
			}
		}
		if (setflag) {
			table.tableChanged(new TableModelEvent(table.getModel(), newrow));
		}

	}

	/**
	 * 某个单元格的数据发生了变化
	 * 
	 * @param row
	 *            行
	 * @param colname
	 *            列名
	 * @param value
	 *            现在的值
	 */
	protected void on_itemvaluechange(int row, String colname, String value) {
		if (ruleeng != null) {
			ruleeng.process(this, "自动计算", row, colname);
			try {
				ruleeng.processCalcColumn(this, "计算列", row);
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			form.setbindingvalue(true);
			try {
				if (ruleeng != null)
					ruleeng.processItemvaluechanged(this, ruleprefix
							+ "设置动态下拉选择", row, colname, value);
				resetDdldbmodel(row);
			} catch (Exception e1) {
				logger.error("error", e1);
			} finally {
				form.setbindingvalue(false);
			}
		}

		sumdbmodel.fireDatachanged();
		// 刷新合计行
		tableChanged(table.getRowCount() - 1);

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_itemvaluechange(row, colname, value);
		}

		if (editdelegate != null) {
			editdelegate.on_itemvaluechange(this, row, colname, value);
		}
		if (zxdelegate != null) {
			zxdelegate.on_itemvaluechange(this, row, colname, value);
		}
	}

	/**
	 * 开始从服务器请求查询数据.本数据源将被清除
	 */
	protected void on_retrievestart() {
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_retrievestart();
		}

	}

	/**
	 * 多线程查询时,查询完毕触发本函数
	 */
	protected void on_retrieved() {
		if (formcolumndisplayinfos == null) {
			return;
		}
		if (toolbar != null) {
			toolbar.setQuerybuttonText("查询");
		}
		sort();

		if (ruleeng != null) {
			this.setWaitCursor();
			try {
				ruleeng.processCalcColumn(this, "计算列", -1);
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			DBTableModel crossdbmodel;
			try {
				crossdbmodel = ruleeng.processCrosstable(dbmodel,
						getTableColumns(), "交叉表");
				if (crossdbmodel != null) {
					processCross(crossdbmodel);
				}
			} catch (Exception e) {
				logger.error("error", e);
				errorMessage("错误", "生成交叉表失败" + e.getMessage());
			} finally {
				this.setDefaultCursor();
			}
		}
		getSumdbmodel().fireDatachanged();

		resizeTable();

		// logger.debug("on_retrieved,rowcount="+dbmodel.getRowCount());
		// logger.debug("on_retrieved,sumdbmodel
		// rowcount="+sumdbmodel.getRowCount()+",sumdbmodel="+sumdbmodel);

		if (currow >= 0 && currow < getDBtableModel().getRowCount()) {
			setRow(currow);
			// form.setEnable(on_beforemodify(currow) == 0);
		} else if (getDBtableModel().getRowCount() > 0) {
			setRow(0);
			// form.setEnable(on_beforemodify(0) == 0);
		} else {
			setRow(-1);
		}

		if (editdelegate != null) {
			editdelegate.on_retrieved(this);
		}
		if (zxdelegate != null) {
			zxdelegate.on_retrieved(this);
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_retrieved();
		}

		Runnable r = new Runnable() {
			public void run() {
				if (table != null && table.isVisible()) {
					table.requestFocus();
				}
			}
		};

		SwingUtilities.invokeLater(r);
	}

	/**
	 * 生成交叉表的一切
	 * 
	 * @param crosstable
	 */
	protected void processCross(DBTableModel crossdbmodel) {
		usecrosstable = true;
		if (this.crossdbmodel != null) {
			// 将crossdbmodel中的每个dbcolumndisplayinfo的hov清为null
			Enumeration<DBColumnDisplayInfo> en = this.crossdbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				en.nextElement().setHov(null);
			}

			this.crossdbmodel.freeMemory();
			this.crosstable.freeMemory();
		}

		Vector<String> sumcols = new Vector<String>();
		Enumeration<DBColumnDisplayInfo> en = crossdbmodel
				.getDisplaycolumninfos().elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.isCalcsum()) {
				sumcols.add(col.getColname());
			}
		}

		this.crossdbmodel = crossdbmodel;
		this.sumcrossdbmodel = new Sumdbmodel(crossdbmodel, sumcols);
		sumcrossdbmodel.setCrosstable(true);
		crosstable = new CMultiheadTable(sumcrossdbmodel);
		crosstable.addMouseListener(new TableMouseListener());
		crosstable.getTableHeader().setDefaultRenderer(
				new CTableMultiHeaderRender());
		crosstable.getTableHeader().setUI(new CTableMultiheadUI());

		TableColumnModel cm = crosstable.getColumnModel();
		Vector<DBColumnDisplayInfo> colinfos = crossdbmodel
				.getDisplaycolumninfos();
		for (int c = 0; c < cm.getColumnCount(); c++) {
			TableColumn column = cm.getColumn(c);
			DBColumnDisplayInfo colinfo = colinfos.elementAt(column
					.getModelIndex());
			if (colinfo.getColtype().equals("行号")) {
				column.setCellRenderer(new CTableLinenoRender(sumcrossdbmodel));
			} else {
				PlainTablecellRender cellRenderer = new PlainTablecellRender(
						colinfo);
				column.setCellRenderer(cellRenderer);
			}
		}

		tablescrollpane.setViewportView(crosstable);
		crosstable.autoSize();
	}

	/**
	 * 在on_retrieveend事件中调用排序
	 */
	protected void sort() {
		if(sortexpr!=null && sortexpr.length()>0){
			setSortExpr(sortexpr);
			return;
		}
		String rulesort = null;
		if (ruleeng != null) {
			rulesort = ruleeng.processSort(this, "设置排序");
		}

		if (sortcolumns == null) {
			if (rulesort != null && rulesort.length() > 0) {
				try {
					getDBtableModel().sort(rulesort);
				} catch (Exception e) {
					logger.error("ERROR", e);
				}
			} else {
				getDBtableModel().resort();
			}
		} else {
			getDBtableModel().sort(sortcolumns, sortasc);
		}
	}

	/**
	 * 检查是否允许修改
	 * 
	 * @param row
	 * @return 非0不能修改
	 */
	protected int on_beforemodify(int row) {
		boolean isnew = dbmodel.getdbStatus(row) == RecordTrunk.DBSTATUS_NEW;
		if (ruleeng != null) {
			if (!isnew && ruleeng.process(this, "屏蔽修改", 0) < 0)
				return -1;
			if (!isnew && ruleeng.process(this, "有条件禁止删改", row) < 0)
				return -1;
		}
		if (table.isQuerying()) {
			return -1;
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			if (0 != listener.on_beforemodify(row)) {
				return -1;
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforemodify(this, row);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforemodify(this, row);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 修改了某一行后
	 * 
	 * @param row
	 */
	protected void on_modify(int row) {
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_modify(row);
		}

		if (editdelegate != null) {
			editdelegate.on_modify(this, row);
		}
		if (zxdelegate != null) {
			zxdelegate.on_modify(this, row);
		}
	}

	/**
	 * 删除前检查.
	 * 
	 * @param row
	 * @return 非0不能删除
	 */
	protected int on_beforedel(int row) {
		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽删除", 0) < 0)
				return -1;
			if (ruleeng.process(this, "有条件禁止删改", row) < 0)
				return -1;
		}
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			if (0 != listener.on_beforedel(row)) {
				return -1;
			}
		}

		if (editdelegate != null) {
			int ret = editdelegate.on_beforedel(this, row);
			if (ret != 0)
				return ret;
		}
		if (zxdelegate != null) {
			int ret = zxdelegate.on_beforedel(this, row);
			if (ret != 0)
				return ret;
		}

		return 0;
	}

	/**
	 * 删除后
	 * 
	 * @param row
	 */
	protected void on_del(int row) {
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_del(row);
		}

		if (editdelegate != null) {
			editdelegate.on_del(this, row);
		}
		if (zxdelegate != null) {
			zxdelegate.on_del(this, row);
		}

	}

	protected void scrollToCell(int row, int tablecolno) {
		table.scrollRectToVisible(table.getCellRect(row, tablecolno, true));

		// table.scrollToCell(row, tablecolno, tablescrollpane);
		/*
		 * Dimension viewportsize = tablescrollpane.getViewport().getSize();
		 * JScrollBar hsb = tablescrollpane.getHorizontalScrollBar(); double max
		 * = hsb.getMaximum(); double startx = hsb.getValue();
		 * 
		 * int rectx = hsb.getValue();
		 * 
		 * boolean needscroll = false; // 计算当前列是不是可见 int width = 0; for (int i =
		 * 0; i < tablecolno; i++) { width +=
		 * table.getColumnModel().getColumn(i).getWidth(); } int thiswidth =
		 * table.getColumnModel().getColumn(tablecolno).getWidth();
		 * 
		 * if (width < startx) { rectx = width; needscroll = true; } else if
		 * (width + thiswidth > startx + viewportsize.getWidth()) { rectx =
		 * width + thiswidth - (int) viewportsize.getWidth(); needscroll = true;
		 * } // 计算当前行是不是可见 JScrollBar vsb =
		 * tablescrollpane.getVerticalScrollBar(); int recty = vsb.getValue();
		 * max = vsb.getMaximum(); double rowh = max / table.getRowCount();
		 * double starty = vsb.getValue(); int rowsperpage = (int)
		 * (viewportsize.getHeight() / rowh); int firstvisiblerow = (int)
		 * (starty / rowh); if (row - firstvisiblerow > rowsperpage || row -
		 * firstvisiblerow < 0) { recty = (int) (rowh * row); needscroll = true;
		 * }
		 * 
		 * if (needscroll) { Rectangle r = new Rectangle(rectx, recty, (int)
		 * viewportsize .getWidth(), (int) viewportsize.getHeight());
		 * table.scrollRectToVisible(r); }
		 */
	}

	/**
	 * 返回某一行是否可见.
	 * 
	 * @param row
	 * @return
	 */
	protected boolean isRowvisible(int row) {
		Dimension viewportsize = tablescrollpane.getViewport().getSize();
		JScrollBar vsb = tablescrollpane.getVerticalScrollBar();
		double max = vsb.getMaximum();
		double value = vsb.getValue();

		double vh = viewportsize.getHeight();
		double rowct = this.getRowCount();
		double rowh = max / rowct;
		double rowsperpage = vh / rowh;

		int viewfirstrow = (int) (value / max * rowct + 0.5);
		int viewlastrow = (int) (viewfirstrow + rowsperpage - 1 + 0.5);

		return row >= viewfirstrow && row <= viewlastrow;

		/*
		 * int datapage = (int) ((double) row / rowsperpage);
		 * 
		 * return datapage == scrollpage;
		 */

	}

	/**
	 * 双击了某个单元格
	 * 
	 * @param row
	 *            table的行
	 * @param col
	 *            table的列
	 */
	public void on_doubleclick(int row, int col) {
		doModify();
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_doubleclick(row, col);
		}
		if (editdelegate != null) {
			editdelegate.on_doubleclick(row, col);
		}
		if (zxdelegate != null) {
			zxdelegate.on_doubleclick(this, row, col);
		}
	}

	/**
	 * 单击某一单元格
	 * 
	 * @param row
	 *            table的行
	 * @param col
	 *            dbmodel的列,已经转换过了
	 */
	public void on_click(int row, int col) {
		currow = row;

		if (col >= 0 && col < formcolumndisplayinfos.size()) {
			DBColumnDisplayInfo colinfo = this.formcolumndisplayinfos
					.elementAt(col);
			if (colinfo.getColname().equalsIgnoreCase("filegroupid")) {
				onFiles();
			}
		}

		if (row >= 0 && row < dbmodel.getRowCount()) {
			boolean editable = true;
			if (getdbStatus(row) != RecordTrunk.DBSTATUS_NEW
					&& on_beforemodify(row) != 0) {
				editable = false;
			}
			bindDataSetEnable(row);
		}
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_click(row, col);
		}
		if (editdelegate != null) {
			editdelegate.on_doubleclick(row, col);
		}
		if (zxdelegate != null) {
			zxdelegate.on_doubleclick(this, row, col);
		}

	}

	/**
	 * 表格table的鼠标侦听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class TableMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			mouseclickpoint = e.getPoint();
			int row = table.rowAtPoint(e.getPoint());
			if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getClickCount() > 1) {
				// Object src = e.getSource();

				int col = table.getColumnModel().getColumnIndexAtX(e.getX());
				col = table.convertColumnIndexToModel(col);
				if (row >= 0) {
					on_doubleclick(row, col);
				}
			}

			if (e.getID() == MouseEvent.MOUSE_CLICKED) {
				int col = table.getColumnModel().getColumnIndexAtX(e.getX());
				col = table.convertColumnIndexToModel(col);

				if (e.getButton() == MouseEvent.BUTTON3) {
					on_rclick(row, col);
				} else {
					on_click(row, col);
					if (col == 0) {
						on_clicklineno(row);
					}
				}
			}
		}

	}

	/**
	 * 表格table的鼠标移动监听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class TableMouseMotionListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			CSteModel.this.mousex = e.getX();
			CSteModel.this.mousey = e.getY();
		}
	}

	/**
	 * 数据源发生了变化,调用本函数刷新表格table
	 */
	public void tableChanged() {
		if (EventQueue.isDispatchThread()) {
			tableChangedImpl();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					tableChangedImpl();
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void tableChangedImpl() {
		int row = getRow();
		table.tableChanged(new TableModelEvent(table.getModel()));
		if (row >= 0 && row < dbmodel.getRowCount()) {
			setRow(row);
		}
	}

	/**
	 * 数据源的一行发生了变化,调用本函数刷新表格table的一行
	 * 
	 * @param row
	 */
	public void tableChanged(int row) {
		if (EventQueue.isDispatchThread()) {
			table.tableChanged(new TableModelEvent(table.getModel(), row));
		} else {
			final int tmp_row = row;
			Runnable r = new Runnable() {
				public void run() {
					table.tableChanged(new TableModelEvent(table.getModel(),
							tmp_row));
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*
	 * protected class TablechangeListener implements PropertyChangeListener {
	 * public void propertyChange(PropertyChangeEvent evt) {
	 * 
	 * String name = evt.getPropertyName(); if
	 * (AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY .equals(name)) {
	 * // if // (AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY.equals(name))
	 * // { Object oldv = evt.getOldValue(); Object newv = evt.getNewValue();
	 * int newrow = -1; int oldrow = -1; int newcol = -1; int oldcol = -1;
	 * 
	 * int colct = table.getColumnCount(); if (newv != null) { AccessibleContext
	 * accessibleContext = ((Accessible) newv) .getAccessibleContext(); int
	 * index = accessibleContext.getAccessibleIndexInParent(); newrow = index /
	 * colct; newcol = index % colct;
	 * 
	 * if (oldv != null) { accessibleContext = ((Accessible) oldv)
	 * .getAccessibleContext(); index =
	 * accessibleContext.getAccessibleIndexInParent(); oldrow = index / colct;
	 * oldcol = index % colct; } System.out.println("table changed row,col =" +
	 * newrow + "," + newcol); if (tableeditable) { // getFirstEditableColumn if
	 * (newrow >= 0 && newcol >= 0 && newrow < table.getRowCount() - 1) {
	 * table.editCellAt(newrow, newcol); } } // on_tablerowchanged(newrow,
	 * newcol, oldrow, oldcol); } } else if
	 * (AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY .equals(name)) { int
	 * index = table.getSelectionModel().getMinSelectionIndex(); if (index >= 0)
	 * { on_tablerowchanged(index, -1, -1, -1); } } } }
	 */

	/**
	 * 查询线程的监听器
	 */
	protected class RetrieveListener implements DBTableModelEvent {
		public void retrieveStart(DBTableModel dbmodel) {
			setStatusmessage("开始连接服务进行查询.......");
		}

		public int retrievePart(DBTableModel dbmodel, int startrow, int endrow,
				int retrievesize, int inflatsize) {
			StringBuffer sb = new StringBuffer();
			long t = System.currentTimeMillis() - querystarttime;
			String strspeed = "0";
			strspeed = StringUtil.bytespeed2string(retrievesize, t);
			sb.append("速度" + strspeed);
			sb.append(",已下载" + StringUtil.bytes2string(retrievesize));
			sb.append(",解压" + StringUtil.bytes2string(inflatsize));
			sb.append(",已下载" + dbmodel.getRowCount() + "条记录");
			setStatusmessage(sb.toString());

			sumdbmodel.fireDatachanged();
			tableChanged();
			// table.autoSize();

			Enumeration<CSteModelListener> en = actionListeners.elements();
			while (en.hasMoreElements()) {
				CSteModelListener listener = en.nextElement();
				if (0 != listener.on_retrievepart()) {
					return -1;
				}
			}
			return 0;

		}

		public void retrieveFinish(DBTableModel dbmodel) {
			if (formcolumndisplayinfos != null) {
				tableChanged();
			}
			// table.autoSize();
			setStatusmessage("查询完成，查询到记录" + dbmodel.getRowCount() + "条记录");
			// logger.debug("retrieveFinish ,dbmodel=" + dbmodel);
			on_retrieved();
		}

		public void retrieveError(DBTableModel dbmodel, String errormessage) {
			errorMessage("查询失败", errormessage);
			on_retrieved();
		}

	}

	/**
	 * 扩展命令处理
	 * 
	 * @param command
	 * @return 0 command已被处理;非0 command没有被处理
	 */
	protected int on_actionPerformed(String command) {
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			int ret = listener.on_actionPerformed(command);
			if (0 == ret)
				return ret;
		}

		if (actiondelegate != null) {
			return actiondelegate.on_actionPerformed(this, command);
		}
		if (zxdelegate != null) {
			return zxdelegate.on_actionPerformed(this, command);
		}
		return -1;
	}

	/**
	 * 事件处理函数,调用了on_actionPerformed()
	 */
	public void actionPerformed(ActionEvent e) {
		String actioncommand = e.getActionCommand();
		if (actioncommand == null) {
			return;
		}

		int ret = on_actionPerformed(actioncommand);
		if (0 == ret) {
			return;
		}

		if (actioncommand.startsWith(ACTION_DOCPRINT_PREFIX)) {
			String planname = actioncommand.substring(ACTION_DOCPRINT_PREFIX
					.length());
			docPrint(planname);
		}

		if (actioncommand.equals(CSteModel.ACTION_QUERY)) {
			if (!isquerying()) {
				doQuery();
			} else {
				stopQuery();
			}
		} else if (actioncommand.equals(CSteModel.ACTION_REFRESH)) {
			if (hovshowing) {
				dbmodel.getColumninfo(hovcolname).getHov().retrieveMore();
			} else {
				if (lastselectsql == null || lastselectsql.length() == 0)
					doQuery();
				else
					doRequery();
			}
		} else if (actioncommand.equals(CSteModel.ACTION_NEW)) {
			doNew();
		} else if (actioncommand.equals(CSteModel.ACTION_DEL)) {
			doDel();
		} else if (actioncommand.equals(CSteModel.ACTION_SAVE)) {
			doSave();
		} else if (actioncommand.equals(CSteModel.ACTION_MODIFY)) {
			doModify();
		} else if (actioncommand.equals(CSteModel.ACTION_UNDO)) {
			doUndo();
		} else if (actioncommand.equals(CSteModel.ACTION_HIDEFORM)) {
			doHideform();
		} else if (actioncommand.equals(CSteModel.ACTION_FIRST)) {
			doFirstRow();
		} else if (actioncommand.equals(CSteModel.ACTION_LAST)) {
			doLastRow();
		} else if (actioncommand.equals(CSteModel.ACTION_NEXT)) {
			doNextRow();
		} else if (actioncommand.equals(CSteModel.ACTION_PRIOR)) {
			doPriorRow();
		} else if (actioncommand.equals(ACTION_SETUPUI)) {
			setupUI();
		} else if (actioncommand.equals(ACTION_SAVEUI)) {
			tableColumnwidth2Dbmodel();
			saveUI();
		} else if (actioncommand.equals(ACTION_PRINTSETUP)) {
			printSetup();
		} else if (actioncommand.equals(ACTION_PRINT)) {
			print();
		} else if (actioncommand.equals(ACTION_EXIT)) {
			doExit();
		} else if (actioncommand.equals(ACTION_SELECTOP)) {
			Clientframe.getClientframe().requestFocus();
		} else if (actioncommand.equals(ACTION_EXPORT)) {
			doExport();
		} else if (actioncommand.equals(ACTION_EXPORTAS)) {
			doExportas();
		} else if (actioncommand.equals(ACTION_SELFCHECK)) {
			doSelfcheck();
		} else if (actioncommand.equals(ACTION_SETUPRULE)) {
			doSetuprule();
		} else if (actioncommand.equals(ACTION_FILES)) {
			onFiles();
		} else if (actioncommand.equals(ACTION_SAVEFACE)) {
			saveFace();
		} else if (actioncommand.equals(ACTION_USEFACE)) {
			useFace();
		} else if (actioncommand.equals(ACTION_SAVEASFACE)) {
			saveAsFace();
		} else if (actioncommand.equals(ACTION_FACESORT)) {
			faceSort();
		}
	}

	/**
	 * 保存自定义界面设置
	 */
	protected void saveFace() {
		try {
			saveSkin();
			infoMessage("提示", "保存界面成功");
		} catch (Exception e) {
			warnMessage("提示", "保存界面失败:" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 另存自定义界面
	 */
	protected void saveAsFace() {
		SaveDialog d = new SaveDialog(this.getParentFrame(), "自定义界面",
				schemeName, isdefaultscheme);
		d.setSize(320, 300);
		d.pack();
		d.setVisible(true);

		if (d.isOk()) {
			isdefaultscheme = d.isDefault();
			if (!schemeName.equals(d.getSchemeName())) {
				schemeName = d.getSchemeName();
				userviewid = "";
			}
			try {
				saveSkin();
				//需要重新加载
				faceDbmodel=null;
				// schemeList = null;
				infoMessage("提示", "保存界面成功");
			} catch (Exception e) {
				warnMessage("提示", "保存界面失败:" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 使用界面设置
	 */
	protected void useFace() {
		if (faceDbmodel == null) {
			faceDbmodel = SkinHelper.initSchemeList(this.getOpid(),
					ClientUserManager.getCurrentUser().getUserid());
		}
		ChangeSkinDialog d = new ChangeSkinDialog(getParentFrame(), opid,faceDbmodel);
		d.pack();
		d.setVisible(true);

		if (d.isOk()) {
			final String name = d.getSelectValue();
			// SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
			if (!schemeName.equals(name)) {
				schemeName = name;
				try {
					List<SkinInfo> list = SkinHelper.changeScheme(this);
					setSkin(list);
					// infoMessage("提示", "切换界面成功");
				} catch (Exception e) {
					warnMessage("提示", "切换界面失败:" + e.getMessage());
					e.printStackTrace();
				}
			}
			// }
			// });
		}

	}

	// 保存当前的个性化界面定义
	private void saveSkin() throws Exception {
		tableColumnwidth2Dbmodel();

		List<ColInfo> colInfos = new ArrayList<ColInfo>();
		ColInfo info = null;
		for (int i = 0; i < this.getTableColumns().length; i++) {

			info = new ColInfo();
			info.setColname(this.getTableColumns()[i]);
			info.setColwidth(getDBColumnDisplayInfo(getTableColumns()[i])
					.getTablecolumnwidth());
			info.setOrder(i);
			colInfos.add(info);
		}

		SkinInfo skininfo = new SkinInfo();
		skininfo.setColinfos(colInfos);
		skininfo.setClassname(this.getClass().getName());
		skininfo.setExpr(sortexpr);

		try {
			SkinHelper.save(this, skininfo);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 单据打印
	 * 
	 * @param planname
	 *            方案名
	 */
	public void docPrint(String planname) {
		int rows[] = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			docPrint(planname, row);
		}
	}

	/**
	 * 单据打印
	 * 
	 * @param planname
	 *            方案名
	 * @param row
	 *            行号
	 */
	public void docPrint(String planname, int row) {
		// 找出主键列
		String pkcolname = dbmodel.getPkcolname();
		if (pkcolname == null || pkcolname.length() == 0) {
			errorMessage("错误", "没有定义主键列，不能打印");
			return;
		}
		String masterv = getItemValue(row, pkcolname);

		File configfile = new File("conf/" + planname + ".printplan");
		Printplan plan = new Printplan("", "");
		plan.setForbidreprint(isForbidReprint());
		try {
			// 下载
			Printplan.downloadPrintplan(configfile);
			// 读文件
			plan.read(configfile);
		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("错误", e.getMessage());
			return;
		}

		// 设置调用主键列值
		plan.setInputparam(masterv);
		try {
			// 让plan准备数据
			plan.getParts().prepareData();
			// 送打印机。会弹出窗口选打印机的。
			plan.sendPrinter();
		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("错误", e.getMessage());
			return;
		}
	}

	/**
	 * 是否禁止再打印.缺省为false
	 * 
	 * @return
	 */
	protected boolean isForbidReprint() {
		return false;
	}

	/**
	 * 设置规则
	 */
	public void doSetuprule() {
		// Ruleenginee ruleeng, Object caller, String optype,
		// File savefile
		if (ruleeng == null) {
			ruleeng = new Ruleenginee();
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p > 0) {
			classname = classname.substring(p + 1);
		}
		RulesetupMaindialog frm = new RulesetupMaindialog(
				this.getParentFrame(), ruleeng, this, "ste");
		frm.pack();
		frm.setVisible(true);
		if (!frm.getOk())
			return;

		if (zxmodify) {
			// 要存在classes/专项开发/opid.zip的文件中，并上传服务器。
			File dir = CurrentdirHelper.getZxdir();
			File zxfile = new File(dir, opid + ".zip");
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".rule");
				RuleRepository.saveRule(tempfile, ruleeng.getRuletable());
				ZipHelper.replaceZipfile(zxfile, "ste.rule", tempfile);

				// 上传
				ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
				if (!zu.uploadZxfile(opid, zxfile)) {
					errorMessage("上传错误", zu.getErrormessage());
					return;
				}

			} catch (Exception e) {
				logger.error("e", e);
				errorMessage("错误", e.getMessage());
				return;
			} finally {
				if (tempfile != null)
					tempfile.delete();
			}
		}

		// 先在class目录下存一个
		String url = this.getClass().getResource(classname + ".class")
				.toString();
		if (url.indexOf("!") < 0) {
			url = url.substring("file:/".length());
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			if (zxmodify) {
				url += classname + "_zx.rule";
			} else {
				url += classname + ".rule";
			}
			File outf = new File(url);
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
			}
		} else {
			// 说明在一个JAR文件中.要找到这个jar文件所在的目录的 ../classes
			if (url.startsWith("jar:"))
				url = url.substring(4);
			if (url.startsWith("file:"))
				url = url.substring(5);

			p = url.indexOf("!");
			File jarfile = new File(url.substring(0, p));
			File dir = jarfile.getParentFile();
			File classdir = new File(dir, "classes");
			if (!classdir.exists())
				classdir.mkdirs();
			url = url.substring(p + 1);
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			if (zxmodify) {
				url += classname + "_zx.rule";
			} else {
				url += classname + ".rule";
			}

			File outf = new File(classdir, url);
			outf.getParentFile().mkdirs();
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
			}
		}

		if (!zxmodify) {
			classname = this.getClass().getName();
			classname = classname.replaceAll("\\.", "/");
			File outdir = new File("src");
			File outf = new File(outdir.getPath() + "/" + classname + ".rule");
			outf.getParentFile().mkdirs();
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
				errorMessage("save rule", "保存规则设置失败");
			}
		}
	}

	/**
	 * 自检函数
	 */
	protected void doSelfcheck() {
		String s = selfCheck();
		if (s == null || s.length() == 0)
			return;
		CDefaultProgress prop = new CDefaultProgress(this.getParentFrame());
		prop.appendMessage("自检ERROR:\r\n");
		prop.messageBox("", s);
		prop.show();
	}

	/**
	 * 功能退出
	 */
	protected void doExit() {
		if (dbmodel == null)
			return;

		if (0 != on_beforeclose()) {
			return;
		}
		if (getDBtableModel().isquerying()) {
			getDBtableModel().stopQuery();
		}
		onstopRun();
		getParentFrame().dispose();
	}

	/**
	 * 导出前。返回0可以继续导出。
	 * 
	 * @return
	 */
	protected int on_beforeexport() {
		return 0;
	}

	/**
	 * 导出excel
	 */
	protected void doExport() {
		if (on_beforeexport() != 0)
			return;
		setWaitCursor();
		File outdir = new File("输出EXCEL");
		outdir.mkdirs();
		File outf = new File(outdir, getTitle() + ".xls");
		try {
			if (usecrosstable) {
				ExcelHelper.writeExcel(outf, getTitle(), crosstable);
			} else {
				ExcelHelper.writeExcel(outf, getTitle(), table);
			}
			ExportinfoDlg dlg = new ExportinfoDlg(outf);
			dlg.pack();
			dlg.setVisible(true);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("输出错误", e.getMessage());
		} finally {
			setDefaultCursor();
		}
	}

	public void doExportas() {
		if (on_beforeexport() != 0)
			return;
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new ExcelFileFilter());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		fc.setSelectedFile(new File(getTitle() + "_"
				+ df.format(new java.util.Date())));
		if (fc.showSaveDialog(this.getParentFrame()) != JFileChooser.APPROVE_OPTION)
			return;
		File outf = fc.getSelectedFile();
		if (!outf.getName().toLowerCase().endsWith(".xls")) {
			outf = new File(outf.getParentFile(), outf.getName() + ".xls");
		}
		setWaitCursor();
		try {
			if (usecrosstable) {
				ExcelHelper.writeExcel(outf, getTitle(), crosstable);
			} else {
				ExcelHelper.writeExcel(outf, getTitle(), table);
			}
			ExportinfoDlg dlg = new ExportinfoDlg(outf);
			dlg.pack();
			dlg.setVisible(true);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("输出错误", e.getMessage());
		} finally {
			setDefaultCursor();
		}
	}

	class ExportinfoDlg extends CDialog {
		File outf = null;

		public ExportinfoDlg(File outf) {
			super(getParentFrame(), "导出成功", true);
			this.outf = outf;
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			CLabel lb = new CLabel("输出成功, 文件:" + outf.getAbsolutePath());
			cp.add(lb, BorderLayout.CENTER);

			JPanel bottomp = new JPanel();
			cp.add(bottomp, BorderLayout.SOUTH);

			CButton btn = new CButton("关闭");
			btn.setActionCommand("close");
			btn.addActionListener(this);
			bottomp.add(btn);

			btn = new CButton("打开目录");
			btn.setActionCommand("opendir");
			btn.addActionListener(this);
			bottomp.add(btn);

			localCenter();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("close")) {
				dispose();
			} else if (e.getActionCommand().equals("opendir")) {
				String cmd = "explorer \""
						+ outf.getParentFile().getAbsolutePath() + "\"";
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();
			}
		}
	}

	/**
	 * 弹出选择功能的菜单
	 * 
	 * @param x
	 * @param y
	 */
	protected void on_selectop(int x, int y) {
		JPopupMenu selectopMenu = this.createSelectopMenu();
		selectopMenu.show(this.getParentFrame(), x, y);
	}

	/**
	 * 打印设置
	 */
	protected void printSetup() {
		if (printsetupfrm == null) {
			printsetupfrm = new PrintSetupFrame(opid, zxmodify, this, this);
		}
		printsetupfrm.pack();
		printsetupfrm.setVisible(true);
	}

	/**
	 * 开始打印.如果没有打印方案,进入打印设置窗口
	 */
	protected void print() {
		logger.info("开始打印");
		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}
		String configfilename = classname + ".properties";

		File configf = new File("conf/" + configfilename);

		Configer config = new Configer(configf);
		String reportname = config.get("print.reportname");
		logger.info("配置文件" + configf.getPath() + ",取打印参数," + ",reportname="
				+ reportname);

		boolean needsetup = false;
		needsetup = reportname == null || reportname.length() == 0;
		if (needsetup) {
			printSetup();
			return;
		}
		// 加载报表,打印
		if (printsetupfrm == null) {
			printsetupfrm = new PrintSetupFrame(opid, zxmodify, this, this);
		}
		printsetupfrm.pack();

		logger.info("调用发送到打印机doPrint()");
		boolean ret = printsetupfrm.doPrint(reportname);
		if (!ret) {
			printsetupfrm.setVisible(true);
		}

	}

	/**
	 * 记录鼠标右键位置的监听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class ScrollpaneListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (MouseEvent.BUTTON3 == e.getButton()) {
				mousex = e.getX();
				mousey = e.getY();
				on_rclick(-1, -1);
			}
		}

		public void mousePressed(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseExited(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	/**
	 * 弹出信息对话框
	 * 
	 * @param title
	 * @param msg
	 */
	protected void infoMessage(String title, String msg) {
		if (DefaultNPParam.isservletapp) {
			logger.info(title + ":" + msg);
			return;
		}

		CMessageDialog.infoMessage(getParentFrame(), title, msg);
		/*
		 * JOptionPane.showMessageDialog(getParentFrame(), msg, title,
		 * JOptionPane.INFORMATION_MESSAGE);
		 *//*
			 * if (steformwindow.isVisible()) {
			 * JOptionPane.showMessageDialog(steformwindow, msg, title,
			 * JOptionPane.INFORMATION_MESSAGE); } else {
			 * JOptionPane.showMessageDialog(getParentFrame(), msg, title,
			 * JOptionPane.INFORMATION_MESSAGE); }
			 */// CMessageBox.infoMessage(this,title,msg);
	}

	/**
	 * 弹出错误信息对话框
	 * 
	 * @param title
	 * @param msg
	 */
	protected void errorMessage(String title, String msg) {
		logger.error(title + ":" + msg);
		if (DefaultNPParam.isservletapp) {
			return;
		}
		// JOptionPane.showMessageDialog(getParentFrame(), msg, title,
		// JOptionPane.ERROR_MESSAGE);
		CMessageDialog.errorMessage(getParentFrame(), title, msg);

		/*
		 * if (steformwindow.isVisible()) {
		 * JOptionPane.showMessageDialog(steformwindow, msg, title,
		 * JOptionPane.ERROR_MESSAGE); } else {
		 * JOptionPane.showMessageDialog(getParentFrame(), msg, title,
		 * JOptionPane.ERROR_MESSAGE); }
		 */
	}

	/**
	 * 弹出警告信息对话框
	 * 
	 * @param title
	 * @param msg
	 */
	protected void warnMessage(String title, String msg) {
		logger.warn(title + ":" + msg);
		if (DefaultNPParam.isservletapp) {
			return;
		}
		CMessageDialog.warnMessage(getParentFrame(), title, msg);
		/*
		 * JOptionPane.showMessageDialog(getParentFrame(), msg, title,
		 * JOptionPane.WARNING_MESSAGE);
		 */
		/*
		 * if (steformwindow.isVisible()) {
		 * JOptionPane.showMessageDialog(steformwindow, msg, title,
		 * JOptionPane.WARNING_MESSAGE); } else {
		 * JOptionPane.showMessageDialog(getParentFrame(), msg, title,
		 * JOptionPane.WARNING_MESSAGE); }
		 */
	}

	/**
	 * 设置数据源某项的值
	 * 
	 * @param row
	 *            行
	 * @param colname
	 *            列名
	 * @param value
	 *            设的值
	 */
	public void setItemValue(int row, String colname, String value) {
		getDBtableModel().setItemValue(row, colname, value);
		if (EventQueue.isDispatchThread()) {
			tableChanged(row);
			if (row == currow) {
				bindDataSetEnable(row);
			}
		} else {
			final int tmp_row = row;
			final int tmp_currow = this.currow;
			Runnable r = new Runnable() {
				public void run() {
					tableChanged(tmp_row);
					if (tmp_row == tmp_currow) {
						bindDataSetEnable(tmp_row);
					}
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 取数据源某项的值
	 * 
	 * @param row
	 *            行
	 * @param colname
	 *            列名
	 * @return 数据源的值
	 */
	public String getItemValue(int row, String colname) {
		return getDBtableModel().getItemValue(row, colname);
	}

	/**
	 * 返回某列的编辑控件
	 * 
	 * @param colname
	 * @return
	 */
	public JComponent getEditCompont(String colname) {
		return getDBtableModel().getEditComp(colname);
	}

	/**
	 * 打开列编辑窗口,编辑界面
	 */
	public void setupUI() {
		// 调用getQuerycond() 是为了生成dbcolumndisplayinfo中的子查询功能id 091119
		getQuerycond();
		tableColumnwidth2Dbmodel();
		DesignFrame designfrm = new DesignFrame();
		designfrm.doDesign(this);
	}

	/**
	 * 保存界面设置
	 */
	public void saveUI() {
		if (zxmodify) {
			// 要存在classes/专项开发/opid.zip的文件中，并上传服务器。
			File dir = CurrentdirHelper.getZxdir();
			File zxfile = new File(dir, opid + ".zip");
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".model");
				DBColumnInfoStoreHelp.writeFile(this, tempfile);
				ZipHelper.replaceZipfile(zxfile, modelnameinzxzip, tempfile);

				// 上传
				ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
				if (!zu.uploadZxfile(opid, zxfile)) {
					errorMessage("上传错误", zu.getErrormessage());
					return;
				}

			} catch (Exception e) {
				logger.error("e", e);
				errorMessage("错误", e.getMessage());
				return;
			} finally {
				if (tempfile != null)
					tempfile.delete();
			}
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p > 0) {
			classname = classname.substring(p + 1);
		}

		File outf = null;
		// 先在class目录下存一个
		String url = this.getClass().getResource(classname + ".class")
				.toString();
		if (url.indexOf("!") < 0) {
			url = url.substring("file:/".length());
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			if (zxmodify) {
				url += classname + "_zx.model";
			} else {
				url += classname + ".model";
			}
			outf = new File(url);
		} else {
			// 说明在一个JAR文件中.要找到这个jar文件所在的目录的 ../classes
			if (url.startsWith("jar:"))
				url = url.substring(4);
			if (url.startsWith("file:"))
				url = url.substring(5);

			p = url.indexOf("!");
			File jarfile = new File(url.substring(0, p));
			File dir = jarfile.getParentFile();
			File classdir = new File(dir, "classes");
			if (!classdir.exists())
				classdir.mkdirs();
			url = url.substring(p + 1);
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			if (zxmodify) {
				url += classname + "_zx.model";
			} else {
				url += classname + ".model";
			}

			outf = new File(classdir, url);
			outf.getParentFile().mkdirs();
		}
		try {
			DBColumnInfoStoreHelp.writeFile(this, outf);
		} catch (Exception e) {
			logger.error("saveUI", e);
		}

		if (!zxmodify) {
			classname = this.getClass().getName();
			classname = classname.replaceAll("\\.", "/");
			File outdir = new File("src");
			outf = new File(outdir.getPath() + "/" + classname + ".model");
			outf.getParentFile().mkdirs();
			try {
				DBColumnInfoStoreHelp.writeFile(this, outf);
				infoMessage("界面保存成功", "路径：" + outf.getPath());
			} catch (Exception e) {
				logger.error("saveUI", e);
				errorMessage("保存失败", "保存界面设置失败");
			}
		}
	}

	/**
	 * 返回表格table的列的次序
	 * 
	 * @return
	 */
	public String[] getTableColumns() {
		ArrayList ar = new ArrayList();
		TableColumnModel cm = table.getColumnModel();
		Enumeration<TableColumn> en = cm.getColumns();
		while (en.hasMoreElements()) {
			TableColumn column = en.nextElement();
			int mindex = column.getModelIndex();
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(mindex);
			ar.add(colinfo.getColname());
		}

		String names[] = new String[ar.size()];
		ar.toArray(names);
		return names;
	}

	/**
	 * 生成缺省的可打印的报表
	 * 
	 * @return
	 */
	public PReport createDefaultReport() {
		/*
		 * DBTableModel dbmodel = null; if(this.usecrosstable){
		 * dbmodel=this.crossdbmodel; }else{ dbmodel=this.getDBtableModel(); }
		 * for (int i = 0; i < this.getRowCount(); i++) {
		 * dbmodel.setItemValue(i, 0, String.valueOf(i + 1)); }
		 */BasicReport rpt = new BasicReport();
		try {
			rpt.createDefaultReport(this);
		} catch (Exception e) {
			logger.error("create report", e);
			return null;
		}
		return rpt;
	}

	/**
	 * 返回表格是否可以编辑
	 * 
	 * @return
	 */
	public boolean isTableeditable() {
		return tableeditable;
	}

	/**
	 * 设置表格是否可以编辑
	 * 
	 * @param tableeditable
	 */
	public void setTableeditable(boolean tableeditable) {
		this.tableeditable = tableeditable;
		if (table == null) {
			return;
		}
		if (!tableeditable) {
			table.setReadonly(true);
		} else {
			table.setReadonly(false);
			// 设置编辑器
			TableColumnModel newcm = table.getColumnModel();
			for (int c = 0; c < newcm.getColumnCount(); c++) {
				TableColumn tablecolumn = newcm.getColumn(c);
				int modelIndex = tablecolumn.getModelIndex();
				DBColumnDisplayInfo colinfo = formcolumndisplayinfos
						.elementAt(modelIndex);
				// if (colinfo.getColtype().equals("行号")) {
				// continue;
				// }
				JComponent editcomp = colinfo.getEditComponent();
				removeEnterkey(editcomp);

				Tablecelleditor cCellEditor = null;
				if (editcomp instanceof JTextField) {
					cCellEditor = new Tablecelleditor((JTextField) editcomp,
							colinfo);
				} else if (editcomp instanceof JComboBox) {
					cCellEditor = new Tablecelleditor((JComboBox) editcomp,
							colinfo);
				} else if (editcomp instanceof CTextArea) {
					continue;
				} else if (editcomp instanceof CCheckBox) {
					cCellEditor = new Tablecelleditor((CCheckBox) editcomp,
							colinfo);
				}

				cCellEditor.setClickCountToStart(1);
				tablecolumn.setCellEditor(cCellEditor);
			}
		}
	}

	/**
	 * 表格table的编辑器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class Tablecelleditor extends DefaultCellEditor {
		protected DBColumnDisplayInfo colinfo = null;
		int editorcurrow = -100;
		int editorcurcol = -100;

		public int getCurrow() {
			return editorcurrow;
		}

		public int getCurcol() {
			return editorcurcol;
		}

		public Tablecelleditor(JTextField textField, DBColumnDisplayInfo colinfo) {
			super(textField);
			/*
			 * 一定要去掉基类的delegate
			 */
			textField.removeActionListener(delegate);
			delegate = new MyDelegate();
			textField.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(1);
		}

		public Tablecelleditor(JCheckBox checkBox, DBColumnDisplayInfo colinfo) {
			super(checkBox);
			/*
			 * 一定要去掉基类的delegate
			 */
			checkBox.removeActionListener(delegate);
			delegate = new MyDelegate();
			checkBox.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(1);
		}

		public Tablecelleditor(JComboBox comboBox, DBColumnDisplayInfo colinfo) {
			super(comboBox);
			/*
			 * 一定要去掉基类的delegate
			 */
			comboBox.removeActionListener(delegate);

			delegate = new MyDelegate();
			comboBox.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(1);
		}

		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) anEvent;
				int clickrow = table
						.rowAtPoint(new Point(me.getX(), me.getY()));
				if (clickrow < 0 || clickrow >= table.getRowCount() - 1) {
					return false;
				}
			}
			if (table.isQuerying()) {
				return false;
			}

			// 如果表的列正在被拖动或变列宽,不能编辑.
			JTableHeader th = table.getTableHeader();
			TableColumn tcr = th.getResizingColumn();
			TableColumn tcd = th.getDraggedColumn();
			// System.out.println("isCellEditable col="+colinfo.getColname());
			// System.out.println("table resize c="+tcr + ",table drag c="+tcd);

			if (tcr != null || tcd != null) {
				return false;
			}
			if (colinfo.isReadonly())
				return false;
			if (colinfo.isHide())
				return false;
			if (colinfo.getColtype().equals("行号"))
				return false;

			// 判断行。 如果正在查询中,currow总是在变化的.
			int memrow = currow;
			if (memrow < 0)
				return false;
			if (memrow != currow)
				return false;
			int ret = on_beforemodify(memrow);
			if (ret != 0) {
				return false;
			}
			return super.isCellEditable(anEvent); // To change body of
		}

		/**
		 * stopCellEditing()被调用有两种情况. 1
		 * 是在编辑过程中按回车键,认为做为命令处理.先调用invokehov,后调用stopCellediting()
		 * 
		 * 2 另一种是按Tab键或鼠标点击,移动了焦点. 先调用了stopCellediting(),后调用invokehov
		 * 
		 * 对于第2种情况,发现如果使用了hov,stopCelledtiing()返回false;
		 */
		boolean callfromactionperform = false;

		public boolean stopCellEditing() {
			if (callfromactionperform) {
				if (hovshowing) {
					return false;
				}
			} else {
				if (colinfo.isUsehov()) {
					JComponent comp = colinfo.getEditComponent();
					if (comp instanceof CFormatTextField) {
						/*
						 * 这里不应该触发commitEdit() try { ((CFormatTextField)
						 * comp).commitEdit(); } catch (ParseException e) {
						 * logger.error("ERROR", e); return false; }
						 */}
					if (hovshowing) {
						return false;
					}
				}
			}
			return super.stopCellEditing();
		}

		class ComboboxAction extends AbstractAction {
			Action oldaction;

			public ComboboxAction(String name, Action oldaction) {
				super();
				this.oldaction = oldaction;
				super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			}

			public void actionPerformed(ActionEvent e) {
				CComboBox cb = (CComboBox) e.getSource();
				// System.out.println(e.getWhen()+" "+e.getActionCommand()+",cb
				// visible="+cb.isPopupVisible());
				if (cb.isPopupVisible()) {
					oldaction
							.actionPerformed(new ActionEvent(cb, e.getID(), e
									.getActionCommand(), e.getWhen(), e
									.getModifiers()));
					cb.setPopupVisible(false);
					editNext();
					return;
				}
				editNext();
			}
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			/*
			 * JComponent comp = (JComponent) super.getTableCellEditorComponent(
			 * table, "", isSelected, row, column);
			 */
			// Component comp = this.getComponent();
			Component comp = colinfo.getEditComponent();
			editorcurrow = row;
			editorcurcol = column;
			if (comp instanceof CComboBox) {
				CComboBox cb = (CComboBox) comp;
				if (cb.getModel() instanceof CComboBoxModel) {
					CComboBoxModel ccbmodel = (CComboBoxModel) cb.getModel();
					int sindex = ccbmodel.getKeyIndex((String) value);
					if (sindex < 0)
						sindex = 0;
					((CComboBox) comp).setSelectedIndex(sindex);
				}

				// KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				// 0, false);
				// cb.getInputMap(JComponent.WHEN_FOCUSED).put(vkenter,"donothing");
				Action oldaction = cb.getActionMap().get("enterPressed");
				if (!(oldaction instanceof ComboboxAction)) {
					// System.out.println("oldaction="+oldaction);

					cb.getActionMap().put("enterPressed",
							new ComboboxAction("enterPressed", oldaction));

				}

				// 绝不能要cb.getActionMap().put("enterPressed",new
				// SelectNextRowCellAction());

				/*
				 * JRootPane root = SwingUtilities.getRootPane(cb); if (root !=
				 * null) { InputMap im =
				 * root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				 * im.put(vkenter, "selectNextColumnCell"); }
				 */

			} else if (comp instanceof CFormatTextField) {
				try {
					((CFormatTextField) comp).setValue(value);
					// 设置esc键失效
					InputMap im = ((CFormatTextField) comp).getInputMap()
							.getParent();
					if (im != null) {
						// esc用于关闭hov窗口,因此原来的esc的reset-field-edit不要了
						KeyStroke vesc = KeyStroke.getKeyStroke(
								KeyEvent.VK_ESCAPE, 0, false);
						im.remove(vesc);

					}

					// 删除tab向前20070827 by wwh
					KeyStroke tabkey = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
							0, false);
					Set<AWTKeyStroke> focusTraversalKeys = comp
							.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
					HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
							focusTraversalKeys);
					hasset.remove(tabkey);
					comp
							.setFocusTraversalKeys(
									KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
									hasset);

				} catch (Exception e) {
					logger.error("ERROR", e);
					return null;
				}
			} else if (comp instanceof CCheckBox) {
				CCheckBox cb = (CCheckBox) comp;
				if (value instanceof String) {
					cb.setSelected(((String) value).equals("1"));
				}
				// BasicTableUI会多发一次mouse事件
				cb.setMousereleasecount(0);
			} else {
				logger.error("getTableCellEditorComponent(没有处理"
						+ comp.getClass().getName());
			}

			/*
			 * KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0,
			 * false); KeyStroke vkdown =
			 * KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false); InputMap imap
			 * = comp.getInputMap();
			 */return comp;
		}

		/*
		 * public void editNext() { ((MyDelegate) delegate).editNext(); }
		 */
		class MyDelegate extends EditorDelegate {
			public void setValue(Object value) {
				// System.out.println("celledit,setValue
				// currow="+currow+",curcol="+curcol+",value="+value);
				if (editorComponent instanceof JTextField) {
					((JTextField) editorComponent)
							.setText((value != null) ? value.toString() : "");
				} else if (editorComponent instanceof JCheckBox) {
					boolean selected = false;
					if (value instanceof Boolean) {
						selected = ((Boolean) value).booleanValue();
					} else if (value instanceof String) {
						selected = value.equals("1");
					}
					((JCheckBox) editorComponent).setSelected(selected);

				} else {
					((JComboBox) editorComponent).setSelectedItem(value);
				}
			}

			public Object getCellEditorValue() {
				String value = (String) getEditorvalue();
				if (value == null)
					value = "";

				if (editorcurrow >= 0 && editorcurrow < table.getRowCount() - 1) {
					int modelindex = table.getColumnModel().getColumn(
							editorcurcol).getModelIndex();
					DBColumnDisplayInfo colinfo = formcolumndisplayinfos
							.elementAt(modelindex);

					// 如果当前值和数据库的值不一样,才触发on_itemchanged
					int modelcol = table.getColumnModel().getColumn(
							editorcurcol).getModelIndex();
					if (colinfo.getEditcomptype().equals(
							DBColumnDisplayInfo.EDITCOMP_COMBOBOX)) {
						value = colinfo.getComboboxKey(value);
						if (value == null)
							value = "";
					}

					String dbvalue = getDBtableModel().getItemValue(
							editorcurrow, modelcol);
					if (dbvalue == null)
						dbvalue = "";
					if (!dbvalue.equals(value)) {
						getDBtableModel().setItemValue(editorcurrow,
								colinfo.getColname(), value);
						tableChanged(editorcurrow);
						on_itemvaluechange(editorcurrow, colinfo.getColname(),
								value);
					}
				}
				return value;
			}

			Object getEditorvalue() {
				if (editorComponent instanceof JTextField) {
					// System.out.println("celledit,getValue
					// currow="+currow+",curcol="+curcol+",get
					// value="+((JTextField) editorComponent).getText());
					String text = ((JTextField) editorComponent).getText();
					return text;
				} else if (editorComponent instanceof JCheckBox) {
					return ((JCheckBox) editorComponent).isSelected() ? "1"
							: "0";
				} else if (editorComponent instanceof CComboBox) {
					CComboBox ccb = (CComboBox) editorComponent;
					return ((JComboBox) editorComponent).getSelectedItem();
					// return ccb.getValue();
				} else {
					logger.error("unknow editor type=" + editorComponent);
					return "";
				}
			}

			public void actionPerformed(ActionEvent e) {
				// System.out.println("delegate action performed " + e);
				if (e.getActionCommand().equals("comboBoxChanged")) {
					CComboBox cb = (CComboBox) e.getSource();
					cb.setPopupVisible(false);

					return;
					/*
					 * Object cbselectedobj=cb.getSelectedItem();
					 * if(cbselectedobj==null || !(cbselectedobj instanceof
					 * String))return; String v=(String)cbselectedobj;
					 * if(v.length()==0)return;
					 */
				}
				// return;
				callfromactionperform = true;
				editNext();
				callfromactionperform = false;
			}

			void editNext() {
				if (hovshowing)
					return;
				int ecol = table.getEditingColumn();
				int erow = table.getEditingRow();
				if (!Tablecelleditor.this.stopCellEditing())
					return;

				if (editorcurrow != erow || editorcurcol != ecol) {
					return;
				}

				if (table.getRowCount() <= 1) {
					return;
				}
				if (erow < 0) {
					table.editCellAt(0, getFirstEditableColumn(erow));
				} else {
					if (ecol == getLastEditableColumn(erow)) {
						erow++;
						if (erow >= table.getRowCount() - 1) {
							erow = 0;
						}
						ecol = getFirstEditableColumn(erow);
						table.addRowSelectionInterval(erow, erow);
						// System.out.println("edit cell at " + erow + "," +
						// ecol);
						table.editCellAt(erow, ecol);
					} else {
						// 找下一个可编辑的列
						TableColumnModel tm = table.getColumnModel();
						for (int c = ecol + 1; c < tm.getColumnCount(); c++) {
							TableColumn tc = tm.getColumn(c);
							DBColumnDisplayInfo cinfo = formcolumndisplayinfos
									.elementAt(tc.getModelIndex());
							if (cinfo.getColtype().equals("行号")
									|| cinfo.isHide() || cinfo.isReadonly()
									|| !table.isCellEditable(erow, c)) {
								continue;
							}
							// System.out.println("edit next cell at " + erow
							// + "," + c);
							table.editCellAt(erow, c);
							break;
						}

					}
				}
			}

		}

	}

	/**
	 * 在表格可编辑状态下,取第一个可以编辑的列在table中的列序
	 * 
	 * @return
	 */
	public int getFirstEditableColumn(int row) {
		TableColumnModel cm = table.getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn colm = cm.getColumn(i);
			int mindex = colm.getModelIndex();
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(mindex);
			if (colinfo.getColtype().equals("行号") || colinfo.isReadonly()
					|| colinfo.isHide()
					|| !table.isCellEditable(row, colinfo.getColname())) {
				continue;
			}
			return i;
		}
		return -1;
	}

	/**
	 * 在表格可编辑的状态下,返回最后一个可编辑列的列序
	 * 
	 * @return
	 */
	public int getLastEditableColumn(int row) {
		TableColumnModel cm = table.getColumnModel();
		for (int i = cm.getColumnCount() - 1; i >= 0; i--) {
			TableColumn colm = cm.getColumn(i);
			int mindex = colm.getModelIndex();
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(mindex);

			if (colinfo.getColtype().equals("行号") || colinfo.isReadonly()
					|| colinfo.isHide()
					|| !table.isCellEditable(row, colinfo.getColname())) {
				continue;
			}
			return i;
		}
		return -1;
	}

	/*
	 * class TablecellListener implements CFormatTextFieldListener{
	 * 
	 * public void onchanged(CFormatTextField comp, String value, String
	 * oldvalue) { Enumeration<DBColumnDisplayInfo> en =
	 * formcolumndisplayinfos.elements(); while (en.hasMoreElements()) {
	 * DBColumnDisplayInfo colinfo = en.nextElement();
	 * if(colinfo.getEditComponent()==comp){
	 * colinfo.invokeHov(value,oldvalue,""); break; } } }
	 * 
	 * public boolean isHov(String editorname) {
	 * Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
	 * while (en.hasMoreElements()) { DBColumnDisplayInfo colinfo =
	 * en.nextElement(); if(colinfo.getColname().equals(editorname)){ return
	 * colinfo.isUsehov(); } } return false; }
	 * 
	 * public void invokeHov(String editorname, String newvalue, String
	 * oldvalue) { }
	 * 
	 * public boolean confirmHov(String editorname, String newvalue, String
	 * oldvalue) { return false; //To change body of implemented methods use
	 * File | Settings | File Templates. } }
	 */

	/**
	 * 当前是否有hov正在显示?
	 */
	protected boolean hovshowing = false;

	/**
	 * 如果现在有hov显示,触发hov的列名
	 */
	protected String hovcolname = null;

	/**
	 * 设置当前hov正在显示与否
	 * 
	 * @param colname
	 *            触发hov的列
	 * @param show
	 *            true:hov正在显示 false:hov已隐藏
	 */
	public void setHovshowing(String colname, boolean show) {
		hovshowing = show;
		hovcolname = colname;

		InputMap map = table.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();

		/*
		 * KeyStroke keys[]=map.allKeys(); for(int i=0;i<keys.length;i++){
		 * System.out.println(keys[i]+"==>"+map.get(keys[i])); }
		 */
		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		if (show) {
			map.put(vkenter, null);
			map.put(vktab, null);
			table.setDisablemouseevent(true);
		} else {
			map.put(vkenter, "selectNextColumnCell");
			map.put(vktab, "selectNextColumnCell");
			table.setDisablemouseevent(false);
		}

		/*
		 * KeyStroke keys[] = map.allKeys(); for (int i = 0; i < keys.length;
		 * i++) { System.out.println(keys[i] + "==>" + map.get(keys[i])); }
		 * 
		 * int m; m = 3;
		 */
		/*
		 * enableTableUpdownkey(!show); if (!show) { int modelindex = 0;
		 * Enumeration<DBColumnDisplayInfo> en = this.formcolumndisplayinfos
		 * .elements(); for (int i = 0; en.hasMoreElements(); i++) {
		 * DBColumnDisplayInfo info = en.nextElement(); if
		 * (info.getColname().equals(colname)) { modelindex = i; break; } }
		 * 不应该stopedit by wwh 20070816 TableColumnModel cm =
		 * table.getColumnModel(); for (int c = 0; c < cm.getColumnCount(); c++)
		 * { TableColumn column = cm.getColumn(c); if (column.getModelIndex() ==
		 * modelindex) { TableCellEditor cellEditor = column.getCellEditor(); if
		 * (cellEditor != null) { cellEditor.stopCellEditing(); } break; } } }
		 */

	}

	/**
	 * @deprecated
	 */
	boolean inputmapcopyed = false;

	/**
	 * 在表格窗口弹出hov后，禁止table上的上下键 private void enableTableUpdownkey(boolean
	 * enable) { KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
	 * 0, false);
	 * 
	 * KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
	 * KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
	 * KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
	 * 
	 * if (!inputmapcopyed) { inputmapcopyed = true; InputMap orgmap =
	 * table.getInputMap(
	 * JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent(); vkupaction =
	 * orgmap.get(vkup); vkdownaction = orgmap.get(vkdown); InputMap tmpmap =
	 * new InputMap(); KeyStroke[] keys = orgmap.keys(); for (int i = 0; i <
	 * keys.length; i++) { tmpmap.put(keys[i], orgmap.get(keys[i]));
	 * System.out.println(keys[i]+" ==> "+orgmap.get(keys[i])); }
	 * table.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tmpmap);
	 * tablescrollpane
	 * .setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tmpmap); }
	 * 
	 * InputMap map = table.getInputMap(
	 * JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	 * 
	 * if (!enable) { map.put(vkup, "nothing"); map.put(vkdown, "nothing");
	 * map.put(vkenter, "nothing");
	 * 
	 * //去掉ESCAPE处理 JComponent editcomp = (JComponent)
	 * table.getEditorComponent(); if(editcomp!=null){ InputMap compinputmap =
	 * editcomp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	 * Object action = compinputmap.get(vkesc); System.out.println("editcomp
	 * escapte action="+action); } } else { map.put(vkup, vkupaction);
	 * map.put(vkdown, vkdownaction);
	 * 
	 * if (tableeditable) { // 回车为中止当前的编辑 map.put(vkenter, "tableenterkey");
	 * table.getActionMap().put("tableenterkey", new
	 * TableenterkeyAction("tableenterkey")); } else { // 回车往前 map.put(vkenter,
	 * "selectNextColumnCell"); } } }
	 */

	/**
	 * 表格table按了回车键对应的action
	 */
	class TableenterkeyAction extends AbstractAction {

		public TableenterkeyAction(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println("table enter key");
			TableCellEditor ce = table.getCellEditor();
			if (ce instanceof Tablecelleditor) {
				Tablecelleditor myce = (Tablecelleditor) ce;
				// myce.editNext();
				editNext();
			}
		}
	}

	/**
	 * 撤消控件按回车键的action
	 * 
	 * @param comp
	 */
	private void removeEnterkey(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		hasset.remove(vktab);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);

	}

	/**
	 * 表格table的列序定义
	 */
	protected String[] tablecolumns = null;

	/**
	 * 设置表格的列序
	 * 
	 * @param ss
	 */
	public void setTableColumns(String[] ss) {
		tablecolumns = ss;
		/*
		 * if (tabledelegate != null) { // 如果专项设置了新的列序,以专项为准. String tmps[] =
		 * tabledelegate .on_setTableColumns(this, tablecolumns); if (tmps !=
		 * null) { tablecolumns = tmps; } }
		 */}

	/**
	 * 取表格显示的颜色
	 * 
	 * @param row
	 * @param col
	 * @return null为黑色
	 */
	protected Color getCellColor(int row, int col) {
		if (ruleeng != null) {
			Color c = ruleeng.processColor(this, "表格数据颜色", row);
			if (c != null)
				return c;
		}
		return null;
	}

	/**
	 * 表格table的显示,重载本类可以改变单元格的显示特性
	 * 
	 * @author Administrator
	 * 
	 */
	protected class PlainTablecellRender extends DefaultTableCellRenderer {
		protected JLabel lbnormal = null;
		protected JLabel lbbold = null;

		protected DBColumnDisplayInfo colinfo = null;

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
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			RecordTrunk rec = dbmodel.getRecordThunk(row);
			if (rec.getSumflag() == RecordTrunk.SUMFLAG_SUMMARY) {
				lb = lbbold;
			} else {
				lb = lbnormal;
			}

			int modelIndex = table.getColumnModel().getColumn(column)
					.getModelIndex();
			/*
			 * DBColumnDisplayInfo colinfo = formcolumndisplayinfos
			 * .elementAt(modelIndex);
			 */
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
				if (getCellbgcolor(row, column) != null) {
					lb.setBackground(getCellbgcolor(row, column));
				} else {
					if (row % 2 == 0) {
						lb.setBackground(table.getBackground());
					} else {
						lb.setBackground(secondbackcolor);
					}
				}
				if (tableeditable) {
					if (!table.isCellEditable(row, column)) {
						lb.setBackground(readonlybackcolor);
					}
				} else {
					// 表格不能编辑
					if (!isColumneditable(row, colinfo.getColname())) {
						lb.setBackground(readonlybackcolor);
					}
				}
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
				DBTableModel cbdbmodel = null;
				CComboBoxModel cbmodel = dbmodel.getRecordThunk(row)
						.getColumnddlmodel(colinfo.getColname());
				if (cbmodel != null) {
					cbdbmodel = cbmodel.getDbmodel();
				}

				if (cbdbmodel == null) {
					cbdbmodel = colinfo.getCbdbmodel();
				}
				if (cbdbmodel != null) {
					boolean bfind = false;
					for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
						if (cbdbmodel.getItemValue(r, 0).equals(newvalue)) {
							newvalue = cbdbmodel.getItemValue(r, 1);
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
				if (value != null && value instanceof String) {
					String strv = (String) value;
					if (strv.length() > 0) {
						newvalue = "有附件";
					}
				}
			}

			lb.setText(newvalue);

			Color c = getCellColor(row, column);
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
	 * 表格table当前行选择监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class Tableselectionlistener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// 当前选中的行是
			if (e.getValueIsAdjusting())
				return;
			if (table.isTablechanging()) {
				// 因为JTable.tableChanged()引起，不要理
				return;
			}
			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			int newrow = dm.getAnchorSelectionIndex();
			if (newrow >= 0 && newrow < dbmodel.getRowCount()) {
				on_click(newrow, curcol);
				on_tablerowchanged(newrow, curcol, currow, curcol);
			}
			// 调用tablechanged()后，dm.getAnchorSelectionIndex()返回-1,这不是本意
			/*
			 * if (newrow >= 0) { currow = dm.getAnchorSelectionIndex(); } else
			 * { if (dbmodel.getRowCount() == 0) { currow = -1; } }
			 */
			/*
			 * if (tableeditable && currow >= 0 && currow <
			 * dbmodel.getRowCount()) { bindDataSetEnable(currow); }
			 * 
			 * if (dbmodel.getRowCount() == 0) { currow = -1; curcol = -1; }
			 * on_Focuscell();
			 */
		}

	}

	/**
	 * 表格列被选中的监听器
	 * 
	 * @author Administrator
	 * 
	 *         class Tablecolumnmodellistener implements
	 *         TableColumnModelListener {
	 * 
	 *         public void columnAdded(TableColumnModelEvent e) { }
	 * 
	 *         public void columnMarginChanged(ChangeEvent e) { }
	 * 
	 *         public void columnMoved(TableColumnModelEvent e) { }
	 * 
	 *         public void columnRemoved(TableColumnModelEvent e) { }
	 * 
	 *         public void columnSelectionChanged(ListSelectionEvent e) { } }
	 */

	/**
	 * 编辑表格第r行第c列.
	 * 
	 * @param r
	 * @param c
	 * @return true编辑成功 false中止当前的编辑器失败
	 */
	protected boolean editCellAt(int r, int c) {
		if (table.getCellEditor() != null) {
			if (!table.getCellEditor().stopCellEditing()) {
				return false;
			}
		}
		table.editCellAt(r, c);
		return true;
	}

	/**
	 * 编辑表格状态下编辑下一个
	 */
	protected void editNext() {
		int erow = currow;
		int ecol = curcol;

		if (table.getRowCount() <= 1) {
			return;
		}

		if (erow < 0 || curcol < 0) {
			return;
		}

		DBColumnDisplayInfo cinfo = null;

		cinfo = formcolumndisplayinfos.elementAt(ecol);
		JComponent editcomp = cinfo.getEditComponent();
		if (editcomp instanceof CFormatTextField) {
			try {
				((CFormatTextField) editcomp).commitEdit();
			} catch (ParseException e) {
				return;
			}
		}

		if (hovshowing)
			return;
		commitEdit();
		if (erow == table.getRowCount() - 1) {
			// 说明到了合计行,要跳到第0行.
			editCellAt(0, getFirstEditableColumn(erow));
			return;
		}

		if (erow < 0) {
			editCellAt(0, getFirstEditableColumn(erow));
			return;
		} else {
			if (ecol >= getLastEditableColumn(erow)) {
				erow++;
				if (erow >= table.getRowCount() - 1) {
					erow = 0;
				}
				ecol = getFirstEditableColumn(erow);
				if (ecol >= 0) {
					editCellAt(erow, ecol);
				}
			} else {
				// 找下一个可编辑的列
				TableColumnModel tm = table.getColumnModel();
				for (int c = ecol + 1; c < tm.getColumnCount(); c++) {
					TableColumn tc = tm.getColumn(c);
					cinfo = formcolumndisplayinfos
							.elementAt(tc.getModelIndex());
					if (cinfo.getColtype().equals("行号") || cinfo.isHide()
							|| cinfo.isReadonly()
							|| !table.isCellEditable(erow, c)) {
						continue;
					}
					// System.out.println("edit next cell at " + erow
					// + "," + c);
					if (c >= 0) {
						editCellAt(erow, c);
					}
					break;
				}

			}
		}
	}

	protected void editPrevious() {
		if (hovshowing)
			return;
		int erow = currow;
		int ecol = curcol;

		if (table.getRowCount() <= 1) {
			return;
		}

		if (erow < 0 || curcol < 0) {
			return;
		}

		DBColumnDisplayInfo cinfo = formcolumndisplayinfos.elementAt(ecol);
		JComponent editcomp = cinfo.getEditComponent();
		if (editcomp instanceof CFormatTextField) {
			try {
				((CFormatTextField) editcomp).commitEdit();
			} catch (ParseException e) {
				return;
			}
		}

		if (hovshowing)
			return;

		if (erow < 0) {
			editCellAt(0, getFirstEditableColumn(erow));
			return;
		} else {
			if (ecol <= getFirstEditableColumn(erow)) {
				erow--;
				if (erow < 0) {
					erow = table.getRowCount() - 1 - 1;
				}
				ecol = getLastEditableColumn(erow);
				editCellAt(erow, ecol);
			} else {
				// 找上一个可编辑的列
				TableColumnModel tm = table.getColumnModel();
				for (int c = ecol - 1; c >= 0; c--) {
					TableColumn tc = tm.getColumn(c);
					cinfo = formcolumndisplayinfos
							.elementAt(tc.getModelIndex());
					if (cinfo.getColtype().equals("行号") || cinfo.isHide()
							|| cinfo.isReadonly()
							|| !table.isCellEditable(erow, c)) {
						continue;
					}
					if (c >= 0) {
						editCellAt(erow, c);
					}
					break;
				}
			}
		}
	}

	/**
	 * 单元格被选中.currow,curcol发生了变化
	 * 
	 * protected void on_Focuscell() {
	 * 
	 * if (curcol < 0 && tableeditable) { return; }
	 * 
	 * on_tablerowchanged(currow, curcol, -1, -1);
	 * 
	 * if (!tableeditable) { return; }
	 * 
	 * int modelindex = table.getColumnModel().getColumn(curcol)
	 * .getModelIndex(); DBColumnDisplayInfo colinfo = formcolumndisplayinfos
	 * .elementAt(modelindex); if (colinfo.isHide() || colinfo.isReadonly() ||
	 * colinfo.getColtype().equals("行号")) { // editNext(); return; }
	 * 
	 * if (tableeditable && currow >= 0 && curcol >= 0) { TableCellEditor tce =
	 * table.getCellEditor(); if (tce == null) { table.editCellAt(currow,
	 * curcol); } else if (tce instanceof Tablecelleditor) { Tablecelleditor ce
	 * = (Tablecelleditor) tce; if (currow != ce.getCurrow() || curcol !=
	 * ce.getCurcol()) { if (table.getCellEditor().stopCellEditing()) {
	 * table.editCellAt(currow, curcol); } } } } // System.out.println("current
	 * row,col=" + currow + "," + curcol); }
	 */

	/**
	 * 内部使用
	 */
	Action priorrowaction = null;
	/**
	 * 内部使用
	 */
	Action nextrowaction = null;
	/**
	 * 内部使用
	 */
	Action cancelaction = null;
	/**
	 * 内部使用
	 */
	Action nextcellaction = null;

	/**
	 * 内部使用,记录table初始的action
	 */
	protected void registryTableaction(CTable table) {
		ActionMap am = table.getActionMap();
		priorrowaction = am.get("selectPreviousRow");
		nextrowaction = am.get("selectNextRow");
		cancelaction = am.get("cancel");
		nextcellaction = am.get("selectNextColumnCell"); // 回车

		am.put("selectPreviousRow", new TableAction("selectPreviousRow"));
		am.put("selectNextRow", new TableAction("selectNextRow"));
		am.put("cancel", new TableAction("cancel"));
		am.put("selectNextColumnCell", new TableAction("selectNextColumnCell"));

	}

	/**
	 * 表格table的action
	 * 
	 * @author Administrator
	 * 
	 */
	class TableAction extends AbstractAction {
		public TableAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("selectPreviousRow")) {
				if (hovshowing) {
					/*
					 * DBColumnDisplayInfo colinfo =
					 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
					 * //colinfo.getHov().priorRow();
					 */} else {
					priorrowaction.actionPerformed(e);
				}
			} else if (cmd.equals("selectNextRow")) {
				if (hovshowing) {
					/*
					 * DBColumnDisplayInfo colinfo =
					 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
					 * //colinfo.getHov().nextRow();
					 */} else {
					nextrowaction.actionPerformed(e);
				}
			} else if (cmd.equals("cancel")) {
				;// donothing
				/*
				 * if(hovshowing){ DBColumnDisplayInfo colinfo =
				 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
				 * colinfo.getHov().hide(); }else{
				 * cancelaction.actionPerformed(e); }
				 */
			} else if (cmd.equals("selectNextColumnCell")) {
				if (hovshowing) {
					int m;
					m = 3;
				} else {
					nextcellaction.actionPerformed(e);
				}
			}
		}

	}

	/**
	 * 生成第二工具条
	 * 
	 * @return
	 */
	protected JPanel createSecondtoolbar() {
		return null;
	}

	/**
	 * 当前运行的功能发生了变化
	 */
	public void onRunopschanged(String[] opnames) {
		super.onRunopschanged(opnames);
		if (toolbar != null) {
			toolbar.setRunopnames(opnames);
		}
	}

	// //////////////////////////////////专项开发支持
	// 20070825//////////////////////////

	/**
	 * 初始化专项程序类
	 */
	protected void initInitdelegate() {
		zxdelegate = ZxstejavaDelegate.loadZxfromzxzip(this);

		InitDelegate inst = SpecialProjectManager.loadInitDelegate(this);
		if (inst != null) {
			initdelegate = inst;
		}
		TableDelegate tableinst = SpecialProjectManager.loadTableDelegate(this);
		if (tableinst != null) {
			tabledelegate = tableinst;
		} else {
			tabledelegate = new DefaultTableDelegate();
		}

		FormDelegate forminst = SpecialProjectManager.loadFormDelegate(this);
		if (forminst != null) {
			formdelegate = forminst;
		}
		QueryDelegate queryinst = SpecialProjectManager.loadQueryDelegate(this);
		if (queryinst != null) {
			querydelegate = queryinst;
		}

		EditDelegate editinst = SpecialProjectManager.loadEditDelegate(this);
		if (editinst != null) {
			editdelegate = editinst;
		}

		ActionDelegate actioninst = SpecialProjectManager
				.loadActionDelegate(this);
		if (actioninst != null) {
			actiondelegate = actioninst;
		}
	}

	/**
	 * 初始化专项类
	 */
	protected InitDelegate initdelegate = null;
	/**
	 * table专项类
	 */
	protected TableDelegate tabledelegate = null;

	/**
	 * 卡片专项类
	 */
	protected FormDelegate formdelegate = null;

	/**
	 * 查询专项类
	 */
	protected QueryDelegate querydelegate = null;

	/**
	 * 编辑专项类
	 */
	protected EditDelegate editdelegate = null;

	/**
	 * 命令处理专项类
	 */
	protected ActionDelegate actiondelegate = null;

	/**
	 * 初始化专项
	 */
	public static class InitDelegate {
		/**
		 * CSteModel刚创建成功
		 * 
		 * @param model
		 */
		public void on_init(CSteModel model) {

		}

		/**
		 * 刚创建GUI控件
		 */
		public void on_initControl() {

		}
	}

	/**
	 * 表格CTable的delegate.一般用于调整表格界面
	 * 
	 * @author Administrator
	 * 
	 */
	public static class TableDelegate {
		/**
		 * 在设置列序时,可以重新设定列的次序.增减列
		 * 
		 * @param tablecolumns
		 * @return
		 */
		public String[] on_setTableColumns(CSteModel stemodel,
				String[] tablecolumns) {
			return null;
		}
	}

	/**
	 * 设置卡片的属性
	 * 
	 * @author Administrator
	 * 
	 */
	public static class FormDelegate {

		/**
		 * 在将要生成卡片前窗口,传入单表编辑的formcolumndisplayinfos. 在此可设置列的编辑方法和次序
		 * 
		 * @param formcolumndisplayinfos
		 */
		public void on_createForm(
				Vector<DBColumnDisplayInfo> formcolumndisplayinfos) {
			return;
		}
	}

	/**
	 * 查询条件辅助
	 * 
	 * @author Administrator
	 * 
	 */
	public static class QueryDelegate {

		/**
		 * 在生成查询GUI控件前,对查询条件进行设置
		 * 
		 * @param querycond
		 */
		public Querycond on_query(Querycond querycond) {
			return null;
		}
	}

	/**
	 * 增 删 查 改前的控制
	 * 
	 * @author Administrator
	 * 
	 */
	public static class EditDelegate {
		/**
		 * 按右键
		 * 
		 * @param stemodel
		 * @param row
		 * @param col
		 */
		public void on_rclick(CSteModel stemodel, int row, int col) {

		}

		/**
		 * 双击
		 * 
		 * @param row
		 * @param col
		 */
		public void on_doubleclick(int row, int col) {

		}

		/**
		 * 删除后
		 * 
		 * @param steModel
		 * @param row
		 */
		public void on_del(CSteModel steModel, int row) {

		}

		/**
		 * 删除前
		 * 
		 * @param steModel
		 * @param row
		 * @return
		 */
		public int on_beforedel(CSteModel steModel, int row) {

			return 0;
		}

		/**
		 * 修改后
		 * 
		 * @param steModel
		 * @param row
		 */
		public void on_modify(CSteModel steModel, int row) {
		}

		/**
		 * 修改前
		 * 
		 * @param steModel
		 * @param row
		 * @return
		 */
		public int on_beforemodify(CSteModel steModel, int row) {

			return 0;
		}

		/**
		 * 查询结束
		 * 
		 * @param steModel
		 */
		public void on_retrieved(CSteModel steModel) {

		}

		/**
		 * 值变化
		 * 
		 * @param steModel
		 * @param row
		 * @param colname
		 * @param value
		 */
		public void on_itemvaluechange(CSteModel steModel, int row,
				String colname, String value) {

		}

		/**
		 * 表的当前前变化了
		 * 
		 * @param steModel
		 * @param newrow
		 */
		public void on_tablerowchanged(CSteModel steModel, int newrow) {

		}

		/**
		 * 行检查
		 * 
		 * @param steModel
		 * @param row
		 * @return
		 */
		public int on_checkrow(CSteModel steModel, int row) {

			return 0;
		}

		/**
		 * 窗口关闭前
		 * 
		 * @param steModel
		 * @return
		 */
		public int on_beforeclose(CSteModel steModel) {

			return 0;
		}

		/**
		 * 保存前
		 * 
		 * @param steModel
		 * @return
		 */
		public int on_beforesave(CSteModel steModel) {

			return 0;
		}

		/**
		 * 查询前
		 * 
		 * @param steModel
		 * @return
		 */
		public int on_beforequery(CSteModel steModel) {

			return 0;
		}

		/**
		 * 新增后
		 * 
		 * @param steModel
		 * @param row
		 * @return
		 */
		public int on_new(CSteModel steModel, int row) {

			return 0;
		}

		/**
		 * 新增前
		 * 
		 * @param steModel
		 * @return
		 */
		public int on_beforeNew(CSteModel steModel) {

			return 0;
		}

		/**
		 * 当CSteModel选择hov成功返回
		 * 
		 * @param steModel
		 * @param row
		 * @param colname
		 * @return
		 */
		public int on_hov(CSteModel steModel, int row, String colname) {
			return 0;
		}
	}

	/**
	 * 命令处理专项类
	 * 
	 * @author Administrator
	 * 
	 */
	public static class ActionDelegate {
		/**
		 * 如果本命令能处理返回0,不能返回-1
		 * 
		 * @param command
		 * @return
		 */

		public int on_actionPerformed(CSteModel steModel, String command) {
			return -1;
		}

	}

	/**
	 * 设置光标为"沙漏等待"
	 */
	protected void setWaitCursor() {
		if (tablescrollpane != null && tablescrollpane.isVisible()) {
			Component p = tablescrollpane;

			while ((p = p.getParent()) != null) {
				if (p instanceof Window) {
					((Window) p).setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));
					return;
				}
			}
		}

		if (this.getParentFrame() == null)
			return;
		if (!getParentFrame().isVisible()) {
			return;
		}
		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * 设置光标为缺省箭头
	 */
	protected void setDefaultCursor() {
		if (tablescrollpane != null && tablescrollpane.isVisible()) {
			Component p = tablescrollpane;

			while ((p = p.getParent()) != null) {
				if (p instanceof Window) {
					((Window) p).setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			}
		} else {
			if (Clientframe.getClientframe() != null) {
				Clientframe.getClientframe().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		if (this.getParentFrame() == null)
			return;
		if (!getParentFrame().isVisible()) {
			return;
		}

		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * 实现多选HOV或MDEHOV的函数 如果使用了多选HOV或MDEHOV,必须重载本函数
	 * 
	 * @param row
	 *            行
	 * @param colname
	 *            列名
	 * @param value
	 *            触发hov列的值
	 */
	protected void invokeMultimdehov(int row, String colname, String value) {

	}

	/**
	 * 关闭功能
	 */
	public void onstopRun() {
		super.onstopRun();

		if (getDBtableModel().isquerying()) {
			getDBtableModel().stopQuery();
		}

		// 090211 取消自检
		// if (DefaultNPParam.debug == 1 || DefaultNPParam.develop == 1) {
		// doSelfcheck();
		// }
		on_close();
		// 清理内存
		freeMemory();
	}

	/**
	 * 释放本功能内存
	 */
	public void freeMemory() {
		setWaitCursor();
		if (formcolumndisplayinfos != null) {
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				colinfo.freeMemory();
			}
			formcolumndisplayinfos.removeAllElements();
			formcolumndisplayinfos = null;
		}

		if (orgdbmodelcols != null) {
			Enumeration<DBColumnDisplayInfo> en = orgdbmodelcols.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				colinfo.freeMemory();
			}
			orgdbmodelcols.removeAllElements();
			orgdbmodelcols = null;
		}

		if (table == null) {
			return;
		}
		TableColumnModel cm = table.getColumnModel();
		for (int c = 0; c < cm.getColumnCount(); c++) {
			TableColumn column = cm.getColumn(c);
			TableCellRenderer cr = column.getCellRenderer();
			if (cr instanceof CSteModel.PlainTablecellRender) {
				PlainTablecellRender pcr = (PlainTablecellRender) cr;
				pcr.freeMemory();
			} else if (cr instanceof CTableLinenoRender) {
				CTableLinenoRender lr = (CTableLinenoRender) cr;
				lr.freeMemory();
			}
			column.setCellRenderer(null);
		}

		dbmodel.clearAll();
		tableChanged();

		if (dbmodel != null) {
			dbmodel.freeMemory();
			dbmodel = null;
		}
		if (sumdbmodel != null) {
			sumdbmodel.freeMemory();
			sumdbmodel = null;
		}

		if (table != null) {
			table.freeMemory();
			table = null;
		}

		if (rootpanel != null) {
			rootpanel = null;
		}

		if (statusbar != null)
			statusbar = null;
		if (menubar != null)
			menubar = null;
		if (toolbar != null)
			toolbar = null;

		/*
		 * if (steformwindow != null) { steformwindow.freeMemory();
		 * steformwindow.dispose(); steformwindow = null; }
		 */
		if (form != null) {
			form.freeMemory();
			form = null;
		}

		if (querydlg != null) {
			querydlg.freeMemory();
			querydlg.dispose();
			querydlg = null;
		}

		tablescrollpane = null;
		printsetupfrm = null;
		vkupaction = null;
		vkdownaction = null;
		secondbackcolor = null;
		readonlybackcolor = null;
		sortcolumns = null;

		Runtime.getRuntime().gc();
		setDefaultCursor();
	}

	/**
	 * 自检查 界面 table form 字段次序 可编辑列 不可编辑列 HOV列
	 * 
	 * 程序
	 * 
	 */
	public String selfCheck() {
		// 分析form列
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "检查失败";
		}

		// 检查form列
		Vector<SelfcheckError> errors = new Vector<SelfcheckError>();
		if (formcolumndisplayinfos != null) {
			DBColumnChecker.checkOrder(formcolumndisplayinfos, errors);
			DBColumnhovChecker.checkHov(formcolumndisplayinfos, errors);
			DBColumnComboboxChecker.checkComboBox(formcolumndisplayinfos,
					errors);
			DBColumnUppercaseChecker.checkUppercase(formcolumndisplayinfos,
					errors);

			if (!(this instanceof CQueryStemodel)) {
				String viewname = this.getTablename().toLowerCase();
				if (dbprocesstablename != null
						&& !dbprocesstablename.equals(viewname)) {
					DBColumnEditableChecker.checkEditable(dbprocesstablename,
							viewname, formcolumndisplayinfos, errors);
				}
			}
		}

		// 检查表格列
		if (table != null) {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			TableColumnModel cm = table.getColumnModel();
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			for (int i = 0; i < cm.getColumnCount(); i++) {
				cols.add(dbmodel.getDisplaycolumninfos().elementAt(
						cm.getColumn(i).getModelIndex()));
			}
			DBColumnChecker.checkOrder(cols, errors);
		}

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
	 * 内部使用,为了自检，设置一下服务器process实际使用的表名
	 */
	private String dbprocesstablename;

	/**
	 * 内部使用,返回服务处理器的表名
	 * 
	 * @return
	 */
	public String getDbprocesstablename() {
		return dbprocesstablename;
	}

	/**
	 * 内部使用,设置服务处理器的表名
	 * 
	 * @param dbprocesstablename
	 */
	public void setDbprocesstablename(String dbprocesstablename) {
		this.dbprocesstablename = dbprocesstablename;
	}

	/**
	 * 卡片窗口放弃修改.强行关闭 如果当前行是新增,就删除
	 * 
	 * @return true 可以取消关闭 false:不能关闭
	 */
	public boolean cancelEdit() {
		/*
		 * if (currow >= 0 && currow < dbmodel.getRowCount()) { if
		 * (dbmodel.getdbStatus(currow) == RecordTrunk.DBSTATUS_NEW) {
		 * 
		 * String msg = "如果关闭窗口将丢失本行新增数据,继续吗?"; int ret =
		 * JOptionPane.showConfirmDialog(this.getParentFrame(), msg, "警告",
		 * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); if (ret !=
		 * JOptionPane.YES_OPTION) { return false; } dbmodel.removeRow(currow);
		 * 
		 * tableChanged(); sumdbmodel.fireDatachanged(); currow--; if (currow >=
		 * 0) { table.addRowSelectionInterval(currow, currow); } } }
		 */
		return true;

	}

	/**
	 * 是否创建字段分组.
	 * 
	 * @param colname
	 *            将要创建编辑控件的列
	 * @return 如果需要在colname列前创建一个程序分组标签,返回标签名称.如"货品信息". 不需要则返回null或""
	 */
	public String isCreateFieldgroup(String colname) {
		return null;
	}

	@Override
	public String getHovOtherWheresAp(String hovclassname) {
		if (useap) {
			return super.getHovOtherWheresAp(hovclassname);
		} else {
			return "";
		}
	}

	public Ruleenginee getRuleeng() {
		return ruleeng;
	}

	public void setRuleeng(Ruleenginee ruleeng) {
		this.ruleeng = ruleeng;
		if (ruleeng == null)
			return;
		ruleeng.process(this, "设置下拉选择");
		ruleeng.process(this, "设置系统下拉选择");
		ruleeng.process(this, "设置SQL下拉选择");
		ruleeng.process(this, "表格可以编辑");
	}

	/**
	 * 记录附件管理
	 */
	protected void onFiles() {
		int row = getRow();
		if (row < 0 || row > dbmodel.getRowCount() - 1)
			return;
		String filegroupid = dbmodel.getItemValue(row, "filegroupid");
		if (filegroupid == null)
			filegroupid = "";

		DBTableModel filedbmodel = null;
		if (dbmodel.getdbStatus(row) != RecordTrunk.DBSTATUS_NEW) {
			if (filegroupid.length() > 0) {
				// 每次都重新查
				// filedbmodel = dbmodel.getFiledbmodel(row);
				if (filedbmodel == null) {
					RecordfileUploader rfu = new RecordfileUploader();
					try {
						setWaitCursor();
						filedbmodel = rfu.browserFilegroup(filegroupid);
						dbmodel.setFiledbmodel(row, filedbmodel);
					} catch (Exception e) {
						logger.error("ERROR", e);
						errorMessage("查询附件信息失败", e.getMessage());
						return;
					} finally {
						setDefaultCursor();
					}
				}
			}
		}

		FileinfoDialog finfodlg = new FileinfoDialog(this.getParentFrame(),
				dbmodel, row, filegroupid, filedbmodel,
				on_beforemodify(row) == 0);
		finfodlg.pack();
		finfodlg.setVisible(true);

	}

	/**
	 * 检查每条记录,是不是有附件,如果有则上传
	 */
	public boolean uploadFiles() {
		return uploadFiles(dbmodel);
	}

	public boolean uploadFiles(DBTableModel pmodel) {
		uploadedfilecount = 0;
		if (!useattachfile)
			return true;
		RecordfileUploader rfu = new RecordfileUploader();
		boolean hasfile = false;
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			RecordTrunk rec = dbmodel.getRecordThunk(r);
			if (rec.getWantuploadfiles().size() > 0) {
				hasfile = true;
				break;
			}
		}
		if (!hasfile) {
			// 没有附件上传,按成功处理
			return true;
		}
		try {
			boolean ret = rfu.uploadFile(pmodel);
			if (ret) {
				uploadedfilecount = rfu.getUploadfilecount();
			}
			return ret;
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("上传错误", e.getMessage());
			return false;
		}
	}

	public boolean isUseattachfile() {
		return useattachfile;
	}

	/**
	 * 是否专项调整
	 * 
	 * @return
	 */
	public boolean isZxmodify() {
		return zxmodify;
	}

	/**
	 * 设置专项调整
	 * 
	 * @param zxmodify
	 */
	public void setZxmodify(boolean zxmodify) {
		this.zxmodify = zxmodify;
	}

	/**
	 * 返回后处理存储过程名
	 * 
	 * @return
	 */
	public String getStoreprocname() {
		if (ruleeng == null)
			return null;
		return ruleeng.processStoreproc(this, "后处理存储过程");
	}

	public Vector<String> getQuerycolumns() {
		return querycolumns;
	}

	public void setQuerycolumns(Vector<String> querycolumns) {
		this.querycolumns = querycolumns;
		this.querycond = null;
		if (querydlg != null) {
			querydlg.dispose();
			querydlg = null;
		}
	}

	/**
	 * 点击了lineno
	 * 
	 * @param row
	 */
	protected void on_clicklineno(int row) {
		if (row >= 0 && row < dbmodel.getRowCount()) {
			processQuerylink(row);
		}
	}

	protected void processQuerylink(int row) {
		if (ruleeng == null)
			return;

		Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this, "级联查询");
		if (qlinfos == null) {
			return;
		}

		JPopupMenu popmenu = new JPopupMenu("级联菜单");
		Enumeration<QuerylinkInfo> en = qlinfos.elements();
		while (en.hasMoreElements()) {
			QuerylinkInfo qlinfo = en.nextElement();
			// 菜单
			JMenuItem item;
			item = new JMenuItem(qlinfo.querylinkname);
			item.setActionCommand("run");
			item.addActionListener(new QuerylinkMenuListener(row, qlinfo));
			popmenu.add(item);
		}

		popmenu.show(table, (int) mouseclickpoint.getX(), (int) mouseclickpoint
				.getY());

	}

	protected class QuerylinkMenuListener implements ActionListener {
		int row;
		QuerylinkInfo qlinfo = null;

		public QuerylinkMenuListener(int row, QuerylinkInfo qlinfo) {
			this.row = row;
			this.qlinfo = qlinfo;
		}

		public void actionPerformed(ActionEvent e) {
			String name = e.getActionCommand();
			runQuerylink(row, qlinfo);
		}

	}

	protected void runQuerylink(int row, QuerylinkInfo querylinkinfo) {
		DBTableModel calledopdbmodel = null;
		COpframe frm = null;
		CSteModel calledstemodel = null;
		CMdeModel calledmdemodel = null;
		try {
			if (Clientframe.getClientframe() != null) {
				frm = Clientframe.getClientframe().runOp(querylinkinfo.opid,
						false);
			} else {
				frm = Oplauncher.loadOp(querylinkinfo.opid);
			}

			if (frm instanceof Steframe) {
				Steframe stefrm = (Steframe) frm;
				calledstemodel = stefrm.getCreatedStemodel();
				calledopdbmodel = calledstemodel.getDBtableModel();
			} else if (frm instanceof MdeFrame) {
				MdeFrame mdefrm = (MdeFrame) frm;
				calledmdemodel = mdefrm.getCreatedMdemodel();
				calledopdbmodel = calledmdemodel.getMasterModel()
						.getDBtableModel();
			} else if (frm instanceof MultisteFrame) {
				MultisteFrame mstefrm = (MultisteFrame) frm;
				calledstemodel = mstefrm.getCreatedStemodel();
				calledopdbmodel = calledstemodel.getDBtableModel();
			} else if (frm instanceof MMdeFrame) {
				MMdeFrame mdefrm = (MMdeFrame) frm;
				calledstemodel = mdefrm.getCreatedStemodel();
				calledopdbmodel = calledstemodel.getDBtableModel();
			} else if (frm instanceof ReportcanvasFrame) {
				ReportcanvasFrame bifrm = (ReportcanvasFrame) frm;
				// 调用报表的conds,是 参数=值 :[参数=值]
				String conds = "";
				Enumeration<Querycondinfo> en = querylinkinfo.conds.elements();
				while (en.hasMoreElements()) {
					Querycondinfo qcinfo = en.nextElement();
					if (conds.length() > 0) {
						conds += ":";
					}
					conds += qcinfo.cname1 + "=";
					conds += dbmodel.getItemValue(row, qcinfo.cname2);
				}

				bifrm.doQuery(conds);
				return;
			} else {
				JOptionPane.showMessageDialog(frame, "被调用功能ID"
						+ querylinkinfo.opid + "不是能处理的ste和mde类型");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "下载被调用功能ID"
					+ querylinkinfo.opid + "失败:" + e.getMessage());
			return;
		}

		boolean hasnull = false;
		Querycond querycond = new Querycond();
		Enumeration<Querycondinfo> en = querylinkinfo.conds.elements();
		while (en.hasMoreElements()) {
			Querycondinfo qcinfo = en.nextElement();
			// 取被查询列
			DBColumnDisplayInfo colinfo1 = calledopdbmodel
					.getColumninfo(qcinfo.cname1);
			if (colinfo1 == null) {
				errorMessage("错误", "级联定义错误，被调功能中没有列" + qcinfo.cname1);
				return;
			}
			Querycondline ql = new Querycondline(querycond, colinfo1.copy());
			querycond.add(ql);
			String curv = dbmodel.getItemValue(row, qcinfo.cname2);
			if (curv == null || curv.length() == 0) {
				hasnull = true;
			}
			ql.setValue(curv);
			ql.getCbUse().setSelected(true);
		}

		if (hasnull) {
			int ret = JOptionPane.showConfirmDialog(frame,
					"由于本行记录相关字段为空，查询结果可能较多，继续查询吗？", "提示",
					JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION)
				return;
		}
		String wheres = querycond.getWheres();

		if (querylinkinfo.wheres.length() > 0) {
			String otherwheres = querylinkinfo.wheres;
			Enumeration<DBColumnDisplayInfo> en1 = formcolumndisplayinfos
					.elements();
			while (en1.hasMoreElements()) {
				DBColumnDisplayInfo col = en1.nextElement();
				String v = dbmodel.getItemValue(row, col.getColname());
				if (v == null)
					v = "";
				if (col.getColtype()
						.equals(DBColumnDisplayInfo.COLTYPE_VARCHAR)) {
					v = "'" + v + "'";
				} else if (col.getColtype().equals(
						DBColumnDisplayInfo.COLTYPE_DATE)) {
					if (v.length() == 10) {
						v = "to_date('" + v + "','yyyy-mm-dd')";
					} else if (v.length() == 19) {
						v = "to_date('" + v + "','yyyy-mm-dd hh24:mi:ss')";
					} else {
						v = "to_date(null)";
					}
				}
				otherwheres = replaceParam(otherwheres, col.getColname(), v);
			}
			if (wheres.length() > 0) {
				wheres += " and " + otherwheres;
			} else {
				wheres = otherwheres;
			}

		}

		if (calledstemodel != null) {
			calledstemodel.doQuery(wheres);
		} else if (calledmdemodel != null) {
			calledmdemodel.getMasterModel().doQuery(wheres);
		}

	}

	@Override
	public void setOpid(String opid) {
		super.setOpid(opid);
		// 下载一次专项文件
		zxzipfile = DownloadManager.getInst().getZxfile(opid);
	}

	public File getZxzipfile() {
		return zxzipfile;
	}

	/**
	 * 取保存后上传文件的数量
	 * 
	 * @return
	 */
	public int getUploadedfilecount() {
		return uploadedfilecount;
	}

	/**
	 * 返回在专项调整zip包的命名。 单表和总单为ste 细单为ste1
	 * 
	 * @return
	 */
	public String getModelnameinzxzip() {
		return modelnameinzxzip;
	}

	public Steform getSteform() {
		return form;
	}

	public boolean isShowformonly() {
		return showformonly;
	}

	/**
	 * 只显示卡片，不显示表格
	 * 
	 * @param showformonly
	 */
	public void setShowformonly(boolean showformonly) {
		this.showformonly = showformonly;
	}

	public void setHotkeylistener(ActionListener hl) {
		this.hotkeylistener = hl;
	}

	protected class Rootpanel extends JPanel {

		public Rootpanel(LayoutManager layout) {
			super(layout);
		}

		@Override
		public Dimension getMinimumSize() {
			// TODO Auto-generated method stub
			// Dimension minsize=super.getMinimumSize();
			// return minsize;

			Dimension minsize = new Dimension(100, 100);
			return minsize;
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {
			// TODO Auto-generated method stub
			super.setBounds(x, y, width, height);
			// calcFormwindowsize();
		}

	}

	private void removeUpdownhotkey(JComponent jp) {
		KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		jp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkup,
				"nouse");
		jp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkdown, "nouse");

	}

	public int getErrorcount() {
		return errorcount;
	}

	public boolean isFormvisible() {
		return formvisible;
	}

	/**
	 * 清除数据和显示
	 */
	public void reset() {
		dbmodel.clearAll();
		sumdbmodel.fireDatachanged();
		tableChanged();
		form.clearAll();
	}

	/**
	 * 是否可以触发hov.返回true可以触发hov
	 * 
	 * @param invokehovcolname
	 * @return 返回true可以触发hov
	 */
	public boolean canInvokehov(String invokehovcolname) {
		return true;
	}

	protected Vector<String> getCalcsumcols() {
		Vector<String> sumcols = new Vector<String>();
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.isCalcsum()) {
				sumcols.add(col.getColname());
			}
		}
		return sumcols;
	}

	protected void setFocus(final String colname) {
		Runnable r = new Runnable() {
			public void run() {
				getDBColumnDisplayInfo(colname).getEditComponent()
						.requestFocus();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	/**
	 * 在新增或修改时，返回可以编辑的列 如果返回null或空串，表示按on_beforemodify()的返回值处理。
	 * 如果返回不为空，将勿略on_boforemodify()的控制，以本函数为准
	 * 
	 * @param row
	 *            行号
	 * @return 多个列名，中间用逗号隔开。如果返回opcode,goodsname表示opcode和goodsname可以编辑
	 */
	protected String getEditablecolumns(int row) {
		return "";
	}

	/**
	 * 将dbmodel的值设置给form，并设置控件是否可编
	 * 
	 * @param row
	 */
	public void bindDataSetEnable(int row) {
		if (row < 0 || row > dbmodel.getRowCount() - 1) {
			form.setFormEnable(false);
			return;
		}
		// 检查状态
		int dbstatus = dbmodel.getdbStatus(row);
		if (dbstatus == RecordTrunk.DBSTATUS_NEW) {
			form.bindDatamodel(dbmodel, row, true, getEditablecolumns(row));
		} else {
			// 修改删除，先取on_beforemodify
			boolean editable = on_beforemodify(row) == 0;
			form.bindDatamodel(dbmodel, row, editable, getEditablecolumns(row));
		}
	}

	protected class CTableex extends CTable {

		public CTableex() {
			super();
			// TODO Auto-generated constructor stub
		}

		public CTableex(TableModel dm, TableColumnModel cm) {
			super(dm, cm);
			// TODO Auto-generated constructor stub
		}

		public CTableex(TableModel dm) {
			super(dm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (!tableeditable || row < 0 || row >= dbmodel.getRowCount()
					|| column < 0 || column >= getColumnCount()) {
				return false;
			}
			int mindex = convertColumnIndexToModel(column);
			String colname = formcolumndisplayinfos.get(mindex).getColname();
			return isCellEditable(row, colname);
		}

		@Override
		public boolean isCellEditable(int row, String columnname) {
			if (!tableeditable || row < 0 || row >= dbmodel.getRowCount()) {
				return false;
			}
			boolean ret = isColumneditable(row, columnname);
			// logger.debug("cell editable row=" + row + ",columnname="
			// + columnname + "," + ret);
			return ret;
		}
	}

	/**
	 * 动态检查列是否可编辑
	 * 
	 * @param row
	 * @param colname
	 * @return
	 */
	protected boolean isColumneditable(int row, String colname) {
		if (dbmodel.getRowCount() == 0)
			return false;
		String editablecols = "";
		if (row >= 0 && row < dbmodel.getRowCount()) {
			editablecols = getEditablecolumns(row);
		}
		if (editablecols == null || editablecols.length() == 0) {
			DBColumnDisplayInfo col = getDBColumnDisplayInfo(colname);
			if (col == null)
				return false;
			return !col.isReadonly();
		}
		String ss[] = editablecols.split(",");
		for (int i = 0; i < ss.length; i++) {
			if (ss[i].equalsIgnoreCase(colname))
				return true;
		}
		return false;
	}

	/**
	 * 增加计算列。不能保存，非数据库列
	 * 
	 * @param colname
	 * @param coltype
	 * @param title
	 */
	protected DBColumnDisplayInfo addCalccolumn(String colname, String coltype,
			String title) {
		DBColumnDisplayInfo col = getDBColumnDisplayInfo(colname);
		if (col == null) {
			col = new DBColumnDisplayInfo(colname, coltype, title);
			formcolumndisplayinfos.add(col);
		}
		col.setDbcolumn(false);
		col.setUpdateable(false);
		return col;

	}

	/**
	 * 返回所有支持的打印方案
	 * 
	 * @return
	 */
	public Vector<String> getPrintplans() {
		return printplans;
	}

	/**
	 * 设置所有支持的打印方案
	 * 
	 * @param plans
	 */
	public void setPrintplans(Vector<String> plans) {
		this.printplans = plans;
		uploadPrintplans();
	}

	/**
	 * 将打印方案写到专项文件中，并上传
	 */
	protected void uploadPrintplans() {
		File dir = CurrentdirHelper.getZxdir();
		File zxfile = new File(dir, opid + ".zip");
		File tempfile = null;
		try {
			tempfile = File.createTempFile("temp", ".properties");
			// 第一行是所有方案，第二行是自动方案
			StringBuffer sb = new StringBuffer();
			Enumeration<String> en = printplans.elements();
			while (en.hasMoreElements()) {
				sb.append(en.nextElement());
				sb.append(":");
			}
			if (sb.length() > 0)
				sb.deleteCharAt(sb.length() - 1);
			PrintWriter out = new PrintWriter(new FileWriter(tempfile));
			out.println(sb.toString());
			out.close();

			ZipHelper.replaceZipfile(zxfile, "printplan.properties", tempfile);

			// 上传
			ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
			if (!zu.uploadZxfile(opid, zxfile)) {
				errorMessage("上传错误", zu.getErrormessage());
				return;
			}

		} catch (Exception e) {
			logger.error("e", e);
			errorMessage("错误", e.getMessage());
			return;
		} finally {
			if (tempfile != null)
				tempfile.delete();
		}

	}

	public boolean isUsequerythread() {
		return usequerythread;
	}

	public void setUsequerythread(boolean usequerythread) {
		this.usequerythread = usequerythread;
	}

	public Vector<DBColumnDisplayInfo> getDBColumnDisplayInfos() {
		return getFormcolumndisplayinfos();
	}

	public void recreateForm() {
		form = createForm();
	}

	private static String replaceParam(String sql, String param, String target) {
		StringBuffer sb = new StringBuffer();
		int p = 0;
		for (;;) {
			p = sql.toLowerCase().indexOf("{" + param.toLowerCase() + "}", p);
			if (p < 0)
				break;
			int p1 = sql.indexOf("}", p);
			if (p1 < 0)
				break;
			sb.append(sql.subSequence(0, p));
			sb.append(target);
			sql = sql.substring(p1 + 1);
			p = 0;
		}
		sb.append(sql);
		return sb.toString();
	}

	public void addQuerymustcol(String col) {
		querymustcolmap.put(col.toLowerCase(), col.toLowerCase());
	}

	public void removeQuerymustcol(String col) {
		querymustcolmap.remove(col.toLowerCase());
	}

	public boolean isQuerymustcol(String col) {
		return querymustcolmap.get(col.toLowerCase()) != null;
	}

	public HashMap<String, String> getQuerymustcolmap() {
		return querymustcolmap;
	}

	/**
	 * 返回单元格背景颜色.
	 * 
	 * @param row
	 *            行
	 * @param col
	 *            表格列序
	 * @return 默认返回null,使用系统默认的背景颜色.
	 */
	protected Color getCellbgcolor(int row, int col) {
		return null;
	}

	public boolean isTableautosized() {
		return tableautosized;
	}

	public void setTableautosized(boolean tableautosized) {
		this.tableautosized = tableautosized;
	}

	/**
	 * 将列宽设置到dbtable上
	 */
	protected void tableColumnwidth2Dbmodel() {
		if (table == null)
			return;
		DBTableModel tablemodel = (DBTableModel) table.getModel();
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn tc = table.getColumnModel().getColumn(i);
			int mindex = table.convertColumnIndexToModel(i);
			DBColumnDisplayInfo colinfo = tablemodel.getDisplaycolumninfos()
					.elementAt(mindex);
			int cw = tc.getWidth();
			logger.debug(colinfo.getColname() + " width=" + cw);
			colinfo.setTablecolumnwidth(cw);
		}

	}

	protected class SearchHandler extends AbstractAction {

		public SearchHandler(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(ACTION_SEARCH)) {
				if (currow < 0)
					return;
				if (dbmodel.getRowCount() == 0)
					return;
				table.search(currow);
			} else if (e.getActionCommand().equals(ACTION_SEARCHNEXT)) {
				table.searchNext();
			}

		}

	}

	public String getLastmodify() {
		return lastmodify;
	}

	public void setLastmodify(String lastmodify) {
		this.lastmodify = lastmodify;
	}

	public String getIsdefaultscheme() {
		return isdefaultscheme;
	}

	public void setIsdefaultscheme(String isdefaultscheme) {
		this.isdefaultscheme = isdefaultscheme;
	}

	public String getUserviewid() {
		return userviewid;
	}

	public void setUserviewid(String userviewid) {
		this.userviewid = userviewid;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public String getSortexpr() {
		return sortexpr;
	}


	/**
	 * 设置自定义界面
	 * 
	 * @param list
	 */
	public void setSkin(List<SkinInfo> list) {
		if (table == null)
			return;

		boolean f = tableeditable;
		// 屏蔽掉基类里的TableColumnModelHandel,避免在表格可编辑状态下调整列数据出错
		tableeditable = false;
		HashMap<String, TableColumn> map = new HashMap<String, TableColumn>();

		String classname = this.getClass().getName();
		for (SkinInfo info : list) {
			if (info.getClassname().equals(classname)) {

				// 调整前的表格顺序
				List<String> oldcolunms = Arrays.asList(getTableColumns());

				// 调整后的表格顺序
				List<String> cols = new ArrayList<String>();

				if (map.size() < oldcolunms.size()) {
					map.clear();
					TableColumnModel tc = table.getColumnModel();
					Enumeration<TableColumn> e = tc.getColumns();
					while (e.hasMoreElements()) {
						TableColumn c = e.nextElement();
						map.put(c.getHeaderValue().toString(), c);
					}
				}

				// 设置表格顺序、列宽,过滤掉产品升级后可能减少的字段
				for (ColInfo col : info.getColinfos()) {
					DBColumnDisplayInfo dbcolumn = this
							.getDBColumnDisplayInfo(col.getColname());
					if (dbcolumn != null
							&& oldcolunms.contains(col.getColname())) {
						dbcolumn.setTablecolumnwidth(col.getColwidth());
						cols.add(col.getColname());
					}
				}

				// 合并用户自定义的顺序的设置和产品的顺序设置，处理产品升级可能加字段的情况。加的字段默认放在后面。
				for (int i = 0; i < oldcolunms.size(); i++) {
					if (!cols.contains(oldcolunms.get(i))) {
						cols.add(oldcolunms.get(i));
					}
				}

				// 设置新的表格顺序,和宽度
				this.setTableColumns(cols.toArray(new String[0]));

				TableColumnModel tcm = table.getColumnModel();
				table.stopEdit();

				String colsname = "";
				DBColumnDisplayInfo dbcolumn = null;

				for (int i = 0; i < cols.size(); i++) {

					colsname = cols.get(i);
					dbcolumn = this.getDBColumnDisplayInfo(colsname);
					if (dbcolumn != null) {
						TableColumn tc = map.get(dbcolumn.getTitle());
						if (tc != null) {
							tc
									.setPreferredWidth(dbcolumn
											.getTablecolumnwidth());

							int index = tcm.getColumnIndex(tc.getIdentifier());
							if (index != i)
								table.moveColumn(index, i);
						}
					}
				}

				// 设置表格排序表达式
				setSortExpr(info.getExpr());
				break;
			}
		}
		tableeditable = f;
	}

	/**
	 * 用排序表达式排序
	 * 
	 * @param exprnew
	 */
	public void setSortExpr(String exprnew) {
		if(exprnew==null || exprnew.length()==0){
			return;
		}
		sortexpr=exprnew;
		try {
			getDBtableModel().sort(sortexpr);
			tableChanged();
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
	}

	/**
	 * 打开排序界面
	 */
	protected void faceSort() {
		SortRule rule = new SortRule();
		rule.setExpr(sortexpr);
		rule.setRuletype("设置排序");
		try {
			rule.setupUI(this);
			rule.setUse(true);
			sortexpr = rule.getExpr();
			try {
				getDBtableModel().sort(sortexpr);
				tableChanged();
			} catch (Exception e) {
				logger.error("ERROR", e);
			}
		} catch (Exception e) {
			logger.error("error",e);
		}

	}

	/**
	 * 加载缺省的
	 */
	protected void loadDefaultSkin() {
		SkinHelper.searchDefault(this);
		List<SkinInfo> list;
		try {
			list = SkinHelper.loadskin(this);
			setSkin(list);
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("错误", e.getMessage());
		}

	}

}
