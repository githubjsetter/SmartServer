package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.CPlainTextField;
import com.smart.platform.gui.control.CUpperTextField;

/**
 * ��������������
 * 
 * @author user
 * 
 */
public class ReportopDlg extends CDialogOkcancel {
	private CNumberTextField textOpid;
	private CUpperTextField textOpcode;
	private CPlainTextField textOpname;
	private CPlainTextField textGroupname;
	ReportcanvasFrame frm;
	boolean isnew;
	private CPlainTextField textProdname;
	private CPlainTextField textModulename;

	public ReportopDlg(ReportcanvasFrame frm, boolean isnew) {
		super(frm, "���ñ����Ӧ�Ĺ���", true);
		this.frm = frm;
		this.isnew = isnew;
		init();
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.DISPOSE_ON_CLOSE);
	}

	void init() {
		JPanel cp = new JPanel();
		getContentPane().add(cp, BorderLayout.CENTER);
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		Dimension compsize = new Dimension(140, 27);
		int line = 0;
		JLabel lb = new JLabel("����ID");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textOpid = new CNumberTextField(0);
		if (!isnew)
			textOpid.setText(frm.getRptOpid());
		textOpid.setPreferredSize(compsize);
		cp.add(textOpid, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("���ܲ�����");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textOpcode = new CUpperTextField();
		if (!isnew)
			textOpcode.setText(frm.getOpcode());
		textOpcode.setPreferredSize(compsize);
		cp.add(textOpcode, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("��������");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textOpname = new CPlainTextField();
		if (!isnew)
			textOpname.setText(frm.getOpname());
		textOpname.setPreferredSize(compsize);
		cp.add(textOpname, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("���ܷ���");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textGroupname = new CPlainTextField();
		if (!isnew)
			textGroupname.setText(frm.getGroupname());
		textGroupname.setPreferredSize(compsize);
		cp.add(textGroupname, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("��Ʒ��");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textProdname = new CPlainTextField();
		if (!isnew)
			textProdname.setText(frm.getProdname());
		textProdname.setPreferredSize(compsize);
		cp.add(textProdname, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("ģ����");
		cp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textModulename = new CPlainTextField();
		if (!isnew)
			textModulename.setText(frm.getModulename());
		textModulename.setPreferredSize(compsize);
		cp.add(textModulename, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		getContentPane().add(createOkcancelPane(), BorderLayout.SOUTH);
	}

	@Override
	protected void onOk() {
		if (textOpid.getText().length() == 0) {
			warnMessage("��ʾ", "�������뱨���Ӧ����ID");
			return;
		}
		if (textOpname.getText().length() == 0) {
			warnMessage("��ʾ", "�������뱨���Ӧ������");
			return;
		}
		if (textGroupname.getText().length() == 0) {
			warnMessage("��ʾ", "�������뱨���Ӧ���ܷ�����");
			return;
		}
		if (textProdname.getText().length() == 0) {
			warnMessage("��ʾ", "�������뱨���Ӧ���ܲ�Ʒ��");
			return;
		}

		if (textModulename.getText().length() == 0) {
			warnMessage("��ʾ", "�������뱨���Ӧ���ܲ�Ʒģ����");
			return;
		}

		
		frm.setRptopid(textOpid.getText());
		frm.setOpcode(textOpcode.getText());
		frm.setOpname(textOpname.getText());
		frm.setGroupname(textGroupname.getText());
		frm.setProdname(textProdname.getText());
		frm.setModulename(textModulename.getText());

		super.onOk();
	}

}
