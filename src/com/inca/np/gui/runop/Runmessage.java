package com.inca.np.gui.runop;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-30
 * Time: 16:07:19
 * 运行信息
 */
public class Runmessage {
    public static String MESSAGE_INFO="info";
    public static String MESSAGE_ERROR="error";

    String msg;
    String msgtype;


    public Runmessage(String msg) {
        this.msg = msg;
        this.msgtype=MESSAGE_INFO;
    }

    public Runmessage(String msg, String msgtype) {
        this.msg = msg;
        this.msgtype = msgtype;
    }


    public String getMsg() {
        return msg;
    }

    public String getMsgtype() {
        return msgtype;
    }
}
