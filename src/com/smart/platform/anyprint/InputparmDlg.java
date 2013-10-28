package com.smart.platform.anyprint;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.smart.platform.gui.control.CDialogOkcancel;

/**
 * �����ʼ����
 * @author Administrator
 *
 */
public class InputparmDlg extends CDialogOkcancel{
	Printplan plan=null;
	private JTextArea textInputparam;
	public InputparmDlg(Frame frm,Printplan plan){
		super(frm,"������ô��õ���ڲ���",true);
		this.plan=plan;
		init();
		bind();
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.DISPOSE_ON_CLOSE);
	}
	
	void bind(){
		if(plan!=null){
			textInputparam.setText(plan.getDefaultinputparam());
		}
	}
	
	void init(){
		GridBagLayout g=new GridBagLayout();
		Container cp=getContentPane();
		cp.setLayout(g);
		
		int y=0;
		JLabel lb=new JLabel("��������:");
		cp.add(lb,new GridBagConstraints(0,y,1,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.NONE,
				new Insets(2,2,2,2),0,0));
		y++;
		textInputparam = new JTextArea(3,60);
		cp.add(new JScrollPane(textInputparam),new GridBagConstraints(0,y,1,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.NONE,
				new Insets(2,2,2,2),0,0));
		y++;
		lb=new JLabel("˵��:����������IDö�٣���1,3,1003��Ҳ������SQL����select id from tablename");
		cp.add(lb,new GridBagConstraints(0,y,1,1,1.0,1.0,GridBagConstraints.WEST,GridBagConstraints.NONE,
				new Insets(2,2,2,2),0,0));
		
		JPanel jptb=createOkcancelPane();
		y++;
		cp.add(jptb,new GridBagConstraints(0,y,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,
				new Insets(2,2,2,2),0,0));
	}
	

	@Override
	protected void onOk() {
		if(plan!=null){
			plan.setDefaultinputparam(textInputparam.getText());
		}
		super.onOk();
	}

	public static void main(String[] args) {
		InputparmDlg dlg=new InputparmDlg(null,null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
