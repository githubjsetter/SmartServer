package com.inca.np.gui.ste;

import com.inca.np.gui.control.*;
import com.inca.np.filedb.*;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-30 Time: 10:48:50
 * 一条查询条件
 */
public class Querycondline implements CFormatTextFieldListener {
	public static final String STRINGOP = "匹配,=,列举in,>,>=,<,<=,<>,范围";
	public static final String NUMBEROP = "=,列举in,>,>=,<,<=,<>,范围";
	public static final String DATEOP = "范围,=";
	public static final String COMBOOP = "列举";
	public static final String EQUALOP = "=";

	/**
	 * 列定义
	 */
	DBColumnDisplayInfo dbcolumndisplayinfo = null;

	/**
	 * 比较操作符集
	 */
	String op = NUMBEROP;

	/**
	 * 比较操作符
	 */
	String opvalue = "=";
	// String value;

	/**
	 * 缺省值
	 */
	String defaultvalue;
	// private JComponent condeditcomp;

	/**
	 * 查询条件
	 */
	Querycond querycond = null;

	/**
	 * 是否启用本条件
	 */
	CCheckBox cbUse = null;

	/**
	 * 总单细单列标志。单表和MDE总单为m,mde细单为d。缺省为"m"
	 */
	String mdflag = "m";

	public CCheckBox getCbUse() {
		if (cbUse == null) {
			String title = dbcolumndisplayinfo.getTitle();
			if (dbcolumndisplayinfo.isQuerymust()) {
				title += "*";
			}
			
			if(dbcolumndisplayinfo.getSubqueryopid().length()>0){
				cbUse = new CCheckBox();
			}else{
				cbUse = new CCheckBox(title);
			}
			if (dbcolumndisplayinfo.isQuerymust()) {
				cbUse.setSelected(true);
			}
		}
		return cbUse;
	}

	/**
	 * 构造
	 * 
	 * @param querycond
	 *            查询条件类
	 * @param dbcolumndisplayinfo
	 *            列定义
	 */
	public Querycondline(Querycond querycond,
			DBColumnDisplayInfo dbcolumndisplayinfo) {
		this(querycond, dbcolumndisplayinfo, "m");
	}

	public Querycondline(Querycond querycond,
			DBColumnDisplayInfo dbcolumndisplayinfo, String mdflag) {
		this.querycond = querycond;
		this.dbcolumndisplayinfo = dbcolumndisplayinfo.copy();
		this.mdflag = mdflag;
		// 查询条件时,不要只读
		this.dbcolumndisplayinfo.setReadonly(false);
		this.dbcolumndisplayinfo.setHovcondif(querycond.getHovcond(mdflag));
		String coltype = dbcolumndisplayinfo.getColtype();

		if (dbcolumndisplayinfo.getEditcomptype().equals(
				DBColumnDisplayInfo.EDITCOMP_COMBOBOX)) {
			op = COMBOOP;
		} else if (dbcolumndisplayinfo.getEditcomptype().equals(
				DBColumnDisplayInfo.EDITCOMP_CHECKBOX)) {
			op = EQUALOP;
		} else if (coltype.equalsIgnoreCase("varchar")) {
			op = STRINGOP;
		} else if (coltype.equalsIgnoreCase("date")
				|| coltype.equalsIgnoreCase("datetime")) {
			op = DATEOP;
		} else {
			op = NUMBEROP;
		}
	}

