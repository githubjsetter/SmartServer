package com.inca.np.anyprint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.ErrorManager;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;

import com.inca.np.anyprint.impl.DataprocRule;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.SortsetupDialog;

public class DataprocPane extends JPanel implements ActionListener {

	AnyprintFrame frm = null;
	Printplan plan = null;
	DBTableModel dbmodel = null;
	CTable table = null;

	public DataprocPane(AnyprintFrame frm, Printplan plan) {
		this.frm = frm;
		this.plan = plan;
		init();
	}

	void init() {
		setLayout(new BorderLayout());
		add(createToolbar(), BorderLayout.NORTH);
		dbmodel = createDbmodel();
		table = new CTable(dbmodel);
		table.setReadonly(true);
		table.addMouseListener(new MouseHandle());
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void bind() {
		dbmodel.clearAll();
		Enumeration<DataprocRule> en = plan.getProcrule().elements();
		while (en.hasMoreElements()) {
			DataprocRule rule = en.nextElement();
			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "ruletype", rule.getRuletype());
			dbmodel.setItemValue(r, "expr", rule.getExpr());
		}
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();
	}

	/**
	 * Ω®dbmodel
	 * 
	 * @return
	 */
	DBTableModel createDbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("ruletype", "varchar", "πÊ‘Ú¿‡–Õ");
		cols.add(col);
		col = new DBColumnDisplayInfo("expr", "varchar", "±Ì¥Ô Ω");
		cols.add(col);

