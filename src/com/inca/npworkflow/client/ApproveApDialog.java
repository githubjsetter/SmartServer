package com.inca.npworkflow.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inca.np.gui.control.CDialog;
import com.inca.npx.ste.ApIF;
import com.inca.npx.ste.Apinfo;

/**
 * ������Ȩ����dialog.���ò�ѯ����
 * @author user
 *
 */
public class ApproveApDialog extends CDialog{
	ApIF apif;
	private JTextArea textquerycond;
	private boolean ok = false;
	public ApproveApDialog(Frame owner, String title, ApIF apif)
			throws HeadlessException {
		super(owner, title, true);
		this.apif = apif;
		initdialog();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		this.localCenter();
	}
	
	void initdialog(){
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(createDataconstraintPanel(),BorderLayout.CENTER);
		cp.add(createBottomPanel(),BorderLayout.SOUTH);
	}
	
	protected JPanel createDataconstraintPanel() {
		JPanel jp = new JPanel();
		GridBagLayout g = new GridBagLayout();
		jp.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();
		JLabel lb = new JLabel("ԭʼ������ȨԼ��(where����)");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		textquerycond = new JTextArea(6, 50);
		c.gridwidth = GridBagConstraints.RELATIVE;
		g.setConstraints(textquerycond, c);
		jp.add(new JScrollPane(textquerycond));
		String wheres = apif.getApvalue(Apinfo.APNAME_WHERES);
		textquerycond.setText(wheres);
		
		JPanel buttonpanel = createButtonpanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(buttonpanel, c);
		jp.add(buttonpanel);

		
		return jp;
	}
	
	protected JPanel createButtonpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(layout);

		JButton btn = new JButton("��ǰ����");
		btn.setActionCommand("��ǰ����");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("��ǰ��Ա");
		btn.setActionCommand("��ǰ��Ա");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("��ǰ��ɫ");
		btn.setActionCommand("��ǰ��ɫ");
		btn.addActionListener(this);
		jp.add(btn);

		return jp;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ok")) {
			onOk();
		} else if (command.equals("cancel")) {
			onCancel();
		} else if (command.equals("��ǰ����")) {
			textquerycond.replaceSelection("<��ǰ����ID>");
		} else if (command.equals("��ǰ��Ա")) {
			textquerycond.replaceSelection("<��ǰ��ԱID>");
		} else if (command.equals("��ǰ��ɫ")) {
			textquerycond.replaceSelection("<��ǰ��ɫID>");
		}

	}

	protected JPanel createBottomPanel() {
		JPanel jp = new JPanel();
		JButton btn = new JButton("ȷ��");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("ȡ��");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		return jp;
	}

	protected void onOk() {
		ok = true;
		dispose();
	}

	protected void onCancel() {
		ok = false;
		dispose();
	}

	public boolean getOk() {
		return ok;
	}

	public Vector<Apinfo> getApinfos() {
		Vector<Apinfo> infos = new Vector<Apinfo>();
		Apinfo info = null;

		// ��Ȩ����
		info = new Apinfo(Apinfo.APNAME_WHERES, Apinfo.APTYPE_DATA);
		info.setApvalue(textquerycond.getText());
		infos.add(info);


		return infos;
	}

	public static void main(String[] args) {
		ApproveApDialog dlg=new ApproveApDialog(null,"",null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
