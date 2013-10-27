package com.inca.npx.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.HeadlessException;

import javax.swing.JScrollPane;

import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBTableModel;

/**
 * 设置可编辑列
 * @author user
 *
 */
public class EditcolApDlg extends CDialogOkcancel{
	DBTableModel dmeditable;
	ApIF apif;
	public EditcolApDlg(Dialog owner,ApIF apif,DBTableModel dmeditable) throws HeadlessException {
		super(owner,"设置可编辑列",true);
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
