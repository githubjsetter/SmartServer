package com.inca.np.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.runop.Oplauncher;
import com.inca.np.gui.ste.QuerylinkInfo.Querycondinfo;
import com.inca.np.util.DBHelper;
import com.inca.npbi.client.design.BIReportdsDefine;
import com.inca.npbi.client.design.ReportcanvasFrame;
import com.inca.npbi.client.design.param.BIReportparamdefine;
import com.inca.npclient.system.Clientframe;

public class QuerylinkSetupDlg extends CDialog {
	DBTableModel dbmodel;
	DBTableModel opdbmodel;
	DBTableModel calledopdbmodel;
	String linkexpr;
	CEditableTable table = null;
	DBTableModel tablemodel = null;
	private JTextField textLinkname;
	private JTextField textOpid;
	private Sumdbmodel sumdbmodel;
	QuerylinkInfo querylinkinfo = null;

	public QuerylinkSetupDlg(Frame frame, DBTableModel opdbmodel,
			String linkexpr) {
		super(frame, "设置查询级联", true);
		this.opdbmodel = opdbmodel;
		this.linkexpr = linkexpr;
		if (linkexpr.length() > 0) {
			try {
				querylinkinfo = QuerylinkInfo.create(linkexpr);
			} catch (Exception e) {
				querylinkinfo = new QuerylinkInfo();
				JOptionPane.showMessageDialog(this, e.getMessage());
			}
		} else {
			querylinkinfo = new QuerylinkInfo();
		}
		initDialog();
		bindValue();
		// setHotkey();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	boolean ok = false;
	private JTextArea textWhere;
	private JList listColumns;

	void onOk() {
		querylinkinfo = new QuerylinkInfo();
		if (textLinkname.getText().length() == 0) {
			JOptionPane.showMessageDialog(this, "必须输入级联名称");
			return;
		}
		if (textOpid.getText().length() == 0) {
			JOptionPane.showMessageDialog(this, "必须选择功能ID");
			return;
		}
		if (dbmodel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "必须定义至少一个查询条件");
			return;
		}
		querylinkinfo.querylinkname = textLinkname.getText();
		querylinkinfo.opid = textOpid.getText();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			Querycondinfo info = new Querycondinfo();
			info.cname1 = dbmodel.getItemValue(r, "cname1");
			info.op = dbmodel.getItemValue(r, "op");
			info.cname2 = dbmodel.getItemValue(r, "cname2");
			querylinkinfo.conds.add(info);
		}
		querylinkinfo.wheres=textWhere.getText();

