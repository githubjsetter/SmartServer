package com.smart.platform.filesync;

import com.smart.platform.gui.control.DBTableModel;

import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 14:23:11
 * 比较不同目录下的文件哪里不同
 */
public class Filecompara {
    Category logger=Category.getInstance(Filecompara.class);
    private SimpleDateFormat dataformat;

    public FileinfoDBmodel compara(DBTableModel srcdbmodel,DBTableModel targetdbmodel){
        //将target放在一个hashmap中,加速查询
        HashMap<String,Fileinfo> targetmap=new HashMap<String, Fileinfo>(targetdbmodel.getRowCount()*3/2);
        for(int r=0;r<targetdbmodel.getRowCount();r++){
            String path = targetdbmodel.getItemValue(r, "path");
            Fileinfo info = new Fileinfo(path);
            info.setLastmodifytime(targetdbmodel.getItemValue(r,"lastmodifytime"));
            info.setFilesize(targetdbmodel.getItemValue(r,"filesize"));
            info.setMd5(targetdbmodel.getItemValue(r,"md5"));
            targetmap.put(path,info);
        }

        FileinfoDBmodel diffdbmodel=new FileinfoDBmodel();
        for(int r=0;r<srcdbmodel.getRowCount();r++){
            String srcpath=srcdbmodel.getItemValue(r,"path");
/*
            String srclastmodifytime=srcdbmodel.getItemValue(r,"lastmodifytime");
            String srcfilesize=srcdbmodel.getItemValue(r,"filesize");
*/
            String srcmd5=srcdbmodel.getItemValue(r,"md5");

            //logger.info("file compara srcpath="+srcpath);
            Fileinfo targetfileinfo = targetmap.get(srcpath);
            if(targetfileinfo==null){
                logger.debug("file compara targetfileinfo=null");
                diffdbmodel.appendRecord(srcdbmodel.getRecordThunk(r));
            }else{

                if(!srcmd5.equals(targetfileinfo.getMd5())){
                    logger.debug("需要更新"+srcpath+" src md5="+srcmd5+",targetmd5="+targetfileinfo.getMd5());
                    diffdbmodel.appendRecord(srcdbmodel.getRecordThunk(r));
                }
            }

/*

            }else if(! srcfilesize.equals(targetfileinfo.getFilesize())){
                logger.info("file compara srcfilesize="+srcfilesize+",targetfileinfo size="+targetfileinfo.getFilesize());
                diffdbmodel.appendRecord(srcdbmodel.getRecordThunk(r));
            }else{
                //比较时间
                long srctime=0;long targettime=0;
                try {
                    srctime = Long.parseLong(srclastmodifytime);
                } catch (NumberFormatException e) {
                    srctime=0;
                }
                try {
                    targettime = Long.parseLong(targetfileinfo.getLastmodifytime());
                } catch (NumberFormatException e) {
                    targettime=0;
                }


                //超过5秒需要重新更新上传
                long difftime = srctime - targettime;
                if(difftime<0){
                    difftime=-difftime;
                }

                //正负误差过5秒就更新
                if(difftime >5000){
                    logger.debug("需要更新difftime="+difftime+",file compara srcpath="+srcpath+",srctime="+longtostr(srctime)+",targettime="+longtostr(targettime));

                    diffdbmodel.appendRecord(srcdbmodel.getRecordThunk(r));
                }
            }
*/

        }
        return diffdbmodel;
    }



    String longtostr(long l){
        dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dataformat.format(l);
    }


    public static void main(String argv[]){
        FileinfoFinder finder=new FileinfoFinder();
        FileinfoDBmodel srcdbmodel = finder.searchFile(new File("build/classes"));
        FileinfoDBmodel targetdbmodel = finder.searchFile(new File("c:/tomcat51/webapps/ngpcs/WEB-INF/classes"));

        Filecompara compara=new Filecompara();
        FileinfoDBmodel diffmodel = compara.compara(srcdbmodel, targetdbmodel);

        System.out.println(diffmodel.getRowCount());
    }
}
