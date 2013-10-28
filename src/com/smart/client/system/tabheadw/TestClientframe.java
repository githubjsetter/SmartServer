package com.smart.client.system.tabheadw;

import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.UIManager;


import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import com.l2fprod.util.OS;

public class TestClientframe  extends JFrame {
	public TestClientframe() {
		super("不显示");
		init();

		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	void init() {
		Container cp = getContentPane();
		JLabel lb=new JLabel("测试文本");
		cp.add(lb);
		setUndecorated(true);

		if (getRootPane() instanceof TRootpane) {
			TRootpane rp = (TRootpane) getRootPane();
			rp.init();
		}
	}

	@Override
	protected JRootPane createRootPane() {
		TRootpane rp = new TRootpane();
		rp.setOpaque(true);
		return rp;
	}

	public static void main(String[] args) {
		String clsname = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
		try {
			String xmlpath = "file:skin\\roueBluethemepack\\skinlf-themepack.xml";
			URL url = new URL(xmlpath);
			// SkinLookAndFeel.setSkin(SkinLookAndFeel.loadThemePackDefinition(url));

			String themepackname = "roueBluethemepack";

			Skin skin = SkinLookAndFeel.loadThemePack("skin/" + themepackname
					+ ".zip");
			SkinLookAndFeel.setSkin(skin);

			UIManager.setLookAndFeel(clsname);
		} catch (Exception e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}

		try {
			if (OS.isOneDotFourOrMore()) {
				java.lang.reflect.Method method = JFrame.class.getMethod(
						"setDefaultLookAndFeelDecorated",
						new Class[] { boolean.class });
				method.invoke(null, new Object[] { Boolean.TRUE });

				method = JDialog.class.getMethod(
						"setDefaultLookAndFeelDecorated",
						new Class[] { boolean.class });
				method.invoke(null, new Object[] { Boolean.TRUE });
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		} catch (InvocationTargetException e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}

		TestClientframe frm = new TestClientframe();
		frm.setLocation(40,30);
		frm.pack();
		frm.setVisible(true);
	}
}