		ok = true;
		this.dispose();
	}

	void onCancel() {
		this.dispose();
	}

	public boolean isOk() {
		return ok;
	}

	public QuerylinkInfo getQuerylinkinfo() {
		return querylinkinfo;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("selectop")) {
			selectop();
		} else if (cmd.equals("add")) {
			addCond();
		} else if (cmd.equals("del")) {
			delCond();
		} else if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}

	void delCond() {
		int r = table.getRow();
		if (r < 0 || r > dbmodel.getRowCount() - 1)
			return;

		dbmodel.removeRow(r);
		sumdbmodel.fireDatachanged();
		table.tableChanged(new TableModelEvent(sumdbmodel));
		table.autoSize();

		querylinkinfo.conds.removeElementAt(r);
	}

	void addCond() {
		if (querylinkinfo.opid.length() == 0) {
			JOptionPane.showMessageDialog(this, "必须先选择要调用的功能");
			return;
		}

		AddquerylinkcondDlg addconddlg = new AddquerylinkcondDlg();
		addconddlg.pack();
		addconddlg.setVisible(true);
	}

	void addCond(String cname1, String op, String cname2) {
		Querycondinfo qcondinfo = new Querycondinfo();
		qcondinfo.cname1 = cname1;
		qcondinfo.op = op;
		qcondinfo.cname2 = cname2;
		querylinkinfo.conds.add(qcondinfo);

		DBColumnDisplayInfo colinfo1 = calledopdbmodel
				.getColumninfo(qcondinfo.cname1);
		DBColumnDisplayInfo colinfo2 = opdbmodel
				.getColumninfo(qcondinfo.cname2);

		int r = dbmodel.getRowCount();
		dbmodel.appendRow();
		dbmodel.setItemValue(r, "cname1", qcondinfo.cname1);
		dbmodel.setItemValue(r, "title1", colinfo1.getTitle());
		dbmodel.setItemValue(r, "op", qcondinfo.op);
		dbmodel.setItemValue(r, "cname2", qcondinfo.cname2);
		dbmodel.setItemValue(r, "title2", colinfo2.getTitle());
		sumdbmodel.fireDatachanged();
		table.tableChanged(new TableModelEvent(sumdbmodel));
		table.autoSize();

	}

	void selectop() {
		// 先择功能
		Relateophov ophov = new Relateophov();
		DBTableModel result = ophov.showDialog(this, "选择相关功能", "", "", "");
		if (result == null)
			return;
		querylinkinfo.opid = result.getItemValue(0, "opid");
		this.textOpid.setText(querylinkinfo.opid);
		dbmodel.clearAll();
		table.tableChanged(new TableModelEvent(table.getModel()));

		try {
			COpframe frm;
			if (Clientframe.getClientframe() != null) {
				frm = Clientframe.getClientframe().runOp(querylinkinfo.opid,
						true);
			} else {
				frm = Oplauncher.loadOp(querylinkinfo.opid);
			}
			if (frm instanceof Steframe) {
				Steframe stefrm = (Steframe) frm;
				calledopdbmodel = stefrm.getCreatedStemodel().getDBtableModel();
			} else if (frm instanceof MdeFrame) {
				MdeFrame mdefrm = (MdeFrame) frm;
				calledopdbmodel = mdefrm.getCreatedMdemodel().getMasterModel()
						.getDBtableModel();
			} else if (frm instanceof ReportcanvasFrame) {
				ReportcanvasFrame bifrm=(ReportcanvasFrame)frm;
				BIReportdsDefine dsdefine = bifrm.getDsdefine();
				Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
				Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
				while (en.hasMoreElements()) {
					BIReportparamdefine param = en.nextElement();
					DBColumnDisplayInfo col=new DBColumnDisplayInfo(param.paramname,
							param.paramtype,param.title);
					cols.add(col);
				}
				calledopdbmodel=new DBTableModel(cols);

			} else {
				JOptionPane.showMessageDialog(this, "被调用功能ID"
						+ querylinkinfo.opid + "不是能处理的ste和mde类型");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "下载被调用功能ID"
					+ querylinkinfo.opid + "失败:" + e.getMessage());
			return;
		}

		addCond();
	}

	void bindValue() {
		textLinkname.setText(querylinkinfo.querylinkname);
		textOpid.setText(querylinkinfo.opid);

		if (querylinkinfo.opid.length() == 0)
			return;

		try {
			COpframe frm;
			if (Clientframe.getClientframe() != null) {
				frm = Clientframe.getClientframe().runOp(querylinkinfo.opid,
						true);
			} else {
				frm = Oplauncher.loadOp(querylinkinfo.opid);
			}
			if (frm instanceof Steframe) {
				Steframe stefrm = (Steframe) frm;
				calledopdbmodel = stefrm.getCreatedStemodel().getDBtableModel();
			} else if (frm instanceof MdeFrame) {
				MdeFrame mdefrm = (MdeFrame) frm;
				calledopdbmodel = mdefrm.getCreatedMdemodel().getMasterModel()
						.getDBtableModel();
			} else if (frm instanceof ReportcanvasFrame) {
				ReportcanvasFrame bifrm=(ReportcanvasFrame)frm;
				BIReportdsDefine dsdefine = bifrm.getDsdefine();
				Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
				Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
				while (en.hasMoreElements()) {
					BIReportparamdefine param = en.nextElement();
					DBColumnDisplayInfo col=new DBColumnDisplayInfo(param.paramname,
							param.paramtype,param.title);
					cols.add(col);
				}
				calledopdbmodel=new DBTableModel(cols);
			} else {
				JOptionPane.showMessageDialog(this, "被调用功能ID"
						+ querylinkinfo.opid + "不是能处理的ste和mde类型");
				return;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "下载被调用功能ID"
					+ querylinkinfo.opid + "失败:" + e.getMessage());
			return;
		}

		dbmodel.clearAll();
		Enumeration<Querycondinfo> en = querylinkinfo.conds.elements();
		while (en.hasMoreElements()) {
			Querycondinfo condinfo = en.nextElement();
			DBColumnDisplayInfo colinfo1 = calledopdbmodel
					.getColumninfo(condinfo.cname1);
			DBColumnDisplayInfo colinfo2 = opdbmodel
					.getColumninfo(condinfo.cname2);
			if (colinfo2 == null || colinfo2 == null) {
				continue;
			}

			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "cname1", condinfo.cname1);
			dbmodel.setItemValue(r, "title1", colinfo1.getTitle());
			dbmodel.setItemValue(r, "op", condinfo.op);
			dbmodel.setItemValue(r, "cname2", condinfo.cname2);
			dbmodel.setItemValue(r, "title2", colinfo2.getTitle());
		}
		sumdbmodel.fireDatachanged();
		table.tableChanged(new TableModelEvent(sumdbmodel));
		table.autoSize();
		
		textWhere.setText(querylinkinfo.wheres);
	}

	protected void initDialog() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		JTabbedPane tabbedpane = new JTabbedPane();
		cp.add(tabbedpane, BorderLayout.CENTER);
		tabbedpane.add("调用条件", createBasepane());
		tabbedpane.add("where条件", createAddcondPane());

	}

	// 工具条
	JPanel createBottompane() {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());

		JButton btn;
		btn = new JButton("增加条件");
		btn.setActionCommand("add");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("删除条件");
		btn.setActionCommand("del");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);

		return jp;
	}

	JPanel createToppane() {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());

		JLabel lb = new JLabel("级联名称");
		jp.add(lb);
		textLinkname = new JTextField(40);
		jp.add(textLinkname);

		// 功能
		lb = new JLabel("调用功能ID");
		jp.add(lb);

		textOpid = new JTextField(10);
		textOpid.setEditable(false);
		jp.add(textOpid);

		JButton btn = new JButton("...");
		jp.add(btn);
		btn.setActionCommand("selectop");
		btn.addActionListener(this);

		return jp;
	}

	DBTableModel createDbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = null;
		col = new DBColumnDisplayInfo("cname1", "varchar", "被调用表列名");
		col.setReadonly(true);
		cols.add(col);
		col = new DBColumnDisplayInfo("title1", "varchar", "被调用表列中文名");
		col.setReadonly(true);
		cols.add(col);
		col = new DBColumnDisplayInfo("op", "varchar", "逻辑操作");
		col.setReadonly(true);
		cols.add(col);
		col = new DBColumnDisplayInfo("cname2", "varchar", "调用表列名");
		col.setReadonly(true);
		cols.add(col);
		col = new DBColumnDisplayInfo("title2", "varchar", "调用表列中文名");
		col.setReadonly(true);
		cols.add(col);

		return new DBTableModel(cols);
	}

	class AddquerylinkcondDlg extends CDialog {
		private JList list1;
		private JList listop;
		private JList list2;

		AddquerylinkcondDlg() {
			super(QuerylinkSetupDlg.this, "增加条件", true);
			init();
			this.localCenter();
			this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		}

		void init() {
			// 从左到右放三个list
			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.CENTER);
			jp.setLayout(new FlowLayout());

			Dimension listsize = new Dimension(300, 400);
			Dimension opsize = new Dimension(100, 400);

			list1 = new JList(toColnames(calledopdbmodel, true));
			JScrollPane jsp = new JScrollPane(list1);
			jsp.setPreferredSize(listsize);
			jp.add(jsp);
			list1.setSelectedIndex(0);

			String ops[] = { "=", "like", ">", ">=", "<", "<=", "<>", "between" };
			listop = new JList(ops);
			jsp = new JScrollPane(listop);
			jsp.setPreferredSize(opsize);
			listop.setSelectedIndex(0);
			jp.add(jsp);

			list2 = new JList(toColnames(opdbmodel, false));
			jsp = new JScrollPane(list2);
			jsp.setPreferredSize(listsize);
			list2.setSelectedIndex(0);
			jp.add(new JScrollPane(jsp));

			// 下面是确定
			jp = new JPanel();
			cp.add(jp, BorderLayout.SOUTH);
			jp.setLayout(new FlowLayout());
			JButton btn;
			btn = new JButton("增加");
			btn.setActionCommand("ok");
			btn.addActionListener(this);
			jp.add(btn);

			btn = new JButton("取消");
			btn.setActionCommand("cancel");
			btn.addActionListener(this);
			jp.add(btn);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("ok")) {
				String cname1 = (String) list1.getSelectedValue();
				int p = cname1.indexOf("(");
				cname1 = cname1.substring(0, p);
				String cname2 = (String) list2.getSelectedValue();
				p = cname2.indexOf("(");
				cname2 = cname2.substring(0, p);

				addCond(cname1, (String) listop.getSelectedValue(), cname2);
				dispose();
			} else {
				dispose();
			}
		}

		String[] toColnames(DBTableModel dbmodel, boolean checkqueryable) {
			ArrayList<String> ar = new ArrayList<String>();
			Enumeration<DBColumnDisplayInfo> en = dbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (checkqueryable && !colinfo.isQueryable()
						|| colinfo.getColtype().equals("行号"))
					continue;
				String s = colinfo.getColname() + "(" + colinfo.getTitle()
						+ ")";
				ar.add(s);
			}
			String rs[] = new String[ar.size()];
			ar.toArray(rs);
			return rs;
		}
	}

	/**
	 * 生成自由条件
	 * 
	 * @return
	 */
	JPanel createAddcondPane() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		JLabel lb = new JLabel("where条件.形式为 被调用功能列={本功能列}[and .....]");
		jp.add(lb, BorderLayout.NORTH);

		textWhere = new JTextArea(5, 40);
		textWhere.setWrapStyleWord(true);
		textWhere.setAutoscrolls(true);
		JScrollPane jscrollp = new JScrollPane(textWhere);
		Dimension size = new Dimension(280, 100);
		jscrollp.setPreferredSize(size);
		jscrollp.setMinimumSize(size);

		jp.add(jscrollp, BorderLayout.CENTER);

		ArrayList<String> ar = new ArrayList<String>();
		Enumeration<DBColumnDisplayInfo> en = opdbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.getColname().equals("行号"))
				continue;
			if (!col.isDbcolumn())
				continue;

			ar.add(col.getColname());
		}

		String cols[] = new String[ar.size()];
		ar.toArray(cols);
		listColumns = new JList(cols);
		listColumns.addMouseListener(new ColumnlistMouseHandler());
		jscrollp = new JScrollPane(listColumns);
		jp.add(jscrollp, BorderLayout.SOUTH);

		return jp;
	}

	JPanel createBasepane() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		// 上部为
		jp.add(createToppane(), BorderLayout.NORTH);

		// 中部为关联表
		dbmodel = createDbmodel();
		sumdbmodel = new Sumdbmodel(dbmodel, null);
		table = new CEditableTable(sumdbmodel);
		// 不能排序。
		table.setSortable(false);

		jp.add(new JScrollPane(table), BorderLayout.CENTER);

		jp.add(createBottompane(), BorderLayout.SOUTH);

		return jp;
	}

	class ColumnlistMouseHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			int ii = listColumns.getSelectedIndex();
			if (ii >= 0) {
				String colname = (String) listColumns.getSelectedValue();
				textWhere.replaceSelection("{" + colname + "}");
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		ste.getRootpanel();

		// String expr = "(查询库存,1)(companyid,=,goodsid)";
		String expr = "";
		QuerylinkSetupDlg dlg = new QuerylinkSetupDlg(null, ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);

		if (!dlg.isOk())
			return;

		QuerylinkInfo qlinfo = dlg.getQuerylinkinfo();
		expr = qlinfo.getExpr();
		System.out.println(expr);
		/*
		 * System.out.println(dlg.getOk()); if (dlg.getOk()) {
		 * System.out.println(dlg.getExpr()); }
		 */}

}
