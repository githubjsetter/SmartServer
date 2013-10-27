package com.inca.np.gui.control;

import java.awt.AWTKeyStroke;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase.PlainTablecellRender;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.util.DefaultNPParam;

/**
 * ��ѡ�ɱ༭hov
 * 
 * @author Administrator
 * 
 */
public abstract class CMultiHov extends CHovBase {

	/**
	 * �ɱ༭
	 */
	protected boolean editable = false;

	protected Vector<DBColumnDisplayInfo> formcolumndisplayinfos = null;

	protected DBTableModel dlgdbmodel = null;

	@Override
	protected CTable createTable() {
		if (tablemodel == null) {
			tablemodel = (DBTableModel) createTablemodel();
		}

		sumdbmodel = new Sumdbmodel((DBTableModel) tablemodel,
				new Vector<String>());
		CEditableTable table = new CEditableTable(sumdbmodel);
		table.setRowHeight(27);

		Vector<DBColumnDisplayInfo> formcolumndisplayinfos = tablemodel
				.getDisplaycolumninfos();

		TableColumnModel cm = table.getColumnModel();
		for (int c = 0; c < cm.getColumnCount(); c++) {
			TableColumn column = cm.getColumn(c);
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(column.getModelIndex());
			if (colinfo.getTablecolumnwidth() <= 0) {
				column.setPreferredWidth(65);
			} else {
				column.setPreferredWidth(colinfo.getTablecolumnwidth());
			}
			PlainTablecellRender cellRenderer = new PlainTablecellRender(
					colinfo);
			column.setCellRenderer(cellRenderer);
		}

		return table;
	}

	@Override
	protected void on_retrieved() {
		sumdbmodel.fireDatachanged();
		table.autoSize();
	}

	@Override
	protected void createDlgDatapanel(Container cp) {
		super.createDlgDatapanel(cp);
		dlgtable.setSelectionMode(getTableselectionmode());
		dlgdbmodel = (DBTableModel) dlgtable.getModel();
		formcolumndisplayinfos = dlgdbmodel.getDisplaycolumninfos();

		if (editable) {
			dlgtable.setReadonly(false);
			// ȥ��F2���༭ enter��һ��
			InputMap map = dlgtable.getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
			map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
			KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
					false);
			map.remove(vkenter);
			// �س���ǰ
			map.put(vkenter, "selectNextColumnCell");
		}
	}

	/**
	 * ���ر��е�ѡ��ģʽ. ȱʡ�� ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
	 * 
	 * @return
	 */
	protected int getTableselectionmode() {
		return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	}

	public CTable getDlgtable() {
		return dlgtable;
	}

	public CFormatTextField getEditor() {
		return new CPlainTextField();
	}

	protected MouseListener getDlgmouselistener() {
		return new DlgHovMouseListener();
	}

	@Override
	protected boolean autoReturn() {
		return false;
	}

	class DlgHovMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				dlgtable.confirm();
				onOk();
			}
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

	void removeEnterkey(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);
	}

	protected KeyListener getDlgHovKeylistener() {
		return new DlgHovKeylistener();
	}

	class DlgHovKeylistener implements KeyListener {
		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				// ���س���
				if (getResult() == null)
					return;
				dlgtable.confirm();
				hovdialog.ok = true;
				hovdialog.dispose();
			} else if (e.getKeyCode() == 27) {
				// ��ESC
				hovdialog.ok = false;
				hovdialog.dispose();
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

	public static void main(String[] argv) {
		try {
			DefaultNPParam.debug = 1;
			DefaultNPParam.develop = 1;
			DefaultNPParam.debugdbip = "192.9.200.1";
			DefaultNPParam.debugdbpasswd = "xjxty";
			DefaultNPParam.debugdbsid = "data";
			DefaultNPParam.debugdbusrname = "xjxty";
			DefaultNPParam.prodcontext = "npserver";

			Demo_hov hov = new CMultiHov.Demo_hov();
			hov.showDialog(null, "����CMultiHov");
			DBTableModel result = hov.getResult();
			if (result == null) {
				System.err.println("ѡHOVʧ��");
			}
			CTable table = hov.getDlgtable();
			for (int i = 0; i < table.getRowCount(); i++) {
				if (table.isRowSelected(i)) {
					System.out.println("ѡ���˵�" + i + "��");
				}
			}

		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}

	static class Demo_hov extends CMultiHov {
		public Demo_hov() throws HeadlessException {
			super();
			editable = true;
		}

		public String getDefaultsql() {
			return "select goodsid,opcode,goodspinyin,goodsname,goodstype,prodarea,goodsunit from pub_goods"
					+ " order by opcode";
		}

		public Querycond getQuerycond() {
			Querycond querycond = new Querycond();

			DBColumnDisplayInfo colinfo = null;

			colinfo = new DBColumnDisplayInfo("opcode", "varchar", "������", false);
			colinfo.setUppercase(true);
			querycond.add(new Querycondline(querycond, colinfo));

			colinfo = new DBColumnDisplayInfo("goodspinyin", "varchar", "ƴ��",
					true);
			colinfo.setUppercase(true);
			querycond.add(new Querycondline(querycond, colinfo));

			colinfo = new DBColumnDisplayInfo("goodsname", "varchar", "Ʒ��",
					false);
			querycond.add(new Querycondline(querycond, colinfo));

			colinfo = new DBColumnDisplayInfo("goodsid", "number", "��ƷID", true);
			querycond.add(new Querycondline(querycond, colinfo));

			return querycond;
		}

		protected TableModel createTablemodel() {
			Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo editor = new DBColumnDisplayInfo("goodsid",
					"number", "��ƷID", false);
			editor.setIspk(true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("opcode", "varchar", "������", false);
			editor.setUppercase(true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodspinyin", "varchar", "ƴ��",
					true);
			editor.setUppercase(true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodsname", "varchar", "Ʒ��",
					false);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodstype", "varchar", "���", true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("prodarea", "varchar", "����", false);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodsunit", "varchar", "��λ", true);
			editor.setReadonly(false);
			tablecolumndisplayinfos.add(editor);

			return new DBTableModel(tablecolumndisplayinfos);
		}

		public String getDesc() {
			return "����CMultiHov";
		}

		public String[] getColumns() {
			return new String[] { "goodsid", "opcode", "goodspinyin",
					"goodsname", "goodstype", "prodarea", "goodsunit" };
		}
	}

}
