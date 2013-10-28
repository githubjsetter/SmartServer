package com.smart.platform.demo.communicate;

import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.*;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestDispatch;
import com.smart.platform.util.DefaultNPParam;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-27
 * Time: 13:38:49
 * 进行简单的命令发送
 * 发送命令ping
 * 收到结果server is ok
 */
public class DemoSimpleclient1 {

    public static void main(String[] argv){
        DemoSimpleclient1 client=new DemoSimpleclient1();
        try {
            //client.sendPingRequest();
            while(true){
                System.out.println(new Date());
                client.sendSelectRequest();
                //Thread.sleep(1);
               // break;
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void sendPingRequest() throws Exception{
        StringCommand cmd1 = new StringCommand("ping");

        ClientRequest req = new ClientRequest();
        req.addCommand(cmd1);

        RemoteConnector conn=new RemoteConnector();
        String url=DefaultNPParam.defaultappsvrurl;

        ServerResponse svrresp=null;
        if(DefaultNPParam.debug==1){
            svrresp = RequestDispatch.getInstance().process(req);
        }else{
            svrresp = conn.submitRequest(url,req);
        }

        if(svrresp.getCommandcount()==0){
            System.err.println("无返回命令");
        }else{
            CommandBase tmp = svrresp.commandAt(0);
            if(!(tmp instanceof StringCommand)){
                System.err.println("返回第0个命令是不是StringCommand");
            }else{
                StringCommand cmd=(StringCommand)tmp;
                System.out.println("服务器返回："+cmd.getString());

            }
        }
    }


    public void sendSelectRequest() throws Exception{

        StringCommand cmd1 = new StringCommand("select");

        ClientRequest req = new ClientRequest();
        req.addCommand(cmd1);

        //String url="http://192.9.200.6/np/clientrequest.do";

        //String url="http://218.247.157.241/np/clientrequest.do";

        //String url="http://192.9.200.2:28080/np/clientrequest.do";
        //String url="http://192.9.200.2:18080/np/clientrequest.do";
        String url=DefaultNPParam.defaultappsvrurl;

        String sql = "select reqfile,fullhead from wap_log_20070328 where credate > sysdate - 13";


        //String url="http://220.194.55.95:28080/np/clientrequest.do";
        //String sql = "select * from tab";

        SqlCommand cmd2=new SqlCommand(sql);

        cmd2.setStartrow(0);
        cmd2.setMaxrowcount(1000);
        req.addCommand(cmd2);



        RemoteConnector conn=new RemoteConnector();


        ServerResponse svrresp = conn.submitRequest(url,req);



        if(svrresp.getCommandcount()==0){
            System.err.println("无返回命令");
        }else{
            CommandBase tmp = svrresp.commandAt(0);
            if(!(tmp instanceof StringCommand)){
                System.err.println("返回第0个命令是不是StringCommand");
            }else{
                StringCommand cmd=(StringCommand)tmp;
                String svrreturnstr = cmd.getString();
                System.out.println("服务器返回："+svrreturnstr);

                if(svrreturnstr.startsWith("+")){
                    DataCommand datacmd=(DataCommand)svrresp.commandAt(1);
                    DBTableModel dbmodel = datacmd.getDbmodel();
                    System.out.println("返回记录:"+dbmodel.getRowCount()+"条，hasmore="+dbmodel.hasmore());

                    int rowcount=dbmodel.getRowCount();
                    for(int r=0;r<rowcount;r++){
                        String name = dbmodel.getItemValue(r,0);
                        //System.out.println(name);
                    }
                }
            }
        }

    }

}
