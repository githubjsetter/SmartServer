package com.inca.npserver.server.sysproc;

import java.io.File;
import java.io.RandomAccessFile;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.MD5Helper;

public class ZxhovdownloadProcessor    extends RequestProcessorAdapter {
	static String COMMAND = "npclient:下载专项HOV";
	Category logger=Category.getInstance(ZxhovdownloadProcessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String hovname=pcmd.getValue("hovname");
		String clientmd5=pcmd.getValue("clientmd5");
		
		//检查本地文件
		File classdir=CurrentappHelper.getClassesdir();
		File targetfile=new File(classdir,"专项开发/HOV/"+hovname+".zip");
		if(!targetfile.exists()){
			resp.addCommand(new StringCommand("-ERROR:找不到HOV名为"+hovname+"的专项开发包"));
			return 0;
		}
		
		boolean needdownload=false;
		if(clientmd5!=null && clientmd5.length()>0){
			String servermd5=MD5Helper.MD5(targetfile);
			needdownload=!clientmd5.equals(servermd5);
		}else{
			needdownload=true;
		}
		
		if(!needdownload){
			resp.addCommand(new StringCommand("+OK"));
			ParamCommand respcmd=new ParamCommand();
			respcmd.addParam("length","0");
			respcmd.addParam("totallength","0");
			respcmd.addParam("finished","true");
			resp.addCommand(respcmd);
			return 0;
		}
		
		//需要下载
		int startpos=Integer.parseInt(pcmd.getValue("startpos"));
		int buflen=102400;
		byte[] buf=new byte[buflen];
		
		int rd=0;
		RandomAccessFile fin=null;
		BinfileCommand bcmd=null;
		try{
			fin=new RandomAccessFile(targetfile,"r");
			fin.seek(startpos);
			rd=fin.read(buf);
			if(rd>0){
				bcmd=new BinfileCommand(buf,0,rd);
			}else{
				rd=0;
			}
		}catch(Exception e){
			logger.error("ERROR",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
			return 0;
		}finally{
			if(fin!=null){
				fin.close();
			}
		}
		
		resp.addCommand(new StringCommand("+OK"));
		ParamCommand respcmd=new ParamCommand();
		respcmd.addParam("length",String.valueOf(rd));
		respcmd.addParam("totallength",String.valueOf(targetfile.length()));
		respcmd.addParam("finished",startpos+rd>=targetfile.length()?"true":"false");
		resp.addCommand(respcmd);
		if(rd>0){
			resp.addCommand(bcmd);
		}
		
		
		return 0;
	}
}