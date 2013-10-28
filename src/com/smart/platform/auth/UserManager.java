package com.smart.platform.auth;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.util.DefaultNPParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-26
 * Time: 17:35:28
 * 用户管理。身份验证。
 */
public class UserManager {
    /**
     * 已成功登录用户。
     * key是用户authstring
     */
    private static final HashMap<String, Userruninfo> loginusers = new HashMap<String, Userruninfo>();
    static final long maxtimeout = 3600L * 1000L;

/*
    private static Vector<UserAuthIF> auths=new Vector<UserAuthIF>();
    public static void addAuth(UserAuthIF auth){
        auths.add(auth);
    }
*/

    /**
     * 验证当前用户
     *
     * @param req
     * @return
     */
    public static Userruninfo authUser(ClientRequest req) {
        String authstring = req.getAuthstring();
        //logger.debug("authUser authstring="+authstring);
        if (authstring.length() > 0) {
        	//这里似乎不需要同步 synchronized 20070905
            //synchronized (loginusers) {
                Userruninfo userruninfo = loginusers.get(authstring);
                if (userruninfo != null) {
                    //logger.debug("authUser authstring="+authstring+",user already login, return="+userruninfo);
                    userruninfo.setLastaccesstime(System.currentTimeMillis());
                    return userruninfo;
                }
            //}

            Userruninfo savedu = loadUser(authstring);
            if (savedu != null) {
                savedu.setLastaccesstime(System.currentTimeMillis());
                synchronized (loginusers) {
                    //logger.debug("authUser authstring="+authstring+",load user from file, return="+savedu);
                    loginusers.put(authstring,savedu);
                }
                return savedu;
            }
        }

/*
        //重新验证
        Enumeration<UserAuthIF> en = auths.elements();
        while (en.hasMoreElements()) {
            UserAuthIF author = en.nextElement();
            Userruninfo userruninfo = author.login(req);
            if(userruninfo!=null){
                loginusers.put(authstring,userruninfo);
                return userruninfo;
            }
        }
*/

        return null;
    }


    public static void putLoginok(String authstring, Userruninfo userinfo) {
    	userinfo.setLogindatetime(System.currentTimeMillis());
    	userinfo.setLastaccesstime(System.currentTimeMillis());
        synchronized (loginusers) {
        	//如果这个用户已经登录了,删了原了的
        	//20070905 去掉这个检查. 允许一个帐号多个session
/*        	Iterator it=loginusers.keySet().iterator();
        	while(it.hasNext()){
        		String key=(String)it.next();
        		Userruninfo u=loginusers.get(key);
        		if(u.getUserid().equals(userinfo.getUserid())){
        			loginusers.remove(key);
        			break;
        		}
        	}
*/            
        	//logger.debug("user login ok,authstring="+authstring+",userinfo="+userinfo);
            loginusers.put(authstring, userinfo);
        }
        saveUser(userinfo);
    }


    private static Random random = new Random(System.currentTimeMillis());

    private static int authseq=0;
    public static String genAuthstring(Userruninfo userinfo) {
        StringBuffer sb = new StringBuffer();
        sb.append(userinfo.getUserid());
        sb.append("." + userinfo.getDeptid());
        sb.append("." + System.currentTimeMillis());
        int seq=authseq++;
        seq+=random.nextInt(10000);
        sb.append(String.valueOf(seq));
        return sb.toString();
    }

    /**
     * 20070804 增加处理. 在临时文件中,保存用户信息. 这样服务器中断重启用户也能继续操作.
     */


    static Category logger = Category.getInstance(UserManager.class);


    static File tmpdir = getTempdir();

    static File getTempdir() {
        File tmpfile = null;
        try {
            tmpfile = File.createTempFile("test", "dat");
            return tmpfile.getParentFile();
        } catch (IOException e) {
            logger.error("error", e);
            return new File(".");
        } finally {
            if (tmpfile != null) {
                tmpfile.delete();
            }
        }
    }


    static String PREFIX = "session_";

    static void saveUser(Userruninfo u) {
        File savefile = new File(tmpdir, PREFIX + u.getAuthstring());
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(savefile);
            u.writeData(fout);
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static Userruninfo loadUser(String authstring) {
        File savefile = new File(tmpdir, PREFIX + authstring);
        if (!savefile.exists()) {
            return null;
        }
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(savefile);
            Userruninfo u = new Userruninfo();
            u.readData(fin);
            return u;
        } catch (Exception e) {
            logger.error("error", e);
            return null;
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }

    }

    static {
        CleanThread t = new CleanThread();
        t.setDaemon(true);
        t.setPriority(1);
        t.start();
    }

    static class CleanThread extends Thread {
        public void run() {
            while (DefaultNPParam.systemrunning) {
                scanNousefile();
                scanNotactive();
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                }
            }
        }


        void scanNotactive() {
            logger.info("cache中有"+loginusers.size()+"个用户");
            int deletect = 0;
            do {
                deletect=0;
                Iterator<Userruninfo> it = loginusers.values().iterator();
                while (it.hasNext()) {
                    Userruninfo userruninfo = it.next();
                    if ((System.currentTimeMillis() - userruninfo.getLastaccesstime()) > maxtimeout) {
                        logger.info("用户"+userruninfo.getUsername()+"超时,退出");
                        synchronized (loginusers) {
                            try{
                            	loginusers.remove(userruninfo.getAuthstring());
                            }catch(Exception e){
                            	//可能正好被删除了. by wwh 20070831
                            }
                            deletect++;
                            break;
                        }
                    }
                }

            }while (deletect > 0) ;
        }

        void scanNousefile() {
            File[] fs = tmpdir.listFiles();

            for (int i = 0; fs != null && i < fs.length; i++) {
                File f = fs[i];
                if (f.isDirectory()) {
                    continue;
                }
                if (!f.getName().startsWith(PREFIX)) {
                    continue;
                }

                Userruninfo u = new Userruninfo();
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(f);
                    u.readData(fin);
                } catch (Exception e) {
                    //logger.error("error", e);
                    try {
                    	if(fin!=null)
                        fin.close();
                    } catch (IOException e1) {}
                    f.delete();
                	
                    continue;
                } finally {
                    if (fin != null) {
                        try {
                            fin.close();
                        } catch (IOException e) {
                        }
                    }
                }

                //如果超过了24小时,删除
                long logintime = System.currentTimeMillis() - u.getLastaccesstime();
                if (logintime > 24 * 3600 * 1000) {
                    f.delete();
                }
            }
        }
    }
    
    public static Vector<Userruninfo> listLoginuser(){
    	Vector<Userruninfo> infos=new Vector<Userruninfo>();
    	synchronized(loginusers){
    		Iterator<Userruninfo> it=loginusers.values().iterator();
    		while(it.hasNext()){
    			infos.add(it.next());
    		}
    	}
    	
    	//排序
    	Collections.sort(infos,new Userruninfo());
    	
    	return infos;
    }

}
