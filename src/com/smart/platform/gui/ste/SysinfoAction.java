package com.smart.platform.gui.ste;

import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.CMessageBox;
import com.smart.platform.gui.control.CMessageDialog;
import com.smart.platform.util.DefaultNPParam;

/**
 * 系统信息.按F1键.
 * @author user
 *
 */
public class SysinfoAction extends AbstractAction{
	public static void installHotkey(JComponent jcp){
		InputMap im=jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0,false),"sysinfo");
		jcp.getActionMap().put("sysinfo", new SysinfoAction("sysinfo"));
	}
	
	
	public SysinfoAction(String name) {
		super(name);
		putValue(AbstractAction.ACTION_COMMAND_KEY, name);
	}

	public void actionPerformed(ActionEvent e) {
		StringBuffer sb=new StringBuffer();
		sb.append("npserver版本:"+DefaultNPParam.npversion+"\r\n");
		sb.append("地址:"+DefaultNPParam.defaultappsvrurl+"\r\n");
		Userruninfo userinfo=ClientUserManager.getCurrentUser();
		sb.append("操作员:"+userinfo.getUsername()+"("+userinfo.getUserid()+")\r\n");
		sb.append("部门:"+userinfo.getDeptname()+"("+userinfo.getDeptid()+")\r\n");
		sb.append("角色:"+userinfo.getRolename()+"("+userinfo.getRoleid()+")\r\n");
		
		Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		if(w instanceof Frame){
			CMessageDialog.infoMessage((Frame)w, "系统信息", sb.toString());
		}else{
			JOptionPane.showMessageDialog(w, sb.toString(),"系统信息",JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
