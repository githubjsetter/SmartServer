package com.inca.npx.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SendHelper;


/**
 * �г����еĴ�ӡ����
 * @author Administrator
 *
 */
public class PrintplanlistDlg extends CDialogOkcancel{
	ApIF apif=null;
	public PrintplanlistDlg(JDialog owner,ApIF apif){
		super(owner,"��ӡ��������",true);
		this.apif=apif;
		init();
		bind();
		localCenter();
		table.autoSize();
		setDefaultCloseOperation(CDialogOkcancel.DISPOSE_ON_CLOSE);
	}
	void bind(){
		//�г�apif�е����д�ӡ����
		String autoprintplan=apif.getAutoprintplan();
		Enumeration<String> en=apif.getPrintplans().elements();
		while(en.hasMoreElements()){
			String planname=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r,"planname",planname);
			if(planname.equals(autoprintplan)){
				dbmodel.setItemValue(r, "autoprint","1");
			}
		}
		
	}
	DBTableModel dbmodel=null;
	CEditableTable table=null;
	void init(){
		dbmodel=createDm(); 
		table=new CEditableTable(new Sumdbmodel(dbmodel,null));
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		JScrollPane jsp=new JScrollPane(table);
		jsp.setName("jsptable");
		cp.add(jsp,BorderLayout.CENTER);
		
		JPanel jp=createOkcancelPane();
		cp.add(jp,BorderLayout.SOUTH);
	}
	
	protected JPanel createOkcancelPane() {
		JPanel jp = new JPanel();
		JButton btn = new JButton("��������");
		addEnterkeyConfirm(btn);
		btn.addActionListener(this);
		btn.setActionCommand("����");
		btn.setName("btnadd");
		jp.add(btn);


		
		btn = new JButton("ȷ��");
		addEnterkeyConfirm(btn);
		btn.addActionListener(this);
		btn.setActionCommand("ok");
		btn.setName("btnok");
		jp.add(btn);

		btn = new JButton("ȡ��");
		addEnterkeyTraver(btn);
		btn.addActionListener(this);
		btn.setActionCommand("cancel");
		btn.setName("btncancel");
		jp.add(btn);

		btn = new JButton("ɾ������");
		addEnterkeyConfirm(btn);
		btn.addActionListener(this);
		btn.setActionCommand("ɾ��");
		btn.setName("btndelete");
		jp.add(btn);
		
		
		if(DefaultNPParam.debug==1){
			jp.add(createUIDesignbutton());
		}

		
		return jp;
	}

	
	DBTableModel createDm(){
		DBTableModel plandm=listPlan();

		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("planname","varchar","��ӡ��������");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		cols.add(col);
		col.setTablecolumnwidth(100);
		if(plandm!=null){
			for(int i=0;i<plandm.getRowCount();i++){
				String planname=plandm.getItemValue(i, "planname");
				int p=planname.lastIndexOf(".");
				if(p>0)
					planname=planname.substring(0,p);
				col.addComboxBoxItem(planname, planname);
			}
		}
		
		col=new DBColumnDisplayInfo("autoprint","varchar","�Զ���ӡ");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);
		col.setTablecolumnwidth(200);
		
		
		return new DBTableModel(cols);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.equals("����")){
			addPlan();
			return;
		}else if(cmd.equals("ɾ��")){
			delRow();
			return;
		}
		super.actionPerformed(e);
	}
	
	void addPlan(){
		dbmodel.appendRow();
		table.tableChanged(new TableModelEvent(table.getModel()));
	}
	
	void delRow(){
		int r=table.getRow();
		if(r<0 || r>=dbmodel.getRowCount())return;
		dbmodel.removeRow(r);
		table.tableChanged(new TableModelEvent(table.getModel()));
	}
	
	DBTableModel listPlan(){
		ClientRequest req=new ClientRequest("npclient:�г���ӡ����");
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dm=dcmd.getDbmodel();
			return dm;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return null;
		}
		
	}
	@Override
	protected void onOk() {
		String autoprintplan=null;
		table.stopEdit();
		//���ô�ӡ����
		Vector<String> plannames=new Vector<String>();
		for(int i=0;i<dbmodel.getRowCount();i++){
			String planname=dbmodel.getItemValue(i, "planname");
			if(dbmodel.getItemValue(i, "autoprint").equals("1")){
				if(autoprintplan!=null){
					JOptionPane.showMessageDialog(this, "ֻ��ѡһ������Ϊ�Զ�����!");
					return;
				}
				autoprintplan=planname;
			}
			plannames.add(planname);
		}
		apif.setPrintplans(plannames);
		if(autoprintplan==null)autoprintplan="";
		apif.setAutoprintplan(autoprintplan);
		
		
		super.onOk();
	}
	
	

}
