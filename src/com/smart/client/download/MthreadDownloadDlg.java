package com.smart.client.download;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.util.StringUtil;
import com.smart.server.clientinstall.Blockinfo;
import com.smart.server.clientinstall.DownloadNotifyIF;

/**
 * 下载信息
 * 
 * @author Administrator
 * 
 */
public class MthreadDownloadDlg extends CDialog implements DownloadNotifyIF {
	private JLabel lbmsg;
	private JProgressBar progbar;
	private Antpanel antpane;
	Category logger = Category.getInstance(MthreadDownloadDlg.class);

	public MthreadDownloadDlg(Frame owner, String title) {
		super(owner, title, true);
		init();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	void init() {
		Container cp = getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		lbmsg = new JLabel("准备下载");
		int y = 0;
		Insets insets = new Insets(2, 2, 2, 2);
		cp
				.add(lbmsg, new GridBagConstraints(0, y, 1, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));

		y++;
		progbar = new JProgressBar(0, 100);
		cp
				.add(progbar, new GridBagConstraints(0, y, 1, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						insets, 0, 0));
		progbar.setPreferredSize(new Dimension(400, 15));
		progbar.setStringPainted(true);

		antpane = new Antpanel();
		y++;
		y++;
		cp.add(new JScrollPane(antpane), new GridBagConstraints(0, y, 1, 1,
				1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				insets, 0, 0));

		JPanel bottompanel = credateBottompanel();
		y++;
		cp.add(bottompanel, new GridBagConstraints(0, y, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0,
				0));

	}

	JPanel credateBottompanel() {
		JPanel jp = new JPanel();
		JButton btn;
		btn = new JButton("取消下载");
		jp.add(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		return jp;
	}

	public void log(String logmsg) {
		logger.info(logmsg);
	}

	public void notify(int totalsize, int downloadedsize, long usetimems) {
		final String msg = "已下载" + StringUtil.bytes2string(downloadedsize)
				+ "/" + StringUtil.bytes2string(totalsize) + "，速度:"
				+ StringUtil.bytespeed2string(downloadedsize, usetimems);
		final float percent = (float) downloadedsize / (float) totalsize * 100f;

		if (EventQueue.isDispatchThread()) {
			lbmsg.setText(msg);
			progbar.setValue((int) percent);
		} else {
			Runnable r = new Runnable() {
				public void run() {
					lbmsg.setText(msg);
					progbar.setValue((int) percent);
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}

	public void notify(ArrayList<Blockinfo> blocks) {
		if (EventQueue.isDispatchThread()) {
			antpane.setBlocks(blocks);
			antpane.invalidate();
			antpane.repaint();
			antpane.validate();
		} else {
			final ArrayList<Blockinfo> tmpblocks = blocks;
			Runnable r = new Runnable() {
				public void run() {
					MthreadDownloadDlg.this.notify(tmpblocks);
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}

	public static void main(String[] args) {
		MthreadDownloadDlg dlg = new MthreadDownloadDlg(null, "download info");
		dlg.pack();
		dlg.setVisible(true);
	}

}
