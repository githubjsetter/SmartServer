package com.inca.np.demo.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CTableHeaderRender;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ui.CTableheadUI;
import com.inca.np.util.DefaultNPParam;

public class Testtablesize extends JFrame implements ActionListener{

	private DBTableModel dbmodel;
	private CTable table;
	private Vector<DBColumnDisplayInfo> formcolumndisplayinfos;

	public Testtablesize() throws HeadlessException {
		super();
		Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		JScrollPane sp=new JScrollPane();
		cp.add(sp,BorderLayout.CENTER);
	
		formcolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		for(int c=0;c<50;c++){
			formcolumndisplayinfos.add(new DBColumnDisplayInfo("col"+c,"varchar","列"+c));
		}
		
		dbmodel = new DBTableModel(formcolumndisplayinfos);
		DefaultTableModel model=new DefaultTableModel(3,20);
		table = recreateTable(dbmodel);
		table.getTableHeader().setUI(new CTableheadUI());

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sp.setViewportView(table);
		
		
		JButton btn=new JButton("Test");
		cp.add(btn,BorderLayout.NORTH);
		btn.addActionListener(this);
		btn.setActionCommand("test");
		
	}
	

	protected CTable recreateTable(DBTableModel dbmodel) {
		// 建列
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		String[] tmpcols = new String[10];
		tmpcols[0]="col0";
		tmpcols[1]="col10";
		tmpcols[2]="col2";
		tmpcols[3]="col3";
		tmpcols[4]="col4";
		tmpcols[5]="col5";
		tmpcols[6]="col6";
		tmpcols[7]="col7";
		tmpcols[8]="col8";
		tmpcols[9]="col9";
		
		if (tmpcols == null) {
			ArrayList<String> ar = new ArrayList<String>();
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				ar.add(colinfo.getColname());
			}
			tmpcols = new String[ar.size()];
			ar.toArray(tmpcols);
		}

		for (int i = 0; i < tmpcols.length; i++) {
			String colname = tmpcols[i];
			// 求列序
			int j;
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			for (j = 0; en.hasMoreElements(); j++) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (colinfo.isHide()) {
					continue;
				}
				if (colinfo.getColname().equals(colname)) {
					TableColumn col = new TableColumn(j);
					col.setHeaderValue(colinfo.getTitle());
					cm.addColumn(col);
					break;
				}
			}
		}

		CTable table = new CTable(dbmodel, cm);
		// 去掉F2键编辑 enter下一行
		InputMap map = table.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		map.remove(vkenter);
		// 回车往前
		map.put(vkenter, "selectNextColumnCell");

		KeyStroke vktmp = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		map.put(vktmp, "selectNextColumnCell");

		//table.setTableHeader(new Mytablehead(table.getColumnModel()));

		table.setRowHeight(27);
		return table;
	}
	
	
	class Mytablehead extends JTableHeader{

		public Mytablehead(TableColumnModel cm) {
			super(cm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			Dimension size=super.getPreferredSize();
			return new Dimension((int)size.getWidth(),50);
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.equals("test")){
			JTableHeader th = table.getTableHeader();
			Dimension size = th.getPreferredSize();
			size.setSize(size.getWidth(), 30);
			th.setPreferredSize(size);
			
			
			for(int r=0;r<100;r++){
				dbmodel.appendRow();
				for(int c=0;c<50;c++){
					dbmodel.setItemValue(r,c,"value"+r+"_"+c);
				}
			}
		}
		TableModelEvent event = new TableModelEvent(dbmodel);
		table.tableChanged(event);
		table.autoSize();
	}
	
	public static void main(String[] argv){
		new DefaultNPParam();
		Testtablesize frm=new Testtablesize();
		frm.pack();
		frm.setVisible(true);
	}

}
