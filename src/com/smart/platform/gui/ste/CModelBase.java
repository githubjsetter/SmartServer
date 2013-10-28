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
     * �Ƿ�����ap��Ȩ����
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
     * ��ǰ���еĹ��ܷ����˱仯
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
        JPopupMenu popmenu = new JPopupMenu("����");
        JMenuItem item;
        item = new JMenuItem("�����¹���");
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
     * �����ѯ������Ҫ����ķ�ʽ��������,�ɽ���Щ�����ؼ�����һ��JPanel�з���.
     * ���Jpanel����ڲ�ѯ�������봰�ڵ��ϲ�.
     * @return ���ز�ѯ�Ų�ѯ�����ؼ���JPanelĬ�Ϸ���null
     */
    public   JPanel getOtherquerypanel(){
        return null;
    }

    /**
     * ���ܱ�����
     */
	public void active() {
		if(frame!=null){
			frame.requestFocus();
		}
	}

	/**
	 * HOV����Ȩ����cache key ��hov��classname ,value��where����
	 */
	protected HashMap<String, String> hovapwherescache = new HashMap<String, String>();
	
	/**
	 * ȡhov����Ȩ��ѯԼ��
	 * 
	 * @param hovclassname
	 *            HOV����
	 * @return
	 */
	public String getHovOtherWheresAp(String hovclassname) {
		if(hovclassname.endsWith("CHovbaseGeneral"))return "";
		String wheres = hovapwherescache.get(hovclassname);
		if (wheres != null)
			return wheres;

		// ��ѯ��Ȩ����
		String roleid = ClientUserManager.getCurrentUser().getRoleid();
		if (roleid.length() == 0 && DefaultNPParam.debug == 1) {
			roleid = "2";
		}
		HashMap<String, Apinfo> apmap = null;
		try {
			apmap = Aphelper.downloadHovAp(hovclassname, roleid,
					new StringBuffer());
		} catch (Exception e) {
			//errorMessage("����", "��ѯHOV��Ȩ����ʧ��" + e.getMessage());
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
