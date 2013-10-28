package com.smart.platform.gui.control;

import javax.swing.*;

import com.smart.platform.gui.panedesign.Compinfo;
import com.smart.platform.gui.panedesign.DPanel;
import com.smart.platform.gui.panedesign.Titleborderpane;

import java.awt.event.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 10:11:01
 * To change this template use File | Settings | File Templates.
 */
public class CDialog extends JDialog implements Action, MouseListener {
	protected CDialogUIManager uimanager = null;

	/**
	 * CDialog
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public CDialog() throws HeadlessException {
		this((Frame) null, false);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @throws HeadlessException
	 */
	public CDialog(Frame owner) throws HeadlessException {
		this(owner, false);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public CDialog(Frame owner, boolean modal) throws HeadlessException {
		this(owner, null, modal);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public CDialog(Frame owner, String title) throws HeadlessException {
		this(owner, title, false);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public CDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public CDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @throws HeadlessException
	 */
	public CDialog(Dialog owner) throws HeadlessException {
		this(owner, false);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public CDialog(Dialog owner, boolean modal) throws HeadlessException {
		this(owner, null, modal);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public CDialog(Dialog owner, String title) throws HeadlessException {
		this(owner, title, false);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public CDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * CDialog
	 * 
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public CDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		uimanager = new CDialogUIManager(this);
	}

	/**
	 * Initialize. Install ALT-Pause
	 */
	@Override
	protected void dialogInit() {
		super.dialogInit();
		// CompiereColor.setBackground(this);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(getTitle()); // remove Mn

		//
		/*
		 * Container c = getContentPane(); if (c instanceof JPanel) { JPanel
		 * panel = (JPanel)c; panel.getActionMap().put(ACTION_DISPOSE,
		 * s_dialogAction);
		 * panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
		 * s_disposeKeyStroke, ACTION_DISPOSE); }
		 */
	} // init

	/***************************************************************************
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("UI设计")){
			doUIDesign();
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 * @param e
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 * @param e
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Set Title
	 * 
	 * @param title
	 *            title
	 */
	public void setTitle(String title) {
		if (title != null) {
			int pos = title.indexOf("&");
			if (pos != -1 && title.length() > pos) // We have a nemonic
			{
				int mnemonic = title.toUpperCase().charAt(pos + 1);
				if (mnemonic != ' ')
					title = title.substring(0, pos) + title.substring(pos + 1);
			}
		}
		super.setTitle(title);
	} // setTitle

	/** Dispose Action Name */
	protected static String ACTION_DISPOSE = "CDialogDispose";
	/** Action */
	// protected static DialogAction s_dialogAction = new
	// DialogAction(ACTION_DISPOSE);
	/** ALT-EXCAPE */
	protected static KeyStroke s_disposeKeyStroke = KeyStroke.getKeyStroke(
			KeyEvent.VK_PAUSE, InputEvent.ALT_MASK);

	public Object getValue(String key) {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	public void putValue(String key, Object value) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	/**
	 * Compiere Dialog Action
	 * 
	 */
	class DialogAction extends AbstractAction {
		DialogAction(String actionName) {
			super(actionName);
			putValue(AbstractAction.ACTION_COMMAND_KEY, actionName);
		} // DialogAction

		/**
		 * Action Listener
		 * 
		 * @param e
		 *            event
		 */
		public void actionPerformed(ActionEvent e) {
			if (ACTION_DISPOSE.equals(e.getActionCommand())) {
				Object source = e.getSource();
				while (source != null) {
					if (source instanceof Window) {
						((Window) source).dispose();
						return;
					}
					if (source instanceof Container)
						source = ((Container) source).getParent();
					else
						source = null;
				}
			} else if (ACTION_ENTERKEYCONFIRM.equals(e.getActionCommand())) {
				enterkeyConfirm();
			} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
				doClose();
			} else
				System.out.println("Action: " + e);
		} // actionPerformed

	} // DialogAction

	protected void localScreenCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension size = this.getPreferredSize();
		// 最大取2/3
		size.setSize(size.getWidth() * 1.1, screensize.getHeight() * .618);
		this.setPreferredSize(size);

		double x = (screensize.getWidth() - size.getWidth()) / 2.0;
		double y = (screensize.getHeight() - size.getHeight()) / 2.0;

		this.setLocation((int) x, (int) y);
	}

	public void localCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension size = this.getPreferredSize();

		double x = (screensize.getWidth() - size.getWidth()) / 2.0;
		double y = (screensize.getHeight() - size.getHeight()) / 2.0;

		this.setLocation((int) x, (int) y);
	}

	/**
	 * 增加回车键导航
	 * 
	 * @param comp
	 */
	protected void addEnterkeyTraver(Component comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.add(enterkey);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);
	}

	protected void addEnterkeyConfirm(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterkey,
				ACTION_ENTERKEYCONFIRM);
		comp.getActionMap().put("enterkeyconfirm",
				new DialogAction(ACTION_ENTERKEYCONFIRM));
	}

	protected void addcloseHotkey(JComponent comp) {
		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				vkesc, ACTION_CANCEL);
		comp.getActionMap().put(ACTION_CANCEL, new DialogAction(ACTION_CANCEL));
	}

