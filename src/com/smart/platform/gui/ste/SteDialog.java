package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import org.apache.log4j.Category;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.mde.MdeFrame;

/**
 * 单表编辑的对话框窗口
 * @author Administrator
 *
 */
public abstract class SteDialog extends CDialog{
    protected CSteModel stemodel = null;
    Category logger = Category.getInstance(SteDialog.class);

	public SteDialog(java.awt.Frame owner,String title){
		super(owner,title,true);
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	

    protected void initControl() {
        stemodel = getStemodel();

    	Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        if(stemodel!=null){
        	cp.add(stemodel.getRootpanel(), BorderLayout.CENTER);
        }

        setHotkey();
        Dimension scrsize=getToolkit().getScreenSize();
        //setPreferredSize(new Dimension((int)scrsize.getWidth() ,(int)scrsize.getHeight() - 25));
        setLocation(0, 0);
        if(stemodel!=null){
        	stemodel.onstartRun();
        }
    }

    protected abstract CSteModel getStemodel();


    protected void processWindowEvent(WindowEvent e) {
    	//WINDOW_ICONIFIED WINDOW_DEICONIFIED
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (stemodel !=null && 0 != stemodel.on_beforeclose()) {
                return;
            }
            if(stemodel!=null){
            	stemodel.onstopRun();
            }
        }
        super.processWindowEvent(e);
    }
    
    protected void setHotkey() {
    	if(stemodel instanceof CQueryStemodel){
    		SteControlFactory.setQueryHotkey((JComponent) getContentPane(), stemodel);
    	}else{
    		SteControlFactory.setHotkey((JComponent) getContentPane(), stemodel);
    	}
    }
    
    public void setupAp(String roleid){
    	if(!(stemodel instanceof CSteModelAp)){
    		warnMessage("提示","本功能不能设置授权属性");
    		return;
    	}
    	((CSteModelAp)stemodel).setupAp(roleid);
    }
    
    public CSteModel getCreatedStemodel(){
    	return stemodel;
    }
/*
    public void setOpid(String opid){
    	this.opid=opid;
        initControl();
    }

	public String getOpid() {
		return opid;
	}

*/	@Override
	public void pack() {
		//先调用setopid，再调用pack
		if(stemodel==null){
			initControl();
		}
		super.pack();
	}
    
	
}
