package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.impl.PartschangListener;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.control.DBTableModel;

public class PreviewPane  extends JPanel implements ActionListener,PartschangListener {
	AnyprintFrame frm;
	Printplan plan;
	PreviewcanvasPane canvaspane = null;
	Category logger = Category.getInstance(PreviewPane.class);
	private JTextField textPageno;
	private JLabel lbPagecount;
	private JSpinner jspscale;
	private JScrollPane spcanvaspane;

	public PreviewPane(AnyprintFrame frm, Printplan plan) {
		this.frm = frm;
		this.plan = plan;
		init();
		plan.getParts().addChangelistener(this);
	}

	void init() {
		this.setLayout(new BorderLayout());
		// 上部工具条，中部CanvasPane
		add(createToolbar(), BorderLayout.NORTH);
		canvaspane = new PreviewcanvasPane(frm, plan);
		spcanvaspane = new JScrollPane(canvaspane);
		add(spcanvaspane, BorderLayout.CENTER);
	}

	

	CToolbar createToolbar() {
		CToolbar tb = new CToolbar();
		tb.setLayout(new CFormlayout(2,2));
		
		JButton btn;
		btn = new JButton("设置调用参数");
		btn.setActionCommand("设置调用参数");
		btn.addActionListener(this);
		tb.add(btn);
		
		textPageno = new JTextField("1",4);
		tb.add(textPageno);
		JLabel lb=new JLabel("/");
		tb.add(lb);
		
		lbPagecount = new JLabel();
		tb.add(lbPagecount);
		
		JButton btnGo=new JButton("跳转");
		btnGo.setActionCommand("go");
		btnGo.addActionListener(this);
		tb.add(btnGo);
		
		tb.add(new JLabel("显示比例%"));
		
		Dimension compsize=new Dimension(60,27);
		SpinnerNumberModel spnm=new SpinnerNumberModel(100,25,300,10);
		jspscale = new JSpinner(spnm);
		tb.add(jspscale);
		jspscale.setPreferredSize(compsize);
		jspscale.setMaximumSize(compsize);
		jspscale.addChangeListener(new SpscaleHandler());

		
		return tb;
	}
	
	class SpscaleHandler implements ChangeListener{

		public void stateChanged(ChangeEvent e) {
			int scale=((Integer)jspscale.getValue()).intValue();
			double scalerate=(double)scale/100.0;
			canvaspane.setScalerate(scalerate);
			spcanvaspane.setViewportView(canvaspane);
			canvaspane.repaint();
		}
		
	}

	

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("设置调用参数".equals(cmd)) {
			onGetinputparam();
		}else if("go".equals(cmd)){
			int pageno=1;
			try {
				pageno=Integer.parseInt(textPageno.getText());
			} catch (Exception e1) {
				
			}
			canvaspane.showPage(pageno-1);
		}
	}

	void onGetinputparam() {
		InputparmDlg dlg=new InputparmDlg(frm,plan);
		dlg.pack();
		dlg.setVisible(true);
		if(dlg.isOk()){
			plan.setInputparam(plan.getDefaultinputparam());
			try {
				plan.getParts().prepareData();
				lbPagecount.setText(String.valueOf(plan.getParts().getPagecount()));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frm, e.getMessage());
				return;
			}
			textPageno.setText("1");
			canvaspane.showPage(0);
			lbPagecount.setText(String.valueOf(plan.getParts().getPagecount()));
		}
	}

	public void bind() {
		plan.getParts().addChangelistener(this);
		spcanvaspane.setViewportView(canvaspane);
	}

	/**
	 * part重新分页
	 */
	public void dataChanged() {
		System.out.println("dataChanged ,pagect="+plan.getParts().getPagecount());
		lbPagecount.setText(String.valueOf(plan.getParts().getPagecount()));
	}
}

