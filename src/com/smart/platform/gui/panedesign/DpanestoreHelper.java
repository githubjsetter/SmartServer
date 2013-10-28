package com.smart.platform.gui.panedesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.log4j.Category;

import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;


/**
 * ����panel model�ļ�
 * @author user
 *
 */
public class DpanestoreHelper {
	static Category logger=Category.getInstance(DpanestoreHelper.class);
	public static void savePanel(DPanel dpane){
		//ȷ���ļ���,ȡ��Ԫ��������
		//�������λ��
		String classname=dpane.getClass().getName();
		String purename="";
		int p=classname.lastIndexOf(".");
		if(p<0){
			purename=classname;
		}else{
			purename=classname.substring(p+1);
		}
		
		URL url=dpane.getClass().getResource(purename+".class");
		
		if(url!=null){
			String path=url.toString();
			if(path.startsWith("file:")){
				path=path.substring("file:".length());
			}
			if(path.indexOf("jar:")<0){
				//ȡĿ¼
				File dir=new File(path).getParentFile();
				File outf=new File(dir,purename+".model");
				outf.getParentFile().mkdirs();
				savePanel(outf,dpane);
			}
		}
		
		//�����srcĿ¼,��src��Ҳ��
		if(new File("src").exists()){
			String path=classname.replaceAll("\\.", "/");
			path+=".model";
			File outf=new File(new File("src"),path);
			outf.getParentFile().mkdirs();
			savePanel(outf,dpane);
		}
		
	}

	public static void savePanel(File outf, DPanel dpane) {
		PrintWriter print=null;
		try {
			print=new PrintWriter(new FileWriter(outf));
			dpane.write(print);
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if(print!=null){
				print.close();
			}
		}
	}
	
	/**
	 * ���ļ��м���.��class���µ��ļ�
	 * @param dpane
	 * @throws Exception
	 */
	public static void load(DPanel dpane)throws Exception{
		String classname=dpane.getClass().getName();
		String purename="";
		int p=classname.lastIndexOf(".");
		if(p<0){
			purename=classname;
		}else{
			purename=classname.substring(p+1);
		}
		
		URL url=dpane.getClass().getResource(purename+".model");
		
		if(url==null)return;
		
		//��ȡ
		BufferedReader rd=null;
		try {
			rd=DBColumnInfoStoreHelp.getReaderFromFile(new File(url.toString()));
			String line;
			while((line=rd.readLine())!=null){
				if(line.startsWith("<panelsize>")){
					dpane.readPanelsize(line);
				}
				
				if(line.startsWith("<comp>")){
					dpane.readCreateFromline(line);
				}
			}
		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if(rd==null){
				rd.close();
			}
		}
	}
}
