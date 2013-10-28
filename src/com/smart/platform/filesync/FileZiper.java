package com.smart.platform.filesync;

import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.zip.ZipEntry;

import java.io.*;

import com.smart.platform.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 14:49:53
 * 将文件打包
 */
public class FileZiper {
    public void doZip(File startdir, DBTableModel dbmodel, OutputStream out) throws Exception {
        ZipOutputStream zout = new ZipOutputStream(out);

        for (int i = 0; i < dbmodel.getRowCount(); i++) {
            String fn = dbmodel.getItemValue(i, "path");

            File f = new File(startdir, fn);
            ZipEntry entry = new ZipEntry(fn);
            entry.setTime(f.lastModified());
            zout.putNextEntry(entry);

            writeFile(zout, f);
        }
        zout.close();
    }


    void writeFile(ZipOutputStream zout, File f) throws Exception {
        int buflen = 40960;
        byte[] buffer = new byte[buflen];

        int fl = (int) f.length();

        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            while (fl > 0) {
                int rd = in.read(buffer);
                zout.write(buffer, 0, rd);
                fl -= rd;
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


    public static void main(String argv[]){
        FileinfoFinder finder=new FileinfoFinder();
        File srcdir = new File("build/classes");
        FileinfoDBmodel srcdbmodel = finder.searchFile(srcdir);
        FileinfoDBmodel targetdbmodel = finder.searchFile(new File("c:/tomcat51/webapps/ngpcs/WEB-INF/classes"));

        Filecompara compara=new Filecompara();
        FileinfoDBmodel diffmodel = compara.compara(srcdbmodel, targetdbmodel);

        System.out.println(diffmodel.getRowCount());

        File outf=new File("modify.zip");
        try {
            FileOutputStream out = new FileOutputStream(outf);
            FileZiper ziper=new FileZiper();
            ziper.doZip(srcdir,diffmodel,out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }



}
