package com.inca.np.gui.control;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Category;

import com.inca.np.communicate.CommandFactory;
import com.inca.np.communicate.RemoteDdlHelper;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.gui.ste.Steform;
import com.inca.np.gui.ste.SteformUIManager;
import com.inca.np.image.CIcon;
import com.inca.np.image.IconFactory;
import com.inca.np.util.DecimalHelper;
import com.inca.np.util.DefaultNPParam;
import com.inca.npclient.download.DownloadManager;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 16:09:18
 * 一列数据的定义
 */
public class DBColumnDisplayInfo {
	public final static String COLTYPE_NUMBER = "number";
	public final static String COLTYPE_VARCHAR = "varchar";
	public final static String COLTYPE_DATE = "date";

	public final static String EDITCOMP_TEXTFIELD = "textfield";
	public final static String EDITCOMP_COMBOBOX = "combobox";
	public final static String EDITCOMP_TEXTAREA = "textarea";
	public final static String EDITCOMP_CHECKBOX = "checkbox";

	/**
	 * 数据库列名
	 */
	String colname = "";

	/**
	 * 数据库类型,取值为COLTYPE_xxxx
	 */
	String coltype = "";
	/**
	 * 中文名
	 */
	String title = "";

	/**
	 * 是否主键
	 */
	boolean ispk = false;

	/**
	 * 编辑GUI 控件
	 */
	JComponent editorcomp = null;

	/**
	 * 用于查询between条件
	 */
	JComponent editorcomp1 = null;

	/**
	 * 编辑器属性,缺省为EDITCOMP_TEXTFIELD
	 */
	String editcomptype = EDITCOMP_TEXTFIELD;

	/**
	 * 文本框高
	 */
	int editorheight = 27;

	/**
	 * 文本框宽
	 */
	int editorwidth = 120;
	
	/**
	 * 标题文本的长度.
	 */
	int labelwidth=80;

	/**
	 * 是否在卡片窗口中折行
	 */
	boolean linebreak = false;

	/**
	 * 是否大写
	 */
	boolean uppercase = false;

	/**
	 * 是否只读
	 */
	boolean readonly = false;

	/**
	 * 日期类型是否含时间
	 */
	boolean withtime = false;

	/**
	 * 是否是数据库可更新的列
	 */
	boolean updateable = true;

	/**
	 * 是否用字符串来编辑年月 yyyy-mm形式
	 */
	boolean yearmonth = false;
	/**
	 * 是否是数据库列．如行号、计算列、不是数据库列
	 */
	boolean dbcolumn = true;

	/**
	 * 是否做为查询条件
	 */
	boolean queryable = true;

	/**
	 * 数字小数点后位
	 */
	int numberscale = 0;

	/**
	 * 序列号
	 */
	String seqname = "";

	/**
	 * 能否聚焦
	 */
	boolean focusable = true;
	/**
	 * 设置用的HOV
	 */
	private CHovBase hov = null;

	/**
	 * 卡片显示标题的JLabel
	 */
	private CLabel label;

	/**
	 * 初值
	 */
	String initvalue = "";

	/**
	 * 是否隐藏.如果为true,数据源中有该列,但不显示.
	 */
	private boolean hide = false;

	/**
	 * 行检查
	 */
	RowcheckInfo rowcheck = new RowcheckInfo();

	/**
	 * HOV定义
	 */
	Hovdefine hovdefine = null;

	/**
	 * 用于ccombobox类型的静态数据源
	 */
	DBTableModel cbdbmodel = null;

	/**
	 * 卡片窗口的icon
	 */
	private JLabel lbicon;

	/**
	 * 是否显示在卡片窗口上
	 */
	private boolean placeonform = false;
	
	/**
	 * 是否显示在查询对话框上
	 */
	private boolean placeonquery = false;

	/**
	 * 是否要自动计算合计行的合计。
	 */
	private boolean calcsum = false;

	/**
	 * 显示格式
	 */
	String numberdisplayformat = "";

	/**
	 * 必须的查询条件?
	 */
	boolean querymust = false;

	/*
	 * 是否是附件标志 boolean fileflag =false;
	 */

	/**
	 * 该字段如果显示在表格中,列宽由tablecolumnwidth确定. 如果tablecolumnwidth=-1,表示自动计算列宽
	 */
	int tablecolumnwidth = -1;

	/**
	 * 是否在卡片窗口不显示文本标签
	 */
	boolean hidetitleoncard = false;

	/**
	 * 子查询调用的功能ID
	 */
	String subqueryopid="";

