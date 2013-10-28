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
 * ϵͳ��Ϣ.��F1��.
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
		sb.append("npserver�汾:"+DefaultNPParam.npversion+"\r\n");
		sb.append("��ַ:"+DefaultNPParam.defaultappsvrurl+"\r\n");
		Userruninfo userinfo=ClientUserManager.getCurrentUser();
		sb.append("����Ա:"+userinfo.getUsername()+"("+userinfo.getUserid()+")\r\n");
		sb.append("����:"+userinfo.getDeptname()+"("+userinfo.getDeptid()+")\r\n");
		sb.append("��ɫ:"+userinfo.getRolename()+"("+userinfo.getRoleid()+")\r\n");
		
		Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		if(w instanceof Frame){
			CMessageDialog.infoMessage((Frame)w, "ϵͳ��Ϣ", sb.toString());
		}else{
			JOptionPane.showMessageDialog(w, sb.toString(),"ϵͳ��Ϣ",JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
