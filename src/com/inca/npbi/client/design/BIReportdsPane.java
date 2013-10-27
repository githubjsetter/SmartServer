package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;

import org.apache.log4j.Category;

import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.util.DBHelper;
import com.inca.npbi.client.design.param.ParamFrame;
import com.inca.npbi.client.design.param.Paramste;

/**
 * 数据源定义 上部工具条. 中部为列名定义
 * 
 * @author user
 * 
 */
public class BIReportdsPane extends JPanel implements ActionListener {
	Category logger=Category.getInstance(BIReportdsPane.class);
	ReportcanvasFrame frm = null;
	Vector<BIReportdsDefine> dstable;
	BIReportdsDefine dsdefine = null;
	DBTableModel coldefinedm = null;
	private CTable table;
	private JComboBox cbDs;

	public BIReportdsPane(ReportcanvasFrame frm) {
		this.frm = frm;
		setLayout(new BorderLayout());
		JPanel tb = createToolbar();
		add(tb, BorderLayout.NORTH);

		coldefinedm = frm.createColdefdm();
		coldefinedm.getColumninfo("colname").setReadonly(true);
		coldefinedm.getColumninfo("coltype").setReadonly(true);
		coldefinedm.getColumninfo("title").setReadonly(false);

		table = new CEditableTable(new Sumdbmodel(coldefinedm, null));

		add(new JScrollPane(table), BorderLayout.CENTER);
		bind();
	}

	public void bind() {
		dstable = frm.getDstable();
		int ii = cbDs.getSelectedIndex();
		if(ii<0 && frm.getDstable().size()>0){
			ii=0;
		}
		if(ii<0)return;
		dsdefine = dstable.elementAt(ii);
		genColumn();
		table.tableChanged(new TableModelEvent(coldefinedm));
		table.autoSize();
		cbDs.setModel(createCbdsmodel());
	}

