package com.smart.server.server.sysproc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.SqlCommand;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

public class SelectProcessor  extends RequestProcessorAdapter {
    Category logger = Category.getInstance(SelectProcessor.class);

    protected String svrcommand="select";

    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd = req.commandAt(0);
        if (!(cmd instanceof StringCommand)) {
            return -1;
        }

        StringCommand strcmd = (StringCommand) cmd;
        if (!strcmd.getString().equals(svrcommand)) {
            return -1;
        }


        cmd = req.commandAt(1);
        if (!(cmd instanceof SqlCommand)) {
            return -1;
        }

        SqlCommand sqlcmd = (SqlCommand) cmd;
        String sql = sqlcmd.getSql();

        sql=filteSql(userinfo,sql);

        int startrow = sqlcmd.getStartrow();
        int maxrowcount = sqlcmd.getMaxrowcount();

        StringCommand respstrcmd = null;
        DataCommand datacmd = new DataCommand();

        DBTableModel memds = null;

        Connection con = null;
        PreparedStatement c1 = null;
        try {
            logger.info("begin getConnection()");
            con = getConnection();
            logger.info("got Connection()");
            c1 = con.prepareStatement(sql);
            ResultSet rs = c1.executeQuery();
            logger.info("begin createFromRS");
            memds = DBModel2Jdbc.createFromRS(rs, startrow, maxrowcount);
            datacmd.setDbmodel(memds);

            respstrcmd = new StringCommand("+OK");
            resp.addCommand(respstrcmd);
            resp.addCommand(datacmd);
            logger.info("processed");
        } catch (Exception e) {
            logger.error("SelectProcessor,sql=" + sql, e);
            respstrcmd = new StringCommand("-ERROR " + e.getMessage());
            resp.addCommand(respstrcmd);
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return 0;

    }

    /**
     * 可对sql进行过滤
     * @param sql
     * @return
     */
    protected String filteSql(Userruninfo userinfo,String sql) {
        return sql;
    }
}