	/**
	 * 构造
	 * 
	 * @param colname
	 *            列名
	 * @param coltype
	 *            列类型,取值COLTYPE_xxxx
	 */
	public DBColumnDisplayInfo(String colname, String coltype) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
	}

	/**
	 * 构造
	 * 
	 * @param colname
	 *            列名
	 * @param coltype
	 *            列类型,取值COLTYPE_xxxx
	 * @param title
	 *            标题
	 */
	public DBColumnDisplayInfo(String colname, String coltype, String title) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
		this.title = title;

		label = new CLabel(title);
		// label.setPreferredSize(new Dimension(80, 27));
		label.setPreferredSize(new Dimension(labelwidth, 27));
	}

	/**
	 * 构造
	 * 
	 * @param colname
	 *            列名
	 * @param coltype
	 *            列类型,取值COLTYPE_xxxx
	 * @param title
	 *            标题
	 * @param linebreak
	 *            在卡片窗口中是否折行
	 */
	public DBColumnDisplayInfo(String colname, String coltype, String title,
			boolean linebreak) {
		this.colname = colname;
		this.coltype = dbcoltype2coltype(coltype);
		this.title = title;
		this.linebreak = linebreak;

		label = new CLabel(title);
		label.setPreferredSize(new Dimension(labelwidth, 27));
	}

	/**
	 * 返回编辑类型,取值EDITCOMP_xxxx
	 * 
	 * @return
	 */
	public String getEditcomptype() {
		return editcomptype;
	}

	/**
	 * 设置编辑类型,取值EDITCOMP_xxxx
	 * 
	 * @param editcomptype
	 */
	public void setEditcomptype(String editcomptype) {
		this.editcomptype = editcomptype;
		if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
			cbdbmodel = createCbdbmodel();
		}
	}

	/**
	 * 返回是否大写
	 * 
	 * @return
	 */
	public boolean isUppercase() {
		return uppercase;
	}

	/**
	 * 返回是否可聚焦
	 * 
	 * @return
	 */
	public boolean isFocusable() {
		return focusable;
	}

	/**
	 * 设置是否可聚焦
	 * 
	 * @param focusable
	 */
	public void setFocusable(boolean focusable) {
		this.focusable = focusable;
	}

	/**
	 * 设置能否大写
	 * 
	 * @param uppercase
	 */
	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	/**
	 * 返回是否在卡片窗口中折行
	 * 
	 * @return
	 */
	public boolean isLinebreak() {
		return linebreak;
	}

	/**
	 * 设置是否折行
	 * 
	 * @param linebreak
	 */
	public void setLinebreak(boolean linebreak) {
		this.linebreak = linebreak;
	}

	/**
	 * 返回小数位数
	 * 
	 * @return
	 */
	public int getNumberscale() {
		return numberscale;
	}

	/**
	 * 设置小数位数
	 * 
	 * @param numberscale
	 */
	public void setNumberscale(int numberscale) {
		this.numberscale = numberscale;
	}

	/**
	 * 返回是否只读
	 * 
	 * @return
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * 设置是否只读
	 * 
	 * @param readonly
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
		if (editorcomp != null) {
			setEnable(!readonly);
		}
	}

	/**
	 * 返回是否带时间
	 * 
	 * @return
	 */
	public boolean isWithtime() {
		return withtime;
	}

	/**
	 * 设置是否带时间
	 * 
	 * @param withtime
	 */
	public void setWithtime(boolean withtime) {
		this.withtime = withtime;
	}

	/**
	 * 返回是否使用HOV
	 * 
	 * @return
	 */
	public boolean isUsehov() {
		if (hovdefine == null)
			return false;
		if (placeonform) {
			if (hovdefine.getUsecontext().indexOf("编辑") < 0)
				return false;
		}
		if (placeonquery) {
			if (hovdefine.getUsecontext().indexOf("查询") < 0)
				return false;
		}
		return true;
	}

	/**
	 * 返回HOV定义
	 * 
	 * @return
	 */
	public Hovdefine getHovdefine() {
		return hovdefine;
	}

	/**
	 * 返回是否隐藏
	 * 
	 * @return
	 */
	public boolean isHide() {
		return hide;
	}

	/**
	 * 设置是否隐藏
	 * 
	 * @param hide
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * 是否yyyy-mm形式
	 * 
	 * @return
	 */
	public boolean isYearmonth() {
		return yearmonth;
	}

	/**
	 * 设置是否使用yyyy-mm形式
	 * 
	 * @param yearmonth
	 */
	public void setYearmonth(boolean yearmonth) {
		this.yearmonth = yearmonth;
	}

	Category logger = Category.getInstance(DBColumnDisplayInfo.class);

	/**
	 * 设置hov定义,并根据定义创建hov
	 * 
	 * @param hovdefine
	 */
	public void setHovdefine(Hovdefine hovdefine) {
		this.hovdefine = hovdefine;
		if (hovdefine == null) {
			hov = null;
			return;
		}

		if (!DefaultNPParam.runonserver) {
			String classname = hovdefine.getHovclassname();
			try {
				if (classname.startsWith("hovgeneral_")) {
					String hovname = classname
							.substring("hovgeneral_".length());
					hov = DownloadManager.getInst().downloadZxhov(hovname);
				} else {
					hov = DownloadManager.getInst().downloadProdhov(classname);
				}
			} catch (Exception e) {
				logger.error("加载hov类失败" + hovdefine.getHovclassname(), e);
				return;
			}
		}

	}

	/**
	 * 返回初值
	 * 
	 * @return
	 */
	public String getInitvalue() {
		return initvalue;
	}

	/**
	 * 设置初值
	 * 
	 * @param initvalue
	 */
	public void setInitvalue(String initvalue) {
		this.initvalue = initvalue;
	}

	/**
	 * 返回行检查
	 * 
	 * @return
	 */
	public RowcheckInfo getRowcheck() {
		return rowcheck;
	}

	/**
	 * 设置行检查
	 * 
	 * @param rowcheck
	 */
	public void setRowcheck(RowcheckInfo rowcheck) {
		this.rowcheck = rowcheck;
	}

	public DBTableModel getCbdbmodel() {
		return cbdbmodel;
	}

	/**
	 * 取下拉选择某个key对应的值
	 * 
	 * @param key
	 * @return
	 */
	public String getComboboxValue(String key) {
		if (cbdbmodel == null)
			return null;
		for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
			if (cbdbmodel.getItemValue(r, "key").equals(key)) {
				return cbdbmodel.getItemValue(r, "value");
			}
		}
		return null;
	}

	public String getComboboxKey(String value) {
		if (cbdbmodel == null)
			return null;
		for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
			if (cbdbmodel.getItemValue(r, "value").equals(value)) {
				return cbdbmodel.getItemValue(r, "key");
			}
		}
		return null;
	}

	/**
	 * 创建编辑控件
	 * 
	 * @return
	 */
	public JComponent createComp() {
		JComponent editorcomp = null;
		if (coltype.equalsIgnoreCase("number")) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				if (cbdbmodel != null) {
					editorcomp = new CComboBox(new CComboBoxModel(cbdbmodel,
							"key", "value"));
				} else {
					editorcomp = new CComboBox();
				}
			} else if (editcomptype.equals(EDITCOMP_CHECKBOX)) {
				editorcomp = new CCheckBox();
			} else {
				editorcomp = new CNumberTextField(numberscale);
				((CFormatTextField) editorcomp).setEditorname(colname);
			}
		} else if (coltype.equalsIgnoreCase("date")) {
			if (withtime) {
				editorcomp = new CDatetimeTextField();
				((CFormatTextField) editorcomp).setEditorname(colname);
			} else {
				editorcomp = new CDateTextField();
				((CFormatTextField) editorcomp).setEditorname(colname);
			}
		} else if (coltype.equalsIgnoreCase("varchar")) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				if (cbdbmodel != null) {
					editorcomp = new CComboBox(new CComboBoxModel(cbdbmodel,
							"key", "value"));
				} else {
					editorcomp = new CComboBox();
				}
			} else if (editcomptype.equals(EDITCOMP_CHECKBOX)) {
				editorcomp = new CCheckBox();
			} else if (editcomptype.equals(EDITCOMP_TEXTAREA)) {
				editorcomp = new CTextArea();
			} else {
				if (yearmonth) {
					editorcomp = new CYMTextField();
					((CYMTextField) editorcomp).setEditorname(colname);
				} else if (uppercase) {
					editorcomp = new CUpperTextField();
					((CFormatTextField) editorcomp).setEditorname(colname);
				} else {
					editorcomp = new CPlainTextField();
					((CFormatTextField) editorcomp).setEditorname(colname);
				}
			}
		} else if (coltype.equals("行号")) {
			editorcomp = new CTextField();
		} else {
			// 不明coltype,如blob
			editorcomp = new CTextField();
		}

		if (editorcomp instanceof CTextArea) {
			editorcomp.setPreferredSize(new Dimension(editorwidth * 5 + 50,
					editorheight));
		} else {
			editorcomp
					.setPreferredSize(new Dimension(editorwidth, editorheight));
		}

		// 能否聚焦
		if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setKeyfocusable(focusable);
		}

		// 捕获聚焦
		editorcomp.addFocusListener(new FocusEventHandle());

		// 捕获键盘
		editorcomp.addKeyListener(new KeyEventHandle());

		//设置名称
		editorcomp.setName(colname);
		return editorcomp;
	}

	/**
	 * 创建用于编辑 sql in(列举)的编辑框
	 * 
	 * @return
	 */
	public CPlainTextField createQueryinComp() {
		CPlainTextField editorcomp = null;
		editorcomp = new CPlainTextField();
		((CFormatTextField) editorcomp).setEditorname(colname);

		editorcomp.setPreferredSize(new Dimension(editorwidth, editorheight));

		((CFormatTextField) editorcomp).setKeyfocusable(true);

		// 捕获聚焦
		editorcomp.addFocusListener(new FocusEventHandle());

		// 捕获键盘
		editorcomp.addKeyListener(new KeyEventHandle());

		return editorcomp;
	}

	/**
	 * 触发hov,弹出浮动窗口选hov
	 * 
	 * @param value
	 *            触发值
	 * @param oldvalue
	 *            原值
	 * @param otherwheres
	 *            其它select where条件
	 */
	public void invokeHov(String value, String oldvalue, String otherwheres) {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}
		if (!hovdataselected) {
			showHovWindow();

			// 考虑到触发hov的列，和hov列名是可能不同的，需要转换
			String hovcolname = hovdefine.getHovcolname(getColname());
			if (hovcolname == null) {
				logger.error("在hov中，找不到" + getColname() + "对应的hov列");
				hovcolname = getColname();
			}
			hov.doSelectHov(hovcolname, value, new HovHandle(), otherwheres);
		} else {
			DBTableModel resultdb = hov.getResult();
			confirmHov(resultdb);
		}
	}

	/**
	 * hov确定
	 * 
	 * @return true选择hov成功,false没有成功
	 */
	public boolean confirmHov() {
		if (!hov.isVisible()) {
			// 如果没有打开hov，说明是设置的值。返回true
			return true;
		}
		DBTableModel resultdb = hov.getResult();
		return confirmHov(resultdb);
	}

	/**
	 * 确定了hov的选择.
	 * 
	 * @param resultdb
	 * @return true选择成功,false选择失败
	 */
	private boolean confirmHov(DBTableModel resultdb) {
		if (resultdb != null) {
			hov.hide();
			hovdataselected = true;
			// editorcomp.transferFocus();
			hovlistener.on_hov(DBColumnDisplayInfo.this, resultdb);
			// 确认后，hovdataselected要改为false
			hovdataselected = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * HOV监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class HovHandle implements HovListener {
		// 双击hov记录时被CHovBase调用
		public void on_hov(DBColumnDisplayInfo dispinfo, DBTableModel resultdb) {
			confirmHov(resultdb);
		}

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
		}

	}

	/**
	 * 键盘监听器.对于上下键,让hov的当前行往上或往下滚动 esc键关闭
	 * 
	 * @author Administrator
	 * 
	 */
	class KeyEventHandle implements KeyListener {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			if (!isUsehov())
				return;
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				hov.nextRow();
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				hov.priorRow();
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// 关闭hov
				hov.hide();
				hovlistener.lostFocus(DBColumnDisplayInfo.this);
			}
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	/**
	 * 显示hov浮动窗口
	 */
	private void showHovWindow() {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}

		if (!hov.isVisible()) {
			Component p = null;
			for (p = editorcomp.getParent(); p != null
					&& !(p instanceof Window); p = p.getParent())
				;
			hov.show((Window) p, editorcomp);

			Runnable r = new Runnable() {
				public void run() {
					if (comp1firehov) {
						editorcomp1.requestFocus();
					} else {
						editorcomp.requestFocus();
					}
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}
	
	/**
	 * 是否显示hov窗?
	 * @return
	 */
	public boolean isHovwindowvisible(){
		if(hov==null)return false;
		return hov.isVisible();
	}

	/**
	 * HOV数据是否被选择
	 */
	private boolean hovdataselected = false;

	/**
	 * Focus监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class FocusEventHandle implements FocusListener {
		public void focusGained(FocusEvent e) {
			if (hovlistener == null) {
				return;
			}
			hovlistener.gainFocus(DBColumnDisplayInfo.this);
			if (isUsehov()) {
				// 打开窗口
				hovdataselected = false;
			}
		}

		public void focusLost(FocusEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
			// 2007 08 18 不要在这里调用hovlistener.lostFocus
		}
	}

	/**
	 * 外部的HOV监听器
	 */
	private HovListener hovlistener = null;

	/**
	 * 将编辑控件放在Form上
	 * 
	 * @param parent
	 * @param layout
	 * @param hovlistener
	 */
	public void placeOnForm(JPanel parent, CFormlayout layout,
			HovListener hovlistener) {
		if (isHide()) {
			return;
		}
		placeonform = true;

		this.hovlistener = hovlistener;
		if (editorcomp == null) {
			editorcomp = createComp();
			addEnterkeyTraver(editorcomp);
		}

		if (this.getColtype().equals("行号")) {
			return;
		}

		if (rowcheck != null && rowcheck.getChecktype().equals("非空")) {
			label = new CLabel(title + "*");
		} else {
			label = new CLabel(title);
		}
		label.setName(colname+"_title");
		if (!isHidetitleoncard()) {
			label.setPreferredSize(new Dimension(labelwidth, 27));
			parent.add(label);
		}

		if (getColname().equalsIgnoreCase("filegroupid")) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new FileMouseListener());
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else if (isUsehov() && hovdefine.getUsecontext().indexOf("编辑") >= 0) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			if (isReadonly()) {
				lbicon = new JLabel(IconFactory.ictransp);
			} else {
				lbicon = new JLabel(IconFactory.iccalendar16);
			}
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else if (editorcomp instanceof CDatetimeTextField
				|| editorcomp instanceof CDateTextField) {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			if (isReadonly()) {
				lbicon = new JLabel(IconFactory.ictransp);
			} else {
				lbicon = new JLabel(IconFactory.iccalendar16);
			}
			lbicon.setName("lbicon");
			jp.add(lbicon);
			lbicon.addMouseListener(new DatetimeMouseListener(editorcomp));
			parent.add(jp);
			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		} else {
			JPanel jp = new JPanel();
			jp.setName("fieldpane");
			parent.add(jp);
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			CIcon transimg = IconFactory.ictransp;
			lbicon = new JLabel(transimg);
			lbicon.setName("lbicon");
			
			if(parent.getName()!=null && parent.getName().equals("stecenterform")){
				LayoutManager lm = parent.getLayout();
				if(lm instanceof SteformUIManager){
					SteformUIManager formuim=(SteformUIManager)lm;
					if(formuim.isUseformUI()){
						//一定要去掉这个icon. 理由是在自由调整格式中, 可能将几个编辑文本框横放在一起
					}else{
						jp.add(lbicon);
					}
				}else{
					jp.add(lbicon);
				}
			}else{
				jp.add(lbicon);
			}

			if (linebreak) {
				layout.addLayoutComponent(jp, new CFormlineBreak());
			}
		}

	}

	/**
	 * 将编辑控件放在查询窗口上
	 * 
	 * @param parent
	 * @param layout
	 * @param hovlistener
	 * @param simplemode
	 */
	public void placeOnQuerypanel(JPanel parent, CFormlayout layout,
			HovListener hovlistener, boolean simplemode) {
		this.hovlistener = hovlistener;
		placeonquery = true;
		if (editorcomp == null) {
			editorcomp = createComp();
		}
		if (editorcomp1 == null) {
			editorcomp1 = createComp();
		}

		if (this.getColtype().equals("行号")) {
			return;
		}

		if (this.getColtype().equals(COLTYPE_NUMBER)) {
			if (editorcomp instanceof CNumberTextField) {
				CNumberTextField ntf = (CNumberTextField) editorcomp;
				// ntf.setAllowcomma(true);
			}
		}

		/*
		 * 20080701 if (!queryable) { return; }
		 */
		label = new CLabel(title);
		label.setPreferredSize(new Dimension(labelwidth, 27));
		// parent.add(label);

		if (isUsehov() && hovdefine.getUsecontext().indexOf("查询") >= 0) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());
			if (!simplemode) {
				jp.add(editorcomp1);
				lbicon = new JLabel(IconFactory.iccalendar16);
				jp.add(lbicon);
				lbicon.addMouseListener(new HovMouseListener1());
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());
		} else if (this.editcomptype.equals(EDITCOMP_COMBOBOX)) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			editorcomp = createQueryinComp();
			jp.add(editorcomp);
			lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new HovMouseListener());

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());

		} else if (editorcomp instanceof CDatetimeTextField
				|| editorcomp instanceof CDateTextField) {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			JLabel lbicon = new JLabel(IconFactory.iccalendar16);
			jp.add(lbicon);
			lbicon.addMouseListener(new DatetimeMouseListener(editorcomp));

			if (!simplemode) {
				jp.add(editorcomp1);
				lbicon = new JLabel(IconFactory.iccalendar16);
				jp.add(lbicon);
				lbicon.addMouseListener(new DatetimeMouseListener(editorcomp1));
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());
		} else {
			JPanel jp = new JPanel();
			BoxLayout box = new BoxLayout(jp, BoxLayout.X_AXIS);
			jp.setLayout(box);
			jp.add(editorcomp);
			CIcon transimg = IconFactory.ictransp;
			JLabel lbicon = new JLabel(transimg);
			jp.add(lbicon);

			// 加between条件
			if (!simplemode && !editcomptype.equals(EDITCOMP_CHECKBOX)) {
				jp.add(editorcomp1);
			}

			parent.add(jp);
			layout.addLayoutComponent(jp, new CFormlineBreak());

		}

		if (editorcomp instanceof CTextArea) {
			editorcomp
					.setPreferredSize(new Dimension(editorwidth, editorheight));
			editorcomp1.setPreferredSize(new Dimension(editorwidth,
					editorheight));
		}
	}

	/**
	 * 弹出窗口选日期
	 */
	public void showDatedialog() {
		JComponent comp = comp1firehov ? editorcomp1 : editorcomp;

		if (!((CFormatTextField) comp).isCanedit()) {
			return;
		}

		if (comp instanceof CDatetimeTextField) {
			CDatetimeTextField text = (CDatetimeTextField) comp;
			CCalendar dlg = new CCalendar(null, "选日期");
			dlg.pack();
			dlg.setVisible(true);
			Timestamp rettimestamp = dlg.getTimestamp();
			if (rettimestamp != null) {
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String strv = df.format(rettimestamp);
				strv = strv.substring(0, 10) + " 00:00:00";
				text.setText(strv);
				try {
					text.commitEdit();
				} catch (ParseException e1) {
				}
			}

		} else if (comp instanceof CDateTextField) {
			CDateTextField text = (CDateTextField) comp;
			CCalendar dlg = new CCalendar(null, "选日期");
			dlg.pack();
			dlg.setVisible(true);
			Timestamp rettimestamp = dlg.getTimestamp();
			if (rettimestamp != null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String strv = df.format(rettimestamp);
				text.setText(strv);
				try {
					text.commitEdit();
				} catch (ParseException e1) {
				}
			}
		}
	}

	/**
	 * 日期类型字段的鼠标监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class DatetimeMouseListener implements MouseListener {
		private JComponent ecomp;

		public DatetimeMouseListener(JComponent editorcomp) {
			this.ecomp = editorcomp;
		}

		public void mouseClicked(MouseEvent e) {
			Runnable r = new Runnable() {
				public void run() {
					ecomp.requestFocus();
					comp1firehov = ecomp == editorcomp1;
					showDatedialog();
				}
			};
			SwingUtilities.invokeLater(r);
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
	 * 显示传统HOV对话窗
	 */
	public void showHovDialog() {
		if (hovcondif != null && !hovcondif.canInvokehov(colname)) {
			return;
		}

		if (hovlistener == null)
			return;
		if (hovdefine == null) {
			return;
		}

		if (DBColumnDisplayInfo.this.isReadonly())
			return;
		if (editorcomp instanceof CFormatTextField) {
			if (!((CFormatTextField) editorcomp).isCanedit())
				return;
		}

		// 如果是mde 或 multi hov,改为调用listener的invokehov
		if (hov instanceof CMultiHov || hov instanceof CMdeHov
				|| hov instanceof CStehovEx || hov instanceof CMdehovEx) {
			hovcondif.invokeMultimdehov(colname);
			return;
		}

		// 显示hov
		Component p = null;
		for (p = editorcomp.getParent(); p != null && !(p instanceof Dialog); p = p
				.getParent())
			;
		if (p == null) {
			for (p = editorcomp.getParent(); p != null && !(p instanceof Frame); p = p
					.getParent())
				;
		}

		
		String otherwheres = "";
		if (hovcondif != null) {
			otherwheres = hovcondif.getHovOtherWheres(colname);
			if (otherwheres == null)
				otherwheres = "";
			hov.setOtherwheres(otherwheres);
		}

		DBTableModel resultdb = null;
		if (p instanceof Dialog) {
			resultdb = hov.showDialog((Dialog) p, hov.getDesc(), "", "",
					otherwheres);
		} else {
			resultdb = hov.showDialog((Frame) p, hov.getDesc(), "", "",
					otherwheres);
		}
		if (resultdb != null) {
			confirmHov(resultdb);
		}
	}

	/**
	 * 单击了编辑控件后的小图标。
	 */
	class HovMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (editcomptype.equals(EDITCOMP_COMBOBOX)) {
				// 显示
				editorcomp.requestFocus();
				selectComboboxData();
			} else {
				comp1firehov = false;
				Runnable r = new Runnable() {
					public void run() {
						editorcomp.requestFocus();
						showHovDialog();
					}
				};
				SwingUtilities.invokeLater(r);

			}
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/**
	 * 点击了附件
	 * 
	 * @author Administrator
	 * 
	 */
	class FileMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			hovlistener.on_hov(DBColumnDisplayInfo.this, null);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/**
	 * 弹出一个对话窗口,选
	 */
	public void selectComboboxData() {
		// TODO Auto-generated method stub
		JDialog parentdlg = null;

		Component tmpcomp = editorcomp.getParent();
		while ((tmpcomp = tmpcomp.getParent()) != null) {
			if (tmpcomp instanceof JDialog) {
				parentdlg = (JDialog) tmpcomp;
				break;
			}
		}

		String curvalue = ((CFormatTextField) editorcomp).getText();
		CQueryinDlg dlg = new CQueryinDlg(parentdlg, title, this, cbdbmodel,
				curvalue);
		dlg.pack();
		Dimension dlgsize = dlg.getPreferredSize();
		// 计算位置.算出编辑窗口在屏幕的位置,决定是放上边还是下边
		Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
		Point compscrpos = editorcomp.getLocationOnScreen();
		if (compscrpos.getY() >= scrsize.getHeight() / 2) {
			// 放在上部,计算位置
			int scrx = (int) compscrpos.getX();
			int scry = (int) compscrpos.getY()
					- (int) dlg.getPreferredSize().getHeight();
			if (scry < 0) {
				// 太高了,应该不会发生,暂不处理
				Dimension newsize = new Dimension((int) dlgsize.getWidth(),
						(int) dlgsize.getHeight() + scry);
				dlg.setPreferredSize(newsize);
				dlg.setSize(newsize);
				scry = 0;
			}
			dlg.setLocation(scrx, scry);
		} else {
			// 放在下部
			int scrx = (int) compscrpos.getX();
			int scry = (int) compscrpos.getY()
					+ (int) editorcomp.getPreferredSize().getHeight();
			if (scry + dlgsize.getHeight() > scrsize.getHeight()) {
				// 太高了,应该不会发生,暂不处理
				Dimension newsize = new Dimension((int) dlgsize.getWidth(),
						(int) scrsize.getHeight() - scry - 25);
				dlg.setSize(newsize);
				dlg.setPreferredSize(newsize);
			}
			dlg.setLocation(scrx, scry);
		}

		dlg.setVisible(true);
		if (!dlg.ok) {
			return;
		}
		((CFormatTextField) editorcomp).setText(dlg.getResult());
		try {
			((CFormatTextField) editorcomp).commitEdit();
		} catch (Exception e) {

		}
	}

	/**
	 * 查询条件右边的编辑框触发的hov
	 */
	private boolean comp1firehov = false;

	/**
	 * 查询条件范围,右边的输入框触发的hov
	 * 
	 * @author Administrator
	 * 
	 */
	class HovMouseListener1 implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			comp1firehov = true;
			Runnable r = new Runnable() {
				public void run() {
					editorcomp1.requestFocus();
					showHovDialog();
				}
			};
			SwingUtilities.invokeLater(r);
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

	/*
	 * class QueryHovMouseListener implements MouseListener { public void
	 * mouseClicked(MouseEvent e) { }
	 * 
	 * public void mousePressed(MouseEvent e) { if (queryhovlistener == null)
	 * return; queryhovlistener.on_hov(DBColumnDisplayInfo.this,
	 * DBColumnDisplayInfo.this.getValue());
	 * 
	 * public void mouseReleased(MouseEvent e) { }
	 * 
	 * public void mouseEntered(MouseEvent e) { }
	 * 
	 * public void mouseExited(MouseEvent e) { } }
	 */

	/**
	 * 返回列名
	 */
	public String getColname() {
		return colname;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}

	/**
	 * 返回列类型
	 * 
	 * @return
	 */
	public String getColtype() {
		return coltype;
	}

	/**
	 * 返回标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 返回是否是主键
	 * 
	 * @return
	 */
	public boolean isIspk() {
		return ispk;
	}

	/**
	 * 设置是否是主键
	 * 
	 * @param ispk
	 */
	public void setIspk(boolean ispk) {
		this.ispk = ispk;
	}

	/**
	 * 设置回车键导航
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
		hasset.add(enterkey);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);
	}

	/**
	 * 设置编辑控件当前值
	 * 
	 * @param v
	 */
	public void setValue(String v) {
		if (editorcomp instanceof CTextField) {
			((CTextField) editorcomp).setValue(v);
		} else if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setValue(v);
		} else if (editorcomp instanceof CComboBox) {
			((CComboBox) editorcomp).setValue(v);
		} else if (editorcomp instanceof CCheckBox) {
			((CCheckBox) editorcomp).setValue(v);
		} else if (editorcomp instanceof CTextArea) {
			((CTextArea) editorcomp).setText(v);
		}
	}

	/**
	 * 返回编辑控件当前值
	 * 
	 * @return
	 */
	public String getValue() {
		if (editorcomp instanceof CTextField) {
			return (String) ((CTextField) editorcomp).getValue();
		} else if (editorcomp instanceof CFormatTextField) {
			return (String) ((CFormatTextField) editorcomp).getValue();
		} else if (editorcomp instanceof CComboBox) {
			return ((CComboBox) editorcomp).getValue();
		} else if (editorcomp instanceof CCheckBox) {
			return ((CCheckBox) editorcomp).getValue();
		} else if (editorcomp instanceof CTextArea) {
			return ((CTextArea) editorcomp).getText();
		} else {
			return "";
		}
	}

	/**
	 * 返回编辑控件
	 * 
	 * @return
	 */
	public JComponent getEditComponent() {
		if (editorcomp == null) {
			editorcomp = createComp();
			addEnterkeyTraver(editorcomp);
		}
		return editorcomp;

	}

	public JComponent getEditComponentwithoutKeytraver() {
		if (editorcomp == null) {
			editorcomp = createComp();
		}
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = editorcomp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		editorcomp.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, hasset);

		return editorcomp;

	}

	/**
	 * 返回查询条件范围编辑控件
	 * 
	 * @return
	 */
	public JComponent getEditComponent1() {
		if (editorcomp1 == null) {
			editorcomp1 = createComp();
		}
		return editorcomp1;

	}

	/**
	 * 设置编辑控件可编辑
	 * 
	 * @param flag
	 */
	public void setEnable(boolean flag) {
		if (editorcomp == null) {
			editorcomp = createComp();
		}

		if (this.getColname().equals("stopcode")) {
			int m;
			m = 3;
		}

		if (editorcomp instanceof CFormatTextField) {
			((CFormatTextField) editorcomp).setCanedit(flag);
		} else if (editorcomp instanceof CTextArea) {
			((CTextArea) editorcomp).setCanedit(flag);
		} else {
			editorcomp.setEnabled(flag);
		}

		if (!flag) {
			editorcomp.setBackground(new Color(0xf0, 0xf0, 0xf0));
		} else {
			editorcomp.setBackground(new Color(0xff, 0xff, 0xff));
		}

	}

	/**
	 * 返回是否是数据库列
	 * 
	 * @return
	 */
	public boolean isDbcolumn() {
		return dbcolumn;
	}

	/**
	 * 设置是否是数据库列
	 * 
	 * @param dbcolumn
	 */
	public void setDbcolumn(boolean dbcolumn) {
		this.dbcolumn = dbcolumn;
	}

	/**
	 * 返回能否做为查询条件
	 * 
	 * @return
	 */
	public boolean isQueryable() {
		if (!isDbcolumn())
			return false;
		return queryable;
	}

	/**
	 * 设置能否做为查询条件
	 * 
	 * @param queryable
	 */
	public void setQueryable(boolean queryable) {
		this.queryable = queryable;
	}

	/**
	 * 返回是否可数据库更新
	 * 
	 * @return
	 */
	public boolean isUpdateable() {
		return updateable;
	}

	/**
	 * 设置是否可数据库更新
	 * 
	 * @param updateable
	 */
	public void setUpdateable(boolean updateable) {
		this.updateable = updateable;
	}

	/**
	 * 取数据库序列号名称
	 * 
	 * @return
	 */
	public String getSeqname() {
		return seqname;
	}

	/**
	 * 设置数据库序列号名称
	 * 
	 * @param seqname
	 */
	public void setSeqname(String seqname) {
		this.seqname = seqname;
	}

	/**
	 * 返回label
	 * 
	 * @return
	 */
	public CLabel getLabel() {
		return label;
	}

	/**
	 * 将数据写到out
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeData(OutputStream out) throws Exception {
		CommandFactory.writeString(colname, out);
		CommandFactory.writeString(coltype, out);
		CommandFactory.writeString(title, out);
		CommandFactory.writeString(seqname, out);
		CommandFactory.writeShort(ispk ? 1 : 0, out);
		CommandFactory.writeShort(editorheight, out);
		CommandFactory.writeShort(editorwidth, out);
		CommandFactory.writeShort(linebreak ? 1 : 0, out);
		CommandFactory.writeShort(uppercase ? 1 : 0, out);
		CommandFactory.writeShort(readonly ? 1 : 0, out);
		CommandFactory.writeShort(withtime ? 1 : 0, out);
		CommandFactory.writeShort(updateable ? 1 : 0, out);
		CommandFactory.writeShort(dbcolumn ? 1 : 0, out);
		CommandFactory.writeShort(queryable ? 1 : 0, out);
		CommandFactory.writeShort(numberscale, out);
	}

	/**
	 * 从out创建
	 * 
	 * @param in
	 * @throws Exception
	 */
	public void readData(InputStream in) throws Exception {
		colname = CommandFactory.readString(in);
		coltype = CommandFactory.readString(in);
		title = CommandFactory.readString(in);
		seqname = CommandFactory.readString(in);
		ispk = CommandFactory.readShort(in) == 1;
		editorheight = CommandFactory.readShort(in);
		editorwidth = CommandFactory.readShort(in);
		linebreak = CommandFactory.readShort(in) == 1;
		uppercase = CommandFactory.readShort(in) == 1;
		readonly = CommandFactory.readShort(in) == 1;
		withtime = CommandFactory.readShort(in) == 1;
		updateable = CommandFactory.readShort(in) == 1;
		dbcolumn = CommandFactory.readShort(in) == 1;
		queryable = CommandFactory.readShort(in) == 1;
		numberscale = CommandFactory.readShort(in);
	}

	public static String ROWCHECK_NOTNULL = "非空";

	/**
	 * 行检查类
	 * 
	 * @author Administrator
	 * 
	 */
	public class RowcheckInfo {
		String checktype = "";

		public RowcheckInfo() {
		}

		/**
		 * 提示信息
		 */
		String infomessage = "";

		public String getChecktype() {
			if (checktype == null)
				checktype = "";
			return checktype;
		}

		public void setChecktype(String checktype) {
			if (checktype == null)
				checktype = "";
			this.checktype = checktype;
		}

		public String getInfomessage() {
			return infomessage;
		}

		public void setInfomessage(String infomessage) {
			this.infomessage = infomessage;
		}
	}

	/**
	 * 返回hov
	 * 
	 * @return
	 */
	public CHovBase getHov() {
		return hov;
	}

	/**
	 * 对于编辑类型是EDITCOMP_COMBOBOX的,增加名字和值对.
	 * 
	 * @param key
	 *            内部值
	 * @param value
	 *            外部显示
	 */
	public void addComboxBoxItem(String key, String value) {
		if (cbdbmodel == null) {
			cbdbmodel = createCbdbmodel();
		}

		int row = cbdbmodel.getRowCount();
		cbdbmodel.appendRow();
		cbdbmodel.setItemValue(row, "key", key);
		cbdbmodel.setItemValue(row, "value", value);
	}

	/**
	 * 生成下拉选择的数据源
	 * 
	 * @return
	 */
	DBTableModel createCbdbmodel() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		colinfos.add(new DBColumnDisplayInfo("key", "varchar", "key"));
		colinfos.add(new DBColumnDisplayInfo("value", "varchar", "value"));
		return new DBTableModel(colinfos);
	}

	public boolean isHidetitleoncard() {
		return hidetitleoncard;
	}

	public void setHidetitleoncard(boolean hidetitleoncard) {
		this.hidetitleoncard = hidetitleoncard;
	}

	/**
	 * 复制
	 * 
	 * @return
	 */
	public DBColumnDisplayInfo copy() {
		DBColumnDisplayInfo n = new DBColumnDisplayInfo(colname, coltype,
				title, linebreak);
		n.ispk = ispk;
		n.editorcomp = null;
		n.editcomptype = editcomptype;
		n.editorheight = editorheight;
		n.editorwidth = editorwidth;
		n.linebreak = linebreak;
		n.uppercase = uppercase;
		n.readonly = readonly;
		n.withtime = withtime;
		n.updateable = updateable;
		n.queryable = queryable;
		n.numberscale = numberscale;
		n.seqname = seqname;
		n.focusable = focusable;
		n.hov = hov;
		n.label = null;
		n.initvalue = null;
		n.yearmonth = yearmonth;

		n.hovdefine = hovdefine; // /?????
		n.numberdisplayformat = numberdisplayformat;
		n.querymust = querymust;
		n.tablecolumnwidth = tablecolumnwidth;
		n.hidetitleoncard = hidetitleoncard;
		n.subqueryopid = subqueryopid;

		if (cbdbmodel != null) {
			for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
				String key = cbdbmodel.getItemValue(r, "key");
				String value = cbdbmodel.getItemValue(r, "value");
				n.addComboxBoxItem(key, value);
			}
		}

		return n;
	}

	/**
	 * 返回格式化后的数据.主要是number类型的字段小数位数的format
	 * 
	 * @param value
	 * @return
	 */
	public String getFormatvalue(String value) {
		if (value == null || value.length() == 0)
			return value;

		if (getColtype().equals(COLTYPE_NUMBER)) {
			// 如果已经format过了.就先去掉逗号再format
			value = value.replaceAll(",", "");
			if (numberdisplayformat != null && numberdisplayformat.length() > 0) {
				DecimalFormat df = new DecimalFormat(numberdisplayformat);
				return df.format(DecimalHelper.toDec(value));
			}

			if (getNumberscale() == 0) {
				return value;
			}
			BigDecimal bdec = null;
			try {
				value = value.replaceAll(",", "");
				bdec = new BigDecimal(value);
				bdec = bdec
						.setScale(getNumberscale(), BigDecimal.ROUND_HALF_UP);
				value = bdec.toPlainString();

				if (getNumberscale() > 0) {
					value = formatDig3(value);
				}

				return value;
			} catch (Exception ne) {
				return value;
			}
		} else if (getColtype().equals(COLTYPE_DATE)) {
			if (withtime) {
				if (value.length() == 10) {
					return value + " 00:00:00";
				}
			} else {
				if (value.length() > 10) {
					return value.substring(0, 10);
				}
			}
			return value;
		} else {
			return value;
		}
	}

	/**
	 * 小数点前，三位一组一个逗号
	 * 
	 * @param s
	 */
	protected String formatDig3(String s) {
		int p = s.indexOf(".");
		if (p < 0)
			return s;
		String digs = s.substring(0, p);
		char firstc = digs.charAt(0);
		StringBuffer resultsb = new StringBuffer();
		if (firstc == '+' || firstc == '-') {
			resultsb.append(firstc);
			digs = digs.substring(1);
		}

		int index = 0;
		if (digs.length() > 3) {
			int morect = digs.length() % 3;
			if (morect > 0) {
				for (int i = 0; i < morect; i++) {
					resultsb.append(digs.charAt(index++));
				}
				resultsb.append(",");
			}
		}

		for (int i = 1; index < digs.length(); i++) {
			resultsb.append(digs.charAt(index++));
			if (i % 3 == 0 && index != digs.length()) {
				resultsb.append(",");
			}
		}

		resultsb.append(s.substring(p));
		return resultsb.toString();
	}

	/**
	 * 设置系统选项字典
	 * 
	 * @param keyword
	 *            系统选项字典关键词
	 */
	public void setSystemddl(final String keyword) {
		JComponent editcomp = this.getEditComponent();
		if (!(editcomp instanceof CComboBox)) {
			logger.error(this.getColname()
					+ "的编辑控件不是CComboBox,不能调用setSystemddl");
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				RemoteDdlHelper rmthlp = new RemoteDdlHelper();//
				try {
					rmthlp.doSelect(keyword);
				} catch (Exception e) {
					logger.error("ERROR", e);
					return;
				}

				DBTableModel dbmodel = rmthlp.getDdlmodel();
				for (int i = 0; dbmodel != null && i < dbmodel.getRowCount(); i++) {
					String key = dbmodel.getItemValue(i, "ddlid");
					String value = dbmodel.getItemValue(i, "ddlname");
					addComboxBoxItem(key, value);
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

	}

	/**
	 * 由sql生成下拉选择数据源
	 * 
	 * @param sql
	 *            select 语句
	 * @param keycolname
	 *            键值列名
	 * @param valuecolname
	 *            显示值列名
	 */
	public void setSqlDdl(final String sql, final String keycolname,
			final String valuecolname) {
		if (DefaultNPParam.runonserver)
			return;

		JComponent editcomp = this.getEditComponent();
		if (!(editcomp instanceof CComboBox)) {
			logger.error(this.getColname() + "的编辑控件不是CComboBox,不能调用setSqlDdl");
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				RemotesqlHelper sqlhelper = new RemotesqlHelper();
				DBTableModel dbmodel = null;
				try {
					dbmodel = sqlhelper.doSelect(sql, 0, 100);
				} catch (Exception e) {
					logger.error("ERROR", e);
					return;
				}

				for (int i = 0; dbmodel != null && i < dbmodel.getRowCount(); i++) {
					String key = dbmodel.getItemValue(i, keycolname);
					String value = dbmodel.getItemValue(i, valuecolname);
					addComboxBoxItem(key, value);
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

	}

	/**
	 * 内部使用
	 * 
	 * @return
	 */
	public boolean isComp1firehov() {
		return comp1firehov;
	}

	/**
	 * 内部使用
	 * 
	 * @param comp1firehov
	 */
	public void setComp1firehov(boolean comp1firehov) {
		this.comp1firehov = comp1firehov;
	}

	/**
	 * HOV调用的其它条件接口
	 */
	private HovcondIF hovcondif = null;

	/**
	 * 返回HOV调用的其它条件接口
	 * 
	 * @return
	 */
	public HovcondIF getHovcondif() {
		return hovcondif;
	}

	/**
	 * 设置HOV调用的其它条件接口
	 * 
	 * @param hovcondif
	 */
	public void setHovcondif(HovcondIF hovcondif) {
		this.hovcondif = hovcondif;
	}

	/*
	 * 返回是否是文件标志 public boolean isFileflag() { return fileflag; }
	 * 
	 * public void setFileflag(boolean fileflag) { this.fileflag = fileflag; }
	 */

	/**
	 * 释放内存
	 */
	public void freeMemory() {
		if (editorcomp != null) {
			if (editorcomp instanceof CFormatTextField) {
				((CFormatTextField) editorcomp).freeMemory();
			}
			editorcomp = null;
		}
		if (editorcomp1 != null) {
			if (editorcomp1 instanceof CFormatTextField) {
				((CFormatTextField) editorcomp1).freeMemory();
			}
			editorcomp1 = null;
		}
		if (label != null) {
			label = null;
		}
		if (lbicon != null) {
			lbicon = null;
		}
		if (hov != null) {
			hov.freeMemory();
			hov = null;
		}
		if (rowcheck != null) {
			rowcheck = null;
		}

		if (hovdefine != null) {
			hovdefine = null;
		}

		if (cbdbmodel != null) {
			cbdbmodel.freeMemory();
			cbdbmodel = null;
		}
		hovlistener = null;
	}

	public static String dbcoltype2coltype(String dbcoltype) {
		if (dbcoltype.equals("行号"))
			return dbcoltype;
		dbcoltype = dbcoltype.toLowerCase();
		if (dbcoltype.startsWith("number")) {
			return DBColumnDisplayInfo.COLTYPE_NUMBER;
		} else if (dbcoltype.startsWith("date")) {
			return DBColumnDisplayInfo.COLTYPE_DATE;
		} else {
			return DBColumnDisplayInfo.COLTYPE_VARCHAR;
		}

	}

	/**
	 * 是否是交叉数据列
	 */
	private boolean crossdata = false;

	public boolean isCrossdata() {
		return crossdata;
	}

	public void setCrossdata(boolean crossdata) {
		this.crossdata = crossdata;
	}

	public void setHovlistener(HovListener hovlistener) {
		this.hovlistener = hovlistener;
	}

	public boolean isCalcsum() {
		return calcsum;
	}

	public void setCalcsum(boolean calcsum) {
		this.calcsum = calcsum;
	}

	public boolean isCanedit() {
		JComponent jcomp = getEditComponent();
		if (jcomp instanceof CFormatTextField) {
			boolean canedit = ((CFormatTextField) jcomp).isCanedit();
			if (!canedit)
				return false;
		} else if (jcomp instanceof CTextArea) {
			boolean canedit = ((CTextArea) jcomp).isCanedit();
			if (!canedit)
				return false;
		} else {
			boolean canedit = jcomp.isEnabled();
			if (!canedit)
				return false;
		}
		return true;
	}

	public String getNumberDisplayformat() {
		return numberdisplayformat;
	}

	public void setNumberDisplayformat(String displayformat) {
		this.numberdisplayformat = displayformat;
	}

	public void setHov(CHovBase hov) {
		this.hov = hov;
	}

	public boolean isQuerymust() {
		return querymust;
	}

	public void setQuerymust(boolean querymust) {
		this.querymust = querymust;
	}

	public int getTablecolumnwidth() {
		return tablecolumnwidth;
	}

	public void setTablecolumnwidth(int tablecolumnwidth) {
		this.tablecolumnwidth = tablecolumnwidth;
	}

	public static void main(String[] args) {
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("test", "number");
		col.setNumberscale(2);
		String s = col.getFormatvalue("-1230.67190");
		System.out.println(s);
	}

	public String getSubqueryopid() {
		return subqueryopid;
	}

	public void setSubqueryopid(String subqueryopid) {
		this.subqueryopid = subqueryopid;
	}

	
}
