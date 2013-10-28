package com.smart.platform.auth;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-25
 * Time: 9:37:01
 * 用户管理
 */
public class ClientUserManager {
    private static Userruninfo currentuser = null;

    public static Userruninfo getCurrentUser() {
        if (currentuser == null) {
            currentuser = new Userruninfo();
/*            currentuser.setUserid("128");
            currentuser.setUsername("李娜");
            currentuser.setDeptid("9");
            currentuser.setDeptname("泰安科达平民大药房一分店");
            currentuser.setPlacepointid("9");
            currentuser.setPlacepointname("泰安科达平民大药房一分店");
            currentuser.setStorageid("4");
*/        }
        return currentuser;
    }

    public static void  setCurrentUser(Userruninfo user) {
        currentuser=user;
    }

}
