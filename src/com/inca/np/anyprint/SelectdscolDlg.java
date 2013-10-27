package com.inca.np.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.inca.np.gui.control.CDialog;

/**
 * 选择关联列
 * 
 * @author Administrator
 * 
 */
public class SelectdscolDlg extends CDialog {
	String colnames[];
	String mcolnames[];
	private JList listColnames;

	public SelectdscolDlg(Dialog owner, String colnames[],String mcolnames[]) {
		super(owner, "选择关联的主数据源列", true);
		this.colnames = colnames;
		this.mcolnames = mcolnames;
		init();
		bindValue();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);

	}

	void bindValue() {
		listColnames.setSelectedIndex(0);
		mlistColnames.setSelectedIndex(0);
	}

	void init() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		listColnames = new JList(colnames);
		listColnames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cp.add(new JScrollPane(listColnames), BorderLayout.WEST);

		if (mcolnames != null) {
			mlistColnames = new JList(mcolnames);
		}else{
			String ss[]={"入口参数"};
			mlistColnames = new JList(ss);
		}
		mlistColnames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cp.add(new JScrollPane(mlistColnames), BorderLayout.EAST);

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
		cp.add(tb, BorderLayout.SOUTH);

	}

	boolean ok;
	private JList mlistColnames;

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

	public String getSelectcolname() {
		return (String) listColnames.getSelectedValue();
	}

	public String getMaincolname() {
		return (String) mlistColnames.getSelectedValue();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 400);
	}

}
