package com.inca.np.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import org.apache.log4j.Category;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.npx.mde.CMdeModelAp;
import com.inca.npx.ste.CSteModelAp;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 13:40:47
 * 单表编辑
 */
public abstract class Steframe extends COpframe {
    protected CSteModel stemodel = null;
    Category logger = Category.getInstance(Steframe.class);

    public Steframe() throws HeadlessException {
    	super("");
    }

    public Steframe(String title) throws HeadlessException {
        super(title);
    }

    public CSteModel createStemodel(){
    	return stemodel = getStemodel();
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
        setPreferredSize(new Dimension((int)scrsize.getWidth() ,(int)scrsize.getHeight() - 25));
        setLocation(0, 0);
        if(stemodel!=null){
        	stemodel.onstartRun();
        }
    }

    @Override
    public void dispose() {
		super.dispose();
    	stemodel=null;
	}

	protected abstract CSteModel getStemodel();

	@Override
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
    	SysinfoAction.installHotkey((JComponent) getContentPane());
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

    public void setOpid(String opid){
    	this.opid=opid;
    	if(stemodel==null){
        initControl();
    	}
    }

	public String getOpid() {
		return opid;
	}

	@Override
	public void pack() {
		//先调用setopid，再调用pack
		initControl();
		super.pack();
		if(stemodel instanceof CSteModelAp){
			((CSteModelAp)stemodel).hideColumnAp();
		}
	}
    
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

}
