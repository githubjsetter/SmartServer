package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;

import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.npbi.client.design.link.Linkinfo;

/**
 * 属性页.
 * 
 * @author user
 * 
 */
public class Proppane extends JPanel {
	JComboBox textFontname;
	JSpinner textFontsize;
	JCheckBox cbBold;
	JCheckBox cbItalic;
	JComboBox cbAlign;
	JComboBox cbFormat;
	JCheckBox cbFixrowheight;
	JSpinner textRowheight;
	Dimension panesize = new Dimension(300, 580);
	JCheckBox cbFixrowcount;
	JSpinner textfixrowcount;
	JComboBox cbVAlign;
	ActionListener actionListener;

	public Proppane(ActionListener actionListener) {
		this.actionListener=actionListener;
		BoxLayout boxlayout=new BoxLayout(this,BoxLayout.Y_AXIS);
		GridBagLayout g = new GridBagLayout();
		JPanel cellproppane=new JPanel(g);
		add(cellproppane);
		Border border =BorderFactory.createTitledBorder("单元格属性");
		cellproppane.setBorder(border);
		
		Dimension compsize = new Dimension(120, 27);
		int line = 0;
		JLabel lb = new JLabel("字体");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));

		GraphicsEnvironment genv = java.awt.GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		textFontname = new JComboBox(genv.getAvailableFontFamilyNames());
		cellproppane.add(textFontname, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textFontname.setPreferredSize(compsize);
		textFontname.setMinimumSize(compsize);

		line++;
		lb = new JLabel("字体大小");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textFontsize = new JSpinner();
		cellproppane.add(textFontsize, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textFontsize.setPreferredSize(compsize);
		textFontsize.setMinimumSize(compsize);

		line++;
		lb = new JLabel("加粗");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbBold = new JCheckBox();
		cellproppane.add(cbBold, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbBold.setPreferredSize(compsize);
		cbBold.setMinimumSize(compsize);

		line++;
		lb = new JLabel("斜体");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbItalic = new JCheckBox();
		cellproppane.add(cbItalic, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbItalic.setPreferredSize(compsize);
		cbItalic.setMinimumSize(compsize);

		String ss[] = { "左对齐", "居中", "右对齐" };
		line++;
		lb = new JLabel("水平对齐");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbAlign = new JComboBox(ss);
		cellproppane.add(cbAlign, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbAlign.setPreferredSize(compsize);
		cbAlign.setMinimumSize(compsize);

		ss = new String[] { "靠上", "居中", "靠下" };
		line++;
		lb = new JLabel("垂直对齐");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbVAlign = new JComboBox(ss);
		cellproppane.add(cbVAlign, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbVAlign.setPreferredSize(compsize);
		cbVAlign.setMinimumSize(compsize);

		line++;
		lb = new JLabel("格式");
		cellproppane.add(lb, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		ss = new String[] { "", "0.00" ,"#,###.00" };
		cbFormat = new JComboBox(ss);
		cbFormat.setEditable(true);
		cellproppane.add(cbFormat, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		cbFormat.setPreferredSize(compsize);
		cbFormat.setMinimumSize(compsize);

		add(Box.createRigidArea(new Dimension(0,20)));
		
		JPanel pageproppane=new JPanel(g);
		add(pageproppane);
		border =BorderFactory.createTitledBorder("表格属性");
		pageproppane.setBorder(border);

		
		line=0;
		cbFixrowheight = new JCheckBox("固定行高");
		cbFixrowheight.addItemListener(new CbfixrowheightHandler());
		pageproppane.add(cbFixrowheight, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		ss = new String[] { "", "0.00" };
		textRowheight = new JSpinner();
		pageproppane.add(textRowheight, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textRowheight.setPreferredSize(compsize);
		textRowheight.setMinimumSize(compsize);



		line++;
		cbFixrowcount = new JCheckBox("固定每页行数");
		cbFixrowcount.addItemListener(new CbfixrowcountHandler());
		pageproppane.add(cbFixrowcount, new GridBagConstraints(0, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textfixrowcount = new JSpinner();
		pageproppane.add(textfixrowcount, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		textfixrowcount.setPreferredSize(compsize);
		textfixrowcount.setMinimumSize(compsize);
		
		cbFixrowheight.setSelected(false);
		cbFixrowcount.setSelected(false);
		
		///级联
		JPanel jplink=createLinkpane();
		add(jplink);
		border =BorderFactory.createTitledBorder("链接定义");
		jplink.setBorder(border);
		
		
		

	}
	
	JPanel createLinkpane(){
		JPanel jplink=new JPanel();
		jplink.setLayout(new BorderLayout());
		
		JPanel tb=new JPanel();
		JButton btn=new JButton("增加链接");
		btn.setActionCommand("addlink");
		btn.addActionListener(actionListener);
		tb.add(btn);

		btn=new JButton("修改链接");
		btn.setActionCommand("modifylink");
		btn.addActionListener(actionListener);
		tb.add(btn);

		btn=new JButton("删除链接");
		btn.setActionCommand("dellink");
		btn.addActionListener(actionListener);
		tb.add(btn);
		
		jplink.add(tb,BorderLayout.NORTH);
		

		linkdbmodel=createLinkdbmodel();
		linktable=new CTable(linkdbmodel);
		linktable.setReadonly(true);
		//linktable.addMouseListener(linktable.)
		JScrollPane jsp=new JScrollPane(linktable);
		Dimension compsize=new Dimension(280,200);
		jsp.setPreferredSize(compsize);
		jsp.setMinimumSize(compsize);
		jplink.add(jsp,BorderLayout.CENTER);
		
		
		return jplink;
	}
	
	
	DBTableModel linkdbmodel=null;
	CTable linktable;
	
	DBTableModel createLinkdbmodel(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("linkname","varchar","链接名称");
		cols.add(col);

		col=new DBColumnDisplayInfo("callopname","varchar","调用功能名");
		cols.add(col);

		col=new DBColumnDisplayInfo("callopid","varchar","调用功能ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("callcond","varchar","调用条件");
		cols.add(col);

		return new DBTableModel(cols);
	}
	

	@Override
	public Dimension getPreferredSize() {
		return panesize;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize((int) panesize.getWidth(), (int) panesize.getHeight());
	}
	
	class CbfixrowheightHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e) {
			textRowheight.setEnabled(cbFixrowheight.isSelected());
		}
		
	}
	
	class CbfixrowcountHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e) {
			textfixrowcount.setEnabled(cbFixrowcount.isSelected());
		}
		
	}

	public void addLink(String linkname, String callopid, String callopname,
			String callcond) {
		int r=linkdbmodel.getRowCount();
		linkdbmodel.appendRow();
		linkdbmodel.setItemValue(r,"linkname",linkname);
		linkdbmodel.setItemValue(r,"callopid",callopid);
		linkdbmodel.setItemValue(r,"callopname",callopname);
		linkdbmodel.setItemValue(r,"callcond",callcond);
		linktable.tableChanged(new TableModelEvent(linkdbmodel));
	}

	public void setLinkinfo(BICell cell) {
		linkdbmodel.clearAll();
		for(int i=0;i<cell.getLinkinfos().size();i++){
			linkdbmodel.appendRow();
			Linkinfo linkinfo=cell.getLinkinfos().elementAt(i);
			linkdbmodel.setItemValue(i, "linkname",linkinfo.getLinkname());
			linkdbmodel.setItemValue(i, "callopid",linkinfo.getCallopid());
			linkdbmodel.setItemValue(i, "callopname",linkinfo.getCallopname());
			linkdbmodel.setItemValue(i, "callcond",linkinfo.getCallcond());
		}
		linktable.tableChanged(new TableModelEvent(linkdbmodel));
	}

	public CTable getLinktable() {
		return linktable;
	}

	

}
