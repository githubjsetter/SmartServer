package com.inca.np.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.CButton;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 设置排序 传入DBTableModel的排序表达式. sort expr:=列名:asc|desc[列名:asc|desc]
 * 
 * @author Administrator
 * 
 */
public class SortsetupDialog extends CDialog {
	DBTableModel dbmodel;
	String sortexpr;
	CEditableTable table = null;
	DBTableModel tablemodel = null;

	public SortsetupDialog(Frame frame, DBTableModel dbmodel, String sortexpr) {
		super(frame, "设置排序方法", true);
		this.dbmodel = dbmodel;
		this.sortexpr = sortexpr;
		initDialog();
		setHotkey();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	protected void initDialog() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(new JLabel("双击列名加减列"), BorderLayout.NORTH);
		cp.add(createbottomPanel(), BorderLayout.SOUTH);

		cp.add(createMidpanel(), BorderLayout.CENTER);

	}

	JList listcol;

	/**
	 * 左边显示列,右边显示排序列
	 * 
	 * @return
	 */
	JPanel createMidpanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		ArrayList<String> ar = new ArrayList<String>();
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.getColtype().equals("行号"))
				continue;
			ar.add(col.getColname());
		}
		String[] colnames = new String[ar.size()];
		ar.toArray(colnames);

		listcol = new JList(colnames);
		listcol.addMouseListener(new ListMouseListener());
		jp.add(new JScrollPane(listcol), BorderLayout.WEST);

		// 右边放排序表
		createTable();
		table.addMouseListener(new TableMouseListener());

		bindValue();
		jp.add(new JScrollPane(table), BorderLayout.EAST);

		return jp;
	}

	void createTable() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname", "varchar",
				"列名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("asc", "number", "升序");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		col.setReadonly(false);
		cols.add(col);

		tablemodel = new DBTableModel(cols);
		table = new CEditableTable(tablemodel);

	}

	void bindValue() {
		String ss[] = sortexpr.split(":");
		if (ss.length % 2 == 0) {
			for (int i = 0; ss != null && i < ss.length; i++) {
				tablemodel.appendRow();
				int r = tablemodel.getRowCount() - 1;
				tablemodel.setItemValue(r, "colname", ss[i]);
				if (ss[i + 1].equals("asc")) {
					tablemodel.setItemValue(r, "asc", "1");
				} else {
					tablemodel.setItemValue(r, "asc", "0");
				}
				i++;
			}
		}
		tablemodel.appendRow();
		table.tableChanged(new TableModelEvent(tablemodel));
	}

	protected JPanel createbottomPanel() {
		JPanel jp = new JPanel();
		CButton btnok = new CButton("确定");
		jp.add(btnok);
		btnok.setActionCommand("ok");
		btnok.addActionListener(this);

		CButton btncancel = new CButton("取消");
		jp.add(btncancel);
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(this);
		return jp;
	}

	protected void setHotkey() {
		JComponent jcp = (JComponent) this.getContentPane();
		InputMap im = jcp
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		jcp.getActionMap().put("ok", new DlgAction("ok"));
		jcp.getActionMap().put("cancel", new DlgAction("cancel"));
	}

	class DlgAction extends AbstractAction {
		DlgAction(String cmd) {
			super(cmd);
			putValue(AbstractAction.ACTION_COMMAND_KEY, cmd);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("ok")) {
				onOk();
			} else if (e.getActionCommand().equals("cancel")) {
				onCancel();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) {
			onOk();
		} else if (e.getActionCommand().equals("cancel")) {
			onCancel();
		}
	}

	boolean ok = false;

	protected void onOk() {
		TableCellEditor tce=table.getCellEditor();
		if(tce!=null){
			tce.stopCellEditing();
		}
		ok = true;
		dispose();
	}

	protected void onCancel() {
		ok = false;
		dispose();
	}

	public boolean getOk() {
		return ok;
	}

	public String getExpr() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tablemodel.getRowCount() - 1; i++) {
			String colname = tablemodel.getItemValue(i, "colname");
			String asc = tablemodel.getItemValue(i, "asc");
			if (i > 0) {
				sb.append(":");
			}
			sb.append(colname);
			sb.append(":");
			sb.append(asc.equals("1") ? "asc" : "desc");
		}
		return sb.toString();
	}

	class ListMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				String colname = (String) listcol.getSelectedValue();
				int r = tablemodel.getRowCount() - 1;
				tablemodel.setItemValue(r, "colname", colname);
				tablemodel.setItemValue(r, "asc", "1");
				table.tableChanged(new TableModelEvent(tablemodel));
				tablemodel.appendRow();
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class TableMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int r = table.getRow();
				if (r == tablemodel.getRowCount() - 1)
					return;
				if (r >= 0) {
					tablemodel.removeRow(r);
					table.tableChanged(new TableModelEvent(tablemodel));
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		ste.getRootpanel();

		String expr = "goodsid:asc:goodsname:desc";
		SortsetupDialog dlg = new SortsetupDialog(null, ste.getDBtableModel(),
				expr);
		dlg.pack();
		dlg.setVisible(true);
		System.out.println(dlg.getOk());
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}

}
