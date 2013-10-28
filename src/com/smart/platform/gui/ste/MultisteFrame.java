package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.CTableLinenoRender;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 一总单，多细单。
 * 
 * @author Administrator
 * 
 */
public abstract class MultisteFrame extends Steframe {

	/**
	 * 细单ste向量
	 */
	Vector<Detailsteinfo> dtlsteinfos = new Vector<Detailsteinfo>();
	/**
	 * 左边的表
	 */
	protected CTable lefttable = null;
	private JScrollPane lefttablejsp;
	private JTabbedPane tabbedpane;
	
	/**
	 * 记录当前的行.
	 */
	protected int memrow=-1;

	public MultisteFrame() {
		createStes();
	}

	public MultisteFrame(String title) {
		super(title);
		createStes();
	}

	protected abstract void createStes();

	/**
	 * 增加细单
	 * 
	 * @param ste
	 * @mastercolname 总单列名
	 * @param relatecolname
	 *            关联列名
	 */
	protected void addDetailste(CSteModel ste, String mastercolname,
			String relatecolname) {
		// 不要细单的卡片窗口的热键
		ste.addActionListener(new DetailStelistener(ste));
		dtlsteinfos.add(new Detailsteinfo(ste, mastercolname, relatecolname));
	}

	class Detailsteinfo {
		private CSteModel ste;
		private String mastercolname;
		private String relatecolname;

		Detailsteinfo(CSteModel ste, String mastercolname, String relatecolname) {
			this.ste = ste;
			this.mastercolname = mastercolname;
			this.relatecolname = relatecolname;
		}

		public CSteModel getSte() {
			return ste;
		}

		public String getRelatecolname() {
			return relatecolname;
		}

		public String getMastercolname() {
			return mastercolname;
		}

	}

	/**
	 * 构造。center是splitpane
	 */
	@Override
	protected void initControl() {
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsp.setBorder(BorderFactory.createEmptyBorder());
		removeUpdownhotkey(jsp);

		Container cp = getContentPane();
		cp.add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(getHorizontalsize());
		if (stemodel == null) {
			stemodel = getStemodel();
		}
		stemodel.setShowformonly(true);

		// mastersterootpane上部是工具条，中部是table,下部是状态
		// 将 table放在jsp左

		// jsp右边又是一个JSplitPane。 上部放置masterste的steform
		JSplitPane jsp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		removeUpdownhotkey(jsp1);
		jsp.setRightComponent(jsp1);
		JPanel mastersterootpane = stemodel.getRootpanel();
		// 在 mastersterootpane 中找出toolbar。并放在NORTH
		CStetoolbar tb = getToolbar(mastersterootpane);
		if (tb != null) {
			cp.add(tb, BorderLayout.NORTH);
		}

		createLefttable();
		lefttable.setReadonly(true);
		lefttable.getSelectionModel().addListSelectionListener(
				new Tableselectionlistener());
		removeUpdownhotkey(lefttable);

		lefttablejsp = new JScrollPane(lefttable);
		removeUpdownhotkey(lefttablejsp);
		jsp.setLeftComponent(lefttablejsp);
		jsp1.setLeftComponent(mastersterootpane);

		stemodel.addActionListener(new Stelistener());

		tabbedpane = new JTabbedPane();
		tabbedpane.addChangeListener(new Tabchangelistener());

		removeUpdownhotkey(tabbedpane);
		jsp1.setRightComponent(tabbedpane);
		jsp1.setDividerLocation(getVerticalsize());

		// 创建细单
		Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
		while (en.hasMoreElements()) {
			Detailsteinfo dtlinfo = en.nextElement();
			dtlinfo.getSte().setHotkeylistener(stemodel);
			tabbedpane.add(dtlinfo.getSte().getRootpanel(), dtlinfo.getSte()
					.getTitle());
		}
		/*
		 * JPanel jptest=new JPanel(); jptest.add(new JLabel("test"));
		 * tabbedpane.add("test",jptest);
		 * 
		 * JPanel jptest1=new JPanel(); jptest1.add(new JLabel("test1"));
		 * tabbedpane.add("test1",jptest1);
		 */
		setHotkey();
		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);
		
