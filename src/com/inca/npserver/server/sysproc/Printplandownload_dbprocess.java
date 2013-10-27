package com.inca.npserver.server.sysproc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.MD5Helper;

public class Printplandownload_dbprocess     extends RequestProcessorAdapter {
	static String COMMAND = "npclient:���ش�ӡ����";
	static String COMMAND1 = "npclient:�г���ӡ����";
	Category logger=Category.getInstance(Printplandownload_dbprocess.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand()) && !COMMAND1.equals(req.getCommand())) {
			return -1;
		}
		
		if(COMMAND1.equals(req.getCommand())){
			listPrintplan(userinfo,req,resp);
			return 0;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String planfilename=pcmd.getValue("planfilename");
		String clientmd5=pcmd.getValue("clientmd5");
		
		//��鱾���ļ�
		File classdir=CurrentappHelper.getClassesdir();
		File targetfile=new File(classdir,"��ӡ����/"+planfilename);
		if(!targetfile.exists()){
			resp.addCommand(new StringCommand("-ERROR:�Ҳ�����ӡ�����ļ�"+planfilename));
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
		
		//��Ҫ����
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

	/**
	 * �г���ӡ����
	 * @param userinfo
	 * @param req
	 * @param resp
	 */
	void listPrintplan(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception{
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("planname","varchar");
		cols.add(col);
		DBTableModel dm=new DBTableModel(cols);
		
		File classdir=CurrentappHelper.getClassesdir();
		File dir=new File(classdir,"��ӡ����");
		File fs[]=dir.listFiles();
		
		for(int i=0;fs!=null && i<fs.length;i++){
			File f=fs[i];
			if(f.isDirectory() || !f.getName().endsWith("printplan"))continue;
			int row=dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(row, "planname",f.getName());
		}
		resp.addCommand(new StringCommand("+OK"));
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dm);
		resp.addCommand(dcmd);
		return;
	}
}