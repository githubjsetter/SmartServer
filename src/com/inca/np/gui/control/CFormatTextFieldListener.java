package com.inca.np.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-9
 * Time: 17:47:59
 * 变化接口
 */
public interface CFormatTextFieldListener {
    void onchanged(CFormatTextField comp,String value,String oldvalue);
    /**
     * 返回是不是hov的编辑字段
     * @return
     */
    boolean isHov(String editorname);

    void invokeHov(String editorname,String newvalue,String oldvalue);

    boolean confirmHov(String editorname,String newvalue,String oldvalue);

    void cancelHov(String editorname,String newvalue,String oldvalue);
}
