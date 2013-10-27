package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

public class BireportParampane extends JPanel implements ActionListener{
	DBTableModel paramdm=null;
	private CEditableTable table;
	public BireportParampane(){
		setLayout(new BorderLayout());
		paramdm=new DBTableModel(createCols());
		table = new CEditableTable(paramdm);
		JScrollPane jsp=new JScrollPane(table);
		add(jsp,BorderLayout.CENTER);
		
		//������
		JPanel tb=createToolbar();
		add(tb,BorderLayout.NORTH);
		
	}
	JPanel createToolbar(){
		JPanel jp=new JPanel();
		BoxLayout box=new BoxLayout(jp,BoxLayout.X_AXIS);
		jp.setLayout(box);
		
		JButton btn;
		btn=new JButton("���Ӳ���");
		btn.setActionCommand("newparam");
		btn.addActionListener(this);

		btn=new JButton("���Ӳ���");
		btn.setActionCommand("newparam");
		btn.addActionListener(this);

		return jp;
	}

	static Vector<DBColumnDisplayInfo> createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("paramname","varchar","��������");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("paramtype","varchar","��������");
		col.addComboxBoxItem("number", "����");
		col.addComboxBoxItem("varchar", "�ַ���");
		col.addComboxBoxItem("datetime", "����ʱ��");
		cols.add(col);
		
		
		col=new DBColumnDisplayInfo("title","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("mustinput","varchar","����");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		col=new DBColumnDisplayInfo("hovclass","varchar","ʹ�õ�HOV");
		cols.add(col);

		col=new DBColumnDisplayInfo("hovcolumn","varchar","HOV��");
		cols.add(col);

		
		return cols;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
