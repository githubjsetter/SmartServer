package com.inca.np.auth;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.auth.Userruninfo;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-25
 * Time: 9:37:01
 * �û�����
 */
public class ClientUserManager {
    private static Userruninfo currentuser = null;

    public static Userruninfo getCurrentUser() {
        if (currentuser == null) {
            currentuser = new Userruninfo();
/*            currentuser.setUserid("128");
            currentuser.setUsername("����");
            currentuser.setDeptid("9");
            currentuser.setDeptname("̩���ƴ�ƽ���ҩ��һ�ֵ�");
            currentuser.setPlacepointid("9");
            currentuser.setPlacepointname("̩���ƴ�ƽ���ҩ��һ�ֵ�");
            currentuser.setStorageid("4");
*/        }
        return currentuser;
    }

    public static void  setCurrentUser(Userruninfo user) {
        currentuser=user;
    }

}
