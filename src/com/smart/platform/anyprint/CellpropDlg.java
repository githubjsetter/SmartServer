package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;

import com.smart.platform.anyprint.impl.Cellbase;
import com.smart.platform.anyprint.impl.Columncell;
import com.smart.platform.anyprint.impl.Partbase;
import com.smart.platform.anyprint.impl.Parts;
import com.smart.platform.anyprint.impl.TextCell;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * ����cell���� ����г����е�cell���ұ������Ա༭
 * 
 * @author Administrator
 * 
 */
public class CellpropDlg extends CDialog {
	Frame frm;
	Printplan plan;
	private JTextArea textExpr;
	private JTextField textX;
	private JTextField textY;
	private JTextField textW;
	private JTextField textH;
	private JCheckBox cbPrintable;
	private JComboBox cbBarcode;
	DBTableModel dbmodel;
	CTable table;
	private CComboBox cbPart;
	private JTextField textCelltype;
	private JTextField textFontname;
	private JTextField textFontsize;
	private CComboBox cbAlign;
	private CTable functable;
	private CTable colnametable;
	private Cellbase newcell;

	public CellpropDlg(Frame frm, Printplan plan) {
		super(frm, "��������������", true);
		this.frm = frm;
		this.plan = plan;
		init();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	public void editCell(Partbase part, Cellbase cellbase) {
		Parts parts = plan.getParts();
		for (int i = 0; i < parts.getPartcount(); i++) {
			if (parts.getPart(i) == part) {
				cbPart.setSelectedIndex(i);
				// ��λ��ǰ��
				Vector<Cellbase> cells = part.getCells();
				for (int j = 0; j < cells.size(); j++) {
					if (cells.get(j) == cellbase) {
						table.getSelectionModel().setSelectionInterval(j, j);
						bindProp(j);
						break;
					}
				}
				break;
			}
		}
	}

	boolean addmode = false;

	public void addCell(Cellbase newcell,int defaultpartindex) {
		addmode = true;
		this.newcell = newcell;
		cbPart.setSelectedIndex(defaultpartindex);
		// ����һ��
		dbmodel.clearAll();
		dbmodel.appendRow();
		table.tableChanged(new TableModelEvent(dbmodel));
		this.bindProp(newcell);

	}

	void retrievePart(int partindex) {
		if (plan == null)
			return;
		Partbase part = plan.getParts().getPart(partindex);
		leftablerow = -1;
		dbmodel.clearAll();
		Enumeration<Cellbase> en = part.getCells().elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			int row = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "expr", "");
			if (cell instanceof TextCell) {
				dbmodel.setItemValue(row, "expr", ((TextCell) cell).getExpr());
			}
			dbmodel.setItemValue(row, "celltype", cell.getCelltype());
		}
		table.tableChanged(new TableModelEvent(table.getModel()));
		if (dbmodel.getRowCount() > 0) {
			table.getSelectionModel().addSelectionInterval(0, 0);
		}
	}

	void init() {
		dbmodel = createDbmodel();
		table = new CTable(dbmodel);
		table.setReadonly(true);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		String parts[] = { "ҳͷ", "��ͷ", "����", "��β", "ҳ��" };
		cbPart = new CComboBox(parts);
		cbPart.addItemListener(new Cblistener());
		cbPart.setSelectedIndex(2);
		cbPart.setPreferredSize(new Dimension(200, 20));
		JPanel jpnorth = new JPanel();
		jpnorth.setLayout(new CFormlayout(2, 2));
		jpnorth.add(new JLabel("ѡ������λ��"));
		jpnorth.add(cbPart);
		cp.add(jpnorth, BorderLayout.NORTH);

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		cp.add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(200);
		jsp.setLeftComponent(new JScrollPane(table));

		JSplitPane jsp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp.setRightComponent(jsp1);
		JPanel proppane = createProppane();
		proppane.setMinimumSize(new Dimension(500, 220));
		jsp1.setLeftComponent(proppane);

		JTabbedPane tabbedpane = new JTabbedPane();
		functable = createFunctable();
		functable.addMouseListener(new Funcmouselistener(functable, "func"));
		tabbedpane.add("����", new JScrollPane(functable));
		colnametable = createColumntable();
		tabbedpane.add("����", new JScrollPane(colnametable));
		colnametable.addMouseListener(new Funcmouselistener(colnametable,
				"column"));
		jsp1.setRightComponent(tabbedpane);

		JPanel jptb = new JPanel();
		cp.add(jptb, BorderLayout.SOUTH);
		JButton btn = new JButton("�ر�");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jptb.add(btn);

		table.getSelectionModel().addListSelectionListener(
				new TableselectListener());
	}

	JPanel createProppane() {
		JPanel jp = new JPanel();
		GridBagLayout g = new GridBagLayout();
		jp.setLayout(g);

		JLabel lb = null;
		int y = 0;
		lb = new JLabel("����");
		jp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));
		textCelltype = new JTextField(10);
		textCelltype.setEditable(false);
		jp.add(textCelltype, new GridBagConstraints(1, y, 9, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		y++;
		lb = new JLabel("���ʽ");
		jp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		textExpr = new JTextArea(3, 40);
		textExpr.setLineWrap(true);
		textExpr.setWrapStyleWord(true);
		jp.add(new JScrollPane(textExpr), new GridBagConstraints(1, y, 9, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 12, 1, 5), 0, 0));
		y++;
		lb = new JLabel("x");
		jp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		textX = new JTextField(4);
		jp.add(textX, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		lb = new JLabel("y");
		jp.add(lb, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		textY = new JTextField(4);
		jp.add(textY, new GridBagConstraints(3, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(4, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		textW = new JTextField(4);
		jp.add(textW, new GridBagConstraints(5, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(6, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		textH = new JTextField(4);
		jp.add(textH, new GridBagConstraints(7, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		JButton btn = null;

		/*
		 * btn=new JButton("��Ӻ���"); btn.addActionListener(this);
		 * btn.setActionCommand("����"); jp.add(btn,new GridBagConstraints(9, y,
		 * 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new
		 * Insets(1, 12, 1, 5), 0, 0));
		 */
		// y++;
		y++;
		lb = new JLabel("����");
		jp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));
		textFontname = new JTextField(4);
		jp.add(textFontname, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		lb = new JLabel("��С");
		jp.add(lb, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));
		textFontsize = new JTextField(4);
		jp.add(textFontsize, new GridBagConstraints(3, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		String aligns[] = { "�����", "����", "�Ҷ���" };
		cbAlign = new CComboBox(aligns);
		jp.add(cbAlign, new GridBagConstraints(4, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		cbBold = new JCheckBox("�Ӵ�");
		jp.add(cbBold, new GridBagConstraints(5, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));
		cbItalic = new JCheckBox("б��");
		jp.add(cbItalic, new GridBagConstraints(6, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		y++;
		lb = new JLabel("��ʽ");
		jp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		String formats[] = { "", "#,###.00" };
		cbFormat = new JComboBox(formats);
		cbFormat.setEditable(true);
		jp.add(cbFormat, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		cbPrintable = new JCheckBox("�ɴ�ӡ");
		jp.add(cbPrintable, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		String ss[]={"������","EAN13ͨ����Ʒ����","Code128����","PDF417","EAN.UCC128����"};
		cbBarcode=new JComboBox(ss);
		jp.add(cbBarcode, new GridBagConstraints(3, y, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						12, 1, 5), 0, 0));

		// btn = new JButton("��������");
		// btn.addActionListener(this);
		// btn.setActionCommand("setfont");
		/*
		 * jp.add(btn,new GridBagConstraints(4, y, 1, 1, 0.0, 0.0,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 12,
		 * 1, 5), 0, 0));
		 * 
		 * 
		 * y++; btn=new JButton("Ӧ��"); btn.addActionListener(this);
		 * btn.setActionCommand("apply"); jp.add(btn,new GridBagConstraints(0,
		 * y, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
		 * GridBagConstraints.NONE, new Insets(1, 12, 1, 5), 0, 0));
		 */
		return jp;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(900, 600);
	}

	DBTableModel createDbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("expr", "varchar",
				"���ʽ");
		cols.add(col);

		col = new DBColumnDisplayInfo("celltype", "varchar", "����");
		cols.add(col);
		return new DBTableModel(cols);
	}

	class Cblistener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (addmode)
				return;
			CComboBox cb = (CComboBox) e.getSource();
			retrievePart(cb.getSelectedIndex());
		}

	}

	private int leftablerow = -1;
	private JCheckBox cbBold;
	private JCheckBox cbItalic;
	private JComboBox cbFormat;

	class TableselectListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			if (addmode)
				return;

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			if (leftablerow >= 0) {
				setCellprop(leftablerow);
			}

			int newrow = dm.getAnchorSelectionIndex();
			if (newrow >= 0) {
				bindProp(newrow);
				leftablerow = newrow;
			}
		}

	}

	public static void main(String[] args) {
		CellpropDlg dlg = new CellpropDlg(null, null);
		dlg.pack();
		dlg.setVisible(true);
	}

	void setCellprop(int newrow) {
		Cellbase cell = null;
		if (addmode) {
			cell = newcell;
		} else {
			Partbase part = plan.getParts().getPart(cbPart.getSelectedIndex());
			if (newrow >= part.getCells().size())
				return;
			cell = part.getCells().elementAt(newrow);
		}
		// ��ʾ���Ҳ������Ա༭
		try {
			cell.getRect().x = Integer.parseInt(textX.getText());
		} catch (Exception e) {
		}
		try {
			cell.getRect().y = Integer.parseInt(textY.getText());
		} catch (Exception e) {
		}
		try {
			cell.getRect().width = Integer.parseInt(textW.getText());
		} catch (Exception e) {
		}
		try {
			cell.getRect().height = Integer.parseInt(textH.getText());
		} catch (Exception e) {
		}

		cell.setPrintable(cbPrintable.isSelected());
		cell.setBarcodetype("");
		if(cbBarcode.getSelectedIndex()==1){
			cell.setBarcodetype(Cellbase.BARCODE_EAN13);
		}else if(cbBarcode.getSelectedIndex()==2){
			cell.setBarcodetype(Cellbase.BARCODE_CODE128);
		}else if(cbBarcode.getSelectedIndex()==3){
			cell.setBarcodetype(Cellbase.BARCODE_PDF417);
		}else if(cbBarcode.getSelectedIndex()==4){
			cell.setBarcodetype(Cellbase.BARCODE_EANUCC128);
		}

		if (cell instanceof TextCell) {
			TextCell textcell = (TextCell) cell;
			textcell.setExpr(textExpr.getText());
			String fontname = textFontname.getText();
			int fontsize = 0;
			try {
				fontsize = Integer.parseInt(textFontsize.getText());
			} catch (Exception e) {
			}
			Font font = new Font(fontname, Font.PLAIN, fontsize);
			textcell.setFont(font);
			if (cbAlign.getSelectedIndex() == 0) {
				textcell.setAlign(JLabel.LEFT);
			} else if (cbAlign.getSelectedIndex() == 1) {
				textcell.setAlign(JLabel.CENTER);
			} else {
				textcell.setAlign(JLabel.RIGHT);
			}

			textcell.setBold(cbBold.isSelected());
			textcell.setItalic(cbItalic.isSelected());
			textcell.setFormat((String) cbFormat.getSelectedItem());
			
			if(plan.getPlantype().indexOf("���")>=0){
				if(!( textcell instanceof Columncell)){
					//�ڼ���?
					Enumeration<Cellbase>en=plan.getParts().getTablehead().getCells().elements();
					int i=0;
					for(i=0;en.hasMoreElements();i++){
						Cellbase cb=en.nextElement();
						if(cb==textcell){
							//�Ҷ�Ӧ��columncell
							int datacindex=-1;
							Enumeration<Cellbase>en1=plan.getParts().getBody().getCells().elements();
							while(en1.hasMoreElements()){
								Cellbase cb1=en1.nextElement();
								if(cb1 instanceof Columncell){
									datacindex++;
									if(datacindex == i){
										String s=textExpr.getText().trim();
										if(s.startsWith("\"")){
											s=s.substring(1);
										}
										if(s.endsWith("\"")){
											s=s.substring(0,s.length()-1);
										}
										((Columncell)cb1).setTitle(s);
										break;
									}
								}
							}
							break;
						}
					}
					
				}
				
				
				plan.getParts().alignTable();
			}
		}
		frm.repaint();
	}

	void bindProp(int newrow) {
		Partbase part = plan.getParts().getPart(cbPart.getSelectedIndex());
		Cellbase cell = part.getCells().elementAt(newrow);
		bindProp(cell);
	}

	void bindProp(Cellbase cell) {
		if (cell == null)
			return;

		// ��ʾ���Ҳ������Ա༭
		this.textCelltype.setText(cell.getCelltype());
		textX.setText(String.valueOf(cell.getRect().x));
		textY.setText(String.valueOf(cell.getRect().y));
		textW.setText(String.valueOf(cell.getRect().width));
		textH.setText(String.valueOf(cell.getRect().height));
		cbPrintable.setSelected(cell.isPrintable());
		
		cbBarcode.setSelectedIndex(0);
		if(cell.getBarcodetype().equals(Cellbase.BARCODE_EAN13)){
			cbBarcode.setSelectedIndex(1);
		}else if(cell.getBarcodetype().equals(Cellbase.BARCODE_CODE128)){
			cbBarcode.setSelectedIndex(2);
		}else if(cell.getBarcodetype().equals(Cellbase.BARCODE_PDF417)){
			cbBarcode.setSelectedIndex(3);
		}else if(cell.getBarcodetype().equals(Cellbase.BARCODE_EANUCC128)){
			cbBarcode.setSelectedIndex(4);
		}

		if (cell instanceof TextCell) {
			TextCell textcell = (TextCell) cell;
			textExpr.setText(((TextCell) cell).getExpr());
			textExpr.getCaret().setDot(0);
			Font font = textcell.getFont();
			if (font == null) {
				font = new Font("����", Font.PLAIN, 9);
			}
			textFontname.setText(font.getFontName());
			textFontsize.setText(String.valueOf(font.getSize()));
			if (JLabel.LEFT == textcell.getAlign()) {
				cbAlign.setSelectedIndex(0);
			} else if (JLabel.CENTER == textcell.getAlign()) {
				cbAlign.setSelectedIndex(1);
			} else if (JLabel.RIGHT == textcell.getAlign()) {
				cbAlign.setSelectedIndex(2);
			}
			cbBold.setSelected(textcell.isBold());
			cbItalic.setSelected(textcell.isItalic());
			cbFormat.setSelectedItem(textcell.getFormat());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("setfont")) {
			setFont();
		} else if (cmd.equals("ok")) {
			setCellprop(table.getRow());
			this.dispose();
		}
	}

	void setFont() {

	}

	CTable createFunctable() {
		String funcdesc[][] = { { "rowcount()", "��¼��" },
				{ "getrow()", "��ǰ�к�" }, { "username()", "��ǰ��Ա����" },
				{ "pageno()", "ҳ��" }, { "pagecount()", "��ҳ��" },
				{ "today()", "��ǰ���� YYYY-MM-DD��ʽ" },
				{ "now()", "��ǰʱ�� HH:MM:SS" }, { "round(����,С��λ)", "����С��λ��" },
				{ "abs(����)", "����ֵ" }, { "if(�߼����ʽ,ֵ1,ֵ2)", "if���ʽ" },
				{ "sum(����)", "���" },{ "pagesum(����)", "ҳ���" }, { "tocn(����)", "������Ĵ�д" }, 
				{ "printcopy()","��ӡ�ڼ���"},{"printcopys()","��ӡ������"},
				{"\"��\"+printcopy()+\"��,��\"+printcopys()+\"��\"","��x��,��x��"}};
		
		
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

		CTable table = new CTable(dm);
		table.setReadonly(true);
		table.autoSize();
		return table;
	}

	class Funcmouselistener implements MouseListener {
		CTable table = null;
		String type;

		Funcmouselistener(CTable table, String type) {
			this.table = table;
			this.type = type;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				int row = table.getRow();
				if (row < 0)
					return;
				DBTableModel dbmodel = (DBTableModel) table.getModel();
				String funcname = dbmodel.getItemValue(row, "funcname");
				if (type.equals("column")) {
					funcname = "{" + funcname + "}";
				}
				textExpr.replaceSelection(funcname);
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

	CTable createColumntable() {

		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("funcname", "varchar", "����");
		cols.add(col);
		col = new DBColumnDisplayInfo("title", "varchar", "������");
		cols.add(col);

		DBTableModel dm = new DBTableModel(cols);
		if (plan != null) {
			try {
				DBTableModel datamodel = plan.createFulldatamodel();
				Enumeration<DBColumnDisplayInfo> en = datamodel
						.getDisplaycolumninfos().elements();
				while (en.hasMoreElements()) {
					DBColumnDisplayInfo dbcolinfo = en.nextElement();
					int r = dm.getRowCount();
					dm.appendRow();
					dm.setItemValue(r, "funcname", dbcolinfo.getColname());
					dm.setItemValue(r, "title", dbcolinfo.getTitle());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CTable table = new CTable(dm);
		table.setReadonly(true);
		table.autoSize();
		return table;
	}

	public int getPartindex() {
		return cbPart.getSelectedIndex();
	}
}
