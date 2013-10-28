package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.DBTableModel;
import com.sun.jmx.snmp.EnumRowStatus;

/**
 * 定义分组. 左边为列清单. 右边为分组列.
 * 
 * @author user
 * 
 */
public class GroupsetupDlg extends CDialogOkcancel {
	Tablevdesignpane designpane;
	ColumndragTable columntable = null;
	JList listgroupcols = null;
	Vector<String> groupcols;

	public GroupsetupDlg(Tablevdesignpane designpane,Vector<String> groupcols) {
		super((Frame)null, "设置分组", true);
		this.designpane = designpane;
		this.groupcols=groupcols;
		init();
		localCenter();
	}

	void init() {
		String helpmsg = "用鼠标拖拽左边的列到右边进行分组.右边的列按先大组后小组级别排序.";
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		JLabel lb = new JLabel(helpmsg);
		cp.add(lb, BorderLayout.NORTH);
		JSplitPane sp = new JSplitPane();
		cp.add(sp, BorderLayout.CENTER);
		columntable = designpane.createColumntable();
		columntable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		JScrollPane tsp=new JScrollPane(columntable);
		tsp.setPreferredSize(new Dimension(300,400));
		sp.setLeftComponent(tsp);

		DefaultListModel lm = new DefaultListModel();
		Enumeration<String>en=groupcols.elements();
		while(en.hasMoreElements()){
			lm.addElement(en.nextElement());
		}
		listgroupcols = new JList(lm);
		listgroupcols.setDragEnabled(true);
		listgroupcols.setTransferHandler(new Listtransferhandle());
		listgroupcols.setPreferredSize(new Dimension(200, 200));
		sp.setRightComponent(new JScrollPane(listgroupcols));

		cp.add(createOkcancelPane(), BorderLayout.SOUTH);
	}
	
	

	@Override
	protected void onOk() {
		groupcols=new Vector<String>();
		DefaultListModel lm=(DefaultListModel) listgroupcols.getModel();
		for(int i=0;i<lm.size();i++){
			groupcols.add((String)lm.elementAt(i));
		}
		ok=true;
		super.onOk();
	}



	class Listtransferhandle extends TransferHandler {
		int dragpos = -1;
		int droppos = -1;

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return true;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			dragpos = listgroupcols.getSelectedIndex();
			return new StringSelection((String) listgroupcols
					.getSelectedValue());
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			String mimetype = t.getTransferDataFlavors()[0].getMimeType();
			Object trano = null;
			try {
				trano = t.getTransferData(t.getTransferDataFlavors()[0]);
			} catch (UnsupportedFlavorException e1) {

				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}

			if (trano instanceof Vector) {
				try {
					Vector<RecordTrunk> dm = (Vector<RecordTrunk>) trano;
					if (dm.size() > 0) {
						String colname = (String) dm.elementAt(0).elementAt(1);
						DefaultListModel lm = (DefaultListModel) listgroupcols
								.getModel();
						int pos = listgroupcols.getSelectedIndex();
						if(pos<0){
							droppos = lm.getSize();
							lm.addElement(colname);
						}else{
							lm.insertElementAt( colname,pos+1);
							droppos=pos+1;
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
			
			//在list内移动
			String colname = "";
			try {
				colname = (String) t.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			DefaultListModel lm = (DefaultListModel) listgroupcols.getModel();
			int pos = listgroupcols.getSelectedIndex();
			if (pos >= 0) {
				droppos = pos;
				lm.insertElementAt(colname, pos);
			} else {
				droppos = lm.getSize();
				lm.addElement(colname);
			}
			return true;
		}

		@Override
		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if (dragpos >= 0) {
				DefaultListModel lm = (DefaultListModel) listgroupcols
						.getModel();
				if (droppos >= 0) {
					if (dragpos > droppos) {
						lm.remove(dragpos + 1);
					} else {
						lm.remove(dragpos);
					}
				} else {
					lm.remove(dragpos);
				}
				dragpos = -1;
				droppos = -1;
			}
		}

	}



	public Vector<String> getGroupcols() {
		return groupcols;
	}
}
