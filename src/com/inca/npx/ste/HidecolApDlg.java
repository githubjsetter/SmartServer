package com.inca.npx.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.HeadlessException;

import javax.swing.JScrollPane;

import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBTableModel;

public class HidecolApDlg  extends CDialogOkcancel{
	DBTableModel dmhide;
	ApIF apif;
	public HidecolApDlg(Dialog owner,ApIF apif,DBTableModel dmhide) throws HeadlessException {
		super(owner,"…Ë÷√¡–“˛≤ÿ",true);
		this.apif=apif;
		this.dmhide=dmhide;
		init();
		localCenter();

	}
	
	void init(){
		Container jp=getContentPane();
		CEditableTable table = new CEditableTable(dmhide);

		jp.setLayout(new BorderLayout());
		JScrollPane jsp=new JScrollPane(table);
		jsp.setName("jsptable");
		jp.add(jsp, BorderLayout.CENTER);
		
		jp.add(createOkcancelPane(),BorderLayout.SOUTH);
	}


}
