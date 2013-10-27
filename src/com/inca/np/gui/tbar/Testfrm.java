package com.inca.np.gui.tbar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import com.l2fprod.util.OS;
import com.sun.media.sound.Toolkit;

public class Testfrm extends JFrame{

	public Testfrm() throws HeadlessException {
		super("test toolbar ");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		
		TBar tb=new TBar();
		setPreferredSize(new Dimension(600,400));
		cp.add(tb,BorderLayout.NORTH);
		
		TButton btn;
		btn=new TButton("ÐÂÔö");
		tb.add(btn);

		btn=new TButton("±à¼­");
		tb.add(btn);

		btn=new TButton("²éÑ¯");
		tb.add(btn);

		btn=new TButton("É¾³ý");
		tb.add(btn);

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
		
		
		Testfrm f=new Testfrm();
		f.pack();
		f.setVisible(true);
	}
}
