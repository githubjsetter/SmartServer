package com.inca.npserver.server.sysproc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessIF;
import com.inca.np.server.RequestProcessorAdapter;

public class Repassword_dbprocessor   extends RequestProcessorAdapter{
    Category logger = Category.getInstance(Repassword_dbprocessor.class);
    public int process(Userruninfo userinfo, ClientRequest clientRequest, ServerResponse svrresp) throws Exception {
        StringCommand cmd1=(StringCommand) clientRequest.commandAt(0);
        if(!cmd1.getString().equals("npclient:重设密码")){
            return RequestProcessIF.NOTPROCESS;
        }

        ParamCommand cmd2=(ParamCommand) clientRequest.commandAt(1);
        String userid=userinfo.getUserid();
        String password=cmd2.getValue("password");
        String newpassword=cmd2.getValue("newpassword");

        //登录
        Connection con=null;
        PreparedStatement c1=null;
        PreparedStatement c2=null;
        String sql="select webpass from pub_employee where employeeid=?";
        try
        {
            con = getConnection();
            c1 = con.prepareStatement(sql);
            c1.setString(1,userid);
            ResultSet rs = c1.executeQuery();
            if(!rs.next()){
                loginFailure(svrresp,"-ERROR:没有这个用户ID");
                return RequestProcessIF.PROCESSED;
            }
            String webpass=rs.getString("webpass");

            if(webpass!=null && webpass.length()>0 && !webpass.equals(password)){
                //检查密码失败
                loginFailure(svrresp,"-ERROR:原密码错误");
                return RequestProcessIF.PROCESSED;
            }

            sql="update pub_employee set webpass=? where employeeid=?";
            c2 = con.prepareStatement(sql);
            c2.setString(1,newpassword);
            c2.setString(2,userid);
            c2.executeUpdate();
            con.commit();

            //登录成功
            svrresp.addCommand(new StringCommand("+OK：修改成功"));
            return RequestProcessIF.PROCESSED;

        }catch(Exception e){
        	con.rollback();
            logger.error("login error",e);
            loginFailure(svrresp,"-ERROR:"+e.getMessage());
            return RequestProcessIF.PROCESSED;
        }finally{
            if(c1!=null){
                try {
                    c1.close();
                } catch (SQLException e) {

                }
            }
            if(c2!=null){
                try {
                    c2.close();
                } catch (SQLException e) {

                }
            }
            if(con!=null){
                try {
                    con.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    void loginFailure(ServerResponse svrresp,String errormsg){
        svrresp.addCommand(new StringCommand(errormsg));
    }
}

