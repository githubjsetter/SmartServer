package com.inca.np.fileserver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;

/**
 * np:browsefilegroup
 * 上传filegroupid
 * 返回DBTableModel,包含文件信息
 * filename credate filesize inputmanid inputmanname
 * @author Administrator
 *
 */
public class FileBrowseProcessor extends RequestProcessorAdapter {

    Category logger = Category.getInstance(FileBrowseProcessor.class);
    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd0 = req.commandAt(0);
        if (!(cmd0 instanceof StringCommand && ((StringCommand) cmd0).getString().equals("np:browsefilegroup"))) {
            return -1;
        }

        ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
        String filegroupid = cmd1.getValue("filegroupid");

        File dir=FileServer.getFileRepositoryDir();
        dir=new File(dir,filegroupid);
        
        DBTableModel dbmodel=createFiledbmodel();
        File fs[]=dir.listFiles();
        for(int i=0;fs!=null && i<fs.length;i++){
        	dbmodel.appendRow();
        	dbmodel.setItemValue(i, "filename",fs[i].getName());
        	dbmodel.setItemValue(i, "filesize",String.valueOf(fs[i].length()));
        	java.util.Date dt=new java.util.Date(fs[i].lastModified());
        	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	String modifydate=df.format(dt);
        	dbmodel.setItemValue(i, "modifydate",modifydate);
        }
        
        resp.addCommand(new StringCommand("+OK"));
        DataCommand datacmd=new DataCommand();
        datacmd.setDbmodel(dbmodel);
        resp.addCommand(datacmd);
        
        return 0;
    }
    
    DBTableModel createFiledbmodel(){
    	Vector<DBColumnDisplayInfo> colinfos=new Vector<DBColumnDisplayInfo>();
    	DBColumnDisplayInfo col=new DBColumnDisplayInfo("filename","varchar");
    	colinfos.add(col);
    	
    	col=new DBColumnDisplayInfo("filesize","number");
    	colinfos.add(col);

    	col=new DBColumnDisplayInfo("modifydate","date");
    	colinfos.add(col);

    	
    	return new DBTableModel(colinfos);
    }
}
