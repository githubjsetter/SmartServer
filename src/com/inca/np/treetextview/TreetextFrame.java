package com.inca.np.treetextview;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.runop.Opgroup;

/**
 * 读取目录中的.txt文件,显示 txt文件的第0行是分级,用逗号隔开 第1行是标题
 * 
 * 左边是树,右边是文本
 * 
 * @author Administrator
 * 
 */
public class TreetextFrame extends CFrame {

	private JTextPane textpane;

	public TreetextFrame(String title) throws HeadlessException {
		super(title);
		initFrame();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension scrsize=this.getToolkit().getScreenSize();
		this.setPreferredSize(new Dimension((int)scrsize.getWidth(),(int)scrsize.getHeight()-50));
	}

	void initFrame() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JSplitPane split = new JSplitPane();
		cp.add(split, BorderLayout.CENTER);

		split.setLeftComponent(createTreePanel());

		textpane = new JTextPane();
		split.setRightComponent(new JScrollPane(textpane));

	}

	JScrollPane createTreePanel() {
		Textloader tl = new Textloader();
		String clsname=getClass().getName();
		int p=clsname.lastIndexOf(".");
		clsname=clsname.substring(p+1);
		String url=getClass().getResource(clsname+".class").toString();
		url=url.substring("file:/".length());
		p=url.lastIndexOf("/");
		url=url.substring(0,p);
		
		File dir = new File(url+"/guide");
		DefaultMutableTreeNode rootnode = null;
		try {
			rootnode = tl.loadDir(dir, "ngpcs guide");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JTree tree = new JTree(rootnode);
		tree.addTreeSelectionListener(new TreeselectionHandle());
		// tree.addFocusListener(new FocusHandle());

		JScrollPane jp = new JScrollPane(tree);
		return jp;
	}

	void showText(File f) {
		BufferedReader rd = null;
		StringBuffer sb = new StringBuffer();
		try {
			rd = new BufferedReader(new FileReader(f));
			rd.readLine();
			String line;
			while ((line = rd.readLine()) != null)
				sb.append(line+"\r\n");
			textpane.setText(sb.toString());
			textpane.setSelectionStart(0);
			textpane.setSelectionEnd(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	class TreeselectionHandle implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			Object lastcomp = path.getLastPathComponent();
			if (lastcomp instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) lastcomp;
				Textnode textnode = (Textnode) treenode.getUserObject();
				File f = textnode.getFile();
				if (f != null) {
					showText(f);
				}
			}
		}
	}

	public static void main(String[] argv) {
		TreetextFrame frm = new TreetextFrame("ngpcs guide");
		frm.pack();
		frm.setVisible(true);
	}

}
