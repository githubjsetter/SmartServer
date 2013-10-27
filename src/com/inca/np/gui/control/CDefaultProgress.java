package com.inca.np.gui.control;

import javax.swing.*;

import org.apache.log4j.Category;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-16 Time: 16:22:24
 * To change this template use File | Settings | File Templates.
 */
public class CDefaultProgress implements CProgressIF {
	Category logger = Category.getInstance(CDefaultProgress.class);
	static SimpleDateFormat datefmt = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	private JTextArea textarea;

	public CDefaultProgress(Frame owner) {
		dlgwin = new ProgressWindow(owner);
		dlgwin.setResizable(false);
	}

	public CDefaultProgress(Dialog owner) {
		dlgwin = new ProgressWindow(owner);
	}

	ProgressWindow dlgwin = null;

	public void show() {
		dlgwin.pack();
		dlgwin.setVisible(true);
	}

	public void appendMessage(String msg) {
		logger.info(msg);
		final String tmp_msg = msg;
		Runnable r = new Runnable() {
			public void run() {
				int l = textarea.getText().length();
				textarea.replaceRange(datefmt.format(new Date()) + "   "
						+ tmp_msg + "\r\n", l, l);
				textarea.getCaret().setDot(l);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public void startMessage(String msg) {
		logger.info(msg);
		final String tmp_msg = msg;
		Runnable r = new Runnable() {
			public void run() {
				int l = textarea.getText().length();
				textarea.replaceRange(datefmt.format(new Date()) + "   "
						+ tmp_msg, l, l);
				textarea.getCaret().setDot(l);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public void endMessage(String msg) {
		logger.info(msg);
		final String tmp_msg = msg;
		Runnable r = new Runnable() {
			public void run() {
				int l = textarea.getText().length();
				textarea.replaceRange(" " + tmp_msg + "\r\n", l, l);
				textarea.getCaret().setDot(l);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	/**
	 * 显示状态的窗口
	 */
	class ProgressWindow extends CDialog implements ActionListener {
		private JButton btnclose;

		public ProgressWindow(Dialog owner) throws HeadlessException {
			super(owner, "进度状态", true);
			initControl();

		}

		public ProgressWindow(Frame owner) throws HeadlessException {
			super(owner, "进度状态", true);
			initControl();

		}

		private void initControl() {
			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			cp.add(createMessagePanel(), BorderLayout.CENTER);

			btnclose = new JButton("关闭");
			btnclose.setEnabled(false);
			btnclose.addActionListener(this);
			btnclose.setActionCommand("close");
			JPanel bottompanel = new JPanel();
			bottompanel.add(btnclose);
			cp.add(bottompanel, BorderLayout.SOUTH);

			localScreenCenter();
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

			InputMap inputMap = btnclose
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
					"close");
			btnclose.getActionMap().put("close", new DlgAction("close"));
		}

		JPanel createMessagePanel() {
			textarea = new JTextArea(25, 80);
			textarea.setWrapStyleWord(true);
			textarea.setLineWrap(true);
			JScrollPane scrollp = new JScrollPane(textarea);
			JPanel pane = new JPanel();
			pane.add(scrollp);
			return pane;
		}

		public void actionPerformed(ActionEvent e) {
			super.actionPerformed(e);
			if (e.getActionCommand().equals("close")) {
				dispose();
			}
		}

		protected void localScreenCenter() {
			Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

			Dimension size = this.getPreferredSize();

			double x = (screensize.getWidth() - size.getWidth()) / 2.0;
			double y = (screensize.getHeight() - size.getHeight()) / 2.0;

			this.setLocation((int) x, (int) y);
		}
	}

	public void close() {
		dlgwin.setVisible(false);
		dlgwin.dispose();
	}

	class DlgAction extends AbstractAction {
		public DlgAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("close")) {
				CDefaultProgress.this.dlgwin.dispose();
			}
		}
	}

	public void messageBox(String title, String msg) {
		appendMessage(title + ":" + msg);

		dlgwin.btnclose.setEnabled(true);
		dlgwin.btnclose.requestFocusInWindow();

		AutocloseThread t = new AutocloseThread();
		t.start();
	}

	public void testMulti(String title, String msg) {
		int l = textarea.getText().length();
		if (l > 32000) {
			textarea.setText("");
			l = textarea.getText().length();
		}
		textarea.replaceRange(
				datefmt.format(new Date()) + "   " + msg + "\r\n", l, l);
		textarea.getCaret().setDot(l);

		dlgwin.btnclose.setEnabled(true);
		dlgwin.btnclose.requestFocusInWindow();

		AutocloseThread t = new AutocloseThread();
		t.start();

	}

	class AutocloseThread extends Thread {
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
			boolean haserror = textarea.getText().indexOf("ERROR") >= 0
					|| textarea.getText().indexOf("错误") >= 0
					|| textarea.getText().indexOf("失败") >= 0
					|| textarea.getText().indexOf("部分成功") >= 0;
			if (dlgwin != null && dlgwin.isVisible() && !haserror) {
				logger.debug("set progress dlg visible=false");
				dlgwin.setVisible(false);
				logger.debug("set progress dlg dispose");
				dlgwin.dispose();
				logger.debug("progress dlg dispose finished");
			}
		}
	}

	/*
	 * public void warnBox(String title, String msg) {
	 * appendMessage(title+":"+msg); JOptionPane.showMessageDialog(dlgwin, msg,
	 * title, JOptionPane.WARNING_MESSAGE); }
	 * 
	 * public void errorBox(String title, String msg) {
	 * appendMessage(title+":"+msg); JOptionPane.showMessageDialog(dlgwin, msg,
	 * title, JOptionPane.ERROR_MESSAGE); }
	 */

	public static void main(String[] argv) {
		CDefaultProgress dlg = new CDefaultProgress((Frame) null);
		dlg.appendMessage("ok");
		dlg.show();

	}
}