	/**
	 * 返回查询条件.如果没有输入查询条件,返回空串
	 * 
	 * @return
	 */
	public String getWheres() {
		if (!this.cbUse.isSelected()) {
			return "";
		}
		String value = this.getValue();
		String value1 = this.getValue1();
		if (value == null || value.length() == 0
				|| value.startsWith("0000-00-00")) {
			return "";
		}

		String strop = opvalue;
		String colname = dbcolumndisplayinfo.getColname();
		String coltype = dbcolumndisplayinfo.getColtype();
		if (coltype.equalsIgnoreCase("varchar")) {
			if (strop.equals("匹配")) {
				strop = "like";
				value = "'" + value + "%'";
				return colname + " " + strop + " " + value;
			} else if (strop.equals("列举in")) {
				strop = "in";
				String[] ss = value.split(",");
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				for (int i = 0; i < ss.length; i++) {
					if (i > 0)
						sb.append(",");
					sb.append("'" + ss[i] + "'");
				}
				sb.append(")");
				value = sb.toString();
				return colname + " " + strop + " " + value;
			} else if (strop.equals("范围")) {
				strop = "between";
				value = "'" + value + "'";
				value1 = "'" + value1 + "'";
				return colname + " " + strop + " " + value + " and " + value1;
			} else if (strop.equals("列举")) {
				return colname + " in (" + value + ")";
			} else {
				value = "'" + value + "'";
				return colname + " " + strop + " " + value;
			}
		} else if (coltype.equalsIgnoreCase("date")
				|| coltype.equalsIgnoreCase("datetime")) {
			if (strop.equals("=")) {
				if (value.length() == 10) {
					return colname + " between to_date('" + value
							+ " 00:00:00','yyyy-mm-dd hh24:mi:ss') and "
							+ " to_date('" + value
							+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
				} else {
					return colname + " " + strop + " to_date('" + value
							+ "','yyyy-mm-dd')";
				}
			} else if (strop.equals("范围")) {
				if (value.length() == 10) {
					value = "to_date('" + value
							+ " 00:00:00','yyyy-mm-dd hh24:mi:ss')";
				} else {
					value = "to_date('" + value + "','yyyy-mm-dd hh24:mi:ss')";
				}
				if (value1.length() == 10) {
					value1 = "to_date('" + value1
							+ " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
				} else {
					value1 = "to_date('" + value1
							+ "','yyyy-mm-dd hh24:mi:ss')";
				}

				return colname + " between " + value + " and " + value1;

			} else {
				return "";
			}
		} else {
			if (strop.equals("列举in")) {
				strop = "in";
				value = "(" + value + ")";
				return colname + " " + strop + " " + value;
			} else if (strop.equals("范围")) {
				return colname + " between " + value + " and " + value1;
			} else if (strop.equals("列举")) {
				return colname + " in (" + value + ")";
			} else {
				return colname + " " + strop + " " + value;
			}
		}

	}

	/**
	 * @deprecated
	 */
	private Vector<String> hovothercolname = new Vector<String>();

	/**
	 * @deprecated
	 * @param colname
	 */
	public void addHovothercolname(String colname) {
		hovothercolname.add(colname);
	}

	/**
	 * 返回用于HOV查询的where条件
	 * 
	 * @return
	 */
	public String getHovWheres() {
		String value = this.getValue();
		if (value == null || value.length() == 0
				|| value.startsWith("0000-00-00")) {
			return "";
		}

		String strop = "=";
		String colname = dbcolumndisplayinfo.getColname();
		String coltype = dbcolumndisplayinfo.getColtype();
		StringBuffer sb = new StringBuffer();
		if (coltype.equalsIgnoreCase("varchar")) {
			strop = "like";
			value = "'" + value + "%'";
			sb.append(colname + " " + strop + " " + value);
			if (hovothercolname.size() > 0) {
				Enumeration<String> en = hovothercolname.elements();
				while (en.hasMoreElements()) {
					String othercolname = en.nextElement();
					sb.append(" or ");
					sb.append(othercolname + " " + strop + " "
							+ value.toUpperCase()); // 大写

				}
			}
			return "(" + sb.toString() + ")";
		} else if (coltype.equalsIgnoreCase("date")
				|| coltype.equalsIgnoreCase("datetime")) {
			return colname + " " + strop + " to_date('" + value
					+ "','yyyy-mm-dd')";
		} else {
			if (strop.equals("列举in")) {
				strop = "in";
				value = "(" + value + ")";
			}
			sb.append(colname + " " + strop + " " + value);
			if (hovothercolname.size() > 0) {
				Enumeration<String> en = hovothercolname.elements();
				while (en.hasMoreElements()) {
					String othercolname = en.nextElement();
					sb.append(" or ");
					sb.append(othercolname + " " + strop + " " + value);

				}
			}
			return "(" + sb.toString() + ")";
		}
	}

