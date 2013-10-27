package com.inca.np.gui.control;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.inca.np.filedb.FiledbManager;
import com.inca.np.filedb.FiledbSearchCond;
import com.inca.np.gui.control.CHovBase.HovAction;
import com.inca.np.gui.control.CMultiHov.DlgHovKeylistener;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.gui.ui.CTableheadUI;
import com.inca.np.selfcheck.DBColumnChecker;
import com.inca.np.selfcheck.SelfcheckError;
import com.inca.np.util.DBHelper;
import com.inca.np.util.DefaultNPParam;

/**
 * �ܵ�ϸĿhov
 * 
 * @author Administrator
 * 
 */
public abstract class CMdeHov extends CMultiHov {

	Category logger = Category.getInstance(CMdeHov.class);
	protected CTable dtltable = null;
	protected DBTableModel dtldbmodel = null;
	protected boolean dtleditable = false;
	protected Sumdbmodel dtlsumdbmodel = null;

	protected abstract DBTableModel createDetailTablemodel();

	@Override
	protected HovDialog createHovdialog(Frame parent, String title) {
		return new MdeHovDialog(parent, title);
	}

	@Override
	protected HovDialog createHovdialog(Dialog parent, String title) {
		return new MdeHovDialog(parent, title);
	}

	@Override
	protected boolean autoReturn() {
		return false;
	}

	/**
	 * ��ѯϸ������
	 * 
	 * @return
	 */
	protected abstract String getDetailtablename();

	protected abstract String getMastercolname();

	protected abstract String getDetailcolname();

	/**
	 * ����split�ķָ�λ��
	 * 
	 * @return
	 */
	protected int getDividerLocation() {
		return 200;
	}

	protected String dtlfilename = "";

	public CTable getDtltable() {
		return dtltable;
	}

	/**
	 * �Ի�����
	 */
	protected class MdeHovDialog extends CMdeHov.HovDialog {
		public boolean ok = false;

		public MdeHovDialog(Frame owner, String title) throws HeadlessException {
			super(owner, title);
			initDialog();
			catchEnterkey();
			this.localScreenCenter();
		}

		public MdeHovDialog(Dialog owner, String title)
				throws HeadlessException {
			super(owner, title);
			initDialog();
			catchEnterkey();
			this.localScreenCenter();
		}

		@Override
		protected void initDialog() {
			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());
			JPanel inputpane = buildQuerypanel(querycond);
			cp.add(inputpane, BorderLayout.NORTH);

			JPanel bottompane = createBottompanelDlg();
			cp.add(bottompane, BorderLayout.SOUTH);

			JSplitPane splitpane = new JSplitPane(getSplitDirection());

			cp.add(splitpane, BorderLayout.CENTER);
			splitpane.setDividerLocation(getDividerLocation());
			splitpane.setPreferredSize(new Dimension(800, 500));

			JPanel upperpanel = new JPanel();
			upperpanel.setLayout(new BorderLayout());
			createDlgDatapanel(upperpanel);
			dlgtable
					.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			dlgtable.getSelectionModel().addListSelectionListener(
					new TableSelectListener());

			InputMap dlginputmap = ((JComponent) cp)
					.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			KeyStroke vesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			dlginputmap.put(vesc, "dlgclose");
			((JComponent) cp).getActionMap().put("dlgclose",
					new CloseDlgAction());

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

			JPanel downpanel = new JPanel();
			downpanel.setLayout(new BorderLayout());
			createDetailPanel(downpanel);

			splitpane.setLeftComponent(upperpanel);
			splitpane.setRightComponent(downpanel);

		}

