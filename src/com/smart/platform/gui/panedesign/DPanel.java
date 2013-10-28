package com.smart.platform.gui.panedesign;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CTextArea;

/**
 * 可以被设计的panel. 由专门的layoutmanager,利用model文件,管理 控件坐标. 并可设定panel大小.
 * 
 * @author user
 * 
 */
public class DPanel extends JPanel {
	Category logger = Category.getInstance(DPanel.class);
	/**
	 * 元件名称和定义
	 */
	Vector<Compinfo> compinfos = new Vector<Compinfo>();
	HashMap<String, Compinfo> compmap = new HashMap<String, Compinfo>();
	/**
	 * 大小
	 */
	Dimension panesize = new Dimension(320, 240);

	public DPanel() {
		setLayout(new DirectLayout());
	}

	@Override
	public Component add(Component comp) {
		if (comp.getName() == null) {
			logger.error("没有用setName( )设置名称," + comp + ". ");
			comp.setName(" ");
		}

		if (compmap.get(comp.getName()) != null) {
			logger.error("元件名称必须唯一," + comp.getName() + "有重名");
		}

		Compinfo compinfo = getCompinfo(comp.getName());
		if (compinfo == null) {
			compinfo = new Compinfo(comp.getName(), comp, comp.getBounds());
			compinfos.add(compinfo);
		}
		compinfo.realcomp = comp;
		compmap.put(comp.getName(), compinfo);
		comp.setVisible(true);
		return comp;
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		add(comp);
	}

	@Override
	public void add(Component comp, Object constraints) {
		add(comp);
	}

	@Override
	public Component add(String name, Component comp) {
		add(comp);
		return comp;
	}

	/**
	 * 从文件中加载名称和位置. 文件名为同目录下的 类名.model文件.
	 */
	public void loadModel() {
		try {
			DpanestoreHelper.load(this);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return panesize;
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		panesize = preferredSize;
	}

	@Override
	public Dimension getMaximumSize() {
		return panesize;
	}

	@Override
	public Dimension getMinimumSize() {
		return panesize;
	}

	public Compinfo getCompinfo(String name) {
		Enumeration<Compinfo> en = compinfos.elements();
		while (en.hasMoreElements()) {
			Compinfo compinfo = en.nextElement();
			if (compinfo.compname != null && compinfo.compname.equals(name)) {
				return compinfo;
			}
		}
		return null;
	}

	class DirectLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
			// System.out.println("add");
		}

		public void layoutContainer(Container parent) {
			for (int i = 0; i < getComponentCount(); i++) {
				Component comp = getComponent(i);
				String name = comp.getName();
				if (name == null) {
					continue;
				}
				Compinfo compinfo = getCompinfo(name);
				if (compinfo == null) {
					continue;
				}
				comp.setBounds(compinfo.rect);
			}
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(320, 240);
		}

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(320, 240);
		}

		public void removeLayoutComponent(Component comp) {
			// TODO Auto-generated method stub

		}

	}

	public void write(PrintWriter print) throws Exception {
		print.println("<panelsize>" + getPreferredSize().width + ":"
				+ getPreferredSize().height + "</panelsize>");
		Enumeration<Compinfo> en = compinfos.elements();
		while (en.hasMoreElements()) {
			Compinfo compinfo = en.nextElement();
			compinfo.write(print);
		}
	}

	public void readCreateFromline(String line) {
		if (line.startsWith("<comp>")) {
			int p = "<comp>".length();
			int p1 = line.indexOf("</", p);
			String s = line.substring(p, p1);
			String ss[] = s.split(":");
			String compname = ss[0];
			Rectangle rect = new Rectangle();
			try {
				rect.x = Integer.parseInt(ss[1]);
			} catch (Exception e) {
			}
			try {
				rect.y = Integer.parseInt(ss[2]);
			} catch (Exception e) {
			}
			try {
				rect.width = Integer.parseInt(ss[3]);
			} catch (Exception e) {
			}
			try {
				rect.height = Integer.parseInt(ss[4]);
			} catch (Exception e) {
			}
			Compinfo compinfo = new Compinfo(compname, null, rect);

			if (compname.startsWith("titleborder")) {
				if (ss.length >= 6) {
					String title = ss[5];
					compinfo.realcomp = new Titleborderpane(title);
					compinfo.realcomp.setName(compname);
				}
			}
			compinfos.add(compinfo);

		}
	}

	Enumeration<Compinfo> getCompinfos() {
		return compinfos.elements();
	}

	public void addPanel(JPanel panel) {
		addPanel(0, 0, panel);
	}

	void addPanel(int startx, int starty, JPanel pane) {
		for (int i = 0; i < pane.getComponentCount(); i++) {
			Component comp = pane.getComponent(i);
			if (comp instanceof JLabel && comp.getName() != null
					&& comp.getName().equals("lbicon")) {
				continue;
			}
			if (comp instanceof JTextArea || comp instanceof CTextArea
					|| comp instanceof JComboBox || comp instanceof JComboBox
					|| comp instanceof JLabel || comp instanceof JTextField
					|| comp instanceof JButton
					|| comp instanceof Titleborderpane
					|| comp instanceof JRadioButton
					|| comp instanceof JCheckBox || comp instanceof JScrollPane) {
				Rectangle rect = comp.getBounds();
				rect.x += startx;
				rect.y += starty;
				comp.setBounds(rect);
				DPanel.this.add(comp);
			} else if (comp instanceof JPanel) {
				Rectangle rect = comp.getBounds();
				addPanel(startx + rect.x, starty + rect.y, (JPanel) comp);
			} else {
				logger.error("cann't add " + comp.getClass().getName());
			}
		}
	}

	public Vector<Compinfo> getTitleborderpanes() {
		Vector<Compinfo> tcompinfos = new Vector<Compinfo>();
		Enumeration<Compinfo> en = compinfos.elements();
		while (en.hasMoreElements()) {
			Compinfo cinfo = en.nextElement();
			if (cinfo.compname.startsWith("titleborder")) {
				tcompinfos.add(cinfo);
			}
		}
		return tcompinfos;
	}

	public void readPanelsize(String line) {
		int p = line.indexOf("<panelsize>");
		if (p < 0)
			return;
		p += "<panelsize>".length();
		int p1 = line.indexOf("</", p);
		String s = line.substring(p, p1);
		String ss[] = s.split(":");

		try {
			panesize.width = Integer.parseInt(ss[0]);
			panesize.height = Integer.parseInt(ss[1]);
		} catch (Exception e) {

		}
	}
}
