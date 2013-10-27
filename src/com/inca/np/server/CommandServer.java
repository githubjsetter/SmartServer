package com.inca.np.server;

import com.inca.np.communicate.CommandFactory;
import com.inca.np.communicate.CommandBase;

import java.util.Vector;
import java.util.Enumeration;
import java.util.zip.ZipOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Category;

/**
 * @deprecated 
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-28
 * Time: 17:08:54
 * 命令服务器.
 * 从一个二进流收到命令再进行分别处理,返回二进制流.
 * 应该设计一个接口.在commandserver中放入接口队进行处理
 *
 */
public class CommandServer {
    Vector <CommandProcessIF> processes=new Vector<CommandProcessIF>();
    public CommandServer() {
        processes.add(new SqlCommandProcessor());
    }

    public void process(InputStream in,OutputStream out)throws Exception{
        CommandBase cmd = CommandFactory.readCommand(in);

        Enumeration<CommandProcessIF> en = processes.elements();
        while (en.hasMoreElements()) {
            CommandProcessIF proc =  en.nextElement();
            logger.info("process CommandProcessIF begin");
            CommandBase retcmd=proc.process(cmd);
            logger.info("process CommandProcessIF down");
            if(retcmd!=null){
                logger.info("CommandServer send command");
                DeflaterOutputStream zout = new DeflaterOutputStream(out);
                retcmd.write(zout);
                zout.finish();
                logger.info("CommandServer send command done");
                break;
            }
        }
    }

    Category logger=Category.getRoot();
}