		@Override
		protected JPanel createBottompanelDlg() {
			JPanel toolpane = new JPanel();
			JButton buttonok = new JButton("ȷ��(F10)");
			buttonok.setActionCommand(ACTION_OK);
			buttonok.addActionListener(new HovAction(ACTION_OK));
			toolpane.add(buttonok);
			
			btnmore = new JButton("����(F5)");
			btnmore.setActionCommand("more");
			btnmore.addActionListener(new HovAction("more"));
			toolpane.add(btnmore);
			setBtnmoreEnable();

			addOtherbutton(toolpane);


			JButton btncancel = new JButton("ȡ��");
			btncancel.setActionCommand(ACTION_CANCEL);
			btncancel.addActionListener(new HovAction(ACTION_CANCEL));
			toolpane.add(btncancel);

			/*
			 * buttonStopretrieve = new JButton("�ر�");
			 * buttonStopretrieve.setActionCommand(ACTION_STOPRETRIEVE);
			 * buttonStopretrieve.addActionListener(new
			 * HovAction(ACTION_STOPRETRIEVE));
			 * toolpane.add(buttonStopretrieve);
			 */

			JPanel statuspane = new JPanel();

			lbstatus = new JLabel("�������������س�");
			lbstatus.setPreferredSize(new Dimension(600, 20));
			statuspane.add(lbstatus);

			JPanel jp = new JPanel();

			jp.setLayout(new BorderLayout());
			jp.add(toolpane, BorderLayout.NORTH);
			jp.add(statuspane, BorderLayout.SOUTH);

			return jp;
		}

		protected void localScreenCenter() {
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

			Dimension size = this.getPreferredSize();

			double x = (screensize.getWidth() - size.getWidth()) / 2.0;
			double y = (screensize.getHeight() - size.getHeight()) / 2.0;

			this.setLocation((int) x, (int) y);
		}

