package com.inca.npworkflow.client;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.tbar.TBar;
import com.inca.np.gui.tbar.TButton;
import com.inca.np.util.SendHelper;

/**
 * �˹�����
 * @author user
 *
 */
public class Humanapprove_ste extends CMasterModel{

	private JComboBox cbApprovemsg=null;

	public Humanapprove_ste(CFrame frame, String title,CMdeModel mde)
			throws HeadlessException {
		super(frame, title,mde);
		// TODO Auto-generated constructor stub
	}

/*	@Override
	protected void loadDBColumnInfos() {
		Vector<DBColumnDisplayInfo> cols=new WfnodeinstanceDbmodel().getDisplaycolumninfos();
		this.formcolumndisplayinfos=cols;
	}*/

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public void doQuery() {
		ClientRequest req=new ClientRequest("npserver:��ѯ������");
		try {
			setWaitCursor();
			ServerResponse resp=SendHelper.sendRequest(req);
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dm=dcmd.getDbmodel();
			dbmodel.clearAll();
			dbmodel.bindMemds(dm);
			dbmodel.sort("startdate:asc");
			sumdbmodel.fireDatachanged();
			tableChanged();
			table.autoSize();
			if(dbmodel.getRowCount()>0){
				setRow(0);
			}
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}finally{
			setDefaultCursor();
		}
		
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected JPanel createSecondtoolbar() {
		TBar tb=new TBar();
		TButton btn;
		JLabel lb=new JLabel("�������");
		tb.add(lb);
		
		String msgs[]={"","����ͨ��","�����ܾ�"};
		cbApprovemsg=new JComboBox(msgs);
		cbApprovemsg.setEditable(true);
		cbApprovemsg.setPreferredSize(new Dimension(300,27));
		tb.add(cbApprovemsg);
		
		btn=new TButton("����ͨ��");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("����ͨ��");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�����ܾ�");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("�����ܾ�");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�������");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("�������");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�鿴����");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("�鿴����");
		btn.setFocusable(false);
		tb.add(btn);

		return tb;
	}

	public String getApprovemsg(){
		Object msg=cbApprovemsg.getEditor().getItem();
		return (String)msg;
	}
}
