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
 * ѡ������Դ���мӵ���ͷ ���� ���β
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
		super(frm, "����������", true);
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
		JLabel lb = new JLabel("��ӷ�ʽ");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		String addtype[]={"������������ֵ","����������","����ֵ"};
		cbAddtype = new CComboBox(addtype);
		cbAddtype.setSelectedIndex(2);
		cp.add(cbAddtype, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		y++;
		 lb = new JLabel("���λ��");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		String addpos[] = { "ҳͷ", "��ͷ","����","��β","ҳ��" };
		cbAddpos = new CComboBox(addpos);
		cp.add(cbAddpos, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		//ȱʡ�ӵ�����
		cbAddpos.setSelectedIndex(2);

		JPanel tb = new JPanel();
		JButton btn = null;

		btn = new JButton("ȷ��");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		tb.add(btn);
		btn = new JButton("ȡ��");
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
		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "������");
		cols.add(col);
		return new DBTableModel(cols);
	}
}
