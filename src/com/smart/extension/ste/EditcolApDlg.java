package com.smart.extension.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.HeadlessException;

import javax.swing.JScrollPane;

import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBTableModel;

/**
 * ���ÿɱ༭��
 * @author user
 *
 */
public class EditcolApDlg extends CDialogOkcancel{
	DBTableModel dmeditable;
	ApIF apif;
	public EditcolApDlg(Dialog owner,ApIF apif,DBTableModel dmeditable) throws HeadlessException {
		super(owner,"���ÿɱ༭��",true);
		this.apif=apif;
		this.dmeditable=dmeditable;
		init();
		localCenter();
		
	}
	
	void init(){
		Container jp=getContentPane();
		CEditableTable table = new CEditableTable(dmeditable);

		jp.setLayout(new BorderLayout());
		JScrollPane jsp=new JScrollPane(table);
		jsp.setName("jsptable");
		jp.add(jsp, BorderLayout.CENTER);
		
		jp.add(createOkcancelPane(),BorderLayout.SOUTH);
	}


}
