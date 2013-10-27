package com.inca.np.server.process;

import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-27
 * Time: 14:11:02
 * 接收ping命令
 * 返回server ok + 日期时间
 */
public class Pingprocessor extends RequestProcessorAdapter{
    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd = req.commandAt(0);
        if( !(cmd  instanceof StringCommand )){
            return -1;
        }

        StringCommand strcmd=(StringCommand)cmd;
        if(!strcmd.getString().equals("ping")){
            return -1;
        }

        //这是ping命令。
        SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringCommand respcmd = new StringCommand("+OK server ok "+datefmt.format(new Date()));
        resp.addCommand(respcmd);

        File appdir = guessWhichApp();
        //查询version.txt文件


        String servertime=String.valueOf(System.currentTimeMillis()) ;
        String version=DefaultNPParam.npversion;
        //logger.debug("server npserver="+version);
/*        File versionf=new File(appdir,"client/conf/version.txt");
        if(versionf.exists()){
            BufferedReader rder=null;
            try
            {
                rder=new BufferedReader(new FileReader(versionf));
                version=rder.readLine();
            }catch(Exception e){
                logger.error("error",e);
            }finally{
                if(rder!=null){
                    rder.close();
                }
            }

        }
*/
        ParamCommand paramCommand = new ParamCommand();
        resp.addCommand(paramCommand);
        paramCommand.addParam("version",version);
        paramCommand.addParam("servertime",servertime);

        return 0;

    }

    public File guessWhichApp(){
        URL url = this.getClass().getResource("Pingprocessor.class");
        logger.debug(url);

        String strurl=url.toString();
        int p=strurl.indexOf("/WEB-INF/");

        if(p<0){
            logger.debug("找不到/WEB-INF/,返回whichwebapp");
            return new File("whichwebapp");
        }

        String s=strurl.substring(0,p);
        if(s.startsWith("jar:")){
            s=s.substring(4);
        }

        if(s.startsWith("file:")){
            s=s.substring(5);
        }


        //logger.debug("返回应用目录"+s);
        return new File(s);

    }

}
