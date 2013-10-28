package com.smart.server.servermanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.LicensefileReader;
import com.smart.server.prod.Licenseinfo;


/**
 * �����ϴ���LICENSE�ļ�
 * @author Administrator
 *
 */
public class UploadlicensefileProcessor extends RequestProcessorAdapter{
	Category logger=Category.getInstance(UploadlicensefileProcessor.class);
	static String COMMAND="npserver:uploadlicensefile";

	/**
	 * �����ļ� ����np:fileupload 
	 * ParamCommand filename �ļ��� 
	 *              filegroupid ��������������
	 * 
	 * @param req
	 * @return -1 ʧ�� 0 �ɹ� 1 �ɹ�,��ȫ���ϴ����
	 * @throws Exception
	 */

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
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

        BinfileCommand datacmd = (BinfileCommand) req.commandAt(2);
        byte[] bindata = datacmd.getBindata();

        File repodir = LicenseManager.getInst().getLicensefileDir();
        
        File targetfile = new File(repodir, filename);

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
        
        if(finished){
        	LicensefileReader lfr=new LicensefileReader();
        	Licenseinfo linfo=lfr.readLicensefile(targetfile);
        	if(linfo==null){
        		targetfile.delete();
                resp.addCommand(new StringCommand("-ERROR:��ȡ��Ȩ�ļ�ʧ��"+lfr.getErrormsg()));
                return 0;
        	}
        	LicenseManager.getInst().reload();
        }
        resp.addCommand(new StringCommand("+OK"));
        return 0;
	}


}
