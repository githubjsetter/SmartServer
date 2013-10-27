package com.inca.np.gui.control;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.log4j.Category;

import com.inca.np.gui.panedesign.Compinfo;
import com.inca.np.gui.panedesign.DPanedesignDlg;
import com.inca.np.gui.panedesign.DPanel;
import com.inca.np.gui.panedesign.DpanestoreHelper;
import com.inca.np.gui.panedesign.Titleborderpane;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.DBColumnInfoStoreHelp;

public class CDialogUIManager implements LayoutManager {
	boolean hasdialogUI = false;
	DPanel dpanel = null;
	CDialog dialog = null;
	Category logger = Category.getInstance(CDialogUIManager.class);

	public void openSetupDlg() {
		JPanel dialogpane = (JPanel) dialog.getContentPane();
		Dimension oldsize = dialogpane.getPreferredSize();
		dpanel = new DPanel();
		dpanel.setPreferredSize(oldsize);
		dpanel.addPanel(dialogpane);
		DPanedesignDlg dlg = new DPanedesignDlg((Frame) null, dpanel);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		saveDialogUI(dpanel);

	}

	public CDialogUIManager(CDialog dialog) {
		super();
		this.dialog = dialog;

		// ���Ƿ�������ļ�
		String path = dialog.getClass().getName();
		int p = path.lastIndexOf(".");
		String name = path.substring(p + 1);
		URL url = dialog.getClass().getResource(name + ".form");
		hasdialogUI = url != null;
		if (url == null)
			return;

		// ��ȡ
		dpanel = new DPanel();
		BufferedReader rd = null;
		try {
			rd = DBColumnInfoStoreHelp.getReaderFromFile(new File(url
					.toString()));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<panelsize>")) {
					dpanel.readPanelsize(line);
				}
				if (line.startsWith("<comp>")) {
					dpanel.readCreateFromline(line);
				}
			}
			int m;
			m=3;
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if (rd == null) {
				try {
					rd.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public void addTitlepane(JPanel parent) {
		// �����ӵ�titleborder�ӵ�steform��.
		Vector<Compinfo> titlepanes = dpanel.getTitleborderpanes();
		Enumeration<Compinfo> en = titlepanes.elements();
		while (en.hasMoreElements()) {
			Compinfo cinfo = en.nextElement();
			parent.add(cinfo.realcomp);
		}
	}

	public void saveDialogUI(DPanel dpanel) {
		this.dpanel = dpanel;

		String classname = dialog.getClass().getName();
		String purename = "";
		int p = classname.lastIndexOf(".");
		if (p < 0) {
			purename = classname;
		} else {
			purename = classname.substring(p + 1);
		}

		URL url = dialog.getClass().getResource(purename + ".class");

		if (url != null) {
			String path = url.toString();
			if (path.startsWith("file:")) {
				path = path.substring("file:".length());
			}
			if (path.indexOf("jar:") < 0) {
				// ȡĿ¼
				File dir = new File(path).getParentFile();
				File outf = new File(dir, purename + ".form");
				outf.getParentFile().mkdirs();
				DpanestoreHelper.savePanel(outf, dpanel);
			}
		}

		// �����srcĿ¼,��src��Ҳ��
		if (new File("src").exists()) {
			String path = classname.replaceAll("\\.", "/");
			path += ".form";
			File outf = new File(new File("src"), path);
			outf.getParentFile().mkdirs();
			DpanestoreHelper.savePanel(outf, dpanel);
		}

		hasdialogUI = true;
	}

	/**
	 * �Ƿ�ʹ�����µ�form��������?
	 * 
	 * @return
	 */
	public boolean isUseformUI() {
		return hasdialogUI;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void layoutContainer(Container parent) {
		parent.setPreferredSize(dpanel.getPreferredSize());
		parent.setMinimumSize(dpanel.getPreferredSize());
		// ��������Ԫ����λ��
		int maxx = dpanel.getPreferredSize().width;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component comp = parent.getComponent(i);
			if (comp instanceof Titleborderpane) {
			} else if (comp instanceof JPanel) {
				JPanel tmpjp = (JPanel) comp;
				layoutPanel(parent,tmpjp,maxx);
				continue;
			}
			String name = comp.getName();
			if (name == null || name.length() == 0)
				continue;
			Compinfo compinfo = dpanel.getCompinfo(name);
			if (compinfo != null) {
				comp.setBounds(compinfo.rect);
				if (compinfo.rect.x > maxx) {
					// ����ʾ
					comp.setVisible(false);
				}
			}else{
				//pane����,����form�ļ���û�ж���λ��.
				//������maxx���һ���̶�λ��,visible=false.���������ʱ�����ֿ��Կ���.
				comp.setBounds(maxx+10, 10, 100,30);
				comp.setVisible(false);
			}
		}
	}

	void layoutPanel(Container parent,JPanel jp, int maxx) {
		for (int i = 0; i < jp.getComponentCount(); i++) {
			Component comp = jp.getComponent(i);
			parent.add(comp);
			if (comp instanceof Titleborderpane) {
			} else if (comp instanceof JPanel) {
				layoutPanel((Container)comp,(JPanel) comp, maxx);
			}
			String name = comp.getName();
			if (name == null || name.length() == 0)
				continue;
			Compinfo compinfo = dpanel.getCompinfo(name);
			if (compinfo != null) {
				comp.setBounds(compinfo.rect);
				if (compinfo.rect.x > maxx) {
					// ����ʾ
					comp.setVisible(false);
				}else{
				}
			}
		}
		parent.remove(jp);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(640, 480);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(640, 480);
	}

	public void removeLayoutComponent(Component comp) {
	}

	public DPanel getDPanel(){
		return dpanel;
	}
	
	public Dimension getPreferredSize() {
		return dpanel.getPreferredSize();
	}
}