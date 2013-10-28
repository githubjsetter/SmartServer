package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.JFontChooser;

public class BichartSetupDlg extends CDialogOkcancel {
	private JComboBox cbDs;
	private JTextField textTitle;
	DefaultComboBoxModel cbColumnmodelx = null;
	DefaultComboBoxModel cbColumnmodely1 = null;
	DefaultComboBoxModel cbColumnmodely2 = null;
	DefaultComboBoxModel cbColumnmodely3 = null;
	private JComboBox cbX1Column;
	private JComboBox cbY1Column;
	private JComboBox cbY1Op;
	private JComboBox cbY2Column;
	private JComboBox cbY3Column;
	private JCheckBox cbLegend;
	private JComboBox cbDimension;
	private JTextField textY1title;
	private JTextField textXtitle;
	private JTextField textY2title;
	private JTextField textY3title;
	Chartdefine chartdefine = null;
	Vector<BIReportdsDefine> dstable;
	private JList listCharttype;
	private JComboBox cbSortcolumn;
	private JCheckBox cbSortdesc;
	private JComboBox cbColortype;
	private JComboBox cbShowdata;
	private JTextField textYtitle;
	private JSpinner textX1rotation;
	private JSpinner textYrotation;
	private JSpinner jspnMaxrowcount;
	

	public BichartSetupDlg(Frame frm, Vector<BIReportdsDefine> dstable,
			BiChartRender chartrender) {
		super(frm, "图表设置", true);
		this.chartdefine = chartrender.getChartdefine();
		this.dstable = dstable;

		dstable = new Vector<BIReportdsDefine>();
		dstable.add(new BIReportdsDefine());

		String cols[] = new String[] { "　", "列1", "列2", "列3" };
		cbColumnmodelx = new DefaultComboBoxModel(cols);
		cbColumnmodely1 = new DefaultComboBoxModel(cols);
		cbColumnmodely2 = new DefaultComboBoxModel(cols);
		cbColumnmodely3 = new DefaultComboBoxModel(cols);
		init();
		bindDS();
		bindColumn();
		bind();
		localCenter();
	}

	void bindDS() {
		// 多少个数据源?
		String ss[] = new String[dstable.size()];
		ss[0] = "主数据源";
		for (int i = 1; i < dstable.size(); i++) {
			ss[i] = "数据源" + String.valueOf(i + 1);
		}
		DefaultComboBoxModel cbm = new DefaultComboBoxModel(ss);
		cbDs.setModel(cbm);
	}
	
	void bind() {
		// 多少个数据源?

		textTitle.setText(chartdefine.title);
		textXtitle.setText(chartdefine.xtitle);
		textYtitle.setText(chartdefine.ytitle);
		textY1title.setText(chartdefine.y1title);
		textY2title.setText(chartdefine.y2title);
		textY3title.setText(chartdefine.y3title);

		cbDs.setSelectedIndex(chartdefine.dsdefineindex);
		cbLegend.setSelected(chartdefine.showlegend);
		listCharttype.setSelectedIndex(chartdefine.charttype);
		jspnMaxrowcount.setValue(chartdefine.maxrowcount);
		// 设置可供排序的列.
		setSortablecolumn();

		cbSortdesc.setSelected(chartdefine.sortdesc);
		cbY1Op.setSelectedIndex(chartdefine.y1op);
		
		cbColortype.setSelectedIndex(chartdefine.colortype);
		cbShowdata.setSelectedIndex(chartdefine.showdata);
		cbDimension.setSelectedIndex(chartdefine.dimension);
		
		textX1rotation.setValue(chartdefine.x1fontrotation);
		textYrotation.setValue(chartdefine.yfontrotation);
	}

