package com.smart.platform.auth;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 选择核算单元，门店等
 * 
 * @author Administrator
 * 
 */
public class SelectentryDlg extends CDialogOkcancel {
	DBTableModel dbmodel;
	private CTable table;

	public SelectentryDlg(JFrame owner, String title, DBTableModel dbmodel) {
		super(owner, title, true);
		this.dbmodel = dbmodel;
		init();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	void init() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		table = new CTable(dbmodel);
		table.setReadonly(true);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		table.getInputMap(JComponent.WHEN_FOCUSED).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "ok");
		table.getActionMap().put("ok", this);
		cp.add(new JScrollPane(table), BorderLayout.CENTER);

		cp.add(createOkcancelPane(), BorderLayout.SOUTH);
		table.autoSize();
		if (dbmodel.getRowCount() > 0)
			table.getSelectionModel().setSelectionInterval(0, 0);

	}

	public int getSelectrow() {
		return table.getSelectedRow();
	}
}
