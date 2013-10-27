package com.inca.np.gui.control;

import javax.swing.*;

import com.inca.np.gui.ste.Querycond;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-19
 * Time: 18:02:23
 * 在查询对话窗口的上部,可以再放更多的查询条件.
 */
public interface COtherquerycontrol {
	/**
	 * 返回其它的条件放置是JPanel
	 * @return
	 */
    JPanel getOtherquerypanel();
    
}
