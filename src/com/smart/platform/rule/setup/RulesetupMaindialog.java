package com.smart.platform.rule.setup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CButton;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.enginee.Ruleenginee;

/**
 * 设置一个功能的规则
 * 
 * @author Administrator
 * 
 */
public class RulesetupMaindialog extends CDialog {
	Ruleenginee ruleeng = null;
	Object caller = null;
	String optype = null;
	Frame owner=null;
	int currow = -1;

	public RulesetupMaindialog(Frame owner,Ruleenginee ruleeng, Object caller, String optype) {
		super(owner,"定义规则",true);
		this.owner=owner;
		this.ruleeng = ruleeng;
		this.caller = caller;
		this.optype = optype;
		initFrame();
		bindValue();
		table.autoSize();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		this.localScreenCenter();
	}


	CTable table;

	/**
	 * 中心是表格,显示三列. 是否启用.规则类型,规则表达式.
	 */
	protected void initFrame() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		CToolbar tb = createToolbar();
		cp.add(tb, BorderLayout.NORTH);

		table = createTable();
		table.addMouseListener(new TableMouseListener());
		table.setReadonly(true);
		table.getSelectionModel().addListSelectionListener(
				new TableselectionListener());
		cp.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	
	class TableMouseListener implements MouseListener{
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				modifyRule();
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

	class TableselectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			currow = dm.getAnchorSelectionIndex();
		}

	}

	protected void bindValue() {
		Enumeration<Rulebase> en = ruleeng.getRuletable().elements();
		while (en.hasMoreElements()) {
			Rulebase rule = en.nextElement();
			dbmodel.appendRow();
			int r = dbmodel.getRowCount() - 1;
			dbmodel.setItemValue(r, "use", rule.isUse() ? "启用" : "停用");
			dbmodel.setItemValue(r, "ruletype", rule.getRuletype());
			dbmodel.setItemValue(r, "expr", rule.getExpr());
		}
	}

	CToolbar createToolbar() {
		CToolbar tb = new CToolbar();
		CButton btn = new CButton("增加规则");
		btn.setActionCommand("addrule");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new CButton("修改规则");
		btn.setActionCommand("modifyrule");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new CButton("停用规则");
		btn.setActionCommand("forbidrule");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new CButton("启用规则");
		btn.setActionCommand("userule");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new CButton("删除规则");
		btn.setActionCommand("delrule");
		btn.addActionListener(this);
		tb.add(btn);

		tb.addSeparator(new Dimension(10, 27));
		btn = new CButton("保存");
		btn.setActionCommand("save");
		btn.addActionListener(this);
		tb.add(btn);

		tb.addSeparator(new Dimension(40, 27));

		btn = new CButton("关闭");
		btn.setActionCommand("close");
		btn.addActionListener(this);
		tb.add(btn);

		return tb;
	}

	DBTableModel dbmodel = null;

	CTable createTable() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("use", "varchar",
				"启用");
		cols.add(col);

		col = new DBColumnDisplayInfo("ruletype", "varchar", "规则类型");
		cols.add(col);

		col = new DBColumnDisplayInfo("expr", "varchar", "规则表达式");
		cols.add(col);

		dbmodel = new DBTableModel(cols);
		CTable table = new CTable(dbmodel);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return table;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("addrule")) {
			newRule();
		} else if (e.getActionCommand().equals("modifyrule")) {
			modifyRule();
		} else if (e.getActionCommand().equals("forbidrule")) {
			forbidRule();
		} else if (e.getActionCommand().equals("userule")) {
			useRule();
		} else if (e.getActionCommand().equals("save")) {
			save();
		} else if (e.getActionCommand().equals("close")) {
			close();
		} else if (e.getActionCommand().equals("delrule")) {
			delrule();
		}
	}
	
	void delrule(){
		int rs[] = table.getSelectedRows();
		int delct=0;
		for (int i = 0; i < rs.length; i++,delct++) {
			int r = rs[i];
			ruleeng.getRuletable().removeElementAt(r-delct);
			dbmodel.removeRow(r-delct);
		}
		
	}
	
	void close(){
		ok=false;
		dispose();
	}

	boolean ok=false;
	void save() {
		ok=true;
		dispose();
	}
	public boolean getOk(){
		return ok;
	}

	void forbidRule() {
		int rs[] = table.getSelectedRows();
		for (int i = 0; i < rs.length; i++) {
			int r = rs[i];
			dbmodel.setItemValue(r, "use", "禁用");
			ruleeng.getRuletable().elementAt(r).setUse(false);
		}
		table.tableChanged(new TableModelEvent(dbmodel));
	}

	void useRule() {
		int rs[] = table.getSelectedRows();
		for (int i = 0; i < rs.length; i++) {
			int r = rs[i];
			dbmodel.setItemValue(r, "use", "启用");
			ruleeng.getRuletable().elementAt(r).setUse(true);
		}
		table.tableChanged(new TableModelEvent(dbmodel));
	}

	void modifyRule() {
		if (currow < 0)
			return;

		String ruletype = dbmodel.getItemValue(currow, "ruletype");
		Rulebase rule = ruleeng.getRuletable().elementAt(currow);
		rule.setRuletype(ruletype);
		rule.setExpr(dbmodel.getItemValue(currow, "expr"));
		
		try {
			rule.setupUI(caller);
			dbmodel.setItemValue(currow, "expr",rule.getExpr());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
		}
/*		

		ModifyruleDialog dlg = new ModifyruleDialog(this, caller, optype, rule);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			dbmodel.setItemValue(currow, "expr", rule.getExpr());
			table.tableChanged(new TableModelEvent(dbmodel));
		}
*/
	}

	void newRule() {
		SelectruleDialog dlg = new SelectruleDialog(this, caller, optype);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			Rulebase rule = dlg.getRule();
			dbmodel.appendRow();
			int r = dbmodel.getRowCount() - 1;
			dbmodel.setItemValue(r, "use", "启用");
			dbmodel.setItemValue(r, "ruletype", rule.getRuletype());
			dbmodel.setItemValue(r, "expr", rule.getExpr());

			ruleeng.getRuletable().add(rule);

			table.tableChanged(new TableModelEvent(dbmodel));
		}
	}

	public static void main(String[] argv) {
		Ruleenginee re = new Ruleenginee();
		Pub_goods_ste ste = new Pub_goods_ste(null);
		File outf=new File("test.rule");
		RulesetupMaindialog frm = new RulesetupMaindialog(null,re, ste, "ste");
		frm.pack();
		frm.setVisible(true);
	}
}
