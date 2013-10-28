package com.smart.platform.gui.ste;

import com.smart.client.system.Clientframe;
import com.smart.extension.ap.Aphelper;
import com.smart.extension.ste.Apinfo;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.RunopManager;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CMessageDialog;
import com.smart.platform.gui.control.COtherquerycontrol;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-12
 * Time: 11:52:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class CModelBase implements ActionListener,COtherquerycontrol{
    protected CFrame frame = null;
    protected String title = null;

    /**
     * 是否启用ap授权属性
     */
    protected boolean useap=false;

    protected int commitcount = 0;
    protected synchronized boolean lockData() {
        if (commitcount > 0) {
            return false;
        }
        commitcount++;
        return true;
    }

    protected synchronized void unloakData() {
        commitcount--;
    }

    public abstract String getSaveCommandString();

    protected void infoMessage(String title, String msg) {
		CMessageDialog.infoMessage(getParentFrame(), title, msg);
/*
        JOptionPane.showMessageDialog(getParentFrame(), msg,
                title, JOptionPane.INFORMATION_MESSAGE);
*/        //CMessageBox.infoMessage(this,title,msg);

    }

    protected void errorMessage(String title, String msg) {
		CMessageDialog.errorMessage(getParentFrame(), title, msg);

    }

    protected void warnMessage(String title, String msg) {
		CMessageDialog.warnMessage(getParentFrame(), title, msg);
    }

    public CFrame getParentFrame() {
        return frame;
    }

    public void  setParentFrame(CFrame frame) {
        this.frame=frame;
    }

    public void onstartRun(){
        RunopManager.startRun(this);
    }

    public void onstopRun(){
        RunopManager.stopRun(this);
    }

    /**
     * 当前运行的功能发生了变化
     */
    protected String[] runopnames=null;
    public void onRunopschanged(String[] opnames){
        this.runopnames=opnames;
    }

    public String getTitle() {
        return title;
    }

    public JPopupMenu createSelectopMenu() {
        ActionListener actionListener = new SelectopActionListener();
        JPopupMenu popmenu = new JPopupMenu("功能");
        JMenuItem item;
        item = new JMenuItem("运行新功能");
        item.setActionCommand("run");
        item.addActionListener(actionListener);
        popmenu.add(item);


        popmenu.addSeparator();

        for(int i=0;runopnames!=null&&i<runopnames.length;i++){
            String opname=runopnames[i];
            item = new JMenuItem(opname);
            item.setActionCommand(opname);
            item.addActionListener(actionListener);
            popmenu.add(item);
        }

        return popmenu;
    }

    class SelectopActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if(cmd.equals("run")){
            	Clientframe.getClientframe().requestFocus();
            }else{
                //RunopManager.activeOp(cmd);
            }
        }
    }

    /**
     * 如果查询条件需要更多的方式输入条件,可将这些条件控件放在一个JPanel中返回.
     * 这个Jpanel会放在查询条件输入窗口的上部.
     * @return 返回查询放查询条件控件的JPanel默认返回null
     */
    public   JPanel getOtherquerypanel(){
        return null;
    }

    /**
     * 功能被激活
     */
	public void active() {
		if(frame!=null){
			frame.requestFocus();
		}
	}

	/**
	 * HOV的授权属性cache key 是hov的classname ,value是where条件
	 */
	protected HashMap<String, String> hovapwherescache = new HashMap<String, String>();
	
	/**
	 * 取hov的授权查询约束
	 * 
	 * @param hovclassname
	 *            HOV类名
	 * @return
	 */
	public String getHovOtherWheresAp(String hovclassname) {
		if(hovclassname.endsWith("CHovbaseGeneral"))return "";
		String wheres = hovapwherescache.get(hovclassname);
		if (wheres != null)
			return wheres;

		// 查询授权属性
		String roleid = ClientUserManager.getCurrentUser().getRoleid();
		if (roleid.length() == 0 && DefaultNPParam.debug == 1) {
			roleid = "2";
		}
		HashMap<String, Apinfo> apmap = null;
		try {
			apmap = Aphelper.downloadHovAp(hovclassname, roleid,
					new StringBuffer());
		} catch (Exception e) {
			//errorMessage("错误", "查询HOV授权属性失败" + e.getMessage());
			hovapwherescache.put(hovclassname, "");
			return "";
		}
		Apinfo info = apmap.get(Apinfo.APNAME_WHERES);
		if (info != null) {
			wheres = info.getApvalue();
		}
		if (wheres == null)
			wheres = "";
		wheres = Aphelper.filterApwheres(wheres);
		hovapwherescache.put(hovclassname, wheres);
		return wheres;
	}

	public boolean isUseap() {
		return useap;
	}

	public void setUseap(boolean useap) {
		this.useap = useap;
	}
	
	protected String opid="";
	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}
	
	
	
}
