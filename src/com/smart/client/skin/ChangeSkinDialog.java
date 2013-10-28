package com.smart.client.skin;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CMessageDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.CTableHeaderRender;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ui.CTableheadUI;

/*
 * 切换界面方案Dialog
 */
public class ChangeSkinDialog extends CDialogOkcancel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DBTableModel db;
	private CTable table;
	String opid;

	public ChangeSkinDialog(CFrame cframe, String opid,DBTableModel db) {
		super(cframe, "选择界面方案", true);
		this.db = db;
		this.opid=opid;
		createtop();
		getContentPane().add(super.createOkcancelPane(), "South");
		JButton btn=new JButton("删除");
		btn.setName("btnDelete");
		btn.setActionCommand("delete");
		btn.addActionListener(this);
		getContentPane().add(btn);
		localCenter();

	}

	private void createtop() {
		table = new CTable(db);
		table.addMouseListener(this);
		table.getTableHeader().setDefaultRenderer(new CTableHeaderRender());
		table.getTableHeader().setUI(new CTableheadUI());
		table.setReadonly(true);
		table.setRequestFocusEnabled(false);
		table.getTableHeader().setUI(new CTableheadUI());
		table.setBackground(Color.WHITE);
		table.getColumnModel().getColumn(0).setPreferredWidth(220);
		table.getColumnModel().getColumn(0).setWidth(220);
		InputMap inputmap = table.getInputMap(1).getParent();
		inputmap.remove(KeyStroke.getKeyStroke(10, 0, false));
		JScrollPane jc = new JScrollPane(table);
		jc.setBackground(Color.WHITE);
		
		jc.setName("jc");
		getContentPane().add(jc, "Center");
		if(db.getRowCount()>0){
			table.getSelectionModel().addSelectionInterval(0, 0);
		}

	}

	public String getSelectValue() {

		return db.getItemValue(table.getSelectedRow(), "schemename");

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("delete")){
			int row=table.getRow();
			String name=db.getItemValue(row, "schemename");
			if(row<0)return;
			try {
				SkinHelper.deleteScheme(opid, ClientUserManager
						.getCurrentUser().getUserid(), name);
				db.removeRow(row);
				table.tableChanged(new TableModelEvent(db));
				if(row<db.getRowCount()){
					table.getSelectionModel().addSelectionInterval(row, row);
				}else if(row - 1 < db.getRowCount()){
					row--;
					table.getSelectionModel().addSelectionInterval(row,row);
				}
			} catch (Exception e1) {
				errorMessage("错误", e1.getMessage());
			}

		}else{
			super.actionPerformed(e);
		}
	}

	public void mouseClicked(MouseEvent mouseevent) {
		if (mouseevent.getClickCount() == 2) {
			// 双击
			onOk();
			return;
		}
	}

	@Override
	protected void onOk() {
		if (table != null) {
			if (table.getSelectedRow() > -1) {
				super.onOk();
				return;
			}
			CMessageDialog.infoMessage(this, "提示", "请选择一条记录");
		} else {
			super.onOk();
		}

	}
	
	
}
