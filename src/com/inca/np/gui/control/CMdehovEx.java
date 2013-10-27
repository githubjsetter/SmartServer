package com.inca.np.gui.control;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase.CloseDlgAction;
import com.inca.np.gui.control.CHovBase.HovAction;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModelListenerAdaptor;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.SteControlFactory;

/**
 * 在HOV上放置MDE
 * 
 * @author Administrator
 * 
 */
public abstract class CMdehovEx extends CHovBase {
	CMdeModel mdemodel = null;

	protected abstract CMdeModel createMdemodel();

	public CMdehovEx() throws HeadlessException {
		super();
		mdemodel = createMdemodel();
	}

	public String getDefaultsql() {
		return mdemodel.getMasterModel().buildSelectSql("");
	}

	protected TableModel createTablemodel() {
		return mdemodel.getMasterModel().getDBtableModel();
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
		CTable table = new CTable(mdemodel.getMasterModel().getDBtableModel());
		table.getColumn("行号").setCellRenderer(
				new CTableLinenoRender(mdemodel.getMasterModel()
						.getDBtableModel()));
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

			if (mdemodel != null) {
				JSplitPane splitpane = new JSplitPane(
						JSplitPane.VERTICAL_SPLIT, mdemodel.getMasterModel()
								.getRootpanel(), mdemodel.getDetailModel()
								.getRootpanel());

				splitpane.setDividerLocation(300);
				InputMap inputMap = splitpane
						.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
				inputMap = inputMap.getParent();
				inputMap.clear();
				cp.add(splitpane,BorderLayout.CENTER);
			}

			SteControlFactory
					.setHotkey((JComponent) getContentPane(), mdemodel);
			JComponent jcp = (JComponent) getContentPane();
			InputMap im = jcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
					ACTION_OK);
			jcp.getActionMap().put(ACTION_OK, new HovAction(ACTION_OK));

			InputMap tablemap = mdemodel.getMasterModel().getTable()
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
					.getParent();
			tablemap
					.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));
			tablemap = mdemodel.getDetailModel().getTable().getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
			tablemap
					.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));
			Dimension scrsize = getToolkit().getScreenSize();
			mdemodel.getMasterModel().addActionListener(new Masterstehandle());
			mdemodel.getDetailModel().addActionListener(new Detailstehandle());
			table = dlgtable = mdemodel.getMasterModel().getTable();

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
			((JComponent) cp).getActionMap().put("more",
					new HovAction("more"));

			KeyStroke vesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			((JComponent) cp).getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vesc, "dlgclose");
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

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(800,600);
		}
		
		
	}

	class Masterstehandle extends CSteModelListenerAdaptor {

		@Override
		public void on_doubleclick(int row, int col) {
			onOk();
		}
	}

	class Detailstehandle extends CSteModelListenerAdaptor {

		@Override
		public void on_doubleclick(int row, int col) {
			onOk();
		}
	}

	public CMdeModel getMdemodel(){
		return mdemodel;
	}

	@Override
	protected boolean autoReturn() {
		//不要自动返回
		return false;
	}

	@Override
	protected void on_retrieved() {
		super.on_retrieved();
		if(mdemodel.getMasterModel().getRowCount()>0){
			//查询第0条总单的细单
			mdemodel.getMasterModel().on_click(0, 0);
		}
	}

	@Override
	protected void onOk() {
		mdemodel.getMasterModel().getTable().stopEdit();
		mdemodel.getDetailModel().getTable().stopEdit();
		super.onOk();
	}


	
}
