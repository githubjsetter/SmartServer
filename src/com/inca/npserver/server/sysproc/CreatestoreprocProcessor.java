package com.inca.npserver.server.sysproc;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

public class CreatestoreprocProcessor extends RequestProcessorAdapter {
	Category logger = Category.getInstance(CreatestoreprocProcessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!req.getCommand().equals("npclient:createstoreproc")){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String syntax=pcmd.getValue("syntax");
		
		Connection con=null;
		Statement c1=null;
		try{
			con=this.getConnection();
			c1=con.createStatement();
			syntax="create or replace "+syntax;
			logger.debug(syntax);
			//syntax=filterSyntax(syntax);
			//logger.debug(syntax);
			c1.execute(syntax);
			resp.addCommand(new StringCommand("+OK"));
		}catch(Exception e){
			logger.error("error",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		}finally{
			if(c1!=null){
				c1.close();
			}
			if(con!=null){
				con.close();
			}
		}
		
		return 0;
	}
	
	String filterSyntax(String s){
		StringBuffer sb=new StringBuffer();
		
		for(int i=0;i<s.length();i++){
			char c=s.charAt(i);
			if(c=='\r')continue;
			sb.append(c);
		}
		return sb.toString();
	}
	
}
