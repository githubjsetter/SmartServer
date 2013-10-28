package com.smart.platform.filesync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.fileserver.FileServer;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-6
 * Time: 10:02:40
 * 客户端下载服务
 * 客户端上行
 * cmd0 np:download
 * cmd1:ParamCommand 参数
 * cmd2:客户端文件列表
 */
public class Download_dbprocess extends RequestProcessorAdapter {

    Category logger = Category.getInstance(Download_dbprocess.class);

    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd0 = req.commandAt(0);
        if (!(cmd0 instanceof StringCommand && ((StringCommand) cmd0).getString().equals("np:download"))) {
            return -1;
        }

        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        String downloadtype = cmd1.getValue("downloadtype");

        if (downloadtype.equals("CLIENT")) {
            downloadClient(req, resp);
        } else if (downloadtype.equals("FILE")) {
            //续传文件
            downloadFile(req, resp);
        } else if (downloadtype.equals("RECORDFILE")) {
            //下载续传记录附件文件
            downloadRecordFile(req, resp);
        } else {
            resp.addCommand(new StringCommand("-ERROR:不明downloadtype"));
        }
        return 0;
    }

    /**
     * 上传更新WEB-INF
     * 接收文件
     *
     * @param req
     * @deprecated
     */
    void downloadClient(ClientRequest req, ServerResponse resp) {
        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);

        DataCommand cmd2 = (DataCommand) req.commandAt(2);
        DBTableModel targetdbmodel = cmd2.getDbmodel();

        File srcdir = getWebapplicationDir();
        srcdir=new File(srcdir,"client");
        logger.info("downloadClient:client dir at server is "+srcdir.getPath());
        FileinfoFinder ff = new FileinfoFinder();
        FileinfoDBmodel srcdbmodel = ff.searchFile(srcdir);
        Filecompara fc = new Filecompara();
        FileinfoDBmodel diffmodel = fc.compara(srcdbmodel, targetdbmodel);
        //diffmodel中为客户端需要更新的数据.

        //生成一个临时zip文件
        File tmpfile=null;
        try {
            tmpfile = File.createTempFile("download", "zip");
            ZipOutputStream zout = null;
            try {
                zout = new ZipOutputStream(new FileOutputStream(tmpfile));
                for (int r = 0; r < diffmodel.getRowCount(); r++) {
                    String path = diffmodel.getItemValue(r, "path");
                    File srcfile = new File(srcdir, path);
                    ZipEntry entry = new ZipEntry(path);
                    entry.setTime(srcfile.lastModified());
                    zout.putNextEntry(entry);
                    zipFile(zout, srcfile);
                }
            } finally {
                if (zout != null) {
                    zout.close();
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
            resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
            return;
        }


        //将数据发回,通知客户端来取
        resp.addCommand(new StringCommand("+OK:download成功"));
        ParamCommand respparam = new ParamCommand();
        respparam.addParam("filename",tmpfile.getName());
        respparam.addParam("filesize",String.valueOf(tmpfile.length()));
        resp.addCommand(respparam);
        DataCommand datacmd = new DataCommand();
        datacmd.setDbmodel(diffmodel);
        resp.addCommand(datacmd);

        return;

    }

    void zipFile(ZipOutputStream zout, File srcfile) throws Exception {
        int buflen = 102400;
        FileInputStream in = null;
        try {
            in = new FileInputStream(srcfile);
            byte[] buffer = new byte[buflen];
            while (true) {
                int rd = in.read(buffer);
                if (rd <= 0) break;
                zout.write(buffer, 0, rd);
            }
        } finally {
            if (in != null) in.close();
        }
    }
    
    /**
     * 下载记录附件文件
     * @param req
     * @param resp
     */
    void downloadRecordFile(ClientRequest req, ServerResponse resp)throws Exception {
    	FileServer filesvr=FileServer.getInstance();
    	filesvr.downloadFile(req,resp);
    }

    /**
     * @deprecated
     * @param req
     * @param resp
     */
    void downloadFile(ClientRequest req, ServerResponse resp) {
        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        String filename = cmd1.getValue("filename");
        long startpos=Long.parseLong(cmd1.getValue("startpos"));
        File tmpdir=this.getTempdir();
        File srcfile=new File(tmpdir,filename);
        RandomAccessFile rder=null;
        try {
            rder = new RandomAccessFile(srcfile, "r");
            rder.seek(startpos);

            //读100k
            byte[] buffer=new byte[102400];
            int rded = rder.read(buffer);
            BinfileCommand binfile = new BinfileCommand(buffer, 0, rded);

            resp.addCommand(new StringCommand("+OK:"));

            ParamCommand paramcmd = new ParamCommand();
            resp.addCommand(paramcmd);
            if(startpos+rded>=srcfile.length()){
                paramcmd.addParam("hasmore","false");
                rder.close();rder=null;
                srcfile.delete();
            }else{
                paramcmd.addParam("hasmore","true");
            }

            resp.addCommand(binfile);
            return;

        } catch (Exception e) {
            logger.error("error",e);
            resp.addCommand(new StringCommand("-ERROR:"+e));
            return;
        } finally {
            if(rder!=null){
                try {
                    rder.close();
                } catch (IOException e) {
                }
            }
        }

    }




    void unzipFile(File tmpfile, File webinfodir) throws Exception {

        ZipFile zipfile = new ZipFile(tmpfile);
        logger.debug("zipfile=" + tmpfile.getPath());
        Enumeration en = zipfile.getEntries();
        while (en.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) en.nextElement();
            logger.debug("entry name=" + entry.getName());
            if (entry.isDirectory()) {
                File outf = new File(webinfodir, entry.getName());
                outf.mkdirs();
            } else {
                File outf = new File(webinfodir, entry.getName());
                InputStream zin = null;
                try {
                    zin = zipfile.getInputStream(entry);
                    FileOutputStream fout = null;
                    try {
                        logger.info("正在解压" + outf.getPath());
                        outf.getParentFile().mkdirs();
                        fout = new FileOutputStream(outf);
                        int buflen = 102400;
                        byte[] buffer = new byte[buflen];

                        while (true) {
                            int rd = zin.read(buffer);
                            if (rd <= 0) break;
                            fout.write(buffer, 0, rd);
                        }
                        fout.close();
                        fout = null;
                        outf.setLastModified(entry.getTime());


                    } finally {
                        if (fout != null)
                            fout.close();

                    }
                } finally {
                    if (zin != null) {
                        zin.close();
                    }
                }
            }

        }

    }

    File getTempdir() {
        File f = null;
        try {
            f = File.createTempFile("tmp", "dat");
            return f.getParentFile();
        } catch (IOException e) {
            logger.error("error", e);
            return new File(".");
        } finally {
            if (f != null) {
                f.delete();
            }
        }
    }

}
