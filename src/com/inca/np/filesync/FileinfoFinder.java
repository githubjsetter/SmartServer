package com.inca.np.filesync;

import com.inca.np.util.MD5Helper;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 14:02:51
 * ��һ��Ŀ¼�µ��ļ�ɨ��ŵ�DBTablemodel��.
 */
public class FileinfoFinder {
    private FileinfoDBmodel dbmodel;
    private String startpath;

    public FileinfoDBmodel searchFile(File startdir){
        dbmodel = new FileinfoDBmodel();
        startpath = startdir.getPath();

        searchDir(startdir);
        return dbmodel;
    }

    void searchDir(File startdir){
        File fs[]=startdir.listFiles();

        for(int i=0;fs!=null && i<fs.length;i++){
            File f=fs[i];

            if(f.isDirectory()){
                searchDir(f);
            }else{
            	if(!filterFile(f.getName()))continue;
                String lastmodifytime=String.valueOf(f.lastModified());
                String filesize=String.valueOf(f.length());
                String path=f.getPath();

                if(!path.startsWith(startpath)){
                    System.err.println("���,"+path+","+startpath);
                }

                path=path.substring(startpath.length()+1); //��Ҫ/
                path=path.replaceAll("\\\\","/");

                dbmodel.appendRow();
                int row=dbmodel.getRowCount()-1;
                dbmodel.setItemValue(row,"path",path);
                dbmodel.setItemValue(row,"lastmodifytime",lastmodifytime);
                dbmodel.setItemValue(row,"filesize",filesize);
                dbmodel.setItemValue(row,"md5", MD5Helper.MD5(f));
            }
        }
    }
    
    /**
     * �����ļ�
     * @param fn
     * @return true:���ļ���Ҫ���,false:��Ҫ��������ļ�
     */
    boolean filterFile(String fn){
    	fn=fn.toLowerCase();
    	if(fn.endsWith(".log"))return false;
    	if(fn.indexOf(".log.")>=0)return false;
    	return true;
    }

    public static void main(String[] argv){
        FileinfoFinder app=new FileinfoFinder();
        FileinfoDBmodel dbmodel = app.searchFile(new File("build/classes"));
        System.out.println(dbmodel.getRowCount());
    }
}
