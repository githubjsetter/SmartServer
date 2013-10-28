package com.smart.workflow.client;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.tbar.TBar;
import com.smart.platform.gui.tbar.TButton;
import com.smart.platform.util.SendHelper;

/**
 * 人工审批
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
		ClientRequest req=new ClientRequest("npserver:查询待审批");
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
			errorMessage("错误",e.getMessage());
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
		JLabel lb=new JLabel("审批意见");
		tb.add(lb);
		
		String msgs[]={"","审批通过","审批拒绝"};
		cbApprovemsg=new JComboBox(msgs);
		cbApprovemsg.setEditable(true);
		cbApprovemsg.setPreferredSize(new Dimension(300,27));
		tb.add(cbApprovemsg);
		
		btn=new TButton("审批通过");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("审批通过");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("审批拒绝");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("审批拒绝");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("请求参批");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("请求参批");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("查看单据");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("查看单据");
		btn.setFocusable(false);
		tb.add(btn);

		return tb;
	}

	public String getApprovemsg(){
		Object msg=cbApprovemsg.getEditor().getItem();
		return (String)msg;
	}
}
