package com.inca.npx.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellEditor;

import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;

/**
 * 设置参数类型的授权属性.
 * 用一个可编辑的表格来编辑数据
 * @author Administrator
 *
 */
public class ParamapSetupPanel {
	private CEditableTable table;
	private DBTableModel dbmodel;
	JPanel rootpanel;
	Vector<Apinfo> apinfos;

	public void setup(JPanel rootpanel,Vector<Apinfo> apinfos){
		this.rootpanel=rootpanel;
		this.apinfos=apinfos;
		initPanel();
		bindValue();
	}

	protected void bindValue(){
		dbmodel.clearAll();
		Enumeration<Apinfo> en=apinfos.elements();
		while(en.hasMoreElements()){
			Apinfo apinfo=en.nextElement();
			dbmodel.appendRow();
			int row=dbmodel.getRowCount()-1;
			dbmodel.setItemValue(row, "apname",apinfo.getApname());
			dbmodel.setItemValue(row, "apvalue",apinfo.getApvalue());
		}
	}
	
	protected void initPanel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("apname","varchar","授权属性名");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);
		
		col=new DBColumnDisplayInfo("apvalue","varchar","授权属性值");
		cols.add(col);
		col.setTablecolumnwidth(300);
		
		dbmodel = new DBTableModel(cols);
		Sumdbmodel summodel=new Sumdbmodel(dbmodel,new Vector());
		table = new CEditableTable(summodel);
		
/*		table.getColumnModel().getColumn(0).setWidth(100);
		table.getColumnModel().getColumn(1).setWidth(300);
*/	
		rootpanel.setLayout(new BorderLayout());
		JScrollPane sp=new JScrollPane(table);
		sp.setPreferredSize(new Dimension(400,400));
		rootpanel.add(sp,BorderLayout.CENTER);
		
	}
	
	public Vector<Apinfo> getApinfos(){
		TableCellEditor tce=table.getCellEditor();
		if(tce!=null){
			tce.stopCellEditing();
		}
		Vector<Apinfo> infos=new Vector<Apinfo>();
		for(int r=0;r<dbmodel.getRowCount();r++){
			Apinfo info=new Apinfo(dbmodel.getItemValue(r, "apname"),Apinfo.APTYPE_PARAM);
			info.setApvalue(dbmodel.getItemValue(r, "apvalue"));
			infos.add(info);
		}
		return infos;
	}
	
	public static void main(String[] argv){
		CDialog dlg=new CDialog((Frame)null,"test",true);
		Container cp=dlg.getContentPane();
		cp.setLayout(new BorderLayout());
		
		JPanel jp=new JPanel();
		cp.add(jp,BorderLayout.CENTER);
		
		Vector<Apinfo> apinfos=new Vector<Apinfo>();
		Apinfo info=new Apinfo("apname1",Apinfo.APTYPE_PARAM);
		apinfos.add(info);
		info=new Apinfo("apname2",Apinfo.APTYPE_PARAM);
		apinfos.add(info);
		info=new Apinfo("apname3",Apinfo.APTYPE_PARAM);
		apinfos.add(info);
		
		
		
		ParamapSetupPanel app=new ParamapSetupPanel();
		app.setup(jp,apinfos);
		
		dlg.pack();
		dlg.setVisible(true);
		
	}
	
}
