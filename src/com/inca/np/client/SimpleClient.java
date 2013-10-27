package com.inca.np.client;

import com.inca.np.communicate.*;
import com.inca.np.server.CommandServer;
import com.inca.np.gui.control.DBTableModel;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-28
 * Time: 17:29:11
 * 发送简单的sql请求命令
 */
public class SimpleClient {
    public static void main(String[] argv){
        SimpleClient client=new SimpleClient();
        try {
            client.doSendsql();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    Category logger=Category.getRoot();
    public void doSendsql()throws Exception{
        ByteArrayOutputStream bclientout = new ByteArrayOutputStream();
        String testsql = "select * from wap_log_shanghai where rownum<=100";
        SqlCommand sqlcmd=new SqlCommand(testsql);
        sqlcmd.write(bclientout);

        ByteArrayInputStream bserverin = new ByteArrayInputStream(bclientout.toByteArray());
        ByteArrayOutputStream bserverout = new ByteArrayOutputStream();

        CommandServer svr=new CommandServer();
        svr.process(bserverin,bserverout);

        byte[] data = bserverout.toByteArray();
        logger.info("recv server data "+data.length+" bytes");

        ByteArrayInputStream bclientin = new ByteArrayInputStream(data);
        InflaterInputStream zipin = new InflaterInputStream(bclientin);
        DataCommand datacmd = (DataCommand) CommandFactory.readCommand(zipin);

        DBTableModel dbmodel = datacmd.getDbmodel();

        logger.info("记录数:"+dbmodel.getRowCount()+",列数="+dbmodel.getColumnCount());


    }
}
