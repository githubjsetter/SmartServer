package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 测试数据源
 * 
 * @author Administrator
 * 
 */
public class QuerydsDlg extends CDialog {
	Category logger = Category.getInstance(QuerydsDlg.class);
	private JTextArea textCond;
	Frame frm;
	Printplan plan;
	private DBTableModel dbmodel;
	private CTable table;

	public QuerydsDlg(Frame frm, Printplan plan) {
		super(frm, "测试数据源", true);
		this.frm = frm;
		this.plan = plan;
		init();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	/**
	 * 生成一个表格，显示plan的数据。
	 */
	void init() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());
		cp.add(jp, BorderLayout.NORTH);
		jp.add(new JLabel("入口参数"));
		textCond = new JTextArea(2, 80);
		textCond.setText(plan.getDefaultinputparam());
		jp.add(textCond);

		JButton btn = new JButton("查询");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("query");
		btn.addActionListener(this);
		jp.add(btn);

		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			dbmodel = plan.createFulldatamodel();
			table = new CTable(dbmodel);
			cp.add(new JScrollPane(table), BorderLayout.CENTER);
		} catch (Exception e) {
			logger.error("error", e);
			JOptionPane.showMessageDialog(this, "数据源错误" + e.getMessage());
			return;
		} finally {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		JPanel tb = new JPanel();
		cp.add(tb, BorderLayout.SOUTH);
		btn = new JButton("关闭");
		addEnterkeyTraver(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		tb.add(btn);

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	@Override
	protected void enterkeyConfirm() {
		onOk();
	}

	void onOk() {
		ok = true;
		dispose();
	}

	void onCancel() {
		ok = false;
		dispose();
	}

	boolean ok = false;

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		} else if (cmd.equals("query")) {
			doquery();
		}
	}

	void doquery() {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			plan.setDefaultinputparam(textCond.getText());
			plan.setInputparam(textCond.getText());
			plan.getDbmodel();
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
		} catch (Exception e) {
			logger.error("error", e);
			JOptionPane.showMessageDialog(this, "数据源错误" + e.getMessage());
			return;
		} finally {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		}
	}

}