		void catchEnterkey() {
			KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
					false);
			JComponent jcp = (JComponent) getContentPane();
			jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					vkenter, "enterkeydown");
			jcp.getActionMap().put("enterkeydown", new EnterkeyHandle());
		}

		@Override
		public void setVisible(boolean v) {
			if (v) {
				Runnable r = new Runnable() {
					public void run() {
						querycond.get(0).getDbcolumndisplayinfo()
								.getEditComponent().requestFocus();
					}
				};
				SwingUtilities.invokeLater(r);
			}
			super.setVisible(v);
		}

	}

	@Override
	protected KeyListener getDlgHovKeylistener() {
		return new DlgHovKeylistener();
	}

	class DlgHovKeylistener implements KeyListener {
		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				// ���س���
				if (!dtltable.isFocusOwner()) {
					dtltable.requestFocus();
				} else {
					if (getResult() == null)
						return;
					dlgtable.confirm();
					dtltable.confirm();
					hovdialog.ok = true;
					hovdialog.dispose();
				}

			} else if (e.getKeyCode() == 27) {
				// ��ESC
				hovdialog.ok = false;
				hovdialog.dispose();
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

	protected class HovAction extends AbstractAction {
		public HovAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (ACTION_OK.equals(e.getActionCommand())) {
				if (getResult() == null)
					return;
				dlgtable.confirm();
				dtltable.confirm();
				hovdialog.ok = true;
				hovdialog.dispose();
			} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
				hide();
			} else if (ACTION_STOPRETRIEVE.equals(e.getActionCommand())) {
				stopRetrieve();
			} else if (ACTION_QUERY.equals(e.getActionCommand())) {
				doQuery();
			} else if ("more".equals(e.getActionCommand())) {
				retrieveMore();
			}
		}

	}

	class EnterkeyHandle extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			/*
			 * System.out.println("enter key is down,dlgtable
			 * focus?"+dlgtable.isFocusOwner()+"," + " dtltable
			 * focus?="+dtltable.isFocusOwner());
			 */
			if (dlgtable.isFocusOwner()) {
				dtltable.requestFocus();
			} else if (dtltable.isFocusOwner()) {
				dlgtable.confirm();
				dtltable.confirm();
				hovdialog.ok = true;
				hovdialog.dispose();
			} else {
				dtltable.requestFocus();
			}
		}

	}

	protected void createDetailPanel(JPanel cp) {
		dtltable = createDetailTable();
		dtltable.setSelectionMode(getDetailtableSelectMode());
		dtltable.addMouseListener(new DtltableMouseListener());
		dtltable.getTableHeader().setUI(new CTableheadUI());

		JScrollPane sp = new JScrollPane(dtltable);
		cp.add(sp, BorderLayout.CENTER);
	}

	protected int getDetailtableSelectMode() {
		return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	}

	class DtltableMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				dlgtable.confirm();
				dtltable.confirm();
				hovdialog.ok = true;
				hovdialog.dispose();
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

	protected int currow = -1;

	class TableSelectListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			currow = dm.getAnchorSelectionIndex();
			if (currow >= 0) {
				if (currow >= 0 && currow < tablemodel.getRowCount()) {
					queryDetail(currow);
				}
			}
		}

	}

	protected CTable createDetailTable() {
		if (dtldbmodel == null) {
			dtldbmodel = createDetailTablemodel();
		}
		dtlsumdbmodel = new Sumdbmodel(dtldbmodel, new Vector<String>());

		CEditableTable table = new CEditableTable(dtlsumdbmodel);
		table.setRowHeight(27);
		if (dtleditable) {
			table.setReadonly(false);
		} else {
			table.setReadonly(true);
		}

		InputMap map = table.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));

		// �س���ǰ
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
				"selectNextColumnCell");

		return table;
	}

	@Override
	protected void doQuery() {
		detailmap.clear();
		((DBTableModel) dtltable.getModel()).clearAll();
		dtltable.tableChanged(new TableModelEvent(dtltable.getModel()));
		super.doQuery();
	}

	/**
	 * key:�ܵ�tmppkid value:ϸ�����ڴ��
	 */
	protected HashMap<String, DBTableModel> detailmap = new HashMap<String, DBTableModel>();

	protected void queryDetail(int row) {
		if (row < 0)
			return;
		String tmppkid = ((DBTableModel) tablemodel).getTmppkid(row);
		DBTableModel dtlmodel = detailmap.get(tmppkid);
		if (dtlmodel != null) {
			// ��ʾϸ����ֵ
			dtlsumdbmodel = new Sumdbmodel(dtlmodel, null);
			dtltable.setModel(dtlsumdbmodel);
			dtltable.autoSize();
			if (dtltable.getRowCount() > 0) {
				dtltable.addRowSelectionInterval(0, 0);
				lbstatus.setText("����ѯ��" + dtldbmodel.getRowCount() + "��ϸ��");
			}
			return;
		}

		// ����ϸ��
		dtltable.setModel(createDetailTablemodel());

		DBTableModel dbtablemodel = ((DBTableModel) tablemodel);

		String masterv = dbtablemodel.getItemValue(row, getMastercolname());
		if (usefile) {
			retrieveDtlFromfile(row, masterv);
			return;
		}

		String sql = buildDetailSelectsql(masterv);

		// ���û��ϸ���ļ�¼,��Ҫ��ѯ
		QuerydtlThread dtlthread = new QuerydtlThread(currow, tmppkid, sql);
		dtlthread.start();

	}

	/**
	 * �������ܵ�ֵmasterv��ѯϸ����sql
	 * 
	 * @param masterv
	 * @return
	 */
	protected String buildDetailSelectsql(String masterv) {
		StringBuffer sb = new StringBuffer();
		Vector<DBColumnDisplayInfo> colinfos = dtldbmodel
				.getDisplaycolumninfos();
		Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (!colinfo.isDbcolumn())
				continue;
			if (sb.length() > 0)
				sb.append(",");
			sb.append(colinfo.getColname());
		}
		DBTableModel dbtablemodel = ((DBTableModel) tablemodel);
		DBColumnDisplayInfo mastccolinfo = dbtablemodel
				.getColumninfo(getMastercolname());
		String sql = "Select " + sb.toString() + " from "
				+ this.getDetailtablename();
		sql += " where " + this.getDetailcolname() + "=";
		if (mastccolinfo.getColtype() == DBColumnDisplayInfo.COLTYPE_VARCHAR) {
			sql += "'" + masterv + "'";
		} else {
			sql += masterv;
		}
		String dtlwheres = getOtherDtlwheres();
		if (dtlwheres != null && dtlwheres.length() > 0) {
			sql = DBHelper.addWheres(sql, dtlwheres);
		}
		return sql;
	}

	/**
	 * ����QuerydtlThread,��ʱ400ms,����ܵ����Ǹղŵ���,�ٽ��в�ѯ. ��ֹ������Ա���ٹ�����ɵĶ�����ò�ѯ
	 * 
	 * @author Administrator
	 * 
	 */
	protected class QuerydtlThread extends Thread {
		int memrow = -1;
		String tmppkid = null;
		String sql;

		public QuerydtlThread(int currow, String tmppkid, String sql) {
			this.memrow = currow;
			this.tmppkid = tmppkid;
			this.sql = sql;
		}

		public void run() {
			try {
				Thread.sleep(DefaultNPParam.mderetrievedtldeplay);
			} catch (InterruptedException e) {
			}
			if (memrow != currow) {
				// System.out.println("currow="+currow+",�ϴε�row="+memrow+",���Բ���ѯϸ����");
				return;
			}

			DBTableModel newdtlmodel = createDetailTablemodel();
			detailmap.put(tmppkid, newdtlmodel);
			// ��ѯϸ��
			// ���в�ѯ
			logger.debug("hov ϸ����ѯsql:" + sql);
			dtlsumdbmodel = new Sumdbmodel(newdtlmodel, null);
			dtltable.setModel(dtlsumdbmodel);
			newdtlmodel.setRetrievelistener(new RetrieveListener());
			lbstatus.setText("��ʼ��ѯϸ��");
			newdtlmodel.doRetrieve(sql, 1000);

		}

	}

	/**
	 * ϸ������������
	 * 
	 * @return
	 */
	protected String getOtherDtlwheres() {
		return "";
	}

	protected void retrieveDtlFromfile(int row, String masterv) {
		// ���ļ���ѯϸ��
		lbstatus.setText("��ʼ��ѯϸ��");
		FiledbSearchCond cond = new FiledbSearchCond();
		cond.colname = getDetailcolname();
		cond.op = "=";
		cond.value = masterv;
		FiledbSearchCond conds[] = new FiledbSearchCond[1];
		conds[0] = cond;

		DBTableModel newdtlmodel = createDetailTablemodel();
		detailmap.put(((DBTableModel) tablemodel).getTmppkid(row), newdtlmodel);
		dtltable.setModel(newdtlmodel);

		FiledbManager db = FiledbManager.getInstance();
		DBTableModel dbmodel = null;
		try {
			dbmodel = db.searchFile(dtlfilename, conds, 100);
		} catch (Exception e) {
			logger.error("ERROR", e);
			return;
		}
		newdtlmodel.bindMemds(dbmodel);
		dtltable.tableChanged(new TableModelEvent(dtltable.getModel()));
		if (dtltable.getRowCount() > 0) {
			dtltable.getSelectionModel().setSelectionInterval(0, 0);
		}
		lbstatus.setText("����ѯ��" + dtldbmodel.getRowCount() + "��ϸ��");
	}

	/**
	 * ����splitpane�ķָ��,ȱʡ�� JSplitPane.VERTICAL_SPLIT
	 * 
	 * @return
	 */
	protected int getSplitDirection() {
		return JSplitPane.VERTICAL_SPLIT;
	}

	/**
	 * ϸ����ѯ����
	 */
	protected void on_detailRetrieved() {

	}

	class RetrieveListener implements DBTableModelEvent {
		public void retrieveError(DBTableModel dbmodel, String errormessage) {
		}

		public void retrieveFinish(DBTableModel dbmodel) {
			dtlsumdbmodel.fireDatachanged();
			dtltable.tableChanged(new TableModelEvent(dtltable.getModel()));
			dtltable.autoSize();
			if (dtltable.getRowCount() > 0) {
				dtltable.getSelectionModel().setSelectionInterval(0, 0);
			}
			lbstatus.setText("����ѯ��" + dtldbmodel.getRowCount() + "��ϸ��");
			on_detailRetrieved();
		}

		public int retrievePart(DBTableModel dbmodel, int startrow,
				int endrow, int retrivedsize, int inflatesize) {
			dtlsumdbmodel.fireDatachanged();
			dtltable.tableChanged(new TableModelEvent(dtltable.getModel()));
			dtltable.autoSize();
			if (dtltable.getRowCount() > 0) {
				dtltable.getSelectionModel().setSelectionInterval(0, 0);
			}
			lbstatus.setText("����ѯ��" + dtldbmodel.getRowCount() + "��ϸ��");
			return 0;
		}

		public void retrieveStart(DBTableModel dbmodel) {
		}

	}

	public static void main(String[] argv) {
		try {
			DefaultNPParam.debug=1;
			DefaultNPParam.develop=1;
			DefaultNPParam.debugdbip = "192.9.200.1";
			DefaultNPParam.debugdbpasswd = "xjxty";
			DefaultNPParam.debugdbsid = "data";
			DefaultNPParam.debugdbusrname = "xjxty";
			DefaultNPParam.prodcontext = "npserver";

			
			Demo_hov hov = new CMdeHov.Demo_hov();
			hov.setUsefile(false);
			hov.setFilename("pub_goods");
			hov.setDtlfilename("pub_goods_detail");

			DBTableModel result = hov.showDialog(null, "����CMdeHov");
			if (result == null) {
				System.err.println("ѡHOVʧ��");
				return;
			}
			CTable table = hov.getDlgtable();
			for (int i = 0; i < table.getRowCount(); i++) {
				if (table.isRowSelected(i)) {
					System.out.println("ѡ���˵�" + i + "��");
				}
			}
			CTable dtltable = hov.getDtltable();
			for (int i = 0; i < dtltable.getRowCount(); i++) {
				if (dtltable.isRowSelected(i)) {
					System.out.println("ϸ��ѡ���˵�" + i + "��");
				}
			}

		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}

	static class Demo_hov extends CMdeHov {
		public Demo_hov() throws HeadlessException {
			super();
			editable = true;
			dtleditable = true;
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
			editor.setReadonly(false);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodsname", "varchar", "Ʒ��",
					false);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodstype", "varchar", "���", true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("prodarea", "varchar", "����", false);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodsunit", "varchar", "��λ", true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			return new DBTableModel(tablecolumndisplayinfos);
		}

		protected DBTableModel createDetailTablemodel() {
			Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo editor = new DBColumnDisplayInfo("goodsid",
					"number", "��ƷID", false);
			editor.setIspk(true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("goodsdtlid", "number", "��ϸID",
					false);
			editor.setUppercase(true);
			editor.setReadonly(true);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("packname", "varchar", "��װ��", true);
			editor.setUppercase(true);
			editor.setReadonly(false);
			tablecolumndisplayinfos.add(editor);

			editor = new DBColumnDisplayInfo("packsize", "nubmer", "��װ��С",
					false);
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

		@Override
		protected String getDetailcolname() {
			return "goodsid";
		}

		@Override
		protected String getDetailtablename() {
			return "pub_goods_detail";
		}

		@Override
		protected String getMastercolname() {
			return "goodsid";
		}
	}

	public String getDtlfilename() {
		return dtlfilename;
	}

	public void setDtlfilename(String dtlfilename) {
		this.dtlfilename = dtlfilename;
	}

	@Override
	public void freeMemory() {
		super.freeMemory();
		if (dtltable != null) {
			dtltable.freeMemory();
			dtldbmodel.freeMemory();
			dtldbmodel = null;
			dtltable = null;
		}
	}

	@Override
	public String selfCheck() {
		String s = super.selfCheck();
		// ���ϸ��

		// �����
		// ����form��
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "���ʧ��";
		}

		// ��ѯ������
		Vector<SelfcheckError> errors = new Vector<SelfcheckError>();

		// �����
		if (dtldbmodel == null) {
			dtldbmodel = createDetailTablemodel();
		}
		Vector<DBColumnDisplayInfo> cols = dtldbmodel.getDisplaycolumninfos();
		DBColumnChecker.checkOrder(cols, errors);

		Enumeration<SelfcheckError> en = errors.elements();
		while (en.hasMoreElements()) {
			SelfcheckError error = en.nextElement();
			out.print(this.getClass().getName() + ":");
			error.dump(out);
		}
		out.flush();

		String rets;
		try {
			rets = new String(bout.toByteArray(), "gbk");
			return s + rets;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "���ʧ��";
		}

	}

}
