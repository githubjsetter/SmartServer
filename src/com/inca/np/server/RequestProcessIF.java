package com.inca.np.server;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ServerResponse;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-26
 * Time: 16:40:58
 * ������ӿ�
 */
public interface RequestProcessIF {
    static int PROCESSED=0;
    static int NOTPROCESS=-1;
    int process(Userruninfo userinfo,ClientRequest req,ServerResponse resp)throws Exception;
    /**
     * �ͷ���Դ
     */
    void release();
}
