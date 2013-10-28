package com.smart.platform.demo.mste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.CTableLinenoRender;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.SteActionListener;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Testhotkey extends Steframe{
	public Testhotkey(){
		super("test");
	}
	
	
	
	
	@Override
	protected void initControl() {
        stemodel = getStemodel();

    	Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        cp.add(jsp,BorderLayout.CENTER);
        
        JSplitPane jsp1=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jsp.setRightComponent(jsp1);
        
        Pubgoodsdetail_ste goodsdtlstem=new Pubgoodsdetail_ste(this);
        goodsdtlstem.setShowformonly(true);
        jsp1.setLeftComponent(goodsdtlstem.getRootpanel());
        
        
        JTabbedPane tp=new JTabbedPane();
        jsp1.setRightComponent(tp);

        if(stemodel!=null){
        	//cp.add(stemodel.getRootpanel(), BorderLayout.CENTER);
        	tp.add("1",stemodel.getRootpanel());
        }

        createLefttable();
        jsp.setLeftComponent(new JScrollPane(lefttable));

        Pubgoodsdetail_ste goodsdtlste=new Pubgoodsdetail_ste(this);
        tp.add("2",goodsdtlste.getRootpanel());

		setHotkey1((JComponent) getContentPane());
        //setHotkey1(jsp);

		removeF8(jsp);
		removeF8(jsp1);
        
        dumpKey(jsp1);
	}
	
	void removeF8(JComponent cp){
		cp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F8,0,false), "nouse");
	}
	
	void dumpKey(JComponent cp){
		InputMap im=cp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap am=cp.getActionMap().getParent();
		KeyStroke keys[]=im.allKeys();
		for(int i=0;i<keys.length;i++){
			System.out.println(keys[i]+"==>"+im.get(keys[i]));
		}
	}

	@Override
	protected void setHotkey() {
	}

	void setHotkey1(JComponent compcp){
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkf8, CSteModel.ACTION_QUERY);
		compcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new TestActionListener(CSteModel.ACTION_QUERY));
		
		KeyStroke vk_ctln=KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK,false);
		compcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vk_ctln, CSteModel.ACTION_NEW);
		compcp.getActionMap().put(CSteModel.ACTION_NEW,
				new TestActionListener(CSteModel.ACTION_NEW));
		
		
		
	}
	
	class TestActionListener extends AbstractAction{

		public TestActionListener(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Testhotkey.this, "reveice "+e.getActionCommand());
		}
		
		
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

		Testhotkey frm=new Testhotkey();
		frm.pack();
		frm.setVisible(true);
	}

	@Override
	protected CSteModel getStemodel() {
		// TODO Auto-generated method stub
		return new Pub_goods_ste(this);
	}
	CTable lefttable;
	protected void createLefttable() {
		TableColumnModel tcm = stemodel.getTable().getColumnModel();
		DefaultTableColumnModel newtcm = new DefaultTableColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			newtcm.addColumn(tcm.getColumn(i));
		}

		DBTableModel dbmodel = stemodel.getSumdbmodel();
		lefttable = new CTable(dbmodel, newtcm);
		TableColumn column = lefttable.getColumn("ÐÐºÅ");
		column
				.setCellRenderer(new CTableLinenoRender(stemodel
						.getSumdbmodel()));

	}

}
