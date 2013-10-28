package com.smart.platform.filesync;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.*;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 16:20:09
 * 处理上传文件列表到服务器,返回需要上传的文件
 * cmd0 命令 np:comparafile
 * cmd1 ParamCommand 选项,可包含一些参数,如更新密码. 必需要的是uploadtype
 * uploadtype目前为固定, WEB-INF 表示更新WEB-INF目录下的
 */
public class Comparafile_dbprocess extends RequestProcessorAdapter {

    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd0 = req.commandAt(0);
        if(!(cmd0 instanceof StringCommand && ((StringCommand)cmd0).getString().equals("np:comparafile"))){
            return -1;
        }

        ParamCommand cmd1= (ParamCommand) req.commandAt(1);
        String uploadtype=cmd1.getValue("uploadtype");

        if(uploadtype.equals("WEBAPP")){
            comparaWebinf(req,resp);
        }else{
            resp.addCommand(new StringCommand("-ERROR:不明uploadtype"));
        }


        return 0;
    }

    /**
     * 上传更新WEB-INF
     * @param req
     */
    void comparaWebinf(ClientRequest req, ServerResponse resp) {
        DataCommand datacmd= (DataCommand) req.commandAt(2);
        DBTableModel srcmodel = (DBTableModel) datacmd.getDbmodel();

        File webinfdir=guessWhichApp();
        FileinfoFinder ff=new FileinfoFinder();
        FileinfoDBmodel targetmodel = ff.searchFile(webinfdir);

        Filecompara fc=new Filecompara();
        FileinfoDBmodel diffmodel = fc.compara(srcmodel, targetmodel);

        resp.addCommand(new StringCommand("+OK"));

        DataCommand respdatacmd = new DataCommand();
        respdatacmd.setDbmodel(diffmodel);
        resp.addCommand(respdatacmd);
    }


    /**
     * 猜在哪个应用中,找出应用目录
     * @return
     */
    public  File guessWhichApp(){
        URL url = this.getClass().getResource("Comparafile_dbprocess.class");
        logger.debug(url);

        String strurl=url.toString();
        int p=strurl.indexOf("/WEB-INF/");

        if(p<0){
            logger.debug("找不到/WEB-INF/,返回whichwebapp.");
            return new File("whichwebapp");
        }

        String s=strurl.substring(0,p);
        if(s.startsWith("jar:")){
            s=s.substring(4);
        }

        if(s.startsWith("file:")){
            s=s.substring(5);
        }


        logger.debug("返回应用目录"+s);
        return new File(s);

    }
}
