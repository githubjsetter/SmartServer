package com.smart.platform.server;

import com.smart.platform.communicate.CommandBase;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-28
 * Time: 17:14:41
 * @deprecated  ����
 */
public interface CommandProcessIF {
    /**
     * ����cmdin��������д���.����޷�������null
     * @param cmdin
     * @return
     * @throws Exception
     */
    CommandBase process(CommandBase cmdin)throws Exception;
}
