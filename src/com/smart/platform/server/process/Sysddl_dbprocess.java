package com.smart.platform.server.process;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.*;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-25
 * Time: 14:05:55
 * To change this template use File | Settings | File Templates.
 */
public class Sysddl_dbprocess extends RequestProcessorAdapter {
    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd = req.commandAt(0);
        if (!(cmd instanceof StringCommand)) {
            return -1;
        }

        StringCommand strcmd = (StringCommand) cmd;
        if (!strcmd.getString().equals("查询系统选项字典")) {
            return -1;
        }

        StringCommand cmd1 = (StringCommand) req.commandAt(1);
        if (!(cmd instanceof StringCommand)) {
            return -1;
        }

        String keyword = cmd1.getString();

        Connection con = null;
        PreparedStatement c1 = null;
        PreparedStatement c2 = null;
        try {
            con = getConnection();

            String sql = "select ddlid,keyword,ddlname from sys_ddl_all_v where keyword=?";
            c1 = con.prepareStatement(sql);
            c1.setString(1, keyword);
            ResultSet rs = c1.executeQuery();
            DBTableModel goodsdtlds = DBModel2Jdbc.createFromRS(rs);
            DataCommand resultcmd1 = new DataCommand();
            resultcmd1.setDbmodel(goodsdtlds);
            c1.close();
            c1 = null;
            resp.addCommand(resultcmd1);


        } catch (Exception e) {
            logger.error("save", e);
            ResultCommand errorcmd = new ResultCommand(ResultCommand.RESULT_FAILURE, e.getMessage());
            resp.addCommand(errorcmd);
        } finally {
            if (c1 != null) {
                c1.close();
            }
            if (c2 != null) {
                c2.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return 0;
    }
}
