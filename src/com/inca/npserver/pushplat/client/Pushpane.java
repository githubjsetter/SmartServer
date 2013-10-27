package com.inca.npserver.pushplat.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.CFormlayout;
import com.inca.np.gui.control.CMessageDialog;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.MultisteFrame;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.gui.tbar.TBar;
import com.inca.np.gui.tbar.TButton;
import com.inca.npbi.client.design.BIReportFrame;
import com.inca.npclient.system.Clientframe;
import com.inca.npserver.pushplat.common.Pushinfo;
import com.inca.npserver.pushplat.common.PushshowIF;
import com.inca.npx.ap.Aphelper;

/**
 * 显示推送任务的panel
 * 
 * @author user
 * 
 */
public class Pushpane extends JPanel implements PushshowIF, ActionListener {
	DBTableModel pushdbmodel;
	private Pushtable pushtable;
	private JLabel lbstatus;
	private JComboBox cbMinute;
	private boolean notifystartimmedate = false;
	Vector<Pushinfo> pushinfos = new Vector<Pushinfo>();
	Category logger = Category.getInstance(Pushpane.class);
	private Sumdbmodel pushtablemodel;
	private TButton btnrun;

	public Pushpane() {
		setLayout(new BorderLayout());
		add(createToolbar(), BorderLayout.NORTH);
		add(createStatusbar(), BorderLayout.SOUTH);

		pushdbmodel = createPushdm();
		Vector<String> sumcols = new Vector<String>();
		sumcols.add("rowcount");
		pushtablemodel = new Sumdbmodel(pushdbmodel, sumcols);
		pushtable = new Pushtable(pushtablemodel);
		pushtable.setReadonly(true);
		pushtable.addMouseListener(new TablemouseHandler());

		JScrollPane jsp = new JScrollPane(pushtable);
		add(jsp, BorderLayout.CENTER);
	}

	/**
	 * 建立工具条
	 * 
	 * @return
	 */
	JPanel createToolbar() {
		TBar jp = new TBar();
		CFormlayout formlayout = new CFormlayout(2, 2);
		jp.setLayout(formlayout);

		btnrun = null;
		btnrun = new TButton("立即处理");
		btnrun.setActionCommand("runop");
		btnrun.addActionListener(this);
		btnrun.setEnabled(false);
		jp.add(btnrun);

		TButton btn = null;
		JLabel lb=new JLabel("刷新间隔");
		jp.add(lb);
		String ss[] = { "1", "5", "15", "30" };
		cbMinute = new JComboBox(ss);
		cbMinute.setSelectedIndex(1);
		jp.add(cbMinute);
		lb=new JLabel("分钟");
		jp.add(lb);


		btn = new TButton("立即刷新");
		btn.setActionCommand("refresh");
		btn.addActionListener(this);
		jp.add(btn);

		return jp;
	}