	void setSortablecolumn() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("　");
		ar.add((String) cbX1Column.getSelectedItem());
		if (cbY1Column.getSelectedIndex() > 0) {
			ar.add((String) cbY1Column.getSelectedItem());
		}
		if (cbY2Column.getSelectedIndex() > 0) {
			ar.add((String) cbY2Column.getSelectedItem());
		}
		if (cbY3Column.getSelectedIndex() > 0) {
			ar.add((String) cbY3Column.getSelectedItem());
		}
		String ss[] = new String[ar.size()];
		ar.toArray(ss);
		DefaultComboBoxModel cbm = new DefaultComboBoxModel(ss);
		cbSortcolumn.setModel(cbm);

		for (int i = 0; i < cbm.getSize(); i++) {
			String tmps = (String) cbm.getElementAt(i);

			if (tmps != null && tmps.equals(chartdefine.sortcolumn)) {
				cbSortcolumn.setSelectedIndex(i);
			}
		}
	}

	class CbYColumnHandler implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			setSortablecolumn();
		}

	}

	void bindColumn() {
		BIReportdsDefine dsdefine = dstable.elementAt(cbDs.getSelectedIndex());
		DBTableModel dm = dsdefine.datadm;
		String cols[] = new String[dm.getColumnCount()];
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(" ");
		for (int i = 0; i < dm.getColumnCount(); i++) {
			cols[i] = dm.getColumnDBName(i);
			String coltype = dm.getColumninfo(dm.getDBColumnName(i))
					.getColtype();
			if (coltype.equalsIgnoreCase("number")) {
				ar.add(dm.getColumnDBName(i));
			}
		}
		cbColumnmodelx = new DefaultComboBoxModel(cols);
		cbX1Column.setModel(cbColumnmodelx);

		String ncols[] = new String[ar.size()];
		ar.toArray(ncols);

		cbColumnmodely1 = new DefaultComboBoxModel(ncols);
		cbY1Column.setModel(cbColumnmodely1);
		cbColumnmodely2 = new DefaultComboBoxModel(ncols);
		cbY2Column.setModel(cbColumnmodely2);
		cbColumnmodely3 = new DefaultComboBoxModel(ncols);
		cbY3Column.setModel(cbColumnmodely3);

		setSelectedcolumn(chartdefine.x1column, cbX1Column);
		setSelectedcolumn(chartdefine.y1column, cbY1Column);
		setSelectedcolumn(chartdefine.y2column, cbY2Column);
		setSelectedcolumn(chartdefine.y3column, cbY3Column);

	}

	class CbColumnHandler implements ItemListener {
		JComboBox cb;
		JTextField text;

		public CbColumnHandler(JComboBox cb, JTextField text) {
			super();
			this.cb = cb;
			this.text = text;
		}

		public void itemStateChanged(ItemEvent e) {
			if (cb.getSelectedIndex() == 0) {
				text.setText("");
			} else {
				String colname = (String) cb.getSelectedItem();
				BIReportdsDefine ds = dstable
						.elementAt(cbDs.getSelectedIndex());
				DBColumnDisplayInfo colinfo = ds.datadm.getColumninfo(colname);
				if (colinfo != null) {
					text.setText("\""+colinfo.getTitle()+"\"");
				}
			}
		}

	}

	void setSelectedcolumn(String colname, JComboBox cb) {
		ComboBoxModel cbm = cb.getModel();
		for (int i = 0; i < cbm.getSize(); i++) {
			String tmpcol = (String) cbm.getElementAt(i);
			if (tmpcol.equalsIgnoreCase(colname)) {
				cb.setSelectedIndex(i);
				return;
			}
		}
	}

	void init() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel dspane = createDspane();
		cp.add(dspane, BorderLayout.WEST);

		JPanel proppane = createChartProppane();
		cp.add(proppane, BorderLayout.CENTER);

		cp.add(createOkcancelPane(), BorderLayout.SOUTH);
	}

	JPanel createDspane() {
		JPanel jp = new JPanel();
		BoxLayout box = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(box);

		cbDs = new JComboBox();
		cbDs.addItemListener(new CbdsHandler());
		Dimension cbsize = new Dimension(100, 27);
		cbDs.setPreferredSize(cbsize);
		cbDs.setMinimumSize(cbsize);
		cbDs.setMaximumSize(cbsize);
		jp.add(cbDs);

		JLabel lb;
		lb = new JLabel("图表类型");
		lb.setAlignmentX(JLabel.LEFT);
		jp.add(lb);

		listCharttype = new JList(Chartdefine.charttypes);
		JScrollPane jsp = new JScrollPane(listCharttype);
		jp.add(jsp);
		Dimension size = new Dimension(100, 140);
		jsp.setPreferredSize(size);
		jsp.setMaximumSize(size);
		jsp.setMinimumSize(size);

		cbDimension = new JComboBox(Chartdefine.dimensions);
		cbDimension.setPreferredSize(cbsize);
		cbDimension.setMinimumSize(cbsize);
		cbDimension.setMaximumSize(cbsize);
		jp.add(cbDimension);

		
		cbColortype = new JComboBox(Chartdefine.colortypes);
		cbColortype.setPreferredSize(cbsize);
		cbColortype.setMinimumSize(cbsize);
		cbColortype.setMaximumSize(cbsize);
		jp.add(cbColortype);
		return jp;
	}

	class CbdsHandler implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			int i = cbDs.getSelectedIndex();
			BIReportdsDefine ds = dstable.elementAt(i);
			bindColumn();
		}

	}

	/**
	 * 标题 X轴 系列1 系列2 系列3 图例
	 * 
	 * @return
	 */
	JPanel createChartProppane() {
		JPanel jp = new JPanel();
		GridBagLayout g = new GridBagLayout();
		jp.setLayout(g);

		JLabel lb;
		lb = new JLabel("标题");
		int line = 0;
		Dimension compsize = new Dimension(140, 27);
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textTitle = new JTextField();
		textTitle.setPreferredSize(compsize);
		textTitle.setMinimumSize(compsize);
		jp.add(textTitle, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		JButton btn;
		btn = new JButton("字体");
		btn.setActionCommand("setuptitlefont");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		// /////////////X 轴//////////////////////
		line++;
		lb = new JLabel("X轴标题");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));


		textXtitle = new JTextField();
		textXtitle.setPreferredSize(compsize);
		textXtitle.setMinimumSize(compsize);
		jp.add(textXtitle, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		JPanel fontpane=new JPanel();
		BoxLayout fontlayout=new BoxLayout(fontpane,BoxLayout.X_AXIS);
		fontpane.setLayout(fontlayout);
		jp.add(fontpane, new GridBagConstraints(3, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		
		btn = new JButton("字体");
		btn.setActionCommand("setupxfont");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		fontpane.add(btn);


		
		////////////////////维度
		Dimension compsize1 = new Dimension(50, 27);
		SpinnerNumberModel spnm=new SpinnerNumberModel(0,0,360,10);

		line++;
		lb = new JLabel("维度列");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbX1Column = new JComboBox(cbColumnmodelx);
		cbX1Column.setPreferredSize(compsize);
		cbX1Column.setMinimumSize(compsize);
		jp.add(cbX1Column, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbX1Column.addItemListener(new CbColumnHandler(cbX1Column, new JTextField()));

		fontpane=new JPanel();
		fontlayout=new BoxLayout(fontpane,BoxLayout.X_AXIS);
		fontpane.setLayout(fontlayout);
		jp.add(fontpane, new GridBagConstraints(3, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		
		btn = new JButton("字体");
		btn.setActionCommand("setupx1font");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		fontpane.add(btn);
		fontpane.add(new JLabel("旋转"));

		spnm=new SpinnerNumberModel(0,0,360,10);
		textX1rotation = new JSpinner(spnm);
		textX1rotation.setPreferredSize(compsize1);
		textX1rotation.setMinimumSize(compsize1);
		fontpane.add(textX1rotation);
		
		/////////////y轴
		line++;
		lb = new JLabel("Y轴标题");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));


		textYtitle = new JTextField();
		textYtitle.setPreferredSize(compsize);
		textYtitle.setMinimumSize(compsize);
		jp.add(textYtitle, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		fontpane=new JPanel();
		fontlayout=new BoxLayout(fontpane,BoxLayout.X_AXIS);
		fontpane.setLayout(fontlayout);
		jp.add(fontpane, new GridBagConstraints(3, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		
		btn = new JButton("字体");
		btn.setActionCommand("setupyfont");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		fontpane.add(btn);
		fontpane.add(new JLabel("旋转"));
		

		spnm=new SpinnerNumberModel(0,0,360,10);
		textYrotation = new JSpinner(spnm);
		textYrotation.setPreferredSize(compsize1);
		textYrotation.setMinimumSize(compsize1);
		fontpane.add(textYrotation);
		
		////Y坐标
		line++;
		lb = new JLabel("Y轴坐标");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		btn = new JButton("字体");
		btn.setActionCommand("setupylabelfont");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		
		
		
		// /////////////系列1//////////////////////
		line++;
		lb = new JLabel("系列1");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		textY1title = new JTextField();
		textY1title.setPreferredSize(compsize);
		textY1title.setMinimumSize(compsize);
		jp.add(textY1title, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY1Column = new JComboBox(cbColumnmodely1);
		cbY1Column.addItemListener(new CbYColumnHandler());
		cbY1Column.setPreferredSize(compsize);
		cbY1Column.setMinimumSize(compsize);
		jp.add(cbY1Column, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY1Column
				.addItemListener(new CbColumnHandler(cbY1Column, textY1title));

		cbY1Op = new JComboBox(Chartdefine.columnops);
		jp.add(cbY1Op, new GridBagConstraints(3, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbY1Op.setSelectedIndex(1);

		btn = new JButton("字体");
		btn.setActionCommand("setupy1font");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(4, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		// /////////////系列2//////////////////////
		line++;
		lb = new JLabel("系列2");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		textY2title = new JTextField();
		textY2title.setPreferredSize(compsize);
		textY2title.setMinimumSize(compsize);
		jp.add(textY2title, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY2Column = new JComboBox(cbColumnmodely2);
		cbY2Column.addItemListener(new CbYColumnHandler());
		cbY2Column.setPreferredSize(compsize);
		cbY2Column.setMinimumSize(compsize);
		jp.add(cbY2Column, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY2Column
				.addItemListener(new CbColumnHandler(cbY2Column, textY2title));


		// /////////////系列3//////////////////////
		line++;
		lb = new JLabel("系列3");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		textY3title = new JTextField();
		textY3title.setPreferredSize(compsize);
		textY3title.setMinimumSize(compsize);
		jp.add(textY3title, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY3Column = new JComboBox(cbColumnmodely3);
		cbY3Column.addItemListener(new CbYColumnHandler());
		cbY3Column.setPreferredSize(compsize);
		cbY3Column.setMinimumSize(compsize);
		jp.add(cbY3Column, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		cbY3Column
				.addItemListener(new CbColumnHandler(cbY3Column, textY3title));

		// //////排序
		line++;
		lb = new JLabel("排序列");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbSortcolumn = new JComboBox();
		cbSortcolumn.setPreferredSize(compsize);
		cbSortcolumn.setMinimumSize(compsize);
		jp.add(cbSortcolumn, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbSortdesc = new JCheckBox("降序排序");
		cbSortdesc.setPreferredSize(compsize);
		cbSortdesc.setMinimumSize(compsize);
		jp.add(cbSortdesc, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		//显示数字?
		line++;
		lb = new JLabel("显示数字");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbShowdata = new JComboBox(chartdefine.showdatas);
		jp.add(cbShowdata, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		
		// 图例？
		line++;
		cbLegend = new JCheckBox("显示图例");
		jp.add(cbLegend, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		btn = new JButton("字体");
		btn.setActionCommand("setuplegendfont");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb=new JLabel("最多显示记录数");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		
		SpinnerNumberModel spmaxrowcount=new SpinnerNumberModel(0,0,100000,10);
		jspnMaxrowcount = new JSpinner(spmaxrowcount);
		jspnMaxrowcount.setPreferredSize(compsize);
		jspnMaxrowcount.setMinimumSize(compsize);

		jp.add(jspnMaxrowcount, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		

		return jp;
	}

	void reversebind() {
		chartdefine.dsdefineindex=cbDs.getSelectedIndex();
		chartdefine.setDsdefine(dstable.elementAt(cbDs.getSelectedIndex()));
		chartdefine.maxrowcount=((Integer)jspnMaxrowcount.getValue()).intValue();

		chartdefine.title = textTitle.getText();
		chartdefine.xtitle = textXtitle.getText();
		chartdefine.ytitle = textYtitle.getText();
		chartdefine.y1title = textY1title.getText();
		chartdefine.y2title = textY2title.getText();
		chartdefine.y3title = textY3title.getText();

		if (cbX1Column.getSelectedIndex() >= 0) {
			chartdefine.x1column = (String) cbX1Column.getSelectedItem();
		} else {
			chartdefine.x1column = "";
		}
		if (cbY1Column.getSelectedIndex() > 0) {
			chartdefine.y1column = (String) cbY1Column.getSelectedItem();
		} else {
			chartdefine.y1column = "";
		}
		if (cbY2Column.getSelectedIndex() > 0) {
			chartdefine.y2column = (String) cbY2Column.getSelectedItem();
		} else {
			chartdefine.y2column = "";
		}
		if (cbY3Column.getSelectedIndex() > 0) {
			chartdefine.y3column = (String) cbY3Column.getSelectedItem();
		} else {
			chartdefine.y3column = "";
		}
		chartdefine.y1op = cbY1Op.getSelectedIndex();

		chartdefine.showlegend = cbLegend.isSelected();
		chartdefine.charttype = listCharttype.getSelectedIndex();
		if (cbSortcolumn.getSelectedIndex() > 0) {
			chartdefine.sortcolumn = (String) cbSortcolumn.getSelectedItem();
		}
		chartdefine.sortdesc = cbSortdesc.isSelected();
		chartdefine.colortype=cbColortype.getSelectedIndex();
		chartdefine.showdata=cbShowdata.getSelectedIndex();
		chartdefine.dimension = cbDimension.getSelectedIndex();

		chartdefine.x1fontrotation=((Integer)textX1rotation.getValue()).intValue();
		chartdefine.yfontrotation=((Integer)textYrotation.getValue()).intValue();
	}

	@Override
	protected void onOk() {
		reversebind();
		super.onOk();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("setuptitlefont".equals(e.getActionCommand())){
			setupTitlefont();
		}else if("setuplegendfont".equals(e.getActionCommand())){
			setupLegendfont();
		}else if("setupxfont".equals(e.getActionCommand())){
			setupXfont();
		}else if("setupx1font".equals(e.getActionCommand())){
			setupX1font();
		}else if("setupyfont".equals(e.getActionCommand())){
			setupYfont();
		}else if("setupylabelfont".equals(e.getActionCommand())){
			setupYLabelfont();
		}else if("setupy1font".equals(e.getActionCommand())){
			setupY1font();
		}
		super.actionPerformed(e);
	}

	void setupXfont(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.xfont);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.xfont=fc.getSelectedFont();
	}
	void setupX1font(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.x1font);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.x1font=fc.getSelectedFont();
	}

	void setupYfont(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.yfont);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.yfont=fc.getSelectedFont();
	}

	void setupYLabelfont(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.yLabelfont);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.yLabelfont=fc.getSelectedFont();
	}

	
	void setupY1font(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.y1font);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.y1font=fc.getSelectedFont();
	}

	void setupLegendfont(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.legendfont);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.legendfont=fc.getSelectedFont();
		
	}

	void setupTitlefont(){
		JFontChooser fc=new JFontChooser();
		fc.setSelectedFont(chartdefine.titlefont);
		int ret=fc.showDialog(this);
		if(ret!=JFontChooser.OK_OPTION)return;
		chartdefine.titlefont=fc.getSelectedFont();
		
	}
}
