package com.smart.platform.util;

import java.io.File;

import javax.print.DocFlavor.URL;

import org.apache.log4j.Category;

import com.smart.platform.gui.ste.CSteModel;

/**
 * 专项工程管理者
 * 专项程序布署在与相关类的同一个目录中. 
 * 
 * @author Administrator
 *
 */
public class SpecialProjectManager {
	static Category logger=Category.getInstance(SpecialProjectManager.class);
	public static CSteModel.InitDelegate loadInitDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_InitDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.InitDelegate initdelegate = (CSteModel.InitDelegate)cls.newInstance();
			return initdelegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}

	public static CSteModel.TableDelegate loadTableDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_TableDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.TableDelegate delegate = (CSteModel.TableDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}

	public static CSteModel.FormDelegate loadFormDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_FormDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.FormDelegate delegate = (CSteModel.FormDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}

	public static CSteModel.QueryDelegate loadQueryDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_QueryDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.QueryDelegate delegate = (CSteModel.QueryDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}

	public static CSteModel.EditDelegate loadEditDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_EditDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.EditDelegate delegate = (CSteModel.EditDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}

	public static CSteModel.ActionDelegate loadActionDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_ActionDelegate";
		//加载类
		try {
			Class cls=Class.forName(classname);
			CSteModel.ActionDelegate delegate = (CSteModel.ActionDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//没有专项 
			//logger.debug("没有专项类"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("初始化失败:"+classname,e);
			return null;
		}
	}
	
	/**
	 * 找类的路径,搜到classes目录,在该目录下并列应该有个zxclasses目录
	 * @param o
	 * @return zxclasses目录
	 */
	public static File getZxclassdir(Object o){
		String classname = o.getClass().getName();
		int p=classname.lastIndexOf(".");
		String packagename="";
		if(p>=0){
			packagename=classname.substring(0,p);
			classname=classname.substring(p+1);
		}
		
		//取路径
		String url = o.getClass().getResource(classname+".class").toString();
		if(url.startsWith("jar:")){
			url=url.substring("jar:".length());
		}
		if(url.startsWith("file:")){
			url=url.substring("file:".length());
		}
		
		//一级一级往上找,找到class为止
		
		for(;;){
			p=url.lastIndexOf("/");
			if(p<0)break;
			
			String dirname=url.substring(p+1);
			url=url.substring(0,p);
			if(dirname.equals("classes")){
				return new File(url+"/zxclasses");
			}
		}
		
		return new File("zxclasses");
		
	}

	/**
	 * 将类的package name转为dir path
	 * @param o
	 * @return
	 */
	public static String packname2dirpath(Object o){
		String classname = o.getClass().getName();
		int p=classname.lastIndexOf(".");
		String packagename="";
		if(p>=0){
			packagename=classname.substring(0,p);
			classname=classname.substring(p+1);
		}
		String path=packagename.replaceAll("\\.","/");
		return path;
	}

	public static String getClassname(Object o){
		String classname = o.getClass().getName();
		int p=classname.lastIndexOf(".");
		String packagename="";
		if(p>=0){
			packagename=classname.substring(0,p);
			classname=classname.substring(p+1);
		}
		return classname;
	}
	
	/**
	 * 取专项文件. 取zxclasses目录下,当前目录下"类名.postfix"
	 * @param postfix  文件后缀
	 * @return
	 */
	public static File getZxfile(Object o, String postfix){
		File zxdir=SpecialProjectManager.getZxclassdir(o);
		
		//输出文件为
		File outdir=new File(zxdir,SpecialProjectManager.packname2dirpath(o));
		if(!outdir.exists()){
			outdir.mkdirs();
		}
		
		String outfname=SpecialProjectManager.getClassname(o)+postfix;
		File outf=new File(outdir,outfname);
		
		return outf;
	}
}
