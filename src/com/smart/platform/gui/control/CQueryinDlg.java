package com.smart.platform.gui.control;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * 弹出dialog多选下拉选择的值
 * @author Administrator
 *
 */
public class CQueryinDlg extends CDialog{
	DBTableModel comboboxdbmodel;
	String currentvalue=null;
	/**
	 * comboboxdbmodel 的 每0项可能为空
	 */
	int offset=0;
	String resultvalue="";

	public boolean ok=false;
	
	Vector<CCheckBox> cbtable=null;
	DBColumnDisplayInfo colinfo=null;
	
	public String getResult(){
		return resultvalue;
	}
	public CQueryinDlg(Dialog owner, String title,DBColumnDisplayInfo colinfo,DBTableModel comboboxdbmodel,String currentvalue) throws HeadlessException {
		super(owner, title,true);
		this.comboboxdbmodel=comboboxdbmodel;
		this.currentvalue=currentvalue;
		this.colinfo=colinfo;
		initDialog();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	protected void initDialog(){
		//将所有的选项值列出,以供选择
		Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel datapane=createDatapane();
		JPanel bottompane=createBottompane();
		
		cp.add(new JScrollPane(datapane),BorderLayout.CENTER);
		cp.add(bottompane,BorderLayout.SOUTH);
		
		//设置vk_esc vk_enter热键
		KeyStroke vkesc=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,false);
		KeyStroke vkenter=KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0,false);
		InputMap im=((JComponent)cp).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(vkesc,"cancel");
		im.put(vkenter,"ok");
		
		((JComponent)cp).getActionMap().put("cancel", new DlgAction("cancel"));
		((JComponent)cp).getActionMap().put("ok", new DlgAction("ok"));
	}

	class DlgAction extends AbstractAction{
		DlgAction(String name){
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			if(cmd.equals("ok")){
				onOk();
			}else if(cmd.equals("cancel")){
				onCancel();
			}
		}
	}
	
	protected void onOk(){
		ok=true;
		StringBuffer sb=new StringBuffer();
		//合成结果串
		for(int i=0;i<cbtable.size();i++){
			CCheckBox cb=cbtable.elementAt(i);
			if(cb.isSelected()){
				String key=comboboxdbmodel.getItemValue(i+offset,"key");
				if(sb.length()>0){
					sb.append(",");
				}
				if(colinfo.getColtype().equals("varchar")){
					sb.append("'");
				}
				sb.append(key);
				if(colinfo.getColtype().equals("varchar")){
					sb.append("'");
				}
			}
		}
		resultvalue=sb.toString();
		dispose();
	}
	protected void onCancel(){
		ok=false;
		dispose();
	}
	
	JPanel createBottompane() {
		JPanel jp=new JPanel();
		CButton btn=new CButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jp.add(btn);
		btn=new CButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		return jp;
	}

	JPanel createDatapane() {
		JPanel jp=new JPanel();
		BoxLayout layout=new BoxLayout(jp,BoxLayout.Y_AXIS);
		jp.setLayout(layout);
		
		currentvalue=currentvalue.replaceAll("'","");
		String[] ss=currentvalue.split(",");
		HashMap<String,String> map=new HashMap<String,String>();
		for(int i=0;i<ss.length;i++){
			map.put(ss[i],ss[i]);
		}
		
		offset=0;
		cbtable=new Vector<CCheckBox>();
		for(int r=0;r<comboboxdbmodel.getRowCount();r++){
			String k=comboboxdbmodel.getItemValue(r,"key");
			String v=comboboxdbmodel.getItemValue(r,"value");
			if(k==null || k.length()==0){
				offset++;
				continue;
			}
				
			CCheckBox checkbox=new CCheckBox(v);
			jp.add(checkbox);
			cbtable.add(checkbox);
			if(map.get(k)!=null){
				checkbox.setSelected(true);
			}
		}
		return jp;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.equals("ok")){
			onOk();
		}else if(cmd.equals("cancel")){
			onCancel();
		}
	}
	
	
	
}
