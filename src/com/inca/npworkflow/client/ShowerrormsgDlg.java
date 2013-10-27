package com.inca.npworkflow.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;

public class ShowerrormsgDlg extends CDialogOkcancel{
	DBTableModel dbmodel;
	public ShowerrormsgDlg(Frame frame,DBTableModel dbmodel){
		super(frame,"´íÎóÊý¾ÝÏî",true);
		this.dbmodel=dbmodel;
		
		init();
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.DISPOSE_ON_CLOSE);
	}
	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		CTable table=new CTable(dbmodel);
		cp.add(new JScrollPane(table),BorderLayout.CENTER);
		table.autoSize();
		
		JPanel bottompanel=createOkcancelPane();
		cp.add(bottompanel,BorderLayout.SOUTH);
	}
}
