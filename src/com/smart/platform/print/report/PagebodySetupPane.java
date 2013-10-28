package com.smart.platform.print.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.smart.platform.gui.control.CCheckBox;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.Sumdbmodel;
import com.smart.platform.gui.ste.PrintSetupFrame;
import com.smart.platform.gui.ste.SortsetupDialog;
import com.smart.platform.print.drawable.PColumnCell;
import com.smart.platform.print.drawable.PDataline;
import com.smart.platform.print.drawable.PageBody;
import com.smart.platform.print.drawable.PageHeadFoot;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-29 Time: 11:42:08
 * �������� <p/> northΪctable,���д��� <p/> centerΪctable���������Ƿ���ʾ
 */
public class PagebodySetupPane extends JPanel {
	PrintSetupFrame frame = null;
	AccessableReport report = null;
	PageHeadFoot pagehead = null;
	private CTable table;
	private JScrollPane tablesp;

	public PagebodySetupPane(PrintSetupFrame frame, AccessableReport report,
			PageHeadFoot pagehead) {
		this.frame = frame;
		this.report = report;
		this.pagehead = pagehead;
		init();
	}
	
	

	CTable proptable;
	int proptablerow;

	private void init() {
		this.setLayout(new BorderLayout());
		add(createToolbar(), BorderLayout.NORTH);

		table = createTable();
		tablesp = new JScrollPane(table);
		add(tablesp, BorderLayout.CENTER);
		Dimension size = tablesp.getPreferredSize();
		tablesp.setPreferredSize(new Dimension((int) size.getWidth(), 100));

		proptable = createProptable();

		JScrollPane sp = new JScrollPane(proptable);
		add(sp, BorderLayout.SOUTH);

	}

	JTextField textTitleHeight;
	JTextField textLineHeight;

	JPanel createToolbar() {
		JPanel jp = new JPanel();
		jp.setLayout(new FlowLayout());

		JLabel lb = new JLabel("�����и�");
		jp.add(lb);
		textTitleHeight = new JTextField(10);
		jp.add(textTitleHeight);
		int th = report.getPage().getPbody().getDataline().getTitleheight();
		textTitleHeight.setText(String.valueOf(th));

		lb = new JLabel("�и�");
		jp.add(lb);
		textLineHeight = new JTextField(10);
		jp.add(textLineHeight);
		int lh = report.getPage().getPbody().getDataline().getHeight();
		textLineHeight.setText(String.valueOf(lh));

		JButton btn = null;
		btn = new JButton("��������");
		jp.add(btn);
		btn.setActionCommand("setsort");
		btn.addActionListener(new ToolbarHandle());

		btn = new JButton("Ӧ������");
		jp.add(btn);
		btn.setActionCommand("apply");
		btn.addActionListener(new ToolbarHandle());
		return jp;
	}

