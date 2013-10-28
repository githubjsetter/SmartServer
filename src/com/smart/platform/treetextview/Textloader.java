package com.smart.platform.treetextview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 从指定目录读取.
 * 
 * @author Administrator
 * 
 */
public class Textloader {
	public DefaultMutableTreeNode loadDir(File dir, String roottile)
			throws Exception {
		Textnode roottextnode = new Textnode();
		roottextnode.setTitle(roottile);
		DefaultMutableTreeNode roottreenode = new DefaultMutableTreeNode(
				roottextnode);

		// 先读所有文件的第一次,取出分类.
		// 进行排序,然后生成森林

		Vector<Textnode> textnodes = loadTextnode(dir);
		// 排序
		Collections.sort(textnodes, new Textnode());
		buildTree(roottreenode, textnodes);

		return roottreenode;
	}

	void buildTree(DefaultMutableTreeNode roottreenode,
			Vector<Textnode> textnodes) {
		Enumeration<Textnode> en = textnodes.elements();
		while (en.hasMoreElements()) {
			Textnode node = en.nextElement();
			putNode(roottreenode, node);
		}
	}

	void putNode(DefaultMutableTreeNode roottreenode, Textnode node) {
		String classpath = node.getClasspath();
		String ss[] = classpath.split(",");
		DefaultMutableTreeNode lastp = roottreenode;
		for (int i = 0; ss != null && i < ss.length; i++) {
			String s = ss[i];
			DefaultMutableTreeNode p = searchTree(roottreenode, s);
			if (p == null) {
				p = new DefaultMutableTreeNode(node);
				lastp.add(p);
			}
			lastp = p;
		}
	}

	/**
	 * 广度优先
	 * 
	 * @param root
	 * @param s
	 * @return
	 */
	DefaultMutableTreeNode searchTree(DefaultMutableTreeNode root, String s) {
		String cp=((Textnode) root.getUserObject()).getClasspath();
		
		
		
		if (cp.endsWith(s)) {
			int p=cp.lastIndexOf(s);
			if(p!=0){
				char c=cp.charAt(p-1);
				if(c==','){
					return root;
				}
			}else{
				return root;
			}
		}
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root
					.getChildAt(i);
			DefaultMutableTreeNode r = searchTree(child, s);
			if (r != null) {
				return r;
			}
		}
		return null;

	}

	Vector<Textnode> loadTextnode(File dir) throws Exception {
		Vector<Textnode> nodes = new Vector<Textnode>();
		File[] fs = dir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if (!f.getName().endsWith(".txt"))
				continue;

			// 读2行
			Textnode node = loadFile(f);
			nodes.add(node);
		}
		return nodes;
	}

	Textnode loadFile(File f) throws Exception {
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new FileReader(f));
			String classpath = rd.readLine();
			String title = rd.readLine();
			Textnode node = new Textnode();
			if (classpath != null)
				node.setClasspath(classpath);
			if (title != null)
				node.setTitle(title);
			node.setFile(f);
			return node;
		} finally {
			if (rd != null)
				rd.close();
		}
	}

	public static void main(String[] argv) {
		Textloader tl = new Textloader();
		File dir = new File("guide");
		try {
			tl.loadDir(dir, "ngpcs guide");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
