package com.smart.platform.gui.control;

import com.smart.platform.gui.mde.MdeControlFactory;
import com.smart.platform.gui.ste.CQueryStemodel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.SteControlFactory;
import com.smart.platform.gui.ste.Steform;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.RootPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-11 Time: 10:43:47
 * 单表编辑卡片窗口
 * @deprecated
 */
public class CSteFormWindow extends CDialog implements ActionListener {
	protected ActionListener actionlistener;
	protected Steform steform;
	protected CStetoolbar stetoolbar;
	protected boolean closeabled = true;

	public CSteFormWindow(CFrame owner, Steform steform,
			ActionListener actionlistener, String title)
			throws HeadlessException {
		this(owner, steform, actionlistener, title, true);
	}

	public CSteFormWindow(CFrame owner, Steform steform,
			ActionListener actionlistener, String title, boolean closeabled)
			throws HeadlessException {
		super(owner, title + "　Ctrl+W关闭", false);
		this.actionlistener = actionlistener;
		this.steform = steform;
		this.closeabled = closeabled;

		JRootPane rootpane = this.getRootPane();
		rootpane.setWindowDecorationStyle(JRootPane.NONE);
		this.setUndecorated(true);

		Container cp = this.getContentPane();
		((JComponent) cp).setBorder(BorderFactory.createLoweredBevelBorder());

		cp.setLayout(new BorderLayout());
		cp.add(steform, BorderLayout.CENTER);

		CStetoolbar stetoolbar = createToolbar();
		if (stetoolbar != null) {
			// 取消工具条 20071025
			// cp.add(stetoolbar, BorderLayout.NORTH);
		}

		Dimension formsize = steform.getPreferredSize();
		formsize.setSize(formsize.getWidth() + 20, formsize.getHeight() + 60);
		this.setPreferredSize(formsize);

		localScreenCenter();

		//setHotkey(cp);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

	}

	public Steform getSteform() {
		return steform;
	}

	public void setStetoolbar(CStetoolbar stetoolbar) {
		this.stetoolbar = stetoolbar;
	}

	protected CStetoolbar createToolbar() {
		return this.stetoolbar;
	}

	protected void setHotkey(Container cp) {
		JComponent jcp = (JComponent) cp;
		if (closeabled) {
			KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
					InputEvent.CTRL_MASK, false);
			jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					vkctrlw, CSteModel.ACTION_HIDEFORM);
			jcp.getActionMap().put(CSteModel.ACTION_HIDEFORM,
					new SteformAction(CSteModel.ACTION_HIDEFORM));

			// esc 隐藏编辑窗
			KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0,
					false);
			jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
					vkesc, "cancel");
			jcp.getActionMap().put("cancel", new SteformAction("cancel"));
		}

		if (steform.getStemodel() instanceof CQueryStemodel) {
			SteControlFactory.setQueryHotkey(
					(JComponent) this.getContentPane(), this);
		} else {
			SteControlFactory.setHotkey((JComponent) this.getContentPane(),
					this);
		}

	}

	protected class SteformAction extends AbstractAction {
		public SteformAction(String name) {
			super(name);
			this.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(CSteModel.ACTION_HIDEFORM)) {
				steform.commitEdit();
				setVisible(false);
			} else if (e.getActionCommand().equals("cancel")) {
				if (!steform.cancelEdit())
					return;
				setVisible(false);
			}

		}
	}

	protected void localScreenCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension size = this.getPreferredSize();
		// 最大取2/3
		size.setSize(size.getWidth() * 1.1, screensize.getHeight() * .618);
		this.setPreferredSize(size);

		double x = (screensize.getWidth() - size.getWidth()) / 2.0;
		double y = (screensize.getHeight() - size.getHeight()) / 2.0;

		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		this.setLocation((int) x, (int) y);
	}

	public void actionPerformed(ActionEvent e) {
		actionlistener.actionPerformed(e);
	}

	/**
	 * 
	 * @param focusfirst
	 *            激活并定位到第一个可编辑component
	 */
	public void onActive(boolean focusfirst) {
		final boolean finalfocusfirst = focusfirst;
		Runnable r = new Runnable() {
			public void run() {
				if (finalfocusfirst) {
					Component firstcomp = getFocusTraversalPolicy()
							.getFirstComponent(CSteFormWindow.this);
					if (firstcomp != null) {
						firstcomp.requestFocus();
					}
				}

			}
		};
		SwingUtilities.invokeLater(r);

	}

	public void freeMemory() {
		JComponent jcp = (JComponent) this.getContentPane();
		jcp.getActionMap().clear();
		actionlistener = null;
		steform = null;
		stetoolbar = null;
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (!steform.cancelEdit())
				return;
		}
		super.processWindowEvent(e);
	}

}
