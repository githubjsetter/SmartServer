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
 * �����ϴ��ļ��б�������,������Ҫ�ϴ����ļ�
 * cmd0 ���� np:comparafile
 * cmd1 ParamCommand ѡ��,�ɰ���һЩ����,���������. ����Ҫ����uploadtype
 * uploadtypeĿǰΪ�̶�, WEB-INF ��ʾ����WEB-INFĿ¼�µ�
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
            resp.addCommand(new StringCommand("-ERROR:����uploadtype"));
        }


        return 0;
    }

    /**
     * �ϴ�����WEB-INF
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
     * �����ĸ�Ӧ����,�ҳ�Ӧ��Ŀ¼
     * @return
     */
    public  File guessWhichApp(){
        URL url = this.getClass().getResource("Comparafile_dbprocess.class");
        logger.debug(url);

        String strurl=url.toString();
        int p=strurl.indexOf("/WEB-INF/");

        if(p<0){
            logger.debug("�Ҳ���/WEB-INF/,����whichwebapp.");
            return new File("whichwebapp");
        }

        String s=strurl.substring(0,p);
        if(s.startsWith("jar:")){
            s=s.substring(4);
        }

        if(s.startsWith("file:")){
            s=s.substring(5);
        }


        logger.debug("����Ӧ��Ŀ¼"+s);
        return new File(s);

    }
}
