package com.inca.np.communicate;

import com.inca.np.util.DefaultNPParam;
import com.inca.np.client.RemoteConnector;
import com.inca.np.server.RequestDispatch;
import com.inca.np.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-25
 * Time: 14:02:48
 * ��ѯѡ���ֵ�
 */
public class RemoteDdlHelper  {
    public static String defaulturl=DefaultNPParam.defaultappsvrurl;//"http://218.247.157.239/np/clientrequest.do";
    private RemoteConnector conn;


    public void doSelect(String keyword) throws Exception{
    	if(DefaultNPParam.runonserver){
    		return;
    	}
        StringCommand cmd1 = new StringCommand("��ѯϵͳѡ���ֵ�");
        ClientRequest req = new ClientRequest();
        req.addCommand(cmd1);

        StringCommand cmd2=new StringCommand(keyword);
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
            throw new Exception("�޷�������");
        }

        DataCommand datacmd1=(DataCommand) svrresp.commandAt(0);
        ddlmodel=datacmd1.getDbmodel();
    }

    DBTableModel ddlmodel=null;


    public DBTableModel getDdlmodel() {
        return ddlmodel;
    }

}