	JPanel createStatusbar() {
		JPanel jp = new JPanel();
		CFormlayout formlayout = new CFormlayout(2, 2);
		jp.setLayout(formlayout);
		lbstatus = new JLabel();
		Dimension lbsize = new Dimension(360, 30);
		lbstatus.setPreferredSize(lbsize);
		lbstatus.setMinimumSize(lbsize);
		jp.add(lbstatus);
		return jp;
	}
	public void updateStatus(String msg) {
		if (EventQueue.isDispatchThread()) {
			lbstatus.setText(msg);
		} else {
			final String fmsg = msg;
			Runnable r = new Runnable() {
				public void run() {
					updateStatus(fmsg);
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static DBTableModel createPushdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("level", "varchar", "级别");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem("1", "十分紧急");
		col.addComboxBoxItem("2", "紧急");
		col.addComboxBoxItem("3", "普通");
		cols.add(col);

		col = new DBColumnDisplayInfo("groupname", "varchar", "分组");
		cols.add(col);

		col = new DBColumnDisplayInfo("pushname", "varchar", "推送任务");
		cols.add(col);

		col = new DBColumnDisplayInfo("rowcount", "number", "笔数");
		col.setCalcsum(true);
		cols.add(col);

		return new DBTableModel(cols);
	}

	public void appendPushinfo(Pushinfo pushinfo) {
		pushinfos.add(pushinfo);
		int row = pushdbmodel.getRowCount();
		pushdbmodel.appendRow();
		pushdbmodel.setItemValue(row, "pushname", pushinfo.getPushname());
		pushdbmodel.setItemValue(row, "groupname", pushinfo.getGroupname());
		pushdbmodel.setItemValue(row, "level", String.valueOf(pushinfo
				.getLevel()));
		pushdbmodel.setItemValue(row, "rowcount", String.valueOf(pushinfo
				.getRowcount()));

		logger.debug("appendPushinfo start,pushdbmodel rowcount="+pushdbmodel.getRowCount());
		logger.debug("begin fireDatachanged(),pushdbmodel rowcount="+
				pushdbmodel.getRowCount()+",pushtablemodel rowcount="+pushtablemodel.getRowCount());

		if (EventQueue.isDispatchThread()) {
			showPushtable();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					showPushtable();
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void showPushtable(){
		logger.debug("begin fireDatachanged(),pushdbmodel rowcount="+
				pushdbmodel.getRowCount()+",pushtablemodel rowcount="+pushtablemodel.getRowCount());
		pushtablemodel.fireDatachanged();
		pushtable.tableChanged(new TableModelEvent(pushtable
				.getModel()));
		pushtable.autoSize();
		logger.debug("autoSizeed");

		int r = pushtable.getRow();
		if (r < 0 || r > pushdbmodel.getRowCount() - 1) {
			pushtable.setRowSelectionInterval(0, 0);
		}
		logger.debug("begin btnrun.setEnabled(true);");
		btnrun.setEnabled(true);
		logger.debug("ok");
		
	}

	public void clear() {
		logger.debug("shower.clear()");
		pushinfos.clear();
		if (EventQueue.isDispatchThread()) {
			btnrun.setEnabled(false);
			pushdbmodel.clearAll();
			pushtablemodel.fireDatachanged();
			pushtable.tableChanged(new TableModelEvent(pushtable.getModel()));
		} else {
			Runnable r = new Runnable() {
				public void run() {
					clear();
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public int getPushcount() {
		return pushdbmodel.getRowCount();
	}

	void runOp(Pushinfo pushinfo) {
		Clientframe clientfrm = Clientframe.getClientframe();
		COpframe frm = clientfrm.runOp(pushinfo.getCallopid(), false);
		CSteModel ste = null;
		if (frm instanceof Steframe) {
			ste = ((Steframe) frm).getCreatedStemodel();
		} else if (frm instanceof MdeFrame) {
			ste = ((MdeFrame) frm).getCreatedMdemodel().getMasterModel();
		} else if (frm instanceof MultisteFrame) {
			ste = ((MultisteFrame) frm).getCreatedStemodel();
		} else if (frm instanceof MMdeFrame) {
			ste = ((MMdeFrame) frm).getCreatedStemodel();
		} else if (frm instanceof BIReportFrame) {
		} else {
			logger.error("不明要运行的窗口类型" + frm);
			return;
		}

		if (pushinfo.getCallopid().equals("12")) {
			// 审批功能.不需要条件.
		} else {
			String wheres = pushinfo.getFullwheres();
			wheres = Aphelper.filterApwheres(wheres);
			ste.doQuery(wheres);
		}

	}

	class TablemouseHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int r = pushtable.getRow();
				if (r >= 0) {
					Pushinfo pushinfo = pushinfos.elementAt(r);
					runOp(pushinfo);
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

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("refresh")) {
			notifystartimmedate = true;
		} else if (e.getActionCommand().equals("runop")) {
			int row = pushtable.getRow();
			if (row < 0 || row > pushdbmodel.getRowCount() - 1) {
				CMessageDialog.warnMessage(Clientframe.getClientframe(), "提示",
						"请选择一个要处理的任务");
				return;
			}
			Pushinfo pushinfo = pushinfos.elementAt(row);
			runOp(pushinfo);
		}
	}

	public int getMinute() {
		return Integer.parseInt((String) cbMinute.getSelectedItem());
	}

	public boolean isNotifystartimmediate() {
		return notifystartimmedate;
	}

	public void resetNotifystartimmediate() {
		notifystartimmedate = false;
	}

	class Pushtable extends CEditableTable {

		public Pushtable(TableModel dm) {
			super(dm);
			this.readonlybackcolor=Color.WHITE;
		}

		@Override
		protected Color getCellColor(int row, int col) {
			if (col != 0)
				return super.getCellColor(row, col);
			if (row < 0 || row > pushdbmodel.getRowCount() - 1)
				return super.getCellColor(row, col);
			String level = pushdbmodel.getItemValue(row, "level");
			if (level.equals("1")) {
				return Color.red;
			} else if (level.equals("2")) {
				return Color.ORANGE;
			}
			return super.getCellColor(row, col);
		}
	

	}
}
