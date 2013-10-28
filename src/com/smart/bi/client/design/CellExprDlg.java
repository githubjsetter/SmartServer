package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 表达式设置.
 * 上部为Jtextarea,
 * 下部为tabbed页.分别为列,函数
 * @author user
 *
 */
public class CellExprDlg extends CDialogOkcancel{
	private JTextArea textExpr=null;
	String expr;
	private CTable columntable;
	private CTable functable;
	BIReportdsDefine dsdefine;
	
	public String getExpr() {
		return expr;
	}

	public CellExprDlg(JFrame frm,CTable columnlisttable,String expr,BIReportdsDefine dsdefine){
		super(frm,"设置表达式",true);
		this.expr=expr;
		this.columntable = columnlisttable;
		this.dsdefine=dsdefine;
		init();
		listParam();
		localCenter();
	}
	
	void listParam(){
		DBTableModel dm=(DBTableModel)columntable.getModel();
		//插入参数
		Enumeration<BIReportparamdefine> en=dsdefine.params.elements();
		while(en.hasMoreElements()){
			BIReportparamdefine p=en.nextElement();
			int r=dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(r, "colname", p.paramname);
			dm.setItemValue(r, "coltype", p.paramtype);
			dm.setItemValue(r, "title", p.title);
		}

	}
	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		textExpr = new JTextArea(4,60);
		textExpr.setWrapStyleWord(true);
		textExpr.setLineWrap(true);
		textExpr.setText(expr);
		
		cp.add(new JScrollPane(textExpr),BorderLayout.NORTH);
		
		JTabbedPane tabbedpane=new JTabbedPane();
		cp.add(tabbedpane,BorderLayout.CENTER);
		
		columntable.addMouseListener(new ColumntablemouseHandler());
		columntable.getSelectionModel().setSelectionInterval(0,0);
		columntable.setDragEnabled(false);
		columntable.setReadonly(true);
		tabbedpane.add("数据列", new JScrollPane(columntable));
		
		functable = createFunctable();
		functable.setReadonly(true);
		functable.addMouseListener(new FunctablemouseHandler());
		tabbedpane.add("函数", new JScrollPane(functable));
		
		cp.add(createOkcancelPane(),BorderLayout.SOUTH);
		
	}
	
	class ColumntablemouseHandler implements MouseListener{
		public void mouseClicked(MouseEvent e) {
			int row=columntable.getRow();
			if(row>=0){
				DBTableModel dm=(DBTableModel)columntable.getModel();
				String colname=dm.getItemValue(row, "colname");
				textExpr.replaceSelection("{"+colname+"}");
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

	class FunctablemouseHandler implements MouseListener{
		public void mouseClicked(MouseEvent e) {
			int row=functable.getRow();
			if(row>=0){
				DBTableModel dm=(DBTableModel)functable.getModel();
				String funcname=dm.getItemValue(row, "funcname");
				textExpr.replaceSelection(funcname);
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

	CTable createFunctable(){
		String funcdesc[][] = { { "rowcount()", "记录数" },
				{ "getrow()", "当前行号" }, { "username()", "当前人员姓名" },
				{ "pageno()", "页号" }, { "pagecount()", "总页数" },
				{ "today()", "当前日期 YYYY-MM-DD格式" },
				{ "now()", "当前时间 HH:MM:SS" }, { "round(变量,小数位)", "保留小数位数" },
				{ "abs(变量)", "绝对值" }, { "if(逻辑表达式,值1,值2)", "if表达式" },
				{ "sum(变量)", "求和" },
				{ "sum(变量 for group)", "分组中求和" },
				{ "avg(变量)", "求平均" },
				{ "avg(变量 for group)", "分组中平均" },
				{ "max(变量)", "求最大" },
				{ "max(变量 for group)", "分组中最大" },
				{ "min(变量)", "求最小" },
				{ "min(变量 for group)", "分组中最小" },
				{ "count(变量 for group)", "分组记录数" },
				
				
				
				{ "pagesum(变量)", "页求和" }, { "tocn(变量)", "金额中文大写" },
				{"\"第\"+pageno()+\"页,共\"+pagecount()+\"页\"","第几页,共几页"},
				{"username()","人员姓名"},
				{"\"制表人:+username()\"","制表人"},
		};
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("funcname", "varchar", "函数名");
		cols.add(col);
		col = new DBColumnDisplayInfo("title", "varchar", "说明");
		cols.add(col);	

		DBTableModel dm = new DBTableModel(cols);
		for (int i = 0; i < funcdesc.length; i++) {
			int r = dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(r, "funcname", funcdesc[i][0]);
			dm.setItemValue(r, "title", funcdesc[i][1]);
		}
		
		
		CTable table=new CTable(dm);
		table.autoSize();
		return table;
	}

	@Override
	protected void onOk() {
		expr=textExpr.getText();
		super.onOk();
	}
}
