package com.inca.np.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-9
 * Time: 17:47:59
 * �仯�ӿ�
 */
public interface CFormatTextFieldListener {
    void onchanged(CFormatTextField comp,String value,String oldvalue);
    /**
     * �����ǲ���hov�ı༭�ֶ�
     * @return
     */
    boolean isHov(String editorname);

    void invokeHov(String editorname,String newvalue,String oldvalue);

    boolean confirmHov(String editorname,String newvalue,String oldvalue);

    void cancelHov(String editorname,String newvalue,String oldvalue);
}
