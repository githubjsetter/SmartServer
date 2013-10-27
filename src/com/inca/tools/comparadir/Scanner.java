package com.inca.tools.comparadir;

import java.io.File;

import com.inca.np.util.MD5Helper;

public class Scanner {
	public static Dirfileinfo doScandir(File rootdir){
		Dirfileinfo rootinfo=new Dirfileinfo();
		rootinfo.dir=rootdir;
		
		scan(rootdir,rootinfo);
		return rootinfo;
	}

	private static void scan(File parentdir, Dirfileinfo parentinfo) {
		File[] fs=parentdir.listFiles();
		for(int i=0;fs!=null && i<fs.length;i++){
			File f=fs[i];
			
			if(f.isDirectory() ){
				if(f.getName().equals("CVS")){
					continue;
				}
				
				Dirfileinfo subdirinfo=new Dirfileinfo();
				parentinfo.children.add(subdirinfo);
				subdirinfo.dir=f;
				subdirinfo.parentnode=parentinfo;
				scan(f,subdirinfo);
				
			}else{
				//如果是文件,求MD5
				String md5=MD5Helper.MD5(f);
				parentinfo.filemd5map.put(f.getName(),md5);
			}
			
		}
	}
}