	/**
	 * 返回在本地文件查询的条件
	 * 
	 * @return
	 */
	public FiledbSearchCond getFiledbCond() {
		String value = this.getValue();
		if (value == null || value.length() == 0
				|| value.startsWith("0000-00-00")) {
			return null;
		}

		String strop = "=";
		String colname = dbcolumndisplayinfo.getColname();
		String coltype = dbcolumndisplayinfo.getColtype();
		if (coltype.equalsIgnoreCase("varchar")) {
			strop = "like";
		} else if (coltype.equalsIgnoreCase("date")) {
			strop = "like";
		}

		FiledbSearchCond cond = new FiledbSearchCond();
		cond.colname = colname;
		cond.op = strop;
		cond.value = value;
		return cond;
	}

	/**
	 * 设置比较操作符集.
	 * 
	 * @param op
	 */
	public void setOp(String op) {
		this.op = op;
	}

	/**
	 * 设置控件当前值
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		JComponent comp = getCondEditcomp();
		if (comp instanceof CFormatTextField) {
			((CFormatTextField) comp).setValue(value);
		} else if (comp instanceof JTextField) {
			((JTextField) comp).setText(value);
		}
	}

	/**
	 * 设置缺省值
	 * 
	 * @param defaultvalue
	 */
	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	/**
	 * 返回列名
	 * 
	 * @return
	 */
	public String getColname() {
		return dbcolumndisplayinfo.getColname();
	}

	/**
	 * 返回列的类型
	 * 
	 * @return
	 */
	public String getColtype() {
		return dbcolumndisplayinfo.getColtype();
	}

	/**
	 * 返回列的标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return dbcolumndisplayinfo.getTitle();
	}

	/**
	 * 返回列的比较操作符集
	 * 
	 * @return
	 */
	public String getOp() {
		return op;
	}

	/**
	 * 取编辑控件当前值
	 * 
	 * @return
	 */
	public String getValue() {
		JComponent comp = getCondEditcomp();
		if (comp instanceof CFormatTextField) {
			return ((CFormatTextField) comp).getText();
			/*
			 * Document doc = ((CFormatTextField) comp).getDocument(); try {
			 * return doc.getText(0, doc.getLength()); } catch
			 * (BadLocationException e) { return ""; }
			 */
		} else if (comp instanceof JTextField) {
			return ((JTextField) comp).getText();
			/*
			 * Document doc = ((JTextField) comp).getDocument(); try { return
			 * doc.getText(0, doc.getLength()); } catch (BadLocationException e)
			 * { return ""; }
			 */
		} else if (comp instanceof CComboBox) {
			return ((CComboBox) comp).getValue();
		} else if (comp instanceof CCheckBox) {
			return ((CCheckBox) comp).getValue();
		} else {
			return "";
		}
	}

	/**
	 * 取查询范围后面控件的当前值
	 * 
	 * @return
	 */
	public String getValue1() {
		JComponent comp = getCondEditcomp1();
		if (comp instanceof CFormatTextField) {
			Document doc = ((CFormatTextField) comp).getDocument();
			try {
				return doc.getText(0, doc.getLength());
			} catch (BadLocationException e) {
				return "";
			}
		} else if (comp instanceof JTextField) {
			Document doc = ((JTextField) comp).getDocument();
			try {
				return doc.getText(0, doc.getLength());
			} catch (BadLocationException e) {
				return "";
			}
		} else if (comp instanceof CComboBox) {
			return ((CComboBox) comp).getValue();
		} else if (comp instanceof CCheckBox) {
			return ((CCheckBox) comp).getValue();
		} else {
			return "";
		}
	}

	/**
	 * 取缺省值
	 * 
	 * @return
	 */
	public String getDefaultvalue() {
		return defaultvalue;
	}

	/**
	 * 取当前的比较操作符
	 * 
	 * @return
	 */
	public String getOpvalue() {
		return opvalue;
	}

	/**
	 * 设置当前的操作符
	 * 
	 * @param opvalue
	 */
	public void setOpvalue(String opvalue) {
		this.opvalue = opvalue;

		if (getColtype().equals(DBColumnDisplayInfo.COLTYPE_NUMBER)) {
			if (dbcolumndisplayinfo.getEditComponent() instanceof CNumberTextField) {
				CNumberTextField ntf = (CNumberTextField) dbcolumndisplayinfo
						.getEditComponent();
				if (opvalue.equals("列举in")) {
					ntf.setAllowcomma(true);
				} else {
					ntf.setAllowcomma(false);
				}
			}
		}
	}