	class CbDsHandler implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			int ii = cbDs.getSelectedIndex();
			dsdefine = dstable.elementAt(ii);
			genColumn();
		}

	}

	JPanel createToolbar() {
		JPanel tb = new JPanel();
		BoxLayout boxl = new BoxLayout(tb, BoxLayout.X_AXIS);
		tb.setLayout(boxl);
		tb.add(new JLabel("数据源"));

		Dimension compsize = new Dimension(100, 27);
		cbDs = new JComboBox(createCbdsmodel());
		cbDs.addItemListener(new CbDsHandler());
		cbDs.setPreferredSize(compsize);
		cbDs.setMaximumSize(compsize);
		tb.add(cbDs);
		JButton btn;

		btn = new JButton("设置SQL");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setActionCommand("sql");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("定义参数");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setActionCommand("setupparam");
		btn.addActionListener(this);
		tb.add(btn);


		btn = new JButton("填写中文列名");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setActionCommand("setupcntitle");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("辅助数据源");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setActionCommand("newds");
		btn.addActionListener(this);
		tb.add(btn);

		return tb;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("sql")) {
			setupSql();
		} else if ("setupcntitle".equals(e.getActionCommand())) {
			setupCntitle();
		} else if ("newds".equals(e.getActionCommand())) {
			addDs();
		} else if ("setupparam".equals(e.getActionCommand())) {
			setupParam();
		}
	}
	
	void setupParam(){
		ParamFrame pfrm=new ParamFrame();
		pfrm.pack();
		Paramste pste=(Paramste) pfrm.getCreatedStemodel();
		pste.setCanvasframe(frm);
		pste.setDsdefine(dsdefine);
		pfrm.setVisible(true);
	}

	DefaultComboBoxModel createCbdsmodel() {
		if (dstable == null || dstable.size() == 0) {
			return new DefaultComboBoxModel();
		}
		String ss[] = new String[dstable.size()];
		ss[0] = "主数据源";
		for (int i = 1; i < dstable.size(); i++) {
			ss[i] = "数据源" + String.valueOf(i + 1);
		}
		return new DefaultComboBoxModel(ss);
	}

	void addDs() {
		dsdefine = new BIReportdsDefine();
		dsdefine.params=frm.getDstable().elementAt(0).params;
		dstable.add(dsdefine);
		cbDs.setModel(createCbdsmodel());
		cbDs.setSelectedIndex(cbDs.getItemCount() - 1);
		setupSql();
	}

	void setupCntitle() {
		ColumncntitleHov cnhov = new ColumncntitleHov();
		DBTableModel result = cnhov.showDialog(frm, "选择自动填写中文列名的参照表");
		if (result == null) {
			return;
		}

		DBTableModel dtlmodel = (DBTableModel) cnhov.getDtltable().getModel();
		for (int i = 0; i < dtlmodel.getRowCount() - 1; i++) {
			String cname = dtlmodel.getItemValue(i, "cname");
			String cntitle = dtlmodel.getItemValue(i, "cntitle");
			for (int r = 0; r < coldefinedm.getRowCount(); r++) {
				String colname = coldefinedm.getItemValue(r, "colname");
				String title = coldefinedm.getItemValue(r, "title");
				if (cname.equalsIgnoreCase(colname)) {
					coldefinedm.setItemValue(r, "title", cntitle);
					table.tableChanged(new TableModelEvent(coldefinedm, r));
					break;
				}

			}
		}

	}

	void setupSql() {
		ReportsqlDlg dlg = new ReportsqlDlg(frm, dsdefine.sql,frm.getDstable().elementAt(0));
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.ok)
			return;

		// 生成列
		dsdefine.sql = dlg.getSql();
		genColumn();

	}

	void genColumn() {
		DBTableModel coldm = coldefinedm;
		coldm.clearAll();

		String sql = dsdefine.getTestsql();
		if (sql.length() == 0) {
			return;
		}
		
		sql = DBHelper.addWheres(sql, "1=2");
		logger.debug(sql);
		RemotesqlHelper sh = new RemotesqlHelper();
		try {
			DBTableModel rmtdm = sh.doSelect(sql, 0, 1);
			Vector<DBColumnDisplayInfo> newcols = new Vector<DBColumnDisplayInfo>();
			Enumeration<DBColumnDisplayInfo> en = rmtdm.getDisplaycolumninfos()
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo dbcol = en.nextElement();
				DBColumnDisplayInfo oldcol = null;
				if(dsdefine.datadm != null){
					oldcol=dsdefine.datadm.getColumninfo(dbcol.getColname());
				}
						
				DBColumnDisplayInfo col = null;
				if (oldcol == null) {
					col = dbcol;
				} else {
					col = oldcol;
				}
				newcols.add(col);
				int newrow = coldm.getRowCount();
				coldm.appendRow();
				coldm.setItemValue(newrow, "colname", col.getColname());
				coldm.setItemValue(newrow, "coltype", col.getColtype());
				coldm.setItemValue(newrow, "title", col.getTitle());

			}
			dsdefine.datadm = new DBTableModel(newcols);
			dsdefine.datadm.bindMemds(rmtdm);

			table.tableChanged(new TableModelEvent(coldm));
			frm.prepareData();
		} catch (Exception e) {
			e.printStackTrace();
			frm.errorMessage("错误", e.getMessage());
		}

	}

	/**
	 * 将列的中文名设置回dsdefine.datadm
	 */
	public void reverseBind() {
		for (int i = 0; i < coldefinedm.getRowCount(); i++) {
			String colname = coldefinedm.getItemValue(i, "colname");
			Enumeration<DBColumnDisplayInfo> en = dsdefine.datadm
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo col = en.nextElement();
				if (col.getColname().equalsIgnoreCase(colname)) {
					col.setTitle(coldefinedm.getItemValue(i, "title"));
					break;
				}
			}
		}
	}
}