	public void pack() {
		if(uimanager!=null && uimanager.isUseformUI()){
			uimanager.addTitlepane((JPanel)getContentPane());
		}
		super.pack();
		postPack();
	}

	/**
	 * 调用pack对话窗就续后。
	 */
	protected void postPack() {

	}

	/**
	 * 按回车确定
	 */
	protected static String ACTION_ENTERKEYCONFIRM = "enterkeyconfirm";
	protected static String ACTION_CANCEL = "cancel";
	protected static String ACTION_DESIGN = "design";

	protected void enterkeyConfirm() {
	}

	protected void doClose() {

	}

	protected void infoMessage(String title, String msg) {
		// JOptionPane.showMessageDialog(this, msg, title,
		// JOptionPane.INFORMATION_MESSAGE);
		CMessageDialog.infoMessage(this, title, msg);
		// CMessageBox.infoMessage(this,title,msg);

	}

	protected void errorMessage(String title, String msg) {
		CMessageDialog.errorMessage(this, title, msg);
		// OptionPane.showMessageDialog(this, msg, title,
		// JOptionPane.ERROR_MESSAGE);

	}

	protected void warnMessage(String title, String msg) {
		CMessageDialog.warnMessage(this, title, msg);
		// JOptionPane.showMessageDialog(this, msg, title,
		// JOptionPane.WARNING_MESSAGE);

	}

	public void setWaitcursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setDefaultcursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void setVisible(boolean b) {
		if (EventQueue.isDispatchThread()) {
			super.setVisible(b);
		} else {
			final boolean tmp_b = b;
			Runnable r = new Runnable() {
				public void run() {
					CDialog.super.setVisible(tmp_b);
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

	@Override
	public void dispose() {
		if (EventQueue.isDispatchThread()) {
			super.dispose();
		} else {
			Runnable r = new Runnable() {
				public void run() {
					CDialog.super.dispose();
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

	public boolean isUseformUI() {
		return uimanager.isUseformUI();
	}


	/**
	 * 可设计的container
	 */
	DesignableContainer designcontainer = null;

	@Override
	public Container getContentPane() {
		if (uimanager != null && uimanager.isUseformUI()) {
			if (designcontainer == null) {
				designcontainer = new DesignableContainer();
				designcontainer.setUIManager(uimanager);
				super.getContentPane().add(designcontainer);
			}
			return designcontainer;
		} else {
			return super.getContentPane();
		}
	}


	/**
	 * 生成UI设计按钮
	 * 
	 * @return
	 */
	protected JButton createUIDesignbutton() {
		JButton btn = new JButton("UI设计");
		addEnterkeyTraver(btn);
		btn.addActionListener(this);
		btn.setActionCommand("UI设计");
		btn.setName("btnDesign");
		return btn;
	}

	/**
	 * 进行界面设计
	 */
	protected void doUIDesign() {
		uimanager.openSetupDlg();
	}

} // CDialog