		return new DBTableModel(cols);
	}

	CToolbar createToolbar() {
		CToolbar tb = new CToolbar();
		JButton btn;
		btn = new JButton("…Ë÷√≈≈–Ú");
		btn.setActionCommand("…Ë÷√≈≈–Ú");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("…Ë÷√∑÷“≥");
		btn.setActionCommand("…Ë÷√∑÷“≥");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("ÃÓ–¥¥Ú”°µ•∫≈");
		btn.setActionCommand("ÃÓ–¥¥Ú”°µ•∫≈");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("…æ≥˝");
		btn.setActionCommand("…æ≥˝");
		btn.addActionListener(this);
		tb.add(btn);

		return tb;
	}

	void onAddsort() {
		String sortexpr = "";
		try {
			frm.setWaitcursor();
			DBTableModel plandbmodel = plan.createFulldatamodel();
			SortsetupDialog sortdlg = new SortsetupDialog(frm, plandbmodel,
					sortexpr);
			sortdlg.pack();
			sortdlg.setVisible(true);
			if (!sortdlg.getOk())
				return;
			sortexpr = sortdlg.getExpr();
			int row = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "ruletype", "≈≈–Ú");
			dbmodel.setItemValue(row, "expr", sortexpr);
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
			reverseBind();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			frm.setDefaultcursor();
		}
	}

	void onModifysort(int row) {
		String sortexpr = dbmodel.getItemValue(row, "expr");
		try {
			frm.setWaitcursor();
			DBTableModel plandbmodel = plan.createFulldatamodel();
			SortsetupDialog sortdlg = new SortsetupDialog(frm, plandbmodel,
					sortexpr);
			sortdlg.pack();
			sortdlg.setVisible(true);
			if (!sortdlg.getOk())
				return;
			sortexpr = sortdlg.getExpr();
			dbmodel.setItemValue(row, "ruletype", "≈≈–Ú");
			dbmodel.setItemValue(row, "expr", sortexpr);
			table.tableChanged(new TableModelEvent(dbmodel));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			frm.setDefaultcursor();
		}
	}

	void onModifysplit(int row) {
		String expr = dbmodel.getItemValue(row, "expr");
		try {
			frm.setWaitcursor();
			DBTableModel plandbmodel = plan.createFulldatamodel();
			SplitpageDlg splitdlg = new SplitpageDlg(frm, plandbmodel, expr);
			splitdlg.pack();
			splitdlg.setVisible(true);
			if (!splitdlg.getOk())
				return;
			expr = splitdlg.getExpr();
			dbmodel.setItemValue(row, "ruletype", "∑÷“≥");
			dbmodel.setItemValue(row, "expr", expr);
			table.tableChanged(new TableModelEvent(dbmodel));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			frm.setDefaultcursor();
		}
	}

	void onModify() {
		int row = table.getRow();
		if (row >= 0) {
			String ruletype = dbmodel.getItemValue(row, "ruletype");
			if (ruletype.equals("≈≈–Ú")) {
				onModifysort(row);
			} else if (ruletype.equals("∑÷“≥")) {
				onModifysplit(row);
			} else if (ruletype.equals("ÃÓ–¥¥Ú”°µ•∫≈")) {
				onModifyfillprintno(row);
			}
			table.autoSize();
			reverseBind();
		}
	}

	void reverseBind() {
		plan.clearDataprocrule();
		for (int i = 0; i < dbmodel.getRowCount(); i++) {
			String ruletype = dbmodel.getItemValue(i, "ruletype");
			String expr = dbmodel.getItemValue(i, "expr");
			DataprocRule r = null;
			if (ruletype.equals("∑÷“≥")) {
				r = new DataprocRule(DataprocRule.RULETYPE_SPLITPAGE);
			} else if (ruletype.equals("≈≈–Ú")) {
				r = new DataprocRule(DataprocRule.RULETYPE_SORT);
			} else if (ruletype.equals("ÃÓ–¥¥Ú”°µ•∫≈")) {
				r = new DataprocRule(DataprocRule.RULETYPE_FILLPRINTNO);
			}
			plan.addDataprocrule(r);
			r.setExpr(expr);
		}
		plan.procData();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("…Ë÷√≈≈–Ú")) {
			onAddsort();
		} else if (e.getActionCommand().equals("…Ë÷√∑÷“≥")) {
			onAddsplitpage();
		} else if (e.getActionCommand().equals("ÃÓ–¥¥Ú”°µ•∫≈")) {
			onAddFillprintno();
		} else if (e.getActionCommand().equals("…æ≥˝")) {
			doDel();
		}
	}

	void doDel() {
		int row = table.getRow();
		if (row >= 0) {
			dbmodel.removeRow(row);
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
			reverseBind();
		}
	}

	void onModifyfillprintno(int row) {
		String expr = dbmodel.getItemValue(row, "expr");
		String ss[] = expr.split(":");
		String serialnoid = ss[0];
		String tablename = ss[1];
		String fillcolname = ss[2];
		String pkcolname = ss[3];
		String dbmodelcolname = ss[4];
		String printflagcolname="";
		String printmanidcolname="";
		String printdatecolname="";
		String tablename1="";
		String pkcolname1="";
		String dbmodelcolname1="";
		
		if(ss.length>5){
			printflagcolname=ss[5];
		}
		if(ss.length>6){
			printmanidcolname=ss[6];
		}
		if(ss.length>7){
			printdatecolname=ss[7];
		}
		if(ss.length>8){
			tablename1=ss[8];
		}
		if(ss.length>9){
			pkcolname1=ss[9];
		}
		if(ss.length>10){
			dbmodelcolname1=ss[10];
		}
		
		try {
			frm.setWaitcursor();
			FillprintnoDlg dlg = new FillprintnoDlg(frm, plan
					.createFulldatamodel());
			dlg.setSerialnoid(serialnoid);
			dlg.setTablename(tablename);
			dlg.setFillColumnname(fillcolname);
			dlg.setPkcolname(pkcolname);
			dlg.setDbmodelcolname(dbmodelcolname);
			dlg.setPrintflagcolname(printflagcolname);
			dlg.setPrintmanidColname(printmanidcolname);
			dlg.setPrintdateColname(printdatecolname);
			dlg.setTablename1(tablename1);
			dlg.setPkcolname1(pkcolname1);
			dlg.setDbmodelcolname1(dbmodelcolname1);
			
			dlg.pack();
			frm.setDefaultcursor();
			dlg.setVisible(true);
			if (!dlg.isOk())
				return;

			expr=dlg.getExpr();
			dbmodel.setItemValue(row, "expr", expr);
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
			reverseBind();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			frm.setDefaultcursor();
		}

	}

	void onAddFillprintno() {
		try {
			frm.setWaitcursor();
			FillprintnoDlg dlg = new FillprintnoDlg(frm, plan
					.createFulldatamodel());
			dlg.pack();
			frm.setDefaultcursor();
			dlg.setVisible(true);
			if (!dlg.isOk())
				return;

			String expr=dlg.getExpr();
			int row = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "ruletype", "ÃÓ–¥¥Ú”°µ•∫≈");
			dbmodel.setItemValue(row, "expr", expr);
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
			reverseBind();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(frm, e.getMessage());
		}
		frm.setDefaultcursor();

	}

	void onAddsplitpage() {
		String expr = "";
		try {
			frm.setWaitcursor();
			DBTableModel plandbmodel = plan.createFulldatamodel();
			SplitpageDlg sppagedlg = new SplitpageDlg(frm, plandbmodel, expr);
			sppagedlg.pack();
			sppagedlg.setVisible(true);
			if (!sppagedlg.getOk())
				return;
			expr = sppagedlg.getExpr();
			int row = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "ruletype", "∑÷“≥");
			dbmodel.setItemValue(row, "expr", expr);
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
			reverseBind();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			frm.setDefaultcursor();
		}

	}

	class MouseHandle implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				onModify();
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
}
