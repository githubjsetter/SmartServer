package com.inca.np.fileserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import org.apache.log4j.Category;

import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.ZipHelper;

/**
 * 文件服务器. 负责文件的管理, 上传,下载
 * 
 * @author Administrator
 * 
 */
public class FileServer {
	private static FileServer inst = null;
    Category logger = Category.getInstance(FileServer.class);

	public static synchronized FileServer getInstance() {
		if (inst == null) {
			inst = new FileServer();
		}
		return inst;
	}

	/**
	 * 接收文件 命令np:fileupload 
	 * ParamCommand filename 文件名 
	 *              filegroupid 二进制数据命令
	 * 
	 * @param req
	 * @return -1 失败 0 成功 1 成功,并全部上传完成
	 * @throws Exception
	 */
	public int recvFile(ClientRequest req, ServerResponse resp)
			throws Exception {
        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        int datalen = 0;
        try {
            datalen = Integer.parseInt(cmd1.getValue("length"));
        } catch (Exception e) {
        }

        int startpos = 0;
        try {
            startpos = Integer.parseInt(cmd1.getValue("startpos"));
        } catch (Exception e) {
        }

        boolean finished = cmd1.getValue("finished").equals("true");
        
        String filename=cmd1.getValue("filename");
        String filegroupid=cmd1.getValue("filegroupid");

        BinfileCommand datacmd = (BinfileCommand) req.commandAt(2);
        byte[] bindata = datacmd.getBindata();

        File repodir = getFileRepositoryDir();
        File filegroupdir=new File(repodir,filegroupid);
        filegroupdir.mkdirs();
        
        File targetfile = new File(filegroupdir, filename);

        FileOutputStream fout = null;
        try {
            if (startpos == 0) {
                fout = new FileOutputStream(targetfile, false);
            } else {
                fout = new FileOutputStream(targetfile, true);
            }
            fout.write(bindata);

        } catch (Exception e) {
            logger.error("error", e);
            resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
            return -1;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
        if (finished) {
        	return 1;
        }else{
        	return 0;
        }
	}

	public void downloadFile(ClientRequest req, ServerResponse resp)
			throws Exception {
        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        String filename = cmd1.getValue("filename");
        String filegroupid = cmd1.getValue("filegroupid");
        long startpos=Long.parseLong(cmd1.getValue("startpos"));
        
        File repodir = getFileRepositoryDir();
        File filegroupdir=new File(repodir,filegroupid);
        File srcfile=new File(filegroupdir,filename);
        RandomAccessFile rder=null;
        try {
            rder = new RandomAccessFile(srcfile, "r");
            rder.seek(startpos);

            //读100k
            byte[] buffer=new byte[DefaultNPParam.binfileblocksize];
            int rded = rder.read(buffer);
            BinfileCommand binfile = new BinfileCommand(buffer, 0, rded);

            resp.addCommand(new StringCommand("+OK:"));

            ParamCommand paramcmd = new ParamCommand();
            resp.addCommand(paramcmd);
            if(startpos+rded>=srcfile.length()){
                paramcmd.addParam("hasmore","false");
                rder.close();rder=null;
            }else{
                paramcmd.addParam("hasmore","true");
            }
            paramcmd.addParam("totallength",String.valueOf(srcfile.length()));
            paramcmd.addParam("filename",filename);
            paramcmd.addParam("length", String.valueOf(rded));

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
	
	
	
	public static File getFileRepositoryDir(){
		Properties prop=System.getProperties();
		String userhome=(String)prop.get("user.home");
		File dir=new File(userhome+"/上传文件存储");
		if(!dir.exists()){
			dir.mkdirs();
		}
		return dir;
	}
}
