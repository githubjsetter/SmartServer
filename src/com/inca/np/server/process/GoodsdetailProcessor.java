package com.inca.np.server.process;

import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.*;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 17:36:38
 * 取货品明细和单位
 */
public class GoodsdetailProcessor extends RequestProcessorAdapter {
    public int process(Userruninfo userinfo, ClientRequest req, ServerResponse resp) throws Exception {
        CommandBase cmd = req.commandAt(0);
        if (!(cmd instanceof StringCommand)) {
            return -1;
        }

        StringCommand strcmd = (StringCommand) cmd;
        if (!strcmd.getString().equals("查询货品明细")) {
            return -1;
        }

        StringCommand cmd1 = (StringCommand) req.commandAt(1);
        if (!(cmd instanceof StringCommand)) {
            return -1;
        }

        String goodsid = cmd1.getString();

        Connection con = null;
        PreparedStatement c1 = null;
        PreparedStatement c2 = null;
        try {
            con = getConnection();

            String sql = "select goodsdtlid,packname,packsize,packname||'('||to_char(packsize)||')' packnamesize" +
                    " from pub_goods_detail where goodsid=?";
            c1 = con.prepareStatement(sql);
            c1.setString(1, goodsid);
            ResultSet rs = c1.executeQuery();
            DBTableModel goodsdtlds = DBModel2Jdbc.createFromRS(rs);
            DataCommand resultcmd1 = new DataCommand();
            resultcmd1.setDbmodel(goodsdtlds);
            c1.close();
            c1 = null;
            resp.addCommand(resultcmd1);

            sql = "select goodsunit,baseflag from pub_goods_unit where goodsid=? order by baseflag desc,baseunitqty asc";
            c2 = con.prepareStatement(sql);
            c2.setString(1, goodsid);
            rs = c2.executeQuery();
            DBTableModel goodsunids = DBModel2Jdbc.createFromRS(rs);
            DataCommand resultcmd2 = new DataCommand();
            resultcmd2.setDbmodel(goodsunids);
            c2.close();
            c2 = null;
            resp.addCommand(resultcmd2);


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
