package com.inca.np.filedb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.npserver.server.sysproc.CurrentappHelper;

public class DirHelper {
	/**
	 * 退出系统时要删除的文件
	 */
	private static Vector<File> delonexistfiles=new Vector<File>();
	static Category logger=Category.getInstance(DirHelper.class);

	/**
	 * 给出 purename-version.postfix 形式的文件名,返回version
	 * 
	 * @param fn
	 * @return
	 */
	public static String getVersion(String fn) {
		int p = fn.lastIndexOf(".");
		fn = fn.substring(0, p);
		p = fn.indexOf("-");
		if (p < 0)
			return "";
		return fn.substring(p + 1);
	}

	public static String getPurename(String fn) {
		int p = fn.lastIndexOf(".");
		fn = fn.substring(0, p);
		p = fn.indexOf("-");
		if (p < 0)
			return fn;
		return fn.substring(0,p);
	}
	
	/**
	 * 清理目录dir中,以prefix开头,版本不是最新的
	 * 
	 * @param dir
	 * @param fn
	 * @deprecated
	 */
	public static void clearOlderfile(File dir, String prefix) {
		File targetfile = null;
		File fs[] = dir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if (!f.getName().startsWith(prefix))
				continue;
			if (targetfile == null) {
				targetfile = f;
				continue;
			}
			String v1 = getVersion(targetfile.getName());
			String v2 = getVersion(f.getName());

			File deletefile = null;
			if (v1.compareTo(v2) < 0) {
				deletefile = targetfile;
				targetfile = f;
			} else {
				deletefile = f;
			}

			clearFile(deletefile);
		}
	}
	
	

	/**
	 * 删除文件,删不掉就清为0字节
	 * 
	 * @param deletefile
	 */
	public static void clearFile(File deletefile) {
		logger.info("clear file "+deletefile.getAbsolutePath());
		boolean b = deletefile.delete();
		if (b)
			return;

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(deletefile);
			fout.write(new byte[0]);
		} catch (Exception e) {
			logger.error("error",e);
		} finally {
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
				}
		}
	}
	
	public static void clearZerosizefile(File dir){
		File fs[] = dir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			if(f.length()==0L){
				clearFile(f);
			}
		}
	}
	
	/**
	 * 清除其它版本.不管新旧,只要当前的
	 */
	public static void clearOtherVersion(File f){
		File dir=f.getParentFile();
		String purename=getPurename(f.getName());
		File[] fs=dir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			File tmpf=fs[i];
			if(tmpf.isDirectory())continue;
			if(!getPurename(tmpf.getName()).startsWith(purename))continue;
			
			if(!tmpf.getName().equals(f.getName())){
				clearFile(tmpf);
			}
		}
	}


	/**
	 * 清除其它版本.不管新旧,只要当前的。不马上清除，放在delonexistfiles中，由退出线程来删除
	 */
	public static void clearOtherVersionOnexit(File f){
		Category logger=Category.getInstance("com");
		logger.debug("clearOtherVersionOnexit final file="+f.getName()+",clear sample pure but not equal version");
		File dir=f.getParentFile();
		String purename=getPurename(f.getName());
		File[] fs=dir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			File tmpf=fs[i];
			if(tmpf.isDirectory())continue;
			if(!getPurename(tmpf.getName()).startsWith(purename))continue;
			
			if(!tmpf.getName().equals(f.getName())){
				logger.debug("add file to be delete "+tmpf.getAbsolutePath());
				addDelfileOnexit(tmpf);
			}
		}
	}
	public static void clearOtherVersionOnexit(File dir,String filename){
		Category logger=Category.getInstance("com");
		logger.debug("clearOtherVersionOnexit final file="+filename+",clear sample pure but not equal version");
		String purename=getPurename(filename);
		File[] fs=dir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			File tmpf=fs[i];
			if(tmpf.isDirectory())continue;
			if(!getPurename(tmpf.getName()).startsWith(purename))continue;
			
			if(!tmpf.getName().equals(filename)){
				logger.debug("add file to be delete "+tmpf.getAbsolutePath());
				addDelfileOnexit(tmpf);
			}
		}
	}
	
	public static void addDelfileOnexit(File f){
		delonexistfiles.add(f);
	}
	
	public static Vector<File> getDelfileOnexit(){
		return delonexistfiles;
	}
	
	public static void clearFileonexit(){
		Enumeration<File> en=delonexistfiles.elements();
		while(en.hasMoreElements()){
			File f=en.nextElement();
			clearFile(f);
			
		}
		
	}
}