	CTable createProptable() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname", "varchar",
				"����");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "����");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("display", "number", "��ʾ");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		DBTableModel dbmodel = new DBTableModel(cols);
		CEditableTable table = new CEditableTable(dbmodel);
		setColumnDisplayprop(table);
		return table;

		/*
		 * String colnames[] = {"����", "����", "��ʾ"}; DefaultTableModel model = new
		 * DefaultTableModel(new Object[0][0], colnames); return new
		 * Proptable(model);
		 */}

	void setColumnDisplayprop(CTable table) {
		DBTableModel model = (DBTableModel) table.getModel();
		// �г���ʾ�У��Ͳ���ʾ��
		HashMap<String, String> displaycols = new HashMap<String, String>();
		PDataline dataline = report.getPage().getPbody().getDataline();
		Enumeration<PColumnCell> en = dataline.getColumns().elements();
		while (en.hasMoreElements()) {
			PColumnCell cell = en.nextElement();
			if (!cell.isVisible())
				continue;
			int row = model.getRowCount();
			model.appendRow();
			model.setItemValue(row, "colname", cell.getColname());
			model.setItemValue(row, "title", cell.getTitle());
			model.setItemValue(row, "display", "1");
			displaycols.put(cell.getColname(), cell.getColname());
			if (cell.isVisible()) {
				displaycols.put(cell.getColname(), cell.getColname());
			}
		}

		// �г�����ʾ����
		Enumeration<DBColumnDisplayInfo> en1 = report.getDbmodel()
				.getDisplaycolumninfos().elements();
		while (en1.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en1.nextElement();
			if (displaycols.get(colinfo.getColname()) == null) {
				int row = model.getRowCount();
				model.appendRow();
				model.setItemValue(row, "colname", colinfo.getColname());
				model.setItemValue(row, "title", colinfo.getTitle());
				model.setItemValue(row, "display", "0");

				// ��û�б����
				PColumnCell pcell = new PColumnCell(colinfo.getColname(),
						colinfo.getTitle(), 40);
				pcell.setVisible(false);
				dataline.addColumn(pcell);
			}
		}
		// �ಹһ��,�൱�ںϼ���
		model.appendRow();

		DBColumnDisplayInfo colinfo = model.getDisplaycolumninfos()
				.elementAt(2);
		JCheckBox cb = (JCheckBox) colinfo.getEditComponent();

		cb.addItemListener(new CheckboxItemHandle());

	}

	class CheckboxItemHandle implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			CCheckBox cb = (CCheckBox) e.getSource();
			int row = ((CEditableTable) proptable).getEditingrow();
			if (row < 0) {
				return;
			}
			DBTableModel dbmodel = (DBTableModel) proptable.getModel();
			dbmodel.setItemValue(row, "display", cb.isSelected() ? "1" : "0");
			String colname = dbmodel.getItemValue(row, "colname");
			showColumn(colname, cb.isSelected());
		}
	}

	class ToolbarHandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("apply")) {
				// ���������趨
				setColumnWidth();
			}else if(cmd.equals("setsort")){
				setSort();
			}
		}
	}
	void setSort(){
		DBTableModel dbmodel=report.getDbmodel();
		if(dbmodel instanceof Sumdbmodel){
			dbmodel=((Sumdbmodel)dbmodel).getDbmodel();
		}
		String sortexpr=report.getSortexpr();
		SortsetupDialog dlg=new SortsetupDialog(frame,dbmodel,sortexpr);
		dlg.pack();
		dlg.setVisible(true);
		if(!dlg.getOk())return;
		report.setSortexpr(dlg.getExpr());
	}

	private void setColumnWidth() {
		PageBody pbody = report.getPage().getPbody();
		PDataline dataline = pbody.getDataline();

		try {
			dataline
					.setTitleheight(Integer.parseInt(textTitleHeight.getText()));
			System.out.println("textTitleHeight="+textTitleHeight.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dataline.setHeight(Integer.parseInt(textLineHeight.getText()));
			System.out.println("textLineHeight="+textLineHeight.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//ȡ�����е��з���allcols��.
		//�Ƚ����еĿ���ʾ�а����������newcols��,�ٽ�ʣ���з���newcols��.
		Vector<PColumnCell> allcols=report.getPage().getPbody().getDataline().getColumns();

		TableColumnModel cm = table.getColumnModel();
		/*
		 * �µ��д���
		 */
		Vector<PColumnCell> newcells = new Vector<PColumnCell>();
		for (int c = 0; c < cm.getColumnCount(); c++) {
			TableColumn col = cm.getColumn(c);
			int modelindex = col.getModelIndex();
			String colname=tablecolnamear.get(modelindex);
			
			Enumeration<PColumnCell> en=allcols.elements();
			while(en.hasMoreElements()){
				PColumnCell cell=en.nextElement();
				if(cell.getColname().equals(colname)){
					cell.setWidth(col.getWidth());
					cell.setVisible(true);
					newcells.add(cell);
					allcols.remove(cell);
					break;
				}
			}
		}
		
		//�����µ��з���newcols��
		Enumeration<PColumnCell> en=allcols.elements();
		while(en.hasMoreElements()){
			PColumnCell cell=en.nextElement();
			cell.setVisible(false);
			newcells.add(cell);
		}
		
		dataline.setColumns(newcells);
		frame.fireReportChanged();

	}

	private void showColumn(String colname, boolean bshow) {
		// ����һ��
		Vector<PColumnCell> columns = report.getPage().getPbody().getDataline()
				.getColumns();
		Enumeration<PColumnCell> en = columns.elements();
		int i;
		for (i = 0; en.hasMoreElements(); i++) {
			PColumnCell cell = en.nextElement();
			if (cell.getColname().equals(colname)) {
				cell.setVisible(bshow);
				break;
			}
		}

		table = createTable();
		tablesp.setViewportView(table);
	}

	ArrayList<String> tablecolnamear=null;
	CTable createTable() {
		String[] tmpcols = null;
		ArrayList<String> ar = new ArrayList<String>();
		tablecolnamear = new ArrayList<String>();
		Enumeration<PColumnCell> en1 = report.getPage().getPbody()
				.getDataline().getColumns().elements();
		while (en1.hasMoreElements()) {
			PColumnCell columnCell = en1.nextElement();
			if (!columnCell.isVisible()) {
				continue;
			}
			ar.add(columnCell.getTitle());
			tablecolnamear.add(columnCell.getColname());
		}
		tmpcols = new String[ar.size()];
		ar.toArray(tmpcols);

		DefaultTableModel model = new DefaultTableModel(new Object[0][0],
				tmpcols);
		CTable table = new CTable(model);
		table.setReadonly(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		en1 = report.getPage().getPbody().getDataline().getColumns().elements();

		for (int i = 0; en1.hasMoreElements(); i++) {
			PColumnCell columnCell = en1.nextElement();
			if (!columnCell.isVisible()) {
				i--;
				continue;
			}
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setPreferredWidth(columnCell.getWidth());
		}

		// ȥ��F2���༭ enter��һ��
		InputMap map = table.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));

		return table;

	}

	public void setReport(AccessableReport report) {
		this.report = report;
	}

	public void setPagehead(PageHeadFoot pagehead) {
		this.pagehead = pagehead;
	}

}