		stemodel.onstartRun();
	}

	CStetoolbar getToolbar(JPanel mastersterootpane) {
		CStetoolbar tb = null;
		for (int i = 0; i < mastersterootpane.getComponentCount(); i++) {
			Component jcomp = mastersterootpane.getComponent(i);
			if (jcomp instanceof CStetoolbar) {
				tb = (CStetoolbar) jcomp;
				mastersterootpane.remove(jcomp);
				break;
			} else if (jcomp instanceof JPanel) {
				CStetoolbar tmptb = getToolbar((JPanel) jcomp);
				if (tmptb != null)
					return tmptb;
			}
		}
		return tb;
	}

	@Override
	public void pack() {
		super.pack();
		stemodel.getRootpanel();
		stemodel.showForm();
		stemodel.bindDataSetEnable(-1);
		// stemodel.getSteform().setEnable(false);

		Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
		while (en.hasMoreElements()) {
			Detailsteinfo dtlinfo = en.nextElement();
			if (dtlinfo.getSte().isShowformonly()) {
				dtlinfo.getSte().showForm();
				dtlinfo.getSte().bindDataSetEnable(-1);
			}
		}
	}

	class Tabchangelistener implements ChangeListener {
		Detailsteinfo dtlinfo = null;

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tp = (JTabbedPane) e.getSource();
			int index = tp.getSelectedIndex();
			if(index<0){
				return;
			}

			Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
			for (int i = 0; en.hasMoreElements(); i++) {
				if (i == index)
					continue;
				Detailsteinfo tmpinfo = en.nextElement();
				tmpinfo.getSte().commitEdit();
			}

			dtlinfo = dtlsteinfos.elementAt(index);
			Runnable r = new Runnable() {
				public void run() {
					if (dtlinfo.getSte().isFormvisible()) {
						dtlinfo.getSte().getForm().onActive(false);
					} else {
						CTable tb = dtlinfo.getSte().table;
						TableCellEditor tce = tb.getCellEditor();
						tb.requestFocus();
					}
				}
			};
			SwingUtilities.invokeLater(r);
		}

	}

	protected void createLefttable() {
		TableColumnModel tcm = stemodel.getTable().getColumnModel();
		DefaultTableColumnModel newtcm = new DefaultTableColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			newtcm.addColumn(tcm.getColumn(i));
		}

		DBTableModel dbmodel = stemodel.getSumdbmodel();
		lefttable = new CTable(dbmodel, newtcm);
		TableColumn column = lefttable.getColumn("行号");
		column
				.setCellRenderer(new CTableLinenoRender(stemodel
						.getSumdbmodel()));

		int sm=stemodel.getTable().getSelectionModel().getSelectionMode();
		lefttable.getSelectionModel().setSelectionMode(sm);
	}

	/**
	 * 由当前总单值，查询所有的细单
	 */
	protected void retrieveDetails() {
		logger.debug("retrieveDetails");
		int mrow = stemodel.getRow();
		logger.debug("retrieveDetails mrow="+mrow);
		if (mrow < 0 || mrow >= stemodel.getRowCount())
			return;
		if(memrow==mrow){
			logger.debug("retrieveDetails mrow="+mrow+" not need retrievedetails,return");
			return;
		}
		memrow=mrow;
		int mdbstatus = stemodel.getdbStatus(mrow);
		Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
		while (en.hasMoreElements()) {
			Detailsteinfo dtlinfo = en.nextElement();
			if (mdbstatus == RecordTrunk.DBSTATUS_NEW) {
				dtlinfo.getSte().reset();
			} else {
				// 根据条件查询
				logger.debug("retrieveDetails retrieve detail,dtlinfo="+dtlinfo.ste.getTitle());
				retrieveDetail(dtlinfo);
			}
		}
	}

	protected void retrieveDetail(Detailsteinfo dtlinfo) {
		int mrow = stemodel.getRow();
		if (mrow < 0 || mrow >= stemodel.getRowCount())
			return;
		setWaitcursor();
		String mvalue = stemodel.getItemValue(mrow, dtlinfo.getMastercolname());
		String dtlwheres = dtlinfo.getRelatecolname() + "='" + mvalue + "'";
		dtlinfo.getSte().doQuery(dtlwheres);
	}

	protected class Stelistener extends CSteModelListenerAdaptor {

		public int on_beforedel(int row) {
			
			return 0;
		}

		public int on_beforemodify(int row) {
			
			return 0;
		}

		public int on_beforenew() {
			
			return 0;
		}

		public int on_beforequery() {
			
			return 0;
		}

		public int on_beforesave() {
			saveAll();
			// 返回-1，不要再保存了
			return -1;
		}

		public int on_beforeundo() {
			
			return 0;
		}

		public int on_checkrow(int row, DBTableModel model) {
			
			return 0;
		}

		public void on_click(int row, int col) {
			retrieveDetails();
		}

		public void on_del(int row) {
			lefttable.tableChanged(new TableModelEvent(lefttable.getModel()));
			row = stemodel.getRow();
			lefttable.getSelectionModel().setSelectionInterval(row, row);
			lefttable.scrollToCell(row, 1);
		}

		public void on_doubleclick(int row, int col) {
			

		}

		public void on_itemvaluechange(int row, String colname, String value) {
			lefttable.tableChanged(new TableModelEvent(lefttable.getModel(),	
					row));
			Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
			while (en.hasMoreElements()) {
				Detailsteinfo dtlinfo = en.nextElement();
				if (dtlinfo.getMastercolname().equalsIgnoreCase(colname)) {
					retrieveDetail(dtlinfo);
				}
			}

		}

		public void on_modify(int row) {
		}

		public void on_new(int row) {
			lefttable.tableChanged(new TableModelEvent(lefttable.getModel()));
			lefttable.scrollToCell(row, 1);
			lefttable.getSelectionModel().setSelectionInterval(row, row);
		}

		public void on_rclick(int row, int col) {
			

		}

		public void on_retrieved() {
			lefttable.tableChanged(new TableModelEvent(lefttable.getModel()));
			lefttable.autoSize();
			if (stemodel.getRowCount() > 0) {
				stemodel.doModify();
				lefttable.getSelectionModel().setSelectionInterval(0, 0);
				retrieveDetails();
			}else{
				//如果没有记录,要把细单ste全清
				Enumeration<Detailsteinfo>en=dtlsteinfos.elements();
				while(en.hasMoreElements()){
					en.nextElement().getSte().reset();
				}
			}
		}

		
		@Override
		public int on_retrievepart() {
			lefttable.tableChanged(new TableModelEvent(lefttable.getModel()));
			return super.on_retrievepart();
		}

		public void on_retrievestart() {
			setWaitcursor();
			memrow=-1;
		}

		public void on_save() {
			

		}

		public void on_tablerowchanged(int newrow, int newcol, int oldrow,
				int oldcol) {
			

		}

		public void on_undo() {
			

		}

		public void on_saved(int errorct) {
			if (errorct == 0) {
				// 保存细单
				Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
				while (en.hasMoreElements()) {
					Detailsteinfo dtlinfo = en.nextElement();
					if (dtlinfo.getSte().getDBtableModel().getModifiedData()
							.getRowCount() > 0) {
						dtlinfo.getSte().doSave();
					}
				}
			}
		}

		public int on_beforeclose() {
			
			return 0;
		}

	}

	protected class DetailStelistener extends CSteModelListenerAdaptor {
		CSteModel dtlstemodel = null;

		public DetailStelistener(CSteModel dtlstemodel) {
			this.dtlstemodel = dtlstemodel;
		}

		public int on_beforedel(int row) {
			
			return 0;
		}

		public int on_beforemodify(int row) {
			
			return 0;
		}

		public int on_beforenew() {
			int row = stemodel.getRow();
			if (row < 0)
				return -1;
			if (stemodel.getdbStatus(row) == RecordTrunk.DBSTATUS_NEW) {
				// warnMessage("提示", "请先保存新增的记录，再增加细单记录");
				saveAll();
				if (stemodel.getdbStatus(row) == RecordTrunk.DBSTATUS_NEW) {
					errorMessage("提示", "保存" + stemodel.getTitle() + "失败");
					return -1;
				}
			}
			return 0;
		}

		public int on_beforequery() {
			
			return 0;
		}

		public int on_beforesave() {
			
			return 0;
		}

		public int on_beforeundo() {
			
			return 0;
		}

		public int on_checkrow(int row, DBTableModel model) {
			
			return 0;
		}

		public void on_click(int row, int col) {
		}

		public void on_del(int row) {
		}

		public void on_doubleclick(int row, int col) {
			

		}

		public void on_itemvaluechange(int row, String colname, String value) {
		}

		public void on_modify(int row) {
		}

		public void on_new(int row) {
			// 设置关联列的值
			if (stemodel.getRow() < 0)
				return;
			Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
			while (en.hasMoreElements()) {
				Detailsteinfo dtlinfo = en.nextElement();
				if (dtlinfo.getSte() == dtlstemodel) {
					String mcolname = dtlinfo.getMastercolname();
					String mv = stemodel.getItemValue(stemodel.getRow(),
							mcolname);
					dtlstemodel.setItemValue(row, dtlinfo.getRelatecolname(),
							mv);
					break;
				}
			}
		}

		public void on_rclick(int row, int col) {
		}

		public void on_retrieved() {
			int selectedindex = tabbedpane.getSelectedIndex();
			boolean isquerying = false;
			for (int i = 0; i < dtlsteinfos.size(); i++) {
				Detailsteinfo dtlinfo = dtlsteinfos.elementAt(i);
				if (dtlinfo.getSte().getDBtableModel().isquerying()) {
					isquerying = true;
				}
				if (dtlinfo.getSte() != dtlstemodel)
					continue;
				if (selectedindex == i && dtlstemodel.getRowCount() > 0
						&& dtlstemodel.isShowformonly()) {
					dtlstemodel.doModify();
				} else {
				}
			}
			if (!isquerying) {
				setDefaultcursor();
			}
		}

		public void on_retrievestart() {
			

		}

		public void on_save() {
			

		}

		public void on_tablerowchanged(int newrow, int newcol, int oldrow,
				int oldcol) {
			

		}

		public void on_undo() {
			

		}

		public void on_saved(int errorct) {
			

		}

		public int on_beforeclose() {
			
			return 0;
		}

	}

	class Tableselectionlistener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// 当前选中的行是
			if (e.getValueIsAdjusting())
				return;

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			int newrow = dm.getAnchorSelectionIndex();
			// 调用tablechanged()后，dm.getAnchorSelectionIndex()返回-1,这不是本意
			if (newrow < 0)
				return;
			stemodel.setRow(newrow);
		}
	}

	class Watchthread extends Thread {
		public void run() {
			while (true) {
				whereFocus();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void whereFocus() {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		Component focusc = kfm.getFocusOwner();
		System.out.print(focusc);
		if (focusc instanceof CTable) {
			CTable tb = (CTable) focusc;
			DBTableModel dbmodel = (DBTableModel) tb.getModel();
			System.out.print(" colct=" + tb.getColumnCount() + ",v="
					+ dbmodel.getItemValue(0, 1));
		}

		System.out.println();
	}

	@Override
	protected void setHotkey() {

		// F8 查询
		ActionListener listener = stemodel;
		JComponent compcp = (JComponent) getContentPane();
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf8, CSteModel.ACTION_QUERY);
		compcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new SteActionListener(CSteModel.ACTION_QUERY, listener));

		// Ctrl+N 新增
		KeyStroke vkctrln = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkctrln, CSteModel.ACTION_NEW);
		compcp.getActionMap().put(CSteModel.ACTION_NEW,
				new SteActionListener(CSteModel.ACTION_NEW, listener));

		// Ctrl+Z UNDO
		KeyStroke vkctrlz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkctrlz, CSteModel.ACTION_UNDO);
		compcp.getActionMap().put(CSteModel.ACTION_UNDO,
				new SteActionListener(CSteModel.ACTION_UNDO, listener));

		// 删除
		KeyStroke vkctrld = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkctrld, CSteModel.ACTION_DEL);
		compcp.getActionMap().put(CSteModel.ACTION_DEL,
				new SteActionListener(CSteModel.ACTION_DEL, listener));

		// F9 保存
		KeyStroke vkf9 = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf9, CSteModel.ACTION_SAVE);
		compcp.getActionMap().put(CSteModel.ACTION_SAVE,
				new SteActionListener(CSteModel.ACTION_SAVE, listener));

		// f2 编辑
		KeyStroke vkf2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf2, CSteModel.ACTION_MODIFY);
		compcp.getActionMap().put(CSteModel.ACTION_MODIFY,
				new SteActionListener(CSteModel.ACTION_MODIFY, listener));

		/*
		 * // esc 隐藏编辑窗 KeyStroke vkesc =
		 * KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		 * compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkesc,
		 * CSteModel.ACTION_HIDEFORM);
		 * compcp.getActionMap().put(CSteModel.ACTION_HIDEFORM, new
		 * SteActionListener(CSteModel.ACTION_HIDEFORM, listener));
		 */
		// 打印
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkctrlp, CSteModel.ACTION_PRINT);
		compcp.getActionMap().put(CSteModel.ACTION_PRINT,
				new SteActionListener(CSteModel.ACTION_PRINT, listener));
		
		//退出
		KeyStroke altx = KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altx,
				CSteModel.ACTION_EXIT);
		compcp.getActionMap().put(CSteModel.ACTION_EXIT,
				new SteActionListener(CSteModel.ACTION_EXIT, listener));
		

		//上下键
		KeyStroke vkup=KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		KeyStroke vkdown=KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkup,
				CSteModel.ACTION_PRIOR);
		compcp.getActionMap().put(CSteModel.ACTION_PRIOR,
				new SteActionListener(CSteModel.ACTION_PRIOR, listener));

		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkdown,
				CSteModel.ACTION_NEXT);
		compcp.getActionMap().put(CSteModel.ACTION_NEXT,
				new SteActionListener(CSteModel.ACTION_NEXT, listener));

	}

	/**
	 * 水平分割位置
	 * 
	 * @return
	 */
	protected int getHorizontalsize() {
		return 200;
	}

	/**
	 * 垂直分割位置
	 * 
	 * @return
	 */
	protected int getVerticalsize() {
		return 300;
	}

	private void removeUpdownhotkey(JComponent jp) {
		KeyStroke removekeys[] = {
				//不应该屏蔽上下键
				//KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
				//KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false),
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false) };

		for (int i = 0; i < removekeys.length; i++) {
			jp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					removekeys[i], "nouse");
		}
	}

	public void saveAll() {
		int ct = 0;
		stemodel.commitEdit();
		stemodel.doHideform();
		ct += stemodel.getModifiedDbmodel().getRowCount();
		Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
		while (en.hasMoreElements()) {
			Detailsteinfo dtlinfo = en.nextElement();
			dtlinfo.getSte().commitEdit();
			dtlinfo.getSte().doHideform();
			ct += dtlinfo.getSte().getDBtableModel().getModifiedData()
					.getRowCount();
		}
		if (ct == 0) {
			warnMessage("提示", "没有数据需要保存");
			return;
		}
		CDefaultProgress progress = new CDefaultProgress(this);
		Savethread savet = new Savethread(progress);
		savet.start();
		progress.show();
		logger.debug("保存线程返回");
	}

	class Savethread extends Thread {
		CDefaultProgress progress = null;

		Savethread(CDefaultProgress progress) {
			this.progress = progress;
		}

		public void run() {
			savetoserver(progress);
		}
	}

	protected void savetoserver(CDefaultProgress progress) {
		// 必须先保存细单再存总单。如果先存总单， 在保存后当前一变，细单记录全丢了。
		logger.debug("savetoserver() start");
		Enumeration<Detailsteinfo> en = dtlsteinfos.elements();
		while (en.hasMoreElements()) {
			Detailsteinfo dtlinfo = en.nextElement();
			if (dtlinfo.getSte().getDBtableModel().getModifiedData()
					.getRowCount() > 0
					&& dtlinfo.getSte().on_beforesave() == 0) {
				progress.appendMessage("正在保存" + dtlinfo.getSte().getTitle()
						+ ",等待服务器响应......");
				logger.debug("正在保存" + dtlinfo.getSte().getTitle()
						+ ",等待服务器响应......");
				dtlinfo.getSte().savetoserver(null);
				logger.debug("收到服务器响应,etErrorcount()="
						+ dtlinfo.getSte().getErrorcount());

				if (dtlinfo.getSte().getErrorcount() == 0) {
					progress.appendMessage("保存成功");
				} else {
					progress.appendMessage("失败记录"
							+ dtlinfo.getSte().getErrorcount() + "条");
				}
			}
		}

		if (stemodel.getModifiedDbmodel().getRowCount() > 0
				&& stemodel.on_beforesave() == 0) {
			progress.appendMessage("正在保存" + stemodel.getTitle()
					+ ",等待服务器响应......");
			logger.debug("正在保存" + stemodel.getTitle() + ",等待服务器响应......");
			stemodel.savetoserver(null);
			logger.debug("收到服务器响应,getErrorcount()=" + stemodel.getErrorcount());
			if (stemodel.getErrorcount() == 0) {
				progress.appendMessage("保存成功");
			} else {
				progress.appendMessage("失败记录" + stemodel.getErrorcount() + "条");
			}
		}

		if (progress != null) {
			progress.messageBox("提示", "保存结束");
		}
	}

	@Override
	public void setOpid(String opid) {
		super.setOpid(opid);
		stemodel.setOpid(opid);
	}
	
}
