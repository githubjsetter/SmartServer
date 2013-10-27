package com.inca.np.anyprint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Category;

import com.inca.np.anyprint.impl.DrawableLine;
import com.inca.np.anyprint.impl.Partbase;
import com.inca.np.anyprint.impl.TextCell;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.control.DBTableModel;

public class Designpane extends JPanel implements ActionListener {
	AnyprintFrame frm;
	Printplan plan;
	CanvasPane canvaspane = null;
	Category logger = Category.getInstance(Designpane.class);
	private JCheckBox cbPrintborder;
	private JSpinner jspscale;
	private JScrollPane jspcanvas;

	public Designpane(AnyprintFrame frm, Printplan plan) {
		this.frm = frm;
		this.plan = plan;
		init();
	}

	void init() {
		this.setLayout(new BorderLayout());
		// 上部工具条，中部CanvasPane
		add(createToolbar(), BorderLayout.NORTH);
		canvaspane = new CanvasPane(frm, plan);
		jspcanvas = new JScrollPane(canvaspane);
		add(jspcanvas, BorderLayout.CENTER);

	}

	CToolbar createToolbar() {
		CToolbar tb = new CToolbar();
		JButton btn;
		btn = new JButton("增加数据列");
		btn.setActionCommand("增加列");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("增加表达式列");
		btn.setActionCommand("增加表达式列");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("列属性");
		btn.setActionCommand("列属性");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("删除");
		btn.setActionCommand("删除");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("页面设置");
		btn.setActionCommand("页面设置");
		btn.addActionListener(this);
		tb.add(btn);

		cbPrintborder = new JCheckBox("打印表格");
		cbPrintborder.addChangeListener(new Cblistener());
		tb.add(cbPrintborder);
		cbPrintborder.setSelected(plan.getParts().isPrintline());

		tb.add(new JLabel("显示比例%"));
		
		Dimension compsize=new Dimension(60,27);
		SpinnerNumberModel spnm=new SpinnerNumberModel(100,25,300,10);
		jspscale = new JSpinner(spnm);
		tb.add(jspscale);
		jspscale.setPreferredSize(compsize);
		jspscale.setMaximumSize(compsize);
		jspscale.addChangeListener(new SpscaleHandler());

		
		btn = new JButton("横线");
		btn.setActionCommand("加横线");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("竖线");
		btn.setActionCommand("加竖线");
		btn.addActionListener(this);
		tb.add(btn);
		
		return tb;
	}
	
	class SpscaleHandler implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			int scale=((Integer)jspscale.getValue()).intValue();
			double scalerate=(double)scale/100.0;
			canvaspane.setScalerate(scalerate);
			jspcanvas.setViewportView(canvaspane);
			canvaspane.repaint();
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("增加列".equals(cmd)) {
			onAddcolumn();
		}else if("列属性".equals(cmd)){
			onCellprop();
		}else if("增加表达式列".equals(cmd)){
			onAddExprcolumn();
		}else if("页面设置".equals(cmd)){
			onSetuppage();
		}else if("加横线".equals(cmd)){
			addHorizontalline();
		}else if("加竖线".equals(cmd)){
			addVerticalline();
		}else if("删除".equals(cmd)){
			plan.getParts().delActivecell();
			canvaspane.repaint();
		}
	}
	
	void addHorizontalline(){
		Partbase apart=plan.getParts().getActivepart();
		if(apart==null){
			apart=plan.getParts().getHead();
		}
		
		DrawableLine line=new DrawableLine();
		line.p1=new Point(0,10);
		line.p2=new Point(100,10);
		apart.addDrawableline(line);
		canvaspane.repaint();



	}
	
	void addVerticalline(){
		Partbase apart=plan.getParts().getActivepart();
		if(apart==null){
			apart=plan.getParts().getHead();
		}
		
		DrawableLine line=new DrawableLine(DrawableLine.LINETYPE_VERTICAL);
		line.p1=new Point(50,10);
		line.p2=new Point(50,40);
		apart.addDrawableline(line);

		canvaspane.repaint();

		
	}
	
	void onSetuppage(){
		PagesetupDlg dlg=new PagesetupDlg(frm,plan);
		dlg.pack();
		dlg.setVisible(true);
		if(dlg.isOk()){
			plan.parts.setDatadirty(true);
		}
	}
	
	void onAddExprcolumn(){
		frm.setWaitcursor();
		CellpropDlg dlg=new CellpropDlg(frm,plan);
		frm.setDefaultcursor();
		TextCell newcell=new TextCell("");
		Rectangle rect=new Rectangle(10,10,40,20);
		newcell.setRect(rect);
		int defaultpartindex=0;
		Partbase activepart=plan.getParts().getActivepart();
		if(activepart!=null){
			for(int i=0;i<plan.getParts().getPartcount();i++){
				if(activepart==plan.getParts().getPart(i)){
					defaultpartindex=i;
					break;
				}
			}
		}
		dlg.addCell(newcell,defaultpartindex);
		dlg.pack();
		dlg.setVisible(true);
		
		int partindex=dlg.getPartindex();
		Partbase part=plan.getParts().getPart(partindex);
		part.addCell(newcell);
		repaint();
	}
	
	void onCellprop(){
		//列出所有的列，进行调整
		frm.setWaitcursor();
		CellpropDlg dlg=new CellpropDlg(frm,plan);
		frm.setDefaultcursor();

		dlg.pack();
		dlg.setVisible(true);
		canvaspane.repaint();
	}

	public void bind(){
		cbPrintborder.setSelected(plan.getParts().isPrintline());
		jspcanvas.setViewportView(canvaspane);
	}

	void onAddcolumn() {
		try {
			frm.setWaitcursor();
			DBTableModel dbmodel = plan.createFulldatamodel();
			Addcolumn2canvasDlg dlg = new Addcolumn2canvasDlg(frm, dbmodel);
			dlg.pack();
			frm.setDefaultcursor();
			dlg.setVisible(true);
			if (!dlg.isOk())
				return;
			plan.getParts().addColumns(dlg.getColumntable(), dlg.getAddtype(),
					dlg.getAddpos());
			canvaspane.repaint();
		} catch (Exception e) {
			logger.error("e", e);
			JOptionPane.showMessageDialog(frm, e.getMessage());
			return;
		}
	}
	@Override
	public boolean isFocusable() {
		return true;
	}

	class Cblistener implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			JCheckBox cb=(JCheckBox) e.getSource();
			plan.getParts().setPrintline(cb.isSelected());
			canvaspane.repaint();
		}
		
	}
}
