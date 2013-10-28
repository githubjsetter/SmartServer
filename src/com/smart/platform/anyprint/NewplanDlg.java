package com.smart.platform.anyprint;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CToolbar;

public class NewplanDlg extends CDialog{
	private JTextField textPlanname;
	private JComboBox cbPrinttype;
	boolean ok=false;

	public NewplanDlg(java.awt.Frame owner){
		super(owner,"�½���ӡ����",true);
		init();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	void init(){
		Container cp=this.getContentPane();
		GridBagLayout g=new GridBagLayout();
		cp.setLayout(g);
		
		int y=0;
		JLabel lb=new JLabel("��������");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		textPlanname = new JTextField(20);
		addEnterkeyTraver(textPlanname);
		cp.add(textPlanname, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		y++;
		lb=new JLabel("��ӡ����");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		String[] printtype={"����ӡ","��Ƭ��ӡ"};
		cbPrinttype = new JComboBox(printtype);
		addEnterkeyTraver(cbPrinttype);
		cp.add(cbPrinttype, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		JPanel tb=new JPanel();
		JButton btn=new JButton("ȷ��");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		tb.add(btn);
		btn=new JButton("ȡ��");
		addEnterkeyTraver(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		tb.add(btn);	
		y++;
		cp.add(tb, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.equals("ok")){
			onOk();
		}else if(cmd.equals("cancel")){
			onCancel();
		}
	}
	
	String planname;
	String plantype;
	
	
	@Override
	protected void enterkeyConfirm() {
		onOk();
	}

	void onOk(){
		planname = textPlanname.getText().trim();
		plantype=(String) cbPrinttype.getSelectedItem();
		if(planname.trim().length()==0){
			JOptionPane.showMessageDialog(this, "�������뷽������");
			return;
		}
		ok=true;
		this.dispose();
	}
	
	public String getPlanname() {
		return planname;
	}

	public String getPlantype() {
		return plantype;
	}

	void onCancel(){
		ok=false;
		this.dispose();
	}
	
	public boolean isOk(){
		return ok;
	}

	public static void main(String[] args) {
		NewplanDlg dlg=new NewplanDlg(null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
