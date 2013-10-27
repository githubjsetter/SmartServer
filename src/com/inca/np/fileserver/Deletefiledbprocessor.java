package com.inca.np.fileserver;

import java.io.File;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;

public class Deletefiledbprocessor extends RequestProcessorAdapter {

    Category logger = Category.getInstance(Deletefiledbprocessor.class);
    
    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd0 = req.commandAt(0);
        if (!(cmd0 instanceof StringCommand && ((StringCommand) cmd0).getString().equals("np:deletefile"))) {
            return -1;
        }
        
        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        String filegroupid = cmd1.getValue("filegroupid");
        String filename = cmd1.getValue("filename");

        File dir=FileServer.getFileRepositoryDir();
        dir=new File(dir,filegroupid);
        
        File f=new File(dir,filename);
        if(f.exists()){
        	if(!f.delete()){
        		resp.addCommand(new StringCommand("-ERROR:É¾³ýÎÄ¼þÊ§°Ü"));
        	}else{
        		resp.addCommand(new StringCommand("+OK"));
        	}
        }else{
    		resp.addCommand(new StringCommand("+OK"));
        }

        
        return 0;
    }
}