package com.inca.np.gui.control;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.inca.np.util.DefaultNPParam;

/**
 * 有热键等的确定 取消
 * 
 * @author Administrator
 * 
 */
public class CDialogOkcancel extends CDialog {

	protected boolean ok = false;

	public CDialogOkcancel() throws HeadlessException {
		super();
		initDefault();
	}

	public CDialogOkcancel(Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		initDefault();
	}

	public CDialogOkcancel(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		initDefault();
	}

	public CDialogOkcancel(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initDefault();
	}

	public CDialogOkcancel(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		initDefault();
	}

	public CDialogOkcancel(Dialog owner) throws HeadlessException {
		super(owner);
		initDefault();
	}

	public CDialogOkcancel(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initDefault();
	}

	public CDialogOkcancel(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initDefault();
	}

	public CDialogOkcancel(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initDefault();
	}

	public CDialogOkcancel(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		initDefault();
	}

	public CDialogOkcancel(Frame owner) throws HeadlessException {
		super(owner);
		initDefault();
	}

	protected void initDefault() {
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		JComponent jcp = (JComponent) getContentPane();
		addcloseHotkey(jcp);
	}

	protected void onOk() {
		ok = true;
		this.dispose();
	}

	@Override
	protected void doClose() {
		onCancel();
	}

	protected void onCancel() {
		ok = false;
		this.dispose();
	}

	@Override
	protected void enterkeyConfirm() {
		onOk();
	}

	protected JPanel createOkcancelPane() {
		JPanel jp = new JPanel();
		JButton btn = new JButton("确定");
		addEnterkeyConfirm(btn);
		btn.addActionListener(this);
		btn.setActionCommand("ok");
		btn.setName("btnOk");
		jp.add(btn);

		btn = new JButton("取消");
		addEnterkeyTraver(btn);
		btn.addActionListener(this);
		btn.setActionCommand("cancel");
		btn.setName("btnCancel");
		jp.add(btn);
 
		if (DefaultNPParam.debug == 1) {
			btn = createUIDesignbutton();
			jp.add(btn);
		}

		return jp;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("ok".equals(e.getActionCommand())
				|| "\n".equals(e.getActionCommand())) {
			onOk();
		} else if ("cancel".equals(e.getActionCommand())) {
			onCancel();
		}else{
			super.actionPerformed(e);
		}
	}

	public boolean isOk() {
		return ok;
	}
}