	/**
	 * 返回是否换行
	 * 
	 * @return
	 */
	public boolean isLinebreak() {
		return dbcolumndisplayinfo.isLinebreak();
	}

	/**
	 * 返回列信息
	 * 
	 * @return
	 */
	public DBColumnDisplayInfo getDbcolumndisplayinfo() {
		return dbcolumndisplayinfo;
	}

	/**
	 * 返回查询条件编辑控件
	 * 
	 * @return
	 */
	public JComponent getCondEditcomp() {
		return dbcolumndisplayinfo.getEditComponent();
	}

	/**
	 * 放置查询条件控件
	 * 
	 * @param c
	 * @param layout
	 * @param simplemode
	 */
	public void placeComponent(JPanel c, CFormlayout layout, boolean simplemode) {
		// 取消回车向前20071012
		// addEnterkeyTraver(cbUse);
		dbcolumndisplayinfo.placeOnQuerypanel(c, layout, querycond, simplemode);
		dbcolumndisplayinfo.setEnable(true);

		JComponent editcomp = dbcolumndisplayinfo.getEditComponent();
		editcomp.addKeyListener(new EnterkeyHandler());
		if (editcomp instanceof CTextArea) {
			editcomp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "nouse");
			editcomp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "nouse");
			editcomp = ((CTextArea) editcomp).getTextarea();
		}
		JComponent editcomp1 = dbcolumndisplayinfo.getEditComponent1();
		editcomp1.addKeyListener(new EnterkeyHandler());
		if (editcomp1 instanceof CTextArea) {
			editcomp1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "nouse");
			editcomp1.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "nouse");
			editcomp1 = ((CTextArea) editcomp1).getTextarea();
		}

		editcomp.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "nouse");
		editcomp1.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "nouse");
		editcomp.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "nouse");
		editcomp1.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "nouse");

		JComponent comp = dbcolumndisplayinfo.getEditComponent();
		if (comp instanceof CFormatTextField) {
			((CFormatTextField) comp).setChangelistener(this);
		} else if (comp instanceof CComboBox) {
			((CComboBox) comp).addItemListener(new ComboboxListener());
		} else if (comp instanceof CCheckBox) {
			((CCheckBox) comp).addItemListener(new CheckboxListener());
		}

		JComponent comp1 = dbcolumndisplayinfo.getEditComponent1();
		if (comp1 instanceof CFormatTextField) {
			((CFormatTextField) comp1).setChangelistener(new Changelistener1());
		} else if (comp1 instanceof CComboBox) {
			((CComboBox) comp1).addItemListener(new ComboboxListener());
		} else if (comp1 instanceof CCheckBox) {
			((CCheckBox) comp1).addItemListener(new ComboboxListener());
		}

		if (dbcolumndisplayinfo.getColtype().equalsIgnoreCase("date")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String strnow = df.format(new Date());
			if (dbcolumndisplayinfo.isWithtime()) {
				if (((CFormatTextField) comp).getText().startsWith("0000")) {
					((CFormatTextField) comp).setValue(strnow + " 00:00:00");
					((CFormatTextField) comp1).setValue(strnow + " 23:59:59");
				}
			} else {
				if (((CFormatTextField) comp).getText().startsWith("0000")) {
					((CFormatTextField) comp).setValue(strnow);
					((CFormatTextField) comp1).setValue(strnow);
				}
			}
		}
	}

	class ComboboxListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			String value = (String) e.getItem();
			if (value.length() > 0) {
				cbUse.setSelected(true);
			} else {
				cbUse.setSelected(false);
			}
		}
	}

	class CheckboxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			cbUse.setSelected(cb.isSelected());
			/*
			 * String value = (String) e.getItem(); if (value.length() > 0) {
			 * cbUse.setSelected(true); } else { cbUse.setSelected(false); }
			 */}
	}

	/**
	 * 后面控件的编辑监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class Changelistener1 implements CFormatTextFieldListener {
		public void cancelHov(String editorname, String newvalue,
				String oldvalue) {
			dbcolumndisplayinfo.setComp1firehov(true);
		}

		public boolean confirmHov(String editorname, String newvalue,
				String oldvalue) {
			dbcolumndisplayinfo.setComp1firehov(true);
			DBTableModel result = dbcolumndisplayinfo.getHov().getResult();
			if (result != null) {
				querycond.on_hov(dbcolumndisplayinfo, result);
				return true;
			}
			return false;
		}

		public void invokeHov(String editorname, String newvalue,
				String oldvalue) {
			dbcolumndisplayinfo.setComp1firehov(true);
			if (newvalue.length() == 0) {
				// 清空hov
				JComponent comp = dbcolumndisplayinfo.getEditComponent1();
				if (comp instanceof CFormatTextField) {
					((CFormatTextField) comp).setValue("");
				}
				return;
			}

			String otherwheres = "";
			HovcondIF hovcondif = querycond.getHovcond(mdflag);
			if (hovcondif != null) {
				otherwheres = hovcondif.getHovOtherWheres(editorname);
			}
			dbcolumndisplayinfo.invokeHov(newvalue, oldvalue, otherwheres);
		}

		public boolean isHov(String editorname) {
			// TODO Auto-generated method stub
			dbcolumndisplayinfo.setComp1firehov(true);
			return Querycondline.this.isHov(editorname);

		}

		public void onchanged(CFormatTextField comp, String value,
				String oldvalue) {
			if (dbcolumndisplayinfo.isUsehov()) {
				dbcolumndisplayinfo.setComp1firehov(true);
				String otherwheres = "";
				HovcondIF hovcondif = querycond.getHovcond(mdflag);
				if (hovcondif != null) {
					otherwheres = hovcondif
							.getHovOtherWheres(dbcolumndisplayinfo.getColname());
				}
				dbcolumndisplayinfo.invokeHov(value, oldvalue, otherwheres);
			} else {
				cbUse.setSelected(value != null && value.length() > 0);
				if(value != null && value.length() > 0){
					//fireParentdialogConfirm(comp);
				}
			}
		}

	}

	/**
	 * 触发控件所在dialog的ACTION_QUERY事件 
	 */
	void fireParentdialogConfirm(JComponent comp){
		Component parent;
		parent=comp;
		while((parent=(Component) parent.getParent())!=null){
			if(parent instanceof CQueryDialog){
				CQueryDialog dlg=(CQueryDialog)parent;
				dlg.onconfirm(true);
				break;
			}
		}
	}
	
	
	/**
	 * 数据变化事件,触发HOV
	 */
	public void onchanged(CFormatTextField comp, String value, String oldvalue) {
		dbcolumndisplayinfo.setComp1firehov(false);
		if (dbcolumndisplayinfo.isUsehov()) {
			dbcolumndisplayinfo.setComp1firehov(false);
			String otherwheres = "";
			HovcondIF hovcondif = querycond.getHovcond(mdflag);
			if (hovcondif != null) {
				otherwheres = hovcondif.getHovOtherWheres(dbcolumndisplayinfo
						.getColname());
			}
			dbcolumndisplayinfo.invokeHov(value, oldvalue, otherwheres);
		} else {
			cbUse.setSelected(value != null && value.length() > 0);
			if(value != null && value.length() > 0){
				//fireParentdialogConfirm(comp);
			}

		}
	}

	/**
	 * 返回是否使用hov
	 */
	public boolean isHov(String editorname) {
		return dbcolumndisplayinfo.isUsehov();
	}

	/**
	 * 调用HOV
	 * 
	 * @param editorname列名
	 * @param newvalue
	 *            现在值
	 * @param oldvalue
	 *            原来的值
	 */
	public void invokeHov(String editorname, String newvalue, String oldvalue) {
		dbcolumndisplayinfo.setComp1firehov(false);
		if (newvalue.length() == 0) {
			// 清空hov
			JComponent comp = dbcolumndisplayinfo.getEditComponent();
			if (comp instanceof CFormatTextField) {
				((CFormatTextField) comp).setValue("");
			}
			return;
		}
		String otherwheres = "";
		HovcondIF hovcondif = querycond.getHovcond(mdflag);
		if (hovcondif != null) {
			otherwheres = hovcondif.getHovOtherWheres(editorname);
		}
		if (opvalue.equals("列举in")) {
			// 说明用了in
			// dbcolumndisplayinfo.showHovDialog();
			((CFormatTextField) dbcolumndisplayinfo.getEditComponent())
					.setValue(newvalue);
		} else {
			dbcolumndisplayinfo.invokeHov(newvalue, oldvalue, otherwheres);
		}
	}

	/**
	 * 确定hov
	 * 
	 * @param editorname
	 *            列名
	 * @param newvalue
	 *            现在值
	 * @param oldvalue
	 *            原来的值
	 */
	public boolean confirmHov(String editorname, String newvalue,
			String oldvalue) {
		dbcolumndisplayinfo.setComp1firehov(false);
		DBTableModel result = dbcolumndisplayinfo.getHov().getResult();
		if (result != null) {
			querycond.on_hov(dbcolumndisplayinfo, result);
			return true;
		}
		return false;
	}

	/**
	 * 取消hov
	 */
	public void cancelHov(String editorname, String newvalue, String oldvalue) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	/**
	 * 隐藏HOV
	 */
	public void hideHov() {
		if (dbcolumndisplayinfo.getHov() != null) {
			dbcolumndisplayinfo.getHov().hide();
		}
	}

	/**
	 * 清除输入
	 */
	public void clearControl() {
		JComponent comp = dbcolumndisplayinfo.getEditComponent();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String strnow = df.format(new Date());

		if (comp instanceof CFormatTextField) {
			((CFormatTextField) comp).setValue("");
			if (dbcolumndisplayinfo.getColtype().equalsIgnoreCase("date")) {
				if (dbcolumndisplayinfo.isWithtime()) {
					((CFormatTextField) comp).setValue(strnow + " 00:00:00");
				} else {
					((CFormatTextField) comp).setValue(strnow);
				}
			}
		} else if (comp instanceof CComboBox) {
			((CComboBox) comp).setSelectedIndex(-1);
			comp.invalidate();
			comp.repaint();
		}
		JComponent comp1 = dbcolumndisplayinfo.getEditComponent1();
		if (comp1 instanceof CFormatTextField) {
			((CFormatTextField) comp1).setValue("");
			if (dbcolumndisplayinfo.getColtype().equalsIgnoreCase("date")) {
				if (dbcolumndisplayinfo.isWithtime()) {
					((CFormatTextField) comp1).setValue(strnow + " 23:59:59");
				} else {
					((CFormatTextField) comp1).setValue(strnow);
				}
			}
		} else if (comp1 instanceof CComboBox) {
			((CComboBox) comp1).setSelectedIndex(-1);
			comp1.invalidate();
			comp1.repaint();
		}
		if (cbUse != null) {
			cbUse.setSelected(false);
			if (dbcolumndisplayinfo.isQuerymust()) {
				cbUse.setSelected(true);
			}
		}
	}

	/**
	 * 返回后面的编辑控件
	 * 
	 * @return
	 */
	public JComponent getCondEditcomp1() {
		return dbcolumndisplayinfo.getEditComponent1();
	}

	/**
	 * 释放内存
	 */
	public void freeMemory() {
		if (dbcolumndisplayinfo != null) {
			dbcolumndisplayinfo.freeMemory();
		}
	}

	public String getMdflag() {
		return mdflag;
	}

	class EnterkeyHandler implements KeyListener{

		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == 10){
				JComponent comp=dbcolumndisplayinfo.getEditComponent();
				if(comp instanceof CFormatTextField){
					CFormatTextField textf=(CFormatTextField)comp;
					try {
						textf.commitEdit();
					} catch (ParseException e1) {
						e1.printStackTrace();
						return;
					}
				}
				if(!dbcolumndisplayinfo.isHovwindowvisible()){
					fireParentdialogConfirm(comp);
				}
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}
		
	}
	
	/*
	 * protected void addEnterkeyTraver(JComponent comp) { KeyStroke enterkey =
	 * KeyStroke .getKeyStroke(KeyEvent.VK_ENTER, 0, false); Set<AWTKeyStroke>
	 * focusTraversalKeys = comp
	 * .getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	 * HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
	 * focusTraversalKeys); hasset.add(enterkey);
	 * comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
	 * hasset); }
	 */
}
