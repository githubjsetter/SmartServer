package com.inca.np.gui.ste;

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
import com.sun.jmx.snmp.EnumRowStatus;

/**
 * 单表编辑卡片窗口的UI管理器.可以实现对字段位置大小的任意调整. 对卡片窗口,存一个类名.form文件,保存form的格式.
 * 
 * @author user
 * 
 */
public class SteformUIManager implements LayoutManager {
	CSteModel stemodel;
	boolean hasformUI = false;
	DPanel dpanel = null;
	Category logger = Category.getInstance(SteformUIManager.class);

	public void openSetupDlg(JPanel steformpane) {
		dpanel = new DPanel();
		dpanel.setPreferredSize(steformpane.getPreferredSize());
		dpanel.addPanel(steformpane);
		DPanedesignDlg dlg = new DPanedesignDlg((Frame) null, dpanel);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		saveFormUI(dpanel);

	}

	public SteformUIManager(CSteModel stemodel) {
		super();
		this.stemodel = stemodel;

		// 找是否有相关文件
		String path = stemodel.getClass().getName();
		int p = path.lastIndexOf(".");
		String name = path.substring(p + 1);
		URL url = stemodel.getClass().getResource(name + ".form");
		hasformUI = url != null;
		if (url == null)
			return;

		// 读取
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
		// 将增加的titleborder加到steform中.
		Vector<Compinfo> titlepanes = dpanel.getTitleborderpanes();
		Enumeration<Compinfo> en = titlepanes.elements();
		while (en.hasMoreElements()) {
			Compinfo cinfo = en.nextElement();
			parent.add(cinfo.realcomp);
		}
	}

	public void saveFormUI(DPanel dpanel) {
		this.dpanel = dpanel;

		String classname = stemodel.getClass().getName();
		String purename = "";
		int p = classname.lastIndexOf(".");
		if (p < 0) {
			purename = classname;
		} else {
			purename = classname.substring(p + 1);
		}

		URL url = stemodel.getClass().getResource(purename + ".class");

		if (url != null) {
			String path = url.toString();
			if (path.startsWith("file:")) {
				path = path.substring("file:".length());
			}
			if (path.indexOf("jar:") < 0) {
				// 取目录
				File dir = new File(path).getParentFile();
				File outf = new File(dir, purename + ".form");
				outf.getParentFile().mkdirs();
				DpanestoreHelper.savePanel(outf, dpanel);
			}
		}

		// 如果有src目录,在src中也存
		if (new File("src").exists()) {
			String path = classname.replaceAll("\\.", "/");
			path += ".form";
			File outf = new File(new File("src"), path);
			outf.getParentFile().mkdirs();
			DpanestoreHelper.savePanel(outf, dpanel);
		}

		hasformUI = true;
	}

	/**
	 * 是否使用了新的form界面设置?
	 * 
	 * @return
	 */
	public boolean isUseformUI() {
		return hasformUI;
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void layoutContainer(Container parent) {
		parent.setPreferredSize(dpanel.getPreferredSize());
		parent.setMinimumSize(dpanel.getPreferredSize());
		// 设置所有元件的位置
		int maxx = dpanel.getPreferredSize().width;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component comp = parent.getComponent(i);
			if (comp instanceof Titleborderpane) {
			} else if (comp instanceof JPanel) {
				if (comp.getName() !=null && comp.getName().equals("fieldpane")) {
					// comp是jpanel,内含编辑控件和后面的icon
					Component comp1 = ((JPanel) comp).getComponent(0);
					Component comp2=null;
					if(((JPanel) comp).getComponentCount()>1){
						comp2 = ((JPanel) comp).getComponent(1);
					}
					String name = comp1.getName();
					if (name == null || name.length() == 0)
						continue;
					Compinfo compinfo = dpanel.getCompinfo(name);
					if (compinfo != null) {
						Rectangle rect = compinfo.rect;
						if (compinfo.rect.x > maxx) {
							// 不显示
							comp.setVisible(false);
						}
						comp1.setPreferredSize(new Dimension(rect.width,
								rect.height));
						comp1.setMaximumSize(new Dimension(rect.width,
								rect.height));
						comp1.setMinimumSize(new Dimension(rect.width,
								rect.height));
						if(comp2!=null){
						comp.setBounds(rect.x, rect.y, rect.width
								+ comp2.getPreferredSize().width, rect.height);
						}else{
							comp.setBounds(rect.x, rect.y, rect.width
									, rect.height);
						}

					}else{
						//放在边框外
						comp.setBounds(maxx+10,0,100,25);
						comp.setVisible(false);
					}
					continue;
				} else {
					continue;
				}

			}
			String name = comp.getName();
			if (name == null || name.length() == 0)
				continue;
			Compinfo compinfo = dpanel.getCompinfo(name);
			if (compinfo != null) {
				comp.setBounds(compinfo.rect);
				if (compinfo.rect.x > maxx) {
					// 不显示
					comp.setVisible(false);
				}
			}else{
				//放在边框外
				comp.setBounds(maxx+10,0,100,25);
				comp.setVisible(false);
			}
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(640, 480);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(640, 480);
	}

	public void removeLayoutComponent(Component comp) {
	}

}