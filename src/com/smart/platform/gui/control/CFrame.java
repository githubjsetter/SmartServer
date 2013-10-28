package com.smart.platform.gui.control;

import javax.swing.*;

import com.smart.client.system.Clientframe;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 10:18:54
 * To change this template use File | Settings | File Templates.
 */
public class CFrame extends JFrame implements ActionListener {
	/**
	 * CFrame
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public CFrame() throws HeadlessException {
		super();
	} // CFrame

	/**
	 * CFrame
	 * 
	 * @param gc
	 */
	public CFrame(GraphicsConfiguration gc) {
		super(gc);
	} // CFrame

	/**
	 * CFrame
	 * 
	 * @param title
	 * @throws HeadlessException
	 */
	public CFrame(String title) throws HeadlessException {
		super(cleanup(title));
	} // CFrame

	/**
	 * CFrame
	 * 
	 * @param title
	 * @param gc
	 */
	public CFrame(String title, GraphicsConfiguration gc) {
		super(cleanup(title), gc);
	} // CFrame


	/**
	 * Frame Init. Install ALT-Pause
	 */
	protected void frameInit() {
		super.frameInit();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setHotkey((JComponent)getContentPane());
	} // frameInit

	/**
	 * Cleanedup Title
	 * 
	 * @param title
	 *            title
	 * @return title w/o mn
	 */
	private static String cleanup(String title) {
		if (title != null) {
			int pos = title.indexOf("&");
			if (pos != -1 && title.length() > pos) // We have a nemonic
			{
				int mnemonic = title.toUpperCase().charAt(pos + 1);
				if (mnemonic != ' ')
					title = title.substring(0, pos) + title.substring(pos + 1);
			}
		}
		return title;
	} // getTitle

	/**
	 * Set Title
	 * 
	 * @param title
	 *            title
	 */
	public void setTitle(String title) {
		super.setTitle(cleanup(title));
	} // setTitle


	public void actionPerformed(ActionEvent e) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	protected void localScreenCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension size = this.getPreferredSize();

		double x = (screensize.getWidth() - size.getWidth()) / 2.0;
		double y = (screensize.getHeight() - size.getHeight()) / 2.0;

		this.setLocation((int) x, (int) y);
	}

	public void infoMessage(String title, String msg) {
		// JOptionPane.showMessageDialog(this, msg,
		// title, JOptionPane.INFORMATION_MESSAGE);
		// CMessageBox.infoMessage(this,title,msg);
		CMessageDialog.infoMessage(this, title, msg);

	}

	public void errorMessage(String title, String msg) {
		// JOptionPane.showMessageDialog(this, msg,
		// title, JOptionPane.ERROR_MESSAGE);
		CMessageDialog.errorMessage(this, title, msg);

	}

	public void warnMessage(String title, String msg) {
		// JOptionPane.showMessageDialog(this, msg,
		// title, JOptionPane.WARNING_MESSAGE);

		CMessageDialog.warnMessage(this, title, msg);
	}

	public void setWaitcursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setDefaultcursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void addHotkey(KeyStroke key, String actionname, Action action) {
		JComponent jcp = (JComponent) getContentPane();
		InputMap im = jcp
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(key, actionname);
		jcp.getActionMap().put(actionname, action);
	}

	public void localCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension size = this.getPreferredSize();

		double x = (screensize.getWidth() - size.getWidth()) / 2.0;
		double y = (screensize.getHeight() - size.getHeight()) / 2.0;

		this.setLocation((int) x, (int) y);
	}

	public static void dumpKeyaction(JComponent jcp) {
		KeyStroke keys[] = jcp.getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).allKeys();
		for (int i = 0; keys != null && i < keys.length; i++) {
			Object imtarget = jcp.getInputMap(
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(keys[i]);
			Action aa = jcp.getActionMap().get(imtarget);
			System.out.println(keys[i] + "==>" + imtarget + "==>" + aa);
		}

	}

	protected void setHotkey(JComponent jcp) {
		InputMap im = jcp
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke keyctrly = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				Event.CTRL_MASK, false);
		im.put(keyctrly, "quickop");

		jcp.getActionMap().put("quickop", new HotkeyAction("quickop"));
	}

	protected class HotkeyAction extends AbstractAction {

		public HotkeyAction(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("quickop")) {
				Clientframe w = Clientframe.getClientframe();
				if (w != null) {
					w.quickOp();
				}
			}
		}

	}

	@Override
	public void setVisible(boolean b){
		if(EventQueue.isDispatchThread()){
			super.setVisible(b);
		}else{
			final boolean tmp_b=b;
			Runnable r=new Runnable(){
				public void run(){
					setVisible(tmp_b);
				}
			};
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
} // CFrame

