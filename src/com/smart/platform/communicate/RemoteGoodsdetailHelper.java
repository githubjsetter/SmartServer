package com.smart.platform.communicate;

import com.smart.platform.client.RemoteConnector;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestDispatch;
import com.smart.platform.util.DefaultNPParam;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 17:47:00
 * To change this template use File | Settings | File Templates.
 */
public class RemoteGoodsdetailHelper {
    public static String defaulturl=DefaultNPParam.defaultappsvrurl;//"http://218.247.157.239/np/clientrequest.do";
    private RemoteConnector conn;


    public void doSelect(String goodsid) throws Exception{
        StringCommand cmd1 = new StringCommand("查询货品明细");
        ClientRequest req = new ClientRequest();
        req.addCommand(cmd1);

        StringCommand cmd2=new StringCommand(goodsid);
        req.addCommand(cmd2);
        if(conn == null){
            conn = new RemoteConnector();
        }

        ServerResponse svrresp=null;
        if(DefaultNPParam.debug==1){
            svrresp = RequestDispatch.getInstance().process(req);
        }else{
            svrresp = conn.submitRequest(defaulturl,req);
        }

        if(svrresp.getCommandcount()==0){
            throw new Exception("无返回命令");
        }

        DataCommand datacmd1=(DataCommand) svrresp.commandAt(0);
        goodsdetailmodel=datacmd1.getDbmodel();

        DataCommand datacmd2=(DataCommand) svrresp.commandAt(1);
        goodsunitmodel=datacmd2.getDbmodel();
    }

    DBTableModel goodsdetailmodel=null;
    DBTableModel goodsunitmodel=null;

    public DBTableModel getGoodsdetailmodel() {
        return goodsdetailmodel;
    }

    public DBTableModel getGoodsunitmodel() {
        return goodsunitmodel;
    }

    //1227
    public static void main(String argv[]){
        RemoteGoodsdetailHelper helper=new RemoteGoodsdetailHelper();
        try {
            helper.doSelect("1227");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
