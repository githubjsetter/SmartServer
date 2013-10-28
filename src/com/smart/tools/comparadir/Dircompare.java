package com.smart.tools.comparadir;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;

public class Dircompare {
	public static void compare(java.io.File dir1,File dir2){
		Dirfileinfo fileinfo1=Scanner.doScandir(dir1);
		Dirfileinfo fileinfo2=Scanner.doScandir(dir2);
		compareInfo(fileinfo1,fileinfo2);
	}
	
	static void compareInfo(Dirfileinfo info1,Dirfileinfo info2){
		//��Ŀ¼
		Iterator<String>it=info1.filemd5map.keySet().iterator();
		while(it.hasNext()){
			String fn=it.next();
			String md51=info1.filemd5map.get(fn);
			
			String md52=info2.filemd5map.get(fn);
			if(md52==null){
				System.out.println("ȱ�ļ�:"+info2.dir.getPath()+" "+fn);
			}else if(!md51.equals(md52)){
				System.out.println("��ͬ�ļ�:"+info2.dir.getPath()+" "+fn);
			}
		}
		
		//��Ŀ¼
		Enumeration<Dirfileinfo> en1=info1.children.elements();
		while(en1.hasMoreElements()){
			Dirfileinfo child1=en1.nextElement();
			
			//��info2��child
			Dirfileinfo child2=null;
				Enumeration<Dirfileinfo> en2=info2.children.elements();
				while(en2.hasMoreElements()){
					Dirfileinfo tmp=en2.nextElement();
					if(tmp.dir.getName().equals(child1.dir.getName())){
						child2=tmp;
						break;
					}
				}
				
				if(child2==null){
					System.out.println("ȱĿ¼:"+child1.dir.getPath());
					continue;
				}
				compareInfo(child1,child2);
			
		}
		
	}
	
	public static void main(String[] args) {
		File dir1=new File("d:\\npserver\\src");
		File dir2=new File("d:\\ws\\npserver53\\src");
		
		//File dir1=new File("d:\\temp\\a");
		//File dir2=new File("d:\\temp\\b");

		compare(dir1,dir2);
	}
}
