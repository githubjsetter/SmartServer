package com.smart.platform.util;

import java.io.File;

import javax.print.DocFlavor.URL;

import org.apache.log4j.Category;

import com.smart.platform.gui.ste.CSteModel;

/**
 * ר��̹�����
 * ר�����������������ͬһ��Ŀ¼��. 
 * 
 * @author Administrator
 *
 */
public class SpecialProjectManager {
	static Category logger=Category.getInstance(SpecialProjectManager.class);
	public static CSteModel.InitDelegate loadInitDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_InitDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.InitDelegate initdelegate = (CSteModel.InitDelegate)cls.newInstance();
			return initdelegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}

	public static CSteModel.TableDelegate loadTableDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_TableDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.TableDelegate delegate = (CSteModel.TableDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}

	public static CSteModel.FormDelegate loadFormDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_FormDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.FormDelegate delegate = (CSteModel.FormDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}

	public static CSteModel.QueryDelegate loadQueryDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_QueryDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.QueryDelegate delegate = (CSteModel.QueryDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}

	public static CSteModel.EditDelegate loadEditDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_EditDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.EditDelegate delegate = (CSteModel.EditDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}

	public static CSteModel.ActionDelegate loadActionDelegate(CSteModel stemodel){
		String classname=stemodel.getClass().getName();
		classname+="_ActionDelegate";
		//������
		try {
			Class cls=Class.forName(classname);
			CSteModel.ActionDelegate delegate = (CSteModel.ActionDelegate)cls.newInstance();
			return delegate;
		} catch (ClassNotFoundException e) {
			//û��ר�� 
			//logger.debug("û��ר����"+classname);
			return null;
		} catch (InstantiationException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error("��ʼ��ʧ��:"+classname,e);
			return null;
		}
	}
	
	/**
	 * �����·��,�ѵ�classesĿ¼,�ڸ�Ŀ¼�²���Ӧ���и�zxclassesĿ¼
	 * @param o
	 * @return zxclassesĿ¼
	 */
	public static File getZxclassdir(Object o){
		String classname = o.getClass().getName();
		int p=classname.lastIndexOf(".");
		String packagename="";
		if(p>=0){
			packagename=classname.substring(0,p);
			classname=classname.substring(p+1);
		}
		
		//ȡ·��
		String url = o.getClass().getResource(classname+".class").toString();
		if(url.startsWith("jar:")){
			url=url.substring("jar:".length());
		}
		if(url.startsWith("file:")){
			url=url.substring("file:".length());
		}
		
		//һ��һ��������,�ҵ�classΪֹ
		
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
	 * �����package nameתΪdir path
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
	 * ȡר���ļ�. ȡzxclassesĿ¼��,��ǰĿ¼��"����.postfix"
	 * @param postfix  �ļ���׺
	 * @return
	 */
	public static File getZxfile(Object o, String postfix){
		File zxdir=SpecialProjectManager.getZxclassdir(o);
		
		//����ļ�Ϊ
		File outdir=new File(zxdir,SpecialProjectManager.packname2dirpath(o));
		if(!outdir.exists()){
			outdir.mkdirs();
		}
		
		String outfname=SpecialProjectManager.getClassname(o)+postfix;
		File outf=new File(outdir,outfname);
		
		return outf;
	}
}
