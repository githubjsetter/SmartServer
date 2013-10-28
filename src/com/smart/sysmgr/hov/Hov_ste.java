package com.smart.sysmgr.hov;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.smart.extension.ap.Aphelper;
import com.smart.extension.ste.ApIF;
import com.smart.extension.ste.Apinfo;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.sysmgr.roleop.Role_hov;

/*����"���Ź���"����༭Model*/
public class Hov_ste extends CSteModel implements ApIF{
	public Hov_ste(CFrame frame) throws HeadlessException {
		super(frame, "HOV");
		this.setTableeditable(true);
	}

	public String getTablename() {
		return "np_hov";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.hov.Hov_ste.����HOV����";
	}

	@Override
	protected JPanel createSecondtoolbar() {
		JPanel jp=new JPanel();
		JButton btn=new JButton("������Ȩ����");
		btn.setActionCommand("setap");
		btn.addActionListener(this);
		btn.setFocusable(false);
		jp.add(btn);
		return jp;
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("setap")){
			onSetap();
			return 0;
		}
		return super.on_actionPerformed(command);
	}
	
	HashMap<String,Apinfo> apinfomap=null;
	/**
	 * ��һ����ѡ��hov,�г���ɫ,����ѡ��
	 * ѡ��֮��,ͳһ���ò�ѯ������Ȩ����
	 */
	protected void onSetap(){
		int row=this.getRow();
		if(row<0){
			warnMessage("��ʾ","��ѡ��һ��HOV");
			return;
		}
		String hovclassname=getItemValue(row,"classname");
		Role_hov rolehov=new Role_hov();
		rolehov.showDialog(this.getParentFrame(),"ѡ���ɫ");
		DBTableModel hovdbmodel=rolehov.getResult();
		if(hovdbmodel==null){
			return;
		}
	
		
		String roleid=hovdbmodel.getItemValue(0,"roleid");
		String hovid;
		try{
			StringBuffer hovidsb=new StringBuffer();
			apinfomap=Aphelper.downloadHovAp(hovclassname,roleid,hovidsb);
			hovid=hovidsb.toString();
		}catch(Exception e){
			errorMessage("����",e.getMessage());
			return;
		}
		
		//����������������
		HovapDlg setupdlg=new HovapDlg(this.getParentFrame(),"����HOV��Ȩ����",this);
		setupdlg.pack();
		setupdlg.setVisible(true);
		if(!setupdlg.getOk()){
			return;
		}
		Vector<Apinfo> apinfos=setupdlg.getApinfos();
		
		//����
		if(!saveAp(roleid,hovid,apinfos)){
			return;
		}
		infoMessage("�ɹ�","������Ȩ���Գɹ�");
	
	}
	
	protected boolean saveAp(String roleid,String hovid,Vector<Apinfo> apinfos){
		try{
			Aphelper.saveHovAp(roleid, hovid, apinfos);
		}catch(Exception e){
			errorMessage("����",e.getMessage());
			return false;
		}
		return true;
	}

	public Apinfo getApinfo(String apname) {
		return apinfomap.get(apname);
	}

	public String getApvalue(String apname) {
		Apinfo info=getApinfo(apname);
		return info==null?"":info.getApvalue();
	}

	public boolean isDevelopCandelete() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDevelopCanmodify() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDevelopCannew() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDevelopCanquery() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDevelopCansave() {
		// TODO Auto-generated method stub
		return false;
	}

	public Vector<Apinfo> getParamapinfos() {
		return new Vector<Apinfo>();
	}

	public String getAutoprintplan() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAutoprintplan(String planname) {
		// TODO Auto-generated method stub
		
	}
	
	
}
