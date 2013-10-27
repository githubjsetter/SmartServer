package com.inca.np.treetextview;

import java.io.File;

/**
 * 一个.txt文件
 * @author Administrator
 *
 */
public class Textnode implements java.util.Comparator {
	String title="";
	String classpath="";
	File file=null;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int compare(Object o1, Object o2) {
		Textnode n1=(Textnode)o1;
		Textnode n2=(Textnode)o2;
		
		return n1.getClasspath().compareTo(n2.getClasspath());
	}
	
	public String toString(){
		return title;
	}
	
}
