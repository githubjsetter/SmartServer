package com.smart.platform.client;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.server.RequestDispatch;
import com.smart.platform.util.DefaultNPParam;
import com.smart.server.server.Server;

import org.apache.log4j.Category;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-7
 * Time: 17:58:02
 * 守护线程,不停地与服务器联系,检查网络状态
 * 30秒发送一次
 */
public class PingThread implements Runnable{
    /**
     * 每15秒检查一次网络
     */
    long sleeptime=15 * 1000;

    Category logger = Category.getInstance(PingThread.class);
    SimpleDateFormat datef=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public PingThread() {
        super();
    }

    public void run() {
        try {
            //先等系统建立好窗口
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {

        }
        while(true){
            ping();
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
            }
        }
    }

    void ping() {
        StringCommand cmd = new StringCommand("ping");
        ClientRequest req = new ClientRequest();
        req.addCommand(cmd);

        long starttime=System.currentTimeMillis();
        RemoteConnector rmtconn = new RemoteConnector();
        String url = DefaultNPParam.defaultappsvrurl;
        ServerResponse svrresp = null;
        if (DefaultNPParam.debug == 1) {
            svrresp = Server.getInstance().process(req);
        } else {
            try {
                svrresp = rmtconn.submitRequest(url, req);
            } catch (Exception e) {
                logger.error("error",e);
                return;
            }
        }
        DefaultNPParam.pingtime = System.currentTimeMillis() - starttime;

        StringCommand respcmd= (StringCommand) svrresp.commandAt(0);
        if(!respcmd.getString().startsWith("+OK")){
            logger.error(respcmd.getString());
            return;
        }

        ParamCommand paramcmd= (ParamCommand) svrresp.commandAt(1);
        String version=paramcmd.getValue("version");
        String strservertime=paramcmd.getValue("servertime");
        String fmttime=datef.format(new Date(Long.parseLong(strservertime)));
        logger.info("npversion="+version+",servertime="+fmttime);

        //检查是不是需要自动升级
        if(DefaultNPParam.debug==0){
            //checkAutoupdate(version);
        }

        //显示系统时间
        try {
            DefaultNPParam.lastsvrtime= Long.parseLong(strservertime);
        } catch (NumberFormatException e) {
        
        }

    }

    /**
     * 检查版本
     * @param version
    void checkAutoupdate(String version) {
        boolean isneedupdate = needUpdate(version);
        if(isneedupdate){
            DefaultNPParam.doUpdate();
        }
    }
     */

    boolean needUpdate(String version) {
        File f=new File("conf/version.txt");
        if(!f.exists()){
            return true;
        }

        BufferedReader rder = null;
        try{
        rder = new BufferedReader(new FileReader(f));
            String thisversion = rder.readLine();
            return !thisversion.equals(version);
        }catch(Exception e){
            logger.error("error",e);
            return true;
        }finally{
            try {
                if(rder!=null)rder.close();
            } catch (IOException e) {
            }
        }

    } 

    public static void main(String[] argv){
        new DefaultNPParam();
        DefaultNPParam.debug=1;
        PingThread app=new PingThread();
        Thread t=new Thread(app);
        t.start();
    }

    
}
