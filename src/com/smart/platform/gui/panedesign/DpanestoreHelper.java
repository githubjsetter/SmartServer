package com.smart.platform.gui.panedesign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.log4j.Category;

import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;


/**
 * 保存panel model文件
 * @author user
 *
 */
public class DpanestoreHelper {
	static Category logger=Category.getInstance(DpanestoreHelper.class);
	public static void savePanel(DPanel dpane){
		//确定文件名,取出元件并保存
		//先找类的位置
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
				//取目录
				File dir=new File(path).getParentFile();
				File outf=new File(dir,purename+".model");
				outf.getParentFile().mkdirs();
				savePanel(outf,dpane);
			}
		}
		
		//如果有src目录,在src中也存
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
	 * 从文件中加载.找class类下的文件
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
		
		//读取
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
