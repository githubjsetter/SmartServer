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
 * ���ʽ����.
 * �ϲ�ΪJtextarea,
 * �²�Ϊtabbedҳ.�ֱ�Ϊ��,����
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
		super(frm,"���ñ��ʽ",true);
		this.expr=expr;
		this.columntable = columnlisttable;
		this.dsdefine=dsdefine;
		init();
		listParam();
		localCenter();
	}
	
	void listParam(){
		DBTableModel dm=(DBTableModel)columntable.getModel();
		//�������
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
		tabbedpane.add("������", new JScrollPane(columntable));
		
		functable = createFunctable();
		functable.setReadonly(true);
		functable.addMouseListener(new FunctablemouseHandler());
		tabbedpane.add("����", new JScrollPane(functable));
		
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
		String funcdesc[][] = { { "rowcount()", "��¼��" },
				{ "getrow()", "��ǰ�к�" }, { "username()", "��ǰ��Ա����" },
				{ "pageno()", "ҳ��" }, { "pagecount()", "��ҳ��" },
				{ "today()", "��ǰ���� YYYY-MM-DD��ʽ" },
				{ "now()", "��ǰʱ�� HH:MM:SS" }, { "round(����,С��λ)", "����С��λ��" },
				{ "abs(����)", "����ֵ" }, { "if(�߼����ʽ,ֵ1,ֵ2)", "if���ʽ" },
				{ "sum(����)", "���" },
				{ "sum(���� for group)", "���������" },
				{ "avg(����)", "��ƽ��" },
				{ "avg(���� for group)", "������ƽ��" },
				{ "max(����)", "�����" },
				{ "max(���� for group)", "���������" },
				{ "min(����)", "����С" },
				{ "min(���� for group)", "��������С" },
				{ "count(���� for group)", "�����¼��" },
				
				
				
				{ "pagesum(����)", "ҳ���" }, { "tocn(����)", "������Ĵ�д" },
				{"\"��\"+pageno()+\"ҳ,��\"+pagecount()+\"ҳ\"","�ڼ�ҳ,����ҳ"},
				{"username()","��Ա����"},
				{"\"�Ʊ���:+username()\"","�Ʊ���"},
		};
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("funcname", "varchar", "������");
		cols.add(col);
		col = new DBColumnDisplayInfo("title", "varchar", "˵��");
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
