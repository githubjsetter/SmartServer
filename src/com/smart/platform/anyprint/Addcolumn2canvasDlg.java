package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;

import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 选择数据源的列加到表头 表身 或表尾
 * 
 * @author Administrator
 * 
 */
public class Addcolumn2canvasDlg extends CDialog {
	Frame frm;
	DBTableModel srcdbmodel;
	DBTableModel dbmodel;
	CTable table;

	public Addcolumn2canvasDlg(Frame frm, DBTableModel srcdbmodel) {
		super(frm, "增加数据列", true);
		this.frm = frm;
		this.srcdbmodel = srcdbmodel;
		init();
		bind();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	void bind() {
		Enumeration<DBColumnDisplayInfo> en = srcdbmodel
				.getDisplaycolumninfos().elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "colname", colinfo.getColname());
			dbmodel.setItemValue(r, "title", colinfo.getTitle());
		}
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();

	}

	void init() {
		Container cp = this.getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		dbmodel = createcols();
		table = new CTable(dbmodel);
		table.setReadonly(true);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		int y = 0;
		cp.add(new JScrollPane(table), new GridBagConstraints(0, y, 2, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 12, 5, 5), 0, 0));
		y++;
		JLabel lb = new JLabel("添加方式");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		String addtype[]={"中文列名和列值","仅中文列名","仅列值"};
		cbAddtype = new CComboBox(addtype);
		cbAddtype.setSelectedIndex(2);
		cp.add(cbAddtype, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		y++;
		 lb = new JLabel("添加位置");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		String addpos[] = { "页头", "表头","表身","表尾","页脚" };
		cbAddpos = new CComboBox(addpos);
		cp.add(cbAddpos, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		//缺省加到表身
		cbAddpos.setSelectedIndex(2);

		JPanel tb = new JPanel();
		JButton btn = null;

		btn = new JButton("确定");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		tb.add(btn);
		btn = new JButton("取消");
		addEnterkeyTraver(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		tb.add(btn);
		y++;
		cp.add(tb, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 12, 5, 5), 0, 0));

	}

	@Override
	protected void enterkeyConfirm() {
		onOk();
	}

	boolean ok;
	private CComboBox cbAddpos;
	private CComboBox cbAddtype;

	public boolean isOk() {
		return ok;
	}

	void onOk() {
		ok = true;
		dispose();
	}

	void onCancel() {
		ok = false;
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}
	
	public CTable getColumntable(){
		return table;
	}
	
	
	public String getAddpos(){
		return (String)cbAddpos.getSelectedItem();
	}
	public String getAddtype(){
		return (String)cbAddtype.getSelectedItem();
	}

	DBTableModel createcols() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "列名");
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		cols.add(col);
		return new DBTableModel(cols);
	}
}
