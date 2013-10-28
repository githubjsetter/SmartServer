package com.smart.platform.gui.control;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListenerAdaptor;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.SteControlFactory;

/**
 * 单表编辑的hov
 * 
 * @author Administrator
 * 
 */
public abstract class CStehovEx extends CHovBase {
	protected CSteModel stemodel = null;

	protected abstract CSteModel createStemodel();

	public CStehovEx() throws HeadlessException {
		super();
		stemodel = createStemodel();
		JPanel rootp = stemodel.getRootpanel();
		// CFrame.dumpKeyaction(stemodel.getTable());
		stemodel.addActionListener(new Stehandle());
		table = dlgtable = stemodel.getTable();
	}

	public String getDefaultsql() {
		return stemodel.buildSelectSql("");
	}

	protected TableModel createTablemodel() {
		return stemodel.getDBtableModel();
	}

	@Override
	protected HovDialog createHovdialog(Dialog parent, String title) {
		return new StehovDlg(parent, title);
	}

	@Override
	protected HovDialog createHovdialog(Frame parent, String title) {
		return new StehovDlg(parent, title);
	}

	@Override
	protected CTable createTable() {
		super.createTable();
		CTable table = new CTable(stemodel.getDBtableModel());
		table.getColumn("行号").setCellRenderer(
				new CTableLinenoRender(stemodel.getDBtableModel()));
		return table;
	}

	protected class StehovDlg extends HovDialog {
		private boolean stecreated = false;

		public StehovDlg(Frame owner, String title) {
			super(owner, title);
		}

		public StehovDlg(Dialog owner, String title) {
			super(owner, title);
		}

		@Override
		protected void initDialog() {
			// 原来的初始化
			// do nothing
		}

		protected void initControl() {

			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			JPanel inputpane = buildQuerypanel(querycond);
			cp.add(inputpane, BorderLayout.NORTH);
			JPanel bottompane = createBottompanelDlg();
			cp.add(bottompane, BorderLayout.SOUTH);

			if (stemodel != null) {
				cp.add(stemodel.getRootpanel(), BorderLayout.CENTER);
			}

			SteControlFactory
					.setHotkey((JComponent) getContentPane(), stemodel);

			JComponent jcp = (JComponent) getContentPane();
			InputMap im = jcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
					ACTION_OK);
			jcp.getActionMap().put(ACTION_OK, new HovAction(ACTION_OK));
			setOtherHotkey(jcp);

			InputMap tablemap = stemodel.getTable().getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
			tablemap
					.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));

			// F10确定
			KeyStroke vkf10 = KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0, false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkf10,
					ACTION_OK);
			((JComponent) cp).getActionMap().put(ACTION_OK,
					new HovAction(ACTION_OK));

			KeyStroke vkf5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkf5,
					"more");
			((JComponent) cp).getActionMap().put("more", new HovAction("more"));

			KeyStroke vesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vesc,
					"dlgclose");
			((JComponent) cp).getActionMap().put("dlgclose",
					new CloseDlgAction());

		}

		@Override
		public void pack() {
			// 先调用setopid，再调用pack
			if (!stecreated) {
				initControl();
				stecreated = true;
			}
			localCenter();
			super.pack();
		}
	}

	/**
	 * 重载扩热键
	 * 
	 * @param jcp
	 */
	protected void setOtherHotkey(JComponent jcp) {

	}

	class Stehandle extends CSteModelListenerAdaptor {

		@Override
		public void on_doubleclick(int row, int col) {
			onOk();
		}
	}

	@Override
	public DBTableModel getResult() {
		if (stemodel.getModifiedDbmodel().getRowCount() > 0) {
			if (stemodel.doSave() != 0)
				return null;
		}
		DBTableModel resultmodel = new DBTableModel();
		DBTableModel model = stemodel.getDBtableModel();
		int row;
		if (usedlgwin) {
			row = stemodel.getRow();
		} else {
			row = table.getSelectedRow();
		}
		if (row < 0 || row >= model.getRowCount()) {
			return null;
		}
		resultmodel.setDisplaycolumninfos(model.getDisplaycolumninfos());
		resultmodel.addRecord(model.getRecordThunk(row));

		if (!this.oncheckResultdata(resultmodel)) {
			return null;
		}

		return resultmodel;

	}

	@Override
	public Querycond getQuerycond() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onOk() {
		stemodel.getTable().stopEdit();
		if (hovcallback != null) {
			if (!hovcallback.hovcallback_checkresult(stemodel.getRow(),
					stemodel.getDBtableModel(), stemodel.getTable())) {
				return;
			}
		}
		super.onOk();
		if (hovcallback != null) {
			if(EventQueue.isDispatchThread()){
				hovcallback.hovcallback_ok(stemodel.getRow(), stemodel
						.getDBtableModel(), stemodel.getTable());
			}else{
				Runnable r = new Runnable() {
					public void run() {
						hovcallback.hovcallback_ok(stemodel.getRow(), stemodel
								.getDBtableModel(), stemodel.getTable());

					}
				};
				try {
					SwingUtilities.invokeAndWait(r);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onCancel() {
		super.onCancel();
		if (hovcallback != null) {
			if(EventQueue.isDispatchThread()){
				hovcallback.hovcallback_cancel();
			}else{
				Runnable r = new Runnable() {
					public void run() {
						hovcallback.hovcallback_cancel();
					}
				};
				try {
					SwingUtilities.invokeAndWait(r);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public CSteModel getStemodel() {
		return stemodel;
	}

}
