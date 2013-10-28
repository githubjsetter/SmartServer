package com.smart.platform.server;

import com.smart.platform.communicate.CommandBase;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-28
 * Time: 17:14:41
 * @deprecated  作废
 */
public interface CommandProcessIF {
    /**
     * 根据cmdin的命令进行处理.如果无法处理返回null
     * @param cmdin
     * @return
     * @throws Exception
     */
    CommandBase process(CommandBase cmdin)throws Exception;
}
