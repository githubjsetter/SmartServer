package com.smart.platform.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-16
 * Time: 16:21:33
 * 显示进程的窗口接口
 */
public interface CProgressIF {
    void appendMessage(String msg);
    void close();
    void messageBox(String title,String msg);
}
