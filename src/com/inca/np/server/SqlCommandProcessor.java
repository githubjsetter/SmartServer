package com.inca.np.server;

import com.inca.np.communicate.*;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-28
 * Time: 17:16:07
 * ¥¶¿Ìsql√¸¡Ó
 */
public class SqlCommandProcessor implements CommandProcessIF {
    Category logger=Category.getRoot();

    Connection con=null;

    public CommandBase process(CommandBase cmdin) throws Exception {
        if(!(cmdin instanceof SqlCommand)){
            return null;
        }

        SqlCommand sqlcmd=(SqlCommand)cmdin;
        String sql = sqlcmd.getSql();
        if(!sql.trim().toLowerCase().startsWith("select")){
            return null;
        }

        PreparedStatement c1=null;
        try {
            if(con==null) con = getTestCon();
            logger.debug("connect ok");
            c1 = con.prepareStatement(sql);
            ResultSet rs = c1.executeQuery();

            //logger.debug("DBModel2Jdbc.createFromRS begin");
            DBTableModel retcmd = DBModel2Jdbc.createFromRS(rs);
            //logger.debug("DBModel2Jdbc.createFromRS done");

            DataCommand datacmd=new DataCommand();
            datacmd.setDbmodel(retcmd);
            return datacmd;

        } catch(Exception e){
            logger.error("sqlcommandprocessor,sql="+sql,e);
            throw e;
        }finally {
            if(c1!=null)c1.close();
            //if(con!=null)con.close();
        }


    }


    String dbip=DefaultNPParam.debugdbip;
    String dbname=DefaultNPParam.debugdbsid;
    String dbuser=DefaultNPParam.debugdbusrname;
    String dbpass=DefaultNPParam.debugdbpasswd;

    public Connection getTestCon() throws Exception{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@"+dbip+":1521:"+dbname;

        Connection con = DriverManager.getConnection(url, dbuser, dbpass);
        con.setAutoCommit(false);
        return con;

    }
}
