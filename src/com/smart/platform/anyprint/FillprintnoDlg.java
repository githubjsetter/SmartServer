package com.smart.platform.anyprint;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CComboBoxModel;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 反填规则设置
 * 
 * @author Administrator
 * 
 */
public class FillprintnoDlg extends CDialogOkcancel {
	Category logger = Category.getInstance(FillprintnoDlg.class);

	DBTableModel masterdbmodel = null;
	private CNumberTextField textSerialnoid;
	private JTextField textTablename;
	private CComboBox cbColumnname;
	private JTextField textPkcolname;
	private DBTableModel masterdmcolnamedm;
	private CComboBox cbDbmodelcolname;
	protected JTextField textPrintflagcolname;
	private JTextField textPrintmanidColname;
	private JTextField textPrintdate;
	private JTextField textTablename1;
	private JTextField textPkcolname1;
	private CComboBox cbDbmodelcolname1;

	public FillprintnoDlg(java.awt.Frame owner, DBTableModel masterdbmodel) {
		super(owner, "填写打印单号规则设置", true);
		this.masterdbmodel = masterdbmodel;
		init();
		localCenter();
	}

	void init() {
		GridBagLayout g = new GridBagLayout();
		Container cp = getContentPane();
		cp.setLayout(g);

		JLabel lb;
		lb = new JLabel("外部序列号");
		int y = 0;
		Insets insets = new Insets(1, 1, 1, 1);
		Dimension csize = new Dimension(150, 27);
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		textSerialnoid = new CNumberTextField(0);
		cp
				.add(textSerialnoid, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		textSerialnoid.setPreferredSize(csize);
		JButton btn;
		btn = new JButton("...");
		btn.setActionCommand("serialnohov");
		btn.addActionListener(this);
		cp
				.add(btn, new GridBagConstraints(2, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		// ///////////////////////////
		lb = new JLabel("填写表名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		textTablename = new JTextField();
		textTablename.setPreferredSize(csize);
		cp
				.add(textTablename, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		btn = new JButton("...");
		btn.setActionCommand("tablehov");
		btn.addActionListener(this);
		cp
				.add(btn, new GridBagConstraints(2, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// //////////////////////////////////
		lb = new JLabel("填写字段名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		cbColumnname = new CComboBox();
		cbColumnname.setPreferredSize(csize);
		cp
				.add(cbColumnname, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// //////////////////////////////////
		lb = new JLabel("表主键列名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		textPkcolname = new JTextField();
		textPkcolname.setEditable(false);
		textPkcolname.setPreferredSize(csize);
		cp
				.add(textPkcolname, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// //////////////////////////////////
		lb = new JLabel("表主键对应数据源列名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		cbDbmodelcolname = new CComboBox();
		cbDbmodelcolname.setPreferredSize(csize);
		cp
				.add(cbDbmodelcolname, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// 数据源列
		if (masterdbmodel != null) {
			masterdmcolnamedm = createCnamedm();
			Enumeration<DBColumnDisplayInfo> en = masterdbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				String cname = colinfo.getColname().toUpperCase();
				int newr = masterdmcolnamedm.getRowCount();
				masterdmcolnamedm.appendRow();
				masterdmcolnamedm.setItemValue(newr, "cname", cname);
				masterdmcolnamedm.setItemValue(newr, "ccname", cname);
			}
			cbDbmodelcolname.setModel(new CComboBoxModel(masterdmcolnamedm,
					"cname", "ccname"));
		}

		// ///打印标志
		lb = new JLabel("打印标志字段");
		y++;
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						30, 0, 1, 0), 0, 0));
		textPrintflagcolname = new JTextField("printflag");
		textPrintflagcolname.setPreferredSize(csize);
		cp.add(textPrintflagcolname, new GridBagConstraints(1, y, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						30, 0, 1, 0), 0, 0));
		// ///打印人ID字段
		lb = new JLabel("打印人ID字段");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		textPrintmanidColname = new JTextField("printmanid");
		textPrintmanidColname.setPreferredSize(csize);
		cp
				.add(textPrintmanidColname, new GridBagConstraints(1, y, 1, 1,
						1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		// ///打印日期
		lb = new JLabel("打印日期字段");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		textPrintdate = new JTextField("printdate");
		textPrintdate.setPreferredSize(csize);
		cp
				.add(textPrintdate, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// ///////////////////////////
		lb = new JLabel("打印标志字段表名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		textTablename1 = new JTextField();
		textTablename1.setPreferredSize(csize);
		textTablename1.setEditable(false);
		cp
				.add(textTablename1, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		btn = new JButton("...");
		btn.setActionCommand("tablehov1");
		btn.addActionListener(this);
		cp
				.add(btn, new GridBagConstraints(2, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// //////////////////////////////////
		lb = new JLabel("打印标志字段表主键");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		textPkcolname1 = new JTextField();
		textPkcolname1.setEditable(false);
		textPkcolname1.setPreferredSize(csize);
		cp
				.add(textPkcolname1, new GridBagConstraints(1, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		lb = new JLabel("表主键对应数据源列名");
		y++;
		cp
				.add(lb, new GridBagConstraints(0, y, 1, 1, 1, 1,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		cbDbmodelcolname1 = new CComboBox();
		cbDbmodelcolname1.setPreferredSize(csize);
		cp
				.add(cbDbmodelcolname1, new GridBagConstraints(1, y, 1, 1, 1,
						1, GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		// 数据源列
		cbDbmodelcolname1.setModel(new CComboBoxModel(masterdmcolnamedm,
				"cname", "ccname"));

		// ///////////下部//////////////////////////
		y++;
		JPanel bottompane = super.createOkcancelPane();
		cp.add(bottompane, new GridBagConstraints(0, y, 3, 1, 1, 1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0,
				0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("serialnohov".equals(cmd)) {
			serialnohov();
		} else if ("tablehov".equals(cmd)) {
			tablehov();
		} else if ("tablehov1".equals(cmd)) {
			tablehov1();
		}
		super.actionPerformed(e);
	}

	void serialnohov() {
		Pub_serialno_hov hov = new Pub_serialno_hov();
		DBTableModel dm = hov.showDialog(this, "选择外部序列号", "", "", "");
		if (dm == null)
			return;
		String serialnoid = dm.getItemValue(0, "serialnoid");
		textSerialnoid.setText(serialnoid);

	}

	void tablehov() {
		SelecttabviewHov hov = new SelecttabviewHov();
		DBTableModel result = hov.showDialog(this, "选择表", "", "", "");
		if (result == null) {
			return;
		}
		String tablename = result.getItemValue(0, "tname");
		textTablename.setText(tablename);
		fetchTablerelateinfo(tablename, 0);
	}

	void tablehov1() {
		SelecttabviewHov hov = new SelecttabviewHov();
		DBTableModel result = hov.showDialog(this, "选择表", "", "", "");
		if (result == null) {
			return;
		}
		String tablename = result.getItemValue(0, "tname");
		textTablename1.setText(tablename);
		fetchTablerelateinfo(tablename, 1);
	}

	void fetchTablerelateinfo(String tablename, int index) {

		try {
			String pkcolname = getTablepkcol(tablename);
			if (index == 0)
				textPkcolname.setText(pkcolname);
			else
				textPkcolname1.setText(pkcolname);

			// 找到下是不是有相同的主数据源列名
			if (index == 0) {
				for (int i = 0; i < masterdmcolnamedm.getRowCount(); i++) {
					if (masterdmcolnamedm.getItemValue(i, "cname")
							.equalsIgnoreCase(pkcolname)) {
						cbDbmodelcolname.setSelectedIndex(i);
						break;
					}
				}
			} else {
				for (int i = 0; i < masterdmcolnamedm.getRowCount(); i++) {
					if (masterdmcolnamedm.getItemValue(i, "cname")
							.equalsIgnoreCase(pkcolname)) {
						cbDbmodelcolname1.setSelectedIndex(i);
						break;
					}
				}

			}

			// 检查表列
			String sql = "select cname,cname from col where tname='"
					+ tablename.toUpperCase() + "' order by colno";
			RemotesqlHelper sh = new RemotesqlHelper();
			DBTableModel coldm = sh.doSelect(sql, 0, 1000);
			int rt = coldm.getRowCount();
			CComboBoxModel cbmodel = new CComboBoxModel(coldm, "cname", "cname");
			rt = coldm.getRowCount();
			if (index == 0)
				cbColumnname.setModel(cbmodel);
			else
				cbDbmodelcolname1.setModel(cbmodel);

			// 我们猜一下printno
			for (int i = 0; index == 0 && i < coldm.getRowCount(); i++) {
				String cname = coldm.getItemValue(i, "cname");
				if (cname.indexOf("PRINTNO") >= 0) {
					// 加1是因为第0个为空
					cbColumnname.setSelectedIndex(i);
					break;
				}
			}

		} catch (Exception e) {
			logger.error("Error", e);
			errorMessage("错误", e.getMessage());
		}
	}

	String getTablepkcol(String tname) throws Exception {
		String sql = "select column_name from USER_CONS_COLUMNS where constraint_name in ( "
				+ " select constraint_name from user_constraints where table_name='"
				+ tname.toUpperCase() + "' and constraint_type='P')";

		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel result = sqlh.doSelect(sql, 0, 1);
		if (result.getRowCount() == 0)
			return "";
		return result.getItemValue(0, "column_name");
	}

	DBTableModel createCnamedm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("cname", "varchar");
		cols.add(col);
		col = new DBColumnDisplayInfo("ccname", "varchar");
		cols.add(col);
		return new DBTableModel(cols);
	}

	public String getSerialnoid() {
		return textSerialnoid.getText();
	}

	public void setSerialnoid(String textSerialnoid) {
		this.textSerialnoid.setText(textSerialnoid);
	}

	public String getTablename() {
		return textTablename.getText();
	}

	public void setTablename(String textTablename) {
		this.textTablename.setText(textTablename);
		fetchTablerelateinfo(textTablename, 0);
	}

	public String getFillColumnname() {
		String s = (String) cbColumnname.getSelectedItem();
		if (s == null)
			s = "";
		return s;
	}

	public void setFillColumnname(String cbColumnname) {
		this.cbColumnname.setSelectedItem(cbColumnname);
	}

	public String getPkcolname() {
		return textPkcolname.getText();
	}

	public void setPkcolname(String textPkcolname) {
		this.textPkcolname.setText(textPkcolname);
	}

	public String getDbmodelcolname() {
		String s = (String) cbDbmodelcolname.getSelectedItem();
		if (s == null)
			s = "";
		return s;
	}

	public void setDbmodelcolname(String cbDbmodelcolname) {
		this.cbDbmodelcolname.setSelectedItem(cbDbmodelcolname);
	}

	public String getPrintflagcolname() {
		return (String) textPrintflagcolname.getText();
	}

	public void setPrintflagcolname(String s) {
		textPrintflagcolname.setText(s);
	}

	public String getPrintmanidColname() {
		return (String) textPrintmanidColname.getText();
	}

	public void setPrintmanidColname(String s) {
		textPrintmanidColname.setText(s);
	}

	public String getPrintdateColname() {
		return (String) textPrintdate.getText();
	}

	public void setPrintdateColname(String s) {
		textPrintdate.setText(s);
	}

	public String getTablename1() {
		return textTablename1.getText();
	}

	public void setTablename1(String textTablename) {
		this.textTablename1.setText(textTablename);
		fetchTablerelateinfo(textTablename, 1);
	}

	public String getPkcolname1() {
		return textPkcolname1.getText();
	}

	public void setPkcolname1(String textPkcolname) {
		this.textPkcolname1.setText(textPkcolname);
	}

	public String getDbmodelcolname1() {
		String s = (String) cbDbmodelcolname1.getSelectedItem();
		if (s == null)
			s = "";
		return s;
	}

	public void setDbmodelcolname1(String cbDbmodelcolname) {
		this.cbDbmodelcolname1.setSelectedItem(cbDbmodelcolname);
	}

	public String getExpr() {
		StringBuffer sb = new StringBuffer();
		sb.append(getSerialnoid());
		sb.append(":");
		sb.append(getTablename());
		sb.append(":");
		sb.append(getFillColumnname());
		sb.append(":");
		sb.append(getPkcolname());
		sb.append(":");
		sb.append(getDbmodelcolname());
		sb.append(":");
		sb.append(getPrintflagcolname());
		sb.append(":");
		sb.append(getPrintmanidColname());
		sb.append(":");
		sb.append(getPrintdateColname());
		sb.append(":");
		sb.append(getTablename1());
		sb.append(":");
		sb.append(getPkcolname1());
		sb.append(":");
		sb.append(getDbmodelcolname1());

		String expr = sb.toString();
		return expr;
	}

	
	@Override
	protected void onOk() {
		//检查参数
		if(getTablename1().trim().length()==0 ||
				getPkcolname1().trim().length()==0 ||
				getDbmodelcolname1().trim().length()==0){
			warnMessage("提示", "因为没有完整填写打印标志字段相关表的表名,主键列名和数据源对应列," +
					"所以无法回填打印标志字段打印日期等.");
		}
		super.onOk();
	}

	public static void main(String[] args) {
		FillprintnoDlg dlg = new FillprintnoDlg(null, null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
