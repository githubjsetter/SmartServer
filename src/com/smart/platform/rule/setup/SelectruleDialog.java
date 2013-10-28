package com.smart.platform.rule.setup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.rule.define.Rulebase;

/**
 * 选择并设置
 * @author Administrator
 *
 */
public class SelectruleDialog extends CDialog{
	Rulebase rule=null;
	String optype="";
	Object caller=null;
	private JList listRule;
	private JTextArea textExpr = new JTextArea(2,40);
	boolean ok;
	
	/**
	 * 
	 * @param frame
	 * @param optype ste|mde
	 */
	public SelectruleDialog(Dialog frame,Object caller,String optype){
		super(frame,"新增规则",true);
		this.caller=caller;
		this.optype=optype;
		initDialog();
		this.localScreenCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("setup")){
			onSetup();
		}else if(e.getActionCommand().equals("ok")){
			onSetup();
		}else if(e.getActionCommand().equals("cancel")){
			onCancel();
		}
	}

	void onSetup(){
		int i=listRule.getSelectedIndex();
		if(i<0){
			JOptionPane.showMessageDialog(this,"请选择一个规则","提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			if(rule.setupUI(caller)){
				textExpr.setText(rule.getExpr());
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("没有参数")>=0){
				return;
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
		}
		ok=true;
		this.dispose();
		
	}


	protected void initDialog(){
		Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		listRule = createRulelist();
		listRule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listRule.addListSelectionListener(new RuleListListener());
		listRule.addMouseListener(new ListMouseListener());
		
		cp.add(new JScrollPane(listRule),BorderLayout.WEST);
		
		//中间是表达式
		//cp.add(new JScrollPane(createMidpane()),BorderLayout.CENTER);
		
		//下面按钮
		cp.add(new JScrollPane(createBottompane()),BorderLayout.SOUTH);
	}
	
	class ListMouseListener implements MouseListener{
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				onSetup();
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
	
	class RuleListListener implements ListSelectionListener{

		public void valueChanged(ListSelectionEvent e) {
			if(e.getFirstIndex()<0)return;
			String ruletype=(String)listRule.getSelectedValue();
			rule=RuleRepository.createRule(ruletype);
			rule.setRuletype(ruletype);
			rule.setExpr(textExpr.getText());
		}
	}
	
	public boolean getOk(){
		return ok;
	}
	
	void onCancel(){
		ok=false;
		this.dispose();
	}
	
	public Rulebase getRule(){
		return rule;
	}
	
	
	protected JPanel createBottompane(){
		JPanel jp=new JPanel();

		JButton btnok=new JButton("确定");
		btnok.setActionCommand("ok");
		btnok.addActionListener(this);
		jp.add(btnok);
		
		JButton btncancel=new JButton("取消");
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(this);
		jp.add(btncancel);
		return jp;
	}
	
	protected JPanel createMidpane(){
		JPanel jp=new JPanel();
		jp.setLayout(new BorderLayout());
		JButton btnsetup=new JButton("设置表达式");
		btnsetup.setActionCommand("setup");
		btnsetup.addActionListener(this);
		
		jp.add(btnsetup,BorderLayout.NORTH);
		
		jp.add(textExpr,BorderLayout.CENTER);
		
		return jp;
	}
	
	protected JList createRulelist(){
		
		if(optype.equals("report")){
			String ruletypes[]=RuleRepository.getReportRuletypes(false);
			return new JList(ruletypes);
		}else{
			String ruletypes[]=RuleRepository.getRuletypes(optype.equals("mde"));
			return new JList(ruletypes);
		}
	}
	
	public static void main(String[] argv){
		Pub_goods_ste ste=new Pub_goods_ste(null);
		SelectruleDialog dlg=new SelectruleDialog(null,ste,"ste");
		dlg.pack();
		dlg.setVisible(true);
	}
}
